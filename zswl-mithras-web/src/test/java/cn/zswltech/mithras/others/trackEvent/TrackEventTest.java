package cn.zswltech.mithras.others.trackEvent;

import cn.hutool.json.JSONUtil;
import cn.zswltech.gruul.biz.service.OrgService;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.dashboard.DashboardApprovalListREQ;
import cn.zswltech.mithras.dto.kpi.KpiExpectedLossDecisionQuery;
import cn.zswltech.mithras.dto.rating.decision.DecisionExecuteEclResult;
import cn.zswltech.mithras.dto.riskcontrol.opinion.RiskControlOpinionMonitorListREQ;
import cn.zswltech.mithras.dto.riskcontrol.opinion.RiskControlOpinionMonitorListRSP;
import cn.zswltech.mithras.rating.service.DecisionService;
import cn.zswltech.mithras.rating.service.RatingClientService;
import cn.zswltech.mithras.rating.service.RatingReportService;
import cn.zswltech.mithras.others.service.ApplicationTest;
import cn.zswltech.mithras.service.application.riskcontrol.RiskControlOpinionMonitorFacade;
import cn.zswltech.mithras.fund.domain.enums.financing.FinancingTypeEnum;
import cn.zswltech.mithras.riskcontrol.opinion.RiskControlOpinionHandleStatus;
import cn.zswltech.mithras.service.fund.direct.entity.FundDirectFinancingBaseInfo;
import cn.zswltech.mithras.service.fund.direct.service.FundDirectFinancingBaseInfoService;
import cn.zswltech.mithras.service.fund.direct.service.FundDirectFinancingRepayActualService;
import cn.zswltech.mithras.service.job.NextMonthRentNotify;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.model.financing.FundFinancingBaseInfo;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.system.user.SysUserService;
import cn.zswltech.mithras.fund.application.bo.ComprehensiveFinancingCostBO;
import cn.zswltech.mithras.service.service.dashboard.GuanYuanOperationService;
import cn.zswltech.mithras.dashboard.application.guanyuandata.BusinessContractSummaryDTO;
import cn.zswltech.mithras.dashboard.application.guanyuandata.PayIncomeDTO;
import cn.zswltech.mithras.fund.application.*;
import cn.zswltech.mithras.fund.application.financial.FundFinancialSystemService;
import cn.zswltech.mithras.fund.application.financing.FundFinancingBaseInfoService;
import cn.zswltech.mithras.fund.application.financing.FundFinancingPlanService;
import cn.zswltech.mithras.fund.application.financing.FundFinancingPledgeInfoService;
import cn.zswltech.mithras.fund.application.financing.FundFinancingService;
import cn.zswltech.mithras.fund.application.receiptrepay.FundReceiptRepayBaseInfoService;
import cn.zswltech.mithras.service.service.trackEvent.TrackEventService;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static cn.zswltech.mithras.service.others.SpringContextHolder.getBean;

//@ActiveProfiles(value = "pre")
public class TrackEventTest extends ApplicationTest  {

    @Resource
    private TrackEventService trackEventService;
    @Resource
    private SysUserService sysUserService;
    @Resource
    private OrgService orgService;
    @Resource
    private RatingReportService ratingReportService;
    @Resource
    private RatingClientService ratingClientService;
    @Resource
    private FundFinancingBaseInfoService fundFinancingBaseInfoService;
    @Resource
    private FundFinancingPlanService fundFinancingPlanService;
    @Resource
    private FundOrganizationService fundOrganizationService;
    @Resource
    private FundFinancingCreditRefService financingCreditRefService;
    @Resource
    private FundReceiptRepayBaseInfoService receiptRepayBaseInfoService;
    @Resource
    private FundDirectFinancingRepayActualService directFinancingRepayActualService;
    @Resource
    private FundFinancingService financingService;
    @Resource
    private FundGuaranteeInfoService guaranteeInfoService;
    @Resource
    private FundGuaranteeAgencyService guaranteeAgencyService;
    @Resource
    private FundCreditService creditService;
    @Resource
    private FundInitService fundInitService;
    @Resource
    private GuanYuanOperationService dashboardOperationService;
    @Resource
    private FundFinancingPledgeInfoService financingPledgeInfoService;
    @Resource
    private FundFinancialSystemService fundFinancialSystemService;
    @Resource
    private FundDirectFinancingBaseInfoService directFinancingBaseInfoService;
    @Resource
    private DecisionService decisionService;

    @Test
    public void listPayIncomeTest() {
        LocalDate queryFrom = LocalDate.of(2025, 10, 1);
        LocalDate queryTo = LocalDate.of(2025, 10, 21);
        List<PayIncomeDTO> result = dashboardOperationService.listPayIncome(queryFrom, queryTo);
        System.out.println(JSONUtil.toJsonStr(result));
    }

