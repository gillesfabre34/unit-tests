import { ForReturnArray } from './for-return-array';

describe('ForReturnArray', () => {
    let service;

    beforeEach(() => {
        service = new ForReturnArray();
    });

    describe('myMethod', () => {
        it(`#0 should return [2, 3] when a = [1, 2]`, () => {
            const result = service.myMethod([1, 2]);
            expect(result).toEqual([2, 3]);
        });
    });
});
