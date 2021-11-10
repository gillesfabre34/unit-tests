import { IfTypedObjectInParam } from './if-typed-object-in-param';

describe('IfTypedObjectInParam', () => {
    let service;

    beforeEach(() => {
        service = new IfTypedObjectInParam();
    });

    describe('myMethod', () => {
        it(`#00 should return XXX when person.firstName = 'John'`, () => { // TODO
            const person = {
                firstName: 'John',
                lastName: undefined
            }
            const result = service.myMethod(person);
            expect(result).toEqual(XXX); // TODO
        });
        it(`#01 should return XXX when person.firstName = 'Lara'`, () => { // TODO
            const person = {
                firstName: 'Lara',
                lastName: undefined
            }
            const result = service.myMethod(person);
            expect(result).toEqual(XXX); // TODO
        });
    });
});

