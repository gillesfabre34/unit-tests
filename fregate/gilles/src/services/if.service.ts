import {
    BinaryExpression,
    Expression,
    Identifier,
    IfStatement,
    MethodDeclaration,
    Node,
    Project,
    SyntaxKind,
    TypeChecker
} from 'ts-morph';
import * as chalk from 'chalk';
import { PROJECT } from '../constants/project.const';

export class IfService {

    static checker: TypeChecker;

    static start(): void {
        this.checker = PROJECT.getTypeChecker();
        const filePath = 'src/mocks/examples/ifs/if-not-falsy-without.types.ts';
        const projectIsNotFalsy = new Project();
        projectIsNotFalsy.addSourceFileAtPath(filePath);
        const methodDeclarations: MethodDeclaration[] = projectIsNotFalsy.getSourceFile(filePath)
            .getDescendantsOfKind(SyntaxKind.MethodDeclaration);
        for (const methodDeclaration of methodDeclarations) {
            this.getMethodParams(methodDeclaration);
        }
    }

    static getMethodParams(methodDeclaration: MethodDeclaration): any {
        const ifStatements: IfStatement[] = methodDeclaration.getDescendantsOfKind(SyntaxKind.IfStatement);
        for (const ifStatement of ifStatements) {
            const ifElse: IfElse = this.solvePredicate(ifStatement);
        }
    }


    static solvePredicate(ifStatement: IfStatement): IfElse {
        let ifElse: IfElse = {};
        const expression: Expression = ifStatement.getExpression();
        if (this.isParam(expression)) {
            ifElse.if = this.getTruthyValue(expression as Identifier)
            ifElse.else = FALSY_VALUES;
        }
        switch (expression.getKind()) {
            case SyntaxKind.BinaryExpression: {
                ifElse = this.solveBinaryExpression(expression as BinaryExpression);
                break;
            }
            default:
                break;
        }
        console.log(chalk.greenBright('SOLUTIONS :'))
        console.log(chalk.yellowBright('if-not-falsy :'), ifElse.if);
        console.log(chalk.yellowBright('else :'), ifElse.else);
        return ifElse;
    }


    static isParam(node: Node): boolean;
    static isParam(node: Identifier): boolean {
        if (node.getKind() !== SyntaxKind.Identifier) {
            return false;
        }
        return this.getSymbolValueDeclaration(node).getKind() === SyntaxKind.Parameter;
    }


    static getTruthyValue(identifier: Identifier): any {
        switch (identifier.getType().getText()) {
            case 'string':
                return 'a';
            case 'number':
                return 1;
            case 'boolean':
                return 1;
            case 'object':
                return {};
            default:
                //TODO: Non-primitives
                return {};
        }
    }


    static getSymbolValueDeclaration(node: Node): Node {
        // TODO : remove this line (need it for now due to a bug with asynchronicity)
        const aaa = node.getType()
        return this.checker.getSymbolAtLocation(node).getValueDeclaration();

    }


    static solveBinaryExpression(expression: BinaryExpression): IfElse {
        if (expression.getChildAtIndex(1).getKind() === SyntaxKind.EqualsEqualsEqualsToken) {
            return this.solveEquality(expression);
        }
        const ifElse: IfElse = {
            if: 'zzz'
        }
        return ifElse;
    }


    static solveEquality(expression: BinaryExpression): IfElse {
        const ifElse: IfElse = {
            if: 'aaa'
        }
        const left: Node = expression.getLeft();
        const leftType: string = left.getType().getText();
        if (this.isParam(left)) {
            switch (leftType) {
                case 'string':
                    ifElse.if = expression.getRight().getText().slice(1, -1);
                    break;
                case 'boolean':
                    ifElse.if = expression.getRight().getText();
                    break;
                case 'number':
                    ifElse.if = expression.getRight().getText();
                    break;
                case 'object':
                    ifElse.if = expression.getRight().getText();
                    break;
                default:
                    return {};
            }
        }
        ifElse.else = this.mock(leftType, [ifElse.if]);
        return ifElse;
    }


    static mock(type: string, exclude?: any[]): any {
        switch (type) {
            case 'string':
                return exclude.includes('a') ? 'b' : 'a';
            case 'boolean':
                return exclude.includes(true);
            case 'number':
                return exclude.includes('1') ? 2 : 1;
            case 'object':
                return exclude.includes('{}') ? { a: 1 } : {};
            default:
                return {};
        }
    }


}



export interface IfElse {
    else?: any,
    if?: any
}


export const FALSY_VALUES = [null, undefined, 0, '', false, NaN];

IfService.start();
