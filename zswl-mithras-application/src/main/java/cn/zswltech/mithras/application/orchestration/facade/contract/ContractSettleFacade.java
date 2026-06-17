package cn.zswltech.mithras.application.orchestration.facade.contract;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.contract.application.ContractSettleApplicationService;
import cn.zswltech.mithras.dto.contract.ContractSingleIdREQ;
import cn.zswltech.mithras.dto.contract.price.ContractPriceDetailREQ;
import cn.zswltech.mithras.dto.contract.settle.*;
import cn.zswltech.mithras.dto.contractcp.ContractInfoRSP;
import cn.zswltech.mithras.dto.contractcp.ContractcpContractDetailREQ;
import cn.zswltech.mithras.foundation.auth.aop.DataAuthCheck;
import cn.zswltech.mithras.contract.application.auth.ContractBaseAddSubAuthChecker;
import cn.zswltech.mithras.contract.application.auth.ContractBaseRemoveMainAuthChecker;
import cn.zswltech.mithras.contract.convert.contract.ContractSettlePlanConvert;
import cn.zswltech.mithras.application.orchestration.auth.BusinessModuleEnum;
import cn.zswltech.mithras.foundation.enums.CashFlowItemEnum;
import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.contract.enums.contract.ContractExtraFileTypeEnum;
import cn.zswltech.mithras.contract.enums.contract.ContractSettlePlanTypeEnum;
import cn.zswltech.mithras.document.persistence.model.MaterialsList;
import cn.zswltech.mithras.collection.model.CollectionBaseInfo;
import cn.zswltech.mithras.contract.model.contract.*;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.collection.application.CollectionBaseInfoService;
import cn.zswltech.mithras.application.orchestration.collection.CollectionService;
import cn.zswltech.mithras.contract.core.ContractLeasePriceService;
import cn.zswltech.mithras.contract.core.ContractSettlePlanService;
import cn.zswltech.mithras.contract.versioning.service.ContractRentActualLibService;
import cn.zswltech.mithras.contract.versioning.service.ContractSettlePlanLibService;
import cn.zswltech.mithras.contract.versioning.handler.impl.ContractBaseInfoLibHandler;
import cn.zswltech.mithras.collection.application.contractcp.ContractCollectionPaymentService;
import cn.zswltech.mithras.margin.service.MarginBaseInfoService;
import cn.zswltech.mithras.application.orchestration.document.materialsfile.MaterialsListService;
import cn.zswltech.mithras.foundation.util.LongUtil;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

/**
 * @author dingqi
 * @date 2022/8/24
 * @description
 */
@Service
public class ContractSettleFacade implements ContractSettleApplicationService {
    @Resource
    private ContractBaseInfoLibHandler contractBaseInfoLibHandler;
    @Resource
    private ContractSettlePlanLibService contractSettlePlanLibService;
    @Resource
    private ContractSettlePlanService contractSettlePlanService;
    @Resource
    private MarginBaseInfoService marginBaseInfoService;
    @Resource
    private ContractRentActualLibService contractRentActualLibService;
    @Resource
    private MaterialsListService materialsListService;
    @Resource
    private ContractCollectionPaymentService contractCollectionPaymentService;
    @Resource
    private CollectionBaseInfoService collectionBaseInfoService;
    @Resource
    private CollectionService collectionService;
    @Resource
    private ContractLeasePriceService leasePriceService;


    @DataAuthCheck(keyFieldName = "contractId", checkerClass = ContractBaseAddSubAuthChecker.class, businessModule = "CONTRACT")
    @Transactional
    @Override
    public R<Long> savePlanNormal(@Valid ContractSettlePlanNormalREQ contractSettlePlanNormalREQ) {
        ContractSettlePlan contractSettlePlan = ContractSettlePlanConvert.toContractSettlePlan(contractSettlePlanNormalREQ);
        contractSettlePlan.setSettleType(ContractSettlePlanTypeEnum.SETTLE_NORMAL.name());
        contractSettlePlanService.saveOrUpdatePlan(contractSettlePlan);
        return R.ok(contractSettlePlan.getId());
    }