    @Test
    public void businessContractSummaryTest() {
        List<BusinessContractSummaryDTO> result = dashboardOperationService.listBusinessContractSummary(LocalDate.of(2025, 9, 14));
        System.out.println(JSONUtil.toJsonStr(result));
    }

    @Test
    public void creditInitNo(){
//        fundInitService.creditLimitNo();
        fundInitService.financingOccupyRecyclableNo();
    }

    @Test
    public void initFinancingBaseInfoTable(){
        fundInitService.creditLimit();
    }

    @Test
    public void financingOccupyRecyclable(){
        fundInitService.financingOccupyRecyclable();
    }

    @Test
    public void financingOccupyNotRecyclable(){
        fundInitService.financingOccupyNoRecyclable();
    }

    @Test
    public void initFundTable(){
        fundInitService.initFundTable();
    }

    @Test
    public void test2(){
        Map<Long, Long> longLongMap = receiptRepayBaseInfoService.queryRemainingAmount(Collections.singletonList(1541L), FinancingTypeEnum.INDIRECT);
        Long aLong = longLongMap.get(1541L);

    }

    @Test
    public void financialSystemTest(){
        fundFinancialSystemService.send();
    }

    @Test
    public void TrackTest(){
//        trackEventService.startEffect();
        ratingClientService.needUpdateParam(131L);
    }

    @Test
    public void rating(){

        ratingReportService.checkApprovalOpinion(ratingClientService.getById(99));
    }

    @Test
    public void dashBoardOperateTest(){
        DashboardApprovalListREQ req = new DashboardApprovalListREQ();
        req.setEndTimeFrom(LocalDate.of(2024,1,1));
        req.setEndTimeTo(LocalDate.of(2024,12,31));
        dashboardOperationService.approvalListGuanYuan(req);
    }

    @Test
    public void test(){
        Set<String> jcssywb = sysUserService.getFinancialManagerIdsByDeptCode(12L);
        jcssywb.forEach(System.out::println);
    }

    @Test
    public void calculateCost(){
        ComprehensiveFinancingCostBO bo = new ComprehensiveFinancingCostBO();
        bo.setFinancingType(FinancingTypeEnum.DIRECT.name());
        bo.setFinancingId(251009L);
        receiptRepayBaseInfoService.calculateFundContractRate(bo);
    }

    @Test
    public void cashFlowTest(){
        directFinancingRepayActualService.updateCashFlow(4810L);

    }

    @Test
    public void fund(){
        directFinancingRepayActualService.updateNextPhase(251000L,"ZR2023080102-007");
    }

    @Test
    public void fundJob(){
        financingService.financingFloatRateAdjustTask();
    }

    @Test
    @Transactional(rollbackFor = Throwable.class)
    public void updateContractRate(){
        List<String> errorInfoList = new ArrayList<>();
        List<FundFinancingBaseInfo> list = fundFinancingBaseInfoService.list();
        for (FundFinancingBaseInfo financingBaseInfo : list) {
            try {
                fundFinancingPlanService.updateFinancingCost(financingBaseInfo.getId());
            }catch (Exception e){
                errorInfoList.add("间融不成功,id=" + financingBaseInfo.getId() + "报错信息:" + e.getMessage());
            }
        }

        List<FundDirectFinancingBaseInfo> directList = directFinancingBaseInfoService.list();
        for (FundDirectFinancingBaseInfo directFinancingBaseInfo : directList) {
            try {
                directFinancingBaseInfoService.updateFinancingCost(directFinancingBaseInfo.getId());
            }catch (Exception e){
                errorInfoList.add("直融不成功,id=" + directFinancingBaseInfo.getId() + "报错信息:" + e.getMessage());
            }
        }
        errorInfoList.forEach(System.out::println);
        throw new MithrasException("执行完成");
    }

    @Test
    public void updateContractRate1(){
        fundFinancingPlanService.updateFinancingCost(413L);
    }

    @Test
    public void pledgeTest(){
        getBean(NextMonthRentNotify.class).runJob(null, Collections.singletonList(1377L), null);

//        financingPledgeInfoService.checkPledgeChange(40L);
    }

    @Test
    public void aTest(){
        RiskControlOpinionMonitorListREQ req = new RiskControlOpinionMonitorListREQ();
        req.setHandleStatus("PEND_HANDLE");
        req.setPage(1);
        req.setPage(Integer.MAX_VALUE);
        R<PageR<RiskControlOpinionMonitorListRSP>> list = getBean(RiskControlOpinionMonitorFacade.class).list(req);

        // 创建一个新的工作簿
        Workbook workbook = new XSSFWorkbook();
        List<RiskControlOpinionMonitorListRSP> rspList = list.getData().getList();
        String[] headers = new String[]{"标题", "客户名称", "统一社会信用代码", "状态", "处置意见", "预警星级" ,"预警信号", "主体机构代码", "信息发布日期"};

            // 创建一个工作表
            Sheet sheet = workbook.createSheet("舆情监测");
            // 创建表头
            Row headerRow = sheet.createRow(0);

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }

