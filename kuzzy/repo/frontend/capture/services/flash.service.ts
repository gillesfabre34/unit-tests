import { SystemState } from '../../system/models/system-state.model';
import { GLOBAL } from '../../init/const/global.const';
import { Evolution } from '../models/evolution.model';
import { Mock } from '../models/mock.model';
import { InstanceService } from './instance.service';
import { Instance } from '../models/instance.model';
import { MockDependency } from '../models/mock-dependency.model';
import { Mutation } from '../models/mutation.model';
import { KeyValue } from '../../flag/models/key-value.model';
import { getMockInLastSystemStateWithInstanceId, getMockInStoryWithInstanceId } from '../../../shared/utils/global.util';
import { getInstanceId } from '../../../shared/utils/reflect.util';
import { isPrimitive } from '../../../shared/utils/primitives.util';

export class FlashService {

    // -----------------------------------   START OF SUPER FLASH   ---------------------------------------

    static superFlash(): SystemState {
        const evolutions: Evolution<Mock>[] = [];
        const systemState = new SystemState();
        for (const instance of GLOBAL.dynamicAppState.instances) {
            this.addMockInSystemStateAndAddEvolution(systemState, instance, evolutions);
        }
        GLOBAL.story.addSystemState(systemState);
        return systemState;
    }


    private static addMockInSystemStateAndAddEvolution(systemState: SystemState, instance: Instance, evolutions: Evolution<Mock>[]): void {
        const lastMock: Mock = getMockInLastSystemStateWithInstanceId(instance.id);
        const newEvolution = new Evolution(lastMock?.mockObject, instance.concreteObject, []);
        if (newEvolution.hasMutations) {
            this.addNewMockToSystemStateAndAddEvolution(systemState, instance, evolutions, lastMock, newEvolution);
        } else {
            systemState.addMock(lastMock);
        }
    }


    private static addNewMockToSystemStateAndAddEvolution(systemState: SystemState, instance: Instance, evolutions: Evolution<Mock>[], lastMock: Mock, newEvolution: Evolution<Mock>): void {
        const newMock: Mock = this.addNewOrMutatedMock(instance, lastMock, newEvolution);
        systemState.addMock(newMock);
        evolutions.push(newEvolution);
        GLOBAL.addMock(newMock);
    }


    private static addNewOrMutatedMock(instance: Instance, lastMock: Mock, evolution: Evolution<Mock>): Mock {
        const newMock = new Mock(instance);
        newMock.mocksDependencies = lastMock.mocksDependencies;
        for (const mutation of evolution.mutations) {
            this.addKeyValueToMockMutations(newMock, mutation);
        }
        newMock.setMutationsCode();
        return newMock;
    }


    private static addKeyValueToMockMutations(mock: Mock, mutation: Mutation): void {
        if (!mutation.finalValue || isPrimitive(mutation.finalValue)) {
            mock.mutationsKeyValues.push(new KeyValue(mutation.key, mutation.finalValue));
        } else {
            const id = getInstanceId(mutation.finalValue);
            if (id) {
                const lastMock: Mock = GLOBAL.story.getLastMockWithInstanceId(id);
                const mockDependency = lastMock.associatedMockDependency;
                mock.addMockDependency(mockDependency);
                mock.mutationsKeyValues.push(new KeyValue(mutation.key, lastMock));
            } else if (Array.isArray(mutation.finalValue)) {
                const elements: any[] = [];
                for (const finalValue of mutation.finalValue) {
                    if (finalValue instanceof MockDependency) {
                        mock.addMockDependency(finalValue);
                    }
                    elements.push(finalValue);
                }
                mock.mutationsKeyValues.push(new KeyValue(mutation.key, elements));
            }
        }
    }

    // -----------------------------------   END OF SUPER FLASH   ---------------------------------------


    static addMockDependencies(mockInstance: any, parameters: any[]) {
        const parametersInstances: Instance[] = parameters.filter(p => InstanceService.isInstance(p));
        const mock: Mock = getMockInStoryWithInstanceId(getInstanceId(mockInstance));
        for (const parameterInstance of parametersInstances) {
            const mockParameter: Mock = getMockInStoryWithInstanceId(getInstanceId(parameterInstance));
            mock.mocksDependencies.push(mockParameter.associatedMockDependency);
        }
    }

}
