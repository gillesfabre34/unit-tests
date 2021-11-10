import { Module } from '@nestjs/common';
import { TestCaseController } from './test-case.controller';
import { TestCaseService } from './test-case.service';

@Module({
  controllers: [TestCaseController],
  providers: [TestCaseService]
})
export class TestCaseModule {}
