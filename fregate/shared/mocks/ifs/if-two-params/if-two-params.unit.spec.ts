import { IfTwoParams } from './if-two-params';

describe('IfTwoParams', () => {
    let service;

    beforeEach(() => {
        service = new IfTwoParams();
    });

    describe('myMethod', () => {
        it(`#000 should return XXX when a = 2 and text = 'abc'`, () => { // TODO
            const result = service.myMethod(2, 'abc');
            expect(result).toEqual(XXX); // TODO
        });
        it(`#001 should return XXX when a = 2 and text = 'a'`, () => { // TODO
            const result = service.myMethod(2, 'a');
            expect(result).toEqual(XXX); // TODO
        });
        it(`#010 should return XXX when a = 0 and text = 'abcd'`, () => { // TODO
            const result = service.myMethod(0, 'abc');
            expect(result).toEqual(XXX); // TODO
        });
        it(`#011 should return XXX when a = 0 and text = 'ab'`, () => { // TODO
            const result = service.myMethod(0, 'a');
            expect(result).toEqual(XXX); // TODO
        });
    });
});
