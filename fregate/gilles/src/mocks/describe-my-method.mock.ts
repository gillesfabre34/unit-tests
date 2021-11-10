import { Describe } from '../models/describe.model';
import { IT_NAME_A, IT_NAME_UNDEFINED } from './it.mock';
import { SOURCE_FILE_MOCK } from './source-file.mock';
import cloneDeep from 'clone-deep-circular-references';

const describe = new Describe();
describe.title = 'isNotFalsy';
describe.sourceFile = SOURCE_FILE_MOCK;
describe.its = [
    cloneDeep(IT_NAME_A),
    cloneDeep(IT_NAME_UNDEFINED)
];
describe.beforeEach = undefined;

export const DESCRIBE_MY_METHOD_MOCK: Describe = describe;

export const DESCRIBE_MY_METHOD_CODE_MOCK = `describe('myMethod', () => {
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
`;
