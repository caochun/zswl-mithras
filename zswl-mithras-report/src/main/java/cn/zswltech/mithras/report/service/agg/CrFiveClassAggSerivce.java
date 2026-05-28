package cn.zswltech.mithras.report.service.agg;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.flow.core.enums.ProcessBusinessStatusEnum;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.report.fiveclass.*;
import cn.zswltech.mithras.dto.version.DiffValue;
import cn.zswltech.mithras.report.config.ReportConstants;
import cn.zswltech.mithras.report.enums.biz.DataShowTypeEnum;
import cn.zswltech.mithras.report.enums.biz.FiveClassEnum;
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
import cn.zswltech.mithras.report.mapper.draft.model.CrAccountDraft;
import cn.zswltech.mithras.report.mapper.draft.model.CrFiveClassDraft;
import cn.zswltech.mithras.report.mapper.formal.model.CrAccount;
import cn.zswltech.mithras.report.mapper.formal.model.CrFiveClass;
import cn.zswltech.mithras.report.mapper.fullsnap.CrFiveClassFullSnapMapper;
import cn.zswltech.mithras.report.mapper.model.BatchRecord;
import cn.zswltech.mithras.report.mapper.model.CrModifyDataSnap;
import cn.zswltech.mithras.report.mapper.procsnap.model.CrFiveClassProcSnap;
import cn.zswltech.mithras.report.service.BatchRecordService;
import cn.zswltech.mithras.report.service.CommonInfoService;
import cn.zswltech.mithras.report.service.CrModifyDataSnapService;
import cn.zswltech.mithras.report.service.draft.CrAccountDraftService;
import cn.zswltech.mithras.report.service.draft.CrFiveClassDraftService;
import cn.zswltech.mithras.report.service.formal.CrAccountService;
import cn.zswltech.mithras.report.service.formal.CrFiveClassService;
import cn.zswltech.mithras.report.service.fullsnap.CrFiveClassFullSnapService;
import cn.zswltech.mithras.report.service.procsnap.CrFiveClassProcSnapService;
import cn.zswltech.mithras.report.util.ReportBizUtil;
import cn.zswltech.mithras.report.util.ReportCompareUtil;
import cn.zswltech.mithras.service.constant.FlowConstants;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.service.constant.VersionTypeConstants;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.service.gendoc.render.contract.baoli.yzdy.YzDyBaoLiBizRender;
import cn.zswltech.mithras.service.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.service.mapper.model.payment.PaymentBaseInfo;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.others.SpringContextHolder;
import cn.zswltech.mithras.service.service.contract.ContractAccountService;
import cn.zswltech.mithras.service.service.contract.ContractBaseInfoService;
import cn.zswltech.mithras.service.util.StringUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 征信报送-五级分类表接口
 *
 * @author wangchuanhao
 * @date 2023/1/11 5:19 PM
 */
@Service
@Order(-1)
public class CrFiveClassAggSerivce implements ICrProcessWorker<CrFiveClassDraft, FiveClassListREQ> {

    @Resource
    private CrFiveClassDraftService crFiveClassDraftService;
    @Resource
    private CrAccountDraftService crAccountDraftService;
    @Resource
    private ProcHelper procHelper;
    @Resource
    private CrFiveClassService crFiveClassService;
    @Resource
    private CrFiveClassProcSnapService crFiveClassProcSnapService;
    @Resource
    private CrAccountService crAccountService;
    @Resource
    private CrFiveClassFullSnapService crFiveClassFullSnapService;
    @Resource
    private CrFiveClassFullSnapMapper crFiveClassFullSnapMapper;
    @Resource
    private CommonInfoService commonInfoService;
    @Resource
    private CrModifyDataSnapMapper modifyDataSnapMapper;

    @Transactional(rollbackFor = Exception.class, transactionManager = ReportConstants.TRANSACTION_MANAGER)
    public void add(FiveClassAddREQ req) {
        CrAccountDraft crAccountDraft = crAccountDraftService.getById(req.getAccountId());
        if (Objects.isNull(crAccountDraft)) {
            throw new MithrasException("账户表数据不存在");
        }
        ProcessResp processResp = procHelper.findRelatedProcess();
        if (Objects.nonNull(processResp) &&
                (!FlowConstants.START_USER_TASK.equals(processResp.getCurTaskActivityIds())
                        || !Objects.equals(Long.valueOf(processResp.getStartUserId()), AccountUtil.getLoginInfo().getId()))) {
            // 流程中 且 不在发起人节点
            throw new MithrasException("数据处于审批中，仅流程发起人可在发起人节点新增数据！");
        }
        CrFiveClassDraft crFiveClassDraft = new CrFiveClassDraft();
        crFiveClassDraft.setReportState(ReportState.TO_BE_REPORT.name());
        crFiveClassDraft.setPaymentApplyCode(crAccountDraft.getPaymentApplyCode());
        crFiveClassDraft.setPaymentId(crAccountDraft.getPaymentId());
        crFiveClassDraft.setFiveClass(req.getFiveClass());
        crFiveClassDraft.setIdentificationDate(Optional.ofNullable(req.getIdentificationDate()).map(LocalDateTimeUtil::of).orElse(null));
        crFiveClassDraft.setBusinessKey(crFiveClassDraft.genBusinessKey());
        crFiveClassDraft.setContractId(crAccountDraft.getContractId());
        crFiveClassDraft.setProcBusinessKey(Optional.ofNullable(processResp).map(ProcessResp::getBusinessKey).map(Long::valueOf).orElse(null));
        crFiveClassDraft.setApprovalStatus(Objects.isNull(processResp) ? ApprovalStatus.UN_SUBMIT.name() : ApprovalStatus.UNDER_APPROVAL.name());

        CrModifyDataSnap modifyDataSnap = modifyDataSnapMapper.selectOne(Wrappers.<CrModifyDataSnap>lambdaQuery()
                .eq(CrModifyDataSnap::getTableType, TableTypeEnum.FIVE_CLASS.name())
                .eq(CrModifyDataSnap::getIsTakeEffect, YesOrNoNumberEnum.YES.getCode())
                .eq(CrModifyDataSnap::getVersionType, VersionTypeConstants.NORMAL)
                .orderByDesc(CrModifyDataSnap::getVersion)
                .last(StringUtil.mysqlLimitOne()));
        String version;
        if (ObjectUtil.isNull(modifyDataSnap)) {
            version = ReportBizUtil.getVersion("");
        } else {
            version = ReportBizUtil.getVersion(modifyDataSnap.getVersion());
        }

        modifyDataSnapMapper.insert(CrModifyDataSnap.builder()
                .label(DataShowTypeEnum.ADD.name())
                .businessKey(crFiveClassDraft.getBusinessKey())
                .dataMap(JSONUtil.toJsonStr(crFiveClassDraft))
                .isTakeEffect(YesOrNoNumberEnum.YES.getCode())
                .approvalStatus(ApprovalStatus.UN_SUBMIT.name())
                .tableType(TableTypeEnum.FIVE_CLASS.name())
                //版本号格式 00xxyyyyMMdd
                .version(version)
                .versionType(VersionTypeConstants.NORMAL)
                .reason(req.getReason())
                .build());
    }

