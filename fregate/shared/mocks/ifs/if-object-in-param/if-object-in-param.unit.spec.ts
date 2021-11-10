import { IfObjectInParam } from './if-object-in-param';

describe('IfObjectInParam', () => {
    let service;

    beforeEach(() => {
        service = new IfObjectInParam();
    });

    describe('myMethod', () => {
        it(`#00 should return XXX when obj.a = 2`, () => { // TODO
            const result = service.myMethod({a: 2});
            expect(result).toEqual(XXX); // TODO
        });
        it(`#01 should return XXX when obj.a = 0`, () => { // TODO
            const result = service.myMethod({a: 0});
            expect(result).toEqual(XXX); // TODO
        });
    });
});

