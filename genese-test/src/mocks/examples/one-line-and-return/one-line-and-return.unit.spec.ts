import { MyService } from './one-line-and-return.service';

describe('MyService', () => {
    let service;

    beforeEach(() => {
        service = new MyService();
    });

    describe('myMethod', () => {
        it(`should return 'John'`, () => {
            const result = service.myMethod();
            expect(result).toEqual(); // TODO
        });
    });
});
