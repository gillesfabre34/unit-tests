import { GLOBAL } from '../../init/const/global.const';
import { Instance } from './instance.model';
import { InstanceService } from '../services/instance.service';
import { MockDependency } from './mock-dependency.model';
import { KeyValue } from '../../flag/models/key-value.model';
import { MockEntity } from '../../db/entities/mock.entity';
import { ImportDefault } from '../../write/models/import-default.model';
import { kuzzyPath } from '../../utils/kuzzy-folder.util';
import { clone } from '../../../shared/utils/objects.util';
import { flat } from '../../../shared/utils/arrays.util';
import { isCapital } from '../../../shared/utils/strings.util';
import { getInstanceId } from '../../../shared/utils/reflect.util';
import { prettify } from '../../../shared/utils/prettify.util';
import { classNameToFileName, getFolderPath, getModuleSpecifier } from '../../utils/file-system.util';

export class Mock {

    classId: number = undefined;
    className: string = undefined;
    constructorArguments: string[] = [];
    entity: MockEntity = undefined;
    exportCode: string = undefined;
    fileUTPath: string = undefined;
    id: number = undefined;
    idInClass: number = undefined;
    instanceId: number = undefined;
    instanciationCode: string = '';
    mocksDependencies: MockDependency[] = [];
    mockFilePath: string = undefined;
    mockObject: object = undefined;
    mutationsKeyValues: KeyValue[] = [];
    mutationsCode: string = undefined;
    name: string = undefined;


    constructor(instance: Instance) {
        this.className = instance.className;
        this.instanceId = instance.id;
        this.fileUTPath = kuzzyPath(instance.fileUTPath);
        // this.fileUTPath = kuzzyClonePath(instance.fileUTPath);
        this.mockObject = clone(instance.concreteObject);
        this.setId();
        this.setIdInClass();
        this.setMockFilePath(instance);
        this.setName();
        this.setConstructorArguments(instance);
        this.addMockDependenciesForConstructorArguments();
        this.setInstantiationCode();
        this.setMutationsCode();
        this.setExportCode();
    }


    get associatedMockDependency(): MockDependency {
        return new MockDependency(this.name, this.className, this.mockFilePath, this.fileUTPath);
    }


    get code(): string {
        return `${this.instanciationCode}\n${this.mutationsCode}\n${this.exportCode}\n`;
    }


    get importDefaultMockFile(): ImportDefault {
        return new ImportDefault(this.name, getModuleSpecifier(this.mockFilePath));
    }


    addMockDependency(mockDependency: MockDependency): void {
        if (mockDependency && !this.mocksDependencies.map(m => m.mockName === mockDependency.mockName)) {
            this.mocksDependencies.push(mockDependency);
        }
    }


    private setId() {
        let ids: number[] = flat(GLOBAL.story.systemStates.map(a => a.mocks.map(e => e.id))) ?? [];
        this.id = ids.length > 0 ? Math.max(...ids) + 1 : 0;
    }


    private setIdInClass(): void {
        const maxIdsInClass: number[] = this.getMocks(this.className).map(m => m.idInClass);
        this.idInClass = maxIdsInClass.length > 0 ? Math.max(...maxIdsInClass) + 1 : 0;
    }


    private getMocks(className: string): Mock[] {
        return GLOBAL.lastSystemState.mocks.filter(m => m.className === className);
    }


    private setName(): void {
        let name = this.className[0].toUpperCase();
        for (let i = 1; i < this.className.length; i++) {
            name = isCapital(this.className[i]) ? `${name}_${this.className[i]}` : `${name}${this.className[i]}`;
        }
        name = name.replace(/-/g, '_')
            .replace('.', '_')
            .toUpperCase()
        this.name = `${name}_${this.idInClass}`;
    }


    private setMockFilePath(instance: Instance): void {
        let name = '';
        if (instance.className) {
            name = `${classNameToFileName(instance.className)}`;
        } else {
            name = getModuleSpecifier(instance.fileUTPath);
        }
        this.mockFilePath = kuzzyPath(`${getFolderPath(instance.fileUTPath).replace('flag', 'clone')}/${name}-${this.idInClass.toString()}.mock.ts`);
        // this.mockFilePath = kuzzyClonePath(`${getFolderPath(instance.fileUTPath).replace('flag', 'clone')}/${name}-${this.idInClass.toString()}.mock.ts`);
    }


    private setConstructorArguments(instance: Instance): void {
        for (const argument of instance.constructorArguments) {
            if (InstanceService.isInstance(argument)) {
                const previousMock: Mock = GLOBAL.lastSystemState.getMockWithInstanceId(getInstanceId(argument));
                if (previousMock) {
                    this.constructorArguments.push(previousMock.name);
                    this.mocksDependencies.push(previousMock.associatedMockDependency);
                } else {
                    const newMock: Mock = new Mock(GLOBAL.dynamicAppState.instances.find(i => i.id === getInstanceId(argument)));
                    this.constructorArguments.push(newMock.name);
                    GLOBAL.lastSystemState.addMock(newMock);
                }
            } else {
                this.constructorArguments.push(prettify(argument));
            }
        }
    }

    // TODO
    private addMockDependenciesForConstructorArguments(): void {

    }


    private setInstantiationCode(): void {
        this.instanciationCode = `const _${this.name.toLowerCase()} = new ${this.className}(${this.constructorArguments.join(', ')});`;
    }


    setMutationsCode(): void {
        let code = '';
        for (const keyValue of this.mutationsKeyValues) {
            code = `${code}_${this.name.toLowerCase()}.${keyValue.key} = `;
            const value: any = keyValue.value;
            if (Array.isArray(value)) {
                code = `${code}[`;
                if (value.length > 0) {
                    for (const element of value) {
                        code = element instanceof MockDependency ? `${code}${element.mockName}, ` : `${code}${element}, `;
                        // code = element instanceof MockDependency ? `${code}${element.mockName}, ` : `${code}${prettify(element)}, `;
                    }
                    code = code.slice(0, -2);
                }
                code = `${code}];\n`;
            } else {
                code = value instanceof MockDependency ? `${code}${value.mockName};\n` : `${code}${prettify(value)};\n`;
            }
        }
        this.mutationsCode = code;
    }


    private setExportCode(): void {
        this.exportCode = `export const ${this.name} = _${this.name.toLowerCase()};`;
    }

}
