import {Component, OnDestroy, OnInit} from '@angular/core';
import {Router, RouterOutlet} from '@angular/router';
import {AuthService} from './core/services/auth.service';
import {Subscription} from 'rxjs';
import {WebsocketService} from './core/services/websocket.service';
import '@angular/localize/init'; // Add initialization for localize (For use TranslateService)
import {TranslateService} from '@ngx-translate/core';
import {UtilsService} from './shared/utils/utils.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss'
})
export class AppComponent implements OnInit, OnDestroy {
  private stateSubscription: Subscription | null = null;

  constructor(
    private authService: AuthService,
    private router: Router,
    private websocketService: WebsocketService,
    private translateService: TranslateService,
    private utilsService: UtilsService
  ) {
    if (this.authService.hasToken()) {
      this.authService.authenticate().subscribe();
      this.authService.navigateToPreviousPage();
    } else {
      this.router.navigate(['/login']).then();
    }
  }

  ngOnInit(): void {
    this.stateSubscription = this.websocketService.onState().subscribe();
    this.websocketService.connect();
    this.translateService.use(this.utilsService.getDefaultLocale());
  }

  ngOnDestroy(): void {
    if (this.stateSubscription) {
      this.stateSubscription.unsubscribe();
    }

    this.websocketService.disconnect();
  }
}
