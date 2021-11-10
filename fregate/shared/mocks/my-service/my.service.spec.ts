import { MyService } from './my.service';

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
        it('should call doSomething()', () => {
            spyOn(service.externalService, 'helloMethod').and.returnValues('resultHelloMethod');
            spyOn(service.externalService, 'doSomething');
            const result = service.myMethod('a');
            expect(service.externalService.helloMethod).toHaveBeenCalledWith('a');
            expect(service.externalService.doSomething).toHaveBeenCalledWith('resultHelloMethod');
            expect(service.message).toEqual('resultHelloMethod');
            expect(result).toEqual(666);
        });
        it('should change message value without name', () => {
            spyOn(service.externalService, 'helloMethod').and.returnValues('resultHelloMethod');
            spyOn(service.externalService, 'doSomething');
            const result = service.myMethod(undefined);
            expect(service.externalService.helloMethod).toHaveBeenCalledWith(' World !');
            expect(service.externalService.doSomething).toHaveBeenCalledWith('resultHelloMethod');
            expect(service.message).toEqual('resultHelloMethod');
            expect(result).toEqual(666);
        })
    });
});
