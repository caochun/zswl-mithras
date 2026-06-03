package cn.zswltech.mithras.service.service.groupcreditestablish;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
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
import cn.zswltech.mithras.dto.groupcreditestablish.*;
import cn.zswltech.mithras.dto.groupcreditestablish.baseinfo.*;
import cn.zswltech.mithras.dto.projestablish.baseinfo.jsonbean.ProjEstablishPersonInfo;
import cn.zswltech.mithras.dto.rating.ratingclient.RatingClientProjDetailRSP;
import cn.zswltech.mithras.factory.service.RatingClientService;
import cn.zswltech.mithras.service.config.redis.RedisDistLock;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.service.convert.groupcreditestablish.GroupCreditEstablishBaseInfoConverter;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.enums.CacheEnum;
import cn.zswltech.mithras.service.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.service.enums.client.CorporationClientMaterialSubTypeEnum;
import cn.zswltech.mithras.service.enums.client.CorporationClientMaterialTypeEnum;
import cn.zswltech.mithras.service.enums.client.NormalClientMaterialTypeEnum;
import cn.zswltech.mithras.credit.domain.groupcredit.establish.enums.GroupCreditEstablishMaterialsEnum;
import cn.zswltech.mithras.credit.domain.groupcredit.establish.enums.GroupCreditEstablishProcessStatus;
import cn.zswltech.mithras.credit.domain.groupcredit.review.enums.GroupCreditReviewProcessStatus;
import cn.zswltech.mithras.service.enums.projestablish.ProjEstablishMaterialsEnum;
import cn.zswltech.mithras.riskcontrol.common.RiskControlIndustryClassify;
import cn.zswltech.mithras.service.mapper.client.ClientMapper;
import cn.zswltech.mithras.credit.infrastructure.persistence.groupcredit.establish.dto.GroupCreditEstablishListSelectDTO;
import cn.zswltech.mithras.credit.infrastructure.persistence.groupcredit.establish.mapper.GroupCreditEstablishBaseInfoMapper;
import cn.zswltech.mithras.credit.infrastructure.persistence.groupcredit.establish.mapper.GroupCreditEstablishBaseInfoLibMapper;
import cn.zswltech.mithras.service.mapper.model.BaseModel;
import cn.zswltech.mithras.service.mapper.model.MaterialsList;
import cn.zswltech.mithras.service.mapper.model.client.Client;
import cn.zswltech.mithras.service.mapper.model.client.CorpCommerceInfo;
import cn.zswltech.mithras.credit.infrastructure.persistence.groupcredit.establish.model.GroupCreditEstablishBaseInfo;
import cn.zswltech.mithras.credit.infrastructure.persistence.groupcredit.establish.model.GroupCreditEstablishBaseInfoLib;
import cn.zswltech.mithras.credit.infrastructure.persistence.groupcredit.review.model.GroupCreditReviewBaseInfo;
import cn.zswltech.mithras.service.mapper.model.projestablish.ProjEstablishBaseInfo;
import cn.zswltech.mithras.service.others.Const;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.others.SpringContextHolder;
import cn.zswltech.mithras.service.others.Util;
import cn.zswltech.mithras.service.service.Id2NameService;
import cn.zswltech.mithras.service.service.Listener.client.ClientViewAuthorityEvent;
import cn.zswltech.mithras.service.service.SysUserService;
import cn.zswltech.mithras.service.service.client.ClientAuthorityService;
import cn.zswltech.mithras.service.service.client.ClientService;
import cn.zswltech.mithras.service.service.client.CorpCommerceInfoService;
import cn.zswltech.mithras.service.service.contract.ContractBaseInfoService;
import cn.zswltech.mithras.service.service.groupcreditreview.GroupCreditReviewBaseInfoService;
import cn.zswltech.mithras.service.service.lib.groupcreditestablish.handler.impl.GroupCreditEstablishBaseInfoLibHandler;
import cn.zswltech.mithras.service.service.materialsfile.MaterialsListService;
import cn.zswltech.mithras.service.util.FlowUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import liquibase.pro.packaged.S;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static cn.hutool.core.bean.BeanUtil.copyProperties;
import static cn.hutool.core.text.CharSequenceUtil.isBlank;
import static cn.hutool.core.util.NumberUtil.decimalFormat;
import static cn.hutool.core.util.ObjectUtil.*;
import static cn.hutool.core.util.RandomUtil.randomInt;
import static cn.hutool.json.JSONUtil.toBean;
import static cn.zswltech.mithras.service.constant.ResultMsg.CONCURRENT_OPERATION;
import static cn.zswltech.mithras.service.constant.ResultMsg.ONLY_BIZ_DEPT_DO;
import static cn.zswltech.mithras.service.enums.common.RecordStatus.*;

