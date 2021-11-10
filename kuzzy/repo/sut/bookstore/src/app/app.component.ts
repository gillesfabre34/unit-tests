import { Component } from '@angular/core';
import { environment } from '../environments/environment';
import { GeneseEnvironmentService } from 'genese-angular';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  title = 'genese-demo';


  constructor(geneseEnvironmentService: GeneseEnvironmentService) {
      geneseEnvironmentService.setEnvironment(environment.genese);
  }
}
