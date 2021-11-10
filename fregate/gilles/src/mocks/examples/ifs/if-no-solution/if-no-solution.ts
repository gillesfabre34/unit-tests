export class IfNoSolution {

    myMethod(a: number): number {
        if (a * a < 0) {
            return 1;
        } else {
            return 0;
        }
    }
}
