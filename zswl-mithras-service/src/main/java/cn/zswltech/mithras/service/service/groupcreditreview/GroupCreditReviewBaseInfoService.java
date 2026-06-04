package cn.zswltech.mithras.service.service.groupcreditreview;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import cn.zswl.oss.core.OssClient;
import cn.zswltech.flow.core.api.FlowTaskApiService;
import cn.zswltech.flow.core.api.FlowUserApiService;
import cn.zswltech.flow.core.domain.req.task.ProcessPageReq;
import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.flow.core.enums.ProcessBusinessStatusEnum;
import cn.zswltech.flow.core.util.ApplicationContextUtil;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.gruul.dao.dal.entity.OrgDO;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.groupcreditreview.*;
import cn.zswltech.mithras.dto.groupcreditreview.baseinfo.*;
import cn.zswltech.mithras.dto.rating.ratingclient.RatingClientProjDetailRSP;
import cn.zswltech.mithras.factory.service.RatingClientService;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.service.convert.groupcreditreview.GroupCreditReviewBaseInfoConverter;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.service.enums.common.RecordStatus;
import cn.zswltech.mithras.credit.domain.groupcredit.review.enums.GroupCreditReviewProcessStatus;
import cn.zswltech.mithras.credit.infrastructure.persistence.groupcredit.review.dto.GroupCreditReviewListSelectDTO;
import cn.zswltech.mithras.credit.infrastructure.persistence.groupcredit.review.mapper.GroupCreditReviewBaseInfoMapper;
import cn.zswltech.mithras.credit.infrastructure.persistence.groupcredit.review.mapper.GroupCreditReviewBaseInfoLibMapper;
import cn.zswltech.mithras.service.mapper.model.MaterialsList;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.Client;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.CorpCommerceInfo;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.CorpCommerceInfoLib;
import cn.zswltech.mithras.credit.infrastructure.persistence.groupcredit.establish.model.GroupCreditEstablishBaseInfo;
import cn.zswltech.mithras.credit.infrastructure.persistence.groupcredit.review.model.GroupCreditReviewBaseInfo;
import cn.zswltech.mithras.credit.infrastructure.persistence.groupcredit.review.model.GroupCreditReviewBaseInfoLib;
import cn.zswltech.mithras.projectprocess.mapper.model.projreview.ProjReviewBaseInfo;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.others.SpringContextHolder;
import cn.zswltech.mithras.system.service.Id2NameService;
import cn.zswltech.mithras.service.service.Listener.client.ClientViewAuthorityEvent;
import cn.zswltech.mithras.system.service.SysUserService;
import cn.zswltech.mithras.service.service.client.ClientAuthorityService;
import cn.zswltech.mithras.service.service.client.ClientService;
import cn.zswltech.mithras.service.service.contract.ContractBaseInfoService;
import cn.zswltech.mithras.service.service.groupcreditestablish.GroupCreditEstablishBaseInfoService;
import cn.zswltech.mithras.service.service.lib.client.CorpCommerceInfoLibService;
import cn.zswltech.mithras.service.service.lib.groupcreditreview.handler.impl.GroupCreditReviewBaseInfoLibHandler;
import cn.zswltech.mithras.service.service.materialsfile.MaterialsListService;
import cn.zswltech.mithras.service.service.projfms.ProjProcessState;
import cn.zswltech.mithras.service.service.projreview.ProjReviewBaseInfoService;
import cn.zswltech.mithras.service.util.FlowUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static cn.hutool.core.bean.BeanUtil.copyProperties;
import static cn.hutool.core.text.CharSequenceUtil.isBlank;
import static cn.hutool.core.util.ObjectUtil.isNotEmpty;
import static cn.hutool.core.util.ObjectUtil.isNull;
import static cn.hutool.json.JSONUtil.toBean;
import static cn.zswltech.mithras.service.constant.ResultMsg.ONLY_BIZ_DEPT_DO;
import static cn.zswltech.mithras.service.enums.common.RecordStatus.*;

/**
 * @author wangchuanhao
 * @description 集团授信评审基本信息表
 * @date 2022-11-11
 */
@Slf4j
@Service
public class GroupCreditReviewBaseInfoService extends ServiceImpl<GroupCreditReviewBaseInfoMapper, GroupCreditReviewBaseInfo> implements GroupCreditReviewUpdateAdvice {

