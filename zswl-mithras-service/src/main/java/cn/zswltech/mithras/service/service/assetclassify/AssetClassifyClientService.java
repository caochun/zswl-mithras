package cn.zswltech.mithras.service.service.assetclassify;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.StopWatch;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.Pair;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.text.CharSequenceUtil;
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
import cn.zswltech.gruul.dao.dal.entity.UserDO;
import cn.zswltech.gruul.dao.dal.vo.AccountVO;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.assetclassify.*;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.service.constant.VersionTypeConstants;
import cn.zswltech.mithras.service.convert.assetclassify.AssetClassifyClientConvert;
import cn.zswltech.mithras.service.convert.assetclassify.AssetClassifyConvert;
import cn.zswltech.mithras.service.delayed.RedisDelayedQueue;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.enums.JobEnum;
import cn.zswltech.mithras.service.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.afterlease.domain.enums.AfterLeaseCheckPlanTypeEnum;
import cn.zswltech.mithras.afterlease.domain.enums.AfterLeaseCheckProjectMaterialsEnum;
import cn.zswltech.mithras.afterlease.domain.enums.AfterLeaseCheckReportTypeEnum;
import cn.zswltech.mithras.assetclassify.domain.enums.*;
import cn.zswltech.mithras.service.enums.common.ProcessStatus;
import cn.zswltech.mithras.riskcontrol.common.RiskControlIndustryClassify;
import cn.zswltech.mithras.assetclassify.infrastructure.persistence.mapper.AssetClassifyClientMapper;
import cn.zswltech.mithras.contract.mapper.contract.ContractBaseInfoMapper;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.dto.ClientMaxLeaseMonthDTO;
import cn.zswltech.mithras.service.mapper.model.CommonVersion;
import cn.zswltech.mithras.service.mapper.model.MaterialsList;
import cn.zswltech.mithras.afterlease.infrastructure.persistence.mapper.model.*;
import cn.zswltech.mithras.assetclassify.infrastructure.persistence.mapper.model.*;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.Client;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.CorpCommerceInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.model.PaymentBaseInfo;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.system.service.BizProcessDataService;
import cn.zswltech.mithras.system.service.Id2NameService;
import cn.zswltech.mithras.system.service.SysUserService;
import cn.zswltech.mithras.afterlease.application.AfterLeaseCheckReportMetaService;
import cn.zswltech.mithras.afterlease.application.AfterLeaseCheckReportTemplateService;
import cn.zswltech.mithras.service.service.afterlese.AfterLeaseCheckPlanBaseService;
import cn.zswltech.mithras.service.service.afterlese.AfterLeaseCheckPlanClientService;
import cn.zswltech.mithras.service.service.afterlese.AfterLeaseCheckReportContentService;
import cn.zswltech.mithras.service.service.afterlese.AfterLeaseCheckReportSummaryService;
import cn.zswltech.mithras.service.service.client.ClientService;
import cn.zswltech.mithras.service.service.collection.CollectionService;
import cn.zswltech.mithras.service.service.contract.ContractBaseInfoService;
import cn.zswltech.mithras.assetclassify.application.lib.*;
import cn.zswltech.mithras.assetclassify.application.lib.handler.impl.AssetClassifyClientAuxiliaryLibHandler;
import cn.zswltech.mithras.service.service.materialsfile.MaterialsListService;
import cn.zswltech.mithras.service.service.payment.PaymentActualDetailService;
import cn.zswltech.mithras.service.service.payment.PaymentBaseInfoService;
import cn.zswltech.mithras.service.util.LongUtil;
import cn.zswltech.mithras.service.util.StringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author dingqi
 * @date 2023/1/3
 * @description
 */
@Service
@Slf4j
public class AssetClassifyClientService extends ServiceImpl<AssetClassifyClientMapper, AssetClassifyClient> {
    @Resource
    private AssetClassifyConvert assetClassifyConvert;
    @Resource
    private AssetClassifyClientAuxiliaryLibService assetClassifyClientAuxiliaryLibService;
    @Resource
    private AssetClassifyClientAuxiliaryLibHandler assetClassifyClientAuxiliaryLibHandler;
    @Resource
    private AfterLeaseCheckPlanClientService afterLeaseCheckPlanClientService;
    @Resource
    private AfterLeaseCheckReportMetaService afterLeaseCheckReportMetaService;
    @Resource
    private AfterLeaseCheckReportContentService afterLeaseCheckReportContentService;
    @Resource
    private AfterLeaseCheckReportSummaryService afterLeaseCheckReportSummaryService;
    @Resource
    private AfterLeaseCheckReportTemplateService afterLeaseCheckReportTemplateService;
    @Resource
    private AssetClassifyClientService assetClassifyClientService;
    @Resource
    private AssetClassifyNodeRecordService assetClassifyNodeRecordService;
    @Resource
    private AssetClassifyClientLibService assetClassifyClientLibService;
    @Resource
    private AssetClassifyNodeRecordLibService assetClassifyNodeRecordLibService;
    @Resource
    private AssetClassifyLibVersionService assetClassifyLibVersionService;
    @Resource
    private AssetClassifyCommonService assetClassifyCommonService;
    @Resource
    private AssetClassifyService assetClassifyService;
    @Resource
    private AfterLeaseCheckPlanBaseService afterLeaseCheckPlanBaseService;
    @Resource
    private MaterialsListService materialsListService;
    @Resource
    private Id2NameService id2NameService;
    @Resource
    private SysUserService sysUserService;
    @Resource
    private RedisDelayedQueue redisDelayedQueue;
    @Resource
    private BizProcessDataService bizProcessDataService;
    @Resource
    private AssetClassifyClientMapper assetClassifyClientMapper;
    @Resource
    private CollectionService collectionService;
    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private ContractBaseInfoMapper contractBaseInfoMapper;
    @Resource
    private ClientService clientService;
    @Resource
    private PaymentBaseInfoService paymentBaseInfoService;
    @Resource
    private PaymentActualDetailService paymentActualDetailService;
    @Resource
    private AssetClassifyLibService assetClassifyLibService;

