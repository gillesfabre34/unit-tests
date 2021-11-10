import { IfFifthDegreeEquation } from './if-fifth-degree-equation';

describe('IfFifthDegreeEquation', () => {
    let service;

    beforeEach(() => {
        service = new IfFifthDegreeEquation();
    });

    describe('myMethod', () => {
        it(`#00 should return 0 when a = 1`, () => {
            const result = service.myMethod(1);
            expect(result).toEqual(0);
        });
        it(`#01 should return XXX when XXX`, () => {
            // Impossible to enter in the route #01. Please choose inputs to solve this problem
            const result = service.myMethod(); // TODO
            expect(result).toEqual(); // TODO
        });
    });
});