    @Value("${mithras.job.deptLeader}")
    private String deptLeaderJob;
    @Value("${mithras.job.divisionLeader}")
    private String divisionLeaderJob;
    @Resource
    private GroupCreditReviewBaseInfoMapper groupCreditReviewBaseInfoMapper;
    @Resource
    private GroupCreditReviewBaseInfoConverter groupCreditReviewBaseInfoConverter;
    @Resource
    private SysUserService sysUserService;
    @Resource
    private Id2NameService id2NameService;
    @Resource
    private GroupCreditEstablishBaseInfoService groupCreditEstablishBaseInfoService;
    @Resource
    private FlowUserApiService flowUserApiService;
    @Resource
    private GroupCreditReviewService groupCreditReviewService;
    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private GroupCreditReviewBaseInfoLibMapper baseInfoLibMapper;
    @Resource
    private GroupCreditReviewBaseInfoLibHandler baseInfoLibHandler;
    @Resource
    private ClientService clientService;
    @Resource
    private CorpCommerceInfoLibService corpCommerceInfoLibService;
    @Resource
    private FlowTaskApiService taskApiService;
    @Resource
    private RatingClientService ratingClientService;
    @Resource
    private MaterialsListService materialsListService;
    @Resource
    private OssClient ossClient;

    @Transactional(rollbackFor = Throwable.class)
    public GroupCreditReviewBaseInfoAddRSP add(GroupCreditReviewBaseInfoAddREQ req) {
        OrgDO bizOrgDO = sysUserService.currentUserBizDept();
        if (isNull(bizOrgDO)) {
            throw new MithrasException(ONLY_BIZ_DEPT_DO);
        }
        GroupCreditEstablishBaseInfo esBaseInfo = groupCreditEstablishBaseInfoService.getById(req.getGroupCreditEstablishId());
        if (!RecordStatus.TAKE_EFFECT.name().equals(esBaseInfo.getGroupCreditEstablishStatus())) {
            throw new MithrasException("该项目的立项未生效");
        }
        if (GroupCreditReviewProcessStatus.CHANGING_UN_SUBMIT.name().equals(esBaseInfo.getGroupCreditEstablishProcessStatus()) || GroupCreditReviewProcessStatus.CHANGING_UNDER_APPROVAL.name().equals(esBaseInfo.getGroupCreditEstablishProcessStatus())) {
            throw new MithrasException("当前立项数据处于变更未提交或者变更审批中，无法选取");
        }
        if (!SpringUtil.getBean(ClientAuthorityService.class).currentUserHasManagerAuth(esBaseInfo.getClientId())) {
            throw new MithrasException("无所选客户管护权，无权进行操作");
        }
        // 判断客户风控行业分类
        Client client = clientService.getById(esBaseInfo.getClientId());
        Assert.notNull(client, () -> MithrasException.newException("客户不存在"));
        CorpCommerceInfoLib corpCommerceInfoLib = corpCommerceInfoLibService.getNewestOne(client);
        Assert.notNull(corpCommerceInfoLib, () -> MithrasException.newException("无生效的客户工商信息数据"));
        Assert.notBlank(corpCommerceInfoLib.getRiskControlIndustryClassify(), () -> MithrasException.newException(String.format("请先至客户模块维护<%s>的“风控行业分类”，否则无法判断该项目是否需经董事会审议！", client.getClientName())));
        // 生成授信评审数据
        GroupCreditReviewBaseInfo info = establishToReview(esBaseInfo);
        info.setGroupCreditReviewStatus(NEW.name());
        info.setGroupCreditReviewProcessStatus(GroupCreditReviewProcessStatus.NEW_UN_SUBMIT.name());
        info.setGroupCreditEstablishId(esBaseInfo.getId());
        groupCreditReviewBaseInfoMapper.insert(info);
        // 从授信立项拷贝文件
        this.copyFromProjEstablishClientFile(info.getId(), req.getGroupCreditEstablishId());
        // 通知客户权限变更
        ApplicationContextUtil.getApplicationContext().publishEvent(
                new ClientViewAuthorityEvent(new ClientViewAuthorityEvent.ClientViewAuthorityInfo(
                        BusinessModuleEnum.GROUP_CREDIT_REVIEW,
                        info.getId().toString(),
                        "集团授信评审-创建")
                )
        );
        GroupCreditReviewBaseInfoAddRSP rsp = new GroupCreditReviewBaseInfoAddRSP();
        rsp.setId(info.getId());
        return rsp;
    }

