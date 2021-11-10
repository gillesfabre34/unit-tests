package com.airbus.retex.liquabase;

class TranslationSqlBuilder {

	/**
	 * private TranslationSqlBuilder
	 */
	private TranslationSqlBuilder() {

	}

    /**
     * @param baseTable
     * @return
     */
    public static String generateCreateTranslationTable(String baseTable) {
        return "CREATE TABLE IF NOT EXISTS " + baseTable + "_translation (" +
                "id BIGINT AUTO_INCREMENT NOT NULL, " +
                "language VARCHAR(50) NOT NULL, " +
                "field VARCHAR(50) NOT NULL, " +
                "value VARCHAR(255) NOT NULL, " +
                "entity_id BIGINT NOT NULL, " +
                "CONSTRAINT PK_" + baseTable.toUpperCase() + "_TRANSLATIONS PRIMARY KEY (id), " +
                "CONSTRAINT fk_" + baseTable +"_translations_id FOREIGN KEY (entity_id) REFERENCES " + baseTable + "(id)" +
                ")";
    }

    /**
     * @param baseTable
     * @return
     */
    public static String generateCreateTranslationAuditTable(String baseTable) {
        return "CREATE TABLE IF NOT EXISTS " + baseTable + "_translation_audit (" +
                "id BIGINT NOT NULL, " +
                "revision_id INT NOT NULL, " +
                "revision_type SMALLINT NULL, " +
                "language VARCHAR(50), " +
                "field VARCHAR(50), " +
                "value VARCHAR(255), " +
                "entity_id BIGINT, " +
                "CONSTRAINT PK_" + baseTable.toUpperCase() + "_TRANSLATIONS_AUDIT PRIMARY KEY (id, revision_id) " +
                ")";
    }
}
