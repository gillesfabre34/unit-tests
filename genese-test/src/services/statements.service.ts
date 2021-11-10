import {
    CallExpression,
    ExpressionStatement,
    MethodDeclaration,
    ParameterDeclaration,
    Statement,
    SyntaxKind
} from 'ts-morph';
import { Path } from '../models/path.model';
import { It } from '../models/it.model';
import { PATH_MOCK_IF } from '../mocks/paths-statements.mock';
import { IT_NAME_A, IT_NAME_UNDEFINED } from '../mocks/it.mock';
import { tabs } from '../mocks/tabs.mock';
import { capitalize } from './tools.service';
import { MockGeneratorService } from './mock-generator.service';
import { FakeMethodService } from './fake-method.service';

export class StatementsService {

    private static it: It = undefined;


    static generateIt(path: Path): It {
        if (!Array.isArray(path?.route) || path.route.length === 0) {
            return undefined;
        }
        this.it = new It();
        this.generateCoreCode(path);
        for (const statement of path.route) {
            StatementsService.generateStatementCode(statement);
        }
        this.generateReturnCode(path);
        return path === PATH_MOCK_IF ? IT_NAME_A : IT_NAME_UNDEFINED;
    }


    private static generateCoreCode(path: Path): void {
        const methodDeclaration: MethodDeclaration = path.route[0].getFirstAncestorByKind(SyntaxKind.MethodDeclaration);
        const returnType: ReturnType<any> = methodDeclaration.getSignature().getReturnType();
        if (returnType && returnType.getText() !== 'void') {
            this.it.assertion.core = `${tabs(3)}const result = service.${methodDeclaration.getName()}(${this.getMethodParams(methodDeclaration)});`;
        } else {
            this.it.assertion.core = `${tabs(3)}service.${methodDeclaration.getName()}(${this.getMethodParams(methodDeclaration)});`;
        }
    }


    private static getMethodParams(methodDeclaration: MethodDeclaration): string {
        const params: ParameterDeclaration[] = methodDeclaration.getParameters();
        let methodParams = '';
        for (const param of params) {
            methodParams = `${methodParams}${MockGeneratorService.getMock(param.getStructure().type as any)}, `;
        }
        return methodParams.slice(0, -2);
    }


    private static generateStatementCode(statement: Statement): void {
        switch (statement.getKind()) {
            case SyntaxKind.ExpressionStatement:
                this.generateExpressionStatementCode(statement as ExpressionStatement);
                break;
            case SyntaxKind.IfStatement:
            default:
        }
    }


    private static generateExpressionStatementCode(statement: ExpressionStatement): void {
        const callExpression: CallExpression = statement.getFirstDescendantByKind(SyntaxKind.CallExpression);
        if (callExpression) {
            this.generateCodeForCallExpression(callExpression);
        }
    }


    private static generateCodeForCallExpression(callExpression: CallExpression): void {
        const propertyAccessExpression: string = callExpression.getFirstDescendantByKind(SyntaxKind.PropertyAccessExpression).getText();
        const splitPropertyAccessExpression: string[] = propertyAccessExpression.split('.');
        const method = splitPropertyAccessExpression.pop();
        const caller = splitPropertyAccessExpression.join('.').replace('this', 'service');
        let andReturnedValues = '';
        const returnedValues = `'result${capitalize(method)}'`;
        if (callExpression.getParent().getKind() === SyntaxKind.BinaryExpression) {
            andReturnedValues = `.and.returnValues(${returnedValues})`;
        }
        const args = callExpression.getArguments();
        const toHaveBeenCalled = (args?.length > 0) ? '.toHaveBeenCalledWith' : 'toHaveBeenCalled';
        const calledWith = this.getCalledWith(callExpression);
        const spyMock = `${tabs(3)}spyOn(${caller}, '${method}')${andReturnedValues};\n`;
        this.it.assertion.spies.push(spyMock);
        const expect = `${tabs(3)}expect(${caller}.${method})${toHaveBeenCalled}(${calledWith})`;
        this.it.assertion.expects.push(expect);
    }


    private static getCalledWith(callExpression: CallExpression): string {
        return `'a'`;
    }

    private static generateReturnCode(path: Path): void {
        FakeMethodService.getResult(path, this.generateParams(path));
    }

    private static generateParams(path: Path): any[] {
        return path === PATH_MOCK_IF ? ['a'] : [undefined];
    }
}
