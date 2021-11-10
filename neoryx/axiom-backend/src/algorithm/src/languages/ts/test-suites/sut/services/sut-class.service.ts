import { SutClass } from '../models/sut-class.model';
import { DescribeClass } from '../../../write/models/describe-class.model';
import { AgnosticSutClassService } from '../../../../../agnostic/test-suites/sut/services/agnostic-sut-class.service';
import { getFilename } from '../../../../../agnostic/tools/utils/file-system.util';

export class SutClassService extends AgnosticSutClassService {

    sutClass: SutClass = undefined;

    generate(sutClass: SutClass): SutClassService {
        this.sutClass = sutClass;
        sutClass.describeClass = new DescribeClass().generate(sutClass);
        sutClass.importsCode = this.addClassImport(sutClass);
        return this;
    }


    private addClassImport(sutClass: SutClass): string {
        return `import { ${sutClass.name} } from './${getFilename(this.sutClass.sutFile.path.slice(0, -3))}';\n`;
    }


}
