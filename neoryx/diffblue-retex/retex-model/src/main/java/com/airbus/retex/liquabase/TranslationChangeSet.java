package com.airbus.retex.liquabase;

import liquibase.change.custom.CustomSqlChange;
import liquibase.database.Database;
import liquibase.exception.CustomChangeException;
import liquibase.exception.SetupException;
import liquibase.exception.ValidationErrors;
import liquibase.resource.ResourceAccessor;
import liquibase.statement.SqlStatement;
import liquibase.statement.core.RawSqlStatement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TranslationChangeSet implements CustomSqlChange {

    private String tableNames;

    /**
     * Value is set from xml.
     *
     * @param tableNames
     */
    public void setTableNames(String tableNames) {
        this.tableNames = tableNames;
    }

    @Override
    public SqlStatement[] generateStatements(Database database) throws CustomChangeException {
        List<String> tablesNames = Arrays.asList(this.tableNames.split(","));
        List<SqlStatement> statements = new ArrayList<>();

        tablesNames.forEach(tableName -> {
            tableName = tableName.trim();
            statements.add(new RawSqlStatement(TranslationSqlBuilder.generateCreateTranslationTable(tableName)));
            statements.add(new RawSqlStatement(TranslationSqlBuilder.generateCreateTranslationAuditTable(tableName)));
        });

        return statements.toArray(new SqlStatement[statements.size()]);
    }

    @Override
    public String getConfirmationMessage() {
        return "Translations table created for classes " + this.tableNames + " :)";
    }

    @Override
    public void setUp() throws SetupException {
        // nothing to do
    }

    @Override
    public void setFileOpener(ResourceAccessor resourceAccessor) {
        // nothing to do
    }

    @Override
    public ValidationErrors validate(Database database) {
        if (null == this.tableNames || this.tableNames.equals("")) {
            return new ValidationErrors().addError("Missing or empty 'tableNames' param.");
        }

        return null;
    }
}
