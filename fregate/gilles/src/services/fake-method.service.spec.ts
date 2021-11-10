import { FakeMethodService } from './fake-method.service';
import { IF_EXPRESSION_STATEMENT_MOCK, PATH_MOCK_IF } from '../mocks/paths-statements.mock';
import { FAKE_CLASS_IF_A } from '../mocks/fake-class-stringified.mock';
import * as Tools from './tools.service';
import { Executable } from '../models/executable.model';
import { TEMP_FILE_PATH } from '../mocks/paths.mock';
import { ExecutableService } from './executable.service';
import { CallExpression, SyntaxKind } from 'ts-morph';
import { EXECUTABLE_IF_EXPRESSION_MOCK, EXECUTABLE_IF_MOCK } from '../mocks/executable.mock';

describe('FAKE METHOD SERVICE', () => {

    describe('getResult', () => {
        it('should return 666', () => {
            // @ts-ignore
            spyOn(FakeMethodService, 'generateFakeCode');
            const result = FakeMethodService.getResult(PATH_MOCK_IF, ['a']);
            // @ts-ignore
            expect(FakeMethodService.generateFakeCode).toHaveBeenCalled();
            expect(result).toEqual('666');
        });
    })


    describe('generateFakeCode', () => {
        it('no test: just create file for debug', () => {
            // @ts-ignore
            FakeMethodService.generateFakeCode(PATH_MOCK_IF);
        });

        it('should return FAKE_CLASS_IF_A', () => {
            spyOn(Tools, 'createFileSync');
            // @ts-ignore
            spyOn(FakeMethodService, 'getFakeCode').and.returnValue('resultGetFakeCode');
            // @ts-ignore
            FakeMethodService.generateFakeCode(PATH_MOCK_IF);
            expect(Tools.createFileSync).toHaveBeenCalledWith(TEMP_FILE_PATH, 'resultGetFakeCode');
        });
    })


    describe('getFakeCode', () => {
        it('should return FAKE_CLASS_IF_A', () => {
            // @ts-ignore
            spyOn(FakeMethodService, 'getFakeParameters');
            // @ts-ignore
            spyOn(FakeMethodService, 'getFakeCodeStatement');
            // @ts-ignore
            spyOn(FakeMethodService, 'stringifyProperties').and.returnValue(['message']);
            // @ts-ignore
            spyOn(ExecutableService, 'getCode').and.returnValue(FAKE_CLASS_IF_A);
            // @ts-ignore
            const result = FakeMethodService.getFakeCode(PATH_MOCK_IF);
            // @ts-ignore
            expect(FakeMethodService.getFakeParameters).toHaveBeenCalledWith(PATH_MOCK_IF);
            // @ts-ignore
            expect(FakeMethodService.getFakeCodeStatement).toHaveBeenCalled();
            // @ts-ignore
            expect(FakeMethodService.stringifyProperties).toHaveBeenCalledWith({ message: null, externalService: Object({ helloMethod: null, doSomething: null }) });
            expect(result).toEqual(FAKE_CLASS_IF_A);
        });
    });


    describe('getFakeParameters', () => {
        it(`should return ['name']`, () => {
            // @ts-ignore
            const result = FakeMethodService.getFakeParameters(PATH_MOCK_IF);
            expect(result).toEqual(['name']);
        });
    });


    describe('getFakeCodeStatement', () => {
        it(`should return EXECUTABLE_IF_EXPRESSION_MOCK with statement = IfStatement`, () => {
            const executableMock = new Executable();
            executableMock.methodName = 'helloMethod';
            executableMock.parameters = ['name'];
            // @ts-ignore
            spyOn(FakeMethodService, 'getFakeCodeExpressionStatement').and.returnValue(EXECUTABLE_IF_EXPRESSION_MOCK);
            // @ts-ignore
            const result = FakeMethodService.getFakeCodeStatement(PATH_MOCK_IF.route[1], executableMock);
            // @ts-ignore
            expect(FakeMethodService.getFakeCodeExpressionStatement).toHaveBeenCalledWith(PATH_MOCK_IF.route[1], executableMock)
            expect(result).toEqual(EXECUTABLE_IF_EXPRESSION_MOCK);
        });

        it(`should return EXECUTABLE_IF_EXPRESSION_MOCK with statement = IfStatement + first ExpressionStatement + `, () => {
            const executableMock = new Executable();
            executableMock.methodName = 'helloMethod';
            executableMock.parameters = ['name'];
            // @ts-ignore
            spyOn(FakeMethodService, 'getFakeCodeExpressionStatement').and.returnValue(EXECUTABLE_IF_EXPRESSION_MOCK);
            // @ts-ignore
            const result = FakeMethodService.getFakeCodeStatement(PATH_MOCK_IF.route[2], EXECUTABLE_IF_EXPRESSION_MOCK);
            // @ts-ignore
            expect(FakeMethodService.getFakeCodeExpressionStatement).toHaveBeenCalledWith(PATH_MOCK_IF.route[2], EXECUTABLE_IF_EXPRESSION_MOCK)
            expect(result).toEqual(EXECUTABLE_IF_EXPRESSION_MOCK);
        });

        it(`should return EXECUTABLE_IF_MOCK with statement = IfStatement + first & second ExpressionStatement + `, () => {
            const executableMock = new Executable();
            executableMock.methodName = 'helloMethod';
            executableMock.parameters = ['name'];
            // @ts-ignore
            spyOn(FakeMethodService, 'getFakeCodeReturnStatement').and.returnValue(EXECUTABLE_IF_MOCK);
            // @ts-ignore
            const result = FakeMethodService.getFakeCodeStatement(PATH_MOCK_IF.route[3], EXECUTABLE_IF_MOCK);
            // @ts-ignore
            expect(FakeMethodService.getFakeCodeReturnStatement).toHaveBeenCalledWith(PATH_MOCK_IF.route[3], EXECUTABLE_IF_MOCK);
            expect(result).toEqual(EXECUTABLE_IF_MOCK);
        });
    });


    describe('getFakeCodeExpressionStatement', () => {
        it(`should return EXECUTABLE_IF_EXPRESSION_MOCK`, () => {
            const executableMock = new Executable();
            executableMock.methodName = 'helloMethod';
            executableMock.parameters = ['name'];
            executableMock.properties = ['message: any;'];
            // @ts-ignore
            spyOn(FakeMethodService, 'addProperties').and.returnValue(['message']);
            // @ts-ignore
            spyOn(FakeMethodService, 'removeCallExpressions').and.returnValue(`this.message = 'resultHelloMethod';`);
            // @ts-ignore
            const result = FakeMethodService.getFakeCodeExpressionStatement(PATH_MOCK_IF.route[1], executableMock);
            // @ts-ignore
            expect(FakeMethodService.addProperties).toHaveBeenCalledWith(PATH_MOCK_IF.route[1]);
            // @ts-ignore
            expect(FakeMethodService.removeCallExpressions).toHaveBeenCalledWith(PATH_MOCK_IF.route[1]);
            expect(result).toEqual(EXECUTABLE_IF_EXPRESSION_MOCK);
        });
    });


    describe('getFakeCodeReturnStatement', () => {
        it(`should return EXECUTABLE_IF_EXPRESSION_MOCK`, () => {
            // @ts-ignore
            spyOn(FakeMethodService, 'removeCallExpressions').and.returnValue(`return 666;`);
            // @ts-ignore
            const result = FakeMethodService.getFakeCodeReturnStatement(PATH_MOCK_IF.route[3], EXECUTABLE_IF_EXPRESSION_MOCK);
            // @ts-ignore
            expect(FakeMethodService.removeCallExpressions).toHaveBeenCalledWith(PATH_MOCK_IF.route[3]);
            expect(result).toEqual(EXECUTABLE_IF_MOCK);
        });
    });


    describe('removeCallExpressions', () => {
        it(`should return EXECUTABLE_IF_EXPRESSION_MOCK`, () => {
            // @ts-ignore
            spyOn(FakeMethodService, 'removeCallExpression').and.returnValue(`this.message = 'resultHelloMethod';`);
            // @ts-ignore
            const result = FakeMethodService.removeCallExpressions(PATH_MOCK_IF.route[1]);
            // @ts-ignore
            expect(result).toEqual(`this.message = 'resultHelloMethod';`);
        });
    });


    describe('removeCallExpression', () => {
        it(`should return "this.message = 'resultHelloMethod';"`, () => {
            const callExpression: CallExpression = IF_EXPRESSION_STATEMENT_MOCK.getFirstDescendantByKind(SyntaxKind.CallExpression);
            // @ts-ignore
            const result = FakeMethodService.removeCallExpression(IF_EXPRESSION_STATEMENT_MOCK, callExpression);
            expect(result).toEqual(`this.message = 'resultHelloMethod';`);
        });
    })


    describe('addProperties', () => {
        it(`should return ['message']`, () => {
            // @ts-ignore
            spyOn(FakeMethodService, 'addProperty');
            // @ts-ignore
            FakeMethodService.addProperties(IF_EXPRESSION_STATEMENT_MOCK);
            // @ts-ignore
            expect(FakeMethodService.addProperty).toHaveBeenCalled();
        });
    })


    describe('addProperty', () => {
        it(`should return ['message']`, () => {
            const mockedResult = {
                message: null
            }
            // @ts-ignore
            const result = FakeMethodService.addProperty(IF_EXPRESSION_STATEMENT_MOCK.getFirstDescendantByKind(SyntaxKind.Identifier));
            expect(result).toEqual(mockedResult);
        });
    })


    describe('stringifyProperties', () => {
        it(`should return one Identifier`, () => {
            const properties = { message: null, externalService: { helloMethod: null }};
            const mockedResult = [
                'message: any;',
                `externalService: { helloMethod: any };`
            ]
            // @ts-ignore
            const result = FakeMethodService.stringifyProperties(properties);
            expect(result).toEqual(mockedResult);
        });
    })
})
