package cn.zswltech.mithras.others;

import cn.hutool.json.JSONUtil;
import cn.zswltech.mithras.others.service.ApplicationTest;
import cn.zswltech.mithras.service.CreditLimitManagerService;
import cn.zswltech.mithras.service.enums.CreditLimitBizTypeEnum;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.service.service.bo.*;
import org.junit.Test;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

/**
 * @author dingqi
 * @date 2024/9/4
 * @description
 */
public class CreditLimitManagerServiceTest extends ApplicationTest {
    @Resource
    private CreditLimitManagerService creditLimitManagerService;

    @Test
    public void multiOperTest() {
        String bizType = CreditLimitBizTypeEnum.FUND.name();
        String grantingSubjectKey = "testOrgId-1";
        String bizSourceKey = "testFundCreditId-1";
        String bizTargetKey = "testFinancingId-1";
        // 创建授信额度
        CreditLimitCreateBO creditLimitCreateBO = new CreditLimitCreateBO();
        creditLimitCreateBO.setBizType(bizType);
        creditLimitCreateBO.setGrantSubjectKey(grantingSubjectKey);
        creditLimitCreateBO.setBizSourceKey(bizSourceKey);
        creditLimitCreateBO.setTotalLimit(100000000000L);
        creditLimitCreateBO.setGuaranteeLimit(20000000000L);
        creditLimitCreateBO.setCreditLimit(creditLimitCreateBO.getTotalLimit() - creditLimitCreateBO.getGuaranteeLimit());
        creditLimitCreateBO.setEffectiveDateFrom(LocalDate.of(2024, 9, 12));
        creditLimitCreateBO.setEffectiveDateTo(LocalDate.of(2025, 9, 12));
        creditLimitCreateBO.setRecyclable(YesOrNoNumberEnum.YES.getCode());
        creditLimitManagerService.create(creditLimitCreateBO);
        // 占用额度
        CreditLimitOccupyBO creditLimitOccupyBO = new CreditLimitOccupyBO();
        creditLimitOccupyBO.setBizType(bizType);
        creditLimitOccupyBO.setGrantSubjectKey(grantingSubjectKey);
        creditLimitOccupyBO.setBizSourceKey(bizSourceKey);
        creditLimitOccupyBO.setBizTargetKey(bizTargetKey);
        creditLimitOccupyBO.setAmount(100000000L);
        creditLimitOccupyBO.setGuaranteeAmount(50000000L);
        creditLimitOccupyBO.setHappenDate(LocalDate.of(2024, 10, 1));
        creditLimitManagerService.occupy(creditLimitOccupyBO);
        // 释放额度
        CreditLimitReleaseBO creditLimitReleaseBO = new CreditLimitReleaseBO();
        creditLimitReleaseBO.setBizType(bizType);
        creditLimitReleaseBO.setBizTargetKey(bizTargetKey);
        creditLimitReleaseBO.setAmount(3000000L);
        creditLimitReleaseBO.setHappenDate(LocalDate.now());
        creditLimitManagerService.release(creditLimitReleaseBO);
    }

    @Test
    public void queryBatchTest() {
        String bizType = CreditLimitBizTypeEnum.FUND.name();
        String grantingSubjectKey = "testOrgId-1";
        String bizSourceKey = "testFundCreditId-1";
        List<CreditLimitQueryBO> queryList = new LinkedList<>();
        CreditLimitQueryBO creditLimitQueryBO = new CreditLimitQueryBO();
        creditLimitQueryBO.setBizType(bizType);
        creditLimitQueryBO.setGrantSubjectKey(grantingSubjectKey);
        creditLimitQueryBO.setBizSourceKey(bizSourceKey);
        queryList.add(creditLimitQueryBO);
        List<CreditLimitDetailBO> result = creditLimitManagerService.queryBatch(queryList, true);
        System.out.println(JSONUtil.toJsonStr(result));
    }

    @Test
    public void createTest() {
        String bizType = CreditLimitBizTypeEnum.FUND.name();
        String grantingSubjectKey = "testOrgId-1";
        String bizSourceKey = "testFundCreditId-5";
        // 创建授信额度
        CreditLimitCreateBO creditLimitCreateBO = new CreditLimitCreateBO();
        creditLimitCreateBO.setBizType(bizType);
        creditLimitCreateBO.setGrantSubjectKey(grantingSubjectKey);
        creditLimitCreateBO.setBizSourceKey(bizSourceKey);
        creditLimitCreateBO.setTotalLimit(10000000L);
        creditLimitCreateBO.setGuaranteeLimit(0L);
        creditLimitCreateBO.setCreditLimit(creditLimitCreateBO.getTotalLimit() - creditLimitCreateBO.getGuaranteeLimit());
        creditLimitCreateBO.setEffectiveDateFrom(LocalDate.of(2024, 10, 1));
        creditLimitCreateBO.setEffectiveDateTo(LocalDate.of(2025, 10, 1));
        creditLimitCreateBO.setRecyclable(YesOrNoNumberEnum.YES.getCode());
        creditLimitManagerService.create(creditLimitCreateBO);
    }

    @Test
    public void occupyTest() {
        String bizType = CreditLimitBizTypeEnum.FUND.name();
        String grantingSubjectKey = "testOrgId-1";
        String bizSourceKey = "testFundCreditId-5";
        String bizTargetKey = "testFinancingId-9";
        CreditLimitOccupyBO creditLimitOccupyBO = new CreditLimitOccupyBO();
        creditLimitOccupyBO.setBizType(bizType);
        creditLimitOccupyBO.setGrantSubjectKey(grantingSubjectKey);
        creditLimitOccupyBO.setBizSourceKey(bizSourceKey);
        creditLimitOccupyBO.setBizTargetKey(bizTargetKey);
        creditLimitOccupyBO.setAmount(300000000L);
        creditLimitOccupyBO.setGuaranteeAmount(200000000L);
        creditLimitOccupyBO.setHappenDate(LocalDate.of(2024, 10, 8));
        creditLimitManagerService.occupy(creditLimitOccupyBO);
    }
}
