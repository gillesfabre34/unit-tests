import { IfNoSolution } from './if-no-solution';

describe('IfNoSolution', () => {
    let service;

    beforeEach(() => {
        service = new IfNoSolution();
    });

    describe('myMethod', () => {
        it(`#00 should return XXX when a = 1`, () => { // TODO
            const result = service.myMethod(1);
            expect(result).toEqual(); // TODO
        });
        it(`#01 should return XXX when a = -1`, () => { // TODO
            // Impossible to enter in the route #01. Please choose inputs to solve this problem
            const result = service.myMethod(); // TODO
            expect(result).toEqual(); // TODO
        });
    });
});
