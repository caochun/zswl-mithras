package cn.zswltech.mithras.report.service.agg;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.flow.core.enums.ProcessBusinessStatusEnum;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.report.repay.RepayListREQ;
import cn.zswltech.mithras.dto.report.repay.RepayListRSP;
import cn.zswltech.mithras.dto.report.repay.RepayModifyREQ;
import cn.zswltech.mithras.dto.version.DiffValue;
import cn.zswltech.mithras.report.config.ReportConstants;
import cn.zswltech.mithras.report.enums.biz.DataShowTypeEnum;
import cn.zswltech.mithras.report.enums.biz.TableTypeEnum;
import cn.zswltech.mithras.report.enums.common.ApprovalStatus;
import cn.zswltech.mithras.report.enums.common.QueryChannel;
import cn.zswltech.mithras.report.enums.common.ReportPageEnum;
import cn.zswltech.mithras.report.enums.common.ReportState;
import cn.zswltech.mithras.report.flow.ICrProcessWorker;
import cn.zswltech.mithras.report.flow.ProcHelper;
import cn.zswltech.mithras.report.handler.ReportDataRepository;
import cn.zswltech.mithras.report.mapper.BatchRecordMapper;
import cn.zswltech.mithras.report.mapper.CrModifyDataSnapMapper;
import cn.zswltech.mithras.report.mapper.CrRepayMapper;
import cn.zswltech.mithras.report.mapper.base.model.CrAccountBase;
import cn.zswltech.mithras.report.mapper.base.model.CrBaseModel;
import cn.zswltech.mithras.report.mapper.base.model.CrRepayPlanBase;
import cn.zswltech.mithras.report.mapper.draft.model.CrAccountDraft;
import cn.zswltech.mithras.report.mapper.draft.model.CrActualRepayDraft;
import cn.zswltech.mithras.report.mapper.draft.model.CrRepayPlanDraft;
import cn.zswltech.mithras.report.mapper.dto.CrRepayDTO;
import cn.zswltech.mithras.report.mapper.formal.model.CrAccount;
import cn.zswltech.mithras.report.mapper.formal.model.CrActualRepay;
import cn.zswltech.mithras.report.mapper.formal.model.CrRepayPlan;
import cn.zswltech.mithras.report.mapper.fullsnap.CrActualRepayFullSnapMapper;
import cn.zswltech.mithras.report.mapper.fullsnap.CrRepayPlanFullSnapMapper;
import cn.zswltech.mithras.report.mapper.model.BatchRecord;
import cn.zswltech.mithras.report.mapper.model.CrModifyDataSnap;
import cn.zswltech.mithras.report.mapper.procsnap.model.CrActualRepayProcSnap;
import cn.zswltech.mithras.report.mapper.procsnap.model.CrRepayPlanProcSnap;
import cn.zswltech.mithras.report.mapper.query.CrRepayQuery;
import cn.zswltech.mithras.report.service.CommonInfoService;
import cn.zswltech.mithras.report.service.CrModifyDataSnapService;
import cn.zswltech.mithras.report.service.draft.CrAccountDraftService;
import cn.zswltech.mithras.report.service.draft.CrActualRepayDraftService;
import cn.zswltech.mithras.report.service.draft.CrRepayPlanDraftService;
import cn.zswltech.mithras.report.service.formal.CrAccountService;
import cn.zswltech.mithras.report.service.formal.CrActualRepayService;
import cn.zswltech.mithras.report.service.formal.CrRepayPlanService;
import cn.zswltech.mithras.report.service.fullsnap.CrActualRepayFullSnapService;
import cn.zswltech.mithras.report.service.fullsnap.CrRepayPlanFullSnapService;
import cn.zswltech.mithras.report.service.procsnap.CrActualRepayProcSnapService;
import cn.zswltech.mithras.report.service.procsnap.CrRepayPlanProcSnapService;
import cn.zswltech.mithras.report.util.ReportBizUtil;
import cn.zswltech.mithras.report.util.ReportCompareUtil;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.service.constant.VersionTypeConstants;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.others.SpringContextHolder;
import cn.zswltech.mithras.service.others.Util;
import cn.zswltech.mithras.service.util.LongUtil;
import cn.zswltech.mithras.service.util.StringUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.commons.collections4.CollectionUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yaml.snakeyaml.events.Event;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 征信报送-还款表接口
 *
 * @author wangchuanhao
 * @date 2023/1/11 5:18 PM
 */
@Service
@Order(-1)
public class CrRepayAggService implements ICrProcessWorker<CrRepayPlanDraft, RepayListREQ> {

