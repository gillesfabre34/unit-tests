import { Path } from '../models/path.model';
import { SOURCE_FILE_MOCK } from './source-file.mock';
import { Statement, SyntaxKind } from 'ts-morph';

const METHOD_DECLARATION = SOURCE_FILE_MOCK.getFirstDescendantByKind(SyntaxKind.MethodDeclaration);
export const METHOD_DECLARATION_BLOCK = METHOD_DECLARATION.getFirstDescendantByKind(SyntaxKind.Block);

const IF_STATEMENT = METHOD_DECLARATION_BLOCK.getFirstDescendantByKind(SyntaxKind.IfStatement);
const IF_EXPRESSION_STATEMENT = IF_STATEMENT.getFirstDescendantByKind(SyntaxKind.ExpressionStatement);
export const IF_EXPRESSION_STATEMENT_MOCK = IF_EXPRESSION_STATEMENT;

const ELSE_EXPRESSION_STATEMENT = IF_STATEMENT.getDescendantsOfKind(SyntaxKind.ExpressionStatement)[1];
const LAST_EXPRESSION_STATEMENT = METHOD_DECLARATION_BLOCK.getStatements()[1] as Statement;
const RETURN_STATEMENT = METHOD_DECLARATION_BLOCK.getStatements()[2] as Statement;

let pathMockIf = new Path();
pathMockIf.id = '00';
pathMockIf.route = [
    IF_STATEMENT,
    IF_EXPRESSION_STATEMENT,
    LAST_EXPRESSION_STATEMENT,
    RETURN_STATEMENT
];
export const PATH_MOCK_IF: Path = pathMockIf;

export const PATH_MOCK_ELSE: Path = {
    id: '01',
    route: [
        IF_STATEMENT,
        ELSE_EXPRESSION_STATEMENT,
        LAST_EXPRESSION_STATEMENT,
        RETURN_STATEMENT
    ]
}

export const PATHS_MOCK: Path[] = [PATH_MOCK_IF, PATH_MOCK_ELSE];
