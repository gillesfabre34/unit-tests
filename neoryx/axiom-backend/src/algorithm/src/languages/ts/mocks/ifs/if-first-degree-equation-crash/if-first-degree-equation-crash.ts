export class IfFirstDegreeEquationCrash {

    myMethod(a: number): number {
        if (a + 1 === 0) {
            return 2 / a;
        } else {
            return 1 / a;
        }
    }
}
