/*
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.facebook.react.modules.network;

import androidx.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.Headers;
import okhttp3.HttpUrl;

/** Basic okhttp3 CookieJar container */
public class ReactCookieJarContainer implements CookieJarContainer {

  @Nullable private CookieJar cookieJar = null;

  @Override
  public void setCookieJar(CookieJar cookieJar) {
    this.cookieJar = cookieJar;
  }

  @Override
  public void removeCookieJar() {
    this.cookieJar = null;
  }

  @Override
  public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
    if (cookieJar != null) {
      cookieJar.saveFromResponse(url, cookies);
    }
  }

  @Override
  public List<Cookie> loadForRequest(HttpUrl url) {
    if (cookieJar != null) {
      List<Cookie> cookies;
      try {
        cookies = cookieJar.loadForRequest(url);
      } catch (Exception ignored) {
        // `IllegalArgumentException` can be thrown when building cookies
        // `AndroidRuntimeException` if failed to load WebView provider: No WebView installed
        // `AndroidRuntimeException` NameNotFoundException: com.google.android.webview
        // `AndroidRuntimeException` MissingWebViewPackageException: Failed to verify WebView provider, version code is lower than expected: 451516600 actual: 35381100
        return Collections.emptyList();
      }

      ArrayList<Cookie> validatedCookies = new ArrayList<>();
      for (Cookie cookie : cookies) {
        try {
          Headers.Builder cookieChecker = new Headers.Builder();
          cookieChecker.add(cookie.name(), cookie.value());
          validatedCookies.add(cookie);
        } catch (IllegalArgumentException ignored) {
        }
      }
      return validatedCookies;
    }
    return Collections.emptyList();
  }
}
