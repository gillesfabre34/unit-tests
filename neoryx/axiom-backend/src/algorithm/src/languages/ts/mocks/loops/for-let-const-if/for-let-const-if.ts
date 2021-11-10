export class ForLetConstIf {

    myMethod(decimals: number[]): number {
        let result = 0;
        for (const decimal of decimals) {
            result += decimal;
            if (result > 3) {
                return 1;
            }
        }
        return result;
    }
}
