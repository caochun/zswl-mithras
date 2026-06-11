package cn.zswltech.mithras.others.hand.extract.contract;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.zswltech.mithras.others.service.ApplicationTest;
import cn.zswltech.mithras.foundation.enums.common.ProjectBizType;
import cn.zswltech.mithras.contract.enums.contract.ContractProcessStatusEnum;
import cn.zswltech.mithras.contract.enums.contract.ContractStatus;
import cn.zswltech.mithras.contract.enums.contract.LPRTypeEnum;
import cn.zswltech.mithras.contract.enums.contract.LesseeTypeEnum;
import cn.zswltech.mithras.contract.enums.contract.RepayRateEnum;
import cn.zswltech.mithras.projectprocess.enums.projestablish.*;
import cn.zswltech.mithras.projectprocess.enums.projreview.ProjectClassify;
import cn.zswltech.mithras.projectprocess.enums.projreview.ProjectType;
import cn.zswltech.mithras.contract.mapper.contract.ContractBaseInfoMapper;
import cn.zswltech.mithras.contract.mapper.contract.ContractLeasePriceMapper;
import cn.zswltech.mithras.contract.mapper.contract.ContractTenantryMapper;
import cn.zswltech.mithras.contract.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.contract.model.contract.ContractLeasePrice;
import cn.zswltech.mithras.contract.model.contract.ContractTenantry;
import org.junit.Test;

import javax.annotation.Resource;

/**
 * 自然人数据补录
 *
 * @author wangchuanhao
 * @date 2022/11/25 1:20 PM
 */
public class NormalContractImporter extends ApplicationTest {

    @Resource
    private ContractBaseInfoMapper contractBaseInfoMapper;
    @Resource
    private ContractLeasePriceMapper contractLeasePriceMapper;
    @Resource
    private ContractTenantryMapper contractTenantryMapper;

