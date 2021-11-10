import { IfSecondDegreeEquation } from './if-second-degree-equation';

describe('IfSecondDegreeEquation', () => {
    let service;

    beforeEach(() => {
        service = new IfSecondDegreeEquation();
    });

    describe('myMethod', () => {
        it(`#00 should return -1 when a = -1`, () => {
            const result = service.myMethod(-1);
            expect(result).toEqual(-1);
        });
        it(`#01 should return 1 / 3 when a = 2`, () => {
            const result = service.myMethod(2);
            expect(result).toEqual(1 / 3);
        });
    });
});
