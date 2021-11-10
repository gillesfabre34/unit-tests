import { BugType } from '../../../../agnostic/stats/enums/bug-type.enum';
import { Bug } from '../models/bug.model';

export const BUG_EXECUTION_ERROR: Bug = new Bug(BugType.EXECUTION_ERROR);
export const BUG_OUT_OF_TIME: Bug = new Bug(BugType.OUT_OF_TIME);
