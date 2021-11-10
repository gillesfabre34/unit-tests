import { IndexesChoices } from '../interfaces/indexes-choices.interface';
import { SourceTarget } from '../interfaces/source-target.interface';
import { arrayOfNumbersAreEqual } from '../../tools/utils/arrays.util';
import { DEBUG } from '../../init/constants/debug.const';
import * as chalk from 'chalk';

/**
 * This service is used to choose indexes by priority order of an array of arrays (the first index have the highest priority order)
 *
 * Example with an array of 3 arrays : [[5, 6, 4, 7], ['Léa', 'Léaa', 'Lé', 'Léaaa', 'L'], [0, 1, -1, 2, -2, 3, -3, 4]]
 *
 * Choices of priority 0 : 1 element
 * [ [ 0, 0, 0 ] ]
 * Choices of priority 1 : 7 elements
 * [
 *   [ 1, 0, 0 ],
 *   [ 0, 1, 0 ],
 *   [ 0, 0, 1 ],
 *   [ 1, 1, 0 ],
 *   [ 1, 0, 1 ],
 *   [ 0, 1, 1 ],
 *   [ 1, 1, 1 ]
 * ]
 * Choices of priority 2 : 19 elements
 * [
 *   [ 2, 0, 0 ], [ 0, 2, 0 ],
 *   [ 0, 0, 2 ], [ 2, 1, 0 ],
 *   [ 2, 0, 1 ], [ 1, 2, 0 ],
 *   [ 0, 2, 1 ], [ 1, 0, 2 ],
 *   [ 0, 1, 2 ], [ 2, 1, 1 ],
 *   [ 1, 2, 1 ], [ 1, 1, 2 ],
 *   [ 2, 2, 0 ], [ 2, 0, 2 ],
 *   [ 0, 2, 2 ], [ 2, 2, 1 ],
 *   [ 2, 1, 2 ], [ 1, 2, 2 ],
 *   [ 2, 2, 2 ]
 * ]
 * Choices of priority 3 : 37 elements
 * [
 *   [ 3, 0, 0 ], [ 0, 3, 0 ], [ 0, 0, 3 ],
 *   [ 3, 1, 0 ], [ 3, 0, 1 ], [ 1, 3, 0 ],
 *   [ 0, 3, 1 ], [ 1, 0, 3 ], [ 0, 1, 3 ],
 *   [ 3, 1, 1 ], [ 1, 3, 1 ], [ 1, 1, 3 ],
 *   [ 3, 2, 0 ], [ 3, 0, 2 ], [ 2, 3, 0 ],
 *   [ 0, 3, 2 ], [ 2, 0, 3 ], [ 0, 2, 3 ],
 *   [ 3, 2, 1 ], [ 3, 1, 2 ], [ 2, 3, 1 ],
 *   [ 1, 3, 2 ], [ 2, 1, 3 ], [ 1, 2, 3 ],
 *   [ 3, 2, 2 ], [ 2, 3, 2 ], [ 2, 2, 3 ],
 *   [ 3, 3, 0 ], [ 3, 0, 3 ], [ 0, 3, 3 ],
 *   [ 3, 3, 1 ], [ 3, 1, 3 ], [ 1, 3, 3 ],
 *   [ 3, 3, 2 ], [ 3, 2, 3 ], [ 2, 3, 3 ],
 *   [ 3, 3, 3 ]
 * ]
 *
 */
export class IndexesChoiceService {

    limitsChoices: number[];                            // Temporary variable storing the current choice of indexes
    maxChoices = -1;                                    // Temporary variable
    nextChoices: number[];                              // Temporary variable storing the next choice of indexes
    restOfLowProperties: number[] = undefined;          // Temporary variable

    /**
     * Returns the array of IndexesChoices ordered by priority
     * @param numberOfArrays        The number of arrays in the main array
     * @param maxPriorityOrder      Stops the process for this priority order
     */
    getIndexesByPriorityOrder(numberOfArrays: number, maxPriorityOrder: number): IndexesChoices[] {
        if (!numberOfArrays || !maxPriorityOrder) {
            return [];
        }
        let priorityOrder = 0;
        this.limitsChoices = this.initLimitsChoices(numberOfArrays);
        this.nextChoices = this.limitsChoices;
        const allChoices: IndexesChoices[] = [];
        while (priorityOrder < maxPriorityOrder) {
            allChoices.push(this.addChoicesByPriorityOrder(priorityOrder));
            priorityOrder++;
        }
        return allChoices;
    }


