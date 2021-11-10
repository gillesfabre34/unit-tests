import { CallExternalMethodInsideIf } from './call-external-method-inside-if';

describe('CallExternalMethodInsideIf', () => {
    let service;

    beforeEach(() => {
        service = new CallExternalMethodInsideIf();
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
                expect(result).toEqual(1);
            });
            it(`should return 0 when a = 0`, () => {
                spyOn(service.externalService, 'isGreaterThanOne').and.returnValue(false);
                const result = service.myMethod(0);
                expect(service.externalService.isGreaterThanOne).toHaveBeenCalledWith(0);
                expect(result).toEqual(0);
            });
        })


        describe('pseudo-random tests', () => {
            it(`should return 1 when a = 2`, () => {
                spyOn(service.externalService, 'isGreaterThanOne');
                const result = service.myMethod(2);
                expect(service.externalService.isGreaterThanOne).toHaveBeenCalledWith(2);
                expect(result).toEqual(1);
            });
            it(`should return 0 when a = 0`, () => {
                spyOn(service.externalService, 'isGreaterThanOne');
                const result = service.myMethod(0);
                expect(service.externalService.isGreaterThanOne).toHaveBeenCalledWith(0);
                expect(result).toEqual(0);
            });
        });
    });
});