/**
 * @author wangchuanhao
 * @description 集团授信立项基本信息表
 * @date 2022-11-11
 */
@Slf4j
@Service
public class GroupCreditEstablishBaseInfoService extends ServiceImpl<GroupCreditEstablishBaseInfoMapper, GroupCreditEstablishBaseInfo> implements GroupCreditEstablishUpdateAdvice {

    @Value("${mithras.job.deptLeader}")
    private String deptLeaderJob;
    @Value("${mithras.job.divisionLeader}")
    private String divisionLeaderJob;
    @Resource
    private GroupCreditEstablishBaseInfoMapper groupCreditEstablishBaseInfoMapper;
    @Resource
    private GroupCreditEstablishBaseInfoConverter groupCreditEstablishBaseInfoConverter;
    @Resource
    private SysUserService sysUserService;
    @Resource
    private Id2NameService id2NameService;
    @Resource
    private RedisDistLock redisDistLock;
    @Resource
    private GroupCreditEstablishService groupCreditEstablishService;
    @Resource
    private FlowUserApiService flowUserApiService;
    @Resource
    private ClientMapper clientMapper;
    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private GroupCreditEstablishBaseInfoLibMapper baseInfoLibMapper;
    @Resource
    private GroupCreditEstablishBaseInfoLibHandler baseInfoLibHandler;
    @Resource
    private FlowTaskApiService taskApiService;
    @Resource
    private RatingClientService ratingClientService;
    @Resource
    private CorpCommerceInfoService corpCommerceInfoService;
    @Resource
    private MaterialsListService materialsListService;
    @Resource
    private OssClient ossClient;
    @Resource
    private ClientService clientService;

