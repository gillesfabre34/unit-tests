import { IfSecondDegreeEquation } from './if-second-degree-equation';

describe('IfSecondDegreeEquation', () => {
    let service;

    beforeEach(() => {
        service = new IfSecondDegreeEquation();
    });

    describe('myMethod', () => {
        it(`#00 should return XXX when a = -1`, () => { // TODO
            const result = service.myMethod(-1);
            expect(result).toEqual(); // TODO
        });
        it(`#01 should return XXX when a = 2`, () => { // TODO
            const result = service.myMethod(2);
            expect(result).toEqual(); // TODO
        });
    });
});
