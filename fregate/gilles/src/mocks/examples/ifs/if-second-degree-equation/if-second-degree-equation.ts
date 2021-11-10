export class IfSecondDegreeEquation {

    myMethod(a: number): number {
        if (a * a + 2 * a+ 1 === 0) {
            return 1 / a;
        } else {
            return 1 / (a + 1);
        }
    }
}
