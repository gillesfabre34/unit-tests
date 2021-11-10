import { CallLocalStaticMethod } from './call-local-static-method';

describe('CallLocalStaticMethod', () => {
    let service;

    beforeEach(() => {
        service = new CallLocalStaticMethod();
    });

    describe('myMethod', () => {

        describe('pseudo-random tests', () => {
            it(`should return true when a = 2`, () => {
                spyOn(CallLocalStaticMethod, 'isGreaterThanOne');
                const result = service.myMethod(2);
                expect(CallLocalStaticMethod.isGreaterThanOne).toHaveBeenCalledWith(2);
                expect(result).toBeTrue();
            });
        });
    });

    describe('isGreaterThanOne', () => {
        it(`should return true when a = 2`, () => {
            const result = service.isGreaterThanOne(2);
            expect(result).toBeTrue();
        });
        it(`should return false when a = 0`, () => {
            const result = service.isGreaterThanOne(0);
            expect(result).toBeFalse();
        });
    });
});
