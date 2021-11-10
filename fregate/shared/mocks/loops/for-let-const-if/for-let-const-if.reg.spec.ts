import { ForLetConstIf } from './for-let-const-if';

describe('ForLetConstIf', () => {
    let service;

    beforeEach(() => {
        service = new ForLetConstIf();
    });

    describe('myMethod', () => {
        it(`#000 should return 3 when a = [1, 2]`, () => {
            const result = service.myMethod([1, 2]);
            expect(result).toEqual(3);
        });
        it(`#01 should return 0 when a = [1, 3]`, () => {
            const result = service.myMethod([1, 3]);
            expect(result).toEqual(0);
        });
    });
});
