import { IfNotFalsyStatic } from './if-not-falsy.static';

describe('IfNotFalsyStatic', () => {

    describe('myMethod', () => {
        it(`#00 should return 0 when text = 'a'`, () => {
            const result = IfNotFalsyStatic.myMethod('a');
            expect(result).toEqual(0);
        });
        it(`#01 should return 1 when text = undefined`, () => {
            const result = IfNotFalsyStatic.myMethod(undefined);
            expect(result).toEqual(1);
        });
    });
});
