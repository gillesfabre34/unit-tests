import { FileUT } from '../../frontend/system/models/file-ut.model';
import { GLOBAL } from '../../frontend/init/const/global.const';
import { originalPath } from '../../frontend/utils/kuzzy-folder.util';
import { StatementUT } from '../../frontend/system/models/statement-ut.model';


export function getFileUT(filePath: string): FileUT {
    return GLOBAL.systemUT.filesUts.find(f => f.path === originalPath(filePath));
}


export function getMethodStatementUTs(methodName: string, className: string, fileUTPath: string): StatementUT[] {
    return getFileUT(originalPath(fileUTPath)).statementUTs.filter(s => s.methodOrFunctionName === methodName && s.className === className);
}


export function getStatementUT(statementId: number, methodName: string, className: string, fileUTPath: string): StatementUT {
    return getFileUT(originalPath(fileUTPath)).statementUTs.find(s => s.statementIndex === statementId && s.methodOrFunctionName === methodName && s.className === className);
}
