package cn.zswltech.mithras.report.service.agg;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.flow.core.enums.ProcessBusinessStatusEnum;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.report.account.AccountListREQ;
import cn.zswltech.mithras.dto.report.account.AccountListRSP;
import cn.zswltech.mithras.dto.report.account.AccountModifyREQ;
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
import cn.zswltech.mithras.report.mapper.draft.model.CrAccountDraft;
import cn.zswltech.mithras.report.mapper.formal.model.CrAccount;
import cn.zswltech.mithras.report.mapper.fullsnap.CrAccountFullSnapMapper;
import cn.zswltech.mithras.report.mapper.model.BatchRecord;
import cn.zswltech.mithras.report.mapper.model.CrModifyDataSnap;
import cn.zswltech.mithras.report.mapper.procsnap.model.CrAccountProcSnap;
import cn.zswltech.mithras.report.service.CommonInfoService;
import cn.zswltech.mithras.report.service.CrModifyDataSnapService;
import cn.zswltech.mithras.report.service.draft.CrAccountDraftService;
import cn.zswltech.mithras.report.service.formal.CrAccountService;
import cn.zswltech.mithras.report.service.procsnap.CrAccountProcSnapService;
import cn.zswltech.mithras.report.util.ReportBizUtil;
import cn.zswltech.mithras.report.util.ReportCompareUtil;
import cn.zswltech.mithras.foundation.constant.ResultMsg;
import cn.zswltech.mithras.foundation.constant.VersionTypeConstants;
import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.contract.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.foundation.context.SpringContextHolder;
import cn.zswltech.mithras.foundation.util.Util;
import cn.zswltech.mithras.foundation.util.LongUtil;
import cn.zswltech.mithras.foundation.util.StringUtil;
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
 * 征信报送-账户表接口
 * 理论上应该最后执行 别的处理器都依赖该处理器
 *
 * @author wangchuanhao
 * @date 2023/1/11 5:18 PM
 */
@Service
@Order(100)
public class CrAccountAggService implements ICrProcessWorker<CrAccountDraft, AccountListREQ> {
    @Resource
    private CrAccountDraftService crAccountDraftService;
    @Resource
    private CrAccountService crAccountService;
    @Resource
    private CrAccountProcSnapService crAccountProcSnapService;
    @Resource
    private ProcHelper procHelper;
    @Resource
    private CrAccountFullSnapMapper crAccountFullSnapMapper;
    @Resource
    private CommonInfoService commonInfoService;
    @Resource
    private CrModifyDataSnapMapper modifyDataSnapMapper;

