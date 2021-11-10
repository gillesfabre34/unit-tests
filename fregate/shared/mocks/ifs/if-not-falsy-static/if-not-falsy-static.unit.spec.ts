import { IfNotFalsyStatic } from './if-not-falsy.static';

describe('IfNotFalsyStatic', () => {

    describe('myMethod', () => {
        it(`#00 should return XXX when text = 'a'`, () => { // TODO
            const result = IfNotFalsyStatic.myMethod('a');
            expect(result).toEqual(); // TODO
        });
        it(`#01 should return XXX when text = undefined`, () => { // TODO
            const result = IfNotFalsyStatic.myMethod(undefined);
            expect(result).toEqual(); // TODO
        });
    });
});
