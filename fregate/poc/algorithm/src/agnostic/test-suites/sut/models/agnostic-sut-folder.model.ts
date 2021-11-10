import { AgnosticSutFile } from './agnostic-sut-file.model';
import * as fs from 'fs-extra';
import { AgnosticSourceFileService } from '../../../tools/services/agnostic-sourcefile.service';
import { getFolderPath } from '../../../tools/utils/file-system.util';
import { readdirSync } from 'fs';
import { Project, SourceFile } from 'ts-morph';
import { GLOBALS } from '../../../init/constants/globals.const';
import * as chalk from 'chalk';

export abstract class AgnosticSutFolder {

    children: AgnosticSutFolder[] = [];
    path: string = undefined;
    sutFiles: AgnosticSutFile[] = [];
    abstract diagnose(): Promise<void>;
    abstract isTestFile(path: string): boolean;
    abstract newSutFile(): AgnosticSutFile;
    abstract newSourceFileService(): AgnosticSourceFileService;


    generate(path: string): AgnosticSutFolder {
        if (fs.statSync(path).isDirectory()) {
            this.path = path;
            this.sutFiles = this.getSutFiles(path);
        } else {
            this.path = getFolderPath(path);
            this.sutFiles = [this.newSutFile().generate(this, path)];
        }
        return this;
    }


    getSutFiles(folderPath: string): AgnosticSutFile[] {
        const sutFiles: AgnosticSutFile[] = [];
        const filesOrSubFoldersPaths: string[] = readdirSync(folderPath).map(p => `${folderPath}/${p}`);
        for (const path of filesOrSubFoldersPaths) {
            if (fs.statSync(path).isDirectory()) {
                sutFiles.push(...this.getSutFiles(path));
            } else if (this.newSourceFileService().isSutFilePath(path)) {
                sutFiles.push(this.newSutFile().generate(this, path));
            }
        }
        return sutFiles;
    }


    addConstraints(): void {
        for (const sutFile of this.sutFiles) {
            sutFile.addConstraints();
        }
    }


    async addTestSuites(): Promise<void> {
        for (const sutFile of this.sutFiles) {
            await sutFile.addTestSuites();
        }
    }


    async writeSutFiles(): Promise<void> {
        for (const sutFile of this.sutFiles) {
            await sutFile.write();
        }
    }
}