    @Transactional(rollbackFor = Exception.class, transactionManager = ReportConstants.TRANSACTION_MANAGER)
    public void modify(AccountModifyREQ req) {
        CrAccountDraft existData = crAccountDraftService.getById(req.getId());
        if (Objects.isNull(existData)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        if (!ReportState.TO_BE_REPORT.name().equals(existData.getReportState())) {
            throw new MithrasException("非待报送状态的数据不允许修改");
        }
        if (ApprovalStatus.UNDER_APPROVAL.name().equals(existData.getApprovalStatus()) && !procHelper.canEditData()) {
            throw new MithrasException("该条数据处于审批中，请撤回后再修改！");
        }
        CrAccountDraft baseModel = BeanUtil.copyProperties(existData, CrAccountDraft.class, ReportConstants.IGNORE_ID, "reportFlag");
        baseModel.setLendingDate(req.getLendingDate());
        baseModel.setBizType(req.getBizType());
        baseModel.setRentalCalcType(req.getRentalCalcType());
        baseModel.setRepayRate(req.getRepayRate());
        baseModel.setEarnestMoney(req.getEarnestMoney());
        baseModel.setProjLeaseMonthCount(req.getProjLeaseMonthCount());
        baseModel.setPaymentAmount(req.getPaymentAmount());
        baseModel.setClosedDate(req.getClosedDate());
        baseModel.setProcBusinessKey(existData.getProcBusinessKey());

        CrModifyDataSnap modifyDataSnap = modifyDataSnapMapper.selectOne(Wrappers.<CrModifyDataSnap>lambdaQuery()
                .eq(CrModifyDataSnap::getBusinessKey, req.getBusinessKey())
                .eq(CrModifyDataSnap::getTableType, TableTypeEnum.ACCOUNT.name())
                .eq(CrModifyDataSnap::getIsTakeEffect, YesOrNoNumberEnum.YES.getCode())
                .eq(CrModifyDataSnap::getVersionType, VersionTypeConstants.NORMAL)
                .orderByDesc(CrModifyDataSnap::getVersion)
                .last(StringUtil.mysqlLimitOne()));

        String version;
        if (ObjectUtil.isNull(modifyDataSnap)) {
            version = ReportBizUtil.getVersion("");
        } else {
            modifyDataSnapMapper.updateById(CrModifyDataSnap.builder()
                    .id(modifyDataSnap.getId())
                    .isTakeEffect(YesOrNoNumberEnum.NO.getCode())
                    .build());
            version = ReportBizUtil.getVersion(modifyDataSnap.getVersion());
        }

        modifyDataSnapMapper.insert(CrModifyDataSnap.builder()
                .label(DataShowTypeEnum.MODIFY.name())
                .businessKey(existData.getBusinessKey())
                .dataMap(JSONUtil.toJsonStr(baseModel))
                .oldDataMap(JSONUtil.toJsonStr(existData))
                .approvalStatus(ApprovalStatus.UN_SUBMIT.name())
                .isTakeEffect(YesOrNoNumberEnum.YES.getCode())
                .tableType(TableTypeEnum.ACCOUNT.name())
                //版本号格式 00xxyyyyMMdd
                .version(version)
                .versionType(VersionTypeConstants.NORMAL)
                .reason(req.getReason())
                .build());
    }

//    @Transactional(rollbackFor = Exception.class, transactionManager = ReportConstants.TRANSACTION_MANAGER)
//    public void reportChange(ReportChangeREQ req) {
//        CrAccountDraft existData = crAccountDraftService.getById(req.getId());
//        if (Objects.isNull(existData)) {
//            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
//        }
//        if (!ReportState.TO_BE_REPORT.name().equals(existData.getReportState())) {
//            throw new MithrasException("非待报送状态的数据不允许修改");
//        }
//        if (ApprovalStatus.UNDER_APPROVAL.name().equals(existData.getApprovalStatus()) && !procHelper.canEditData()) {
//            throw new MithrasException("该条数据处于审批中，请撤回后再修改！");
//        }
//        crAccountDraftService.lambdaUpdate()
//                .eq(CrAccountDraft::getId, req.getId())
//                .set(CrAccountDraft::getReportFlag, req.getReportFlag())
//                .update();
//    }


    @Override
    public PageR<Map<String, DiffValue>> list(AccountListREQ req) {
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
            case "bizType":
               return Optional.ofNullable(AccountBizTypeEnum.getByValue(String.valueOf(value.getValue()))).map(AccountBizTypeEnum::getDisplay).orElse("");
            case "rentalCalcType":
                RepayCalcTypeEnum rentalCalcTypeEnum = RepayCalcTypeEnum.findByValue(String.valueOf(value.getValue()));
                if (Objects.isNull(rentalCalcTypeEnum)) {
                    return "";
                }
                return rentalCalcTypeEnum.display();
            case "repayRate":
                AccountRepayRateEnum repayRateEnum = AccountRepayRateEnum.findByValue(String.valueOf(value.getValue()));
                if (Objects.isNull(repayRateEnum)) {
                    return "";
                }
                return repayRateEnum.display();
            case "earnestMoney":
            case "paymentAmount":
                return new BigDecimal(LongUtil.null2zero(Long.parseLong(String.valueOf(value.getValue()))))
                        .divide(new BigDecimal(10000),2, RoundingMode.HALF_UP).toPlainString()
                        .replaceAll("\\.00", "");
        }
        return Objects.nonNull(value.getValue()) ? String.valueOf(value.getValue()) : "";
    }

