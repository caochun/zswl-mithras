package cn.zswltech.mithras.service.controller.third;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.third.FinancialApi;
import cn.zswltech.mithras.dto.third.financial.*;
import cn.zswltech.mithras.service.auth.aop.ThirdAuthCheck;
import cn.zswltech.mithras.service.enums.CashFlowItemEnum;
import cn.zswltech.mithras.service.enums.projestablish.LeaseType;
import cn.zswltech.mithras.service.fund.direct.service.FundDirectFinancingRepayActualSplitRecordService;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.model.PaymentBaseInfo;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.contract.ContractBaseInfoService;
import cn.zswltech.mithras.service.service.payment.PaymentBaseInfoService;
import cn.zswltech.mithras.service.service.third.financial.FinancialExtraService;
import cn.zswltech.mithras.service.service.third.financial.FinancialService;
import cn.zswltech.mithras.service.service.third.financial.impl.FinancialManagerServiceImpl2;
import cn.zswltech.mithras.service.service.third.financial.impl.FinancialServiceImpl;
import cn.zswltech.mithras.third.financialshare.infrastructure.client.resp.CQ2WithdrawRSP;
import cn.zswltech.mithras.third.financialshare.application.dto.CQ2WithdrawVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName FinancialController
 * @Description
 * @Author jackerhe
 * @Date 2022/10/17 5:08 下午
 * @Version 1.0
 **/
@RestController
@Slf4j
public class FinancialController implements FinancialApi {
    @Resource
    private FinancialService financialService;
    @Resource
    private FinancialExtraService financialExtraService;
    @Resource
    private PaymentBaseInfoService paymentBaseInfoService;
    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private FinancialManagerServiceImpl2 financialManagerServiceImpl2;

    @ThirdAuthCheck
    @Override
    public R<String> paymentRecode(@Valid ThirdPaymentDetailREQ req) {
        req.setReqFromCq(true);
        // 根据合同类型判断是否需要接收处理
        PaymentBaseInfo paymentBaseInfo = paymentBaseInfoService.getOneByPaymentCode(req.getCollectionCode());
        if (Objects.isNull(paymentBaseInfo)) {
            log.error("核销失败，没有找到对应的付款申请信息[{}]", JSONUtil.toJsonStr(req));
            return R.fail("核销失败，没有找到对应的付款申请信息");
        }
        ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(paymentBaseInfo.getContractId());
        if (Objects.isNull(contractBaseInfo)) {
            log.error("核销失败，没有找到对应的合同信息[{}]", JSONUtil.toJsonStr(req));
            return R.fail("核销失败，没有找到对应的合同信息");
        }
        req.setReqNeedHandle(Objects.equals(contractBaseInfo.getLeaseType(), LeaseType.zhi_zu.name()));
        return financialExtraService.paymentRecodeExtra(req);
    }

    @ThirdAuthCheck
    @Override
    public R<String> collectionRecode(@Valid ThirdCollectionRecordREQ req) {
        log.info("收款记录明细入参{}", JSONUtil.toJsonStr(req));
        //校验罚息
        if (CashFlowItemEnum.RENT.name().equals(req.getCashFlowItem())) {
            if (!NumberUtil.equals(req.getCollectionAmount(), NumberUtil.add(req.getPrincipal(), req.getInterest(), req.getPenaltyInterest()))) {
                throw new MithrasException("收款金额不等于各项金额之和");
            }
        }
        req.setDataSource(FinancialServiceImpl.CWXT);
        return financialService.collectionRecode(req);
    }

    @ThirdAuthCheck
    @Override
    public R<String> addBackRecord(@Valid ThirdMarginRecordREQ req) {
        log.info("保证金管理-内扣/退回入参{}", JSONUtil.toJsonStr(req));
        return financialService.backRecord(req);
    }


    @Override
    public R<Void> innerRecord(InnerCollectionRecordREQ req) {
        financialService.innerRecord(req);
        return R.ok();
    }

    @Override
    public R<List<ThirdFinancialWithdrawRSP>> withdraw(List<ThirdFinancialWithdrawREQ> reqs) {
        List<ThirdFinancialWithdrawRSP> rsps = new ArrayList<>();
        CQ2WithdrawRSP cq2WithdrawRSP = null;
        Set<Long> existIds = new HashSet<>();
        List<ThirdFinancialWithdrawREQ.FundDirectRepayActualSplitRecordInfo> filterList = new LinkedList<>();
        for(ThirdFinancialWithdrawREQ req : reqs) {
            if (CollectionUtil.isNotEmpty(req.getDirectRepaySplitList())) {
                // 为了改动较小，先去重，因为数据结构的缘故，前端会在每一个ThirdFinancialWithdrawREQ中放入全量的directRepaySplitList
                // 比较合理的方式是把请求参数改成object，里面放入两个list，而不是把直融拆分核销明细加到ThirdFinancialWithdrawREQ中
                req.getDirectRepaySplitList().forEach(e -> {
                    if (existIds.contains(e.getId())) {
                        return;
                    }
                    filterList.add(e);
                    existIds.add(e.getId());
                });
            }
        }
        if (CollectionUtil.isNotEmpty(filterList)) {
            List<Long> fundReceiptFlowDetailIds = reqs.stream().map(e -> Long.valueOf(e.getBusinessKey())).distinct().collect(Collectors.toList());
            SpringUtil.getBean(FundDirectFinancingRepayActualSplitRecordService.class).withdraw(fundReceiptFlowDetailIds, filterList);
        }
        for(ThirdFinancialWithdrawREQ req : reqs) {
            cq2WithdrawRSP = financialManagerServiceImpl2.cq2Withdraw(BeanUtil.copyProperties(req, CQ2WithdrawVO.class));
            if(ObjectUtil.isEmpty(cq2WithdrawRSP)) {
                cq2WithdrawRSP = new CQ2WithdrawRSP();
                cq2WithdrawRSP.setSuccess(true);
                cq2WithdrawRSP.setStatus(true);
            }
            rsps.add(BeanUtil.copyProperties(cq2WithdrawRSP, ThirdFinancialWithdrawRSP.class));
        }
        return R.ok(rsps);
    }
}
