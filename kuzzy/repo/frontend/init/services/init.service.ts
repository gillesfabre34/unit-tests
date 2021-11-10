import { GLOBAL } from '../const/global.const';
import { Project } from 'ts-morph';
import * as chalk from 'chalk';
import { SystemUtService } from '../../system/services/system-ut.service';
import { Story } from '../../system/models/story.model';
import { SystemState } from '../../system/models/system-state.model';
import { DynamicSystemState } from '../../system/models/dynamic-app-state.model';
import { InitSpecialCasesService } from './init-special-cases.service';
import { SystemUT } from '../../system/models/system-ut.model';
import { copySync } from 'fs-extra';
import { kuzzyPath } from '../../utils/kuzzy-folder.util';
import { TestCaseDataService } from '../../api/services/test-case-data.service';

export class InitService {

    static async start(projectPath: string, algoPath: string, appName: string): Promise<void> {
        console.log(chalk.yellowBright('Initialization...'));
        GLOBAL.kuzzyPath = `${algoPath}/dist/kuzzy`;
        GLOBAL.projectPath = projectPath;
        GLOBAL.configFilePath = `${GLOBAL.projectPath}/tsconfig.json`;
        GLOBAL.entryPoint = `${projectPath}/launch-capture.ts`;
        GLOBAL.algoPath = algoPath;
        GLOBAL.apiUrl = 'http://localhost:3001';
        GLOBAL.dynamicAppState = new DynamicSystemState();
        GLOBAL.story = new Story(new SystemState());
        GLOBAL.systemUT = new SystemUT(appName);
        this.createProject();
        await this.createFlaggedProject();
        await SystemUtService.create();
        await InitSpecialCasesService.start();
        GLOBAL.testCasesDto = await TestCaseDataService.findTestCases();
    }


    private static createProject(): void {
        GLOBAL.project = new Project({
            tsConfigFilePath: GLOBAL.configFilePath,
        });
    }


    private static async createFlaggedProject(): Promise<void> {
        copySync(GLOBAL.configFilePath, kuzzyPath(GLOBAL.configFilePath));
        GLOBAL.flaggedProject = new Project({
            tsConfigFilePath: kuzzyPath(GLOBAL.configFilePath),
            skipFileDependencyResolution: true
        });
    }


    static async resetFlaggedProject(): Promise<void> {
        await this.createFlaggedProject();
    }
}
