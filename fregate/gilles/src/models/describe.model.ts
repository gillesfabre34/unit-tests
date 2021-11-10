import { It } from './it.model';
import { BeforeEach } from './before-each.model';
import { DescribeService } from '../services/describe.service';
import { MethodDeclaration, SourceFile, SyntaxKind } from 'ts-morph';

export class Describe {

    private _beforeEach: BeforeEach = undefined;
    private _describes: Describe[] = [];
    private _its: It[] = [];
    private _sourceFile: SourceFile = undefined;
    private _title: string = undefined;



    // ---------------------------------------------------------------------------------
    //                                Getters and setters
    // ---------------------------------------------------------------------------------


    get beforeEach(): BeforeEach {
        return this._beforeEach;
    }


    set beforeEach(beforeEach: BeforeEach) {
        this._beforeEach = beforeEach;
    }


    get code(): string {
        return DescribeService.getCode(this);
    }


    get describes(): Describe[] {
        return this._describes;
    }


    set describes(describes: Describe[]) {
        this._describes = describes;
    }


    get its(): It[] {
        return this._its;
    }


    set its(its: It[]) {
        this._its = its;
    }



    get sourceFile(): SourceFile {
        return this._sourceFile;
    }


    get method(): MethodDeclaration {
        return this.sourceFile.getDescendantsOfKind(SyntaxKind.MethodDeclaration)?.filter(m => m.getName() === this.title)?.[0] ?? undefined
    }


    set sourceFile(sourceFile: SourceFile) {
        this._sourceFile = sourceFile;
    }


    get title(): string {
        return this._title;
    }


    set title(title: string) {
        this._title = title;
    }

}
