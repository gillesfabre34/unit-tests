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

            it(`should return XXX when a = 2`, () => { // TODO
                spyOn(service.externalService, 'isGreaterThanOne').and.returnValue(XXX); // TODO
                const result = service.myMethod(2);
                expect(service.externalService.isGreaterThanOne).toHaveBeenCalledWith(XXX); // TODO
                expect(result).XXX; // TODO
            });
            it(`should return XXX when a = 0`, () => { // TODO
                spyOn(service.externalService, 'isGreaterThanOne').and.returnValue(XXX); // TODO
                const result = service.myMethod(0);
                expect(service.externalService.isGreaterThanOne).toHaveBeenCalledWith(XXX); // TODO
                expect(result).XXX; // TODO
            });
        })


        describe('pseudo-random tests', () => {
            it(`should return XXX when a = 2`, () => { // TODO
                spyOn(service.externalService, 'isGreaterThanOne');
                const result = service.myMethod(2);
                expect(service.externalService.isGreaterThanOne).toHaveBeenCalledWith(XXX); // TODO
                expect(result).XXX; // TODO
            });
            it(`should return XXX when a = 0`, () => { // TODO
                spyOn(service.externalService, 'isGreaterThanOne');
                const result = service.myMethod(0);
                expect(service.externalService.isGreaterThanOne).toHaveBeenCalledWith(XXX); // TODO
                expect(result).XXX; // TODO
            });
        });
    });
});
