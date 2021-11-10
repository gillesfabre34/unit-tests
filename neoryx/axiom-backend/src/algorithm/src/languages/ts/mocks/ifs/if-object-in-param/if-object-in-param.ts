export class IfObjectInParam {

    myMethod(obj: { a: number }): number {
        if (obj?.a > 1) {
            return 1;
        } else {
            return 0;
        }
    }
}
