import { InitService } from './init/services/init.service';
import { MainProcessLanguage } from '../../agnostic/init/interfaces/main-language-file.interface';
import { FlagsService } from './init/services/flags.service';

export class MainTs implements MainProcessLanguage {


    newFlagsService(): FlagsService {
        return new FlagsService();
    }


    newInitService(): InitService {
        return new InitService();
    }

}
