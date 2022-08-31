import { Controller, Get } from '@nestjs/common';
import { AppService } from './app.service';

@Controller()
export class AppController {
    constructor(private readonly appService: AppService) {}

    @Get('/:userId')
    pointStatus(): any {
        return {
            good_point: 0,
            bad_point: 33
        };
    }
}
