import { SutClass } from '../models/sut-class.model';
import { DescribeClass } from '../../../write/models/describe-class.model';
import { AgnosticSutClassService } from '../../../../../agnostic/test-suites/sut/services/agnostic-sut-class.service';
import { getFilename } from '../../../../../agnostic/tools/utils/file-system.util';

export class SutClassService<T> extends AgnosticSutClassService<T> {

    sutClass: SutClass<T> = undefined;

    generate(sutClass: SutClass<T>): SutClassService<T> {
        this.sutClass = sutClass;
        sutClass.describeClass = new DescribeClass<T>().generate(sutClass);
        sutClass.importsCode = this.addClassImport(sutClass);
        return this;
    }


    private addClassImport(sutClass: SutClass<T>): string {
        return `import { ${sutClass.name} } from './${getFilename(this.sutClass.sutFile.path.slice(0, -3))}';\n`;
    }


}
