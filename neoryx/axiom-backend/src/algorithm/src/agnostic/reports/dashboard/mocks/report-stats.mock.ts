import { ReportStats } from '../models/report-stats.model';
import { FOLDER_STATS, NEW_FOLDER_STATS } from './folder-stats.mock';

export const REPORT_STATS: ReportStats = new ReportStats(undefined, undefined, FOLDER_STATS);
export const NEW_REPORT_STATS: ReportStats = new ReportStats(undefined, undefined, NEW_FOLDER_STATS);
