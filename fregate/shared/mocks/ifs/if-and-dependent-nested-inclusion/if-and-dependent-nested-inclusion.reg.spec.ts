import { IfAndDependentNestedInclusion } from './if-and-dependent-nested-inclusion';

describe('IfAndDependentNestedInclusion', () => {
    let service;

    beforeEach(() => {
        service = new IfAndDependentNestedInclusion();
    });

    describe('myMethod', () => {
        it(`#000 should return 0 when a = 1`, () => {
            const result = service.myMethod(0);
            expect(result).toEqual(0);
        });
        it(`#001 should return 2 when a = 3`, () => {
            const result = service.myMethod(6);
            expect(result).toEqual(2);
        });
        it(`#01 should return 0 when a = 6`, () => {
            const result = service.myMethod(6);
            expect(result).toEqual(0);
        });
    });
});
