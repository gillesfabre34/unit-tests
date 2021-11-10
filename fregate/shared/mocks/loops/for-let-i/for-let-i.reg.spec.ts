import { ForLetI } from './for-let-i';

describe('ForLetI', () => {
    let service;

    beforeEach(() => {
        service = new ForLetI();
    });

    describe('myMethod', () => {
        it(`#0 should return 10 when a = 0`, () => {
            const result = service.myMethod(0);
            expect(result).toEqual(10);
        });
    });
});
