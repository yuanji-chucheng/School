package com.example.demo.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.awt.Desktop;
import java.net.URI;

/**
 * 应用启动完成后自动打开主界面
 */
@Component
@ConditionalOnProperty(name = "app.browser.auto-open", havingValue = "true", matchIfMissing = true)
public class BrowserLauncher implements ApplicationListener<ApplicationReadyEvent> {

    private static final Logger log = LoggerFactory.getLogger(BrowserLauncher.class);

    @Value("${server.port:8080}")
    private int port;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        // DevTools 热重启时不重复打开浏览器
        if (event.getApplicationContext().getParent() != null) {
            return;
        }

        String url = "http://localhost:" + port + "/index.html";
        try {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(URI.create(url));
            } else {
                Runtime.getRuntime().exec(new String[]{"cmd", "/c", "start", "", url});
            }
            log.info("已自动打开浏览器: {}", url);
        } catch (Exception e) {
            log.warn("无法自动打开浏览器，请手动访问: {}", url);
        }
    }
}
