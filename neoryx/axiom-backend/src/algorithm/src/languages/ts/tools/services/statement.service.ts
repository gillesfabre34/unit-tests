import { Node, Statement, SyntaxKind } from 'ts-morph';
import { AgnosticStatementService } from '../../../../agnostic/tools/services/agnostic-statement.service';
import { StatementKind } from '../../../../agnostic/tools/enums/statement-kind.enum';

export class StatementService implements AgnosticStatementService {


    kind(statement: Statement): StatementKind {
        switch (statement.getKind()) {
            case SyntaxKind.VariableStatement:
                return StatementKind.ASSIGNATION;
            default:
                return undefined;
        }
    }


    getText(statement: Statement): string {
        return statement.getText();
    }


    static isStatement(node: Node): boolean {
        switch (node.getKind()) {
            case SyntaxKind.BreakStatement:
            case SyntaxKind.ContinueStatement:
            case SyntaxKind.DebuggerStatement:
            case SyntaxKind.DoStatement:
            case SyntaxKind.EmptyStatement:
            case SyntaxKind.ExpressionStatement:
            case SyntaxKind.ForInStatement:
            case SyntaxKind.ForOfStatement:
            case SyntaxKind.ForStatement:
            case SyntaxKind.IfStatement:
            case SyntaxKind.LastStatement:
            case SyntaxKind.NotEmittedStatement:
            case SyntaxKind.ReturnStatement:
            case SyntaxKind.SwitchStatement:
            case SyntaxKind.TryStatement:
            case SyntaxKind.VariableStatement:
            case SyntaxKind.WhileStatement:
            case SyntaxKind.WithStatement:
                return true;
            default:
                return false;
        }
    }

}
