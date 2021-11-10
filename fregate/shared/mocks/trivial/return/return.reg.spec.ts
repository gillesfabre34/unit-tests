import { ReturnService } from './return.service';

describe('ReturnService', () => {
    let service;

    beforeEach(() => {
        service = new ReturnService();
    });

    describe('myMethod', () => {
        it('should return 1', () => {
            const result = service.myMethod();
            expect(result).toEqual(1);
        });
    });
});
