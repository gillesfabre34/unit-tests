import { IfAndIndependent } from './if-and-independent';

describe('IfAndIndependent', () => {
    let service;

    beforeEach(() => {
        service = new IfAndIndependent();
    });

    describe('myMethod', () => {
        it(`#00 should return XXX when a = 2 and b = 4`, () => { // TODO
            const result = service.myMethod(2, 4);
            expect(result).toEqual(XXX); // TODO
        });
        it(`#01 should return XXX when a = 0`, () => { // and b = 4 ? // TODO
            const result = service.myMethod(0, 4);
            expect(result).toEqual(XXX); // TODO
        });
    });
});
