import { Node, ParameterDeclaration, SourceFile, SyntaxKind } from 'ts-morph';
import { getParameterElementType } from '../utils/solvers.util';
import { Constraint } from '../models/constraint.model';
import { InputConstraints } from '../models/input-constraints.model';
import { AgnosticSutMethod } from '../../test-suites/sut/models/agnostic-sut-method.model';
import * as chalk from 'chalk';
import { ElementType } from '../enums/element-type.enum';
import { TypeReference } from '../models/type-reference.model';

export abstract class AgnosticInputConstraintsService {

    inputsConstraints: InputConstraints[] = [];
    methodDeclaration: any;

    abstract addInputsConstraintsForCondition(condition: any, exclamation: boolean): void;
    abstract getConditionExpressions(): any[];
    abstract getParameterName(parameterDeclaration: any): string;
    abstract getParameters(): any[];


    getInputsConstraints(method: AgnosticSutMethod): InputConstraints[] {
        this.methodDeclaration = method?.agnosticMethod?.methodDeclaration;
        this.addInputsConstraintsForConditions();
        this.addInputsConstraintsForParametersWithoutConstraints()
        if (!this.hasInstanceConstraints()) {
            this.addArbitraryInstanceConstraint();
        }
        this.orderInputsConstraints();
        return this.inputsConstraints;
    }


    private addInputsConstraintsForConditions(): void {
        for (const condition of this.getConditionExpressions()) {
            this.addInputsConstraintsForCondition(condition, false);
        }
    }


    private addInputsConstraintsForParametersWithoutConstraints(): void {
        for (const parameter of this.getParametersWithoutConstraints())  {
            const elementType: ElementType = getParameterElementType(parameter);
            let typeReference: TypeReference = undefined;
            if (elementType === ElementType.TYPE_REFERENCE) {
                typeReference = this.getTypeReference(parameter);
            }
            const inputConstraints: InputConstraints = new InputConstraints(this.getParameterName(parameter), false, [], elementType, typeReference);
            this.inputsConstraints.push(inputConstraints)
        }
    }


    private getParametersWithoutConstraints(): ParameterDeclaration[] {
        const inputConstraintsNames = this.inputsConstraints.map(i => i.name);
        return this.getParameters().filter(p => !inputConstraintsNames.includes(this.getParameterName(p)));
    }


    protected addConstraintsToInputConstraints(name: string, ...constraints: Constraint[]): void {
        const inputConstraints: InputConstraints = this.getInputConstraintsByName(name);
        inputConstraints?.constraints?.push(...constraints);
    }


    private orderInputsConstraints(): void {
        const indexInstanceInputConstraints: number = this.inputsConstraints.findIndex(i => i.isInstanceProperty === true);
        if (indexInstanceInputConstraints > -1) {
            const instanceInputConstraints = this.inputsConstraints[indexInstanceInputConstraints];
            this.inputsConstraints.splice(indexInstanceInputConstraints, 1);
            this.inputsConstraints.push(instanceInputConstraints);
        }
    }


    private hasInstanceConstraints(): boolean {
        return !!this.inputsConstraints.find(i => i.isInstanceProperty === true && i.name !== undefined);
    }


    private addArbitraryInstanceConstraint() {
        const arbitraryConstraint: InputConstraints = new InputConstraints('', true);
        arbitraryConstraint.pseudoRandomValues = [{}];
        this.inputsConstraints.push(arbitraryConstraint);

    }

    private getInputConstraintsByName(name: string): InputConstraints {
        return this.inputsConstraints.find((ic: InputConstraints) => ic.name === name);
    }


    private getTypeReference(parameter: ParameterDeclaration): TypeReference {
        let typeReference: TypeReference = undefined;
        const declaration: Node = parameter.getType().getSymbol().getValueDeclaration();
        const declarationParent: Node = parameter.getType().getSymbol().getValueDeclaration().getParent();
        // console.log(chalk.magentaBright('PARAMMMM DECLLLL PT'), declaration.getKindName())
        if (declarationParent.getKind() === SyntaxKind.SourceFile) {
            const sourceFile: SourceFile = declarationParent as SourceFile;
            // console.log(chalk.magentaBright('PARAMMMM <STRRRRR'), parameter.getStructure())
            typeReference = new TypeReference(sourceFile.getFilePath(), parameter.getStructure().type as string)
        }
        // console.log(chalk.magentaBright('TYPE REFFFFF'), typeReference)
        return typeReference;
    }

}
