import { Solver } from '../interfaces/solver.interface';
import { Constraint } from './constraint.model';
import { TypeReference } from './type-reference.model';
import { ClassDeclaration, ConstructorDeclaration, SourceFile } from 'ts-morph';
import { GLOBALS } from '../../init/constants/globals.const';
import * as chalk from 'chalk';

export class TypeReferenceSolver extends Solver<any> {


	async resolve(constraints: Constraint[], typeReference: TypeReference): Promise<void> {
		this.addValues(undefined);
		if (typeReference) {
			const sourceFile: SourceFile = GLOBALS.project.getSourceFile(typeReference.sourceFilePath);
			if (sourceFile) {
				const flaggedFile: any = await require(typeReference.sourceFilePath);
				const classDeclaration: ClassDeclaration = this.getClassDeclaration(sourceFile, typeReference.className);
				const params: undefined[] = this.getConstructorParameters(this.getNumberOfConstructorParameters(classDeclaration));
				const undefinedObject = new flaggedFile[typeReference.className](...params);
				// console.log(chalk.greenBright('RESOLVVV NEW OBJJJJJ'), undefinedObject)
				this.addValues(undefinedObject);
				// const mock = this.getMock(classDeclaration, undefinedObject);
			}
		}
	}


	private getClassDeclaration(flaggedSourceFile: SourceFile, originalClassName: string): ClassDeclaration {
		const classDeclaration: ClassDeclaration = flaggedSourceFile.getClasses().find(c => c.getName() === originalClassName);
		if (!classDeclaration) {
			console.log(chalk.red('ERROR: classDeclaration not found in flagged SourceFile.'));
		}
		return classDeclaration;
	}


	private getConstructorParameters(numberOfParameters: number): undefined[] {
		const params: undefined[] = [];
		for (let i = 0; i < numberOfParameters; i++) {
			params.push(undefined);
		}
		return params;
	}


	private getNumberOfConstructorParameters(classDeclaration: ClassDeclaration): number {
		const constructorDeclarations: ConstructorDeclaration[] = classDeclaration.getConstructors();
		let minNbOfParams: number = undefined;
		for (const constructorDeclaration of constructorDeclarations) {
			const nbOfParams: number = constructorDeclaration.getParameters().length;
			minNbOfParams = (!minNbOfParams || minNbOfParams < nbOfParams) ? nbOfParams : minNbOfParams;
		}
		return minNbOfParams;
	}


	private getMock<T>(classDeclaration: ClassDeclaration, obj: T) {
		console.log(chalk.yellowBright('KEY/VALLLL'), classDeclaration.getName())
		for (const property of classDeclaration.getProperties()) {
			console.log(chalk.yellowBright('PROPPP KEY/VALLLL'), property.getName(), property.getType().getText())
			// obj[property.getName()] =
		}
	}


	random(): any {
		return {};
	}
}
