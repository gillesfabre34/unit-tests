export class IfObjectEquality {

    myMethod(obj: object): number {
        if (obj === { text: 'a' }) {
            return 0;
        } else {
            return 1;
        }
    }
}
