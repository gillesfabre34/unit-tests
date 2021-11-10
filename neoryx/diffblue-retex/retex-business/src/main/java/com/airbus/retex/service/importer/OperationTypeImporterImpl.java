package com.airbus.retex.service.importer;

import com.airbus.retex.model.common.Language;
import com.airbus.retex.model.operation.OperationType;
import com.airbus.retex.model.operation.OperationTypeBehaviorEnum;
import com.airbus.retex.persistence.operationType.OperationTypeRepository;
import com.airbus.retex.service.importer.exception.DataImporterException;
import com.airbus.retex.service.importer.exception.EntryAlreadyExistsException;
import com.airbus.retex.service.importer.exception.InvalidEntryException;
import com.airbus.retex.service.translate.ITranslateService;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(60)
public class OperationTypeImporterImpl extends AbstractDataImporter {


    @Autowired
    private ITranslateService translateService;
    @Autowired
    private OperationTypeRepository operationTypeRepository;

    private static final String FIELD_NAME_FR = "name_FR";
    private static final String FIELD_NAME_EN = "name_EN";
    private static final String FIELD_BEHAVIOR = "behavior";
    private static final String FIELD_TEMPLATE = "template";

    public OperationTypeImporterImpl() {
        super("operation_type.csv", new String[]{FIELD_NAME_FR, FIELD_NAME_EN});
    }


    /**
     * @param row
     */
    @Override
    protected void persistBatch(CSVRecord row) throws DataImporterException {

        String template = row.get(FIELD_TEMPLATE);
        if (operationTypeRepository.findByTemplate(template).isPresent()) {
            throw new EntryAlreadyExistsException(row.toMap());
        }
        String nameFr = getAndCheckUniquenessOfTranslatedField(row, FIELD_NAME_FR, Language.FR, OperationType.FIELD_NAME);
        String nameEn = getAndCheckUniquenessOfTranslatedField(row, FIELD_NAME_EN, Language.EN, OperationType.FIELD_NAME);

        OperationType operationType = new OperationType();
        operationType.setTemplate(template);
        operationType.setBehavior(OperationTypeBehaviorEnum.valueOf(row.get(FIELD_BEHAVIOR)));
        operationTypeRepository.save(operationType);
        translateService.saveFieldValue(operationType, OperationType.FIELD_NAME, Language.FR, nameFr);
        translateService.saveFieldValue(operationType, OperationType.FIELD_NAME, Language.EN, nameEn);
    }

    protected String getAndCheckUniquenessOfTranslatedField(CSVRecord row, String rowField, Language lang, String field) throws DataImporterException {
        String value = row.get(rowField);
        if (value.isBlank()) {
            throw new InvalidEntryException(String.format("%s=%s", rowField, value));
        }
        if (operationTypeRepository.findOne(translateFilter(field, lang, value)).isPresent()) {
            throw new EntryAlreadyExistsException(String.format("%s=%s", rowField, value));
        }
        return value;
    }
}
