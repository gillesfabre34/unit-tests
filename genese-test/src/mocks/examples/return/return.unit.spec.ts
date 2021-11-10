import { MyService } from './return.service';

describe('MyService', () => {
    let service;

    beforeEach(() => {
        service = new MyService();
    });

    describe('myMethod', () => {
        it('should return 1', () => {
            const result = service.myMethod();
            expect(result).toEqual(1);
        });
    });
});
