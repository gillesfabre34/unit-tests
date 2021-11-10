import { FileUT } from '../models/file-ut.model';
import { GLOBAL } from '../../init/const/global.const';
import { FunctionDeclaration, MethodDeclaration, SourceFile, SyntaxKind } from 'ts-morph';
import { StatementUT } from '../models/statement-ut.model';
import { MethodOrFunctionDeclaration } from '../../flag/types/method-or-function-declaration.type';
import * as chalk from 'chalk';

export class FileUtService {


    static initFileUTs(): FileUT[] {
        const filesUTs: FileUT[] = [];
        for (const sourceFile of GLOBAL.project.getSourceFiles()) {
            const fileUT = new FileUT(sourceFile);
            fileUT.statementUTs = this.initFileUTStatementUTs(sourceFile);
            filesUTs.push(fileUT);
        }
        return filesUTs;
    }


    private static initFileUTStatementUTs(sourceFile: SourceFile): StatementUT[] {
        const statementUTs: StatementUT[] = [];
        for (const methodDeclaration of sourceFile.getDescendantsOfKind(SyntaxKind.MethodDeclaration)) {
            statementUTs.push(...this.initMethodOrFunctionStatementUTs(methodDeclaration));
        }
        for (const functionDeclaration of sourceFile.getDescendantsOfKind(SyntaxKind.FunctionDeclaration)) {
            statementUTs.push(...this.initMethodOrFunctionStatementUTs(functionDeclaration));
        }
        return statementUTs;
    }


    private static initMethodOrFunctionStatementUTs(methodOrFunctionDeclaration: MethodOrFunctionDeclaration): StatementUT[] {
        const statementUTs: StatementUT[] = [];
        const className: string = methodOrFunctionDeclaration.getFirstAncestorByKind(SyntaxKind.ClassDeclaration)?.getName();
        let index = 0;
        for (const statement of methodOrFunctionDeclaration.getDescendantStatements()) {
            statementUTs.push(new StatementUT(className, methodOrFunctionDeclaration.getName(), index, methodOrFunctionDeclaration.getSourceFile().getFilePath()));
            index++;
        }
        return statementUTs;
    }

}
