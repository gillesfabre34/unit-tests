import { exec } from "child_process";
import * as fs from "fs-extra";
import * as chalk from 'chalk';
import { GLOBALS } from '../../init/constants/globals.const';

export function gitChanges() : string[] {
    const gitChangesFile: string = createGitChangesFile();
    const matches = gitChangesFile.match(/^--- a\/(.*)/gm)?.map(m => `${GLOBALS.projectPath}/${m.slice(6)}`);
    return matches ?? [];
}


function createGitChangesFile(): string {
    const command = `git diff --output=gitChanges.txt`;
    exec(command);
    return fs.readFileSync('gitChanges.txt', 'utf-8');
}
