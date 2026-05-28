package cn.zswltech.mithras.others;

import cn.hutool.core.io.FileUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.zswltech.mithras.associationreport.service.AssociationFileHandleReportService;
import cn.zswltech.mithras.associationreport.service.AssociationMainBusinessService;
import cn.zswltech.mithras.associationreport.storedata.*;
import cn.zswltech.mithras.others.service.ApplicationTest;
import cn.zswltech.mithras.service.job.AssociationReportJob;
import cn.zswltech.mithras.service.mapper.model.associationreport.AssociationMainBusiness;
import org.junit.Test;

import java.util.List;

/**
 * @author dingqi
 * @date 2025/4/19
 * @description
 */
public class AssociationReportTest extends ApplicationTest {
    @Test
    public void collectDataFromSystemTest() {
        SpringUtil.getBean(AssociationReportJob.class).collectDataFromSystem();
    }

    @Test
    public void associationEntityEconomyDataTest() {
        SpringUtil.getBean(AssociationEntityEconomyData.class).storeFromSystemJob("20251110190645279");
    }

    @Test
    public void associationExternFinancingDataTest() {
        SpringUtil.getBean(AssociationExternFinancingData.class).storeFromSystemJob("20251110190749138");
    }

    @Test
    public void associationCompanyProfitStatementDataTest() {
        SpringUtil.getBean(AssociationCompanyProfitStatementData.class).storeFromSystemJob("20251024100907079");
    }

    @Test
    public void associationBusinessSituationDataTest() {
//        SpringUtil.getBean(AssociationBusinessSituationData.class).storeFromExcel("", FileUtil.getInputStream("/Users/dingqi/Downloads/融资租赁公司业务情况表.xlsx"));
        SpringUtil.getBean(AssociationBusinessSituationData.class).storeFromSystemJob("20251110190643193");
    }

    @Test
    public void associationBasicSituationDataTest() {
        SpringUtil.getBean(AssociationBasicSituationData.class).storeFromExcel("", FileUtil.getInputStream("/Users/dingqi/Downloads/融资租赁公司基本情况统计表.xlsx"));
    }

    @Test
    public void associationBalanceSheetPartialDataTest() {
        SpringUtil.getBean(AssociationBalanceSheetPartialData.class).storeFromExcel("", FileUtil.getInputStream("/Users/dingqi/Downloads/融资租赁公司资产负债表.xlsx"));
    }

    @Test
    public void associationShahsStorInfoDataTest() {
        SpringUtil.getBean(AssociationShahsStorInfoData.class).storeFromExcel("", FileUtil.getInputStream("/Users/dingqi/Downloads/股东股权信息一览表-股东股权信息.xlsx"));
    }

    @Test
    public void associationMainBusinessTest() {
        SpringUtil.getBean(AssociationMainBusinessStoreData.class).storeFromExcel("", FileUtil.getInputStream("/Users/dingqi/Downloads/融资租赁公司主要业务清单.xlsx"));
    }

    @Test
    public void associationTop10ClientConcentrationTest() {
        SpringUtil.getBean(AssociationTop10ClientConcentrationStoreData.class).storeFromExcel("", FileUtil.getInputStream("/Users/dingqi/Downloads/融资租赁公司最大十家客户（含集团）集中度统计表.xlsx"));
    }

    @Test
    public void associationRelationTest() {
//        SpringUtil.getBean(AssociationRelationStoreData.class).storeFromExcel("", FileUtil.getInputStream("/Users/dingqi/Downloads/融资租赁公司关联方信息汇总表.xlsx"));
        SpringUtil.getBean(AssociationRelationStoreData.class).storeFromSystemJob("20251110190800187");
    }

    @Test
    public void createAndTransferCsv() {
        List<AssociationMainBusiness> list = SpringUtil.getBean(AssociationMainBusinessService.class).list();

        try {
            SpringUtil.getBean(AssociationFileHandleReportService.class).createAndTransferCsv("金融协会报送-主要业务清单表实体类1.csv", list, "send/J0005/202503/", null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
