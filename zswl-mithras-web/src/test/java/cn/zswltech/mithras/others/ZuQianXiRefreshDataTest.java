package cn.zswltech.mithras.others;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import cn.zswltech.mithras.others.service.ApplicationTest;
import cn.zswltech.mithras.foundation.enums.CashFlowItemEnum;
import cn.zswltech.mithras.foundation.enums.common.RecordStatus;
import cn.zswltech.mithras.payment.enums.PaymentStatusEnum;
import cn.zswltech.mithras.foundation.enums.LeaseType;
import cn.zswltech.mithras.foundation.persistence.model.BaseModel;
import cn.zswltech.mithras.collection.mapper.model.CollectionBaseInfo;
import cn.zswltech.mithras.contract.model.contract.*;
import cn.zswltech.mithras.payment.mapper.model.PaymentBaseInfo;
import cn.zswltech.mithras.payment.mapper.model.PaymentBaseInfoLib;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.collection.application.CollectionBaseInfoService;
import cn.zswltech.mithras.contract.core.application.ContractBaseInfoService;
import cn.zswltech.mithras.contract.core.application.ContractReceiptService;
import cn.zswltech.mithras.application.orchestration.contract.ContractRentActualService;
import cn.zswltech.mithras.application.orchestration.contract.impl.ContractRentActualServiceImpl;
import cn.zswltech.mithras.contract.archive.service.ContractReceiptLibService;
import cn.zswltech.mithras.contract.archive.service.ContractRentActualLibService;
import cn.zswltech.mithras.payment.application.lib.service.PaymentBaseInfoLibService;
import cn.zswltech.mithras.application.orchestration.payment.PaymentBaseInfoService;
import cn.zswltech.mithras.application.orchestration.contract.util.ContractUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.junit.Test;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author dingqi
 * @date 2023/4/18
 * @description
 */
public class ZuQianXiRefreshDataTest extends ApplicationTest {
    @Resource
    private TransactionTemplate transactionTemplate;
    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private ContractReceiptService contractReceiptService;
    @Resource
    private ContractReceiptLibService contractReceiptLibService;
    @Resource
    private ContractRentActualService contractRentActualService;
    @Resource
    private ContractRentActualLibService contractRentActualLibService;
    @Resource
    private PaymentBaseInfoService paymentBaseInfoService;
    @Resource
    private PaymentBaseInfoLibService paymentBaseInfoLibService;
    @Resource
    private CollectionBaseInfoService collectionBaseInfoService;