    public PageR<Map<String, DiffValue>> listByEdit(AccountListREQ req) {
        Page<CrAccountDraft> dataPage = crAccountDraftService.getBaseMapper().selectPage(new Page<>(req.getPage(), req.getPageSize()), Wrappers.<CrAccountDraft>lambdaQuery()
                .eq(Objects.equals(YesOrNoNumberEnum.YES.getCode(), req.getOnlyToBeReportFlag()), CrAccountDraft::getReportState, ReportState.TO_BE_REPORT.name())
                .eq(Objects.nonNull(req.getReportFlag()), CrAccountDraft::getReportFlag, req.getReportFlag())
                .eq(StringUtils.isNotBlank(req.getApprovalStatus()), CrAccountDraft::getApprovalStatus, req.getApprovalStatus())
                .like(StringUtils.isNotBlank(req.getClientName()), CrAccountDraft::getClientName, req.getClientName())
                .like(StringUtils.isNotBlank(req.getPaymentApplyCode()), CrAccountDraft::getPaymentApplyCode, req.getPaymentApplyCode())
        );

        List<CrAccountDraft> records = dataPage.getRecords();
        if (CollUtil.isEmpty(records)) {
            return PageR.of(Collections.emptyList(), dataPage.getTotal(), dataPage.getPages(), dataPage.getCurrent(), dataPage.getSize());
        }
        Set<Long> contractIds = records.stream().map(CrAccountDraft::getContractId).collect(Collectors.toSet());
        Map<Long, ContractBaseInfo> contractBaseInfoMap = commonInfoService.getLongContractBaseInfoMap(contractIds);
        List<AccountListRSP> rspList = records.stream().map(d -> {
            AccountListRSP account = BeanUtil.copyProperties(d, AccountListRSP.class);
            ContractBaseInfo info = Optional.ofNullable(contractBaseInfoMap.get(d.getContractId())).orElse(new ContractBaseInfo());
            account.setContractCode(info.getContractCode());
            return account;
        }).collect(Collectors.toList());
        //封装结果
        List<String> businessKeys = rspList.stream().map(AccountListRSP::getBusinessKey).collect(Collectors.toList());
        List<Map<String, DiffValue>> mapList = commonInfoService.buildDiffMapList(rspList, businessKeys, null, TableTypeEnum.ACCOUNT, AccountListRSP.class, null, 1);
        return PageR.of(mapList, dataPage.getTotal(),
                dataPage.getPages(),
                dataPage.getCurrent(),
                dataPage.getSize());
    }

    public PageR<Map<String, DiffValue>> listByProc(AccountListREQ req) {
        boolean processEndFlag = procHelper.judgeProcessEndWithCheck(req.getProcBusinessKey());
        if (processEndFlag) {
            Page<CrAccountProcSnap> dataPage = crAccountProcSnapService.getBaseMapper().selectPage(new Page<>(req.getPage(), req.getPageSize()), Wrappers.<CrAccountProcSnap>lambdaQuery()
                    .eq(CrAccountProcSnap::getProcBusinessKey, Long.valueOf(req.getProcBusinessKey()))
                    .eq(Objects.nonNull(req.getReportFlag()), CrAccountProcSnap::getReportFlag, req.getReportFlag())
                    .like(StringUtils.isNotBlank(req.getClientName()), CrAccountProcSnap::getClientName, req.getClientName())
                    .like(StringUtils.isNotBlank(req.getPaymentApplyCode()), CrAccountProcSnap::getPaymentApplyCode, req.getPaymentApplyCode())
            );
            return getMapPageR(dataPage, req.getProcBusinessKey(), null);
        } else {
            Page<CrAccountDraft> dataPage = crAccountDraftService.getBaseMapper().selectPage(new Page<>(req.getPage(), req.getPageSize()), Wrappers.<CrAccountDraft>lambdaQuery()
                    .eq(CrAccountDraft::getProcBusinessKey, Long.valueOf(req.getProcBusinessKey()))
                    .eq(Objects.equals(YesOrNoNumberEnum.YES.getCode(), req.getOnlyToBeReportFlag()), CrAccountDraft::getReportState, ReportState.TO_BE_REPORT.name())
                    .eq(Objects.nonNull(req.getReportFlag()), CrAccountDraft::getReportFlag, req.getReportFlag())
                    .eq(StringUtils.isNotBlank(req.getApprovalStatus()), CrAccountDraft::getApprovalStatus, req.getApprovalStatus())
                    .like(StringUtils.isNotBlank(req.getClientName()), CrAccountDraft::getClientName, req.getClientName())
                    .like(StringUtils.isNotBlank(req.getPaymentApplyCode()), CrAccountDraft::getPaymentApplyCode, req.getPaymentApplyCode())
            );
            List<CrAccountDraft> records = dataPage.getRecords();
            if (CollUtil.isEmpty(records)) {
                return PageR.of(Collections.emptyList(), dataPage.getTotal(), dataPage.getPages(), dataPage.getCurrent(), dataPage.getSize());
            }
            Set<Long> contractIds = records.stream().map(CrAccountDraft::getContractId).collect(Collectors.toSet());
            Map<Long, ContractBaseInfo> contractBaseInfoMap = commonInfoService.getLongContractBaseInfoMap(contractIds);
            List<AccountListRSP> rspList = records.stream().map(d -> {
                AccountListRSP account = BeanUtil.copyProperties(d, AccountListRSP.class);
                ContractBaseInfo info = Optional.ofNullable(contractBaseInfoMap.get(d.getContractId())).orElse(new ContractBaseInfo());
                account.setContractCode(info.getContractCode());
                return account;
            }).collect(Collectors.toList());
            List<String> businessKeys = rspList.stream().map(AccountListRSP::getBusinessKey).collect(Collectors.toList());
            List<Map<String, DiffValue>> mapList = commonInfoService.buildDiffMapList(rspList, businessKeys, req.getProcBusinessKey(), TableTypeEnum.ACCOUNT, AccountListRSP.class, null, null);
            return PageR.of(mapList, dataPage.getTotal(),
                    dataPage.getPages(),
                    dataPage.getCurrent(),
                    dataPage.getSize());
        }
    }

