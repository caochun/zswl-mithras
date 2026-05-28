package cn.zswltech.mithras.others.hand.projEtb;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.StrUtil;
import com.microsoft.playwright.Download;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author junke
 */
@Slf4j
public class SingleEtbGrabber {

    private static final Path basePath = Paths.get(System.getProperty("user.home"), "Desktop", "cn/zswltech/mithras/others/hand", "etb");

    public void grab(Page page, ProjEtb etb) {
        Path path = Paths.get(basePath.toString(), etb.get列表信息().get立项编号());
        if (path.toFile().exists()) {
            log.info("项目：{}文件已存在，跳过", etb.get列表信息().get立项编号());
            return;
        }


        page.evaluate("refreshFrame()");
        page.waitForSelector("//iframe[@id='tabFrame2']");
        page.frame("tabFrame2").waitForSelector("//tr[contains(@id,'_G_CHANCE_RESULT_prj_chance_layout_grid_id-u-')]");
        Locator trLocator;
        int count = 0;
        while ((trLocator = page.frameLocator("#tabFrame2").locator("//tr[contains(@id,'_G_CHANCE_RESULT_prj_chance_layout_grid_id-u-')]")).count() > 1) {
            page.frameLocator("#tabFrame2").locator("//input[@name='chance_number']").fill(etb.get列表信息().get立项编号());
            page.frameLocator("#tabFrame2").locator("//div[text()='查询']").click();
            ThreadUtil.sleep(500);
            if (count++ > 5 && etb.get列表信息().get立项编号().equals("ZTZLCHA202103002")) {
                break;
            }
        }
        trLocator.first().locator("//td").first().click();
        page.frame("tabFrame2").waitForSelector("//div[text()='退出']");
        ThreadUtil.sleep(1500);
        //
        etb.set项目概述(page.frameLocator("#tabFrame2").locator("//textarea[contains(@id,'_PRJ_CHANCE_SUMMARY_PRJ_CHANCE_PRJ_CHANCE_SUMMARY')]").inputValue());
        //基本信息
        ProjEtb.基本信息 baseInfo = new ProjEtb.基本信息();
        baseInfo.set立项名称(page.frameLocator("#tabFrame2").locator("//input[@name='chance_name']").first().getAttribute("title"));
        baseInfo.set立项编号(page.frameLocator("#tabFrame2").locator("//input[@name='chance_number']").first().getAttribute("title"));
        baseInfo.set业务类型(page.frameLocator("#tabFrame2").locator("//input[@name='ref_v06_n']").first().getAttribute("title"));
        baseInfo.set客户名称(page.frameLocator("#tabFrame2").locator("//input[@name='bp_name']").first().getAttribute("title"));
        baseInfo.set业务经理(page.frameLocator("#tabFrame2").locator("//input[@name='employee_id_n']").first().getAttribute("title"));
        baseInfo.set协办经理(page.frameLocator("#tabFrame2").locator("//input[@name='assist_employee_id_n']").first().getAttribute("title"));
        baseInfo.set部门(page.frameLocator("#tabFrame2").locator("//input[@name='lease_organization_n']").first().getAttribute("title"));
        baseInfo.set单据类型(page.frameLocator("#tabFrame2").locator("//input[@name='document_category_n']").first().getAttribute("title"));
        baseInfo.set产品线(page.frameLocator("#tabFrame2").locator("//input[@name='division_n']").first().getAttribute("title"));

        baseInfo.set供应商(page.frameLocator("#tabFrame2").locator("//input[@name='vender_bp_id_n']").first().getAttribute("title"));
        baseInfo.set关联合同编号(page.frameLocator("#tabFrame2").locator("//input[@name='associated_number']").first().getAttribute("title"));
        baseInfo.set项目来源(page.frameLocator("#tabFrame2").locator("//textarea[contains(@id,'_BASIC_PRJ_CHANCE_REF_V10')]").first().inputValue());
        etb.set基本信息(baseInfo);
        //
       /* grab客户信息(page, etb);
        grab租赁方案(page, etb);
        grab现金流信息(page, etb);
        grab租赁物概况(page, etb);
        grab租赁物清单(page, etb);
        grab其他风险情况(page, etb);*/
//        Path path = Paths.get(basePath.toString(), etb.get列表信息().get立项编号());
        if (!path.toFile().exists()) {
            grab资料清单(page, etb);
            grab附件(page, etb);
        }

    }

