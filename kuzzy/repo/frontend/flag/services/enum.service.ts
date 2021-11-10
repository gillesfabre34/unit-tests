import { GLOBAL } from '../../init/const/global.const';
import {
    ClassDeclaration,
    EnumDeclaration,
    EnumMemberStructure,
    FunctionDeclaration,
    ImportDeclaration,
    MethodDeclaration,
    PropertyDeclaration,
    SourceFile,
    SyntaxKind,
    TypeReferenceNode
} from 'ts-morph';
import { TypeReferenceService } from '../../system/services/type-reference.service';
import { Enumerable } from '../models/enumerable.model';
import { EnumerableService } from './enumerable.service';
import {
    addImportDeclarationFromRelativeOriginalPath,
    addImportDeclarationIfNotExists
} from '../../../shared/utils/ast-imports.util';
import { getDeclaration } from '../../../shared/utils/ast-statements.util';
import { originalPath } from '../../utils/kuzzy-folder.util';
import { getModuleSpecifier } from '../../utils/file-system.util';

export class EnumService {

    //   -------------------------------------   Enumerable files creation   ----------------------------------


    static async createEnumerableFiles(): Promise<void> {
        const sourceFilesWithEnums: SourceFile[] = GLOBAL.fileUtsToFlag.filter(s => s.getDescendantsOfKind(SyntaxKind.EnumDeclaration).length > 0);
        // const sourceFilesWithEnums: SourceFile[] = GLOBAL.flaggedProject.getSourceFiles().filter(s => s.getDescendantsOfKind(SyntaxKind.EnumDeclaration).length > 0);
        for (const sourceFileWithEnum of sourceFilesWithEnums) {
            await this.createEnumerableFile(sourceFileWithEnum);
        }
    }


    private static async createEnumerableFile(sourceFile: SourceFile): Promise<void> {
        for (const enumDeclaration of sourceFile.getEnums()) {
            const enumerableName: string = this.enumerableName(enumDeclaration);
            const enumName: string = enumDeclaration.getName();
            const fileCode: string = this.getCodeFile(enumDeclaration, sourceFile.getFilePath());
            sourceFile.insertText(0, fileCode);
            this.addImports(sourceFile);
            sourceFile.saveSync();
            GLOBAL.enumerables.push(new Enumerable(enumerableName, enumName, sourceFile.getFilePath()));
        }
    }


    private static getCodeFile(enumDeclaration: EnumDeclaration, filePath: string): string {
        let code = `const _${this.enumerableName(enumDeclaration)} = new Enumerable('${this.enumerableName(enumDeclaration)}', '${enumDeclaration.getName()}', '${filePath}');\n`;
        const enumMembers: EnumMemberStructure[] = enumDeclaration.getMembers().map(m => m.getStructure());
        for (const enumMember of enumMembers) {
            code = `${code}_${this.enumerableName(enumDeclaration)}.keyValues.push(new KeyValue('${enumMember.name}', ${enumMember.initializer}));\n`;
        }
        return `${code}export const ${this.enumerableName(enumDeclaration)} = _${this.enumerableName(enumDeclaration)};\n\n`;
    }


    private static enumerableName(enumDeclaration: EnumDeclaration): string {
        return `${enumDeclaration.getName().toUpperCase()}_K_ENUM`;
    }


    private static addImports(sourceFile: SourceFile): void {
        addImportDeclarationFromRelativeOriginalPath(sourceFile, 'Enumerable', '/frontend/flag/models/enumerable.model.ts');
        addImportDeclarationFromRelativeOriginalPath(sourceFile, 'KeyValue', '/frontend/flag/models/key-value.model.ts');
    }


    //   ---------------------------------------   kuzzyEnums getters   ---------------------------------------


