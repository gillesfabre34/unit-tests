import { CallExternalStaticMethod } from './call-external-static-method';
import { ExternalService } from './external-service';

describe('CallExternalStaticMethod', () => {
    let service;

    beforeEach(() => {
        service = new CallExternalStaticMethod();
    });

    describe('myMethod', () => {

        describe('no illusion tests', () => {
            it(`should return true when a = 2`, () => {
                spyOn(ExternalService, 'isGreaterThanOne').and.returnValue(true);
                const result = service.myMethod(2);
                expect(ExternalService.isGreaterThanOne).toHaveBeenCalledWith(2);
                expect(result).toBeTrue();
            });
        });


        describe('pseudo-random tests', () => {
            it(`should return true when a = 2`, () => {
                spyOn(ExternalService, 'isGreaterThanOne');
                const result = service.myMethod(2);
                expect(ExternalService.isGreaterThanOne).toHaveBeenCalledWith(2);
                expect(result).toBeTrue();
            });
        });
    });
});
