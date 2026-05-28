package playwright;

import com.microsoft.playwright.*;

import static cn.hutool.core.thread.ThreadUtil.sleep;

/**
 * @author yibin
 */
public class PwBase {
    public Page page;
    public boolean showcase = true;


    public void login() {
        login("admin", "Td@123456");
    }


    public void login(String username, String password) {
        Playwright playwright = Playwright.create();
        Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
        page = browser.newPage();
        page.navigate("http://mithras-test.zswl.cn:8888/login/");
        Locator accountInputLocator = page.locator("//input[@id='account']");
        if (accountInputLocator.count() > 0) {
            accountInputLocator.fill(username);
            page.locator("//input[@id='password']").fill(password);
            page.locator("//span[text()='登录']/parent::button").click();
        }
        sleep(2000);
        page.onDialog(dialog -> {
        });
    }
}
