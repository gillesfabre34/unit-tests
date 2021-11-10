import {
    ClassDeclaration,
    Identifier,
    Node,
    OptionalKind,
    ParameterDeclarationStructure,
    SourceFile,
    SyntaxKind
} from 'ts-morph';

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
}
