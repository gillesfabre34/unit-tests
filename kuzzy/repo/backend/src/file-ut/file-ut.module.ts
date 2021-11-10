import { Module } from '@nestjs/common';
import { FileUtController } from './file-ut.controller';
import { FileUtService } from './file-ut.service';

@Module({
  controllers: [FileUtController],
  providers: [FileUtService]
})
export class FileUtModule {}
