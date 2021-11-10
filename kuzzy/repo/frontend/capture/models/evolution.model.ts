import { Mutation } from './mutation.model';
import { GLOBAL } from '../../init/const/global.const';
import { MockDependency } from './mock-dependency.model';
import { isSameObject } from '../../../shared/utils/objects.util';
import { getInstanceId } from '../../../shared/utils/reflect.util';
import { prettify } from '../../../shared/utils/prettify.util';

export class Evolution<T> {

    finalState: T = undefined;
    initialState: T = undefined;
    mutations: Mutation[] = [];
    name: string = undefined;

    constructor(initialState: T, finalState: T, ignoredKeys: string[] = []) {
        this.initialState = initialState;
        this.finalState = finalState;
        this.setMutations(ignoredKeys);
    }


    get hasMutations(): boolean {
        return this.mutations.length > 0;
    }


    setMutations(ignoredKeys: string[]): void {
        this.mutations = this.getMutations(this.initialState, this.finalState);
    }


    private getMutations<T>(initialState: T, finalState: T, propertyPath = ''): Mutation[] {
        if (isSameObject(initialState, finalState)) {
            return [];
        }
        const mutations: Mutation[] = [];
        for (const key of this.getKeys(initialState, finalState)) {
            this.addMutationForKey(mutations, key, initialState?.[key], finalState?.[key], propertyPath);
        }
        return mutations;
    }


    private addMutationForKey(mutations: Mutation[], key: string, valueOfInitialStateForThisKey: any, valueOfFinalStateForThisKey: any, propertyPath = ''): void {
        if (!isSameObject(valueOfInitialStateForThisKey, valueOfFinalStateForThisKey)) {
            const id: number = getInstanceId(valueOfFinalStateForThisKey);
            if (id) {
                this.addMockDependencyMutation(mutations, key, valueOfInitialStateForThisKey, id);
            } else {
                this.addOtherKindOfMutation(mutations, key, valueOfInitialStateForThisKey, valueOfFinalStateForThisKey, propertyPath);
            }
        }
    }


    private addMockDependencyMutation(mutations: Mutation[], key: string, value: any, instanceId: number): void {
        const mockDependency = this.getMockDependencyForLastMockWithInstanceId(instanceId);
        mutations.push(new Mutation(key, value, mockDependency));
    }


    private addOtherKindOfMutation(mutations: Mutation[], key: string, initialValueForThisKey: any, finalValueForThisKey: any, propertyPath: string): void {
        if (Array.isArray(finalValueForThisKey)) {
            this.addMutationForArrayOfFinalValues(mutations, key, initialValueForThisKey, finalValueForThisKey);
        } else if (typeof finalValueForThisKey === 'object') {
            mutations.push(...this.getMutations(initialValueForThisKey, finalValueForThisKey, `${propertyPath}.${key}`));
        } else {
            mutations.push(new Mutation(key, initialValueForThisKey, finalValueForThisKey));
        }
    }


    private addMutationForArrayOfFinalValues(mutations: Mutation[], key: string, initialValueForThisKey: any, finalValueForThisKey: any): void {
        const finalValue: any[] = [];
        for (const element of finalValueForThisKey) {
            const elementId: number = getInstanceId(element);
            if (elementId) {
                const mockDependency = this.getMockDependencyForLastMockWithInstanceId(elementId);
                finalValue.push(mockDependency);
            } else {
                finalValue.push(prettify(element));
            }
        }
        mutations.push(new Mutation(key, initialValueForThisKey, finalValue));

    }


    private getMockDependencyForLastMockWithInstanceId(instanceId: number): MockDependency {
        return GLOBAL.story.getLastMockWithInstanceId(instanceId).associatedMockDependency;
    }


    expectMutations(name: string, ignoredKeys: string[] = []): string[] {
        this.name = name;
        return this.getExpectMutations(this.initialState, this.finalState, ignoredKeys);
    }


    private getExpectMutations<T>(initialState: T, finalState: T, ignoredKeys: string[], propertyPath = ''): string[] {
        if (isSameObject(initialState, finalState)) {
            return [];
        }
        const expectMutations: string[] = [];
        for (const key of this.getKeys(initialState, finalState, ignoredKeys)) {
            if (!isSameObject(initialState?.[key], finalState?.[key]) && key !== 'kuzzyChain') {
                if (Array.isArray(initialState?.[key])) {
                    for (let i = 0; i < Math.max(initialState?.[key]?.length, finalState?.[key]?.length); i++) {
                        expectMutations.push(...this.getExpectMutations(initialState?.[key]?.[i], finalState?.[key]?.[i], ignoredKeys, `${propertyPath}.${key}[${i}]`));
                    }
                } else if (typeof initialState?.[key] === 'object') {
                    expectMutations.push(...this.getExpectMutations(initialState?.[key], finalState?.[key], ignoredKeys, `${propertyPath}.${key}`));
                } else {
                    expectMutations.push(`expect(${this.name}${propertyPath}.${key}).toEqual(${prettify(finalState?.[key])});`);
                }
            }
        }
        return expectMutations;
    }


    private getKeys(initialState: any, finalState: any, ignoredKeys: any[] = []): Set<string> {
        let keysSet = new Set<string>(this.getStateKey(initialState).concat(this.getStateKey(finalState)));
        for (const key of ignoredKeys) {
            keysSet.delete(key);
        }
        return keysSet;
    }


    private getStateKey(state: any): string[] {
        return state && typeof state === 'object' ? Object.keys(state) : [];
    }


}
