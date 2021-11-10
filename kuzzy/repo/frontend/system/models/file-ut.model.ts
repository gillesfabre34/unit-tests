import { SourceFile } from 'ts-morph';
import { StatementUT } from './statement-ut.model';
import { FileUTEntity } from '../../db/entities/file-ut.entity';
import { GLOBAL } from '../../init/const/global.const';

export class FileUT {

    sourceFile: SourceFile = undefined;
    statementUTs: StatementUT[] = [];

    constructor(sourceFile: SourceFile) {
        this.sourceFile = sourceFile;
    }


    get path(): string {
        return this.sourceFile?.getFilePath() ?? '';
    }


    get parsedStatements(): StatementUT[] {
        return this.statementUTs.filter(s => s.isParsed);
    }


    async save(): Promise<FileUTEntity> {
        return await new FileUTEntity(this.sourceFile.getFilePath(), GLOBAL.systemUTEntity).save();
    }

}
