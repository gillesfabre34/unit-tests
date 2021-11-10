import { SystemUtDataService } from '../data-services/system-ut-data.service';

async function dropBookStoreSystemUT() {
    await SystemUtDataService.dropSystemUT('cats');
}

dropBookStoreSystemUT();
