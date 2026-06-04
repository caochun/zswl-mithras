package cn.zswltech.mithras.service.service.fund.financial;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.contract.enums.contract.LPRTypeEnum;
import cn.zswltech.mithras.contract.enums.contract.RepayRateEnum;
import cn.zswltech.mithras.fund.domain.enums.financial.FundFinancialSystemEnum;
import cn.zswltech.mithras.fund.domain.enums.financing.*;
import cn.zswltech.mithras.projectprocess.enums.projestablish.RateType;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.model.FundOrganization;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.model.financing.FundFinancingBaseInfo;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.model.financing.FundFinancingPlan;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.others.Util;
import cn.zswltech.mithras.service.service.fund.FundOrganizationService;
import cn.zswltech.mithras.fund.application.financial.dto.FinancialSystemSubmitQuery;
import cn.zswltech.mithras.service.service.fund.financing.FundFinancingBaseInfoService;
import cn.zswltech.mithras.fund.application.financing.FundFinancingPayAccountService;
import cn.zswltech.mithras.service.service.fund.financing.FundFinancingPlanService;
import cn.zswltech.mithras.service.service.fund.receiptrepay.FundReceiptRepayBaseInfoService;
import cn.zswltech.mithras.service.util.StringUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author zswl
 */
@Slf4j
@Service
public class FinancialSystemReceiptService extends FinancialSystemService{

    @Resource
    private FundFinancingBaseInfoService financingBaseInfoService;
    @Resource
    private FundFinancingPlanService financingPlanService;
    @Resource
    private FundFinancingPayAccountService financingPayAccountService;
    @Resource
    private FundOrganizationService organizationService;
    @Resource
    private FundReceiptRepayBaseInfoService fundReceiptRepayBaseInfoService;

    private List<String> errorInfoList = new ArrayList<>();


    @Override
    protected FundFinancialSystemEnum getModule() {
        return FundFinancialSystemEnum.RECEIPT;
    }

