import { ClassSpec } from '../models/class-spec.model';
import { DESCRIBE_MOCK } from '../mocks/describe.mock';
import { throwError } from './tools.service';
import { Describe } from '../models/describe.model';
import { BeforeEachService } from './before-each.service';
import { ClassDeclaration, SourceFile } from 'ts-morph';
import { Ast } from './ast.service';
import { DescribeService } from './describe.service';

export class ClassSpecService {


    static generate(sourceFile: SourceFile): ClassSpec {
        const classDeclaration: ClassDeclaration = Ast.classDeclaration(sourceFile);
        if (!classDeclaration) {
            throwError(`Incorrect sourceFile : impossible to generate its ClassSpec. ${sourceFile}`)
        }
        const classSpec = new ClassSpec();
        classSpec.className = Ast.classDeclaration(sourceFile).getStructure().name;
        classSpec.describe = ClassSpecService.generateDescribe(sourceFile, classSpec.className);
        classSpec.imports = ClassSpecService.generateImports(sourceFile, classSpec.className);
        return classSpec;
    }


    private static generateDescribe(sourceFile: SourceFile, className: string): Describe {
        const describe = new Describe();
        describe.title = className;
        describe.sourceFile = sourceFile;
        describe.beforeEach = BeforeEachService.generate(sourceFile);
        describe.describes = DescribeService.generate(sourceFile);
        return DESCRIBE_MOCK;
    }


    private static generateImports(sourceFile: SourceFile, className: string): string {
        return `import { ${className} } from './${sourceFile.getBaseName().slice(0, -3)}';\n`;
    }


    /**
     * Returns the code of the test
     */
    static getCode(classSpec: ClassSpec): string {
        return `${classSpec.imports}${classSpec.describe.code}`;
    }

}
