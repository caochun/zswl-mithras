package cn.zswltech.mithras.others.hand.projRv;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.StrUtil;
import cn.zswltech.mithras.others.hand.projEtb.ProjEtb;
import com.microsoft.playwright.Download;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

/**
 * @author junke
 */
@Slf4j
public class SingleRvGrabber {

    private static final Path basePath = Paths.get(System.getProperty("user.home"), "Desktop", "cn/zswltech/mithras/others/hand", "rv");

    @SneakyThrows
    public void grab(Page page, ProjRv rv) {
        Path zlqdPath = Paths.get(basePath.toString(), rv.get列表信息().get项目编号(), "资料清单");

        page.evaluate("refreshFrame()");
        page.waitForSelector("//iframe[@id='tabFrame2']");
        page.frame("tabFrame2").waitForSelector("//tr[contains(@id,'_G_PROJECT_RESULT_prj_project_layout_grid_id-u-')]");
        Locator trLocator;
        int count = 0;
        while ((trLocator = page.frameLocator("#tabFrame2").locator("//tr[contains(@id,'_G_PROJECT_RESULT_prj_project_layout_grid_id-u-')]")).count() > 1) {
            page.frameLocator("#tabFrame2").locator("//input[@name='project_id_n']").fill(rv.get列表信息().get项目编号());
            page.frameLocator("#tabFrame2").locator("//div[text()='查询']").click();
            ThreadUtil.sleep(500);
            if (count++ > 5) {
                break;
            }
        }
        trLocator.first().locator("//td").first().click();
        page.frame("tabFrame2").waitForSelector("//input[@name='chance_name']");
        ThreadUtil.sleep(1500);
        //
        //基本信息
       /* ProjRv.基本信息 baseInfo = new ProjRv.基本信息();
        baseInfo.set立项名称(page.frameLocator("#tabFrame2").locator("//input[@name='chance_name']").first().getAttribute("title"));
        baseInfo.set立项编号(page.frameLocator("#tabFrame2").locator("//input[@name='chance_number_n']").first().getAttribute("title"));
        baseInfo.set项目编号(page.frameLocator("#tabFrame2").locator("//input[@name='project_number']").first().getAttribute("title"));
        baseInfo.set项目名称(page.frameLocator("#tabFrame2").locator("//input[@name='project_name']").first().getAttribute("title"));
        baseInfo.set承租人名称(page.frameLocator("#tabFrame2").locator("//input[@name='bp_id_tenant_n']").first().getAttribute("title"));
        baseInfo.set单据类型(page.frameLocator("#tabFrame2").locator("//input[@name='document_type_n']").first().getAttribute("title"));
        baseInfo.set业务类型(page.frameLocator("#tabFrame2").locator("//input[@name='business_type_n']").first().getAttribute("title"));
        baseInfo.set业务主办(page.frameLocator("#tabFrame2").locator("//input[@name='employee_id_n']").first().getAttribute("title"));
        baseInfo.set业务协办一(page.frameLocator("#tabFrame2").locator("//input[@name='assist_employee_id_n']").first().getAttribute("title"));
        baseInfo.set业务协办二(page.frameLocator("#tabFrame2").locator("//input[@name='assist_employee_id_a_n']").first().getAttribute("title"));
        baseInfo.set业务部(page.frameLocator("#tabFrame2").locator("//input[@name='lease_organization_n']").first().getAttribute("title"));
        baseInfo.set预计起租日(page.frameLocator("#tabFrame2").locator("//input[@name='future_lease_date']").first().getAttribute("title"));
        baseInfo.set预测算XIRR(page.frameLocator("#tabFrame2").locator("//div[contains(@id,'_F_PRJ_BASIC_INFO_PRJ_PROJECT_FUTURE_XIRR')]//input").first().getAttribute("title"));
        baseInfo.set行业(page.frameLocator("#tabFrame2").locator("//input[@name='industry_n']").first().getAttribute("title"));
        baseInfo.set资产类别(page.frameLocator("#tabFrame2").locator("//input[@name='property_type_n']").first().getAttribute("title"));


        baseInfo.set产品线(titleValue(page, "//input[@name='division_n']"));
        baseInfo.set项目类型(titleValue(page, "//input[@name='prj_company_type_n']"));
        baseInfo.set资金用途(inputValue(page, "//textarea[contains(@id,'_F_PRJ_BASIC_INFO_PRJ_PROJECT_FUND_USE')]"));
        baseInfo.set风控措施_担保(inputValue(page, "//textarea[contains(@id,'_F_PRJ_BASIC_INFO_PRJ_PROJECT_GUARANTEE')]"));
        baseInfo.set风控措施_抵押(inputValue(page, "//textarea[contains(@id,'_F_PRJ_BASIC_INFO_PRJ_PROJECT_MORTGAGE')]"));
        baseInfo.set风控措施_质押(inputValue(page, "//textarea[contains(@id,'_F_PRJ_BASIC_INFO_PRJ_PROJECT_PLEDGE')]"));
        baseInfo.set风控措施_其他(inputValue(page, "//textarea[contains(@id,'_F_PRJ_BASIC_INFO_PRJ_PROJECT_OTHER_RISK_MEASURES')]"));
        baseInfo.set历史合作情况(inputValue(page, "//textarea[contains(@id,'_F_PRJ_BASIC_INFO_PRJ_PROJECT_HISTORICAL_COOPERATION')]"));
        baseInfo.set其他情况说明(inputValue(page, "//textarea[contains(@id,'_F_PRJ_BASIC_INFO_PRJ_PROJECT_OTHER_INFORMATION')]"));
        rv.set基本信息(baseInfo);*/
        //
       /* grab客户信息(page, rv);
        grab报价信息(page, rv);
        grab现金流信息(page, rv);*/

//        grab资料清单(page, rv);
//        grab附件(page, rv);

        deleteMoreDirectory(zlqdPath);

//        if (!Paths.get(basePath.toString(), rv.get列表信息().get项目编号(), "资料清单").toFile().exists()) {
        grab资料清单(page, rv);
//        }
//        if (!fjPath.toFile().exists()) {
//        grab附件(page, rv);
//        }
    }


