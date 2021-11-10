import { Module } from '@nestjs/common';
import { AppController } from './app.controller';
import { AppService } from './app.service';
import { SystemUtModule } from './system-ut/system-ut.module';
import { FileUtModule } from './file-ut/file-ut.module';
import { TestCaseModule } from './test-case/test-case.module';

@Module({
  imports: [FileUtModule, SystemUtModule, TestCaseModule],
  controllers: [AppController],
  providers: [AppService],
})
export class AppModule {}
