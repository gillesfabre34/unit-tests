import { Main } from './main';
import { OUTPUT_PATH_MOCK, PATH_MOCK } from './mocks/paths.mock';
import { Project } from 'ts-morph';
import { MAIN_SPEC_FILE_MOCK } from './mocks/examples/my-service/main.mock';

describe('MAIN INTEGRATION', () => {
    let main: Main;
    let project: Project;

    beforeEach(() => {
        main = new Main();
        project = new Project();
        project.addSourceFileAtPath(OUTPUT_PATH_MOCK);
    })

    describe('start', () => {
        it('should create MAIN_SPEC_FILE_MOCK', () => {
            main.start(PATH_MOCK);
            const result = project.getSourceFile(OUTPUT_PATH_MOCK).getFullText();
            // expect(result).toEqual(MAIN_SPEC_FILE_MOCK);
        })
    })

});
