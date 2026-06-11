package cn.zswltech.mithras.report.service.agg;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.flow.core.enums.ProcessBusinessStatusEnum;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.report.mortgage.MortgageListREQ;
import cn.zswltech.mithras.dto.report.mortgage.MortgageListRSP;
import cn.zswltech.mithras.dto.version.DiffValue;
import cn.zswltech.mithras.report.config.ReportConstants;
import cn.zswltech.mithras.report.enums.biz.*;
import cn.zswltech.mithras.report.enums.common.ApprovalStatus;
import cn.zswltech.mithras.report.enums.common.QueryChannel;
import cn.zswltech.mithras.report.enums.common.ReportPageEnum;
import cn.zswltech.mithras.report.enums.common.ReportState;
import cn.zswltech.mithras.report.flow.ICrProcessWorker;
import cn.zswltech.mithras.report.flow.ProcHelper;
import cn.zswltech.mithras.report.mapper.BatchRecordMapper;
import cn.zswltech.mithras.report.mapper.CrModifyDataSnapMapper;
import cn.zswltech.mithras.report.mapper.draft.model.CrMortgageDraft;
import cn.zswltech.mithras.report.mapper.formal.model.CrAccount;
import cn.zswltech.mithras.report.mapper.formal.model.CrMortgage;
import cn.zswltech.mithras.report.mapper.fullsnap.CrMortgageFullSnapMapper;
import cn.zswltech.mithras.report.mapper.model.BatchRecord;
import cn.zswltech.mithras.report.mapper.model.CrModifyDataSnap;
import cn.zswltech.mithras.report.mapper.procsnap.model.CrMortgageProcSnap;
import cn.zswltech.mithras.report.service.CommonInfoService;
import cn.zswltech.mithras.report.service.CrModifyDataSnapService;
import cn.zswltech.mithras.report.service.draft.CrMortgageDraftService;
import cn.zswltech.mithras.report.service.formal.CrAccountService;
import cn.zswltech.mithras.report.service.formal.CrMortgageService;
import cn.zswltech.mithras.report.service.procsnap.CrMortgageProcSnapService;
import cn.zswltech.mithras.report.util.ReportBizUtil;
import cn.zswltech.mithras.report.util.ReportCompareUtil;
import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.contract.enums.contract.MortgageTypeEnum;
import cn.zswltech.mithras.contract.gendoc.BusinessDataRepository;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.foundation.context.SpringContextHolder;
import cn.zswltech.mithras.foundation.util.Util;
import cn.zswltech.mithras.foundation.util.LongUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 征信报送-抵押表接口
 *
 * @author wangchuanhao
 * @date 2023/1/11 5:19 PM
 */
@Service
@Order(-1)
public class CrMortgageAggService implements ICrProcessWorker<CrMortgageDraft, MortgageListREQ> {

    @Resource
    private CrMortgageService crMortgageService;
    @Resource
    private CrMortgageDraftService crMortgageDraftService;
    @Resource
    private CrMortgageProcSnapService crMortgageProcSnapService;
    @Resource
    private ProcHelper procHelper;
    @Resource
    private CrAccountService crAccountService;
    @Resource
    private CrMortgageFullSnapMapper crMortgageFullSnapMapper;
    @Resource
    private CommonInfoService commonInfoService;
    @Resource
    private CrModifyDataSnapMapper modifyDataSnapMapper;
    @Resource
    private BusinessDataRepository businessDataRepository;

    @Override
    public PageR<Map<String, DiffValue>> list(MortgageListREQ req) {
        ReportBizUtil.checkListREQ(req);
        QueryChannel channel = QueryChannel.of(req.getChannel());
        switch (channel) {
            case EDIT:
                return listByEdit(req);
            case PROC:
                return listByProc(req);
            case PROC_BATCH:
                return listByBatchIncre(req);
            case EFFECT:
                return listByEffect(req);
            default:
                break;
        }
        return PageR.of(new ArrayList<>(), 0);
    }

