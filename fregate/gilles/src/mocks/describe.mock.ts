import { Describe } from '../models/describe.model';
import { DESCRIBE_MY_METHOD_MOCK } from './describe-my-method.mock';
import { BEFORE_EACH_MOCK } from './before-each.mock';
import { SOURCE_FILE_MOCK } from './source-file.mock';

const describe = new Describe();
describe.sourceFile = SOURCE_FILE_MOCK;
describe.title = 'MyService';
describe.beforeEach = BEFORE_EACH_MOCK;
describe.describes = [DESCRIBE_MY_METHOD_MOCK];

export const DESCRIBE_MOCK: Describe = describe;

export const DESCRIBE_CODE_MOCK = `
describe('MyService', () => {
    let service;

    beforeEach(() => {
        service = new MyService(undefined);
        service.externalService = {
            helloMethod: () => {},
            doSomething: () => {}
        }
    });

    it('should exist', () => {
        expect(service).toBeTruthy();
    });

    describe('myMethod', () => {
        it('should return 666 when name = "a"', () => {
            spyOn(service.externalService, 'helloMethod').and.returnValues('resultHelloMethod');
            spyOn(service.externalService, 'doSomething');
            const result = service.myMethod('a');
            expect(service.externalService.helloMethod).toHaveBeenCalledWith('a');
            expect(service.externalService.doSomething).toHaveBeenCalledWith('resultHelloMethod');
            expect(service.message).toEqual('resultHelloMethod');
            expect(result).toEqual(666);
        });
        it('should return 666 when name = undefined', () => {
            spyOn(service.externalService, 'helloMethod').and.returnValues('resultHelloMethod');
            spyOn(service.externalService, 'doSomething');
            const result = service.myMethod(undefined);
            expect(service.externalService.helloMethod).toHaveBeenCalledWith(' World !');
            expect(service.externalService.doSomething).toHaveBeenCalledWith('resultHelloMethod');
            expect(service.message).toEqual('resultHelloMethod');
            expect(result).toEqual(666);
        });
    });
});`;
