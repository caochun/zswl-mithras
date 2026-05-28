package cn.zswltech.mithras.others;

import cn.hutool.core.io.FileUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.zswltech.mithras.others.service.ApplicationTest;
import cn.zswltech.mithras.service.enums.TradeStructureRoleEnum;
import cn.zswltech.mithras.service.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.service.mapper.model.projestablish.ProjEstablishBaseInfo;
import cn.zswltech.mithras.service.mapper.model.projreview.ProjReviewBaseInfo;
import cn.zswltech.mithras.service.service.BizDataFixService;
import cn.zswltech.mithras.service.service.contract.ContractBaseInfoService;
import cn.zswltech.mithras.service.service.contract.ContractTradeStructureService;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.mapper.app.VisitRecordMapper;
import cn.zswltech.mithras.service.mapper.model.app.VisitRecord;
import cn.zswltech.mithras.service.service.materialsfile.MaterialsListService;
import cn.zswltech.mithras.service.service.projestablish.ProjEstablishBaseInfoService;
import cn.zswltech.mithras.service.service.projestablish.ProjEstablishTradeStructureService;
import cn.zswltech.mithras.service.service.projreview.ProjReviewBaseInfoService;
import cn.zswltech.mithras.service.service.projreview.ProjReviewTradeStructureService;
import cn.zswltech.mithras.service.service.third.DmImportService;
import org.junit.Test;

import javax.annotation.Resource;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author dingqi
 * @date 2022/12/12
 * @description
 */
public class BizDataFixTest extends ApplicationTest {
    @Resource
    private BizDataFixService bizDataFixService;

    @Test
    public void aaaa() throws Exception {
        for (int i = 0; i < 100; i++) {
            VisitRecord visitRecord = new VisitRecord();
            visitRecord.setClientId(3292L);
            visitRecord.setClientName("天津铁厂有限***");
            visitRecord.setVisitWay("ON_SITE_VISIT");
            visitRecord.setVisitType("CLIENT_VISIT");
            visitRecord.setVisitPhase("CONTRACT_SIGN_OFFLINE");
            visitRecord.setProjCode("ZL202581274");
            visitRecord.setContractCode("浙商租【2025】租字第(A-0176)号");
            visitRecord.setContractId(1865L);
            visitRecord.setCheckInDate(LocalDateTime.now());
            visitRecord.setCheckInLocation("浙江省杭州市萧山区盈丰街道天人大厦");
            visitRecord.setUserId(68L);
            visitRecord.setDeptId(6L);
            visitRecord.setStatus("PASSED");
            visitRecord.setCreateBy(68L);
            visitRecord.setUpdateBy(68L);
            SpringUtil.getBean(VisitRecordMapper.class).insert(visitRecord);
            for (int j = 0; j < 5; j++) {
                InputStream is = FileUtil.getInputStream("/Users/dingqi/test.jpg");
                SpringUtil.getBean(MaterialsListService.class).add(is, "test-" + j + ".jpg", visitRecord.getId(), null, BusinessModuleEnum.VISIT_RECORD.name());
            }
            Thread.sleep(2000L);
        }
    }

    @Test
    public void importFtpInterestDataTest() {
        bizDataFixService.importFtpInterestData();
    }

    @Test
    public void syncContractTradeStructure() {
        List<ContractBaseInfo> list = SpringUtil.getBean(ContractBaseInfoService.class).list();
        for (ContractBaseInfo contractBaseInfo : list) {
            SpringUtil.getBean(ContractTradeStructureService.class).syncTradeStructure(contractBaseInfo.getId(), TradeStructureRoleEnum.LESSEE);
            SpringUtil.getBean(ContractTradeStructureService.class).syncTradeStructure(contractBaseInfo.getId(), TradeStructureRoleEnum.GUARANTOR);
            SpringUtil.getBean(ContractTradeStructureService.class).syncTradeStructure(contractBaseInfo.getId(), TradeStructureRoleEnum.MORTGAGE);
            SpringUtil.getBean(ContractTradeStructureService.class).syncTradeStructure(contractBaseInfo.getId(), TradeStructureRoleEnum.PLEDGE);
        }
    }

    @Test
    public void syncTradeStructure() {
        List<ProjEstablishBaseInfo> list = SpringUtil.getBean(ProjEstablishBaseInfoService.class).list();
        for (ProjEstablishBaseInfo projEstablishBaseInfo : list) {
            SpringUtil.getBean(ProjEstablishTradeStructureService.class).syncTradeStructure(projEstablishBaseInfo.getId());
        }
        List<ProjReviewBaseInfo> reviewList = SpringUtil.getBean(ProjReviewBaseInfoService.class).list();
        for (ProjReviewBaseInfo projReviewBaseInfo : reviewList) {
            SpringUtil.getBean(ProjReviewTradeStructureService.class).syncTradeStructure(projReviewBaseInfo.getId());
        }
    }

    @Test
    public void fixProjReviewMissDataTest() throws Exception {
        bizDataFixService.fixProjReviewMissData();
    }

    @Test
    public void fixRatingClientAreaIndicatorTest() {
        bizDataFixService.fixRatingClientAreaIndicator();
    }

    @Test
    public void fixEvaluationSubjectData() throws Exception {
        bizDataFixService.fixEvaluationSubjectData();
    }

    @Test
    public void initFinancingActualRepayFromExcel() throws Exception {
        bizDataFixService.initFinancingActualRepayFromExcel();
    }

    @Test
    public void initClientAuthorityStep1() {
        // 处理生效非公海但是又无归属主办的客户数据（在执行sql数据初始化前执行）
        bizDataFixService.initClientAuthorityStep1();
    }

    @Test
    public void initClientAuthorityStep2() {
        // 初始化客户权限
        bizDataFixService.initClientAuthorityStep2();
    }

    @Test
    public void initClientAuthorityStep3() throws Exception {
        // 初始化用户的客户数据副本
        bizDataFixService.initClientAuthorityStep3();
    }

    @Test
    public void dmImportTest() {
        SpringUtil.getBean(DmImportService.class).importData();
    }

    @Test
    public void importHistoryPolicyInfo() {
        bizDataFixService.importHistoryPolicy();
    }

    @Test
    public void fixClientRiskCtlIndustry() {
        bizDataFixService.fixClientRiskControlIndustry();
    }

    @Test
    public void fixBizBaseData() {
        bizDataFixService.fixBizBaseData();
    }

    @Test
    public void fixContractActualFinishDate() {
        bizDataFixService.fixContractActualFinishDate();
    }

    @Test
    public void importProfitData() {
        bizDataFixService.importFinanceProfitData("/Users/dingqi/Downloads/项目利润202507.xlsx", 2025, 7, 4);
        bizDataFixService.importFinanceProfitData("/Users/dingqi/Downloads/项目利润202508.xlsx", 2025, 8, 4);
        bizDataFixService.importFinanceProfitData("/Users/dingqi/Downloads/项目利润202509.xlsx", 2025, 9, 4);
    }

    @Test
    public void importBonusData() {
        bizDataFixService.importKpiBonusData();
    }

    @Test
    public void importProvisionData() {
        String fileName = "/Users/dingqi/Downloads/预算管理初始化/拨备计提-202506.xlsx";
        int startRowIndex = 2;
        LocalDate provisionDate = LocalDate.of(2025, 6, 30);
        bizDataFixService.importFinanceProvisionData(fileName, startRowIndex, provisionDate);
    }
}
