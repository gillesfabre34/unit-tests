import { AgnosticMethod } from '../../../../../agnostic/test-suites/sut/models/agnostic-method.model';
import { StatementTs } from './statement-ts';
import { MethodDeclaration, Statement } from 'ts-morph';

export class Method extends AgnosticMethod {

    getDescendantStatements(methodDeclaration: MethodDeclaration): StatementTs[] {
        const statements: StatementTs[] = [];
        let id = 0;
        for (const statement of methodDeclaration.getDescendantStatements()) {
            statements.push(new StatementTs(statement as Statement, id));
            id++;
        }
        return statements;
    }

}
