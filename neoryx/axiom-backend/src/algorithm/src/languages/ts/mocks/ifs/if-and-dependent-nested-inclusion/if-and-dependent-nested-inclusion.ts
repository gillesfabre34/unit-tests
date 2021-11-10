export class IfAndDependentNestedInclusion {

    myMethod(a: number): number {
        if (a < 5) {
            if (a < 2) {
                return 0;
            } else {
                return 2;
            }
        } else {
            return 1;
        }
    }
}
