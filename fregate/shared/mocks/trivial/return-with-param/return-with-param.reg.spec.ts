import { ReturnWithParam } from './return-with-param';

describe('ReturnWithParam', () => {
    let service;

    beforeEach(() => {
        service = new ReturnWithParam();
    });

    describe('myMethod', () => {
        it('should return 2 for-let-i a = 1', () => {
            const result = service.myMethod(1);
            expect(result).toEqual(2);
        });
    });
});
