import {
    BinaryExpression,
    Expression,
    MethodDeclaration,
    Node,
    ObjectLiteralExpression,
    ParameterDeclaration,
    PrefixUnaryExpression,
    PropertyAccessExpression,
    PropertyAssignmentStructure,
    SyntaxKind
} from 'ts-morph';
import { ComparisonOperator } from '../../../../agnostic/constraints/enums/comparison-operator.enum';
import { ElementType } from '../../../../agnostic/constraints/enums/element-type.enum';
import { Constraint } from '../../../../agnostic/constraints/models/constraint.model';
import { InputConstraints } from '../../../../agnostic/constraints/models/input-constraints.model';
import { FALSY_CONSTRAINTS } from '../../../../agnostic/constraints/const/falsy-constraints.const';
import { AgnosticInputConstraintsService } from '../../../../agnostic/constraints/services/agnostic-input-constraints.service';
import * as chalk from 'chalk';
import {
    getElementTypeFromType,
    getParameterElementType,
    syntaxKindToElementType
} from '../../../../agnostic/constraints/utils/solvers.util';


export class InputConstraintsService extends AgnosticInputConstraintsService {

    inputsConstraints: InputConstraints[] = [];
    methodDeclaration: MethodDeclaration;


    getParameters(): ParameterDeclaration[] {
        return this.methodDeclaration.getParameters();
    }


    getParameter(parameterName: string): ParameterDeclaration {
        return this.getParameters().find(p => this.getParameterName(p) === parameterName);
    }


    getParameterName(parameterDeclaration: ParameterDeclaration): string {
        return parameterDeclaration.getName();
    }


    getParametersName(): string[] {
        return this.getParameters().map(p => this.getParameterName(p));
    }


    // TODO : add switches
    getConditionExpressions(): Expression[] {
        return this.methodDeclaration.getDescendantsOfKind(SyntaxKind.IfStatement).map(s => s.getExpression());
    }


    addInputsConstraintsForCondition(condition: Expression, exclamation = false): void {
        switch (condition.getKind()) {
            case SyntaxKind.PropertyAccessExpression:
                if (this.isThisExpression(condition as PropertyAccessExpression)) {
                    this.addInstanceConstraint(condition as PropertyAccessExpression);
                }
                break;
            case SyntaxKind.BinaryExpression:
                this.addBinaryExpressionInputsConstraints(condition as BinaryExpression);
                break;
            case SyntaxKind.Identifier:
                this.addConstraintsToInputConstraints(condition.getText(), ...FALSY_CONSTRAINTS);
                break;
            case SyntaxKind.PrefixUnaryExpression:
                this.addInputsConstraintsForCondition((condition as PrefixUnaryExpression).getOperand(), true);
                break;
        }
    }


    // TODO: remove the nodes like this.zzz()
    private isThisExpression(propertyExpression: PropertyAccessExpression): boolean {
        return propertyExpression.getExpression().getKind() === SyntaxKind.ThisKeyword;
    }


    private addInstanceConstraint(propertyExpression: PropertyAccessExpression): void {
        const constraint: Constraint = new Constraint('unknown', ComparisonOperator.IS_NOT_EQUAL); // TODO : fix for undefined
        this.addConstraint(this.getProperty(propertyExpression), true, constraint, getElementTypeFromType(propertyExpression.getType().getText()));
    }


    private getProperty(thisExpression: PropertyAccessExpression): string {
        return thisExpression.getText().slice(5);
    }


    private addBinaryExpressionInputsConstraints(binaryExpression: BinaryExpression): void {
        if (binaryExpression.getLeft().getKind() === SyntaxKind.BinaryExpression) {
            this.addInputsConstraintsForCondition(binaryExpression.getLeft())
        }
        if (binaryExpression.getRight().getKind() === SyntaxKind.BinaryExpression) {
            this.addInputsConstraintsForCondition(binaryExpression.getRight())
        }
        this.addBinaryExpressionInputConstraints(binaryExpression);
    }


    private addBinaryExpressionInputConstraints(binaryExpression: BinaryExpression): void {
        this.addSideInputConstraint(binaryExpression.getLeft(), binaryExpression.getRight(), this.getComparisonOperator(binaryExpression));
        this.addSideInputConstraint(binaryExpression.getRight(), binaryExpression.getLeft(), this.getComparisonOperator(binaryExpression));
    }


    // TODO: check if it's intersting or not to find the ComparisonOperator
    private getComparisonOperator(binaryExpression: BinaryExpression): ComparisonOperator {
        const operator: Node = binaryExpression.getChildAtIndex(1);
        return ComparisonOperator.IS_EQUAL
        // switch (operator.getKind()) {
        //     case SyntaxKind.EqualsEqualsToken:
        //     case SyntaxKind.EqualsEqualsEqualsToken:
        //         return ComparisonOperator.IS_EQUAL;
        //     case SyntaxKind.ExclamationEqualsToken:
        //     case SyntaxKind.ExclamationEqualsEqualsToken:
        //         return ComparisonOperator.IS_NOT_EQUAL;
        //     case SyntaxKind.GreaterThanToken:
        //         return ComparisonOperator.IS_GREATER;
        //     case SyntaxKind.GreaterThanEqualsToken:
        //         return ComparisonOperator.IS_GREATER_OR_EQUAL;
        //     case SyntaxKind.LessThanToken:
        //         return ComparisonOperator.IS_LOWER;
        //     case SyntaxKind.LessThanEqualsToken:
        //         return ComparisonOperator.IS_LOWER_OR_EQUAL;
        //     default:
        //         return undefined;
        // }
    }


