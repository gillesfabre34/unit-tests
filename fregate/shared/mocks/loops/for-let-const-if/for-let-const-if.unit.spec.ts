import { ForLetConstIf } from './for-let-const-if';

describe('ForLetConstIf', () => {
    let service;

    beforeEach(() => {
        service = new ForLetConstIf();
    });

    describe('myMethod', () => {
        it(`#000 should return XXX when a = [1, 2]`, () => { // TODO
            const result = service.myMethod([1, 2]);
            expect(result).toEqual(XXX); // TODO
        });
        it(`#01 should return XXX when a = [1, 3]`, () => { // TODO
            const result = service.myMethod([1, 3]);
            expect(result).toEqual(XXX); // TODO
        });
    });
});
