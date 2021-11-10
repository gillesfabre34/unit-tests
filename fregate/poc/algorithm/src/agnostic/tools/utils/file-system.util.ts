import { GLOBALS } from '../../init/constants/globals.const';
import * as fs from 'fs-extra';


/**
 * Returns the name of the file at a given path
 * @param pathFile      // The path of the file
 */
export function getFilename(pathFile = ''): string {
    const splittedPath = pathFile.split('/');
    return splittedPath[splittedPath.length - 1];
}


/**
 * Returns a path with a ./ at the beginning
 * @param filePath
 */
export function getFolderPath(filePath: string): string {
    return filePath?.slice(0, -getFilename(filePath).length - 1) ?? undefined;
}


export function neoryxPath(originalPath: string): string {
    return originalPath.replace(GLOBALS.appRoot, `${GLOBALS.appRoot}/neoryx`);
}


/**
 * Copy a file from a path to another one
 * @param filePath
 * @param content
 */
export async function writeFile(filePath: string, content: string): Promise<any> {
    await fs.ensureDir(getFolderPath(filePath));
    await fs.writeFile(filePath, content, {encoding: 'utf-8'});
}


export async function copySync(source: string, target: string): Promise<void> {
    await fs.ensureDir(getFolderPath(target));
    fs.copySync(source, target);
}


