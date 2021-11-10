import { MethodDeclaration, SourceFile } from 'ts-morph';
import { Describe } from '../models/describe.model';
import { Ast } from './ast.service';
import { IT_NAME_A, IT_NAME_UNDEFINED } from '../mocks/it.mock';
import { throwError } from './tools.service';
import { ItService } from './it.service';
import { tab, tabs } from '../mocks/tabs.mock';
import cloneDeep from 'clone-deep-circular-references';

export class DescribeService {


    static generate(sourceFile: SourceFile): Describe[] {
        if (!sourceFile) {
            throwError('No SourceFile for Describe generation');
        }
        const describes: Describe[] = [];
        const classDeclaration = Ast.classDeclaration(sourceFile);
        const methods: MethodDeclaration[] = classDeclaration.getMethods();
        for (const method of methods) {
            describes.push(this.generateMethodDescribe(method, sourceFile));
        }
        return describes;
    }


    private static generateMethodDescribe(method: MethodDeclaration, sourceFile: SourceFile): Describe {
        const describe = new Describe();
        describe.sourceFile = sourceFile;
        describe.title = method.getName() || undefined;
        describe.its = ItService.generateIts(describe);
        return describe;
    }


    static getCode(describe: Describe): string {
        const beforeEachCode = `
describe('${describe.title}', () => {
    let service;

    beforeEach(() => {
        service = new ${describe.title}(${describe.beforeEach.constructorParamsCode});
        ${describe.beforeEach.mock}
    });

    it('should exist', () => {
        expect(service).toBeTruthy();
    });
`;
        let describeCode = '';
        for (const childDescribe of describe.describes) {
            describeCode = `${describeCode}\n${tab}${this.getDescribeMethodCode(childDescribe)}\n`;
        }
        describeCode = describeCode.slice(0, -1);
        return `${beforeEachCode}${describeCode}});`;
    }


    private static getDescribeMethodCode(describe: Describe): string {
        return `describe('${describe.title}', () => {\n${ItService.joinItsText(describe.its)}    });\n`;
    }

}
