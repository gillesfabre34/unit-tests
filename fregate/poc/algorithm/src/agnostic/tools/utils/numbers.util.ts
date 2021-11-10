

export function percentage(coveredStatements: number, totalStatements: number): number {
    if (!totalStatements || totalStatements === 0) {
        return 0;
    }
    const percentage = 100 * coveredStatements / totalStatements;
    return Number(percentage.toFixed(2));
}


export function sum(arr: number[]): number {
    return arr.reduce((a, b) => a + b, 0);
}
