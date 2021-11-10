import { IoMethod } from './io-method.model';
import { IoService } from '../services/io.service';

export class IoClass {

    className: string = undefined;
    methods: IoMethod[] = [];


    log(): void {
        IoService.log(this);
    }

}
