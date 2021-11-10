import { CallLocalMethod } from './call-local-method';

describe('CallLocalMethod', () => {
    let service;

    beforeEach(() => {
        service = new CallLocalMethod();
    });

    describe('myMethod', () => {
        it(`#0 should return XXX`, () => { // TODO
            spyOn(service, 'methodToCall');
            const result = service.myMethod();
            expect(service.methodToCall).toHaveBeenCalled();
            expect(result).toEqual(); // TODO
        });
    });

    describe('methodToCall', () => {
        it(`#0 should return XXX`, () => { // TODO
            const result = service.methodToCall();
            expect(result).toEqual(); // TODO
        });
    });
});
