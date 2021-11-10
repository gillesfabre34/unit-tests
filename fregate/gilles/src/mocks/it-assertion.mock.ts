import { ItAssertion } from '../models/it-assertion.model';

const itNameAAssertion = new ItAssertion();
itNameAAssertion.core = `const result = service.myMethod('a');\n`;
itNameAAssertion.expects = [
    `expect(service.externalService.helloMethod).toHaveBeenCalledWith('a');\n`,
    `expect(service.externalService.doSomething).toHaveBeenCalledWith('resultHelloMethod');\n`,
    `expect(service.message).toEqual('resultHelloMethod');\n`,
    `expect(result).toEqual(666);\n`
];
itNameAAssertion.spies = [
    `spyOn(service.externalService, 'helloMethod').and.returnValues('resultHelloMethod');\n`,
    `spyOn(service.externalService, 'doSomething');\n`
]


const itNameUndefinedAssertion = new ItAssertion();
itNameUndefinedAssertion.core = `const result = service.myMethod(undefined);\n`;
itNameUndefinedAssertion.expects = [
    `expect(service.externalService.helloMethod).toHaveBeenCalledWith(' World !');\n`,
    `expect(service.externalService.doSomething).toHaveBeenCalledWith('resultHelloMethod');\n`,
    `expect(service.message).toEqual('resultHelloMethod');\n`,
    `expect(result).toEqual(666);\n`
];
itNameUndefinedAssertion.spies = [
    `spyOn(service.externalService, 'helloMethod').and.returnValues('resultHelloMethod');\n`,
    `spyOn(service.externalService, 'doSomething');\n`
]


export const IT_NAME_A_ASSERTION = itNameAAssertion;
export const IT_NAME_UNDEFINED_ASSERTION = itNameUndefinedAssertion;


const itNameAAssertionCode = `            spyOn(service.externalService, 'helloMethod').and.returnValues('resultHelloMethod');
            spyOn(service.externalService, 'doSomething');
            const result = service.myMethod('a');
            expect(service.externalService.helloMethod).toHaveBeenCalledWith('a');
            expect(service.externalService.doSomething).toHaveBeenCalledWith('resultHelloMethod');
            expect(service.message).toEqual('resultHelloMethod');
            expect(result).toEqual(666);\n`;

const itNameUndefinedAssertionCode = `            spyOn(service.externalService, 'helloMethod').and.returnValues('resultHelloMethod');
            spyOn(service.externalService, 'doSomething');
            const result = service.myMethod(undefined);
            expect(service.externalService.helloMethod).toHaveBeenCalledWith(' World !');
            expect(service.externalService.doSomething).toHaveBeenCalledWith('resultHelloMethod');
            expect(service.message).toEqual('resultHelloMethod');
            expect(result).toEqual(666);\n`;



export const IT_NAME_A_ASSERTION_CODE = itNameAAssertionCode;
export const IT_NAME_UNDEFINED_ASSERTION_CODE = itNameUndefinedAssertionCode;