    @Override
    public String converter(@NotNull String fieldName, DiffValue value) {
        switch (fieldName) {
            case "applyPaymentAmount":
            case "assessedValue":
                return new BigDecimal(LongUtil.null2zero(Long.parseLong(String.valueOf(value.getValue()))))
                        .divide(new BigDecimal(10000),2, RoundingMode.HALF_UP).toPlainString()
                        .replaceAll("\\.00", "");
            case "appraisalCompanyType":
                return Optional.ofNullable(MortgageAppraisalCompanyTypeEnum.getByValue(String.valueOf(value.getValue()))).map(MortgageAppraisalCompanyTypeEnum::getDisplay).orElse("");
            case "maxFlag":
                return Optional.ofNullable(YesOrNoNumberEnum.findByCodeStr(String.valueOf(value.getValue()))).map(YesOrNoNumberEnum::getChinese).orElse(YesOrNoNumberEnum.NO.getChinese());
            case "modelType":
                return Optional.ofNullable(MortgageModelTypeEnum.getByValue(String.valueOf(value.getValue()))).map(MortgageModelTypeEnum::getDisplay).orElse("");
            case "mortgageType":
                return Optional.ofNullable(CrClientTypeEnum.getByValue(String.valueOf(value.getValue()))).map(CrClientTypeEnum::getDisplay).orElse("");
            case "type":
                return Optional.ofNullable(MortgageDataTypeEnum.getByValue(String.valueOf(value.getValue()))).map(MortgageDataTypeEnum::getDisplay).orElse("");
            case "mortgageIdType":
                // clientType 为 1 的时候  用certType 不然 显示统一社会信用代码
                return businessDataRepository.getCertTypeNameFromLocalCache(String.valueOf(value.getValue()));
            default:
                return Objects.nonNull(value.getValue()) ? String.valueOf(value.getValue()) : "";
        }
    }

    public PageR<Map<String, DiffValue>> listByEdit(MortgageListREQ req) {
        Page<CrMortgageDraft> dataPage = crMortgageDraftService.getBaseMapper().selectPage(new Page<>(req.getPage(), req.getPageSize()), Wrappers.<CrMortgageDraft>lambdaQuery()
                .eq(CrMortgageDraft::getReportState, ReportState.TO_BE_REPORT.name())
                .eq(StringUtils.isNotBlank(req.getApprovalStatus()), CrMortgageDraft::getApprovalStatus, req.getApprovalStatus())
                .like(StringUtils.isNotBlank(req.getPaymentApplyCode()), CrMortgageDraft::getPaymentApplyCode, req.getPaymentApplyCode())
                .like(StringUtils.isNotBlank(req.getClientName()), CrMortgageDraft::getMortgageName, req.getClientName()));
        return getMapPageR(dataPage, null, null, 1);
    }

    @NotNull
    private PageR<Map<String, DiffValue>> getMapPageR(Page<CrMortgageDraft> dataPage, String procBusinessKey, String batchNo, Integer isShow) {
        List<CrMortgageDraft> records = dataPage.getRecords();
        if (CollUtil.isEmpty(records)) {
            return PageR.of(Collections.emptyList(), dataPage.getTotal(), dataPage.getPages(), dataPage.getCurrent(), dataPage.getSize());
        }
        Set<Long> contractIds = records.stream().map(CrMortgageDraft::getContractId).collect(Collectors.toSet());
        Map<Long, ContractBaseInfo> contractBaseInfoMap = commonInfoService.getLongContractBaseInfoMap(contractIds);
        List<MortgageListRSP> rspList = records.stream().map(d -> {
            MortgageListRSP mortgage = BeanUtil.copyProperties(d, MortgageListRSP.class);
            ContractBaseInfo info = Optional.ofNullable(contractBaseInfoMap.get(d.getContractId())).orElse(new ContractBaseInfo());
            mortgage.setContractCode(info.getContractCode());
            return mortgage;
        }).collect(Collectors.toList());
        List<String> businessKeys = rspList.stream().map(MortgageListRSP::getBusinessKey).collect(Collectors.toList());
        List<Map<String, DiffValue>> mapList = commonInfoService.buildDiffMapList(rspList, businessKeys, procBusinessKey, TableTypeEnum.MORTGAGE, MortgageListRSP.class, batchNo, isShow);
        return PageR.of(mapList, dataPage.getTotal(),
                dataPage.getPages(),
                dataPage.getCurrent(),
                dataPage.getSize());
    }

