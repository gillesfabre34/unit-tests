import { MethodStats } from '../models/method-stats.model';
import { BUG_EXECUTION_ERROR } from '../../core/bugs/mocks/bug.mock';

export const MY_METHOD_STATS: MethodStats = new MethodStats('myMethod', 'myClassName', false, 3, [0, 1, 2], []);
export const MY_OTHER_METHOD_STATS: MethodStats = new MethodStats('myOtherMethod', 'myClassName', false, 2, [], [BUG_EXECUTION_ERROR]);
export const MY_OTHER_FILE_METHOD_STATS: MethodStats = new MethodStats('myOtherFileMethod', 'myOtherFileClassName', false, 7, [0, 1, 2, 3], []);

export const METHODS_STATS: MethodStats[] = [MY_METHOD_STATS, MY_OTHER_METHOD_STATS];
export const METHODS_OTHER_FILE_STATS: MethodStats[] = [MY_OTHER_FILE_METHOD_STATS];





export const NEW_MY_METHOD_STATS: MethodStats = new MethodStats('myMethod', 'myClassName', false, 3);
export const NEW_MY_OTHER_FILE_METHOD_STATS: MethodStats = new MethodStats('myOtherFileMethod', 'myOtherFileClassName', false, 7);
export const NEW_MY_OTHER_FILE_OTHER_METHOD_STATS: MethodStats = new MethodStats('myOtherFileOtherMethod', 'myOtherFileClassName', false, 2);

export const NEW_METHODS_STATS: MethodStats[] = [NEW_MY_METHOD_STATS];
export const NEW_METHODS_OTHER_FILE_STATS: MethodStats[] = [NEW_MY_OTHER_FILE_METHOD_STATS, NEW_MY_OTHER_FILE_OTHER_METHOD_STATS];
