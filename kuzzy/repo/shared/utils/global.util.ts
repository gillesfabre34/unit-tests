import { Mock } from '../../frontend/capture/models/mock.model';
import { GLOBAL } from '../../frontend/init/const/global.const';
import { TestCase } from '../../frontend/capture/models/test-case.model';

export function getMockInLastSystemStateWithInstanceId(instanceId: number): Mock {
    return GLOBAL.story.lastSystemState.getMockWithInstanceId(instanceId);
}

export function getMockInStoryWithInstanceId(instanceId: number): Mock {
    return GLOBAL.story.mocks.find(m => m.instanceId === instanceId);
}


export function addTestCaseToStory(testCase: TestCase): void {
    GLOBAL.story.lastSystemState.testCase = testCase;
}