    @Transactional(rollbackFor = Throwable.class)
    public GroupCreditEstablishBaseInfo add(GroupCreditEstablishBaseInfoAddREQ req) {
        if (!SpringUtil.getBean(ClientAuthorityService.class).currentUserHasManagerAuth(req.getClientId())) {
            throw new MithrasException("无所选客户管护权，无权进行操作");
        }
        String lockKey = CacheEnum.getCacheKey(CacheEnum.GROUP_CREDIT_ESTABLISH_ADD_LOCK, req.getProjName());
        boolean lock = redisDistLock.tryLock(lockKey, 2000L, CacheEnum.GROUP_CREDIT_ESTABLISH_ADD_LOCK.getExpire());
        if (!lock) {
            throw new MithrasException(CONCURRENT_OPERATION);
        }
        OrgDO bizOrgDO = sysUserService.currentUserBizDept();
        if (isNull(bizOrgDO)) {
            throw new MithrasException(ONLY_BIZ_DEPT_DO);
        }
        Client client = clientMapper.selectById(req.getClientId());
        if (Objects.isNull(client) || !TAKE_EFFECT.name().equals(client.getClientStatus())) {
            throw new MithrasException("客户不存在或未生效");
        }
        if (isNotNull(baseMapper.selectOne(Wrappers.<GroupCreditEstablishBaseInfo>lambdaQuery()
                .notIn(GroupCreditEstablishBaseInfo::getGroupCreditEstablishStatus, CLOSED.name(), EXPIRE.name())
                .eq(GroupCreditEstablishBaseInfo::getProjName, req.getProjName())))) {
            throw new MithrasException("项目名称已存在");
        }
        GroupCreditEstablishBaseInfo info = copyProperties(req, GroupCreditEstablishBaseInfo.class);
        info.setGroupCreditEstablishStatus(NEW.name());
        info.setGroupCreditEstablishProcessStatus(GroupCreditEstablishProcessStatus.NEW_UN_SUBMIT.name());
        List<Long> riskControlManagerIds = new ArrayList<>(sysUserService.getRiskControlManagerIdsByDeptCode(bizOrgDO.getCode()));
        info.setRiskControlManagerId(JSON.toJSONString(riskControlManagerIds));

        baseMapper.insert(info);

        //生成项目编号
        int maxCount = 0;
        while (maxCount++ < 10) {
            try {
                Long seqId = maxSeqId() + 1;
                String projCode = generateProjCode(seqId);
                GroupCreditEstablishBaseInfo toUpdate = new GroupCreditEstablishBaseInfo();
                toUpdate.setId(info.getId());
                toUpdate.setProjCode(projCode);
                toUpdate.setTypeSeqId(seqId);
                //自动设置项目主办、业务部门，业务部门负责人，业务部门分管领导
                toUpdate.setProjSponsorUserId(AccountUtil.getLoginInfo().getId());
                toUpdate.setBizDeptId(bizOrgDO.getId());
                toUpdate.setBizDeptLeaderId(sysUserService.getUserIdByOrgJob(bizOrgDO.getId(), deptLeaderJob));
                toUpdate.setBizDivisionLeaderId(sysUserService.getUserIdByOrgJob(bizOrgDO.getId(), divisionLeaderJob));
                baseMapper.updateById(toUpdate);
            } catch (DuplicateKeyException e) {
                log.warn("seq id duplicate.");
                String mysqlIndexName = Util.extractKey(e);
                if (mysqlIndexName.equals(Const.SEQID_INDEX)) {
                    continue;
                }
            }
            break;
        }
        // 从客户模块带出资料
        this.refreshClientMaterials(info.getId(), Collections.singleton(info.getClientId()));
        return info;
    }

    private void refreshClientMaterials(Long groupProjEstablishId, Set<Long> clientIds) {
        if (CollectionUtil.isEmpty(clientIds)) {
            return;
        }
        // 先移除老数据
        materialsListService.remove(Wrappers.<MaterialsList>lambdaQuery()
                .eq(MaterialsList::getBelongId, groupProjEstablishId)
                .eq(MaterialsList::getBusinessType, BusinessModuleEnum.GROUP_CREDIT_ESTABLISH_CLIENT.name())
                .in(MaterialsList::getSourceBusinessKey, clientIds.stream().map(Object::toString).collect(Collectors.toSet()))
        );
        List<Client> clientList = clientService.listByIds(clientIds);
        LambdaQueryWrapper<MaterialsList> query = Wrappers.lambdaQuery();
        query.eq(MaterialsList::getBusinessType, BusinessModuleEnum.CLIENT.name());
        List<String> typeList = new LinkedList<>();
        typeList.addAll(CorporationClientMaterialTypeEnum.needCopyType());
        typeList.addAll(NormalClientMaterialTypeEnum.needCopyType());
        query.in(MaterialsList::getMaterialsType, typeList);
        query.and(innerQuery -> {
            for (Client client : clientList) {
                innerQuery.or().eq(MaterialsList::getBelongId, client.getId());
                if (Objects.nonNull(client.getBelongSponsorId())) {
                    innerQuery.eq(BaseModel::getCreateBy, client.getBelongSponsorId());
                }
            }
        });
        List<MaterialsList> mList = materialsListService.list(query);
        if (CollectionUtil.isNotEmpty(mList)) {
            List<MaterialsList> todoList = new LinkedList<>();
            mList.forEach(item -> {
                // 业务申请书特殊处理，放入指定的立项分类下
                String businessModule = null;
                String materialType = null;
                String materialSubType = null;
                if (Objects.equals(item.getMaterialSubType(), CorporationClientMaterialSubTypeEnum.LEASE_APPLICATION.name())) {
                    businessModule = BusinessModuleEnum.GROUP_CREDIT_ESTABLISH.name();
                    materialType = GroupCreditEstablishMaterialsEnum.PROJ_INFORMATION.name();
                } else {
                    businessModule = BusinessModuleEnum.GROUP_CREDIT_ESTABLISH_CLIENT.name();
                    materialType = item.getMaterialsType();
                    materialSubType = item.getMaterialSubType();
                }
                String bizPath = businessModule + "/" + groupProjEstablishId + "/" + materialType;
                if (StrUtil.isNotBlank(materialSubType)) {
                    bizPath = bizPath + "/" + materialSubType;
                }
                String newFilePath = ossClient.getBasePath() + bizPath;
                String newOssFilename = bizPath + "/" + item.getFilename();
                try {
                    ossClient.copy(ossClient.getBasePath() + item.getOssFilename(), ossClient.getBasePath() + newOssFilename);
                } catch (Exception e) {
                    log.error("授信立项拷贝客户资料发生异常[groupProjEstablishId:{}, item:{}]", groupProjEstablishId, JSONUtil.toJsonStr(item), e);
                    // 拷贝失败的话就不存文件记录了
                    return;
                }
                MaterialsList newMaterial = new MaterialsList();
                newMaterial.setBelongId(groupProjEstablishId);
                newMaterial.setBusinessType(businessModule);
                newMaterial.setMaterialsType(materialType);
                newMaterial.setMaterialSubType(materialSubType);
                newMaterial.setOssFilename(newOssFilename);
                newMaterial.setSuffix(item.getSuffix());
                newMaterial.setFilename(item.getFilename());
                newMaterial.setFilePath(newFilePath);
                newMaterial.setSystemGenerate(item.getSystemGenerate());
                newMaterial.setSourceBusinessKey(item.getBelongId().toString());
                todoList.add(newMaterial);
            });
            if (CollectionUtil.isNotEmpty(todoList)) {
                materialsListService.saveBatch(todoList);
            }
        }
    }

