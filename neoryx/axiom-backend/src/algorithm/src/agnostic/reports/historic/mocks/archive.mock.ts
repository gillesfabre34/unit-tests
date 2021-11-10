import { Archive } from '../models/archive.model';
import { NEW_REPORT_STATS, REPORT_STATS } from '../../dashboard/mocks/report-stats.mock';

export const ARCHIVE: Archive = new Archive(1604599193026, '2020-11-05T17:59:53.026Z', REPORT_STATS);
export const NEW_ARCHIVE: Archive = new Archive(1604599193027, '2020-11-05T17:59:53.027Z', NEW_REPORT_STATS);
