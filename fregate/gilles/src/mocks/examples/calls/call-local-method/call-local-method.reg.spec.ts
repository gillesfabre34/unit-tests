import { CallLocalMethod } from './call-local-method';

describe('CallLocalMethod', () => {
    let service;

    beforeEach(() => {
        service = new CallLocalMethod();
    });

    describe('myMethod', () => {
        it(`#00 should return 1`, () => {
            spyOn(service, 'methodToCall').and.returnValue(1);
            const result = service.myMethod();
            expect(service.methodToCall).toHaveBeenCalled();
            expect(result).toEqual(1);
        });
    });

    describe('methodToCall', () => {
        it(`#00 should return 1`, () => {
            const result = service.myMethod();
            expect(result).toEqual(1);
        });
    });
});
