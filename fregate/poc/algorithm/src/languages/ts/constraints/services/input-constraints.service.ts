import {
    BinaryExpression,
    Expression,
    MethodDeclaration,
    Node,
    ParameterDeclaration, PrefixUnaryExpression,
    PropertyAccessExpression,
    SyntaxKind,
    ts,
    Type
} from 'ts-morph';
import { ComparisonOperator } from '../../../../agnostic/constraints/enums/comparison-operator.enum';
import { PrimitiveType } from '../../../../agnostic/constraints/enums/primitive-type.enum';
import { Constraint } from '../../../../agnostic/constraints/models/constraint.model';
import { InputConstraints } from '../../../../agnostic/constraints/models/input-constraints.model';
import { FALSY_CONSTRAINTS } from '../../../../agnostic/constraints/const/falsy-constraints.const';
import { AgnosticInputConstraintsService } from '../../../../agnostic/constraints/services/agnostic-input-constraints.service';
import { PrimitiveTypeService } from '../../../../agnostic/constraints/services/primitive-type.service';
import * as chalk from 'chalk';


export class InputConstraintsService extends AgnosticInputConstraintsService {

    inputsConstraints: InputConstraints<any>[] = [];


    // TODO : add switches
    getConditionExpressions(methodDeclaration: MethodDeclaration): Expression[] {
        return methodDeclaration.getDescendantsOfKind(SyntaxKind.IfStatement).map(s => s.getExpression());
    }


    addConditionInputsConstraints(condition: Node, exclamation = false): void {
        if (ts.isPrefixUnaryExpression(condition.compilerNode)) {
            // const realCondition = condition.getFirstChild();
            // this.addConditionInputsConstraints(realCondition);
            this.addConditionInputsConstraints((condition as PrefixUnaryExpression).getOperand(), true);
        }

        if (ts.isIdentifier(condition.compilerNode)) {
            this.addConstraintsToInputConstraints(condition.getText(), ...FALSY_CONSTRAINTS);
        }

        if (condition.getKind() === SyntaxKind.PropertyAccessExpression) {
            if (this.isThisExpression(condition as PropertyAccessExpression)) {
                this.addInstanceConstraint(condition as PropertyAccessExpression);
            }
        }

        if (ts.isBinaryExpression(condition.compilerNode)) {
            this.addBinaryExpressionInputsConstraints(condition as BinaryExpression);
        }
    }


    // TODO: remove the nodes like this.zzz()
    private isThisExpression(propertyExpression: PropertyAccessExpression): boolean {
        return propertyExpression.getExpression().getKind() === SyntaxKind.ThisKeyword;
    }


    private addInstanceConstraint(propertyExpression: PropertyAccessExpression): void {
        const constraint: Constraint = new Constraint('zzz', ComparisonOperator.IS_NOT_EQUAL); // TODO : fix for undefined
        this.addConstraint(this.getProperty(propertyExpression), true, constraint, propertyExpression.getType());
    }


    private getProperty(thisExpression: PropertyAccessExpression): string {
        return thisExpression.getText().slice(5);
    }


    private addBinaryExpressionInputsConstraints(binaryExpression: BinaryExpression): void {
        if (binaryExpression.getLeft().getKind() === SyntaxKind.BinaryExpression) {
            this.addConditionInputsConstraints(binaryExpression.getLeft())
        }
        if (binaryExpression.getRight().getKind() === SyntaxKind.BinaryExpression) {
            this.addConditionInputsConstraints(binaryExpression.getRight())
        }
        this.addBinaryExpressionInputConstraints(binaryExpression);
    }


    private addBinaryExpressionInputConstraints(binaryExpression: BinaryExpression): void {
        this.addSideInputConstraint(binaryExpression.getLeft(), binaryExpression.getRight(), this.getComparisonOperator(binaryExpression));
        this.addSideInputConstraint(binaryExpression.getRight(), binaryExpression.getLeft(), this.getComparisonOperator(binaryExpression));
    }


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
        if (this.isParameter(left) && this.isPrimitive(right)) {
            const constraint: Constraint = new Constraint(this.getValue(right), comparisonOperator);
            this.addConstraint(left.getText(), false, constraint, left.getType());
        }
    }


    private getValue(right: Node): any {
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
        }
    }


    private removeFirstAndLastCharacter(text: string): string {
        return text.slice(1, -1);
    }


    private addConstraint(name: string, isInstanceProperty: boolean, constraint: Constraint, type: Type): void {
        const inputConstraints: InputConstraints<any> = this.inputConstraintsAlreadyExists(name, isInstanceProperty);
        if (inputConstraints && !constraint.alreadyExists(inputConstraints)) {
            inputConstraints.addConstraint(constraint);
        } else if (!inputConstraints) {
            const primitiveType: PrimitiveType = type ? PrimitiveTypeService.get(type.getText()) : undefined;
            this.inputsConstraints.push(new InputConstraints<any>(name, isInstanceProperty, [constraint], undefined, primitiveType))
        }
    }


    private inputConstraintsAlreadyExists(name: string, isInstanceProperty: boolean): InputConstraints<any> {
        return this.inputsConstraints.find(i => i.name === name && i.isInstanceProperty === isInstanceProperty)
    }


    private isParameter(node: Node): node is ParameterDeclaration {
        if (node.getKind() !== SyntaxKind.Identifier) {
            return false;
        }
        return node.getSymbol()?.getDeclarations()?.[0] instanceof ParameterDeclaration;
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
