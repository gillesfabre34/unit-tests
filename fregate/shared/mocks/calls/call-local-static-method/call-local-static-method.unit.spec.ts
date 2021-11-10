import { CallLocalStaticMethod } from './call-local-static-method';

describe('CallLocalStaticMethod', () => {
    let service;

    beforeEach(() => {
        service = new CallLocalStaticMethod();
    });

    describe('myMethod', () => {

        describe('pseudo-random tests', () => {
            it(`should return XXX when a = 2`, () => { // TODO
                spyOn(CallLocalStaticMethod, 'isGreaterThanOne');
                const result = service.myMethod(2);
                expect(CallLocalStaticMethod.isGreaterThanOne).toHaveBeenCalledWith(XXX); // TODO
                expect(result).XXX; // TODO
            });
        });
    });

    describe('isGreaterThanOne', () => {
        it(`should return XXX when a = 2`, () => { // TODO
            const result = service.isGreaterThanOne(2);
            expect(result).XXX; // TODO
        });
        it(`should return XXX when a = 0`, () => { // TODO
            const result = service.isGreaterThanOne(0);
            expect(result).XXX; // TODO
        });
    });
});
