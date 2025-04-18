import { Component } from '@angular/core';
import {ToastrService} from 'ngx-toastr';
import {AuthService} from '../../core/services/auth.service';
import {Router} from '@angular/router';
import {UtilsService} from '../../shared/utils/utils.service';
import {ICON_EYE, ICON_EYE_CROSS} from '../../shared/utils/icon';
import {LoginRequest} from '../../core/models/login.model';
import {SafeHtmlPipe} from '../../shared/pipes/safe-html.pipe';
import {FormsModule} from '@angular/forms';
import {NgIf} from '@angular/common';
import {LOCAL_AUTHORITIES_KEY} from '../../constants/authorities.constants';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    SafeHtmlPipe,
    FormsModule,
    NgIf
  ],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss'
})
export class LoginComponent {
  isHiddenPassword: boolean = true;
  isRememberMe: boolean = false;
  loginRequest: LoginRequest = {
    username: '',
    password: '',
    rememberMe: false,
    deviceId: 'w498ss78g8gdf'
  };

  constructor(
    private router: Router,
    private toastr: ToastrService,
    private authService: AuthService,
    private utilsService: UtilsService
  ) {}

  login() {
    if (!this.loginRequest.username) {
      this.toastr.error('Tên đăng nhập không được để trống', 'Thông báo');
      return;
    }

    if (!this.loginRequest.password) {
      this.toastr.error('Mật khẩu không được để trống', 'Thông báo');
      return;
    } else if (!this.utilsService.validatePassword(this.loginRequest.password)) {
      return;
    }

    this.loginRequest.rememberMe = this.isRememberMe;

    this.authService.authenticate(this.loginRequest, true)
      .subscribe(account => {
        if (account) {
          localStorage.setItem(LOCAL_AUTHORITIES_KEY, JSON.stringify(account.authorities));
          const redirectUrl = this.utilsService.findFirstAccessibleRoute(account.authorities);
          this.router.navigate([redirectUrl]).then();
        }
      });
  }

  toggleDisplayPassword() {
    this.isHiddenPassword = !this.isHiddenPassword;
  }

  protected readonly ICON_EYE = ICON_EYE;
  protected readonly ICON_EYE_CROSS = ICON_EYE_CROSS;
}
