package com.predictry.oasis.util;

import org.thymeleaf.TemplateProcessingParameters;
import org.thymeleaf.resourceresolver.IResourceResolver;
import org.thymeleaf.templateresolver.TemplateResolver;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;

/**
 * This <code>TemplateResolver</code> will resolve view that is passed as plain string in view name.
 *
 */
public class StringTemplateResolver extends TemplateResolver {

    public StringTemplateResolver() {
        setResourceResolver(new IResourceResolver() {

            @Override
            public String getName() {
                return "stringResourceResolver";
            }

            @Override
            public InputStream getResourceAsStream(TemplateProcessingParameters templateProcessingParameters, String resourceName) {
                return new ByteArrayInputStream(resourceName.getBytes(Charset.defaultCharset()));
            }

        });
    }

}