    @Transactional(rollbackFor = Exception.class, transactionManager = ReportConstants.TRANSACTION_MANAGER)
    public void modify(FiveClassModifyREQ req) {
        CrFiveClassDraft existData = crFiveClassDraftService.getById(req.getId());
        CrModifyDataSnap modifyDataSnap;
        if (Objects.isNull(existData)) {
            modifyDataSnap = modifyDataSnapMapper.selectOne(Wrappers.<CrModifyDataSnap>lambdaQuery()
                    .eq(CrModifyDataSnap::getIsTakeEffect, YesOrNoNumberEnum.YES.getCode())
                    .eq(CrModifyDataSnap::getTableType, TableTypeEnum.FIVE_CLASS.name())
                    .eq(CrModifyDataSnap::getBusinessKey, req.getBusinessKey()));
            if (ObjectUtil.isNull(modifyDataSnap)) {
                throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
            }
            existData = JSONUtil.toBean(modifyDataSnap.getDataMap(), CrFiveClassDraft.class);
        }
        if (!ReportState.TO_BE_REPORT.name().equals(existData.getReportState())) {
            throw new MithrasException("非待报送状态的数据不允许修改");
        }
        if (ApprovalStatus.UNDER_APPROVAL.name().equals(existData.getApprovalStatus()) && !procHelper.canEditData()) {
            throw new MithrasException("该条数据处于审批中，请撤回后再修改！");
        }
        CrFiveClassDraft baseModel = BeanUtil.copyProperties(existData, CrFiveClassDraft.class, ReportConstants.IGNORE_ID);
        baseModel.setFiveClass(req.getFiveClass());
        baseModel.setIdentificationDate(Optional.ofNullable(req.getIdentificationDate()).map(LocalDateTimeUtil::of).orElse(null));
        baseModel.setProcBusinessKey(existData.getProcBusinessKey());
        baseModel.setBusinessKey(req.getBusinessKey());

        modifyDataSnap = modifyDataSnapMapper.selectOne(Wrappers.<CrModifyDataSnap>lambdaQuery()
                .eq(CrModifyDataSnap::getBusinessKey, req.getBusinessKey())
                .eq(CrModifyDataSnap::getTableType, TableTypeEnum.FIVE_CLASS.name())
                .eq(CrModifyDataSnap::getIsTakeEffect, YesOrNoNumberEnum.YES.getCode())
                .eq(CrModifyDataSnap::getVersionType, VersionTypeConstants.NORMAL)
                .orderByDesc(CrModifyDataSnap::getVersion)
                .last(StringUtil.mysqlLimitOne()));
        String version;
        String modifyType = DataShowTypeEnum.MODIFY.name();
        if (ObjectUtil.isNull(modifyDataSnap)) {
            version = ReportBizUtil.getVersion("");
        } else {
            modifyDataSnapMapper.updateById(CrModifyDataSnap.builder()
                    .id(modifyDataSnap.getId())
                    .isTakeEffect(YesOrNoNumberEnum.NO.getCode())
                    .build());
            modifyType = modifyDataSnap.getLabel();
            version = ReportBizUtil.getVersion(modifyDataSnap.getVersion());
        }
        modifyDataSnapMapper.insert(CrModifyDataSnap.builder()
                .label(modifyType)
                .businessKey(existData.getBusinessKey())
                .dataMap(JSONUtil.toJsonStr(baseModel))
                .oldDataMap(JSONUtil.toJsonStr(existData))
                .approvalStatus(ApprovalStatus.UN_SUBMIT.name())
                .isTakeEffect(YesOrNoNumberEnum.YES.getCode())
                .tableType(TableTypeEnum.FIVE_CLASS.name())
                //版本号格式 00xxyyyyMMdd
                .version(version)
                .versionType(VersionTypeConstants.NORMAL)
                .reason(req.getReason())
                .build());
    }

    @Transactional(rollbackFor = Exception.class, transactionManager = ReportConstants.TRANSACTION_MANAGER)
    public void remove(FiveClassRemoveREQ req) {
        CrFiveClassDraft existData = crFiveClassDraftService.getById(req.getId());
        if (Objects.isNull(existData)) {
            CrModifyDataSnap modifyDataSnap = modifyDataSnapMapper.selectOne(Wrappers.<CrModifyDataSnap>lambdaQuery()
                    .eq(CrModifyDataSnap::getIsTakeEffect, YesOrNoNumberEnum.YES.getCode())
                    .eq(CrModifyDataSnap::getTableType, TableTypeEnum.FIVE_CLASS.name())
                    .eq(CrModifyDataSnap::getBusinessKey, req.getBusinessKey()));
            if (ObjectUtil.isNull(modifyDataSnap)) {
                throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
            }
            // 如果是删除 存在数据直接删掉就好了
            modifyDataSnapMapper.deleteById(modifyDataSnap.getId());
            return;
        }
        if (!ReportState.TO_BE_REPORT.name().equals(existData.getReportState())) {
            throw new MithrasException("非待报送状态的数据不允许删除");
        }
        if (ApprovalStatus.UNDER_APPROVAL.name().equals(existData.getApprovalStatus()) && !procHelper.canEditData()) {
            throw new MithrasException("该条数据处于审批中，请撤回后再删除！");
        }
        CrModifyDataSnap modifyDataSnap = modifyDataSnapMapper.selectOne(Wrappers.<CrModifyDataSnap>lambdaQuery()
                .eq(CrModifyDataSnap::getBusinessKey, req.getBusinessKey())
                .eq(CrModifyDataSnap::getTableType, TableTypeEnum.FIVE_CLASS.name())
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
                .label(DataShowTypeEnum.REMOVE.name())
                .businessKey(existData.getBusinessKey())
                .isTakeEffect(YesOrNoNumberEnum.YES.getCode())
                .approvalStatus(ApprovalStatus.UN_SUBMIT.name())
                .tableType(TableTypeEnum.FIVE_CLASS.name())
                //版本号格式 00xxyyyyMMdd
                .version(version)
                .reason(req.getReason())
                .versionType(VersionTypeConstants.NORMAL)
                .build());
    }

