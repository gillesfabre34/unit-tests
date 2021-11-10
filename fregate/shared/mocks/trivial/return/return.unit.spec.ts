import { ReturnService } from './return.service';

describe('ReturnService', () => {
    let service;

    beforeEach(() => {
        service = new ReturnService();
    });

    describe('myMethod', () => {
        it('should return XXX', () => { // TODO
            const result = service.myMethod();
            expect(result).toEqual(); // TODO
        });
    });
});
