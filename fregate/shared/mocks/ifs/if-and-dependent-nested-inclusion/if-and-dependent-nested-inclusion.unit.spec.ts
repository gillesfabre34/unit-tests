import { IfAndDependentNestedInclusion } from './if-and-dependent-nested-inclusion';

describe('IfAndDependentNestedInclusion', () => {
    let service;

    beforeEach(() => {
        service = new IfAndDependentNestedInclusion();
    });

    describe('myMethod', () => {
        it(`#000 should return XXX when a = 1`, () => { // TODO
            const result = service.myMethod(2);
            expect(result).toEqual(XXX); // TODO
        });
        it(`#001 should return XXX when a = 3`, () => { // TODO
            const result = service.myMethod(6);
            expect(result).toEqual(XXX); // TODO
        });
        it(`#01 should return XXX when a = 6`, () => { // TODO
            const result = service.myMethod(6);
            expect(result).toEqual(XXX); // TODO
        });
    });
});
