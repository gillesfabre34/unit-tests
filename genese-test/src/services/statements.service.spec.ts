import { IT_NAME_A, IT_NAME_UNDEFINED } from '../mocks/it.mock';
import { PATH_MOCK_ELSE, PATH_MOCK_IF } from '../mocks/paths-statements.mock';
import { StatementsService } from './statements.service';
import { CallExpression, ExpressionStatement, MethodDeclaration, SyntaxKind } from 'ts-morph';
import { SOURCE_FILE_MOCK } from '../mocks/source-file.mock';
import { tabs } from '../mocks/tabs.mock';
import { It } from '../models/it.model';
import { MockGeneratorService } from './mock-generator.service';
import { FakeMethodService } from './fake-method.service';

describe('STATEMENTS SERVICE', () => {

    beforeEach(() => {
        // @ts-ignore
        StatementsService.it = new It();
    })

    describe('generateIt', () => {

        beforeEach(() => {
            // @ts-ignore
            spyOn(StatementsService, 'generateReturnCode');
        });

        it('Should return IT_NAME_A', () => {
            // @ts-ignore
            const result = StatementsService.generateIt(PATH_MOCK_IF);
            // @ts-ignore
            expect(StatementsService.generateReturnCode).toHaveBeenCalledWith(PATH_MOCK_IF);
            expect(result).toEqual(IT_NAME_A)
        });
        it('Should return IT_NAME_UNDEFINED', () => {
            // @ts-ignore
            const result = StatementsService.generateIt(PATH_MOCK_ELSE);
            // @ts-ignore
            expect(StatementsService.generateReturnCode).toHaveBeenCalledWith(PATH_MOCK_ELSE);
            expect(result).toEqual(IT_NAME_UNDEFINED)
        });
    });



    describe('generateCoreCode', () => {
        it('Should update this.it.assertion.core', () => {
            // @ts-ignore
            spyOn(StatementsService, 'getMethodParams').and.returnValue(`'a'`);
            // @ts-ignore
            StatementsService.generateCoreCode(PATH_MOCK_IF);
            // @ts-ignore
            expect(StatementsService.it.assertion.core).toEqual(`${tabs(3)}const result = service.myMethod('a');`)
        });
    });


    describe('getMethodParams', () => {
        it(`Should return 'a'`, () => {
            const methodDeclaration: MethodDeclaration = SOURCE_FILE_MOCK.getFirstDescendantByKind(SyntaxKind.MethodDeclaration);
            spyOn(MockGeneratorService, 'getMock').and.returnValues(`'a'`);
            // @ts-ignore
            const result = StatementsService.getMethodParams(methodDeclaration);
            // @ts-ignore
            expect(result).toEqual(`'a'`)
        });
    });


    describe('generateExpressionStatementCode', () => {
        it('Should call generateCodeForCallExpression', () => {
            // @ts-ignore
            spyOn(StatementsService, 'generateCodeForCallExpression');
            const statement: ExpressionStatement = SOURCE_FILE_MOCK.getFirstDescendantByKind(SyntaxKind.ExpressionStatement);
            const callExpression: CallExpression = statement.getFirstDescendantByKind(SyntaxKind.CallExpression);
            // @ts-ignore
            StatementsService.generateExpressionStatementCode(statement);
            // @ts-ignore
            expect(StatementsService.generateCodeForCallExpression).toHaveBeenCalledWith(callExpression)
        });
    });


    describe('generateCodeForCallExpression', () => {
        it('Should add spyOn and expect', () => {
            // @ts-ignore
            spyOn(StatementsService, 'getCalledWith').and.returnValue(`'a'`)
            const callExpression: CallExpression = SOURCE_FILE_MOCK.getFirstDescendantByKind(SyntaxKind.ExpressionStatement)
                .getFirstDescendantByKind(SyntaxKind.CallExpression);
            // @ts-ignore
            StatementsService.generateCodeForCallExpression(callExpression);
            // @ts-ignore
            expect(StatementsService.it.assertion.spies).toEqual([`${tabs(3)}spyOn(service.externalService, 'helloMethod').and.returnValues('resultHelloMethod');\n`]);
            // @ts-ignore
            expect(StatementsService.it.assertion.expects).toEqual([`${tabs(3)}expect(service.externalService.helloMethod).toHaveBeenCalledWith('a')`]);
        });
    });


    describe('getCalledWith', () => {
        it('Should add spyOn and expect', () => {
            const callExpression: CallExpression = SOURCE_FILE_MOCK.getFirstDescendantByKind(SyntaxKind.ExpressionStatement)
                .getFirstDescendantByKind(SyntaxKind.CallExpression);
            // @ts-ignore
            const result = StatementsService.getCalledWith(callExpression);
            expect(result).toEqual(`'a'`);
        });
    });


    describe('generateReturnCode', () => {
        it('Should call FakeMethodService.getResult and update this.it', () => {
            // @ts-ignore
            spyOn(StatementsService, 'generateParams').and.returnValue(['a']);
            spyOn(FakeMethodService, 'getResult');
            // @ts-ignore
            StatementsService.generateReturnCode(PATH_MOCK_IF);
            expect(FakeMethodService.getResult).toHaveBeenCalledWith(PATH_MOCK_IF, ['a']);
        });
    });


    describe('generateParams', () => {
        it( `Should return ['a']`, () => {
            // @ts-ignore
            const result = StatementsService.generateParams(PATH_MOCK_IF);
            expect(result).toEqual(['a']);
        });
    });
})
