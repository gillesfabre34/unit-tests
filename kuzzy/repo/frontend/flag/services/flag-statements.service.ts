import {
    Block,
    CallExpression,
    IfStatement,
    Node,
    Statement,
    StatementedNode,
    SwitchStatement,
    SyntaxKind
} from 'ts-morph';
import { KzFilePathService } from './kz-filepath.service';
import { FlagMethodsService } from './flag-methods.service';
import { CallerInformations } from '../models/caller-informations.model';
import { isIdentifierDeclaredOutOfProject, nodeIsAnImport } from '../../../shared/utils/ast-imports.util';

export abstract class FlagStatementsService {


    static async flagStatements(callerInformations: CallerInformations): Promise<void> {
        this.setFlagToBlock(callerInformations.name, callerInformations.className, callerInformations.declaration, 0);
        await this.addArgumentsToReferenceNodes(callerInformations);
    }


    /**
     * Flags the statements inside a given Block and all its descendant Blocks
     * @param methodName / the name of the Method
     * @param className / the name of the Class
     * @param statementedNode / the Block parent of the statement to flag
     * @param parentIndex / the index of this Block in its parent's Block
     * @private
     */
    private static setFlagToBlock(methodName: string, className: string, statementedNode: StatementedNode, parentIndex: number): number {
        let scopeIndex = 0;
        for (const statement of statementedNode.getStatements()) {
            this.setFlagToStatement(methodName, className, statementedNode, parentIndex, scopeIndex);
            parentIndex++;
            parentIndex = this.setFlagsToNestedStatementsAndAddBlockIndex(methodName, className, statement, parentIndex);
            scopeIndex++;
        }
        return parentIndex;
    }


    /**
     * Insert a statement
     * @param methodName / the name of the Method
     * @param className / the name of the Class
     * @param statementedNode / the Block parent of the statement to flag
     * @param parentIndex / the index of this Block in its parent's Block
     * @param scopeIndex / the index of the statement in the Block's scope
     * @private
     */
    private static setFlagToStatement(methodName: string, className: string, statementedNode: StatementedNode, parentIndex: number, scopeIndex: number): void {
        statementedNode.insertStatements(scopeIndex * 2, `parse(${parentIndex}, '${methodName}', '${className}', kz_filePath);`);
    }


    /**
     * When the statement have nested statements, this method flags the nested statements and returns the new parentIndex
     * @param methodName / the name of the Method
     * @param className / the name of the Class
     * @param statement / the statement which potentially has nested statements
     * @param parentIndex / the index of the statement in the Block's scope
     * @private
     */
    private static setFlagsToNestedStatementsAndAddBlockIndex(methodName: string, className: string, statement: Statement, parentIndex: number): number {
        switch (statement.getKind()) {
            case SyntaxKind.SwitchStatement:
                for (const caseClause of (statement as SwitchStatement).getClauses()) {
                    parentIndex = this.setFlagToBlock(methodName, className, caseClause, parentIndex);
                }
                break;
            case SyntaxKind.ForStatement:
            case SyntaxKind.ForInStatement:
            case SyntaxKind.ForOfStatement:
            case SyntaxKind.IfStatement:
            case SyntaxKind.WhileStatement:
                for (const block of (statement as IfStatement).getChildrenOfKind(SyntaxKind.Block)) {
                    parentIndex = this.setFlagToBlock(methodName, className, block, parentIndex);
                }
                break;
            case SyntaxKind.ExpressionStatement:
                if (statement.getDescendantStatements().length > 0) {
                    for (const block of this.getChildrenBlocks(statement)) {
                        parentIndex = this.setFlagToBlock(methodName, className, block, parentIndex);
                    }
                }
                break;
            case SyntaxKind.TryStatement:
                const tryBlock: Block = statement.getFirstChildByKind(SyntaxKind.Block);
                parentIndex = this.setFlagToBlock(methodName, className, tryBlock, parentIndex);
                const catchBlock: Block = statement.getFirstChildByKind(SyntaxKind.CatchClause).getFirstChildByKind(SyntaxKind.Block);
                parentIndex = this.setFlagToBlock(methodName, className, catchBlock, parentIndex);
                break;
            default:
        }
        return parentIndex;
    }


    private static getChildrenBlocks(parentNode: Node): Block[] {
        let blocks: Block[] = [];
        for (const childNode of parentNode.getChildren()) {
            if (childNode.getKind() === SyntaxKind.Block) {
                blocks.push(childNode as Block);
            } else {
                blocks.push(...this.getChildrenBlocks(childNode));
            }
        }
        return blocks;
    }


    private static async addArgumentsToReferenceNodes(callerInformations: CallerInformations): Promise<void> {
        if (!callerInformations.isInheritedFromOutOfSystem) {
            FlagMethodsService.insertParameter(callerInformations, 0, {
                name: 'callingInstancePath',
                type: 'any'
            });
        }
        const nodes: Node[] = callerInformations.declaration.findReferencesAsNodes();
        for (const node of nodes) {
            const callExpression: CallExpression = node.getFirstAncestorByKind(SyntaxKind.CallExpression);
            if (callExpression) {
                // const identifier = callExpression.getFirstDescendantByKind(SyntaxKind.Identifier).getText();
                // if (identifier === 'TranslateModule') {
                //     console.log(chalk.blueBright('CALLEXPRRRR IDENTIFIERRRR'), callExpression.getFirstDescendantByKind(SyntaxKind.Identifier).getText());
                //     console.log(chalk.blueBright('CALLEXPRRRR isCallExpressionFromOutOfProject'), this.isCallExpressionFromOutOfProject(callExpression));
                //     getImportDefaults(callExpression.getSourceFile());
                // }
                if (this.isCallExpressionFromOutOfProject(callExpression)) {
                    // console.log(chalk.magentaBright('CALLEXPRRRR'), callExpression.getText());
                    continue;
                }
                KzFilePathService.addKzFilePathArgument(callExpression);
            } else if (nodeIsAnImport(node)) {
                for (const callExpressionBoundToImportIdentifier of this.callExpressionsUsingImport(node)) {
                    KzFilePathService.addKzFilePathArgument(callExpressionBoundToImportIdentifier);
                }
            }
            node.getSourceFile().saveSync();
        }
    }


    private static isCallExpressionFromOutOfProject(callExpression: CallExpression): boolean {
        return isIdentifierDeclaredOutOfProject(callExpression.getFirstDescendantByKind(SyntaxKind.Identifier));
    }


    private static callExpressionsUsingImport(node: Node): CallExpression[] {
        return node.getSourceFile().getDescendantsOfKind(SyntaxKind.Identifier).filter(i => i.getText() === node.getText() && i.getParent().getKind() === SyntaxKind.CallExpression).map(i => i.getParent() as CallExpression);
    }
}
