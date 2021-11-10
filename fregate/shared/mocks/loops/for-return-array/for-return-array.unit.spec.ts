import { ForReturnArray } from './for-return-array';

describe('ForReturnArray', () => {
    let service;

    beforeEach(() => {
        service = new ForReturnArray();
    });

    describe('myMethod', () => {
        it(`#0 should return XXX when a = [1, 2]`, () => { // TODO
            const result = service.myMethod([1, 2]);
            expect(result).toEqual(XXX); // TODO
        });
    });
});
