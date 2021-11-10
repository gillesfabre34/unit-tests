import { ForLetConst } from './for-let-const';

describe('ForLetConst', () => {
    let service;

    beforeEach(() => {
        service = new ForLetConst();
    });

    describe('myMethod', () => {
        it(`#0 should return XXX when a = [1, 2]`, () => { // TODO
            const result = service.myMethod([1, 2]);
            expect(result).toEqual(XXX); // TODO
        });
    });
});
