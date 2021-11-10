import { EMPTY_PROJECT_MOCK, PROJECT_MOCK } from './projects.mock';
import { EMPTY_PATH_MOCK, PATH_MOCK } from './paths.mock';
import { SourceFile } from 'ts-morph';

export const SOURCE_FILE_MOCK: SourceFile = PROJECT_MOCK.getSourceFileOrThrow(PATH_MOCK);

export const EMPTY_SOURCE_FILE_MOCK: SourceFile = EMPTY_PROJECT_MOCK.getSourceFileOrThrow(EMPTY_PATH_MOCK);

// export const RETURN_STATEMENT_SOURCE_FILE_MOCK: SourceFile = RETURN_STATEMENT_PROJECT_MOCK.getSourceFileOrThrow(RETURN_STATEMENT_PATH_MOCK);




