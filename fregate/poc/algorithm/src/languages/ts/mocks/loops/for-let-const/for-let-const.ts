export class ForLetConst {

    myMethod(decimals: number[]): number {
        let result = 0;
        for (const decimal of decimals) {
            result += 1;
        }
        return result;
    }
}
