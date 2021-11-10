import * as fs from 'fs-extra';
import { Dirent } from 'fs';
import { isCapital } from '../../shared/utils/strings.util';
import * as chalk from 'chalk';

const { resolve } = require('path');

/**
 * Returns the name of the file at a given path
 * @param pathFile      // The path of the file
 */
export function getFilename(pathFile = ''): string {
    const splitPath = pathFile.split('/');
    return splitPath[splitPath.length - 1];
}


export function removeExtension(pathFile = ''): string {
    return pathFile.slice(0, -getExtension(pathFile).length - 1);
}


export function getExtension(pathFile = ''): string {
    const split: string[] = pathFile.split('.');
    return split[split.length - 1];
}


export function getModuleSpecifier(filePath: string): string {
    return `./${removeExtension(getFilename(filePath))}`;
}


export function classNameToFileName(className: string): string {
    if (!className) {
        return '';
    }
    let fileName = className[0].toLowerCase();
    for (let i = 1; i < className.length; i++) {
        fileName = isCapital(className[i]) ? `${fileName}-${className[i].toLowerCase()}` : `${fileName}${className[i]}`;
    }
    fileName = fileName.replace('_', '-');
    return fileName;
}


export function getFolderPath(filePath: string): string {
    return filePath?.slice(0, -getFilename(filePath).length - 1) ?? undefined;
}


export async function writeFile(filePath: string, content: string): Promise<any> {
    await fs.ensureDir(getFolderPath(filePath));
    await fs.writeFile(filePath, content, {encoding: 'utf-8'});
}


export async function ensureDir(folderPath: string): Promise<void> {
    await fs.ensureDir(folderPath);
}


export async function ensureDirAndCopy(source: string, target: string): Promise<void> {
    await fs.ensureDir(getFolderPath(target));
    fs.copySync(source, target);
}


export async function removeFiles(filePaths: string[]): Promise<void> {
    for (const filePath of filePaths) {
        await removeFile(filePath);
    }
}


export async function removeFile(filePath: string): Promise<void> {
    fs.removeSync(filePath);
}


export async function getFiles(dir: string): Promise<string[]> {
    const dirents: Dirent[] = fs.readdirSync(dir, { withFileTypes: true });
    const files: string[] = await Promise.all(dirents.map((dirent) => {
        const res = resolve(dir, dirent.name); // TODO : Type of res = string ?
        return dirent.isDirectory() && !isNodeModulesFolder(res) ? getFiles(res) : res;
    }));
    return Array.prototype.concat(...files);
}


export function isNodeModulesFolder(folder: string): boolean {
    return lastSubFolder(folder) === 'node_modules';
}


function lastSubFolder(folder: string): string {
    const split: string[] = folder?.split('/');
    return split?.[split.length - 1];
}

