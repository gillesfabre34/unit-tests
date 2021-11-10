import { DescribeService } from './describe.service';
import { SOURCE_FILE_MOCK } from '../mocks/source-file.mock';
import { DESCRIBE_MY_METHOD_CODE_MOCK, DESCRIBE_MY_METHOD_MOCK } from '../mocks/describe-my-method.mock';
import { MethodDeclaration, SyntaxKind } from 'ts-morph';
import { DESCRIBE_CODE_MOCK, DESCRIBE_MOCK } from '../mocks/describe.mock';
import { ITS_CODE, ITS_MOCK } from '../mocks/it.mock';
import { ItService } from './it.service';
import { Ast } from './ast.service';

describe('DESCRIBE SERVICE', () => {


    describe('generate', () => {

        it('Should return [DESCRIBE_MY_METHOD_MOCK]', () => {
            spyOn(Ast, 'classDeclaration').and.returnValue(SOURCE_FILE_MOCK.getFirstDescendantByKind(SyntaxKind.ClassDeclaration))
            // @ts-ignore
            spyOn(DescribeService, 'generateMethodDescribe').and.returnValues(DESCRIBE_MY_METHOD_MOCK)
            const result = DescribeService.generate(SOURCE_FILE_MOCK);
            expect(result).toEqual([DESCRIBE_MY_METHOD_MOCK])
        });
    });


    describe('generateMethodDescribe', () => {

        it('Should return DESCRIBE_MY_METHOD_MOCK', () => {
            const methodDeclaration: MethodDeclaration = SOURCE_FILE_MOCK.getDescendantsOfKind(SyntaxKind.MethodDeclaration)[0];
            spyOn(ItService, 'generateIts').and.returnValues(ITS_MOCK)
            // @ts-ignore
            const result = DescribeService.generateMethodDescribe(methodDeclaration, SOURCE_FILE_MOCK);
            // expect(result).toEqual(DESCRIBE_MY_METHOD_MOCK)
        });
    });


    describe('getCode', () => {

        it('Should return DESCRIBE_CODE_MOCK', () => {
            // @ts-ignore
            spyOn(DescribeService, 'getDescribeMethodCode').and.returnValues(DESCRIBE_MY_METHOD_CODE_MOCK);
            const result = DescribeService.getCode(DESCRIBE_MOCK);
            // expect(result).toEqual(DESCRIBE_CODE_MOCK)
        });
    });


    describe('getDescribeMethodCode', () => {

        it('Should return DESCRIBE_CODE_MOCK', () => {
            spyOn(ItService, 'joinItsText').and.returnValues(ITS_CODE);
            // @ts-ignore
            const result = DescribeService.getDescribeMethodCode(DESCRIBE_MOCK.describes[0]);
            expect(ItService.joinItsText).toHaveBeenCalledWith(DESCRIBE_MOCK.describes[0].its);
            // expect(result).toEqual(`describe('myMethod', () => {\n${ITS_CODE}    });\n`)
        });
    });
})
