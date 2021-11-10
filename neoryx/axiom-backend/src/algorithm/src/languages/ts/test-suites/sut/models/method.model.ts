import { AgnosticMethod } from '../../../../../agnostic/test-suites/sut/models/agnostic-method.model';
import { SutStatement } from './sut-statement';
import { MethodDeclaration, Statement } from 'ts-morph';
import * as chalk from 'chalk';

export class Method extends AgnosticMethod {

    getDescendantStatements(methodDeclaration: MethodDeclaration): SutStatement[] {
        const statements: SutStatement[] = [];
        let id = 0;
        for (const statement of methodDeclaration.getDescendantStatements()) {
            statements.push(new SutStatement(statement as Statement, id));
            id++;
        }
        return statements;
    }

    get isVoid(): boolean {
        return this.methodDeclaration.getReturnType().getText() === 'void';
    }

}
