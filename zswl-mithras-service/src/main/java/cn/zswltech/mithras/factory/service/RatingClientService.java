package cn.zswltech.mithras.factory.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.zswltech.decision.engine.api.common.dto.R;
import cn.zswltech.decision.engine.dto.decision.DecisionModelREQ;
import cn.zswltech.decision.engine.dto.decision.DecisionModelRSP;
import cn.zswltech.decision.engine.dto.decision.DecisionREQ;
import cn.zswltech.decision.engine.dto.decision.DecisionRSP;
import cn.zswltech.flow.core.api.FlowProcessApiService;
import cn.zswltech.flow.core.api.FlowTaskApiService;
import cn.zswltech.flow.core.dao.OperateRecordMapper;
import cn.zswltech.flow.core.domain.entity.OperateRecord;
import cn.zswltech.flow.core.domain.req.StartProcessReq;
import cn.zswltech.flow.core.domain.req.task.ProcessPageReq;
import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.flow.core.enums.CommentTypeEnum;
import cn.zswltech.flow.core.enums.ProcessBusinessStatusEnum;
import cn.zswltech.flow.core.enums.ProcessNodeVariableEnum;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.gruul.dao.dal.entity.OrgDO;
import cn.zswltech.gruul.dao.dal.entity.UserDO;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.basic.Constant;
import cn.zswltech.mithras.customer.hymx.infrastructure.model.ClientHymx;
import cn.zswltech.mithras.dto.client.client.ClientUnifiedRatingHistoryRSP;
import cn.zswltech.mithras.dto.rating.*;
import cn.zswltech.mithras.dto.rating.decision.DecisionExecuteResult;
import cn.zswltech.mithras.dto.rating.ratingclient.*;
import cn.zswltech.mithras.factory.enums.*;
import cn.zswltech.mithras.factory.feign.DecisionApiClient;
import cn.zswltech.mithras.factory.lib.ratingclient.RatingClientLibService;
import cn.zswltech.mithras.factory.lib.ratingclient.impl.RatingClientVersionServiceImpl;
import cn.zswltech.mithras.factory.mapper.AreaInfoMapper;
import cn.zswltech.mithras.factory.mapper.RatingClientMapper;
import cn.zswltech.mithras.factory.mapper.RatingReportMapper;
import cn.zswltech.mithras.factory.mapper.RzyDmCalculateIndicatorMapper;
import cn.zswltech.mithras.factory.model.*;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.service.constant.VersionTypeConstants;
import cn.zswltech.mithras.service.enums.*;
import cn.zswltech.mithras.customer.domain.enums.client.ClientLevelEnum;
import cn.zswltech.mithras.customer.domain.enums.client.ClientType;
import cn.zswltech.mithras.service.enums.common.ProcessStatus;
import cn.zswltech.mithras.riskcontrol.common.RiskControlIndustryClassify;
import cn.zswltech.mithras.service.mapper.AddressDictionaryMapper;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.corp.CorpAddressInfoMapper;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.corp.IndustryTypeMapper;
import cn.zswltech.mithras.service.mapper.model.AddressDictionary;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.*;
import cn.zswltech.mithras.service.mapper.model.process.prepare.CommonProcessPrepare;
import cn.zswltech.mithras.service.mapper.process.prepare.CommonProcessPrepareMapper;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.others.SpringContextHolder;
import cn.zswltech.mithras.service.service.BizProcessDataService;
import cn.zswltech.mithras.service.service.Id2NameService;
import cn.zswltech.mithras.service.service.SysUserService;
import cn.zswltech.mithras.service.service.client.ClientAuthorityService;
import cn.zswltech.mithras.customer.hymx.application.ClientHymxService;
import cn.zswltech.mithras.service.service.client.ClientService;
import cn.zswltech.mithras.service.service.client.CorpCommerceInfoService;
import cn.zswltech.mithras.service.service.flow.FlowEndEventProcessor;
import cn.zswltech.mithras.service.util.ClientAuthorityUtil;
import cn.zswltech.mithras.service.util.StringUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static cn.hutool.core.text.CharSequenceUtil.equalsAny;
import static cn.hutool.core.util.ObjectUtil.isNotNull;
import static cn.zswltech.mithras.basic.Constant.hymxScoreMap;
import static cn.zswltech.mithras.basic.Constant.subjectVesselSet;
import static cn.zswltech.mithras.service.enums.ProcessModelTypeEnum.RatingClientCreateFlow;
import static cn.zswltech.mithras.service.enums.ProcessModelTypeEnum.RatingClientUpdateFlow;

@Slf4j
@Service
public class RatingClientService extends ServiceImpl<RatingClientMapper, RatingClient> implements FlowEndEventProcessor {

    @Resource
    private AddressDictionaryMapper addressDictionaryMapper;
    @Resource
    private CorpAddressInfoMapper corpAddressInfoMapper;
    @Resource
    private Id2NameService id2NameService;
    @Resource
    private CorpCommerceInfoService corpCommerceInfoService;
    @Resource
    private FlowProcessApiService processApiService;
    @Resource
    private BizProcessDataService bizProcessDataService;
    @Resource
    private DecisionApiClient decisionApiClient;
    @Resource
    private SysUserService sysUserService;
    @Resource
    private ClientService clientService;
    @Resource
    private RatingClientLibService libService;
    @Resource
    private DecisionService decisionService;
    @Resource
    private RatingSnapshotService ratingSnapshotService;
    @Resource
    private RatingReportMapper ratingReportMapper;
    @Resource
    private RatingClientVersionServiceImpl versionService;
    @Resource
    private CommonProcessPrepareMapper commonProcessPrepareMapper;
    @Resource
    private IndustryTypeMapper industryTypeMapper;
    @Resource
    private AreaInfoMapper areaInfoMapper;
    @Resource
    private FlowTaskApiService taskApiService;
    @Resource
    private OperateRecordMapper operateRecordMapper;
    @Resource
    private RatingReportService ratingReportService;
    @Resource
    private RzyDmCalculateIndicatorMapper dmCalculateIndicatorMapper;
    @Resource
    private RatingClientAreaIndicatorService ratingClientAreaIndicatorService;
    @Resource
    private ClientHymxService clientHymxService;

    /**
     * 客户评级列表
     *
     * @param req
     * @return
     */
    public PageR<RatingClientPageRSP> ratingClientPage(RatingClientPageREQ req) {
        LambdaQueryWrapper<RatingClient> wrapper = createCondition(req);
        Page<RatingClient> page = this.page(new Page<>(req.getPage(), req.getPageSize()), wrapper);
        List<RatingClient> records = page.getRecords();
        if (records == null) {
            return new PageR<>();
        }
        Set<Long> userIdList = records.stream().map(RatingClient::getCreateBy).collect(Collectors.toSet());
        Set<Long> clientIdList = records.stream().map(RatingClient::getClientId).collect(Collectors.toSet());
        Set<Long> deptIdList = records.stream().map(RatingClient::getBelongDeptId).collect(Collectors.toSet());
        Map<Long, String> userId2Name = id2NameService.sysUserId2Name(userIdList);
        Map<Long, String> clientId2Name = id2NameService.clientId2Name(clientIdList);
        Map<Long, String> deptId2Name = id2NameService.deptId2Name(deptIdList);

        List<RatingClientPageRSP> result = records.stream().map(record -> {
            RatingClientPageRSP rsp = new RatingClientPageRSP();
            BeanUtil.copyProperties(record, rsp);
            if (!ProcessStatus.APPROVAL_PASS.name().equals(record.getProcessStatus())) {
                // 只有流程状态为“已通过”的评级显示评级认定结果
                rsp.setFinalScore(null);
            }
            rsp.setCreateByName(userId2Name.get(record.getCreateBy()));
            rsp.setClientName(clientId2Name.get(record.getClientId()));
            rsp.setStartOrg(deptId2Name.get(record.getBelongDeptId()));
            return rsp;
        }).collect(Collectors.toList());
        return PageR.of(page, result);
    }

    /**
     * 客户评级详情
     *
     * @param req
     * @return
     */
    public RatingClientDetailRSP ratingClientDetail(RatingClientDetailREQ req) {
        RatingClient ratingClient = this.getById(req.getId());
        if (ratingClient == null) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        String createByName = id2NameService.sysUserId2NameSingle(ratingClient.getCreateBy());
        String clientName = id2NameService.clientId2NameSingle(ratingClient.getClientId());
        String deptName = id2NameService.deptId2NameSingle(ratingClient.getBelongDeptId());

        RatingClientDetailRSP rsp = new RatingClientDetailRSP();
        BeanUtil.copyProperties(ratingClient, rsp);
//        RatingSnapshot ratingSnapshot = ratingSnapshotService.getById(ratingClient.getSnapshotId());
//        if(ratingSnapshot != null){
//            DecisionExecuteResult executeResult = JSON.parseObject(ratingSnapshot.getScore(), DecisionExecuteResult.class);
//            if(executeResult != null) {
//                rsp.setScore(executeResult.getFirstScore());
//                rsp.setAdjustScore(Optional.ofNullable(executeResult.getScore()).orElse(executeResult.getFirstScore()));
//            }
//        }
        if (ratingClient.getSnapshotId() != null) {
            RatingSnapshotDetailRSP snapshotDetail = ratingSnapshotService.getSnapshotDetail(ratingClient.getSnapshotId());
            rsp.setFirstScore(Optional.ofNullable(snapshotDetail.getScoreRsp()).map(DecisionExecuteResult::getFirstScore).orElse(null));
        }
        rsp.setAdjustScore(getAdjustScore(ratingClient));
        rsp.setFinalScore(getFinalScore(ratingClient));
        if (rsp.getFinalScore() != null) {
            rsp.setFinalScoreValue(RatingLevelEnum.findByDisplay(rsp.getFinalScore()).name());
        }
        rsp.setClientId(ratingClient.getClientId());
        rsp.setClientName(clientName);
        rsp.setCreateByName(createByName);
        rsp.setStartOrg(deptName);
        return rsp;
    }

    public RatingClientInfoRSP ratingClientHymxInfo(RatingClientInfoREQ req, ClientHymx clientHymx) {

        RatingClientInfoRSP rsp = new RatingClientInfoRSP();
        /*如果航运模型没有存储过该用户信息  什么都不处理由前端手动填写，否则反馈其中的航运客户信息*/
        if (clientHymx == null) {
            return rsp;
        }
        rsp.setClientCode(clientHymx.getClientCode());
        rsp.setClientName(clientHymx.getClientName());
        rsp.setHymxFlag(true);
        return rsp;
    }

    /**
     * 客户信息
     *
     * @param req
     * @return
     */
    public RatingClientInfoRSP ratingClientInfo(RatingClientInfoREQ req) {
        Long clientId = req.getClientId();
        if (clientId == null) {
            RatingClient ratingClient = this.getById(req.getId());
            if (ratingClient == null) {
                throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
            }
            clientId = ratingClient.getClientId();
        }
        /*航运模型 部分信息可能来自于client_hymc*/
        if (clientId > Constant.hymxIndexStart - 1) {
            ClientHymx clientHymx = clientHymxService.getById(clientId);
            return ratingClientHymxInfo(req, clientHymx);
        }

        CorpCommerceInfo corpCommerceInfo = corpCommerceInfoService.detail(clientId, null);
        if (corpCommerceInfo == null) {
            throw new MithrasException("该客户不存在");
        }
        CorpAddressInfo corpAddressInfo = corpAddressInfoMapper.selectOne(Wrappers.<CorpAddressInfo>lambdaQuery()
                .eq(CorpAddressInfo::getClientId, clientId)
                .eq(CorpAddressInfo::getAddressType, CorpAddressType.REGISTRY_ADDRESS.name())
                .orderByDesc(CorpAddressInfo::getCreateTime)
                .last(StringUtil.mysqlLimitOne()));
        if (corpAddressInfo == null) {
            throw new MithrasException("请先维护该客户的地址信息");
        }
        List<String> codeList = Stream.of(corpAddressInfo.getDistrict(), corpAddressInfo.getCity(), corpAddressInfo.getProvince()).collect(Collectors.toList());
        Map<String, String> nameMap = addressDictionaryMapper.selectList(Wrappers.<AddressDictionary>lambdaQuery()
                        .in(CollectionUtils.isNotEmpty(codeList), AddressDictionary::getCode, codeList))
                .stream().collect(Collectors.toMap(AddressDictionary::getCode, AddressDictionary::getDisplay, (m1, m2) -> m1));

        RatingClientInfoRSP rsp = new RatingClientInfoRSP();
        BeanUtil.copyProperties(corpCommerceInfo, rsp);
        if (CollectionUtils.isNotEmpty(nameMap)) {
            // 行业分类
            IndustryType industryType = industryTypeMapper.selectOne(Wrappers.<IndustryType>lambdaQuery().eq(IndustryType::getCode, rsp.getIndustryType()));
            if (isNotNull(industryType)) {
                rsp.setIndustryTypeName(industryType.getDisplay());
            }
            // 地区
            rsp.setDistrict(nameMap.get(corpAddressInfo.getDistrict()));
            rsp.setCity(nameMap.get(corpAddressInfo.getCity()));
            rsp.setProvince(nameMap.get(corpAddressInfo.getProvince()));
        }
        notNullValid(rsp);
        return rsp;
    }

