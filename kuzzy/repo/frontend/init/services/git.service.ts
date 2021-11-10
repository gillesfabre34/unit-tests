import { GLOBAL } from '../const/global.const';
import simpleGit, { StatusResult } from 'simple-git';
import { SourceFile } from 'ts-morph';

export class GitService {

    static async modifiedProjectFilePaths(): Promise<SourceFile[]> {
        const gitStatus: StatusResult = await simpleGit().status();
        const modifiedFilePaths: string[] = gitStatus.modified.map(p => `${GLOBAL.algoPath}/${p}`);
        return GLOBAL.project.getSourceFiles().filter(s => modifiedFilePaths.includes(s.getFilePath()));
    }

}
