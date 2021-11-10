import { Person } from './person';

export class IfTypedObjectInParam {

    myMethod(person: Person): string {
        if (person?.firstName === 'John') {
            return 'Doe';
        } else {
            return 'Someone else';
        }
    }
}