    public RatingClientAddRSP ratingClientHymxAdd(RatingClientAddREQ req) {
        ClientHymx clientHymx = null;
        LambdaQueryWrapper<ClientHymx> wrapper = Wrappers.lambdaQuery();
        if(req.getClientId() != null){
            wrapper.eq(ClientHymx::getId, req.getClientId());
        }else {
            wrapper.eq(ClientHymx::getClientName, req.getClientName());
        }
        List<ClientHymx> clientHymxs = clientHymxService.list(wrapper);
        Long clientId = null;
        if (clientHymxs.size() == 0) { // 防止恶意写入相同名称的客户
            clientHymx = new ClientHymx();
            clientHymx.setClientName(req.getClientName());
            clientHymx.setProjectManager(AccountUtil.getLoginInfo().getId());
            clientHymx.setBelongDeptId(Optional.ofNullable(sysUserService.getUserDept()).map(OrgDO::getId).orElse(null));
            clientHymxService.save(clientHymx);

            /*根据数据库中存储的id  生成唯一的客户编码并保存  默认航运用户主键开始于500000000000L*/
            clientHymx = clientHymxService.getOne(wrapper);
            clientHymx.setClientCode("HY" + (clientHymx.getId() - Constant.hymxIndexStart + 100000L));
            clientHymxService.updateById(clientHymx);
            clientId = clientHymx.getId();
        } else {
            clientHymx = clientHymxs.get(0);
            clientId = clientHymx.getId();
        }

        /*判断是否存在流程中的或者其他不同模型为生效的数据*/
        LambdaQueryWrapper<RatingClient> clientWrapper = Wrappers.lambdaQuery();
        clientWrapper.eq(RatingClient::getModelCode, Constant.hymxCode)
                .eq(RatingClient::getClientId, clientId)
                .eq(RatingClient::getRatingStatus, false)
                .isNull(RatingClient::getAbandonTime);
        List<RatingClient> existRatingClientList = this.list(clientWrapper);
        if (existRatingClientList.size() > 0) {
            RatingClient existRatingClient = existRatingClientList.get(0);
            // 存在未生效评级，返回id，展示草稿数据
            if (ProcessStatus.UNDER_APPROVAL.name().equals(existRatingClient.getProcessStatus())) {
                throw new MithrasException("该评级正处于流程中");
            }
            if (req.getCode().equals(existRatingClient.getModelCode())) {
                return new RatingClientAddRSP(existRatingClient.getId());
            } else {
                throw new MithrasException("存在不同模型的未生效评级数据，无法新建评级");
            }
        }

        RatingClient ratingClient = new RatingClient();
        /*如果存在已经审批通过的评级则为复评  否则为初评*/
        LambdaQueryWrapper<RatingClient> lambdaQueryWrapper = Wrappers.lambdaQuery();
        if (Constant.hymxCode.equals(req.getCode())) {// 航运模型
            lambdaQueryWrapper.eq(RatingClient::getModelCode, Constant.hymxCode);
        }
        lambdaQueryWrapper.eq(RatingClient::getClientId, clientId)
                .eq(RatingClient::getProcessStatus, Constant.processStatusPass);
        List<RatingClient> existPassRatingClient = this.list(lambdaQueryWrapper);
        if (existPassRatingClient.size() > 0) {
            ratingClient.setRatingType(Constant.ratingTypeOther);
        } else {
            ratingClient.setRatingType(Constant.ratingTypeFirst);
        }
        ratingClient.setClientId(clientId);
        ratingClient.setClientName(clientHymx.getClientName());
        ratingClient.setClientCode(clientHymx.getClientCode());
        ratingClient.setModelCode(req.getCode());
        ratingClient.setModelName(req.getName());
        ratingClient.setBelongDeptId(Optional.ofNullable(sysUserService.getUserDept()).map(OrgDO::getId).orElse(null));
        ratingClient.setBelongSponsorUserId(clientHymx.getProjectManager());
//        ratingClient.setUscc("");
        this.save(ratingClient);

        return new RatingClientAddRSP(ratingClient.getId());
    }

    /**
     * 新增评级
     */
    public RatingClientAddRSP ratingClientAdd(RatingClientAddREQ req) {
        if (Constant.hymxCode.equals(req.getCode())) { // 航运模型  内部客户走之前逻辑 外部客户没有地址等信息
            if (req.getClientId() != null) {
                ClientHymx clientHymx = clientHymxService.getById(req.getClientId());
                if (clientHymx != null) {
                    return ratingClientHymxAdd(req);
                }
            } else {
                return ratingClientHymxAdd(req);
            }
        }
//        if(req.getId() != null){
//            // 存在生效评级，代表修改评级，调整原评级为失效
//            update(Wrappers.<RatingClient>lambdaUpdate().eq(RatingClient::getId,req.getId())
//                    .set(RatingClient::getRatingStatus,false)
//                    .set(RatingClient::getAbandonTime,LocalDate.now()));
//        }
        Long clientId = req.getClientId();
        Client client = clientService.getById(clientId);
        if (client == null) {
            throw new MithrasException("客户不存在");
        }
        if (Objects.equals(client.getClientType(), ClientType.NORMAL.name())) {
            throw new MithrasException("自然人无需评级");
        }
        if (!SpringUtil.getBean(ClientAuthorityUtil.class).isIntraGroupCollaboration(clientId)) {
            // 非公海需要判断管护权和申办权
            LambdaQueryWrapper<ClientAuthority> queryWrapper = Wrappers.lambdaQuery();
            queryWrapper.eq(ClientAuthority::getClientId, clientId);
            queryWrapper.eq(ClientAuthority::getUserId, AccountUtil.getLoginInfo().getId());
            List<ClientAuthority> clientAuthorityList = SpringUtil.getBean(ClientAuthorityService.class).list(queryWrapper);
            boolean canDo = false;
            if (CollectionUtil.isNotEmpty(clientAuthorityList)) {
                for (ClientAuthority clientAuthority : clientAuthorityList) {
                    if (Objects.equals(clientAuthority.getLevel(), ClientLevelEnum.MANAGE.getLevel())) {
                        canDo = true;
                        break;
                    }
                    if (Objects.equals(clientAuthority.getLevel(), ClientLevelEnum.APPLY.getLevel())) {
                        canDo = true;
                        break;
                    }
                }
            }
            if (!canDo) {
                throw new MithrasException("仅管护权/申办权人可发起该客户的评级");
            }
//            ClientAuthority clientAuthority = SpringUtil.getBean(ClientAuthorityService.class).getSpecificClientManagerAuthority(clientId);
//            if (Objects.isNull(clientAuthority) || !Objects.equals(clientAuthority.getUserId(), AccountUtil.getLoginInfo().getId())) {
//                throw new MithrasException("仅管护权人可发起该客户的评级");
//            }
        }
        RatingClient existRatingClient = this.getOne(Wrappers.<RatingClient>lambdaQuery()
                .eq(RatingClient::getClientId, clientId)
                .eq(RatingClient::getRatingStatus, false)
                .isNull(RatingClient::getAbandonTime));
        if (existRatingClient != null) {
            // 存在未生效评级，返回id，展示草稿数据
            if (ProcessStatus.UNDER_APPROVAL.name().equals(existRatingClient.getProcessStatus())) {
                throw new MithrasException("该评级正处于流程中");
            }
            if (req.getCode().equals(existRatingClient.getModelCode())) {
                return new RatingClientAddRSP(existRatingClient.getId());
            } else {
                throw new MithrasException("存在不同模型的未生效评级数据，无法新建评级");
            }
        }
        RatingClient ratingClient = new RatingClient();
        /*如果存在已经审批通过的评级则为复评  否则为初评*/
        LambdaQueryWrapper wrapper = new LambdaQueryWrapper<RatingClient>()
                .eq(RatingClient::getClientId, clientId)
                .eq(RatingClient::getProcessStatus, Constant.processStatusPass);
        List<RatingClient> existPassRatingClient = this.list(wrapper);
        if (existPassRatingClient.size() > 0) {
            ratingClient.setRatingType(Constant.ratingTypeOther);
        } else {
            ratingClient.setRatingType(Constant.ratingTypeFirst);
        }
        ratingClient.setClientId(clientId);
        ratingClient.setClientName(client.getClientName());
        ratingClient.setClientCode(client.getClientCode());
        ratingClient.setModelCode(req.getCode());
        ratingClient.setModelName(req.getName());
        ratingClient.setBelongDeptId(Optional.ofNullable(sysUserService.getUserDept()).map(OrgDO::getId).orElse(null));
        ratingClient.setBelongSponsorUserId(client.getBelongSponsorId());
        ratingClient.setUscc(client.getUscCode());
        this.save(ratingClient);
        // 不管是否是区县类模型都初始化一份区域指标的系统数据，这样相对简单，不用考虑模型类型和模型切换带来的额外逻辑
        Long areaUniCode = this.getClientAreaUniCode(ratingClient.getClientId(), ratingClient.getModelName());
        ratingClientAreaIndicatorService.create(ratingClient.getId(), areaUniCode, LocalDate.now().getYear());
        return new RatingClientAddRSP(ratingClient.getId());
    }

    //检查改评级下是否可关闭
    public void checkCancelProcess(Long ratingClientId) {
        RatingClient byId = this.getById(ratingClientId);
        if (ObjectUtil.isEmpty(byId)) {
            return;
        }
        Map<Long, Long> clientRemainingPrincipalMap = SpringContextHolder.getBean(ClientService.class).getClientRemainingPrincipalMap(Collections.singletonList(byId.getClientId()));
        if (clientRemainingPrincipalMap.getOrDefault(byId.getClientId(), 0L) > 0) {
            throw new MithrasException("项目未结清，不可关闭此流程");
        }
    }

    /**
     * 提交审批流
     *
     * @param req
     */
    @Transactional(rollbackFor = Throwable.class)
    public void ratingClientEffect(RatingClientEffectREQ req) {
        RatingClient ratingClient = this.getById(req.getId());
        if (ratingClient == null) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        if (ratingClient.getScore() == null || ratingClient.getReportId() == null) {
            throw new MithrasException("未完成评级,不可提交流程");
        }
        ProcessResp relatedProcess = findRelatedProcess(req.getId());
        if (relatedProcess != null) {
            throw new MithrasException("数据已在流程中，请勿重复提交");
        }
        StartProcessReq startProcessReq = new StartProcessReq();
        int count = this.count(Wrappers.<RatingClient>lambdaQuery().eq(RatingClient::getClientId, ratingClient.getClientId())
                .eq(RatingClient::getRatingStatus, true));
        startProcessReq.setModelKey(count > 0 ? ProcessModelTypeEnum.RatingClientUpdateFlow.name() : ProcessModelTypeEnum.RatingClientCreateFlow.name());
        String clientName = id2NameService.clientId2NameSingle(ratingClient.getClientId());

        startProcessReq.setProcessInstanceName(clientName);
        Map<String, Object> varMap = new HashMap<>();
        OrgDO userDept = sysUserService.getUserDept();
        if (Objects.isNull(userDept)) {
            throw new MithrasException("当前用户部门为空");
        }
        List<UserDO> bizDeptLeaderUserList = sysUserService.listSpecificOrgJobUser(userDept.getId(), JobEnum.businesshead.name());
        List<UserDO> divisionLeaderUserList = sysUserService.listSpecificOrgJobUser(userDept.getId(), JobEnum.leaderincharge.name());
        //风控经理 先按部门查询，部门没有查所有
        List<String> riskControlManagerIds = SpringContextHolder.getBean(SysUserService.class).getRiskManagerIdsOrderByDeptId().get(ratingClient.getBelongDeptId());
        if (CollectionUtil.isEmpty(riskControlManagerIds)) {
            riskControlManagerIds = sysUserService.getAllRiskControlManagerIds().stream().map(String::valueOf).collect(Collectors.toList());
        }
        varMap.put("deptLeader", bizDeptLeaderUserList.stream().map(UserDO::getId).map(String::valueOf).collect(Collectors.toList()));
        varMap.put("divisionLeader", divisionLeaderUserList.stream().map(UserDO::getId).map(String::valueOf).collect(Collectors.toList()));
        varMap.put("riskControlManager", riskControlManagerIds);
        if (req.getAdjustOpinion() != null) {
            varMap.put(ProcessNodeVariableEnum.START_NODE_MESSAGE.name(), req.getAdjustOpinion());
        }
        startProcessReq.setVariables(varMap);
        startProcessReq.setBusinessKey(String.valueOf(ratingClient.getId()));
        startProcessReq.setStartUserId(String.valueOf(ratingClient.getCreateBy()));
        String processInstanceId = processApiService.start(startProcessReq);
        bizProcessDataService.recordBizData(processInstanceId, ratingClient.getClientId());

        this.update(Wrappers.<RatingClient>lambdaUpdate().eq(RatingClient::getId, req.getId())
                .set(RatingClient::getProcessStatus, ProcessStatus.UNDER_APPROVAL.name()));
    }

