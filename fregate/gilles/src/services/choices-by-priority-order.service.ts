import * as chalk from 'chalk';

export class ChoicesByPriorityOrderService {

    chooseByPriorityOrder(lastChoices: number[]): number[] {
        let sortedChoices = [...lastChoices].sort((a, b) => a - b);
        const prioritiesSet = new Set(sortedChoices);
        const priorities = [...prioritiesSet];
        let newChoices = [];
        if (this.isOrdered(lastChoices)) {
            return this.nextLevel(sortedChoices);
        } else {
            let lowPriorities = lastChoices.filter(n => n === sortedChoices[0])
            for (const priority of priorities) {
                if (!this.isOrdered(lowPriorities)) {
                    const indexToMove = this.indexToMove(lastChoices, priority);
                    const maxOfLowPriorities = lowPriorities.filter(n => n === priority);
                    const lowPrioritiesWithoutMax = lowPriorities.filter(n => n !== priority)
                        .sort((a,b) => b - a);
                    let restOfLowProperties = undefined;
                    for (let i = 0; i < lastChoices.length; i++) {
                        if (lastChoices[i] > priority) {
                            newChoices.push(lastChoices[i]);
                        } else if ((i < indexToMove.target && lastChoices[i] < priority) || i === indexToMove.source) {
                            newChoices.push(lowPrioritiesWithoutMax.shift());
                        } else if (i < indexToMove.target && lastChoices[i] === priority) {
                            newChoices.push(maxOfLowPriorities.shift());
                        } else {
                            if (!restOfLowProperties) {
                                restOfLowProperties = maxOfLowPriorities.concat(lowPrioritiesWithoutMax);
                            }
                            newChoices.push(restOfLowProperties.shift());
                        }
                    }
                    break;
                }
                lowPriorities = lastChoices.filter(n => n <= priorities[priorities.indexOf(priority) + 1]);
            }
        }
        return newChoices;
    }



    indexToMove(arr: number[], value: number): { source: number, target: number } {
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


    nextLevel(choices: number[]): number[] {
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



    isOrdered(arr: number[]): boolean {
        let sortedArray = [...arr].sort((a, b) => a - b);
        return this.areEqual(arr, sortedArray);
    }


    areEqual(arr1: number[], arr2: number[]): boolean {
        if (!Array.isArray(arr1) || !Array.isArray(arr2) || arr1.length !== arr2.length) {
            return false;
        }
        for (let i = 0; i < arr1.length; i++) {
            if (arr1[i] !== arr2[i]) {
                return false;
            }
        }
        return true;
    }

}

export interface Choice {
    priorityOrder: number;
    choices: number[][]
}

const service = new ChoicesByPriorityOrderService();

let priorityOrder = 0;
let limitsChoices = [0, 0, 0, 0];
const allChoices: Choice[] = [];
let nextChoices = limitsChoices;
let maxChoices = -1;
while (priorityOrder < 4) {
    const newChoices: Choice = {
        priorityOrder: priorityOrder,
        choices: [nextChoices]
    }
    while (maxChoices <= priorityOrder) {
        limitsChoices = service.chooseByPriorityOrder(limitsChoices);
        maxChoices = Math.max(...limitsChoices);
        if (maxChoices > priorityOrder) {
            nextChoices = limitsChoices;
        } else {
            newChoices.choices.push(limitsChoices);
        }
    }
    allChoices.push(newChoices);
    priorityOrder++;
    console.log(chalk.blueBright(`Choices of priority ${newChoices.priorityOrder} :`), newChoices.choices.length, chalk.yellowBright('elements'))
    console.log(newChoices.choices)
}