    @NotNull
    private PageR<Map<String, DiffValue>> getMapPageR(Page<CrAccountProcSnap> dataPage, String procBusinessKey, String batchNo) {
        List<CrAccountProcSnap> records = dataPage.getRecords();
        if (CollUtil.isEmpty(records)) {
            return PageR.of(Collections.emptyList(), dataPage.getTotal(), dataPage.getPages(), dataPage.getCurrent(), dataPage.getSize());
        }
        Set<Long> contractIds = records.stream().map(CrAccountProcSnap::getContractId).collect(Collectors.toSet());
        Map<Long, ContractBaseInfo> contractBaseInfoMap = commonInfoService.getLongContractBaseInfoMap(contractIds);
        List<AccountListRSP> rspList = records.stream().map(d -> {
            AccountListRSP account = BeanUtil.copyProperties(d, AccountListRSP.class);
            ContractBaseInfo info = Optional.ofNullable(contractBaseInfoMap.get(d.getContractId())).orElse(new ContractBaseInfo());
            account.setContractCode(info.getContractCode());
            return account;
        }).collect(Collectors.toList());
        List<String> businessKeys = rspList.stream().map(AccountListRSP::getBusinessKey).collect(Collectors.toList());
        List<Map<String, DiffValue>> mapList = commonInfoService.buildDiffMapList(rspList, businessKeys, procBusinessKey, TableTypeEnum.ACCOUNT, AccountListRSP.class, batchNo, null);
        return PageR.of(mapList, dataPage.getTotal(),
                dataPage.getPages(),
                dataPage.getCurrent(),
                dataPage.getSize());
    }

    public PageR<Map<String, DiffValue>> listByBatchIncre(AccountListREQ req) {
        Page<CrAccountProcSnap> dataPage = crAccountProcSnapService.getBaseMapper().selectPage(new Page<>(req.getPage(), req.getPageSize()), Wrappers.<CrAccountProcSnap>lambdaQuery()
                .eq(CrAccountProcSnap::getBatchId, req.getBatchId())
                .eq(CrAccountProcSnap::getReportFlag, YesOrNoNumberEnum.YES.getCode())
                .like(StringUtils.isNotBlank(req.getClientName()), CrAccountProcSnap::getClientName, req.getClientName())
                .like(StringUtils.isNotBlank(req.getPaymentApplyCode()), CrAccountProcSnap::getPaymentApplyCode, req.getPaymentApplyCode())
        );
        BatchRecord batchRecord = SpringContextHolder.getBean(BatchRecordMapper.class).selectById(req.getBatchId());
        return getMapPageR(dataPage, req.getProcBusinessKey(), batchRecord.getBatchNo());
    }

