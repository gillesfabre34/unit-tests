import {
    ClassDeclaration,
    Identifier, MethodDeclaration,
    Node,
    OptionalKind,
    ParameterDeclarationStructure, Project,
    SourceFile,
    SyntaxKind
} from 'ts-morph';
import { PROJECT } from '../constants/project.const';
import { TEMP_FILE_NAME } from '../mocks/paths.mock';

/**
 * Service for operations on TreeNode elements relative to a given node in Abstract Syntax TreeNode (AST)
 */
export class Ast {


    static nextChainedIdentifier(node: Node): Identifier {
        const parent: Node = node?.getParent();
        if (!parent || parent.getKind() !== SyntaxKind.PropertyAccessExpression) {
            return undefined;
        }
        return  node === parent.getChildAtIndex(0) ? parent.getChildAtIndex(2) as Identifier : Ast.nextChainedIdentifier(parent);
    }


    static classDeclaration(sourceFile: SourceFile): ClassDeclaration {
        return sourceFile?.getDescendantsOfKind(SyntaxKind.ClassDeclaration)?.[0] ?? undefined;
    }


    static classConstructorParams(sourceFile: SourceFile): OptionalKind<ParameterDeclarationStructure>[] {
        return Ast.classDeclaration(sourceFile)?.getStructure()?.ctors?.[0]?.parameters ?? undefined;
    }


    static async copySourceFile(sourceFile: SourceFile): Promise<SourceFile> {
        const copy = await sourceFile.copyImmediately("copy-my.service.ts", {overwrite: true});
        await copy.save();
        const filePath = 'src/mocks/examples/my-service/copy-my.service.ts';
        PROJECT.addSourceFileAtPath(filePath);
        return PROJECT.getSourceFileOrThrow(filePath);
    }
}
