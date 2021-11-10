import { PrimitiveType } from '../../shared/utils/primitives.util';

export class PostFileUTsDto {
    fileUTs?: PostFileUTDto[] = [new PostFileUTDto()];
    systemId ?= '';
}


export class PostFileUTDto {
    classUTs?: PostFileUTDtoClassUT[] = [new PostFileUTDtoClassUT()];
    enumUTs?: PostFileUTDtoEnumUT[] = [new PostFileUTDtoEnumUT()];
    functionUTs?: PostFileUTDtoCallerUT[] = [new PostFileUTDtoCallerUT()];
    path ?= '';
}


export class PostFileUTDtoClassUT {
    methodUTs?: PostFileUTDtoCallerUT[] = [new PostFileUTDtoCallerUT()];
    name ?= '';
    properties?: PostFileUTDtoProperty[] = [new PostFileUTDtoProperty()];
    reference ?= 0;
}


export class PostFileUTDtoCallerUT {
    name ?= '';
    numberOfStatementUTs ?= 0;
}


export class PostFileUTDtoEnumUT {
    keyValues?: Record<'key' | 'value', string>[] = [{
        key: '',
        value: ''
    }];
    name ?= '';
    primitiveType?: 'string' | 'number' = 'string';
    reference ?= 0;
}


export class PostFileUTDtoProperty {
    name ?= '';
    primitiveType?: PrimitiveType | 'unknown' = 'unknown';
    typeClassReference?: number | 'unknown' = 'unknown';
}
