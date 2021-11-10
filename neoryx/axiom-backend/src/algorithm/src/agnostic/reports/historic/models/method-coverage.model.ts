import * as chalk from 'chalk';
import { Historic } from './historic.model';
import { HistoricService } from '../services/historic.service';
import { JsonHistoricService } from '../services/json-historic.service';
import { ArchiveStats } from './archive-stats.model';

export class MethodCoverage {

    date: number = undefined;
    filePath: string = undefined;
    name: string = undefined;
    numberOfBugs: number = undefined;
    numberOfCoveredStatements: number = undefined;
    numberOfTests: number = undefined;
    totalStatements: number = undefined;


    constructor(name?: string, filePath?: string, numberOfBugs?: number, totalStatements?: number, numberOfCoveredStatements?: number, numberOfTests?: number, date?: number) {
        this.name = name;
        this.filePath = filePath;
        this.numberOfBugs = numberOfBugs;
        this.totalStatements = totalStatements;
        this.numberOfCoveredStatements= numberOfCoveredStatements;
        this.numberOfTests= numberOfTests;
        this.date = date;
    }


    get codeCoverage(): number {
        return this.totalStatements ? this.numberOfCoveredStatements / this.totalStatements : 0;
    }


    async hasRegressionInJsonHistoric(): Promise<boolean> {
        const jsonHistoric: any = await JsonHistoricService.getJsonHistoricFromJsonFile();
        const historic: Historic = JsonHistoricService.convertJsonHistoricToHistoricObject(jsonHistoric);
        const previousArchiveStats: ArchiveStats = HistoricService.getLastArchiveStats(historic);
        if (!previousArchiveStats) {
            return false;
        }
        const previousMethodCoverage: MethodCoverage = previousArchiveStats.getMethodCoverage(this.filePath, this.name);
        return this.isRegressionOf(previousMethodCoverage);
    }


    isRegressionOf(previousMethodCoverage: MethodCoverage): boolean {
        return this.hasSameNumberOfStatements(previousMethodCoverage) && (this.hasLowerCoverage(previousMethodCoverage) || this.hasNewBugs(previousMethodCoverage));
    }


    private hasLowerCoverage(previousMethodCoverage: MethodCoverage): boolean {
        return this.codeCoverage < previousMethodCoverage.codeCoverage;
    }


    private hasNewBugs(previousMethodCoverage: MethodCoverage): boolean {
        return this.numberOfBugs > previousMethodCoverage.numberOfBugs;
    }


    private hasSameNumberOfStatements(previousMethodCoverage: MethodCoverage): boolean {
        return this.totalStatements === previousMethodCoverage?.totalStatements;
    }


    isSameAs(otherMethodCoverage: MethodCoverage): boolean {
        return otherMethodCoverage.name === this.name && otherMethodCoverage.filePath === this.filePath;
    }

}
