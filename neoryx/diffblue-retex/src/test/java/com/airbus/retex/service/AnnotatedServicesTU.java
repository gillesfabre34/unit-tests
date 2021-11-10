package com.airbus.retex.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class AnnotatedServicesTU {
    @Test
    public void checkTransactionalServices() {
        ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false);

        provider.addIncludeFilter(new AnnotationTypeFilter(Transactional.class));
        Set<BeanDefinition> transactionals = provider.findCandidateComponents("com.airbus.retex.service.impl");

        List<String> transactionalsClassNames = transactionals.stream().map(
                BeanDefinition::getBeanClassName
        ).collect(Collectors.toList());


        provider.resetFilters(false);

        provider.addIncludeFilter(new AnnotationTypeFilter(Service.class));
        Set<BeanDefinition> services = provider.findCandidateComponents("com.airbus.retex.service.impl");


        List<String> servicesClassNames = services.stream().map(
                BeanDefinition::getBeanClassName
        ).collect(Collectors.toList());

        servicesClassNames.stream().filter(
                item -> ! transactionalsClassNames.contains(item)
        ).forEach(
                Assertions::fail
        );

        transactionalsClassNames.stream().filter(
                item -> ! servicesClassNames.contains(item)
        ).forEach(
                Assertions::fail
        );

        int countTransactional = transactionals.size();
        int countServices = services.size();

        assertThat(countServices, equalTo(countTransactional));
    }

}
