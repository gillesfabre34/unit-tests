import { BeforeEachService } from './before-each.service';
import { BEFORE_EACH_MOCK } from '../mocks/before-each.mock';
import { SOURCE_FILE_MOCK } from '../mocks/source-file.mock';
import { PROJECT_MOCK } from '../mocks/projects.mock';
import { Identifier, Node, SourceFile, SyntaxKind } from 'ts-morph';
import { Ast } from './ast.service';

describe('BEFORE EACH SERVICE', () => {
    let sourceFile: SourceFile
    let someNode: Node;

    beforeEach(() => {
        sourceFile = PROJECT_MOCK.getSourceFileOrThrow('my.service.ts');
        someNode = sourceFile.getChildAtIndex(0);
    })


    describe('generate', () => {

        it('should return BEFORE_EACH_MOCK', () => {
            const result = BeforeEachService.generate(SOURCE_FILE_MOCK);
            expect(result).toEqual(BEFORE_EACH_MOCK);
        });
    })


    describe('getThisServiceNodes', () => {
        let result: Node[];

        beforeEach(() => {
            result = BeforeEachService.getThisServiceNodes(sourceFile);
        })

        it('should return length = 3', () => {
            expect(result.length).toEqual(3);
        });
    })


    describe('chainedElementIsServiceIdentifier', () => {

        it('should return false when next chained element is undefined', () => {
            spyOn(Ast, 'nextChainedIdentifier').and.returnValue(undefined)
            const result = BeforeEachService.chainedElementIsServiceIdentifier(someNode);
            expect(result).toBeFalse();
        });

        it('should return false when next chained identifier is not the service name', () => {
            spyOn(Ast, 'nextChainedIdentifier').and.returnValue(undefined)
            const result = BeforeEachService.chainedElementIsServiceIdentifier(someNode);
            expect(result).toBeFalse();
        });

        it('should return false when next chained identifier is not the service name', () => {
            spyOn(Ast, 'nextChainedIdentifier').and.returnValue({ getText: () => 'externalService'} as any)
            const result = BeforeEachService.chainedElementIsServiceIdentifier(someNode);
            expect(result).toBeTrue();
        });
    })


    describe('getMock', () => {
        let result: string;

        it('should return BEFORE_EACH_MOCK.mock', () => {
            result = BeforeEachService.getMock([undefined])
            expect(result).toEqual(BEFORE_EACH_MOCK.mock);
        });
    })


    describe('getMockValue', () => {
        let result: string;

        it('should return {}', () => {
            // result = BeforeEachService.getMockValue(undefined)
            // expect(result).toEqual({});
        });

        it('should return () => {}', () => {
            // The helloMethod node in 'this.externalService.helloMethod'
            const helloMethodFirstNode: Identifier = sourceFile.getDescendantsOfKind(SyntaxKind.Identifier)
                .filter(n => n.getText() === 'helloMethod')[0];
            result = BeforeEachService.getMockValue(helloMethodFirstNode);
            expect(result).toEqual('() => {}');
        })
    })


    describe('addPropertyToMockObject', () => {
        let result: {};

        it('should return undefined when param is undefined', () => {
            result = BeforeEachService.addPropertyToMockObject(undefined)
            expect(result).toEqual({});
        });

        it('should return BEFORE_EACH_MOCK.mock', () => {
            // The externalService node in 'this.externalService.helloMethod'
            const externalServiceFirstNode: Identifier = sourceFile.getDescendantsOfKind(SyntaxKind.Identifier)
                .filter(n => n.getText() === 'externalService')[1];
            result = BeforeEachService.addPropertyToMockObject(externalServiceFirstNode)
            expect(result).toEqual({
                externalService: {
                    helloMethod: '() => {}'
                }
            });
        });
    })

})
