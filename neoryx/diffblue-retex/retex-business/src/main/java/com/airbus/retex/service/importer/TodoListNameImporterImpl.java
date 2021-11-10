package com.airbus.retex.service.importer;

import java.util.Optional;

import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.airbus.retex.business.dto.todoListName.TodoListNameFieldsEnum;
import com.airbus.retex.model.common.Language;
import com.airbus.retex.model.operation.OperationType;
import com.airbus.retex.model.task.TodoListName;
import com.airbus.retex.persistence.operationType.OperationTypeRepository;
import com.airbus.retex.persistence.todoList.TodoListNameRepository;
import com.airbus.retex.service.importer.exception.DataImporterException;
import com.airbus.retex.service.importer.exception.EntryAlreadyExistsException;
import com.airbus.retex.service.importer.exception.InvalidEntryException;
import com.airbus.retex.service.translate.ITranslateService;

@Component
@Order(100)
public class TodoListNameImporterImpl extends AbstractDataImporter {

    @Autowired
    private ITranslateService translateService;
    @Autowired
    private TodoListNameRepository todoListNameRepository;
    @Autowired
    private OperationTypeRepository operationTypeRepository;

    private static final String FIELD_NAME_FR = "name_FR";
    private static final String FIELD_NAME_EN = "name_EN";
    private static final String OPERATION_TYPE_NAME_EN = "operation_type_name_EN";

    public TodoListNameImporterImpl() {
        super("todo_list_name.csv", new String[]{FIELD_NAME_FR, FIELD_NAME_EN, OPERATION_TYPE_NAME_EN});
    }

    /**
     * @param row
     */
    @Override
    protected void persistBatch(CSVRecord row) throws DataImporterException {
        String operationTypeName = row.get(OPERATION_TYPE_NAME_EN);
        Optional<OperationType> operationTypeOpt = operationTypeRepository.findOne(
                translateFilter(OperationType.FIELD_NAME, Language.EN, operationTypeName));
        if (!operationTypeOpt.isPresent()) {
            throw new DataImporterException("Can't found operation type: " + operationTypeName);
        }

        String nameFr = getAndCheckUniquenessOfTranslatedField(row, FIELD_NAME_FR, Language.FR, TodoListNameFieldsEnum.name.name());
        String nameEn = getAndCheckUniquenessOfTranslatedField(row, FIELD_NAME_EN, Language.EN, TodoListNameFieldsEnum.name.name());

        TodoListName todoListName = new TodoListName();
        todoListName.setOperationTypeId(operationTypeOpt.get().getId());
        todoListNameRepository.save(todoListName);
        translateService.saveFieldValue(todoListName, TodoListNameFieldsEnum.name.name(), Language.FR, nameFr);
        translateService.saveFieldValue(todoListName, TodoListNameFieldsEnum.name.name(), Language.EN, nameEn);
    }

    protected String getAndCheckUniquenessOfTranslatedField(CSVRecord row, String rowField, Language lang, String field) throws DataImporterException {
        String value = row.get(rowField);
        if (value.isBlank()) {
            throw new InvalidEntryException(String.format("%s=%s", rowField, value));
        }
        if (!todoListNameRepository.findAll(translateFilter(field, lang, value)).isEmpty()) {
            throw new EntryAlreadyExistsException(String.format("%s=%s", rowField, value));
        }
        return value;
    }
}
