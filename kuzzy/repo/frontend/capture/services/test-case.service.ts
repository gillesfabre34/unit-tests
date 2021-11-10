import { TestCase } from '../models/test-case.model';
import { SystemState } from '../../system/models/system-state.model';
import { addTestCaseToStory } from '../../../shared/utils/global.util';

export class TestCaseService {

    static create(methodName: string, methodClass: any, returnedValue: any, systemStateBeforeCall: SystemState, systemStateAfterFlash: SystemState, methodInstancePath: string, parametersValues: any[] = []): TestCase {
        const testCase = new TestCase(methodName, methodClass, returnedValue, systemStateBeforeCall, systemStateAfterFlash, parametersValues);
        addTestCaseToStory(testCase);
        testCase.save().then(testCaseEntity => {
            // console.log(chalk.cyanBright('TEST CASE ENTITY SAVED'), testCaseEntity);
        })
        return testCase;
    }

}