    public List<Map<String, DiffValue>> pageList(FiveClassListREQ req) {
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
        return Collections.emptyList();
    }

    public List<Map<String, DiffValue>> listByEdit(FiveClassListREQ req) {
        List<CrFiveClassDraft> records = crFiveClassDraftService.getBaseMapper().selectList(Wrappers.<CrFiveClassDraft>lambdaQuery()
                .eq(CrFiveClassDraft::getReportState, ReportState.TO_BE_REPORT.name())
                .eq(StringUtils.isNotBlank(req.getFiveClass()), CrFiveClassDraft::getFiveClass, req.getFiveClass())
                .eq(StringUtils.isNotBlank(req.getApprovalStatus()), CrFiveClassDraft::getApprovalStatus, req.getApprovalStatus())
                .like(StringUtils.isNotBlank(req.getPaymentApplyCode()), CrFiveClassDraft::getPaymentApplyCode, req.getPaymentApplyCode())
                .inSql(CrFiveClassDraft::getPaymentId, "SELECT payment_id FROM cr_account_draft WHERE report_flag = 1")
        );

        List<CrModifyDataSnap> list = modifyDataSnapMapper.selectList(Wrappers.<CrModifyDataSnap>lambdaQuery()
                .eq(CrModifyDataSnap::getTableType, TableTypeEnum.FIVE_CLASS.name())
                .ne(CrModifyDataSnap::getLabel, DataShowTypeEnum.REMOVE.name())
                .eq(CharSequenceUtil.isNotBlank(req.getApprovalStatus()), CrModifyDataSnap::getApprovalStatus, req.getApprovalStatus())
                .eq(CrModifyDataSnap::getIsShow, YesOrNoNumberEnum.YES.getCode())
                .eq(CharSequenceUtil.isNotBlank(req.getApprovalStatus()), CrModifyDataSnap::getApprovalStatus, req.getApprovalStatus())
                .eq(CrModifyDataSnap::getIsTakeEffect, YesOrNoNumberEnum.YES.getCode()));

        if (CollUtil.isNotEmpty(records)) {
            List<String> businessKeys = records.stream().map(CrFiveClassDraft::getBusinessKey).collect(Collectors.toList());
            list.removeIf(a -> businessKeys.contains(a.getBusinessKey()));
        }

        if (CollUtil.isNotEmpty(list)) {
            list.forEach(info -> records.add(JSONUtil.toBean(info.getDataMap(), CrFiveClassDraft.class)));
        }
        List<CrFiveClassDraft> res = records.stream()
                .filter(a -> CharSequenceUtil.isNotBlank(req.getFiveClass()) ? req.getFiveClass().equals(a.getFiveClass()) : Boolean.TRUE)
                .filter(a -> CharSequenceUtil.isNotBlank(req.getPaymentApplyCode()) ? a.getPaymentApplyCode().contains(req.getPaymentApplyCode()) : Boolean.TRUE)
                .collect(Collectors.toList());
        return getMaps(res, null, null, 1);
    }

    private List<Map<String, DiffValue>> getMaps(List<CrFiveClassDraft> records, String procBusinessKey, String batchNo, Integer isShow) {
        if (CollUtil.isEmpty(records)) {
            return Collections.emptyList();
        }
        Set<Long> contractIds = records.stream().map(CrFiveClassDraft::getContractId).collect(Collectors.toSet());
        Set<Long> paymentIds = records.stream().map(CrFiveClassDraft::getPaymentId).collect(Collectors.toSet());
        Map<Long, ContractBaseInfo> contractBaseInfoMap = commonInfoService.getLongContractBaseInfoMap(contractIds);
        Map<Long, String> clientIdMap = commonInfoService.getClientId2NameMap(paymentIds);
        Map<Long, PaymentBaseInfo> paymentBaseInfoMap = commonInfoService.getPaymentBaseInfoMap(paymentIds);
        List<FiveClassListRSP> rspList = records.stream().map(d -> {
            FiveClassListRSP fiveClass = BeanUtil.copyProperties(d, FiveClassListRSP.class);
            ContractBaseInfo contractBaseInfo = Optional.ofNullable(contractBaseInfoMap.get(d.getContractId())).orElse(new ContractBaseInfo());
            fiveClass.setContractCode(contractBaseInfo.getContractCode());
            fiveClass.setClientName(Optional.of(paymentBaseInfoMap.get(d.getPaymentId())).map(e -> clientIdMap.get(e.getClientId())).orElse(null));
            return fiveClass;
        }).collect(Collectors.toList());
        List<String> businessKeys = rspList.stream().map(FiveClassListRSP::getBusinessKey).collect(Collectors.toList());
        return commonInfoService.buildDiffMapList(rspList, businessKeys, procBusinessKey, TableTypeEnum.FIVE_CLASS, FiveClassListRSP.class, batchNo, isShow);
    }

