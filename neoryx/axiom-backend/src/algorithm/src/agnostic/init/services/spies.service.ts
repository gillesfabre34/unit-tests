import { SpyMethod } from '../../write/models/spy-method.model';
import {
    CallExpression,
    Expression,
    Identifier,
    MethodDeclaration,
    Node,
    PropertyAccessExpression,
    SyntaxKind, VariableDeclaration
} from 'ts-morph';
import * as chalk from 'chalk';
import { switchAll } from 'rxjs/operators';

export class SpiesService {

    private static instance: SpiesService = undefined;

    private _spiedObject: string = undefined;
    private _spies: SpyMethod[] = [];

    private constructor() {
    }


    get spiedObject(): string {
        return this._spiedObject;
    }


    set spiedObject(spiedObject: string) {
        this._spiedObject = spiedObject;
    }


    get spies(): SpyMethod[] {
        return this._spies;
    }


    /**
     * Singleton instanceProperties of this service
     */
    static getInstance(): SpiesService {
        if (!SpiesService.instance) {
            SpiesService.instance = new SpiesService();
        }
        return SpiesService.instance;
    }


    clear() {
        this._spies = [];
    }


    getSpyMethod(filePath: string, className: string, methodName: string): SpyMethod {
        let spyMethod: SpyMethod = this._spies.find(s => s.filePath === filePath && s.className === className && s.methodName === methodName);
        if (spyMethod) {
            return spyMethod;
        }
        spyMethod = new SpyMethod(filePath, className, methodName);
        this._spies.push(spyMethod);
        return spyMethod;
    }


    // TODO finish to implement for different callExpression cases
    static getSpiedObject(callExpression: CallExpression): string {
        const expression: Expression = callExpression.getExpression();
        if (expression.getKind() === SyntaxKind.PropertyAccessExpression) {
            const identifier: Identifier = this.getFirstIdentifier(expression as PropertyAccessExpression);
            if (identifier) {
                const declaration: Node = identifier.getSymbol().getDeclarations()?.[0];
                if (declaration.getKind() === SyntaxKind.VariableDeclaration) {
                    return this.areInSameMethod((declaration as VariableDeclaration).getInitializer(), callExpression) ? undefined : identifier.getText();
                }
            }
        }
        return undefined;
    }


    private static getFirstIdentifier(propertyAccessExpression: PropertyAccessExpression): Identifier {
        const expression: Expression = propertyAccessExpression.getExpression();
        switch (expression.getKind()) {
            case SyntaxKind.PropertyAccessExpression:
                return this.getFirstIdentifier(expression as PropertyAccessExpression);
            case SyntaxKind.Identifier:
                return expression as Identifier;
            default:
                return undefined;
        }
    }


    private static areInSameMethod(firstNode: Node, secondNode: Node): boolean {
        return firstNode && secondNode && firstNode.getFirstAncestorByKind(SyntaxKind.MethodDeclaration) === secondNode.getFirstAncestorByKind(SyntaxKind.MethodDeclaration);
    }

}
