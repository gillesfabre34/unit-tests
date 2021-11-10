import { IfOr } from './if-or';

describe('IfOr', () => {
    let service;

    beforeEach(() => {
        service = new IfOr();
    });

    describe('myMethod', () => {
        it(`#00 should return XXX when a = 6`, () => { // TODO
            const result = service.myMethod(6);
            expect(result).toEqual(XXX); // TODO
        });
        it(`#01 should return XXX when a = 2`, () => { // TODO
            const result = service.myMethod(2);
            expect(result).toEqual(XXX); // TODO
        });
    });
});
