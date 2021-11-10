import { AgnosticSutClassService } from './agnostic-sut-class.service';
import { FileStatsService } from '../../../reports/dashboard/services/file-stats.service';
import { AgnosticSutFile } from '../models/agnostic-sut-file.model';
import { BugType } from '../../../reports/core/bugs/enums/bug-type.enum';
import { writeFile, getFilename, neoryxPath } from '../../../tools/utils/file-system.util';

export abstract class AgnosticSutFileService {

    sutFile: AgnosticSutFile = undefined;
    abstract getNumberOfStatements(): number;
    abstract getSpecFileName(fileName: string): string;
    abstract newSutClassService(): AgnosticSutClassService<any>;
    abstract setCode(): void;


    async generate(sutFile: AgnosticSutFile): Promise<void> {
        this.sutFile = sutFile;
        for (const sutClass of sutFile.sutClasses) {
            if (!sutClass.name) {
                FileStatsService.getFileStats(sutFile?.path).addBug(BugType.ERROR_WRITING_FILE_NO_CLASS_NAME);
                return;
            }
            this.newSutClassService().generate(sutClass);
        }
        this.setCode();
        await this.writeFile();
    }


    private async writeFile(): Promise<void> {
        const filename = getFilename(this.sutFile.path);
        const specFileName = this.getSpecFileName(filename);
        const neoryxFilePath = neoryxPath(this.sutFile.path).replace('src', 'src/tests');
        const neoryxSpecFilePath = `${neoryxFilePath.slice(0, -filename.length)}${specFileName}`;
        await writeFile(neoryxSpecFilePath, this.sutFile.code);
    }

}