    public LambdaQueryWrapper<RatingClient> createCondition(RatingClientPageREQ req) {
        QueryWrapper<RatingClient> wrapper = new QueryWrapper<>();
        Set<Long> userIdAll = new HashSet<>();
        boolean isBizDept = sysUserService.currentUserIsBizDept();
        if (req.getClientId() != null) {
            userIdAll.add(req.getClientId());
        } else {
            if (isBizDept) {
                if (sysUserService.currentUserIsSpecificJob(JobEnum.businesshead.name())) {
                    // 部门负责人可查看部门下所有客户的评级信息
                    List<Long> orgIdList = sysUserService.listOrgByJob(AccountUtil.getLoginInfo().getId(), JobEnum.businesshead.name()).stream().map(OrgDO::getId).collect(Collectors.toList());
                    List<Client> clientList = clientService.list(Wrappers.<Client>lambdaQuery()
                            .in(CollectionUtils.isNotEmpty(orgIdList), Client::getBelongDeptId, orgIdList));
                    if (CollectionUtils.isNotEmpty(clientList)) {
                        List<Long> clientIdList = clientList.stream().map(Client::getId).collect(Collectors.toList());
                        userIdAll.addAll(clientIdList);
                    }
                }
                if (sysUserService.currentUserIsSpecificJob(JobEnum.projmanager.name())) {
                    // 项目经理仅可查看自己作为所属主办、项目主办/协办的客户评级信息
                    List<Client> clientList = clientService.list(Wrappers.<Client>lambdaQuery().eq(Client::getBelongSponsorId, AccountUtil.getLoginInfo().getId()));
                    if (CollectionUtils.isNotEmpty(clientList)) {
                        List<Long> clientIdList = clientList.stream().map(Client::getId).collect(Collectors.toList());
                        userIdAll.addAll(clientIdList);
                    }
                }
                // 业务部门
                List<RatingClient> createClientList = this.list(Wrappers.<RatingClient>lambdaQuery().eq(RatingClient::getCreateBy, AccountUtil.getLoginInfo().getId()));
//                List<Client> createClientList = clientService.list(Wrappers.<Client>lambdaQuery().eq(Client::getCreateBy, AccountUtil.getLoginInfo().getId()));
                if (CollectionUtils.isNotEmpty(createClientList)) {
                    Set<Long> createClientIdList = createClientList.stream().map(RatingClient::getClientId).collect(Collectors.toSet());
                    userIdAll.addAll(createClientIdList);
                }
                if (CollectionUtils.isEmpty(userIdAll)) {
                    // 代表该用户无查看数据的权限
                    userIdAll.add(-1L);
                }
            }
        }

        LocalDate createTimeTo = req.getCreateTimeTo();
        if (createTimeTo != null) {
            createTimeTo = createTimeTo.plusDays(1);
        }


        // 中后台用户 可以看到所有数据
        LambdaQueryWrapper<RatingClient> wrapper1 = wrapper.lambda()
                .in(CollectionUtils.isNotEmpty(userIdAll), RatingClient::getClientId, userIdAll)
                .like(Objects.nonNull(req.getClientCode()), RatingClient::getClientCode, req.getClientCode())
                .like(Objects.nonNull(req.getClientName()), RatingClient::getClientName, req.getClientName())
                .like(Objects.nonNull(req.getModelName()), RatingClient::getModelName, req.getModelName())
                .eq(Objects.nonNull(req.getRatingStatus()), RatingClient::getRatingStatus, req.getRatingStatus())
                .eq(Objects.nonNull(req.getRatingType()), RatingClient::getRatingType, req.getRatingType())
                .ge(Objects.nonNull(req.getCreateTimeFrom()), RatingClient::getCreateTime, req.getCreateTimeFrom())
                .le(Objects.nonNull(createTimeTo), RatingClient::getCreateTime, createTimeTo)
                .orderByDesc(RatingClient::getCreateTime);
        if (ProcessStatus.UN_SUBMIT.name().equals(req.getProcessStatus())) {
            wrapper.lambda().and(i -> i.eq(Objects.nonNull(req.getProcessStatus()), RatingClient::getProcessStatus, req.getProcessStatus())
                    .or().isNull(RatingClient::getProcessStatus));
        } else {
            wrapper.lambda().eq(Objects.nonNull(req.getProcessStatus()), RatingClient::getProcessStatus, req.getProcessStatus());
        }

        return wrapper1;
    }


    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void processEnd(Long id, Integer endType, Long startUserId, String processInstanceId, String modelKey) {
        RatingClient ratingClient = this.getById(id);
        if (ratingClient == null) {
            return;
        }
        boolean processPass = ProcessBusinessStatusEnum.success(endType);
        if (processPass) {
            ratingClient.setEffectTime(LocalDate.now());
            ratingClient.setRatingStatus(true);
            ratingClient.setProcessStatus(ProcessStatus.APPROVAL_PASS.name());
            if ((ratingClient.getOverturn() == null || Objects.equals(ratingClient.getOverturn(), Boolean.FALSE)) && (ratingClient.getAdjust() == null || Objects.equals(ratingClient.getAdjust(), Boolean.FALSE))) {
                ratingClient.setFinalScore(ratingClient.getScore());
            }
            // 将原版本设置为失效
            update(Wrappers.<RatingClient>lambdaUpdate().eq(RatingClient::getClientId, ratingClient.getClientId())
                    .eq(RatingClient::getRatingStatus, true)
                    .set(RatingClient::getRatingStatus, false)
                    .set(RatingClient::getAbandonTime, LocalDate.now()));
            this.updateById(ratingClient);
            versionService.recordVersion(ratingClient.getId(), VersionTypeEnum.APPROVAL, startUserId, processInstanceId, VersionTypeConstants.NORMAL);
        } else {
            if(ProcessBusinessStatusEnum.CANCEL.getType().equals(endType)&&equalsAny(modelKey, RatingClientCreateFlow.name(),RatingClientUpdateFlow.name())){
                ratingClient.setProcessStatus(ProcessStatus.CANCEL.name());
            }else{
                ratingClient.setProcessStatus(ProcessStatus.APPROVAL_REJECT.name());
            }
            this.updateById(ratingClient);
            // 重置试算次数
            ratingSnapshotService.update(Wrappers.<RatingSnapshot>lambdaUpdate()
                    .eq(RatingSnapshot::getId, ratingClient.getSnapshotId())
                    .set(RatingSnapshot::getExecuteCount, 0));
            versionService.recordVersion(id, VersionTypeEnum.APPROVAL, startUserId, processInstanceId, VersionTypeConstants.INVALID);
            versionService.reset(id);
        }


    }


    private void notNullValid(RatingClientInfoRSP rsp) {
        StringBuilder buffer = new StringBuilder();
        if (Objects.isNull(rsp.getCity())) {
            buffer.append("城市,");
        }
        if (Objects.isNull(rsp.getClientCode())) {
            buffer.append("客户编号,");
        }
        if (Objects.isNull(rsp.getDistrict())) {
            buffer.append("区/县,");
        }
        if (Objects.isNull(rsp.getProvince())) {
            buffer.append("省份,");
        }
        if (Objects.isNull(rsp.getBizScope())) {
            buffer.append("业务范围,");
        }
        if (Objects.isNull(rsp.getEstablishDate())) {
            buffer.append("成立日期,");
        }
        if (Objects.isNull(rsp.getIndustryType())) {
            buffer.append("行业分类,");
        }
        if (Objects.isNull(rsp.getRiskControlIndustryClassify())) {
            buffer.append("风控行业分类,");
        }
        if (Objects.isNull(rsp.getRegisterCapital())) {
            buffer.append("注册资本");
        }
        if (buffer.length() > 0) {
            throw new MithrasException("客户基本信息不完整，请在客户管理模块维护客户基本信息后再发起评级！" + buffer + " 信息为空");
        }
    }

    /**
     * 获取评级创建人为userList，生效时间 < 流程发起时间 ，并生效的评级
     *
     * @param day       要求生效日期在day天内
     * @param clientId  客户id
     * @param userList  评级创建人
     * @param startTime 流程发起时间
     * @return 评估主体评分
     */
    public RatingClientProjDetailRSP getEffectRating(Integer day, Long clientId, List<Long> userList, Date startTime) {
        if (Objects.isNull(clientId)) {
            return null;
        }
        LocalDate endDate = null;
        if (day != null) {
            endDate = LocalDate.now().minusDays(day);
        }
        RatingClientLib ratingClientLib = libService.getOne(Wrappers.<RatingClientLib>lambdaQuery()
                .eq(RatingClientLib::getClientId, clientId)
                .ge(endDate != null, RatingClientLib::getCreateTime, endDate)
                .in(CollectionUtils.isNotEmpty(userList), RatingClientLib::getDataCreateBy, userList)
                .eq(RatingClientLib::getRatingStatus, true)
                .le(Objects.nonNull(startTime), RatingClientLib::getCreateTime, startTime)
                .orderByDesc(RatingClientLib::getCreateTime)
                .last("limit 1"));
        RatingClientProjDetailRSP rsp = new RatingClientProjDetailRSP();
        if (ratingClientLib != null) {
            rsp.setId(ratingClientLib.getOriginId());
            rsp.setRatingFinalScore(ratingClientLib.getFinalScore());
        }
        return rsp;
    }

    public List<ClientUnifiedRatingHistoryRSP> ratingHistory(Long clientId) {
        List<RatingClientLib> ratingClientList = libService.list(Wrappers.<RatingClientLib>lambdaQuery()
                .eq(RatingClientLib::getClientId, clientId)
                .eq(RatingClientLib::getRatingStatus, true)
                .eq(RatingClientLib::getVersionType, YesOrNoNumberEnum.YES.getCode())
                .orderByDesc(RatingClientLib::getEffectTime));
        if (ObjectUtil.isEmpty(ratingClientList)) {
            return null;
        }
        return BeanUtil.copyToList(ratingClientList, ClientUnifiedRatingHistoryRSP.class);
    }

    public List<RatingModelQueryRSP> modelQuery(RatingModelQueryREQ req) {
        List<DecisionModelRSP> decisionModelRSPS = decisionService.modelQuery(new DecisionModelREQ());
        RatingBizTypeEnum ratingBizTypeEnum = RatingBizTypeEnum.find(req.getBizType());

        List<RatingModelQueryRSP> ratingModelQueryRSPS = null;
        if (ratingBizTypeEnum != null) {
            String extra = ratingBizTypeEnum.getExtra();
            ratingModelQueryRSPS = decisionModelRSPS.stream().filter(f -> {
                String[] s = f.getCode().split("_");
                return extra.equals(s[0]);
            }).map(m -> BeanUtil.copyProperties(m, RatingModelQueryRSP.class)).collect(Collectors.toList());
        } else {
            ratingModelQueryRSPS = BeanUtil.copyToList(decisionModelRSPS, RatingModelQueryRSP.class);
        }

        /*新增一种自定义模型  航运模型*/
        RatingModelQueryRSP hymxMode = new RatingModelQueryRSP();
        hymxMode.setCode(Constant.hymxCode);
        hymxMode.setName(Constant.hymxName);
        ratingModelQueryRSPS.add(hymxMode);
        return ratingModelQueryRSPS;
    }

    private Map<String, List<RatingParamFieldApprovalRSP>> getParamInfoHymx(RatingParamInfoREQ req, RatingClient ratingClient) {
        /*本地测试，忽略取到的问卷，使用自定义的问卷*/
        List<RatingParamFieldApprovalRSP> params = JSON.parseArray(Constant.hymxParamInfo, RatingParamFieldApprovalRSP.class);
        Map<String, List<RatingParamFieldApprovalRSP>> paramMap = new HashMap<>();
        paramMap.put("定性指标", params);

        return paramMap;
    }

