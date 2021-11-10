import { Describe } from '../models/describe.model';
import { Path } from '../models/path.model';
import { Block, IfStatement, MethodDeclaration, Statement, SyntaxKind } from 'ts-morph';
import { lastElement } from './tools.service';
import * as chalk from 'chalk';
import cloneDeep from 'clone-deep-circular-references';

export class PathService {


    static getPaths(describe: Describe): Path[] {
        const methodDeclaration: MethodDeclaration = describe?.method;
        const firstBlock: Block = methodDeclaration?.getFirstDescendantByKind(SyntaxKind.Block);
        if (!firstBlock) {
            return [];
        }
        const firstPath = new Path();
        firstPath.id = '0';
        return this.createPathsByBlock(firstPath, firstBlock.getStatements());
    }


    private static createPathsByBlock(path: Path, blockStatements: Statement[]): Path[] {
        if (!blockStatements || blockStatements.length === 0) {
            return [path];
        }
        const firstBlockStatement = blockStatements[0];
        const clonePath: Path = cloneDeep(path);
        clonePath.route.push(firstBlockStatement);
        const nextBlockStatements: Statement[] = blockStatements.slice(1);
        if (firstBlockStatement.getKind() !== SyntaxKind.IfStatement) {
            return blockStatements.length > 1 ? this.createPathsByBlock(clonePath, nextBlockStatements) : [clonePath];
        } else {
            const [branchThen, branchElse] = this.createBranch(firstBlockStatement as IfStatement, clonePath);
            return this.extendBranches([branchThen, branchElse], nextBlockStatements);
        }
    }


    private static createBranch(ifStatement: IfStatement, path: Path): Path[] {
        const blockThen: Block = ifStatement.getThenStatement() as Block
        const branchThen: Path[] = this.createPathsByBlock({
                id: `${path.id}0`,
                route: path.route
            }, blockThen.getStatements()
        );
        const blockElse: Block = ifStatement.getElseStatement() as Block;
        const branchElse: Path[] = blockElse
            ? this.createPathsByBlock({id: `${path.id}1`,route: path.route}, blockElse.getStatements())
            : [{
                id: `${path.id}1`,
                route: path.route
            }];
        return [branchThen[0], branchElse[0]];
    }


    private static extendBranches(branches: Path[], blockStatements: Statement[]): Path[] {
        if (!branches || branches.length === 0 || !blockStatements || blockStatements.length === 0) {
            return branches;
        }
        let extendedBranches: Path[] = [];
        for (const branch of branches) {
            if (lastElement(branch.route).getKind() !== SyntaxKind.ReturnStatement) {
                extendedBranches = extendedBranches.concat(this.createPathsByBlock(branch, blockStatements))
            } else {
                extendedBranches = extendedBranches.concat(branch);
            }
        }
        return extendedBranches;
    }


    static log(paths: Path | Path[], message = ''): void {
        if (Array.isArray(paths)) {
            for (const path of paths) {
                this.logStatements(path, message)
            }
        } else {
            this.logStatements(paths, message);
        }
    }


    static logStatements(path: Path, message = ''): void {
        let log = '';
        for (const statement of path.route) {
            if (statement) {
                log += ` => ${statement.getKindName()} ${statement.getText().slice(0, 80)}\n`;
            } else {
                log += ` => undefined\n`;
            }
        }
        console.log(chalk.greenBright(message));
        console.log(log);
    }

}
