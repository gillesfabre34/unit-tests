import { IndexesChoices } from '../../../constraints/interfaces/indexes-choices.interface';
import { TestSuite } from '../../../../languages/ts/test-suites/generation/models/test-suite.model';
import { SutMethod } from '../../../../languages/ts/test-suites/sut/models/sut-method.model';
import { InputConstraints } from '../../../constraints/models/input-constraints.model';
import { PropertyValue } from '../models/property-value.model';
import { MethodStatsService } from '../../../reports/dashboard/services/method-stats.service';
import { Bug } from '../../../reports/core/bugs/models/bug.model';
import { AgnosticSutMethod } from '../../sut/models/agnostic-sut-method.model';
import { BugType } from '../../../reports/core/bugs/enums/bug-type.enum';

export class TestSuiteService {


    /**
     * Returns the next indexes from a given last choices array in a given array of IndexesChoices
     * Example :
     * If lastChosenIndexes = [3, 0, 0], it will returns the next choice found in indexesByPriorityOrder, which is [0, 3, 0]
     * @param indexesByPriorityOrder
     * @param lastChosenIndexes
     */
    nextIndexes(indexesByPriorityOrder: IndexesChoices[], lastChosenIndexes: number[]): number[] {
        if (!Array.isArray(indexesByPriorityOrder) || !lastChosenIndexes) {
            throw Error('Impossible to get next choice of indexes : indexes array is undefined.');
        }
        if (this.allElementsAreTheSame(lastChosenIndexes)) {
            return this.returnFirstChoiceWithHigherPriorityOrder(indexesByPriorityOrder, lastChosenIndexes);
        }
        return this.returnNextChoiceWithSamePriorityOrder(indexesByPriorityOrder, lastChosenIndexes);
    }


    /**
     * Checks if all elements of an array of numbers have the same value
     * @param numbers
     * @private
     */
    private allElementsAreTheSame(numbers: number[]): boolean {
        return numbers?.length > 0 ? Math.max(...numbers) === Math.min(...numbers) : true;
    }


    /**
     * Returns, for example, [3, 0, 0] when it receives [2, 2, 2]
     * @param numbers
     * @private
     */
    private returnFirstChoiceWithHigherPriorityOrder(indexesByPriorityOrder: IndexesChoices[], numbers: number[]): number[] {
        const higherPriorityOrder = numbers.length > 0 ? numbers[0] + 1 : 0;
        const firstChoiceWithHigherPriorityOrder = indexesByPriorityOrder.find(i => i.priorityOrder === higherPriorityOrder);
        if (firstChoiceWithHigherPriorityOrder) {
            return indexesByPriorityOrder.find(i => i.priorityOrder === higherPriorityOrder).choices[0];
        } else {
            return undefined;
        }
    }


    private returnNextChoiceWithSamePriorityOrder(indexesByPriorityOrder: IndexesChoices[], lastChosenIndexes: number[]): number[] {
        const lastChosenIndexesPriorityOrder: number = Math.max(...lastChosenIndexes);
        const choicesWithSamePriorityOrder: number[][] = indexesByPriorityOrder.find(i => i.priorityOrder === lastChosenIndexesPriorityOrder).choices;
        const indexOfLastChosenIndexesArray: number = choicesWithSamePriorityOrder.findIndex(c => c === lastChosenIndexes);
        return choicesWithSamePriorityOrder[indexOfLastChosenIndexesArray + 1];
    }


    getTestSuite(inputsConstraints: InputConstraints[] = [], chosenIndexes: number[], sutMethod: AgnosticSutMethod): TestSuite {
        let testSuite = new TestSuite(sutMethod as SutMethod);
        if (!chosenIndexes) {
            MethodStatsService.addBug(new Bug(BugType.NO_INDEXES_CHOSEN), sutMethod?.sutFile?.path, sutMethod?.name, sutMethod?.sutClass?.name, sutMethod?.isFunction);
            // return testSuite;
            return undefined;
        }
        for (let i = 0; i < inputsConstraints.length && i < chosenIndexes.length; i++) {
            testSuite = this.addPropertyValue(testSuite, inputsConstraints[i], chosenIndexes[i]);
        }
        return testSuite;
    }


    private addPropertyValue(testSuite: TestSuite, inputConstraints: InputConstraints, chosenIndex: number): TestSuite {
        if (inputConstraints.name !== undefined) {
            if (inputConstraints?.isInstanceProperty) {
                testSuite.instanceProperties.push(new PropertyValue(inputConstraints.name, inputConstraints.pseudoRandomValues[chosenIndex]));
            } else {
                testSuite.parameters.push(new PropertyValue(inputConstraints.name, inputConstraints.pseudoRandomValues[chosenIndex]));
            }
        }
        return testSuite;
    }

}
