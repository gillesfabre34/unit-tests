import { CoverageService } from '../capture/services/coverage.service';

export function parse(statementId: number, methodName: string, className: string, fileUTPath: string): void {
    CoverageService.parse(statementId, methodName, className, fileUTPath);
}
