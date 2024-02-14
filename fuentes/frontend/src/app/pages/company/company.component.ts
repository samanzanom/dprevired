import {Component, OnInit} from '@angular/core';
import {CompanyService} from "../../services/company.service";
import {Company} from "../../_model/company";
import {PaginationInstance} from "ngx-pagination";
import {NzModalRef, NzModalService} from 'ng-zorro-antd/modal';
import {LoginService} from "../../services/login.service";

@Component({
  selector: 'app-company',
  templateUrl: './company.component.html',
  styleUrls: ['./company.component.scss']
})
export class CompanyComponent implements OnInit {
  confirmModal?: NzModalRef;

  username: string | undefined = "";
  companies: Company[] = [];
  // @ts-ignore
  totalItems: number;
  p:  number = 1;
  public maxSize: number = 3;
  public directionLinks: boolean = true;
  public autoHide: boolean = false;
  public responsive: boolean = false;
  public config: PaginationInstance = {
    id: 'advanced',
    itemsPerPage: 3,
    currentPage: 1,
    totalItems: 0
  };
  public labels: any = {
    previousLabel: 'Previous',
    nextLabel: 'Next',
    screenReaderPaginationLabel: 'Pagination',
    screenReaderPageLabel: 'page',
    screenReaderCurrentLabel: `You're on page`
  };
  isVisible = false;
  isVisibleEdit: boolean = false;
  isConfirmLoading = false;
  companyName: string = "";
  rut: string = "";
  errorRS: boolean = false;
  errorRut: boolean = false;
  errorRSMsg: string = "";
  errorRutMsg: string = "";
  editableCompany: Company  = new Company();

  constructor(private companyService: CompanyService,
              private modal: NzModalService,
              private loginService : LoginService,) {
  }
  ngOnInit(): void {
    let user = this.loginService.userValue;
    this.username = user?.username;
    this.loadData(1);
  }

  loadData(page: number) {

    this.companyService.list(page, this.config.itemsPerPage).subscribe({
      next: (resp) => {
        this.companies = resp.companies;
        this.totalItems = resp.totalItems;
        this.config.totalItems = resp.totalItems;
        this.config.currentPage = resp.currentPage;
      },
      error: (error) => {
        console.log(error);
      }
    });
  }
  onPageChange($event: number) {
      this.loadData($event);
  }

  onPageBoundsCorrection($event: number) {
    this.loadData($event);
  }

  handleOk(): void {
    this.isConfirmLoading = true;
    if (this.companyName?.trim() === '') {
      this.errorRS = true;
      this.isConfirmLoading = false;
      this.errorRSMsg = "La razón social es requerido.";
      return;
    } else if (this.companyName?.length < 6 || this.companyName?.length > 50) {
      this.errorRS = true;
      this.isConfirmLoading = false;
      this.errorRSMsg = "El nombre de la empresa debe tener entre 6 y 50 caracteres.";
      return;
    }

    // Validación del RUT
    if (this.rut?.trim() === '') {
      this.errorRut = true;
      this.isConfirmLoading = false;
      this.errorRutMsg = "El RUT es requerido.";
      return;
    } else if (!this.validarRut(this.rut)) {
      this.errorRut = true;
      this.isConfirmLoading = false;
      this.errorRutMsg = "El formato del RUT no es válido. El rut debe contener puntos y guion";
      return;
    }

    this.companyService.add({ rut: this.rut, companyName: this.companyName })
      .subscribe({
        next: (resp) => {
          if (resp.body.error) {
            this.modal.error({
              nzTitle: 'Tuvimos un problema',
              nzContent: resp.body.description
            });
          } else {
            this.modal.success({
              nzTitle: '¡Bien!',
              nzContent: 'Hemos creado la nueva empresa'
            });
            this.isVisible = false;
            this.isConfirmLoading = false;
            this.loadData(this.config.currentPage);
          }
        },
        error: (error) => {
          if(error === 'Duplicated Element') {
            this.modal.error({
              nzTitle: 'Error',
              nzContent:'El rut ya esta registrado'
            });
          } else {
            this.modal.error({
              nzTitle: 'Error',
              nzContent: error || 'Algo salió mal al intentar crear la empresa'
            });
          }

          this.isConfirmLoading = false;
        }
      });
  }

