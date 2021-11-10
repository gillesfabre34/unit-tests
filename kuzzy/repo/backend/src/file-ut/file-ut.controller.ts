import { Body, Controller, Delete, Get, HttpException, Post } from '@nestjs/common';
import { FileUtService } from './file-ut.service';
import * as chalk from 'chalk';
import { DeleteFileUtPathsDto } from '../../../dtos/file-ut/delete-file-ut-paths.dto';
import { plural } from '../../../shared/utils/strings.util';
import { GeneseMapper } from 'genese-mapper';
import { PostFileUTsDto } from '../../../dtos/file-ut/post-file-uts.dto';
import { Mapper } from '@genese/mapper';
import { GLOBAL } from '../../../frontend/init/const/global.const';

@Controller('file-ut')
export class FileUtController {

    constructor(private fileUtService: FileUtService) { }

    @Get('/paths')
    async getFileUTPaths(): Promise<string[]> {
        const fileUTPaths: string[] = await this.fileUtService.getFileUTPaths();
        console.log(chalk.greenBright('Get fileUTs : '), fileUTPaths?.length);
        return fileUTPaths;
    }


    @Delete('/paths')
    async deleteFileUTs(@Body() body: any): Promise<string> {
        if (!Array.isArray(body?.fileUTPaths)) {
            throw new HttpException('Bad DTO : fileUTPaths is not an array', 400);
        }
        if (body.fileUTPaths.length === 0) {
            return 'No file to delete';
        }
        const deleteFileUTPathsDto: DeleteFileUtPathsDto = await Mapper.create(DeleteFileUtPathsDto, body);
        await this.fileUtService.deleteFileUTPaths(deleteFileUTPathsDto.fileUTPaths);
        console.log(chalk.greenBright('Deleted files : '), deleteFileUTPathsDto.fileUTPaths?.length);
        return `${deleteFileUTPathsDto.fileUTPaths?.length} ${plural('fileUT', deleteFileUTPathsDto.fileUTPaths?.length)} deleted`;
    }


    @Post()
    async postFileUTs(@Body() body: any): Promise<string> {
        console.log(chalk.redBright('POST PATHSSSS BODY'), body);
        GLOBAL.start = Date.now();
        GLOBAL.logDuration('Start mapping');
        const postFileUTsDto: PostFileUTsDto = await Mapper.create(PostFileUTsDto, body);
        GLOBAL.logDuration('End of mapping');
        console.log(chalk.greenBright('POST PATHSSSS MAPPED'), postFileUTsDto?.fileUTs);
        console.log(chalk.blueBright('FileUts to save : '), postFileUTsDto.fileUTs.length);
        if (!Array.isArray(body?.fileUTs)) {
            throw new HttpException('No FileUTs in DTO', 400);
        }
        const numberOfSavedFiles: number = await this.fileUtService.saveFileUTs(postFileUTsDto);
        console.log(chalk.greenBright('Saved files : '), numberOfSavedFiles);
        return `${numberOfSavedFiles} ${plural('fileUT', numberOfSavedFiles)} saved`;
    }
}
