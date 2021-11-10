import { SpyMethod } from '../../../../agnostic/write/models/spy-method.model';
import { SpiesService } from '../../../../agnostic/init/services/spies.service';
import cloneDeep from 'clone-deep-circular-references';

export function Wrapper(filePath: string) {
    return function (target: any, propertyKey: string, descriptor: PropertyDescriptor) {
        let originalMethod = descriptor.value;
        descriptor.value = function (...args: any[]) {
            const spiedObject = cloneDeep(SpiesService.getInstance().spiedObject);
            let result = originalMethod.apply(this, args);
            const spyOnService = SpiesService.getInstance();
            const spyMethod: SpyMethod = spyOnService.getSpyMethod(filePath, target?.constructor?.name, propertyKey);
            spyMethod.spiedObject = spiedObject;
            spyMethod.calledWith.push(...args);
            spyMethod.returnedValues.push(result);
            return result;
        }
    }
}
