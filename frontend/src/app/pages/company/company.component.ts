import {Component, OnInit} from '@angular/core';
import {CompanyService} from "../../services/company.service";
import {first} from "rxjs";
import {Company} from "../../_model/company";

@Component({
  selector: 'app-company',
  templateUrl: './company.component.html',
  styleUrls: ['./company.component.scss']
})
export class CompanyComponent implements OnInit {

  companies: Company[] | undefined;

  constructor(private companyService: CompanyService) {
  }
  ngOnInit(): void {
    this.companyService.list(1).subscribe(resp => {
      this.companies = resp.companies;
      console.log(this.companies);
    });
  }

}
