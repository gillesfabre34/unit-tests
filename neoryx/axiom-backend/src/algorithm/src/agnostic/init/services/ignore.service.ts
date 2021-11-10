import * as chalk from 'chalk';
import * as fs from 'fs-extra';
import { GLOBALS } from '../constants/globals.const';
import { isFileOrDirPath, cleanPath } from '../../tools/utils/file-system.util';

export class IgnoreService {

    static async getIgnoredFilesOrDirs(projectPath: string): Promise<string[]> {
        try {
            const gitIgnorePath = `${projectPath}/.gitignore`;
            const gitIgnore = await this.getGitIgnoreContent(gitIgnorePath);
            if (!gitIgnore) {
                return [];
            }
            const ignored: string[] = [];
            const lines: string[] = gitIgnore.split('\n');
            for (const line of lines) {
                const cleanedLine: string = cleanPath(line);
                if (isFileOrDirPath(cleanedLine)) {
                    ignored.push(`${GLOBALS.projectPath}/${cleanedLine}`);
                }
            }
            return ignored;
        } catch (err) {
            console.log(chalk.red('ERR GETTING IGNORED FILES OR DIRS'), err)
            throw (err);
        }
    }


    private static async getGitIgnoreContent(gitIgnorePath?: string): Promise<string> {
        try {
            gitIgnorePath = gitIgnorePath || `${GLOBALS.projectPath}/.gitignore`;
            return fs.readFileSync(gitIgnorePath, "utf8");
        } catch (err) {
            return undefined;
        }
    }


    static isIgnored(fileOrDirPath: string): boolean {
        return GLOBALS.ignoredFilesOrDirs.includes(fileOrDirPath);
    }

}
