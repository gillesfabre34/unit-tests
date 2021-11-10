import {
    ClassDeclaration,
    FunctionDeclaration,
    ImportDeclaration,
    SourceFile,
    SyntaxKind,
    TypeReferenceNode
} from 'ts-morph';
import { getDefaultImport } from '../../../shared/utils/ast-imports.util';

export class TypeReferenceService {

    // TODO : parameters and more complex types like objects containing properties with enum types
    static getPropertiesOrMethodsTypeReferences(sourceFile: SourceFile, enumDeclaration: ImportDeclaration, classOrFunctionDeclaration: ClassDeclaration | FunctionDeclaration): TypeReferenceNode[] {
        return classOrFunctionDeclaration.getDescendantsOfKind(SyntaxKind.TypeReference).filter(t => t.getText() === getDefaultImport(enumDeclaration));
    }


    static isArrayType(typeReferenceNode: TypeReferenceNode): boolean {
        return typeReferenceNode.getParent().getKind() === SyntaxKind.ArrayType;
    }

}