    public PageR<Map<String, DiffValue>> listByProc(MortgageListREQ req) {
        boolean processEndFlag = procHelper.judgeProcessEndWithCheck(req.getProcBusinessKey());
        if (processEndFlag) {
            Page<CrMortgageProcSnap> dataPage = crMortgageProcSnapService.getBaseMapper().selectPage(new Page<>(req.getPage(), req.getPageSize()), Wrappers.<CrMortgageProcSnap>lambdaQuery()
                    .eq(CrMortgageProcSnap::getProcBusinessKey, Long.valueOf(req.getProcBusinessKey()))
                    .like(StringUtils.isNotBlank(req.getPaymentApplyCode()), CrMortgageProcSnap::getPaymentApplyCode, req.getPaymentApplyCode())
                    .like(StringUtils.isNotBlank(req.getClientName()), CrMortgageProcSnap::getMortgageName, req.getClientName())
            );
            List<CrMortgageProcSnap> records = dataPage.getRecords();
            if (CollUtil.isEmpty(records)) {
                return PageR.of(Collections.emptyList(), dataPage.getTotal(), dataPage.getPages(), dataPage.getCurrent(), dataPage.getSize());
            }
            Set<Long> contractIds = records.stream().map(CrMortgageProcSnap::getContractId).collect(Collectors.toSet());
            Map<Long, ContractBaseInfo> contractBaseInfoMap = commonInfoService.getLongContractBaseInfoMap(contractIds);
            List<MortgageListRSP> rspList = records.stream().map(d -> {
                MortgageListRSP mortgage = BeanUtil.copyProperties(d, MortgageListRSP.class);
                ContractBaseInfo info = Optional.ofNullable(contractBaseInfoMap.get(d.getContractId())).orElse(new ContractBaseInfo());
                mortgage.setContractCode(info.getContractCode());
                return mortgage;
            }).collect(Collectors.toList());
            List<String> businessKeys = rspList.stream().map(MortgageListRSP::getBusinessKey).collect(Collectors.toList());
            List<Map<String, DiffValue>> mapList = commonInfoService.buildDiffMapList(rspList, businessKeys, req.getProcBusinessKey(), TableTypeEnum.MORTGAGE, MortgageListRSP.class, null, null);
            return PageR.of(mapList, dataPage.getTotal(),
                    dataPage.getPages(),
                    dataPage.getCurrent(),
                    dataPage.getSize());
        } else {
            Page<CrMortgageDraft> dataPage = crMortgageDraftService.getBaseMapper().selectPage(new Page<>(req.getPage(), req.getPageSize()), Wrappers.<CrMortgageDraft>lambdaQuery()
                            .eq(CrMortgageDraft::getProcBusinessKey, Long.valueOf(req.getProcBusinessKey()))
                            .eq(CrMortgageDraft::getReportState, ReportState.TO_BE_REPORT.name())
                            .eq(StringUtils.isNotBlank(req.getApprovalStatus()), CrMortgageDraft::getApprovalStatus, req.getApprovalStatus())
                            .like(StringUtils.isNotBlank(req.getPaymentApplyCode()), CrMortgageDraft::getPaymentApplyCode, req.getPaymentApplyCode())
                            .like(StringUtils.isNotBlank(req.getClientName()), CrMortgageDraft::getMortgageName, req.getClientName())
//                    .inSql(CrMortgageDraft::getPaymentId, "SELECT payment_id FROM cr_account_draft WHERE report_flag = 1")
            );
            return getMapPageR(dataPage, req.getProcBusinessKey(), null, null);
        }
    }

