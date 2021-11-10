import { Instance } from '../../capture/models/instance.model';
import { Constructor } from '../../../shared/types/constructor.type';
import { GLOBAL } from '../../init/const/global.const';
import { setFilePath, setInstanceId } from '../../../shared/utils/reflect.util';
import { originalPath } from '../../utils/kuzzy-folder.util';

// Creates new Instance object linked to the real object
export function CreateInstance(filePath: string) {

    return function CreateNewInstance<T extends Constructor>(constructor: T) {

        return class extends constructor {

            constructor(...args: any[]) {
                super(...args);
                const instance: Instance = this.createInstance(args);
                GLOBAL.dynamicAppState.addInstance(instance);
                GLOBAL.story.addNewSystemStateForNewInstance(instance);
            }

            createInstance(args: any[]): Instance {
                const instance = new Instance(originalPath(filePath), this, args);
                setInstanceId(this, instance.id);
                setFilePath(this, filePath);
                return instance;
            }
        }
    }
}


