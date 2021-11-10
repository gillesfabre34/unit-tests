import { capitalize, createFileSync, deepMerge } from './tools.service';
import { TEMP_FILE_PATH } from '../mocks/paths.mock';
import {
    CallExpression,
    ExpressionStatement,
    Identifier,
    MethodDeclaration,
    ReturnStatement,
    Statement,
    SyntaxKind,
    ThisExpression
} from 'ts-morph';
import { Path } from '../models/path.model';
import { Executable } from '../models/executable.model';
import { ExecutableService } from './executable.service';
import { tabs } from '../mocks/tabs.mock';
import { Ast } from './ast.service';

export class FakeMethodService {

    static properties: object = {};

    static getResult(path: Path, params: any[], fakePath?: string): string {
        this.generateFakeCode(path);
        const fakeClassPath = fakePath ?? '../mocks/examples/my-service/fake-class.mock.ts';
        const fakeFile = require(fakeClassPath);
        const fakeClass = new fakeFile.FakeClass();
        // @ts-ignore
        const result: any = fakeClass.fakeMethod(...params);
        return result?.toString();
    }


    private static generateFakeCode(path: Path): void {
        const fakeCode: string = this.getFakeCode(path);
        createFileSync(TEMP_FILE_PATH, fakeCode);
    }


    private static getFakeCode(path: Path): string {
        let executable = new Executable();
        executable.methodName = path.route[0].getFirstAncestorByKind(SyntaxKind.MethodDeclaration).getName();
        executable.parameters = this.getFakeParameters(path);
        for (const statement of path.route) {
            this.getFakeCodeStatement(statement, executable);
        }
        executable.properties = this.stringifyProperties(this.properties);
        return ExecutableService.getCode(executable);
    }


    private static getFakeParameters(path: Path): string[] {
        const methodDeclaration: MethodDeclaration = path.route[0].getFirstAncestorByKind(SyntaxKind.MethodDeclaration);
        return methodDeclaration.getStructure().parameters?.map(p => p.name) ?? [];
    }


    private static getFakeCodeStatement(statement: Statement, executable: Executable): Executable {
        switch (statement.getKind()) {
            case SyntaxKind.ExpressionStatement:
                return this.getFakeCodeExpressionStatement(statement as ExpressionStatement, executable);
            case SyntaxKind.ReturnStatement:
                return this.getFakeCodeReturnStatement(statement as ReturnStatement, executable);
            default:
                return executable;
        }
    }


    private static getFakeCodeExpressionStatement(expressionStatement: ExpressionStatement, executable: Executable): Executable {
        const statementWithoutCalls: string = this.removeCallExpressions(expressionStatement);
        executable.methodBody = statementWithoutCalls ? `${executable.methodBody}${tabs(3)}${statementWithoutCalls}\n` : executable.methodBody;
        this.addProperties(expressionStatement);
        return executable;
    }


    private static getFakeCodeReturnStatement(returnStatement: ReturnStatement, executable: Executable): Executable {
        const statementWithoutCalls: string = this.removeCallExpressions(returnStatement);
        executable.methodBody = `${executable.methodBody}${tabs(3)}${statementWithoutCalls}\n`;
        return executable;
    }


    // TODO : recursivity
    private static removeCallExpressions(statement: Statement): string {
        const callExpression: CallExpression = statement.getFirstDescendantByKind(SyntaxKind.CallExpression);
        if (statement.getFirstChild() === callExpression) {
            return '';
        }
        return callExpression ? this.removeCallExpression(statement, callExpression) : statement.getText();
    }


    private static removeCallExpression(statement: Statement, callExpression: CallExpression): string {
        const splitCall = callExpression.getText()
            .split('(')[0]
            .split('.');
        const returnedValue = `'result${capitalize(splitCall[splitCall.length - 1])}'`;
        return statement.getText().replace(callExpression.getText(), returnedValue);
    }


    private static addProperties(expressionStatement: ExpressionStatement): void {
        const thisExpressions: ThisExpression[] = expressionStatement.getDescendantsOfKind(SyntaxKind.ThisKeyword);
        for (const thisExpression of thisExpressions) {
            const nextChainedIdentifier: Identifier = Ast.nextChainedIdentifier(thisExpression);
            this.properties = deepMerge(this.properties, this.addProperty(nextChainedIdentifier))
        }
    }


    private static addProperty(identifier: Identifier): object {
        if (!identifier) {
            return {};
        }
        const nameIdentifier = identifier.getText();
        let result = {};
        const nextChainedIdentifier = Ast.nextChainedIdentifier(identifier);
        result[nameIdentifier] = nextChainedIdentifier
            ? this.addProperty(nextChainedIdentifier)
            : null;
        return result;
    }


    private static stringifyProperties(properties: object): string[] {
        if (Object.keys(properties).length === 0) {
            return [];
        }
        let props = JSON.stringify(properties)
            .slice(1, -1)
            .replace(/":/g, ': ')
            .replace(/"/g, '')
            .replace(/{/g, '{ ')
            .replace(/}/g, ' }')
            .replace(/null/g, 'any');
        let deepObject = 0;
        for (let i = 0; i < props.length; i++) {
            if (props.charAt(i) === ',' && deepObject === 0) {
                props = `${props.slice(0, i)};${props.slice(i + 1)}`;
            }
            if (props.charAt(i) === '{') {
                deepObject++;
            }
            if (props.charAt(i) === '}') {
                deepObject--;
            }
        }
        const propsArray: string[] = props
            .replace(/,/, ', ')
            .split(';')
            .map(p => `${p};`);
        return propsArray;
    }

}
