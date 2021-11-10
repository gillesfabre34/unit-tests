import { IfTwoParams } from './if-two-params';

describe('IfTwoParams', () => {
    let service;

    beforeEach(() => {
        service = new IfTwoParams();
    });

    describe('myMethod', () => {
        it(`#000 should return 1 when a = 2 and text = 'abc'`, () => {
            const result = service.myMethod(2, 'abc');
            expect(result).toEqual(0);
        });
        it(`#001 should return 2 when a = 2 and text = 'a'`, () => {
            const result = service.myMethod(2, 'a');
            expect(result).toEqual(1);
        });
        it(`#010 should return 3 when a = 0 and text = 'abcd'`, () => {
            const result = service.myMethod(0, 'abc');
            expect(result).toEqual(3);
        });
        it(`#011 should return 2 when a = 0 and text = 'ab'`, () => {
            const result = service.myMethod(0, 'a');
            expect(result).toEqual(2);
        });
    });
});
