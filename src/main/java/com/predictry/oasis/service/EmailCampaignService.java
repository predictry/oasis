package com.predictry.oasis.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.predictry.oasis.domain.EmailCampaign;
import com.predictry.oasis.domain.Tenant;
import com.predictry.oasis.domain.exception.TemplateDataNotFoundException;
import com.predictry.oasis.dto.mail.Product;
import com.predictry.oasis.dto.mail.ProductExpr;
import com.predictry.oasis.dto.mail.Recommendation;
import com.predictry.oasis.repository.EmailCampaignRepository;
import com.predictry.oasis.util.JsonMessageCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.jms.JMSException;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * This class provide services related to <code>EmailCampaign</code>.
 */
@Service
@Transactional
public class EmailCampaignService {

    private static final Logger LOG = LoggerFactory.getLogger(EmailCampaignService.class);

    @Autowired
    private EmailCampaignRepository emailCampaignRepository;

    @Autowired
    private S3Service s3Service;

    @Autowired @Qualifier("queue")
    private JmsTemplate jmsTemplate;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired @Qualifier("stringTemplateEngine")
    private TemplateEngine stringTemplateEngine;

    public EmailCampaign save(EmailCampaign emailCampaign) {
        emailCampaign = emailCampaignRepository.saveAndFlush(emailCampaign);
        Map<String, String> message = new HashMap<>();
        message.put("id", emailCampaign.getId().toString());
        jmsTemplate.send("OMS.EMAIL_CAMPAIGN", new JsonMessageCreator(objectMapper, message));
        return emailCampaign;
    }

    public EmailCampaign getCampaign(Long id) {
        return emailCampaignRepository.getOne(id);
    }

    @JmsListener(containerFactory = "queueJmsListenerContainerFactory", destination = "OMS.EMAIL_CAMPAIGN")
    public void receiveEmailCampaignEvents(Map<String, Object> map) throws JMSException {
        Long campaignId = Long.valueOf(map.get("id").toString());
        LOG.info("Processing email campaign [" + campaignId + "]");
        EmailCampaign emailCampaign = emailCampaignRepository.findOne(campaignId);
        emailCampaign.getTargets().forEach(target -> processEmailCampaign(emailCampaign, target.getAction().toLowerCase(), target.targetDate()));
    }

    public void processEmailCampaign(EmailCampaign emailCampaign, String action, LocalDate targetDate) {
        String prefix = String.format("data/tenants/%s/history/%d/%02d/%02d/", emailCampaign.getTenant().getId(), targetDate.getYear(), targetDate.getMonthValue(), targetDate.getDayOfMonth());
        s3Service.listBucket("predictry", prefix).forEach(key -> {
            try {
                Map json = objectMapper.readValue(s3Service.read("predictry", key), Map.class);
                if (json.containsKey("email")) {
                    // Check last action for that user
                    String userId = key.substring(key.lastIndexOf('/') + 1, key.length() - 5);
                    String uri = String.format("http://fisher.predictry.com:8090/fisher/userprofile/%s/%s/%s", emailCampaign.getTenant().getId(), userId, action);
                    Map userProfile = restTemplate.getForObject(uri, Map.class);
                    if (userProfile.containsKey("lastAction") && (userProfile.get("lastAction") != null)) {
                        LocalDateTime lastAction = LocalDateTime.parse((String) userProfile.get("lastAction"));
                        if (lastAction.isAfter(targetDate.atStartOfDay())) {
                            return;
                        }
                    }

                    List<String> items = null;
                    if ("buy".equals(action)) {
                        //noinspection unchecked
                        items = (List<String>) json.get("buys");
                    } else if ("view".equals(action)) {
                        //noinspection unchecked
                        items = (List<String>) json.get("views");
                    }
                    // Don't send email if no item in history
                    if ((items == null) || items.isEmpty()) {
                        return;
                    }

                    try {
                        String htmlEmail = processTemplate(emailCampaign, items);
                        if ((htmlEmail != null) && (!htmlEmail.isEmpty())) {
                            Map<String, String> message = new HashMap<>();
                            message.put("mandrill_key", emailCampaign.getMandrillAPIKey());
                            message.put("from", emailCampaign.getEmailFrom());
                            message.put("to", (String) json.get("email"));
                            message.put("subject", emailCampaign.getEmailSubject());
                            message.put("html_content", htmlEmail);
                            jmsTemplate.send("OMS.EMAIL", new JsonMessageCreator(objectMapper, message));
                        }
                    } catch (TemplateDataNotFoundException ex) {
                        LOG.warn("Not sending email to [" + json.get("email") + "]");
                    }
                }
            } catch (IOException ex) {
                LOG.error("Error while parsing Json [" + key + "]", ex);
            }
        });
    }

    public String processTemplate(EmailCampaign emailCampaign, List<String> items) throws IOException {
        Tenant tenant = emailCampaign.getTenant();
        if (items.isEmpty()) {
            throw new TemplateDataNotFoundException("No item to recommend");
        }
        Context context = new Context();
        List<ProductExpr> productExprs = items.stream().map(itemId -> {
            try {
                ProductExpr productExpr = getProductExpr(tenant.getId(), itemId);
                if (productExpr != null) {
                    Recommendation rec = getProductRecommendation(tenant.getId(), productExpr.getId(), "duo");
                    if (rec.getItems().isEmpty()) {
                        LOG.error("Can't find product recommendation for [" + itemId + "] for tenant [" + tenant.getId() + "]");
                    }
                    rec.getItems().forEach(recItemId -> {
                        ProductExpr recProduct = getProductExpr(tenant.getId(), recItemId);
                        if (recProduct != null) {
                            productExpr.addRecs(recProduct);
                        }
                    });
                }
                return ((productExpr == null) || (productExpr.getRecs().isEmpty())) ? null : productExpr;
            } catch (IOException  e) {
                LOG.error("Error retrieving product information [" + itemId + "] for tenant [" + tenant.getId() + "]");
            }
            return null;
        }).filter(p -> p != null).collect(Collectors.toList());
        context.setVariable("products", productExprs);
        return stringTemplateEngine.process(emailCampaign.getTemplate(), context);
    }

    public ProductExpr getProductExpr(String tenantId, String productId) {
        ProductExpr productExpr;
        try {
            Product product = getProduct(tenantId, productId);
            if (product == null) {
                LOG.error("Can't find product information for [" + productId + "] for tenant [" + tenantId + "]");
                return null;
            }
            productExpr = new ProductExpr(product);
        } catch (IOException e) {
            LOG.error("Error retrieving product information [" + productId + "] for tenant [" + tenantId + "]");
            return null;
        }
        return productExpr;
    }

    public Product getProduct(String tenantId, String productId) throws IOException {
        String jsonContent = s3Service.read("predictry", String.format("data/tenants/%s/items/%s.json", tenantId, productId));
        return (jsonContent != null) ? objectMapper.readValue(jsonContent, Product.class) : null;
    }

    public Recommendation getProductRecommendation(String tenantId, String productId, String algorithm) throws IOException {
        String jsonContent = s3Service.read("predictry", String.format("data/tenants/%s/recommendations/%s/%s.json", tenantId, algorithm, productId));
        return (jsonContent != null) ? objectMapper.readValue(jsonContent, Recommendation.class) : null;
    }

}
