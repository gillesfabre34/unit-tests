import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

import { ElectronService } from "../core/services";

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit {

//---------------------------------------------------------
//                    Constructor
//---------------------------------------------------------

  constructor(
    private router: Router,
    private electronService: ElectronService,
    ) {

  }

//---------------------------------------------------------
//                      Methods
//---------------------------------------------------------

  // filesPicked(files: FileList):void {
  //   Array.prototype.forEach.call(files, file => {
  //     console.log("file-----" ,file.webkitRelativePath);
  //   });
  // }

  ngOnInit(): void { }

}