    @Override
    protected FinancialSystemSubmitQuery buildRequest() {
        errorInfoList = new ArrayList<>();
        FinancialSystemSubmitQuery result = new FinancialSystemSubmitQuery();
        List<FinancialSystemSubmitQuery.ReceiptBody> bodyList = new ArrayList<>();
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
            throw new MithrasException("不存在需要推送的借据维度数据");
        }
        List<Long> financingIdList = financingBaseInfoList.stream().map(FundFinancingBaseInfo::getId).collect(Collectors.toList());
        Map<Long, List<FundOrganization>> orgMap = organizationService.getBatchByFinancingId(financingIdList);
        Map<Long, FundFinancingPlan> planMap = financingPlanService.getMapByFinancingIds(financingIdList);
        Map<Long, String> payAccountMap = financingPayAccountService.queryRepayPrincipal(financingIdList);
        Map<Long, Long> remainingAmountMap = fundReceiptRepayBaseInfoService.queryRemainingAmount(financingIdList, FinancingTypeEnum.INDIRECT);
        for (FundFinancingBaseInfo financingBaseInfo : financingBaseInfoList) {
            FinancialSystemSubmitQuery.ReceiptBody body = new FinancialSystemSubmitQuery.ReceiptBody();
            List<String> errorInfo = new ArrayList<>();
            FundFinancingPlan financingPlan = planMap.get(financingBaseInfo.getId());
            if(financingPlan == null){
                errorInfo.add("未找到融资方案");
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
            String rateType = Optional.ofNullable(RateType.of(financingPlan.getInterestRateType())).map(RateType::getFinancialSystemCode).orElse(null);
            body.setMAIN_CONTR_NO(financingBaseInfo.getFinancingCode())
                    .setCONTR_NO(financingBaseInfo.getFinancingCode())
                    .setRCPLN_NO(financingBaseInfo.getFinancingCode())
                    .setLN_ACCT_NO(payAccount)
                    .setLN_USE("项目投放")
                    .setPRINC_NORMAL_BAL(Optional.ofNullable(remainingAmountMap.get(financingBaseInfo.getId())).map(Util::toYuanWithoutSplit).orElse(null))
                    .setMAKE_LN_DATE(Optional.ofNullable(financingBaseInfo.getActualLoanDate()).map(m -> m.format(DateTimeFormatter.ofPattern("yyyyMMdd"))).orElse(null))
                    .setORGIN_DUE_DATE(Optional.ofNullable(financingBaseInfo.getActualExpireDate()).map(m -> m.format(DateTimeFormatter.ofPattern("yyyyMMdd"))).orElse(null))
                    .setOPEN_DATE(Optional.ofNullable(financingBaseInfo.getActualLoanDate()).map(m -> m.format(DateTimeFormatter.ofPattern("yyyyMMdd"))).orElse(null))
                    .setCCY_CD("CNY")
                    .setRCPLN_AMT(Optional.ofNullable(financingBaseInfo.getFinancingAmount()).map(Util::toYuanWithoutSplit).orElse(null))
                    .setCURR_BAL(Optional.ofNullable(remainingAmountMap.get(financingBaseInfo.getId())).map(Util::toYuanWithoutSplit).orElse(null))
                    .setEXEC_RATE(Util.toRateWithoutSplit(Optional.ofNullable(financingPlan.getLprRatePercent()).orElse(0) + Optional.ofNullable(financingPlan.getLprAddPercent()).orElse(0)))
                    .setINTR_FLOAT_VALUE(Util.toRateWithoutSplit(Optional.ofNullable(financingPlan.getLprAddPercent()).orElse(0)))
                    .setREPAY_ACCT_NO(payAccount)
                    .setLN_IN_ACCT_NO(payAccount)
                    .setLILVFDFS(rateType)
                    .setLILVTZGZ(ObjectUtil.equals(RateType.FIXED.getFinancialSystemCode(), rateType) ? RateType.FIXED.getFinancialSystemCode() : Optional.ofNullable(LprArrangeModeEnum.of(financingPlan.getLprArrangeMode())).map(LprArrangeModeEnum::getFinancialSystemCode).orElse("0"))
                    .setLILVJXFS(Optional.ofNullable(RepayRateEnum.of(financingPlan.getRepayFrequency())).map(RepayRateEnum::getFinancialSystemCode).orElse("1"))
                    .setLixiclfs("1");
            //ObjectUtil.equals(RepayRateEnum.LRREGULAR.name(), financingPlan.getRepayFrequency())
            if (ObjectUtil.equals(FundFinancingRepayWayEnum.LSBQ.name(), financingPlan.getRepayWay())) {
                body.setLILVJXFS("0");
            }
            String orgName = organizationList.stream().map(FundOrganization::getOrganizationName).distinct().collect(Collectors.joining(";"));
            String orgFirstLetterName = organizationList.stream().map(FundOrganization::getOrganizationName)
                    .distinct().map(StringUtil::getFirstLetters).collect(Collectors.joining(";"));

            body.setTKZQRZJG(orgName)
                    .setTKZQRZJGWD(orgFirstLetterName)
                    .setLILVLEIX(ObjectUtil.equals(RateType.FIXED.getFinancialSystemCode(), rateType) ? "1" : Optional.ofNullable(LPRTypeEnum.valueOf(financingPlan.getLprType())).map(LPRTypeEnum::getFinancialSystemCode).orElse("1"))
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
        return JSON.toJSONString(errorInfoList);
    }


    public static void main(String[] args) {
        List<FundOrganization> organizationList = new ArrayList();
        FundOrganization organization = new FundOrganization();
        organization.setOrganizationName("啊123啊");
        FundOrganization organization1 = new FundOrganization();

        organization1.setOrganizationName("这是机构");
        organizationList.add(organization);
        organizationList.add(organization1);

        String orgFirstLetterName = organizationList.stream().map(FundOrganization::getOrganizationName)
                .distinct().map(StringUtil::getFirstLetters).collect(Collectors.joining(";"));
        System.out.println(orgFirstLetterName);

    }


}
