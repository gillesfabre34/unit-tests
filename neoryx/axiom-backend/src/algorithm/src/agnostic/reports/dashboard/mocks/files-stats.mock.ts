import { FileStats } from '../models/file-stats.model';
import {
    METHODS_OTHER_FILE_STATS,
    METHODS_STATS,
    NEW_METHODS_OTHER_FILE_STATS,
    NEW_METHODS_STATS
} from './method-stats.mock';

export const FILE_STATS: FileStats = new FileStats('myFilePath', 5, [], METHODS_STATS);
export const OTHER_FILE_STATS: FileStats = new FileStats('myOtherFilePath', 7, [], METHODS_OTHER_FILE_STATS);
export const FILES_STATS: FileStats[] = [FILE_STATS, OTHER_FILE_STATS];

export const NEW_FILE_STATS: FileStats = new FileStats('myFilePath', 5, [], NEW_METHODS_STATS);
export const NEW_OTHER_FILE_STATS: FileStats = new FileStats('myOtherFilePath', 7, [], NEW_METHODS_OTHER_FILE_STATS);
export const NEW_FILES_STATS: FileStats[] = [NEW_FILE_STATS, NEW_OTHER_FILE_STATS];
