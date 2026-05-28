package cn.zswltech.mithras.service.service.contract.operationprepare.impl;

import cn.hutool.core.lang.Assert;
import cn.zswltech.mithras.service.enums.contract.ContractOperationEnum;
import cn.zswltech.mithras.service.enums.contract.ContractProcessStatusEnum;
import cn.zswltech.mithras.service.enums.payment.PaymentStatusEnum;
import cn.zswltech.mithras.service.enums.projestablish.LeaseType;
import cn.zswltech.mithras.service.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.service.mapper.model.payment.PaymentBaseInfo;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.contract.operationprepare.AbstractContractOperationPrepare;
import cn.zswltech.mithras.service.service.payment.PaymentBaseInfoService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author dingqi
 * @date 2023/4/3
 * @description 准备发起合同起租
 */
@Component
public class StartRentPrepare extends AbstractContractOperationPrepare {
    @Resource
    private PaymentBaseInfoService paymentBaseInfoService;
    
    @Override
    protected void doPrepare(ContractBaseInfo contractBaseInfo) {
//        if (!Objects.equals(contractBaseInfo.getLeaseType(), LeaseType.zhi_zu.name())) {
//            throw new MithrasException("仅直租合同支持该种方式起租");
//        }
        if (Objects.equals(ContractProcessStatusEnum.START_RENT_UNCOMMIT.name(), contractBaseInfo.getContractProcessStatus())) {
            return;
        }
        LambdaQueryWrapper<PaymentBaseInfo> paymentQuery = Wrappers.lambdaQuery();
        paymentQuery.eq(PaymentBaseInfo::getContractId, contractBaseInfo.getId());
        paymentQuery.in(PaymentBaseInfo::getPaymentStatus, Arrays.asList(PaymentStatusEnum.TAKE_EFFECT.name(), PaymentStatusEnum.FINISHED.name()));
        List<PaymentBaseInfo> paymentBaseInfoList = paymentBaseInfoService.list(paymentQuery);
        Assert.notEmpty(paymentBaseInfoList, () -> MithrasException.newException("不存在生效的付款申请"));
        if (!Objects.equals(contractBaseInfo.getLeaseType(), LeaseType.zhi_zu.name())) {
            // 非直租需判断付款申请是否被使用
            List<PaymentBaseInfo> filterList = paymentBaseInfoList.stream().filter(e -> Objects.isNull(e.getReceiptId())).collect(Collectors.toList());
            Assert.notEmpty(filterList, () -> MithrasException.newException("不存在可被关联的生效付款申请"));
        }
        Assert.isTrue(ContractProcessStatusEnum.canDoStatus().contains(contractBaseInfo.getContractProcessStatus()), () -> MithrasException.newException("当前合同流状态不允许发起该操作"));
        contractBaseInfoService.updateContractProcessStatus(ContractProcessStatusEnum.START_RENT_UNCOMMIT.name(), null, contractBaseInfo.getId());
    }

    @Override
    public ContractOperationEnum operationScene() {
        return ContractOperationEnum.START_RENT;
    }
}