    public PageR<Map<String, DiffValue>> listByBatchIncre(MortgageListREQ req) {
        Page<CrMortgageProcSnap> dataPage = crMortgageProcSnapService.getBaseMapper().selectPage(new Page<>(req.getPage(), req.getPageSize()), Wrappers.<CrMortgageProcSnap>lambdaQuery()
                .eq(CrMortgageProcSnap::getBatchId, req.getBatchId())
                .like(StringUtils.isNotBlank(req.getPaymentApplyCode()), CrMortgageProcSnap::getPaymentApplyCode, req.getPaymentApplyCode())
                .like(StringUtils.isNotBlank(req.getClientName()), CrMortgageProcSnap::getMortgageName, req.getClientName())

        );
        List<CrMortgageProcSnap> records = dataPage.getRecords();
        if (CollUtil.isEmpty(records)) {
            return PageR.of(Collections.emptyList(), dataPage.getTotal(), dataPage.getPages(), dataPage.getCurrent(), dataPage.getSize());
        }
        Set<Long> contractIds = records.stream().map(CrMortgageProcSnap::getContractId).collect(Collectors.toSet());
        Map<Long, ContractBaseInfo> contractBaseInfoMap = commonInfoService.getLongContractBaseInfoMap(contractIds);
        List<MortgageListRSP> rspList = records.stream().map(d -> {
            MortgageListRSP mortgage = BeanUtil.copyProperties(d, MortgageListRSP.class);
            ContractBaseInfo info = Optional.ofNullable(contractBaseInfoMap.get(d.getContractId())).orElse(new ContractBaseInfo());
            mortgage.setContractCode(info.getContractCode());
            return mortgage;
        }).collect(Collectors.toList());
        List<String> businessKeys = rspList.stream().map(MortgageListRSP::getBusinessKey).collect(Collectors.toList());
        BatchRecord batchRecord = SpringContextHolder.getBean(BatchRecordMapper.class).selectById(req.getBatchId());
        List<Map<String, DiffValue>> mapList = commonInfoService.buildDiffMapList(rspList, businessKeys, null, TableTypeEnum.MORTGAGE, MortgageListRSP.class, batchRecord.getBatchNo(), null);
        return PageR.of(mapList, dataPage.getTotal(),
                dataPage.getPages(),
                dataPage.getCurrent(),
                dataPage.getSize());
    }

    public PageR<Map<String, DiffValue>> listByEffect(MortgageListREQ req) {
        String paymentApplyCode = null;
        if (Objects.nonNull(req.getAccountId())) {
            CrAccount crAccount = crAccountService.getById(req.getAccountId());
            if (Objects.isNull(crAccount)) {
                throw new MithrasException("账户表数据不存在");
            }
            paymentApplyCode = crAccount.getPaymentApplyCode();
        }
        Page<CrMortgage> dataPage = crMortgageService.getBaseMapper().selectPage(new Page<>(req.getPage(), req.getPageSize()), Wrappers.<CrMortgage>lambdaQuery()
                .eq(Objects.nonNull(paymentApplyCode), CrMortgage::getPaymentApplyCode, paymentApplyCode)
                .like(StringUtils.isNotBlank(req.getPaymentApplyCode()), CrMortgage::getPaymentApplyCode, req.getPaymentApplyCode())
                .like(StringUtils.isNotBlank(req.getClientName()), CrMortgage::getMortgageName, req.getClientName())
        );
        List<CrMortgage> records = dataPage.getRecords();
        if (CollUtil.isEmpty(records)) {
            return PageR.of(Collections.emptyList(), dataPage.getTotal(), dataPage.getPages(), dataPage.getCurrent(), dataPage.getSize());
        }
        Set<Long> contractIds = records.stream().map(CrMortgage::getContractId).collect(Collectors.toSet());
        Map<Long, ContractBaseInfo> contractBaseInfoMap = commonInfoService.getLongContractBaseInfoMap(contractIds);
        List<MortgageListRSP> rspList = records.stream().map(d -> {
            MortgageListRSP mortgage = BeanUtil.copyProperties(d, MortgageListRSP.class);
            ContractBaseInfo info = Optional.ofNullable(contractBaseInfoMap.get(d.getContractId())).orElse(new ContractBaseInfo());
            mortgage.setContractCode(info.getContractCode());
            return mortgage;
        }).collect(Collectors.toList());
        List<String> businessKeys = rspList.stream().map(MortgageListRSP::getBusinessKey).collect(Collectors.toList());
        List<Map<String, DiffValue>> mapList = commonInfoService.buildDiffMapEffectList(rspList, businessKeys, TableTypeEnum.MORTGAGE, MortgageListRSP.class);
        return PageR.of(mapList, dataPage.getTotal(),
                dataPage.getPages(),
                dataPage.getCurrent(),
                dataPage.getSize());
    }

