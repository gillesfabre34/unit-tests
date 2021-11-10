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

            it(`should return XXX when a = 2`, () => { // TODO
                spyOn(service.externalService, 'isGreaterThanOne').and.returnValue(XXX); // TODO
                const result = service.myMethod(2);
                expect(service.externalService.isGreaterThanOne).toHaveBeenCalledWith(XXX); // TODO
                expect(result).XXX; // TODO
            });
        })


        describe('pseudo-random tests', () => {
            it(`should return XXX when a = 2`, () => { // TODO
                spyOn(service.externalService, 'isGreaterThanOne');
                const result = service.myMethod(2);
                expect(service.externalService.isGreaterThanOne).toHaveBeenCalledWith(2);
                expect(result).XXX; // TODO
            });
        });
    });
});