    private void grab附件(Page page, ProjRv rv) {
//点击锚点，让表格可见
//        page.frameLocator("#tabFrame2").locator("//a[contains(@title,'_T_CC_PRJ_BASIC_INFO_component_id')]").first().click();
        clickMd(page, "//a[contains(@title,'_T_CC_PRJ_BASIC_INFO_component_id')]", "//a[text()='附件']");

        // page size: all
        /*Locator pageTr = page.frameLocator("#tabFrame2").locator("//table[contains(@id,'2_C_PRJ_BASIC_INFO_prj_project_attachment_layout_grid_id_wrap')]/tbody/tr").last();
        pageTr.locator("//div[@class='item-trigger item-comboButton' and  @atype='triggerfield.trigger']").first().click();
        Locator liList = page.frameLocator("#tabFrame2").locator("//div[@class='item-popup-content item-comboBox-view']/ul/li[text()='all']");
        liList.first().click();
        ThreadUtil.sleep(2000);*/

        Locator trList = page.frameLocator("#tabFrame2").locator("//tr[contains(@id,'_C_PRJ_BASIC_INFO_prj_project_attachment_layout_grid_id-u-')]");
        for (int i = 0; i < trList.count(); i++) {
            Locator tdList = trList.nth(i).locator("//td");
            ProjEtb.附件 info = new ProjEtb.附件();
            for (int j = 0; j < tdList.count(); j++) {
                if (j == 0) {
                    info.set文件名称(tdList.nth(j).locator("//span").innerHTML());
                    FileUtil.mkdir(Paths.get(basePath.toString(), rv.get列表信息().get项目编号(), "附件", info.get文件名称().replaceAll("/", "_")));
                }
                if (j == 1) {
                    //
                    String attachFileNames = tdList.nth(2).locator("//span").innerHTML();
                    if (StrUtil.isNotBlank(attachFileNames)) {
                        String href = tdList.nth(j).locator("//span/a").first().getAttribute("href");
                        page.frame("tabFrame2").evaluate(href);
                        //                        tdList.nth(j).locator("//span/a").first().click(new Locator.ClickOptions().setForce(true));
                        ThreadUtil.sleep(1000);
                        Locator items = page.frameLocator("#tabFrame2").locator("//a[text()='下载']");
                        for (int k = 0; k < items.count(); k++) {
                            Locator nth = items.nth(k);
                            String filename = nth.getAttribute("title");
                            Download download = page.waitForDownload(nth::click);
                            download.saveAs(Paths.get(basePath.toString(), rv.get列表信息().get项目编号(), "附件", info.get文件名称().replaceAll("/", "_"), download.suggestedFilename()));
                        }
                        page.frameLocator("#tabFrame2").locator("//div[@atype='window.close']").first().click();
                        ThreadUtil.sleep(1000);
                    }
                }
            }
        }
    }

