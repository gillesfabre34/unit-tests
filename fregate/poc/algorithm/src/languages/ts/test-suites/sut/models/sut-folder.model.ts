import { SutFile } from './sut-file.model';
import { GLOBALS } from '../../../../../agnostic/init/constants/globals.const';
import { Diagnostic, Project, SourceFile } from 'ts-morph';
import { AgnosticSourceFileService } from '../../../../../agnostic/tools/services/agnostic-sourcefile.service';
import { SourceFileService } from '../../../tools/services/sourcefile.service';
import { AgnosticSutFolder } from '../../../../../agnostic/test-suites/sut/models/agnostic-sut-folder.model';
import { FileStats } from '../../../../../agnostic/reports/dashboard/models/file-stats.model';
import { FileStatsService } from '../../../../../agnostic/reports/dashboard/services/file-stats.service';
import { BugType } from '../../../../../agnostic/reports/core/bugs/enums/bug-type.enum';

export class SutFolder extends AgnosticSutFolder {


    newSourceFileService(): AgnosticSourceFileService {
        return new SourceFileService();
    }


    newSutFile(): SutFile {
        return new SutFile();
    }


    getSourceFiles(project: Project): SourceFile[] {
        return project.getSourceFiles();
    }


    isTestFile(path: string): boolean {
        return path.slice(-7) === 'spec.ts';
    }


    async diagnose(): Promise<void> {
        const project = new Project({
            tsConfigFilePath: `${GLOBALS.appRoot}/neoryx/tsconfig.json`,
        });
        const diagnostics: Diagnostic[] = project.getPreEmitDiagnostics();
        const sourceFilesWithCompilationErrors: SourceFile[] = diagnostics.filter(d => d.getCategory() === 1).map(d => d.getSourceFile());
        const filePaths: Set<string> = new Set(sourceFilesWithCompilationErrors.map(s => s.getFilePath()).filter(f => this.isTestFile(f)));
        for (const filePath of filePaths) {
            const originalPath = filePath.replace(`${GLOBALS.appRoot}/neoryx/src/tests`, `${GLOBALS.appRoot}/src`)
                .replace('spec.', '');
            const fileStats: FileStats = FileStatsService.getFileStats(originalPath);
            if (fileStats) {
                fileStats.addBug(BugType.COMPILATION_ERROR);
            }
        }
    }

}
