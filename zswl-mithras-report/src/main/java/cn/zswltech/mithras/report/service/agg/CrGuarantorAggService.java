package cn.zswltech.mithras.report.service.agg;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.flow.core.enums.ProcessBusinessStatusEnum;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.report.guarantor.GuarantorListREQ;
import cn.zswltech.mithras.dto.report.guarantor.GuarantorListRSP;
import cn.zswltech.mithras.dto.version.DiffValue;
import cn.zswltech.mithras.report.config.ReportConstants;
import cn.zswltech.mithras.report.enums.biz.CrClientTypeEnum;
import cn.zswltech.mithras.report.enums.biz.GuarantorClientClassEnum;
import cn.zswltech.mithras.report.enums.biz.GuarantorJointFlagEnum;
import cn.zswltech.mithras.report.enums.biz.TableTypeEnum;
import cn.zswltech.mithras.report.enums.common.ApprovalStatus;
import cn.zswltech.mithras.report.enums.common.QueryChannel;
import cn.zswltech.mithras.report.enums.common.ReportPageEnum;
import cn.zswltech.mithras.report.enums.common.ReportState;
import cn.zswltech.mithras.report.flow.ICrProcessWorker;
import cn.zswltech.mithras.report.flow.ProcHelper;
import cn.zswltech.mithras.report.mapper.BatchRecordMapper;
import cn.zswltech.mithras.report.mapper.CrModifyDataSnapMapper;
import cn.zswltech.mithras.report.mapper.base.model.CrBaseModel;
import cn.zswltech.mithras.report.mapper.draft.model.CrGuarantorDraft;
import cn.zswltech.mithras.report.mapper.formal.model.CrAccount;
import cn.zswltech.mithras.report.mapper.formal.model.CrGuarantor;
import cn.zswltech.mithras.report.mapper.fullsnap.CrGuarantorFullSnapMapper;
import cn.zswltech.mithras.report.mapper.model.BatchRecord;
import cn.zswltech.mithras.report.mapper.model.CrModifyDataSnap;
import cn.zswltech.mithras.report.mapper.procsnap.model.CrGuarantorProcSnap;
import cn.zswltech.mithras.report.service.CommonInfoService;
import cn.zswltech.mithras.report.service.CrModifyDataSnapService;
import cn.zswltech.mithras.report.service.draft.CrGuarantorDraftService;
import cn.zswltech.mithras.report.service.formal.CrAccountService;
import cn.zswltech.mithras.report.service.formal.CrGuarantorService;
import cn.zswltech.mithras.report.service.procsnap.CrGuarantorProcSnapService;
import cn.zswltech.mithras.report.util.ReportBizUtil;
import cn.zswltech.mithras.report.util.ReportCompareUtil;
import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;
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
 * 征信报送-保证表接口
 *
 * @author wangchuanhao
 * @date 2023/1/11 5:19 PM
 */
@Service
@Order(-1)
public class CrGuarantorAggService implements ICrProcessWorker<CrGuarantorDraft, GuarantorListREQ> {

    @Resource
    private CrGuarantorDraftService crGuarantorDraftService;
    @Resource
    private CrGuarantorService crGuarantorService;
    @Resource
    private CrGuarantorProcSnapService crGuarantorProcSnapService;
    @Resource
    private ProcHelper procHelper;
    @Resource
    private CrAccountService crAccountService;
    @Resource
    private CrGuarantorFullSnapMapper crGuarantorFullSnapMapper;
    @Resource
    private CommonInfoService commonInfoService;
    @Resource
    private CrModifyDataSnapMapper modifyDataSnapMapper;
    @Resource
    private BusinessDataRepository businessDataRepository;


