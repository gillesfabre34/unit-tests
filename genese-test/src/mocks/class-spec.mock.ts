import { ClassSpec } from '../models/class-spec.model';
import { DESCRIBE_MOCK } from './describe.mock';
import { IMPORT_LINE_MOCK } from './import-lines.mock';

const classSpec = new ClassSpec();
classSpec.className = 'IsNotFalsyService';
classSpec.describe = DESCRIBE_MOCK;
classSpec.imports = IMPORT_LINE_MOCK;

export const CLASS_SPEC_MOCK: ClassSpec = classSpec;
