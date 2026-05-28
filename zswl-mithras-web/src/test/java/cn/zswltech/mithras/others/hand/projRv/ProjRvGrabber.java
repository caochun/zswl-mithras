package cn.zswltech.mithras.others.hand.projRv;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.microsoft.playwright.*;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

/**
 * @author junke
 */
@Slf4j
public class ProjRvGrabber {


    @Test
    @SneakyThrows
    public void syncGrab() {
        List<String> allCode = ListUtil.toList("ZTZLPRJ202107048", "ZTZLPRJ202107044", "ZTZLPRJ202103013", "ZTZLPRJ202103011",
                "ZTZLPRJ202101002", "ZTZLPRJ202202013", "ZTZLPRJ202202012", "ZTZLPRJ202109069", "ZTZLPRJ202107050");
        List<List<String>> data = grab(new HashSet<>(allCode));
        saveExcel(data);
    }

    @Test
    @SneakyThrows
    public void parallelGrab() {

        List<String> allCode = JSONUtil.toList(IoUtil.read(new ClassPathResource("rv/rvAll.json").getInputStream(), Charset.defaultCharset()), String.class);

        int pageSize = allCode.size() / 3;
        List<String> list1 = allCode.subList(0, pageSize);
        List<String> list2 = allCode.subList(pageSize, pageSize * 2);
        List<String> list3 = allCode.subList(pageSize * 2, allCode.size());


        ExecutorService executorService = Executors.newFixedThreadPool(3);
        Future<List<List<String>>> f1 = executorService.submit(() -> grab(new HashSet<>(list1)));
        Future<List<List<String>>> f2 = executorService.submit(() -> grab(new HashSet<>(list2)));
        Future<List<List<String>>> f3 = executorService.submit(() -> grab(new HashSet<>(list3)));

        List<List<String>> data = new ArrayList<>();
        data.addAll(f1.get());
        data.addAll(f2.get());
        data.addAll(f3.get());

        saveExcel(data);
    }

    @Test
    public List<List<String>> grab(Set<String> inSet) {
        try (Playwright playwright = Playwright.create()) {
            BrowserType.LaunchOptions launchOptions = new BrowserType.LaunchOptions()
                    .setHeadless(false)
//                    .setArgs(ListUtil.toList("–-blink-settings=imagesEnabled=false"))
                    ;
            Browser browser = playwright.chromium().launch(launchOptions);
            Page page = browser.newPage();
            page.navigate("http://10.100.222.10/core/leaf.lview");
            //
            login(page);
            //
            toProjReviewListPage(page);
            //
            pageAll(page);
            //
            List<ProjRv> rvList = processList(page);
            //
            if (!inSet.isEmpty()) {
                rvList = rvList.stream().filter(e -> inSet.contains(e.get列表信息().get项目编号())).collect(Collectors.toList());
            }

            //
            List<List<String>> data = new ArrayList<>();
            SingleRvGrabber singleRvGrabber = new SingleRvGrabber();
            for (ProjRv projRv : rvList) {

                //项目报告查询.项目状态 ！=“审批中” or “撤回” or “关闭” or “附条件”
                String sta = projRv.get列表信息().get项目状态();
                if (StrUtil.equals(sta, "审批中") || StrUtil.equals(sta, "撤回") || StrUtil.equals(sta, "关闭") || StrUtil.equals(sta, "附条件")) {
                    log.info("项目[{}]状态为：{}，跳过。", projRv.get列表信息().get项目状态(), sta);
                    continue;
                }
                try {
                    singleRvGrabber.grab(page, projRv);
                    data.add(ListUtil.toList(projRv.get列表信息().get项目编号(), projRv.get列表信息().get项目名称(), JSONUtil.toJsonPrettyStr(projRv)));
                } catch (Exception e) {
                    e.printStackTrace();
                    System.err.println("grab  failed. 立项编号:" + projRv.get列表信息().get项目编号());
                }
            }
            return data;
        }
    }

    private void saveExcel(List<List<String>> data) {
        File file = Paths.get(System.getProperty("user.home"), "Desktop", "评审.xls").toFile();
        FileUtil.del(file);
        ExcelWriter writer = new ExcelWriter(file);
        writer.writeHeadRow(ListUtil.toList("项目编号", "项目名称", "评审信息"));
        writer.write(data);
        writer.close();
        System.out.println("done");
    }

    private List<ProjRv> processList(Page page) {
        List<ProjRv> result = new ArrayList<>();
        Locator trList = page.frameLocator("#tabFrame2").locator("//tr[contains(@id,'_G_PROJECT_RESULT_prj_project_layout_grid_id-u-')]");
        for (int i = 0; i < trList.count(); i++) {
            Locator tdList = trList.nth(i).locator("//td");
            ProjRv rv = new ProjRv();
            ProjRv.列表信息 info = new ProjRv.列表信息();
            for (int j = 0; j < tdList.count(); j++) {
                String text = "";
                if (j == 0) {
                    Locator a = tdList.nth(j).locator("//span/a");
                    String function = a.getAttribute("href").replace("javascript:", "");
                    rv.setJsFunction(function);
                    text = a.innerHTML();
                } else {
                    text = tdList.nth(j).locator("//span").innerHTML();
                }
                switch (j) {
                    case 0: {
                        info.set项目编号(text);
                        break;
                    }
                    case 1: {
                        info.set项目名称(text);
                        break;
                    }
                    case 2: {
                        info.set承租人名称(text);
                        break;
                    }
                    case 3: {
                        info.set项目类型(text);
                        break;
                    }
                    case 4: {
                        info.set项目经理(text);
                        break;
                    }
                    case 5: {
                        info.set部门负责人(text);
                        break;
                    }
                    case 6: {
                        info.set业务部(text);
                        break;
                    }
                    case 7: {
                        info.set创建日期(text);
                        break;
                    }
                    case 8: {
                        info.set项目状态(text);
                        break;
                    }
                    /*case 9: {
                        info.set立项编号(text);
                        break;
                    }*/
                }
            }
            rv.set列表信息(info);
            result.add(rv);
            //for test
            /*if (i >= 10) {
                break;
            }*/

        }
        return result;
    }

    private void pageAll(Page page) {
        page.frameLocator("#tabFrame2").locator("//div[@atype='pageSizeInfo2']/following-sibling::div[1]//div[@class='item-trigger item-comboButton' and  @atype='triggerfield.trigger']").click();
        page.frameLocator("#tabFrame2").locator("//div[@class='item-popup-content item-comboBox-view']/ul/li[text()='all']").last().click();
        ThreadUtil.sleep(1000);
    }

    private void login(Page page) {
        page.locator("//input[@id='username']").fill("Admin");
        page.locator("//input[@id='password']").fill("hlshand");
        page.locator("//button[@class='btn blue']").click();
    }

    private void toProjReviewListPage(Page page) {
        page.waitForSelector("//img[@src='/core/leafresources/images/hap/easy_icon@2x.png']");
        ThreadUtil.sleep(1000);
        page.evaluate("javascript:goToModuleFunction('10244','PRJ317','项目报告查询','modules/PRJ/PRJ_PROJECT/PRJ316/prj_project_query.lview','项目管理','租前管理','three')");
        page.waitForSelector("//iframe[@id='tabFrame2']");
        ThreadUtil.sleep(1500);
    }

}
