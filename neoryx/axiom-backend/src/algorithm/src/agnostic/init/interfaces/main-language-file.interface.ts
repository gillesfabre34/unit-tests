import { AgnosticInitService } from '../services/agnostic-init.service';
import { AgnosticFlagsService } from '../services/agnostic-flags.service';

export interface MainProcessLanguage {
    newFlagsService: () => AgnosticFlagsService;
    newInitService: () => AgnosticInitService;
}
