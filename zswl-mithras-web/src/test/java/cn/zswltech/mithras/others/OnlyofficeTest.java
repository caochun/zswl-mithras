package cn.zswltech.mithras.others;

import cn.hutool.core.collection.ListUtil;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.LoadState;
import com.ruiyun.jvppeteer.core.Puppeteer;
import com.ruiyun.jvppeteer.core.browser.Browser;
import com.ruiyun.jvppeteer.core.page.Frame;
import com.ruiyun.jvppeteer.core.page.Page;
import com.ruiyun.jvppeteer.options.LaunchOptions;
import com.ruiyun.jvppeteer.options.LaunchOptionsBuilder;
import com.ruiyun.jvppeteer.options.WaitForSelectorOptions;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

import static cn.hutool.core.thread.ThreadUtil.sleep;
import static cn.hutool.core.util.RandomUtil.randomInt;

/**
 * TODO
 *
 * @author wangchuanhao
 * @date 2022/11/2 4:56 PM
 */
@Slf4j
public class OnlyofficeTest {

    //    private static final Logger log = LoggerFactory.getLogger(OnlyofficeTest.class);
    private static List<String> docIdList = ListUtil.toList("4395", "4393", "4392", "4391", "4388", "4387", "4379", "4378", "4376", "4375", "4374", "4373", "4372", "4371", "4370", "4369", "4368", "4367", "4366", "4274", "4273", "4272", "4267", "4266", "4265", "4264", "4263", "4262", "4261", "4260", "4259", "4217", "4216", "4215", "4214", "4213", "4212", "4211", "4210", "4209", "4198", "4192", "4191", "4184", "4179", "4177", "4169", "4166", "4153", "4152", "4145", "4144", "4143", "4142", "4141", "4140", "4135", "4134", "4133", "4132", "4131", "4130", "4129", "4128", "4127", "4117", "4116", "4115", "4114", "4113", "4112", "4111", "4110", "4109", "4108", "4107", "4106", "4105", "4085", "4082", "4081", "4078", "4077", "4076");


