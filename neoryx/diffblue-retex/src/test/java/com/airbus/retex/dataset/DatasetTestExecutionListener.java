package com.airbus.retex.dataset;

import lombok.extern.slf4j.Slf4j;
import org.h2.store.fs.FileUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestExecutionListener;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

@Slf4j
public class DatasetTestExecutionListener implements TestExecutionListener {

    public static final String DUMP_SCHEMA_FILE = "memFS:h2-schema-backup.sql";

    public static final String DUMP_DATASET_FILE = "memFS:h2-dataset-backup.sql";

    private ByteArrayOutputStream serializedDataset = null;

    @Override
    public void beforeTestMethod(TestContext testContext) throws Exception {
        //FIXME Optimize : it's not well integrated with the test execution flow and not yet full optimized
        boolean schemaBackupExists = FileUtils.exists(DUMP_SCHEMA_FILE);
        if(!schemaBackupExists) {
            backupDatabase(testContext, DUMP_SCHEMA_FILE);
        }

        ApplicationContext appContext = testContext.getApplicationContext();
        IDatasetInitializer initializer = appContext.getBean(IDatasetInitializer.class);

        Dataset dataset;
        if(serializedDataset == null) {
            dataset = new Dataset();
            restoreDatabase(testContext, DUMP_SCHEMA_FILE);

            initializer.setDataset(dataset);
            initializer.initDataset();

            backupDatabase(testContext, DUMP_DATASET_FILE);
            serializedDataset = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(serializedDataset);
            out.writeObject(dataset);
            out.close();
        } else {
            restoreDatabase(testContext, DUMP_DATASET_FILE);
            ByteArrayInputStream bis = new ByteArrayInputStream(serializedDataset.toByteArray());
            ObjectInputStream in = new ObjectInputStream(bis);
            dataset = (Dataset) in.readObject();
            in.close();
        }
        initializer.setDataset(dataset);

        Object testInstance = testContext.getTestInstance();
        if(IDatasetAware.class.isAssignableFrom(testInstance.getClass())) {
            ((IDatasetAware)testInstance).setDataset(dataset);
        }
    }

    private void cleanDatabase(TestContext testContext) {
        //FIXME log.info("Clean H2 database");
        executeSql(testContext, "DROP ALL OBJECTS");
    }

    private void backupDatabase(TestContext testContext, String dump) {
        //FIXME log.info("Backup H2 database ("+dump+")");
        executeSql(testContext, "SCRIPT TO '"+dump+"'");
    }

    private void restoreDatabase(TestContext testContext, String dump) {
        cleanDatabase(testContext);
        //FIXME log.info("Restore H2 database ("+dump+")");
        executeSql(testContext, "RUNSCRIPT FROM '"+dump+"'");
    }

    private void executeSql(TestContext testContext, String... sqlLines){
        JdbcTemplate jdbcTemplate = getJdbcTemplate(testContext);
        for(var sql : sqlLines) {
            jdbcTemplate.execute(sql);
        }
    }

    private JdbcTemplate getJdbcTemplate(TestContext testContext) {
        return testContext.getApplicationContext().getBean(JdbcTemplate.class);
    }

}