    private void grab附件(Page page, ProjEtb etb) {
//点击锚点，让表格可见
        page.frameLocator("#tabFrame2").locator("//a[contains(@title,'_G_MASTER_ATTACHMENT_component_id')]").first().click();
        // page size: all
        Locator pageTr = page.frameLocator("#tabFrame2").locator("//table[contains(@id,'_MASTER_ATTACHMENT_prj_chance_attachment_layout_grid_id_wrap')]/tbody/tr").last();
        pageTr.locator("//div[@class='item-trigger item-comboButton' and  @atype='triggerfield.trigger']").first().click();
        Locator liList = page.frameLocator("#tabFrame2").locator("//div[@class='item-popup-content item-comboBox-view']/ul/li[text()='all']");
        liList.first().click();
        ThreadUtil.sleep(2000);

        Locator trList = page.frameLocator("#tabFrame2").locator("//tr[contains(@id,'_MASTER_ATTACHMENT_prj_chance_attachment_layout_grid_id-u-')]");
        List<ProjEtb.附件> infoList = new ArrayList<>(trList.count());
        for (int i = 0; i < trList.count(); i++) {
            Locator tdList = trList.nth(i).locator("//td");
            ProjEtb.附件 info = new ProjEtb.附件();
            for (int j = 0; j < tdList.count(); j++) {
                if (j == 0) {
                    info.set文件名称(tdList.nth(j).locator("//span").innerHTML());
                    FileUtil.mkdir(Paths.get(basePath.toString(), etb.get列表信息().get立项编号(), "附件", info.get文件名称().replaceAll("/", "_")));
                }
                if (j == 1) {
                    Map<String, List<String>> map = info.get附件map();
                    map.putIfAbsent(info.get文件名称(), new ArrayList<>());
                    //
                    String attachFileNames = tdList.nth(2).locator("//span").innerHTML();
                    if (StrUtil.isNotBlank(attachFileNames)) {
                        tdList.nth(j).locator("//span/a").first().click();
                        ThreadUtil.sleep(1000);
                        Locator items = page.frameLocator("#tabFrame2").locator("//a[text()='下载']");
                        for (int k = 0; k < items.count(); k++) {
                            Locator nth = items.nth(k);
                            String filename = nth.getAttribute("title");
                            map.get(info.get文件名称()).add(filename);
                            Download download = page.waitForDownload(nth::click);
                            download.saveAs(Paths.get(basePath.toString(), etb.get列表信息().get立项编号(), "附件", info.get文件名称().replace("/", "_"), download.suggestedFilename()));
                        }
                        page.frameLocator("#tabFrame2").locator("//div[@atype='window.close']").first().click();
                        ThreadUtil.sleep(1000);
                    }
                }
                if (j == 2) {
                    info.set附件名称(tdList.nth(j).locator("//span").innerHTML());
                }
                if (j == 3) {
                    info.set归档类别(tdList.nth(j).locator("//span").innerHTML());
                }
                if (j == 4) {
                    info.set备注(tdList.nth(j).locator("//span").innerHTML());
                }
            }
            infoList.add(info);
        }
        etb.set附件列表(infoList);

    }

