import { Mock } from '../../capture/models/mock.model';
import { GLOBAL } from '../../init/const/global.const';
import { TestCase } from '../../capture/models/test-case.model';
import { lastElement } from '../../../shared/utils/arrays.util';

export class SystemState {

    id: number = undefined;
    mocks: Mock[] = [];
    testCase: TestCase = undefined;

    constructor() {
        this.setId();
    }


    addMock(mock: Mock): SystemState {
        this.mocks.push(mock);
        return this;
    }


    clone(): SystemState {
        const systemState = new SystemState();
        for (const mock of this.mocks) {
            systemState.mocks.push(mock);
        }
        return systemState;
    }


    getMockWithInstanceId(instanceId: number): Mock {
        return this.mocks.find(m => m?.instanceId === instanceId);
    }


    setId(): void {
        this.id = lastElement(GLOBAL.story?.systemStates) ? lastElement(GLOBAL.story.systemStates.map(e => e.id)) + 1 : 0;
    }

}
