import { IfNotFalsyWithoutTypes } from './if-not-falsy-without.types';

describe('IfNotFalsyWithoutTypes', () => {
    let service;

    beforeEach(() => {
        service = new IfNotFalsyWithoutTypes();
    });

    describe('myMethod', () => {
        it(`#00 should return XXX when text = 'a'`, () => { // TODO
            const result = service.myMethod('a');
            expect(result).toEqual(); // TODO
        });
        it(`#01 should return XXX when text = undefined`, () => { // TODO
            const result = service.myMethod(undefined);
            expect(result).toEqual(); // TODO
        });
    });
});
