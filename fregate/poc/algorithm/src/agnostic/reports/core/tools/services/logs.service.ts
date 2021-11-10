import { percentage } from '../../../../tools/utils/numbers.util';
import { Bug } from '../../bugs/models/bug.model';
import { plural } from '../../../../tools/utils/strings.util';
import * as chalk from 'chalk';
import { Regression } from '../../../historic/models/regression.model';

export class LogsService {


    static logCodeCoverage(numberOfCoveredStatements: number, totalStatements: number, hasCompilationError = false): string {
        const codeCoverage = percentage(numberOfCoveredStatements, totalStatements);
        const compilationErrorMessage: string = hasCompilationError ? ` => 0 %` : '';
        return `${numberOfCoveredStatements} / ${totalStatements} (${codeCoverage} %)${compilationErrorMessage}`;
    }


    static logDuration(duration: number, tries: number): string {
        return `${duration} ms (${tries} ${plural('try', tries)})\n`;
    }


    static logBugs(bugs: Bug[]): string {
        let log = '';
        for (const bug of bugs) {
            log = `${log}${bug.bugType} / `;
        }
        return log.slice(0, -3);
    }


    static logRegressions(regressions: Regression[] = []): void {
        console.log(chalk.yellowBright(`ARCHIVE CREATED\n`));
        console.log(chalk.redBright(`Regressions : ${regressions.length}\n`));
        for (const regression of regressions) {
            const coverage: number = regression.methodCoverage?.numberOfCoveredStatements;
            const previousCoverage: number = regression.previousMethodCoverage?.numberOfCoveredStatements;
            const bugs: number = regression.methodCoverage?.numberOfBugs;
            const previousBugs: number = regression.previousMethodCoverage?.numberOfBugs;
            console.log(chalk.blueBright(`${regression.methodCoverage?.name} : `), chalk.whiteBright(`${previousCoverage} => ${coverage} covered ${plural('statement', coverage)} / ${previousBugs} => ${bugs} ${plural('bug', bugs)}`));
        }
        console.log();
    }

}
