import { CallExternalStaticMethod } from './call-external-static-method';
import { ExternalService } from './external-service';

describe('CallExternalMethodWithNewKeyword', () => {
    let service;

    beforeEach(() => {
        service = new CallExternalStaticMethod();
    });

    describe('myMethod', () => {

        describe('no illusion tests', () => {
            it(`should return XXX when a = 2`, () => { // TODO
                spyOn(ExternalService, 'isGreaterThanOne').and.returnValue(XXX); // TODO
                const result = service.myMethod(2);
                expect(ExternalService.isGreaterThanOne).toHaveBeenCalledWith(XXX); // TODO
                expect(result).XXX; // TODO
            });
        })


        describe('pseudo-random tests', () => {
            it(`should return XXX when a = 2`, () => { // TODO
                spyOn(ExternalService, 'isGreaterThanOne');
                const result = service.myMethod(2);
                expect(ExternalService.isGreaterThanOne).toHaveBeenCalledWith(XXX); // TODO
                expect(result).XXX; // TODO
            });
        });
    });
});
