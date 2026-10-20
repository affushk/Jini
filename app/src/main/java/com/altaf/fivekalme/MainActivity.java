package com.altaf.fivekalme;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.webkit.WebViewAssetLoader;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {
    private WebView webView;
    private FrameLayout splash;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private long splashStartedAt;
    private boolean pageReady = false;
    private boolean splashClosed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Window w = getWindow();
        w.setStatusBarColor(Color.parseColor("#FFF4F8"));
        w.setNavigationBarColor(Color.parseColor("#FFF4F8"));

        FrameLayout root = new FrameLayout(this);
        root.setBackgroundColor(Color.parseColor("#FFF4F8"));
        setContentView(root);

        webView = new WebView(this);
        webView.setAlpha(0f);
        root.addView(webView, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));

        setupWebView();
        splash = buildSplash();
        root.addView(splash, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));

        splashStartedAt = System.currentTimeMillis();
        playSplashAnimation();

        webView.loadUrl("https://appassets.androidplatform.net/assets/index.html");

        // Safety fallback: never leave the user stuck on the splash.
        handler.postDelayed(this::closeSplashIfPossible, 7000);
    }

    private void setupWebView() {
        WebSettings s = webView.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        s.setMediaPlaybackRequiresUserGesture(false);
        s.setLoadsImagesAutomatically(true);
        s.setUseWideViewPort(true);
        s.setLoadWithOverviewMode(true);

        CookieManager.getInstance().setAcceptCookie(true);
        CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true);

        final WebViewAssetLoader assetLoader = new WebViewAssetLoader.Builder()
                .addPathHandler("/assets/", new WebViewAssetLoader.AssetsPathHandler(this))
                .build();

        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public android.webkit.WebResourceResponse shouldInterceptRequest(
                    WebView view, android.webkit.WebResourceRequest request) {
                return assetLoader.shouldInterceptRequest(request.getUrl());
            }

            @Override
            @SuppressWarnings("deprecation")
            public android.webkit.WebResourceResponse shouldInterceptRequest(
                    WebView view, String url) {
                return assetLoader.shouldInterceptRequest(android.net.Uri.parse(url));
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                pageReady = true;
                closeSplashIfPossible();
            }
        });
    }

    private FrameLayout buildSplash() {
        FrameLayout page = new FrameLayout(this);
        GradientDrawable bg = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{
                        Color.parseColor("#FFF9FC"),
                        Color.parseColor("#FCE4EE"),
                        Color.parseColor("#FFF8F2")
                }
        );
        page.setBackground(bg);

        addDecoration(page, "✦", 18, "#E9A8C2", Gravity.TOP | Gravity.START, 28, 62);
        addDecoration(page, "★", 14, "#E9C66E", Gravity.TOP | Gravity.END, 34, 92);
        addDecoration(page, "☾", 22, "#E7B2C7", Gravity.BOTTOM | Gravity.START, 34, 78);
        addDecoration(page, "✦", 13, "#E8B7CA", Gravity.BOTTOM | Gravity.END, 48, 120);

        LinearLayout center = new LinearLayout(this);
        center.setOrientation(LinearLayout.VERTICAL);
        center.setGravity(Gravity.CENTER_HORIZONTAL);
        center.setPadding(dp(28), 0, dp(28), 0);

        FrameLayout.LayoutParams cp = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        cp.gravity = Gravity.CENTER;
        page.addView(center, cp);

        FrameLayout logo = new FrameLayout(this);
        logo.setId(View.generateViewId());
        logo.setElevation(dp(12));
        logo.setBackground(roundBg("#FFFDFE", 34, "#F2BCD1", 2));
        LinearLayout.LayoutParams lpLogo = new LinearLayout.LayoutParams(dp(112), dp(112));
        center.addView(logo, lpLogo);

        TextView moon = text("☾", 58, "#B85E82", Typeface.BOLD);
        moon.setGravity(Gravity.CENTER);
        logo.addView(moon, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));

        TextView star = text("✦", 22, "#E7BC55", Typeface.BOLD);
        FrameLayout.LayoutParams sp = new FrameLayout.LayoutParams(dp(34), dp(34));
        sp.gravity = Gravity.TOP | Gravity.END;
        sp.topMargin = dp(12);
        sp.rightMargin = dp(11);
        logo.addView(star, sp);

        TextView title = text("NOOR KIDS", 29, "#643F50", Typeface.BOLD);
        title.setLetterSpacing(.08f);
        title.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams tp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        tp.topMargin = dp(22);
        center.addView(title, tp);

        TextView kalme = text("5 KALME", 15, "#B86C8B", Typeface.BOLD);
        kalme.setLetterSpacing(.16f);
        kalme.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams kp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        kp.topMargin = dp(7);
        center.addView(kalme, kp);

        View line = new View(this);
        GradientDrawable lineBg = new GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT,
                new int[]{
                        Color.TRANSPARENT,
                        Color.parseColor("#E8AFC6"),
                        Color.TRANSPARENT
                }
        );
        line.setBackground(lineBg);
        LinearLayout.LayoutParams lineP = new LinearLayout.LayoutParams(dp(190), dp(1));
        lineP.topMargin = dp(24);
        center.addView(line, lineP);

        TextView designed = text("DESIGNED BY", 10, "#A48190", Typeface.BOLD);
        designed.setLetterSpacing(.22f);
        designed.setGravity(Gravity.CENTER);
        designed.setBackground(roundBg("#FFF7FA", 18, "#F0CDD9", 1));
        designed.setPadding(dp(13), dp(7), dp(13), dp(7));
        LinearLayout.LayoutParams dp1 = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        dp1.topMargin = dp(18);
        center.addView(designed, dp1);

        TextView designer = text("Altaf Shaikh", 18, "#6D4355", Typeface.BOLD);
        designer.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams dp2 = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        dp2.topMargin = dp(9);
        center.addView(designer, dp2);

        TextView tagline = text("Listen  •  See  •  Repeat", 11, "#9E7E8B", Typeface.NORMAL);
        tagline.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams tg = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        tg.topMargin = dp(13);
        center.addView(tagline, tg);

        // Store animation targets via tags.
        page.setTag(new View[]{logo, title, kalme, line, designed, designer, tagline, star});
        return page;
    }

    private void playSplashAnimation() {
        View[] v = (View[]) splash.getTag();
        for (View item : v) {
            item.setAlpha(0f);
            item.setTranslationY(dp(14));
        }

        v[0].setScaleX(.72f);
        v[0].setScaleY(.72f);
        v[0].animate().alpha(1f).translationY(0).scaleX(1f).scaleY(1f)
                .setStartDelay(120).setDuration(620).start();

        reveal(v[1], 430, 500);
        reveal(v[2], 610, 450);

        v[3].setScaleX(.20f);
        v[3].animate().alpha(1f).translationY(0).scaleX(1f)
                .setStartDelay(790).setDuration(520).start();

        reveal(v[4], 930, 420);
        reveal(v[5], 1090, 460);
        reveal(v[6], 1280, 420);

        v[7].setAlpha(0f);
        v[7].setScaleX(.45f);
        v[7].setScaleY(.45f);
        v[7].animate().alpha(1f).scaleX(1f).scaleY(1f)
                .rotationBy(18f).setStartDelay(520).setDuration(650).start();

        // Very subtle logo breathing after the intro animation.
        handler.postDelayed(() -> {
            if (!splashClosed) {
                v[0].animate().scaleX(1.025f).scaleY(1.025f)
                        .setDuration(650)
                        .withEndAction(() -> {
                            if (!splashClosed) {
                                v[0].animate().scaleX(1f).scaleY(1f)
                                        .setDuration(650).start();
                            }
                        }).start();
            }
        }, 1650);
    }

    private void reveal(View v, long delay, long duration) {
        v.animate().alpha(1f).translationY(0)
                .setStartDelay(delay).setDuration(duration).start();
    }

    private void closeSplashIfPossible() {
        if (splashClosed) return;

        long elapsed = System.currentTimeMillis() - splashStartedAt;
        long minimum = 2600;

        if (!pageReady && elapsed < 6900) return;

        if (elapsed < minimum) {
            handler.postDelayed(this::closeSplashIfPossible, minimum - elapsed);
            return;
        }

        splashClosed = true;
        webView.animate().alpha(1f).setDuration(420).start();
        splash.animate()
                .alpha(0f)
                .scaleX(1.025f)
                .scaleY(1.025f)
                .setDuration(520)
                .withEndAction(() -> splash.setVisibility(View.GONE))
                .start();
    }

    private void addDecoration(FrameLayout page, String value, int sp, String color,
                               int gravity, int sideMargin, int verticalMargin) {
        TextView t = text(value, sp, color, Typeface.BOLD);
        t.setAlpha(.72f);
        FrameLayout.LayoutParams p = new FrameLayout.LayoutParams(dp(48), dp(48));
        p.gravity = gravity;
        p.leftMargin = dp(sideMargin);
        p.rightMargin = dp(sideMargin);
        p.topMargin = dp(verticalMargin);
        p.bottomMargin = dp(verticalMargin);
        page.addView(t, p);
    }

    private TextView text(String value, float size, String color, int style) {
        TextView t = new TextView(this);
        t.setText(value);
        t.setTextSize(size);
        t.setTextColor(Color.parseColor(color));
        t.setTypeface(Typeface.create("sans-serif", style));
        t.setIncludeFontPadding(false);
        return t;
    }

    private GradientDrawable roundBg(String fill, int radiusDp, String stroke, int strokeDp) {
        GradientDrawable g = new GradientDrawable();
        g.setColor(Color.parseColor(fill));
        g.setCornerRadius(dp(radiusDp));
        g.setStroke(dp(strokeDp), Color.parseColor(stroke));
        return g;
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }

    @Override
    public void onBackPressed() {
        if (webView != null && webView.canGoBack()) webView.goBack();
        else super.onBackPressed();
    }
}
