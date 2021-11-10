import { It } from '../models/it.model';
import { IT_NAME_A_ASSERTION, IT_NAME_UNDEFINED_ASSERTION } from './it-assertion.mock';

const itNameA = new It();
itNameA.expectation = 'should return 666 when name = "a"';
itNameA.assertion = IT_NAME_A_ASSERTION;

const itNameUndefined = new It();
itNameUndefined.expectation = 'should return 666 when name = undefined';
itNameUndefined.assertion = IT_NAME_UNDEFINED_ASSERTION;


export const IT_NAME_A = itNameA;
export const IT_NAME_UNDEFINED = itNameUndefined;

export const IT_NAME_A_CODE = `        it('should return 666 when name = "a"', () => {
            spyOn(service.externalService, 'helloMethod').and.returnValues('resultHelloMethod');
            spyOn(service.externalService, 'doSomething');
            const result = service.myMethod('a');
            expect(service.externalService.helloMethod).toHaveBeenCalledWith('a');
            expect(service.externalService.doSomething).toHaveBeenCalledWith('resultHelloMethod');
            expect(service.message).toEqual('resultHelloMethod');
            expect(result).toEqual(666);
        });\n`;

export const IT_NAME_UNDEFINED_CODE = `        it('should return 666 when name = undefined', () => {
            spyOn(service.externalService, 'helloMethod').and.returnValues('resultHelloMethod');
            spyOn(service.externalService, 'doSomething');
            const result = service.myMethod(undefined);
            expect(service.externalService.helloMethod).toHaveBeenCalledWith(' World !');
            expect(service.externalService.doSomething).toHaveBeenCalledWith('resultHelloMethod');
            expect(service.message).toEqual('resultHelloMethod');
            expect(result).toEqual(666);
        });\n`;

export const ITS_MOCK = [IT_NAME_A, IT_NAME_UNDEFINED];
export const ITS_CODE = `${IT_NAME_A_CODE}${IT_NAME_UNDEFINED_CODE}`;

