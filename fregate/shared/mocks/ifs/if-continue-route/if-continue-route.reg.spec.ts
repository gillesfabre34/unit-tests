import { IfContinueRoute } from './if-continue-route';

describe('IfContinueRoute', () => {
    let service;

    beforeEach(() => {

        service = new IfContinueRoute();
    });

    describe('myMethod', () => {
        it(`#00 should return 'ab' when text = 'a'`, () => {
            const result = service.myMethod('a');
            expect(result).toEqual('ab');
        });
        it(`#01 should return 1 when text = undefined`, () => {``
            const result = service.myMethod(undefined);
            expect(result).toEqual(undefined);
        });
    });
});
