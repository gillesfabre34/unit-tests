import { IfFirstDegreeEquation } from './if-first-degree-equation';

describe('IfFirstDegreeEquation', () => {
    let service;

    beforeEach(() => {
        service = new IfFirstDegreeEquation();
    });

    describe('myMethod', () => {
        it(`#00 should return -1 when a = -1`, () => {
            const result = service.myMethod(-1);
            expect(result).toEqual(-1);
        });
        it(`#01 should return 1 when a = 2`, () => {
            const result = service.myMethod(2);
            expect(result).toEqual(1);
        });
    });
});
