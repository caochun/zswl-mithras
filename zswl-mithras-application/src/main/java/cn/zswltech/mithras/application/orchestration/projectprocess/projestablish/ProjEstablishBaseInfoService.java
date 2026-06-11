package cn.zswltech.mithras.application.orchestration.projectprocess.projestablish;
import cn.zswltech.mithras.customer.enums.CorpAddressType;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import cn.zswl.oss.core.OssClient;
import cn.zswltech.flow.core.api.FlowProcessApiService;
import cn.zswltech.flow.core.api.FlowTaskApiService;
import cn.zswltech.flow.core.domain.req.ProcessHistoryReq;
import cn.zswltech.flow.core.domain.req.task.ProcessPageReq;
import cn.zswltech.flow.core.domain.resp.ProcessHistoryResp;
import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.flow.core.util.ApplicationContextUtil;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.gruul.dao.dal.entity.OrgDO;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.flow.execution.ExecutionProcessBaseREQ;
import cn.zswltech.mithras.dto.projestablish.baseinfo.*;
import cn.zswltech.mithras.dto.projestablish.baseinfo.jsonbean.ProjEstablishPersonInfo;
import cn.zswltech.mithras.dto.rating.ratingclient.RatingClientProjDetailRSP;
import cn.zswltech.mithras.rating.service.RatingAmountService;
import cn.zswltech.mithras.rating.service.RatingClientService;
import cn.zswltech.mithras.foundation.cache.RedisDistLock;
import cn.zswltech.mithras.projectprocess.convert.projestablish.ProjEstablishBaseInfoConverter;
import cn.zswltech.mithras.application.orchestration.enums.BusinessModuleEnum;
import cn.zswltech.mithras.foundation.enums.CacheEnum;
import cn.zswltech.mithras.workflow.flow.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.customer.enums.client.CorporationClientMaterialSubTypeEnum;
import cn.zswltech.mithras.customer.enums.client.CorporationClientMaterialTypeEnum;
import cn.zswltech.mithras.customer.enums.client.NormalClientMaterialTypeEnum;
import cn.zswltech.mithras.foundation.enums.common.ProcessStatus;
import cn.zswltech.mithras.foundation.enums.common.RecordStatus;
import cn.zswltech.mithras.projectprocess.enums.projestablish.ProjEstablishMaterialsEnum;
import cn.zswltech.mithras.basedata.mapper.AddressDictionaryMapper;
import cn.zswltech.mithras.customer.mapper.client.ClientMapper;
import cn.zswltech.mithras.projectprocess.dto.persistence.ProjEstablishListSelectDTO;
import cn.zswltech.mithras.projectprocess.mapper.lib.projestablish.ProjEstablishBaseInfoLibMapper;
import cn.zswltech.mithras.basedata.mapper.model.AddressDictionary;
import cn.zswltech.mithras.foundation.persistence.model.BaseModel;
import cn.zswltech.mithras.document.model.MaterialsList;
import cn.zswltech.mithras.customer.mapper.model.client.Client;
import cn.zswltech.mithras.customer.mapper.model.client.CorpCommerceInfo;
import cn.zswltech.mithras.customer.mapper.model.client.CorpCommerceInfoLib;
import cn.zswltech.mithras.projectprocess.mapper.model.projestablish.ProjEstablishBaseInfo;
import cn.zswltech.mithras.projectprocess.mapper.model.projestablish.ProjEstablishBaseInfoLib;
import cn.zswltech.mithras.projectprocess.mapper.model.projreview.ProjReviewBaseInfo;
import cn.zswltech.mithras.projectprocess.mapper.projestablish.ProjEstablishBaseInfoMapper;
import cn.zswltech.mithras.projectprocess.mapper.projreview.ProjReviewBaseInfoMapper;
import cn.zswltech.mithras.projectprocess.application.projestablish.ProjEstablishTradeStructureService;
import cn.zswltech.mithras.projectprocess.application.projestablish.ProjEstablishUpdateAdvice;
import cn.zswltech.mithras.foundation.util.Const;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.foundation.context.SpringContextHolder;
import cn.zswltech.mithras.foundation.util.Util;
import cn.zswltech.mithras.system.user.Id2NameService;
import cn.zswltech.mithras.customer.event.ClientViewAuthorityEvent;
import cn.zswltech.mithras.system.application.projectcode.ProjCodeStoreService;
import cn.zswltech.mithras.system.user.SysUserService;
import cn.zswltech.mithras.application.orchestration.client.ClientAuthorityService;
import cn.zswltech.mithras.application.orchestration.client.ClientService;
import cn.zswltech.mithras.customer.application.client.CorpCommerceInfoService;
import cn.zswltech.mithras.contract.core.ContractBaseInfoService;
import cn.zswltech.mithras.application.orchestration.workflow.flow.service.ExecutionService;
import cn.zswltech.mithras.customer.application.lib.client.CorpCommerceInfoLibService;
import cn.zswltech.mithras.customer.application.lib.client.impl.CorpAddressInfoLibServiceImpl;
import cn.zswltech.mithras.projectprocess.application.lib.projestablish.handler.impl.ProjEstablishBaseInfoLibHandler;
import cn.zswltech.mithras.application.orchestration.document.materialsfile.MaterialsListService;
import cn.zswltech.mithras.foundation.state.ProjContext;
import cn.zswltech.mithras.foundation.state.ProjEvent;
import cn.zswltech.mithras.foundation.state.ProjProcessState;
import cn.zswltech.mithras.projectprocess.application.projfms.impl.ProjEstablishStateMachine;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

import static cn.hutool.core.bean.BeanUtil.copyProperties;
import static cn.hutool.core.text.CharSequenceUtil.isBlank;
import static cn.hutool.core.util.ObjectUtil.*;
import static cn.hutool.json.JSONUtil.toBean;
import static cn.hutool.json.JSONUtil.toJsonStr;
import static cn.zswltech.mithras.foundation.constant.ResultMsg.*;
import static cn.zswltech.mithras.foundation.enums.common.ProjectBizType.*;
import static cn.zswltech.mithras.foundation.enums.common.RecordStatus.*;
import static cn.zswltech.mithras.foundation.util.Util.errMithras;

/**
 * @author luyi
 * @description 立项基本信息表
 * @date 2022-07-19
 */
