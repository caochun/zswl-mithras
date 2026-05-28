package cn.zswltech.mithras.service.convert.monthlymanage;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.gruul.common.util.UUIDUtil;
import cn.zswltech.gruul.dao.dal.dao.OrgDOMapper;
import cn.zswltech.gruul.dao.dal.entity.OrgDO;
import cn.zswltech.mithras.dto.monthly.*;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.service.enums.common.ProjectBizType;
import cn.zswltech.mithras.service.enums.projestablish.LeaseType;
import cn.zswltech.mithras.service.enums.third.CQAccountApplicationTypeENUM;
import cn.zswltech.mithras.service.enums.third.CQBusinessTypeENUM;
import cn.zswltech.mithras.service.enums.third.CQTaxRateENUM;
import cn.zswltech.mithras.service.enums.third.ExceptionSourceENUM;
import cn.zswltech.mithras.service.mapper.finance.ContractAssessDeptDetailMapper;
import cn.zswltech.mithras.service.mapper.model.client.Client;
import cn.zswltech.mithras.service.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.service.mapper.model.finance.ContractAssessDeptDetail;
import cn.zswltech.mithras.service.others.SpringContextHolder;
import cn.zswltech.mithras.service.service.third.financial.impl.FinancialManagerServiceImpl2;
import cn.zswltech.mithras.service.service.third.financial.vo.CQ2AccountApplicationVO;
import cn.zswltech.mithras.service.util.LongUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * 月结参数
 * @author: jackerhe
 * @date: 2024/6/6 5:36 下午
 **/
@Component
public class MonthlyManageConvert {

    @Resource
    private FinancialManagerServiceImpl2 financialManagerServiceImpl2;

    @Resource
    private ContractAssessDeptDetailMapper contractAssessDeptDetailMapper;

    @Resource
    private OrgDOMapper orgDOMapper;


    public void sendAirAccountApplication(MonthlyAIRListRSP allMonthlyAir, CQAccountApplicationTypeENUM cqAccountApplicationTypeENUM, OrgDO orgDO, Client client, String yearAndMonth, ContractBaseInfo contractBaseInfo){
        if(ObjectUtil.hasEmpty(allMonthlyAir, orgDO, client, yearAndMonth)) {
            return;
        }
        LocalDate now = LocalDate.now();
        String nowFormat = now.format(DateTimeFormatter.ofPattern(DatePattern.NORM_DATE_PATTERN));
        CQ2AccountApplicationVO vo = new CQ2AccountApplicationVO();
        vo.setDescription(cqAccountApplicationTypeENUM.getDisplay());
        vo.setBizdate(nowFormat);
        vo.setTallydate(getTallydate(yearAndMonth));
        vo.setCico_customer(allMonthlyAir.getClientName());
        vo.setCico_taxcategory("normal");//不确定
        vo.setMainbiztype_number("JTZB001");//报账业务类型.编码
        vo.setDept_number(String.valueOf(orgDO.getMainOrgId()));
        vo.setCico_period_number(yearAndMonth.replaceAll("-", ""));
        vo.setCico_sourcebillno(String.join("-", allMonthlyAir.getProjName() + UUIDUtil.genUuid()));//
        //todo
        if (ObjectUtil.isNotNull(contractBaseInfo.getId())) {
            List<ContractAssessDeptDetail> deptDetailList = contractAssessDeptDetailMapper.selectList(Wrappers.<ContractAssessDeptDetail>lambdaQuery()
                    .eq(ContractAssessDeptDetail::getContractId, contractBaseInfo.getId())
                    .eq(ContractAssessDeptDetail::getDeleted, YesOrNoNumberEnum.NO.getCode()));
            if (!deptDetailList.isEmpty()) {
                ContractAssessDeptDetail contractAssessDeptDetail = deptDetailList.get(0);
                Long deptId = contractAssessDeptDetail.getAssessDeptId();
                OrgDO newOrgDO = orgDOMapper.selectByPrimaryKey(deptId);
                if (newOrgDO != null && ObjectUtil.isNotNull(newOrgDO.getMainOrgId())) {
                    vo.setDept_number(String.valueOf(newOrgDO.getMainOrgId()));
                }
            }
        }
        BigDecimal taxrate;
        //税率
        if (ProjectBizType.ZL.name().equals(allMonthlyAir.getBizType()) && LeaseType.zhi_zu.name().equals(allMonthlyAir.getLeaseType())) {
            taxrate = BigDecimal.valueOf(0.13);
        } else {
            taxrate = BigDecimal.valueOf(0.06);
        }
        List<CQ2AccountApplicationVO.CQ2AccountApplicationVOBody> bodys = new ArrayList<>();
        bodys.add(getBody(vo, taxrate, ListUtil.toList(allMonthlyAir), cqAccountApplicationTypeENUM, client, orgDO));
        vo.setTallyentryentity(bodys);
        //项目端实际利率法
        vo.setSource(ExceptionSourceENUM.ASSET_SIDE_AIR_ACCOUNT.name());
        vo.setBusinessKey(String.valueOf(allMonthlyAir.getId()));
        vo.setBusinessTitle(allMonthlyAir.getContractCode());
        financialManagerServiceImpl2.cq2AccountApplicationExec(vo);
    }