    public List<Map<String, DiffValue>> listByProc(FiveClassListREQ req) {
        boolean processEndFlag = procHelper.judgeProcessEndWithCheck(req.getProcBusinessKey());
        if (processEndFlag) {
            List<CrFiveClassProcSnap> records = crFiveClassProcSnapService.getBaseMapper().selectList(Wrappers.<CrFiveClassProcSnap>lambdaQuery()
                    .eq(CrFiveClassProcSnap::getProcBusinessKey, Long.valueOf(req.getProcBusinessKey()))
                    .eq(StringUtils.isNotBlank(req.getFiveClass()), CrFiveClassProcSnap::getFiveClass, req.getFiveClass())
                    .like(StringUtils.isNotBlank(req.getPaymentApplyCode()), CrFiveClassProcSnap::getPaymentApplyCode, req.getPaymentApplyCode())
            );
            List<CrModifyDataSnap> list = modifyDataSnapMapper.selectList(Wrappers.<CrModifyDataSnap>lambdaQuery()
                    .eq(CrModifyDataSnap::getTableType, TableTypeEnum.FIVE_CLASS.name())
                    .eq(CharSequenceUtil.isNotBlank(req.getProcBusinessKey()), CrModifyDataSnap::getProcBusinessKey, req.getProcBusinessKey())
                    .eq(CharSequenceUtil.isNotBlank(req.getApprovalStatus()), CrModifyDataSnap::getApprovalStatus, req.getApprovalStatus())
                    .eq(CrModifyDataSnap::getLabel, DataShowTypeEnum.ADD.name()));

            List<CrModifyDataSnap> list1 = modifyDataSnapMapper.selectList(Wrappers.<CrModifyDataSnap>lambdaQuery()
                    .eq(CrModifyDataSnap::getTableType, TableTypeEnum.FIVE_CLASS.name())
                    .eq(CharSequenceUtil.isNotBlank(req.getProcBusinessKey()), CrModifyDataSnap::getProcBusinessKey, req.getProcBusinessKey())
                    .eq(CharSequenceUtil.isNotBlank(req.getApprovalStatus()), CrModifyDataSnap::getApprovalStatus, req.getApprovalStatus())
                    .eq(CrModifyDataSnap::getLabel, DataShowTypeEnum.REMOVE.name()));

            if (CollUtil.isNotEmpty(list1)) {
                List<String> businessKeyList = list1.stream().map(CrModifyDataSnap::getBusinessKey).collect(Collectors.toList());
                list.removeIf(a -> businessKeyList.contains(a.getBusinessKey()));
            }
            if (CollUtil.isNotEmpty(list)) {
                list.forEach(info -> records.add(BeanUtil.copyProperties(JSONUtil.toBean(info.getDataMap(), CrFiveClassDraft.class), CrFiveClassProcSnap.class, ReportConstants.IGNORE_ID)));
            }
            if (CollUtil.isEmpty(records)) {
                return Collections.emptyList();
            }

            List<CrFiveClassProcSnap> res = records.stream()
                    .filter(a -> CharSequenceUtil.isNotBlank(req.getFiveClass()) ? Objects.equals(req.getFiveClass(), a.getFiveClass()) : Boolean.TRUE)
                    .filter(a -> CharSequenceUtil.isNotBlank(req.getPaymentApplyCode()) ? a.getPaymentApplyCode().contains(req.getPaymentApplyCode()) : Boolean.TRUE)
                    .collect(Collectors.toList());

            Set<Long> contractIds = records.stream().map(CrFiveClassProcSnap::getContractId).collect(Collectors.toSet());
            Set<Long> paymentIds = records.stream().map(CrFiveClassProcSnap::getPaymentId).collect(Collectors.toSet());
            Map<Long, ContractBaseInfo> contractBaseInfoMap = commonInfoService.getLongContractBaseInfoMap(contractIds);
            Map<Long, String> clientIdMap = commonInfoService.getClientId2NameMap(paymentIds);
            Map<Long, PaymentBaseInfo> paymentBaseInfoMap = commonInfoService.getPaymentBaseInfoMap(paymentIds);
            List<FiveClassListRSP> rspList = res.stream().map(d -> {
                FiveClassListRSP fiveClass = BeanUtil.copyProperties(d, FiveClassListRSP.class);
                ContractBaseInfo info = Optional.ofNullable(contractBaseInfoMap.get(d.getContractId())).orElse(new ContractBaseInfo());
                fiveClass.setContractCode(info.getContractCode());
                fiveClass.setClientName(Optional.of(paymentBaseInfoMap.get(d.getPaymentId())).map(e -> clientIdMap.get(e.getClientId())).orElse(null));
                return fiveClass;
            }).collect(Collectors.toList());
            List<String> businessKeys = rspList.stream().map(FiveClassListRSP::getBusinessKey).collect(Collectors.toList());
            return commonInfoService.buildDiffMapList(rspList, businessKeys, req.getProcBusinessKey(), TableTypeEnum.FIVE_CLASS, FiveClassListRSP.class, null, null);
        } else {
            List<CrFiveClassDraft> records = crFiveClassDraftService.getBaseMapper().selectList(Wrappers.<CrFiveClassDraft>lambdaQuery()
                    .eq(CrFiveClassDraft::getProcBusinessKey, Long.valueOf(req.getProcBusinessKey()))
                    .eq(CrFiveClassDraft::getReportState, ReportState.TO_BE_REPORT.name())
                    .eq(StringUtils.isNotBlank(req.getFiveClass()), CrFiveClassDraft::getFiveClass, req.getFiveClass())
                    .eq(StringUtils.isNotBlank(req.getApprovalStatus()), CrFiveClassDraft::getApprovalStatus, req.getApprovalStatus())
                    .like(StringUtils.isNotBlank(req.getPaymentApplyCode()), CrFiveClassDraft::getPaymentApplyCode, req.getPaymentApplyCode())
                    .inSql(CrFiveClassDraft::getPaymentId, "SELECT payment_id FROM cr_account_draft WHERE report_flag = 1")
            );

            List<CrModifyDataSnap> list = modifyDataSnapMapper.selectList(Wrappers.<CrModifyDataSnap>lambdaQuery()
                    .eq(CrModifyDataSnap::getTableType, TableTypeEnum.FIVE_CLASS.name())
                    .eq(CharSequenceUtil.isNotBlank(req.getProcBusinessKey()), CrModifyDataSnap::getProcBusinessKey, req.getProcBusinessKey())
                    .eq(CharSequenceUtil.isNotBlank(req.getApprovalStatus()), CrModifyDataSnap::getApprovalStatus, req.getApprovalStatus())
                    .eq(CrModifyDataSnap::getIsTakeEffect, YesOrNoNumberEnum.YES.getCode()));

            List<CrModifyDataSnap> modifyDataSnaps = modifyDataSnapMapper.selectList(Wrappers.<CrModifyDataSnap>lambdaQuery()
                    .eq(CrModifyDataSnap::getIsTakeEffect, YesOrNoNumberEnum.YES.getCode())
                    .eq(CharSequenceUtil.isNotBlank(req.getApprovalStatus()), CrModifyDataSnap::getApprovalStatus, req.getApprovalStatus())
                    .eq(CharSequenceUtil.isNotBlank(req.getProcBusinessKey()), CrModifyDataSnap::getProcBusinessKey, req.getProcBusinessKey())
                    .eq(CrModifyDataSnap::getLabel, DataShowTypeEnum.ADD.name()));

            if (CollUtil.isNotEmpty(modifyDataSnaps)) {
                List<String> collect = modifyDataSnaps.stream().map(CrModifyDataSnap::getBusinessKey).collect(Collectors.toList());
                if (CollUtil.isNotEmpty(list)) {
                    list.removeIf(a -> !collect.contains(a.getBusinessKey()) && DataShowTypeEnum.ADD.name().equals(a.getLabel()));
                    list.forEach(info -> records.add(JSONUtil.toBean(info.getDataMap(), CrFiveClassDraft.class)));
                }
            } else {
                list.forEach(info -> records.add(JSONUtil.toBean(info.getDataMap(), CrFiveClassDraft.class)));
            }

            List<CrFiveClassDraft> res = records.stream()
                    .filter(a -> CharSequenceUtil.isNotBlank(req.getFiveClass()) ? Objects.equals(req.getFiveClass(), a.getFiveClass()) : Boolean.TRUE)
                    .filter(a -> CharSequenceUtil.isNotBlank(req.getPaymentApplyCode()) ? req.getPaymentApplyCode().contains(a.getPaymentApplyCode()) : Boolean.TRUE)
                    .collect(Collectors.toList());

            return getMaps(res, req.getProcBusinessKey(), null, null);
        }
    }

