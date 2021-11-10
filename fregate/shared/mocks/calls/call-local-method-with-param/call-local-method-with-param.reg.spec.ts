import { CallLocalMethodWithParam } from './call-local-method-with-param';

describe('CallInsideIf', () => {
    let service;

    beforeEach(() => {
        service = new CallLocalMethodWithParam();
    });

    describe('myMethod', () => {
        it(`#0 should return 4 for a = 1`, () => {
            spyOn(service, 'methodToCall').and.returnValue(1);
            const result = service.myMethod(1);
            expect(service.methodToCall).toHaveBeenCalledWith(2);
            expect(result).toEqual(4);
        });
    });

    describe('methodToCall', () => {
        it(`#0 should return 2 for a = 1`, () => {
            const result = service.myMethod(1);
            expect(result).toEqual(2);
        });
    });
});
