import { PathService } from './path.service';
import { Path } from '../models/path.model';
import { Block, Statement, SyntaxKind } from 'ts-morph';
import { DESCRIBE_MY_METHOD_MOCK } from '../mocks/describe-my-method.mock';
import { PATHS_MOCK } from '../mocks/paths-statements.mock';
import { DESCRIBE_MOCK } from '../mocks/describe.mock';
import { astMocks, expectJasmineArray } from './tools.service';
import cloneDeep from 'clone-deep-circular-references';

describe('PATH SERVICE', () => {

    describe('getPaths', () => {
        it('Should return []', () => {
            const result = PathService.getPaths(undefined);
            expect(result).toEqual([])
        });
        // TODO
        // it('Should return PATHS_MOCK', () => {
        //     const result = PathService.getPaths(DESCRIBE_MOCK.describes[0]);
        //     expectJasmineArray(result, PATHS_MOCK, true, false);
        // });
    });


    describe('createPathsByBlock', () => {
        it('Should return [ReturnStatement]', () => {
            const code = `export class MyService {
                myMethod() {
                    return 666;
                }
            }`;
            const mocks = astMocks(code);
            const mockedResult: any = [{
                id: '0',
                route: [mocks.sourceFile.getFirstDescendantByKind(SyntaxKind.ReturnStatement)] as any[]
            }];
            // @ts-ignore
            const result: any[] = PathService.createPathsByBlock(mocks.firstPath, mocks.firstBlock.getStatements()) as any[];
            expectJasmineArray(result, mockedResult, true, false);
        });
        it('Should return [ExpressionStatement, ReturnStatement]', () => {
            const code = `export class MyService {
                myMethod() {
                    this.a = 2;
                    return 666;
                }
            }`;
            const mocks = astMocks(code);
            const mockedResult: Path[] = [{
                id: '0',
                route: mocks.sourceFile.getFirstDescendantByKind(SyntaxKind.ClassDeclaration).getDescendantStatements() as Statement[]
            }];
            // @ts-ignore
            const result: any[] = PathService.createPathsByBlock(mocks.firstPath, mocks.firstBlock.getStatements());
            expectJasmineArray(result, mockedResult, true, false);
        });
        it('Should return [[If, Expression], [If]]', () => {
            const code = `export class MyService {
                myMethod() {
                    if (zzz > 3) {
                        console.log(666);
                    }
                }
            }`;
            const mocks = astMocks(code);
            const statements: Statement[] = mocks.sourceFile.getFirstDescendantByKind(SyntaxKind.ClassDeclaration).getDescendantStatements() as Statement[];
            const mockedResult: Path[] = [
                {
                    id: '00',
                    route: statements
                },
                {
                    id: '01',
                    route: [statements[0]]
                }
            ];
            // @ts-ignore
            const result: any[] = PathService.createPathsByBlock(mocks.firstPath, mocks.firstBlock.getStatements());
            expectJasmineArray(result, mockedResult, true, false);
        });
        it('Should return [[Expression, If, Expression], [Expression, If]]', () => {
            const code = `export class MyService {
                myMethod() {
                    console.log(665);
                    if (b > 3) {
                        console.log(666);
                    }
                }
            }`;
            const mocks = astMocks(code);
            const statements: Statement[] = mocks.sourceFile.getFirstDescendantByKind(SyntaxKind.ClassDeclaration).getDescendantStatements() as Statement[];
            const mockedResult: Path[] = [
                {
                    id: '00',
                    route: statements
                },
                {
                    id: '01',
                    route: [statements[0], statements[1]]
                }
            ];
            // @ts-ignore
            const result: any[] = PathService.createPathsByBlock(mocks.firstPath, mocks.firstBlock.getStatements());
            expectJasmineArray(result, mockedResult, true, false);
        });
        it('Should return [[If, Expression, Return], [If, Return]]', () => {
            const code = `export class MyService {
                myMethod() {
                    if (zzz > 3) {
                        console.log(666);
                    }
                    return 667;
                }
            }`;
            const mocks = astMocks(code);
            const statements: Statement[] = mocks.sourceFile.getFirstDescendantByKind(SyntaxKind.ClassDeclaration).getDescendantStatements() as Statement[];
            const mockedResult: Path[] = [
                {
                    id: '00',
                    route: statements
                },
                {
                    id: '01',
                    route: [statements[0], statements[2]]
                }
            ];
            // @ts-ignore
            const result: any[] = PathService.createPathsByBlock(mocks.firstPath, mocks.firstBlock.getStatements());
            expectJasmineArray(cloneDeep(result), cloneDeep(mockedResult), true, false);
        });

        it('Should return [[If, Expression, ReturnIf], [If, ReturnFinal]]', () => {
            const code = `export class MyService {
                myMethod() {
                    if (zzz > 3) {
                        console.log(666);
                        return undefined;
                    }
                    return 667;
                }
            }`;
            const mocks = astMocks(code);
            const statements: Statement[] = mocks.sourceFile.getFirstDescendantByKind(SyntaxKind.ClassDeclaration).getDescendantStatements() as Statement[];
            const mockedResult: Path[] = [
                {
                    id: '00',
                    route: [statements[0], statements[1], statements[2]]
                },
                {
                    id: '01',
                    route: [statements[0], statements[3]]
                }
            ];
            // @ts-ignore
            const result: any[] = PathService.createPathsByBlock(mocks.firstPath, mocks.firstBlock.getStatements());
            expectJasmineArray(cloneDeep(result), cloneDeep(mockedResult), true, false);
        });

        it('Should return PATHS_MOCK', () => {
            const path = new Path();
            path.id = '0';
            // const firstBlock: Block = DESCRIBE_MY_METHOD_MOCK.method.getFirstDescendantByKind(SyntaxKind.Block);
            // // @ts-ignore
            // const result = PathService.createPathsByBlock(path, firstBlock.getStatements());
            // expectJasmineArray(cloneDeep(result), cloneDeep(PATHS_MOCK), true, false);
        });
    });
})
