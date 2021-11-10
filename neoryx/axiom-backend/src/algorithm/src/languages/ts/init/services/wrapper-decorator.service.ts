import { SpyMethod } from '../../../../agnostic/write/models/spy-method.model';
import { SpiesService } from '../../../../agnostic/init/services/spies.service';
import cloneDeep from 'clone-deep-circular-references';
import * as chalk from 'chalk';

const DEBUG_LOG_WRAPPER = false;

export function Wrapper(filePath: string) {
    return function (target: any, propertyKey: string, descriptor: PropertyDescriptor) {
        if (DEBUG_LOG_WRAPPER) {
            console.log(chalk.magentaBright('WRAPPER DECORATOR'));
            console.log(chalk.blueBright('Target : '), target);
            console.log(chalk.blueBright('Property : '), propertyKey);
            console.log(chalk.blueBright('Descriptor : '), descriptor);
        }
        let originalMethod = descriptor.value;
        descriptor.value = function (...args: any[]) {
            const spiedObject = cloneDeep(SpiesService.getInstance().spiedObject);
            let result = originalMethod.apply(this, args);
            const spyOnService = SpiesService.getInstance();
            const spyMethod: SpyMethod = spyOnService.getSpyMethod(filePath, target?.constructor?.name, propertyKey);
            spyMethod.spiedObject = spiedObject;
            spyMethod.calledWith.push(...args);
            spyMethod.returnedValues.push(result);
            if (DEBUG_LOG_WRAPPER) {
                console.log(chalk.blueBright('Args'), args);
                console.log(chalk.blueBright('SpyMethod'), spyMethod);
            }
            return result;
        }
    }
}