    public List<Map<String, DiffValue>> listByBatchIncre(FiveClassListREQ req) {
        List<CrFiveClassProcSnap> records = crFiveClassProcSnapService.getBaseMapper().selectList(Wrappers.<CrFiveClassProcSnap>lambdaQuery()
                .eq(CrFiveClassProcSnap::getBatchId, req.getBatchId())
                .eq(StringUtils.isNotBlank(req.getFiveClass()), CrFiveClassProcSnap::getFiveClass, req.getFiveClass())
                .like(StringUtils.isNotBlank(req.getPaymentApplyCode()), CrFiveClassProcSnap::getPaymentApplyCode, req.getPaymentApplyCode())
        );

        BatchRecord batchRecord = SpringContextHolder.getBean(BatchRecordService.class).getById(req.getBatchId());
        if (CollUtil.isEmpty(records)) {
            List<CrModifyDataSnap> list = modifyDataSnapMapper.selectList(Wrappers.<CrModifyDataSnap>lambdaQuery()
                    .eq(CrModifyDataSnap::getTableType, TableTypeEnum.FIVE_CLASS.name())
                    .eq(CrModifyDataSnap::getIsTakeEffect, YesOrNoNumberEnum.YES.getCode())
                    .eq(CrModifyDataSnap::getBatchNo, batchRecord.getBatchNo())
                    .eq(CharSequenceUtil.isNotBlank(req.getApprovalStatus()), CrModifyDataSnap::getApprovalStatus, req.getApprovalStatus())
                    .eq(CrModifyDataSnap::getLabel, DataShowTypeEnum.ADD.name()));

            List<CrModifyDataSnap> list1 = modifyDataSnapMapper.selectList(Wrappers.<CrModifyDataSnap>lambdaQuery()
                    .eq(CrModifyDataSnap::getTableType, TableTypeEnum.FIVE_CLASS.name())
                    .eq(CrModifyDataSnap::getIsTakeEffect, YesOrNoNumberEnum.YES.getCode())
                    .eq(CrModifyDataSnap::getBatchNo, batchRecord.getBatchNo())
                    .eq(CharSequenceUtil.isNotBlank(req.getApprovalStatus()), CrModifyDataSnap::getApprovalStatus, req.getApprovalStatus())
                    .eq(CrModifyDataSnap::getLabel, DataShowTypeEnum.REMOVE.name()));

            if (CollUtil.isNotEmpty(list1)) {
                List<String> businessKeyList = list1.stream().map(CrModifyDataSnap::getBusinessKey).collect(Collectors.toList());
                list.removeIf(a -> businessKeyList.contains(a.getBusinessKey()));
            }

            //这里需要做动态查询
            if (CollUtil.isNotEmpty(list)) {
                list = list.stream()
                        .filter(a -> !CharSequenceUtil.isNotBlank(req.getFiveClass()) || JSONUtil.toBean(a.getDataMap(), CrFiveClassDraft.class).getFiveClass().equals(req.getFiveClass()))
                        .filter(a -> !CharSequenceUtil.isNotBlank(req.getPaymentApplyCode()) || JSONUtil.toBean(a.getDataMap(), CrFiveClassDraft.class).getPaymentApplyCode().contains(req.getPaymentApplyCode()))
                        .collect(Collectors.toList());
            }

            if (CollUtil.isNotEmpty(list)) {
                list.forEach(info -> records.add(BeanUtil.copyProperties(JSONUtil.toBean(info.getDataMap(), CrFiveClassDraft.class), CrFiveClassProcSnap.class, ReportConstants.IGNORE_ID)));
            }
            if (CollUtil.isEmpty(records)) {
                return Collections.emptyList();
            }
        }
        Set<Long> contractIds = records.stream().map(CrFiveClassProcSnap::getContractId).collect(Collectors.toSet());
        Set<Long> paymentIds = records.stream().map(CrFiveClassProcSnap::getPaymentId).collect(Collectors.toSet());
        Map<Long, ContractBaseInfo> contractBaseInfoMap = commonInfoService.getLongContractBaseInfoMap(contractIds);
        Map<Long, String> clientIdMap = commonInfoService.getClientId2NameMap(paymentIds);
        Map<Long, PaymentBaseInfo> paymentBaseInfoMap = commonInfoService.getPaymentBaseInfoMap(paymentIds);

        List<FiveClassListRSP> rspList = records.stream().map(d -> {
            FiveClassListRSP fiveClass = BeanUtil.copyProperties(d, FiveClassListRSP.class);
            ContractBaseInfo info = Optional.ofNullable(contractBaseInfoMap.get(d.getContractId()))
                    .orElse(SpringContextHolder.getBean(ContractBaseInfoService.class).getById(d.getContractId()));
            fiveClass.setContractCode(info.getContractCode());
            fiveClass.setClientName(Optional.of(paymentBaseInfoMap.get(d.getPaymentId())).map(e -> clientIdMap.get(e.getClientId())).orElse(null));
            return fiveClass;
        }).collect(Collectors.toList());
        List<String> businessKeys = rspList.stream().map(FiveClassListRSP::getBusinessKey).collect(Collectors.toList());
        return commonInfoService.buildDiffMapList(rspList, businessKeys, req.getProcBusinessKey(), TableTypeEnum.FIVE_CLASS, FiveClassListRSP.class, batchRecord.getBatchNo(), null);
    }

