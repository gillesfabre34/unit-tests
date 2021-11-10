import {
    Identifier,
    ImportDeclaration,
    ImportSpecifier,
    ImportSpecifierStructure,
    Node,
    OptionalKind,
    SourceFile,
    SyntaxKind
} from 'ts-morph';
import { flat } from './arrays.util';
import { isOutOfProject } from './ast-sourcefile.util';
import { Enumerable } from '../../frontend/flag/models/enumerable.model';
import { GLOBAL } from '../../frontend/init/const/global.const';
import { ImportDefault } from '../../frontend/write/models/import-default.model';
import { removeExtension } from '../../frontend/utils/file-system.util';


export function enumImports(sourceFile: SourceFile): ImportDeclaration[] {
    if (!sourceFile) {
        return [];
    }
    return sourceFile.getImportDeclarations().filter(c => isEnumImport(c));
}


export function isEnumImport(importDeclaration: ImportDeclaration): boolean {
    const enumFilesWithSameEnumName: Enumerable[] = GLOBAL.enumerables.filter(e => namedImports(importDeclaration).includes(e.originalEnumName));
    return !!enumFilesWithSameEnumName.find(e => e.moduleSpecifier === importDeclaration.getModuleSpecifierValue());
}


export function namedImports(importDeclaration: ImportDeclaration): string[] {
    return importDeclaration.getNamedImports().map(n => n.getName());
}


export function addImportDeclarationFromRelativeOriginalPath(sourceFile: SourceFile, importName: string, relativeOriginalPath: string): void {
    addImportDeclaration(sourceFile, importName, `${GLOBAL.algoPath}${removeExtension(relativeOriginalPath)}`);
}

export function addImportDeclarationIfNotExists(sourceFile: SourceFile, importName: string, path: string): void {
    const importSpecifierStructures: OptionalKind<ImportSpecifierStructure>[] = flat(sourceFile.getImportDeclarations().map(i => i.getStructure().namedImports));
    const namedImports: string[] = importSpecifierStructures.map(i => i.name);
    if (!namedImports.includes(importName)) {
        addImportDeclaration(sourceFile, importName, path);
    }
}


export function addImportDeclaration(sourceFile: SourceFile, importName: string, path: string): void {
    const defaultImport = `{ ${importName} }`;
    sourceFile.addImportDeclaration({
        defaultImport: defaultImport,
        moduleSpecifier: path
    });
}


export function getDefaultImport(importDeclaration: ImportDeclaration): string {
    const importSpecifiers: ImportSpecifier[] = importDeclaration.getNamedImports();
    return importSpecifiers.length > 0 ? importSpecifiers[0].getName() : undefined;
}


export function isIdentifierDeclaredOutOfProject(identifier: Identifier): boolean {
    const importSpecifier: Node = identifier?.getSymbol()?.getDeclarations()[0];
    if (!importSpecifier || importSpecifier.constructor.name !== 'ImportSpecifier') {
        return false;
    }
    return isImportFromOutOfProject(importSpecifier as ImportSpecifier);
}


export function isImportFromOutOfProject(importSpecifier: ImportSpecifier): boolean {
    return isOutOfProject(importSpecifier.getImportDeclaration().getModuleSpecifierSourceFile());
}


export function nodeIsAnImport(node: Node): boolean {
    return node.getParent().getKind() === SyntaxKind.ImportSpecifier;
}


export function getImportDefaultsInsideProject(sourceFile: SourceFile): ImportDefault[] {
    return getImportDefaults(sourceFile).filter(i => i.isOutOfProject === false);
}


export function getImportDefaults(sourceFile: SourceFile): ImportDefault[] {
    const importDefaults: ImportDefault[] = [];
    for (const importDeclaration of sourceFile.getImportDeclarations()) {
        for (const namedImport of importDeclaration.getNamedImports()) {
            const importDefault  = new ImportDefault(namedImport.getName(), importDeclaration.getModuleSpecifier().getText(), importDeclaration.getModuleSpecifierSourceFile());
            importDefaults.push(importDefault)
        }
    }
    return importDefaults;
}
