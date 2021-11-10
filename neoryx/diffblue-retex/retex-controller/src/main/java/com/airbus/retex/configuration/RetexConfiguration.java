package com.airbus.retex.configuration;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateProperties;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateSettings;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.security.core.Authentication;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.airbus.retex.business.dto.TranslateDto;
import com.airbus.retex.business.dto.TranslateFieldsDto;
import com.airbus.retex.config.RetexConfig;
import com.airbus.retex.configuration.serializer.ITranslatedFields;
import com.airbus.retex.exception.MessagesDto;
import com.airbus.retex.persistence.EntityInterceptor;
import com.airbus.retex.persistence.impl.VersionableRepositoryImpl;
import com.airbus.retex.service.impl.audit.AuditorAwareImpl;
import com.fasterxml.classmate.TypeResolver;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.ResponseMessageBuilder;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ResponseMessage;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
@EnableJpaRepositories(basePackages = "com.airbus.retex.persistence",
        repositoryBaseClass = VersionableRepositoryImpl.class)
public class RetexConfiguration {


    @Autowired
    private RetexConfig retexConfig;

	@Autowired
	private MessageSource messageSource;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public AuditorAware<String> auditorAware() {
        return new AuditorAwareImpl();
    }


    @Bean
    public Docket docket(TypeResolver typeResolver) {
        final List<ResponseMessage> globalResponses = Arrays.asList(
                responseMessage(400, "The request is not correct"),
                responseMessage(500, "The server encoutered an error"));

        return new Docket(DocumentationType.SWAGGER_2)
                .enable(retexConfig.isSwaggerEnabled())
                // .additionalModels(typeResolver.resolve(MessagesDto.class))
                .globalResponseMessage(RequestMethod.HEAD, globalResponses)
                .globalResponseMessage(RequestMethod.GET, globalResponses)
                .globalResponseMessage(RequestMethod.PUT, globalResponses)
                .globalResponseMessage(RequestMethod.POST, globalResponses)
                .globalResponseMessage(RequestMethod.PATCH, globalResponses)
                .globalResponseMessage(RequestMethod.DELETE, globalResponses)
                .ignoredParameterTypes(Authentication.class)
                .ignoredParameterTypes(MultipartHttpServletRequest.class)
                .directModelSubstitute(TranslateDto.class, String.class)
                .directModelSubstitute(TranslateFieldsDto.class, ITranslatedFields.class)
                .additionalModels(typeResolver.resolve(MessagesDto.class))
                .enableUrlTemplating(true)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.airbus.retex"))
                .paths(PathSelectors.any())
                .build()
                ;
    }

    /**
     * @param code    int http code
     * @param message String
     * @return ResponseMessage }
     */
    private ResponseMessage responseMessage(int code, String message) {
        return new ResponseMessageBuilder()
                .code(code)
                .message(message)
                .responseModel(new ModelRef(MessagesDto.class.getSimpleName()))
                .build();
    }

    @Bean
    public LocalValidatorFactoryBean getValidator() {
        LocalValidatorFactoryBean bean = new LocalValidatorFactoryBean();
        bean.setValidationMessageSource(messageSource);
        return bean;
    }

    @Bean
    @DependsOn("createEntityInterceptor")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
            EntityManagerFactoryBuilder builder,
            JpaProperties jpaProperties,
            DataSource dataSource,
            HibernateProperties hibernateProperties
    ) {
        Map<String, Object> properties = hibernateProperties.determineHibernateProperties(
            jpaProperties.getProperties(),
            new HibernateSettings()
        );
        properties.put("hibernate.ejb.interceptor", createEntityInterceptor());

        return builder
                .dataSource(dataSource)
                .properties(properties)
                .packages("com.airbus.retex.model", "com.airbus.retex.business.audit")
                .build();
    }

    @Bean
    public EntityInterceptor createEntityInterceptor() {
        return new EntityInterceptor();
    }
}
