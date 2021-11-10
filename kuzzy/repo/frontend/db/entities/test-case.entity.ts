import { BaseEntity, Entity, JoinColumn, ManyToOne, OneToMany, OneToOne, PrimaryGeneratedColumn } from "typeorm";
import { MockEntity } from './mock.entity';
import { StatementUTEntity } from './statement-ut.entity';
import { ImportLine } from '../../write/models/import-line.model';
import { ImportLineService } from '../../write/services/import-line.service';
import { KeyValueEntity } from './key-value.entity';
import { ParameterEntity } from './parameter.entity';
import { getModuleSpecifier } from '../../utils/file-system.util';

@Entity()
export class TestCaseEntity extends BaseEntity {

    @PrimaryGeneratedColumn()
    id: number;

    @ManyToOne(() => MockEntity, mock => mock.testCases, { onDelete: 'CASCADE' })
    @JoinColumn()
    mock: MockEntity;

    @OneToMany(() => ParameterEntity, parameter => parameter.testCase, { cascade: true })
    @JoinColumn()
    parameters: ParameterEntity[];

    @OneToOne(() => KeyValueEntity, keyValue => keyValue.testCaseReturnedValue, { cascade: true })
    @JoinColumn()
    returnedValue: KeyValueEntity;

    @OneToMany(() => StatementUTEntity, statementUT => statementUT.testCase, { cascade: true })
    @JoinColumn()
    statementUTs: StatementUTEntity[];

    importLines: ImportLine[] = [];


    get fileUTPath(): string {
        return this.statementUTs?.[0]?.path;
    }


    get isAsync(): boolean {
        const firstStatementUT: StatementUTEntity = this.statementUTs?.[0];
        return firstStatementUT?.methodUT?.isAsync || firstStatementUT?.functionUT?.isAsync;
    }


    get isVoid(): boolean {
        const firstStatementUT: StatementUTEntity = this.statementUTs?.[0];
        return firstStatementUT?.methodUT?.isVoid || firstStatementUT?.functionUT?.isVoid;
    }


    get methodOrFunctionName(): string {
        return this.statementUTs?.[0]?.methodOrFunctionName;
    }


    setImportLines(): void {
        const importLines: ImportLine[] = [new ImportLine(getModuleSpecifier(this.mock.path), [this.mock.name])];
        this.addMockDependenciesImports(importLines);
        this.addReturnedValueImports(importLines);
        this.importLines = importLines;
    }


    private addMockDependenciesImports(importLines: ImportLine[]): void {
        for (const mockDependency of this.mock.mocksDependencies ?? []) {
            ImportLineService.addImport(mockDependency.importDefaultClassUT, importLines);
            ImportLineService.addImport(mockDependency.importDefaultMockFile, importLines);
        }
    }


    private addReturnedValueImports(importLines: ImportLine[]): void {
        if (this.returnedValue?.importDefault) {
            ImportLineService.addImport(this.returnedValue.importDefault, importLines);
        }
    }

}
