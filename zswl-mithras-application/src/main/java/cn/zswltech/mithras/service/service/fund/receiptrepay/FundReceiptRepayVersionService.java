package cn.zswltech.mithras.service.service.fund.receiptrepay;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.lang.Pair;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.zswltech.flow.core.api.FlowProcessApiService;
import cn.zswltech.flow.core.api.FlowTaskApiService;
import cn.zswltech.flow.core.domain.req.StartProcessReq;
import cn.zswltech.flow.core.domain.req.task.ProcessPageReq;
import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.flow.core.enums.ProcessBusinessStatusEnum;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.gruul.dao.dal.entity.OrgDO;
import cn.zswltech.gruul.dao.dal.vo.AccountVO;
import cn.zswltech.mithras.dto.fund.financing.pledge.FundFinancingPledgeListREQ;
import cn.zswltech.mithras.dto.fund.financing.pledge.FundFinancingPledgeListRSP;
import cn.zswltech.mithras.dto.fund.receiptrepay.version.*;
import cn.zswltech.mithras.dto.version.CommonVersionDiffBO;
import cn.zswltech.mithras.dto.version.CommonVersionDiffRSP;
import cn.zswltech.mithras.dto.version.CommonVersionListRSP;
import cn.zswltech.mithras.dto.version.DiffValue;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.service.constant.VersionTypeConstants;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.enums.JobEnum;
import cn.zswltech.mithras.workflow.application.flow.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.service.enums.VersionTypeEnum;
import cn.zswltech.mithras.fund.domain.enums.DirectFinancingType;
import cn.zswltech.mithras.fund.domain.enums.financing.FinancingTypeEnum;
import cn.zswltech.mithras.fund.domain.enums.financing.FundFinancingBizTypeEnum;
import cn.zswltech.mithras.fund.domain.enums.receiptrepay.CashFlowState;
import cn.zswltech.mithras.fund.domain.enums.receiptrepay.CreateBatchType;
import cn.zswltech.mithras.fund.domain.enums.receiptrepay.FundReceiptRepayInfoModule;
import cn.zswltech.mithras.workflow.application.flow.enums.ProcessState;
import cn.zswltech.mithras.fund.excel.exporter.FundReceiptRepayBatchPlanExcelExporter;
import cn.zswltech.mithras.fund.excel.model.FundReceiptRepayBatchPlanExcelModel;
import cn.zswltech.mithras.service.fund.direct.entity.FundDirectFinancingBaseInfo;
import cn.zswltech.mithras.service.fund.direct.entity.FundDirectFinancingPledgeInfo;
import cn.zswltech.mithras.service.fund.direct.service.FundDirectFinancingBaseInfoService;
import cn.zswltech.mithras.service.fund.direct.service.FundDirectFinancingPledgeInfoService;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.receiptrepay.FundReceiptRepayBaseInfoMapper;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.receiptrepay.FundReceiptRepayBatchMapper;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.receiptrepay.FundReceiptRepayBorrowingMapper;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.receiptrepay.FundReceiptRepayCashFlowMapper;
import cn.zswltech.mithras.service.mapper.lib.CommonVersionMapper;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.lib.financing.FundFinancingBaseInfoLibMapper;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.lib.financing.FundFinancingPledgeInfoLibMapper;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.lib.receiptrepay.FundReceiptRepayBaseInfoLibMapper;
import cn.zswltech.mithras.service.mapper.model.CommonVersion;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.model.FundOrganization;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.model.financing.FundFinancingBaseInfo;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.model.financing.FundFinancingBaseInfoLib;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.model.financing.FundFinancingPledgeInfoLib;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.model.receiptrepay.*;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.others.Util;
import cn.zswltech.mithras.workflow.application.process.BizProcessDataService;
import cn.zswltech.mithras.system.user.SysUserService;
import cn.zswltech.mithras.fund.application.FundOrganizationService;
import cn.zswltech.mithras.service.service.fund.financing.FundFinancingBaseInfoService;
import cn.zswltech.mithras.service.service.fund.financing.FundFinancingPledgeInfoService;
import cn.zswltech.mithras.service.service.lib.CommonVersionService;
import cn.zswltech.mithras.fund.application.receiptrepay.FundReceiptRepayPlanService;
import cn.zswltech.mithras.fund.application.lib.receiptrepay.handler.AbstractFundReceiptRepayLibHandler;
import cn.zswltech.mithras.service.util.CompareUtil;
import cn.zswltech.mithras.service.util.LongUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.collect.Sets;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static cn.zswltech.mithras.service.util.CompareUtil.MODULE_CHANGED_FLAG_KEY;

/**
 * 资金收付款审批相关service
 *
 * @author wangchuanhao
 * @date 2023/2/20 10:55 AM
 */
@Service
public class FundReceiptRepayVersionService extends CommonVersionService<FundReceiptRepayBaseInfo> {

    @Resource
    private FlowTaskApiService taskApiService;
    @Resource
    private FlowProcessApiService processApiService;
    @Resource
    private FundReceiptRepayPlanService fundReceiptRepayPlanService;
    @Resource
    private FundReceiptRepayBatchMapper fundReceiptRepayBatchMapper;
    @Resource
    private FundReceiptRepayBaseInfoMapper fundReceiptRepayBaseInfoMapper;
    @Resource
    private FundReceiptRepayBaseInfoLibMapper fundReceiptRepayBaseInfoLibMapper;
    @Resource
    private BizProcessDataService bizProcessDataService;
    @Resource
    private FundReceiptRepayStateService fundReceiptRepayStateService;
    @Resource
    private List<AbstractFundReceiptRepayLibHandler> libHandlerList;
    @Resource
    private CommonVersionMapper commonVersionMapper;
    @Resource
    private FundReceiptRepayBatchPlanExcelExporter fundReceiptRepayBatchPlanExcelExporter;
    @Resource
    private SysUserService sysUserService;
    @Resource
    private FundReceiptRepayCashFlowMapper fundReceiptRepayCashFlowMapper;
    @Resource
    private FundReceiptRepayBorrowingMapper fundReceiptRepayBorrowingMapper;
    @Resource
    private FundFinancingPledgeInfoLibMapper fundFinancingPledgeInfoLibMapper;
    @Resource
    private FundDirectFinancingPledgeInfoService fundDirectFinancingPledgeInfoService;
    @Resource
    private FundFinancingBaseInfoLibMapper fundFinancingBaseInfoLibMapper;
    @Resource
    private FundFinancingPledgeInfoService fundFinancingPledgeInfoService;
    @Resource
    private FundOrganizationService organizationService;
    @Resource
    private FundFinancingBaseInfoService financingBaseInfoService;
    @Resource
    private FundDirectFinancingBaseInfoService directFinancingBaseInfoService;

