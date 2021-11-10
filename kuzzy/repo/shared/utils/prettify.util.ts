import * as chalk from 'chalk';
import { tabs } from './strings.util';
import { isCyclic } from './objects.util';

export function prettify(source: any, numberOfTabs = 0, code: string = undefined): string {
    if (source === undefined) {
        return 'undefined';
    }
    if (source === null) {
        return 'null';
    }
    if (isCyclic(source)) {
        console.log(chalk.red('CYCLIC OBJECT : '), source);
        return 'CYCLIC OBJECT';
    }
    switch (typeof source) {
        case 'string':
            return addQuotesOrAntiQuotes(source);
        case 'object':
            if (Array.isArray(source)) {
                const prettifiedArray = [];
                for (const element of source) {
                    prettifiedArray.push(prettify(element));
                }
                return '[' + prettifiedArray.join(', ') + ']';
            }
            if (!code) {
                code = `{\n`;
            }
            for (const [key, value] of Object.entries(source)) {
                switch (typeof value) {
                    case 'number':
                        code = `${code}${tabs(numberOfTabs + 1)}${key} : ${value},\n`;
                        break;
                    case 'string':
                        code = `${code}${tabs(numberOfTabs + 1)}${key} : \`${value}\`,\n`;
                        break;
                    case 'object':
                        code = prettify(source[key], numberOfTabs + 1, `${code}${tabs(numberOfTabs + 1)}${key} : {\n`);
                        code = `${code},\n`;
                        break;
                }
            }
            code = `${code}${tabs(numberOfTabs)}}`;
            return code;
        case 'boolean':
            return source ? 'true' : 'false';
        case 'number':
        default:
            return source?.toString() || '';
    }
}


export function prettifyEachValue(texts: string[]): string[] {
    if (!Array.isArray(texts)) {
        return [];
    }
    const strings: string[] = [];
    for (const text of texts) {
        strings.push(prettify(text));
    }
    return strings;
}


function addQuotesOrAntiQuotes(text: string): string {
    if (!text) {
        return `''`;
    }
    if (!text.includes('`')) {
        return `\`${text}\``;
    }
    if (!text.includes('\'')) {
        return `'${text}'`;
    }
    if (!text.includes('"')) {
        return `"${text}"`;
    }
    return undefined;
}
