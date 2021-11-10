import { GlobalService } from './services/global.service';
import { Main } from './main';
import { PATH_MOCK } from './mocks/paths.mock';
import { PROJECT } from './constants/project.const';
import * as Tools from './services/tools.service';

describe('MAIN', () => {
    let main: Main;

    beforeEach(() => {
        main = new Main();
        GlobalService.generate(PROJECT.getSourceFileOrThrow(PATH_MOCK));
    })

    describe('start', () => {
        it('should create MAIN_SPEC_FILE_MOCK', () => {
            spyOn(Tools, 'getFilename').and.returnValue('my.service.ts');
            spyOn(Tools, 'createFileAsync');
            main.start(PATH_MOCK);
            expect(Tools.getFilename).toHaveBeenCalledWith(PATH_MOCK);
            expect(Tools.createFileAsync).toHaveBeenCalled();
        })
    })

});
