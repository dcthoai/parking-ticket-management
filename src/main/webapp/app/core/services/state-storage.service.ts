import { Injectable } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class StateStorageService {
  private previousUrlKey = 'previousUrl';

  storeUrl(url: string): void {
    sessionStorage.setItem(this.previousUrlKey, url);
  }

  getUrl(): string | null {
    return sessionStorage.getItem(this.previousUrlKey) as string | null;
  }

  clearUrl(): void {
    sessionStorage.removeItem(this.previousUrlKey);
  }
}
