import { ReturnWithParam } from './return-with-param';

describe('ReturnWithParam', () => {
    let service;

    beforeEach(() => {
        service = new ReturnWithParam();
    });

    describe('myMethod', () => {
        it('should return XXX for-let-i a = 1', () => { // TODO
            const result = service.myMethod(1);
            expect(result).toEqual(XXX); // TODO
        });
    });
});
