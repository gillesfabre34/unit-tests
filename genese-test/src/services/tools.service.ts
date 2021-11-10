import * as chalk from 'chalk';
import * as fs from 'fs-extra';
import { Block, Project, SourceFile, SyntaxKind } from 'ts-morph';
import { Path } from '../models/path.model';
import { AstMock } from '../interfaces/ast-mock.interface';
import { TEMP_FILE_NAME } from '../mocks/paths.mock';

/**
 * Sets in capitals the first letter of a text
 * @param text
 */
export function capitalize(text: string): string {
    return `${text.charAt(0).toUpperCase()}${text.slice(1)}`;
}


/**
 * Thanks to Prashant Yadav :
 * https://medium.com/javascript-in-plain-english/how-to-merge-objects-in-javascript-98f2209710e3
 * @param args
 */
export function deepMerge(...args) {
    let target = {};
    // Merge the object into the target object
    let merger = (obj) => {
        for (let prop in obj) {
            if (obj.hasOwnProperty(prop)) {
                if (Object.prototype.toString.call(obj[prop]) === '[object Object]'){
                    // If we're doing a deep merge
                    // and the property is an object
                    target[prop] = deepMerge(target[prop], obj[prop]);
                } else {
                    // Otherwise, do a regular merge
                    target[prop] = obj[prop];
                }
            }
        }
    };
    //Loop through each object and conduct a merge
    for (let i = 0; i < arguments.length; i++) {
        merger(arguments[i]);
    }
    return target;
}


export function throwError(message: string): void {
    console.log(chalk.redBright(`No class in the file ${message}`))
    throw Error(`No class in the file ${message}`)
}


/**
 * Returns the name of the file at a given path
 * @param pathFile      // The path of the file
 */
export function getFilename(pathFile = ''): string {
    const splittedPath = pathFile.split('/');
    return splittedPath[splittedPath.length - 1];
}


/**
 * Copy a file from a path to another one
 * @param path
 * @param content
 */
export function createFileSync(path: string, content: string): void {
    fs.writeFileSync(path, content, {encoding: 'utf-8'});
}

/**
 * Copy a file from a path to another one
 * @param path
 * @param content
 */
export async function createFileAsync(path: string, content: string): Promise<any> {
    await fs.writeFile(path, content, {encoding: 'utf-8'});
}


export function logCharsAndCompare(textLeft: string, textRight: string) {
    console.log(chalk.blueBright('COMPARE TEXTS'));
    let line = 1;
    console.log(chalk.blueBright('Line 1'));
    for (let i = 0; i < Math.max(textLeft.length, textRight.length); i++) {
        if (textLeft[i] === '\n') {
            line++;
            console.log(chalk.blueBright(`Line ${line}`));
        }
        console.log(chalk.yellowBright(textLeft[i], '-', textRight[i]));
        if (textLeft[i] !== textRight[i]) {
            console.log(chalk.redBright(`DIFFERENCE BETWEEN ${textLeft.charCodeAt(i)} AND ${textRight.charCodeAt(i)}`));
            throw Error('Stop here')
        }
    }
}


export function lastElement<T>(arr: T[]): T {
    return arr.slice(-1).pop();
}


export function astMocks(code: string): AstMock {
    const sourceFile = sourceFileMock(code);
    return {
        sourceFile: sourceFile,
        firstBlock: sourceFile.getFirstDescendantByKind(SyntaxKind.Block),
        firstPath: {
            id: '0',
            route: []
        }
    }
}

export function sourceFileMock(code: string): SourceFile {
    createFileSync(TEMP_FILE_NAME, code);
    const project = new Project();
    project.addSourceFileAtPath(TEMP_FILE_NAME);
    return project.getSourceFileOrThrow(TEMP_FILE_NAME);
}


export function expectJasmineArray(result: any[], mockedResult: any[], check = true, log = true): void {
    for (let i = 0; i < result.length; i++) {
        if (log) {
            console.log(chalk.yellowBright('RESULT'), result[i])
            console.log(chalk.magentaBright('MOCKED RESULT'), mockedResult[i]);
        }
        if (check) {
            expect(Object.assign({}, result[i])).toEqual(Object.assign({}, mockedResult[i]));
        } else {
            console.log(chalk.redBright('NO LOG FOR JASMINE EXPECT'))
        }
    }
}

