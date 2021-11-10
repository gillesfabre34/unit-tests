import { BeforeEach } from '../models/before-each.model';

const beforeEach = new BeforeEach();
beforeEach.constructorParams = [undefined];
beforeEach.mock = `service.externalService = {
            helloMethod: () => {},
            doSomething: () => {}
        }`;

export const BEFORE_EACH_MOCK = beforeEach;

export const BEFORE_EACH_MOCK_OBJECT: {service: any} = {
    service: {
        helloMethod: () => {},
        doSomething: () => {}
    }
};
