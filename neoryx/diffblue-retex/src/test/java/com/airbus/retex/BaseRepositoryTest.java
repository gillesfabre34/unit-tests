package com.airbus.retex;

import com.airbus.retex.dataset.Dataset;
import com.airbus.retex.dataset.DatasetInitializer;
import com.airbus.retex.dataset.DatasetTestExecutionListener;
import com.airbus.retex.dataset.IDatasetAware;
import liquibase.integration.spring.SpringLiquibase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockitoTestExecutionListener;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.AbstractTestExecutionListener;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import javax.persistence.EntityManager;
import java.util.function.Supplier;

@DisplayName("initialization for repository tests")
@SpringBootTest
@TestExecutionListeners(value = {
        DatasetTestExecutionListener.class,
        DependencyInjectionTestExecutionListener.class,
        MockitoTestExecutionListener.class,
        TransactionalTestExecutionListener.class})
@ActiveProfiles("test,debug")
@AutoConfigureMockMvc
public abstract class BaseRepositoryTest extends AbstractTestExecutionListener implements IDatasetAware {

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    protected EntityManager entityManager;

    @Autowired
    private SpringLiquibase springLiquibase;

    @Autowired
    public DatasetInitializer dataSetInitializer;

    protected Dataset dataset;

    @Autowired
    private PlatformTransactionManager platformTransactionManager;

    private TransactionTemplate transactionTemplate;

    @Override
    public void setDataset(Dataset dataset) {
        this.dataset = dataset;
    }

    @BeforeEach
    public void beforeEachTest() {
        cacheManager.getCacheNames().parallelStream().forEach(name -> cacheManager.getCache(name).clear());
        transactionTemplate = new TransactionTemplate(platformTransactionManager);
    }

    protected void runInTransaction(Runnable callback) {
        runInTransactionAndReturn(() -> {
            callback.run();
            return null;
        });
    }

    protected <T extends Object> T runInTransactionAndReturn(Supplier<T> callback) {
        return transactionTemplate.execute((status -> {
            return callback.get();
        }));
    }
}