    @DataAuthCheck(keyFieldName = "contractId", checkerClass = ContractBaseAddSubAuthChecker.class, businessModule = "CONTRACT")
    @Transactional
    @Override
    public R<Long> savePlanInAdvance(@Valid ContractSettlePlanInAdvanceREQ contractSettlePlanInAdvanceREQ) {
        Assert.notBlank(contractSettlePlanInAdvanceREQ.getApplySettleDate(), () -> MithrasException.newException("申请结清日不能为空"));
        ContractSettlePlan contractSettlePlan = ContractSettlePlanConvert.toContractSettlePlan(contractSettlePlanInAdvanceREQ);
        contractSettlePlan.setSettleType(ContractSettlePlanTypeEnum.SETTLE_IN_ADVANCE.name());
        contractSettlePlan.setApplySettleDate(LocalDateTimeUtil.parse(contractSettlePlanInAdvanceREQ.getApplySettleDate(), DatePattern.NORM_DATE_PATTERN).toLocalDate());
        contractSettlePlan.setSettleRemark(contractSettlePlanInAdvanceREQ.getSettleRemark());
        contractSettlePlanService.saveOrUpdatePlan(contractSettlePlan);
        return R.ok(contractSettlePlan.getId());
    }

    @Override
    public R<ContractSettlePlanDetailRSP> getLatestSettlePlan(@Valid ContractSettlePlanDetailREQ contractSettlePlanDetailREQ) {
        ContractSettlePlanDetailRSP contractSettlePlanDetailRSP;
        // 判断是否携带有数据版本号，如果有，则查询指定版本
        if (StrUtil.isNotBlank(contractSettlePlanDetailREQ.getVersion())) {
            ContractSettlePlanLib contractSettlePlanLib = contractSettlePlanLibService.getBy(contractSettlePlanDetailREQ.getContractId(), contractSettlePlanDetailREQ.getVersion());
            Assert.notNull(contractSettlePlanLib, () -> MithrasException.newException("对应版本的结清方案数据不存在"));
            contractSettlePlanDetailRSP = ContractSettlePlanConvert.toContractSettlePlanDetailRSP(contractSettlePlanLib);
            return R.ok(contractSettlePlanDetailRSP);
        }
        // 没有携带版本则尝试从编辑区获取合同结清方案
        ContractSettlePlan contractSettlePlan = contractSettlePlanService.getLatestContractSettlePlan(contractSettlePlanDetailREQ.getContractId());
        if (Objects.nonNull(contractSettlePlan)) {
            contractSettlePlanDetailRSP = ContractSettlePlanConvert.toContractSettlePlanDetailRSP(contractSettlePlan);
        } else {
            contractSettlePlanDetailRSP = new ContractSettlePlanDetailRSP();
            // 如果没有保存过结清方案则系统计算相关数据带出
            if (Objects.equals(ContractSettlePlanTypeEnum.SETTLE_IN_ADVANCE.name(), contractSettlePlanDetailREQ.getPlanType())) {
                // 提前结清方案需要额外计算到期未付租金、未到期本金
                ContractcpContractDetailREQ req = new ContractcpContractDetailREQ();
                req.setContractId(contractSettlePlanDetailREQ.getContractId());
                ContractInfoRSP contractInfoRSP = contractCollectionPaymentService.contractInfo(req);
                // 到期未付租金
                contractSettlePlanDetailRSP.setOutstandingRent(contractInfoRSP.getOverdueAmount());
                // 未到期本金
                contractSettlePlanDetailRSP.setBeforeMaturityPrincipal(contractInfoRSP.getLastPrincipal());
            }
            // 罚息余额
            ContractLeasePrice detail = leasePriceService.detail(new ContractPriceDetailREQ(contractSettlePlanDetailREQ.getContractId()));
            contractSettlePlanDetailRSP.setNominalPrice(detail != null ? detail.getNominalPrice() : 0L);
//            contractSettlePlanDetailRSP.setNominalPrice(collectionService.getRemainingAmountByContract(contractSettlePlanDetailREQ.getContractId(), CashFlowItemEnum.NOMINAL_PRICE.name()));
            List<CollectionBaseInfo> collectionBaseInfoList = collectionBaseInfoService.listBy(contractSettlePlanDetailREQ.getContractId(), CashFlowItemEnum.RENT);
            if (CollectionUtil.isNotEmpty(collectionBaseInfoList)) {
                long total = 0;
                for (CollectionBaseInfo collectionBaseInfo : collectionBaseInfoList) {
                    // 合计
                    total = total + LongUtil.null2zero(collectionBaseInfo.getPenaltyInterest()) - LongUtil.null2zero(collectionBaseInfo.getCollectionPenaltyInterest());
                }
                contractSettlePlanDetailRSP.setLiquidatedDamages(total);
            }
            // 查找最新数据版本
            ContractBaseInfoLib contractBaseInfoLib = contractBaseInfoLibHandler.queryLatestDataByOriginId(contractSettlePlanDetailREQ.getContractId());
            Assert.notNull(contractBaseInfoLib, () -> MithrasException.newException("没有找到最新有效的合同数据版本"));
            List<ContractRentActual> contractRentActualList = contractRentActualLibService.listByContractVersion(contractSettlePlanDetailREQ.getContractId(), contractBaseInfoLib.getVersion());
            Assert.notEmpty(contractRentActualList, () -> MithrasException.newException("没有找到最新有效的实际租金表数据版本"));
            // 根据现金流日期排序
            contractRentActualList.sort(Comparator.comparing(ContractRentActual::getCashFlowDate));
            LocalDate lastDate = contractRentActualList.get(contractRentActualList.size() - 1).getCashFlowDate();
            contractSettlePlanDetailRSP.setOriginalDeadline(LocalDateTimeUtil.format(lastDate, DatePattern.NORM_DATE_PATTERN));
        }
        // 补全一些数据
        // 从收款信息汇总获取保证金余额
        if (ObjectUtil.isEmpty(contractSettlePlanDetailREQ.getNeedReal()) || !ObjectUtil.equals(contractSettlePlanDetailREQ.getNeedReal(), YesOrNoNumberEnum.NO.getCode())) {
            long earnestBalance = marginBaseInfoService.getMarginBalance(contractSettlePlanDetailREQ.getContractId());
            contractSettlePlanDetailRSP.setEarnestBalance(earnestBalance);
        }
        return R.ok(contractSettlePlanDetailRSP);
    }

