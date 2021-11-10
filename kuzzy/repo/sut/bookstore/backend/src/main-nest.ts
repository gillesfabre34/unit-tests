import { NestFactory } from '@nestjs/core';
import { AppModule } from './app.module';

export async function bootstrap() {
  const app = await NestFactory.create(AppModule);
  await app.listen(3000);
}
// TODO : Think about how to launch the app before commenting this line
// bootstrap();
