package cn.zswltech.mithras.service.controller.contract;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import cn.zswl.oss.core.OssClient;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.contract.ContractRentApi;
import cn.zswltech.mithras.dto.SinglePkREQ;
import cn.zswltech.mithras.dto.contract.ContractSingleIdREQ;
import cn.zswltech.mithras.dto.contract.price.ContractPriceDetailREQ;
import cn.zswltech.mithras.dto.contract.price.ContractRentPhaseRSP;
import cn.zswltech.mithras.dto.contract.rent.*;
import cn.zswltech.mithras.dto.projreview.cashflowplan.IRRCalculateResultRSP;
import cn.zswltech.mithras.service.annotation.ContractChangeOther;
import cn.zswltech.mithras.service.auth.aop.DataAuthCheck;
import cn.zswltech.mithras.service.auth.checker.contract.ContractBaseAddSubAuthChecker;
import cn.zswltech.mithras.service.auth.checker.contract.ContractRentActualAuthChecker;
import cn.zswltech.mithras.service.constant.GlobalConstants;
import cn.zswltech.mithras.service.convert.contract.ContractRentConvert;
import cn.zswltech.mithras.service.enums.BizDataSourceEnum;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.enums.CashFlowItemEnum;
import cn.zswltech.mithras.service.enums.collection.CollectionWriteOffStatusEnum;
import cn.zswltech.mithras.service.mapper.model.collection.CollectionBaseInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.*;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.collection.CollectionBaseInfoService;
import cn.zswltech.mithras.service.service.contract.*;
import cn.zswltech.mithras.service.service.lib.contract.ContractBaseInfoLibService;
import cn.zswltech.mithras.contract.service.lib.contract.ContractRentEstimateLibService;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author dingqi
 * @date 2022/8/18
 * @description
 */
@Slf4j
@RestController
public class ContractRentController implements ContractRentApi {
    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private ContractBaseInfoLibService contractBaseInfoLibService;
    @Resource
    private ContractReceiptService contractReceiptService;
    @Resource
    private ContractRentEstimateService contractRentEstimateService;
    @Resource
    private ContractRentEstimateLibService contractRentEstimateLibService;
    @Resource
    private ContractRentActualService contractRentActualService;
    @Resource
    private HttpServletResponse httpServletResponse;
    @Resource
    private OssClient ossClient;
    @Resource
    private CollectionBaseInfoService collectionBaseInfoService;
    @Resource
    private ContractLeasePriceService contractLeasePriceService;

    private final static String PHASE = "第%d期";


    //    @DataAuthCheck(keyFieldName = "id", checkerClass = ContractBaseAddSubAuthChecker.class, businessModule = BusinessModuleEnum.CONTRACT)
    @Override
    public R<IRRCalculateResultRSP> calculateIRR(@Valid SinglePkREQ singlePkREQ) {
        return R.ok(contractRentEstimateService.calculateIRR(singlePkREQ));
    }

    @DataAuthCheck(keyFieldName = "id", checkerClass = ContractBaseAddSubAuthChecker.class, businessModule = BusinessModuleEnum.CONTRACT)
    @Override
    public R<Void> generateEstimate(@Valid ContractRentEstimateGenerateREQ req) {
        contractRentEstimateService.generate(req);
        return R.ok();
    }

    @Override
    public R<String> downloadEstimateRentTemplate() {
        try {
            /*httpServletResponse.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(GlobalConstants.TEMPLATE_OSS_NAME_PROJ_REVIEW_CASH_FLOW, StandardCharsets.UTF_8.name()));
            ossClient.downLoad(httpServletResponse.getOutputStream(), GlobalConstants.TEMPLATE_OSS_NAME_PROJ_REVIEW_CASH_FLOW);*/
            return R.ok(ossClient.getPreviewUrl(GlobalConstants.TEMPLATE_OSS_NAME_PROJ_REVIEW_CASH_FLOW, GlobalConstants.FILE_TEMPLATE_EXPIRY));
        } catch (Exception e) {
            log.error("下载概算租金表表模板发生异常", e);
            throw new MithrasException("下载模板发生异常");
        }
    }

    @DataAuthCheck(keyFieldName = "contractId", checkerClass = ContractBaseAddSubAuthChecker.class, businessModule = BusinessModuleEnum.CONTRACT)
    @Override
    @ContractChangeOther
    public R<Void> importEstimateRent(ContractRentEstimateImportREQ contractRentEstimateImportREQ) {
        try {
            contractRentEstimateService.importExcel(contractRentEstimateImportREQ.getFile(), contractRentEstimateImportREQ.getContractId(), contractRentEstimateImportREQ.getPlanStartDate(), contractRentEstimateImportREQ.getScene());
            return R.ok();
        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            log.error("导入概算租金表发生未知异常[contractId:{}]", contractRentEstimateImportREQ.getContractId(), e);
            return R.fail("导入概算租金表发生未知异常");
        }
    }

