export class ForReturnArray {

    myMethod(decimals: number[]): number[] {
        let result = [];
        for (const decimal of decimals) {
            result.push(decimal + 1);
        }
        return result;
    }
}
