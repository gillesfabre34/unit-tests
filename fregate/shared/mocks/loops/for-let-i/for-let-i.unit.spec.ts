import { ForLetI } from './for-let-i';

describe('IfNotFalsy', () => {
    let service;

    beforeEach(() => {
        service = new ForLetI();
    });

    describe('myMethod', () => {
        it(`#0 should return XXX when a = 0`, () => { // TODO
            const result = service.myMethod(0);
            expect(result).toEqual(); // TODO
        });
    });
});
