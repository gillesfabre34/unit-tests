import { AgnosticStatement } from './agnostic-statement.model';

export abstract class AgnosticMethod {

    isStatic = false;
    methodDeclaration: any = undefined;
    statements: AgnosticStatement[] = [];

    constructor(methodDeclaration?: any, isStatic = false) {
        this.methodDeclaration = methodDeclaration;
        this.isStatic = isStatic;
        this.statements = this.getDescendantStatements(methodDeclaration);
    }

    abstract getDescendantStatements(methodDeclaration: any): AgnosticStatement[];


    flagStatement(id: number): void {
        this.statements[id].isCovered = true;
    }


    getCoveredStatements(): number[] {
        return this.statements.filter(s => s.isCovered).map(s => s.id);
    }


    get codeCoverage(): number {
        if (this.statements.length === 0) {
            return 0;
        }
        const coveredStatements: AgnosticStatement[] = this.statements.filter(s => s?.isCovered);
        const percentage = 100 * coveredStatements.length / this.statements.length;
        return Number(percentage.toFixed(2));
    }
}
