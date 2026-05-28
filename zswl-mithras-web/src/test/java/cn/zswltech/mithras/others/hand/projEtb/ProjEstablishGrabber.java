package cn.zswltech.mithras.others.hand.projEtb;

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
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

/**
 * @author junke
 */
@Slf4j
public class ProjEstablishGrabber {


    @SneakyThrows
    @Test
    public void syncGrab() {
        //肯定会异常的数据
        List<String> allCode = ListUtil.toList();
        List<List<String>> data = grab(new HashSet<>(allCode));
        saveExcel(data);
    }

    @SneakyThrows
    @Test
    public void parallelGrab() {
        List<String> allCode = JSONUtil.toList(IoUtil.read(new ClassPathResource("etb/etbAll.json").getInputStream(), Charset.defaultCharset()), String.class);
        //肯定会异常的数据
        List<String> expList = ListUtil.toList("ZTZLCHA202103002");
        allCode.removeAll(expList);

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
            toProjEstablishListPage(page);
            //
            pageAll(page);
            //
            List<ProjEtb> etbList = processList(page);

            //
            List<List<String>> data = new ArrayList<>();
            SingleEtbGrabber singleEtbGrabber = new SingleEtbGrabber();

            //
            if (!inSet.isEmpty()) {
                etbList = etbList.stream().filter(e -> inSet.contains(e.get列表信息().get立项编号())).collect(Collectors.toList());
            }

            //去重
            Map<String, ProjEtb> map = new HashMap<>(etbList.size());
            for (ProjEtb projEtb : etbList) {
                if (map.containsKey(projEtb.get列表信息().get立项编号())) {
                    continue;
                }
                map.put(projEtb.get列表信息().get立项编号(), projEtb);
            }
            etbList = new ArrayList<>(map.values());

            log.info("总共{}条记录。", etbList.size());
            int count = 1;
            for (ProjEtb projEtb : etbList) {
                log.info("正在处理第{}条记录, 编号：{}", count++, projEtb.get列表信息().get立项编号());
                //立项查询.状态 ！=“撤回” or “审批中” or “否决”
                String sta = projEtb.get列表信息().get状态();
                if (StrUtil.equals(sta, "撤回") || StrUtil.equals(sta, "审批中") || StrUtil.equals(sta, "否决")) {
                    log.info("项目[{}]状态为：{}，跳过。", projEtb.get列表信息().get立项编号(), sta);
                    continue;
                }

                try {
                    singleEtbGrabber.grab(page, projEtb);
                    data.add(ListUtil.toList(projEtb.get列表信息().get立项编号(), projEtb.get列表信息().get立项名称(), JSONUtil.toJsonPrettyStr(projEtb)));
                } catch (Exception e) {
                    e.printStackTrace();
                    System.err.println("grab  failed. 立项编号:" + projEtb.get列表信息().get立项编号());
                }
            }
            return data;
        }
    }

    private void saveExcel(List<List<String>> data) {
        File file = Paths.get(System.getProperty("user.home"), "Desktop", "立项.xls").toFile();
        FileUtil.del(file);
        ExcelWriter writer = new ExcelWriter(file);
        writer.writeHeadRow(ListUtil.toList("立项编号", "立项名称", "立项信息"));
        writer.write(data);
        writer.close();
        System.out.println("done");
    }

    private List<ProjEtb> processList(Page page) {
        List<ProjEtb> result = new ArrayList<>();
        Locator trList = page.frameLocator("#tabFrame2").locator("//tr[contains(@id,'_G_CHANCE_RESULT_prj_chance_layout_grid_id-u-')]");
        for (int i = 0; i < trList.count(); i++) {
            Locator tdList = trList.nth(i).locator("//td");
            ProjEtb etb = new ProjEtb();
            ProjEtb.列表信息 info = new ProjEtb.列表信息();
            for (int j = 0; j < tdList.count(); j++) {
                String text = "";
                if (j == 0) {
                    Locator a = tdList.nth(j).locator("//span/a");
                    String function = a.getAttribute("href").replace("javascript:", "");
                    etb.setJsFunction(function);
                    text = a.innerHTML();
                } else {
                    text = tdList.nth(j).locator("//span").innerHTML();
                }
                switch (j) {
                    case 0: {
                        info.set立项编号(text);
                        break;
                    }
                    case 1: {
                        info.set立项名称(text);
                        break;
                    }
                    case 2: {
                        info.set业务经理(text);
                        break;
                    }
                    case 3: {
                        info.set业务部(text);
                        break;
                    }
                    case 4: {
                        info.set业务类型(text);
                        break;
                    }
                    case 5: {
                        info.set产品线(text);
                        break;
                    }
                    case 6: {
                        info.set客户名称(text);
                        break;
                    }
                    case 7: {
                        info.set预计融资金额(text);
                        break;
                    }
                    case 8: {
                        info.set创建日期(text);
                        break;
                    }
                    case 9: {
                        info.set状态(text);
                        break;
                    }
                }
            }
            etb.set列表信息(info);
            result.add(etb);
            //for test
            /*if (i >= 10) {
                break;
            }*/

        }
        return result;
    }

    private void pageAll(Page page) {
        page.frameLocator("#tabFrame2").locator("//div[@class='item-trigger item-comboButton' and  @atype='triggerfield.trigger']").nth(1).click();
        page.frameLocator("#tabFrame2").locator("//div[@class='item-popup-content item-comboBox-view']/ul/li").last().click();
        ThreadUtil.sleep(1000);
    }

    private void login(Page page) {
        page.locator("//input[@id='username']").fill("Admin");
        page.locator("//input[@id='password']").fill("hlshand");
        page.locator("//button[@class='btn blue']").click();
    }

    private void toProjEstablishListPage(Page page) {
        page.waitForSelector("//img[@src='/core/leafresources/images/hap/easy_icon@2x.png']");
        ThreadUtil.sleep(1000);
        page.evaluate("goToModuleFunction('10247','PRJ311','立项查询','modules/PRJ/PRJ_CHANCE/PRJ311/prj_chance_query_entrance.lview','立项管理','租前管理','three')");
        page.waitForSelector("//iframe[@id='tabFrame2']");
        ThreadUtil.sleep(1000);
    }

}