    public RatingParamInfoDuoApprovalRSP paramInfo(RatingParamInfoREQ req) {
        RatingClient ratingClient = this.getById(req.getId());
        if (ratingClient == null) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        // 先查询快照
        RatingReport ratingReport = null;
        if (Arrays.asList(ProcessStatus.UNDER_APPROVAL.name(), ProcessStatus.APPROVAL_REJECT.name()).contains(ratingClient.getProcessStatus())) {
            ratingReport = ratingReportMapper.selectById(ratingClient.getReportId());
        }
        RatingParamInfoDuoApprovalRSP result = ratingSnapshotService.getSnapshotInfoByGroupNameDuoApproval(ratingClient.getSnapshotId(), ratingReport);
        if (result != null) {
            Map<String, RatingParamRSP> snapshotResult = ratingSnapshotService.getSnapshotResult(ratingClient.getSnapshotId());
            result.setRatingParam(snapshotResult.values());
        } else {
            // 不存在快照 -> 请求决策流引擎，并保存快照
            result = new RatingParamInfoDuoApprovalRSP();
            Map<String, List<RatingParamFieldApprovalRSP>> paramMap = null;
            /*模拟航运模型 单独获取，但是快照还是需要存储*/
            if (Constant.hymxCode.equals(ratingClient.getModelCode())) {
                paramMap = getParamInfoHymx(req, ratingClient);
            } else {
                paramMap = decisionService.paramInfo(ratingClient.getModelCode());
            }
            if (CollectionUtils.isNotEmpty(paramMap)) {
                Map<String, Map<String, List<RatingParamFieldApprovalRSP>>> info = paramMap.values().stream().flatMap(Collection::stream)
                        .filter(f -> f.getGroupName() != null && f.getGroupName().contains("-"))
                        .collect(Collectors.groupingBy(
                                obj -> obj.getGroupName().split("-")[0], // 根据'-'前的部分分组
                                Collectors.groupingBy(obj -> obj.getGroupName().split("-")[1]) // 在每个子组里根据'-'后的部分再分组
                        ));
                String content = JSON.toJSONString(paramMap.values().stream().flatMap(Collection::stream).collect(Collectors.toList()));
                RatingSnapshot ratingSnapshot = new RatingSnapshot();
                ratingSnapshot.setContent(content);
                ratingSnapshot.setServiceCode(ratingClient.getModelCode());
                ratingSnapshotService.save(ratingSnapshot);

                result.setExecuteCount(0);
                result.setInfo(info);

                // 关联评级与模型快照
                this.update(Wrappers.<RatingClient>lambdaUpdate().eq(RatingClient::getId, req.getId())
                        .set(RatingClient::getSnapshotId, ratingSnapshot.getId()));
            }
        }

        /*航运模型没有定量部分  无须从系统取数*/
        if (Constant.hymxCode.equals(ratingClient.getModelCode())) {
            return result;
        }
        List<RatingParamFieldApprovalRSP> systemIndex = getSystemIndex(ratingClient);
        Map<String, Map<String, List<RatingParamFieldApprovalRSP>>> info = result.getInfo();
        if (info.get("定量指标") != null) {
            info.get("定量指标").put("系统取数指标", systemIndex);
        } else {
            Map<String, List<RatingParamFieldApprovalRSP>> map = new HashMap<>();
            map.put("系统取数指标", systemIndex);
            info.put("定量指标", map);
        }
        return result;

    }


    /**
     * 拿到系统取数的指标
     *
     * @param ratingClient
     * @return
     */
    public List<RatingParamFieldApprovalRSP> getSystemIndex(RatingClient ratingClient) {
        Map<String, List<RatingParamFieldApprovalRSP>> paramMap = decisionService.paramInfo(ratingClient.getModelCode());
        Map<String, Object> request = paramMap.values().stream().flatMap(Collection::stream)
                .collect(Collectors.toMap(RatingParamFieldApprovalRSP::getFieldName, param ->
                                getDataTypeDefault(RatingDataTypeEnum.findByExtra(param.getDataType()), param.getEnumList())
                        , (m1, m2) -> m1));
        request.put("client_id", ratingClient.getClientId());
        Long areaUniCode = getClientAreaUniCode(ratingClient.getClientId(), ratingClient.getModelName());
        request.put("area_uni_code", areaUniCode);
        if (StrUtil.equalsAny(ratingClient.getModelName(), "政信主体地级市模型", "政信主体区县级模型")) {
            this.fillAreaParam(request, ratingClient, areaUniCode);
        }
        DecisionExecuteResult execute = decisionService.execute(ratingClient.getModelCode(), request);

        // 补充系统取数的定量指标
        List<DecisionExecuteResult.Var> varList = execute.getVarList();
        List<RatingParamFieldApprovalRSP> sysIndexList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(varList)) {
            for (DecisionExecuteResult.Var var : varList) {
                if (Arrays.asList("政信主体地级市模型", "政信主体区县级模型").contains(ratingClient.getModelName()) && Objects.equals(var.getName(), "区域得分")) {
                    continue;
                }
                RatingParamFieldApprovalRSP sysIndex = new RatingParamFieldApprovalRSP();
                sysIndex.setFieldName(var.getCode());
                sysIndex.setFieldComment(var.getName());
                sysIndex.setFieldValue("-9999999999999".equals(var.getValue()) ? null : var.getValue());
                sysIndex.setFetchMethod(RatingFetchMethodEnum.SYSTEM.name());
                sysIndex.setDataType(RatingDataTypeEnum.NUMBER.name());
                sysIndexList.add(sysIndex);
            }
        }