    private CQ2AccountApplicationVO.CQ2AccountApplicationVOBody getBody(CQ2AccountApplicationVO vo, BigDecimal taxrate, List<MonthlyAIRListRSP> allMonthlyAir,CQAccountApplicationTypeENUM cqAccountApplicationTypeENUM, Client client,OrgDO orgDO) {
        CQ2AccountApplicationVO.CQ2AccountApplicationVOBody body = vo.new CQ2AccountApplicationVOBody();

        //记账期间 起租日的年月
        String actualLeaseDate = allMonthlyAir.get(0).getYearAndMonth();
        vo.setCico_period_number(allMonthlyAir.get(0).getYearAndMonth().replaceAll("-", ""));
        String[] split = actualLeaseDate.split("-");
        YearMonth yearMonth = YearMonth.of(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
        LocalDate lastDayOfMonth = yearMonth.atEndOfMonth();
        body.setBusinessdate(lastDayOfMonth.format(DateTimeFormatter.ofPattern(DatePattern.NORM_DATE_PATTERN)));
        //项目起租：租金总额
        if (CQAccountApplicationTypeENUM.ACCRUE_INTEREST_ACTUAL_RATE.equals(cqAccountApplicationTypeENUM)) {
            body.setTallyexplanation("实际利率法");
        } else {
            body.setTallyexplanation("剩余本金法");
        }
        //含税利息收入
        body.setCico_amount(allMonthlyAir.stream().map(MonthlyAIRListRSP::getIncomeSum).map(LongUtil::null2zero).map(String::valueOf).map(LongUtil::tenThousand2Dollar).reduce(BigDecimal.ZERO, BigDecimal::add));
        //不含税金额
        body.setCico_bhsje(allMonthlyAir.stream().map(MonthlyAIRListRSP::getIncomeWithoutTaxSum).map(LongUtil::null2zero).map(String::valueOf).map(LongUtil::tenThousand2Dollar).reduce(BigDecimal.ZERO, BigDecimal::add));
        //含税-不含税
        body.setCico_se(body.getCico_amount().subtract(body.getCico_bhsje()));//不含税金额*税率 todo
        body.setCico_contract_num(allMonthlyAir.get(0).getContractCode());
        body.setCico_custname(client.getClientCode());
        body.setCico_hsje(body.getCico_amount());
        body.setTallyamount(body.getCico_amount());
        body.setCico_financialins(client.getClientCode());
        body.setTallydeptid_number(String.valueOf(orgDO.getMainOrgId()));
        body.setCustomer_number(client.getClientCode());
        //body.setCico_project2_number(allMonthlyAir.get(0).getContractCode());
        body.setCico_sl_number(CQTaxRateENUM.getCqBusinessType(taxrate).getCode());
        body.setCico_project2_name(allMonthlyAir.get(0).getContractCode());
        body.setCico_ywlxtyoe_number(Optional.ofNullable(CQBusinessTypeENUM.getCqBusinessType(ProjectBizType.of(allMonthlyAir.get(0).getBizType()), LeaseType.of(allMonthlyAir.get(0).getLeaseType()))).map(CQBusinessTypeENUM::getCode).orElse(null));
        return body;
    }

    public void sendRPAccountApplication(MonthlyRPListRSP allMonthlyRP, CQAccountApplicationTypeENUM cqAccountApplicationTypeENUM, OrgDO orgDO, Client client, String yearAndMonth, ContractBaseInfo contractBaseInfo){
        if(ObjectUtil.hasEmpty(allMonthlyRP, orgDO, client, yearAndMonth)) {
            return;
        }
        LocalDate now = LocalDate.now();
        String nowFormat = now.format(DateTimeFormatter.ofPattern(DatePattern.NORM_DATE_PATTERN));
        CQ2AccountApplicationVO vo = new CQ2AccountApplicationVO();
        vo.setDescription(cqAccountApplicationTypeENUM.getDisplay());
        vo.setBizdate(nowFormat);
        vo.setTallydate(getTallydate(yearAndMonth));

        vo.setCico_customer(allMonthlyRP.getClientName());
        vo.setCico_taxcategory("normal");//不确定
        vo.setMainbiztype_number("JTZB001");//报账业务类型.编码
        vo.setDept_number(String.valueOf(orgDO.getMainOrgId()));
        vo.setCico_period_number(yearAndMonth.replaceAll("-", ""));
        vo.setCico_sourcebillno(String.join("-", allMonthlyRP.getProjName() + UUIDUtil.genUuid()));//
        //todo
        if (ObjectUtil.isNotNull(contractBaseInfo.getId())) {
            List<ContractAssessDeptDetail> deptDetailList = contractAssessDeptDetailMapper.selectList(Wrappers.<ContractAssessDeptDetail>lambdaQuery()
                    .eq(ContractAssessDeptDetail::getContractId, contractBaseInfo.getId())
                    .eq(ContractAssessDeptDetail::getDeleted, YesOrNoNumberEnum.NO.getCode()));
            if (!deptDetailList.isEmpty()) {
                ContractAssessDeptDetail contractAssessDeptDetail = deptDetailList.get(0);
                Long deptId = contractAssessDeptDetail.getAssessDeptId();
                OrgDO newOrgDO = orgDOMapper.selectByPrimaryKey(deptId);
                if (newOrgDO != null && ObjectUtil.isNotNull(newOrgDO.getMainOrgId())) {
                    vo.setDept_number(String.valueOf(newOrgDO.getMainOrgId()));
                }
            }
        }
        BigDecimal taxrate;
        //税率
        if (ProjectBizType.ZL.name().equals(allMonthlyRP.getBizType()) && LeaseType.zhi_zu.name().equals(allMonthlyRP.getLeaseType())) {
            taxrate = BigDecimal.valueOf(0.13);
        } else {
            taxrate = BigDecimal.valueOf(0.06);
        }
        List<CQ2AccountApplicationVO.CQ2AccountApplicationVOBody> bodys = new ArrayList<>();
        bodys.add(getRPBody(vo, taxrate, Collections.singletonList(allMonthlyRP), cqAccountApplicationTypeENUM, client, orgDO));
        vo.setTallyentryentity(bodys);
        //项目端剩余本金法
        vo.setSource(ExceptionSourceENUM.ASSET_SIDE_PR_ACCOUNT.name());
        vo.setBusinessKey(String.valueOf(allMonthlyRP.getId()));
        vo.setBusinessTitle(allMonthlyRP.getContractCode());
        SpringContextHolder.getBean(FinancialManagerServiceImpl2.class).cq2AccountApplicationExec(vo);
    }

    private CQ2AccountApplicationVO.CQ2AccountApplicationVOBody getRPBody(CQ2AccountApplicationVO vo, BigDecimal taxrate, List<MonthlyRPListRSP> allMonthlyAir,CQAccountApplicationTypeENUM cqAccountApplicationTypeENUM, Client client,OrgDO orgDO) {
        CQ2AccountApplicationVO.CQ2AccountApplicationVOBody body = vo.new CQ2AccountApplicationVOBody();
        //记账期间 起租日的年月
        String actualLeaseDate = allMonthlyAir.get(0).getYearAndMonth();
        vo.setCico_period_number(actualLeaseDate);
        String[] split = actualLeaseDate.split("-");
        YearMonth yearMonth = YearMonth.of(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
        LocalDate lastDayOfMonth = yearMonth.atEndOfMonth();
        body.setBusinessdate(lastDayOfMonth.format(DateTimeFormatter.ofPattern(DatePattern.NORM_DATE_PATTERN)));
        //项目起租：租金总额
        if (CQAccountApplicationTypeENUM.ACCRUE_INTEREST_ACTUAL_RATE.equals(cqAccountApplicationTypeENUM)) {
            body.setTallyexplanation("实际利率法");
        } else {
            body.setTallyexplanation("剩余本金法");
        }
        //含税利息收入
        body.setCico_amount(allMonthlyAir.stream().map(MonthlyRPListRSP::getIncomeSum).map(LongUtil::null2zero).map(String::valueOf).map(LongUtil::tenThousand2Dollar).reduce(BigDecimal.ZERO, BigDecimal::add));
        //不含税金额
        body.setCico_bhsje(allMonthlyAir.stream().map(MonthlyRPListRSP::getIncomeWithoutTaxSum).map(LongUtil::null2zero).map(String::valueOf).map(LongUtil::tenThousand2Dollar).reduce(BigDecimal.ZERO, BigDecimal::add));
        //含税-不含税
        body.setCico_se(body.getCico_amount().subtract(body.getCico_bhsje()));//不含税金额*税率 todo
        body.setCico_contract_num(allMonthlyAir.get(0).getContractCode());
        body.setCico_custname(client.getClientCode());
        body.setCico_hsje(body.getCico_amount());
        body.setTallyamount(body.getCico_amount());
        body.setCico_financialins(client.getClientCode());
        body.setTallydeptid_number(String.valueOf(orgDO.getMainOrgId()));
        body.setCustomer_number(client.getClientCode());
        //body.setCico_project2_number(allMonthlyAir.get(0).getContractCode());
        body.setCico_project2_name(allMonthlyAir.get(0).getContractCode());
        body.setCico_sl_number(CQTaxRateENUM.getCqBusinessType(taxrate).getCode());
        body.setCico_ywlxtyoe_number(Optional.ofNullable(CQBusinessTypeENUM.getCqBusinessType(ProjectBizType.of(allMonthlyAir.get(0).getBizType()), LeaseType.of(allMonthlyAir.get(0).getLeaseType()))).map(CQBusinessTypeENUM::getCode).orElse(null));
        return body;
    }


    public void sendCostList(MonthlyCostRSP monthlyCostRSPS, OrgDO orgDO, String yearAndMonth, Client client, ContractBaseInfo contractBaseInfo, String description){
        if(ObjectUtil.hasEmpty(monthlyCostRSPS, orgDO, client, yearAndMonth, contractBaseInfo)) {
            return;
        }
        LocalDate now = LocalDate.now();
        String nowFormat = now.format(DateTimeFormatter.ofPattern(DatePattern.NORM_DATE_PATTERN));
        CQ2AccountApplicationVO vo = new CQ2AccountApplicationVO();
        if (ProjectBizType.ZL.name().equals(contractBaseInfo.getBizType())){
            if(LeaseType.zhi_zu.name().equals(contractBaseInfo.getLeaseType())) {
                vo.setDescription(String.format("%s(%s)", description, LeaseType.zhi_zu.getDisplay()));
            } else if (LeaseType.hui_zu.name().equals(contractBaseInfo.getLeaseType())) {
                vo.setDescription(String.format("%s(%s)", description, LeaseType.hui_zu.getDisplay()));
            } else {
                vo.setDescription(String.format("%s(%s)", description, LeaseType.jyx_zu.getDisplay()));
            }
        } else {
            vo.setDescription(String.format("%s(%s)", description, Optional.ofNullable(ProjectBizType.of(contractBaseInfo.getBizType())).map(ProjectBizType::display).orElse(ProjectBizType.ZL.display)));
        }
        vo.setBizdate(nowFormat);

        vo.setTallydate(getTallydate(yearAndMonth));

        vo.setCico_customer(client.getClientCode());
        vo.setCico_taxcategory("normal");//不确定
        vo.setMainbiztype_number("JTZB001");//报账业务类型.编码
        vo.setDept_number(String.valueOf(orgDO.getMainOrgId()));
        vo.setCico_period_number(yearAndMonth.replaceAll("-", ""));
        vo.setCico_sourcebillno(String.join("-", monthlyCostRSPS.getOrganizationName() + UUIDUtil.genUuid()));//
        //todo
        OrgDO newOrgDO = orgDOMapper.queryByCode("ZJGLB");
        if (ObjectUtil.isNotNull(newOrgDO) && ObjectUtil.isNotNull(newOrgDO.getMainOrgId())) {
            vo.setDept_number(String.valueOf(newOrgDO.getMainOrgId()));
        }
        BigDecimal taxrate;
        //税率
        if (ProjectBizType.ZL.name().equals(contractBaseInfo.getBizType()) && LeaseType.zhi_zu.name().equals(contractBaseInfo.getLeaseType())) {
            taxrate = BigDecimal.valueOf(0.13);
        } else {
            taxrate = BigDecimal.valueOf(0.06);
        }
        List<CQ2AccountApplicationVO.CQ2AccountApplicationVOBody> bodys = new ArrayList<>();
        bodys.add(getCostListBody(vo, monthlyCostRSPS, taxrate, client, orgDO, yearAndMonth,contractBaseInfo));
        vo.setTallyentryentity(bodys);
        //项目端剩余本金法
        if ("DK".equalsIgnoreCase(monthlyCostRSPS.getBusinessType())){
            vo.setSource(ExceptionSourceENUM.ASSET_SIDE_COST_DK.name());
        } else {
            vo.setSource(ExceptionSourceENUM.ASSET_SIDE_COST_ZR.name());
        }
        vo.setBusinessKey(String.valueOf(monthlyCostRSPS.getId()));
        vo.setBusinessTitle(monthlyCostRSPS.getFinancingCode());
        SpringContextHolder.getBean(FinancialManagerServiceImpl2.class).cq2AccountApplicationExec(vo);
    }

    private CQ2AccountApplicationVO.CQ2AccountApplicationVOBody getCostListBody(CQ2AccountApplicationVO vo, MonthlyCostRSP monthlyCostRSPS, BigDecimal taxrate, Client client,OrgDO orgDO,String yearAndMonth, ContractBaseInfo contractBaseInfo) {
        CQ2AccountApplicationVO.CQ2AccountApplicationVOBody body = vo.new CQ2AccountApplicationVOBody();
        //记账期间 起租日的年月
        vo.setCico_period_number(yearAndMonth);
        String[] split = yearAndMonth.split("-");
        YearMonth yearMonth = YearMonth.of(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
        LocalDate lastDayOfMonth = yearMonth.atEndOfMonth();
        body.setBusinessdate(lastDayOfMonth.format(DateTimeFormatter.ofPattern(DatePattern.NORM_DATE_PATTERN)));
        //项目起租：租金总额
        body.setTallyexplanation(CQAccountApplicationTypeENUM.ACCRUE_FINANCING_COST.getDisplay());

        //含税利息收入
        body.setCico_amount(LongUtil.tenThousand2Dollar(String.valueOf(LongUtil.null2zero(monthlyCostRSPS.getTermCapitalCost()))).setScale(4, RoundingMode.HALF_UP));
        //不含税金额
        body.setCico_bhsje(LongUtil.tenThousand2Dollar(String.valueOf(LongUtil.null2zero(monthlyCostRSPS.getTermCapitalCostAfterTax()))).setScale(4, RoundingMode.HALF_UP));
        //含税-不含税
        body.setCico_se(body.getCico_amount().subtract(body.getCico_bhsje()));//不含税金额*税率 todo
        body.setCico_contract_num(contractBaseInfo.getContractCode());
        body.setCico_custname(client.getClientCode());
        body.setCico_hsje(body.getCico_amount());
        body.setTallyamount(body.getCico_amount());
        body.setCico_financialins(client.getClientCode());
        body.setTallydeptid_number(String.valueOf(orgDO.getMainOrgId()));
        body.setCustomer_number(client.getClientCode());
        //body.setCico_project2_number(allMonthlyAir.get(0).getContractCode());
        body.setCico_project2_name(contractBaseInfo.getContractCode());
        body.setCico_sl_number(CQTaxRateENUM.getCqBusinessType(taxrate).getCode());
        body.setCico_ywlxtyoe_number(Optional.ofNullable(CQBusinessTypeENUM.getCqBusinessType(ProjectBizType.of(contractBaseInfo.getBizType()), LeaseType.of(contractBaseInfo.getLeaseType()))).map(CQBusinessTypeENUM::getCode).orElse(null));
        return body;
    }

    public void sendStampDuty(MonthlyStampDutyProjRSP stampDutyProjRSP, OrgDO orgDO, Client client, String yearAndMonth, ContractBaseInfo contractBaseInfo){
        if(ObjectUtil.hasEmpty(stampDutyProjRSP, orgDO, client, yearAndMonth, contractBaseInfo)) {
            return;
        }
        LocalDate now = LocalDate.now();
        String nowFormat = now.format(DateTimeFormatter.ofPattern(DatePattern.NORM_DATE_PATTERN));
        CQ2AccountApplicationVO vo = new CQ2AccountApplicationVO();
        vo.setDescription(CQAccountApplicationTypeENUM.ACCRUE_STAMP_DUTY.getDisplay());
        vo.setBizdate(nowFormat);

        vo.setTallydate(getTallydate(yearAndMonth));

        vo.setCico_customer(client.getClientName());
        vo.setCico_taxcategory("normal");//不确定
        vo.setMainbiztype_number("JTZB001");//报账业务类型.编码
        vo.setDept_number(String.valueOf(orgDO.getMainOrgId()));
        vo.setCico_period_number(yearAndMonth.replaceAll("-", ""));
        vo.setCico_sourcebillno(String.join("-", contractBaseInfo.getProjName() + UUIDUtil.genUuid()));//
        //todo
        if (ObjectUtil.isNotNull(contractBaseInfo.getId())) {
            List<ContractAssessDeptDetail> deptDetailList = contractAssessDeptDetailMapper.selectList(Wrappers.<ContractAssessDeptDetail>lambdaQuery()
                    .eq(ContractAssessDeptDetail::getContractId, contractBaseInfo.getId())
                    .eq(ContractAssessDeptDetail::getDeleted, YesOrNoNumberEnum.NO.getCode()));
            if (!deptDetailList.isEmpty()) {
                ContractAssessDeptDetail contractAssessDeptDetail = deptDetailList.get(0);
                Long deptId = contractAssessDeptDetail.getAssessDeptId();
                OrgDO newOrgDO = orgDOMapper.selectByPrimaryKey(deptId);
                if (newOrgDO != null && ObjectUtil.isNotNull(newOrgDO.getMainOrgId())) {
                    vo.setDept_number(String.valueOf(newOrgDO.getMainOrgId()));
                }
            }
        }
        BigDecimal taxrate;
        //税率
        if (ProjectBizType.ZL.name().equals(contractBaseInfo.getBizType()) && LeaseType.zhi_zu.name().equals(contractBaseInfo.getLeaseType())) {
            taxrate = BigDecimal.valueOf(0.13);
        } else {
            taxrate = BigDecimal.valueOf(0.06);
        }
        List<CQ2AccountApplicationVO.CQ2AccountApplicationVOBody> bodys = new ArrayList<>();
        bodys.add(getStampDutyBody(vo, taxrate, stampDutyProjRSP, client, orgDO, yearAndMonth, contractBaseInfo));
        vo.setTallyentryentity(bodys);
        //印花税 项目端
        vo.setSource(ExceptionSourceENUM.ASSET_SIDE_COST_STAMP_DUTY.name());
        vo.setBusinessKey(String.valueOf(stampDutyProjRSP.getId()));
        vo.setBusinessTitle(stampDutyProjRSP.getContractCode());
        financialManagerServiceImpl2.cq2AccountApplicationExec(vo);
    }

    private CQ2AccountApplicationVO.CQ2AccountApplicationVOBody getStampDutyBody(CQ2AccountApplicationVO vo, BigDecimal taxrate, MonthlyStampDutyProjRSP stampDutyProjRSP, Client client,OrgDO orgDO, String yearAndMonth, ContractBaseInfo contractBaseInfo ) {
        CQ2AccountApplicationVO.CQ2AccountApplicationVOBody body = vo.new CQ2AccountApplicationVOBody();
        //记账期间 起租日的年月
        vo.setCico_period_number(yearAndMonth);

        String[] split = yearAndMonth.split("-");
        YearMonth yearMonth = YearMonth.of(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
        LocalDate lastDayOfMonth = yearMonth.atEndOfMonth();
        body.setBusinessdate(lastDayOfMonth.format(DateTimeFormatter.ofPattern(DatePattern.NORM_DATE_PATTERN)));

        //项目起租：租金总额
        body.setTallyexplanation(CQAccountApplicationTypeENUM.ACCRUE_STAMP_DUTY.getDisplay());
        //含税利息收入
        body.setCico_amount(LongUtil.tenThousand2Dollar(String.valueOf(LongUtil.null2zero(stampDutyProjRSP.getStampDuty()))).setScale(4, RoundingMode.HALF_UP));
        //不含税金额
        body.setCico_bhsje(LongUtil.tenThousand2Dollar(String.valueOf(LongUtil.null2zero(stampDutyProjRSP.getStampDuty()))).setScale(4, RoundingMode.HALF_UP));
        //含税-不含税
        body.setCico_se(body.getCico_amount().subtract(body.getCico_bhsje()));//不含税金额*税率 todo
        body.setCico_contract_num(contractBaseInfo.getContractCode());
        body.setCico_custname(client.getClientCode());
        body.setCico_hsje(body.getCico_amount());
        body.setTallyamount(body.getCico_amount());
        body.setCico_financialins(client.getClientCode());
        body.setTallydeptid_number(String.valueOf(orgDO.getMainOrgId()));
        body.setCustomer_number(client.getClientCode());
        //body.setCico_project2_number(allMonthlyAir.get(0).getContractCode());
        body.setCico_sl_number(CQTaxRateENUM.getCqBusinessType(taxrate).getCode());
        body.setCico_project2_name(contractBaseInfo.getContractCode());
        body.setCico_ywlxtyoe_number(Optional.ofNullable(CQBusinessTypeENUM.getCqBusinessType(ProjectBizType.of(contractBaseInfo.getBizType()), LeaseType.of(contractBaseInfo.getLeaseType()))).map(CQBusinessTypeENUM::getCode).orElse(null));
        return body;
    }


    public void sendFinPage(MonthlyStampDutyFinRSP stampDutyProjRSP, OrgDO orgDO, Client client, String yearAndMonth, ContractBaseInfo contractBaseInfo){
        if(ObjectUtil.hasEmpty(stampDutyProjRSP, orgDO, client, yearAndMonth, contractBaseInfo)) {
            return;
        }
        LocalDate now = LocalDate.now();
        String nowFormat = now.format(DateTimeFormatter.ofPattern(DatePattern.NORM_DATE_PATTERN));
        CQ2AccountApplicationVO vo = new CQ2AccountApplicationVO();
        vo.setDescription(CQAccountApplicationTypeENUM.ACCRUE_STAMP_DUTY.getDisplay());
        vo.setBizdate(nowFormat);

        vo.setTallydate(getTallydate(yearAndMonth));

        vo.setCico_customer(client.getClientName());
        vo.setCico_taxcategory("normal");//不确定
        vo.setMainbiztype_number("JTZB001");//报账业务类型.编码
        vo.setDept_number(String.valueOf(orgDO.getMainOrgId()));
        vo.setCico_period_number(yearAndMonth.replaceAll("-", ""));
        vo.setCico_sourcebillno(String.join("-", contractBaseInfo.getProjName() + UUIDUtil.genUuid()));//
        //todo
        if (ObjectUtil.isNotNull(contractBaseInfo.getId())) {
            List<ContractAssessDeptDetail> deptDetailList = contractAssessDeptDetailMapper.selectList(Wrappers.<ContractAssessDeptDetail>lambdaQuery()
                    .eq(ContractAssessDeptDetail::getContractId, contractBaseInfo.getId())
                    .eq(ContractAssessDeptDetail::getDeleted, YesOrNoNumberEnum.NO.getCode()));
            if (!deptDetailList.isEmpty()) {
                ContractAssessDeptDetail contractAssessDeptDetail = deptDetailList.get(0);
                Long deptId = contractAssessDeptDetail.getAssessDeptId();
                OrgDO newOrgDO = orgDOMapper.selectByPrimaryKey(deptId);
                if (newOrgDO != null && ObjectUtil.isNotNull(newOrgDO.getMainOrgId())) {
                    vo.setDept_number(String.valueOf(newOrgDO.getMainOrgId()));
                }
            }
        }
        BigDecimal taxrate;
        //税率
        if (ProjectBizType.ZL.name().equals(contractBaseInfo.getBizType()) && LeaseType.zhi_zu.name().equals(contractBaseInfo.getLeaseType())) {
            taxrate = BigDecimal.valueOf(0.13);
        } else {
            taxrate = BigDecimal.valueOf(0.06);
        }
        List<CQ2AccountApplicationVO.CQ2AccountApplicationVOBody> bodys = new ArrayList<>();
        bodys.add(getFinPageBody(vo, taxrate, stampDutyProjRSP, client, orgDO, yearAndMonth, contractBaseInfo));
        vo.setTallyentryentity(bodys);
        //印花税 项目端
        vo.setSource(ExceptionSourceENUM.FINANCE_SIDE_COST_STAMP_DUTY.name());
        vo.setBusinessKey(String.valueOf(stampDutyProjRSP.getId()));
        vo.setBusinessTitle(stampDutyProjRSP.getFinancingCode());
        financialManagerServiceImpl2.cq2AccountApplicationExec(vo);
    }

    private String getTallydate(String yearAndMonth){
        String[] split = yearAndMonth.split("-");
        YearMonth yearMonth = YearMonth.of(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
        LocalDate lastDayOfMonth = yearMonth.atEndOfMonth();
        return lastDayOfMonth.format(DateTimeFormatter.ofPattern(DatePattern.NORM_DATE_PATTERN));
    }

    private CQ2AccountApplicationVO.CQ2AccountApplicationVOBody getFinPageBody(CQ2AccountApplicationVO vo, BigDecimal taxrate, MonthlyStampDutyFinRSP stampDutyProjRSP, Client client,OrgDO orgDO, String yearAndMonth, ContractBaseInfo contractBaseInfo ) {
        CQ2AccountApplicationVO.CQ2AccountApplicationVOBody body = vo.new CQ2AccountApplicationVOBody();
        //记账期间 起租日的年月
        vo.setCico_period_number(yearAndMonth);
        String[] split = yearAndMonth.split("-");
        YearMonth yearMonth = YearMonth.of(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
        LocalDate lastDayOfMonth = yearMonth.atEndOfMonth();
        body.setBusinessdate(lastDayOfMonth.format(DateTimeFormatter.ofPattern(DatePattern.NORM_DATE_PATTERN)));
        //项目起租：租金总额
        body.setTallyexplanation(CQAccountApplicationTypeENUM.ACCRUE_STAMP_DUTY.getDisplay());
        //含税利息收入
        body.setCico_amount(LongUtil.tenThousand2Dollar(String.valueOf(LongUtil.null2zero(stampDutyProjRSP.getStampDuty()))).setScale(4, RoundingMode.HALF_UP));
        //不含税金额
        body.setCico_bhsje(LongUtil.tenThousand2Dollar(String.valueOf(LongUtil.null2zero(stampDutyProjRSP.getStampDuty()))).setScale(4, RoundingMode.HALF_UP));
        //含税-不含税
        body.setCico_se(body.getCico_amount().subtract(body.getCico_bhsje()));//不含税金额*税率 todo
        body.setCico_contract_num(contractBaseInfo.getContractCode());
        body.setCico_custname(client.getClientCode());
        body.setCico_hsje(body.getCico_amount());
        body.setTallyamount(body.getCico_amount());
        body.setCico_financialins(client.getClientCode());
        body.setTallydeptid_number(String.valueOf(orgDO.getMainOrgId()));
        body.setCustomer_number(client.getClientCode());
        //body.setCico_project2_number(allMonthlyAir.get(0).getContractCode());
        body.setCico_sl_number(CQTaxRateENUM.getCqBusinessType(taxrate).getCode());
        body.setCico_project2_name(contractBaseInfo.getContractCode());
        body.setCico_ywlxtyoe_number(Optional.ofNullable(CQBusinessTypeENUM.getCqBusinessType(ProjectBizType.of(contractBaseInfo.getBizType()), LeaseType.of(contractBaseInfo.getLeaseType()))).map(CQBusinessTypeENUM::getCode).orElse(null));
        return body;
    }

}