    @Transactional(rollbackFor = Throwable.class)
    public void modify(GroupCreditEstablishBaseInfoModifyREQ req) {
        saveCheck(req.getId());
        GroupCreditEstablishBaseInfo originalInfo = groupCreditEstablishBaseInfoMapper.selectById(req.getId());
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        GroupCreditEstablishBaseInfo info = groupCreditEstablishBaseInfoConverter.modifyREQtoEntity(req);
        groupCreditEstablishBaseInfoMapper.updateAnnotationIncludeNullById(info);
        // 更新审批流审批人
        ProcessResp runningProcess = groupCreditEstablishService.findRelatedProcess(req.getId());
        if (Objects.nonNull(runningProcess)) {
            if (!Objects.equals(originalInfo.getBizDeptLeaderId(), req.getBizDeptLeaderId())) {
                flowUserApiService.setTaskApprover(FlowUtil.buildSetApproverReq(ListUtil.toList(req.getBizDeptLeaderId()), runningProcess.getProcessInstanceId(), "userTask_deptMaster"));
            }
            if (!Objects.equals(originalInfo.getBizDivisionLeaderId(), req.getBizDivisionLeaderId())) {
                flowUserApiService.setTaskApprover(FlowUtil.buildSetApproverReq(ListUtil.toList(req.getBizDivisionLeaderId()), runningProcess.getProcessInstanceId(), "userTask_bizDivisionLeader"));
            }
        }
        recordStatus(req.getId());
        // 通知客户权限变更
        ApplicationContextUtil.getApplicationContext().publishEvent(
                new ClientViewAuthorityEvent(new ClientViewAuthorityEvent.ClientViewAuthorityInfo(
                        BusinessModuleEnum.GROUP_CREDIT_ESTABLISH,
                        info.getId().toString(),
                        "集团授信立项-修改")
                )
        );
    }

