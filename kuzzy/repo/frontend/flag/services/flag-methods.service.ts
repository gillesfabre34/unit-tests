import {
    ClassDeclaration,
    HeritageClause,
    MethodDeclaration,
    OptionalKind,
    ParameterDeclarationStructure
} from 'ts-morph';
import { FlagStatementsService } from './flag-statements.service';
import { HeritageDeclaration } from '../types/heritage-declaration.type';
import { CallerInformations } from '../models/caller-informations.model';
import { flat } from '../../../shared/utils/arrays.util';


export abstract class FlagMethodsService {


    static async flagMethods(classDeclaration: ClassDeclaration): Promise<void> {
        const inheritedMethodNames: string[] = this.getOutOfSystemInterfaceMethodNames(classDeclaration);
        for (let methodDeclaration of classDeclaration.getMethods()) {
            const callerInformations = new CallerInformations(methodDeclaration, classDeclaration, this.isInheritedFromOutOfSystem(methodDeclaration, inheritedMethodNames));
            await this.flagMethod(callerInformations);
        }
    }


    private static async flagMethod(callerInformations: CallerInformations): Promise<void> {
        if (!callerInformations.isAbstract()) {
            // console.log(chalk.blueBright('CALLER NAME'), callerInformations.name);
            await FlagStatementsService.flagStatements(callerInformations);
            this.decorateMethod(callerInformations);
        }
    }


    private static getOutOfSystemInterfaceMethodNames(classDeclaration: ClassDeclaration): string[] {
        const methodNames: string[] = [];
        for (const heritageClause of classDeclaration.getHeritageClauses()) {
            for (const heritageDeclaration of this.getOutOfSystemHeritageDeclarations(heritageClause)) {
                const methodStructures: any[] = heritageDeclaration.getStructure().methods;
                methodNames.push(...methodStructures.map(m => m.name));
            }
        }
        return methodNames;
    }


    private static getOutOfSystemHeritageDeclarations(heritageClause: HeritageClause): HeritageDeclaration[] {
        return this.getHeritageDeclarations(heritageClause).filter(h => h.getSourceFile().isFromExternalLibrary() || h.getSourceFile().isInNodeModules());
    }


    private static getHeritageDeclarations(heritageClause: HeritageClause): HeritageDeclaration[] {
        return flat(heritageClause.getTypeNodes().map(t => t.getType().getSymbol()?.getDeclarations() ?? []));
        // return flat(heritageClause.getTypeNodes().map(t => t.getType().getSymbol() ? t.getType().getSymbol().getDeclarations() : []));
    }


    private static isInheritedFromOutOfSystem(methodDeclaration: MethodDeclaration, inheritedMethodNames: string[]): boolean {
        return inheritedMethodNames.includes(methodDeclaration.getName());
    }


    static decorateMethod(callerInformations: CallerInformations): void {
        const methodDeclaration = callerInformations.declaration as MethodDeclaration;
        methodDeclaration.addDecorator({
            name: 'Flash',
            arguments: [`kz_filePath`, `${callerInformations.isInheritedFromOutOfSystem}`],
            typeArguments: ['string', 'boolean']
        })
    }


    static insertParameter(callerInformations: CallerInformations, index: number, parameterDeclarationStructure: OptionalKind<ParameterDeclarationStructure>): void {
        callerInformations.declaration.insertParameter(index, parameterDeclarationStructure);
    }

}
