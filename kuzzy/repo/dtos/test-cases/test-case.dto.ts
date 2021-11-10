import { PrimitiveType } from '../../shared/utils/primitives.util';

export class TestCaseDto {

    mock?: PostTestCaseDtoMock = new PostTestCaseDtoMock();
    parameters?: PostTestCaseDtoParameter[] = [new PostTestCaseDtoParameter()];
    returnedValue?: PostTestCaseDtoKeyValue = new PostTestCaseDtoKeyValue();
    statementsIds?: number[] = [0];

}


export class PostTestCaseDtoMock {

    classId ?= 0;
    code ?= '';
    constructorArguments?: PostTestCaseDtoParameter[] = [new PostTestCaseDtoParameter()];
    mockFilePath ?= '';
    mocksDependencies?: PostTestCaseDtoMock[] = undefined;

}


export class PostTestCaseDtoParameter {

    index ?= 0;
    keyValue?: PostTestCaseDtoKeyValue = new PostTestCaseDtoKeyValue();

}


export class PostTestCaseDtoKeyValue {

    declarationId ?= 0;
    isArray ?= false;
    key ?= '';
    primitiveType ?= '';
    values?: string[] = [''];

}
