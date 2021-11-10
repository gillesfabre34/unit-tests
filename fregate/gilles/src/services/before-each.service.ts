import { BEFORE_EACH_MOCK } from '../mocks/before-each.mock';
import { BeforeEach } from '../models/before-each.model';
import { Identifier, Node, SourceFile, SyntaxKind, ThisExpression } from 'ts-morph';
import { Ast } from './ast.service';
import { deepMerge } from './tools.service';

export class BeforeEachService {


    static generate(sourceFile: SourceFile): BeforeEach {
        const beforeEach = new BeforeEach();
        const thisServiceNodes: ThisExpression[] = BeforeEachService.getThisServiceNodes(sourceFile);
        const setOfParamsNames: Set<string> = new Set(thisServiceNodes.map(n => n.getText()));
        beforeEach.constructorParams = [...setOfParamsNames].map(() => undefined);
        beforeEach.mock = BeforeEachService.getMock(thisServiceNodes);
        return beforeEach;
    }


    static getThisServiceNodes(node: Node | SourceFile): ThisExpression[] {
        const thisExpressions: ThisExpression[] = node.getDescendantsOfKind(SyntaxKind.ThisKeyword);
        return thisExpressions.filter(n => BeforeEachService.chainedElementIsServiceIdentifier(n))
    }


    static chainedElementIsServiceIdentifier(node: Node): boolean {
        const nextChainedIdentifier = Ast.nextChainedIdentifier(node);
        if (!nextChainedIdentifier) {
            return false;
        }
        return Ast.classConstructorParams(node?.getSourceFile())?.map(p => p.name).includes(nextChainedIdentifier.getText());
    }


    static getMock(thisExpressions: ThisExpression[]): string {
        if (!thisExpressions || thisExpressions.length === 0) {
            return '';
        }
        let mockObject = {
            service: {}
        };
        for (const node of thisExpressions) {
            const nextChainedIdentifier = Ast.nextChainedIdentifier(node);
            mockObject.service = deepMerge(mockObject.service, BeforeEachService.addPropertyToMockObject(nextChainedIdentifier))
        }
        return  BeforeEachService.getStringMock(mockObject.service)
    }


    static addPropertyToMockObject(identifier: Identifier): any {
        if (!identifier) {
            return {};
        }
        const nameIdentifier = identifier.getText();
        let result = {};
        const nextChainedIdentifier = Ast.nextChainedIdentifier(identifier);
        result[nameIdentifier] = nextChainedIdentifier
            ? BeforeEachService.addPropertyToMockObject(nextChainedIdentifier)
            : BeforeEachService.getMockValue(identifier);
        return result;
    }


    static getMockValue(identifier: Identifier): string {
        return (!identifier?.getParent()?.getParent() || identifier.getParent().getParent().getKind() !== SyntaxKind.CallExpression) ? '{}' : '() => {}';
    }


    static getStringMock(mockObjectService: object): string {
        // console.IO('SERVICCCC', mockObjectService)
        let mock = ``;
        for (const parameter of Object.keys(mockObjectService)) {
            mock = `${mock}service.${parameter} = {\n`;
            for (const  [key, value] of Object.entries(mockObjectService[parameter])) {
                mock = `${mock}\t\t\t${key} = ${value},\n`;
            }
            mock = `${mock.slice(0, -1)}\n\t\t}`;
        }
        // console.IO('MOCKKKKKK', mock)
        // console.IO('MOCKKKKKK REALLY MOCK', BEFORE_EACH_MOCK.mock)
        return BEFORE_EACH_MOCK.mock;
    }
}
