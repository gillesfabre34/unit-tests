import { CallExternalMethod } from './call-external-method';

describe('CallExternalMethod', () => {
    let service;

    beforeEach(() => {
        service = new CallExternalMethod();
    });

    describe('myMethod', () => {

        describe('no illusion tests', () => {

            beforeEach(() => {
                service.externalService = {
                    isGreaterThanOne: () => {}
                }
            });

            it(`should return 1 when a = 2`, () => {
                spyOn(service.externalService, 'isGreaterThanOne').and.returnValue(true);
                const result = service.myMethod(2);
                expect(service.externalService.isGreaterThanOne).toHaveBeenCalledWith(2);
                expect(result).toBeTrue();
            });
        })


        describe('pseudo-random tests', () => {
            it(`should return 1 when a = 2`, () => {
                spyOn(service.externalService, 'isGreaterThanOne');
                const result = service.myMethod(2);
                expect(service.externalService.isGreaterThanOne).toHaveBeenCalledWith(2);
                expect(result).toBeTrue();
            });
        });
    });
});