    private void grab资料清单(Page page, ProjEtb etb) {
        //点击锚点，让表格可见
        page.frameLocator("#tabFrame2").locator("//a[contains(@title,'_G_CHANCE_ATTACHMENT_component_id')]").first().click();
        // page size: all
        Locator pageTr = page.frameLocator("#tabFrame2").locator("//table[contains(@id,'_CHANCE_ATTACHMENT_prj_chance_attachment_layout_grid_id_wrap')]/tbody/tr").last();
        pageTr.locator("//div[@class='item-trigger item-comboButton' and  @atype='triggerfield.trigger']").first().click();
        Locator liList = page.frameLocator("#tabFrame2").locator("//div[@class='item-popup-content item-comboBox-view']/ul/li[text()='all']");
        liList.first().click();
        ThreadUtil.sleep(2000);

        Locator trList = page.frameLocator("#tabFrame2").locator("//tr[contains(@id,'_CHANCE_ATTACHMENT_prj_chance_attachment_layout_grid_id-u-')]");
        List<ProjEtb.资料清单> infoList = new ArrayList<>(trList.count());
        for (int i = 0; i < trList.count(); i++) {
            Locator tdList = trList.nth(i).locator("//td");
            ProjEtb.资料清单 info = new ProjEtb.资料清单();
            for (int j = 0; j < tdList.count(); j++) {
                if (j == 0) {
                    info.set文件名称(tdList.nth(j).locator("//span").innerHTML());
                    FileUtil.mkdir(Paths.get(basePath.toString(), etb.get列表信息().get立项编号(), "资料清单", info.get文件名称().replace("/", "_")));
                }
                if (j == 1) {
                    Map<String, List<String>> map = info.get附件map();
                    map.putIfAbsent(info.get文件名称(), new ArrayList<>());
                    //
                    String attachFileNames = tdList.nth(2).locator("//span").innerHTML();
                    if (StrUtil.isNotBlank(attachFileNames)) {
                        String href = tdList.nth(j).locator("//span/a").first().getAttribute("href");
                        page.frame("tabFrame2").evaluate(href);
//                        tdList.nth(j).locator("//span/a").first().click();
                        ThreadUtil.sleep(1000);
                        Locator items = page.frameLocator("#tabFrame2").locator("//a[text()='下载']");
                        for (int k = 0; k < items.count(); k++) {
                            Locator nth = items.nth(k);
                            String filename = nth.getAttribute("title");
                            map.get(info.get文件名称()).add(filename);
                            Download download = page.waitForDownload(nth::click);
                            download.saveAs(Paths.get(basePath.toString(), etb.get列表信息().get立项编号(), "资料清单", info.get文件名称().replace("/", "_"), download.suggestedFilename()));
                        }
                        page.frameLocator("#tabFrame2").locator("//div[@atype='window.close']").first().click();
                        ThreadUtil.sleep(1000);
                    }
                }
                if (j == 2) {
                    info.set附件名称(tdList.nth(j).locator("//span").innerHTML());
                }
                if (j == 3) {
                    info.set归档类别(tdList.nth(j).locator("//span").innerHTML());
                }
                if (j == 4) {
                    info.set备注(tdList.nth(j).locator("//span").innerHTML());
                }
            }
            infoList.add(info);
        }
        etb.set资料清单列表(infoList);
    }


    private void grab其他风险情况(Page page, ProjEtb etb) {
        ProjEtb.其他风险情况 info = new ProjEtb.其他风险情况();
        info.set项目情况(page.frameLocator("#tabFrame2").locator("//textarea[contains(@id,'_F_OTHER_RISK_PRJ_CHANCE_PROJECT_SITUATION')]").first().inputValue());
        info.set资金用途(page.frameLocator("#tabFrame2").locator("//textarea[contains(@id,'_F_OTHER_RISK_PRJ_CHANCE_USE_OF_FUNDS')]").first().inputValue());
        info.set担保措施(page.frameLocator("#tabFrame2").locator("//textarea[contains(@id,'_F_OTHER_RISK_PRJ_CHANCE_GUAR_MEASURES')]").first().inputValue());
        info.set其它风险缓释措施(page.frameLocator("#tabFrame2").locator("//textarea[contains(@id,'_F_OTHER_RISK_PRJ_CHANCE_MITIGATION_MEASURES')]").first().inputValue());
        info.set还款来源(page.frameLocator("#tabFrame2").locator("//textarea[contains(@id,'_F_OTHER_RISK_PRJ_CHANCE_SOURCE_OF_REPAYMENT')]").first().inputValue());
        info.set项目亮点(page.frameLocator("#tabFrame2").locator("//textarea[contains(@id,'_F_OTHER_RISK_PRJ_CHANCE_PROJECT_HIGHLIGHTS')]").first().inputValue());
        info.set项目风险及防范措施(page.frameLocator("#tabFrame2").locator("//textarea[contains(@id,'_F_OTHER_RISK_PRJ_CHANCE_PREVENTIVE_MEASURES')]").first().inputValue());
        etb.set其他风险情况(info);
    }

