import { GlobalService } from './global.service';
import { CLASS_SPEC_MOCK } from '../mocks/class-spec.mock';
import { SOURCE_FILE_MOCK } from '../mocks/source-file.mock';
import { IMPORT_LINE_MOCK } from '../mocks/import-lines.mock';
import { DESCRIBE_CODE_MOCK, DESCRIBE_MOCK } from '../mocks/describe.mock';
import { DescribeService } from './describe.service';
import { MAIN_SPEC_FILE_MOCK } from '../mocks/examples/my-service/main.mock';

describe('CLASS_SPEC SERVICE', () => {

    describe('generate', () => {

        // it('Should return correct Global', () => {
        //     // @ts-ignore
        //     spyOn(GlobalService, 'generateDescribe').and.returnValue(DESCRIBE_MOCK);
        //     // @ts-ignore
        //     spyOn(GlobalService, 'generateImports').and.returnValue(IMPORT_LINE_MOCK);
        //     const result = GlobalService.generateFlags(SOURCE_FILE_MOCK);
        //     // @ts-ignore
        //     expect(GlobalService.generateImports).toHaveBeenCalledWith(SOURCE_FILE_MOCK, CLASS_SPEC_MOCK.className);
        //     expect(result).toEqual(CLASS_SPEC_MOCK);
        // });
    });


    describe('generateDescribe', () => {

        it('Should return correct DESCRIBE_MOCK', () => {
            // @ts-ignore
            const result = GlobalService.generateDescribe(SOURCE_FILE_MOCK, CLASS_SPEC_MOCK.className)
            expect(result).toEqual(DESCRIBE_MOCK);
        });
    });


    describe('generateImports', () => {

        // it('Should return correct IMPORTS_MOCK', () => {
        //     spyOn(SOURCE_FILE_MOCK, 'getBaseName').and.returnValue('my.service.ts');
        //     // @ts-ignore
        //     const result = GlobalService.generateImports(SOURCE_FILE_MOCK, CLASS_SPEC_MOCK.className);
        //     expect(SOURCE_FILE_MOCK.getBaseName).toHaveBeenCalled();
        //     expect(result).toEqual(IMPORT_LINE_MOCK);
        // });
    });


    describe('getCode', () => {

        it('Should return correct MAIN_SPEC_FILE_MOCK', () => {
            spyOn(DescribeService, 'getCode').and.returnValues(DESCRIBE_CODE_MOCK);
            // @ts-ignore
            const result = GlobalService.getCode(CLASS_SPEC_MOCK);
            expect(result).toEqual(MAIN_SPEC_FILE_MOCK);
        });
    });
});
