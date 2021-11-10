import { OneLineAndReturn } from './one-line-and-return';

describe('ReturnService', () => {
    let service;

    beforeEach(() => {
        service = new OneLineAndReturn();
    });

    describe('myMethod', () => {
        it(`should return 'John'`, () => {
            const result = service.myMethod();
            // expect(result).toEqual(); // TODO
        });
    });
});
