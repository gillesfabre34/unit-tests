import { IfAndIndependent } from './if-and-independent';

describe('IfAndIndependent', () => {
    let service;

    beforeEach(() => {
        service = new IfAndIndependent();
    });

    describe('myMethod', () => {
        it(`#00 should return 0 when a = 2 and b = 4`, () => {
            const result = service.myMethod(2, 4);
            expect(result).toEqual(0);
        });
        it(`#01 should return 1 when a = 0`, () => { // and b = 4 ?
            const result = service.myMethod(0, 4);
            expect(result).toEqual(1);
        });
    });
});
