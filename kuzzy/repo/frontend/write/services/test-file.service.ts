import { GLOBAL } from '../../init/const/global.const';
import { TestFile } from '../models/test-file.model';
import { DescribeClassService } from './describe-class.service';
import { TestCaseEntity } from '../../db/entities/test-case.entity';
import { db } from '../../init/const/db.const';
import { kuzzyPath } from '../../utils/kuzzy-folder.util';
import { getFilename } from '../../utils/file-system.util';

export class TestFileService {

    static async writeTestFiles(): Promise<void> {
        const testCaseEntities: TestCaseEntity[] = await this.findTestCaseEntities();
        const testFiles: TestFile[] = this.createTestFiles(testCaseEntities);
        this.addDescribeClasses(testFiles);
        this.addDescribeMethods(testFiles);
        this.setProperties(testFiles);
        await this.write(testFiles);
    }


    private static async findTestCaseEntities(): Promise<TestCaseEntity[]> {
        return await db.connection.getRepository(TestCaseEntity)
            .createQueryBuilder('testCase')
            .leftJoinAndSelect('testCase.mock', 'mock')
            .leftJoinAndSelect('mock.classUT', 'classUT')
            .leftJoinAndSelect('classUT.fileUT', 'fileUT')
            .leftJoinAndSelect('fileUT.systemUT', 'systemUT')
            .leftJoinAndSelect('mock.mocksDependencies', 'mocksDependencies')
            .leftJoinAndSelect('mocksDependencies.classUT', 'mocksDependenciesClassUT')
            .leftJoinAndSelect('mocksDependenciesClassUT.fileUT', 'mocksDependenciesClassUTFileUT')
            .leftJoinAndSelect('testCase.statementUTs', 'statementUT')
            .leftJoinAndSelect('statementUT.methodUT', 'methodUT')
            .leftJoinAndSelect('methodUT.classUT', 'methodClassUT')
            .leftJoinAndSelect('methodClassUT.fileUT', 'methodFileUT')
            .leftJoinAndSelect('statementUT.functionUT', 'functionUT')
            .leftJoinAndSelect('functionUT.fileUT', 'functionFileUT')
            .leftJoinAndSelect('testCase.returnedValue', 'returnedValueKeyValue')
            .leftJoinAndSelect('returnedValueKeyValue.declaration', 'returnedValueKeyValueDeclaration')
            .leftJoinAndSelect('returnedValueKeyValueDeclaration.enumUT', 'returnedValueKeyValueDeclarationEnumUT')
            .leftJoinAndSelect('returnedValueKeyValueDeclaration.mock', 'returnedValueKeyValueDeclarationMock')
            .leftJoinAndSelect('returnedValueKeyValueDeclarationEnumUT.fileUT', 'returnedValueKeyValueDeclarationEnumUTFileUT')
            .leftJoinAndSelect('returnedValueKeyValue.values', 'returnedValueKeyValueValues')
            .leftJoinAndSelect('testCase.parameters', 'parameters')
            .leftJoinAndSelect('parameters.keyValue', 'parametersKeyValues')
            .leftJoinAndSelect('parametersKeyValues.values', 'parametersKeyValuesValues')
            .leftJoinAndSelect('parametersKeyValues.declaration', 'parametersKeyValuesDeclaration')
            .leftJoinAndSelect('parametersKeyValuesDeclaration.mock', 'parametersKeyValuesDeclarationMock')
            .leftJoinAndSelect('parametersKeyValuesDeclaration.enumUT', 'parametersKeyValuesDeclarationEnumUT')
            .leftJoinAndSelect('parametersKeyValuesDeclaration.classUT', 'parametersKeyValuesDeclarationClassUT')
            .where('systemUT.name = :name', { name: GLOBAL.systemUT.name })
            .getMany();
    }


    private static createTestFiles(testCaseEntities: TestCaseEntity[]): TestFile[] {
        const testFiles: TestFile[] = [];
        const fileUTPaths = new Set<string>();
        for (const testCaseEntity of testCaseEntities) {
            if (!fileUTPaths.has(testCaseEntity.fileUTPath)) {
                testFiles.push(new TestFile(testCaseEntity));
                fileUTPaths.add(testCaseEntity.fileUTPath);
            } else {
                testFiles.find(t => t.fileUTPath === testCaseEntity.fileUTPath).addTestCaseEntity(testCaseEntity);
            }
        }
        return testFiles;
    }


    private static setProperties(testFiles: TestFile[]): void {
        for (const testFile of testFiles) {
            testFile.setProperties();
        }
    }


    static testFileName(fileUTPath: string): string {
        return getFilename(fileUTPath.replace(/ts$/, 'spec.ts'));
    }


    static testFilePath(fileUTPath: string): string {
        return `${kuzzyPath(fileUTPath).slice(0, -getFilename(fileUTPath).length)}${this.testFileName(fileUTPath)}`;
        // return `${kuzzyClonePath(fileUTPath).slice(0, -getFilename(fileUTPath).length)}${this.testFileName(fileUTPath)}`;
    }


    private static addDescribeClasses(testFiles: TestFile[]): void {
        for (const testFile of testFiles) {
            testFile.addDescribeClasses();
        }
    }


    private static addDescribeMethods(testFiles: TestFile[]): void {
        DescribeClassService.generateDescribeMethods(testFiles);
    }


    private static async write(testFiles: TestFile[]): Promise<void> {
        for (const testFile of testFiles) {
            await testFile.writeFile();
        }
    }

}
