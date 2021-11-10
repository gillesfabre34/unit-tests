import { ClassDeclarationStructure, PropertyAccessExpression, SyntaxKind } from 'ts-morph';
import { Ast } from './ast.service';
import { EMPTY_SOURCE_FILE_MOCK, SOURCE_FILE_MOCK } from '../mocks/source-file.mock';

describe('AST SERVICE', () => {
    let propertyAccessExpressions: PropertyAccessExpression[];

    beforeEach(() => {
        propertyAccessExpressions = SOURCE_FILE_MOCK.getDescendantsOfKind(SyntaxKind.PropertyAccessExpression);
    })


    describe('nextChainedIdentifier', () => {

        it('should return this.message Node', () => {
            const node = propertyAccessExpressions[0].getChildAtIndex(0);
            const chainedIdentifier = propertyAccessExpressions[0].getChildAtIndex(2);
            const result = Ast.nextChainedIdentifier(node);
            expect(result).toEqual(chainedIdentifier);
        });

        it('should return this.externalService.helloMethod Node', () => {
            const node = propertyAccessExpressions[1].getChildAtIndex(0).getChildAtIndex(2);
            const chainedIdentifier = propertyAccessExpressions[1].getChildAtIndex(2);
            const result = Ast.nextChainedIdentifier(node);
            expect(result).toEqual(chainedIdentifier);
        });

        it('should return undefined when there is no chained element', () => {
            const result = Ast.nextChainedIdentifier(propertyAccessExpressions[0]);
            expect(result).toBeUndefined();
        });
    })


    describe('classDeclaration', () => {

        it('should return undefined', () => {
            const result = Ast.classDeclaration(undefined);
            expect(result).toBeUndefined();
        })

        it('should return undefined', () => {
            const result = Ast.classDeclaration(EMPTY_SOURCE_FILE_MOCK);
            expect(result).toBeUndefined();
        })

        it('should return undefined', () => {
            const result = Ast.classDeclaration(SOURCE_FILE_MOCK);
            const structure: ClassDeclarationStructure = result.getStructure();
            expect(structure.name).toEqual('IsNotFalsyService');
        })

    });


    describe('classConstructorParams', () => {

        it('should return undefined', () => {
            const result = Ast.classConstructorParams(undefined);
            expect(result).toBeUndefined();
        })

        it('should return undefined', () => {
            const result = Ast.classConstructorParams(EMPTY_SOURCE_FILE_MOCK);
            expect(result).toBeUndefined();
        })

        it('should return undefined', () => {
            const result = Ast.classConstructorParams(SOURCE_FILE_MOCK).map(c => c.name);
            expect(result?.includes('externalService')).toBeTrue();
        })

    });

})
