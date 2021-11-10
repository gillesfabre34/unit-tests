
/**
 * Sets in capitals the first letter of a bugType
 * @param text
 */
export function unCapitalize(text: string): string {
    return text ? `${text.charAt(0).toLowerCase()}${text.slice(1)}` : text;
}

export const tab = '    ';
export function tabs(numberOfTabs: number): string {
    let spaces = '';
    for (let i = 0; i < numberOfTabs; i++) {
        spaces = `${spaces}${tab}`;
    }
    return spaces;
}


export function plural(word: string, quantity: number): string {
    if (!word || word.length === 0) {
        return '';
        ;    }
    if (quantity < 2) {
        return word;
    }
    if (word.slice(-1) === 'y') {
        return `${word.slice(0, -1)}ies`;
    }
    return `${word}s`;
}
