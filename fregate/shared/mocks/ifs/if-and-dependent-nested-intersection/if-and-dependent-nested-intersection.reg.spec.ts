import { IfAndDependentNestedIntersection } from './if-and-dependent-nested-intersection';

describe('IfAndDependentNestedIntersection', () => {
    let service;

    beforeEach(() => {
        service = new IfAndDependentNestedIntersection();
    });

    describe('myMethod', () => {
        it(`#000 should return 2 when a = 4`, () => {
            const result = service.myMethod(4);
            expect(result).toEqual(2);
        });
        it(`#001 should return 1 when a = 6`, () => {
            const result = service.myMethod(6);
            expect(result).toEqual(1);
        });
        it(`#01 should return 0 when a = 4.5`, () => {
            const result = service.myMethod(4.5);
            expect(result).toEqual(0);
        });
    });
});