    @Test
    @SneakyThrows
    public void test() {
//        Playwright p = Playwright.create();
//        BrowserType c = p.chromium();
//       /* BrowserType.LaunchOptions options = new BrowserType.LaunchOptions();
//        options.setArgs(ListUtil.toList("--no-sandbox", "--disable-setuid-sandbox", "--start-maximized", "--disk-cache-size=268435456"));
//        options.setHeadless(false);
//        com.microsoft.playwright.Browser b = c.launch(options);
//        com.microsoft.playwright.Page page = b.newPage();*/
//
//        //
//        BrowserType.LaunchPersistentContextOptions options2 = new BrowserType.LaunchPersistentContextOptions();
//        options2.setArgs(ListUtil.toList("--no-sandbox", "--disable-setuid-sandbox", "--start-maximized", "--disk-cache-size=268435456"));
//        options2.setHeadless(false);
        for (int j = 0; j < 3; j++) {
            Playwright p = Playwright.create();
            BrowserType c = p.chromium();
       /* BrowserType.LaunchOptions options = new BrowserType.LaunchOptions();
        options.setArgs(ListUtil.toList("--no-sandbox", "--disable-setuid-sandbox", "--start-maximized", "--disk-cache-size=268435456"));
        options.setHeadless(false);
        com.microsoft.playwright.Browser b = c.launch(options);
        com.microsoft.playwright.Page page = b.newPage();*/

            //
            BrowserType.LaunchPersistentContextOptions options2 = new BrowserType.LaunchPersistentContextOptions();
            options2.setArgs(ListUtil.toList("--no-sandbox", "--disable-setuid-sandbox", "--start-maximized", "--disk-cache-size=268435456"));
            options2.setHeadless(false);
            // FIXME 需要修改为自己的缓存地址，并创建多个缓存文件夹（多个浏览器不能共用一个）
            BrowserContext bc = c.launchPersistentContext(Paths.get("/Users/wang/Desktop/Cache" + j + 2), options2);
            com.microsoft.playwright.Page page = bc.newPage();


            page.navigate("http://mithras-test.zswl.cn:8888/login/");
            Locator accountInputLocator = page.locator("//input[@id='account']");
            if (accountInputLocator.count() > 0) {
                accountInputLocator.fill("admin");
                page.locator("//input[@id='password']").fill("Td@123456");
                page.locator("//span[text()='登录']/parent::button").click();
            }
            sleep(2000);
//            page.close();
            ExecutorService fixPool = Executors.newFixedThreadPool(1);
//            Semaphore semaphore = new Semaphore(5);
            for (int i = 0; i < 1000; i++) {
                fixPool.submit(() -> {
                    try {
//                        semaphore.acquire();
//                        com.microsoft.playwright.Page detailPage = bc.newPage();
                        long startTime = System.currentTimeMillis();
                        // 随机去一个界面
                        page.navigate("http://mithras-test.zswl.cn:8888/preview/reportPreview/" + docIdList.get(randomInt(docIdList.size())));
                        // 等待内嵌的onlyoffice html界面
                        page.waitForSelector("//iframe[@name='frameEditor']");
                        // 等待网络连接处理结束
                        page.waitForLoadState(LoadState.NETWORKIDLE);
                        log.info("渲染时间:{}", System.currentTimeMillis() - startTime);
                        Thread.sleep(2000);
//                        detailPage.close();
//                        semaphore.release();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                });
            }
        }


        Thread.currentThread().join();


    }

    /**
     * 用于测试onlyoffice性能
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        List<String> docIdList = ListUtil.toList("4395", "4393", "4392", "4391", "4388", "4387", "4379", "4378", "4376", "4375", "4374", "4373", "4372", "4371", "4370", "4369", "4368", "4367", "4366", "4274", "4273", "4272", "4267", "4266", "4265", "4264", "4263", "4262", "4261", "4260", "4259", "4217", "4216", "4215", "4214", "4213", "4212", "4211", "4210", "4209", "4198", "4192", "4191", "4184", "4179", "4177", "4169", "4166", "4153", "4152", "4145", "4144", "4143", "4142", "4141", "4140", "4135", "4134", "4133", "4132", "4131", "4130", "4129", "4128", "4127", "4117", "4116", "4115", "4114", "4113", "4112", "4111", "4110", "4109", "4108", "4107", "4106", "4105", "4085", "4082", "4081", "4078", "4077", "4076");
        Random random = new Random();
        ArrayList<String> argList = new ArrayList<>();
        argList.add("--no-sandbox");
        argList.add("--disable-setuid-sandbox");
        argList.add("--start-maximized");
        argList.add("--disk-cache-size=268435456");
//        argList.add("--disable-features=site-per-process");
//        argList.add("–-no-first-run");
        // FIXME 需要修改为自己本地的 随便自己指定的 用于作chrome缓存的地址
        argList.add("--user-data-dir=/Users/wang/Desktop/Cache/");
        LaunchOptions options = new LaunchOptionsBuilder()
                // FIXME 需要修改为自己本地的 chrome 执行地址
                .withExecutablePath("/Applications/Google Chrome.app/Contents/MacOS/Google Chrome")
                .withArgs(argList)
                .withHeadless(false)
                .build();
//        argList.add("--cast-initial-screen-width");
        Browser browser = Puppeteer.launch(options);
        // 登录
        Page page = browser.newPage();
        page.goTo("http://mithras-test.zswl.cn:8888/login/");
        Thread.sleep(2000);
        if (page.$(".ant-layout-sider-children") == null) {
            // 未登录 左侧边栏没有
            //等待元素加载完成，输入账号密码并提交
            page.waitForSelector("#account");
            page.type("#account", "admin", 100);
            Thread.sleep(1000);
            page.type("#password", "Td@123456", 100);
            page.click("button");
            Thread.sleep(2000);
        }
        page.close();
        ExecutorService fixPool = Executors.newFixedThreadPool(1);
        for (int i = 0; i < 100; i++) {
            fixPool.submit(() -> {
                Page onlyofficePage = browser.newPage();
                try {
                    long startTime = System.currentTimeMillis();
                    // 随机去一个界面
                    onlyofficePage.goTo("http://mithras-test.zswl.cn:8888/preview/reportPreview/" + docIdList.get(random.nextInt(docIdList.size())));
//                    onlyofficePage.$("iframe").contentFrame().$("#rib-doc-name")
                    onlyofficePage.waitForSelector("iframe");
                    Frame frame = onlyofficePage.$("iframe").contentFrame();
                    WaitForSelectorOptions waitForSelectorOptions = new WaitForSelectorOptions();
                    waitForSelectorOptions.setTimeout(Integer.MAX_VALUE);
                    // 文章标题
                    frame.waitForSelector("#rib-doc-name", waitForSelectorOptions);
                    // 画布
//                    frame.waitForSelector("#id_viewer_overlay", waitForSelectorOptions);
                    log.info("渲染时间:{}", System.currentTimeMillis() - startTime);
                    Thread.sleep(5000);
                    onlyofficePage.close();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }

    }

}
