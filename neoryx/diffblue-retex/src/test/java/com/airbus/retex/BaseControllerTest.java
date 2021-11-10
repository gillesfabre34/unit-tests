package com.airbus.retex;

import com.airbus.retex.dataset.Dataset;
import com.airbus.retex.dataset.DatasetInitializer;
import com.airbus.retex.dataset.DatasetTestExecutionListener;
import com.airbus.retex.dataset.IDatasetAware;
import com.airbus.retex.helper.DatabaseVerificationService;
import com.airbus.retex.model.common.Language;
import com.airbus.retex.model.user.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.BeforeEach;
import org.opentest4j.AssertionFailedError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockitoTestExecutionListener;
import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.AbstractTestExecutionListener;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Locale;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@TestExecutionListeners(value = {
        DatasetTestExecutionListener.class,
        DependencyInjectionTestExecutionListener.class,
        MockitoTestExecutionListener.class,
        TransactionalTestExecutionListener.class})
@ActiveProfiles("test,debug")
@AutoConfigureMockMvc
public abstract class BaseControllerTest  extends AbstractTestExecutionListener implements IDatasetAware {

    public static final String API = "/api";
    public HttpStatus expectedStatus;
    public User asUser;
    public Language language;
    public Locale withLocale = Locale.ENGLISH;
    public MockHttpServletRequestBuilder withRequest;
    public MultiValueMap<String, String> withParams;
    public boolean checkResponseContentType = true;
    @Autowired
    public MockMvc mockMvc;
    @Autowired
    public DatasetInitializer dataSetInitializer;
    protected Dataset dataset;
    @Autowired
    protected ObjectMapper objectMapper;
    protected MediaType mediatype = MediaType.APPLICATION_JSON;
    @Autowired
    private MessageSource messageSource;
    private MessageSourceAccessor accessor;

    private TransactionTemplate transactionTemplate;

    @Autowired
    private PlatformTransactionManager platformTransactionManager;

    @Autowired
    protected DatabaseVerificationService databaseVerificationService;

    @Override
    public void setDataset(Dataset dataset) {
        this.dataset = dataset;
    }

    @BeforeEach
    public void beforeBaseControllerTest() {
        withParams = new LinkedMultiValueMap<>();
        accessor = new MessageSourceAccessor(messageSource, Locale.ENGLISH);
        transactionTemplate = new TransactionTemplate(platformTransactionManager);
    }

    public ResultActions abstractCheck() throws Exception {
        ResultActions result =
                mockMvc.perform(withRequest
                        .contentType(MediaType.APPLICATION_JSON)
                        .locale(withLocale)
                        .params(withParams)
                        .header(FakeAuthentication.headerUserId, asUser == null ? "" : asUser.getEmail()))
                        .andExpect(status().is(expectedStatus.value()));

        if (expectedStatus == HttpStatus.OK && checkResponseContentType) {
            result.andExpect(content().contentTypeCompatibleWith(mediatype));
        }
        return result;
    }

    private ResultActions abstractCheckNonConnected(MockHttpServletRequestBuilder requestBuilder) throws Exception {
        ResultActions result = mockMvc.perform(requestBuilder);
        if (expectedStatus == HttpStatus.OK) {
            result.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
        }
        return result;
    }

    protected static <T> ResultMatcher jsonAt(String path, BiConsumer<Object, String>... ands) {
        return jsonAt(path, null, ands);
    }

    protected static <T> ResultMatcher jsonAt(String path, Matcher<T> matcher, BiConsumer<Object, String>... ands) {
        return (result) -> {
            Object jsonObject = Configuration.defaultConfiguration().jsonProvider().parse(result.getResponse().getContentAsString());
            T valueAtPath = valueAtPath(jsonObject, path);
            if (matcher != null) {
                assertThat(valueAtPath, matcher);
            }
            for (var and : ands) {
                and.accept(jsonObject, path);
            }
        };
    }

    protected <T> BiConsumer<Object, String> andAt(String path, BiConsumer<Object, String>... ands) {
        return andAt(path, null, ands);
    }

    protected <T> BiConsumer<Object, String> andAt(String path, Matcher<T> matcher, BiConsumer<Object, String>... ands) {
        return (jsonObject, basePath) -> {
            String finalPath = basePath + path;
            T valueAtPath = valueAtPath(jsonObject, finalPath);
            if (matcher != null) {
                assertThat(valueAtPath, matcher);
            }
            for (var and : ands) {
                and.accept(jsonObject, finalPath);
            }
        };
    }

    private static <T> T valueAtPath(Object jsonObject, String path) {
        T valueAtPath;
        try {
            valueAtPath = JsonPath.read(jsonObject, path);
        } catch (PathNotFoundException ex) {
            throw new AssertionFailedError(ex.getMessage());
        }
        return valueAtPath;
    }

    public MockMultipartFile testMultipartImage() throws Exception {
        ClassPathResource inputStreamSource = new ClassPathResource("/media/h160.jpg");
        return new MockMultipartFile("file", "filename.png", "image/png", inputStreamSource.getInputStream());
    }

    public ResultActions checkMedia() throws Exception {
        return this.abstractCheck()
                .andExpect(jsonPath("$.filename", notNullValue()))
                .andExpect(jsonPath("$.uuid", notNullValue()));
    }

    public void checkMedias() throws Exception{
        this.abstractCheck().andExpect(jsonPath("$.medias[0].filename", notNullValue()))
                .andExpect(jsonPath("$.medias[0].uuid", notNullValue()));
    }

    public void checkFunctionalException(String messageCode) throws Exception{
        abstractCheck().andExpect(jsonPath("$.messages").isNotEmpty())
                .andExpect(jsonPath("$.messages").value(accessor.getMessage(messageCode)));
    }

    protected void runInTransaction(Runnable callback) {//TODO Factorize with BaseRepositoryTest
        runInTransactionAndReturn(() -> {
            callback.run();
            return null;
        });
    }

    protected <T extends Object> T runInTransactionAndReturn(Supplier<T> callback) {//TODO Factorize with BaseRepositoryTest
        return transactionTemplate.execute((status -> {
            return callback.get();
        }));
    }
}