    static setKuzzyEnumGetters(flaggedSourceFile: SourceFile, enumImportsDeclarations: ImportDeclaration[]): void {
        const originalSourceFile: SourceFile = GLOBAL.project.getSourceFile(originalPath(flaggedSourceFile.getFilePath()));
        const classDeclarations: ClassDeclaration[] = originalSourceFile.getClasses();
        const functionDeclarations: FunctionDeclaration[] = originalSourceFile.getFunctions();
        for (const classDeclaration of classDeclarations) {
            this.addCodeForEnumImportDeclarations(flaggedSourceFile, enumImportsDeclarations, classDeclaration);
        }
        for (const functionDeclaration of functionDeclarations) {
            this.addCodeForEnumImportDeclarations(flaggedSourceFile, enumImportsDeclarations, functionDeclaration);
        }
    }


    private static addCodeForEnumImportDeclarations(flaggedSourceFile: SourceFile, enumImportsDeclarations: ImportDeclaration[], originalClassOrFunctionDeclaration: ClassDeclaration | FunctionDeclaration): void {
        let statements: string[] = [];
        statements.push('let classEnums: ClassEnum[] = [];');
        for (const enumImportDeclaration of enumImportsDeclarations) {
            const references: TypeReferenceNode[] = TypeReferenceService.getPropertiesOrMethodsTypeReferences(flaggedSourceFile, enumImportDeclaration, originalClassOrFunctionDeclaration);
            for (const reference of references) {
                statements.push(this.getClassEnumStatement(flaggedSourceFile, enumImportDeclaration, reference));
            }
        }
        statements.push(`return classEnums;`);
        const sameClassOrFunctionDeclarationInFlaggedFile: ClassDeclaration | FunctionDeclaration = this.getCorrespondingClassOrFunctionDeclarationInFlaggedFile(flaggedSourceFile, originalClassOrFunctionDeclaration);
        this.setKuzzyEnumAccessor(sameClassOrFunctionDeclarationInFlaggedFile, statements);
    }


    private static getClassEnumStatement(flaggedSourceFile: SourceFile, enumImportDeclaration: ImportDeclaration, reference: TypeReferenceNode): string {
        const declaration: PropertyDeclaration | FunctionDeclaration | MethodDeclaration = getDeclaration(reference);
        if (!declaration) {
            return '';
        }
        const isProperty: boolean = declaration instanceof PropertyDeclaration;
        const isArrayType: boolean = TypeReferenceService.isArrayType(reference);
        const enumModuleSpecifier: string = enumImportDeclaration.getModuleSpecifierValue();
        const enumName: string = reference.getText();
        const enumerableName: string = this.addEnumerableImportAndReturnEnumerableName(flaggedSourceFile, enumName);
        return `classEnums.push(new ClassEnum('${declaration.getName()}', '${enumName}', '${enumModuleSpecifier}', ${enumerableName}, ${isProperty}, ${isArrayType}));`;
    }


    private static addEnumerableImportAndReturnEnumerableName(flaggedSourceFile: SourceFile, enumName: string): string {
        const enumerable: Enumerable = EnumerableService.getEnumerableWithOriginalEnumName(GLOBAL.enumerables, enumName);
        addImportDeclarationIfNotExists(flaggedSourceFile, enumerable.enumerableName, getModuleSpecifier(enumerable.filePath));
        return enumerable.enumerableName;
    }


    private static setKuzzyEnumAccessor(classOrFunctionDeclaration: ClassDeclaration | FunctionDeclaration, statements: string[]): void {
        if (classOrFunctionDeclaration instanceof ClassDeclaration) {
            classOrFunctionDeclaration.addGetAccessor({
                name: 'kuzzyEnums',
                statements: statements,
                returnType: 'ClassEnum[]'
            });
        } else {
            this.setKuzzyEnumAccessorForFunctions();
        }
    }


    private static getCorrespondingClassOrFunctionDeclarationInFlaggedFile(flaggedSourceFile: SourceFile, originalClassOrFunctionDeclarationInOriginalFile: ClassDeclaration | FunctionDeclaration): ClassDeclaration | FunctionDeclaration {
        return originalClassOrFunctionDeclarationInOriginalFile instanceof ClassDeclaration ? flaggedSourceFile.getFirstDescendantByKind(SyntaxKind.ClassDeclaration) : flaggedSourceFile.getFirstDescendantByKind(SyntaxKind.FunctionDeclaration);
    }


    // TODO
    private static setKuzzyEnumAccessorForFunctions(): void {

    }

}