    @Resource
    private CrRepayMapper crRepayMapper;
    @Resource
    private CrActualRepayDraftService crActualRepayDraftService;
    @Resource
    private CrRepayPlanDraftService crRepayPlanDraftService;
    @Resource
    private CrAccountService crAccountService;
    @Resource
    private ProcHelper procHelper;
    @Resource
    private CrActualRepayProcSnapService crActualRepayProcSnapService;
    @Resource
    private CrRepayPlanProcSnapService crRepayPlanProcSnapService;
    @Resource
    private CrActualRepayService crActualRepayService;
    @Resource
    private CrRepayPlanService crRepayPlanService;
    @Resource
    private CrAccountDraftService crAccountDraftService;
    @Resource
    private CrActualRepayFullSnapMapper crActualRepayFullSnapMapper;
    @Resource
    private CrRepayPlanFullSnapMapper crRepayPlanFullSnapMapper;
    @Resource
    private CommonInfoService commonInfoService;
    @Resource
    private CrModifyDataSnapMapper modifyDataSnapMapper;
    @Resource
    protected ReportDataRepository reportDataRepository;

    @Transactional(rollbackFor = Exception.class, transactionManager = ReportConstants.TRANSACTION_MANAGER)
    public void modify(RepayModifyREQ req) {
        String[] idArray = req.getIdKey().split("_");
        if (!NumberUtil.isNumber(idArray[0])) {
            throw new MithrasException("idKey不合法");
        }
        Long repayPlanId = Long.valueOf(idArray[0]);
        Long actualRepayId = idArray.length > 1 ? Long.valueOf(idArray[1]) : null;
        CrRepayPlanDraft existRepayPlan = crRepayPlanDraftService.getById(repayPlanId);
        if (Objects.isNull(existRepayPlan)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
//        if (existRepayPlan.getCashFlowDate().isBefore(LocalDate.now())) {
//            throw new MithrasException("该还款日期在当前时间之前，不允许修改");
//        }
        if (reportDataRepository.getPlanIsOverdueRecord(existRepayPlan)) {
            throw new MithrasException("该还款数据已报送逾期，不允许修改。");
        }
        // 校验是否可修改 还款计划和实际还款有一个处于待报送就可修改
        if (!ReportState.TO_BE_REPORT.name().equals(existRepayPlan.getReportState())) {
            CrActualRepayDraft existActualRepay = Objects.nonNull(actualRepayId) ? crActualRepayDraftService.getById(actualRepayId) : null;
            if (Objects.nonNull(existActualRepay)
                    && (!Objects.equals(existRepayPlan.getPaymentId(), existActualRepay.getPaymentId()) || !Objects.equals(existRepayPlan.getPhase(), existActualRepay.getPhase()))) {
                // 校验是否属于该期计划的还款
                throw new MithrasException("idKey不合法");
            }
            if (Objects.isNull(existActualRepay) || !ReportState.TO_BE_REPORT.name().equals(existActualRepay.getReportState())) {
                throw new MithrasException("非待报送状态的数据不允许修改");
            }
        }
        if (ApprovalStatus.UNDER_APPROVAL.name().equals(existRepayPlan.getApprovalStatus()) && !procHelper.canEditData()) {
            throw new MithrasException("该条数据处于审批中，请撤回后再修改！");
        }

        CrModifyDataSnap modifyDataSnap = modifyDataSnapMapper.selectOne(Wrappers.<CrModifyDataSnap>lambdaQuery()
                .eq(CrModifyDataSnap::getBusinessKey, existRepayPlan.getBusinessKey())
                .eq(CrModifyDataSnap::getTableType, TableTypeEnum.REPAY_PLAN.name())
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
        CrRepayPlanDraft repayPlanDraft = BeanUtil.copyProperties(existRepayPlan, CrRepayPlanDraft.class, ReportConstants.IGNORE_ID);
        repayPlanDraft.setGracePeriod(String.valueOf(req.getGracePeriod()));
        modifyDataSnapMapper.insert(CrModifyDataSnap.builder()
                .label(DataShowTypeEnum.MODIFY.name())
                .businessKey(existRepayPlan.getBusinessKey())
                .dataMap(JSONUtil.toJsonStr(repayPlanDraft))
                .oldDataMap(JSONUtil.toJsonStr(existRepayPlan))
                .approvalStatus(ApprovalStatus.UN_SUBMIT.name())
                .isTakeEffect(YesOrNoNumberEnum.YES.getCode())
                .tableType(TableTypeEnum.REPAY_PLAN.name())
                //版本号格式 00xxyyyyMMdd
                .version(version)
                .versionType(VersionTypeConstants.NORMAL)
                .reason(req.getReason())
                .build());
    }

    @Override
    public PageR<Map<String, DiffValue>> list(RepayListREQ req) {
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
    public String converter(String fieldName, DiffValue value) {
        switch (fieldName) {
            case "principal":
            case "rent":
            case "collectionAmount":
            case "collectionPrincipal":
                if (Objects.isNull(value.getValue())) {
                    return "-";
                }
                return new BigDecimal(LongUtil.null2zero(Long.parseLong(String.valueOf(value.getValue()))))
                        .divide(new BigDecimal(10000), 2, RoundingMode.HALF_UP).toPlainString()
                        .replaceAll("\\.00", "");
            default:
                return Objects.nonNull(value.getValue()) ? String.valueOf(value.getValue()) : "";
        }
    }

    public PageR<Map<String, DiffValue>> listByEdit(RepayListREQ req) {
        CrRepayQuery query = buildQuery(req);
        query.setFilterAccountReportFlag(YesOrNoNumberEnum.YES.getCode());
        query.setReportState(ReportState.TO_BE_REPORT.name());
        Page<CrRepayDTO> dataPage = crRepayMapper.draftList(new Page<>(req.getPage(), req.getPageSize()), query);
        List<CrRepayDTO> records = dataPage.getRecords();
        if (CollUtil.isEmpty(records)) {
            return PageR.of(Collections.emptyList(), dataPage.getTotal(), dataPage.getPages(), dataPage.getCurrent(), dataPage.getSize());
        }
        List<RepayListRSP> rspList = getContractMap(records);
        List<Map<String, DiffValue>> mapList = getMaps(records, rspList, null, null, 1);
        return PageR.of(mapList, dataPage.getTotal(),
                dataPage.getPages(),
                dataPage.getCurrent(),
                dataPage.getSize());
    }

    @NotNull
    private List<RepayListRSP> getContractMap(List<CrRepayDTO> records) {
        Set<Long> contractIds = records.stream().map(CrRepayDTO::getContractId).collect(Collectors.toSet());
        Map<Long, ContractBaseInfo> contractBaseInfoMap = commonInfoService.getLongContractBaseInfoMap(contractIds);
        return records.stream()
                .map(d -> {
                    RepayListRSP repay = buildRSP(d);
                    ContractBaseInfo info = Optional.ofNullable(contractBaseInfoMap.get(d.getContractId())).orElse(new ContractBaseInfo());
                    repay.setContractCode(info.getContractCode());
                    repay.setBusinessKey(d.getBusinessKey());
                    repay.setGracePeriod(Integer.valueOf(d.getGracePeriod()));
                    return repay;
                }).collect(Collectors.toList());
    }

    public PageR<Map<String, DiffValue>> listByProc(RepayListREQ req) {
        boolean processEndFlag = procHelper.judgeProcessEndWithCheck(req.getProcBusinessKey());
        if (processEndFlag) {
            CrRepayQuery query = buildQuery(req);
            Page<CrRepayDTO> dataPage = crRepayMapper.procSnapList(new Page<>(req.getPage(), req.getPageSize()), query);
            List<CrRepayDTO> records = dataPage.getRecords();
            if (CollUtil.isEmpty(records)) {
                return PageR.of(Collections.emptyList(), dataPage.getTotal(), dataPage.getPages(), dataPage.getCurrent(), dataPage.getSize());
            }
            List<RepayListRSP> rspList = getContractMap(records);
            List<Map<String, DiffValue>> mapList = getMaps(records, rspList, req.getProcBusinessKey(), null, null);
            return PageR.of(mapList, dataPage.getTotal(),
                    dataPage.getPages(),
                    dataPage.getCurrent(),
                    dataPage.getSize());
        } else {
            CrRepayQuery query = buildQuery(req);
            query.setFilterAccountReportFlag(1);
            query.setReportState(ReportState.TO_BE_REPORT.name());
            Page<CrRepayDTO> dataPage = crRepayMapper.draftList(new Page<>(req.getPage(), req.getPageSize()), query);
            List<CrRepayDTO> records = dataPage.getRecords();
            if (CollUtil.isEmpty(records)) {
                return PageR.of(Collections.emptyList(), dataPage.getTotal(), dataPage.getPages(), dataPage.getCurrent(), dataPage.getSize());
            }
            List<RepayListRSP> rspList = getContractMap(records);
            List<Map<String, DiffValue>> mapList = getMaps(records, rspList, req.getProcBusinessKey(), null, null);
            return PageR.of(mapList, dataPage.getTotal(),
                    dataPage.getPages(),
                    dataPage.getCurrent(),
                    dataPage.getSize());
        }
    }

    public PageR<Map<String, DiffValue>> listByBatchIncre(RepayListREQ req) {
        CrRepayQuery query = buildQuery(req);
        Page<CrRepayDTO> dataPage = crRepayMapper.procSnapList(new Page<>(req.getPage(), req.getPageSize()), query);
        List<CrRepayDTO> records = dataPage.getRecords();
        if (CollUtil.isEmpty(records)) {
            return PageR.of(Collections.emptyList(), dataPage.getTotal(), dataPage.getPages(), dataPage.getCurrent(), dataPage.getSize());
        }
        List<RepayListRSP> rspList = getContractMap(records);
        BatchRecord batchRecord = SpringContextHolder.getBean(BatchRecordMapper.class).selectById(req.getBatchId());
        List<Map<String, DiffValue>> mapList = getMaps(records, rspList, null, batchRecord.getBatchNo(), null);
        return PageR.of(mapList, dataPage.getTotal(),
                dataPage.getPages(),
                dataPage.getCurrent(),
                dataPage.getSize());
    }

    public PageR<Map<String, DiffValue>> listByEffect(RepayListREQ req) {
        String paymentApplyCode = null;
        if (Objects.nonNull(req.getAccountId())) {
            CrAccount crAccount = crAccountService.getById(req.getAccountId());
            if (Objects.isNull(crAccount)) {
                throw new MithrasException("账户表数据不存在");
            }
            paymentApplyCode = crAccount.getPaymentApplyCode();
        }
        CrRepayQuery query = buildQuery(req);
        query.setPaymentApplyCode(paymentApplyCode);
        Page<CrRepayDTO> dataPage = crRepayMapper.effectList(new Page<>(req.getPage(), req.getPageSize()), query);
        List<CrRepayDTO> records = dataPage.getRecords();
        if (CollUtil.isEmpty(records)) {
            return PageR.of(Collections.emptyList(), dataPage.getTotal(), dataPage.getPages(), dataPage.getCurrent(), dataPage.getSize());
        }
        List<RepayListRSP> rspList = getContractMap(records);
        List<Map<String, DiffValue>> mapList = getMaps(records, rspList, null, null, 0);
        return PageR.of(mapList, dataPage.getTotal(),
                dataPage.getPages(),
                dataPage.getCurrent(),
                dataPage.getSize());
    }

    @NotNull
    private List<Map<String, DiffValue>> getMaps(List<CrRepayDTO> records, List<RepayListRSP> rspList, String procBusinessKey, String batchNo, Integer isShow) {
        List<CrModifyDataSnap> modifyDataSnaps = modifyDataSnapMapper.selectList(Wrappers.<CrModifyDataSnap>lambdaQuery()
                .eq(CrModifyDataSnap::getVersionType, VersionTypeConstants.NORMAL)
                .eq(CrModifyDataSnap::getIsTakeEffect, YesOrNoNumberEnum.YES.getCode())
                .eq(Objects.nonNull(isShow), CrModifyDataSnap::getIsShow, isShow)
                .eq(CharSequenceUtil.isNotBlank(procBusinessKey), CrModifyDataSnap::getProcBusinessKey, procBusinessKey)
                .eq(CharSequenceUtil.isNotBlank(batchNo), CrModifyDataSnap::getBatchNo, batchNo)
                .in(CollUtil.isNotEmpty(records), CrModifyDataSnap::getBusinessKey, records.stream().map(CrRepayDTO::getBusinessKey).collect(Collectors.toList()))
                .eq(CrModifyDataSnap::getTableType, TableTypeEnum.REPAY_PLAN.name()));
        Map<String, CrModifyDataSnap> dataSnapMap = new HashMap<>();
        if (CollUtil.isNotEmpty(modifyDataSnaps)) {
            dataSnapMap = modifyDataSnaps.stream().collect(Collectors.toMap(CrModifyDataSnap::getBusinessKey, Function.identity(), (a, b) -> a));
        }
        List<Map<String, DiffValue>> mapList = new LinkedList<>();
        Map<String, CrModifyDataSnap> finalDataSnapMap = dataSnapMap;
        rspList.forEach(dto -> {
            Map<String, DiffValue> map = new HashMap<>(8);
            Field[] fields = ReflectUtil.getFields(dto.getClass());
            CrModifyDataSnap dataSnap = finalDataSnapMap.get(ReflectUtil.getFieldValue(dto, "businessKey"));
            CrRepayPlanDraft repayPlanDraft = BeanUtil.copyProperties(dto, CrRepayPlanDraft.class, ReportConstants.IGNORE_ID);
            for (Field field : fields) {
                DiffValue diffValue = new DiffValue();
                Object fieldValue = ReflectUtil.getFieldValue(dto, field.getName());
                if (ObjectUtil.isNull(dataSnap)) {
                    //没改过
                    if (!"label".equals(field.getName()) && !"reason".equals(field.getName())) {
                        diffValue.setIsChange(Boolean.FALSE);
                        diffValue.setChangeType(DataShowTypeEnum.NORMAL.name());
                        diffValue.setBeforeValue(fieldValue);
                        diffValue.setValue(fieldValue);
                    }
                    if ("label".equals(field.getName())) {
                        diffValue.setChangeType(DataShowTypeEnum.NORMAL.name());
                        diffValue.setIsChange(Boolean.FALSE);
                    }
                    if ("reason".equals(field.getName())) {
                        diffValue.setChangeType(DataShowTypeEnum.NORMAL.name());
                        diffValue.setIsChange(Boolean.FALSE);
                    }
                } else {
                    Object object = JSONUtil.toBean(dataSnap.getDataMap(), CrRepayPlanDraft.class);
                    Object value = ReflectUtil.getFieldValue(object, field.getName());
                    if (ObjectUtil.isNull(value)) {
                        //没改过
                        diffValue.setIsChange(Boolean.FALSE);
                        diffValue.setChangeType(DataShowTypeEnum.NORMAL.name());
                        diffValue.setBeforeValue(fieldValue);
                        diffValue.setValue(fieldValue);
                    } else {
                        //改过
                        if (!DataShowTypeEnum.ADD.name().equals(dataSnap.getLabel()) && !DataShowTypeEnum.REMOVE.name().equals(dataSnap.getLabel()) && !value.equals(fieldValue)) {
                            diffValue.setIsChange(Boolean.TRUE);
                            diffValue.setChangeType(dataSnap.getLabel());
                            diffValue.setBeforeValue(fieldValue);
                            diffValue.setValue(value);
                        }
                        if (!DataShowTypeEnum.ADD.name().equals(dataSnap.getLabel()) && !DataShowTypeEnum.REMOVE.name().equals(dataSnap.getLabel()) && value.equals(fieldValue)) {
                            diffValue.setIsChange(Boolean.FALSE);
                            diffValue.setChangeType(dataSnap.getLabel());
                            diffValue.setBeforeValue(fieldValue);
                            diffValue.setValue(value);
                        }
                        if (DataShowTypeEnum.REMOVE.name().equals(dataSnap.getLabel())) {
                            diffValue.setIsChange(Boolean.TRUE);
                            diffValue.setChangeType(DataShowTypeEnum.REMOVE.name());
                            diffValue.setBeforeValue(value);
                        }
                    }
                    if ("label".equals(field.getName()) && repayPlanDraft.equals(object) && !DataShowTypeEnum.ADD.name().equals(dataSnap.getLabel())) {
                        diffValue.setChangeType(null);
                        diffValue.setValue(null);
                    }
                    if ("label".equals(field.getName()) && !repayPlanDraft.equals(object)) {
                        diffValue.setChangeType(dataSnap.getLabel());
                        diffValue.setIsChange(Boolean.TRUE);
                        diffValue.setValue(dataSnap.getLabel());
                    }
                    if ("label".equals(field.getName()) && DataShowTypeEnum.ADD.name().equals(dataSnap.getLabel())) {
                        diffValue.setChangeType(dataSnap.getLabel());
                        diffValue.setIsChange(Boolean.TRUE);
                        diffValue.setValue(dataSnap.getLabel());
                    }
                    if ("reason".equals(field.getName())) {
                        diffValue.setIsChange(Boolean.FALSE);
                        diffValue.setValue(dataSnap.getReason());
                        diffValue.setChangeType(dataSnap.getLabel());
                    }
                }
                map.put(field.getName(), diffValue);
            }
            //获取是否在逾期表中存在，是否逾期字段写入列表
            boolean isOverdue = reportDataRepository.getPlanIsOverdue(repayPlanDraft);
            DiffValue diffValue = new DiffValue();
            diffValue.setValue(isOverdue);
            map.put("isOverdue",diffValue);
            mapList.add(map);
        });
        return mapList;
    }

    private CrRepayQuery buildQuery(RepayListREQ req) {
        CrRepayQuery crRepayQuery = new CrRepayQuery();
        crRepayQuery.setPaymentApplyCode(req.getPaymentApplyCode());
        crRepayQuery.setApprovalStatus(req.getApprovalStatus());
        crRepayQuery.setBatchId(req.getBatchId());
        crRepayQuery.setProcBusinessKey(req.getProcBusinessKey());
        return crRepayQuery;
    }

    private RepayListRSP buildRSP(CrRepayDTO dto) {
        RepayListRSP repayListRSP = new RepayListRSP();
        repayListRSP.setIdKey(String.join("_", String.valueOf(dto.getRepayPlanId()), Objects.nonNull(dto.getActualRepayId()) ? String.valueOf(dto.getActualRepayId()) : ""));
        repayListRSP.setPaymentApplyCode(dto.getPaymentApplyCode());
        repayListRSP.setPhase(dto.getPhase());
        repayListRSP.setClientName(dto.getClientName());
        repayListRSP.setCashFlowDate(dto.getCashFlowDate());
        repayListRSP.setGracePeriod(Optional.ofNullable(dto.getGracePeriod()).map(Integer::valueOf).orElse(null));
        repayListRSP.setRent(dto.getRent());
        repayListRSP.setPrincipal(dto.getPrincipal());
        repayListRSP.setPayDate(dto.getPayDate());
        repayListRSP.setCollectionAmount(dto.getCollectionAmount());
        repayListRSP.setCollectionPrincipal(dto.getCollectionPrincipal());
        repayListRSP.setApprovalStatus(dto.getApprovalStatus());
        repayListRSP.setContractId(dto.getContractId());
        repayListRSP.setPaymentId(dto.getPaymentId());
        return repayListRSP;

    }

    /**
     * 请注意该类需要在accountAggService之前执行，依赖AccountDraft数据的状态
     */
    @Override
    public void processEnd(Long procBusinessKey, Integer endType, Long startUserId, String processInstanceId,
                           LocalDateTime reportTime, BatchRecord procSnapRecord, BatchRecord fullSnapRecord) {
        boolean processPass = ProcessBusinessStatusEnum.success(endType);
        List<CrRepayPlanDraft> planDraftList = crRepayPlanDraftService.getBaseMapper().selectList(Wrappers.<CrRepayPlanDraft>lambdaQuery()
                .eq(CrRepayPlanDraft::getReportState, ReportState.TO_BE_REPORT.name())
                .eq(CrRepayPlanDraft::getProcBusinessKey, procBusinessKey)
        );
        List<CrActualRepayDraft> actualDraftList = crActualRepayDraftService.getBaseMapper().selectList(Wrappers.<CrActualRepayDraft>lambdaQuery()
                .eq(CrActualRepayDraft::getReportState, ReportState.TO_BE_REPORT.name())
                .eq(CrActualRepayDraft::getProcBusinessKey, procBusinessKey)
        );
        // 找到编辑区账户表数据
        Map<String, CrAccountDraft> crAccountDraftMap = CollectionUtils.isEmpty(planDraftList) ? new HashMap<>()
                : crAccountDraftService.list(Wrappers.<CrAccountDraft>lambdaQuery()
                .in(CrAccountDraft::getPaymentId, planDraftList.stream().map(CrRepayPlanDraft::getPaymentId).collect(Collectors.toSet()))
        ).stream().collect(Collectors.toMap(CrAccountDraft::getPaymentApplyCode, Function.identity(), (a, b) -> a));

        // 1.编辑区数据插入ProcSnap 对应的账户表报送，此子表才显示 才抄
        // 还款计划处理
        List<CrRepayPlanProcSnap> planProcSnapList = planDraftList.stream()
                .filter(d -> Objects.equals(YesOrNoNumberEnum.YES.getCode(),
                        Optional.ofNullable(crAccountDraftMap.get(d.getPaymentApplyCode()))
                                .map(CrAccountDraft::getReportFlag).orElse(0)))
                .map(d -> {
                    CrRepayPlanProcSnap procSnap = BeanUtil.copyProperties(d, CrRepayPlanProcSnap.class, ReportConstants.IGNORE_ID);
                    procSnap.setBatchId(Optional.ofNullable(procSnapRecord).map(BatchRecord::getId).orElse(null));
                    procSnap.setBatchNo(Optional.ofNullable(procSnapRecord).map(BatchRecord::getBatchNo).orElse(null));
                    return procSnap;
                }).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(planProcSnapList)) {
            crRepayPlanProcSnapService.saveBatch(planProcSnapList);
        }
        // 实际还款处理
        List<CrActualRepayProcSnap> actualProcSnapList = actualDraftList.stream()
                .filter(d -> Objects.equals(YesOrNoNumberEnum.YES.getCode(),
                        Optional.ofNullable(crAccountDraftMap.get(d.getPaymentApplyCode()))
                                .map(CrAccountDraft::getReportFlag).orElse(0)))
                .map(d -> {
                    CrActualRepayProcSnap procSnap = BeanUtil.copyProperties(d, CrActualRepayProcSnap.class, ReportConstants.IGNORE_ID);
                    procSnap.setBatchId(Optional.ofNullable(procSnapRecord).map(BatchRecord::getId).orElse(null));
                    procSnap.setBatchNo(Optional.ofNullable(procSnapRecord).map(BatchRecord::getBatchNo).orElse(null));
                    return procSnap;
                }).collect(Collectors.toList());
        crActualRepayProcSnapService.saveBatch(actualProcSnapList);
        if (processPass) {
            // 1.如果审批成功 把修改数据更新到draft表
            // 2.如果审批成功 把编辑区选择报送的数据抄到生效区 对应的账户表报送，此子表才报送
            // 还款计划处理
            if (CollectionUtils.isNotEmpty(planDraftList)) {
                List<CrModifyDataSnap> modifyDataSnaps = modifyDataSnapMapper.selectList(Wrappers.<CrModifyDataSnap>lambdaQuery()
                        .eq(CrModifyDataSnap::getProcBusinessKey, procBusinessKey)
                        .eq(CrModifyDataSnap::getTableType, TableTypeEnum.REPAY_PLAN.name())
                        .eq(CrModifyDataSnap::getIsTakeEffect, YesOrNoNumberEnum.YES.getCode()));

                Map<String, CrModifyDataSnap> modifyDataSnapMap = new LinkedHashMap<>();
                if (CollUtil.isNotEmpty(modifyDataSnaps)) {
                    modifyDataSnapMap = modifyDataSnaps.stream().collect(Collectors.toMap(CrModifyDataSnap::getBusinessKey, Function.identity(), (a, b) -> a));
                }
                Map<String, CrModifyDataSnap> finalModifyDataSnapMap = modifyDataSnapMap;
                planDraftList.forEach(draft -> {
                    CrModifyDataSnap modifyDataSnap = finalModifyDataSnapMap.get(draft.getBusinessKey());
                    if (ObjectUtil.isNull(modifyDataSnap)) {
                        return;
                    }
                    CrRepayPlanDraft repayPlanDraft = JSONUtil.toBean(modifyDataSnap.getDataMap(), CrRepayPlanDraft.class);
                    BeanUtil.copyProperties(repayPlanDraft, draft, ReportConstants.IGNORE_ID);
                });

                List<CrRepayPlan> planDraft2FormalList = planDraftList.stream()
                        .filter(d -> Objects.equals(YesOrNoNumberEnum.YES.getCode(),
                                Optional.ofNullable(crAccountDraftMap.get(d.getPaymentApplyCode()))
                                        .map(CrAccountDraft::getReportFlag).orElse(0)))
                        .map(d -> BeanUtil.copyProperties(d, CrRepayPlan.class, ReportConstants.IGNORE_ID))
                        .collect(Collectors.toList());

                List<CrRepayPlan> needInsertList = new ArrayList<>();
                Map<String, CrRepayPlan> existReportDataMap = crRepayPlanService.list(Wrappers.<CrRepayPlan>lambdaQuery()
                                .in(CrRepayPlan::getBusinessKey, planDraftList.stream().map(CrRepayPlanDraft::getBusinessKey).collect(Collectors.toSet())))
                        .stream().collect(Collectors.toMap(CrRepayPlan::getBusinessKey, Function.identity(), (a, b) -> a));
                for (CrRepayPlan draft2Formal : planDraft2FormalList) {
                    if (!existReportDataMap.containsKey(draft2Formal.getBusinessKey())) {
                        needInsertList.add(draft2Formal);
                    } else {
                        CrRepayPlan existData = existReportDataMap.get(draft2Formal.getBusinessKey());
                        if (ReportCompareUtil.checkChange(draft2Formal, existData, existData.ignoreCompareFieldNames())) {
                            draft2Formal.setId(existData.getId());
                            crRepayPlanService.updateById(draft2Formal);
                        }
                    }
                }
                crRepayPlanService.saveBatch(needInsertList);
            }
            // 实际还款处理
            if (CollectionUtils.isNotEmpty(actualDraftList)) {
                List<CrActualRepay> actualDraft2FormalList = actualDraftList.stream()
                        .filter(d -> Objects.equals(YesOrNoNumberEnum.YES.getCode(), Optional.ofNullable(crAccountDraftMap.get(d.getPaymentApplyCode()))
                                .map(CrAccountDraft::getReportFlag).orElse(0)))
                        .map(d -> BeanUtil.copyProperties(d, CrActualRepay.class, ReportConstants.IGNORE_ID))
                        .collect(Collectors.toList());
                List<CrActualRepay> needInsertList = new ArrayList<>();
                Map<String, CrActualRepay> existReportDataMap = crActualRepayService.list(Wrappers.<CrActualRepay>lambdaQuery()
                                .in(CrActualRepay::getBusinessKey, actualDraftList.stream().map(CrActualRepayDraft::getBusinessKey).collect(Collectors.toSet())))
                        .stream().collect(Collectors.toMap(CrActualRepay::getBusinessKey, cr -> cr));
                for (CrActualRepay draft2Formal : actualDraft2FormalList) {
                    if (!existReportDataMap.containsKey(draft2Formal.getBusinessKey())) {
                        needInsertList.add(draft2Formal);
                    } else {
                        existReportDataMap.remove(draft2Formal.getBusinessKey());
                        CrActualRepay existData = existReportDataMap.get(draft2Formal.getBusinessKey());
                        if (ReportCompareUtil.checkChange(draft2Formal, existData, existData.ignoreCompareFieldNames())) {
                            draft2Formal.setId(existData.getId());
                            crActualRepayService.updateById(draft2Formal);
                        }
                    }
                }
                // 需要处理删除的逻辑
                if (CollUtil.isNotEmpty(existReportDataMap)) {
                    crActualRepayService.remove(Wrappers.<CrActualRepay>lambdaQuery()
                            .eq(CrActualRepay::getBusinessKey, existReportDataMap.keySet()));
                }
                crActualRepayService.saveBatch(needInsertList);
            }
            // 3.如果审批成功 把生效区数据全量抄到FullSnap
            // 还款计划处理
            crRepayPlanFullSnapMapper.copyFromEffect(Optional.ofNullable(fullSnapRecord).map(BatchRecord::getId).orElse(null),
                    Optional.ofNullable(fullSnapRecord).map(BatchRecord::getBatchNo).orElse(null));
            // 实际还款处理
            crActualRepayFullSnapMapper.copyFromEffect(Optional.ofNullable(fullSnapRecord).map(BatchRecord::getId).orElse(null),
                    Optional.ofNullable(fullSnapRecord).map(BatchRecord::getBatchNo).orElse(null));
        }

        //流程里面的数据需要进行展示的变更
        crRepayPlanDraftService.lambdaUpdate()
                .eq(CrRepayPlanDraft::getIsShow, YesOrNoNumberEnum.YES.getCode())
                .set(CrRepayPlanDraft::getIsShow, processPass ? YesOrNoNumberEnum.NO.getCode() : YesOrNoNumberEnum.YES.getCode())
                .update();

        // 如果本次选择不报送的话，需要将患狂计划继续展示
        List<CrAccountDraft> list = crAccountDraftService.list(Wrappers.<CrAccountDraft>lambdaQuery()
                .eq(CrAccountDraft::getReportFlag, YesOrNoNumberEnum.NO.getCode()));
        if (CollUtil.isNotEmpty(list)) {
            crRepayPlanDraftService.lambdaUpdate()
                    .eq(CrRepayPlanDraft::getPaymentApplyCode, list.stream().map(CrAccountDraft::getPaymentApplyCode).collect(Collectors.toSet()))
                    .set(CrRepayPlanDraft::getIsShow, YesOrNoNumberEnum.YES.getCode())
                    .update();
        }

        // 4.编辑区数据处理
        // 还款计划处理 全部处理成待报送 是否待报送是否展示标志从 1 设置为 0
        crRepayPlanDraftService.lambdaUpdate()
                .set(processPass, CrRepayPlanDraft::getReportState, ReportState.TO_BE_REPORT.name())
                .set(CrRepayPlanDraft::getApprovalStatus, ApprovalStatus.UN_SUBMIT.name())
                .set(CrRepayPlanDraft::getProcBusinessKey, null)
                .update();

        // 实际还款处理
        List<Long> existReportDraftIdList = actualDraftList.stream()
                .filter(d -> Objects.equals(YesOrNoNumberEnum.YES.getCode(),
                        Optional.ofNullable(crAccountDraftMap.get(d.getPaymentApplyCode()))
                                .map(CrAccountDraft::getReportFlag).orElse(0)))
                .map(CrActualRepayDraft::getId).collect(Collectors.toList());

        List<Long> notReportDraftIdList = actualDraftList.stream().filter(d -> !Objects.equals(YesOrNoNumberEnum.YES.getCode(),
                        Optional.ofNullable(crAccountDraftMap.get(d.getPaymentApplyCode()))
                                .map(CrAccountDraft::getReportFlag).orElse(0)))
                .map(CrActualRepayDraft::getId).collect(Collectors.toList());

        if (CollectionUtils.isNotEmpty(existReportDraftIdList)) {
            // 审批通过才处理成已报送
            crActualRepayDraftService.lambdaUpdate()
                    .set(processPass, CrActualRepayDraft::getReportState, ReportState.REPORTED.name())
                    .set(CrActualRepayDraft::getApprovalStatus, ApprovalStatus.UN_SUBMIT.name())
                    .set(CrActualRepayDraft::getProcBusinessKey, null)
                    .in(CrActualRepayDraft::getId, existReportDraftIdList)
                    .update();
        }
        if (CollectionUtils.isNotEmpty(notReportDraftIdList)) {
            // 不报送数据处理
            crActualRepayDraftService.lambdaUpdate()
                    .set(CrActualRepayDraft::getReportState, ReportState.TO_BE_REPORT.name())
                    .set(CrActualRepayDraft::getApprovalStatus, ApprovalStatus.UN_SUBMIT.name())
                    .set(CrActualRepayDraft::getProcBusinessKey, null)
                    .in(CrActualRepayDraft::getId, notReportDraftIdList)
                    .update();
        }
    }

    @Override
    public void submit(Long procBusinessKey, String batchNo) {
        crActualRepayDraftService.lambdaUpdate()
                .eq(CrActualRepayDraft::getReportState, ReportState.TO_BE_REPORT.name())
                .set(CrActualRepayDraft::getApprovalStatus, ApprovalStatus.UNDER_APPROVAL.name())
                .set(CrActualRepayDraft::getProcBusinessKey, procBusinessKey)
                .update();

        // 理论上是所有还款计划都要处理成审批中
        crRepayPlanDraftService.lambdaUpdate()
                .eq(CrRepayPlanDraft::getReportState, ReportState.TO_BE_REPORT.name())
                .set(CrRepayPlanDraft::getApprovalStatus, ApprovalStatus.UNDER_APPROVAL.name())
                .set(CrRepayPlanDraft::getProcBusinessKey, procBusinessKey)
                .update();

        //更新数据修改到流程
        SpringContextHolder.getBean(CrModifyDataSnapService.class)
                .lambdaUpdate()
                .eq(CrModifyDataSnap::getTableType, TableTypeEnum.REPAY_PLAN.name())
                .isNull(CrModifyDataSnap::getProcBusinessKey)
                .set(CrModifyDataSnap::getBatchNo, batchNo)
                .set(CrModifyDataSnap::getApprovalStatus, ApprovalStatus.UNDER_APPROVAL.name())
                .set(CrModifyDataSnap::getProcBusinessKey, procBusinessKey)
                .update();
    }

    @Override
    public ReportPageEnum reportPageEnum() {
        return ReportPageEnum.REPAY;
    }

    @Override
    public int countInProcessData() {
        return crRepayMapper.countInProcessData();
    }

    @Override
    public IService<CrRepayPlanDraft> getServiceInstance() {
        return crRepayPlanDraftService;
    }

}
