import { Block, SourceFile } from 'ts-morph';
import { Path } from '../models/path.model';

export interface AstMock {

    sourceFile: SourceFile,
    firstBlock: Block,
    firstPath: Path

}