            // 计算二维数组的大小
            int rowCount = rspList.size();

            // 创建二维数组
            Object[][] dataArray = new Object[rowCount][headers.length];

            int j = 0;
            // 遍历列表并填充二维数组
            for (int i = 0; i < rowCount; i++) {
                RiskControlOpinionMonitorListRSP tempData = rspList.get(i);
                dataArray[i][j++] = tempData.getTitle();
                dataArray[i][j++] = tempData.getChiName();
                dataArray[i][j++] = tempData.getCreditCode();
                dataArray[i][j++] = RiskControlOpinionHandleStatus.valueOf(tempData.getHandleStatus()).display;
                dataArray[i][j++] = tempData.getAdvisement();
                dataArray[i][j++] = tempData.getWarnStar();
                dataArray[i][j++] = tempData.getWarnLevel();
                dataArray[i][j++] = tempData.getMajorOrgCode();
                dataArray[i][j] = tempData.getInfoPublDate();
                j = 0;
            }


            int rowNum = 1;
            for (Object[] datum : dataArray) {
                Row row = sheet.createRow(rowNum++);
                for (int i = 0; i < datum.length; i++) {
                    Cell cell = row.createCell(i);
                    if (datum[i] instanceof Integer) {
                        cell.setCellValue((Integer) datum[i]);
                    } else if (datum[i] instanceof String) {
                        cell.setCellValue((String) datum[i]);
                    }
                }
            }

            // 自动调整列宽
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }


        // 将工作簿写入文件
        try (FileOutputStream fileOut = new FileOutputStream("/Users/zswl/Desktop/舆情监测.xlsx")) {
            workbook.write(fileOut);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 关闭工作簿
        try {
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void eclTest(){
        KpiExpectedLossDecisionQuery query = new KpiExpectedLossDecisionQuery();
        query.setOrder_num("浙商租【2022】租字第(A-0060)号");
        query.setClient_name("四川创网通信科技有限公司");
        query.setInner_level("BB");
        query.setInner_pd(new BigDecimal("0.1021118156704127"));
        query.setGroup("制造业");
        query.setClassify("正常");
        query.setLate_day(0L);
        query.setLease_type("不含船");
        query.setRemain_principal(new BigDecimal("20527861.4"));
        query.setAccrued_interest(new BigDecimal("37699.22"));
        query.setDeposit(new BigDecimal("600000"));
        query.setExpire_day(LocalDate.of(2027,10,20));
        query.setInner_first_level("BB");
        DecisionExecuteEclResult result = decisionService.eclExecute(query);


//        KpiExpectedLossDecisionQuery query2 = new KpiExpectedLossDecisionQuery();
//        query2.setOrder_num("浙商租【2022】租字第(A-0027)号");
//        query2.setClient_name("安吉七彩灵峰农业发展有限公司");
//        query2.setInner_level("BB");
//        query2.setInner_pd(new BigDecimal("0.10885056539561695"));
//        query2.setGroup("政信");
//        query2.setClassify("正常");
//        query2.setLate_day(0L);
//        query2.setLease_type("不含船");
//        query2.setRemain_principal(new BigDecimal("26326658.51"));
//        query2.setAccrued_interest(BigDecimal.ZERO);
//        query2.setDeposit(BigDecimal.ZERO);
//        query2.setExpire_day(LocalDate.of(2025,5,18));
//        query2.setInner_first_level("BB");
//        DecisionExecuteEclResult result2 = decisionService.eclExecute(query2);
//
//
//        KpiExpectedLossDecisionQuery query3 = new KpiExpectedLossDecisionQuery();
//        query3.setOrder_num("浙商租【2022】租字第(A-0027)号");
//        query3.setClient_name("安吉七彩灵峰农业发展有限公司");
//        query3.setInner_level("BB");
//        query3.setInner_pd(new BigDecimal("0.10885056539561695"));
//        query3.setGroup("政信");
//        query3.setClassify("正常");
//        query3.setLate_day(0L);
//        query3.setLease_type("不含船");
//        query3.setRemain_principal(new BigDecimal("26326658.51"));
//        query3.setAccrued_interest(BigDecimal.ZERO);
//        query3.setDeposit(BigDecimal.ZERO);
//        query3.setExpire_day(LocalDate.of(2025,5,18));
//        query3.setInner_first_level("BB");
//        DecisionExecuteEclResult result3 = decisionService.eclExecute(query3);
    }

}