    public List<Map<String, DiffValue>> listByEffect(FiveClassListREQ req) {
        String paymentApplyCode = null;
        if (Objects.nonNull(req.getAccountId())) {
            CrAccount crAccount = crAccountService.getById(req.getAccountId());
            if (Objects.isNull(crAccount)) {
                throw new MithrasException("账户表数据不存在");
            }
            paymentApplyCode = crAccount.getPaymentApplyCode();
        }
        List<CrFiveClass> records = crFiveClassService.getBaseMapper().selectList(Wrappers.<CrFiveClass>lambdaQuery()
                .eq(CharSequenceUtil.isNotBlank(paymentApplyCode), CrFiveClass::getPaymentApplyCode, paymentApplyCode)
                .eq(StringUtils.isNotBlank(req.getFiveClass()), CrFiveClass::getFiveClass, req.getFiveClass())
                .like(StringUtils.isNotBlank(req.getPaymentApplyCode()), CrFiveClass::getPaymentApplyCode, req.getPaymentApplyCode())
        );
        if (CollUtil.isEmpty(records)) {
            return Collections.emptyList();
        }
        Set<Long> contractIds = records.stream().map(CrFiveClass::getContractId).collect(Collectors.toSet());
        Set<Long> paymentIds = records.stream().map(CrFiveClass::getPaymentId).collect(Collectors.toSet());
        Map<Long, ContractBaseInfo> contractBaseInfoMap = commonInfoService.getLongContractBaseInfoMap(contractIds);
        Map<Long, String> clientIdMap = commonInfoService.getClientId2NameMap(paymentIds);
        Map<Long, PaymentBaseInfo> paymentBaseInfoMap = commonInfoService.getPaymentBaseInfoMap(paymentIds);
        List<FiveClassListRSP> rspList = records.stream().map(d -> {
            FiveClassListRSP fiveClass = BeanUtil.copyProperties(d, FiveClassListRSP.class);
            ContractBaseInfo info = Optional.ofNullable(contractBaseInfoMap.get(d.getContractId())).orElse(new ContractBaseInfo());
            fiveClass.setContractCode(info.getContractCode());
            fiveClass.setClientName(Optional.of(paymentBaseInfoMap.get(d.getPaymentId())).map(e -> clientIdMap.get(e.getClientId())).orElse(null));
            return fiveClass;
        }).collect(Collectors.toList());
        List<String> businessKeys = rspList.stream().map(FiveClassListRSP::getBusinessKey).collect(Collectors.toList());
        return commonInfoService.buildDiffMapEffectList(rspList, businessKeys, TableTypeEnum.FIVE_CLASS, FiveClassListRSP.class);
    }

