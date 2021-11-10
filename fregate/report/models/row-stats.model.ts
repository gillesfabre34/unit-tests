export class RowStats {

    bugs = '';
    color = 'black';
    codeCoverage = '';
    duration = '';
    fileName = '';
    methodName = '';
    numberOfTests = 0;

    constructor(bugs = '', codeCoverage = '', duration = '', fileName = '', methodName = '', numberOfTests = 0, color = 'black') {
        this.bugs = bugs;
        this.codeCoverage = codeCoverage;
        this.duration = duration;
        this.fileName = fileName;
        this.methodName = methodName;
        this.numberOfTests = numberOfTests;
        this.color = color;
    }
}
