import { Module } from '@nestjs/common';
import { SystemUtController } from './system-ut.controller';
import { SystemUtService } from './system-ut.service';

@Module({
  controllers: [SystemUtController],
  providers: [SystemUtService]
})
export class SystemUtModule {}