    /**
     * 请注意该类需要在accountAggService之前执行，依赖AccountDraft数据的状态
     */
    @Override
    @Transactional(rollbackFor = Exception.class, transactionManager = ReportConstants.TRANSACTION_MANAGER)
    public void processEnd(Long procBusinessKey, Integer endType, Long startUserId, String processInstanceId,
                           LocalDateTime reportTime, BatchRecord procSnapRecord, BatchRecord fullSnapRecord) {
        boolean processPass = ProcessBusinessStatusEnum.success(endType);
        List<CrFiveClassDraft> draftList = crFiveClassDraftService.getBaseMapper().selectList(Wrappers.<CrFiveClassDraft>lambdaQuery()
                .eq(CrFiveClassDraft::getReportState, ReportState.TO_BE_REPORT.name())
                .eq(CrFiveClassDraft::getProcBusinessKey, procBusinessKey)
        );
        // 找到编辑区账户表数据
        Map<String, CrAccountDraft> crAccountDraftMap = CollectionUtils.isEmpty(draftList) ? new HashMap<>()
                : crAccountDraftService.list(Wrappers.<CrAccountDraft>lambdaQuery()
                .in(CrAccountDraft::getPaymentId, draftList.stream().map(CrFiveClassDraft::getPaymentId).collect(Collectors.toSet()))
        ).stream().collect(Collectors.toMap(CrAccountDraft::getPaymentApplyCode, Function.identity(), (a, b) -> a));

        // 1.编辑区数据插入ProcSnap 对应的账户表报送，此子表才显示 才抄
        List<CrFiveClassProcSnap> procSnapList = draftList.stream()
                .filter(d -> Objects.equals(YesOrNoNumberEnum.YES.getCode(), Optional.ofNullable(crAccountDraftMap.get(d.getPaymentApplyCode()))
                        .map(CrAccountDraft::getReportFlag).orElse(0)))
                .map(d -> {
                    CrFiveClassProcSnap procSnap = BeanUtil.copyProperties(d, CrFiveClassProcSnap.class, ReportConstants.IGNORE_ID);
                    procSnap.setBatchId(Optional.ofNullable(procSnapRecord).map(BatchRecord::getId).orElse(null));
                    procSnap.setBatchNo(Optional.ofNullable(procSnapRecord).map(BatchRecord::getBatchNo).orElse(null));
                    return procSnap;
                }).collect(Collectors.toList());

        crFiveClassProcSnapService.saveBatch(procSnapList);
        if (processPass) {
            // 1.如果审批成功 把修改数据更新到draft表
            List<CrModifyDataSnap> modifyDataSnaps = modifyDataSnapMapper.selectList(Wrappers.<CrModifyDataSnap>lambdaQuery()
                    .eq(CrModifyDataSnap::getProcBusinessKey, procBusinessKey)
                    .eq(CrModifyDataSnap::getIsShow, YesOrNoNumberEnum.YES.getCode())
                    .eq(CrModifyDataSnap::getTableType, TableTypeEnum.FIVE_CLASS.name())
                    .eq(CrModifyDataSnap::getIsTakeEffect, YesOrNoNumberEnum.YES.getCode()));

            Map<String, CrModifyDataSnap> modifyDataSnapMap = new LinkedHashMap<>();
            if (CollUtil.isNotEmpty(modifyDataSnaps)) {
                modifyDataSnapMap = modifyDataSnaps.stream().collect(Collectors.toMap(CrModifyDataSnap::getBusinessKey, Function.identity(), (a, b) -> a));
            }
            // 2.如果审批成功 把编辑区选择报送的数据抄到生效区 对应的账户表报送，此子表才报送
            if (CollectionUtils.isNotEmpty(draftList)) {
                Map<String, CrModifyDataSnap> finalModifyDataSnapMap = modifyDataSnapMap;
                draftList.forEach(draft -> {
                    CrModifyDataSnap modifyDataSnap = finalModifyDataSnapMap.get(draft.getBusinessKey());
                    if (ObjectUtil.isNull(modifyDataSnap) || CharSequenceUtil.isBlank(modifyDataSnap.getDataMap())) {
                        return;
                    }
                    CrFiveClassDraft fiveClassDraft = JSONUtil.toBean(modifyDataSnap.getDataMap(), CrFiveClassDraft.class);
                    BeanUtil.copyProperties(fiveClassDraft, draft, ReportConstants.IGNORE_ID);
                    finalModifyDataSnapMap.remove(draft.genBusinessKey());
                });

                List<CrFiveClass> draft2FormalList = draftList.stream()
                        .filter(d -> Objects.equals(YesOrNoNumberEnum.YES.getCode(),
                                Optional.ofNullable(crAccountDraftMap.get(d.getPaymentApplyCode()))
                                        .map(CrAccountDraft::getReportFlag)
                                        .orElse(0)))
                        .map(d -> BeanUtil.copyProperties(d, CrFiveClass.class, ReportConstants.IGNORE_ID))
                        .collect(Collectors.toList());

                List<CrFiveClass> needInsertList = new ArrayList<>();
                Set<String> businessKeyList = draftList.stream().map(CrFiveClassDraft::getBusinessKey).collect(Collectors.toSet());
                Map<String, CrFiveClass> existReportDataMap = new HashMap<>();
                if (CollUtil.isNotEmpty(businessKeyList)) {
                    existReportDataMap = crFiveClassService.list(Wrappers.<CrFiveClass>lambdaQuery()
                                    .in(CrFiveClass::getBusinessKey, businessKeyList))
                            .stream().collect(Collectors.toMap(CrFiveClass::getBusinessKey, Function.identity(), (a, b) -> a));
                }

                for (CrFiveClass draft2Formal : draft2FormalList) {
                    //删除数据
                    CrModifyDataSnap snap = finalModifyDataSnapMap.get(draft2Formal.getBusinessKey());
                    if (!finalModifyDataSnapMap.isEmpty() && Objects.nonNull(snap) && DataShowTypeEnum.REMOVE.name().equals(snap.getLabel())) {
                        //啥也不干
                        continue;
                    }
                    if (!existReportDataMap.containsKey(draft2Formal.getBusinessKey())) {
                        needInsertList.add(draft2Formal);
                    } else {
                        CrFiveClass existData = existReportDataMap.get(draft2Formal.getBusinessKey());
                        if (ReportCompareUtil.checkChange(draft2Formal, existData, existData.ignoreCompareFieldNames()) && Objects.nonNull(existData)) {
                            draft2Formal.setId(existData.getId());
                            crFiveClassService.updateById(draft2Formal);
                        }
                    }
                }
                crFiveClassService.saveBatch(needInsertList);
            }

            //如果还有新增的值，需要抄送到draft
            if (CollUtil.isNotEmpty(modifyDataSnapMap)) {
                for (CrModifyDataSnap dataSnap : modifyDataSnapMap.values()) {
                    //新增的数据需要抄表，删除的数据后续抄生效区表时处理
                    if (DataShowTypeEnum.ADD.name().equals(dataSnap.getLabel())) {
                        CrFiveClassDraft classDraft = JSONUtil.toBean(dataSnap.getDataMap(), CrFiveClassDraft.class);
                        classDraft.setReportState(ReportState.REPORTED.name());
                        classDraft.setApprovalStatus(ApprovalStatus.UN_SUBMIT.name());
                        classDraft.setProcBusinessKey(null);
                        crFiveClassDraftService.save(classDraft);
                        crFiveClassService.save(BeanUtil.copyProperties(classDraft, CrFiveClass.class, ReportConstants.IGNORE_ID));

                        // 流程表也需要新增
                        CrFiveClassProcSnap procSnap = BeanUtil.copyProperties(classDraft, CrFiveClassProcSnap.class, ReportConstants.IGNORE_ID);
                        procSnap.setBatchId(Optional.ofNullable(procSnapRecord).map(BatchRecord::getId).orElse(null));
                        procSnap.setBatchNo(Optional.ofNullable(procSnapRecord).map(BatchRecord::getBatchNo).orElse(null));
                        crFiveClassProcSnapService.save(procSnap);
                    } else if (DataShowTypeEnum.MODIFY.name().equals(dataSnap.getLabel())) {
                        CrModifyDataSnap modifyDataSnap = modifyDataSnapMapper.selectOne(Wrappers.<CrModifyDataSnap>lambdaQuery()
                                .eq(CrModifyDataSnap::getBusinessKey, dataSnap.getBusinessKey())
                                .eq(CrModifyDataSnap::getLabel, DataShowTypeEnum.ADD.name())
                                .last(StringUtil.mysqlLimitOne()));
                        if (ObjectUtil.isNotNull(modifyDataSnap)) {
                            CrFiveClassDraft classDraft = JSONUtil.toBean(dataSnap.getDataMap(), CrFiveClassDraft.class);
                            classDraft.setReportState(ReportState.REPORTED.name());
                            classDraft.setApprovalStatus(ApprovalStatus.UN_SUBMIT.name());
                            classDraft.setProcBusinessKey(null);
                            crFiveClassDraftService.save(classDraft);
                            crFiveClassService.save(BeanUtil.copyProperties(classDraft, CrFiveClass.class, ReportConstants.IGNORE_ID));
                        }
                    }
                }
            }
            // 3.如果审批成功 把生效区数据全量抄到FullSnap
            crFiveClassFullSnapMapper.copyFromEffect(Optional.ofNullable(fullSnapRecord).map(BatchRecord::getId).orElse(null),
                    Optional.ofNullable(fullSnapRecord).map(BatchRecord::getBatchNo).orElse(null));
        }
        // 4.编辑区数据处理
        List<Long> existReportDraftIdList = draftList.stream()
                .filter(d -> Objects.equals(YesOrNoNumberEnum.YES.getCode(), Optional.ofNullable(crAccountDraftMap.get(d.getPaymentApplyCode()))
                        .map(CrAccountDraft::getReportFlag).orElse(0))).map(CrFiveClassDraft::getId)
                .collect(Collectors.toList());
        List<Long> notReportDraftIdList = draftList.stream()
                .filter(d -> !Objects.equals(YesOrNoNumberEnum.YES.getCode(), Optional.ofNullable(crAccountDraftMap.get(d.getPaymentApplyCode()))
                        .map(CrAccountDraft::getReportFlag).orElse(0))).map(CrFiveClassDraft::getId)
                .collect(Collectors.toList());

        if (CollectionUtils.isNotEmpty(existReportDraftIdList)) {
            // 审批通过才处理成已报送
            crFiveClassDraftService.lambdaUpdate()
                    .set(processPass, CrFiveClassDraft::getReportState, ReportState.REPORTED.name())
                    .set(CrFiveClassDraft::getApprovalStatus, ApprovalStatus.UN_SUBMIT.name())
                    .set(CrFiveClassDraft::getProcBusinessKey, null)
                    .in(CrFiveClassDraft::getId, existReportDraftIdList)
                    .update();
        }
        if (CollectionUtils.isNotEmpty(notReportDraftIdList)) {
            // 不报送数据处理
            crFiveClassDraftService.lambdaUpdate()
                    .set(CrFiveClassDraft::getReportState, ReportState.TO_BE_REPORT.name())
                    .set(CrFiveClassDraft::getApprovalStatus, ApprovalStatus.UN_SUBMIT.name())
                    .set(CrFiveClassDraft::getProcBusinessKey, null)
                    .in(CrFiveClassDraft::getId, notReportDraftIdList)
                    .update();
        }
    }