    @Override
    public void exportEstimateRent(@Valid ContractRentEstimateExportREQ contractRentEstimateExportREQ) {
        try {
            httpServletResponse.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
            httpServletResponse.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("概算租金表" + GlobalConstants.OFFICE_EXCEL_SUFFIX, StandardCharsets.UTF_8.name()));
            contractRentEstimateService.exportExcel(contractRentEstimateExportREQ, httpServletResponse.getOutputStream());
        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            log.error("导出概算租金表发生未知异常", e);
            throw new MithrasException("导出概算租金表发生未知异常");
        }
    }

    @Override
    public void exportEstimateCashFlow(@Valid ContractRentEstimateExportREQ contractRentEstimateExportREQ) {
        try {
            httpServletResponse.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
            httpServletResponse.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("概算现金流表" + GlobalConstants.OFFICE_EXCEL_SUFFIX, StandardCharsets.UTF_8.name()));
            contractRentEstimateService.exportRichExcel(contractRentEstimateExportREQ, httpServletResponse.getOutputStream());
        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            log.error("导出概算现金流表发生未知异常", e);
            throw new MithrasException("导出概算现金流表发生未知异常");
        }
    }

    @Override
    public R<ContractRentEstimateListRSP> listEstimateRent(ContractSingleIdREQ contractSingleIdREQ) {
        ContractRentEstimateListRSP contractRentEstimateListRSP = new ContractRentEstimateListRSP();
        ContractBaseInfo contractBaseInfo;
        List<ContractRentEstimate> contractRentEstimateList;
        if (ObjectUtil.isNull(contractSingleIdREQ.getVersion())) {
            contractBaseInfo = contractBaseInfoService.getById(contractSingleIdREQ.getContractId());
            contractRentEstimateList = contractRentEstimateService.listByContractId(contractSingleIdREQ.getContractId(), null);
        } else {
            contractBaseInfo = contractBaseInfoLibService.getByOriginIdVersion(contractSingleIdREQ.getContractId(), contractSingleIdREQ.getVersion());
            contractRentEstimateList = contractRentEstimateLibService.getByVersion(contractSingleIdREQ.getContractId(), contractSingleIdREQ.getVersion());
        }

        ContractLeasePrice contractLeasePrice = contractLeasePriceService.lambdaQuery().eq(ContractLeasePrice::getContractId, contractSingleIdREQ.getContractId()).one();
        if (!ObjectUtils.isEmpty(contractRentEstimateList) && !ObjectUtils.isEmpty(contractLeasePrice) && !ObjectUtils.isEmpty(contractLeasePrice.getFirstInstallmentInterest())
                && contractLeasePrice.getFirstInstallmentInterest() > 0) {
            // 增加零期现金流
            ContractRentEstimate contractRentEstimate = new ContractRentEstimate();
            contractRentEstimate.setContractId(contractSingleIdREQ.getContractId());
            contractRentEstimate.setCashFlowDate(contractBaseInfo.getEstimatedLeaseDate());
            contractRentEstimate.setCashFlowPhase(0);
            contractRentEstimate.setRent(contractLeasePrice.getFirstInstallmentInterest());
            contractRentEstimate.setPrincipal(0L);
            contractRentEstimate.setRemainingPrincipal(0L);
            contractRentEstimate.setInterest(contractLeasePrice.getFirstInstallmentInterest());
            contractRentEstimateList.add(contractRentEstimate);
        }

        contractRentEstimateListRSP.setId(contractBaseInfo.getId());
        contractRentEstimateListRSP.setPlanStartDate(LocalDateTimeUtil.format(contractBaseInfo.getEstimatedLeaseDate(), DatePattern.NORM_DATE_PATTERN));
        if (CollectionUtils.isEmpty(contractRentEstimateList)) {
            contractRentEstimateListRSP.setRentEstimateList(Collections.emptyList());
        } else {
            List<ContractRentEstimateListRSP.TableData> result = contractRentEstimateList.stream()
                    .map(ContractRentConvert::toContractRentEstimateListRSP)
                    .sorted(Comparator.comparingInt(ContractRentEstimateListRSP.TableData::getPhase))
                    .collect(Collectors.toList());
            contractRentEstimateListRSP.setRentEstimateList(result);
        }
        return R.ok(contractRentEstimateListRSP);
    }

    @Override
    public R<IRRCalculateResultRSP> calculateActualIRR(@Valid SinglePkREQ singlePkREQ) {
        return R.ok(contractRentActualService.calculateIRR(singlePkREQ.getId(), true));
    }

    @DataAuthCheck(keyFieldName = "contractId", checkerClass = ContractRentActualAuthChecker.class, businessModule = BusinessModuleEnum.CONTRACT)
    @Override
    public R<Void> importActualRent(@Valid ContractRentActualImportREQ contractRentActualImportREQ) {
        try {
            contractRentActualService.importExcel(contractRentActualImportREQ);
            return R.ok();
        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            log.error("导入实际租金表发生未知异常[contractId:{}]", contractRentActualImportREQ.getContractId(), e);
            return R.fail("导入实际租金表发生未知异常");
        }
    }

    @Override
    public R<List<ContractRentActualListRSP>> listActualRent(@Valid ContractSingleIdREQ contractSingleIdREQ) {
        // 主合同信息
        ContractBaseInfo contractBaseInfo;
        if (ObjectUtil.isNull(contractSingleIdREQ.getVersion())) {
            contractBaseInfo = contractBaseInfoService.getById(contractSingleIdREQ.getContractId());
        } else {
            contractBaseInfo = contractBaseInfoLibService.getByOriginIdVersion(contractSingleIdREQ.getContractId(), contractSingleIdREQ.getVersion());
        }
        if (Objects.isNull(contractBaseInfo)) {
            return R.fail("没有找到合同信息");
        }
        Map<Long, List<ContractRentActual>> rentActualMap;
        // 借据信息
        Map<Long, ContractReceipt> receiptMap;
        //Service区分是否版本区数据
        rentActualMap = contractRentActualService.getMapGroupByReceipt(contractSingleIdREQ.getContractId(), contractSingleIdREQ.getVersion());
        if (CollectionUtils.isEmpty(rentActualMap)) {
            return R.ok(Collections.emptyList());
        }
        receiptMap = contractReceiptService.getMapByIds(new ArrayList<>(rentActualMap.keySet()), contractSingleIdREQ.getVersion());
        // 借据是否删除可删除
        Map<Long, Boolean> canRemoveMap = contractReceiptService.checkCanRemove(contractSingleIdREQ.getContractId());
        List<ContractRentActualListRSP> result = new ArrayList<>(rentActualMap.size());
        for (Map.Entry<Long, List<ContractRentActual>> entry : rentActualMap.entrySet()) {
            Long receiptId = entry.getKey();
            ContractReceipt contractReceipt = receiptMap.get(receiptId);
            if (Objects.isNull(contractReceipt)) {
                log.error("实际租金表没有找到对应借据信息[receiptId: {}, contractId: {}]", receiptId, contractSingleIdREQ.getContractId());
                return R.fail("没有找到借据信息");
            }
            ContractRentActualListRSP rsp = new ContractRentActualListRSP();
            rsp.setId(contractReceipt.getId());
            rsp.setBizType(contractBaseInfo.getBizType());
            rsp.setContractId(contractReceipt.getContractId());
            rsp.setContractStatus(contractBaseInfo.getContractStatus());
            rsp.setContractProcessStatus(contractBaseInfo.getContractProcessStatus());
            rsp.setReceiptId(contractReceipt.getId());
            rsp.setActualIrr(contractReceipt.getActualIrr());
            rsp.setActualStartDate(LocalDateTimeUtil.format(contractReceipt.getReceiptStartDate(), DatePattern.NORM_DATE_PATTERN));
            rsp.setIsFirstReceipt(contractReceipt.getIsFirstReceipt());
            Boolean canRemove = canRemoveMap.get(contractReceipt.getId());
            rsp.setRemove(Optional.ofNullable(canRemove).orElse(Boolean.FALSE));
            rsp.setReceiptCode(contractReceipt.getReceiptCode());
            List<ContractRentActualListRSP.TableData> actualRentTableDate = entry.getValue().stream()
                    .map(ContractRentConvert::toContractRentActualListTableData).collect(Collectors.toList());
            Map<Long, CollectionBaseInfo> collectionBaseInfoMap =
                    collectionBaseInfoService.list(Wrappers.<CollectionBaseInfo>lambdaQuery()
                                    .eq(CollectionBaseInfo::getReceiptId, rsp.getReceiptId())).stream()
                            .filter(v -> v.getRentActualId() != null)
                            .collect(Collectors.toMap(CollectionBaseInfo::getRentActualId, v -> v));
            actualRentTableDate.forEach(tableData -> {
                if (!Objects.isNull(tableData.getPhase()) && tableData.getPhase() == 0) {
                    List<CollectionBaseInfo> collectionBaseInfoList = collectionBaseInfoService.lambdaQuery().eq(CollectionBaseInfo::getContractId, contractReceipt.getContractId()).list();
                    Optional<CollectionBaseInfo> collectionBaseInfoOptional = collectionBaseInfoList.stream().filter(collectionBaseInfo ->
                            CashFlowItemEnum.RENT.name().equals(collectionBaseInfo.getCashFlowItem()) && !Objects.isNull(collectionBaseInfo.getPhase()) && collectionBaseInfo.getPhase() == 0
                    ).findFirst();
                    collectionBaseInfoOptional.ifPresent(collectionBaseInfo -> tableData.setReceived(CollectionWriteOffStatusEnum.WRITE_OFF_COMPLETED.name().equals(collectionBaseInfo.getWriteOffStatus())));
                } else {
                    CollectionBaseInfo collectionBaseInfo = collectionBaseInfoMap.get(tableData.getId());
                    if (ObjectUtil.isNotNull(collectionBaseInfo)) {
                        tableData.setReceived(CollectionWriteOffStatusEnum.isReceived(CollectionWriteOffStatusEnum.valueOf(collectionBaseInfo.getWriteOffStatus())));
                        tableData.setReceivedDate(collectionBaseInfo.getCollectionDate());
                        tableData.setReceivedAmount(collectionBaseInfo.getCollectionAmount());
                    } else {
                        tableData.setReceived(false);
                    }
                }
            });
            rsp.setRentActualList(actualRentTableDate);
            result.add(rsp);
        }
        return R.ok(result);
    }

    @Override
    public void exportActualRent(@Valid ContractRentActualExportREQ contractRentActualExportREQ) {
        try {
            httpServletResponse.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
            httpServletResponse.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("实际租金表" + GlobalConstants.OFFICE_EXCEL_SUFFIX, StandardCharsets.UTF_8.name()));
            boolean isHistory = Objects.equals(contractRentActualExportREQ.getDataSource(), BizDataSourceEnum.HISTORY.name());
            contractRentActualService.exportExcel(isHistory, contractRentActualExportREQ, httpServletResponse.getOutputStream());
        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            log.error("导出实际租金表发生未知异常[req: {}]", JSONUtil.toJsonStr(contractRentActualExportREQ), e);
            throw new MithrasException("导出实际租金表发生未知异常");
        }
    }

    @Override
    public void exportActualCashFlow(@Valid ContractRentActualExportREQ contractRentActualExportREQ) {
        try {
            httpServletResponse.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
            httpServletResponse.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("实际现金流表" + GlobalConstants.OFFICE_EXCEL_SUFFIX, StandardCharsets.UTF_8.name()));
            boolean isHistory = Objects.equals(contractRentActualExportREQ.getDataSource(), BizDataSourceEnum.HISTORY.name());
            contractRentActualService.exportRichExcel(isHistory, contractRentActualExportREQ, httpServletResponse.getOutputStream());
        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            log.error("导出实际现金流表发生未知异常", e);
            throw new MithrasException("导出实际现金流表发生未知异常");
        }
    }

    @Override
    public R<String> downloadActualRentTemplate() {
        try {
            /*httpServletResponse.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(GlobalConstants.TEMPLATE_OSS_NAME_PROJ_REVIEW_CASH_FLOW, StandardCharsets.UTF_8.name()));
            ossClient.downLoad(httpServletResponse.getOutputStream(), GlobalConstants.TEMPLATE_OSS_NAME_PROJ_REVIEW_CASH_FLOW);*/
            return R.ok(ossClient.getPreviewUrl(GlobalConstants.TEMPLATE_OSS_NAME_PROJ_REVIEW_CASH_FLOW, GlobalConstants.FILE_TEMPLATE_EXPIRY));
        } catch (Exception e) {
            log.error("下载实际租金表模板发生异常", e);
            throw new MithrasException("下载模板发生异常");
        }
    }

    @Override
    public R<List<ContractRentPhaseRSP>> rentPhaseList(@Valid ContractPriceDetailREQ req) {
        List<ContractRentActual> contractRentActualList = contractRentActualService.firstRentByContract(req.getContractId());
        List<ContractRentPhaseRSP> rsps = new ArrayList<>();
        if (ObjectUtil.isNotEmpty(contractRentActualList)) {
            //填充第0期
            List<Integer> collect = contractRentActualList.stream().map(ContractRentActual::getCashFlowPhase).collect(Collectors.toList());
            if (!collect.contains(0)) {
                rsps.add(new ContractRentPhaseRSP(String.format(PHASE, 0), 0));
            }
            contractRentActualList.forEach(e -> {
                rsps.add(new ContractRentPhaseRSP(String.format(PHASE, e.getCashFlowPhase()), e.getCashFlowPhase()));
            });
        }
        return R.ok(rsps);
    }

    @Override
    public R<BigDecimal> calculateCombinedIRR(ContractSingleIdREQ contractSingleIdREQ) {
        BigDecimal result = contractReceiptService.calculateCombinedIRR(contractSingleIdREQ.getContractId(), contractSingleIdREQ.getVersion());
        return R.ok(result);
    }
}
