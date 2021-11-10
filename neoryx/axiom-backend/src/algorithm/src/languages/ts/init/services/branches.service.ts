import { AgnosticBranchesService } from '../../../../agnostic/init/services/agnostic-branches.service';
import { ConditionalExpression, MethodDeclaration, Node, ReturnStatement, Statement, SyntaxKind } from 'ts-morph';
import * as chalk from 'chalk';
import { AgnosticStatementService } from '../../../../agnostic/tools/services/agnostic-statement.service';
import { StatementService } from '../../tools/services/statement.service';
import { tab, tabs } from '../../../../agnostic/tools/utils/strings.util';

export class BranchesService extends AgnosticBranchesService {


    getReturnBinariesStatements(methodDeclaration: MethodDeclaration): Statement[] {
        const returnType = methodDeclaration.getStructure()?.returnType;
        if (returnType && returnType !== 'boolean') {
            return [];
        }
        const returnStatements: ReturnStatement[] = methodDeclaration.getDescendantsOfKind(SyntaxKind.ReturnStatement);
        const binariesStatements = returnStatements.filter(c => c.getExpression().getKind() === SyntaxKind.BinaryExpression);
        if (binariesStatements.length === 0 || !methodDeclaration.getReturnType().isBoolean()) {
            return [];
        }
        return binariesStatements;
    }


    getNewReturnBinaryCode(code: string): string {
        let expression: string = code?.slice(7);
        expression = expression.slice(-1) === ';' ? expression.slice(0, -1) : expression;
        let newCode = `if (${expression}) {\n`;
        newCode = `${newCode}${tab}return true;\n`;
        newCode = `${newCode}} else {\n`;
        newCode = `${newCode}${tab}return false;\n`;
        newCode = `${newCode}}\n`;
        return newCode;
    }


    newStatementService(): AgnosticStatementService {
        return new StatementService();
    }


    getTernariesStatements(methodDeclaration: MethodDeclaration): Statement[] {
        const conditionalExpressions: ConditionalExpression[] = methodDeclaration.getDescendantsOfKind(SyntaxKind.ConditionalExpression);
        return conditionalExpressions.map(c => this.getFirstAncestorStatement(c));
    }


    getFirstAncestorStatement(node: Node): Statement {
        if (node && !Node.isStatement(node)) {
            return this.getFirstAncestorStatement(node.getParent());
        }
        return node as Statement;
    }


    getNewTernaryAssignmentCode(beforeEqual: string, condition: string, firstCase: string, secondCase: string): string {
        let newCode = '';
        const varName: string = this.varDeclaration(beforeEqual);
        if (varName) {
            newCode = `let ${varName};\n`;
            beforeEqual = varName;
        }
        newCode = `${newCode}if (${condition.trim()}) {\n`;
        newCode = `${newCode}${tabs(1)}${beforeEqual}=${firstCase};\n`;
        newCode = `${newCode}} else {\n`;
        newCode = `${newCode}${tabs(1)}${beforeEqual}=${secondCase}\n`;
        newCode = `${newCode}}\n`;
        return newCode;

    }


    private varDeclaration(beforeEqual: string): string {
        const splitSpaces: string[] = beforeEqual.split(' ');
        return ['const', 'let', 'var'].includes(splitSpaces[0]) ? `${splitSpaces[1]} ` : '';
    }


    replaceCode(methodDeclaration: MethodDeclaration, statement: Statement, newCode: string): void {
        statement.replaceWithText(newCode);
    }

}
