import { AgnosticSutStatement } from './agnostic-sut-statement.model';
import { percentage } from '../../../tools/utils/numbers.util';
import * as chalk from 'chalk';

export abstract class AgnosticMethod {

    isStatic = false;
    methodDeclaration: any = undefined;
    statements: AgnosticSutStatement[] = [];

    constructor(methodDeclaration?: any, isStatic = false) {
        this.methodDeclaration = methodDeclaration;
        this.isStatic = isStatic;
        this.statements = this.getDescendantStatements(methodDeclaration);
    }

    abstract getDescendantStatements(methodDeclaration: any): AgnosticSutStatement[];
    abstract get isVoid(): boolean;

    get codeCoverage(): number {
        if (this.statements.length === 0) {
            return 0;
        }
        const coveredStatements: AgnosticSutStatement[] = this.statements.filter(s => s?.isCovered);
        return percentage(coveredStatements.length, this.statements.length);
    }


    flagStatement(id: number): void {
        this.statements[id].isCovered = true;
    }


    getCoveredStatements(): number[] {
        return this.statements.filter(s => s.isCovered).map(s => s.id);
    }

}
