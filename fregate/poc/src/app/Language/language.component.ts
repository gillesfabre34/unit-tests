import { Component, OnInit } from '@angular/core';

import { ElectronService } from "../core/services";


@Component({
  selector: 'app-language',
  templateUrl: './language.component.html',
  styleUrls: ['./language.component.scss']
})
export class LanguageComponent implements OnInit {


    directoryPath: String;
    configFilePath: String;
//---------------------------------------------------------
//                    Constructor
//---------------------------------------------------------

  constructor(
    private electronService: ElectronService,
    ) { }


    ngOnInit(): void { }

//---------------------------------------------------------
//                    Methods
//---------------------------------------------------------



    //TODO On windows and linux the selector cannot be both file and directory

  openDirectory():void {
    let path = this.electronService.dialog.showOpenDialog({
      properties: ['openDirectory', 'openFile']
    }).then(a => {
        console.log(a.filePaths[0]);
        this.directoryPath = a.filePaths[0];
    });
  }

  openConfigFile():void {
    let path = this.electronService.dialog.showOpenDialog({
        properties: ['openFile'],
        filters: [{
            name: "config",
            extensions : ['json']
        }]
    }).then(a => {
        console.log(a.filePaths);
        this.configFilePath = a.filePaths[0];

    });
  }




}