    public PageR<Map<String, DiffValue>> listByEffect(AccountListREQ req) {
        Page<CrAccount> dataPage = crAccountService.getBaseMapper().selectPage(new Page<>(req.getPage(), req.getPageSize()), Wrappers.<CrAccount>lambdaQuery()
                .eq(Objects.nonNull(req.getAccountId()), CrAccount::getId, req.getAccountId())
                .like(StringUtils.isNotBlank(req.getClientName()), CrAccount::getClientName, req.getClientName())
                .like(StringUtils.isNotBlank(req.getPaymentApplyCode()), CrAccount::getPaymentApplyCode, req.getPaymentApplyCode())
        );
        List<CrAccount> records = dataPage.getRecords();
        if (CollUtil.isEmpty(records)) {
            return PageR.of(Collections.emptyList(), dataPage.getTotal(), dataPage.getPages(), dataPage.getCurrent(), dataPage.getSize());
        }
        Set<Long> contractIds = records.stream().map(CrAccount::getContractId).collect(Collectors.toSet());
        Map<Long, ContractBaseInfo> contractBaseInfoMap = commonInfoService.getLongContractBaseInfoMap(contractIds);
        List<AccountListRSP> rspList = records.stream().map(d -> {
            AccountListRSP account = BeanUtil.copyProperties(d, AccountListRSP.class);
            ContractBaseInfo info = Optional.ofNullable(contractBaseInfoMap.get(d.getContractId())).orElse(new ContractBaseInfo());
            account.setContractCode(info.getContractCode());
            return account;
        }).collect(Collectors.toList());
        List<String> businessKeys = rspList.stream().map(AccountListRSP::getBusinessKey).collect(Collectors.toList());
        List<Map<String, DiffValue>> mapList = commonInfoService.buildDiffMapEffectList(rspList, businessKeys, TableTypeEnum.ACCOUNT, AccountListRSP.class);
        return PageR.of(mapList, dataPage.getTotal(),
                dataPage.getPages(),
                dataPage.getCurrent(),
                dataPage.getSize());
    }

    @Override
    @Transactional(rollbackFor = Exception.class, transactionManager = ReportConstants.TRANSACTION_MANAGER)
    public void processEnd(Long procBusinessKey, Integer endType, Long startUserId, String processInstanceId,
                           LocalDateTime reportTime, BatchRecord procSnapRecord, BatchRecord fullSnapRecord) {
        boolean processPass = ProcessBusinessStatusEnum.success(endType);
        List<CrAccountDraft> draftList = crAccountDraftService.list(Wrappers.<CrAccountDraft>lambdaQuery()
                .eq(CrAccountDraft::getReportState, ReportState.TO_BE_REPORT.name())
                .eq(CrAccountDraft::getProcBusinessKey, procBusinessKey));

        // 1.编辑区数据插入ProcSnap
        List<CrAccountProcSnap> procSnapList = draftList.stream().map(d -> {
            CrAccountProcSnap procSnap = BeanUtil.copyProperties(d, CrAccountProcSnap.class, ReportConstants.IGNORE_ID);
            procSnap.setBatchId(Optional.ofNullable(procSnapRecord).map(BatchRecord::getId).orElse(null));
            procSnap.setBatchNo(Optional.ofNullable(procSnapRecord).map(BatchRecord::getBatchNo).orElse(null));
            return procSnap;
        }).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(procSnapList)) {
            crAccountProcSnapService.saveBatch(procSnapList);
        }

