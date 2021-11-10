

export function percentage(nominator: number, denominator: number): number {
    if (!denominator || denominator === 0) {
        return 0;
    }
    const percentage = 100 * nominator / denominator;
    return Number(percentage.toFixed(2));
}


export function sum(arr: number[]): number {
    return arr.reduce((a, b) => a + b, 0);
}
