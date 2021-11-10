import { SystemState } from './system-state.model';
import { Instance } from '../../capture/models/instance.model';
import { Mock } from '../../capture/models/mock.model';
import { GLOBAL } from '../../init/const/global.const';
import { TestCase } from '../../capture/models/test-case.model';
import { flat, lastElement } from '../../../shared/utils/arrays.util';

export class Story {

    systemStates: SystemState[] = [];

    constructor(systemState: SystemState) {
        this.systemStates = [systemState];
    }


    get lastSystemState(): SystemState {
        return lastElement(this.systemStates);
    }


    get mocks(): Mock[] {
        return flat(this.systemStates.map(s => s.mocks));
    }


    get testCases(): TestCase[] {
        return this.systemStates.filter(s => !!s.testCase).map(s => s.testCase);
    }


    addSystemState(systemState: SystemState): Story {
        this.systemStates.push(systemState);
        return this;
    }


    addNewSystemStateForNewInstance(instance: Instance): void {
        const systemState: SystemState = this.lastSystemState.clone();
        const mock = new Mock(instance);
        systemState.addMock(mock);
        GLOBAL.story.addSystemState(systemState);
        GLOBAL.addMock(mock);
    }


    getLastMockWithInstanceId(instanceId: number): Mock {
        return this.mocks.filter(m => m.instanceId === instanceId)
            .sort((a, b) => { return b.instanceId - a.instanceId; })[0];
    }

}
