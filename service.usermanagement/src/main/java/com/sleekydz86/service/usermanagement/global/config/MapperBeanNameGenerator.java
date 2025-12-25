package com.sleekydz86.service.usermanagement.global.config;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;

public class MapperBeanNameGenerator extends AnnotationBeanNameGenerator implements BeanNameGenerator {
    
    @Override
    public String generateBeanName(BeanDefinition definition, BeanDefinitionRegistry registry) {
        String className = definition.getBeanClassName();
        if (className != null) {
            if (className.contains("global.mapper")) {
                String simpleName = className.substring(className.lastIndexOf('.') + 1);
                return "global" + simpleName;
            }
            else if (className.contains(".mapper.") && !className.contains("global")) {
                String simpleName = className.substring(className.lastIndexOf('.') + 1);
                return "mapper" + simpleName;
            }
        }
        
        return super.generateBeanName(definition, registry);
    }
}

