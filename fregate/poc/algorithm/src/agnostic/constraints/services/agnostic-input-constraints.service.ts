import { SyntaxKind } from 'ts-morph';
import { getTypeNodeAsPrimitiveType } from '../utils/solvers.util';
import { Constraint } from '../models/constraint.model';
import { InputConstraints } from '../models/input-constraints.model';
import { AgnosticSutMethod } from '../../test-suites/sut/models/agnostic-sut-method.model';

export abstract class AgnosticInputConstraintsService {

    inputsConstraints: InputConstraints<any>[] = [];

    abstract getConditionExpressions(methodDeclaration: any): any[];
    abstract addConditionInputsConstraints(condition: any, exclamation: boolean): void;


    getInputsConstraints<T>(method: AgnosticSutMethod<T>): InputConstraints<T>[] {
        const methodDeclaration: any = method?.agnosticMethod?.methodDeclaration;
        for (const PARAMETER of method.getParameters())  {
            const PARAMETER_NAME = PARAMETER.getFirstDescendantByKind(SyntaxKind.Identifier).getText();
            const PARAMETER_TYPE = PARAMETER.getType();
            const PRIMITIVE_TYPE = getTypeNodeAsPrimitiveType(PARAMETER_TYPE);
            const INPUT_CONSTRAINTS = new InputConstraints(PARAMETER_NAME, false, [], undefined, PRIMITIVE_TYPE);
            this.inputsConstraints.push(INPUT_CONSTRAINTS)
        }
        for (const condition of this.getConditionExpressions(methodDeclaration)) {
            this.addConditionInputsConstraints(condition, false);
        }
        if (!this.hasInstanceConstraints()) {
            this.addArbitraryInstanceConstraint();
        }
        this.orderInputsConstraints();
        return this.inputsConstraints;
    }


    addConstraintsToInputConstraints(name: string, ...constraints: Constraint[]): void {
        const INPUT_CONSTRAINTS = this.getInputConstraintsByName(name);
        INPUT_CONSTRAINTS?.constraints?.push(...constraints);
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
        const arbitraryConstraint: InputConstraints<any> = new InputConstraints<any>('', true);
        arbitraryConstraint.pseudoRandomValues = [{}];
        this.inputsConstraints.push(arbitraryConstraint);

    }

    private getInputConstraintsByName(name: string): InputConstraints {
        return this.inputsConstraints.find((ic: InputConstraints) => ic.name === name);
    }

}