  validarRut(rutCompleto: string): boolean {
    if (!/.*-\dK?$/i.test(rutCompleto)) {
      return false; // No tiene un "-" seguido por un dígito o una K al final
    }

    // Eliminar puntos y guion para cálculo del dígito verificador
    const rutSinFormato = rutCompleto.replace(/\./g, '').replace('-', '');
    const cuerpo = parseInt(rutSinFormato.slice(0, -1), 10);
    let dv = rutSinFormato.slice(-1).toUpperCase();

    // Calcular Dígito Verificador
    let suma = 0;
    let multiplo = 2;

    for (let i = 1; i <= cuerpo.toString().length; i++) {
      // @ts-ignore
      const index = multiplo * rutSinFormato.charAt(cuerpo.toString().length - i);
      suma = suma + index;
      if (multiplo < 7) {
        multiplo = multiplo + 1;
      } else {
        multiplo = 2;
      }
    }

    const dvEsperado = 11 - (suma % 11);
    dv = dv === 'K' ? '10' : dv;
    dv = dv === '0' ? '11' : dv;

    return dvEsperado.toString() === dv;
  }

  handleCancel(): void {
    this.isVisible = false;
  }

  showModal1(): void {
    this.isVisible = true;
  }

  eliminar(c: Company) {
    this.confirmModal = this.modal.confirm({
      nzTitle: '¿Desea eliminar esta empresa?',
      nzContent: 'La eliminación sera de forma permanente',
      nzOnOk: () =>
        this.companyService.delete(c.id)
          .subscribe({
            next: (resp) => {
              if (resp.status !== 204) {
                this.modal.error({
                  nzTitle: 'Tuvimos un problema',
                  nzContent: 'No se ha eliminado la empresa'
                });
              } else {
                this.modal.success({
                  nzTitle: '¡Bien!',
                  nzContent: 'Hemos eliminado la empresa'
                });
                this.loadData(this.config.currentPage);
              }
            },
            error: (error) => {
              this.modal.error({
                nzTitle: 'Error',
                nzContent: error || 'Algo salió mal al intentar crear la empresa'
              });
              this.isConfirmLoading = false;
            }
          })
    });
  }

  editar(c: Company) {
    this.companyService.get(c.id).subscribe({
      next: (resp) => {
        if(resp.status == 200) {
          this.editableCompany = resp.body;
          this.isVisibleEdit= true;
        } else {
          this.modal.error({
            nzTitle: 'Error',
            nzContent: 'Algo salio mal al obtener los datos de la empresa'
          });
        }
      },
      error: (error) => {
        console.log(error);
      }
    });

  }

  logout() {
    this.loginService.logout();
  }

  handleCancelEdit() {
    this.isVisibleEdit = false;
  }

  handleOkEdit() {
    this.isConfirmLoading = true;
    if (this.editableCompany.companyName?.trim() === '' || this.editableCompany.companyName == undefined) {
      this.errorRS = true;
      this.isConfirmLoading = false;
      this.errorRSMsg = "La razón social es requerido.";
      return;
    } else if (this.editableCompany.companyName?.length < 6 || this.editableCompany.companyName?.length > 50) {
      this.errorRS = true;
      this.isConfirmLoading = false;
      this.errorRSMsg = "El nombre de la empresa debe tener entre 6 y 50 caracteres.";
      return;
    }

    // Validación del RUT
    if (this.editableCompany.rut == undefined || this.editableCompany.rut?.trim() === '') {
      this.errorRut = true;
      this.isConfirmLoading = false;
      this.errorRutMsg = "El RUT es requerido.";
      return;
    } else if (!this.validarRut(this.editableCompany.rut)) {
      this.errorRut = true;
      this.isConfirmLoading = false;
      this.errorRutMsg = "El formato del RUT no es válido. El rut debe contener puntos y guion";
      return;
    }

    this.companyService.edit( this.editableCompany.id,  this.editableCompany.rut, this.editableCompany.companyName)
      .subscribe({
        next: (resp) => {
          if (resp.body.error) {
            this.modal.error({
              nzTitle: 'Tuvimos un problema',
              nzContent: resp.body.description
            });
          } else {
            this.modal.success({
              nzTitle: '¡Bien!',
              nzContent: 'Hemos actualizdo empresa'
            });
            this.isVisibleEdit = false;
            this.isConfirmLoading = false;
            this.loadData(this.config.currentPage);
          }
        },
        error: (error) => {

          this.modal.error({
            nzTitle: 'Error',
            nzContent: error || 'Algo salió mal al intentar crear la empresa'
          });

          this.isConfirmLoading = false;
        }
      });
  }
}
