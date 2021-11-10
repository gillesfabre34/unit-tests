import { ImportLine } from '../../write/models/import-line.model';
import { Mock } from './mock.model';
import { ImportLineService } from '../../write/services/import-line.service';
import { SystemState } from '../../system/models/system-state.model';
import { ClassEnum } from '../../flag/models/class-enum.model';
import { ClassEnumService } from '../../flag/services/class-enum.service';
import { TestCaseEntity } from '../../db/entities/test-case.entity';
import { StatementUT } from '../../system/models/statement-ut.model';
import { KeyValueEntityService } from '../../db/services/key-value-entity.service';
import { MockEntityService } from '../../db/services/mock-entity.service';
import { StatementUtEntityService } from '../../db/services/statement-ut-entity.service';
import { ParameterEntityService } from '../../db/services/parameter-entity.service';
import { getInstanceId } from '../../../shared/utils/reflect.util';
import { getModuleSpecifier } from '../../utils/file-system.util';
import { getMockInLastSystemStateWithInstanceId } from '../../../shared/utils/global.util';
import { getMethodStatementUTs } from '../../../shared/utils/system-ut.util';
import { PostTestCaseDtoMock, TestCaseDto } from '../../../dtos/test-cases/test-case.dto';
import { TestCaseDataService } from '../../api/services/test-case-data.service';
import { originalPath } from '../../utils/kuzzy-folder.util';

export class TestCase {

    entity: TestCaseEntity = undefined;
    importLines: ImportLine[] = [];
    methodName: string = undefined;
    mock: Mock = undefined;
    parametersValues: any[] = [];
    returnedValue: any = undefined;
    returnedValueClassEnum: ClassEnum = undefined;
    statementUTs: StatementUT[] = [];
    systemStateAfterFlash: SystemState = undefined;


// TODO: Add ClassEnums for method parameters
    constructor(methodName: string, callingInstance: any, returnedValue: any, systemStateBeforeCall: SystemState, systemStateAfterFlash: SystemState, parametersValues: any[] = []) {
        this.setMock(callingInstance, systemStateBeforeCall);
        this.methodName = methodName;
        this.returnedValue = returnedValue;
        this.systemStateAfterFlash = systemStateAfterFlash;
        this.setReturnedValueClassEnum(methodName, callingInstance);
        this.parametersValues = parametersValues;
        this.setImportLines();
        this.setReturnedValue(returnedValue);
        this.setStatementUTs();
    }


    get postTestCaseDtoMock(): PostTestCaseDtoMock {
        return {
            classId: this.mock.classId,
            code: this.mock.code,
            mockFilePath: originalPath(this.mock.fileUTPath),
        }
    }


    private setMock(callingInstance: any, systemStateBeforeCall: SystemState): void {
        this.mock = systemStateBeforeCall.getMockWithInstanceId(getInstanceId(callingInstance));
    }


    private setReturnedValueClassEnum(methodName: string, callingInstance: any): void {
        this.returnedValueClassEnum = ClassEnumService.getClassEnum(methodName, callingInstance['kuzzyEnums']);
    }


    private setImportLines(): void {
        this.importLines = [new ImportLine(getModuleSpecifier(this.mock.mockFilePath), [this.mock.name])];
        for (const mockDependency of this.mock.mocksDependencies) {
            ImportLineService.addImport(mockDependency.importDefaultClassUT, this.importLines);
            ImportLineService.addImport(mockDependency.importDefaultMockFile, this.importLines);
        }
    }


    private setReturnedValue(returnedValue: any): void {
        const instanceId: number = getInstanceId(returnedValue);
        if (instanceId) {
            const mock: Mock = getMockInLastSystemStateWithInstanceId(instanceId);
            this.returnedValue = mock.associatedMockDependency;
            if (mock.importDefaultMockFile) {
                ImportLineService.addImport(mock.importDefaultMockFile, this.importLines);
            }
        } else {
            this.returnedValue = returnedValue;
        }
    }


    private setStatementUTs(): void {
        const statementUTs: StatementUT[] = getMethodStatementUTs(this.methodName, this.mock.className, this.mock.fileUTPath);
        for (const statementUT of statementUTs.filter(s => s.isParsed === true)) {
            statementUT.cover();
            this.statementUTs.push(statementUT);
        }
    }


    // -------------------------------------------   Save to DB   ----------------------------------------------------


    async save(): Promise<void> {
        const testCaseDto: TestCaseDto = {
            mock: this.postTestCaseDtoMock,
            statementsIds: this.statementUTs.map(s => s.id)
        };
        if (this.returnedValue !== undefined && this.returnedValue !== null) {
            testCaseDto.returnedValue = KeyValueEntityService.create('returnedValue', this.returnedValue, this.returnedValueClassEnum);
        }
        // testCaseDto.parameters = ParameterEntityService.createParameterEntities(this.parametersValues);
        // testCaseDto.statementsIds = this.saveCoverage();
        TestCaseDataService.postTestCase(testCaseDto);
    }


    // async save(): Promise<TestCaseEntity> {
    //     this.entity = new TestCaseEntity();
    //     this.entity.mock = await MockEntityService.save(this.mock);
    //     this.entity.statementUTs = this.statementUTs.map(s => s.entity);
    //     if (this.returnedValue !== undefined && this.returnedValue !== null) {
    //         this.entity.returnedValue = await KeyValueEntityService.create('returnedValue', this.returnedValue, this.returnedValueClassEnum);
    //     }
    //     this.entity.parameters = await ParameterEntityService.createParameterEntities(this.parametersValues);
    //     await this.entity.save();
    //     await this.saveCoverage();
    //     return this.entity;
    // }


    private async saveCoverage(): Promise<void> {
        for (const statementUT of this.statementUTs) {
            const statementUTEntity = await StatementUtEntityService.findStatementEntity(statementUT);
            statementUTEntity.isCovered = true;
            statementUTEntity.testCase = this.entity;
            await statementUTEntity.save();
        }
    }
}
