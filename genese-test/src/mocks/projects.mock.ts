import { Project } from 'ts-morph';
import { EMPTY_PATH_MOCK, PATH_MOCK, RETURN_STATEMENT_PATH_MOCK } from './paths.mock';

const projectMock = new Project();
projectMock.addSourceFileAtPath(PATH_MOCK);

export const PROJECT_MOCK = projectMock;

const emptyProjectMock = new Project();
emptyProjectMock.addSourceFileAtPath(EMPTY_PATH_MOCK);

export const EMPTY_PROJECT_MOCK = emptyProjectMock;

const returnStatementProjectMock = new Project();
returnStatementProjectMock.addSourceFileAtPath(RETURN_STATEMENT_PATH_MOCK);

export const RETURN_STATEMENT_PROJECT_MOCK = returnStatementProjectMock;
