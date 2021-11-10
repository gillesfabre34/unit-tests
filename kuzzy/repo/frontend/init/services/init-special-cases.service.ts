import { execSync } from 'child_process';
import * as fs from 'fs-extra';
import { getProjectFilePaths, kuzzyPath } from '../../utils/kuzzy-folder.util';
import { getFilename, getFolderPath } from '../../utils/file-system.util';
import { GLOBAL } from '../const/global.const';
import * as chalk from 'chalk';

export class InitSpecialCasesService {


    static async start(): Promise<void> {
        await this.initNodeModulesForEachPackageJson();
    }


    private static async initNodeModulesForEachPackageJson(): Promise<void> {
        const packagesJson: string[] = (await getProjectFilePaths(kuzzyPath(GLOBAL.projectPath))).filter(p => getFilename(p) === 'package.json');
        for (const packageJson of packagesJson) {
            const nodeModulesFolder: string = `${getFolderPath(packageJson)}/node_modules`;
            if (!fs.existsSync(nodeModulesFolder)) {
                await this.npmInstall(packageJson);
            }
        }
    }


    private static async npmInstall(packageJsonPath: string): Promise<void> {
        const folder: string = getFolderPath(packageJsonPath);
        console.log(chalk.yellowBright('Install node_modules...\n'), folder);
        const command = `cd ${folder} && npm i`;
        execSync(command);
    }

}
