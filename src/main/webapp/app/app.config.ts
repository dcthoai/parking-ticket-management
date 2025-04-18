import {ApplicationConfig, LOCALE_ID, provideZoneChangeDetection} from '@angular/core';
import {provideRouter} from '@angular/router';
import {routes} from './app.routes';
import {
  provideHttpClient,
  withInterceptors,
  HttpInterceptorFn,
  HttpInterceptor,
  HttpClient,
  HttpRequest,
  HttpResponse,
  HttpErrorResponse,
  withFetch
} from '@angular/common/http';
import {AuthExpiredInterceptorFn} from './core/interceptors/auth-expired.interceptor';
import {ErrorHandlerInterceptorFn} from './core/interceptors/error-handler.interceptor';
import {NotificationInterceptorFn} from './core/interceptors/notification.interceptor';
import {ApiInterceptorFn} from './core/interceptors/api.interceptor';
import {ResponseInterceptorFn} from './core/interceptors/response.interceptor';
import {provideToastr} from 'ngx-toastr';
import {provideAnimations} from '@angular/platform-browser/animations';
import {provideTranslateService} from '@ngx-translate/core';
import {NgbDateAdapter} from '@ng-bootstrap/ng-bootstrap';
import {NgbDateDayjsAdapter} from './core/config/datepicker.config';

/**
 * {@link provideHttpClient} configure HTTP Interceptors in Angular
 *
 * Functional interceptors are defined using the {@link HttpInterceptorFn} type
 * instead of implementing the {@link HttpInterceptor} interface
 *
 * Interceptors only work with HTTP requests made through Angular's {@link HttpClient}:
 *    - They do not work with {@link fetch()} or native {@link XMLHttpRequest}
 *    - When using {@link fetch()} or {@link XMLHttpRequest}, you need to manually add logic to ensure correct behavior
 *      (e.g., adding a token to the request, etc.)
 *
 * An Interceptor is a function used to intercept the HTTP request and response flow:
 *    - Before the request is sent ({@link HttpRequest})
 *    - After the response is received or in case of an error ({@link HttpResponse} / {@link HttpErrorResponse})
 *
 * Angular will combine all interceptors into a single processing chain (pipeline)
 *
 * In Angular 17+ / 18 (with Standalone API), interceptors do not need to be declared in `AppModule`:
 *    - Instead, interceptors are configured in {@link ApplicationConfig} within {@link appConfig.providers}.
 *
 * Execution order:
 *    - For **requests**: interceptors at the beginning of the array run first
 *    - For **responses**: interceptors at the end of the array run first
 *
 * Therefore, order interceptors by their role:
 *    - {@link AuthExpiredInterceptorFn}: Handle expired authentication
 *    - {@link ErrorHandlerInterceptorFn}: Handle global errors
 *    - {@link NotificationInterceptorFn}: Display notifications
 *    - {@link ApiInterceptorFn}: Logging / modify URLs
 *    - {@link ResponseInterceptorFn}:
 */
export const appConfig: ApplicationConfig = {
  providers: [
    provideZoneChangeDetection({ eventCoalescing: true }), // Optional: to optimize change detection
    provideRouter(routes),
    provideTranslateService(),
    provideAnimations(),
    provideToastr(),
    //MqttModule.forRoot(MQTT_SERVICE_OPTIONS),
    { provide: LOCALE_ID, useValue: 'en' }, // Locale configuration
    { provide: NgbDateAdapter, useClass: NgbDateDayjsAdapter }, // Date adapter
    provideHttpClient(
      withFetch(), // To let HttpClient use Fetch API instead of XMLHttpRequest (XHR)
      withInterceptors([
        AuthExpiredInterceptorFn,
        ErrorHandlerInterceptorFn,
        NotificationInterceptorFn,
        ApiInterceptorFn,
        ResponseInterceptorFn
      ])
    )
  ]
}
