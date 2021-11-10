import { IfAndDependentNestedIntersection } from './if-and-dependent-nested-intersection';

describe('IfAndDependentNestedIntersection', () => {
    let service;

    beforeEach(() => {
        service = new IfAndDependentNestedIntersection();
    });

    describe('myMethod', () => {
        it(`#000 should return XXX when a = 4`, () => { // TODO
            const result = service.myMethod(4);
            expect(result).toEqual(XXX); //TODO
        });
        it(`#001 should return XXX when a = 6`, () => { // TODO
            const result = service.myMethod(6);
            expect(result).toEqual(XXX); //TODO
        });
        it(`#01 should return XXX when a = 4.5`, () => { // TODO
            const result = service.myMethod(4.5);
            expect(result).toEqual(XXX); //TODO
        });
    });
});
