package cn.zswltech.mithras.service.service.fund.financial;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.StrUtil;
import cn.zswltech.mithras.contract.enums.contract.LPRTypeEnum;
import cn.zswltech.mithras.service.enums.fund.financial.FundFinancialSystemEnum;
import cn.zswltech.mithras.service.enums.fund.financing.FundFinancingBizTypeEnum;
import cn.zswltech.mithras.service.enums.fund.financing.FundFinancingStatusEnum;
import cn.zswltech.mithras.service.enums.fund.financing.LprAdjustmentDayEnum;
import cn.zswltech.mithras.service.enums.fund.financing.LprArrangeModeEnum;
import cn.zswltech.mithras.service.enums.projestablish.RateType;
import cn.zswltech.mithras.service.mapper.model.fund.FundOrganization;
import cn.zswltech.mithras.service.mapper.model.fund.financing.FundFinancingBaseInfo;
import cn.zswltech.mithras.service.mapper.model.fund.financing.FundFinancingPlan;
import cn.zswltech.mithras.service.mapper.model.fund.financing.FundFinancingRepayActual;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.others.Util;
import cn.zswltech.mithras.service.service.fund.FundOrganizationService;
import cn.zswltech.mithras.service.service.fund.financial.dto.FinancialSystemSubmitQuery;
import cn.zswltech.mithras.service.service.fund.financing.FundFinancingBaseInfoService;
import cn.zswltech.mithras.service.service.fund.financing.FundFinancingPayAccountService;
import cn.zswltech.mithras.service.service.fund.financing.FundFinancingPlanService;
import cn.zswltech.mithras.service.service.fund.financing.FundFinancingRepayActualService;
import cn.zswltech.mithras.service.util.StringUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author chenyifei
 */

@Slf4j
@Service
public class FinancialSystemContractService extends FinancialSystemService{

    @Resource
    private FundFinancingBaseInfoService financingBaseInfoService;
    @Resource
    private FundFinancingPlanService financingPlanService;
    @Resource
    private FundFinancingPayAccountService financingPayAccountService;
    @Resource
    private FundOrganizationService organizationService;
    @Resource
    private FundFinancingRepayActualService financingRepayActualService;

    private List<String> errorInfoList = new ArrayList<>();



    @Override
    protected FundFinancialSystemEnum getModule() {
        return FundFinancialSystemEnum.CONTRACT;
    }