    //财务分析, 经营状况分析, 租赁物分析, 担保人分析, 抵质押物分析, 风险信号及重大事项、风险防范措施 || 五极分类特有 风险信号及重大事项、风险防范措施， 重要公开信息分析， 其他重大事项分析
    private final static List<String> CHECK_CODE = ListUtil.toList("NP_C_2_03", "NP_C_2_04", "NP_C_2_06", "NP_C_3_01"
            , "NP_C_3_02", "NP_S_1_01", "ACNP_S_1_01", "ACNP_S_1_02", "ACNP_S_1_03");
    private final static List<String> number2china = ListUtil.toList("零", "一", "二", "三", "四");
    private static final ThreadPoolExecutor threadPool = new ThreadPoolExecutor(1, Runtime.getRuntime().availableProcessors() + 1, 30, TimeUnit.SECONDS, new LinkedBlockingQueue<>(500));


    public String findLatestClassifyByClientId(Long clientId) {
        // 找到最近生效的五级分类
        LambdaQueryWrapper<AssetClassify> query = Wrappers.lambdaQuery();
        query.eq(AssetClassify::getFinish, YesOrNoNumberEnum.YES.getCode());
        query.orderByDesc(AssetClassify::getId);
        query.last(StringUtil.mysqlLimitOne());
        AssetClassify latest = SpringUtil.getBean(AssetClassifyService.class).getOne(query);
        if (Objects.isNull(latest)) {
            return null;
        }
        // 找到客户的五级分类结果
        LambdaQueryWrapper<AssetClassifyClient> queryClient = Wrappers.lambdaQuery();
        queryClient.eq(AssetClassifyClient::getAssetClassifyId, latest.getId());
        queryClient.eq(AssetClassifyClient::getClientId, clientId);
        queryClient.orderByDesc(AssetClassifyClient::getId);
        queryClient.last(StringUtil.mysqlLimitOne());
        AssetClassifyClient assetClassifyClient = assetClassifyClientService.getOne(queryClient);
        return Optional.ofNullable(assetClassifyClient).map(AssetClassifyClient::getClassifyResult).orElse(null);
    }

    public void joinBoardMeeting(Collection<Long> ids) {
        List<AssetClassifyClient> toUpdateList = new ArrayList<>(ids.size());
        for (Long id : ids) {
            AssetClassifyClient assetClassifyClient = new AssetClassifyClient();
            assetClassifyClient.setId(id);
            assetClassifyClient.setBoardMeeting(YesOrNoNumberEnum.YES.getCode());
            toUpdateList.add(assetClassifyClient);
        }
        this.updateBatchById(toUpdateList);
    }

    public List<AssetClassifyClient> listByAssetClassifyId(Long assetClassifyId) {
        LambdaQueryWrapper<AssetClassifyClient> query = Wrappers.lambdaQuery();
        query.eq(AssetClassifyClient::getAssetClassifyId, assetClassifyId);
        return this.list(query);
    }

    public List<AssetClassifyClient> listByClientIds(List<Long> clientIds) {
        if (ObjectUtil.isEmpty(clientIds)) {
            return ListUtil.empty();
        }
        return assetClassifyClientMapper.listByClientIds(clientIds);
    }

    public AssetClassifyClient getLastOneByClientId(Long clientId, Long exportClassifyId) {
        LambdaQueryWrapper<AssetClassifyClient> query = Wrappers.lambdaQuery();
        query.eq(AssetClassifyClient::getClientId, clientId);
        query.ne(ObjectUtil.isNotEmpty(exportClassifyId), AssetClassifyClient::getAssetClassifyId, exportClassifyId);
        query.orderByDesc(AssetClassifyClient::getId);
        query.last(StringUtil.mysqlLimitOne());
        return this.getOne(query);
    }

    public PageR<AssetClassifyClientListRSP> pageList(AssetClassifyClientListREQ req) {
        Page<AssetClassifyClient> pageResult;
        if (StrUtil.isBlank(req.getVersion())) {
            // 分页条件
            Page<AssetClassifyClient> pageQuery = new Page<>();
            pageQuery.setCurrent(req.getPage());
            pageQuery.setSize(req.getPageSize());
            // 业务条件
            LambdaQueryWrapper<AssetClassifyClient> conditionQuery = assetClassifyCommonService.buildQuery(req);
            // 分页查询结果
            pageResult = this.page(pageQuery, conditionQuery);
        } else {
            // 查询版本表数据
            Page<AssetClassifyClientAuxiliaryLib> libPageResult = assetClassifyClientAuxiliaryLibService.pageList(req);
            // 模型转换
            pageResult = new Page<>();
            pageResult.setCurrent(req.getPage());
            pageResult.setSize(req.getPageSize());
            pageResult.setTotal(libPageResult.getTotal());
            pageResult.setRecords(libPageResult.getRecords().stream().map(assetClassifyClientAuxiliaryLibHandler::actualLib2Entity).collect(Collectors.toList()));
        }
        if (CollectionUtil.isEmpty(pageResult.getRecords())) {
            return PageR.empty(req.getPage(), req.getPageSize());
        }
        // 查询部门和主办名称
        List<Long> deptIdList = new ArrayList<>(pageResult.getRecords().size());
        List<Long> sponsorIdList = new ArrayList<>(pageResult.getRecords().size());
        for (AssetClassifyClient assetClassifyClient : pageResult.getRecords()) {
            deptIdList.add(assetClassifyClient.getBelongDeptId());
            sponsorIdList.add(assetClassifyClient.getBelongSponsorId());
        }
        Map<Long, String> deptMap = id2NameService.deptId2Name(deptIdList);
        Map<Long, String> sponsorMap = id2NameService.sysUserId2Name(sponsorIdList);
        // 模型转换
        List<AssetClassifyClientListRSP> rspList = pageResult.getRecords().stream().map(item -> {
            AssetClassifyClientListRSP rsp = AssetClassifyClientConvert.toAssetClassifyClientListRSP(item);
            rsp.setBelongDeptName(deptMap.get(rsp.getBelongDeptId()));
            rsp.setBelongSponsorName(sponsorMap.get(rsp.getBelongSponsorId()));
            return rsp;
        }).collect(Collectors.toList());
        // 判断是否要填充版本数据
        if (Objects.nonNull(req.getQueryLatestVersionClassifyResult()) && req.getQueryLatestVersionClassifyResult()) {
            this.fillVersionData(rspList, req.getAssetClassifyId(), req.getVersion());
        }
        return PageR.of(rspList, pageResult.getTotal(), pageResult.getCurrent(), pageResult.getSize());
    }