        // 政信类模型的特殊处理
        if (Arrays.asList("政信主体地级市模型", "政信主体区县级模型").contains(ratingClient.getModelName())) {
            List<RatingParamFieldApprovalRSP> areaModelInfo = areaModelInfo(ratingClient.getId(), ratingClient.getModelName());
            if (CollectionUtils.isNotEmpty(areaModelInfo)) {
                // 补充政信模型系统取数的指标
                sysIndexList.addAll(areaModelInfo);
            }
        }
        return sysIndexList;


    }


    public Object getDataTypeDefault(RatingDataTypeEnum ratingDataTypeEnum, List<RatingParamFieldApprovalRSP.DictionaryDTO> enumList) {
        if (ratingDataTypeEnum != null) {
            switch (ratingDataTypeEnum) {
                case DATE:
                case TIME:
                    return new Date();
                case NUMBER:
                    return 0;
                case STRING:
                    return "0";
                case COLLECTION:
                    return Collections.singletonList("0");
                case ENUM:
                    return enumList.get(0).getValue();
                case ENUM_COLLECTION:
                    return Collections.singletonList(enumList.get(0).getValue());
            }
        }
        return "0";
    }

    /*航运模型试算  需要预先生成快照*/
    private RatingExecuteRSP executeHymx(RatingExecuteREQ req, RatingClient ratingClient) {
        RatingSnapshot ratingSnapshot = ratingSnapshotService.getById(ratingClient.getSnapshotId());
        if (ratingSnapshot == null) {
            throw new MithrasException("模型数据不存在");
        }
        if (!req.isFinishedCall() && ratingSnapshot.getExecuteCount() >= 3) {
            throw new MithrasException("已达试算次数上限");
        }
        /*从前端传入参数中直接计算模型得分  1、计算总得分  2、从得分获取评级  3、从调整项绝对是否需要提升等级*/
        List<RatingParamREQ> paramList = req.getParam();
        DecisionExecuteResult execute = getDecisionExecuteResult(paramList);
        // 评级结果留存
        ratingSnapshotService.update(Wrappers.<RatingSnapshot>lambdaUpdate()
                .eq(RatingSnapshot::getId, ratingClient.getSnapshotId())
                .set(RatingSnapshot::getScore, JSON.toJSONString(execute))
                .set(RatingSnapshot::getExecuteCount, req.isFinishedCall() ? ratingSnapshot.getExecuteCount() : ratingSnapshot.getExecuteCount() + 1));
        RatingExecuteRSP rsp = new RatingExecuteRSP();
        // 评级结果入库
        ratingClient.setScore(Optional.ofNullable(execute.getScore()).orElse(execute.getFirstScore()));
        ratingClient.setPd(execute.getDefaultRate());
        ratingClient.setModelScore(execute.getModelScore());
        this.updateById(ratingClient);

        BeanUtil.copyProperties(execute, rsp);
        rsp.setExecuteCount(ratingSnapshot.getExecuteCount() + 1);
        return rsp;
    }


    /*模拟模型计算  生成计算结果*/
    private static DecisionExecuteResult getDecisionExecuteResult(List<RatingParamREQ> params) {
        DecisionExecuteResult execute = new DecisionExecuteResult();

        String levelName = "";
        Integer levelAdd = 0;
        Integer level = 0;
        Integer modelScore = 0;//客户评级得分
        Integer subjectVesselScore = 0;//标的物得分
        Integer custScore = 0;//客户综合得分

        for (RatingParamREQ param : params) {
            /*如果是行内客户  需要提升两个等级  */
//            log.info("name is : {}, value is {}",param.getFieldName() , param.getFieldValue());
            if (Constant.hymxBankClientFlag.equals(param.getFieldName())) {
                if ("Y".equals(param.getFieldValue())) { // 是本行客户  提升两个等级
                    levelAdd = Constant.hymxUpLevel;
                }
            } else if (subjectVesselSet.contains(param.getFieldName())) {
                subjectVesselScore = subjectVesselScore + hymxScoreMap.get(param.getFieldName() + "_" + param.getFieldValue());
            } else {
                custScore = custScore + hymxScoreMap.get(param.getFieldName() + "_" + param.getFieldValue());
            }
        }
        modelScore = subjectVesselScore + custScore;

        /*获取评定的航运等级*/
        Integer[] levelArr = new Integer[]{0, 7, 13, 19, 25, 31, 38, 45, 52, 58, 65, 72, 79, 85, 100};
        for (int i = 0; i < levelArr.length - 1; i++) { //正常分值
            if (modelScore >= levelArr[i] && modelScore < levelArr[i + 1]) {
                level = i + 1;
            }
        }
        execute.setFirstScore(getRatingDisplay(level));

        /*航运等级评级调整  限制最高等级 14 ：AAA*/
        level = level + levelAdd;
        if (level > 14) {
            level = 14;
        }

        execute.setScore(getRatingDisplay(level));
        execute.setModelScore(String.valueOf(modelScore));
        execute.setQualitativeScore(String.valueOf(modelScore));
        execute.setCustScore(String.valueOf(custScore));
        execute.setSubjectVesselScore(String.valueOf(subjectVesselScore));
        return execute;
    }

    private static String getRatingDisplay(Integer level) {
        String display = "";
        RatingLevelEnum[] enums = RatingLevelEnum.values();
        for (RatingLevelEnum levelEnum : enums) {
            if (levelEnum.score == level) {
                display = levelEnum.display();
                break;
            }
        }
        return display;
    }

    public RatingExecuteRSP execute(RatingExecuteREQ req) {
        RatingClient ratingClient = getById(req.getId());
        if (ratingClient == null) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        /*航运模型单独试算*/
        if (Constant.hymxCode.equals(ratingClient.getModelCode())) {
            return executeHymx(req, ratingClient);
        }
        RatingSnapshot ratingSnapshot = ratingSnapshotService.getById(ratingClient.getSnapshotId());
        if (ratingSnapshot == null) {
            throw new MithrasException("模型数据不存在");
        }
        if (!req.isFinishedCall() && ratingSnapshot.getExecuteCount() >= 3) {
            throw new MithrasException("已达试算次数上限");
        }
        List<RatingParamFieldRSP> fieldRSPList = JSON.parseArray(ratingSnapshot.getContent(), RatingParamFieldRSP.class);
        List<String> fieldNumberList = fieldRSPList.stream().filter(f -> Objects.equals(f.getDataType(), RatingDataTypeEnum.NUMBER.getExtra())).map(RatingParamFieldRSP::getFieldName).collect(Collectors.toList());
        List<String> fieldNumberRateList = fieldRSPList.stream().filter(f -> Objects.equals(f.getDataType(), RatingDataTypeEnum.NUMBER.getExtra()) && Objects.equals(f.getUnit(), "%")).map(RatingParamFieldRSP::getFieldName).collect(Collectors.toList());
        List<RatingParamREQ> paramList = req.getParam();
        Map<String, Object> param = paramList.stream().filter(f -> Objects.nonNull(f.getFieldValue()) && fieldRSPList.stream().map(RatingParamFieldRSP::getFieldName).collect(Collectors.toList()).contains(f.getFieldName()))
                .peek(paramREQ -> {
                    if (CollectionUtils.isNotEmpty(fieldNumberRateList) && fieldNumberRateList.contains(paramREQ.getFieldName())) {
                        BigDecimal bigDecimal = new BigDecimal(paramREQ.getFieldValue().toString()).divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP);
                        paramREQ.setFieldValue(bigDecimal.toString());
                    } else if (CollectionUtils.isNotEmpty(fieldNumberList) && fieldNumberList.contains(paramREQ.getFieldName())) {
                        BigDecimal bigDecimal = new BigDecimal(paramREQ.getFieldValue().toString()).multiply(new BigDecimal("10000"));
                        paramREQ.setFieldValue(bigDecimal.toString());
                    }
                }).collect(Collectors.toMap(RatingParamREQ::getFieldName, RatingParamREQ::getFieldValue, (m1, m2) -> m1));
        param.put("client_id", ratingClient.getClientId());
        Long areaUniCode = getClientAreaUniCode(ratingClient.getClientId(), ratingClient.getModelName());
        param.put("area_uni_code", areaUniCode);
        if (StrUtil.equalsAny(ratingClient.getModelName(), "政信主体地级市模型", "政信主体区县级模型")) {
            this.fillAreaParam(param, ratingClient, areaUniCode);
        }
        DecisionExecuteResult execute = decisionService.execute(ratingClient.getModelCode(), param);
        if (execute == null) {
            throw new MithrasException("试算不成功");
        }
        // 评级结果留存
        ratingSnapshotService.update(Wrappers.<RatingSnapshot>lambdaUpdate()
                .eq(RatingSnapshot::getId, ratingClient.getSnapshotId())
                .set(RatingSnapshot::getScore, JSON.toJSONString(execute))
                .set(RatingSnapshot::getExecuteCount, req.isFinishedCall() ? ratingSnapshot.getExecuteCount() : ratingSnapshot.getExecuteCount() + 1));
        RatingExecuteRSP rsp = new RatingExecuteRSP();
        // 评级结果入库
        ratingClient.setScore(Optional.ofNullable(execute.getScore()).orElse(execute.getFirstScore()));
        ratingClient.setPd(execute.getDefaultRate());
        ratingClient.setModelScore(execute.getModelScore());
        this.updateById(ratingClient);

        BeanUtil.copyProperties(execute, rsp);
        rsp.setExecuteCount(ratingSnapshot.getExecuteCount() + 1);
        return rsp;
    }

    private void fillAreaParam(Map<String, Object> param, RatingClient ratingClient, Long areaUniCode) {
        List<RatingClientAreaIndicator> areaIndicatorList = null;
        String prefix = null;
        String serviceCode = null;
        if (Objects.equals(ratingClient.getModelName(), "政信主体地级市模型")) {
            serviceCode = "zl_zxztdjs_qydf_fw";
            prefix = "zl_zxztdjs_qydf_srcs_";
            areaIndicatorList = ratingClientAreaIndicatorService.listByRatingClientAndCategory(ratingClient.getId(), RatingClientAreaIndicatorConfig.CategoryCodeEnum.zxztdjs_dlzb.name());
        }
        if (Objects.equals(ratingClient.getModelName(), "政信主体区县级模型")) {
            serviceCode = "zl_zxztqxj_qydf_fw";
            prefix = "zl_zxztqxj_qydf_srcs_";
            areaIndicatorList = ratingClientAreaIndicatorService.listByRatingClientAndCategory(ratingClient.getId(), RatingClientAreaIndicatorConfig.CategoryCodeEnum.zxztqxj_dlzb.name());
        }
        if (CollectionUtil.isEmpty(areaIndicatorList) || StrUtil.isBlank(serviceCode) || StrUtil.isBlank(prefix)) {
            throw new MithrasException("尝试计算区域得分失败-缺失请求参数");
        }
        Map<String, RatingClientAreaIndicator> areaIndicatorMap = areaIndicatorList.stream().collect(Collectors.toMap(RatingClientAreaIndicator::getIndicatorCode, e -> e));
        Map<String, Object> areaFinalScoreParam = new HashMap<>();
        areaFinalScoreParam.put("area_uni_code", areaUniCode);
        for (Map.Entry<String, RatingClientAreaIndicator> entry : areaIndicatorMap.entrySet()) {
            areaFinalScoreParam.put(prefix + entry.getKey(), entry.getValue().getIndicatorValue());
        }
        // 调用区域评级模型获取区域最终得分
        DecisionREQ decisionREQ = new DecisionREQ();
        decisionREQ.setServiceCode(serviceCode);
        decisionREQ.setParam(areaFinalScoreParam);
        decisionREQ.setEnableDetail(true);
        R<DecisionRSP> rsp = decisionApiClient.execute(decisionREQ);
        if (!rsp.isSuccess()) {
            throw new MithrasException("尝试计算区域得分失败-引擎返回失败");
        }
        JSONObject jsonObject = JSONUtil.parseObj(rsp.getData().getOutputMap().get("rr_final_score"));
        String finalScore = jsonObject.getStr("finalScore");
        if (StrUtil.isBlank(finalScore)) {
            throw new MithrasException("尝试计算区域得分失败-引擎没有返回区域得分");
        }
        if (Objects.equals(ratingClient.getModelName(), "政信主体地级市模型")) {
            param.put("zl_zxztdjs_ztpj_srcs_area_final_score", finalScore);
        }
        if (Objects.equals(ratingClient.getModelName(), "政信主体区县级模型")) {
            param.put("zl_zxztqxj_ztpj_srcs_area_final_score", finalScore);
        }
    }

    @Transactional(rollbackFor = Throwable.class)
    public void clientFinish(RatingClientFinishREQ req) {
        RatingClient ratingClient = this.getById(req.getId());
        if (ratingClient == null) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        if (ProcessStatus.APPROVAL_PASS.name().equals(ratingClient.getProcessStatus())) {
            throw new MithrasException("不能修改已完成的数据");
        }
        RatingSnapshotDetailRSP snapshotDetail = ratingSnapshotService.getSnapshotDetail(ratingClient.getSnapshotId());

        // 用户答题记录表单
//        if(needUpdateParam(req.getId())) { // 若状态为流程中的更新需要保留原来的快照
        LambdaUpdateWrapper<RatingSnapshot> wrapper = Wrappers.<RatingSnapshot>lambdaUpdate()
                .eq(RatingSnapshot::getId, ratingClient.getSnapshotId())
                .set(RatingSnapshot::getResult, JSON.toJSONString(req.getParam()));
        if (ProcessStatus.UN_SUBMIT.name().equals(ratingClient.getProcessStatus())) {
            wrapper.set(RatingSnapshot::getLastResult, JSON.toJSONString(req.getParam()));
        }
        ratingSnapshotService.update(wrapper);
//        }

        if (req.isOperationType()) {
            // 完成评级 -> 生成评级报告
            Map<String, List<RatingParamFieldRSP>> snapshotInfo = snapshotDetail.getContentRsp();
            DecisionExecuteResult scoreRsp = snapshotDetail.getScoreRsp();
            if (scoreRsp == null || snapshotDetail.getExecuteCount() == 0) {
                throw new MithrasException("未试算不能完成评级");
            }
            Map<String, RatingParamRSP> resultMap = req.getParam().stream().collect(Collectors.toMap(RatingParamRSP::getFieldName, Function.identity(), (m1, m2) -> m2));

            List<RatingQualitativeRSP> qualitativeList = new ArrayList<>();
            List<RatingQuantitativeRSP> quantitativeList = new ArrayList<>();
            List<RatingQualitativeRSP> adjustEventList = new ArrayList<>();
            AtomicInteger index = new AtomicInteger();
//            Map<String, Object> varMap = scoreRsp.getVarList().stream().collect(Collectors.toMap(DecisionExecuteResult.Var::getCode, DecisionExecuteResult.Var::getValue));
//            Map<String, Object> paramMap = scoreRsp.getParamList().stream().collect(Collectors.toMap(DecisionExecuteResult.Param::getCode, DecisionExecuteResult.Param::getValue));
            for (Map.Entry<String, List<RatingParamFieldRSP>> entry : snapshotInfo.entrySet()) {
                if (Arrays.asList(RatingDataTypeEnum.ENUM.getExtra(), RatingDataTypeEnum.ENUM_COLLECTION.getExtra()).contains(entry.getKey())) {
                    // 定性指标
                    for (RatingParamFieldRSP rsp : entry.getValue()) {
                        RatingQualitativeRSP qualitativeRSP = new RatingQualitativeRSP();
                        // 拿到答题结果
                        RatingParamRSP ratingParamRSP = resultMap.get(rsp.getFieldName());
                        qualitativeRSP.setFieldCode("");
                        qualitativeRSP.setFieldName(rsp.getFieldName());
                        qualitativeRSP.setDataType(rsp.getDataType());
                        qualitativeRSP.setEnumList(rsp.getEnumList());
                        qualitativeRSP.setValue(Optional.ofNullable(ratingParamRSP).map(RatingParamRSP::getFieldValue).orElse(""));
                        /*结果集 包含二级选项 需要将二级选项记录下来*/
                        if (resultMap.containsKey(rsp.getFieldName() + "_" + qualitativeRSP.getValue())) {
                            qualitativeRSP.setSecondValue(resultMap.get(rsp.getFieldName() + "_" + qualitativeRSP.getValue()).getFieldValue());
                        }
                        qualitativeRSP.setFieldComment(rsp.getFieldComment());
//                        qualitativeRSP.setFieldScore(Optional.ofNullable(paramMap.get(rsp.getFieldName())).map(String::valueOf).orElse(null));
                        if (rsp.getGroupName().contains("评级调整事项-")) {
                            adjustEventList.add(qualitativeRSP);
                        } else {
                            qualitativeList.add(qualitativeRSP);
                        }
                    }
                } else {
                    // 定量指标
                    for (RatingParamFieldRSP rsp : entry.getValue()) {
                        RatingQuantitativeRSP quantitativeRSP = new RatingQuantitativeRSP();
                        // 拿到答题结果
                        RatingParamRSP ratingParamRSP = resultMap.get(rsp.getFieldName());
                        quantitativeRSP.setIndex(index.incrementAndGet());
                        quantitativeRSP.setFieldName(rsp.getFieldName());
                        quantitativeRSP.setFieldComment(rsp.getFieldComment());
                        quantitativeRSP.setDataType(rsp.getDataType());
                        quantitativeRSP.setFetchMethod(ratingParamRSP != null ? RatingFetchMethodEnum.IMPORT.name() : RatingFetchMethodEnum.SYSTEM.name());
                        quantitativeRSP.setUnit(rsp.getUnit());
                        quantitativeRSP.setFormal(rsp.getFormal());
                        quantitativeRSP.setValue(Optional.ofNullable(ratingParamRSP).map(RatingParamRSP::getFieldValue).orElse(""));
                        quantitativeRSP.setDate(Optional.ofNullable(ratingParamRSP).map(RatingParamRSP::getDate).orElse(null));
//                        quantitativeRSP.setFieldScore(Optional.ofNullable(paramMap.get(rsp.getFieldName())).map(String::valueOf).orElse(null));
                        if (rsp.getGroupName().contains("评级调整事项-")) {
                            adjustEventList.add(BeanUtil.copyProperties(quantitativeRSP, RatingQualitativeRSP.class));
                        } else {
                            quantitativeList.add(quantitativeRSP);
                        }

                    }
                }
            }
            /*针对航运模型添加排除逻辑  航运模型没有定量指标*/
            if (!Constant.hymxCode.equals(ratingClient.getModelCode())) {
                // 补充系统取数的定量指标
                List<DecisionExecuteResult.Var> varList = scoreRsp.getVarList();
//            Map<String, Object> paramGroupCodeAndNameMap = scoreRsp.getParamList().stream().collect(Collectors.toMap(m -> m.getGroup() + "-" + m.getName(), DecisionExecuteResult.Param::getValue, (m1, m2) -> m1));
                for (DecisionExecuteResult.Var var : varList) {
                    RatingQuantitativeRSP sysIndex = new RatingQuantitativeRSP();
                    if (Arrays.asList("政信主体地级市模型", "政信主体区县级模型").contains(ratingClient.getModelName()) && Objects.equals(var.getName(), "区域得分")) {
                        scoreRsp.setAreaScore(Optional.ofNullable(var.getValue()).map(String::valueOf).orElse(null));
                        continue;
                    }
                    sysIndex.setFieldName(var.getCode());
                    sysIndex.setFieldComment(var.getName());
                    sysIndex.setValue("-9999999999999".equals(var.getValue()) ? null : var.getValue());
//                sysIndex.setFieldScore(Optional.ofNullable(paramGroupCodeAndNameMap.get(var.getGroup() + "-" + var.getName())).map(String::valueOf).orElse(null));
                    sysIndex.setFetchMethod(RatingFetchMethodEnum.SYSTEM.name());
                    sysIndex.setDataType(RatingDataTypeEnum.NUMBER.name());
                    quantitativeList.add(sysIndex);
                }

                // 政信类模型的特殊处理
                if (Arrays.asList("政信主体地级市模型", "政信主体区县级模型").contains(ratingClient.getModelName())) {
                    List<RatingParamFieldApprovalRSP> areaModelInfo = areaModelInfo(ratingClient.getId(), ratingClient.getModelName());
                    if (CollectionUtils.isNotEmpty(areaModelInfo)) {
                        // 补充政信模型系统取数的指标
                        List<RatingQuantitativeRSP> collect = areaModelInfo.stream().map(item -> {
                            RatingQuantitativeRSP sysIndex = new RatingQuantitativeRSP();
                            sysIndex.setFieldName(item.getFieldName());
                            sysIndex.setFieldComment(item.getFieldComment());
                            sysIndex.setValue(item.getFieldValue());
                            sysIndex.setFetchMethod(item.getFetchMethod());
                            sysIndex.setDataType(item.getDataType());
                            sysIndex.setIsAreaModelIndex(Boolean.TRUE);
                            sysIndex.setUnit(item.getUnit());
                            return sysIndex;
                        }).collect(Collectors.toList());
                        quantitativeList.addAll(collect);
                    }
                    ratingSnapshotService.update(Wrappers.<RatingSnapshot>lambdaUpdate()
                            .eq(RatingSnapshot::getId, ratingClient.getSnapshotId())
                            .set(RatingSnapshot::getScore, JSON.toJSONString(scoreRsp)));
                }
            }

            RatingReport ratingReport = new RatingReport();
            ratingReport.setQualitative(JSON.toJSONString(qualitativeList));
            ratingReport.setQuantitative(JSON.toJSONString(quantitativeList));
            ratingReport.setAdjustEvent(JSON.toJSONString(adjustEventList));
            ratingReport.setServiceCode(ratingClient.getModelCode());
            if (ratingClient.getReportId() == null) {
                ratingReportMapper.insert(ratingReport);
            } else {
                ratingReport.setId(ratingClient.getReportId());
                ratingReportMapper.updateById(ratingReport);
            }

            // 关联评级主表与评级报告
            this.update(Wrappers.<RatingClient>lambdaUpdate().eq(RatingClient::getId, req.getId())
                    .set(RatingClient::getReportId, ratingReport.getId()));
        } else {
            if (ProcessStatus.UN_SUBMIT.name().equals(ratingClient.getProcessStatus())) {
                return;
            }
            Long belongSponsorUserId = ratingClient.getBelongSponsorUserId();
            if (belongSponsorUserId == null) {
                throw new MithrasException("主办用户不存在，保存草稿失败");
            }
            int count = this.count(Wrappers.<RatingClient>lambdaQuery().eq(RatingClient::getClientId, ratingClient.getClientId())
                    .eq(RatingClient::getRatingStatus, true));

            String clientName = id2NameService.clientId2NameSingle(ratingClient.getClientId());
            CommonProcessPrepare prepare = CommonProcessPrepare.builder()
                    .processType(count > 0 ? ProcessModelTypeEnum.RatingClientUpdateFlow.name() : ProcessModelTypeEnum.RatingClientCreateFlow.name())
                    .businessId(String.valueOf(ratingClient.getId()))
                    .formName(clientName)
                    .projName(null)
                    .clientName(clientName)
                    .currentAssignee(JSON.toJSONString(Collections.singletonList(ratingClient.getBelongSponsorUserId())))
                    .currentNode("项目经理确认")
                    .applyTime(LocalDateTime.now())
                    .status(CommonProcessPrepareStatus.PEND_COMMIT.name())
                    .build();
            commonProcessPrepareMapper.insert(prepare);

            update(Wrappers.<RatingClient>lambdaUpdate().eq(RatingClient::getId, ratingClient.getId())
                    .set(RatingClient::getProcessStatus, ProcessStatus.UN_SUBMIT.name()));
        }

    }

    public RatingClientReportRSP clientReport(RatingClientReportREQ req) {
        RatingClientAbstractRSP scoreRSP = new RatingClientAbstractRSP();
        RatingClient ratingClient = this.getById(req.getId());
        if (ratingClient == null) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        RatingClientReportRSP rsp = new RatingClientReportRSP();
        // 指标信息
        RatingReport ratingReport = ratingReportMapper.selectById(ratingClient.getReportId());
        if (ratingReport != null) {
            List<RatingQualitativeRSP> qualitativeList = JSON.parseArray(ratingReport.getQualitative(), RatingQualitativeRSP.class);
            List<RatingQuantitativeRSP> quantitativeList = JSON.parseArray(ratingReport.getQuantitative(), RatingQuantitativeRSP.class);
            List<RatingQualitativeRSP> adjustEventIndex = JSON.parseArray(ratingReport.getAdjustEvent(), RatingQualitativeRSP.class);
            // 流程为退回后重新发起：补充比较字段
            if (ProcessStatus.UNDER_APPROVAL.name().equals(ratingClient.getProcessStatus())) {
                ratingSnapshotService.compareReport(ratingClient.getSnapshotId(), qualitativeList, quantitativeList, adjustEventIndex);
            }

            /*航运模型将定性指标拆分  如果有二级选项  需要将二级选项拼到一级的后面*/
            if (Constant.hymxCode.equals(ratingClient.getModelCode())) {
                List<RatingQualitativeRSP> subjectVesselScoreList = new ArrayList<>();
                List<RatingQualitativeRSP> custScoreList = new ArrayList<>();
                for (RatingQualitativeRSP rsp1 : qualitativeList) {
                    /*如果包含二级选项，需要将二级选项的分挂到枚举上面*/
                    if (rsp1.getSecondValue() != null) {
                        Integer secondScore = hymxScoreMap.get(rsp1.getFieldName() + "_" + rsp1.getValue() + "_" + rsp1.getSecondValue());
                        List<RatingParamFieldRSP.DictionaryDTO> dicts = rsp1.getEnumList();
                        for (RatingParamFieldRSP.DictionaryDTO dict : dicts) {
                            if (dict.getValue().equals(rsp1.getValue())) { // 二级选项匹配
                                dict.setLabel(dict.getLabel() + "（" + secondScore + "分）");
                                break;
                            }
                        }
                    }
                    if (subjectVesselSet.contains(rsp1.getFieldName())) {
                        subjectVesselScoreList.add(rsp1);
                    } else {
                        custScoreList.add(rsp1);
                    }
                }
                rsp.setSubjectVesselScoreList(subjectVesselScoreList);
                rsp.setCustList(custScoreList);
            } else {
                // 补充各个指标的审批意见
                if (ratingReport.getApprovalInfo() != null) {
                    List<RatingReportApprovalRSP.RatingApprovalRSP> approvalInfoList = JSON.parseArray(ratingReport.getApprovalInfo(), RatingReportApprovalRSP.RatingApprovalRSP.class);
                    Map<String, RatingReportApprovalRSP.RatingApprovalRSP> approvalInfoMap = approvalInfoList.stream().collect(Collectors.toMap(RatingReportApprovalRSP.RatingApprovalRSP::getFieldName, Function.identity(), (m1, m2) -> m2));
                    if (CollectionUtils.isNotEmpty(qualitativeList)) {
                        for (RatingQualitativeRSP qualitative : qualitativeList) {
                            RatingReportApprovalRSP.RatingApprovalRSP approvalInfo = approvalInfoMap.getOrDefault(qualitative.getFieldName(), new RatingReportApprovalRSP.RatingApprovalRSP());
                            qualitative.setApprovalStatus(approvalInfo.getApprovalStatus());
                            qualitative.setApprovalOpinion(approvalInfo.getApprovalOpinion());
                        }
                    }
                    if (CollectionUtils.isNotEmpty(quantitativeList)) {
                        for (RatingQuantitativeRSP quantitative : quantitativeList) {
                            RatingReportApprovalRSP.RatingApprovalRSP approvalInfo = approvalInfoMap.getOrDefault(quantitative.getFieldName(), new RatingReportApprovalRSP.RatingApprovalRSP());
                            quantitative.setApprovalStatus(approvalInfo.getApprovalStatus());
                            quantitative.setApprovalOpinion(approvalInfo.getApprovalOpinion());
                        }
                    }
                    if (CollectionUtils.isNotEmpty(adjustEventIndex)) {
                        for (RatingQualitativeRSP adjust : adjustEventIndex) {
                            RatingReportApprovalRSP.RatingApprovalRSP approvalInfo = approvalInfoMap.getOrDefault(adjust.getFieldName(), new RatingReportApprovalRSP.RatingApprovalRSP());
                            adjust.setApprovalStatus(approvalInfo.getApprovalStatus());
                            adjust.setApprovalOpinion(approvalInfo.getApprovalOpinion());
                        }
                    }
                }
            }
            rsp.setQualitativeList(qualitativeList);
            rsp.setQuantitativeList(quantitativeList);
            rsp.setAdjustEventList(adjustEventIndex);
        }

        // 成绩
        RatingSnapshot ratingSnapshot = ratingSnapshotService.getById(ratingClient.getSnapshotId());
        if (ratingSnapshot != null) {
            DecisionExecuteResult executeResult = JSON.parseObject(ratingSnapshot.getScore(), DecisionExecuteResult.class);
            if (executeResult != null) {
                scoreRSP.setModelScore(executeResult.getModelScore());
                scoreRSP.setQualitativeScore(executeResult.getQualitativeScore());
                scoreRSP.setQuantitativeScore(executeResult.getQuantitativeScore());
                scoreRSP.setFirstScore(executeResult.getFirstScore());
                scoreRSP.setCustScore(executeResult.getCustScore());
                scoreRSP.setSubjectVesselScore(executeResult.getSubjectVesselScore());
                if (executeResult.getDefaultRate() != null) {
                    BigDecimal bigDecimal = new BigDecimal(executeResult.getDefaultRate()).setScale(2, RoundingMode.DOWN);
                    scoreRSP.setDefaultRate(bigDecimal.toString());
                }
                if (Arrays.asList("政信主体地级市模型", "政信主体区县级模型").contains(ratingClient.getModelName())) {
                    scoreRSP.setSubjectScore(executeResult.getModelScore());
                    scoreRSP.setAreaScore(executeResult.getAreaScore());
                    BigDecimal areaScore = new BigDecimal(Optional.ofNullable(executeResult.getAreaScore()).orElse("0")).multiply(new BigDecimal("0.75"));
                    scoreRSP.setModelScore(areaScore.add(new BigDecimal(executeResult.getModelScore()).multiply(new BigDecimal("0.25"))).toString());
                }
            }
        }
        scoreRSP.setScore(ratingClient.getScore());
        scoreRSP.setAdjustScore(getAdjustScore(ratingClient));
        scoreRSP.setFinalScore(getFinalScore(ratingClient));
        rsp.setRatingScoreRSP(scoreRSP);

        // 历史评级信息
        RatingClient history = this.getOne(Wrappers.<RatingClient>lambdaQuery().eq(RatingClient::getClientId, ratingClient.getClientId())
                .isNotNull(RatingClient::getEffectTime)
                .ne(RatingClient::getId, ratingClient.getId())
                .orderByDesc(RatingClient::getCreateTime)
                .last("limit 1"));
//        RatingClientDetailLibRSP lib = ratingClientLibService.detail(req.getId());
        if (history != null) {
            RatingClientDetailLibRSP historyInfo = BeanUtil.copyProperties(history, RatingClientDetailLibRSP.class);
            historyInfo.setCreateByName(id2NameService.sysUserId2NameSingle(historyInfo.getCreateBy()));
            rsp.setHistoryInfo(historyInfo);
        }
        // 定量指标部分字段用本地数据库最新数据覆盖
        if (CollectionUtil.isNotEmpty(rsp.getQuantitativeList()) && StrUtil.equalsAny(ratingClient.getModelCode(), "client_djs_service", "client_qxj_service")) {
            List<RatingClientAreaIndicator> indicatorList = ratingClientAreaIndicatorService.listByRatingClientAndCategory(ratingClient.getId(), Objects.equals(ratingClient.getModelCode(), "client_qxj_service") ? RatingClientAreaIndicatorConfig.CategoryCodeEnum.zxztqxj_dlzb.name() : RatingClientAreaIndicatorConfig.CategoryCodeEnum.zxztdjs_dlzb.name());
            if (CollectionUtil.isNotEmpty(indicatorList)) {
                Map<String, RatingClientAreaIndicator> indicatorMap = indicatorList.stream().collect(Collectors.toMap(RatingClientAreaIndicator::getIndicatorCode, e -> e));
                rsp.getQuantitativeList().forEach(e -> {
                    RatingClientAreaIndicator indicator = indicatorMap.get(e.getFieldName());
                    if (Objects.nonNull(indicator)) {
                        e.setFetchMethod(indicator.getMode());
                        boolean same = Optional.ofNullable(indicator.getIndicatorValue()).orElse(BigDecimal.ZERO).compareTo(Optional.ofNullable(indicator.getIndicatorValueSystem()).orElse(BigDecimal.ZERO)) == 0;
                        e.setIsChange(!same);
                    }
                });
            }
        }
        return rsp;
    }

    public void overturn(RatingClientOverturnREQ req) {
        if (req.getFinalScore() == null) {
            throw new MithrasException("评级下迁时评级认定不得为空");
        }
        RatingClient ratingClient = this.getById(req.getId());
        if (ratingClient == null) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
//        RatingReport ratingReport = ratingReportMapper.selectById(ratingClient.getReportId());
//        if(ratingReport != null && ratingReport.getApprovalInfo() != null) {
//            List<RatingReportApprovalRSP.RatingApprovalRSP> approvalRSPList = JSON.parseArray(ratingReport.getApprovalInfo(), RatingReportApprovalRSP.RatingApprovalRSP.class);
//            for (RatingReportApprovalRSP.RatingApprovalRSP approvalRSP : approvalRSPList) {
//                if(Objects.equals(Boolean.FALSE,approvalRSP.getApprovalStatus())){
//                    throw new MithrasException("审核意见中存在不通过指标，不可进行评级下迁");
//                }
//            }
//        }
        String compareScore = Objects.equals(Boolean.TRUE, ratingClient.getAdjust()) ? ratingClient.getAdjustScore() : ratingClient.getScore();
        // 风控经理的二次调整可能会把评级给调整回来，不是回调评级的情况 再作出评分未改变拦截
        if (Objects.equals(compareScore, req.getFinalScore()) && Objects.equals(Boolean.FALSE, ratingClient.getOverturn())) {
            throw new MithrasException("评分未改变");
        }
        RatingLevelEnum overTurnScoreLevel = RatingLevelEnum.find(req.getFinalScore());
        RatingLevelEnum scoreLevel = RatingLevelEnum.findByDisplay(compareScore);
        if (overTurnScoreLevel.getScore() > scoreLevel.getScore()) {
            throw new MithrasException("评级下迁不支持上调评分");
        }
        this.update(Wrappers.<RatingClient>lambdaUpdate().eq(RatingClient::getId, req.getId())
                .set(RatingClient::getOverturnScore, overTurnScoreLevel.getDisplay())
                .set(RatingClient::getFinalScore, overTurnScoreLevel.getDisplay())
                .set(RatingClient::getOverturn, true));
    }


    public void adjust(RatingClientOverturnREQ req) {
        if (req.getFinalScore() == null) {
            throw new MithrasException("评级调整时评级认定不得为空");
        }
        RatingClient ratingClient = this.getById(req.getId());
        if (ratingClient == null) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        RatingLevelEnum adjustScoreLevel = RatingLevelEnum.find(req.getFinalScore());
        if (Objects.equals(ratingClient.getScore(), req.getFinalScore())) {
            throw new MithrasException("评分未改变");
        }
        this.update(Wrappers.<RatingClient>lambdaUpdate().eq(RatingClient::getId, req.getId())
                .set(RatingClient::getFinalScore, adjustScoreLevel.getDisplay())
                .set(RatingClient::getAdjustScore, adjustScoreLevel.getDisplay())
                .set(RatingClient::getAdjust, true));
    }

    public void overturnApproval(RatingOverturnApprovalREQ req) {
        boolean agreeOverturn = req.isAgreeOverturn();
        if (!agreeOverturn) {
            this.update(Wrappers.<RatingClient>lambdaUpdate().eq(RatingClient::getId, req.getId())
                    .set(RatingClient::getFinalScore, null)
                    .set(RatingClient::getOverturn, false));
        }
    }

    public RatingClientUpdateRSP ratingClientUpdate(RatingClientUpdateREQ req) {
        RatingClient ratingClient = this.getById(req.getId());
        if (ratingClient == null) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        if (!Objects.equals(AccountUtil.getLoginInfo().getId(), ratingClient.getCreateBy())) {
            throw new MithrasException("只允许创建人进行编辑操作");
        }
        if (ratingClient.getRatingStatus()) {
            throw new MithrasException("不能修改生效的评级数据");
        }
        if (findRelatedProcess(req.getId()) != null) {
            throw new MithrasException("不能修改处于流程中的评级数据");
        }
        Client client = clientService.getById(req.getClientId());
        if (client == null) {
            throw new MithrasException("该客户不存在");
        }
        update(Wrappers.<RatingClient>lambdaUpdate().eq(RatingClient::getId, req.getId())
                .set(RatingClient::getModelCode, req.getCode())
                .set(RatingClient::getModelName, req.getName())
                .set(RatingClient::getClientId, client.getId())
                .set(RatingClient::getClientName, client.getClientName())
                .set(RatingClient::getClientCode, client.getClientCode())
                .set(RatingClient::getUscc, client.getUscCode())
                .set(RatingClient::getSnapshotId, null)
                .set(RatingClient::getReportId, null));
        return new RatingClientUpdateRSP(ratingClient.getId());
    }

    public void ratingClientDelete(RatingClientDeleteREQ req) {
        RatingClient ratingClient = this.getById(req.getId());
        if (ratingClient == null) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        if (!Objects.equals(AccountUtil.getLoginInfo().getId(), ratingClient.getCreateBy())) {
            throw new MithrasException("只允许创建人进行删除操作");
        }
        if (ratingClient.getRatingStatus()) {
            throw new MithrasException("不能删除生效的评级数据");
        }
        if(ratingClient.getProcessStatus() != null && !StrUtil.equalsAny(ratingClient.getProcessStatus(), ProcessStatus.UN_SUBMIT.name(), ProcessStatus.APPROVAL_REJECT.name())){
            throw new MithrasException("只能删除草稿数据");
        }
        update(Wrappers.<RatingClient>lambdaUpdate().eq(RatingClient::getId, req.getId())
                .set(RatingClient::getDeleted, true));

    }

    /**
     * 校验评估主体是否存在生效的客户评级
     *
     * @param evaluationSubjectId 客户id
     */
    public void effectClientCheck(Long evaluationSubjectId, Long clientId) {
        if (evaluationSubjectId == null) {
            throw new MithrasException("项目不存在评估主体客户，请添加评估主体后再提交流程！");
        }
        List<CorpCommerceInfo> list = corpCommerceInfoService.list(Collections.singletonList(evaluationSubjectId));
        String riskControlIndustryClassify = list.get(0).getRiskControlIndustryClassify();
        // 公用类的客户需在提交项目立项时完成评估主体的客户评级
        if (RiskControlIndustryClassify.CIVIL_CONSUMPTION.name().equals(riskControlIndustryClassify) ||
                RiskControlIndustryClassify.PUBLIC_UTILITIES.name().equals(riskControlIndustryClassify) ||
                RiskControlIndustryClassify.TRAVEL.name().equals(riskControlIndustryClassify)) {
//            int evaluationCount = this.count(Wrappers.<RatingClient>lambdaQuery()
//                    .in(RatingClient::getClientId, evaluationSubjectId)
//                    .eq(RatingClient::getRatingStatus, true));
//            int mainCount = this.count(Wrappers.<RatingClient>lambdaQuery()
//                    .in(RatingClient::getClientId, clientId)
//                    .eq(RatingClient::getRatingStatus, true));
//            if(evaluationCount == 0 || mainCount == 0){
//                throw new MithrasException("无90天内有效的主承租人/评估主体客户评级信息，请完成或更新评级后再提交流程！");
//            }
            RatingClientProjDetailRSP rsp1 = this.getEffectRating(90, evaluationSubjectId, null, new Date());
            if (Objects.isNull(rsp1) || StrUtil.isBlank(rsp1.getRatingFinalScore())) {
                throw new MithrasException("无90天内有效的评估主体客户评级信息，请完成或更新评级后再提交流程！");
            }
            RatingClientProjDetailRSP rsp2 = this.getEffectRating(90, clientId, null, new Date());
            if (Objects.isNull(rsp2) || StrUtil.isBlank(rsp2.getRatingFinalScore())) {
                throw new MithrasException("无90天内有效的主承租人客户评级信息，请完成或更新评级后再提交流程！");
            }
        }
    }

    public RatingClientAbstractRSP clientAbstract(RatingClientAbstractREQ req) {
        RatingClient ratingClient = this.getById(req.getId());
        if (ratingClient == null) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        RatingSnapshot ratingSnapshot = ratingSnapshotService.getById(ratingClient.getReportId());
        if (ratingSnapshot != null) {
            DecisionExecuteResult executeResult = JSON.parseObject(ratingSnapshot.getScore(), DecisionExecuteResult.class);
            RatingClientAbstractRSP rsp = BeanUtil.copyProperties(executeResult, RatingClientAbstractRSP.class);
            BigDecimal rate = new BigDecimal(rsp.getDefaultRate());
            BigDecimal bigDecimal = rate.setScale(2, RoundingMode.DOWN);
            rsp.setDefaultRate(bigDecimal.toString());
            return rsp;
        }
        return null;
    }

    public RatingClientAccessCheckRSP ratingClientAccessCheck(RatingClientAccessCheckREQ req) {
        if (Constant.hymxCode.equals(req.getCode())) { // 航运模型要校验 客户名称
            if (req.getClientId() == null && req.getClientName() == null) {
                throw new MithrasException("客户名称不得为空");
            }
            int count = this.count(Wrappers.<RatingClient>lambdaQuery()
                    .eq(RatingClient::getClientId, req.getClientId())
                    .eq(RatingClient::getModelCode, Constant.hymxCode)
                    .eq(RatingClient::getRatingStatus, true));
            RatingClientAccessCheckRSP rsp = new RatingClientAccessCheckRSP();
            rsp.setExist(count > 0);
            return rsp;
        } else { // 其他模型  校验外置
            if (req.getClientId() == null) {
                throw new MithrasException("客户id不得为空");
            }
        }
        // 新增前再校验一次
        RatingClientInfoREQ infoREQ = new RatingClientInfoREQ();
        infoREQ.setClientId(req.getClientId());
        ratingClientInfo(infoREQ);
        /*if (req.getName().contains("政信")) {
            Map<String, Object> param = new HashMap<>();
            param.put("area_uni_code", getClientAreaUniCode(req.getClientId(), null));
            RatingAccessCheckRSP ratingAccessCheckRSP = decisionService.accessCheckClient(param);
            if (!ratingAccessCheckRSP.getClientAreaAccess()) {
                throw new MithrasException("该征信客户所属区域不在准入范围内，无法进行客户评级");
            }
        }*/
        int count = this.count(Wrappers.<RatingClient>lambdaQuery().eq(RatingClient::getClientId, req.getClientId())
                .eq(RatingClient::getRatingStatus, true));
        RatingClientAccessCheckRSP rsp = new RatingClientAccessCheckRSP();
        rsp.setExist(count > 0);
        return rsp;
    }

    public void ratingClientIndexApproval(RatingReportApprovalRSP req) {
        RatingClient ratingClient = getById(req.getId());
        if (ratingClient == null) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        if (req.getRatingApprovalRSP().getFieldName() == null) {
            return;
        }
        RatingReport ratingReport = ratingReportMapper.selectById(ratingClient.getReportId());
        if (ratingReport != null) {
            // 评级审批结果集
            List<RatingReportApprovalRSP.RatingApprovalRSP> approvalRSPList = new ArrayList<>();
            RatingReportApprovalRSP.RatingApprovalRSP newApproval = req.getRatingApprovalRSP();
            if (newApproval == null) {
                return;
            }
            // 标记是否为已存在的指标
            boolean flag = false;
            if (ratingReport.getApprovalInfo() != null) {
                approvalRSPList = JSON.parseArray(ratingReport.getApprovalInfo(), RatingReportApprovalRSP.RatingApprovalRSP.class);
                for (int i = 0; i < approvalRSPList.size(); i++) {
                    if (Objects.equals(approvalRSPList.get(i).getFieldName(), newApproval.getFieldName())) {
                        if (newApproval.getApprovalStatus() != null) {
                            approvalRSPList.get(i).setApprovalStatus(newApproval.getApprovalStatus());
                        }
                        if (newApproval.getApprovalOpinion() != null) {
                            approvalRSPList.get(i).setApprovalOpinion(newApproval.getApprovalOpinion());
                        }
                        flag = true;
                        break;
                    }
                }

            }
            if (!flag) {
                approvalRSPList.add(newApproval);
            }
            ratingReport.setApprovalInfo(JSON.toJSONString(approvalRSPList));
            ratingReportMapper.updateById(ratingReport);
        }
    }

    public List<RatingClientOverturnRecordRSP> clientOverturnRecord(RatingClientOverturnRecordREQ req) {
        RatingClient ratingClient = this.getOne(Wrappers.<RatingClient>lambdaQuery().eq(RatingClient::getId, req.getId()));
        List<RatingClientOverturnRecordRSP> rspList = new ArrayList<>();
        if (ratingClient != null) {
            Map<String, OperateRecord> processTime = getProcessRecordTime(ratingClient.getId());
//            RatingClientDetailLibRSP libRSP = libService.detail(ratingClient.getId());
            if (Boolean.TRUE.equals(ratingClient.getOverturn())) {
                OperateRecord overturnRecord = Optional.ofNullable(processTime.get("userTask_riskManager_fuhe")).orElse(processTime.get("userTask_riskManager"));
                RatingClientOverturnRecordRSP rsp = new RatingClientOverturnRecordRSP();
                if (overturnRecord != null) {
                    rsp.setOverturnTime(overturnRecord.getGmtCreate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
                    rsp.setOverturnOpinion(overturnRecord.getNote());
                    rsp.setOverturnUserName(id2NameService.sysUserId2NameSingle(Long.valueOf(overturnRecord.getHandlerId())));
                }
                rsp.setAdjustType("下迁");
                rsp.setScore(ratingClient.getScore());
                rsp.setFinalScore(ratingClient.getOverturnScore());
                rspList.add(rsp);
            } else if (Boolean.TRUE.equals(ratingClient.getAdjust())) {
                OperateRecord adjustRecord = processTime.get("userTask_startUser");
                RatingClientOverturnRecordRSP rsp = new RatingClientOverturnRecordRSP();
                rsp.setScore(ratingClient.getScore());
                rsp.setFinalScore(ratingClient.getAdjustScore());
                rsp.setAdjustType("调整");
                if (adjustRecord != null) {
                    rsp.setOverturnTime(adjustRecord.getGmtCreate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
                    rsp.setOverturnOpinion(adjustRecord.getNote());
                    rsp.setOverturnUserName(id2NameService.sysUserId2NameSingle(ratingClient.getCreateBy()));
                }
                rspList.add(rsp);
            }
        }
        return rspList;
    }


    public Long getClientAreaUniCode(Long clientId, String modelName) {
        CorpAddressInfo corpAddressInfo = corpAddressInfoMapper.selectOne(Wrappers.<CorpAddressInfo>lambdaQuery()
                .eq(CorpAddressInfo::getClientId, clientId)
                .eq(CorpAddressInfo::getAddressType, CorpAddressType.REGISTRY_ADDRESS.name()).orderByDesc(CorpAddressInfo::getCreateTime)
                .last(StringUtil.mysqlLimitOne()));
        if (corpAddressInfo == null) {
            throw new MithrasException("请先维护该客户的地址信息");
        }
        Long areaCode = null;
        // 通过系统中address表的code关联dm地址表
        AreaInfo areaInfo = areaInfoMapper.selectOne(Wrappers.<AreaInfo>lambdaQuery().eq(AreaInfo::getAdministrativeCode, corpAddressInfo.getDistrict()));
        if (areaInfo != null) {
            areaCode = Objects.equals(modelName, "政信主体地级市模型") ? areaInfo.getCityUniCode() : areaInfo.getAreaUniCode();
        } else {
            // 通过code找不到则使用地址名称来匹配
            List<String> codeList = Stream.of(corpAddressInfo.getDistrict(), corpAddressInfo.getCity(), corpAddressInfo.getProvince()).collect(Collectors.toList());
            Map<String, String> nameMap = addressDictionaryMapper.selectList(Wrappers.<AddressDictionary>lambdaQuery().in(CollectionUtils.isNotEmpty(codeList), AddressDictionary::getCode, codeList))
                    .stream().collect(Collectors.toMap(AddressDictionary::getCode, AddressDictionary::getDisplay, (m1, m2) -> m2));

            LambdaQueryWrapper<AreaInfo> wrapper = Wrappers.lambdaQuery();
            // 评估主体区域
            if (ObjectUtil.isNotNull(nameMap.get(corpAddressInfo.getProvince()))) {
                wrapper.eq(nameMap.get(corpAddressInfo.getProvince()) != null, AreaInfo::getProvinceName, nameMap.get(corpAddressInfo.getProvince()));
            }
            if (ObjectUtil.isNotNull(nameMap.get(corpAddressInfo.getCity()))) {
                wrapper.eq(nameMap.get(corpAddressInfo.getCity()) != null, AreaInfo::getCityName, nameMap.get(corpAddressInfo.getCity()));
            }
            if (ObjectUtil.isNotNull(nameMap.get(corpAddressInfo.getDistrict()))) {
                if (nameMap.get(corpAddressInfo.getDistrict()).equals("市辖区")) {
                    wrapper.isNull(AreaInfo::getDistrictName);
                } else {
                    wrapper.eq(nameMap.get(corpAddressInfo.getDistrict()) != null, AreaInfo::getDistrictName, nameMap.get(corpAddressInfo.getDistrict()));
                }
            }
            try {
                AreaInfo areaInfo1 = areaInfoMapper.selectOne(wrapper);
                areaCode = Objects.equals(modelName, "政信主体地级市模型") ? areaInfo1.getCityUniCode() : areaInfo1.getAreaUniCode();
            } catch (Exception e) {
                log.error("获取dm地址信息发生异常-地址为:{}-{}-{}", nameMap.get(corpAddressInfo.getProvince()),
                        nameMap.get(corpAddressInfo.getCity()), nameMap.get(corpAddressInfo.getDistrict()));
            }
        }
        return areaCode;
    }

    private Map<String, OperateRecord> getProcessRecordTime(Long id) {
        ProcessPageReq processPageReq = new ProcessPageReq();
        processPageReq.setPageIndex(1);
        processPageReq.setPageSize(Integer.MAX_VALUE);
        processPageReq.setBusinessKey(String.valueOf(id));
        processPageReq.setModelKeyList(Arrays.asList(ProcessModelTypeEnum.RatingClientCreateFlow.name(), ProcessModelTypeEnum.RatingClientUpdateFlow.name()));
        cn.zswltech.flow.core.util.Page<ProcessResp> processRespPage = taskApiService.queryProcess(processPageReq);
        if (CollectionUtils.isEmpty(processRespPage.getContents())) {
            return new HashMap<>();
        }
        ProcessResp processResp = processRespPage.getContents().get(0);
        String processInstanceId = processResp.getProcessInstanceId();
        Example example = new Example(OperateRecord.class);
        example.createCriteria().andEqualTo("processInstanceId", processInstanceId);
        example.orderBy("gmtCreate").desc();
        List<OperateRecord> operateRecordList = operateRecordMapper.selectByCondition(example);

        Map<String, OperateRecord> operateRecordMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(operateRecordList)) {
            operateRecordMap = operateRecordList.stream().collect(Collectors.toMap(OperateRecord::getTaskActivityId, Function.identity(), (m1, m2) -> m1));
            // 补充发起人节点
            operateRecordMap.put("userTask_startUser", operateRecordList.get(operateRecordList.size() - 1));
        }
        return operateRecordMap;
    }

    public ProcessResp findRelatedProcess(Long ratingClientId) {
        ProcessPageReq processPageReq = new ProcessPageReq();
        processPageReq.setPageIndex(1);
        processPageReq.setPageSize(1);
        processPageReq.setBusinessKey(String.valueOf(ratingClientId));
        processPageReq.setModelKeyList(BusinessModuleEnum.RATING_CLIENT.getModelKeyList());
        processPageReq.setProcessStatusList(Arrays.asList(ProcessBusinessStatusEnum.RUNNING.getType(), ProcessBusinessStatusEnum.SUSPEND.getType()));
        cn.zswltech.flow.core.util.Page<ProcessResp> processRespPage = taskApiService.queryProcess(processPageReq);
        return processRespPage.getContents().stream().findFirst().orElse(null);
    }

    /**
     * 是否需要更新快照
     *
     * @param ratingClientId
     * @return
     */
    public boolean needUpdateParam(Long ratingClientId) {
        ProcessResp relatedProcess = findRelatedProcess(ratingClientId);
        if (relatedProcess != null) {
            String processInstanceId = relatedProcess.getProcessInstanceId();
            Example example = new Example(OperateRecord.class);
            example.createCriteria().andEqualTo("processInstanceId", processInstanceId);
            example.orderBy("gmtCreate").desc();
            List<OperateRecord> operateRecordList = operateRecordMapper.selectByCondition(example);
            long count = operateRecordList.stream().filter(f -> Arrays.asList(CommentTypeEnum.BHFQR.name(), CommentTypeEnum.BHFQR_ZJDW.name()).contains(f.getType())).count();
            return count > 0;
        }
        return true;
    }


    public String getAdjustScore(RatingClient ratingClient) {
        if (Objects.equals(Boolean.TRUE, ratingClient.getAdjust())) {
            // 已调整,取adjustScore
            return ratingClient.getAdjustScore();
        } else if (Objects.equals(Boolean.FALSE, ratingClient.getAdjust())) {
            // 未调整,取score
            return ratingClient.getScore();
        } else {
            // 流程未到项目经理
            return null;
        }
    }

    public String getFinalScore(RatingClient ratingClient) {
        if (Objects.equals(Boolean.TRUE, ratingClient.getOverturn())) {
            // 已下迁,取overturnScore
            return ratingClient.getOverturnScore();
        } else if (Objects.equals(Boolean.FALSE, ratingClient.getOverturn())) {
            // 未下迁
            if (Objects.equals(Boolean.TRUE, ratingClient.getAdjust())) {
                // 已调整,取adjustScore
                return ratingClient.getAdjustScore();
            } else if (Objects.equals(Boolean.FALSE, ratingClient.getAdjust())) {
                // 未调整,取score
                return Optional.ofNullable(ratingClient.getFinalScore()).orElse(ratingClient.getScore());
            } else {
                // 流程未到项目经理
                return null;
            }
        } else {
            // 流程未到风控经理
            return null;
        }
    }


    public void clientIndexCheck(RatingClientIndexCheckREQ req) {
        RatingClient ratingClient = baseMapper.selectById(req.getId());
        if (ratingClient == null) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        ratingReportService.checkApprovalOpinion(ratingClient);
    }

    public List<RatingParamFieldApprovalRSP> areaModelInfo(Long ratingClientId, String modelName) {
        LambdaQueryWrapper<RatingClientAreaIndicator> query = Wrappers.lambdaQuery();
        query.eq(RatingClientAreaIndicator::getRatingClientId, ratingClientId);
        if (Objects.equals("政信主体地级市模型", modelName)) {
//            CityModelResult cityModelResult = dmCalculateIndicatorMapper.selectCityModelInfo(areaUniCode);
//            return areaModelIndexReflect(cityModelResult);
            query.eq(RatingClientAreaIndicator::getCategoryCode, RatingClientAreaIndicatorConfig.CategoryCodeEnum.zxztdjs_dlzb.name());
        } else if (Objects.equals("政信主体区县级模型", modelName)) {
//            AreaModelResult areaModelResult = dmCalculateIndicatorMapper.selectAreaModelInfo(areaUniCode);
//            return areaModelIndexReflect(areaModelResult);
            query.eq(RatingClientAreaIndicator::getCategoryCode, RatingClientAreaIndicatorConfig.CategoryCodeEnum.zxztqxj_dlzb.name());
        }
        List<RatingClientAreaIndicator> areaIndicatorList = ratingClientAreaIndicatorService.list(query);
        if (CollectionUtil.isEmpty(areaIndicatorList)) {
            return Collections.emptyList();
        }
        return areaIndicatorList.stream().map(e -> {
            RatingParamFieldApprovalRSP rsp = new RatingParamFieldApprovalRSP();
            rsp.setFieldName(e.getIndicatorCode());
            rsp.setFieldComment(e.getIndicatorName());
            rsp.setFieldValue(e.getIndicatorValue());
            rsp.setFetchMethod(e.getMode());
            rsp.setDataType(e.getIndicatorDataType());
            rsp.setIsAreaModelIndex(Boolean.TRUE);
            rsp.setUnit(e.getIndicatorUnit());
            boolean same = Optional.ofNullable(e.getIndicatorValue()).orElse(BigDecimal.ZERO).compareTo(Optional.ofNullable(e.getIndicatorValueSystem()).orElse(BigDecimal.ZERO)) == 0;
            rsp.setIsChange(!same);
            rsp.setBizId(e.getId());
            rsp.setCanEdit(Boolean.TRUE);
            return rsp;
        }).collect(Collectors.toList());
    }


    public List<RatingParamFieldApprovalRSP> areaModelIndexReflect(Object obj) {
        if (Objects.isNull(obj)) {
            return null;
        }
        Map<String, BigDecimal> indexMap = new HashMap<>();
        Class<?> clazz = obj.getClass();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            // 获取字段名
            String fieldName = field.getName();
            try {
                // 获取字段值
                BigDecimal fieldValue = (BigDecimal) field.get(obj);
                indexMap.put(fieldName, fieldValue);
            } catch (IllegalAccessException e) {
                log.error("区域模型指标获取异常：{}", e);
            }
        }
        List<RatingParamFieldApprovalRSP> sysIndexList = new ArrayList<>();
        for (Map.Entry<String, BigDecimal> entry : indexMap.entrySet()) {
            AreaModelIndexEnum areaModelIndexEnum = AreaModelIndexEnum.findByFieldName(entry.getKey());
            String indexName = Optional.ofNullable(areaModelIndexEnum).map(AreaModelIndexEnum::getDisplay).orElse(null);
            String unit = Optional.ofNullable(areaModelIndexEnum).map(AreaModelIndexEnum::getUnit).orElse(null);
            RatingParamFieldApprovalRSP sysIndex = new RatingParamFieldApprovalRSP();
            sysIndex.setFieldName(entry.getKey());
            sysIndex.setFieldComment(indexName);
            sysIndex.setFieldValue(new BigDecimal("-9999999999999.0000").equals(entry.getValue()) ? null : entry.getValue());
            sysIndex.setFetchMethod(RatingFetchMethodEnum.SYSTEM.name());
            sysIndex.setDataType(RatingDataTypeEnum.NUMBER.name());
            sysIndex.setIsAreaModelIndex(Boolean.TRUE);
            sysIndex.setUnit(unit);
            sysIndexList.add(sysIndex);
        }
        return sysIndexList;
    }
}
