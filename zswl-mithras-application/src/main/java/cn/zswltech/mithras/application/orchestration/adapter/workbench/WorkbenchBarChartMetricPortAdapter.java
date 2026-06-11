package cn.zswltech.mithras.application.orchestration.adapter.workbench;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.contract.model.contract.ContractAocPriceLib;
import cn.zswltech.mithras.contract.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.contract.model.contract.ContractFactoringPriceLib;
import cn.zswltech.mithras.contract.model.contract.ContractLeasePrice;
import cn.zswltech.mithras.contract.model.contract.ContractLeasePriceLib;
import cn.zswltech.mithras.contract.versioning.application.ContractAocPriceLibService;
import cn.zswltech.mithras.contract.versioning.application.ContractFactoringPriceLibService;
import cn.zswltech.mithras.contract.versioning.application.ContractLeasePriceLibService;
import cn.zswltech.mithras.customer.model.client.Client;
import cn.zswltech.mithras.payment.enums.PaymentWriteOffStatus;
import cn.zswltech.mithras.payment.model.PaymentActualDetail;
import cn.zswltech.mithras.payment.model.PaymentBaseInfo;
import cn.zswltech.mithras.projectprocess.model.projestablish.ProjEstablishAocPrice;
import cn.zswltech.mithras.projectprocess.model.projestablish.ProjEstablishAocPriceLib;
import cn.zswltech.mithras.projectprocess.model.projestablish.ProjEstablishBaseInfo;
import cn.zswltech.mithras.projectprocess.model.projestablish.ProjEstablishFactoringPrice;
import cn.zswltech.mithras.projectprocess.model.projestablish.ProjEstablishFactoringPriceLib;
import cn.zswltech.mithras.projectprocess.model.projestablish.ProjEstablishLeasePrice;
import cn.zswltech.mithras.projectprocess.model.projestablish.ProjEstablishLeasePriceLib;
import cn.zswltech.mithras.projectprocess.model.projreview.ProjReviewAocPrice;
import cn.zswltech.mithras.projectprocess.model.projreview.ProjReviewAocPriceLib;
import cn.zswltech.mithras.projectprocess.model.projreview.ProjReviewBaseInfo;
import cn.zswltech.mithras.projectprocess.model.projreview.ProjReviewFactoringPrice;
import cn.zswltech.mithras.projectprocess.model.projreview.ProjReviewFactoringPriceLib;
import cn.zswltech.mithras.projectprocess.model.projreview.ProjReviewLeasePrice;
import cn.zswltech.mithras.projectprocess.model.projreview.ProjReviewLeasePriceLib;
import cn.zswltech.mithras.projectprocess.application.lib.projestablish.ProjEstablishAocPriceLibService;
import cn.zswltech.mithras.projectprocess.application.lib.projestablish.ProjEstablishFactoringPriceLibService;
import cn.zswltech.mithras.projectprocess.application.lib.projestablish.ProjEstablishLeasePriceLibService;
import cn.zswltech.mithras.projectprocess.application.lib.projreview.ProjReviewAocPriceLibService;
import cn.zswltech.mithras.projectprocess.application.lib.projreview.ProjReviewFactoringPriceLibService;
import cn.zswltech.mithras.projectprocess.application.lib.projreview.ProjReviewLeasePriceLibService;
import cn.zswltech.mithras.foundation.persistence.model.BaseModel;
import cn.zswltech.mithras.application.orchestration.client.ClientService;
import cn.zswltech.mithras.contract.core.ContractBaseInfoService;
import cn.zswltech.mithras.application.orchestration.payment.PaymentActualDetailService;
import cn.zswltech.mithras.application.orchestration.payment.PaymentBaseInfoService;
import cn.zswltech.mithras.application.orchestration.projectprocess.projestablish.ProjEstablishBaseInfoService;
import cn.zswltech.mithras.application.orchestration.projectprocess.projreview.ProjReviewBaseInfoService;
import cn.zswltech.mithras.foundation.util.LongUtil;
import cn.zswltech.mithras.system.user.SysUserService;
import cn.zswltech.mithras.workbench.application.WorkbenchBarChartMetricPort;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class WorkbenchBarChartMetricPortAdapter implements WorkbenchBarChartMetricPort {
    private static final BigDecimal DISPLAY_AMOUNT_DIVISOR = BigDecimal.valueOf(100000000L);

    @Resource
    private SysUserService sysUserService;
    @Resource
    private ProjEstablishBaseInfoService establishBaseInfoService;
    @Resource
    private ProjEstablishAocPriceLibService aocPriceLibService;
    @Resource
    private ProjEstablishFactoringPriceLibService factoringPriceLibService;
    @Resource
    private ProjEstablishLeasePriceLibService leasePriceLibService;
    @Resource
    private ProjReviewBaseInfoService reviewBaseInfoService;
    @Resource
    private ProjReviewAocPriceLibService reviewAocPriceLibService;
    @Resource
    private ProjReviewFactoringPriceLibService reviewFactoringPriceLibService;
    @Resource
    private ProjReviewLeasePriceLibService reviewLeasePriceLibService;
    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private ContractAocPriceLibService contractAocPriceLibService;
    @Resource
    private ContractFactoringPriceLibService contractFactoringPriceLibService;
    @Resource
    private ContractLeasePriceLibService contractLeasePriceLibService;
    @Resource
    private PaymentBaseInfoService paymentBaseInfoService;
    @Resource
    private PaymentActualDetailService actualDetailService;
    @Resource
    private ClientService clientService;

    @Override
    public int countProjectEstablish(String deptScope, LocalDateTime startTime) {
        return listProjectEstablish(deptScope, startTime).size();
    }

    @Override
    public BigDecimal calculateProjectEstablishAmount(String deptScope, LocalDateTime startTime) {
        Set<Long> projEstablishIds = listProjectEstablish(deptScope, startTime).stream()
                .map(ProjEstablishBaseInfo::getId)
                .collect(Collectors.toSet());
        if (ObjectUtil.isEmpty(projEstablishIds)) {
            return zeroAmount();
        }
        List<ProjEstablishLeasePriceLib> leasePriceLibs = leasePriceLibService
                .listNewestByProjEstablishIds(projEstablishIds);
        List<ProjEstablishAocPriceLib> aocPriceLibs = aocPriceLibService
                .listNewestByProjEstablishIds(projEstablishIds);
        List<ProjEstablishFactoringPriceLib> factoringPriceLibs = factoringPriceLibService
                .listNewestByProjEstablishIds(projEstablishIds);

        BigDecimal amount = BigDecimal.ZERO;
        amount = leasePriceLibs.stream().map(ProjEstablishLeasePrice::getApplyCreditAmount)
                .map(LongUtil::null2zero).map(BigDecimal::new).reduce(amount, BigDecimal::add);
        amount = aocPriceLibs.stream().map(ProjEstablishAocPrice::getApplyCreditAmount)
                .map(LongUtil::null2zero).map(BigDecimal::new).reduce(amount, BigDecimal::add);
        amount = factoringPriceLibs.stream().map(ProjEstablishFactoringPrice::getApplyCreditAmount)
                .map(LongUtil::null2zero).map(BigDecimal::new).reduce(amount, BigDecimal::add);
        return displayAmount(amount);
    }

    @Override
    public int countProjectReview(String deptScope, LocalDateTime startTime) {
        return listProjectReview(deptScope, startTime).size();
    }

    @Override
    public BigDecimal calculateProjectReviewAmount(String deptScope, LocalDateTime startTime) {
        Set<Long> projReviewIds = listProjectReview(deptScope, startTime).stream()
                .map(ProjReviewBaseInfo::getId)
                .collect(Collectors.toSet());
        if (ObjectUtil.isEmpty(projReviewIds)) {
            return zeroAmount();
        }
        List<ProjReviewLeasePriceLib> leasePriceLibs = reviewLeasePriceLibService
                .listNewestByProjReviewIds(projReviewIds);
        List<ProjReviewAocPriceLib> aocPriceLibs = reviewAocPriceLibService.listNewestByProjReviewIds(projReviewIds);
        List<ProjReviewFactoringPriceLib> factoringPriceLibs = reviewFactoringPriceLibService
                .listNewestByProjReviewIds(projReviewIds);

        BigDecimal amount = BigDecimal.ZERO;
        amount = leasePriceLibs.stream().map(ProjReviewLeasePrice::getApplyCreditAmount)
                .map(LongUtil::null2zero).map(BigDecimal::new).reduce(amount, BigDecimal::add);
        amount = aocPriceLibs.stream().map(ProjReviewAocPrice::getApplyCreditAmount)
                .map(LongUtil::null2zero).map(BigDecimal::new).reduce(amount, BigDecimal::add);
        amount = factoringPriceLibs.stream().map(ProjReviewFactoringPrice::getApplyCreditAmount)
                .map(LongUtil::null2zero).map(BigDecimal::new).reduce(amount, BigDecimal::add);
        return displayAmount(amount);
    }

    @Override
    public int countContract(String deptScope, LocalDateTime startTime) {
        return listContract(deptScope, startTime).size();
    }

    @Override
    public BigDecimal calculateContractAmount(String deptScope, LocalDateTime startTime) {
        Set<Long> contractIds = listContract(deptScope, startTime).stream()
                .map(ContractBaseInfo::getId)
                .collect(Collectors.toSet());
        if (ObjectUtil.isEmpty(contractIds)) {
            return zeroAmount();
        }
        List<ContractLeasePriceLib> leasePriceLibs = contractLeasePriceLibService.queryNewestLib(contractIds);
        List<ContractAocPriceLib> aocPriceLibs = contractAocPriceLibService.queryNewestLib(contractIds);
        List<ContractFactoringPriceLib> factoringPriceLibs = contractFactoringPriceLibService.queryNewestLib(contractIds);

        BigDecimal amount = BigDecimal.ZERO;
        amount = leasePriceLibs.stream().map(ContractLeasePrice::getApplyCreditAmount)
                .map(LongUtil::null2zero).map(BigDecimal::new).reduce(amount, BigDecimal::add);
        amount = aocPriceLibs.stream().map(ContractAocPriceLib::getContractAmount)
                .map(LongUtil::null2zero).map(BigDecimal::new).reduce(amount, BigDecimal::add);
        amount = factoringPriceLibs.stream().map(ContractFactoringPriceLib::getContractAmount)
                .map(LongUtil::null2zero).map(BigDecimal::new).reduce(amount, BigDecimal::add);
        return displayAmount(amount);
    }

    @Override
    public int countPayment(String deptScope, LocalDateTime startTime) {
        Set<Long> targetContractIds = contractBaseInfoService.list(Wrappers.<ContractBaseInfo>lambdaQuery()
                        .eq(ContractBaseInfo::getBizDeptId, sysUserService.getOrgIdByCode(deptScope))
                        .in(ContractBaseInfo::getContractStatus, "TAKE_EFFECT", "START_RENT"))
                .stream()
                .map(ContractBaseInfo::getId)
                .collect(Collectors.toSet());
        if (ObjectUtil.isEmpty(targetContractIds)) {
            return 0;
        }
        return paymentBaseInfoService.list(Wrappers.<PaymentBaseInfo>lambdaQuery()
                        .in(PaymentBaseInfo::getContractId, targetContractIds)
                        .in(PaymentBaseInfo::getWriteOffStatus, PaymentWriteOffStatus.WRITTEN_OFF.name(),
                                PaymentWriteOffStatus.PART_WRITTEN_OFF.name())
                        .ge(BaseModel::getCreateTime, startTime))
                .size();
    }

    @Override
    public BigDecimal calculatePaymentAmount(String deptScope, LocalDate startDate) {
        Set<Long> targetClientIds = clientService.list(Wrappers.<Client>lambdaQuery()
                        .eq(Client::getClientStatus, "TAKE_EFFECT")
                        .eq(Client::getBelongDeptId, sysUserService.getOrgIdByCode(deptScope)))
                .stream()
                .map(Client::getId)
                .collect(Collectors.toSet());
        BigDecimal totalLaunch = actualDetailService.writtenOffDetailsByClientIds(targetClientIds, startDate, null)
                .stream()
                .map(PaymentActualDetail::getPaidInAmount)
                .map(LongUtil::null2zero)
                .map(BigDecimal::new)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return displayAmount(totalLaunch);
    }

    private List<ProjEstablishBaseInfo> listProjectEstablish(String deptScope, LocalDateTime startTime) {
        return establishBaseInfoService.list(Wrappers.<ProjEstablishBaseInfo>lambdaQuery()
                .eq(ProjEstablishBaseInfo::getProjEstablishStatus, "TAKE_EFFECT")
                .eq(ProjEstablishBaseInfo::getBizDeptId, sysUserService.getOrgIdByCode(deptScope))
                .ge(BaseModel::getCreateTime, startTime));
    }

    private List<ProjReviewBaseInfo> listProjectReview(String deptScope, LocalDateTime startTime) {
        return reviewBaseInfoService.list(Wrappers.<ProjReviewBaseInfo>lambdaQuery()
                .eq(ProjReviewBaseInfo::getProjReviewStatus, "TAKE_EFFECT")
                .eq(ProjReviewBaseInfo::getBizDeptId, sysUserService.getOrgIdByCode(deptScope))
                .ge(BaseModel::getCreateTime, startTime));
    }

    private List<ContractBaseInfo> listContract(String deptScope, LocalDateTime startTime) {
        return contractBaseInfoService.list(Wrappers.<ContractBaseInfo>lambdaQuery()
                .eq(ContractBaseInfo::getBizDeptId, sysUserService.getOrgIdByCode(deptScope))
                .in(ContractBaseInfo::getContractStatus, "TAKE_EFFECT", "SETTLE", "START_RENT")
                .ge(BaseModel::getCreateTime, startTime));
    }

    private BigDecimal displayAmount(BigDecimal amount) {
        return amount.divide(DISPLAY_AMOUNT_DIVISOR, 2, RoundingMode.HALF_UP);
    }

    private BigDecimal zeroAmount() {
        return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
    }
}
