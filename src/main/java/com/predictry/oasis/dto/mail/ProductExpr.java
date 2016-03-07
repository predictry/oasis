package com.predictry.oasis.dto.mail;

import java.util.ArrayList;
import java.util.List;

/**
 * This DTO is very similiar to <code>Product</code> but has an extra field that stores recommendation (<code>recs</code>).
 * It is used in email template as context variable.
 *
 */
@SuppressWarnings("unused")
public class ProductExpr {

    private String id;

    private String name;

    private String item_url;

    private String img_url;

    private Double price;

    private List<ProductExpr> recs = new ArrayList<>();

    public ProductExpr() { }

    public ProductExpr(Product product) {
        this.id = product.getId();
        this.name = product.getName();
        this.item_url = product.getItem_url();
        this.img_url = product.getImg_url();
        this.price = product.getPrice();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getItem_url() {
        return item_url;
    }

    public void setItem_url(String item_url) {
        this.item_url = item_url;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public List<ProductExpr> getRecs() {
        return recs;
    }

    public void setRecs(List<ProductExpr> recs) {
        this.recs = recs;
    }

    public void addRecs(ProductExpr productExpr) {
        this.recs.add(productExpr);
    }

}