    private void grab租赁物清单(Page page, ProjEtb etb) {
        //点击锚点，让表格可见
        page.frameLocator("#tabFrame2").locator("//a[contains(@title,'_T_LEASE_ITEM_component_id')]").first().click();
        //todo
    }

    private void grab租赁物概况(Page page, ProjEtb etb) {
        ProjEtb.租赁物概况 info = new ProjEtb.租赁物概况();
        info.set租赁物概况(page.frameLocator("#tabFrame2").locator("//textarea[contains(@id,'_F_LEASE_INFO_PRJ_CHANCE_LEASE_SIUATION')]").first().inputValue());
        info.set租赁物价值(page.frameLocator("#tabFrame2").locator("//textarea[contains(@id,'_F_LEASE_INFO_PRJ_CHANCE_LEASE_VALUE')]").first().inputValue());
        info.set租赁物权属(page.frameLocator("#tabFrame2").locator("//textarea[contains(@id,'_F_LEASE_INFO_PRJ_CHANCE_LEASE_OWN')]").first().inputValue());
        info.set租赁物保险(page.frameLocator("#tabFrame2").locator("//textarea[contains(@id,'_F_LEASE_INFO_PRJ_CHANCE_LEASE_INSURANCE')]").first().inputValue());
        etb.set租赁物概况(info);

    }

    private void grab现金流信息(Page page, ProjEtb etb) {
        //点击锚点，让表格可见
        page.frameLocator("#tabFrame2").locator("//a[contains(@title,'_G_PRJ_QUOTATION_CASHFLOW_component_id')]").first().click();
        // page size: all
        Locator pageTr = page.frameLocator("#tabFrame2").locator("//table[contains(@id,'_PRJ_QUOTATION_CASHFLOW_prj_quotation_cashflow_layout_grid_id_wrap')]/tbody/tr").last();
        pageTr.locator("//div[@class='item-trigger item-comboButton' and  @atype='triggerfield.trigger']").first().click();
        Locator liList = page.frameLocator("#tabFrame2").locator("//div[@class='item-popup-content item-comboBox-view']/ul/li[text()='all']");
        liList.first().click();
        ThreadUtil.sleep(1000);

        Locator trList = page.frameLocator("#tabFrame2").locator("//tr[contains(@id,'_PRJ_QUOTATION_CASHFLOW_prj_quotation_cashflow_layout_grid_id-u-')]");
        List<ProjEtb.现金流信息> infoList = new ArrayList<>(trList.count());
        for (int i = 0; i < trList.count(); i++) {
            Locator tdList = trList.nth(i).locator("//td");
            ProjEtb.现金流信息 info = new ProjEtb.现金流信息();
            for (int j = 0; j < tdList.count(); j++) {
                String text = tdList.nth(j).locator("//span").innerHTML();
                switch (j) {
                    case 0: {
                        info.set应收日期(text);
                        break;
                    }
                    case 1: {
                        info.set期数(text);
                        break;
                    }
                    case 2: {
                        info.set现金流类型(text);
                        break;
                    }
                    case 3: {
                        info.set现金流项目(text);
                        break;
                    }
                    case 4: {
                        info.set应收金额(text);
                        break;
                    }
                    case 5: {
                        info.set本金(text);
                        break;
                    }
                    case 6: {
                        info.set利息(text);
                        break;
                    }
                    case 7: {
                        info.set当期剩余本金(text);
                        break;
                    }
                    default: {
                        break;
                    }
                }
            }
            infoList.add(info);
        }
        etb.set现金流信息列表(infoList);
    }