    private addSideInputConstraint(left: Node, right: Node, comparisonOperator: ComparisonOperator): void {
        if (this.isParameter(left) || this.isInstanceAssignment(left)) {
            if (this.isPrimitive(right)) {
                this.addConstraintForPrimitive(left, right, comparisonOperator, this.isInstanceAssignment(left));
            } else if (right.getKind() === SyntaxKind.ObjectLiteralExpression) {
                this.addInputConstraintsForObjectLiteral(left, right as ObjectLiteralExpression);
            }
        }
    }


    private addConstraintForPrimitive(left: Node, right: Node, comparisonOperator: ComparisonOperator, isInstanceProperty: boolean): void {
        const leftText: string = left.getText();
        const name: string = isInstanceProperty ? this.firstChainedElement(leftText.slice(5)) : this.firstChainedElement(leftText);
        const properties: string = this.lastChainedElements(leftText);
        let type: ElementType;
        if (isInstanceProperty) {
            type = syntaxKindToElementType(right.getKindName());
        } else {
            const parameter: ParameterDeclaration = this.getParameter(name);
            type = getParameterElementType(parameter);
        }
        const constraint: Constraint = new Constraint(this.getValue(left, right), comparisonOperator, properties);
        this.addConstraint(name, isInstanceProperty, constraint, type);
    }


    private getValue(left: Node, right: Node): any {
        const text = right.getText();
        switch (right.getKind()) {
            case SyntaxKind.StringLiteral:
                return this.removeFirstAndLastCharacter(text);
            case SyntaxKind.NumericLiteral:
                return Number(text);
            case SyntaxKind.FalseKeyword:
                return false;
            case SyntaxKind.TrueKeyword:
                return true;
            case SyntaxKind.ObjectLiteralExpression:
                return this.addInputConstraintsForObjectLiteral(left, right as ObjectLiteralExpression);
        }
    }


    private removeFirstAndLastCharacter(text: string): string {
        return text.slice(1, -1);
    }


    private addInputConstraintsForObjectLiteral(left: Node, objectLiteralExpression: ObjectLiteralExpression): void {
        let value: any = undefined;
        for (const propertyAssignment of objectLiteralExpression.getProperties()) {
            const structure: PropertyAssignmentStructure = propertyAssignment.getStructure() as PropertyAssignmentStructure;
            const type: string = propertyAssignment.getType().getText();
            switch (type) {
                case 'string':
                    value = (structure.initializer as string).slice(1, -1);
                    break;
                case 'number':
                    // TODO: fix bug
                    value = structure.initializer;
                    break;
                default:
                    // TODO: implement
                    value = structure.initializer;
            }
            if (!this.inputConstraintsAlreadyExists(structure.name, true)) {
                const constraint = new Constraint(value, undefined, structure.name);
                const isInstanceProperty: boolean = left?.getText().slice(0, 5) === 'this.';
                const parameterName: string = this.firstChainedElement(left.getText());
                const inputConstraints = new InputConstraints(parameterName, isInstanceProperty, [constraint], ElementType.OBJECT);
                this.inputsConstraints.push(inputConstraints);
            }

        }
    }


    private addConstraint(name: string, isInstanceProperty: boolean, constraint: Constraint, elementType: ElementType): void {
        const inputConstraints: InputConstraints = this.inputConstraintsAlreadyExists(name, isInstanceProperty);
        if (inputConstraints && !constraint.alreadyExists(inputConstraints)) {
            inputConstraints.addConstraint(constraint);
        } else if (!inputConstraints) {
            this.inputsConstraints.push(new InputConstraints(name, isInstanceProperty, [constraint], elementType));
        }
    }


    private inputConstraintsAlreadyExists(name: string, isInstanceProperty: boolean): InputConstraints {
        return this.inputsConstraints.find(i => i.name === name && i.isInstanceProperty === isInstanceProperty)
    }


    private isParameter(node: Node): boolean {
        return this.getParametersName().includes(this.firstChainedElement(node.getText())) && this.hasNoTransformer(node);
    }


    private isInstanceAssignment(node: Node): boolean {
        return this.firstChainedElement(node.getText()) === 'this' && this.hasNoTransformer(node);
    }


    // TODO : implement this method correctly, or refactor the entire process
    private hasNoTransformer(node: Node): boolean {
        const lastElt = this.lastChainedElement(node.getText());
        return lastElt !== 'length';
    }


    private firstChainedElement(text: string): string {
        return text?.replace('?', '')?.split('.')?.[0];
    }


    private lastChainedElement(text: string): string {
        return text?.split('.').pop();
    }


    private lastChainedElements(text: string): string {
        const split: string[] = text?.split('.');
        if (!split || split.length < 2) {
            return undefined;
        }
        return text.slice(split[0].length + 1)?.replace('?', '');
    }


    // TODO : Arrays-------
    private isPrimitive(node: Node): boolean {
        const kind = node.getKind();
        return kind === SyntaxKind.StringLiteral
            || kind === SyntaxKind.NumericLiteral
            // || kind === SyntaxKind.ArrayLiteralExpression
            // || kind === SyntaxKind.ObjectLiteralExpression
            || kind === SyntaxKind.FalseKeyword
            || kind === SyntaxKind.TrueKeyword
    }


}