    private void grab资料清单(Page page, ProjRv rv) {
        //点击锚点，让表格可见
//        page.frameLocator("#tabFrame2").locator("//a[contains(@title,'_T_DOCUMENT_LIST_component_id')]").first().click();
        clickMd(page, "//a[contains(@title,'_T_DOCUMENT_LIST_component_id')]", "//a[text()='资料清单']");

        // page size: all
        Locator pageTr = page.frameLocator("#tabFrame2").locator("//table[contains(@id,'_G_DOCUMENT_LIST_prj_project_attachment_layout_grid_id_wrap')]/tbody/tr").last();
        pageTr.locator("//div[@class='item-trigger item-comboButton' and  @atype='triggerfield.trigger']").first().click();
        Locator liList = page.frameLocator("#tabFrame2").locator("//div[@class='item-popup-content item-comboBox-view']/ul/li[text()='all']");
        liList.first().click();
        ThreadUtil.sleep(2000);

        Locator trList = page.frameLocator("#tabFrame2").locator("//tr[contains(@id,'_G_DOCUMENT_LIST_prj_project_attachment_layout_grid_id-u-')]");
        List<ProjEtb.资料清单> infoList = new ArrayList<>(trList.count());
        for (int i = 0; i < trList.count(); i++) {
            Locator tdList = trList.nth(i).locator("//td");
            ProjEtb.资料清单 info = new ProjEtb.资料清单();
            for (int j = 0; j < tdList.count(); j++) {
                if (j == 0) {
                    info.set文件名称(tdList.nth(j).locator("//span").last().innerHTML());
                    FileUtil.mkdir(Paths.get(basePath.toString(), rv.get列表信息().get项目编号(), "资料清单", info.get文件名称().replace("/", "_")));
                }
                if (j == 1) {
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
                            Download download = page.waitForDownload(nth::click);
                            download.saveAs(Paths.get(basePath.toString(), rv.get列表信息().get项目编号(), "资料清单", info.get文件名称().replace("/", "_"), download.suggestedFilename()));
                        }
                        page.frameLocator("#tabFrame2").locator("//div[@atype='window.close']").first().click();
                        ThreadUtil.sleep(1000);
                    }
                }
            }
        }
    }


    private void grab现金流信息(Page page, ProjRv etb) {
        //点击锚点，让表格可见
//        page.frameLocator("#tabFrame2").locator("//a[contains(@title,'_G_PRJ_QUOTATION_CASHFLOW_component_id')]").first().click();
        clickMd(page, "//a[contains(@title,'_G_PRJ_QUOTATION_CASHFLOW_component_id')]", "//a[text()='现金流信息']");

        // page size: all
        Locator pageTr = page.frameLocator("#tabFrame2").locator("//table[contains(@id,'_PRJ_QUOTATION_CASHFLOW_prj_quotation_cashflow_layout_grid_id_wrap')]/tbody/tr").last();
        pageTr.locator("//div[@class='item-trigger item-comboButton' and  @atype='triggerfield.trigger']").first().click();
        Locator liList = page.frameLocator("#tabFrame2").locator("//div[@class='item-popup-content item-comboBox-view']/ul/li[text()='all']");
        liList.first().click();
        ThreadUtil.sleep(1000);

        Locator trList = page.frameLocator("#tabFrame2").locator("//tr[contains(@id,'_PRJ_QUOTATION_CASHFLOW_prj_quotation_cashflow_layout_grid_id-u-')]");
        List<ProjRv.现金流信息> infoList = new ArrayList<>(trList.count());
        for (int i = 0; i < trList.count(); i++) {
            Locator tdList = trList.nth(i).locator("//td");
            ProjRv.现金流信息 info = new ProjRv.现金流信息();
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
        etb.set现金流信息List(infoList);
    }

    private void grab报价信息(Page page, ProjRv etb) {
        //点击锚点，让表格可见
//        page.frameLocator("#tabFrame2").locator("//a[contains(@title,'_T_QUOTATION_component_id')]").first().click();
        clickMd(page, "//a[contains(@title,'_T_QUOTATION_component_id')]", "//a[text()='报价信息']");
        ThreadUtil.sleep(500);
        ProjRv.报价信息 info = new ProjRv.报价信息();

        info.set融资金额_元(page.frameLocator("#tabFrame2").locator("//input[@name='finance_amount']").first().getAttribute("title"));
        info.set报价方案(page.frameLocator("#tabFrame2").locator("//input[@name='price_list_n']").first().getAttribute("title"));
        info.set币种(page.frameLocator("#tabFrame2").locator("//input[@name='currency_n']").first().getAttribute("title"));
        info.set起租日(page.frameLocator("#tabFrame2").locator("//input[@name='lease_start_date']").first().getAttribute("title"));
        info.set租赁期限_月(page.frameLocator("#tabFrame2").locator("//input[@name='lease_term_month']").first().getAttribute("title"));
        info.set结束日(page.frameLocator("#tabFrame2").locator("//input[@name='end_date']").first().getAttribute("title"));
        info.set投放日期(page.frameLocator("#tabFrame2").locator("//input[@name='delivery_date']").first().getAttribute("title"));
        info.set利率类型(page.frameLocator("#tabFrame2").locator("//input[@name='int_rate_display_n']").first().getAttribute("title"));
        info.set租赁期数(page.frameLocator("#tabFrame2").locator("//input[@name='lease_times']").first().getAttribute("title"));
        info.setLPR利率类型(page.frameLocator("#tabFrame2").locator("//input[@name='lpr_rate_type_n']").first().getAttribute("title"));
        info.setLPR利率(page.frameLocator("#tabFrame2").locator("//input[@name='base_rate_id_n']").first().getAttribute("title"));
        info.set幅度(page.frameLocator("#tabFrame2").locator("//input[@name='floating_way_range']").first().getAttribute("title"));
        info.set租赁利率(page.frameLocator("#tabFrame2")
                .locator("//div[substring(@id, string-length(@id) - string-length('_G_QUOTATION_PRJ_QUOTATION_INT_RATE') +1) = '_G_QUOTATION_PRJ_QUOTATION_INT_RATE']//input")
                .first().getAttribute("title"));

//        info.set租赁利率(page.frameLocator("#tabFrame2").locator("//div[ends-with(@id,'_G_QUOTATION_PRJ_QUOTATION_INT_RATE')]//input").first().getAttribute("title"));
        info.set保证比例(page.frameLocator("#tabFrame2").locator("//div[contains(@id,'_G_QUOTATION_PRJ_QUOTATION_DEPOSIT_RATIO')]//input").first().getAttribute("title"));
        info.set保证金_元(page.frameLocator("#tabFrame2").locator("//input[@name='deposit']").first().getAttribute("title"));
        info.set租赁咨询费_元(page.frameLocator("#tabFrame2").locator("//input[@name='lease_charge']").first().getAttribute("title"));
        info.set租赁咨询费比例(page.frameLocator("#tabFrame2").locator("//div[contains(@id,'_G_QUOTATION_PRJ_QUOTATION_CHARGE_RATIO')]//input").first().getAttribute("title"));
        info.set名义货价_元(page.frameLocator("#tabFrame2").locator("//input[@name='residual_value']").first().getAttribute("title"));
        info.set租金支付频率(page.frameLocator("#tabFrame2").locator("//input[@name='annual_pay_times_n']").first().getAttribute("title"));
        info.set罚息日利率(page.frameLocator("#tabFrame2").locator("//div[contains(@id,'_G_QUOTATION_PRJ_QUOTATION_PENALTY_RATE')]//input").first().getAttribute("title"));
        info.set每期租金(page.frameLocator("#tabFrame2").locator("//input[@name='rental']").first().getAttribute("title"));
        info.setIRR(page.frameLocator("#tabFrame2").locator("//div[contains(@id,'_G_QUOTATION_PRJ_QUOTATION_IRR')]//input").first().getAttribute("title"));
        info.setXIRR(page.frameLocator("#tabFrame2").locator("//div[contains(@id,'_G_QUOTATION_PRJ_QUOTATION_XIRR')]//input").first().getAttribute("title"));
        info.set授信金额_元(page.frameLocator("#tabFrame2").locator("//input[@name='credit_amount']").first().getAttribute("title"));
        info.set额度类型(page.frameLocator("#tabFrame2").locator("//input[@name='credit_type_n']").first().getAttribute("title"));
        info.set风险敞口(page.frameLocator("#tabFrame2").locator("//input[@name='risk_exposure']").first().getAttribute("title"));
        info.set还款方式(page.frameLocator("#tabFrame2").locator("//input[@name='repayment_type_n']").first().getAttribute("title"));
        info.set其他还款保证方式(page.frameLocator("#tabFrame2").locator("//input[@name='other_repayment_n']").first().getAttribute("title"));
        etb.set报价信息(info);
    }

    private void grab客户信息(Page page, ProjRv etb) {
        //点击锚点，让表格可见
        clickMd(page, "//a[contains(@title,'_T_TENANT_BASIC_INFO_component_id')]", "//a[text()='客户信息']");
//        page.frameLocator("#tabFrame2").locator("//a[contains(@title,'_T_TENANT_BASIC_INFO_component_id')]").first().click();
        // page size: all
        Locator pageTr = page.frameLocator("#tabFrame2").locator("//table[contains(@id,'_G_TENANT_BASIC_INFO_prj_project_bp_layout_grid_id_wrap')]/tbody/tr").last();
        pageTr.locator("//div[@class='item-trigger item-comboButton' and  @atype='triggerfield.trigger']").first().click();
        Locator liList = page.frameLocator("#tabFrame2").locator("//div[@class='item-popup-content item-comboBox-view']/ul/li[text()='all']");
        liList.first().click();
        ThreadUtil.sleep(1000);
        //

        Locator trList = page.frameLocator("#tabFrame2").locator("//tr[contains(@id,'_G_TENANT_BASIC_INFO_prj_project_bp_layout_grid_id-u-')]");
        List<ProjRv.客户信息> infoList = new ArrayList<>(trList.count());
        for (int i = 0; i < trList.count(); i++) {
            Locator tdList = trList.nth(i).locator("//td");
            ProjRv.客户信息 info = new ProjRv.客户信息();
            for (int j = 0; j < tdList.count(); j++) {
                switch (j) {
                    case 1: {
                        info.set客户名称(tdList.nth(j).locator("//span").innerHTML());
                        break;
                    }
                    case 3: {
                        info.set客户分类(tdList.nth(j).locator("//span").innerHTML());
                        break;
                    }
                    case 4: {
                        info.set客户类别(tdList.nth(j).locator("//span").innerHTML());
                        break;
                    }
                    case 5: {
                        info.set客户类型(tdList.nth(j).locator("//span").innerHTML());
                        break;
                    }
                    case 6: {
                        info.set备注(tdList.nth(j).locator("//span").innerHTML());
                    }
                    default: {
                        break;
                    }
                }
            }
            infoList.add(info);
        }
        etb.set客户信息List(infoList);
    }

    private void clickMd(Page page, String xpath1, String xpath2) {
        Locator locator = page.frameLocator("#tabFrame2").locator(xpath1);
        if (locator.count() > 0) {
            locator.first().click();
        } else {
            locator = page.frameLocator("#tabFrame2").locator(xpath2);
            locator.first().click();
        }
    }


    private String inputValue(Page page, String xpath) {
        Locator locator = page.frameLocator("#tabFrame2").locator(xpath);
        if (locator.count() > 0) {
            return locator.first().inputValue();
        }
        return null;
    }

    private String titleValue(Page page, String xpath) {
        Locator locator = page.frameLocator("#tabFrame2").locator(xpath);
        if (locator.count() > 0) {
            return locator.first().getAttribute("title");
        }
        return null;
    }

    /**
     * 删除多级目录
     */
    private static void deleteMoreDirectory(Path path) throws IOException {
        if (!path.toFile().exists()) {
            return;
        }
        Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
            //进入文件夹之前
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                return super.preVisitDirectory(dir, attrs);
            }

            //遍历文件
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return super.visitFile(file, attrs);
            }

            //进入文件夹之后
            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                Files.delete(dir);
                return super.postVisitDirectory(dir, exc);
            }
        });
    }
}