    @Test
    public void refreshReceiptData() {
        List<ContractReceipt> all = contractReceiptService.list();
        if (CollectionUtil.isEmpty(all)) {
            return;
        }
        for (ContractReceipt contractReceipt : all) {
            ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(contractReceipt.getContractId());
            if (Objects.isNull(contractBaseInfo)) {
                continue;
            }
            if (StrUtil.isNotBlank(contractReceipt.getReceiptCode())) {
                continue;
            }
            transactionTemplate.executeWithoutResult(transactionStatus -> {
                log.info("租前息历史数据修订 - 开始处理借据[id: {}, paymentCode: {}]", contractReceipt.getId(), contractReceipt.getPaymentApplyCode());
                boolean isZhiZu = Objects.equals(contractBaseInfo.getLeaseType(), LeaseType.zhi_zu.name());
                try {
                    if (isZhiZu) {
                        if (StrUtil.isBlank(contractReceipt.getPaymentApplyCode())) {
                            contractReceipt.setSequence(1);
                            contractReceipt.setReceiptCode(ContractUtil.generateReceiptCode(contractBaseInfo.getContractCode(), 1, true));
                        } else {
                            int index = contractReceipt.getPaymentApplyCode().lastIndexOf("-");
                            String s = contractReceipt.getPaymentApplyCode().substring(0, index);
                            contractReceipt.setReceiptCode(s + "-HZ");
                            contractReceipt.setSequence(1);
                        }
                    } else {
                        if (StrUtil.isBlank(contractReceipt.getPaymentApplyCode())) {
                            // 直接生成一个
                            Integer sequence = contractReceiptService.getNextSequenceByContractId(contractBaseInfo.getId());
                            contractReceipt.setSequence(sequence);
                            contractReceipt.setReceiptCode(ContractUtil.generateReceiptCode(contractBaseInfo.getContractCode(), sequence, false));
                        } else {
                            // 按照付款编号进行处理
                            contractReceipt.setReceiptCode(contractReceipt.getPaymentApplyCode());
                            // 获取序列号
                            int index = contractReceipt.getPaymentApplyCode().lastIndexOf("-");
                            String s = contractReceipt.getPaymentApplyCode().substring(index);
                            char[] chars = s.toCharArray();
                            char c = chars[chars.length - 1];
                            contractReceipt.setSequence(Integer.valueOf(String.valueOf(c)));
                        }
                    }
                    contractReceiptService.updateById(contractReceipt);
                    // 更新版本表
                    LambdaUpdateWrapper<ContractReceiptLib> updateWrapper = Wrappers.lambdaUpdate();
                    updateWrapper.set(ContractReceipt::getReceiptCode, contractReceipt.getReceiptCode());
                    updateWrapper.set(ContractReceipt::getSequence, contractReceipt.getSequence());
                    updateWrapper.eq(ContractReceiptLib::getOriginId, contractReceipt.getId());
                    contractReceiptLibService.update(updateWrapper);
                    // 直租如果有现金流编号则进行修改
                    if (isZhiZu) {
                        List<ContractRentActual> contractRentActualList = contractRentActualService.listByReceipt(contractReceipt.getId());
                        if (CollectionUtil.isNotEmpty(contractRentActualList)) {
                            for (ContractRentActual contractRentActual : contractRentActualList) {
                                if (StrUtil.isBlank(contractRentActual.getCashFlowCode())) {
                                    continue;
                                }
                                contractRentActual.setCashFlowCode(ContractRentActualServiceImpl.getCashFlowCode(contractReceipt.getReceiptCode(), contractRentActual.getCashFlowPhase()));
                            }
                            contractRentActualService.updateBatchById(contractRentActualList);
                            // 修改版本表
                            Map<Long, ContractRentActual> map = contractRentActualList.stream().collect(Collectors.toMap(ContractRentActual::getId, e -> e));
                            List<ContractRentActualLib> libList = contractRentActualLibService.listByReceipt(contractReceipt.getId());
                            for (ContractRentActualLib contractRentActualLib : libList) {
                                if (StrUtil.isBlank(contractRentActualLib.getCashFlowCode())) {
                                    continue;
                                }
                                ContractRentActual cra = map.get(contractRentActualLib.getOriginId());
                                if (Objects.isNull(cra)) {
                                    continue;
                                }
                                contractRentActualLib.setCashFlowCode(cra.getCashFlowCode());
                            }
                            contractRentActualLibService.updateBatchById(libList);
                        }
                    }
                } catch (Exception e) {
                    log.error("租前息历史数据修订 - 处理借据发生异常[id: {}, paymentCode: {}]", contractReceipt.getId(), contractReceipt.getPaymentApplyCode(), e);
                    transactionStatus.setRollbackOnly();
                }
                log.info("租前息历史数据修订 - 结束处理借据[id: {}, paymentCode: {}]", contractReceipt.getId(), contractReceipt.getPaymentApplyCode());
            });
        }
    }