    @Override
    protected FinancialSystemSubmitQuery buildRequest() {
        this.errorInfoList = new ArrayList<>();
        FinancialSystemSubmitQuery result = new FinancialSystemSubmitQuery();
        List<FinancialSystemSubmitQuery.ContractBody> bodyList = new ArrayList<>();
        result.setCwgsHead(getMessageHead());
        result.setCwgsApiAppUser(getAppHead());
        // 处理间融
//        List<FundFinancingBaseInfo> financingBaseInfoList = financingBaseInfoService.list(Wrappers.<FundFinancingBaseInfo>lambdaQuery()
//                .eq(FundFinancingBaseInfo::getFinancingStatus, FundFinancingStatusEnum.CARRY_INTEREST.name())
//                .notIn(FundFinancingBaseInfo::getBusinessType, FundFinancingBizTypeEnum.BANK_ACCEPTANCE.name(), FundFinancingBizTypeEnum.COMMERCE_ACCEPTANCE.name(), FundFinancingBizTypeEnum.LETTER_OF_CREDIT.name()));
        // 调整取数逻辑，当月结清的也要取出来
        LocalDate now = LocalDate.now();
        List<FundFinancingBaseInfo> financingBaseInfoList = financingBaseInfoService.listFinancialSystemTodoList(LocalDate.of(now.getYear(), now.getMonthValue(), now.lengthOfMonth()));
        if(CollectionUtil.isEmpty(financingBaseInfoList)){
            throw new MithrasException("不存在需要推送的间融数据");
        }
        List<Long> financingIdList = financingBaseInfoList.stream().map(FundFinancingBaseInfo::getId).collect(Collectors.toList());
        Map<Long, List<FundOrganization>> orgMap = organizationService.getBatchByFinancingId(financingIdList);
        Map<Long, FundFinancingPlan> planMap = financingPlanService.getMapByFinancingIds(financingIdList);
        Map<Long, String> payAccountMap = financingPayAccountService.queryRepayPrincipal(financingIdList);
        Map<Long, List<FundFinancingRepayActual>> repayActualMap = financingRepayActualService.getMapByFinancingId(financingIdList);
        for (FundFinancingBaseInfo financingBaseInfo : financingBaseInfoList) {
            FinancialSystemSubmitQuery.ContractBody body = new FinancialSystemSubmitQuery.ContractBody();
            List<String> errorInfo = new ArrayList<>();
            FundFinancingPlan financingPlan = planMap.get(financingBaseInfo.getId());
            List<FundFinancingRepayActual> repayActualList = repayActualMap.get(financingBaseInfo.getId());
            if(financingPlan == null){
                errorInfo.add("未找到融资方案");
            } else if(Arrays.asList(LprArrangeModeEnum.YEAR.name(), LprArrangeModeEnum.MONTH_DECEMBER.name()).contains(financingPlan.getLprArrangeMode())
                    && CollectionUtil.isEmpty(repayActualList)){
                errorInfo.add("未找到实际租金表");
            }
            List<FundOrganization> organizationList = orgMap.get(financingBaseInfo.getId());
            if(organizationList == null){
                errorInfo.add("未找到融资对应机构信息");
            }
            String payAccount = payAccountMap.get(financingBaseInfo.getId());
            if(payAccount == null){
                errorInfo.add("未找到融资对应的还本账户");
            }

            if(CollectionUtil.isNotEmpty(errorInfo)) {
                String error = String.format("融资编号:%s -> %s", financingBaseInfo.getFinancingCode(), String.join(",", errorInfo));
                log.warn(error);
                errorInfoList.add(error);
                continue;
            }
            body.setORG_ID("ZSZL")
                    .setCUST_NO("ZSZL")
                    .setCUST_NAME("浙商租赁")
                    .setBUSI_TYP_CD(Optional.ofNullable(FundFinancingBizTypeEnum.finaByName(financingBaseInfo.getBusinessType())).map(FundFinancingBizTypeEnum::getFinancialSystemCode).orElse(null))
                    .setBUSI_CONTR_NO(financingBaseInfo.getFinancingCode())
                    .setMAIN_CONTR_NO(financingBaseInfo.getFinancingCode())
                    .setCONTR_START_DATE(Optional.ofNullable(financingBaseInfo.getActualLoanDate()).map(m -> m.format(DateTimeFormatter.ofPattern("yyyyMMdd"))).orElse(null))
                    .setCONTR_END_DATE(Optional.ofNullable(financingBaseInfo.getActualExpireDate()).map(m -> m.format(DateTimeFormatter.ofPattern("yyyyMMdd"))).orElse(null))
                    .setCCY_CD("CNY")
                    .setCONTR_AMT(Optional.ofNullable(financingBaseInfo.getFinancingAmount()).map(Util::toYuanWithoutSplit).orElse(null))
                    .setINTRT_ADJ_WAY_CD("lpr")
                    .setEXEC_RATE(Util.toRateWithoutSplit(Optional.ofNullable(financingPlan.getLprRatePercent()).orElse(0) + Optional.ofNullable(financingPlan.getLprAddPercent()).orElse(0)))
                    //.setLN_USE(financingBaseInfo.getFundsPurpose())
                    .setLN_USE("项目投放")
                    .setSIGNING_DATE(Optional.ofNullable(financingBaseInfo.getActualLoanDate()).map(m -> m.format(DateTimeFormatter.ofPattern("yyyyMMdd"))).orElse(null))
                    .setCORP_PRIV_FLG("1")
                    .setLILVFDFS(Optional.ofNullable(RateType.of(financingPlan.getInterestRateType())).map(RateType::getFinancialSystemCode).orElse(null))
                    .setLILVTZGZ(Optional.ofNullable(LprArrangeModeEnum.of(financingPlan.getLprArrangeMode())).map(LprArrangeModeEnum::getFinancialSystemCode).orElse("0"));
            String orgName = organizationList.stream().map(FundOrganization::getOrganizationName).distinct().collect(Collectors.joining(";"));
            String orgFirstLetterName = organizationList.stream().map(FundOrganization::getOrganizationName)
                    .distinct().map(StringUtil::getFirstLetters).collect(Collectors.joining(";"));
            if(Arrays.asList(LprArrangeModeEnum.YEAR.name(), LprArrangeModeEnum.MONTH_DECEMBER.name()).contains(financingPlan.getLprArrangeMode())){
                if(CharSequenceUtil.isNotEmpty(financingPlan.getLprAdjustmentDay())) {
                    LocalDate repayDate = null;
                    /*对应起息日*/
                    if(Objects.equals(financingPlan.getLprAdjustmentDay(),LprAdjustmentDayEnum.CARRY_INTEREST_DAY.name())){
                        try {
                            repayDate = repayActualList.stream()
                                    .map(FundFinancingRepayActual::getRepayDate)
                                    .filter(f -> f.isAfter(financingBaseInfo.getActualLoanDate().plusYears(1)))
                                    .filter(Objects::nonNull).findFirst().get();
                        } catch(NoSuchElementException noSuchElementException){
                            FundFinancingRepayActual repayActual = repayActualList.stream().max(Comparator.comparing(FundFinancingRepayActual::getRepayDate)).get();
                            if(repayActual != null){
                                repayDate = repayActual.getRepayDate();
                            }
                        } catch(Exception e){
                            log.error("获取利率生效时间失败",e);
                        }
                    } else {
                        String lprAdjustmentDay = financingPlan.getLprAdjustmentDay();
                        String[] split = lprAdjustmentDay.split("-");
                        if (split.length == 2) {
                            int month = Integer.parseInt(split[0]);
                            int day = Integer.parseInt(split[1]);
                            LocalDate localDate = LocalDate.of(LocalDate.now().getYear(), month, 1);
                            int lengthDay = localDate.lengthOfMonth();
                            if (day > lengthDay) {
                                day = lengthDay;
                            }
                            repayDate = LocalDate.of(LocalDate.now().getYear(), month, day);
                        }
                    }
                    if(repayDate != null) {
                        body.setLILVTZSXY(String.valueOf(repayDate.getDayOfMonth()));
                        body.setLILVTZSXR(String.valueOf(repayDate.getMonthValue()));
                    }else {
                        String error = String.format("融资编号:%s -> %s", financingBaseInfo.getFinancingCode(), "获取利率生效时间失败");
                        errorInfoList.add(error);
                        continue;
                    }
                }
            }

            body.setDKZQRZJG(orgName)
                    .setDKZQRZJGW(orgFirstLetterName)
                    .setTKZHANGHAO(payAccount)
                    .setLILVLEIX(Optional.ofNullable(LPRTypeEnum.valueOf(financingPlan.getLprType())).map(LPRTypeEnum::getFinancialSystemCode).orElse("1"))
                    .setDKDWLEIB("04")
                    .setTKFANGS("01")
                    .setHUANKRI(Optional.ofNullable(financingBaseInfo.getRepayDay()).map(String::valueOf).orElse(null))
                    .setLAIYUAXT("浙商租赁");
            bodyList.add(body);
        }
        FinancialSystemSubmitQuery.Body body = new FinancialSystemSubmitQuery.Body();
        body.setList(bodyList);
        result.setBody(body);
        return result;

    }

    @Override
    protected String errorInfo() {
        return JSON.toJSONString(this.errorInfoList);
    }


}