    @Override
    public void submit(Long procBusinessKey, String batchNo) {
        crFiveClassDraftService.lambdaUpdate()
                .eq(CrFiveClassDraft::getReportState, ReportState.TO_BE_REPORT.name())
                .set(CrFiveClassDraft::getApprovalStatus, ApprovalStatus.UNDER_APPROVAL.name())
                .set(CrFiveClassDraft::getProcBusinessKey, procBusinessKey)
                .update();

        //更新数据修改到流程
        SpringContextHolder.getBean(CrModifyDataSnapService.class)
                .lambdaUpdate()
                .eq(CrModifyDataSnap::getTableType, TableTypeEnum.FIVE_CLASS.name())
                .isNull(CrModifyDataSnap::getProcBusinessKey)
                .set(CrModifyDataSnap::getBatchNo, batchNo)
                .set(CrModifyDataSnap::getApprovalStatus, ApprovalStatus.UNDER_APPROVAL.name())
                .set(CrModifyDataSnap::getProcBusinessKey, procBusinessKey)
                .update();
    }

    @Override
    public ReportPageEnum reportPageEnum() {
        return ReportPageEnum.FIVE_CLASS;
    }

    @Override
    public int countInProcessData() {
        return crFiveClassDraftService.count(Wrappers.<CrFiveClassDraft>lambdaQuery()
                .select(CrFiveClassDraft::getId)
                .eq(CrFiveClassDraft::getApprovalStatus, ApprovalStatus.UNDER_APPROVAL.name())
        );
    }

    @Override
    public IService<CrFiveClassDraft> getServiceInstance() {
        return crFiveClassDraftService;
    }

    @Override
    public PageR<Map<String, DiffValue>> list(FiveClassListREQ req) {
        // 手动处理分页
        List<Map<String, DiffValue>> maps = this.pageList(req);
        if (CollUtil.isEmpty(maps)) {
            return PageR.empty(req.getPage(), req.getPageSize());
        }
        int start = (req.getPage() - 1) * req.getPageSize();
        int end = Math.min(start + req.getPageSize(), maps.size());
        List<Map<String, DiffValue>> subList = maps.subList(start, end);
        return PageR.of(subList, maps.size(),req.getPage(), req.getPageSize());

    }

    @Override
    public String converter(String fieldName, DiffValue value) {
        switch (fieldName) {
            case "fiveClass":
                return Optional.ofNullable(FiveClassEnum.getByValue(String.valueOf(value.getValue()))).map(FiveClassEnum::getDisplay).orElse("");
            case "identificationDate":
                return DateUtil.format(DateUtil.parseDate(String.valueOf(value.getValue())), DatePattern.NORM_DATE_PATTERN);
            default:
                return Objects.nonNull(value.getValue()) ? String.valueOf(value.getValue()) : "";
        }
    }

    public void cancelRemove(FiveClassRemoveREQ req) {
        CrFiveClassDraft existData = crFiveClassDraftService.getById(req.getId());
        if (Objects.isNull(existData)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        if (!ReportState.TO_BE_REPORT.name().equals(existData.getReportState())) {
            throw new MithrasException("非待报送状态的数据不允许修改");
        }
        if (ApprovalStatus.UNDER_APPROVAL.name().equals(existData.getApprovalStatus()) && !procHelper.canEditData()) {
            throw new MithrasException("该条数据处于审批中，请撤回后再修改！");
        }
        CrModifyDataSnap modifyDataSnap = modifyDataSnapMapper.selectOne(Wrappers.<CrModifyDataSnap>lambdaQuery()
                .eq(CrModifyDataSnap::getBusinessKey, req.getBusinessKey())
                .eq(CrModifyDataSnap::getIsTakeEffect, YesOrNoNumberEnum.YES.getCode())
                .eq(CrModifyDataSnap::getTableType, TableTypeEnum.FIVE_CLASS.name())
                .eq(CrModifyDataSnap::getLabel, DataShowTypeEnum.REMOVE.name())
                .eq(CrModifyDataSnap::getVersionType, VersionTypeConstants.NORMAL)
                .orderByDesc(CrModifyDataSnap::getVersion)
                .last(StringUtil.mysqlLimitOne()));

        if (ObjectUtil.isNull(modifyDataSnap)) {
            throw new MithrasException("非法操作， 该数据未进行过删除操作");
        }
        String version;
        if (ObjectUtil.isNull(modifyDataSnap)) {
            version = ReportBizUtil.getVersion("");
        } else {
            version = ReportBizUtil.getVersion(modifyDataSnap.getVersion());
        }

        SpringContextHolder.getBean(CrModifyDataSnapService.class).lambdaUpdate()
                .eq(CrModifyDataSnap::getId, modifyDataSnap.getId())
                .set(CrModifyDataSnap::getIsTakeEffect, YesOrNoNumberEnum.NO.getCode())
                .update();

        modifyDataSnapMapper.insert(CrModifyDataSnap.builder()
                .label(DataShowTypeEnum.CANCEL_REMOVE.name())
                .businessKey(existData.getBusinessKey())
                .isTakeEffect(YesOrNoNumberEnum.NO.getCode())
                .tableType(TableTypeEnum.FIVE_CLASS.name())
                //版本号格式 00xxyyyyMMdd
                .version(version)
                .versionType(VersionTypeConstants.NORMAL)
                .build());
    }
}
