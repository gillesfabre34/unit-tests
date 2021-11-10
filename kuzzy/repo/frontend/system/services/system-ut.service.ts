import { GLOBAL } from '../../init/const/global.const';
import { GitService } from '../../init/services/git.service';
import { SourceFile } from 'ts-morph';
import { FileUtService } from './file-ut.service';
import * as chalk from 'chalk';
import { mergeWithoutDuplicates } from '../../../shared/utils/arrays.util';
import { copyToKuzzyFolder, kuzzyPath, originalPath } from '../../utils/kuzzy-folder.util';
import { SystemUtDataService } from '../../api/services/system-ut-data.service';
import { FileUTDataService } from '../../api/services/file-ut-data.service';
import { ShouldBeSavedService } from '../../api/services/should-be-saved.service';
import { ensureDir, getFiles, isNodeModulesFolder, removeFiles } from '../../utils/file-system.util';
import { InitService } from '../../init/services/init.service';


export class SystemUtService {


    static async create(): Promise<void> {
        GLOBAL.systemUT.id = await SystemUtDataService.putAndReturnId(GLOBAL.systemUT.name);
        await this.createProjectSystemUT();
        const modifiedSourceFiles: SourceFile[] = await GitService.modifiedProjectFilePaths();
        await this.deleteRemovedOrModifiedFileUTsInDb(modifiedSourceFiles);
        await this.saveModifiedOrNewFileUTEntities(modifiedSourceFiles);
        await this.synchronizeKuzzyFolder(modifiedSourceFiles);
    }


    private static async createProjectSystemUT(): Promise<void> {
        GLOBAL.systemUT.filesUts = FileUtService.initFileUTs();
    }


    /**
     * Removes in database the FileUTEntities corresponding to a file which was removed or modified
     * @private
     */
    private static async deleteRemovedOrModifiedFileUTsInDb(modifiedSourceFiles: SourceFile[]): Promise<void> {
        const dbFileUTPaths = await FileUTDataService.findFileUTPaths();
        const projectFilePaths: string[] = GLOBAL.project.getSourceFiles().map(s => s.getFilePath());
        const removedFilesStillInDb: string[] = dbFileUTPaths.filter(p => !projectFilePaths.includes(p));
        const modifiedFilePathsAlreadyInDb: string[] = dbFileUTPaths.filter(p => modifiedSourceFiles.map(s => s.getFilePath() as string).includes(p));
        const filePathsToDelete = mergeWithoutDuplicates(removedFilesStillInDb, modifiedFilePathsAlreadyInDb);
        await FileUTDataService.deleteFileUts(filePathsToDelete);
    }


    /**
     * Save in database the new files and the modified files
     * @private
     */
    private static async saveModifiedOrNewFileUTEntities(modifiedSourceFiles: SourceFile[]): Promise<void> {
        const dbFileUTPaths = await FileUTDataService.findFileUTPaths();
        const newFiles: SourceFile[] = GLOBAL.project.getSourceFiles().filter(s => !dbFileUTPaths.includes(s.getFilePath()));
        const newOrModifiedFiles: SourceFile[] = mergeWithoutDuplicates(newFiles, modifiedSourceFiles);
        const sourceFilesToSave: SourceFile[] = ShouldBeSavedService.shouldBeSaved(newOrModifiedFiles);
        console.log(chalk.blueBright('New or modified FileUTs to save : '), sourceFilesToSave.length);
        let numberOfSavedFiles = 0;
        if (sourceFilesToSave.length > 0) {
            numberOfSavedFiles = await FileUTDataService.postFileUTDtos(sourceFilesToSave);
        }
        console.log(chalk.cyanBright('New or modified FileUTs saved : '), numberOfSavedFiles);
    }


    private static async synchronizeKuzzyFolder(modifiedSourceFiles: SourceFile[]): Promise<void> {
        await ensureDir(GLOBAL.kuzzyPath);
        const clonedFiles: string[] = await getFiles(GLOBAL.kuzzyPath);
        const projectFiles: string[] = await getFiles(GLOBAL.projectPath);
        await this.removeFilesInKuzzyFolder(clonedFiles, projectFiles);
        await this.cloneFilesToKuzzyFolder(clonedFiles, projectFiles, modifiedSourceFiles);
    }

    /**
     * Removes from kuzzy folder the files corresponding to the removed or modified original files
     * @private
     */
    private static async removeFilesInKuzzyFolder(clonedFiles: string[], projectFiles: string[]): Promise<void> {
        const removedFiles: string[] = clonedFiles.filter(f => !projectFiles.includes(originalPath(f)) && !isNodeModulesFolder(f));
        await removeFiles(removedFiles);
    }


    private static async cloneFilesToKuzzyFolder(clonedFiles: string[], projectFiles: string[], modifiedSourceFiles: SourceFile[]): Promise<void> {
        const newFiles: string[] = projectFiles.filter(f => !clonedFiles.includes(kuzzyPath(f)));
        const filesToCloneAndFlag: string[] = newFiles.concat(modifiedSourceFiles.map(s => s.getFilePath()));
        console.log(chalk.blueBright('Files to clone : '), filesToCloneAndFlag.length);
        await copyToKuzzyFolder(filesToCloneAndFlag);
        await InitService.resetFlaggedProject();
        GLOBAL.fileUtsToFlag = GLOBAL.flaggedProject.getSourceFiles().filter(s => kuzzyPath(filesToCloneAndFlag).includes(s.getFilePath()));
    }
}
