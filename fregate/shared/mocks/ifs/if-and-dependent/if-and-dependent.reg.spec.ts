import { IfAndDependent } from './if-and-dependent';

describe('IfAndDependent', () => {
    let service;

    beforeEach(() => {
        service = new IfAndDependent();
    });

    describe('myMethod', () => {
        it(`#00 should return 0 when a = 2`, () => {
            const result = service.myMethod(2);
            expect(result).toEqual(0);
        });
        it(`#01 should return 1 when a = 6`, () => {
            const result = service.myMethod(6);
            expect(result).toEqual(1);
        });
    });
});
