package cn.zswltech.mithras.projlifecycle.application;

import cn.zswltech.mithras.projlifecycle.application.model.ProjLifecycleSnapshot;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface ProjLifecycleDataPort {

    List<ProjLifecycleSnapshot.ProjReview> listReviewByIds(Collection<Long> reviewIds);

    List<ProjLifecycleSnapshot.ProjEstablish> listAllEstablish();

    List<ProjLifecycleSnapshot.ProjReview> listAllReview();

    List<ProjLifecycleSnapshot.Contract> listAllContract();

    List<ProjLifecycleSnapshot.Contract> listContractsByReviewIds(List<Long> reviewIds);

    List<ProjLifecycleSnapshot.ContractPrice> listLeasePricesByContractIds(List<Long> contractIds);

    List<ProjLifecycleSnapshot.ContractPrice> listAocPricesByContractIds(List<Long> contractIds);

    List<ProjLifecycleSnapshot.ContractPrice> listFactoringPricesByContractIds(List<Long> contractIds);

    List<ProjLifecycleSnapshot.Payment> listPaymentsByContractIds(List<Long> contractIds);

    Map<Long, List<ProjLifecycleSnapshot.PaymentActualDetail>> getPaymentActualDetailsByPaymentIds(Collection<Long> paymentIds);

    List<ProjLifecycleSnapshot.Collection> listCollectionsByContractIds(List<Long> contractIds);
}
