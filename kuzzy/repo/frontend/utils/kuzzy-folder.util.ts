import { GLOBAL } from '../init/const/global.const';
import { ensureDirAndCopy, getFiles } from './file-system.util';
import * as chalk from 'chalk';




export function kuzzyPath(original: string): string
export function kuzzyPath(original: string[]): string[]
export function kuzzyPath(original: string | string[]): string | string[] {
    if (Array.isArray(original)) {
        return original.map(o => kuzzyPathFile(o));
    } else {
        return kuzzyPathFile(original);
    }
}


function kuzzyPathFile(original: string): string {
    return original?.replace(GLOBAL.projectPath, `${GLOBAL.kuzzyPath}/flag`);
}


// TODO: remove
// export function kuzzyClonePath(originalPath: string): string {
//     return originalPath.replace(GLOBAL.projectPath, `${GLOBAL.kuzzyPath}/clone`);
// }


export function originalPath(kuzzyFlagOrClonePath: string): string {
    return kuzzyFlagOrClonePath.replace(`${GLOBAL.kuzzyPath}`, GLOBAL.projectPath)
        .replace('/flag', '');
}


export async function copyToKuzzyFolder(original: string | string[]): Promise<void> {
    if (Array.isArray(original)) {
        for (const path of original) {
            await copyFileToKuzzyFolder(path);
        }
    } else {
        await copyFileToKuzzyFolder(original);
    }
}


export async function copyFileToKuzzyFolder(originalPath: string): Promise<void> {
    if (originalPath !== kuzzyPath(originalPath)) {
        await ensureDirAndCopy(originalPath, kuzzyPath(originalPath));
    }
}


export async function getFlaggedProjectFilePaths(): Promise<string[]> {
    return await getFiles(kuzzyPath(GLOBAL.projectPath));
}


export async function getProjectFilePaths(projectPath: string): Promise<string[]> {
    return await getFiles(projectPath);
}