    /**
     * merge IndexesChoices array to only get choices without take care of priority
     * @param numberOfInputs        The number of the elements of the array including the other arrays
     * @param maxPriorityOrder      Stops the process for this priority order
     */
    getIndexesWithoutPriorityOrder(numberOfInputs: number, maxPriorityOrder: number): number[][] {
        const INDEXES_CHOICES: number[][] = [];
        this.getIndexesByPriorityOrder(numberOfInputs, maxPriorityOrder).forEach((ic: IndexesChoices) => {
            INDEXES_CHOICES.push(...ic.choices);
        });
        return INDEXES_CHOICES;
    }


    /**
     * Returns the array of IndexesChoices for a given priority order ordered by sum of indexes of the choices
     *
     * Example (part of the example explained in the top of this file) :
     *
     * For priority order = 2, this method will return
     *
     * [
     *   [ 2, 0, 0 ], [ 0, 2, 0 ],
     *   [ 0, 0, 2 ], [ 2, 1, 0 ],
     *   [ 2, 0, 1 ], [ 1, 2, 0 ],
     *   [ 0, 2, 1 ], [ 1, 0, 2 ],
     *   [ 0, 1, 2 ], [ 2, 1, 1 ],
     *   [ 1, 2, 1 ], [ 1, 1, 2 ],
     *   [ 2, 2, 0 ], [ 2, 0, 2 ],
     *   [ 0, 2, 2 ], [ 2, 2, 1 ],
     *   [ 2, 1, 2 ], [ 1, 2, 2 ],
     *   [ 2, 2, 2 ]
     * ]
     * @param priorityOrder
     * @private
     */
    private addChoicesByPriorityOrder(priorityOrder: number): IndexesChoices {
        const newChoices: IndexesChoices = {
            priorityOrder: priorityOrder,
            choices: [this.nextChoices]
        }
        while (this.maxChoices <= priorityOrder) {
            this.limitsChoices = this.chooseIndexesByPriorityOrder(this.limitsChoices);
            this.maxChoices = Math.max(...this.limitsChoices);
            if (this.maxChoices > priorityOrder) {
                this.nextChoices = this.limitsChoices;
            } else {
                newChoices.choices.push(this.limitsChoices);
            }
        }
        if (DEBUG.logIndexesCombinationsArray) {
            console.log(chalk.blueBright(`Choices of priority ${newChoices.priorityOrder} :`), newChoices.choices.length, chalk.yellowBright('elements'))
            console.log(newChoices.choices)
        }
        return newChoices;
    }


    /**
     * Returns the array of choices with priority max (= 0)
     * Example :
     * If there are 3 inputs, this method will return [0, 0, 0]
     * @param numberOfInputs
     * @private
     */
    private initLimitsChoices(numberOfInputs: number): number[] {
        const limitsChoices: number[] = [];
        for (let i = 0; i < numberOfInputs; i++) {
            limitsChoices.push(0);
        }
        return limitsChoices;
    }


    /**
     * Returns the next array of choices after the last one
     *
     * Example:
     * [1, 0, 2] => [0, 1, 2]
     *
     * @param lastChoices
     * @private
     */
    private chooseIndexesByPriorityOrder(lastChoices: number[]): number[] {
        let sortedChoices: number[] = [...lastChoices].sort((a, b) => a - b);
        if (this.isOrdered(lastChoices)) {
            return this.nextPriorityLevel(sortedChoices);
        } else {
            return this.nextChoicesWithSamePriorityLevel(lastChoices, sortedChoices);
        }
    }


    /**
     * When the last choices are in increasing order, this method returns a new choices array in decreasing order with one of the elements of the initial array with a minimum value is increased of one
     *
     * Examples :
     *   [1, 1, 2] => [2, 2, 0]
     *   [0, 2, 3] => [3, 2, 1]
     *
     * @param choices
     * @private
     */
    private nextPriorityLevel(choices: number[]): number[] {
        const minChoice = Math.min(...choices);
        const copyChoices = [...choices];
        copyChoices[copyChoices.indexOf(minChoice)] = minChoice + 1;
        for (let i = 0; i < copyChoices.length; i++) {
            if (copyChoices[i] === minChoice) {
                copyChoices[i] = 0;
            }
        }
        copyChoices.sort((a, b) => b - a);
        return copyChoices;
    }


