export class ForLetI {

    myMethod(a: number): number {
        for (let i = 0; i < 10; i++) {
            a = a+ 1;
        }
        return a;
    }
}