    /**
     * 请注意该类需要在accountAggService之前执行，依赖AccountDraft数据的状态
     */
    @Override
    @Transactional(rollbackFor = Exception.class, transactionManager = ReportConstants.TRANSACTION_MANAGER)
    public void processEnd(Long procBusinessKey, Integer endType, Long startUserId, String processInstanceId,
                           LocalDateTime reportTime, BatchRecord procSnapRecord, BatchRecord fullSnapRecord) {
        boolean processPass = ProcessBusinessStatusEnum.success(endType);
        List<CrMortgageDraft> draftList = crMortgageDraftService.getBaseMapper().selectList(Wrappers.<CrMortgageDraft>lambdaQuery()
                .eq(CrMortgageDraft::getReportState, ReportState.TO_BE_REPORT.name())
                .eq(CrMortgageDraft::getProcBusinessKey, procBusinessKey)
        );
        // 1.编辑区数据插入ProcSnap 对应的账户表报送，此子表才显示 才抄
        List<CrMortgageProcSnap> procSnapList = draftList.stream()
                .filter(d -> Objects.equals(YesOrNoNumberEnum.YES.getCode(), d.getReportFlag()))
                .map(d -> {
                    CrMortgageProcSnap procSnap = BeanUtil.copyProperties(d, CrMortgageProcSnap.class);
                    procSnap.setId(null);
                    procSnap.setBatchId(Optional.ofNullable(procSnapRecord).map(BatchRecord::getId).orElse(null));
                    procSnap.setBatchNo(Optional.ofNullable(procSnapRecord).map(BatchRecord::getBatchNo).orElse(null));
                    return procSnap;
                }).collect(Collectors.toList());
        crMortgageProcSnapService.saveBatch(procSnapList);
        if (processPass) {
            // 1.如果审批成功 把修改数据更新到draft表
            // 2.如果审批成功 把编辑区选择报送的数据抄到生效区 对应的账户表报送，此子表才报送
            if (CollectionUtils.isNotEmpty(draftList)) {
                List<CrModifyDataSnap> modifyDataSnaps = modifyDataSnapMapper.selectList(Wrappers.<CrModifyDataSnap>lambdaQuery()
                        .eq(CrModifyDataSnap::getProcBusinessKey, procBusinessKey)
                        .eq(CrModifyDataSnap::getTableType, TableTypeEnum.MORTGAGE.name())
                        .eq(CrModifyDataSnap::getIsTakeEffect, YesOrNoNumberEnum.YES.getCode()));

                Map<String, CrModifyDataSnap> modifyDataSnapMap = new LinkedHashMap<>();
                if (CollUtil.isNotEmpty(modifyDataSnaps)) {
                    modifyDataSnapMap = modifyDataSnaps.stream().collect(Collectors.toMap(CrModifyDataSnap::getBusinessKey, Function.identity(), (a, b) -> a));
                }
                Map<String, CrModifyDataSnap> finalModifyDataSnapMap = modifyDataSnapMap;
                draftList.forEach(draft -> {
                    CrModifyDataSnap modifyDataSnap = finalModifyDataSnapMap.get(draft.getBusinessKey());
                    if (ObjectUtil.isNull(modifyDataSnap)) {
                        return;
                    }
                    CrMortgageDraft mortgageDraft = JSONUtil.toBean(modifyDataSnap.getDataMap(), CrMortgageDraft.class);
                    BeanUtil.copyProperties(mortgageDraft, draft, ReportConstants.IGNORE_ID);
                });
                List<CrMortgage> draft2FormalList = draftList.stream().filter(d -> Objects.equals(1, d.getReportFlag())).map(d -> {
                    CrMortgage formal = BeanUtil.copyProperties(d, CrMortgage.class);
                    formal.setId(null);
                    return formal;
                }).collect(Collectors.toList());
                List<CrMortgage> needInsertList = new ArrayList<>();
                Map<String, CrMortgage> existReportDataMap = crMortgageService.list(Wrappers.<CrMortgage>lambdaQuery()
                                .in(CrMortgage::getBusinessKey, draftList.stream().map(CrMortgageDraft::getBusinessKey).collect(Collectors.toSet())))
                        .stream().collect(Collectors.toMap(CrMortgage::getBusinessKey, cr -> cr));
                for (CrMortgage draft2Formal : draft2FormalList) {
                    if (!existReportDataMap.containsKey(draft2Formal.getBusinessKey())) {
                        needInsertList.add(draft2Formal);
                    } else {
                        CrMortgage existData = existReportDataMap.get(draft2Formal.getBusinessKey());
                        if (ReportCompareUtil.checkChange(draft2Formal, existData, existData.ignoreCompareFieldNames())) {
                            draft2Formal.setId(existData.getId());
                            crMortgageService.updateById(draft2Formal);
                        }
                    }
                }
                crMortgageService.saveBatch(needInsertList);
            }
            // 3.如果审批成功 把生效区数据全量抄到FullSnap
            crMortgageFullSnapMapper.copyFromEffect(Optional.ofNullable(fullSnapRecord).map(BatchRecord::getId).orElse(null), Optional.ofNullable(fullSnapRecord).map(BatchRecord::getBatchNo).orElse(null));
        }
        // 4.编辑区数据处理
        List<Long> existReportDraftIdList = draftList.stream().filter(d -> Objects.equals(YesOrNoNumberEnum.YES.getCode(), d.getReportFlag())).map(CrMortgageDraft::getId).collect(Collectors.toList());
        List<Long> notReportDraftIdList = draftList.stream().filter(d -> Objects.equals(YesOrNoNumberEnum.NO.getCode(), d.getReportFlag())).map(CrMortgageDraft::getId).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(existReportDraftIdList)) {
            // 已报送数据处理
            // 审批通过才处理成已报送
            crMortgageDraftService.lambdaUpdate()
                    .set(processPass, CrMortgageDraft::getReportState, ReportState.REPORTED.name())
                    .set(CrMortgageDraft::getApprovalStatus, ApprovalStatus.UN_SUBMIT.name())
                    .set(CrMortgageDraft::getProcBusinessKey, null)
                    .in(CrMortgageDraft::getId, existReportDraftIdList)
                    .update();
        }
        if (CollectionUtils.isNotEmpty(notReportDraftIdList)) {
            // 不报送数据处理
            crMortgageDraftService.lambdaUpdate()
                    .set(CrMortgageDraft::getReportState, ReportState.TO_BE_REPORT.name())
                    .set(CrMortgageDraft::getApprovalStatus, ApprovalStatus.UN_SUBMIT.name())
                    .set(CrMortgageDraft::getProcBusinessKey, null)
                    .in(CrMortgageDraft::getId, notReportDraftIdList)
                    .update();
        }
    }

    @Override
    public void submit(Long procBusinessKey, String batchNo) {
        crMortgageDraftService.lambdaUpdate()
                .eq(CrMortgageDraft::getReportState, ReportState.TO_BE_REPORT.name())
                .set(CrMortgageDraft::getApprovalStatus, ApprovalStatus.UNDER_APPROVAL.name())
                .set(CrMortgageDraft::getProcBusinessKey, procBusinessKey)
                .update();

        //更新数据修改到流程
        SpringContextHolder.getBean(CrModifyDataSnapService.class)
                .lambdaUpdate()
                .eq(CrModifyDataSnap::getTableType, TableTypeEnum.MORTGAGE.name())
                .isNull(CrModifyDataSnap::getProcBusinessKey)
                .set(CrModifyDataSnap::getBatchNo, batchNo)
                .set(CrModifyDataSnap::getApprovalStatus, ApprovalStatus.UNDER_APPROVAL.name())
                .set(CrModifyDataSnap::getProcBusinessKey, procBusinessKey)
                .update();
    }

    @Override
    public ReportPageEnum reportPageEnum() {
        return ReportPageEnum.MORTGAGE;
    }

    @Override
    public int countInProcessData() {
        return crMortgageDraftService.count(Wrappers.<CrMortgageDraft>lambdaQuery()
                .select(CrMortgageDraft::getId)
                .eq(CrMortgageDraft::getApprovalStatus, ApprovalStatus.UNDER_APPROVAL.name())
        );
    }

    @Override
    public IService<CrMortgageDraft> getServiceInstance() {
        return crMortgageDraftService;
    }

}
