import { FolderStats } from '../models/folder-stats.model';
import { FILES_STATS, NEW_FILES_STATS } from './files-stats.mock';

export const FOLDER_STATS: FolderStats = new FolderStats('myFolderPath', FILES_STATS);
export const NEW_FOLDER_STATS: FolderStats = new FolderStats('myFolderPath', NEW_FILES_STATS);
