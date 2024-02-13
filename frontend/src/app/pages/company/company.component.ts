import {Component, OnInit} from '@angular/core';
import {CompanyService} from "../../services/company.service";
import {Company} from "../../_model/company";
import {PaginationInstance} from "ngx-pagination";

@Component({
  selector: 'app-company',
  templateUrl: './company.component.html',
  styleUrls: ['./company.component.scss']
})
export class CompanyComponent implements OnInit {

  companies: Company[] = [];
  // @ts-ignore
  totalItems: number;
  p:  number = 1;
  public maxSize: number = 10;
  public directionLinks: boolean = true;
  public autoHide: boolean = false;
  public responsive: boolean = false;
  public config: PaginationInstance = {
    id: 'advanced',
    itemsPerPage: 10,
    currentPage: 1
  };
  public labels: any = {
    previousLabel: 'Previous',
    nextLabel: 'Next',
    screenReaderPaginationLabel: 'Pagination',
    screenReaderPageLabel: 'page',
    screenReaderCurrentLabel: `You're on page`
  };


  constructor(private companyService: CompanyService) {
  }
  ngOnInit(): void {
    this.companyService.list(1).subscribe(resp => {
      this.companies = resp.companies;
      this.totalItems = resp.totalItems;
    });
  }
  onPageChange($event: number) {

  }

  onPageBoundsCorrection($event: number) {

  }
}
