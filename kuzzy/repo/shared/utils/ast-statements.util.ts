import { FunctionDeclaration, MethodDeclaration, Node, PropertyDeclaration, SourceFile, SyntaxKind } from 'ts-morph';
import { MethodOrFunctionDeclaration } from '../../frontend/flag/types/method-or-function-declaration.type';
import { GLOBAL } from '../../frontend/init/const/global.const';
import { originalPath } from '../../frontend/utils/kuzzy-folder.util';


export function isVoidMethod(methodOrFunctionDeclaration: MethodOrFunctionDeclaration): boolean {
    // const sourceFile: SourceFile = methodDeclaration.getSourceFile();
    // const sourceFile: SourceFile = GLOBAL.project.getSourceFile(filePath);
    // const methodOrFunctionDeclaration: MethodOrFunctionDeclaration = className ? sourceFile.getClass(className)?.getMethod(methodOrFunctionName) : sourceFile.getFunction(methodOrFunctionName);
    // return isVoidMethodNode(methodOrFunctionDeclaration);
// }
//
//
// function isVoidMethodNode(node: MethodDeclaration | FunctionDeclaration): boolean {
    return methodOrFunctionDeclaration?.getStructure().returnType === 'void';
}


export function hasStatements(sourceFile: SourceFile): boolean {
    return sourceFile.getClasses().filter(c => c.getDescendantStatements().length > 0).length > 0
        || sourceFile.getFunctions().filter(c => c.getDescendantStatements().length > 0).length > 0;
}


export function getDeclaration(node: Node): PropertyDeclaration | FunctionDeclaration | MethodDeclaration {
    if (!node) {
        return undefined;
    }
    const parentNode: Node = node.getParent();
    return isPropertyOrMethodOrFunctionDeclaration(parentNode) ? parentNode as PropertyDeclaration | FunctionDeclaration | MethodDeclaration : getDeclaration(parentNode);
}


export function isPropertyOrMethodOrFunctionDeclaration(node: Node): boolean {
    if (!node) {
        return false;
    }
    return [SyntaxKind.PropertyDeclaration, SyntaxKind.FunctionDeclaration, SyntaxKind.MethodDeclaration].includes(node.getKind());
}


export function getOriginalSourceFile(flaggedSourceFile: SourceFile): SourceFile {
    return GLOBAL.project.getSourceFile(originalPath(flaggedSourceFile.getFilePath()));
}