    public PageR<GroupCreditEstablishListRSP> list(GroupCreditEstablishListREQ req) {
        GroupCreditEstablishListSelectDTO dto = new GroupCreditEstablishListSelectDTO();
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
        Page<GroupCreditEstablishBaseInfo> baseInfoPage = groupCreditEstablishBaseInfoMapper.myList(new Page<>(req.getPage(), req.getPageSize()), dto);
        List<GroupCreditEstablishListRSP> resultList = new ArrayList<>(baseInfoPage.getRecords().size());

        Set<Long> sysUserIds = new HashSet<>();
        Set<Long> clientIds = new HashSet<>();
        Set<Long> deptIds = new HashSet<>();
        for (GroupCreditEstablishBaseInfo record : baseInfoPage.getRecords()) {
            GroupCreditEstablishListRSP rsp = new GroupCreditEstablishListRSP();
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
        for (GroupCreditEstablishListRSP rsp : resultList) {
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

    public GroupCreditEstablishBaseInfoDetailRSP detail(Long groupCreditEstablishId, String version) {
        if (StringUtils.isBlank(version)) {
            GroupCreditEstablishBaseInfo baseInfo = groupCreditEstablishBaseInfoMapper.selectById(groupCreditEstablishId);
            if (Objects.isNull(baseInfo)) {
                throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
            }
            GroupCreditEstablishBaseInfoDetailRSP rsp = groupCreditEstablishBaseInfoConverter.entityToDetailRSP(baseInfo);
            join(rsp);
            // 填充风险敞口
            rsp.setClientRiskExposure(contractBaseInfoService.getGroupCreditStockRiskExposure(baseInfo.getClientId()));
            // 判断是否业务部门
            rsp.setIsBizDept(Objects.nonNull(sysUserService.currentUserBizDept()));
            // 更新评级
            final GroupCreditEstalishInfoUpdateRatingRSP updateRating = updateRating(new GroupCreditEstalishBaseInfoUpdateRatingREQ(groupCreditEstablishId));
            rsp.setClientRatingScoreId(updateRating.getClientRatingScoreId());
            rsp.setClientRatingScore(updateRating.getClientRatingScore());
            return rsp;
        } else {
            GroupCreditEstablishBaseInfoLib versionLib = baseInfoLibMapper.selectOne(Wrappers.<GroupCreditEstablishBaseInfoLib>lambdaQuery().eq(GroupCreditEstablishBaseInfoLib::getOriginId, groupCreditEstablishId).eq(GroupCreditEstablishBaseInfoLib::getVersion, version).last("LIMIT 1"));
            if (Objects.isNull(versionLib)) {
                throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
            }
            return baseInfoLibHandler.actualLib2Rsp(versionLib);
        }
    }

    private Long maxSeqId() {
        QueryWrapper queryWrapper = new QueryWrapper<>();
        queryWrapper.select("IFNULL(max(type_seq_id),0) as typeSeqId");
        GroupCreditEstablishBaseInfo info = baseMapper.selectOne(queryWrapper);
        return info.getTypeSeqId();
    }

    private static String generateProjCode(Long id) {
        LocalDate now = LocalDate.now();
        return "JT" + now.getYear() + randomInt(0, 10) + decimalFormat("0000", id);
    }

    public List<GroupCreditEstablishVagueListRSP> vagueQuery(GroupCreditEstablishVagueListREQ req) {
        LambdaQueryWrapper<GroupCreditEstablishBaseInfo> queryWrapper = Wrappers.<GroupCreditEstablishBaseInfo>lambdaQuery()
                .eq(GroupCreditEstablishBaseInfo::getGroupCreditEstablishStatus, TAKE_EFFECT.name());
        if (req.getProjVagueName() != null) {
            queryWrapper.like(GroupCreditEstablishBaseInfo::getProjName, req.getProjVagueName());
        }
        queryWrapper.orderByDesc(GroupCreditEstablishBaseInfo::getId);
        List<GroupCreditEstablishBaseInfo> groupCreditEstablishBaseInfos = baseMapper.selectList(queryWrapper);
        if (CollectionUtil.isEmpty(groupCreditEstablishBaseInfos)) {
            return new ArrayList<>();
        }
        Set<Long> clientIds = groupCreditEstablishBaseInfos.stream().map(GroupCreditEstablishBaseInfo::getClientId).collect(Collectors.toSet());
        Map<Long, String> clientNameMap = id2NameService.clientId2Name(clientIds);
        List<GroupCreditEstablishVagueListRSP> resList = new ArrayList<>();
        for (GroupCreditEstablishBaseInfo baseInfo : groupCreditEstablishBaseInfos) {
            GroupCreditEstablishVagueListRSP one = BeanUtil.copyProperties(baseInfo, GroupCreditEstablishVagueListRSP.class);
            one.setClientNames(clientNameMap.get(baseInfo.getClientId()) == null ? new ArrayList<>() : Collections.singletonList(clientNameMap.get(baseInfo.getClientId())));
            resList.add(one);
        }
        return resList;
    }

    /**
     * 用于详情查询填充冗余字段给前端显示
     *
     * @param rsp
     */
    public void join(GroupCreditEstablishBaseInfoDetailRSP rsp) {
        Set<Long> sysUserIds = new HashSet<>();
        Set<Long> clientIds = new HashSet<>();
        Set<Long> deptIds = new HashSet<>();
        sysUserIds.add(rsp.getProjSponsorUserId());
        if (CollUtil.isNotEmpty(rsp.getProjCosponsorUserIds())) {
            sysUserIds.addAll(rsp.getProjCosponsorUserIds());
        }
        sysUserIds.add(rsp.getBizDeptLeaderId());
        sysUserIds.add(rsp.getBizDivisionLeaderId());

        if (ObjectUtil.isNotEmpty(rsp.getRiskControlManagerId())) {
            sysUserIds.addAll(rsp.getRiskControlManagerId());
        }

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
        if (ObjectUtil.isNotEmpty(rsp.getRiskControlManagerId())) {
            rsp.setRiskControlManagerName(rsp.getRiskControlManagerId().stream()
                    .map(sysUserMap::get).collect(Collectors.toList()));
        }

    }

    /**
     * 维护失效状态
     **/
    @Transactional(rollbackFor = Throwable.class)
    public void doExpire(Integer days) {
        //1.查找所有立项审批通过时间
        ProcessPageReq flowReq = new ProcessPageReq();
        flowReq.setModelKeyList(Collections.singletonList(ProcessModelTypeEnum.GroupCreditEstablishCreateFlow.name()));
        flowReq.setSortType(1);
        flowReq.setPageIndex(1);
        flowReq.setPageSize(Integer.MAX_VALUE);
        flowReq.setProcessStatusList(ListUtil.toList(2, 6));
        cn.zswltech.flow.core.util.Page<ProcessResp> flowRespPage = taskApiService.queryProcess(flowReq);
        if (ObjectUtil.isEmpty(flowRespPage) || ObjectUtil.isEmpty(flowRespPage.getContents())) {
            return;
        }
        Set<Long> needExpireIds = new HashSet<>();
        List<Long> groupEstablishIds = flowRespPage.getContents().stream().filter(e -> isNotEmpty(e.getEndTime())).filter(e -> LocalDateTimeUtil.of(e.getEndTime()).plusDays(days).isBefore(LocalDateTime.now())).map(ProcessResp::getBusinessKey).map(Long::valueOf).collect(Collectors.toList());
        if (ObjectUtil.isEmpty(groupEstablishIds)) {
            return;
        }
        //剔除已经关闭或失效的
        groupEstablishIds = this.baseMapper.selectList(Wrappers.<GroupCreditEstablishBaseInfo>lambdaQuery()
                .in(GroupCreditEstablishBaseInfo::getId, groupEstablishIds)
                .notIn(GroupCreditEstablishBaseInfo::getGroupCreditEstablishStatus, CLOSED.name(), EXPIRE.name())).stream().map(GroupCreditEstablishBaseInfo::getId).collect(Collectors.toList());
        if (ObjectUtil.isEmpty(groupEstablishIds)) {
            return;
        }
        //2.剔除已经提起项目立项审批的
        List<GroupCreditReviewBaseInfo> groupReviewBaseInfos = SpringContextHolder.getBean(GroupCreditReviewBaseInfoService.class).list(Wrappers.<GroupCreditReviewBaseInfo>lambdaQuery()
                .in(GroupCreditReviewBaseInfo::getGroupCreditEstablishId, groupEstablishIds));
        //需要修改为释放
        if (ObjectUtil.isNotEmpty(groupReviewBaseInfos)) {
            Map<Long, List<GroupCreditReviewBaseInfo>> groupEsId2Review = groupReviewBaseInfos.stream().collect(Collectors.groupingBy(GroupCreditReviewBaseInfo::getGroupCreditEstablishId));
            for (Long esId : groupEstablishIds) {
                List<GroupCreditReviewBaseInfo> tmpReviewBaseInfos = groupEsId2Review.get(esId);
                if (isEmpty(tmpReviewBaseInfos)) {
                    //未评审的需要释放
                    needExpireIds.add(esId);
                } else {
                    //已经有立项需要判断是否有提交过
                    boolean needExpire = true;
                    for (GroupCreditReviewBaseInfo baseInfo : tmpReviewBaseInfos) {
                        if (!(ObjectUtil.equals(baseInfo.getGroupCreditReviewStatus(), NEW.name()) && (ObjectUtil.equals(baseInfo.getGroupCreditReviewProcessStatus(), GroupCreditReviewProcessStatus.NEW_UN_SUBMIT.name())))) {
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
            needExpireIds = new HashSet<>(groupEstablishIds);
        }

        //释放
        if (ObjectUtil.isNotEmpty(needExpireIds)) {
            LambdaUpdateWrapper<GroupCreditEstablishBaseInfo> wrapper = new LambdaUpdateWrapper<>();
            wrapper.set(GroupCreditEstablishBaseInfo::getGroupCreditEstablishStatus, EXPIRE.name());
            wrapper.in(GroupCreditEstablishBaseInfo::getId, needExpireIds);
            SpringContextHolder.getBean(GroupCreditEstablishBaseInfoService.class).update(wrapper);
        }
    }

    public void checkClientRating(GroupCreditEstablishBaseInfo info) {
        // 公用类客户需在提交授信立项时完成授信主体的客户评级，否则流程将无法提交
        List<CorpCommerceInfo> list = corpCommerceInfoService.list(Collections.singletonList(info.getClientId()));
        String riskControlIndustryClassify = list.get(0).getRiskControlIndustryClassify();
        // 公用类的客户需在提交项目立项时完成评估主体的客户评级
        if (RiskControlIndustryClassify.CIVIL_CONSUMPTION.name().equals(riskControlIndustryClassify) ||
                RiskControlIndustryClassify.PUBLIC_UTILITIES.name().equals(riskControlIndustryClassify)
//                || RiskControlIndustryClassify.TRAVEL.name().equals(riskControlIndustryClassify)
        ) {
            final List<Long> userList = getUserList(info);
            if (CollectionUtil.isEmpty(userList)) {
                throw new MithrasException("无生效的授信主体客户评级信息");
            }
            RatingClientProjDetailRSP flushAfterMainLessee = ratingClientService.getEffectRating(60, info.getClientId(), userList, new Date());
            if (Objects.isNull(flushAfterMainLessee) || ObjectUtil.isEmpty(flushAfterMainLessee.getId())) {
                throw new MithrasException("无生效的授信主体客户评级信息");
            }
            info.setClientRatingScoreId(flushAfterMainLessee.getId());
            info.setClientRatingScore(flushAfterMainLessee.getRatingFinalScore());
        }
    }

    /**
     * 项目经理在提交流程前或流程中撤回/退回节点在项目经理时 才进行更新操作
     *
     * @param req 参数
     * @return 结果
     */
    public GroupCreditEstalishInfoUpdateRatingRSP updateRating(GroupCreditEstalishBaseInfoUpdateRatingREQ req) {
        GroupCreditEstalishInfoUpdateRatingRSP rsp = new GroupCreditEstalishInfoUpdateRatingRSP();
        Date date = new Date();
        if (Objects.nonNull(req.getId())) {
            final GroupCreditEstablishBaseInfo groupCreditEstablishBaseInfo = this.getById(req.getId());
            rsp.setClientRatingScore(groupCreditEstablishBaseInfo.getClientRatingScore());
            rsp.setClientRatingScoreId(groupCreditEstablishBaseInfo.getClientRatingScoreId());
            if (groupCreditEstablishBaseInfo != null) {
                final List<Long> userList = getUserList(groupCreditEstablishBaseInfo);
                final String groupCreditEstablishStatus = groupCreditEstablishBaseInfo.getGroupCreditEstablishStatus();
                // 项目经理在提交流程前或流程中撤回/退回节点在项目经理时
                if (CollectionUtil.isNotEmpty(userList) && Objects.equals(groupCreditEstablishStatus, NEW.name()) && needUpdateByProcessJudge(groupCreditEstablishBaseInfo.getId())) {
                    RatingClientProjDetailRSP flushAfterMainLessee = ratingClientService.getEffectRating(60, groupCreditEstablishBaseInfo.getClientId(), userList, date);
                    LambdaUpdateWrapper<GroupCreditEstablishBaseInfo> updateWrapper = Wrappers.lambdaUpdate();
                    updateWrapper.eq(GroupCreditEstablishBaseInfo::getId, req.getId());
                    updateWrapper.set(GroupCreditEstablishBaseInfo::getRatingUpdateTime, LocalDateTime.now());
                    if (flushAfterMainLessee != null) {
                        updateWrapper.set(GroupCreditEstablishBaseInfo::getClientRatingScoreId, flushAfterMainLessee.getId());
                        updateWrapper.set(GroupCreditEstablishBaseInfo::getClientRatingScore, flushAfterMainLessee.getRatingFinalScore());
                        rsp.setClientRatingScoreId(flushAfterMainLessee.getId());
                        rsp.setClientRatingScore(flushAfterMainLessee.getRatingFinalScore());
                    }
                    this.update(updateWrapper);
                }
            }
        }
        return rsp;
    }

    public GroupCreditEstablishRatingCheckRSP checkRatingInfo(Long id) {
        GroupCreditEstablishRatingCheckRSP rsp = new GroupCreditEstablishRatingCheckRSP();
        final GroupCreditEstablishBaseInfo baseInfo = this.getById(id);
        if (Objects.isNull(baseInfo)) {
            throw new MithrasException("集团授信立项数据不存在");
        }
        rsp.setClientId(baseInfo.getClientId());
        List<CorpCommerceInfo> list = corpCommerceInfoService.list(Collections.singletonList(baseInfo.getClientId()));
        String riskControlIndustryClassify = list.get(0).getRiskControlIndustryClassify();
        // 公用类的客户需在提交项目立项时完成评估主体的客户评级
        if (RiskControlIndustryClassify.CIVIL_CONSUMPTION.name().equals(riskControlIndustryClassify) ||
                RiskControlIndustryClassify.PUBLIC_UTILITIES.name().equals(riskControlIndustryClassify)
//                ||  RiskControlIndustryClassify.TRAVEL.name().equals(riskControlIndustryClassify)  //  /*添加限制  风控行业分类为公用事业、民生消费类  则校验客户是否完成评级*/
        ) {
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
        } else {
            rsp.setRatingClientIsDone(true);
        }
        return rsp;
    }

    List<Long> getUserList(GroupCreditEstablishBaseInfo info) {
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
        final List<String> modelKeyList = Arrays.asList(ProcessModelTypeEnum.GroupCreditEstablishCreateFlow.name());
        processPageReq.setModelKeyList(modelKeyList);
//        processPageReq.setModelKeyList(BusinessModuleEnum.GROUP_CREDIT_ESTABLISH.getModelKeyList());
        cn.zswltech.flow.core.util.Page<ProcessResp> processRespPage = taskApiService.queryProcess(processPageReq);
        ProcessResp processResp = processRespPage.getContents().stream().findFirst().orElse(null);
        if (Objects.isNull(processResp)) {
            return true;
        } else {
            return processResp.getProcessStatus().equals(ProcessBusinessStatusEnum.RUNNING.getType()) && processResp.getCurTaskActivityIds().contains("userTask_startUser");
        }
    }
}