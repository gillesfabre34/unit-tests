import { IfThisProperty } from './if-this-property';

describe('IfThisProperty', () => {
    let service;

    beforeEach(() => {
        service = new IfThisProperty();
    });

    describe('myMethod', () => {
        it(`#00 should return 0 when this.name = 'foo'`, () => {
            service.name = 'foo';
            const result = service.myMethod();
            expect(result).toEqual(0);
        });
        it(`#01 should return 1 when this.name = undefined`, () => {
            service.name = undefined;
            const result = service.myMethod();
            expect(result).toEqual(1);
        });
    });
});
