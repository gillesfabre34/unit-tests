import { ExternalService } from './external-service';

describe('ExternalService', () => {
    let service;

    beforeEach(() => {
        service = new ExternalService ();
    });

    describe('isGreaterThanOne', () => {
        it(`should return true when a = 2`, () => {
            const result = service.isGreaterThanOne(2);
            expect(result).toBeTrue();
        });
        it(`should return false when a = 0`, () => {
            const result = service.isGreaterThanOne(0);
            expect(result).toBeFalse();
        });
    });
});