    @DataAuthCheck(keyFieldName = "contractId", checkerClass = ContractBaseAddSubAuthChecker.class, businessModule = "CONTRACT")
    @Override
    public R<Void> uploadExtraFile(@Valid ContractSettleExtraFileUploadREQ contractSettleExtraFileUploadREQ) {
        Long contractId = contractSettleExtraFileUploadREQ.getContractId();
//        // 清空已存在的
//        List<MaterialsList> materialsListList = materialsListService.list(BusinessModuleEnum.CONTRACT.name(), Collections.singletonList(ContractExtraFileTypeEnum.CONTRACT_SETTLE.name()), Collections.singletonList(contractId));
//        if (!CollectionUtils.isEmpty(materialsListList)) {
//            List<Long> ids = materialsListList.stream().map(MaterialsList::getId).collect(Collectors.toList());
//            materialsListService.removeByIds(ids);
//        }
        for (MultipartFile file : contractSettleExtraFileUploadREQ.getFiles()) {
            materialsListService.add(file, contractId, ContractExtraFileTypeEnum.CONTRACT_SETTLE.name(), BusinessModuleEnum.CONTRACT.name());
        }
        return R.ok();
    }

    @DataAuthCheck(keyFieldName = "contractId", checkerClass = ContractBaseRemoveMainAuthChecker.class, businessModule = "CONTRACT")
    @Override
    public R<Void> removeExtraFile(@Valid ContractSettleExtraFileRemoveREQ contractSettleExtraFileRemoveREQ) {
        materialsListService.removeById(contractSettleExtraFileRemoveREQ.getFileId());
        return R.ok();
    }

    @Override
    public R<List<ContractSettleExtraFileRSP>> listSettleExtraFile(@Valid ContractSingleIdREQ contractSingleIdREQ) {
        List<MaterialsList> materialsListList = materialsListService.list(BusinessModuleEnum.CONTRACT.name(), Collections.singletonList(ContractExtraFileTypeEnum.CONTRACT_SETTLE.name()), Collections.singletonList(contractSingleIdREQ.getContractId()));
        if (CollectionUtils.isEmpty(materialsListList)) {
            return R.ok(Collections.emptyList());
        }
        List<ContractSettleExtraFileRSP> result = materialsListList.stream().map(item -> {
            ContractSettleExtraFileRSP rsp = new ContractSettleExtraFileRSP();
            rsp.setFileId(item.getId());
            rsp.setFileName(item.getFilename());
            return rsp;
        }).collect(Collectors.toList());
        return R.ok(result);
    }
}
