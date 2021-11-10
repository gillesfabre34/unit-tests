import { IfThisProperty } from './if-this-property';

describe('IfThisProperty', () => {
    let service;

    beforeEach(() => {
        service = new IfThisProperty();
    });

    describe('myMethod', () => {
        it(`#00 should return XXX when this.name = 'foo'`, () => { // TODO
            service.name = 'foo';
            const result = service.myMethod();
            expect(result).toEqual(); // TODO
        });
        it(`#01 should return XXX when this.name = undefined`, () => { // TODO
            service.name = undefined;
            const result = service.myMethod(undefined);
            expect(result).toEqual(); // TODO
        });
    });
});
