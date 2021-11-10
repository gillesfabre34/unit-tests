import * as chalk from 'chalk';
import { ImportDeclaration, InterfaceDeclaration, MethodSignature, SourceFile } from 'ts-morph';
import { GLOBAL } from '../../init/const/global.const';
import { EnumService } from './enum.service';
import { KzFilePathService } from './kz-filepath.service';
import { FlagStatementsService } from './flag-statements.service';
import { FlagMethodsService } from './flag-methods.service';
import { CallerInformations } from '../models/caller-informations.model';
import { getOriginalSourceFile, hasStatements } from '../../../shared/utils/ast-statements.util';
import { addImportDeclarationFromRelativeOriginalPath, enumImports } from '../../../shared/utils/ast-imports.util';
import { copyToKuzzyFolder } from '../../utils/kuzzy-folder.util';
import { getExtension, removeFiles } from '../../utils/file-system.util';
import { plural } from '../../../shared/utils/strings.util';
import { InitService } from '../../init/services/init.service';

const FORCE_CLONE = false;

export abstract class FlagService {


    static async start(): Promise<void> {
        await EnumService.createEnumerableFiles();
        if (FORCE_CLONE) {
            await this.resetFlags();
        }
        console.log(chalk.yellowBright(`Flag ${GLOBAL.fileUtsToFlag.length} ${plural('file', GLOBAL.fileUtsToFlag.length)}...`));
        for (const sourceFile of GLOBAL.fileUtsToFlag) {
            await this.flagSourceFile(sourceFile);
        }
    }


    private static async resetFlags(): Promise<void> {
        await removeFiles(GLOBAL.flaggedProject.getSourceFiles().map(s => s.getFilePath()));
        await copyToKuzzyFolder(GLOBAL.project.getSourceFiles().map(s => s.getFilePath()));
        await InitService.resetFlaggedProject();
        GLOBAL.fileUtsToFlag = GLOBAL.flaggedProject.getSourceFiles();
    }


    static async flagSourceFile(sourceFile: SourceFile): Promise<void> {
        if (hasStatements(sourceFile)) {
            this.addImportDeclarations(sourceFile);
            await this.flagClassesAndFunctions(sourceFile);
        }
        this.flagInterfaces(sourceFile);
        const enumsImports: ImportDeclaration[] = enumImports(getOriginalSourceFile(sourceFile));
        if (enumsImports.length > 0) {
            EnumService.setKuzzyEnumGetters(sourceFile, enumsImports);
        }
        await sourceFile.save();
    }


    private static addImportDeclarations(sourceFile: SourceFile): void {
        if (getExtension(sourceFile.getBaseName()) !== 'ts') {
            // TODO : .js case
            return;
        }
        this.addImports(sourceFile);
        KzFilePathService.addConstants(sourceFile);
    }


    private static addImports(sourceFile: SourceFile): void {
        addImportDeclarationFromRelativeOriginalPath(sourceFile, 'CreateInstance', '/frontend/flag/decorators/create-instance.decorator.ts');
        addImportDeclarationFromRelativeOriginalPath(sourceFile, 'Flash', '/frontend/flag/decorators/flash.decorator.ts');
        addImportDeclarationFromRelativeOriginalPath(sourceFile, 'parse', '/frontend/utils/coverage.util.ts');
        addImportDeclarationFromRelativeOriginalPath(sourceFile, 'ClassEnum', '/frontend/flag/models/class-enum.model.ts');
    }


    private static async flagClassesAndFunctions(sourceFile: SourceFile): Promise<void> {
        for (const classDeclaration of sourceFile.getClasses()) {
            KzFilePathService.setClassDecorator(classDeclaration);
            await FlagMethodsService.flagMethods(classDeclaration);
        }
        for (const functionDeclaration of sourceFile.getFunctions()) {
            await FlagStatementsService.flagStatements(new CallerInformations(functionDeclaration));
        }
    }


    private static flagInterfaces(sourceFile: SourceFile): void {
        const interfaceDeclarations: InterfaceDeclaration[] = sourceFile.getInterfaces();
        for (const interfaceDeclaration of interfaceDeclarations) {
            this.addParameterToMethodSignaturesInInterfaces(interfaceDeclaration.getMethods());
        }
    }


    private static addParameterToMethodSignaturesInInterfaces(methodSignatures: MethodSignature[]): void {
        for (const methodSignature of methodSignatures) {
            methodSignature.addParameter({ name: 'callingInstancePath', type: 'any' });
        }
    }
}
