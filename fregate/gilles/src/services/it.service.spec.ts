import { DESCRIBE_MOCK } from '../mocks/describe.mock';
import { IT_NAME_A, IT_NAME_A_CODE, IT_NAME_UNDEFINED, IT_NAME_UNDEFINED_CODE, ITS_CODE } from '../mocks/it.mock';
import { ItService } from './it.service';
import {
    PATH_MOCK_ELSE,
    PATH_MOCK_IF,
    PATHS_MOCK
} from '../mocks/paths-statements.mock';
import { PathService } from './path.service';
import { tabs } from '../mocks/tabs.mock';
import { StatementsService } from './statements.service';
import {
    IT_NAME_A_ASSERTION,
    IT_NAME_A_ASSERTION_CODE, IT_NAME_UNDEFINED_ASSERTION,
    IT_NAME_UNDEFINED_ASSERTION_CODE
} from '../mocks/it-assertion.mock';
import { logCharsAndCompare } from './tools.service';

describe('IT SERVICE', () => {


    describe('getIts', () => {
        it('Should return [IT_NAME_A, IT_NAME_UNDEFINED]', () => {
            spyOn(PathService, 'getPaths').and.returnValue(PATHS_MOCK);
            // @ts-ignore
            spyOn(StatementsService, 'generateIt').and.returnValues(IT_NAME_A, IT_NAME_UNDEFINED);
            const result = ItService.generateIts(DESCRIBE_MOCK.describes[0]);
            expect(PathService.getPaths).toHaveBeenCalledWith(DESCRIBE_MOCK.describes[0]);
            // @ts-ignore
            expect(StatementsService.generateIt).toHaveBeenCalledWith(PATH_MOCK_IF);
            // @ts-ignore
            expect(StatementsService.generateIt).toHaveBeenCalledWith(PATH_MOCK_ELSE);
            expect(result).toEqual([IT_NAME_A, IT_NAME_UNDEFINED])
        });
    });


    describe('joinItsText', () => {
        it('Should return ITS_CODE', () => {
            spyOn(ItService, 'getCodeFromItObject').and.returnValues(IT_NAME_A_CODE, IT_NAME_UNDEFINED_CODE);
            // @ts-ignore
            const result = ItService.joinItsText([IT_NAME_A, IT_NAME_UNDEFINED]);
            expect(result).toEqual(ITS_CODE)
        });
    });


    describe('getCodeFromItObject', () => {
        it('Should return IT_NAME_A_CODE', () => {
            // @ts-ignore
            spyOn(ItService, 'getItDeclaration').and.returnValue(`${tabs(2)}it('should return 666 when name = "a"', () => {\n`);
            // @ts-ignore
            spyOn(ItService, 'getAssertionCode').and.returnValue(IT_NAME_A_ASSERTION_CODE);
            const result = ItService.getCodeFromItObject(IT_NAME_A);
            expect(result).toEqual(IT_NAME_A_CODE);
        });
        it('Should return IT_NAME_UNDEFINED_CODE', () => {
            // @ts-ignore
            spyOn(ItService, 'getItDeclaration').and.returnValue(`${tabs(2)}it('should return 666 when name = undefined', () => {\n`);
            // @ts-ignore
            spyOn(ItService, 'getAssertionCode').and.returnValue(IT_NAME_UNDEFINED_ASSERTION_CODE);
            const result = ItService.getCodeFromItObject(IT_NAME_UNDEFINED);
            expect(result).toEqual(IT_NAME_UNDEFINED_CODE);
        });
    });


    describe('getItDeclaration', () => {
        it('Should return IT_NAME_A', () => {
            const mockedResult = `${tabs(2)}it('should return 666 when name = "a"', () => {\n`;
            // @ts-ignore
            const result = ItService.getItDeclaration(IT_NAME_A);
            expect(result).toEqual(mockedResult);
        });
        it('Should return IT_NAME_UNDEFINED', () => {
            const mockedResult = `${tabs(2)}it('should return 666 when name = undefined', () => {\n`;
            // @ts-ignore
            const result = ItService.getItDeclaration(IT_NAME_UNDEFINED);
            expect(result).toEqual(mockedResult);
        });
    });


    describe('getAssertionCode', () => {
        it('Should return IT_NAME_A_ASSERTION_CODE', () => {
            const result = ItService.getAssertionCode(IT_NAME_A_ASSERTION);
            expect(result).toEqual(IT_NAME_A_ASSERTION_CODE)
        });
        it('Should return IT_NAME_UNDEFINED_ASSERTION_CODE', () => {
            const result = ItService.getAssertionCode(IT_NAME_UNDEFINED_ASSERTION);
            expect(result).toEqual(IT_NAME_UNDEFINED_ASSERTION_CODE);
        });
    });
})