    @Test
    public void refreshPayCollectData() {
        List<PaymentBaseInfo> paymentBaseInfoList = paymentBaseInfoService.list();
        if (CollectionUtil.isEmpty(paymentBaseInfoList)) {
            return;
        }
        for (PaymentBaseInfo paymentBaseInfo : paymentBaseInfoList) {
            if (Objects.equals(paymentBaseInfo.getPaymentStatus(), PaymentStatusEnum.CLOSED.name())) {
                continue;
            }
            transactionTemplate.executeWithoutResult(transactionStatus -> {
                log.info("租前息历史数据修订 - 开始处理收付款[paymentId: {}, paymengCode: {}]", paymentBaseInfo.getId(), paymentBaseInfo.getPaymentCode());
                try {
                    ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(paymentBaseInfo.getContractId());
                    Assert.notNull(contractBaseInfo, () -> MithrasException.newException("合同不存在"));
                    if (Objects.equals(contractBaseInfo.getLeaseType(), LeaseType.zhi_zu.name())) {
                        List<ContractReceipt> contractReceiptList = contractReceiptService.listByContractId(contractBaseInfo.getId());
                        Assert.notEmpty(contractReceiptList, () -> MithrasException.newException("借据不存在"));
                        Assert.isTrue(contractReceiptList.size() == 1, () -> MithrasException.newException("直租合同存在多个借据"));
                        ContractReceipt contractReceipt = contractReceiptList.get(0);
                        LambdaUpdateWrapper<PaymentBaseInfo> updateWrapper = Wrappers.lambdaUpdate();
                        updateWrapper.set(PaymentBaseInfo::getReceiptId, contractReceipt.getId());
                        updateWrapper.set(PaymentBaseInfo::getReceiptCode, contractReceipt.getReceiptCode());
                        if (Objects.equals(paymentBaseInfo.getPaymentStatus(), PaymentStatusEnum.TAKE_EFFECT.name())) {
                            updateWrapper.set(PaymentBaseInfo::getReceiptIdFinal, contractReceipt.getId());
                        }
                        updateWrapper.set(BaseModel::getUpdateTime, paymentBaseInfo.getUpdateTime());
                        updateWrapper.eq(PaymentBaseInfo::getId, paymentBaseInfo.getId());
                        paymentBaseInfoService.update(updateWrapper);
                    } else {
                        ContractReceipt contractReceipt = contractReceiptService.getOneByPaymentCode(paymentBaseInfo.getPaymentCode());
                        if (Objects.nonNull(contractReceipt)) {
                            LambdaUpdateWrapper<PaymentBaseInfo> updateWrapper = Wrappers.lambdaUpdate();
                            updateWrapper.set(PaymentBaseInfo::getReceiptId, contractReceipt.getId());
                            updateWrapper.set(PaymentBaseInfo::getReceiptCode, contractReceipt.getReceiptCode());
                            updateWrapper.set(PaymentBaseInfo::getReceiptIdFinal, contractReceipt.getId());
                            updateWrapper.set(BaseModel::getUpdateTime, paymentBaseInfo.getUpdateTime());
                            updateWrapper.eq(PaymentBaseInfo::getId, paymentBaseInfo.getId());
                            paymentBaseInfoService.update(updateWrapper);
                        }
                    }
                    // 重新查询最新的付款数据
                    PaymentBaseInfo newPaymentBaseInfo = paymentBaseInfoService.getById(paymentBaseInfo.getId());
                    // 更新付款申请版本表
                    LambdaUpdateWrapper<PaymentBaseInfoLib> paymentUpdateQuery = Wrappers.lambdaUpdate();
                    paymentUpdateQuery.set(PaymentBaseInfo::getReceiptId, newPaymentBaseInfo.getReceiptId());
                    paymentUpdateQuery.set(PaymentBaseInfo::getReceiptCode, newPaymentBaseInfo.getReceiptCode());
                    paymentUpdateQuery.set(PaymentBaseInfo::getReceiptIdFinal, newPaymentBaseInfo.getReceiptIdFinal());
                    paymentUpdateQuery.eq(PaymentBaseInfoLib::getOriginId, paymentBaseInfo.getId());
                    paymentBaseInfoLibService.update(paymentUpdateQuery);
                    // 更新收款信息
                    LambdaUpdateWrapper<CollectionBaseInfo> collectUpdateQuery = Wrappers.lambdaUpdate();
                    collectUpdateQuery.set(CollectionBaseInfo::getReceiptId, newPaymentBaseInfo.getReceiptId());
                    collectUpdateQuery.set(CollectionBaseInfo::getReceiptCode, newPaymentBaseInfo.getReceiptCode());
                    collectUpdateQuery.eq(CollectionBaseInfo::getPaymentId, newPaymentBaseInfo.getId());
                    collectUpdateQuery.eq(CollectionBaseInfo::getCashFlowItem, CashFlowItemEnum.RENT.name());
                    collectionBaseInfoService.update(collectUpdateQuery);
                    if (Objects.equals(contractBaseInfo.getLeaseType(), LeaseType.zhi_zu.name())) {
                        // 直租合同需要同步修改收款现金流编号
                        LambdaQueryWrapper<CollectionBaseInfo> collectQuery = Wrappers.lambdaQuery();
                        collectQuery.eq(CollectionBaseInfo::getPaymentId, newPaymentBaseInfo.getId());
                        collectQuery.eq(CollectionBaseInfo::getCashFlowItem, CashFlowItemEnum.RENT.name());
                        List<CollectionBaseInfo> collectionBaseInfoList = collectionBaseInfoService.list(collectQuery);
                        if (CollectionUtil.isNotEmpty(collectionBaseInfoList)) {
                            List<ContractRentActual> contractRentActualList = contractRentActualService.listByReceipt(newPaymentBaseInfo.getReceiptId());
                            Map<Long, ContractRentActual> map = contractRentActualList.stream().collect(Collectors.toMap(ContractRentActual::getId, e -> e));
                            for (CollectionBaseInfo collectionBaseInfo : collectionBaseInfoList) {
                                ContractRentActual cra = map.get(collectionBaseInfo.getRentActualId());
                                if (Objects.isNull(cra)) {
                                    continue;
                                }
                                collectionBaseInfo.setCode(cra.getCashFlowCode());
                            }
                            collectionBaseInfoService.updateBatchById(collectionBaseInfoList);
                        }
                    }
                } catch (Exception e) {
                    log.error("租前息历史数据修订 - 处理收付款发生异常[paymentId: {}, paymengCode: {}]", paymentBaseInfo.getId(), paymentBaseInfo.getPaymentCode(), e);
                    transactionStatus.setRollbackOnly();
                }
                log.info("租前息历史数据修订 - 结束处理收付款[paymentId: {}, paymengCode: {}]", paymentBaseInfo.getId(), paymentBaseInfo.getPaymentCode());
            });
        }
    }
}
