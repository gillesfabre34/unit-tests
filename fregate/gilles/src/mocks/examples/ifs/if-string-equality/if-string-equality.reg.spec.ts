import { IfStringEquality } from './if-string-equality';

describe('IfStringEquality', () => {
    let service;

    beforeEach(() => {
        service = new IfStringEquality();
    });

    describe('myMethod', () => {
        it(`#00 should return 0 when text = 'a'`, () => {
            const result = service.myMethod('a');
            expect(result).toEqual(0);
        });
        it(`#01 should return 1 when text = undefined`, () => {
            const result = service.myMethod(undefined);
            expect(result).toEqual(1);
        });
    });
});
