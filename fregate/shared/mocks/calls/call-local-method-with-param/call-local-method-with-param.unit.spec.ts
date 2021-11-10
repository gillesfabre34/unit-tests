import { CallLocalMethodWithParam } from './call-local-method-with-param';

describe('CallInsideIf', () => {
    let service;

    beforeEach(() => {
        service = new CallLocalMethodWithParam();
    });

    describe('myMethod', () => {
        it(`#0 should return XXX for a = 1`, () => { // TODO
            spyOn(service, 'methodToCall');
            const result = service.myMethod(1);
            expect(service.methodToCall).toHaveBeenCalled();
            expect(result).toEqual(XXX); // TODO
        });
    });

    describe('methodToCall', () => {
        it(`#0 should return XXX for a = 1`, () => {
            const result = service.myMethod(1);
            expect(result).toEqual(XXX); // TODO
        });
    });
});
