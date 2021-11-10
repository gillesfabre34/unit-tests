import { Mock } from './mock.model';
import { GLOBAL } from '../../init/const/global.const';
import { ImportDefault } from '../../write/models/import-default.model';
import { getModuleSpecifier } from '../../utils/file-system.util';

export class MockDependency {

    className: string = undefined;
    fileUTPath: string = undefined;
    mockFilePath: string = undefined;
    mockName: string = undefined;

    constructor(mockName: string, className: string, mockFilePath: string, fileUTPath: string) {
        this.mockName = mockName;
        this.className = className;
        this.mockFilePath = mockFilePath;
        this.fileUTPath = fileUTPath;
    }


    get importDefaultMockFile(): ImportDefault {
        return new ImportDefault(this.mockName, getModuleSpecifier(this.mockFilePath));
    }


    get importDefaultClassUT(): ImportDefault {
        return new ImportDefault(this.className, getModuleSpecifier(this.fileUTPath));
    }


    get mock(): Mock {
        return GLOBAL.mocks.find(m => m.className === this.className && m.fileUTPath === this.fileUTPath && m.name === this.mockName);
    }

}