    /**
     * When the last choices are not in increasing order, this method finds the array of the indexes with the lowest values which are not ordered (lowPriorities)
     * and then moves the last element of lowPriorities to the right (permutation n => n + 1 in normal cases and n => 0 when n is the last index)
     * and then returns this new choices array
     *
     * Examples :
     *   [0, 0, 3, 0] => [0, 0, 0, 3]
     *   [1, 0, 3, 0] => [0, 1, 3, 0]
     *   [3, 1, 2, 0] => [3, 0, 2, 1]
     *   [0, 3, 1, 1] => [1, 1, 3, 0]
     *   [2, 1, 3, 0] => [2, 0, 3, 1]
     *
     * @param lastChoices
     * @param sortedChoices
     * @private
     */
    private nextChoicesWithSamePriorityLevel(lastChoices: number[], sortedChoices: number[]): number[] {
        const prioritiesSet: Set<number> = new Set(sortedChoices);
        const priorities: number[] = [...prioritiesSet];
        let lowPriorities: number[] = lastChoices.filter(n => n === sortedChoices[0])
        for (const priority of priorities) {
            if (!this.isOrdered(lowPriorities)) {
                return this.getNewChoicesWhenLowPrioritiesAreNotOrdered(lastChoices, priority, lowPriorities);
            }
            lowPriorities = lastChoices.filter(n => n <= priorities[priorities.indexOf(priority) + 1]);
        }
        throw Error('Error getting new choices of indexes by priority order.');
    }


    /**
     * When the sub-array lowPriorities of the array lastChoices is not ordered, this method permutes to the right the last element of lowPriorities with value equalling to priority param in the array lastChoices
     * and returns the array of choices re-ordered
     *
     * Example :
     *   lastChoices = [2, 1, 3, 0] / priority = 1 / lowPriorities = [1, 0] => [2, 0, 3, 1]
     *   lastChoices = [0, 1, 3, 0] / priority = 1 / lowPriorities = [0, 1, 0] => [0, 0, 3, 1]
     *
     * @param lastChoices
     * @param priority
     * @param lowPriorities
     * @private
     */
    private getNewChoicesWhenLowPrioritiesAreNotOrdered(lastChoices: number[], priority: number, lowPriorities: number[]): number[] {
        let newChoices: number[] = [];
        const indexToMove: SourceTarget = this.indexToMove(lastChoices, priority);
        const maxOfLowPriorities: number[] = lowPriorities.filter(n => n === priority);
        const lowPrioritiesWithoutMax: number[] = lowPriorities.filter(n => n !== priority)
            .sort((a,b) => b - a);
        this.restOfLowProperties = undefined;
        for (let i = 0; i < lastChoices.length; i++) {
            newChoices.push(this.addNewChoices(lastChoices, priority, lowPrioritiesWithoutMax, maxOfLowPriorities, indexToMove, i));
        }
        return newChoices;
    }


    /**
     * This method finds the last element of an array with a value lower than the value param and defines it as the future target of the permutation of the elements (see getNewChoicesWhenLowPrioritiesAreNotOrdered())
     * Then, it finds le last element with a value equalling to the value param and defines it as the future source of the permutation of the elements
     *
     * Example :
     *   arr = [2, 1, 3, 0] / priority = 1 => {source : 1, target : 3}
     *   arr = [0, 1, 3, 0] / priority = 1 => {source : 1, target : 3}
     *   arr = [0, 3, 1, 1] / priority = 1 => {source : 3, target : 0}
     *
     * @param arr
     * @param value
     * @private
     */
    private indexToMove(arr: number[], value: number): SourceTarget {
        let target = undefined;
        for (let i = arr.length; i >= 0; i--) {
            if (arr[i] < value) {
                target = i;
            }
            if (arr[i] === value && target) {
                return {
                    source: i,
                    target: target
                };
            }
        }
        return undefined;
    }


    /**
     * For a given element of lastChoices with the index i, this method returns a re-ordered array depending of the current priority, the array of lowPriorities without its max values,
     * the max of lowPriorities array and the source and target values for index permutation
     *
     * @param lastChoices
     * @param priority
     * @param lowPrioritiesWithoutMax
     * @param maxOfLowPriorities
     * @param indexToMove
     * @param i
     * @private
     */
    private addNewChoices(lastChoices: number[], priority: number, lowPrioritiesWithoutMax: number[], maxOfLowPriorities: number[], indexToMove: SourceTarget, i: number): number {
        if (lastChoices[i] > priority) {
            return lastChoices[i];
        } else if ((i < indexToMove.target && lastChoices[i] < priority) || i === indexToMove.source) {
            return lowPrioritiesWithoutMax.shift();
        } else if (i < indexToMove.target && lastChoices[i] === priority) {
            return maxOfLowPriorities.shift();
        } else {
            if (!this.restOfLowProperties) {
                this.restOfLowProperties = maxOfLowPriorities.concat(lowPrioritiesWithoutMax);
            }
            return this.restOfLowProperties.shift();
        }
    }


    /**
     * Checks if an array is ordered by increasing order
     * @param arr
     * @private
     */
    private isOrdered(arr: number[]): boolean {
        let sortedArray = [...arr].sort((a, b) => a - b);
        return arrayOfNumbersAreEqual(arr, sortedArray);
    }

}