    @Override
    public PageR<Map<String, DiffValue>> list(GuarantorListREQ req) {
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
            case "clientClass":
                return Optional.ofNullable(GuarantorClientClassEnum.getByValue(String.valueOf(value.getValue()))).map(GuarantorClientClassEnum::getDisplay).orElse("");
            case "clientType":
                return Optional.ofNullable(CrClientTypeEnum.getByValue(String.valueOf(value.getValue()))).map(CrClientTypeEnum::getDisplay).orElse("");
//            case "guarantorIdType":
//                return businessDataRepository.getCertTypeNameFromLocalCache(String.valueOf(value.getValue()));
            case "jointGuarantorFlag":
                return Optional.ofNullable(GuarantorJointFlagEnum.getByValue(String.valueOf(value.getValue()))).map(GuarantorJointFlagEnum::getDisplay).orElse("");
            case "repayLiabilityAmount":
                if (value.getValue() == null) {
                    return "-";
                }
                return new BigDecimal(LongUtil.null2zero(Long.parseLong(String.valueOf(value.getValue()))))
                        .divide(new BigDecimal(10000),2, RoundingMode.HALF_UP).toPlainString()
                        .replaceAll("\\.00", "");
            default:
                return Objects.nonNull(value.getValue()) ? String.valueOf(value.getValue()) : "";
        }
    }

    public PageR<Map<String, DiffValue>> listByEdit(GuarantorListREQ req) {
        Page<CrGuarantorDraft> dataPage = crGuarantorDraftService.getBaseMapper().selectPage(new Page<>(req.getPage(), req.getPageSize()), Wrappers.<CrGuarantorDraft>lambdaQuery()
                .eq(CrGuarantorDraft::getReportState, ReportState.TO_BE_REPORT.name())
                .eq(StringUtils.isNotBlank(req.getApprovalStatus()), CrGuarantorDraft::getApprovalStatus, req.getApprovalStatus())
                .like(StringUtils.isNotBlank(req.getPaymentApplyCode()), CrGuarantorDraft::getPaymentApplyCode, req.getPaymentApplyCode())
                .like(StringUtils.isNotBlank(req.getClientName()), CrGuarantorDraft::getClientName, req.getClientName()));
        return getMapPageR(dataPage, null, null, 1);
    }

    @NotNull
    private PageR<Map<String, DiffValue>> getMapPageR(Page<CrGuarantorDraft> dataPage, String procBusinessKey, String batchNo, Integer isShow) {
        List<CrGuarantorDraft> records = dataPage.getRecords();
        if (CollUtil.isEmpty(records)) {
            return PageR.of(Collections.emptyList(), dataPage.getTotal(), dataPage.getPages(), dataPage.getCurrent(), dataPage.getSize());
        }
        Set<Long> contractIds = records.stream().map(CrGuarantorDraft::getContractId).collect(Collectors.toSet());
        Map<Long, ContractBaseInfo> contractBaseInfoMap = commonInfoService.getLongContractBaseInfoMap(contractIds);
        List<GuarantorListRSP> rspList = records.stream().map(d -> {
            GuarantorListRSP guarantor = BeanUtil.copyProperties(d, GuarantorListRSP.class);
            ContractBaseInfo info = Optional.ofNullable(contractBaseInfoMap.get(d.getContractId())).orElse(new ContractBaseInfo());
            guarantor.setContractCode(info.getContractCode());
            return guarantor;
        }).collect(Collectors.toList());
        List<String> businessKeys = rspList.stream().map(GuarantorListRSP::getBusinessKey).collect(Collectors.toList());
        List<Map<String, DiffValue>> mapList = commonInfoService.buildDiffMapList(rspList, businessKeys, procBusinessKey, TableTypeEnum.GUARANTOR, GuarantorListRSP.class, batchNo, isShow);
        return PageR.of(mapList, dataPage.getTotal(),
                dataPage.getPages(),
                dataPage.getCurrent(),
                dataPage.getSize());
    }

    public PageR<Map<String, DiffValue>> listByProc(GuarantorListREQ req) {
        boolean processEndFlag = procHelper.judgeProcessEndWithCheck(req.getProcBusinessKey());
        if (processEndFlag) {
            Page<CrGuarantorProcSnap> dataPage = crGuarantorProcSnapService.getBaseMapper().selectPage(new Page<>(req.getPage(), req.getPageSize()), Wrappers.<CrGuarantorProcSnap>lambdaQuery()
                    .eq(CrGuarantorProcSnap::getProcBusinessKey, Long.valueOf(req.getProcBusinessKey()))
                    .like(StringUtils.isNotBlank(req.getPaymentApplyCode()), CrGuarantorProcSnap::getPaymentApplyCode, req.getPaymentApplyCode())
                    .like(StringUtils.isNotBlank(req.getClientName()), CrGuarantorProcSnap::getClientName, req.getClientName())
            );
            List<CrGuarantorProcSnap> records = dataPage.getRecords();
            if (CollUtil.isEmpty(records)) {
                return PageR.of(Collections.emptyList(), dataPage.getTotal(), dataPage.getPages(), dataPage.getCurrent(), dataPage.getSize());
            }
            Set<Long> contractIds = records.stream().map(CrGuarantorProcSnap::getContractId).collect(Collectors.toSet());
            Map<Long, ContractBaseInfo> contractBaseInfoMap = commonInfoService.getLongContractBaseInfoMap(contractIds);
            List<GuarantorListRSP> rspList = records.stream().map(d -> {
                GuarantorListRSP guarantor = BeanUtil.copyProperties(d, GuarantorListRSP.class);
                ContractBaseInfo info = Optional.ofNullable(contractBaseInfoMap.get(d.getContractId())).orElse(new ContractBaseInfo());
                guarantor.setContractCode(info.getContractCode());
                return guarantor;
            }).collect(Collectors.toList());
            List<String> businessKeys = rspList.stream().map(GuarantorListRSP::getBusinessKey).collect(Collectors.toList());
            List<Map<String, DiffValue>> mapList = commonInfoService.buildDiffMapList(rspList, businessKeys, req.getProcBusinessKey(), TableTypeEnum.GUARANTOR, GuarantorListRSP.class, null, null);
            return PageR.of(mapList, dataPage.getTotal(),
                    dataPage.getPages(),
                    dataPage.getCurrent(),
                    dataPage.getSize());
        } else {
            Page<CrGuarantorDraft> dataPage = crGuarantorDraftService.getBaseMapper().selectPage(new Page<>(req.getPage(), req.getPageSize()), Wrappers.<CrGuarantorDraft>lambdaQuery()
                    .eq(CrGuarantorDraft::getProcBusinessKey, Long.valueOf(req.getProcBusinessKey()))
                    .eq(CrGuarantorDraft::getReportState, ReportState.TO_BE_REPORT.name())
                    .eq(StringUtils.isNotBlank(req.getApprovalStatus()), CrGuarantorDraft::getApprovalStatus, req.getApprovalStatus())
                    .like(StringUtils.isNotBlank(req.getPaymentApplyCode()), CrGuarantorDraft::getPaymentApplyCode, req.getPaymentApplyCode())
                    .like(StringUtils.isNotBlank(req.getClientName()), CrGuarantorDraft::getClientName, req.getClientName()));
            return getMapPageR(dataPage, req.getProcBusinessKey(), null, null);
        }
    }

    public PageR<Map<String, DiffValue>> listByBatchIncre(GuarantorListREQ req) {
        Page<CrGuarantorProcSnap> dataPage = crGuarantorProcSnapService.getBaseMapper().selectPage(new Page<>(req.getPage(), req.getPageSize()), Wrappers.<CrGuarantorProcSnap>lambdaQuery()
                .eq(CrGuarantorProcSnap::getBatchId, req.getBatchId())
                .like(StringUtils.isNotBlank(req.getPaymentApplyCode()), CrGuarantorProcSnap::getPaymentApplyCode, req.getPaymentApplyCode())
                .like(StringUtils.isNotBlank(req.getClientName()), CrGuarantorProcSnap::getClientName, req.getClientName())

        );
        List<CrGuarantorProcSnap> records = dataPage.getRecords();
        if (CollUtil.isEmpty(records)) {
            return PageR.of(Collections.emptyList(), dataPage.getTotal(), dataPage.getPages(), dataPage.getCurrent(), dataPage.getSize());
        }
        Set<Long> contractIds = records.stream().map(CrGuarantorProcSnap::getContractId).collect(Collectors.toSet());
        Map<Long, ContractBaseInfo> contractBaseInfoMap = commonInfoService.getLongContractBaseInfoMap(contractIds);
        List<GuarantorListRSP> rspList = records.stream().map(d -> {
            GuarantorListRSP guarantor = BeanUtil.copyProperties(d, GuarantorListRSP.class);
            ContractBaseInfo info = Optional.ofNullable(contractBaseInfoMap.get(d.getContractId())).orElse(new ContractBaseInfo());
            guarantor.setContractCode(info.getContractCode());
            return guarantor;
        }).collect(Collectors.toList());
        List<String> businessKeys = rspList.stream().map(GuarantorListRSP::getBusinessKey).collect(Collectors.toList());
        BatchRecord batchRecord = SpringContextHolder.getBean(BatchRecordMapper.class).selectById(req.getBatchId());
        List<Map<String, DiffValue>> mapList = commonInfoService.buildDiffMapList(rspList, businessKeys, null, TableTypeEnum.GUARANTOR, GuarantorListRSP.class, batchRecord.getBatchNo(), null);
        return PageR.of(mapList, dataPage.getTotal(),
                dataPage.getPages(),
                dataPage.getCurrent(),
                dataPage.getSize());
    }

    public PageR<Map<String, DiffValue>> listByEffect(GuarantorListREQ req) {
        String paymentApplyCode = null;
        if (Objects.nonNull(req.getAccountId())) {
            CrAccount crAccount = crAccountService.getById(req.getAccountId());
            if (Objects.isNull(crAccount)) {
                throw new MithrasException("账户表数据不存在");
            }
            paymentApplyCode = crAccount.getPaymentApplyCode();
        }
        Page<CrGuarantor> dataPage = crGuarantorService.getBaseMapper().selectPage(new Page<>(req.getPage(), req.getPageSize()), Wrappers.<CrGuarantor>lambdaQuery()
                .eq(Objects.nonNull(paymentApplyCode), CrGuarantor::getPaymentApplyCode, paymentApplyCode)
                .like(StringUtils.isNotBlank(req.getPaymentApplyCode()), CrGuarantor::getPaymentApplyCode, req.getPaymentApplyCode())
                .like(StringUtils.isNotBlank(req.getClientName()), CrGuarantor::getClientName, req.getClientName())
        );
        List<CrGuarantor> records = dataPage.getRecords();
        if (CollUtil.isEmpty(records)) {
            return PageR.of(Collections.emptyList(), dataPage.getTotal(), dataPage.getPages(), dataPage.getCurrent(), dataPage.getSize());
        }
        Set<Long> contractIds = records.stream().map(CrGuarantor::getContractId).collect(Collectors.toSet());
        Map<Long, ContractBaseInfo> contractBaseInfoMap = commonInfoService.getLongContractBaseInfoMap(contractIds);
        List<GuarantorListRSP> rspList = records.stream().map(d -> {
            GuarantorListRSP guarantor = BeanUtil.copyProperties(d, GuarantorListRSP.class);
            ContractBaseInfo info = Optional.ofNullable(contractBaseInfoMap.get(d.getContractId())).orElse(new ContractBaseInfo());
            guarantor.setContractCode(info.getContractCode());
            return guarantor;
        }).collect(Collectors.toList());
        List<String> businessKeys = rspList.stream().map(GuarantorListRSP::getBusinessKey).collect(Collectors.toList());
        List<Map<String, DiffValue>> mapList = commonInfoService.buildDiffMapEffectList(rspList, businessKeys, TableTypeEnum.GUARANTOR, GuarantorListRSP.class);
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
        List<CrGuarantorDraft> draftList = crGuarantorDraftService.getBaseMapper().selectList(Wrappers.<CrGuarantorDraft>lambdaQuery()
                .eq(CrGuarantorDraft::getReportState, ReportState.TO_BE_REPORT.name())
                .eq(CrGuarantorDraft::getProcBusinessKey, procBusinessKey)
        );

        // 1.编辑区数据插入ProcSnap 对应的账户表报送，此子表才显示 才抄
        List<CrGuarantorProcSnap> procSnapList = draftList.stream()
                .filter(d -> Objects.equals(YesOrNoNumberEnum.YES.getCode(), d.getReportFlag()))
                .map(d -> {
                    CrGuarantorProcSnap procSnap = BeanUtil.copyProperties(d, CrGuarantorProcSnap.class, ReportConstants.IGNORE_ID);
                    procSnap.setBatchId(Optional.ofNullable(procSnapRecord).map(BatchRecord::getId).orElse(null));
                    procSnap.setBatchNo(Optional.ofNullable(procSnapRecord).map(BatchRecord::getBatchNo).orElse(null));
                    return procSnap;
                }).collect(Collectors.toList());
        crGuarantorProcSnapService.saveBatch(procSnapList);
        if (processPass) {
            // 1.如果审批成功 把修改数据更新到draft表
            // 2.如果审批成功 把编辑区选择报送的数据抄到生效区 对应的账户表报送，此子表才报送
            if (CollectionUtils.isNotEmpty(draftList)) {
                List<String> businessKey = draftList.stream()
                        .filter(a -> Objects.equals(YesOrNoNumberEnum.YES.getCode(), a.getReportFlag()))
                        .map(CrBaseModel::getBusinessKey).collect(Collectors.toList());
                List<CrModifyDataSnap> modifyDataSnaps = null;
                if (CollUtil.isNotEmpty(businessKey)) {
                    modifyDataSnaps = modifyDataSnapMapper.selectList(Wrappers.<CrModifyDataSnap>lambdaQuery()
                            .in(CrModifyDataSnap::getBusinessKey, businessKey)
                            .eq(CrModifyDataSnap::getProcBusinessKey, procBusinessKey)
                            .eq(CrModifyDataSnap::getTableType, TableTypeEnum.GUARANTOR.name())
                            .eq(CrModifyDataSnap::getIsTakeEffect, YesOrNoNumberEnum.YES.getCode()));
                }

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
                    CrGuarantorDraft guarantorDraft = JSONUtil.toBean(modifyDataSnap.getDataMap(), CrGuarantorDraft.class);
                    BeanUtil.copyProperties(guarantorDraft, draft, ReportConstants.IGNORE_ID);
                });
                List<CrGuarantor> draft2FormalList = draftList.stream()
                        .filter(d -> Objects.equals(YesOrNoNumberEnum.YES.getCode(), d.getReportFlag()))
                        .map(d -> BeanUtil.copyProperties(d, CrGuarantor.class, ReportConstants.IGNORE_ID))
                        .collect(Collectors.toList());

                List<CrGuarantor> needInsertList = new ArrayList<>();
                Map<String, CrGuarantor> existReportDataMap = crGuarantorService.list(Wrappers.<CrGuarantor>lambdaQuery()
                                .in(CrGuarantor::getBusinessKey, draftList.stream().map(CrGuarantorDraft::getBusinessKey).collect(Collectors.toSet())))
                        .stream().collect(Collectors.toMap(CrGuarantor::getBusinessKey, Function.identity(), (a, b) -> a));
                for (CrGuarantor draft2Formal : draft2FormalList) {
                    if (!existReportDataMap.containsKey(draft2Formal.getBusinessKey())) {
                        needInsertList.add(draft2Formal);
                    } else {
                        CrGuarantor existData = existReportDataMap.get(draft2Formal.getBusinessKey());
                        if (ReportCompareUtil.checkChange(draft2Formal, existData, existData.ignoreCompareFieldNames())) {
                            draft2Formal.setId(existData.getId());
                            crGuarantorService.updateById(draft2Formal);
                        }
                    }
                }
                crGuarantorService.saveBatch(needInsertList);
            }
            // 3.如果审批成功 把生效区数据全量抄到FullSnap
            crGuarantorFullSnapMapper.copyFromEffect(Optional.ofNullable(fullSnapRecord).map(BatchRecord::getId).orElse(null),
                    Optional.ofNullable(fullSnapRecord).map(BatchRecord::getBatchNo).orElse(null));
        }
        // 4.编辑区数据处理
        List<Long> existReportDraftIdList = draftList.stream().filter(d -> Objects.equals(YesOrNoNumberEnum.YES.getCode(), d.getReportFlag())).map(CrGuarantorDraft::getId).collect(Collectors.toList());
        List<Long> notReportDraftIdList = draftList.stream().filter(d -> Objects.equals(YesOrNoNumberEnum.NO.getCode(), d.getReportFlag())).map(CrGuarantorDraft::getId).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(existReportDraftIdList)) {
            // 审批通过才处理成已报送
            crGuarantorDraftService.lambdaUpdate()
                    .set(processPass, CrGuarantorDraft::getReportState, ReportState.REPORTED.name())
                    .set(CrGuarantorDraft::getApprovalStatus, ApprovalStatus.UN_SUBMIT.name())
                    .set(CrGuarantorDraft::getProcBusinessKey, null)
                    .in(CrGuarantorDraft::getId, existReportDraftIdList)
                    .update();
        }
        if (CollectionUtils.isNotEmpty(notReportDraftIdList)) {
            // 不报送数据处理
            crGuarantorDraftService.lambdaUpdate()
                    .set(CrGuarantorDraft::getReportState, ReportState.TO_BE_REPORT.name())
                    .set(CrGuarantorDraft::getApprovalStatus, ApprovalStatus.UN_SUBMIT.name())
                    .set(CrGuarantorDraft::getProcBusinessKey, null)
                    .in(CrGuarantorDraft::getId, notReportDraftIdList)
                    .update();
        }
    }

    @Override
    public void submit(Long procBusinessKey, String batchNo) {
        crGuarantorDraftService.lambdaUpdate()
                .eq(CrGuarantorDraft::getReportState, ReportState.TO_BE_REPORT.name())
                .set(CrGuarantorDraft::getApprovalStatus, ApprovalStatus.UNDER_APPROVAL.name())
                .set(CrGuarantorDraft::getProcBusinessKey, procBusinessKey)
                .update();

        //更新数据修改到流程
        SpringContextHolder.getBean(CrModifyDataSnapService.class)
                .lambdaUpdate()
                .eq(CrModifyDataSnap::getTableType, TableTypeEnum.GUARANTOR.name())
                .isNull(CrModifyDataSnap::getProcBusinessKey)
                .set(CrModifyDataSnap::getBatchNo, batchNo)
                .set(CrModifyDataSnap::getApprovalStatus, ApprovalStatus.UNDER_APPROVAL.name())
                .set(CrModifyDataSnap::getProcBusinessKey, procBusinessKey)
                .update();
    }

    @Override
    public ReportPageEnum reportPageEnum() {
        return ReportPageEnum.GUARANTOR;
    }

    @Override
    public int countInProcessData() {
        return crGuarantorDraftService.count(Wrappers.<CrGuarantorDraft>lambdaQuery()
                .select(CrGuarantorDraft::getId)
                .eq(CrGuarantorDraft::getApprovalStatus, ApprovalStatus.UNDER_APPROVAL.name())
        );
    }

    @Override
    public IService<CrGuarantorDraft> getServiceInstance() {
        return crGuarantorDraftService;
    }

}
