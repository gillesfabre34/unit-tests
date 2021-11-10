import { MyService } from './object-equality.service';

describe('MyService', () => {
    let service;

    beforeEach(() => {
        service = new MyService();
    });

    describe('myMethod', () => {
        it(`#00 should return XXX when obj = { text: 'a' }`, () => { // TODO
            const result = service.myMethod({ text: 'a' });
            expect(result).toEqual(); // TODO
        });
        it(`#01 should return XXX when obj = undefined`, () => { // TODO
            const result = service.myMethod(undefined);
            expect(result).toEqual(); // TODO
        });
    });
});
