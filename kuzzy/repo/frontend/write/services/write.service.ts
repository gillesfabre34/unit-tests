import { TestFileService } from './test-file.service';
import { GLOBAL } from '../../init/const/global.const';
import { MockFileService } from './mock-file.service';
import * as chalk from 'chalk';

export class WriteService {

    static async start(projectPath: string, algoPath: string, appName: string): Promise<void> {
        console.log(chalk.yellowBright("START OF KUZZY WRITE FILE PROCESS"));
        this.init(projectPath, algoPath, appName);
        await TestFileService.writeTestFiles();
        await MockFileService.writeMockFiles();
        console.log(chalk.yellowBright("END OF KUZZY WRITE FILE PROCESS"));
    }


    private static init(projectPath: string, algoPath: string, appName: string): void {
        GLOBAL.kuzzyPath = `${algoPath}/kuzzy`;
        GLOBAL.appName = appName;
        GLOBAL.projectPath = projectPath;
    }

}
