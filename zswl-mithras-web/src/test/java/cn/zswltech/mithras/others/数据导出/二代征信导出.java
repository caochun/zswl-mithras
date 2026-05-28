package cn.zswltech.mithras.others.数据导出;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.microsoft.playwright.*;
import lombok.SneakyThrows;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * @author luyi
 */
public class 二代征信导出 {
    private static final String HOST =  "10.158.33.212";
    private static final String ACCOUNT = "fengsha";
    private static final String PASSWORD = "1";

    Page page;

    private JSONObject jo = new JSONObject();

    @Test
    @SneakyThrows
    public void exportByJson() {
        String str = FileUtil.readString(
                new ClassPathResource("a.json").getFile(), "UTF-8");
        jo = JSONUtil.parseObj(str);
        write2Excel();
    }

    @Test
    public void export() {
        Browser browser = Playwright.create().chromium().launch(
                new BrowserType.LaunchOptions()
                        .setHeadless(false));
        page = browser.newPage();
        page.setDefaultTimeout(3000);
        page.navigate("http://" + HOST + "/login", new Page.NavigateOptions().setTimeout(10000));
        page.locator("//input[@id='account']").fill(ACCOUNT);
        page.locator("//input[@id='password']").fill(PASSWORD);
        page.locator("//button[@id='submit']").click();
        page.waitForSelector("//li/span[text()='工作台']", new Page.WaitForSelectorOptions().setTimeout(5000));
        征信页面();
        单个借据页面();
        write2Excel();
        System.out.println(jo.toString());
    }


    private void write2Excel() {
        ExcelWriter w = ExcelUtil.getWriter(Paths.get(System.getProperty("user.home"), "二代征信.xlsx").toFile());
        for (String tableName : jo.keySet()) {
            w.setSheet(tableName);
            w.writeRow(jo.getJSONObject(tableName).getJSONArray("headers"));
            JSONArray data = jo.getJSONObject(tableName).getJSONArray("data");
            for (int i = 0; i < data.size(); i++) {
                JSONArray l = data.getJSONArray(i);
                if (CollUtil.isNotEmpty(l) && !l.get(0).equals("暂无数据")) {
                    w.writeRow(l);
                }
            }
        }
        w.flush();
    }

    private void 征信页面() {
        page.navigate("http://" + HOST + "/creditTable/finish");
        page.locator("//span[text()='账户维度']").click();
        page.locator("//span[@class='ant-select-selection-item']").click();
        ThreadUtil.sleep(2000);
        page.locator("//div[@class='ant-select-item-option-content' and contains(text(),'100')]").last().click();

        //
        Locator thList = page.locator("//table/thead/tr/th");
        List<String> accountHeaders = new ArrayList<>();
        for (int i = 0; i < thList.count(); i++) {
            accountHeaders.add(thList.nth(i).textContent().trim());
        }

        List<List<String>> accounts = new ArrayList<>();
        Locator last;
        do {
            Locator trList = page.locator("//table/tbody/tr");
            if (trList.count() == 0) {
                break;
            }
            for (int i = 1; i < trList.count(); i++) {
                Locator tr = trList.nth(i);
                Locator tdList = tr.locator("//td");
                List<String> line = new ArrayList<>();
                for (int j = 0; j < tdList.count(); j++) {
                    String text = tdList.nth(j).textContent().trim();
                    line.add(text);
                }
                String settleStr = line.get(line.size() - 1);
                if (StrUtil.isBlank(settleStr.replaceAll("-", "").trim())) {
                    accounts.add(line);
                }
            }
            last = page.locator("//button[@class='ant-pagination-item-link']").last();
            if (last.isEnabled()) {
                last.click();
                ThreadUtil.sleep(1000);
            } else {
                break;
            }
        } while (true);
        setData("账户表", accountHeaders, accounts);
    }

    private void 单个借据页面() {
        JSONObject jsonObject = jo.getJSONObject("账户表");
        JSONArray array = jsonObject.getJSONArray("data");
        for (int i = 0; i < array.size(); i++) {
            JSONArray a = array.getJSONArray(i);
            String code = a.getStr(0);
            page.locator("//input[@id='paymentApplyCode']").fill(code);
            page.locator("//span[text()='搜索']").click();
            page.locator(String.format("//a[text()='%s']", code)).click();
            //
            subTable("还款表");
            subTable("特定交易表");
            subTable("逾期表");
            subTable("五级分类表");
            subTable("客户表");
            subTable("保证表");
            subTable("抵押表");
            subTable("质押表");
            page.locator("//div[@class='ant-drawer-content-wrapper']//button[@class='ant-drawer-close']").click();
        }
    }

    private void subTable(String tableName) {
        page.locator("//div[@class='ant-drawer-content-wrapper']//div[text()='" + tableName + "']").click();
        ThreadUtil.sleep(300);
        List<String> headers = new ArrayList<>();
        Locator locator = page.locator("//div[@class='ant-drawer-content-wrapper']//table/thead/tr/th");
        for (int j = 0; j < locator.count(); j++) {
            headers.add(locator.nth(j).textContent().trim());
        }
        //
        List<List<String>> data = new ArrayList<>();
        Locator last;
        do {
            Locator trList = page.locator("//div[@class='ant-drawer-content-wrapper']//table/tbody/tr");
            if (trList.count() == 0) {
                break;
            }
            for (int i = 1; i < trList.count(); i++) {
                Locator tr = trList.nth(i);
                Locator tdList = tr.locator("//td");
                List<String> line = new ArrayList<>();
                for (int j = 0; j < tdList.count(); j++) {
                    String text = tdList.nth(j).textContent().trim();
                    line.add(text);
                }
                data.add(line);
            }
            last = page.locator("//div[@class='ant-drawer-content-wrapper']//button[@class='ant-pagination-item-link']");
            if (last.count() > 0 && last.last().isEnabled()) {
                last.last().click();
                ThreadUtil.sleep(1000);
            } else {
                break;
            }
        } while (true);
        setData(tableName, headers, data);
    }

    private void setData(String tableName, List<String> headers, List<List<String>> data) {
//
        JSONObject a = jo.getJSONObject(tableName);
        if (null == a) {
            jo.set(tableName, new JSONObject());
            a = jo.getJSONObject(tableName);
        }
        JSONArray ja = a.getJSONArray("headers");
        if (null == ja) {
            a.set("headers", headers);
        }
        JSONArray da = a.getJSONArray("data");
        if (null == da) {
            a.set("data", data);
        } else {
            da.addAll(data);
        }
    }
}