    public AssetClassifyClientDetailRSP clientDetail(AssetClassifyClientDetailREQ req) {
        AssetClassifyClient assetClassifyClient;
        AssetClassifyClientDetailRSP assetClassifyClientDetailRSP;
        AssetClassifyNodeRecord nodeRecord;
        //查询流程中数据
        if (ObjectUtil.isNotNull(req.getVersion())) {
            if (ProcessModelTypeEnum.AssetClassifyReview.name().equals(req.getProcessType())) {
                assetClassifyClient = assetClassifyClientLibService.getByVersion(req.getId(), req.getVersion());
            } else {
                assetClassifyClient = assetClassifyClientAuxiliaryLibService.getByVersion(req.getId(), req.getVersion());
            }
            assetClassifyClientDetailRSP = assetClassifyConvert.assetClassifyClient2AssetClassifyClientDetailRSP(assetClassifyClient);
            nodeRecord = assetClassifyNodeRecordLibService.getByVersionAndNodeName(assetClassifyClient.getAssetClassifyId(),
                    req.getVersion(), AssetClassifyBizNodeEnum.REVIEW.name());
        } else {
            assetClassifyClient = baseMapper.selectById(req.getId());
            assetClassifyClientDetailRSP = assetClassifyConvert.assetClassifyClient2AssetClassifyClientDetailRSP(assetClassifyClient);
            nodeRecord = assetClassifyNodeRecordService.getOne(Wrappers.<AssetClassifyNodeRecord>lambdaQuery()
                    .eq(AssetClassifyNodeRecord::getAssetClassifyId, assetClassifyClient.getAssetClassifyId())
                    .eq(AssetClassifyNodeRecord::getNodeName, AssetClassifyBizNodeEnum.REVIEW.name()));
        }
        assetClassifyClientDetailRSP.setNodeStatue(ObjectUtil.isNull(nodeRecord) ? null : nodeRecord.getNodeStatue());
        assetClassifyClientDetailRSP.setRepayment(LongUtil.null2zero(assetClassifyClientDetailRSP.getOverdueAmount()) > 0 ? "已逾期" : "正常");
        //填充租后检查信息
        //获取最近的检查计划
        NewAfterLeaseCheckPlanClient newAfterLeaseCheckPlanClient = afterLeaseCheckPlanClientService.getOne(Wrappers.<NewAfterLeaseCheckPlanClient>lambdaQuery()
                .in(NewAfterLeaseCheckPlanClient::getClientId, assetClassifyClient.getClientId())
                .eq(NewAfterLeaseCheckPlanClient::getIsCheck, YesOrNoNumberEnum.YES.getCode())
                .eq(NewAfterLeaseCheckPlanClient::getApprovalStatus, ProcessStatus.APPROVAL_PASS.name())
                .orderByDesc(NewAfterLeaseCheckPlanClient::getUpdateTime)
                .last(StringUtil.mysqlLimitOne()));
        if (ObjectUtil.isNotEmpty(newAfterLeaseCheckPlanClient)) {
            NewAfterLeaseCheckPlanBase byId = afterLeaseCheckPlanBaseService.getById(newAfterLeaseCheckPlanClient.getPlanId());
            assetClassifyClientDetailRSP.setCheckId(newAfterLeaseCheckPlanClient.getId());
            if (ObjectUtil.isNotEmpty(byId)) {
                assetClassifyClientDetailRSP.setCheckName(byId.getPlanName());
//                StringBuilder stringBuilder = new StringBuilder();
//                stringBuilder.append(byId.getYear());
//                stringBuilder.append("年");
//                if (ObjectUtil.isNotEmpty(byId.getQuarter())) {
//                    stringBuilder.append(byId.getQuarter());
//                    stringBuilder.append("季度");
//                } else {
//                    stringBuilder.append(byId.getMonth());
//                    stringBuilder.append("月");
//                }
//                stringBuilder.append(byId.getPlanName());
//                assetClassifyClientDetailRSP.setCheckName(stringBuilder.toString());
            }
        }
        return assetClassifyClientDetailRSP;
    }