        if (processPass) {
            // 1.如果审批成功 把修改数据更新到draft表
            // 2.如果审批成功 把编辑区选择报送的数据抄到生效区
            if (CollectionUtils.isNotEmpty(draftList)) {
                List<CrModifyDataSnap> modifyDataSnaps = modifyDataSnapMapper.selectList(Wrappers.<CrModifyDataSnap>lambdaQuery()
                        .eq(CrModifyDataSnap::getProcBusinessKey, procBusinessKey)
                        .eq(CrModifyDataSnap::getTableType, TableTypeEnum.ACCOUNT.name())
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
                    CrAccountDraft accountDraft = JSONUtil.toBean(modifyDataSnap.getDataMap(), CrAccountDraft.class);
                    Integer reportFlag = draft.getReportFlag();
                    BeanUtil.copyProperties(accountDraft, draft, ReportConstants.IGNORE_ID);
                    draft.setReportFlag(reportFlag);
                });
                List<CrAccount> draft2FormalList = draftList.stream()
                        .filter(d -> Objects.equals(YesOrNoNumberEnum.YES.getCode(), d.getReportFlag()))
                        .map(d -> BeanUtil.copyProperties(d, CrAccount.class, ReportConstants.IGNORE_ID))
                        .collect(Collectors.toList());

                List<CrAccount> needInsertList = new ArrayList<>();
                Map<String, CrAccount> existReportDataMap = crAccountService.list(Wrappers.<CrAccount>lambdaQuery()
                                .in(CrAccount::getBusinessKey, draftList.stream().map(CrAccountDraft::getBusinessKey).collect(Collectors.toSet())))
                        .stream().collect(Collectors.toMap(CrAccount::getBusinessKey, Function.identity(), (a, b) -> a));

                for (CrAccount draft2Formal : draft2FormalList) {
                    if (!existReportDataMap.containsKey(draft2Formal.getBusinessKey())) {
                        needInsertList.add(draft2Formal);
                    } else {
                        CrAccount existData = existReportDataMap.get(draft2Formal.getBusinessKey());
                        if (ReportCompareUtil.checkChange(draft2Formal, existData, existData.ignoreCompareFieldNames())) {
                            draft2Formal.setId(existData.getId());
                            crAccountService.updateById(draft2Formal);
                        }
                    }
                }
                crAccountService.saveBatch(needInsertList);
            }
            // 3.如果审批成功 把生效区数据全量抄到FullSnap
            crAccountFullSnapMapper.copyFromEffect(Optional.ofNullable(fullSnapRecord).map(BatchRecord::getId).orElse(null),
                    Optional.ofNullable(fullSnapRecord).map(BatchRecord::getBatchNo).orElse(null));
        }
        // 4.编辑区数据处理
        List<Long> existReportDraftIdList = draftList.stream()
                .filter(d -> Objects.equals(YesOrNoNumberEnum.YES.getCode(), d.getReportFlag()))
                .map(CrAccountDraft::getId).collect(Collectors.toList());

        List<Long> notReportDraftIdList = draftList.stream()
                .filter(d -> !Objects.equals(YesOrNoNumberEnum.YES.getCode(), d.getReportFlag()))
                .map(CrAccountDraft::getId).collect(Collectors.toList());

        if (CollectionUtils.isNotEmpty(existReportDraftIdList)) {
            crAccountDraftService.lambdaUpdate()
                    // 已报送数据处理
                    .set(CrAccountDraft::getReportFlag, YesOrNoNumberEnum.YES.getCode())
                    // 审批通过才处理成已报送
                    .set(processPass, CrAccountDraft::getReportState, ReportState.REPORTED.name())
                    .set(CrAccountDraft::getApprovalStatus, ApprovalStatus.UN_SUBMIT.name())
                    .set(CrAccountDraft::getProcBusinessKey, null)
                    .in(CrAccountDraft::getId, existReportDraftIdList)
                    .update();
        }
        if (CollectionUtils.isNotEmpty(notReportDraftIdList)) {
            // 不报送数据处理
            crAccountDraftService.lambdaUpdate()
                    .set(CrAccountDraft::getReportFlag, YesOrNoNumberEnum.NO.getCode())
                    .set(CrAccountDraft::getReportState, ReportState.TO_BE_REPORT.name())
                    .set(CrAccountDraft::getApprovalStatus, ApprovalStatus.UN_SUBMIT.name())
                    .set(CrAccountDraft::getProcBusinessKey, null)
                    .in(CrAccountDraft::getId, notReportDraftIdList)
                    .update();
        }
    }

    @Override
    public void submit(Long procBusinessKey, String batchNo) {
        crAccountDraftService.lambdaUpdate()
                .eq(CrAccountDraft::getReportState, ReportState.TO_BE_REPORT.name())
                .set(CrAccountDraft::getApprovalStatus, ApprovalStatus.UNDER_APPROVAL.name())
                .set(CrAccountDraft::getProcBusinessKey, procBusinessKey)
                .update();

        //更新数据修改到流程
        SpringContextHolder.getBean(CrModifyDataSnapService.class)
                .lambdaUpdate()
                .eq(CrModifyDataSnap::getTableType, TableTypeEnum.ACCOUNT.name())
                .isNull(CrModifyDataSnap::getProcBusinessKey)
                .set(CrModifyDataSnap::getApprovalStatus, ApprovalStatus.UNDER_APPROVAL.name())
                .set(CrModifyDataSnap::getProcBusinessKey, procBusinessKey)
                .set(CrModifyDataSnap::getBatchNo, batchNo)
                .update();
    }

    @Override
    public ReportPageEnum reportPageEnum() {
        return ReportPageEnum.ACCOUNT;
    }

    @Override
    public int countInProcessData() {
        return crAccountDraftService.count(Wrappers.<CrAccountDraft>lambdaQuery()
                .select(CrAccountDraft::getId)
                .eq(CrAccountDraft::getApprovalStatus, ApprovalStatus.UNDER_APPROVAL.name())
        );
    }

    @Override
    public IService<CrAccountDraft> getServiceInstance() {
        return crAccountDraftService;
    }

}