    private void copyFromProjEstablishClientFile(Long groupProjReviewId, Long groupProjEstablishId) {
        LambdaQueryWrapper<MaterialsList> query = Wrappers.lambdaQuery();
        query.eq(MaterialsList::getBusinessType, BusinessModuleEnum.GROUP_CREDIT_ESTABLISH_CLIENT.name());
        query.eq(MaterialsList::getBelongId, groupProjEstablishId);
        List<MaterialsList> sourceList = materialsListService.list(query);
        if (CollectionUtil.isEmpty(sourceList)) {
            return;
        }
        List<MaterialsList> targetList = new LinkedList<>();
        sourceList.forEach(item -> {
            String bizPath = BusinessModuleEnum.GROUP_CREDIT_REVIEW_CLIENT.name() + "/" + groupProjReviewId + "/" + item.getMaterialsType();
            if (StrUtil.isNotBlank(item.getMaterialSubType())) {
                bizPath = bizPath + "/" + item.getMaterialSubType();
            }
            String newFilePath = ossClient.getBasePath() + bizPath;
            String newOssFilename = bizPath + "/" + item.getFilename();
            try {
                ossClient.copy(ossClient.getBasePath() + item.getOssFilename(), ossClient.getBasePath() + newOssFilename);
            } catch (Exception e) {
                log.error("授信评审从授信立项拷贝客户文件发生异常[groupProjReviewId:{}, item:{}]", groupProjReviewId, JSONUtil.toJsonStr(item), e);
                // 拷贝失败的话就不存文件记录了
                return;
            }
            MaterialsList newMaterial = new MaterialsList();
            newMaterial.setBelongId(groupProjReviewId);
            newMaterial.setBusinessType(BusinessModuleEnum.GROUP_CREDIT_REVIEW_CLIENT.name());
            newMaterial.setMaterialsType(item.getMaterialsType());
            newMaterial.setMaterialSubType(item.getMaterialSubType());
            newMaterial.setOssFilename(newOssFilename);
            newMaterial.setSuffix(item.getSuffix());
            newMaterial.setFilename(item.getFilename());
            newMaterial.setFilePath(newFilePath);
            newMaterial.setSystemGenerate(item.getSystemGenerate());
            newMaterial.setSourceBusinessKey(item.getSourceBusinessKey());
            targetList.add(newMaterial);
        });
        if (CollectionUtil.isNotEmpty(targetList)) {
            materialsListService.saveBatch(targetList);
        }
    }