    @Override
    public void customFlushData(FundReceiptRepayBaseInfo fundReceiptRepayBaseInfo, String version, boolean needClearLastFlag, Integer versionType) {
        // 处理抄表逻辑
        for (AbstractFundReceiptRepayLibHandler libHandler : libHandlerList) {
            libHandler.flushData(version, fundReceiptRepayBaseInfo.getId(), needClearLastFlag, versionType);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void customReset(FundReceiptRepayBaseInfo fundReceiptRepayBaseInfo, CommonVersion commonVersion) {
        for (AbstractFundReceiptRepayLibHandler libHandler : libHandlerList) {
            libHandler.reset(fundReceiptRepayBaseInfo.getId(), commonVersion.getVersion());
        }
    }

    @Override
    public CommonVersionDiffRSP doCompare(CommonVersion newVersion, CommonVersion oldVersion) {
        FundReceiptRepayBaseInfo baseInfo = fundReceiptRepayBaseInfoMapper.selectById(newVersion.getMainId());
        if (Objects.isNull(baseInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        CommonVersionDiffRSP versionDiffRSP = new CommonVersionDiffRSP();
        Map<String, List> oldData = new HashMap<>();
        Map<String, List<Map<String, DiffValue>>> newData = new HashMap<>();
        Map<String, Boolean> moduleChanged = new HashMap<>();
        for (AbstractFundReceiptRepayLibHandler libHandler : libHandlerList) {
            CommonVersionDiffBO commonVersionDiffBO = libHandler.libCompareLib(newVersion, oldVersion);
            oldData.put(libHandler.getSubModule().name(), commonVersionDiffBO.getBeforeData());
            newData.put(libHandler.getSubModule().name(), commonVersionDiffBO.getAfterData());
            moduleChanged.put(libHandler.getSubModule().name(), commonVersionDiffBO.getModuleChanged());
        }
        // 需要加上质押措施 单独处理
        CommonVersionDiffBO pledgeDiffBO = calPledgeDiffBO(newVersion, oldVersion);
        oldData.put(FundReceiptRepayInfoModule.PLEDGE.name(), pledgeDiffBO.getBeforeData());
        newData.put(FundReceiptRepayInfoModule.PLEDGE.name(), pledgeDiffBO.getAfterData());
        moduleChanged.put(FundReceiptRepayInfoModule.PLEDGE.name(), pledgeDiffBO.getModuleChanged());

        versionDiffRSP.setOldData(oldData);
        versionDiffRSP.setNewData(newData);
        versionDiffRSP.setModuleChanged(moduleChanged);
        return versionDiffRSP;
    }

    @Override
    protected CommonVersionListRSP convertPageRsp(CommonVersion cv, FundReceiptRepayBaseInfo baseModel, Map<Long, String> userNameMap) {
        CommonVersionListRSP rsp = BeanUtil.copyProperties(cv, CommonVersionListRSP.class);
        return rsp;
    }

    @Override
    public BusinessModuleEnum getBusinessModule() {
        return BusinessModuleEnum.FUND_RECEIPT_REPAY;
    }

    /**
     * 寻找关联的流程
     *
     * @param fundReceiptRepayId
     * @return
     */
    public ProcessResp findRelatedProcess(Long fundReceiptRepayId) {
        // 单体流程查询
        ProcessPageReq singleProcessReq = new ProcessPageReq();
        singleProcessReq.setPageIndex(1);
        singleProcessReq.setPageSize(1);
        singleProcessReq.setBusinessKey(String.valueOf(fundReceiptRepayId));
        singleProcessReq.setModelKeyList(BusinessModuleEnum.FUND_RECEIPT_REPAY.getModelKeyList());
        singleProcessReq.setProcessStatusList(Arrays.asList(ProcessBusinessStatusEnum.RUNNING.getType(), ProcessBusinessStatusEnum.SUSPEND.getType()));
        cn.zswltech.flow.core.util.Page<ProcessResp> singleProcessRespPage = taskApiService.queryProcess(singleProcessReq);
        if (CollectionUtils.isNotEmpty(singleProcessRespPage.getContents())) {
            return singleProcessRespPage.getContents().stream().findFirst().orElse(null);
        }

        // 批量流程查询
        List<Long> batchIdList = fundReceiptRepayPlanService.list(Wrappers.<FundReceiptRepayPlan>lambdaQuery()
                .select(FundReceiptRepayPlan::getBatchId)
                .eq(FundReceiptRepayPlan::getReceiptRepayId, fundReceiptRepayId)
        ).stream().map(FundReceiptRepayPlan::getBatchId).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(batchIdList)) {
            return null;
        }
        return findBatchProcess(batchIdList, Arrays.asList(ProcessBusinessStatusEnum.RUNNING.getType(), ProcessBusinessStatusEnum.SUSPEND.getType()));
    }

    /**
     * 寻找批次关联的流程
     *
     * @param batchIdList
     * @return
     */
    public ProcessResp findBatchProcess(List<Long> batchIdList, @Nullable List<Integer> statusList) {
        if (CollectionUtils.isEmpty(batchIdList)) {
            return null;
        }
        ProcessPageReq batchProcessReq = new ProcessPageReq();
        batchProcessReq.setPageIndex(1);
        batchProcessReq.setPageSize(1);
        batchProcessReq.setBusinessKeyList(batchIdList.stream().map(String::valueOf).collect(Collectors.toList()));
        batchProcessReq.setModelKeyList(BusinessModuleEnum.BATCH_FUND_RECEIPT_REPAY.getModelKeyList());
        if (CollectionUtils.isNotEmpty(statusList)) {
            batchProcessReq.setProcessStatusList(statusList);
        }
        cn.zswltech.flow.core.util.Page<ProcessResp> batchProcessRespPage = taskApiService.queryProcess(batchProcessReq);
        return batchProcessRespPage.getContents().stream().findFirst().orElse(null);
    }

    /**
     * 单个审批提交流程
     *
     * @param id
     */
    @Transactional(rollbackFor = Exception.class)
    public void submit(Long id) {
        FundReceiptRepayBaseInfo baseInfo = fundReceiptRepayBaseInfoMapper.selectById(id);
        checkCanHandleVersion(id, baseInfo);

        OrgDO orgDO = sysUserService.getUserDept();
        if (Objects.isNull(orgDO)) {
            throw new MithrasException("用户部门为空");
        }
        Long bizDeptLeaderId = sysUserService.getUserIdByOrgJob(orgDO.getId(), JobEnum.businesshead.name());

        StartProcessReq startProcessReq = new StartProcessReq();
        startProcessReq.setModelKey(ProcessModelTypeEnum.FundReceiptRepayFlow.name());
        startProcessReq.setVariables(MapUtil.of(
                Pair.of("bizDeptLeader", Objects.nonNull(bizDeptLeaderId) ? ListUtil.toList(String.valueOf(bizDeptLeaderId)) : new ArrayList<>())
        ));
        startProcessReq.setStartUserId(Optional.ofNullable(AccountUtil.getLoginInfo())
                .map(AccountVO::getId)
                .map(String::valueOf)
                .orElseThrow(() -> new MithrasException(ResultMsg.USER_NOT_LOGIN)));
        startProcessReq.setBusinessKey(String.valueOf(id));
        startProcessReq.setSubModule("DEFAULT");
        if (Objects.equals(baseInfo.getFinancingType(), "DIRECT")) {
            // 直融查询直融的信息
            FundDirectFinancingBaseInfo fundDirectFinancingBaseInfo = SpringUtil.getBean(FundDirectFinancingBaseInfoService.class).getById(baseInfo.getFinancingId());
            if (Objects.isNull(fundDirectFinancingBaseInfo)) {
                throw new MithrasException("对应融资信息不存在");
            }
            DirectFinancingType directFinancingType = DirectFinancingType.findByName(fundDirectFinancingBaseInfo.getDirectFinancingType());
            startProcessReq.setProcessInstanceName(String.format("【%s】%s付款审批", fundDirectFinancingBaseInfo.getProductName(), Optional.ofNullable(directFinancingType).map(DirectFinancingType::display).orElse("")));
        } else {
            FundFinancingBaseInfo financingBaseInfo = SpringUtil.getBean(FundFinancingBaseInfoService.class).getById(baseInfo.getFinancingId());
            if (Objects.isNull(financingBaseInfo)) {
                throw new MithrasException("对应融资信息不存在");
            }
            FundFinancingBizTypeEnum fundFinancingBizTypeEnum = FundFinancingBizTypeEnum.finaByName(financingBaseInfo.getBusinessType());
            List<FundOrganization> organizationList = organizationService.getByFinancingId(financingBaseInfo.getId());
            // 间融查询间融的信息
            startProcessReq.setProcessInstanceName(String.format("【%s】%s%s月份付款审批", Optional.ofNullable(organizationList.get(0)).map(FundOrganization::getOrganizationName).orElse(null), Optional.ofNullable(fundFinancingBizTypeEnum).map(FundFinancingBizTypeEnum::getDisplay).orElse(""), LocalDate.now().getMonthValue()));
        }
        startProcessReq.setStartUserDeptId(String.valueOf(orgDO.getId()));
        String processInstanceId = processApiService.start(startProcessReq);
        bizProcessDataService.recordBizData(processInstanceId, null);

        // 更新付款、收款状态
        fundReceiptRepayStateService.handleSubmitState(ListUtil.toList(id));

        // 更新流程状态为审批中
        fundReceiptRepayStateService.updateProcessState(ListUtil.toList(id), ProcessState.COMMIT);
    }

    /**
     * 创建批次
     *
     * @param req
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public Long createBatch(CreateBatchREQ req) {
        LocalDate curMonthLastDay = LocalDate.now().with(TemporalAdjusters.lastDayOfMonth());
        FundReceiptRepayBatch batch = new FundReceiptRepayBatch();
        fundReceiptRepayBatchMapper.insert(batch);
        List<FundReceiptRepayPlan> planList = new ArrayList<>();
        if (CreateBatchType.BATCH.name().equals(req.getBatchType())) {
            // 手动勾选 去重
            List<Long> receiptIdList = req.getReceiptIdList().stream().distinct().collect(Collectors.toList());
            for (Long receiptId : receiptIdList) {
                FundReceiptRepayPlan plan = new FundReceiptRepayPlan();
                plan.setBatchId(batch.getId());
                plan.setReceiptRepayId(receiptId);
                if (Objects.nonNull(req.getRepayMonth())) {
                    plan.setRepayYear(req.getRepayMonth().getYear());
                    plan.setRepayMonth(req.getRepayMonth().getMonthValue());
                }
                planList.add(plan);
            }
        } else {
            // 自动生成 查找本月待提交审批的数据 未付款
            List<FundReceiptRepayCashFlow> noWriteOffCashFlowList = fundReceiptRepayCashFlowMapper.selectList(Wrappers.<FundReceiptRepayCashFlow>lambdaQuery()
                    .eq(FundReceiptRepayCashFlow::getWriteOffState, CashFlowState.NO_WRITE_OFF.name())
                    .le(FundReceiptRepayCashFlow::getRepayDate, curMonthLastDay)
            );
            if (CollectionUtils.isEmpty(noWriteOffCashFlowList)) {
                throw new MithrasException("本月暂无待还款数据，无需自动还款");
            }
            Set<Long> noWriteOffReceiptIdSet = noWriteOffCashFlowList.stream().map(FundReceiptRepayCashFlow::getReceiptRepayId).collect(Collectors.toSet());
            for (Long receiptId : noWriteOffReceiptIdSet) {
                FundReceiptRepayPlan plan = new FundReceiptRepayPlan();
                plan.setBatchId(batch.getId());
                plan.setReceiptRepayId(receiptId);
                planList.add(plan);
            }
        }
        fundReceiptRepayPlanService.saveBatch(planList);
        return batch.getId();
    }

    /**
     * 查询某个批次下的付款列表
     *
     * @param batchId
     * @param receiptIdList 不为空的话 做过滤
     * @return
     */
    public List<BatchReceiptListRSP> batchReceiptRspList(Long batchId, @Nullable List<Long> receiptIdList) {
        List<FundReceiptRepayPlan> planList = batchReceiptPlanList(batchId, receiptIdList, false);
        List<Long> receiptRepayIdList = planList.stream().map(FundReceiptRepayPlan::getReceiptRepayId).collect(Collectors.toList());
        Map<Long, FundReceiptRepayBaseInfo> repayFinancingMap = fundReceiptRepayBaseInfoMapper.selectBatchIds(receiptRepayIdList).stream()
                .collect(Collectors.toMap(FundReceiptRepayBaseInfo::getId, Function.identity()));
        Map<Long, List<FundOrganization>> orgMap = organizationService.getBatchByFinancingId(repayFinancingMap.values().stream().map(FundReceiptRepayBaseInfo::getFinancingId).collect(Collectors.toList()));
        Map<Long, String> financingCodeMap = financingBaseInfoService.listByIds(repayFinancingMap.values().stream().map(FundReceiptRepayBaseInfo::getFinancingId).collect(Collectors.toList())).stream()
                .collect(Collectors.toMap(FundFinancingBaseInfo::getId, FundFinancingBaseInfo::getFinancingCode));
        Map<Long, FundDirectFinancingBaseInfo> directFinancingMap = directFinancingBaseInfoService.listByIds(repayFinancingMap.values().stream().map(FundReceiptRepayBaseInfo::getFinancingId).collect(Collectors.toList())).stream()
                .collect(Collectors.toMap(FundDirectFinancingBaseInfo::getId, Function.identity()));

        List<BatchReceiptListRSP> rspList = planList.stream().map(p -> {
            BatchReceiptListRSP rsp = BeanUtil.copyProperties(p, BatchReceiptListRSP.class);
            FundReceiptRepayBaseInfo repayBaseInfo = repayFinancingMap.get(p.getReceiptRepayId());
            Long financingId = repayBaseInfo.getFinancingId();
            if(Objects.equals(repayBaseInfo.getFinancingType(), FinancingTypeEnum.DIRECT.name())){
                FundDirectFinancingBaseInfo directFinancingBaseInfo = directFinancingMap.get(financingId);
                rsp.setFinancingOrgName(Collections.singletonList(directFinancingBaseInfo.getProductName()));
                rsp.setFinancingCode(directFinancingBaseInfo.getFinancingCode());
            }else {
                List<FundOrganization> organizationList = orgMap.getOrDefault(financingId, Collections.emptyList());
                rsp.setFinancingOrgId(organizationList.stream().map(FundOrganization::getId).collect(Collectors.toList()));
                rsp.setFinancingOrgName(organizationList.stream().map(FundOrganization::getOrganizationName).collect(Collectors.toList()));
                rsp.setFinancingCode(financingCodeMap.get(financingId));
            }
            rsp.setId(p.getReceiptRepayId());
            rsp.setPledgeContractCodeList(JSON.parseArray(p.getPledgeContracCodeJson(), String.class));
            rsp.setProjNameList(JSON.parseArray(p.getSupportingProjectJson(), String.class));

            return rsp;
        }).collect(Collectors.toList());
        return rspList;
    }

    /**
     * 查找批次对应的数据
     *
     * @param batchId
     * @param receiptIdList
     * @param forceEditDataFlag 强制取编辑区数据
     * @return
     */
    public List<FundReceiptRepayPlan> batchReceiptPlanList(Long batchId, @Nullable List<Long> receiptIdList, boolean forceEditDataFlag) {
        // 判断有没有流程、流程有没有结束
        ProcessResp processResp = findBatchProcess(ListUtil.toList(batchId), null);
        List<FundReceiptRepayPlan> planList = fundReceiptRepayPlanService.list(Wrappers.<FundReceiptRepayPlan>lambdaQuery()
                .eq(FundReceiptRepayPlan::getBatchId, batchId)
                .in(CollectionUtils.isNotEmpty(receiptIdList), FundReceiptRepayPlan::getReceiptRepayId, receiptIdList)
        );
        List<Long> needHandleReceiptIdList = planList.stream().map(FundReceiptRepayPlan::getReceiptRepayId).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(planList)) {
            return planList;
        }
        if (!forceEditDataFlag && Objects.nonNull(processResp) && !ProcessBusinessStatusEnum.RUNNING.getType().equals(processResp.getProcessStatus())) {
            // 流程已结束直接用快照返回
            return planList;
        }

        // 没发起流程或流程未结束 用编辑区数据计算
//        LocalDate curMonthLastDay = LocalDate.now().with(TemporalAdjusters.lastDayOfMonth());
//        LocalDate curMonthFirstDay = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth());

        Map<Long, FundReceiptRepayBaseInfo> baseInfoMap = fundReceiptRepayBaseInfoMapper.selectBatchIds(planList.stream().map(FundReceiptRepayPlan::getReceiptRepayId).collect(Collectors.toList()))
                .stream().collect(Collectors.toMap(FundReceiptRepayBaseInfo::getId, b -> b));
        // 间接融资的financingId， 添加-1为了防止后面的sql报错
        List<Long> needHandleFinancingIdList = baseInfoMap.values().stream().filter(v -> !"DIRECT".equals(v.getFinancingType()))
                .map(FundReceiptRepayBaseInfo::getFinancingId).collect(Collectors.toList());
        needHandleFinancingIdList.add(-1L);
        //直接融资的financingId
        List<Long> needHandleDirectFinancingIdList = baseInfoMap.values().stream().filter(v -> "DIRECT".equals(v.getFinancingType()))
                .map(FundReceiptRepayBaseInfo::getFinancingId).collect(Collectors.toList());
        needHandleDirectFinancingIdList.add(-1L);

        // 首付款资金流入记录
        Map<Long, FundReceiptRepayBorrowing> directBorrowingMap = fundReceiptRepayBorrowingMapper.selectList(
                        Wrappers.<FundReceiptRepayBorrowing>lambdaQuery()
                                .in(FundReceiptRepayBorrowing::getReceiptRepayId, baseInfoMap.keySet()))
                .stream().collect(Collectors.toMap(FundReceiptRepayBorrowing::getReceiptRepayId, b -> b, (k1, k2) -> k1));

        // 付款的还款表数据
        Map<Long, List<FundReceiptRepayCashFlow>> cashFlowMap = fundReceiptRepayCashFlowMapper.selectList(Wrappers.<FundReceiptRepayCashFlow>lambdaQuery().in(FundReceiptRepayCashFlow::getReceiptRepayId, needHandleReceiptIdList))
                .stream().collect(Collectors.groupingBy(FundReceiptRepayCashFlow::getReceiptRepayId));
        // 找出这些付款 对应的 最新版本的融资 的基本信息列表
        List<Long> financingBaseInfoLibIdList = fundFinancingBaseInfoLibMapper
                .queryLastestVersionLibIdList(needHandleFinancingIdList);
        financingBaseInfoLibIdList.add(-1L);
        Map<Long, FundFinancingBaseInfoLib> financingBaseInfoMap = fundFinancingBaseInfoLibMapper
                .selectBatchIds(financingBaseInfoLibIdList).stream()
                .collect(Collectors.toMap(FundFinancingBaseInfoLib::getOriginId, f -> f, (k1, k2) -> k1));
        // 间接融资付款对应的 最新版本的融资 的质押措施列表
        List<Long> pledgeLibIdList = fundFinancingPledgeInfoLibMapper.queryLastestVersionLibIdList(needHandleFinancingIdList);
        pledgeLibIdList.add(-1L);
        Map<Long, List<FundFinancingPledgeInfoLib>> pledgeLibMap = CollectionUtils.isEmpty(pledgeLibIdList) ? new HashMap<>() : fundFinancingPledgeInfoLibMapper.selectBatchIds(pledgeLibIdList).stream().collect(Collectors.groupingBy(FundFinancingPledgeInfoLib::getFinancingId));

        // 直接接融资付款对应的质押项目列表
        Map<Long, List<FundDirectFinancingPledgeInfo>> directPledgeLibMap = fundDirectFinancingPledgeInfoService.list(
                        Wrappers.<FundDirectFinancingPledgeInfo>lambdaQuery()
                                .in(FundDirectFinancingPledgeInfo::getFinancingId, needHandleDirectFinancingIdList))
                .stream().collect(Collectors.groupingBy(FundDirectFinancingPledgeInfo::getFinancingId));

        for (FundReceiptRepayPlan plan : planList) {
            LocalDate targetDate = LocalDate.now();
            if (Objects.nonNull(plan.getRepayYear()) && Objects.nonNull(plan.getRepayMonth())) {
                targetDate = LocalDate.of(plan.getRepayYear(), plan.getRepayMonth(), 1);
            }
            LocalDate curMonthLastDay = targetDate.with(TemporalAdjusters.lastDayOfMonth());
            LocalDate curMonthFirstDay = targetDate.with(TemporalAdjusters.firstDayOfMonth());
            // 各模块取值 我醉了，之后如果要做搜索完全没法做
            FundReceiptRepayBaseInfo baseInfo = baseInfoMap.get(plan.getReceiptRepayId());
            List<FundReceiptRepayCashFlow> cashFlowList = cashFlowMap.getOrDefault(plan.getReceiptRepayId(), new ArrayList<>());

            // 塞值
            plan.setReceiptRepayCode(baseInfo.getReceiptRepayCode());
            if ("DIRECT".equals(baseInfo.getFinancingType())) {
//                plan.setFinancingOrgName(baseInfo.getFinancingChannel());
                FundReceiptRepayBorrowing borrowing = directBorrowingMap.get(plan.getReceiptRepayId());
                if (ObjectUtil.isNotEmpty(borrowing)) {
                    plan.setBorrowingDate(borrowing.getActualLoanDate());
                }
                List<FundReceiptRepayCashFlow> cashFlows = cashFlowMap.get(plan.getReceiptRepayId());
                if (ObjectUtil.isNotEmpty(cashFlows)) {
                    cashFlows.sort(Comparator.comparing(FundReceiptRepayCashFlow::getRepayDate));
                    plan.setExpirationDate(cashFlows.get(cashFlows.size() - 1).getRepayDate());
                }
                List<FundDirectFinancingPledgeInfo> directPledge = directPledgeLibMap.getOrDefault(baseInfo.getFinancingId(), new ArrayList<>());
                plan.setSupportingProjectJson(JSON.toJSONString(directPledge.stream().map(FundDirectFinancingPledgeInfo::getProjName).collect(Collectors.toList())));
                plan.setPledgeContracCodeJson(JSON.toJSONString(directPledge.stream().map(FundDirectFinancingPledgeInfo::getPledgeCode).collect(Collectors.toList())));

            } else {
                FundFinancingBaseInfoLib financingBaseInfoLib = financingBaseInfoMap.get(baseInfo.getFinancingId());
//                plan.setFinancingOrgName(financingBaseInfoLib.getOrganizationNames());
                plan.setBorrowingDate(financingBaseInfoLib.getActualLoanDate());
                plan.setExpirationDate(financingBaseInfoLib.getActualExpireDate());

                List<FundFinancingPledgeInfoLib> pledgeInfoLibList = pledgeLibMap.getOrDefault(baseInfo.getFinancingId(), new ArrayList<>());
                plan.setSupportingProjectJson(JSON.toJSONString(pledgeInfoLibList.stream().map(FundFinancingPledgeInfoLib::getProjName).collect(Collectors.toList())));
                plan.setPledgeContracCodeJson(JSON.toJSONString(pledgeInfoLibList.stream().map(FundFinancingPledgeInfoLib::getPledgeCode).collect(Collectors.toList())));
            }
//            plan.setFinancingCode(plan.getFinancingCode());
            plan.setFinancingAmount(baseInfo.getFinancingAmount());
            plan.setPaidPrincipal(cashFlowList.stream().filter(c -> CashFlowState.WRITTEN_OFF.name().equals(c.getWriteOffState())).mapToLong(s -> LongUtil.null2zero(s.getPrincipleAmount())).sum());
            plan.setPaidInterest(cashFlowList.stream().filter(c -> CashFlowState.WRITTEN_OFF.name().equals(c.getWriteOffState())).mapToLong(s -> LongUtil.null2zero(s.getInterestAmount())).sum());

            // 本月计划金额 = 本月底前的金额 && 不是核销完毕 + 审批通过时间在本月内 && 核销完毕
            plan.setPlanedRepayPrincipal(cashFlowList.stream()
                    .filter(c -> {
//                        if (!CashFlowState.WRITTEN_OFF.name().equals(c.getWriteOffState())) {
//                            return Util.dateLe(c.getRepayDate(), curMonthLastDay);
//                        } else if (Objects.nonNull(c.getApprovalPassDate())) {
//                            return Util.dateLe(c.getRepayDate(), curMonthLastDay) && Util.dateGe(c.getRepayDate(), curMonthFirstDay);
//                        }
//                        return false;
                        return !c.getRepayDate().isBefore(curMonthFirstDay) && !c.getRepayDate().isAfter(curMonthLastDay);
                    })
                    .mapToLong(s -> LongUtil.null2zero(s.getPrincipleAmount())).sum());
            plan.setPlanedRepayInterest(cashFlowList.stream()
                    .filter(c -> {
//                        if (!CashFlowState.WRITTEN_OFF.name().equals(c.getWriteOffState())) {
//                            return Util.dateLe(c.getRepayDate(), curMonthLastDay);
//                        } else if (Objects.nonNull(c.getApprovalPassDate())) {
//                            return Util.dateLe(c.getRepayDate(), curMonthLastDay) && Util.dateGe(c.getRepayDate(), curMonthFirstDay);
//                        }
//                        return false;
                        return !c.getRepayDate().isBefore(curMonthFirstDay) && !c.getRepayDate().isAfter(curMonthLastDay);
                    })
                    .mapToLong(s -> LongUtil.null2zero(s.getInterestAmount())).sum());
            plan.setPlanedRepayAmount(plan.getPlanedRepayPrincipal() + plan.getPlanedRepayInterest());
            plan.setPlanedRepayPrincipleDate(cashFlowList.stream().filter(c -> Util.dateLe(c.getRepayDate(), curMonthLastDay) && LongUtil.null2zero(c.getPrincipleAmount()) > 0).sorted(Comparator.comparing(FundReceiptRepayCashFlow::getRepayDate).reversed()).findFirst().map(FundReceiptRepayCashFlow::getRepayDate).orElse(null));
            plan.setPlanedRepayInterestDate(cashFlowList.stream().filter(c -> Util.dateLe(c.getRepayDate(), curMonthLastDay) && LongUtil.null2zero(c.getInterestAmount()) > 0).sorted(Comparator.comparing(FundReceiptRepayCashFlow::getRepayDate).reversed()).findFirst().map(FundReceiptRepayCashFlow::getRepayDate).orElse(null));
        }
        return planList;
    }

    /**
     * 批量提交审批
     *
     * @param req
     */
    @Transactional(rollbackFor = Exception.class)
    public void batchSubmit(BatchSubmitREQ req) {
        FundReceiptRepayBatch batch = fundReceiptRepayBatchMapper.selectById(req.getBatchId());
        if (Objects.isNull(batch)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        if (Objects.nonNull(findBatchProcess(ListUtil.toList(req.getBatchId()), null))) {
            throw new MithrasException("该批次已存在审批流程，不可重复提交审批");
        }

        Map<Long, FundReceiptRepayBaseInfo> baseInfoMap = fundReceiptRepayBaseInfoMapper.selectBatchIds(req.getReceiptIdList()).stream().collect(Collectors.toMap(FundReceiptRepayBaseInfo::getId, f -> f));
        // 校验选中要提交审批的数据是否包含在该批次内
        List<FundReceiptRepayPlan> planList = fundReceiptRepayPlanService.list(Wrappers.<FundReceiptRepayPlan>lambdaQuery().eq(FundReceiptRepayPlan::getBatchId, req.getBatchId()));
        Set<Long> planReceiptRepayIdSet = planList.stream().map(FundReceiptRepayPlan::getReceiptRepayId).collect(Collectors.toSet());
        for (Long receiptRepayId : req.getReceiptIdList()) {
            if (!planReceiptRepayIdSet.contains(receiptRepayId)) {
                throw new MithrasException(String.format("付款【%s】不存在该批次中，不可一起提交审批", receiptRepayId));
            }
        }

        // 单条数据是否在流程内的校验
        for (Long receiptRepayId : req.getReceiptIdList()) {
            checkCanHandleVersion(receiptRepayId, baseInfoMap.get(receiptRepayId));
        }

        OrgDO orgDO = sysUserService.getUserDept();
        if (Objects.isNull(orgDO)) {
            throw new MithrasException("用户部门为空");
        }
        Long bizDeptLeaderId = sysUserService.getUserIdByOrgJob(orgDO.getId(), JobEnum.businesshead.name());

        StartProcessReq startProcessReq = new StartProcessReq();
        startProcessReq.setModelKey(ProcessModelTypeEnum.BatchFundReceiptRepayFlow.name());
        startProcessReq.setVariables(MapUtil.of(
                Pair.of("bizDeptLeader", Objects.nonNull(bizDeptLeaderId) ? ListUtil.toList(String.valueOf(bizDeptLeaderId)) : new ArrayList<>())
        ));
        startProcessReq.setStartUserId(Optional.ofNullable(AccountUtil.getLoginInfo())
                .map(AccountVO::getId)
                .map(String::valueOf)
                .orElseThrow(() -> new MithrasException(ResultMsg.USER_NOT_LOGIN)));
        startProcessReq.setBusinessKey(String.valueOf(req.getBatchId()));
        startProcessReq.setSubModule("DEFAULT");
        startProcessReq.setProcessInstanceName(Objects.nonNull(req.getRepayMonth()) ? String.format("%s月份资金付款批量审批", req.getRepayMonth().getMonthValue()) :"资金付款批量审批");
        startProcessReq.setStartUserDeptId(String.valueOf(orgDO.getId()));
        String processInstanceId = processApiService.start(startProcessReq);
        bizProcessDataService.recordBizData(processInstanceId, null);


        // 删除该批次内曾经被选中，但没提交审批的数据
        Sets.SetView<Long> notSubmitReceiptIdSet = Sets.difference(planReceiptRepayIdSet, new HashSet<>(req.getReceiptIdList()));
        if (CollectionUtils.isNotEmpty(notSubmitReceiptIdSet)) {
            fundReceiptRepayPlanService.remove(Wrappers.<FundReceiptRepayPlan>lambdaQuery().eq(FundReceiptRepayPlan::getBatchId, req.getBatchId()).in(FundReceiptRepayPlan::getReceiptRepayId, notSubmitReceiptIdSet));
        }

        // 更新批次备注
        batch.setRemark(req.getRemark());
        fundReceiptRepayBatchMapper.updateById(batch);

        // 更新付款、还款状态
        fundReceiptRepayStateService.handleSubmitState(req.getReceiptIdList());

        // 更新流程状态
        fundReceiptRepayStateService.updateProcessState(req.getReceiptIdList(), ProcessState.COMMIT);
    }

    /**
     * operateCode 1 提交审批校验； 2 重置校验
     * 校验是否可提交审批
     * 在流程中不可提交
     * 已关闭不可提交
     *
     * @param receiptRepayId
     */
    public void checkCanHandleVersion(Long receiptRepayId, FundReceiptRepayBaseInfo baseInfo) {
        String noticeMsg = "不可提交审批";
        if (Objects.isNull(baseInfo)) {
            throw new MithrasException("付款数据不存在：" + receiptRepayId);
        }
        if (Objects.nonNull(findRelatedProcess(receiptRepayId))) {
            throw new MithrasException(String.format("【%s】已存在审批中的流程实例，%s", baseInfo.getReceiptRepayCode(), noticeMsg));
        }
    }

    /**
     * 某个批次的详情信息
     * 目前就是为了透出备注
     *
     * @param batchId
     * @return
     */
    public BatchDetailRSP batchDetail(Long batchId) {
        FundReceiptRepayBatch batch = fundReceiptRepayBatchMapper.selectById(batchId);
        if (Objects.isNull(batch)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        BatchDetailRSP rsp = BeanUtil.copyProperties(batch, BatchDetailRSP.class);
        return rsp;
    }

    /**
     * 单个审批的流程结束
     *
     * @param modelKey
     * @param receiptId
     * @param endType
     * @param startUserId
     * @param processInstanceId
     */
    @Transactional(rollbackFor = Exception.class)
    public void processEnd(String modelKey, Long receiptId, Integer endType, Long startUserId, String processInstanceId) {
        boolean processPass = ProcessBusinessStatusEnum.success(endType);
        // 更新流程状态
        ProcessState processState = null;
        if (processPass) {
            processState = ProcessState.PASS;
        } else {
            processState = ProcessBusinessStatusEnum.CANCEL.getType().equals(endType) ? ProcessState.CANCEL : ProcessState.REJECT;
        }
        if (processPass) {
            // 记录审批通过时间
            fundReceiptRepayStateService.recordApprovalPassDate(ListUtil.toList(receiptId));
        }
        fundReceiptRepayStateService.updateProcessState(ListUtil.toList(receiptId), processState);
        // 记录版本
        int versionType = processPass ? VersionTypeConstants.NORMAL : VersionTypeConstants.INVALID;
        this.recordVersion(receiptId, VersionTypeEnum.APPROVAL, startUserId, processInstanceId, versionType);
        // 如果审批失败 && 有一个有效的历史版本 则回滚
        if (!processPass) {
            long validVersionCount = commonVersionMapper.selectCount(Wrappers.<CommonVersion>lambdaQuery()
                    .eq(CommonVersion::getMainId, receiptId)
                    .eq(CommonVersion::getModule, BusinessModuleEnum.FUND_RECEIPT_REPAY.name())
                    .eq(CommonVersion::getVersionType, VersionTypeConstants.NORMAL));
            if (validVersionCount > 0) {
                reset(receiptId);
                fundReceiptRepayStateService.updateProcessState(ListUtil.toList(receiptId), processState);
            }
        }
//        // 更新状态
//        fundReceiptRepayStateService.handleProcessEndState(ListUtil.toList(receiptId), processPass);
//        // 更新费用
//        fundReceiptRepayStateService.processEndUpdateExpense(ListUtil.toList(receiptId), processPass);
    }

    /**
     * 批量审批的流程结束
     *
     * @param modelKey
     * @param batchId
     * @param endType
     * @param startUserId
     * @param processInstanceId
     */
    @Transactional(rollbackFor = Exception.class)
    public void batchProcessEnd(String modelKey, Long batchId, Integer endType, Long startUserId, String processInstanceId) {
        // 查询出批次对应的付款数据
        List<FundReceiptRepayPlan> planList = batchReceiptPlanList(batchId, null, true);
        List<Long> receiptIdList = planList.stream().map(FundReceiptRepayPlan::getReceiptRepayId).collect(Collectors.toList());
        List<Long> resetNeedUpdateProcessStateIdList = new ArrayList<>();
        boolean processPass = ProcessBusinessStatusEnum.success(endType);
        // 更新流程状态
        ProcessState processState = null;
        if (processPass) {
            processState = ProcessState.PASS;
        } else {
            processState = ProcessBusinessStatusEnum.CANCEL.getType().equals(endType) ? ProcessState.CANCEL : ProcessState.REJECT;
        }
        // 记录审批通过时间
        if (processPass) {
            fundReceiptRepayStateService.recordApprovalPassDate(receiptIdList);
        }
        fundReceiptRepayStateService.updateProcessState(receiptIdList, processState);

        int versionType = processPass ? VersionTypeConstants.NORMAL : VersionTypeConstants.INVALID;
        for (FundReceiptRepayPlan plan : planList) {
            // 该版本不记录审批流id
            String version = this.recordVersion(plan.getReceiptRepayId(), VersionTypeEnum.APPROVAL, startUserId, null, versionType);
            plan.setReceiptRepayVersion(version);

            // 如果审批失败 && 有一个有效的历史版本 则回滚
            if (!processPass) {
                long validVersionCount = commonVersionMapper.selectCount(Wrappers.<CommonVersion>lambdaQuery()
                        .eq(CommonVersion::getMainId, plan.getReceiptRepayId())
                        .eq(CommonVersion::getModule, BusinessModuleEnum.FUND_RECEIPT_REPAY.name())
                        .eq(CommonVersion::getVersionType, VersionTypeConstants.NORMAL));
                if (validVersionCount > 0) {
                    reset(plan.getReceiptRepayId());
                    // 回滚了，状态需要单独处理一遍
                    resetNeedUpdateProcessStateIdList.add(plan.getReceiptRepayId());
                }
            }
        }

//        // 更新状态
//        fundReceiptRepayStateService.handleProcessEndState(receiptIdList, processPass);
//        // 更新费用
//        fundReceiptRepayStateService.processEndUpdateExpense(receiptIdList, processPass);

        // 由于reset 重新更新编辑区的审批状态
        fundReceiptRepayStateService.updateProcessState(resetNeedUpdateProcessStateIdList, processState);

        // 记录批次快照
        fundReceiptRepayPlanService.updateBatchById(planList);

    }

    /**
     * 列表数据excel导出
     *
     * @param req
     * @param outputStream
     */
    public void batchReceiptDownload(BatchReceiptDownloadREQ req, ServletOutputStream outputStream) {
        List<FundReceiptRepayPlan> planList = batchReceiptPlanList(req.getBatchId(), req.getReceiptIdList(), false);
        Map<Long, Long> repayFinancingMap = fundReceiptRepayBaseInfoMapper.selectBatchIds(planList.stream().map(FundReceiptRepayPlan::getReceiptRepayId).collect(Collectors.toList()))
                .stream().collect(Collectors.toMap(FundReceiptRepayBaseInfo::getId, FundReceiptRepayBaseInfo::getFinancingId));
        Map<Long, List<FundOrganization>> orgMap = organizationService.getBatchByFinancingId(repayFinancingMap.values());
        List<FundReceiptRepayBatchPlanExcelModel> excelModelList = planList.stream().map(fundReceiptRepayBatchPlanExcelExporter::entity2Model).collect(Collectors.toList());
        for (FundReceiptRepayBatchPlanExcelModel excelModel : excelModelList) {
            List<FundOrganization> organizationList = orgMap.getOrDefault(repayFinancingMap.get(excelModel.getReceiptRepayId()), Collections.emptyList());
            excelModel.setFinancingOrgName(JSON.toJSONString(organizationList.stream().map(FundOrganization::getOrganizationName).collect(Collectors.toList())));
        }
        fundReceiptRepayBatchPlanExcelExporter.exportExcel(excelModelList, outputStream);
    }

    /**
     * 质押模块的模块对比数据需要单独处理
     *
     * @param newVersion
     * @param oldVersion
     * @return
     */
    private CommonVersionDiffBO calPledgeDiffBO(CommonVersion newVersion, CommonVersion oldVersion) {
        CommonVersionDiffBO diffBO = new CommonVersionDiffBO();
        Map<String, FundReceiptRepayBaseInfoLib> baseInfoLibMap = fundReceiptRepayBaseInfoLibMapper.selectList(Wrappers.<FundReceiptRepayBaseInfoLib>lambdaQuery()
                .eq(FundReceiptRepayBaseInfoLib::getOriginId, newVersion.getMainId())
                .in(FundReceiptRepayBaseInfoLib::getVersion, ListUtil.toList(newVersion.getVersion(), oldVersion.getVersion()))
        ).stream().collect(Collectors.toMap(FundReceiptRepayBaseInfoLib::getVersion, c -> c, (k1, k2) -> k1));
        String newFinancingVersion = baseInfoLibMap.get(newVersion.getVersion()).getFinancingVersion(), oldFinancingVersion = baseInfoLibMap.get(oldVersion.getVersion()).getFinancingVersion();
        FundFinancingPledgeListREQ pledgeListReq = new FundFinancingPledgeListREQ();
        pledgeListReq.setFinancingId(baseInfoLibMap.get(newVersion.getVersion()).getFinancingId());
        pledgeListReq.setVersion(newFinancingVersion);
        List<FundFinancingPledgeListRSP> newVersionRspList = fundFinancingPledgeInfoService.list(pledgeListReq);
        List<FundFinancingPledgeListRSP> oldVersionRspList = newVersionRspList;
        if (!Objects.equals(newFinancingVersion, oldFinancingVersion)) {
            pledgeListReq.setVersion(oldFinancingVersion);
            oldVersionRspList = fundFinancingPledgeInfoService.list(pledgeListReq);
        }
        Map<Long, FundFinancingPledgeListRSP> oldVersionRspMap = oldVersionRspList.stream().collect(Collectors.toMap(FundFinancingPledgeListRSP::getId, f -> f));
        List<Map<String, DiffValue>> diffDataList = newVersionRspList.stream().map(n -> {
            Map<String, DiffValue> compareResult =
                    CompareUtil.compare(n, oldVersionRspMap.get(n.getId()), new HashSet<>());
            if (compareResult.containsKey(MODULE_CHANGED_FLAG_KEY)) {
                if (diffBO.getModuleChanged().equals(Boolean.FALSE)) {
                    diffBO.setModuleChanged(Boolean.TRUE);
                }
                compareResult.remove(MODULE_CHANGED_FLAG_KEY);
            }
            return compareResult;
        }).collect(Collectors.toList());
        diffBO.setBeforeData(oldVersionRspList);
        diffBO.setAfterData(diffDataList);
        //判读数据条数是否一致
        if (diffBO.getModuleChanged().equals(Boolean.FALSE)) {
            diffBO.setModuleChanged(!(newVersionRspList.size() == oldVersionRspList.size()));
        }
        return diffBO;
    }

}
