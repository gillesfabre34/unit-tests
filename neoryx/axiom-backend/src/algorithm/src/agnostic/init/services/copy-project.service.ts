import * as chalk from 'chalk';
import * as fs from 'fs-extra';
import { GLOBALS } from '../constants/globals.const';
import { IgnoreService } from './ignore.service';
import { exec } from "child_process";
import { copySync, neoryxClonePath, neoryxFlagPath } from '../../tools/utils/file-system.util';

export class CopyProjectService {

    static async copyProject(gitChanges: string[]): Promise<void> {
        console.log(chalk.yellowBright('Clone project...'));
        if (fs.existsSync(GLOBALS.neoryxPath) && fs.statSync(GLOBALS.neoryxPath)?.isDirectory() && Array.isArray(gitChanges)) {
            for (const filePath of gitChanges) {
                if (!IgnoreService.isIgnored(filePath)) {
                    await this.copyFileToNeoryxFolder(filePath)
                }
            }
        } else {
            await this.clone(GLOBALS.projectPath);
            await this.npmInstall();
        }
        await this.copyCssFiles();
    }


    static async clone(fileOrDirPath: string, flag = false): Promise<void> {
        if (IgnoreService.isIgnored(fileOrDirPath)) {
            return;
        }
        let pathToClone: string = undefined;
        if (fs.statSync(fileOrDirPath).isDirectory()) {
            const filesOrDirs: string[] = fs.readdirSync(fileOrDirPath, 'utf-8');
            for (const fileOrDir of filesOrDirs) {
                pathToClone = `${fileOrDirPath}/${fileOrDir}`;
                if (!IgnoreService.isIgnored(pathToClone)) {
                    await this.clone(pathToClone, flag);
                }
            }
        } else {
            pathToClone = `${fileOrDirPath}`;
            if (!IgnoreService.isIgnored(pathToClone)) {
                await this.copyFileToNeoryxFolder(pathToClone, flag)
            }
        }
    }


    private static async copyFileToNeoryxFolder(originalPath: string, flag = false): Promise<string> {
        const targetPath = flag ? neoryxFlagPath(originalPath) : neoryxClonePath(originalPath);
        await copySync(originalPath, targetPath);
        return targetPath;
    }


    private static async copyCssFiles(): Promise<void> {
        const stylesFolderPath = `${GLOBALS.algoPath}/src/algorithm/src/agnostic/reports/styles`;
        await copySync(stylesFolderPath, `${GLOBALS.neoryxPath}/reports/styles`)
    }


    private static async npmInstall(): Promise<void> {
        const command = `cd ${GLOBALS.neoryxPath}/clone && npm i`;
        exec(command, (error, stdout, stderr) => {
            if (error) {
                console.log(`error: ${error.message}`);
                return;
            }
            if (stderr) {
                console.log(`stderr: ${stderr}`);
                return;
            }
            console.log(`stdout: ${stdout}`);
        });
    }
}
