import { isPrimitive } from '../../../shared/utils/primitives.util';
import { getInstanceId } from '../../../shared/utils/reflect.util';

export class InstanceService {

    static isInstance(obj: any): boolean {
        if (!obj || isPrimitive(obj)) {
            return false;
        }
        return !!getInstanceId(obj);
    }

}
