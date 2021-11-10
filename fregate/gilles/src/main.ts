import * as chalk from 'chalk';
import { createFileAsync, getFilename } from './services/tools.service';
import { PROJECT } from './constants/project.const';
import { FlagService } from './services/flag.service';
import { GlobalService } from './services/global.service';
import { CodeCoverageService } from './services/code-coverage.service';

export class Main {

    /**
     * Starts the main process
     * @param pathToAnalyze
     */
    async start(pathToAnalyze: string): Promise<any> {
        await FlagService.generateFlags(pathToAnalyze);
        await CodeCoverageService.execute(pathToAnalyze);
        const classSpec = await GlobalService.generate(PROJECT.getSourceFileOrThrow(pathToAnalyze));
        const filename = getFilename(pathToAnalyze);
        const specFileName = 'output.' + filename.replace(/ts$/, 'spec.ts');
        const specPath = `${pathToAnalyze.slice(0, -filename.length)}${specFileName}`;
        await createFileAsync(specPath, classSpec.code);
        console.log(chalk.yellowBright('\nFREGATE UNIT TESTS GENERATED SUCCESSFULLY'));
    }

}
