import { IfObjectInParam } from './if-object-in-param';

describe('IfObjectInParam', () => {
    let service;

    beforeEach(() => {
        service = new IfObjectInParam();
    });

    describe('myMethod', () => {
        it(`#00 should return 1 when obj.a = 2`, () => {
            const result = service.myMethod({a: 2});
            expect(result).toEqual(1);
        });
        it(`#01 should return 0 when obj.a = 0`, () => {
            const result = service.myMethod({a: 0});
            expect(result).toEqual(0);
        });
    });
});

