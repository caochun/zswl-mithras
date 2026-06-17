package cn.zswltech.mithras.application.orchestration.adapter.projectprocess.projlifecycle;

import cn.hutool.core.collection.CollUtil;
import cn.zswltech.mithras.collection.model.CollectionBaseInfo;
import cn.zswltech.mithras.contract.core.ContractAocPriceService;
import cn.zswltech.mithras.contract.core.ContractFactoringPriceService;
import cn.zswltech.mithras.contract.core.ContractLeasePriceService;
import cn.zswltech.mithras.contract.model.contract.ContractAocPrice;
import cn.zswltech.mithras.contract.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.contract.model.contract.ContractFactoringPrice;
import cn.zswltech.mithras.contract.model.contract.ContractLeasePrice;
import cn.zswltech.mithras.payment.model.PaymentActualDetail;
import cn.zswltech.mithras.payment.model.PaymentBaseInfo;
import cn.zswltech.mithras.projectprocess.model.projestablish.ProjEstablishBaseInfo;
import cn.zswltech.mithras.projectprocess.model.projreview.ProjReviewBaseInfo;
import cn.zswltech.mithras.projectprocess.projlifecycle.application.ProjLifecycleDataPort;
import cn.zswltech.mithras.projectprocess.projlifecycle.application.model.ProjLifecycleSnapshot;
import cn.zswltech.mithras.collection.application.CollectionBaseInfoService;
import cn.zswltech.mithras.contract.core.ContractBaseInfoService;
import cn.zswltech.mithras.application.orchestration.payment.PaymentActualDetailService;
import cn.zswltech.mithras.application.orchestration.payment.PaymentBaseInfoService;
import cn.zswltech.mithras.application.orchestration.projectprocess.projestablish.ProjEstablishBaseInfoService;
import cn.zswltech.mithras.application.orchestration.projectprocess.projreview.ProjReviewBaseInfoService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ProjLifecycleDataPortAdapter implements ProjLifecycleDataPort {

    @Resource
    private ProjReviewBaseInfoService projReviewBaseInfoService;
    @Resource
    private ProjEstablishBaseInfoService projEstablishBaseInfoService;
    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private ContractLeasePriceService contractLeasePriceService;
    @Resource
    private ContractAocPriceService contractAocPriceService;
    @Resource
    private ContractFactoringPriceService contractFactoringPriceService;
    @Resource
    private PaymentBaseInfoService paymentBaseInfoService;
    @Resource
    private PaymentActualDetailService paymentActualDetailService;
    @Resource
    private CollectionBaseInfoService collectionBaseInfoService;

    @Override
    public List<ProjLifecycleSnapshot.ProjReview> listReviewByIds(Collection<Long> reviewIds) {
        if (CollUtil.isEmpty(reviewIds)) {
            return Collections.emptyList();
        }
        return projReviewBaseInfoService.listByIds(reviewIds).stream().map(this::toReview).collect(Collectors.toList());
    }

    @Override
    public List<ProjLifecycleSnapshot.ProjEstablish> listAllEstablish() {
        return projEstablishBaseInfoService.list().stream().map(this::toEstablish).collect(Collectors.toList());
    }

    @Override
    public List<ProjLifecycleSnapshot.ProjReview> listAllReview() {
        return projReviewBaseInfoService.list().stream().map(this::toReview).collect(Collectors.toList());
    }

    @Override
    public List<ProjLifecycleSnapshot.Contract> listAllContract() {
        return contractBaseInfoService.list().stream().map(this::toContract).collect(Collectors.toList());
    }

    @Override
    public List<ProjLifecycleSnapshot.Contract> listContractsByReviewIds(List<Long> reviewIds) {
        if (CollUtil.isEmpty(reviewIds)) {
            return Collections.emptyList();
        }
        return contractBaseInfoService.listByProjReviewIds(reviewIds).stream().map(this::toContract).collect(Collectors.toList());
    }

    @Override
    public List<ProjLifecycleSnapshot.ContractPrice> listLeasePricesByContractIds(List<Long> contractIds) {
        if (CollUtil.isEmpty(contractIds)) {
            return Collections.emptyList();
        }
        return contractLeasePriceService.listByContractIds(contractIds).stream().map(this::toPrice).collect(Collectors.toList());
    }

    @Override
    public List<ProjLifecycleSnapshot.ContractPrice> listAocPricesByContractIds(List<Long> contractIds) {
        if (CollUtil.isEmpty(contractIds)) {
            return Collections.emptyList();
        }
        return contractAocPriceService.listByContractIds(contractIds).stream().map(this::toPrice).collect(Collectors.toList());
    }

    @Override
    public List<ProjLifecycleSnapshot.ContractPrice> listFactoringPricesByContractIds(List<Long> contractIds) {
        if (CollUtil.isEmpty(contractIds)) {
            return Collections.emptyList();
        }
        return contractFactoringPriceService.listByContractIds(contractIds).stream().map(this::toPrice).collect(Collectors.toList());
    }

    @Override
    public List<ProjLifecycleSnapshot.Payment> listPaymentsByContractIds(List<Long> contractIds) {
        if (CollUtil.isEmpty(contractIds)) {
            return Collections.emptyList();
        }
        return paymentBaseInfoService.listByContractIds(contractIds).stream().map(this::toPayment).collect(Collectors.toList());
    }

    @Override
    public Map<Long, List<ProjLifecycleSnapshot.PaymentActualDetail>> getPaymentActualDetailsByPaymentIds(Collection<Long> paymentIds) {
        if (CollUtil.isEmpty(paymentIds)) {
            return Collections.emptyMap();
        }
        return paymentActualDetailService.getMapByPaymentIds(paymentIds).entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().stream().map(this::toPaymentActualDetail).collect(Collectors.toList())));
    }

    @Override
    public List<ProjLifecycleSnapshot.Collection> listCollectionsByContractIds(List<Long> contractIds) {
        if (CollUtil.isEmpty(contractIds)) {
            return Collections.emptyList();
        }
        return collectionBaseInfoService.listByContractIds(contractIds).stream().map(this::toCollection).collect(Collectors.toList());
    }

    private ProjLifecycleSnapshot.ProjEstablish toEstablish(ProjEstablishBaseInfo source) {
        ProjLifecycleSnapshot.ProjEstablish target = new ProjLifecycleSnapshot.ProjEstablish();
        target.setId(source.getId());
        target.setProjEstablishStatus(source.getProjEstablishStatus());
        target.setCreateTime(source.getCreateTime());
        return target;
    }

    private ProjLifecycleSnapshot.ProjReview toReview(ProjReviewBaseInfo source) {
        ProjLifecycleSnapshot.ProjReview target = new ProjLifecycleSnapshot.ProjReview();
        target.setId(source.getId());
        target.setProjEstablishId(source.getProjEstablishId());
        target.setGroupCreditReviewId(source.getGroupCreditReviewId());
        target.setProjReviewStatus(source.getProjReviewStatus());
        target.setCreateTime(source.getCreateTime());
        return target;
    }

    private ProjLifecycleSnapshot.Contract toContract(ContractBaseInfo source) {
        ProjLifecycleSnapshot.Contract target = new ProjLifecycleSnapshot.Contract();
        target.setId(source.getId());
        target.setProjReviewId(source.getProjReviewId());
        target.setContractStatus(source.getContractStatus());
        target.setCreateTime(source.getCreateTime());
        target.setUpdateTime(source.getUpdateTime());
        return target;
    }

    private ProjLifecycleSnapshot.ContractPrice toPrice(ContractLeasePrice source) {
        ProjLifecycleSnapshot.ContractPrice target = new ProjLifecycleSnapshot.ContractPrice();
        target.setContractId(source.getContractId());
        target.setAmount(source.getApplyCreditAmount());
        return target;
    }

    private ProjLifecycleSnapshot.ContractPrice toPrice(ContractAocPrice source) {
        ProjLifecycleSnapshot.ContractPrice target = new ProjLifecycleSnapshot.ContractPrice();
        target.setContractId(source.getContractId());
        target.setAmount(source.getContractAmount());
        return target;
    }

    private ProjLifecycleSnapshot.ContractPrice toPrice(ContractFactoringPrice source) {
        ProjLifecycleSnapshot.ContractPrice target = new ProjLifecycleSnapshot.ContractPrice();
        target.setContractId(source.getContractId());
        target.setAmount(source.getContractAmount());
        return target;
    }

    private ProjLifecycleSnapshot.Payment toPayment(PaymentBaseInfo source) {
        ProjLifecycleSnapshot.Payment target = new ProjLifecycleSnapshot.Payment();
        target.setId(source.getId());
        target.setContractId(source.getContractId());
        target.setApplyPaymentAmount(source.getApplyPaymentAmount());
        target.setPaymentStatus(source.getPaymentStatus());
        return target;
    }

    private ProjLifecycleSnapshot.PaymentActualDetail toPaymentActualDetail(PaymentActualDetail source) {
        ProjLifecycleSnapshot.PaymentActualDetail target = new ProjLifecycleSnapshot.PaymentActualDetail();
        target.setPaymentId(source.getPaymentId());
        target.setContractId(source.getContractId());
        target.setPaidInAmount(source.getPaidInAmount());
        return target;
    }

    private ProjLifecycleSnapshot.Collection toCollection(CollectionBaseInfo source) {
        ProjLifecycleSnapshot.Collection target = new ProjLifecycleSnapshot.Collection();
        target.setContractId(source.getContractId());
        target.setPlanCollectionAmount(source.getPlanCollectionAmount());
        target.setCollectionAmount(source.getCollectionAmount());
        return target;
    }
}
