import { IndexesChoices } from '../../../constraints/interfaces/indexes-choices.interface';

export class AddTestSuiteProcess {

    chosenIndexes: number[] = [];
    indexesByPriorityOrder: IndexesChoices[] = [];
    isFinished = false;
    previousCodeCoverage = 0;
    numberOfTestsToWrite = 0;
    tryNumber = 0;

    constructor(indexesByPriorityOrder: IndexesChoices[] = []) {
        this.indexesByPriorityOrder = indexesByPriorityOrder;
    }

}