    private void grab租赁方案(Page page, ProjEtb etb) {
        ProjEtb.租赁方案 info = new ProjEtb.租赁方案();
        info.set价目表(page.frameLocator("#tabFrame2").locator("//input[@name='price_list_n']").first().getAttribute("title"));
        info.set净投放额(page.frameLocator("#tabFrame2").locator("//input[@name='net_investment']").first().getAttribute("title"));
        info.set租赁期限(page.frameLocator("#tabFrame2").locator("//input[@name='lease_term']").first().getAttribute("title"));
        info.set租金偿还方式(page.frameLocator("#tabFrame2").locator("//input[@name='rent_repayment_method_n']").first().getAttribute("title"));
        info.set融资金额(page.frameLocator("#tabFrame2").locator("//input[@name='finance_amount']").first().getAttribute("title"));
        info.set首期租金(page.frameLocator("#tabFrame2").locator("//input[@name='down_payment']").first().getAttribute("title"));
        info.set咨询费(page.frameLocator("#tabFrame2").locator("//input[@name='lease_charge']").first().getAttribute("title"));
        info.set其他费用(page.frameLocator("#tabFrame2").locator("//input[@name='other_expenses']").first().getAttribute("title"));
        info.set保证金(page.frameLocator("#tabFrame2").locator("//input[@name='deposit']").first().getAttribute("title"));
        info.set租赁利率(page.frameLocator("#tabFrame2").locator("//div[contains(@id,'_PRJ_ELEMENT_PRJ_QUOTATION_INT_RATE')]//input").first().getAttribute("title"));
        info.set租赁利率类型(page.frameLocator("#tabFrame2").locator("//input[@name='int_rate_display_n']").first().getAttribute("title"));
        info.set留购价款(page.frameLocator("#tabFrame2").locator("//input[@name='residual_value']").first().getAttribute("title"));
        info.set支付频率(page.frameLocator("#tabFrame2").locator("//input[@name='annual_pay_times_n']").first().getAttribute("title"));
        info.set还租期数(page.frameLocator("#tabFrame2").locator("//input[@name='lease_times']").first().getAttribute("title"));
        info.set每期租金(page.frameLocator("#tabFrame2").locator("//input[@name='rental']").first().getAttribute("title"));
        info.set首期还款日(page.frameLocator("#tabFrame2").locator("//input[@name='first_repayment_date']").first().getAttribute("title"));
        info.set内涵报酬率XIRR(page.frameLocator("#tabFrame2").locator("//div[contains(@id,'_PRJ_ELEMENT_PRJ_QUOTATION_XIRR')]//input").first().getAttribute("title"));
        info.set预计内涵报酬率XIRR(page.frameLocator("#tabFrame2").locator("//div[contains(@id,'_PRJ_ELEMENT_PRJ_QUOTATION_PRE_XIRR')]//input").first().getAttribute("title"));
        info.set起租日(page.frameLocator("#tabFrame2").locator("//input[@name='lease_start_date']").first().getAttribute("title"));
        info.set说明(page.frameLocator("#tabFrame2").locator("//textarea[contains(@id,'_PRJ_ELEMENT_PRJ_QUOTATION_NOTE')]").first().inputValue());
        etb.set租赁方案(info);
    }

    private void grab客户信息(Page page, ProjEtb etb) {
        Locator trList = page.frameLocator("#tabFrame2").locator("//tr[contains(@id,'_G_BP_prj_chance_bp_layout_grid_id-u')]");
        List<ProjEtb.客户信息> infoList = new ArrayList<>(trList.count());
        for (int i = 0; i < trList.count(); i++) {
            Locator tdList = trList.nth(i).locator("//td");
            ProjEtb.客户信息 info = new ProjEtb.客户信息();
            for (int j = 0; j < tdList.count(); j++) {
                String text = tdList.nth(j).locator("//span").innerHTML();
                switch (j) {
                    case 0: {
                        info.set客户编号(text);
                        break;
                    }
                    case 1: {
                        info.set客户类型(text);
                        break;
                    }
                    case 2: {
                        info.set客户名称(text);
                        break;
                    }
                    default: {
                        break;
                    }
                }
            }
            infoList.add(info);
        }
        etb.set客户信息列表(infoList);
    }

    private void mkDir(Path path) {
        FileUtil.mkdir(path);
    }
}