    @Test
    public void importContract() {
        // 主办：冯帅，业务部门：化工建材业务部，业务部门负责人：杨剑雄，业务分管领导：杨剑雄
        Long creatorId = 77L, bizDeptId = 7L, bizDeptLeaderId = 53L, bizDivisionLeaderId = 53L;
        Long hugaoshan = 2936L, lubinghua = 2938L, zhouhaohan = 2939L, huaqianliang = 2937L;
        ContractBaseInfo cf005 = new ContractBaseInfo();
        cf005.setContractCode("浙商租【2021】租字第(CF-0005)号");
        cf005.setClientId(hugaoshan);
        cf005.setBizType(ProjectBizType.ZL.name());
        cf005.setLeaseType(LeaseType.hui_zu.name());
        cf005.setProjectType(ProjectType.OTHER.name());
        cf005.setProjItem(ProjectClassify.MODERATE_SUPPORT.name());
        cf005.setProjSponsorUserId(creatorId);
        cf005.setCreateBy(creatorId);
        cf005.setUpdateBy(creatorId);
        cf005.setBizDeptId(bizDeptId);
        cf005.setBizDeptLeaderId(bizDeptLeaderId);
        cf005.setBizDivisionLeaderId(bizDivisionLeaderId);
        cf005.setContractYear(2021);
        cf005.setSequence(5);
        cf005.setApplyCreditAmount(160000_0000L);
        cf005.setActualLeaseDate(LocalDateTimeUtil.parseDate("2021-04-15", "yyyy-MM-dd"));
        cf005.setEstimatedLeaseDate(LocalDateTimeUtil.parseDate("2021-04-15", "yyyy-MM-dd"));
        cf005.setSettleTime(LocalDateTimeUtil.parse("2022-04-15 00:00:00", "yyyy-MM-dd HH:mm:ss"));
        cf005.setActualFinishDate(LocalDateTimeUtil.parseDate("2022-04-15", "yyyy-MM-dd"));
        cf005.setPaymentCount(1L);
        cf005.setContractStatus(ContractStatus.SETTLE.name());
        cf005.setContractProcessStatus(ContractProcessStatusEnum.SETTLE_PASS.name());
        cf005.setProjReviewId(-1L);
        contractBaseInfoMapper.insert(cf005);
        ContractLeasePrice cf005Price = new ContractLeasePrice();
        cf005Price.setContractId(cf005.getId());
        cf005Price.setApplyCreditAmount(160000_0000L);
        cf005Price.setLeaseMonthCount(12);
        cf005Price.setRepayRate(RepayRateEnum.MONTH.name());
        cf005Price.setRepayTimesTotal(12);
        cf005Price.setPayType(PayType.AFTERWARD.name());
        cf005Price.setRentalCalcType(RepayCalcType.DEBX.name());
        cf005Price.setEarnestMoney(0L);
        cf005Price.setDownPayment(64000_0000L);
        cf005Price.setConsultingFee(960_0000L);
        cf005Price.setNominalPrice(100_0000L);
        cf005Price.setRateType(RateType.FIXED.name());
        cf005Price.setLprType(LPRTypeEnum.ONE_YEAR.name());
        cf005Price.setLprPercent(38500);
        cf005Price.setLprAddPercent(23500);
        cf005Price.setLeaseRatePercent(62000);
        cf005Price.setDefaultInterestRate(500);
        cf005Price.setIrrPercent(80800);
        contractLeasePriceMapper.insert(cf005Price);
        ContractTenantry cf005Tenantry = new ContractTenantry();
        cf005Tenantry.setContractId(cf005.getId());
        cf005Tenantry.setIsReport(0);
        cf005Tenantry.setLesseeType(LesseeTypeEnum.MAIN_LESSSEE.name());
        cf005Tenantry.setLesseeName("胡高山");
        cf005Tenantry.setLesseeId(hugaoshan);
        contractTenantryMapper.insert(cf005Tenantry);


        ContractBaseInfo cf003 = new ContractBaseInfo();
        cf003.setContractCode("浙商租【2021】租字第(CF-0003)号");
        cf003.setClientId(lubinghua);
        cf003.setBizType(ProjectBizType.ZL.name());
        cf003.setLeaseType(LeaseType.hui_zu.name());
        cf003.setProjectType(ProjectType.OTHER.name());
        cf003.setProjItem(ProjectClassify.MODERATE_SUPPORT.name());
        cf003.setProjSponsorUserId(creatorId);
        cf003.setCreateBy(creatorId);
        cf003.setUpdateBy(creatorId);
        cf003.setBizDeptId(bizDeptId);
        cf003.setBizDeptLeaderId(bizDeptLeaderId);
        cf003.setBizDivisionLeaderId(bizDivisionLeaderId);
        cf003.setContractYear(2021);
        cf003.setSequence(3);
        cf003.setApplyCreditAmount(240000_0000L);
        cf003.setActualLeaseDate(LocalDateTimeUtil.parseDate("2021-04-15", "yyyy-MM-dd"));
        cf003.setEstimatedLeaseDate(LocalDateTimeUtil.parseDate("2021-04-15", "yyyy-MM-dd"));
        cf003.setPaymentCount(1L);
        cf003.setContractStatus(ContractStatus.START_RENT.name());
        cf003.setContractProcessStatus(ContractProcessStatusEnum.START_RENT_PASS.name());
        cf003.setProjReviewId(-1L);
        contractBaseInfoMapper.insert(cf003);
        ContractLeasePrice cf003Price = new ContractLeasePrice();
        cf003Price.setContractId(cf003.getId());
        cf003Price.setApplyCreditAmount(240000_0000L);
        cf003Price.setLeaseMonthCount(24);
        cf003Price.setRepayRate(RepayRateEnum.MONTH.name());
        cf003Price.setRepayTimesTotal(24);
        cf003Price.setPayType(PayType.AFTERWARD.name());
        cf003Price.setRentalCalcType(RepayCalcType.DEBX.name());
        cf003Price.setEarnestMoney(0L);
        cf003Price.setDownPayment(72000_0000L);
        cf003Price.setConsultingFee(3360_0000L);
        cf003Price.setNominalPrice(100_0000L);
        cf003Price.setRateType(RateType.FIXED.name());
        cf003Price.setLprType(LPRTypeEnum.ONE_YEAR.name());
        cf003Price.setLprPercent(38500);
        cf003Price.setLprAddPercent(23500);
        cf003Price.setLeaseRatePercent(62000);
        cf003Price.setDefaultInterestRate(500);
        cf003Price.setIrrPercent(80000);
        contractLeasePriceMapper.insert(cf003Price);
        ContractTenantry cf003Tenantry = new ContractTenantry();
        cf003Tenantry.setContractId(cf003.getId());
        cf003Tenantry.setIsReport(0);
        cf003Tenantry.setLesseeType(LesseeTypeEnum.MAIN_LESSSEE.name());
        cf003Tenantry.setLesseeName("陆冰花");
        cf003Tenantry.setLesseeId(lubinghua);
        contractTenantryMapper.insert(cf003Tenantry);


        ContractBaseInfo cf002 = new ContractBaseInfo();
        cf002.setContractCode("浙商租【2021】租字第(CF-0002)号");
        cf002.setClientId(zhouhaohan);
        cf002.setBizType(ProjectBizType.ZL.name());
        cf002.setLeaseType(LeaseType.hui_zu.name());
        cf002.setProjectType(ProjectType.OTHER.name());
        cf002.setProjItem(ProjectClassify.MODERATE_SUPPORT.name());
        cf002.setProjSponsorUserId(creatorId);
        cf002.setCreateBy(creatorId);
        cf002.setUpdateBy(creatorId);
        cf002.setBizDeptId(bizDeptId);
        cf002.setBizDeptLeaderId(bizDeptLeaderId);
        cf002.setBizDivisionLeaderId(bizDivisionLeaderId);
        cf002.setContractYear(2021);
        cf002.setSequence(2);
        cf002.setApplyCreditAmount(150000_0000L);
        cf002.setActualLeaseDate(LocalDateTimeUtil.parseDate("2021-04-15", "yyyy-MM-dd"));
        cf002.setEstimatedLeaseDate(LocalDateTimeUtil.parseDate("2021-04-15", "yyyy-MM-dd"));
        cf002.setPaymentCount(1L);
        cf002.setContractStatus(ContractStatus.START_RENT.name());
        cf002.setContractProcessStatus(ContractProcessStatusEnum.START_RENT_PASS.name());
        cf002.setProjReviewId(-1L);
        contractBaseInfoMapper.insert(cf002);
        ContractLeasePrice cf002Price = new ContractLeasePrice();
        cf002Price.setContractId(cf002.getId());
        cf002Price.setApplyCreditAmount(150000_0000L);
        cf002Price.setLeaseMonthCount(24);
        cf002Price.setRepayRate(RepayRateEnum.MONTH.name());
        cf002Price.setRepayTimesTotal(24);
        cf002Price.setPayType(PayType.AFTERWARD.name());
        cf002Price.setRentalCalcType(RepayCalcType.DEBX.name());
        cf002Price.setEarnestMoney(0L);
        cf002Price.setDownPayment(45000_0000L);
        cf002Price.setConsultingFee(2100_0000L);
        cf002Price.setNominalPrice(100_0000L);
        cf002Price.setRateType(RateType.FIXED.name());
        cf002Price.setLprType(LPRTypeEnum.ONE_YEAR.name());
        cf002Price.setLprPercent(38500);
        cf002Price.setLprAddPercent(23500);
        cf002Price.setLeaseRatePercent(62000);
        cf002Price.setDefaultInterestRate(500);
        cf002Price.setIrrPercent(80600);
        contractLeasePriceMapper.insert(cf002Price);
        ContractTenantry cf002Tenantry = new ContractTenantry();
        cf002Tenantry.setContractId(cf002.getId());
        cf002Tenantry.setIsReport(0);
        cf002Tenantry.setLesseeType(LesseeTypeEnum.MAIN_LESSSEE.name());
        cf002Tenantry.setLesseeName("周昊瀚");
        cf002Tenantry.setLesseeId(zhouhaohan);
        contractTenantryMapper.insert(cf002Tenantry);

        ContractBaseInfo cf001 = new ContractBaseInfo();
        cf001.setContractCode("浙商租【2021】租字第(CF-0001)号");
        cf001.setClientId(huaqianliang);
        cf001.setBizType(ProjectBizType.ZL.name());
        cf001.setLeaseType(LeaseType.hui_zu.name());
        cf001.setProjectType(ProjectType.OTHER.name());
        cf001.setProjItem(ProjectClassify.MODERATE_SUPPORT.name());
        cf001.setProjSponsorUserId(creatorId);
        cf001.setCreateBy(creatorId);
        cf001.setUpdateBy(creatorId);
        cf001.setBizDeptId(bizDeptId);
        cf001.setBizDeptLeaderId(bizDeptLeaderId);
        cf001.setBizDivisionLeaderId(bizDivisionLeaderId);
        cf001.setContractYear(2021);
        cf001.setSequence(1);
        cf001.setApplyCreditAmount(160000_0000L);
        cf001.setActualLeaseDate(LocalDateTimeUtil.parseDate("2021-04-15", "yyyy-MM-dd"));
        cf001.setEstimatedLeaseDate(LocalDateTimeUtil.parseDate("2021-04-15", "yyyy-MM-dd"));
        cf001.setPaymentCount(1L);
        cf001.setContractStatus(ContractStatus.START_RENT.name());
        cf001.setContractProcessStatus(ContractProcessStatusEnum.START_RENT_PASS.name());
        cf001.setProjReviewId(-1L);
        contractBaseInfoMapper.insert(cf001);
        ContractLeasePrice cf001Price = new ContractLeasePrice();
        cf001Price.setContractId(cf001.getId());
        cf001Price.setApplyCreditAmount(160000_0000L);
        cf001Price.setLeaseMonthCount(24);
        cf001Price.setRepayRate(RepayRateEnum.MONTH.name());
        cf001Price.setRepayTimesTotal(24);
        cf001Price.setPayType(PayType.AFTERWARD.name());
        cf001Price.setRentalCalcType(RepayCalcType.DEBX.name());
        cf001Price.setEarnestMoney(0L);
        cf001Price.setDownPayment(56000_0000L);
        cf001Price.setConsultingFee(2080_0000L);
        cf001Price.setNominalPrice(100_0000L);
        cf001Price.setRateType(RateType.FIXED.name());
        cf001Price.setLprType(LPRTypeEnum.ONE_YEAR.name());
        cf001Price.setLprPercent(38500);
        cf001Price.setLprAddPercent(23500);
        cf001Price.setLeaseRatePercent(62000);
        cf001Price.setDefaultInterestRate(500);
        cf001Price.setIrrPercent(80600);
        contractLeasePriceMapper.insert(cf001Price);
        ContractTenantry cf001Tenantry = new ContractTenantry();
        cf001Tenantry.setContractId(cf001.getId());
        cf001Tenantry.setIsReport(0);
        cf001Tenantry.setLesseeType(LesseeTypeEnum.MAIN_LESSSEE.name());
        cf001Tenantry.setLesseeName("华铅良");
        cf001Tenantry.setLesseeId(huaqianliang);
        contractTenantryMapper.insert(cf001Tenantry);
    }

}