@Slf4j
@Service
public class ProjEstablishBaseInfoService extends ServiceImpl<ProjEstablishBaseInfoMapper, ProjEstablishBaseInfo>
        implements ProjEstablishUpdateAdvice {
    @Value("${mithras.job.deptLeader}")
    private String deptLeaderJob;
    @Value("${mithras.job.divisionLeader}")
    private String divisionLeaderJob;
    @Resource
    private SysUserService sysUserService;
    @Resource
    private ClientMapper clientMapper;
    @Resource
    private Id2NameService id2NameService;
    @Resource
    private ProjEstablishBaseInfoConverter baseInfoConverter;
    @Resource
    private ProjEstablishService projEstablishService;
    @Resource
    private ExecutionService executionService;
    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private ProjReviewBaseInfoMapper reviewBaseInfoMapper;
    @Resource
    private ProjEstablishStateMachine stateMachine;
    @Resource
    private RedisDistLock redisDistLock;
    @Resource
    private ClientService clientService;
    @Resource
    private ProjCodeStoreService projCodeStoreService;
    @Resource
    private ProjEstablishBaseInfoLibHandler baseInfoLibHandler;
    @Resource
    private ProjEstablishBaseInfoLibMapper projEstablishBaseInfoLibMapper;
    @Resource
    private CorpAddressInfoLibServiceImpl corpAddressInfoLibService;
    @Resource
    private AddressDictionaryMapper addressDictionaryMapper;
    @Resource
    private FlowTaskApiService taskApiService;
    @Resource
    private RatingClientService ratingClientService;
    @Resource
    private RatingAmountService ratingAmountService;
    @Resource
    private CorpCommerceInfoService commerceInfoService;
    @Resource
    private CorpCommerceInfoLibService corpCommerceInfoLibService;
    @Resource
    private FlowProcessApiService flowProcessApiService;
    @Autowired
    private MaterialsListService materialsListService;
    @Resource
    private OssClient ossClient;

    /**
     * 立项模糊查询
     *
     * @param req
     * @return
     */
    public Map<String, ProjEstablishVagueListRSP> vagueQuery(ProjEstablishVagueListREQ req) {
        Long id = AccountUtil.getLoginInfo().getId();
        LambdaQueryWrapper<ProjEstablishBaseInfo> queryWrapper = Wrappers.<ProjEstablishBaseInfo>lambdaQuery()
                .eq(ProjEstablishBaseInfo::getProjSponsorUserId, id)
                .eq(ProjEstablishBaseInfo::getProjEstablishStatus, TAKE_EFFECT.name())
                .notIn(ProjEstablishBaseInfo::getProjEstablishProcessStatus,Arrays.asList(ProjProcessState.CHANGING_UN_SUBMIT.name(), ProjProcessState.CHANGING_UNDER_APPROVAL.name()));
        if (req.getProjVagueName() != null) {
            queryWrapper.like(ProjEstablishBaseInfo::getProjName, req.getProjVagueName());
        }
        queryWrapper.orderByDesc(ProjEstablishBaseInfo::getId);
        List<ProjEstablishBaseInfo> projEstablishBaseInfos = baseMapper.selectList(queryWrapper);
        if (CollectionUtil.isEmpty(projEstablishBaseInfos)) {
            return Collections.emptyMap();
        }
        Set<Long> clientIds = projEstablishBaseInfos.stream().map(ProjEstablishBaseInfo::getClientId).collect(Collectors.toSet());
        List<Client> clientList = clientService.listByClientIds(clientIds);
        Map<Long, Client> clientMap = clientList.stream().collect(Collectors.toMap(Client::getId, e -> e));
        Map<String, ProjEstablishVagueListRSP> projEstablishBaseInfoMap = new HashMap<>();
        for (ProjEstablishBaseInfo baseInfo : projEstablishBaseInfos) {
            ProjEstablishVagueListRSP one = BeanUtil.copyProperties(baseInfo, ProjEstablishVagueListRSP.class);
            if (Objects.nonNull(baseInfo.getClientId())) {
                Client client = clientMap.get(baseInfo.getClientId());
                if (Objects.nonNull(client)) {
                    one.setClientNames(Collections.singletonList(client.getClientName()));
                }
            }
            projEstablishBaseInfoMap.put(one.getProjName(), one);
        }
        return projEstablishBaseInfoMap;
    }

    @Transactional(rollbackFor = Throwable.class)
    public ProjEstablishBaseInfo add(ProjEstablishBaseInfoAddREQ req) {
        if (!SpringUtil.getBean(ClientAuthorityService.class).currentUserHasManagerAuth(req.getClientId())) {
            throw new MithrasException("无所选客户管护权，无权进行操作");
        }
        String lockKey = CacheEnum.getCacheKey(CacheEnum.PROJ_ESTABLISH_ADD_LOCK, req.getProjName());
        boolean lock = redisDistLock.tryLock(lockKey, 2000L, CacheEnum.PROJ_ESTABLISH_ADD_LOCK.getExpire());
        if (!lock) {
            throw new MithrasException(CONCURRENT_OPERATION);
        }
        OrgDO bizOrgDO = sysUserService.currentUserBizDept();
        if (isNull(bizOrgDO)) {
            throw new MithrasException(ONLY_BIZ_DEPT_DO);
        }
        if (isNotNull(baseMapper.selectOne(Wrappers.<ProjEstablishBaseInfo>lambdaQuery()
                .notIn(ProjEstablishBaseInfo::getProjEstablishStatus, ListUtil.toList(CLOSED.name(), EXPIRE.name()))
                .eq(ProjEstablishBaseInfo::getProjName, req.getProjName())))) {
            throw new MithrasException("项目名称已存在");
        }
        ProjEstablishBaseInfo info = copyProperties(req, ProjEstablishBaseInfo.class);
        info.setProjEstablishStatus(NEW.name());
        info.setProjEstablishProcessStatus(ProjProcessState.NEW_UN_SUBMIT.name());
        info.setRiskControlManagerId(JSON.toJSONString(
                sysUserService.getRiskControlManagerIdsByDeptCode(bizOrgDO.getCode())));

        // 客户风控行业分类判断
        Client client = clientService.getById(info.getClientId());
        Assert.notNull(client, () -> MithrasException.newException("客户不存在"));
        CorpCommerceInfoLib corpCommerceInfoLib = corpCommerceInfoLibService.getNewestOne(client);
        Assert.notNull(corpCommerceInfoLib, () -> MithrasException.newException("无生效的客户工商信息数据"));
        Assert.notBlank(corpCommerceInfoLib.getRiskControlIndustryClassify(), () -> MithrasException.newException(String.format("请先至客户模块维护<%s>的“风控行业分类”，否则无法判断该项目是否需经董事会审议！", client.getClientName())));
        // 填信息
        info.setRiskControlIndustryClassify(corpCommerceInfoLib.getRiskControlIndustryClassify());
        //获取注册地址信息
       /* List<CorpAddressInfoLib> corpAddressInfoLibs = corpAddressInfoLibService.listAddressByClientIdAndType(Collections.singleton(req.getClientId()), CorpAddressType.REGISTRY_ADDRESS.name());
        if(CollectionUtil.isNotEmpty(corpAddressInfoLibs)){
            //取第一个
            CorpAddressInfoLib corpAddressInfoLib = corpAddressInfoLibs.stream().findFirst().orElse(null);
            if(corpAddressInfoLib != null){
                info.setCountry(corpAddressInfoLib.getCountry());
                info.setProvince(corpAddressInfoLib.getProvince());
                info.setCity(corpAddressInfoLib.getCity());
                info.setDistrict(corpAddressInfoLib.getDistrict());
            }
        }*/
        baseMapper.insert(info);

        //生成项目编号
        int maxCount = 0;
        while (maxCount++ < 10) {
            try {
                Long seqId = projCodeStoreService.maxSeqId(req.getBizType()) + 1;
                String projCode = projCodeStoreService.generateProjCode(req.getBizType(), seqId);
                ProjEstablishBaseInfo toUpdate = new ProjEstablishBaseInfo();
                toUpdate.setId(info.getId());
                toUpdate.setProjCode(projCode);
                toUpdate.setTypeSeqId(seqId);
                //默认当前客户作为承租人、债权人
                ProjEstablishPersonInfo personInfo = new ProjEstablishPersonInfo();
                personInfo.setClientId(req.getClientId());
                personInfo.setClientName(client.getClientName());
                personInfo.setStockRiskExposure(contractBaseInfoService.getStockRiskExposure(req.getClientId(), info.getId(), null));
                personInfo.setClientType(client.getClientType());
                if ((ZL.name().equals(info.getBizType()) || ZZ.name().equals(info.getBizType()))) {
                    toUpdate.setLesseeInfo(toJsonStr(ListUtil.toList(personInfo)));
                }
                if ((BL.name().equals(info.getBizType()) || ZR.name().equals(info.getBizType()))) {
                    toUpdate.setCreditorInfo(toJsonStr(ListUtil.toList(personInfo)));
                }
                //自动设置项目主办、业务部门，业务部门负责人，业务部门分管领导
                toUpdate.setProjSponsorUserId(AccountUtil.getLoginInfo().getId());
                toUpdate.setBizDeptId(bizOrgDO.getId());
                toUpdate.setBizDeptLeaderId(sysUserService.getUserIdByOrgJob(bizOrgDO.getId(), deptLeaderJob));
                toUpdate.setBizDivisionLeaderId(sysUserService.getUserIdByOrgJob(bizOrgDO.getId(), divisionLeaderJob));
                baseMapper.updateById(toUpdate);
                // 项目编号拆表记录数据
                projCodeStoreService.add(info.getBizType(), seqId, projCode);
            } catch (DuplicateKeyException e) {
                log.warn("seq id duplicate.", e);
                String mysqlIndexName = Util.extractKey(e);
                if (mysqlIndexName.equals(Const.SEQID_INDEX)) {
                    continue;
                }
            }
            break;
        }
        // 写交易结构辅助表
        SpringUtil.getBean(ProjEstablishTradeStructureService.class).syncTradeStructure(info.getId());
        // 从客户模块带出资料
        this.refreshClientMaterials(info.getId(), this.ensureChangeClient(null, this.getById(info.getId())), true);
        return info;
    }

    private Set<Long> ensureChangeClient(ProjEstablishBaseInfo oldInfo, ProjEstablishBaseInfo newInfo) {
        // FIXME 代码有待整理
        List<ProjEstablishPersonInfo> newPersonInfoList = new LinkedList<>();
        if (StrUtil.isNotBlank(newInfo.getLesseeInfo())) {
            newPersonInfoList.addAll(JSONUtil.toList(newInfo.getLesseeInfo(), ProjEstablishPersonInfo.class));
        }
        if (StrUtil.isNotBlank(newInfo.getGuaranteeInfo())) {
            newPersonInfoList.addAll(JSONUtil.toList(newInfo.getGuaranteeInfo(), ProjEstablishPersonInfo.class));
        }
        if (StrUtil.isNotBlank(newInfo.getMortgagorInfo())) {
            newPersonInfoList.addAll(JSONUtil.toList(newInfo.getMortgagorInfo(), ProjEstablishPersonInfo.class));
        }
        if (StrUtil.isNotBlank(newInfo.getPledgorInfo())) {
            newPersonInfoList.addAll(JSONUtil.toList(newInfo.getPledgorInfo(), ProjEstablishPersonInfo.class));
        }
        if (StrUtil.isNotBlank(newInfo.getCreditorInfo())) {
            newPersonInfoList.addAll(JSONUtil.toList(newInfo.getCreditorInfo(), ProjEstablishPersonInfo.class));
        }
        if (StrUtil.isNotBlank(newInfo.getDebtorInfo())) {
            newPersonInfoList.addAll(JSONUtil.toList(newInfo.getDebtorInfo(), ProjEstablishPersonInfo.class));
        }
        if (Objects.isNull(oldInfo)) {
            // 说明是新创建，返回全部
            return newPersonInfoList.stream().map(ProjEstablishPersonInfo::getClientId).collect(Collectors.toSet());
        }
        List<ProjEstablishPersonInfo> oldPersonInfoList = new LinkedList<>();
        if (StrUtil.isNotBlank(oldInfo.getLesseeInfo())) {
            oldPersonInfoList.addAll(JSONUtil.toList(oldInfo.getLesseeInfo(), ProjEstablishPersonInfo.class));
        }
        if (StrUtil.isNotBlank(oldInfo.getGuaranteeInfo())) {
            oldPersonInfoList.addAll(JSONUtil.toList(oldInfo.getGuaranteeInfo(), ProjEstablishPersonInfo.class));
        }
        if (StrUtil.isNotBlank(oldInfo.getMortgagorInfo())) {
            oldPersonInfoList.addAll(JSONUtil.toList(oldInfo.getMortgagorInfo(), ProjEstablishPersonInfo.class));
        }
        if (StrUtil.isNotBlank(oldInfo.getPledgorInfo())) {
            oldPersonInfoList.addAll(JSONUtil.toList(oldInfo.getPledgorInfo(), ProjEstablishPersonInfo.class));
        }
        if (StrUtil.isNotBlank(oldInfo.getCreditorInfo())) {
            oldPersonInfoList.addAll(JSONUtil.toList(oldInfo.getCreditorInfo(), ProjEstablishPersonInfo.class));
        }
        if (StrUtil.isNotBlank(oldInfo.getDebtorInfo())) {
            oldPersonInfoList.addAll(JSONUtil.toList(oldInfo.getDebtorInfo(), ProjEstablishPersonInfo.class));
        }
        // 比对得到发生变化的客户
        Set<Long> oldClientIds = oldPersonInfoList.stream().map(ProjEstablishPersonInfo::getClientId).collect(Collectors.toSet());
        Set<Long> newClientIds = newPersonInfoList.stream().map(ProjEstablishPersonInfo::getClientId).collect(Collectors.toSet());
        Set<Long> diff1 = oldClientIds.stream().filter(e -> !newClientIds.contains(e)).collect(Collectors.toSet());
        Set<Long> diff2 = newClientIds.stream().filter(e -> !oldClientIds.contains(e)).collect(Collectors.toSet());
        Set<Long> result = new HashSet<>();
        result.addAll(diff1);
        result.addAll(diff2);
        return result;
    }

    private void refreshClientMaterials(Long projEstablishId, Set<Long> clientIds, boolean isCreate) {
        if (CollectionUtil.isEmpty(clientIds)) {
            return;
        }
        // 先移除老数据
        materialsListService.remove(Wrappers.<MaterialsList>lambdaQuery()
                .eq(MaterialsList::getBelongId, projEstablishId)
                .eq(MaterialsList::getBusinessType, BusinessModuleEnum.PROJ_ESTABLISH_CLIENT.name())
                .in(MaterialsList::getSourceBusinessKey, clientIds.stream().map(Object::toString).collect(Collectors.toSet()))
        );
        List<Client> clientList = clientService.listByIds(clientIds);
        LambdaQueryWrapper<MaterialsList> query = Wrappers.lambdaQuery();
        query.eq(MaterialsList::getBusinessType, BusinessModuleEnum.CLIENT.name());
        List<String> typeList = new LinkedList<>();
        typeList.addAll(CorporationClientMaterialTypeEnum.needCopyType());
        typeList.addAll(NormalClientMaterialTypeEnum.needCopyType());
        if (isCreate) {
            // 创建需要多处理两个类型
            typeList.add(CorporationClientMaterialTypeEnum.CREDIT_LETTER.name());
            typeList.add(CorporationClientMaterialTypeEnum.LEASE_APPLICATION.name());
        }
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
                // 业务申请书和征信授权书特殊处理，放入指定的立项分类下
                String businessModule = null;
                String materialType = null;
                String materialSubType = null;
                if (Objects.equals(item.getMaterialSubType(), CorporationClientMaterialSubTypeEnum.CREDIT_LETTER.name())) {
                    businessModule = "PROJ_ESTABLISH";
                    materialType = ProjEstablishMaterialsEnum.PROJ_CREDIT_LETTER.name();
                } else if (Objects.equals(item.getMaterialSubType(), CorporationClientMaterialSubTypeEnum.LEASE_APPLICATION.name())) {
                    businessModule = "PROJ_ESTABLISH";
                    materialType = ProjEstablishMaterialsEnum.PROJ_INFORMATION.name();
                } else {
                    businessModule = "PROJ_ESTABLISH_CLIENT";
                    materialType = item.getMaterialsType();
                    materialSubType = item.getMaterialSubType();
                }
                String bizPath = businessModule + "/" + projEstablishId + "/" + materialType;
                if (StrUtil.isNotBlank(materialSubType)) {
                    bizPath = bizPath + "/" + materialSubType;
                }
                String newFilePath = ossClient.getBasePath() + bizPath;
                String newOssFilename = bizPath + "/" + item.getFilename();
                try {
                    ossClient.copy(ossClient.getBasePath() + item.getOssFilename(), ossClient.getBasePath() + newOssFilename);
                } catch (Exception e) {
                    log.error("项目立项拷贝客户资料发生异常[projEstablishId:{}, item:{}]", projEstablishId, JSONUtil.toJsonStr(item), e);
                    // 拷贝失败的话就不存文件记录了
                    return;
                }
                MaterialsList newMaterial = new MaterialsList();
                newMaterial.setBelongId(projEstablishId);
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
    public void modify(ProjEstablishBaseInfoModifyREQ req) {
        saveCheck(req.getId());
        ProjEstablishBaseInfo originalInfo = baseMapper.selectById(req.getId());
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException(RECORD_NOT_EXIST);
        }
        if (ObjectUtil.equal(originalInfo.getProjEstablishStatus(), CLOSED.name()) || ObjectUtil.equal(originalInfo.getProjEstablishStatus(), EXPIRE.name())) {
            throw new MithrasException(PROJ_CLOSED);
        }

        //立项对应的客户id，跟承租人和债权人对应的
        if (ZL.name().equals(originalInfo.getBizType()) || ZZ.name().equals(originalInfo.getBizType())) {
            Long clientId = req.getLesseeInfo().get(0).getClientId();
            if (ObjectUtil.notEqual(clientId, originalInfo.getClientId())) {
                ProjEstablishBaseInfo updateInfo = new ProjEstablishBaseInfo();
                updateInfo.setClientId(clientId);
                updateInfo.setId(originalInfo.getId());
                baseMapper.updateById(updateInfo);
            }
        }
        if (BL.name().equals(originalInfo.getBizType()) || ZR.name().equals(originalInfo.getBizType())) {
            Long clientId = req.getCreditorInfo().get(0).getClientId();
            if (ObjectUtil.notEqual(clientId, originalInfo.getClientId())) {
                ProjEstablishBaseInfo updateInfo = new ProjEstablishBaseInfo();
                updateInfo.setClientId(clientId);
                updateInfo.setId(originalInfo.getId());
                baseMapper.updateById(updateInfo);
            }
        }


        ProjEstablishBaseInfo info = copyProperties(req, ProjEstablishBaseInfo.class);
        if (isNotNull(req.getLesseeInfo())) {
            info.setLesseeInfo(toJsonStr(req.getLesseeInfo()));
        }
        if (isNotNull(req.getCreditorInfo())) {
            info.setCreditorInfo(toJsonStr(req.getCreditorInfo()));
        }
        if (isNotNull(req.getDebtorInfo())) {
            info.setDebtorInfo(toJsonStr(req.getDebtorInfo()));
        }
        if (isNotNull(req.getGuaranteeInfo())) {
            info.setGuaranteeInfo(toJsonStr(req.getGuaranteeInfo()));
        }
        if (isNotNull(req.getMortgagorInfo())) {
            info.setMortgagorInfo(toJsonStr(req.getMortgagorInfo()));
        }
        if (isNotNull(req.getPledgorInfo())) {
            info.setPledgorInfo(toJsonStr(req.getPledgorInfo()));
        }
        if (isNotNull(req.getProjCosponsorUserIds())) {
            info.setProjCosponsorUserIds(toJsonStr(req.getProjCosponsorUserIds()));
        }
        if (isNotNull(req.getLeaseTypes())) {
            info.setLeaseTypes(toJsonStr(req.getLeaseTypes()));
        }
        info.setProjCode(null);//防止code被更新
        //更新业务部门负责人，业务分管领导
        OrgDO bizOrgDO = sysUserService.geBizDeptByOrgId(originalInfo.getBizDeptId());
        if (isNotNull(bizOrgDO)) {
            info.setBizDeptLeaderId(sysUserService.getUserIdByOrgJob(bizOrgDO.getId(), deptLeaderJob));
            info.setBizDivisionLeaderId(sysUserService.getUserIdByOrgJob(bizOrgDO.getId(), divisionLeaderJob));
        }
        baseMapper.updateAnnotationIncludeNullById(info);
        // 写交易结构辅助表
        SpringUtil.getBean(ProjEstablishTradeStructureService.class).syncTradeStructure(info.getId());
        // 更新客户资料
        this.refreshClientMaterials(req.getId(), this.ensureChangeClient(originalInfo, this.getById(req.getId())), false);
        recordStatus(req.getId());
        // 通知客户权限变更
        ApplicationContextUtil.getApplicationContext().publishEvent(
                new ClientViewAuthorityEvent(new ClientViewAuthorityEvent.ClientViewAuthorityInfo(
                        BusinessModuleEnum.PROJ_ESTABLISH,
                        info.getId().toString(),
                        "项目立项-修改")
                )
        );
    }

    //更新部门领导信息
    @Transactional(rollbackFor = Throwable.class)
    public void renewLeader(Long projEstablishId) {
        ProjEstablishBaseInfo projEstablishBaseInfo = baseMapper.selectById(projEstablishId);
        if (ObjectUtil.isEmpty(projEstablishBaseInfo)) {
            return;
        }
        OrgDO bizOrgDO = sysUserService.geBizDeptByOrgId(projEstablishBaseInfo.getBizDeptId());
        if (isNotNull(bizOrgDO)) {
            LambdaUpdateWrapper<ProjEstablishBaseInfo> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(ProjEstablishBaseInfo::getId, projEstablishBaseInfo.getId());
            updateWrapper.set(ProjEstablishBaseInfo::getBizDeptLeaderId, sysUserService.getUserIdByOrgJob(bizOrgDO.getId(), deptLeaderJob));
            updateWrapper.set(ProjEstablishBaseInfo::getBizDivisionLeaderId, sysUserService.getUserIdByOrgJob(bizOrgDO.getId(), divisionLeaderJob));
            this.baseMapper.update(null, updateWrapper);
        }
    }

    public Page<ProjEstablishBaseInfoListRSP> list(ProjEstablishBaseInfoListREQ req) {
        ProjEstablishListSelectDTO dto = new ProjEstablishListSelectDTO();
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
        Page<ProjEstablishBaseInfo> data = baseMapper.myList(new Page<>(req.getPage(), req.getPageSize()), dto);
        List<ProjEstablishBaseInfoListRSP> list = new ArrayList<>(data.getRecords().size());
        Set<Long> sysUserIds = new HashSet<>();
        Set<Long> clientIds = new HashSet<>();
        Set<Long> deptIds = new HashSet<>();
        for (ProjEstablishBaseInfo record : data.getRecords()) {
            ProjEstablishBaseInfoListRSP rsp = new ProjEstablishBaseInfoListRSP();
            copyProperties(record, rsp, new CopyOptions().ignoreError());
            rsp.setProjCosponsorUserIds(isBlank(record.getProjCosponsorUserIds()) ? null : toBean(record.getProjCosponsorUserIds(), new TypeReference<List<Long>>() {
            }, true));
            rsp.setLeaseTypes(isBlank(record.getLeaseTypes()) ? null : toBean(record.getLeaseTypes(), new TypeReference<List<String>>() {
            }, true));
            sysUserIds.add(rsp.getProjSponsorUserId());
            if (CollUtil.isNotEmpty(rsp.getProjCosponsorUserIds())) {
                sysUserIds.addAll(rsp.getProjCosponsorUserIds());
            }
            clientIds.add(rsp.getClientId());
            deptIds.add(rsp.getBizDeptId());
            list.add(rsp);
        }
        Map<Long, String> clientMap = id2NameService.clientId2Name(clientIds);
        Map<Long, String> sysUserMap = id2NameService.sysUserId2Name(sysUserIds);
        Map<Long, String> deptMap = id2NameService.deptId2Name(deptIds);
        for (ProjEstablishBaseInfoListRSP rsp : list) {
            rsp.setBizDeptName(deptMap.get(rsp.getBizDeptId()));
            rsp.setProjSponsorUserName(sysUserMap.get(rsp.getProjSponsorUserId()));
            if (CollUtil.isNotEmpty(rsp.getProjCosponsorUserIds())) {
                rsp.setProjCosponsorUserNames(rsp.getProjCosponsorUserIds().stream().map(sysUserMap::get).collect(Collectors.toList()));
            }
            rsp.setClientName(clientMap.get(rsp.getClientId()));
        }
        return new Page<ProjEstablishBaseInfoListRSP>()
                .setCurrent(data.getCurrent())
                .setRecords(list)
                .setSize(data.getSize())
                .setTotal(data.getTotal());
    }

    @Transactional(rollbackFor = Throwable.class)
    public void disable(ProjEstablishBaseInfoRemoveREQ req) {
        if (CollUtil.isEmpty(req.getIds())) {
            return;
        }
        List<ProjEstablishBaseInfo> infos = baseMapper.selectBatchIds(req.getIds());
        Set<Long> clientIds = new HashSet<>();
        ProjEstablishBaseInfoListRSP projEstablishBaseInfoListRSP;
        for (ProjEstablishBaseInfo info : infos) {
            if (ObjectUtil.equal(info.getProjEstablishStatus(), CLOSED.name()) || ObjectUtil.equal(info.getProjEstablishStatus(), EXPIRE.name())) {
                throw new MithrasException(String.format("项目[%s]已关闭", info.getProjName()));
            }
            stateMachine.execute(ProjContext.of(info, ProjEvent.DISABLE, info.getProcessStatus()));
            projEstablishBaseInfoListRSP = baseInfoConverter.entityToDetailRSP(info);
            if(ObjectUtil.isNotEmpty(info.getLesseeInfo())){
                clientIds.addAll(projEstablishBaseInfoListRSP.getLesseeInfo().stream().map(ProjEstablishPersonInfo::getClientId).collect(Collectors.toSet()));
            }
            //债权人
            if(ObjectUtil.isNotEmpty(projEstablishBaseInfoListRSP.getCreditorInfo())){
                clientIds.addAll(projEstablishBaseInfoListRSP.getCreditorInfo().stream().map(ProjEstablishPersonInfo::getClientId).collect(Collectors.toSet()));
            }
            //债务人
            if(ObjectUtil.isNotEmpty(projEstablishBaseInfoListRSP.getDebtorInfo())){
                clientIds.addAll(projEstablishBaseInfoListRSP.getDebtorInfo().stream().map(ProjEstablishPersonInfo::getClientId).collect(Collectors.toSet()));
            }
            if(ObjectUtil.isNotEmpty(clientIds)){
                //clientIds.forEach(client -> clientService.updateClientAuth(client));
            }
        }

        for (Long id : req.getIds()) {
            checkCouldBeClosed(id);
        }
        // 关闭立项 需要结束审批流程 放在所有校验之后做 因为会发消息等
        for (Long id : req.getIds()) {
            ProcessResp processResp = projEstablishService.findRelatedProcess(id);
            if (Objects.nonNull(processResp)) {
                ExecutionProcessBaseREQ cancelProcessReq = new ExecutionProcessBaseREQ();
                cancelProcessReq.setProcessInstanceId(processResp.getProcessInstanceId());
                cancelProcessReq.setMessage("因关闭立项，审批自动取消");
                executionService.cancelProcess(cancelProcessReq);
            }
            // 通知客户权限变更
            ApplicationContextUtil.getApplicationContext().publishEvent(
                    new ClientViewAuthorityEvent(new ClientViewAuthorityEvent.ClientViewAuthorityInfo(
                            BusinessModuleEnum.PROJ_ESTABLISH,
                            id.toString(),
                            "项目立项-关闭")
                    )
            );
        }
        ProjEstablishBaseInfo toUpdate = new ProjEstablishBaseInfo();
        toUpdate.setProjEstablishStatus(CLOSED.name());
        baseMapper.update(toUpdate, Wrappers.<ProjEstablishBaseInfo>lambdaUpdate().in(ProjEstablishBaseInfo::getId, req.getIds()));
    }

    /**
     * 查询立项day天前生效的项目立项
     **/
    //todo 这里要除去公海客户
    public List<ProjEstablishBaseInfo> dayBeforeEffect(int day, Set<Long> projEstablishIds, boolean isDay) {
        LocalDate localDate = LocalDate.now().minusDays(day+1);
        Date beganDate = Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(localDate.plusDays(1).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
        //查询流程中结束时间
        ProcessPageReq flowReq = new ProcessPageReq();
        flowReq.setModelKeyList(BusinessModuleEnum.PROJ_ESTABLISH.getModelKeyList());
        flowReq.setSortType(1);
        flowReq.setPageIndex(1);
        flowReq.setPageSize(Integer.MAX_VALUE);
        cn.zswltech.flow.core.util.Page<ProcessResp> flowRespPage = taskApiService.queryProcess(flowReq);
        if(flowRespPage == null || flowRespPage.getContents() == null){
            return ListUtil.empty();
        }
        //已经到期合同
        List<ProcessResp> processResps = flowRespPage.getContents().stream().filter(base -> ObjectUtil.isNotEmpty(base.getEndTime()) && (isDay ? (base.getEndTime().before(beganDate) && endDate.before(base.getEndTime())) : base.getEndTime().before(beganDate))).collect(Collectors.toList());
        if(ObjectUtil.isEmpty(processResps)){
            return ListUtil.empty();
        }
        Set<Long> projEstablishEndIds = processResps.stream().map(ProcessResp::getBusinessKey).map(Long::valueOf).collect(Collectors.toSet());
        if (ObjectUtil.isNotEmpty(projEstablishEndIds) && ObjectUtil.isNotEmpty(projEstablishIds)){
            projEstablishEndIds.removeIf(e -> !projEstablishIds.contains(e));
        }
        if(ObjectUtil.isEmpty(projEstablishEndIds)){
            return ListUtil.empty();
        }
        //查询今天过期的立项
        return baseMapper.selectList(Wrappers.<ProjEstablishBaseInfo>lambdaQuery()
                .eq(ProjEstablishBaseInfo::getProjEstablishStatus, RecordStatus.TAKE_EFFECT.name())
                .in(ProjEstablishBaseInfo::getId, projEstablishEndIds));
    }

    private void checkCouldBeClosed(Long id) {
        List<ProjReviewBaseInfo> relatedReviewList = reviewBaseInfoMapper.selectList(
                Wrappers.<ProjReviewBaseInfo>lambdaQuery()
                        .notIn(ProjReviewBaseInfo::getProjReviewStatus, CLOSED.name(), EXPIRE.name())
                        .eq(ProjReviewBaseInfo::getProjEstablishId, id));
        if (isNotEmpty(relatedReviewList)) {
            ProjEstablishBaseInfo originalInfo = baseMapper.selectById(id);
            String projName = "";
            if (null != originalInfo) {
                projName = originalInfo.getProjName();
            }
            errMithras(true, String.format("已被评审关联，[%s]不能关闭", projName));
        }

    }

    public ProjEstablishBaseInfo getById(Long id) {
        return baseMapper.selectById(id);
    }

    public ProjEstablishBaseInfo getByCode(String projCode) {
        return baseMapper.selectOne(Wrappers.<ProjEstablishBaseInfo>lambdaQuery().eq(ProjEstablishBaseInfo::getProjCode, projCode).last("LIMIT 1"));
    }

    public R<ProjEstablishBaseInfoListRSP> detail(ProjEstablishBaseInfoDetailREQ req) {
        ProjEstablishBaseInfoListRSP rsp = null;
        if (StringUtils.isBlank(req.getVersion())) {
            // 前端不查版本
            ProjEstablishBaseInfo record = getById(req.getId());
            rsp = baseInfoConverter.entityToDetailRSP(record);
            setExposureRisk(rsp.getDebtorInfo());
            setExposureRisk(rsp.getPledgorInfo());
            setExposureRisk(rsp.getLesseeInfo());
            setExposureRisk(rsp.getMortgagorInfo());
            setExposureRisk(rsp.getGuaranteeInfo());
            join(rsp);
            rsp.setIsProjSponsor(Objects.equals(AccountUtil.getLoginInfo().getId(), rsp.getProjSponsorUserId()));
            // 新建状态并且未处于审批中，查询详情的时候需要静默更新一下评级
            if (Objects.equals(record.getProjEstablishStatus(), NEW.name()) && !Objects.equals(record.getProjEstablishProcessStatus(), ProcessStatus.UNDER_APPROVAL.name())) {
                ProjEstablishBaseInfoUpdateRatingREQ updateRatingREQ = new ProjEstablishBaseInfoUpdateRatingREQ();
                updateRatingREQ.setId(req.getId());
                ProjEstablishBaseInfoUpdateRatingRSP updateRatingRsp = this.updateRating(updateRatingREQ);
                // 如果有值，更新一下详情的返回值
                if (Objects.nonNull(updateRatingRsp)) {
                    if (Objects.nonNull(updateRatingRsp.getRatingClientId())) {
                        rsp.setRatingClientId(updateRatingRsp.getRatingClientId());
                    }
                    if (StrUtil.isNotBlank(updateRatingRsp.getRatingFinalScore())) {
                        rsp.setRatingFinalScore(updateRatingRsp.getRatingFinalScore());
                    }
                    if (Objects.nonNull(updateRatingRsp.getMainLesseeRatingId())) {
                        rsp.setRatingMainClientId(updateRatingRsp.getMainLesseeRatingId());
                    }
                    if (StrUtil.isNotBlank(updateRatingRsp.getMainLesseeFinalScore())) {
                        rsp.setRatingMainFinalScore(updateRatingRsp.getMainLesseeFinalScore());
                    }
                }
            }
        } else {
            ProjEstablishBaseInfoLib versionLib = projEstablishBaseInfoLibMapper.selectOne(Wrappers.<ProjEstablishBaseInfoLib>lambdaQuery()
                    .eq(ProjEstablishBaseInfoLib::getOriginId, req.getId())
                    .eq(ProjEstablishBaseInfoLib::getVersion, req.getVersion())
                    .last("LIMIT 1")
            );
            if (Objects.nonNull(versionLib)) {
                rsp = baseInfoLibHandler.actualLib2Rsp(versionLib);
            }
        }
        if(ObjectUtil.isNotEmpty(rsp)){
            Map<String, String> nameMap = addressDictionaryMapper.selectList(Wrappers.<AddressDictionary>lambdaQuery().in(AddressDictionary::getCode, ListUtil.toList(rsp.getProvince(), rsp.getCity(), rsp.getDistrict())))
                    .stream().collect(Collectors.toMap(AddressDictionary::getCode, AddressDictionary::getDisplay));
            StringBuilder st = new StringBuilder();
            if(ObjectUtil.isNotNull(nameMap.get(rsp.getProvince()))){
                st.append(nameMap.get(rsp.getProvince()));
            }
            if(ObjectUtil.isNotNull(nameMap.get(rsp.getCity()))){
                st.append(nameMap.get(rsp.getCity()));
            }
            if(ObjectUtil.isNotNull(nameMap.get(rsp.getDistrict()))){
                st.append(nameMap.get(rsp.getDistrict()));
            }
            rsp.setAreaName(st.toString());
            if(ObjectUtil.isNotEmpty(rsp.getEvaluationSubjectId())){
                rsp.setEvaluationSubjectName(id2NameService.clientId2NameSingle(rsp.getEvaluationSubjectId()));
            }

            List<Long> projCosponsorUserIds = Optional.ofNullable(rsp.getProjCosponsorUserIds())
                    .orElseGet(ArrayList::new).stream().collect(Collectors.toList());
            projCosponsorUserIds.add(rsp.getProjSponsorUserId());
//            CorpCommerceInfo corpCommerceInfo = commerceInfoService.detail(rsp.getClientId(), null);
//            if(corpCommerceInfo != null) {
//                rsp.setRiskControlIndustryClassify(corpCommerceInfo.getRiskControlIndustryClassify());
//            }
//            Date canSeeStartTime = getProjManagerOperateTime(req.getId());
//            Date ratingUpdateTime = rsp.getRatingUpdateTime();
//            if(ratingUpdateTime != null) {
//                // 取最新的日期
//                canSeeStartTime = ratingUpdateTime.compareTo(Optional.ofNullable(canSeeStartTime).orElse(new Date(0L))) >= 0 ? ratingUpdateTime : canSeeStartTime;
//            }
//            RatingClientProjDetailRSP clientRSP = ratingClientService.getEffectRating(90, rsp.getEvaluationSubjectId(), null, canSeeStartTime);
//            RatingClientProjDetailRSP mainClientRSP = null;
//            if(!Objects.equals(rsp.getEvaluationSubjectId(), rsp.getClientId())){
//                mainClientRSP = ratingClientService.getEffectRating(90, rsp.getClientId(), null, canSeeStartTime);
//            }else{
//                mainClientRSP = clientRSP;
//            }
//            rsp.setRatingClientId(clientRSP.getId());
//            rsp.setRatingFinalScore(clientRSP.getRatingFinalScore());
//            rsp.setRatingMainClientId(mainClientRSP.getId());
//            rsp.setRatingMainFinalScore(mainClientRSP.getRatingFinalScore());
        }
        return R.ok(rsp);
    }

    private Date getProjManagerOperateTime(Long id){
        // 查最新的流程
        ProcessResp processRespAndPass = projEstablishService.findRelatedAndPassProcess(id);
        if(processRespAndPass == null){
            return null;
        }
        // 查最近一次项目经理操作的时间点
        ProcessHistoryReq historyReq = new ProcessHistoryReq();
        historyReq.setPageIndex(1);
        historyReq.setPageSize(Integer.MAX_VALUE);
        historyReq.setProcessInstanceId(processRespAndPass.getProcessInstanceId());
        cn.zswltech.flow.core.util.Page<ProcessHistoryResp> history = flowProcessApiService.history(historyReq);
        Date finalDate = processRespAndPass.getStartTime();
        if (ObjectUtil.isNotEmpty(history) && ObjectUtil.isNotEmpty(history.getContents())) {
            for (ProcessHistoryResp content : history.getContents()) {
                if ("CHFQR".equals(content.getType()) || "BHFQR".equals(content.getType()) || "BHFQR_ZJDW".equals(content.getType())) {
                    finalDate = content.getOperateTime();
                }
            }
        }
        return finalDate;
    }

    /**
     * 用于详情查询填充冗余字段给前端显示
     *
     * @param rsp
     */
    public void join(ProjEstablishBaseInfoListRSP rsp) {
        Set<Long> sysUserIds = new HashSet<>();
        Set<Long> clientIds = new HashSet<>();
        Set<Long> deptIds = new HashSet<>();
        sysUserIds.add(rsp.getProjSponsorUserId());
        if (CollUtil.isNotEmpty(rsp.getProjCosponsorUserIds())) {
            sysUserIds.addAll(rsp.getProjCosponsorUserIds());
        }
        sysUserIds.add(rsp.getBizDeptLeaderId());
        sysUserIds.add(rsp.getBizDivisionLeaderId());

        if (isNotEmpty(rsp.getRiskControlManagerId())) {
            sysUserIds.addAll(rsp.getRiskControlManagerId());
        }

        deptIds.add(rsp.getBizDeptId());
        if (CollUtil.isNotEmpty(rsp.getLesseeInfo())) {
            clientIds.addAll(rsp.getLesseeInfo().stream().map(ProjEstablishPersonInfo::getClientId).collect(Collectors.toList()));
        }
        if (CollUtil.isNotEmpty(rsp.getCreditorInfo())) {
            clientIds.addAll(rsp.getCreditorInfo().stream().map(ProjEstablishPersonInfo::getClientId).collect(Collectors.toList()));
        }
        if (CollUtil.isNotEmpty(rsp.getDebtorInfo())) {
            clientIds.addAll(rsp.getDebtorInfo().stream().map(ProjEstablishPersonInfo::getClientId).collect(Collectors.toList()));
        }
        if (CollUtil.isNotEmpty(rsp.getGuaranteeInfo())) {
            clientIds.addAll(rsp.getGuaranteeInfo().stream().map(ProjEstablishPersonInfo::getClientId).collect(Collectors.toList()));
        }
        if (CollUtil.isNotEmpty(rsp.getMortgagorInfo())) {
            clientIds.addAll(rsp.getMortgagorInfo().stream().map(ProjEstablishPersonInfo::getClientId).collect(Collectors.toList()));
        }
        if (CollUtil.isNotEmpty(rsp.getPledgorInfo())) {
            clientIds.addAll(rsp.getPledgorInfo().stream().map(ProjEstablishPersonInfo::getClientId).collect(Collectors.toList()));
        }
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


        if (isNotEmpty(rsp.getRiskControlManagerId())) {
            rsp.setRiskControlManagerName(rsp.getRiskControlManagerId().stream().map(sysUserMap::get).collect(Collectors.toList()));
        }
        if (CollUtil.isNotEmpty(rsp.getLesseeInfo())) {
            rsp.getLesseeInfo().stream().filter(e -> isNotNull(e.getClientId())).forEach(e -> e.setClientName(clientMap.get(e.getClientId())));
        }
        if (CollUtil.isNotEmpty(rsp.getCreditorInfo())) {
            rsp.getCreditorInfo().stream().filter(e -> isNotNull(e.getClientId())).forEach(e -> e.setClientName(clientMap.get(e.getClientId())));
        }
        if (CollUtil.isNotEmpty(rsp.getDebtorInfo())) {
            rsp.getDebtorInfo().stream().filter(e -> isNotNull(e.getClientId())).forEach(e -> e.setClientName(clientMap.get(e.getClientId())));
        }
        if (CollUtil.isNotEmpty(rsp.getGuaranteeInfo())) {
            rsp.getGuaranteeInfo().stream().filter(e -> isNotNull(e.getClientId())).forEach(e -> e.setClientName(clientMap.get(e.getClientId())));
        }
        if (CollUtil.isNotEmpty(rsp.getMortgagorInfo())) {
            rsp.getMortgagorInfo().stream().filter(e -> isNotNull(e.getClientId())).forEach(e -> e.setClientName(clientMap.get(e.getClientId())));
        }
        if (CollUtil.isNotEmpty(rsp.getPledgorInfo())) {
            rsp.getPledgorInfo().stream().filter(e -> isNotNull(e.getClientId())).forEach(e -> e.setClientName(clientMap.get(e.getClientId())));
        }
        Map<String, String> nameMap = addressDictionaryMapper.selectList(Wrappers.<AddressDictionary>lambdaQuery().in(AddressDictionary::getCode, ListUtil.toList(rsp.getProvince(), rsp.getCity(), rsp.getDistrict())))
                .stream().collect(Collectors.toMap(AddressDictionary::getCode, AddressDictionary::getDisplay, (a, b) -> a));
        if(ObjectUtil.isNotEmpty(nameMap)){
            StringBuilder st = new StringBuilder();
            if(ObjectUtil.isNotNull(nameMap.get(rsp.getProvince()))){
                st.append(nameMap.get(rsp.getProvince()));
            }
            if(ObjectUtil.isNotNull(nameMap.get(rsp.getCity()))){
                st.append(nameMap.get(rsp.getCity()));
            }
            if(ObjectUtil.isNotNull(nameMap.get(rsp.getDistrict()))){
                st.append(nameMap.get(rsp.getDistrict()));
            }
            rsp.setAreaName(st.toString());
        }
        if(ObjectUtil.isNotEmpty(rsp.getEvaluationSubjectId())){
            rsp.setEvaluationSubjectName(id2NameService.clientId2NameSingle(rsp.getEvaluationSubjectId()));
        }
    }


    public void setExposureRisk(List<ProjEstablishPersonInfo> list) {
        if (CollUtil.isNotEmpty(list)) {
            for (ProjEstablishPersonInfo personInfo : list) {
                personInfo.setStockRiskExposure(contractBaseInfoService.getStockRiskExposure(personInfo.getClientId(), null, null));
            }
        }
    }

    public List<ProjEstablishBaseInfo> listByClients(List<Long> clientIdList) {
        return this.getBaseMapper().selectList(Wrappers.<ProjEstablishBaseInfo>lambdaQuery()
                .in(ProjEstablishBaseInfo::getClientId, clientIdList)
                .notIn(ProjEstablishBaseInfo::getProjEstablishStatus, ListUtil.toList(CLOSED.name(), EXPIRE.name()))
        );
    }

    public ProjEstablishBaseInfoUpdateRatingRSP updateRating(ProjEstablishBaseInfoUpdateRatingREQ req) {
        Date date = new Date();
        ProjEstablishBaseInfoUpdateRatingRSP rsp = new ProjEstablishBaseInfoUpdateRatingRSP();
        ProjEstablishBaseInfo establishBaseInfo = this.getById(req.getId());
        if(establishBaseInfo != null){
            boolean needUpdateStatus = false;
            RatingClientProjDetailRSP evaluationSubjectScoreAfter = ratingClientService.getEffectRating(90, establishBaseInfo.getEvaluationSubjectId(), null, date);
            RatingClientProjDetailRSP mainLesseeScoreAfter = ratingClientService.getEffectRating(90, establishBaseInfo.getClientId(), null, date);
            LambdaUpdateWrapper<ProjEstablishBaseInfo> updateWrapper = Wrappers.lambdaUpdate();
            updateWrapper.eq(ProjEstablishBaseInfo::getId, req.getId());
            updateWrapper.set(ProjEstablishBaseInfo::getRatingUpdateTime,date);
            if (Objects.nonNull(evaluationSubjectScoreAfter) && StrUtil.isNotBlank(evaluationSubjectScoreAfter.getRatingFinalScore())) {
                needUpdateStatus = true;
                updateWrapper.set(ProjEstablishBaseInfo::getEvaluationSubjectRatingScoreId, evaluationSubjectScoreAfter.getId());
                updateWrapper.set(ProjEstablishBaseInfo::getEvaluationSubjectRatingScore, evaluationSubjectScoreAfter.getRatingFinalScore());
                rsp.setRatingClientId(evaluationSubjectScoreAfter.getId());
                rsp.setRatingFinalScore(evaluationSubjectScoreAfter.getRatingFinalScore());
            }
            if (Objects.nonNull(mainLesseeScoreAfter) && StrUtil.isNotBlank(mainLesseeScoreAfter.getRatingFinalScore())) {
                needUpdateStatus = true;
                updateWrapper.set(ProjEstablishBaseInfo::getMainLesseeRatingScoreId, mainLesseeScoreAfter.getId());
                updateWrapper.set(ProjEstablishBaseInfo::getMainLesseeRatingScore, mainLesseeScoreAfter.getRatingFinalScore());
                rsp.setMainLesseeRatingId(mainLesseeScoreAfter.getId());
                rsp.setMainLesseeFinalScore(mainLesseeScoreAfter.getRatingFinalScore());
            }
            this.update(updateWrapper);
            if (needUpdateStatus) {
                // 更新状态
                recordStatus(req.getId());
            }
        }
        return rsp;
    }

    /**
     * 维护失效状态
     **/
    @Transactional(rollbackFor = Throwable.class)
    public void doExpire(Integer days) {
        //1.查找所有立项审批通过时间
        ProcessPageReq flowReq = new ProcessPageReq();
        flowReq.setModelKeyList(Collections.singletonList(ProcessModelTypeEnum.ProjEstablishCreateFlow.name()));
        flowReq.setSortType(1);
        flowReq.setPageIndex(1);
        flowReq.setPageSize(Integer.MAX_VALUE);
        flowReq.setProcessStatusList(ListUtil.toList(2, 6));
        cn.zswltech.flow.core.util.Page<ProcessResp> flowRespPage = taskApiService.queryProcess(flowReq);
        if (ObjectUtil.isEmpty(flowRespPage) || ObjectUtil.isEmpty(flowRespPage.getContents())) {
            return;
        }
        Set<Long> needExpireIds = new HashSet<>();
        List<Long> projEstablishIds = flowRespPage.getContents().stream().filter(e -> isNotEmpty(e.getEndTime())).filter(e -> LocalDateTimeUtil.of(e.getEndTime()).plusDays(days).isBefore(LocalDateTime.now())).map(ProcessResp::getBusinessKey).map(Long::valueOf).collect(Collectors.toList());
        if (ObjectUtil.isEmpty(projEstablishIds)) {
            return;
        }
        //剔除已经关闭或者失效的
        projEstablishIds = this.baseMapper.selectList(Wrappers.<ProjEstablishBaseInfo>lambdaQuery()
        .in(ProjEstablishBaseInfo::getId, projEstablishIds)
        .notIn(ProjEstablishBaseInfo::getProjEstablishStatus, CLOSED.name(), EXPIRE.name())).stream().map(ProjEstablishBaseInfo::getId).collect(Collectors.toList());
        if (ObjectUtil.isEmpty(projEstablishIds)) {
            return;
        }
        //2.剔除已经提起项目立项审批的
        List<ProjReviewBaseInfo> projReviewBaseInfos = reviewBaseInfoMapper.selectList(Wrappers.<ProjReviewBaseInfo>lambdaQuery()
                .in(ProjReviewBaseInfo::getProjEstablishId, projEstablishIds));
        //需要修改为释放
        if (ObjectUtil.isNotEmpty(projReviewBaseInfos)) {
            Map<Long, List<ProjReviewBaseInfo>> projEsId2Review = projReviewBaseInfos.stream().collect(Collectors.groupingBy(ProjReviewBaseInfo::getProjEstablishId));
            for (Long esId : projEstablishIds) {
                List<ProjReviewBaseInfo> tmpReviewBaseInfos = projEsId2Review.get(esId);
                if (isEmpty(tmpReviewBaseInfos)) {
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
            needExpireIds = new HashSet<>(projEstablishIds);
        }
        //释放
        if (ObjectUtil.isNotEmpty(needExpireIds)) {
            LambdaUpdateWrapper<ProjEstablishBaseInfo> wrapper = new LambdaUpdateWrapper<>();
            wrapper.set(ProjEstablishBaseInfo::getProjEstablishStatus, EXPIRE.name());
            wrapper.in(ProjEstablishBaseInfo::getId, needExpireIds);
            SpringContextHolder.getBean(ProjEstablishBaseInfoService.class).update(wrapper);
        }
    }
}
