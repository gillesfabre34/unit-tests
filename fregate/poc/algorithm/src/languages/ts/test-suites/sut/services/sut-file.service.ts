import { SutFile } from '../models/sut-file.model';
import { SutClassService } from './sut-class.service';
import { Expression, Statement } from 'ts-morph';
import { AgnosticSutFileService } from '../../../../../agnostic/test-suites/sut/services/agnostic-sut-file.service';
import { AgnosticSutClassService } from '../../../../../agnostic/test-suites/sut/services/agnostic-sut-class.service';

export class SutFileService extends AgnosticSutFileService {

    sutFile: SutFile = undefined;


    getNumberOfStatements(): number {
        const statements: (Statement | Expression)[] = this.sutFile?.getSourceFile(this.sutFile.path)?.getDescendantStatements();
        return statements ? statements.length : 0;
    }


    newSutClassService(): AgnosticSutClassService<any> {
        return new SutClassService<any>();
    }


    setCode(): void {
        this.setImportsCode(this.sutFile);
        this.setDescribeClassesCode(this.sutFile);
        this.sutFile.code = `${this.sutFile.importsCode}${this.sutFile.describesClassesCode}`;
    }


    private setImportsCode(sutFile: SutFile): void {
        sutFile.importsCode = '';
        for (const sutClass of sutFile.sutClasses) {
            sutFile.importsCode = `${sutFile.importsCode}${sutClass.importsCode}\n`;
        }
    }


    private setDescribeClassesCode(sutFile: SutFile): void {
        sutFile.describesClassesCode = '';
        for (const sutClass of sutFile.sutClasses) {
            sutFile.describesClassesCode = `${sutFile.describesClassesCode}${sutClass.describeClass.code}\n`;
        }
    }


    getSpecFileName(fileName: string): string {
        return fileName.replace(/ts$/, 'spec.ts');
    }

}