    @Transactional(rollbackFor = Throwable.class)
    public void doModifyProjectApprovalAmount(Long id, Long projectApprovalAmount) {
        LambdaUpdateWrapper<GroupCreditReviewBaseInfo> updateWrapper = new LambdaUpdateWrapper();
        updateWrapper.set(GroupCreditReviewBaseInfo::getProjectApprovalAmount, projectApprovalAmount);
        updateWrapper.eq(GroupCreditReviewBaseInfo::getId, id);
        groupCreditReviewBaseInfoMapper.update(null, updateWrapper);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void modify(GroupCreditReviewBaseInfoModifyREQ req) {
        saveCheck(req.getId());
        GroupCreditReviewBaseInfo originalInfo = groupCreditReviewBaseInfoMapper.selectById(req.getId());
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        GroupCreditReviewBaseInfo info = groupCreditReviewBaseInfoConverter.modifyREQtoEntity(req);
        groupCreditReviewBaseInfoMapper.updateAnnotationIncludeNullById(info);
        // 更新审批流审批人
        ProcessResp runningProcess = groupCreditReviewService.findRelatedProcess(req.getId());
        if (Objects.nonNull(runningProcess)) {
            if (!Objects.equals(originalInfo.getBizDeptLeaderId(), req.getBizDeptLeaderId())) {
                flowUserApiService.setTaskApprover(FlowUtil.buildSetApproverReq(ListUtil.toList(req.getBizDeptLeaderId()), runningProcess.getProcessInstanceId(), "userTask_deptMaster"));
            }
            if (!Objects.equals(originalInfo.getBizDivisionLeaderId(), req.getBizDivisionLeaderId())) {
                flowUserApiService.setTaskApprover(FlowUtil.buildSetApproverReq(ListUtil.toList(req.getBizDivisionLeaderId()), runningProcess.getProcessInstanceId(), "userTask_bizDivisionLeader"));
            }
            if (!Objects.equals(originalInfo.getRiskControlManagerId(), req.getRiskControlManagerId())) {
                flowUserApiService.setTaskApprover(FlowUtil.buildSetApproverReq(ListUtil.toList(req.getRiskControlManagerId()), runningProcess.getProcessInstanceId(), "userTask_riskManager"));
            }
            if (!Objects.equals(originalInfo.getLegalManagerUserId(), req.getLegalManagerUserId())) {
                flowUserApiService.setTaskApprover(FlowUtil.buildSetApproverReq(ListUtil.toList(req.getLegalManagerUserId()), runningProcess.getProcessInstanceId(), "userTask_lawManager"));
            }
        }
        recordStatus(req.getId());
        // 通知客户权限变更
        ApplicationContextUtil.getApplicationContext().publishEvent(
                new ClientViewAuthorityEvent(new ClientViewAuthorityEvent.ClientViewAuthorityInfo(
                        BusinessModuleEnum.GROUP_CREDIT_REVIEW,
                        info.getId().toString(),
                        "集团授信评审-修改")
                )
        );
    }

    public PageR<GroupCreditReviewListRSP> list(GroupCreditReviewListREQ req) {
        GroupCreditReviewListSelectDTO dto = new GroupCreditReviewListSelectDTO();
        copyProperties(req, dto, new CopyOptions().ignoreError());
        List<Long> canViewDeptIds = sysUserService.canViewDeptIds();
        boolean isBizUser = null != canViewDeptIds;
        if (isBizUser && canViewDeptIds.isEmpty()) {
            //防止sql in报错
            canViewDeptIds.add(Long.MIN_VALUE);
        }
        dto.setIsBizUser(isBizUser);
        dto.setDeptIdList(canViewDeptIds);
        dto.setCurrentUserId(AccountUtil.getLoginInfo().getId());

        dto.setCreateFrom(req.getCreateFrom() == null ? null : req.getCreateFrom().atStartOfDay());
        dto.setCreateTo(req.getCreateTo() == null ? null : req.getCreateTo().plusDays(1).atStartOfDay());
        dto.setUpdateFrom(req.getUpdateFrom() == null ? null : req.getUpdateFrom().atStartOfDay());
        dto.setUpdateTo(req.getUpdateTo() == null ? null : req.getUpdateTo().plusDays(1).atStartOfDay());
        Page<GroupCreditReviewBaseInfo> baseInfoPage = groupCreditReviewBaseInfoMapper.myList(new Page<>(req.getPage(), req.getPageSize()), dto);
        List<GroupCreditReviewListRSP> resultList = new ArrayList<>(baseInfoPage.getRecords().size());

        Set<Long> sysUserIds = new HashSet<>();
        Set<Long> clientIds = new HashSet<>();
        Set<Long> deptIds = new HashSet<>();
        for (GroupCreditReviewBaseInfo record : baseInfoPage.getRecords()) {
            GroupCreditReviewListRSP rsp = new GroupCreditReviewListRSP();
            copyProperties(record, rsp, new CopyOptions().ignoreError());
            rsp.setProjCosponsorUserIds(isBlank(record.getProjCosponsorUserIds()) ? null : toBean(record.getProjCosponsorUserIds(), new TypeReference<List<Long>>() {
            }, true));
            sysUserIds.add(rsp.getProjSponsorUserId());
            if (CollUtil.isNotEmpty(rsp.getProjCosponsorUserIds())) {
                sysUserIds.addAll(rsp.getProjCosponsorUserIds());
            }
            clientIds.add(rsp.getClientId());
            deptIds.add(rsp.getBizDeptId());
            resultList.add(rsp);
        }
        Map<Long, String> clientMap = id2NameService.clientId2Name(clientIds);
        Map<Long, String> sysUserMap = id2NameService.sysUserId2Name(sysUserIds);
        Map<Long, String> deptMap = id2NameService.deptId2Name(deptIds);
        for (GroupCreditReviewListRSP rsp : resultList) {
            rsp.setBizDeptName(deptMap.get(rsp.getBizDeptId()));
            rsp.setProjSponsorUserName(sysUserMap.get(rsp.getProjSponsorUserId()));
            if (CollUtil.isNotEmpty(rsp.getProjCosponsorUserIds())) {
                rsp.setProjCosponsorUserNames(rsp.getProjCosponsorUserIds().stream().map(sysUserMap::get).collect(Collectors.toList()));
            }
            rsp.setClientName(clientMap.get(rsp.getClientId()));
        }
        return PageR.of(resultList, baseInfoPage.getTotal(),
                baseInfoPage.getPages(),
                baseInfoPage.getCurrent(),
                baseInfoPage.getSize());
    }

    public GroupCreditReviewBaseInfoDetailRSP detail(Long groupCreditReviewId, String version) {
        if (StringUtils.isBlank(version)) {
            GroupCreditReviewBaseInfo baseInfo = groupCreditReviewBaseInfoMapper.selectById(groupCreditReviewId);
            if (Objects.isNull(baseInfo)) {
                throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
            }
            GroupCreditReviewBaseInfoDetailRSP rsp = groupCreditReviewBaseInfoConverter.entityToDetailRSP(baseInfo);
            join(rsp);
            rsp.setClientRiskExposure(contractBaseInfoService.getGroupCreditStockRiskExposure(baseInfo.getClientId()));
            // 判断是否业务部门
            rsp.setIsBizDept(Objects.nonNull(sysUserService.currentUserBizDept()));
            // 详情页实时获取客户最新的风控行业分类
            CorpCommerceInfoLib corpCommerceInfoLib = corpCommerceInfoLibService.getNewestOne(baseInfo.getClientId());
            rsp.setRiskControlIndustryClassify(Optional.ofNullable(corpCommerceInfoLib).map(CorpCommerceInfo::getRiskControlIndustryClassify).orElse(null));
            rsp.setApprovedAmount(baseInfo.getProjectApprovalAmount());
            // 更新评级
            final GroupCreditReviewInfoUpdateRatingRSP updateRating = updateRating(new GroupCreditReviewBaseInfoUpdateRatingREQ(groupCreditReviewId));
            rsp.setClientRatingScoreId(updateRating.getClientRatingScoreId());
            rsp.setClientRatingScore(updateRating.getClientRatingScore());
            return rsp;
        } else {
            GroupCreditReviewBaseInfoLib versionLib = baseInfoLibMapper.selectOne(Wrappers.<GroupCreditReviewBaseInfoLib>lambdaQuery().eq(GroupCreditReviewBaseInfoLib::getOriginId, groupCreditReviewId).eq(GroupCreditReviewBaseInfoLib::getVersion, version).last("LIMIT 1"));
            if (Objects.isNull(versionLib)) {
                throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
            }
            return baseInfoLibHandler.actualLib2Rsp(versionLib);
        }
    }

    /**
     * 用于详情查询填充冗余字段给前端显示
     *
     * @param rsp
     */
    public void join(GroupCreditReviewBaseInfoDetailRSP rsp) {
        Set<Long> sysUserIds = new HashSet<>();
        Set<Long> clientIds = new HashSet<>();
        Set<Long> deptIds = new HashSet<>();
        sysUserIds.add(rsp.getProjSponsorUserId());
        if (CollUtil.isNotEmpty(rsp.getProjCosponsorUserIds())) {
            sysUserIds.addAll(rsp.getProjCosponsorUserIds());
        }
        sysUserIds.add(rsp.getBizDeptLeaderId());
        sysUserIds.add(rsp.getBizDivisionLeaderId());
        sysUserIds.add(rsp.getRiskControlManagerId());
        sysUserIds.add(rsp.getLegalManagerUserId());
        deptIds.add(rsp.getBizDeptId());

        clientIds.add(rsp.getClientId());
        Map<Long, String> clientMap = id2NameService.clientId2Name(clientIds);
        Map<Long, String> sysUserMap = id2NameService.sysUserId2Name(sysUserIds);
        Map<Long, String> deptMap = id2NameService.deptId2Name(deptIds);
        rsp.setClientName(clientMap.get(rsp.getClientId()));
        rsp.setBizDeptName(deptMap.get(rsp.getBizDeptId()));
        rsp.setProjSponsorUserName(sysUserMap.get(rsp.getProjSponsorUserId()));
        if (CollUtil.isNotEmpty(rsp.getProjCosponsorUserIds())) {
            rsp.setProjCosponsorUserNames(rsp.getProjCosponsorUserIds().stream().map(sysUserMap::get).collect(Collectors.toList()));
        }
        rsp.setBizDeptLeaderName(sysUserMap.get(rsp.getBizDeptLeaderId()));
        rsp.setBizDivisionLeaderName(sysUserMap.get(rsp.getBizDivisionLeaderId()));
        rsp.setRiskControlManagerName(sysUserMap.get(rsp.getRiskControlManagerId()));
        rsp.setLegalManagerName(sysUserMap.get(rsp.getLegalManagerUserId()));
    }

    private GroupCreditReviewBaseInfo establishToReview(GroupCreditEstablishBaseInfo establishBaseInfo) {
        if (establishBaseInfo == null) {
            return null;
        }
        GroupCreditReviewBaseInfo groupCreditReviewBaseInfo = new GroupCreditReviewBaseInfo();
        groupCreditReviewBaseInfo.setClientId(establishBaseInfo.getClientId());
        groupCreditReviewBaseInfo.setProjName(establishBaseInfo.getProjName());
        groupCreditReviewBaseInfo.setProjCode(establishBaseInfo.getProjCode());
        groupCreditReviewBaseInfo.setProjBackground(establishBaseInfo.getProjBackground());
        groupCreditReviewBaseInfo.setApplyCreditAmount(establishBaseInfo.getApplyCreditAmount());
        groupCreditReviewBaseInfo.setValidMonthCount(establishBaseInfo.getValidMonthCount());
        groupCreditReviewBaseInfo.setCreditAmountLoop(establishBaseInfo.getCreditAmountLoop());
        groupCreditReviewBaseInfo.setProjSponsorUserId(establishBaseInfo.getProjSponsorUserId());
        groupCreditReviewBaseInfo.setProjCosponsorUserIds(establishBaseInfo.getProjCosponsorUserIds());
        groupCreditReviewBaseInfo.setBizDeptId(establishBaseInfo.getBizDeptId());
        groupCreditReviewBaseInfo.setBizDeptLeaderId(establishBaseInfo.getBizDeptLeaderId());
        groupCreditReviewBaseInfo.setBizDivisionLeaderId(establishBaseInfo.getBizDivisionLeaderId());
        if (establishBaseInfo.getRiskControlManagerId() != null) {
            List<Long> ids = JSON.parseObject(establishBaseInfo.getRiskControlManagerId(), new com.alibaba.fastjson.TypeReference<List<Long>>() {
            });
            groupCreditReviewBaseInfo.setRiskControlManagerId(ids.get(0));
        }
        return groupCreditReviewBaseInfo;
    }

    public List<GroupCreditReviewVagueListRSP> vagueQuery(GroupCreditReviewVagueListREQ req) {
        LambdaQueryWrapper<GroupCreditReviewBaseInfo> queryWrapper = Wrappers.<GroupCreditReviewBaseInfo>lambdaQuery()
                .eq(GroupCreditReviewBaseInfo::getGroupCreditReviewStatus, RecordStatus.TAKE_EFFECT.name());
        if (req.getProjVagueName() != null) {
            queryWrapper.like(GroupCreditReviewBaseInfo::getProjName, req.getProjVagueName());
        }
        queryWrapper.orderByDesc(GroupCreditReviewBaseInfo::getId);
        List<GroupCreditReviewBaseInfo> groupCreditReviewBaseInfos = baseMapper.selectList(queryWrapper);
        if (CollectionUtils.isEmpty(groupCreditReviewBaseInfos)) {
            return new ArrayList<>();
        }
        Set<Long> clientIds = groupCreditReviewBaseInfos.stream().map(GroupCreditReviewBaseInfo::getClientId).collect(Collectors.toSet());
        Map<Long, String> clientNameMap = id2NameService.clientId2Name(clientIds);
        List<GroupCreditReviewVagueListRSP> resList = new ArrayList<>();
        for (GroupCreditReviewBaseInfo baseInfo : groupCreditReviewBaseInfos) {
            GroupCreditReviewVagueListRSP one = BeanUtil.copyProperties(baseInfo, GroupCreditReviewVagueListRSP.class);
            one.setClientNames(clientNameMap.get(baseInfo.getClientId()) == null ? new ArrayList<>() : Collections.singletonList(clientNameMap.get(baseInfo.getClientId())));
            resList.add(one);
        }
        return resList;
    }

    /**
     * 维护失效状态
     **/
    @Transactional(rollbackFor = Throwable.class)
    public void doExpire(Integer days) {
        //1.查找所有立项审批通过时间
        ProcessPageReq flowReq = new ProcessPageReq();
        flowReq.setModelKeyList(Collections.singletonList(ProcessModelTypeEnum.GroupCreditReviewCreateFlow.name()));
        flowReq.setSortType(1);
        flowReq.setPageIndex(1);
        flowReq.setPageSize(Integer.MAX_VALUE);
        flowReq.setProcessStatusList(ListUtil.toList(2, 6));
        cn.zswltech.flow.core.util.Page<ProcessResp> flowRespPage = taskApiService.queryProcess(flowReq);
        if (ObjectUtil.isEmpty(flowRespPage) || ObjectUtil.isEmpty(flowRespPage.getContents())) {
            return;
        }
        Set<Long> needExpireIds = new HashSet<>();
        List<Long> groupReviewIds = flowRespPage.getContents().stream().filter(e -> isNotEmpty(e.getEndTime())).filter(e -> LocalDateTimeUtil.of(e.getEndTime()).plusDays(days).isBefore(LocalDateTime.now())).map(ProcessResp::getBusinessKey).map(Long::valueOf).collect(Collectors.toList());
        if (ObjectUtil.isEmpty(groupReviewIds)) {
            return;
        }
        //剔除已经关闭或失效的
        groupReviewIds = this.baseMapper.selectList(Wrappers.<GroupCreditReviewBaseInfo>lambdaQuery()
                .in(GroupCreditReviewBaseInfo::getId, groupReviewIds)
                .notIn(GroupCreditReviewBaseInfo::getGroupCreditReviewStatus, CLOSED.name(), EXPIRE.name())).stream().map(GroupCreditReviewBaseInfo::getId).collect(Collectors.toList());
        if (ObjectUtil.isEmpty(groupReviewIds)) {
            return;
        }
        //2.剔除已经提起项目立项审批的
        List<ProjReviewBaseInfo> projReviewBaseInfos = SpringContextHolder.getBean(ProjReviewBaseInfoService.class).list(Wrappers.<ProjReviewBaseInfo>lambdaQuery()
                .in(ProjReviewBaseInfo::getGroupCreditReviewId, groupReviewIds));
        //需要修改为释放
        if (ObjectUtil.isNotEmpty(projReviewBaseInfos)) {
            Map<Long, List<ProjReviewBaseInfo>> projEsId2Review = projReviewBaseInfos.stream().collect(Collectors.groupingBy(ProjReviewBaseInfo::getGroupCreditReviewId));
            for (Long esId : groupReviewIds) {
                List<ProjReviewBaseInfo> tmpReviewBaseInfos = projEsId2Review.get(esId);
                if (ObjectUtil.isEmpty(tmpReviewBaseInfos)) {
                    //未评审的需要释放
                    needExpireIds.add(esId);
                } else {
                    //已经有立项需要判断是否有提交过
                    boolean needExpire = true;
                    for (ProjReviewBaseInfo baseInfo : tmpReviewBaseInfos) {
                        if (!(ObjectUtil.equals(baseInfo.getProjReviewStatus(), NEW.name()) && (ObjectUtil.equals(baseInfo.getProjReviewProcessStatus(), ProjProcessState.NEW_UN_SUBMIT.name()) || ObjectUtil.equals(baseInfo.getProjReviewProcessStatus(), ProjProcessState.UN_SUBMIT.name())))) {
                            needExpire = false;
                            break;
                        }
                    }
                    if (needExpire) {
                        needExpireIds.add(esId);
                    }
                }
            }
        } else {
            needExpireIds = new HashSet<>(groupReviewIds);
        }
        //释放
        if (ObjectUtil.isNotEmpty(needExpireIds)) {
            LambdaUpdateWrapper<GroupCreditReviewBaseInfo> wrapper = new LambdaUpdateWrapper<>();
            wrapper.set(GroupCreditReviewBaseInfo::getGroupCreditReviewStatus, EXPIRE.name());
            wrapper.in(GroupCreditReviewBaseInfo::getId, needExpireIds);
            SpringContextHolder.getBean(GroupCreditReviewBaseInfoService.class).update(wrapper);
        }
    }

    public void checkClientRating(GroupCreditReviewBaseInfo info) {
        // 所有客户均需在提交授信评审前完成授信主体的客户评级
        final List<Long> userList = getUserList(info);
        if (CollectionUtil.isEmpty(userList)) {
            throw new MithrasException("无生效的授信主体客户评级信息");
        }
        RatingClientProjDetailRSP flushAfterMainLessee = ratingClientService.getEffectRating(60, info.getClientId(), userList, new Date());
        if (Objects.isNull(flushAfterMainLessee) || Objects.isNull(flushAfterMainLessee.getId())) {
            throw new MithrasException("无生效的授信主体客户评级信息");
        }
        info.setClientRatingScoreId(flushAfterMainLessee.getId());
        info.setClientRatingScore(flushAfterMainLessee.getRatingFinalScore());
    }

    public GroupCreditReviewInfoUpdateRatingRSP updateRating(GroupCreditReviewBaseInfoUpdateRatingREQ req) {
        GroupCreditReviewInfoUpdateRatingRSP rsp = new GroupCreditReviewInfoUpdateRatingRSP();
        Date date = new Date();
        if (Objects.nonNull(req.getId())) {
            final GroupCreditReviewBaseInfo reviewBaseInfo = this.getById(req.getId());
            rsp.setClientRatingScore(reviewBaseInfo.getClientRatingScore());
            rsp.setClientRatingScoreId(reviewBaseInfo.getClientRatingScoreId());
            if (reviewBaseInfo != null) {
                final List<Long> userList = getUserList(reviewBaseInfo);
                final String groupCreditEstablishStatus = reviewBaseInfo.getGroupCreditReviewStatus();
                if (CollectionUtil.isNotEmpty(userList) && Objects.equals(groupCreditEstablishStatus, NEW.name()) & needUpdateByProcessJudge(reviewBaseInfo.getId())) {
                    RatingClientProjDetailRSP flushAfterMainLessee = ratingClientService.getEffectRating(60, reviewBaseInfo.getClientId(), userList, date);
                    LambdaUpdateWrapper<GroupCreditReviewBaseInfo> updateWrapper = Wrappers.lambdaUpdate();
                    updateWrapper.eq(GroupCreditReviewBaseInfo::getId, req.getId());
                    updateWrapper.set(GroupCreditReviewBaseInfo::getRatingUpdateTime, LocalDateTime.now());
                    if (flushAfterMainLessee != null) {
                        updateWrapper.set(GroupCreditReviewBaseInfo::getClientRatingScoreId, flushAfterMainLessee.getId());
                        updateWrapper.set(GroupCreditReviewBaseInfo::getClientRatingScore, flushAfterMainLessee.getRatingFinalScore());
                        rsp.setClientRatingScoreId(flushAfterMainLessee.getId());
                        rsp.setClientRatingScore(flushAfterMainLessee.getRatingFinalScore());
                    }
                    this.update(updateWrapper);
                }
            }
        }
        return rsp;
    }

    public GroupCreditReviewRatingCheckRSP checkRatingInfo(Long id) {
        GroupCreditReviewRatingCheckRSP rsp = new GroupCreditReviewRatingCheckRSP();
        final GroupCreditReviewBaseInfo baseInfo = this.getById(id);
        if (Objects.isNull(baseInfo)) {
            throw new MithrasException("集团授信评审数据不存在");
        }
        rsp.setClientId(baseInfo.getClientId());
        final List<Long> userList = getUserList(baseInfo);
        if (CollectionUtil.isNotEmpty(userList)) {
            RatingClientProjDetailRSP flushAfterMainLessee = ratingClientService.getEffectRating(60, baseInfo.getClientId(), userList, new Date());
            Boolean ratingClientIsDone = Objects.nonNull(flushAfterMainLessee);
            if (ratingClientIsDone) {
                ratingClientIsDone = Objects.nonNull(flushAfterMainLessee.getId());
            }
            rsp.setRatingClientIsDone(ratingClientIsDone);
        } else {
            rsp.setRatingClientIsDone(false);
        }
        return rsp;
    }

    List<Long> getUserList(GroupCreditReviewBaseInfo info) {
        List<Long> userIds = new ArrayList<>();
        final Long projSponsorUserId = info.getProjSponsorUserId();
        if (projSponsorUserId != null) {
            userIds.add(projSponsorUserId);
        }
        final String projCosponsorUserIds = info.getProjCosponsorUserIds();
        if (StrUtil.isNotBlank(projCosponsorUserIds)) {
            userIds.addAll(JSONUtil.toList(projCosponsorUserIds, Long.class));
        }
        return userIds;
    }

    /**
     * 项目经理在提交流程前或流程中撤回/退回节点在项目经理时
     *
     * @param id 主键id
     * @return 是否可以更新
     */
    public boolean needUpdateByProcessJudge(Long id) {
        ProcessPageReq processPageReq = new ProcessPageReq();
        processPageReq.setPageIndex(1);
        processPageReq.setPageSize(1);
        processPageReq.setBusinessKey(String.valueOf(id));
        final List<String> modelKeyList = Arrays.asList(ProcessModelTypeEnum.GroupCreditReviewCreateFlow.name());
        processPageReq.setModelKeyList(modelKeyList);
//        processPageReq.setModelKeyList(BusinessModuleEnum.GROUP_CREDIT_REVIEW.getModelKeyList());
        cn.zswltech.flow.core.util.Page<ProcessResp> processRespPage = taskApiService.queryProcess(processPageReq);
        ProcessResp processResp = processRespPage.getContents().stream().findFirst().orElse(null);
        if (Objects.isNull(processResp)) {
            return true;
        } else {
            return processResp.getProcessStatus().equals(ProcessBusinessStatusEnum.RUNNING.getType()) && processResp.getCurTaskActivityIds().contains("userTask_startUser");
        }
    }
}