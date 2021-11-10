import { SpyMethod } from '../../write/models/spy-method.model';
import { CallExpression, SyntaxKind } from 'ts-morph';

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



    static getSpiedObject(callExpression: CallExpression): string {
        const spied: string = callExpression.getFirstChild()
            .getDescendants()
            .filter(d => d.getKind() === SyntaxKind.Identifier || d.getKind() === SyntaxKind.ThisKeyword)
            .map(i => i.getText())
            .join('.');
        const split = spied.split('.');
        const spiedMethod = split[split.length - 1];
        return spied.slice(0, spied.length - spiedMethod.length - 1);
    }

}
