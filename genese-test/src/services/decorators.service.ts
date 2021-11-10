import { IoService } from './io.service';
import { IoClass } from '../models/io-class.model';
import { IoMethod } from '../models/io-method.model';

export function IO(target: any, propertyKey: string, descriptor: PropertyDescriptor) {
    let originalMethod = descriptor.value;
    descriptor.value = function (...args: any[]) {
        let result = originalMethod.apply(this, args);
        const ioClass = new IoClass();
        ioClass.className = '';
        const ioMethod = new IoMethod();
        ioMethod.ios.push({
            input: args,
            output: result
        });
        ioClass.methods.push(ioMethod);
        IoService.ioClasses.push(ioClass);
        return result;
    }
}
