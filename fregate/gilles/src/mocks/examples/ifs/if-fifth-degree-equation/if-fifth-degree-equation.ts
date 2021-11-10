export class IfFifthDegreeEquation {

    myMethod(a: number): number {
        if (Math.pow(a, 5) + 3 * a * a + 2 * a+ 1 === 0) {
            return 1;
        } else {
            return 0;
        }
    }
}
