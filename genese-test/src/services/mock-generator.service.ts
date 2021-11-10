import { TsTypes } from '../interfaces/ts-types.type';

export class MockGeneratorService {

    // TODO : implement for arrays
    static getMock(type: TsTypes): string {
        switch (type) {
            case 'string':
                return `'a'`;
            case 'number':
                return `1`;
            case 'boolean':
                return 'true';
            default:
                return '';
        }
    }

}