    @Transactional(rollbackFor = Throwable.class)
    public void clientModify(AssetClassifyClientModifyREQ req) {
        checkAuth();
        AssetClassifyClient assetClassifyClient = baseMapper.selectById(req.getId());
        if (ObjectUtil.isNull(assetClassifyClient)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        assetClassifyClient.setSuggestResult(req.getSuggestResult());
        assetClassifyClient.setRemark(req.getRemark());
        assetClassifyClient.setAwardRatio(req.getAwardRatio());
        assetClassifyClient.setSuggestFlag(req.getSuggestFlag());
        assetClassifyClient.setSuggestReason(req.getSuggestReason());
        baseMapper.updateById(assetClassifyClient);
        assetClassifyClientService.updateReviewStatus(assetClassifyClient.getId(), AssetClassifyReviewStatusEnum.PROCESS.name());
    }

    private void checkAuth() {
        Long userId = AccountUtil.getLoginInfo().getId();
        List<String> jobList = sysUserService.queryUserJobList(userId);
        // 资产管理岗可发起
        boolean operation = false;
        for (String jobCode : jobList) {
            if (Objects.equals(JobEnum.assetmanagement.name(), jobCode)) {
                operation = true;
                break;
            }
        }
        Assert.isTrue(operation, () -> MithrasException.newException("仅资产管理岗可操作"));
    }

    @Transactional(rollbackFor = Throwable.class)
    public void updateReviewStatus(Long id, String reviewStatus) {
        AssetClassifyClient assetClassifyClient = baseMapper.selectById(id);
        assetClassifyClient.setReviewStatus(reviewStatus);
        if (AssetClassifyReviewStatusEnum.FINISH.name().equals(reviewStatus)) {
            assetClassifyClient.setReviewPassTime(LocalDateTime.now());
        } else {
            assetClassifyClient.setReviewPassTime(null);
//            assetClassifyNodeRecordService.updateNodeStatue(assetClassifyClient.getAssetClassifyId(), AssetClassifyBizNodeEnum.REVIEW.name(), AssetClassifyStatusEnum.PROCESS.name());
        }
        this.baseMapper.updateById(assetClassifyClient);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void saveSuggestResult(Long id, String classifyResult) {
        AssetClassifyClient assetClassifyClient = baseMapper.selectById(id);
        Assert.notNull(assetClassifyClient, () -> MithrasException.newException(ResultMsg.RECORD_NOT_EXIST));
//        assetClassifyClient.setClassifyResult(classifyResult);
        assetClassifyClient.setSuggestResult(classifyResult);
        this.updateById(assetClassifyClient);
    }

    public AssetClassifyClientDetailRSP clientDetailByReq(AssetClassifyClientDetailGeneralREQ req) {
        AssetClassifyClient assetClassifyClientDetailRSP = baseMapper.selectOne(Wrappers.<AssetClassifyClient>lambdaQuery()
                .eq(ObjectUtil.isNotEmpty(req.getClientId()), AssetClassifyClient::getClientId, req.getClientId())
                .orderByDesc(AssetClassifyClient::getId)
                .last(StringUtil.mysqlLimitOne()));
        if (ObjectUtil.isEmpty(assetClassifyClientDetailRSP)) {
            return null;
        }
        return assetClassifyConvert.assetClassifyClient2AssetClassifyClientDetailRSP(assetClassifyClientDetailRSP);
    }

    /**
     * @param assetClassId 分类ID
     **/
    public Boolean checkReviewFinish(Long assetClassId) {
        return ObjectUtil.equals(this.count(Wrappers.<AssetClassifyClient>lambdaQuery()
                .eq(AssetClassifyClient::getAssetClassifyId, assetClassId)
                .ne(AssetClassifyClient::getReviewStatus, AssetClassifyReviewStatusEnum.FINISH)), 0);
    }

    /**
     * 根据客户assetClassifyClientId获取
     *
     * @param assetClassifyClientId, 五级分类客户表ID
     * @author: jackerhe
     * @date: 2023/1/6 6:12 下午
     **/
    @Deprecated
    public List<AssetClassifyCheckContent> getLastCheckProjectReportByClientId(Long assetClassifyClientId) {
        AssetClassifyClient classifyClient = assetClassifyClientService.getById(assetClassifyClientId);
        if (ObjectUtil.isNull(classifyClient)) {
            return new ArrayList<>();
        }
        List<AssetClassifyCheckContent> assetClassifyCheckContents = new ArrayList<>();
        Map<String, NewAfterLeaseCheckReportContent> code2ContentMap = new HashMap<>();
        //获取最近的检查计划
        NewAfterLeaseCheckPlanClient newAfterLeaseCheckPlanClient = afterLeaseCheckPlanClientService.getOne(Wrappers.<NewAfterLeaseCheckPlanClient>lambdaQuery()
                .in(NewAfterLeaseCheckPlanClient::getClientId, classifyClient.getClientId())
                .eq(NewAfterLeaseCheckPlanClient::getIsCheck, YesOrNoNumberEnum.YES.getCode())
                .eq(NewAfterLeaseCheckPlanClient::getApprovalStatus, ProcessStatus.APPROVAL_PASS.name())
                .orderByDesc(NewAfterLeaseCheckPlanClient::getUpdateTime)
                .last(StringUtil.mysqlLimitOne()));
        Map<String, NewAfterLeaseCheckReportTemplate> code2TempMap = afterLeaseCheckReportTemplateService.list(Wrappers.<NewAfterLeaseCheckReportTemplate>lambdaQuery()
                .in(NewAfterLeaseCheckReportTemplate::getCode, CHECK_CODE)).stream().collect(Collectors.toMap(NewAfterLeaseCheckReportTemplate::getCode, e -> e));
        if (ObjectUtil.isNotNull(newAfterLeaseCheckPlanClient)) {
            NewAfterLeaseCheckReportMeta projectReportMeta = afterLeaseCheckReportMetaService.getByCheckPlanClientId(newAfterLeaseCheckPlanClient.getId());
            if (ObjectUtil.isNotNull(projectReportMeta) && ObjectUtil.equals(projectReportMeta.getReportType(), AfterLeaseCheckReportTypeEnum.NON_PUBLIC.name())) {
                code2ContentMap.putAll(afterLeaseCheckReportContentService.listByCheckPlanClientId(newAfterLeaseCheckPlanClient.getId()).stream().collect(Collectors.toMap(NewAfterLeaseCheckReportContent::getTemplateCode, e -> e)));
                code2ContentMap.putAll(afterLeaseCheckReportSummaryService.listByCheckPlanClientId(newAfterLeaseCheckPlanClient.getId()).stream().map(base -> BeanUtil.copyProperties(base, NewAfterLeaseCheckReportContent.class)).collect(Collectors.toMap(NewAfterLeaseCheckReportContent::getTemplateCode, e -> e)));
            }
        }
        initAssetClassifyCheckContent(assetClassifyCheckContents, code2ContentMap, code2TempMap, classifyClient, newAfterLeaseCheckPlanClient);
        return assetClassifyCheckContents;
    }

    public void saveLastCheckProjectReportFile(List<AssetClassifyCheckContent> checkContents) {
        Map<Long, Long> planIdMap = new HashMap<>();
        for (AssetClassifyCheckContent content : checkContents) {
            planIdMap.put(content.getAfterLeaseCheckPlanProjectId(), content.getAssetClassifyClientId());
        }
        if (ObjectUtil.isEmpty(planIdMap)) {
            return;
        }
        List<MaterialsList> toSaveList = new ArrayList<>();
        MaterialsList toSave;
        List<MaterialsList> materialsLists = materialsListService.list(Wrappers.<MaterialsList>lambdaQuery()
                .in(MaterialsList::getBelongId, planIdMap.keySet())
                .eq(MaterialsList::getBusinessType, BusinessModuleEnum.AFTER_LEASE_CHECK_PROJECT.name())
                .eq(MaterialsList::getMaterialsType, AfterLeaseCheckProjectMaterialsEnum.CHECK_PROJECT_NON_PUBLIC_REPORT_ATTACHMENT.name()));
        if (ObjectUtil.isNotEmpty(materialsLists)) {
            for (MaterialsList materialsList : materialsLists) {
                toSave = BeanUtil.copyProperties(materialsList, MaterialsList.class);
                toSave.setId(null);
                toSave.setBusinessType(BusinessModuleEnum.ASSET_CLASSIFY_REVIEW.name());
                toSave.setMaterialsType(AssetClassifyMaterialsEnum.ASSET_CLASSIFY_CHECK_REPORT.name());
                toSave.setBelongId(planIdMap.get(materialsList.getBelongId()));
                toSaveList.add(toSave);
            }
            materialsListService.saveBatch(toSaveList);
        }
    }

    //初始化数据
    private void initAssetClassifyCheckContent(List<AssetClassifyCheckContent> assetClassifyCheckContents, Map<String, NewAfterLeaseCheckReportContent> code2ContentMap,
                                               Map<String, NewAfterLeaseCheckReportTemplate> code2TempMap,
                                               AssetClassifyClient assetClassifyClient, NewAfterLeaseCheckPlanClient newAfterLeaseCheckPlanClient) {
        AssetClassifyCheckContent content;
        NewAfterLeaseCheckReportTemplate template;
        //code2TempMap.put("")
        for (String code : CHECK_CODE) {
            if (ObjectUtil.equals("NP_S_1_01", code)) {
                continue;
            }
            template = code2TempMap.get(code);
            content = new AssetClassifyCheckContent();
            content.setAssetClassifyId(assetClassifyClient.getAssetClassifyId());
            content.setAssetClassifyClientId(assetClassifyClient.getId());
            content.setTemplateId(template.getId());
            content.setTemplateCode(template.getCode());
            content.setTemplateGroupName(template.getGroupName());
            content.setTemplateTitle(template.getTitle());
            content.setTemplateContentInputType(template.getContentInputType());
            content.setTemplateContentInputOption(template.getContentInputOption());
            content.setTemplateOrderNum(template.getOrderNum());
            if (code.equals("ACNP_S_1_01")) {
                code = "NP_S_1_01";
            }
            content.setContent(ObjectUtil.isNull(code2ContentMap.get(code)) ? null : code2ContentMap.get(code).getContent());
            content.setAfterLeaseCheckPlanProjectId(ObjectUtil.isNull(newAfterLeaseCheckPlanClient) ? null : newAfterLeaseCheckPlanClient.getId());
            assetClassifyCheckContents.add(content);
        }
        //保存表头，来源
        if (ObjectUtil.isNotNull(newAfterLeaseCheckPlanClient)) {
            NewAfterLeaseCheckPlanBase planBase = afterLeaseCheckPlanBaseService.getById(newAfterLeaseCheckPlanClient.getPlanId());
            content = new AssetClassifyCheckContent();
            content.setAssetClassifyId(assetClassifyClient.getAssetClassifyId());
            content.setAssetClassifyClientId(assetClassifyClient.getId());
            content.setTemplateGroupName("TABLE_HEAD");
            content.setTemplateCode("TABLE_HEAD");
            content.setTemplateTitle("检查来源");
            content.setTemplateOrderNum(0);
            content.setTemplateContentInputType("textArea");
            content.setContent(getTitle(planBase.getPlanType(), planBase.getYear(), planBase.getQuarter(), planBase.getMonth()));
            assetClassifyCheckContents.add(content);
        }

    }

    private String getTitle(String planType, Integer year, Integer quarter, Integer month) {
        AfterLeaseCheckPlanTypeEnum planTypeEnum = Optional.ofNullable(AfterLeaseCheckPlanTypeEnum.of(planType)).orElseThrow(() -> new MithrasException(ResultMsg.UNSUPPORT_TYPE));
        String content;
        switch (planTypeEnum) {
            case QUARTER:
                content = String.format("%s第%s季度 季度检查", year, number2china.get(quarter % 5));
                break;
            case MONTH:
                content = String.format("%s年%s月 月度检查", year, month);
                break;
            case SPECIAL:
                content = String.format("%s年%s月 专项检查", year, month);
                break;
            case RANDOM:
                content = String.format("%s年%s月 抽查检查", year, month);
                break;
            default:
                content = null;

        }
        return content;
    }


    public List<AssetClassifyHistoryRSP> history(AssetClassifyCheckContentREQ req) {
        AssetClassifyClient assetClassifyClient = baseMapper.selectById(req.getAssetClassifyClientId());
        if (ObjectUtil.isNull(assetClassifyClient)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        List<AssetClassifyHistoryRSP> rsps = new ArrayList<>();
        AssetClassifyHistoryRSP rsp;
        List<AssetClassify> assetClassifies = assetClassifyService.list(Wrappers.<AssetClassify>lambdaQuery()
                .ne(AssetClassify::getFinish, YesOrNoNumberEnum.YES.getCode()));
        List<Long> ignoreClassifies = null;
        if (ObjectUtil.isNotEmpty(assetClassifies)) {
            ignoreClassifies = assetClassifies.stream().map(AssetClassify::getId).collect(Collectors.toList());
        }
        List<AssetClassifyClient> assetClassifyClients = baseMapper.selectList(Wrappers.<AssetClassifyClient>lambdaQuery()
                .eq(AssetClassifyClient::getClientId, assetClassifyClient.getClientId())
                .notIn(ObjectUtil.isNotEmpty(ignoreClassifies), AssetClassifyClient::getAssetClassifyId, ignoreClassifies)
                .orderByDesc(AssetClassifyClient::getCreateTime));
        for (AssetClassifyClient assetClassifyClient1 : assetClassifyClients) {
            rsp = BeanUtil.copyProperties(assetClassifyClient1, AssetClassifyHistoryRSP.class, "createTime");
            rsp.setCreateTime(assetClassifyClient1.getCreateTime().toLocalDate());
            rsps.add(rsp);
        }
        // 增加查询季中初分完成的历史数据
        List<AssetClassifyClientLib> midClientlist = getMidClientlist(assetClassifyClient);
        if (CollectionUtil.isNotEmpty(midClientlist)){
            for (AssetClassifyClientLib lib : midClientlist) {
                rsp = BeanUtil.copyProperties(lib, AssetClassifyHistoryRSP.class, "createTime");
                rsp.setCreateTime(lib.getCreateTime().toLocalDate());
                rsps.add(rsp);
            }
            // 重新按照时间降序排序
            return rsps.stream()
                    .sorted(Comparator.comparing(AssetClassifyHistoryRSP::getCreateTime).reversed())
                    .collect(Collectors.toList());
        }else {
            return rsps;
        }
    }

    // 查询季中初分完成的历史数据
    private List<AssetClassifyClientLib> getMidClientlist(AssetClassifyClient assetClassifyClient){
        List<AssetClassifyClientLib> midClientlist = new ArrayList<>();
        // 获取季中初分
        List<AssetClassify> midList = assetClassifyService.list(Wrappers.<AssetClassify>lambdaQuery()
                .eq(AssetClassify::getInitType, AssetClassifyInitTypeEnum.QUARTER_MID.name()));
        if (CollectionUtil.isNotEmpty(midList)){
            // 获取季中初分版本数据
            List<Long> midIds = midList.stream().map(AssetClassify::getId).collect(Collectors.toList());
            List<AssetClassifyLib> assetClassifyLibList = assetClassifyLibService.list(Wrappers.<AssetClassifyLib>lambdaQuery()
                    .eq(AssetClassifyLib::getInitType, AssetClassifyInitTypeEnum.QUARTER_MID.name())
                    .eq(AssetClassifyLib::getVersionType, VersionTypeConstants.NORMAL)
                    .in(AssetClassifyLib::getOriginId, midIds));
            // 按照OriginId字段分组，取id最新的一条数据
            List<AssetClassifyLib> result = new ArrayList<>(assetClassifyLibList.stream()
                    .collect(Collectors.toMap(
                            AssetClassifyLib::getOriginId,                                  // 以originId作为key
                            asset -> asset,                                 // 值就是数据本身
                            (existing, replacement) ->       // 当key冲突时，选择id更大的
                                    existing.getId() > replacement.getId() ? existing : replacement
                    ))
                    .values());                                                             // 转换为List
            List<String> versionList = result.stream().map(AssetClassifyLib::getVersion).collect(Collectors.toList());
            // 查询客户版本表中的历史数据
            midClientlist = assetClassifyClientLibService.list(Wrappers.<AssetClassifyClientLib>lambdaQuery()
                    .eq(AssetClassifyClientLib::getClientId, assetClassifyClient.getClientId())
                    .in(AssetClassifyClientLib::getVersion, versionList));
            return midClientlist;
        }
        return midClientlist;
    }

    private void fillVersionData(List<AssetClassifyClientListRSP> rspList, Long assetClassifyId, String version) {
        Map<Long, String> versionClassifyResultMap;
        // 查询最近一个有效版本数据
        CommonVersion commonVersion;
        if (StrUtil.isBlank(version)) {
            commonVersion = assetClassifyLibVersionService.findNewestVersion(assetClassifyId);
        } else {
            commonVersion = assetClassifyLibVersionService.findSpecificLatestVersion(assetClassifyId, version);
        }
        if (Objects.nonNull(commonVersion)) {
            List<AssetClassifyClientAuxiliaryLib> assetClassifyClientAuxiliaryLibList = assetClassifyClientAuxiliaryLibService.listSpecificVersionData(assetClassifyId, commonVersion.getVersion());
            versionClassifyResultMap = assetClassifyClientAuxiliaryLibList.stream().collect(Collectors.toMap(AssetClassifyClientAuxiliaryLib::getOriginId, AssetClassifyClient::getClassifyResult));
        } else {
            versionClassifyResultMap = Collections.emptyMap();
        }
        // 填充版本数据
        for (AssetClassifyClientListRSP rsp : rspList) {
            rsp.setLatestVersionClassifyResult(versionClassifyResultMap.get(rsp.getId()));
        }
    }

    public String ensureResult(int days, CorpCommerceInfo corpCommerceInfo) {
        // 查询客户工商信息确定是否关联方
        boolean isRelated = Objects.equals(YesOrNoNumberEnum.YES.getCode(), corpCommerceInfo.getIsRelated());
        if (isRelated) {
            if (days <= 180) {
                return AssetClassifyResultEnum.NORMAL.name();
            } else if (days <= 270) {
                return AssetClassifyResultEnum.ATTENTION.name();
            } else if (days <= 360) {
                return AssetClassifyResultEnum.SECONDARY.name();
            } else {
                return AssetClassifyResultEnum.SUSPICIOUS.name();
            }
        } else {
            if (days <= 30) {
                return AssetClassifyResultEnum.NORMAL.name();
            } else if (days <= 90) {
                return AssetClassifyResultEnum.ATTENTION.name();
            } else if (days <= 180) {
                return AssetClassifyResultEnum.SECONDARY.name();
            } else if (days <= 360) {
                return AssetClassifyResultEnum.SUSPICIOUS.name();
            } else {
                return AssetClassifyResultEnum.LOSS.name();
            }
        }
    }

    //客户维度计提
    public Long getInitAwardRatio(String riskControlIndustryClassify, String classifyResult, Long month) {
        AssetClassifyResultEnum assetClassifyResultEnum = AssetClassifyResultEnum.of(classifyResult);
        if (assetClassifyResultEnum == null) {
            return null;
        }
        Long awardRatio = 0L;
        switch (assetClassifyResultEnum) {
            case NORMAL:
                awardRatio = 10000L;
                break;
            case ATTENTION:
                awardRatio = 30000L;
                break;
            case SECONDARY:
                awardRatio = 150000L;
                break;
            case SUSPICIOUS:
                awardRatio = 300000L;
                break;
            case LOSS:
                awardRatio = 1000000L;
                break;
            default:
                awardRatio = 0L;
        }
        //三年以下1%，三到五年1.5%，五年以上 2%。
        if (Objects.equals(AssetClassifyResultEnum.NORMAL.name(), riskControlIndustryClassify) && !CharSequenceUtil.equalsAny(riskControlIndustryClassify,
//                RiskControlIndustryClassify.ENGINEERING_MACHINERY.name(),
                RiskControlIndustryClassify.PUBLIC_UTILITIES.name(), RiskControlIndustryClassify.CIVIL_CONSUMPTION.name())) {
            if (month > 36 && month <= 60) {
                awardRatio += 5000;
            } else if (month > 60) {
                awardRatio += 10000;
            }
        }
        return awardRatio;
    }

    @Resource
    private FlowTaskApiService flowTaskApiService;
    @Resource
    private FlowProcessApiService processApiService;

    private boolean hasRelatedRunningProcess(String modleKey, Long businessId) {
        ProcessPageReq processPageReq = new ProcessPageReq();
        processPageReq.setModelKey(modleKey);
        processPageReq.setBusinessKey(String.valueOf(businessId));
        processPageReq.setPageSize(500);
        processPageReq.setProcessStatusList(Collections.singletonList(ProcessBusinessStatusEnum.RUNNING.getType()));
        cn.zswltech.flow.core.util.Page<ProcessResp> processRespPage = flowTaskApiService.queryProcess(processPageReq);
        return processRespPage != null && CollectionUtil.isNotEmpty(processRespPage.getContents());
    }

    @Transactional(rollbackFor = Throwable.class)
    public void removeById(Long id) {
        AssetClassifyClient assetClassifyClients = baseMapper.selectById(id);
        if (ObjectUtil.isEmpty(assetClassifyClients)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        if (AssetClassifyReviewStatusEnum.FINISH.name().equals(assetClassifyClients.getReviewStatus())) {
            throw new MithrasException("已复核，不允许删除");
        }
        baseMapper.deleteById(id);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void clientReviewSubmit(Long id) {
        AssetClassifyClient assetClassifyClient = getById(id);
        if (assetClassifyClient == null) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        if (hasRelatedRunningProcess(ProcessModelTypeEnum.AssetClassifyReview.name(), id)) {
            throw new MithrasException("该客户正在复合中，无法提交复合");
        }
        if (hasRelatedRunningProcess(ProcessModelTypeEnum.AssetClassifyReviewFlow.name(), assetClassifyClient.getAssetClassifyId())) {
            throw new MithrasException("本季度资产五级分类已进入复合审批流程，无法提交复合");
        }
        StartProcessReq startProcessReq = buildReviewSubmitReq(assetClassifyClient);
        startProcessReq.setModelKey(ProcessModelTypeEnum.AssetClassifyReview.name());
        if (assetClassifyClient.getBelongSponsorId() == null) {
            throw new MithrasException("五级分类客户无主办，暂不允许提交");
        }
        //获取业务部门负责人
        List<UserDO> bizDeptLeaderUserList = sysUserService.listSpecificOrgJobUser(assetClassifyClient.getBelongDeptId(), JobEnum.businesshead.name());
        Map<String, Object> varMap = new HashMap<>();
        //客户主办
        varMap.put("clientSponsorId", Collections.singletonList(String.valueOf(assetClassifyClient.getBelongSponsorId())));
        // 业务部门负责人
        varMap.put("bizDeptLeader", bizDeptLeaderUserList.stream().map(UserDO::getId).map(String::valueOf).collect(Collectors.toList()));
        startProcessReq.setVariables(varMap);
        String processInstanceId = processApiService.start(startProcessReq);
        assetClassifyClient.setReviewStatus(AssetClassifyReviewStatusEnum.PROCESS.name());
        updateById(assetClassifyClient);
        bizProcessDataService.recordBizData(processInstanceId, assetClassifyClient.getClientId());

        // 发送redis 24小时过期键--20251231版本转到资产管理复核提交后24小时
        //redisDelayedQueue.addQueueHours(processInstanceId, 24, AssetReviewExpirationListener.class);
    }

    /**
     * 构建通用的启动流程参数
     * 还需自己填充 subModule 和 modelKey
     *
     * @return
     */
    private StartProcessReq buildReviewSubmitReq(AssetClassifyClient assetClassifyClient) {
        AssetClassify assetClassify = assetClassifyService.getById(assetClassifyClient.getAssetClassifyId());
        StartProcessReq startProcessReq = new StartProcessReq();
        startProcessReq.setBusinessKey(String.valueOf(assetClassifyClient.getId()));
        // 季中与季末使用不同的流程名称
        if (AssetClassifyInitTypeEnum.QUARTER_MID.name().equals(assetClassify.getInitType())){
            startProcessReq.setProcessInstanceName(String.format("%s客户%s年%s季度(季中调整)", assetClassifyClient.getClientName(), assetClassify.getYear(),
                    assetClassify.getQuarter()));
        }else {
            startProcessReq.setProcessInstanceName(String.format("%s客户%s年%s季度", assetClassifyClient.getClientName(), assetClassify.getYear(),
                    assetClassify.getQuarter()));
        }
        startProcessReq.setStartUserId(Optional.ofNullable(AccountUtil.getLoginInfo())
                .map(AccountVO::getId)
                .map(String::valueOf)
                .orElseThrow(() -> new MithrasException(ResultMsg.USER_NOT_LOGIN)));
        startProcessReq.setStartUserDeptId(Optional.ofNullable(assetClassifyClient.getBelongDeptId()).map(String::valueOf).orElse(null));
        startProcessReq.setVariables(MapUtil.of(Pair.of("ccTabShowFlag", Boolean.FALSE)
        ));
        return startProcessReq;
    }

    /**
     * 五级分类-季中初分-客户列表查询
     *
     */
    public PageR<MidQuarterClientListRSP> clientListByMidQuarter(MidQuarterClientListREQ req){
        StopWatch stopWatch = new StopWatch("五级分类-季中初分-客户列表查询");
        stopWatch.start("处理有效合同");
        List<MidQuarterClientListRSP> results = new ArrayList<>();
        // 获取所有有剩余本金大于0的起租合同（与季末初分相同的数据范围）
        Map<Long, Long> startRentContractMap = collectionService.listContractAmountByContractIds(null);
        List<Long> startRentContractIds = new ArrayList<>();
        startRentContractMap.forEach((key, value) -> {
            if(LongUtil.null2zero(value) > 0){
                startRentContractIds.add(key) ;
            }
        });
        if(CollectionUtil.isEmpty(startRentContractIds)){
            log.info("没有剩余本金大于0的合同,返回空列表");
            Page<MidQuarterClientListRSP> pageResult = buildPageResult(req, results);
            return PageR.of(results, pageResult.getTotal(), pageResult.getCurrent(), pageResult.getSize());
        }
        // 2. 获取合同基本信息
        List<ContractBaseInfo> startRentContractList = contractBaseInfoService.listByIds(startRentContractIds);
        if (CollectionUtil.isEmpty(startRentContractList)) {
            log.info("没有处于起租状态的合同,返回空列表");
            Page<MidQuarterClientListRSP> pageResult = buildPageResult(req, results);
            return PageR.of(results, pageResult.getTotal(), pageResult.getCurrent(), pageResult.getSize());
        }
        stopWatch.stop();
        log.info("季中初分-处理有效合同: 耗时={}ms", stopWatch.getTotalTimeMillis());
        stopWatch.start("处理客户分组和搜索");
        // 3. 按照客户ID分组
        Map<Long, List<ContractBaseInfo>> contractMap = startRentContractList.stream()
                .collect(Collectors.groupingBy(ContractBaseInfo::getClientId));

        // 4. 获取客户ID列表
        List<Long> clientIds = new ArrayList<>(contractMap.keySet());

        // 5. 关键词搜索过滤
        if (StrUtil.isNotBlank(req.getKeyword())) {
            clientIds = filterClientsByKeyword(clientIds, req.getKeyword());
            if (CollectionUtil.isEmpty(clientIds)) {
                log.info("关键词搜索无结果，返回空列表");
                Page<MidQuarterClientListRSP> pageResult = buildPageResult(req, results);
                return PageR.of(results, pageResult.getTotal(), pageResult.getCurrent(), pageResult.getSize());
            }
        }
        stopWatch.stop();
        log.info("季中初分-处理客户分组和搜索: 耗时={}ms", stopWatch.getTotalTimeMillis());
        stopWatch.start("获取客户基本信息");
        // 6. 获取客户基本信息
        List<Client> clientList = clientService.listByIds(clientIds);
        // 查询部门和主办名称
        List<Long> deptIdList = new ArrayList<>(clientList.size());
        List<Long> sponsorIdList = new ArrayList<>(clientList.size());
        for (Client client : clientList) {
            deptIdList.add(client.getBelongDeptId());
            sponsorIdList.add(client.getBelongSponsorId());
        }
        Map<Long, String> deptMap = id2NameService.deptId2Name(deptIdList);
        Map<Long, String> sponsorMap = id2NameService.sysUserId2Name(sponsorIdList);
        // 模型转换
        results = clientList.stream().map(item -> {
            MidQuarterClientListRSP rsp = new MidQuarterClientListRSP();
            rsp.setClientId(item.getId());
            rsp.setClientName(item.getClientName());
            rsp.setBelongDeptId(item.getBelongDeptId());
            rsp.setBelongDeptName(deptMap.get(item.getBelongDeptId()));
            rsp.setBelongSponsorId(item.getBelongSponsorId());
            rsp.setBelongSponsorName(sponsorMap.get(item.getBelongSponsorId()));
            return rsp;
        }).collect(Collectors.toList());
        stopWatch.stop();
        log.info("季中初分客户列表查询完成: 总客户数={}, 耗时={}ms",
                results.size(), stopWatch.getTotalTimeMillis());
        Page<MidQuarterClientListRSP> pageResult = buildPageResult(req, results);
        return PageR.of(results, pageResult.getTotal(), pageResult.getCurrent(), pageResult.getSize());
    }

    /**
     * 根据关键词过滤客户
     */
    private List<Long> filterClientsByKeyword(List<Long> clientIds, String keyword) {
        // 获取客户名称映射
        Map<Long, String> clientNameMap = clientService.listByIds(clientIds)
                                            .stream()
                                            .collect(Collectors.toMap(Client::getId, Client::getClientName));

        return clientIds.stream()
                .filter(clientId -> {
                    // 匹配客户名称
                    String clientName = clientNameMap.get(clientId);
                    return StrUtil.containsIgnoreCase(clientName, keyword);
                })
                .collect(Collectors.toList());
    }

    /**
     * 构建分页结果
     */
    private Page<MidQuarterClientListRSP> buildPageResult(MidQuarterClientListREQ req,List<MidQuarterClientListRSP> list) {
        Page<MidQuarterClientListRSP> pageResult = new Page<>();
        pageResult.setTotal(list.size());
        pageResult.setRecords(list);
        pageResult.setCurrent(req.getPage() != null ? req.getPage() : 1);
        pageResult.setSize(req.getPageSize() != null ? req.getPageSize() : 10);

        return pageResult;
    }
}
