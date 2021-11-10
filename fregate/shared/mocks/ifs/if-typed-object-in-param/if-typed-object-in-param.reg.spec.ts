import { IfTypedObjectInParam } from './if-typed-object-in-param';

describe('IfTypedObjectInParam', () => {
    let service;

    beforeEach(() => {
        service = new IfTypedObjectInParam();
    });

    describe('myMethod', () => {
        it(`#00 should return 'Doe' when person.firstName = 'John'`, () => {
            const person = {
                firstName: 'John',
                lastName: undefined
            }
            const result = service.myMethod(person);
            expect(result).toEqual('Doe');
        });
        it(`#01 should return 'Someone else' when person.firstName = 'Lara'`, () => {
            const person = {
                firstName: 'Lara',
                lastName: undefined
            }
            const result = service.myMethod(person);
            expect(result).toEqual('Someone else');
        });
    });
});

