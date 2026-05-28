package cn.zswltech.mithras.others.client;

import cn.hutool.core.util.IdUtil;
import com.microsoft.playwright.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static cn.hutool.core.thread.ThreadUtil.sleep;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author yibin
 */
public class ClientPlaywrightTest {
    private boolean showcase = true;
    private Page page;

    @BeforeEach
    public void login() {
        Playwright playwright = Playwright.create();
        Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
        page = browser.newPage();
        page.navigate("http://mithras-test.zswl.cn:8888/login/");
        Locator accountInputLocator = page.locator("//input[@id='account']");
        if (accountInputLocator.count() > 0) {
            accountInputLocator.fill("admin");
            page.locator("//input[@id='password']").fill("Td@123456");
            page.locator("//span[text()='登录']/parent::button").click();
        }
        sleep(2000);
        page.onDialog(dialog -> {
        });
    }

    /**
     * action: 工商信息模块下的必填字段为空时，直接点击【确认】按钮
     * assert: 保存失败，并给予提示
     */
    @Test
    public void t1() {
        page.locator("//span[text()='客户管理']").first().click();
        page.locator("//span[contains(text(),'新增客户')]").click();
        page.locator("//input[@id='clientName']").last().fill("PlayWright-" + System.currentTimeMillis());
        page.locator("//span[@title='法人']").last().click();
        page.locator("//div[text()='法人']").last().click();
        page.locator("//input[@id='uscCode']").last().fill(IdUtil.getSnowflakeNextIdStr().substring(0, 18));
        page.locator("//span[text()='确定']").last().click();
        sleep(1000);
        alert("现在直接点击确认，请注意查看提示区域");
        page.locator("//span[text()='确认']").last().click();
        sleep(800);
        assertTrue(page.locator("//span[contains(text(), '数据不完整')]").count() > 0);
        sleep(1500);
    }


    @Test
    public void 新建法人() {
        page.locator("//span[text()='客户管理']").first().click();
        page.locator("//span[contains(text(),'新增客户')]").click();
        String clientName = "PlayWright-" + System.currentTimeMillis();
        page.locator("//input[@id='clientName']").last().fill(clientName);
        page.locator("//span[@title='法人']").last().click();
        page.locator("//div[text()='法人']").last().click();
        page.locator("//input[@id='uscCode']").last().fill(IdUtil.getSnowflakeNextIdStr().substring(0, 18));
        page.locator("//span[text()='确定']").last().click();
        sleep(1000);

//        page.locator("//span[text()='保存']").last().click();
//        alert("直接点击保存，请看泛红报错的区域");
//        sleep(1000);
        page.locator("//input[@id='establishDate']").click();
        page.locator("//div[text()='1']").last().click();
        page.locator("//input[@id='approvalDate']").click();
        page.locator("//div[text()='1']").last().click();
        page.locator("//input[@id='bizLicenseEndDate']").click();
        page.locator("//div[text()='25']").last().click();
        page.locator("//input[@id='continuousStatus']").click();
        page.locator("//div[text()='正常']").last().click();
        page.locator("//input[@id='industryType']").click();
        page.locator("//div[text()='制造业']").last().click();
        page.locator("//div[text()='食品制造业']").last().click();
        page.locator("//div[text()='乳制品制造']").last().click();
        page.locator("//div[text()='液体乳制造']").last().click();
        page.locator("//input[@id='economyType']").click();
        page.locator("//div[text()='内资']").last().click();
        page.locator("//input[@id='orgType']").click();
        page.locator("//div[text()='企业']").last().click();
        page.locator("//input[@id='orgScale']").click();
        page.locator("//div[text()='中型']").last().click();
        page.locator("//span[text()='注册资本']//ancestor::th/following-sibling::td//input").last().fill("10000000");

        page.locator("//input[@id='registerCurrencyType']").click();
        page.locator("//div[text()='人民币']").last().click();

        page.locator("//span[text()='实收资本']//ancestor::th/following-sibling::td//input").last().fill("8000000");

        page.locator("//input[@id='realCurrencyType']").click();
        page.locator("//div[text()='人民币']").last().click();

        page.locator("//input[@id='corpRepresent']").last().fill("孙悟空");

        page.locator("//input[@id='corpGender']").click();
        page.locator("//div[text()='男']").last().click();

        page.locator("//input[@id='corpCertType']").click();
        page.locator("//div[text()='居民身份证']").last().click();

        page.locator("//span[text()='法人证件号码']//ancestor::th/following-sibling::td//input").last().fill("350781196403077066");
        page.locator("//textarea[@id='bizScope']").fill("这是一段测试文本");

        page.locator("//input[@id='listedCompany']").click();
        page.locator("//div[text()='是']").last().click();

        page.locator("//input[@id='isRelated']").click();
        page.locator("//div[text()='是']").last().click();

        page.locator("//input[@id='groupFlag']").click();
        page.locator("//div[text()='是']").last().click();

        page.locator("//div[@id='gsxx']//span[text()='保存']").click();
        alert("只点击保存，不点及确认");

        //回到列表页
        page.locator("//span[text()='客户管理']").first().click();
        page.locator("//input[@id='clientName']").last().fill(clientName);
        page.locator("//span[text()='搜索']").first().click();


        Locator header = page.locator("//th[text()='客户状态']").last();
        header.scrollIntoViewIfNeeded();
        String indexStr = header.getAttribute("data-index");
        int index = Integer.parseInt(indexStr) + 1;
        String status = page.locator(String.format("//tbody/tr/td[%s]", index)).last().textContent();
        Assertions.assertEquals(status, "新建");
        alert("请看当前状态：新建");
    }


    private void alert(String message) {
        if (showcase) {
            page.evaluate(String.format("alert('%s')", message));
        }
    }
}
