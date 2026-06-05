package cn.zswltech.mithras.factory.service;
import cn.zswltech.mithras.customer.domain.enums.SubjectItemType;
import cn.zswltech.mithras.customer.domain.enums.CorpAddressType;
import cn.zswltech.mithras.workflow.domain.enums.CommonProcessPrepareStatus;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.zswltech.decision.engine.dto.decision.DecisionModelREQ;
import cn.zswltech.decision.engine.dto.decision.DecisionModelRSP;
import cn.zswltech.flow.core.api.FlowProcessApiService;
import cn.zswltech.flow.core.api.FlowTaskApiService;
import cn.zswltech.flow.core.domain.req.task.ProcessPageReq;
import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.flow.core.enums.ProcessBusinessStatusEnum;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.client.client.ClientInfo;
import cn.zswltech.mithras.dto.client.client.ClientListRSP;
import cn.zswltech.mithras.dto.message.MessageAddREQ;
import cn.zswltech.mithras.dto.rating.*;
import cn.zswltech.mithras.dto.rating.ratingamount.*;
import cn.zswltech.mithras.dto.rating.ratingclient.RatingClientProjDetailRSP;
import cn.zswltech.mithras.factory.enums.RatingDataTypeEnum;
import cn.zswltech.mithras.factory.enums.RatingModelTypeEnum;
import cn.zswltech.mithras.factory.lib.ratingamount.RatingAmountLibService;
import cn.zswltech.mithras.factory.lib.ratingamount.impl.RatingAmountVersionServiceImpl;
import cn.zswltech.mithras.factory.mapper.AreaInfoMapper;
import cn.zswltech.mithras.factory.mapper.RatingAmountMapper;
import cn.zswltech.mithras.factory.mapper.RatingReportMapper;
import cn.zswltech.mithras.factory.model.*;
import cn.zswltech.mithras.factory.service.RatingAmountApplicationService;
import cn.zswltech.mithras.dto.rating.decision.DecisionExecuteResult;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.service.constant.VersionTypeConstants;
import cn.zswltech.mithras.message.convert.MessageConver;
import cn.zswltech.mithras.service.enums.*;
import cn.zswltech.mithras.service.enums.common.ProcessStatus;
import cn.zswltech.mithras.message.enums.notice.MessageTypeEnum;
import cn.zswltech.mithras.message.enums.notice.NoticeSourceENUM;
import cn.zswltech.mithras.projectprocess.enums.projreview.ProjRegionalClassify;
import cn.zswltech.mithras.riskcontrol.common.RiskControlIndustryClassify;
import cn.zswltech.mithras.basedata.mapper.AddressDictionaryMapper;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.client.ClientMapper;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.corp.CorpAddressInfoMapper;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.corp.CorpCommerceInfoMapper;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.corp.CorpSubjectItemMapper;
import cn.zswltech.mithras.basedata.mapper.model.AddressDictionary;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.Client;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.CorpAddressInfo;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.CorpCommerceInfo;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.CorpSubjectItem;
import cn.zswltech.mithras.workflow.infrastructure.persistence.mapper.model.process.prepare.CommonProcessPrepare;
import cn.zswltech.mithras.projectprocess.mapper.model.projreview.ProjReviewBaseInfo;
import cn.zswltech.mithras.workflow.infrastructure.persistence.mapper.process.prepare.CommonProcessPrepareMapper;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.system.service.BizProcessDataService;
import cn.zswltech.mithras.system.service.Id2NameService;
import cn.zswltech.mithras.system.service.SysUserService;
import cn.zswltech.mithras.service.service.client.ClientService;
import cn.zswltech.mithras.service.service.flow.FlowEndEventProcessor;
import cn.zswltech.mithras.projectprocess.service.lib.projreview.ProjReviewBaseInfoLibService;
import cn.zswltech.mithras.message.service.MessageService;
import cn.zswltech.mithras.service.service.projreview.ProjReviewBaseInfoService;
import cn.zswltech.mithras.service.util.StringUtil;
import cn.zswltech.mithras.service.util.VersionUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static cn.zswltech.mithras.customer.domain.enums.SubjectItemType.PROFIT;

@Slf4j
@Service
public class RatingAmountService extends ServiceImpl<RatingAmountMapper, RatingAmount> implements RatingAmountApplicationService, FlowEndEventProcessor {

    @Resource
    private ProjReviewBaseInfoService projReviewBaseInfoService;
    @Resource
    private ProjReviewBaseInfoLibService projReviewBaseInfoLibService;
    @Resource
    private ClientService clientService;
    @Resource
    private DecisionService decisionService;
    @Resource
    private RatingClientService ratingClientService;
    @Resource
    private Id2NameService id2NameService;
    @Resource
    private AddressDictionaryMapper addressDictionaryMapper;
    @Resource
    private RatingSnapshotService ratingSnapshotService;
    @Resource
    private CorpSubjectItemMapper subjectItemMapper;
    @Resource
    private RatingReportMapper ratingReportMapper;
    @Resource
    private RatingAmountLibService libService;
    @Resource
    private CommonProcessPrepareMapper commonProcessPrepareMapper;
    @Resource
    private SysUserService sysUserService;
    @Resource
    private FlowProcessApiService processApiService;
    @Resource
    private BizProcessDataService bizProcessDataService;
    @Resource
    private RatingAmountVersionServiceImpl versionService;
    @Resource
    private CorpCommerceInfoMapper ccfMapper;
    @Resource
    private MessageService messageService;
    @Resource
    private MessageConver messageConver;
    @Resource
    private RatingAmountMapper ratingAmountMapper;
    @Resource
    private AreaInfoMapper areaInfoMapper;
    @Resource
    private CorpAddressInfoMapper corpAddressInfoMapper;
    @Resource
    private FlowTaskApiService taskApiService;
    @Resource
    private ClientMapper clientMapper;


    public PageR<RatingAmountPageRSP> ratingAmountPage(RatingAmountPageREQ req) {
        LambdaQueryWrapper<RatingAmount> wrapper = createCondition(req);
        Page<RatingAmount> page = this.page(new Page<>(req.getPage(), req.getPageSize()), wrapper);
        List<RatingAmount> records = page.getRecords();
        if(records == null) {
            return new PageR<>();
        }
        List<Long> userIdList = records.stream().map(RatingAmount::getCreateBy).collect(Collectors.toList());
        List<Long> clientIdList = records.stream().map(RatingAmount::getClientId).collect(Collectors.toList());
        Set<Long> deptIdList = records.stream().map(RatingAmount::getBelongDeptId).collect(Collectors.toSet());
        Map<Long, String> userId2Name = id2NameService.sysUserId2Name(userIdList);
        Map<Long, String> clientId2Name = id2NameService.clientId2Name(clientIdList);
        Map<Long, String> deptId2Name = id2NameService.deptId2Name(deptIdList);

        List<RatingAmountPageRSP> result = records.stream().map(record -> {
            RatingAmountPageRSP rsp = new RatingAmountPageRSP();
            BeanUtil.copyProperties(record, rsp);
//            if(ProcessStatus.APPROVAL_PASS.name().equals(record.getProcessStatus())){
//                // 只有流程状态为“已通过”的评级显示评级认定结果
//                double projQuota = generateProjQuota(record, record.getClientQuota());
            if(record.getClientQuota() != null) {
                rsp.setProjQuota(transformWanYuan(generateProjQuota(record, record.getClientQuota())));
            }
//            }
            rsp.setCreateByName(userId2Name.get(rsp.getCreateBy()));
            rsp.setClientName(clientId2Name.get(rsp.getClientId()));
            rsp.setStartOrg(deptId2Name.get(record.getBelongDeptId()));
            return rsp;
        }).collect(Collectors.toList());
        return PageR.of(page,result);
    }

    private LambdaQueryWrapper<RatingAmount> createCondition(RatingAmountPageREQ req) {
        QueryWrapper<RatingAmount> wrapper = new QueryWrapper<>();
        // 债项评级tab页仅展示与该项目评审相关的数据
        return wrapper.lambda().eq(RatingAmount::getProjReviewId,req.getProjReviewId())
                .orderByDesc(RatingAmount::getCreateTime);
    }

    public RatingAmountInfoRSP ratingAmountInfo(RatingAmountInfoREQ req) {
        RatingAmountInfoRSP rsp = new RatingAmountInfoRSP();
        ProjReviewBaseInfo reviewBaseInfo = projReviewBaseInfoService.getById(req.getProjReviewId());
        if(reviewBaseInfo == null){
            throw new MithrasException("评审数据不存在");
        }
        // 处理主承租人，评估主体
        List<ClientInfo> clientInfos = JSON.parseArray(reviewBaseInfo.getLesseeInfo(), ClientInfo.class);
        if(CollectionUtils.isNotEmpty(clientInfos)){
            List<Long> clientIdList = clientInfos.stream().map(ClientInfo::getClientId).collect(Collectors.toList());
            clientIdList.add(reviewBaseInfo.getEvaluationSubjectId());
            List<Client> clientList = clientService.list(Wrappers.<Client>lambdaQuery()
                    .in(Client::getId,clientIdList));
            if(CollectionUtils.isNotEmpty(clientList)){
                Map<Long, Client> uscCodeMap = clientList.stream().collect(Collectors.toMap(Client::getId, Function.identity(), (m1,m2) -> m1));
                // 评估主体下拉框
                List<RatingAmountInfoRSP.EvaluationSubject> evaluationSubjectList = clientInfos.stream().map(clientInfo -> {
                    RatingAmountInfoRSP.EvaluationSubject subject = new RatingAmountInfoRSP.EvaluationSubject();
                    subject.setEvaluationSubjectId(clientInfo.getClientId());
                    subject.setEvaluationSubjectName(clientInfo.getClientName());
                    subject.setEvaluationSubjectUscCode(uscCodeMap.get(clientInfo.getClientId()).getUscCode());
                    return subject;
                }).collect(Collectors.toList());
                // 新能源企业评估主体填写主承租人，其他敞口企业评估主体填写原评估主体
                String riskControlIndustryClassify = reviewBaseInfo.getRiskControlIndustryClassify();
                if(RiskControlIndustryClassify.NEW_MATERIALS.name().equals(riskControlIndustryClassify)){
                    Client client = uscCodeMap.get(reviewBaseInfo.getClientId());
                    rsp.setEvaluationSubjectId(client.getId());
                    rsp.setEvaluationSubjectName(client.getClientName());
                    rsp.setEvaluationSubjectUscCode(client.getUscCode());
                }else{
                    Client client = uscCodeMap.get(reviewBaseInfo.getClientId());
                    Client evaluationClient = uscCodeMap.get(reviewBaseInfo.getEvaluationSubjectId());
                    rsp.setEvaluationSubjectId(Optional.ofNullable(evaluationClient).orElse(client).getId());
                    rsp.setEvaluationSubjectName(Optional.ofNullable(evaluationClient).orElse(client).getClientName());
                    rsp.setEvaluationSubjectUscCode(Optional.ofNullable(evaluationClient).orElse(client).getUscCode());
                }
                rsp.setEvaluationSubjectList(evaluationSubjectList);
                rsp.setClientId(reviewBaseInfo.getClientId());
                rsp.setClientName(uscCodeMap.get(reviewBaseInfo.getClientId()).getClientName());
                rsp.setClientUscCode(uscCodeMap.get(reviewBaseInfo.getClientId()).getUscCode());
            }
        }
        rsp.setProjName(reviewBaseInfo.getProjName());
        rsp.setProjCode(reviewBaseInfo.getProjCode());
        rsp.setProjReviewId(reviewBaseInfo.getId());
        notNullValid(rsp);
        return rsp;
    }

    public RatingAmountAddRSP ratingAmountAdd(RatingAmountAddREQ req) {
        int count = ratingClientService.count(Wrappers.<RatingClient>lambdaQuery()
                .in(RatingClient::getClientId, Stream.of(req.getEvaluationSubjectId(),req.getClientId()).collect(Collectors.toList()))
                .eq(RatingClient::getRatingStatus, true));
        if(count == 0){
            throw new MithrasException("当前所选评估主体无生效的客户评级信息，请完成客户评级后再进行债项评级！");
        }

        Long clientId = req.getClientId();
        RatingAmount existRatingAmount = this.getOne(Wrappers.<RatingAmount>lambdaQuery()
                .eq(RatingAmount::getClientId, clientId)
                .eq(RatingAmount::getProjReviewId,req.getProjReviewId())
                .eq(RatingAmount::getRatingStatus,false)
                .isNull(RatingAmount::getAbandonTime));
        if(existRatingAmount != null){
            // 存在未生效评级，返回id，展示草稿数据
            if(existRatingAmount.getModelCode().equals(req.getCode())) {
                return new RatingAmountAddRSP(existRatingAmount.getId(), true);
            }else{
                throw new MithrasException("存在不同模型的未生效评级数据，无法新建评级");
            }
        }
        RatingAmount ratingAmount = BeanUtil.copyProperties(req, RatingAmount.class);
        Client client = clientService.getById(clientId);
        if(client == null){
            throw new MithrasException("客户不存在");
        }
        ratingAmount.setModelCode(req.getCode());
        ratingAmount.setProjReviewId(req.getProjReviewId());
        ratingAmount.setModelName(req.getName());
        ratingAmount.setBelongDeptId(client.getBelongDeptId());
        ratingAmount.setBelongSponsorUserId(client.getBelongSponsorId());
        ratingAmount.setEvaluationSubjectUscc(client.getUscCode());
        this.save(ratingAmount);
        return new RatingAmountAddRSP(ratingAmount.getId(),false);
    }

    public RatingAmountProjInfoRSP ratingAmountProjInfo(RatingAmountProjInfoREQ req) {
        RatingAmount ratingAmount = this.getById(req.getId());
        if(ratingAmount == null){
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        ProjReviewBaseInfo reviewBaseInfo = projReviewBaseInfoService.getById(ratingAmount.getProjReviewId());
        if(reviewBaseInfo == null){
            throw new MithrasException("评审数据不存在");
        }
//        CorpAddressInfo corpAddressInfo = corpAddressInfoMapper.selectOne(Wrappers.<CorpAddressInfo>lambdaQuery()
//                .eq(CorpAddressInfo::getClientId, ratingAmount.getEvaluationSubjectId())
//                .eq(CorpAddressInfo::getAddressType, CorpAddressType.REGISTRY_ADDRESS.name()));
        if(reviewBaseInfo.getProvince() == null || reviewBaseInfo.getCity() == null || reviewBaseInfo.getDistrict() == null){
            throw new MithrasException("请先维护评估主体地址信息");
        }
        List<String> codeList = Stream.of(reviewBaseInfo.getDistrict(), reviewBaseInfo.getCity(), reviewBaseInfo.getProvince()).collect(Collectors.toList());
        Map<String, String> nameMap = addressDictionaryMapper.selectList(Wrappers.<AddressDictionary>lambdaQuery()
                        .in(CollectionUtils.isNotEmpty(codeList),AddressDictionary::getCode, codeList))
                .stream().collect(Collectors.toMap(AddressDictionary::getCode, AddressDictionary::getDisplay,(m1,m2)->m1));
        String areaName = String.format("%s%s%s",nameMap.get(reviewBaseInfo.getProvince()),nameMap.get(reviewBaseInfo.getCity()),nameMap.get(reviewBaseInfo.getDistrict()));
        // 评估主体营收
        CorpSubjectItem subjectItem = subjectItemMapper.selectOne(Wrappers.<CorpSubjectItem>lambdaQuery()
                .eq(CorpSubjectItem::getClientId,ratingAmount.getEvaluationSubjectId())
                .eq(CorpSubjectItem::getSubjectCode, "H9170")
                .eq(CorpSubjectItem::getSubjectType, PROFIT.name())
                .orderByDesc(CorpSubjectItem::getYear).orderByDesc(CorpSubjectItem::getQuarter)
                .last("limit 1"));
        Long subjectValue = null;
        if(subjectItem != null){
            subjectValue = subjectItem.getSubjectValue();
        }
        Map<Long, String> clientId2Name = id2NameService.clientId2Name(Stream.of(ratingAmount.getClientId(), ratingAmount.getEvaluationSubjectId()).collect(Collectors.toList()));
        RatingAmountProjInfoRSP rsp = BeanUtil.copyProperties(ratingAmount, RatingAmountProjInfoRSP.class);
        rsp.setClientId(ratingAmount.getClientId())
                .setClientName(clientId2Name.get(ratingAmount.getClientId()))
                .setEvaluationSubjectId(ratingAmount.getEvaluationSubjectId())
                .setEvaluationSubjectName(clientId2Name.get(ratingAmount.getEvaluationSubjectId()))
                .setEvaluationSubjectAreaName(areaName)
                .setLeaseTypes(reviewBaseInfo.getLeaseTypes())
                .setRegionalProjectClassify(reviewBaseInfo.getRegionalProjectClassify())
                .setProjectClassify(reviewBaseInfo.getProjectClassify())
                .setFundsPurpose(reviewBaseInfo.getFundsPurpose())
                .setBizType(reviewBaseInfo.getBizType())
                .setEvaluationSubjectOperatingIncome(subjectValue);
        return rsp;
    }



    public RatingParamInfoDuoApprovalRSP paramInfo(RatingParamInfoREQ req) {
        RatingAmount ratingAmount = this.getById(req.getId());
        if(ratingAmount == null){
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        // 先查询快照
        RatingReport ratingReport = null;
        if(Arrays.asList(ProcessStatus.UNDER_APPROVAL.name(),ProcessStatus.APPROVAL_REJECT.name()).contains(ratingAmount.getProcessStatus())){
            ratingReport = ratingReportMapper.selectById(ratingAmount.getReportId());
        }
        RatingParamInfoDuoApprovalRSP result = ratingSnapshotService.getSnapshotInfoByGroupNameDuoApproval(ratingAmount.getSnapshotId(), ratingReport);
        if(result != null){
            Map<String, RatingParamRSP> snapshotResult = ratingSnapshotService.getSnapshotResult(ratingAmount.getSnapshotId());
            result.setRatingParam(snapshotResult.values());
        }else{
            // 不存在快照 -> 请求决策流引擎，并保存快照
            result = new RatingParamInfoDuoApprovalRSP();
            Map<String, List<RatingParamFieldApprovalRSP>> paramMap = decisionService.paramInfo(ratingAmount.getModelCode());
            if(CollectionUtils.isNotEmpty(paramMap)) {
                Map<String, Map<String, List<RatingParamFieldApprovalRSP>>> info = paramMap.values().stream().flatMap(Collection::stream)
                        .filter(f -> f.getGroupName() != null && f.getGroupName().contains("-"))
                        .collect(Collectors.groupingBy(
                                obj -> obj.getGroupName().split("-")[0], // 根据'-'前的部分分组
                                Collectors.groupingBy(obj -> obj.getGroupName().split("-")[1]) // 在每个子组里根据'-'后的部分再分组
                        ));
                String content = JSON.toJSONString(paramMap.values().stream().flatMap(Collection::stream).collect(Collectors.toList()));
                RatingSnapshot ratingSnapshot = new RatingSnapshot();
                ratingSnapshot.setContent(content);
                ratingSnapshot.setServiceCode(ratingAmount.getModelCode());
                ratingSnapshotService.save(ratingSnapshot);

                result.setExecuteCount(0);
                result.setInfo(info);

                // 关联评级与模型快照
                this.update(Wrappers.<RatingAmount>lambdaUpdate().eq(RatingAmount::getId,req.getId())
                        .set(RatingAmount::getSnapshotId,ratingSnapshot.getId()));
            }
        }
        return result;
    }

    public RatingExecuteRSP execute(RatingExecuteREQ req) {
        RatingAmount ratingAmount = getById(req.getId());
        if(ratingAmount == null){
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        RatingSnapshot ratingSnapshot = ratingSnapshotService.getById(ratingAmount.getSnapshotId());
        if(ratingSnapshot == null){
            throw new MithrasException("模型数据不存在");
        }
        if(!req.isFinishedCall() && ratingSnapshot.getExecuteCount() >= 3){
            throw new MithrasException("已达试算次数上限");
        }
        List<RatingParamFieldRSP> fieldRSPList = JSON.parseArray(ratingSnapshot.getContent(), RatingParamFieldRSP.class);
        List<String> fieldNumberList = fieldRSPList.stream().filter(f -> Objects.equals(f.getDataType(),RatingDataTypeEnum.NUMBER.getExtra())).map(RatingParamFieldRSP::getFieldName).collect(Collectors.toList());
        List<String> fieldNumberRateList = fieldRSPList.stream().filter(f -> Objects.equals(f.getDataType(),RatingDataTypeEnum.NUMBER.getExtra()) && Objects.equals(f.getUnit(),"%")).map(RatingParamFieldRSP::getFieldName).collect(Collectors.toList());
        List<RatingParamREQ> paramList = req.getParam();
        Map<String, Object> param = paramList.stream().filter(f -> Objects.nonNull(f.getFieldValue()) && fieldRSPList.stream().map(RatingParamFieldRSP::getFieldName).collect(Collectors.toList()).contains(f.getFieldName()))
                .peek(paramREQ -> {
                    if(CollectionUtils.isNotEmpty(fieldNumberRateList) && fieldNumberRateList.contains(paramREQ.getFieldName())){
                        BigDecimal bigDecimal = new BigDecimal(paramREQ.getFieldValue().toString()).divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP);
                        paramREQ.setFieldValue(bigDecimal.toString());
                    }else if(CollectionUtils.isNotEmpty(fieldNumberList) && fieldNumberList.contains(paramREQ.getFieldName())){
                        BigDecimal bigDecimal = new BigDecimal(paramREQ.getFieldValue().toString()).multiply(new BigDecimal("10000"));
                        paramREQ.setFieldValue(bigDecimal.toString());
                    }
                }).collect(Collectors.toMap(RatingParamREQ::getFieldName, RatingParamREQ::getFieldValue, (m1, m2) -> m1));
        suppleParam(param,ratingAmount);
        DecisionExecuteResult execute = decisionService.execute(ratingAmount.getModelCode(),param);
        if(execute == null){
            throw new MithrasException("试算不成功");
        }
        if("债项评级模型-政信类".equals(ratingAmount.getModelName())) {
            String groupQuota = getGroupQuota(ratingAmount);
            execute.setGroupQuota(groupQuota);
        }
        // 评级结果留存
        ratingSnapshotService.update(Wrappers.<RatingSnapshot>lambdaUpdate()
                .eq(RatingSnapshot::getId,ratingAmount.getSnapshotId())
                .set(RatingSnapshot::getScore,JSON.toJSONString(execute))
                .set(RatingSnapshot::getExecuteCount,req.isFinishedCall() ? ratingSnapshot.getExecuteCount() : ratingSnapshot.getExecuteCount() + 1));
        RatingExecuteRSP rsp = new RatingExecuteRSP();
        // 评级结果入库
        ratingAmount.setClientQuota(execute.getClientQuota());
        this.updateById(ratingAmount);
        BeanUtil.copyProperties(execute,rsp);

        double projQuota = generateProjQuota(ratingAmount, execute.getClientQuota());
        if(Double.compare(projQuota, 0d) != 0) {
            rsp.setProjQuota(transformWanYuan(projQuota));
        }
        rsp.setExecuteCount(ratingSnapshot.getExecuteCount() + 1);
        return rsp;
    }

    private String getGroupQuota(RatingAmount ratingAmount) {
        CorpCommerceInfo corpCommerceInfo = ccfMapper.selectOne(Wrappers.<CorpCommerceInfo>lambdaQuery().eq(CorpCommerceInfo::getClientId, ratingAmount.getClientId()));
        Map<String, Object> param = new HashMap<>();
        suppleParam(param,ratingAmount);
        if(corpCommerceInfo != null) {
            Long groupClientId;
            // 找到所属集团
            Long belongGroupClientId = corpCommerceInfo.getBelongGroupClientId();
            if (belongGroupClientId == null || belongGroupClientId == -1L) {
                // 评估主体就是集团
                groupClientId = corpCommerceInfo.getClientId();
            } else {
                groupClientId = belongGroupClientId;
            }
            List<RatingAmount> groupRatingAmount = this.list(Wrappers.<RatingAmount>lambdaQuery()
                    .eq(RatingAmount::getClientId, groupClientId).eq(RatingAmount::getRatingStatus, true));
            if(CollectionUtils.isNotEmpty(groupRatingAmount)){
                BigDecimal groupQuota = groupRatingAmount.stream().map(m -> Optional.ofNullable(m.getClientQuota()).map(BigDecimal::new).orElse(BigDecimal.ZERO))
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                param.put("group_quota",groupQuota.toPlainString());
            }
        }
        return decisionService.groupQuota(param);
    }

    private double generateProjQuota(RatingAmount ratingAmount,String clientQuota){
        double otherClientQuota = 0d;
        List<RatingAmount> ratingAmountProj = this.list(Wrappers.<RatingAmount>lambdaQuery()
                .ne(RatingAmount::getEvaluationSubjectId,ratingAmount.getEvaluationSubjectId())
                .eq(RatingAmount::getProjReviewId, ratingAmount.getProjReviewId())
                .eq(RatingAmount::getRatingStatus,true));
        if(CollectionUtils.isNotEmpty(ratingAmountProj)) {
            otherClientQuota = ratingAmountProj.stream().map(RatingAmount::getClientQuota).map(Double::valueOf).mapToDouble(Double::doubleValue).sum();
        }
        return Double.parseDouble(clientQuota) + otherClientQuota;
    }


    /**
     * 补充入参，填充系统字段
     * @param param
     */
    private void suppleParam(Map<String, Object> param,RatingAmount ratingAmount) {
        ProjReviewBaseInfo reviewBaseInfo = projReviewBaseInfoService.getById(ratingAmount.getProjReviewId());

        param.put("area_uni_code",ratingClientService.getClientAreaUniCode(ratingAmount.getEvaluationSubjectId(), null));
        param.put("client_id",ratingAmount.getEvaluationSubjectId());
        param.put("area_in_zhejiang",ProjRegionalClassify.ZHEJIANG.name().equals(reviewBaseInfo.getRegionalProjectClassify()) ? "Y" : "N");
        param.put("materiality_lease",ratingAmount.getMaterialLeaseItem() ? "Y" : "N");

    }

    @Transactional(rollbackFor = Throwable.class)
    public void ratingAmountEffect(RatingAmountEffectREQ req) {
        RatingAmount ratingAmount = this.getById(req.getId());
        if(ratingAmount == null){
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        if(ratingAmount.getClientQuota() == null || ratingAmount.getReportId() == null){
            throw new MithrasException("未完成评级,不可提交");
        }

        ratingAmount.setEffectTime(LocalDate.now());
        ratingAmount.setRatingStatus(true);
        ratingAmount.setProcessStatus(ProcessStatus.APPROVAL_PASS.name());
        // 将原版本设置为失效
        update(Wrappers.<RatingAmount>lambdaUpdate().eq(RatingAmount::getEvaluationSubjectId,ratingAmount.getEvaluationSubjectId())
                .eq(RatingAmount::getProjReviewId, ratingAmount.getProjReviewId())
                .eq(RatingAmount::getRatingStatus,true)
                .set(RatingAmount::getRatingStatus,false)
                .set(RatingAmount::getAbandonTime,LocalDate.now()));
        this.updateById(ratingAmount);

        RatingAmountLib ratingAmountLib = buildLib(ratingAmount);
        libService.save(ratingAmountLib);

    }

    private RatingAmountLib buildLib(RatingAmount ratingAmount) {
        RatingAmountLib ratingAmountLib = BeanUtil.copyProperties(ratingAmount, RatingAmountLib.class);
        ratingAmountLib.setId(null);
        ratingAmountLib.setOriginId(ratingAmount.getId());
        ratingAmountLib.setDataCreateBy(ratingAmount.getCreateBy());
        ratingAmountLib.setDataCreateTime(ratingAmount.getCreateTime());
        ratingAmountLib.setDataUpdateBy(ratingAmount.getUpdateBy());
        ratingAmountLib.setDataUpdateTime(ratingAmount.getUpdateTime());
        ratingAmountLib.setVersionType(VersionTypeConstants.NORMAL);
        ratingAmountLib.setVersion(VersionUtil.generateVersion(null));
        return ratingAmountLib;
    }

//    public void ratingAmountEffect(RatingAmountEffectREQ req) {
//        RatingAmount ratingAmount = this.getById(req.getId());
//        if(ratingAmount == null){
//            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
//        }
//        if(ratingAmount.getClientQuota() == null || ratingAmount.getReportId() == null){
//            throw new MithrasException("未完成评级,不可提交流程");
//        }
//        ProcessResp relatedProcess = findRelatedProcess(req.getId());
//        if(relatedProcess != null){
//            throw new MithrasException("数据已在流程中，无法重复提交");
//        }
//
//        StartProcessReq startProcessReq = new StartProcessReq();
//        int count = this.count(Wrappers.<RatingAmount>lambdaQuery().eq(RatingAmount::getProjReviewId, ratingAmount.getProjReviewId())
//                .eq(RatingAmount::getEvaluationSubjectId,ratingAmount.getEvaluationSubjectId()).eq(RatingAmount::getRatingStatus, true));
//        startProcessReq.setModelKey(count > 0 ? ProcessModelTypeEnum.RatingAmountUpdateFlow.name() : ProcessModelTypeEnum.RatingAmountCreateFlow.name());
//        startProcessReq.setProcessInstanceName(ratingAmount.getProjName());
//        Map<String, Object> varMap = new HashMap<>();        OrgDO userDept = sysUserService.getUserDept();
//        if (Objects.isNull(userDept)) {
//            throw new MithrasException("当前用户部门为空");
//        }
//        List<UserDO> bizDeptLeaderUserList = sysUserService.listSpecificOrgJobUser(userDept.getId(), JobEnum.businesshead.name());
//        List<UserDO> divisionLeaderUserList = sysUserService.listSpecificOrgJobUser(userDept.getId(), JobEnum.leaderincharge.name());
//        varMap.put("deptLeader",bizDeptLeaderUserList.stream().map(UserDO::getId).map(String::valueOf).collect(Collectors.toList()));
//        varMap.put("divisionLeader",divisionLeaderUserList.stream().map(UserDO::getId).map(String::valueOf).collect(Collectors.toList()));
//        varMap.put("riskControlManager",sysUserService.getAllRiskControlManagerIds().stream().map(String::valueOf).collect(Collectors.toList()));
//        startProcessReq.setVariables(varMap);
//        startProcessReq.setBusinessKey(String.valueOf(ratingAmount.getId()));
//        startProcessReq.setStartUserId(String.valueOf(ratingAmount.getCreateBy()));
//        String processInstanceId = processApiService.start(startProcessReq);
//        bizProcessDataService.recordBizData(processInstanceId, ratingAmount.getClientId());
//
//        RatingAmountProjInfoRSP rsp = ratingAmountProjInfo(new RatingAmountProjInfoREQ(ratingAmount.getId()));
//        RARelationProjInfoRSP raRelationProjInfoRSP = BeanUtil.copyProperties(rsp, RARelationProjInfoRSP.class);
//        String projInfo = Optional.ofNullable(raRelationProjInfoRSP).map(JSON::toJSONString).orElse(null);
//        this.update(Wrappers.<RatingAmount>lambdaUpdate().eq(RatingAmount::getId,req.getId())
//                .set(RatingAmount::getProcessStatus,ProcessStatus.UNDER_APPROVAL.name())
//                .set(projInfo != null,RatingAmount::getRelationProjInfo,projInfo));
//    }


    public ProcessResp findRelatedProcess(Long ratingClientId) {
        ProcessPageReq processPageReq = new ProcessPageReq();
        processPageReq.setPageIndex(1);
        processPageReq.setPageSize(1);
        processPageReq.setBusinessKey(String.valueOf(ratingClientId));
        processPageReq.setModelKeyList(BusinessModuleEnum.RATING_AMOUNT.getModelKeyList());
        processPageReq.setProcessStatusList(Arrays.asList(ProcessBusinessStatusEnum.RUNNING.getType(), ProcessBusinessStatusEnum.SUSPEND.getType()));
        cn.zswltech.flow.core.util.Page<ProcessResp> processRespPage = taskApiService.queryProcess(processPageReq);
        return processRespPage.getContents().stream().findFirst().orElse(null);
    }

    public RatingAmountReportRSP amountReport(RatingAmountReportREQ req) {
        RatingAmount ratingAmount = this.getById(req.getId());
        if(ratingAmount == null){
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        RatingAmountReportRSP rsp = new RatingAmountReportRSP();
        // 指标信息
        RatingReport ratingReport = ratingReportMapper.selectById(ratingAmount.getReportId());
        if(ratingReport != null){

            Map<String, List<RatingEvaluateBaseRSP>> evaluateBaseList = JSON.parseObject(ratingReport.getEvaluateBase(), new TypeReference<Map<String, List<RatingEvaluateBaseRSP>>>() {});
            Map<String, List<RatingCreditMeasureRSP>> measuresListMap = JSON.parseObject(ratingReport.getCreditMeasure(), new TypeReference<Map<String, List<RatingCreditMeasureRSP>>>() {});

            // 流程为退回后重新发起：补充比较字段
            if(ProcessStatus.UNDER_APPROVAL.name().equals(ratingAmount.getProcessStatus())) {
                ratingSnapshotService.compareReport(ratingAmount.getSnapshotId(), evaluateBaseList, measuresListMap);
            }
            // 补充各个指标的审批意见
            if(ratingReport.getApprovalInfo() != null){
                List<RatingReportApprovalRSP.RatingApprovalRSP> approvalInfoList = JSON.parseArray(ratingReport.getApprovalInfo(), RatingReportApprovalRSP.RatingApprovalRSP.class);
                Map<String, RatingReportApprovalRSP.RatingApprovalRSP> approvalInfoMap = approvalInfoList.stream().collect(Collectors.toMap(RatingReportApprovalRSP.RatingApprovalRSP::getFieldName, Function.identity(),(m1, m2)->m2));
                evaluateBaseList.forEach((k,v) -> {
                    for (RatingEvaluateBaseRSP evaluateBaseRSP : v) {
                        RatingReportApprovalRSP.RatingApprovalRSP approvalInfo = approvalInfoMap.getOrDefault(evaluateBaseRSP.getFieldName(),new RatingReportApprovalRSP.RatingApprovalRSP());
                        evaluateBaseRSP.setApprovalStatus(approvalInfo.getApprovalStatus());
                        evaluateBaseRSP.setApprovalOpinion(approvalInfo.getApprovalOpinion());
                    }
                });

                measuresListMap.forEach((k,v) -> {
                    for (RatingCreditMeasureRSP creditMeasureRSP : v) {
                        RatingReportApprovalRSP.RatingApprovalRSP approvalInfo = approvalInfoMap.getOrDefault(creditMeasureRSP.getFieldName(),new RatingReportApprovalRSP.RatingApprovalRSP());
                        creditMeasureRSP.setApprovalStatus(approvalInfo.getApprovalStatus());
                        creditMeasureRSP.setApprovalOpinion(approvalInfo.getApprovalOpinion());
                    }
                });

            }

            rsp.setEvaluateBaseList(evaluateBaseList);
            rsp.setCreditMeasureListMap(measuresListMap);

        }

        // 成绩
        RatingSnapshot ratingSnapshot = ratingSnapshotService.getById(ratingAmount.getSnapshotId());

        if(ratingSnapshot != null && ratingSnapshot.getScore() != null) {
            DecisionExecuteResult executeResult = JSON.parseObject(ratingSnapshot.getScore(), DecisionExecuteResult.class);
            // 客户限额
            String clientQuota = Optional.ofNullable(executeResult.getClientQuota()).orElse("0");
            // 集团限额
            String groupQuota = Optional.ofNullable(executeResult.getGroupQuota()).orElse("0");
            // 项目限额
            String projQuota = String.valueOf(generateProjQuota(ratingAmount, clientQuota));
            String clientQuotaWanYuan = transformWanYuan(clientQuota);
            String projQuotaWanYuan = transformWanYuan(projQuota);

            RatingAmountQuotaRSP ratingAmountQuotaRSP = new RatingAmountQuotaRSP();
            if(ratingAmount.getModelName().contains(RatingModelTypeEnum.POLITICE_CREDIT.display())){
                ratingAmountQuotaRSP.setModelType(RatingModelTypeEnum.POLITICE_CREDIT.name());
                CorpCommerceInfo corpCommerceInfo = ccfMapper.selectOne(Wrappers.<CorpCommerceInfo>lambdaQuery().eq(CorpCommerceInfo::getClientId, ratingAmount.getClientId()));
                Long groupClientId;
                if(corpCommerceInfo != null) {
                    // 找到所属集团
                    Long belongGroupClientId = corpCommerceInfo.getBelongGroupClientId();
                    if (belongGroupClientId == null || belongGroupClientId == -1L) {
                        // 评估主体就是集团
                        groupClientId = corpCommerceInfo.getClientId();
                    } else {
                        groupClientId = belongGroupClientId;
                    }
                    List<CorpCommerceInfo> corpCommerceInfos = ccfMapper.selectList(Wrappers.<CorpCommerceInfo>lambdaQuery().eq(CorpCommerceInfo::getBelongGroupClientId, groupClientId)
                            .or().eq(CorpCommerceInfo::getClientId,groupClientId));
                    if(CollectionUtils.isNotEmpty(corpCommerceInfos)) {
                        List<RatingAmount> ratingAmountList = this.list(Wrappers.<RatingAmount>lambdaQuery().eq(RatingAmount::getEvaluationSubjectId, corpCommerceInfos.stream().map(CorpCommerceInfo::getClientId).collect(Collectors.toList()))
                                .ne(RatingAmount::getEvaluationSubjectId,ratingAmount.getEvaluationSubjectId())
                                .eq(RatingAmount::getRatingStatus, true));
                        double groupSum = ratingAmountList.stream().map(RatingAmount::getClientQuota).map(Double::valueOf).mapToDouble(Double::doubleValue).sum();
                        groupSum += Double.parseDouble(clientQuota);
                        // 集团剩余可用额度
                        String groupSurplusQuota = String.valueOf(Double.parseDouble(groupQuota) - groupSum);
                        ratingAmountQuotaRSP.setGroupSurplusQuota(transformWanYuan(groupSurplusQuota));
                    }
                }
                String groupQuotaWanYuan = transformWanYuan(groupQuota);
                ratingAmountQuotaRSP.setGroupQuota(groupQuotaWanYuan);
                ratingAmountQuotaRSP.setClientQuota(clientQuotaWanYuan);
                ratingAmountQuotaRSP.setProjQuota(projQuotaWanYuan);

            }else if(ratingAmount.getModelName().contains(RatingModelTypeEnum.PROJ.display())){
                ratingAmountQuotaRSP.setModelType(RatingModelTypeEnum.PROJ.name());

                ratingAmountQuotaRSP.setClientQuota(clientQuotaWanYuan);
                ratingAmountQuotaRSP.setProjQuota(projQuotaWanYuan);
            }else{
                ratingAmountQuotaRSP.setModelType(RatingModelTypeEnum.NORMAL.name());
                ratingAmountQuotaRSP.setEvaluationSubjectQuota(clientQuotaWanYuan);
                ratingAmountQuotaRSP.setProjQuota(projQuotaWanYuan);
            }
            if(ratingSnapshot.getResult() != null){
                List<RatingParamRSP> ratingParamList = JSON.parseArray(ratingSnapshot.getResult(), RatingParamRSP.class);
                for (RatingParamRSP ratingParamRSP : ratingParamList) {
                    if("value_of_leased_property".equals(ratingParamRSP.getFieldName())){
                        ratingAmountQuotaRSP.setLeaseItemPrice(Optional.ofNullable(ratingParamRSP.getFieldValue()).map(String::valueOf).orElse(null));
                        break;
                    }
                }
            }
            ratingAmountQuotaRSP.setRatingAdjustFactor(executeResult.getRatingAdjustFactor());
            ratingAmountQuotaRSP.setCreditMeasurePrice(transformWanYuan(executeResult.getCreditMeasurePrice()));
            rsp.setQuota(ratingAmountQuotaRSP);
        }
        // 历史评级信息
        RatingAmount history = this.getOne(Wrappers.<RatingAmount>lambdaQuery().eq(RatingAmount::getProjReviewId, ratingAmount.getProjReviewId())
                .eq(RatingAmount::getEvaluationSubjectId,ratingAmount.getEvaluationSubjectId())
                .isNotNull(RatingAmount::getEffectTime)
                .ne(RatingAmount::getId,ratingAmount.getId())
                .orderByDesc(RatingAmount::getEffectTime)
                .last("limit 1"));
//        RatingAmountDetailLibRSP lib = libService.detail(req.getId());
        if(history != null){
            RatingAmountDetailLibRSP historyRSP = BeanUtil.copyProperties(history, RatingAmountDetailLibRSP.class);
            historyRSP.setCreateByName(id2NameService.sysUserId2NameSingle(historyRSP.getCreateBy()));
            historyRSP.setProjQuota(transformWanYuan(generateProjQuota(history,history.getClientQuota())));
            rsp.setHistoryInfo(historyRSP);
        }

        RatingClient ratingClient = ratingClientService.getOne(Wrappers.<RatingClient>lambdaQuery()
                .eq(RatingClient::getClientId, ratingAmount.getEvaluationSubjectId()).eq(RatingClient::getRatingStatus, true));
        Client client = clientService.getById(ratingAmount.getEvaluationSubjectId());
        if(ratingClient != null && client != null) {
            RatingAmountClientScoreRSP clientScoreRSP = new RatingAmountClientScoreRSP();
            clientScoreRSP.setFinalScore(ratingClient.getFinalScore());
            clientScoreRSP.setEvaluationSubjectName(client.getClientName());
            clientScoreRSP.setEvaluationSubjectUscCode(client.getUscCode());
            rsp.setScoreRSP(clientScoreRSP);
        }
        return rsp;
    }

    private String transformWanYuan(String projQuota) {
        if("0".equals(projQuota)){
            return "0";
        }
        BigDecimal bigDecimal = new BigDecimal(projQuota).divide(new BigDecimal("10000")).setScale(2, RoundingMode.HALF_UP);
        return bigDecimal.toString();
    }

    private String transformWanYuan(double projQuota) {
        if(Double.compare(projQuota, 0d) == 0){
            return "0";
        }
        BigDecimal bigDecimal = new BigDecimal(String.valueOf(projQuota)).divide(new BigDecimal("10000")).setScale(2, RoundingMode.HALF_UP);
        return bigDecimal.toString();
    }

    @Transactional(rollbackFor = Throwable.class)
    public void amountFinish(RatingAmountFinishREQ req) {
        RatingAmount ratingAmount = this.getById(req.getId());
        if(ratingAmount == null){
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        if(ProcessStatus.APPROVAL_PASS.name().equals(ratingAmount.getProcessStatus())){
            throw new MithrasException("不能修改已完成的数据");
        }
        // 完成评级时再试算一次，避免用户修改了数据
        if(CollectionUtils.isNotEmpty(req.getParam())) {
            RatingExecuteREQ executeREQ = new RatingExecuteREQ();
            executeREQ.setId(req.getId());
            executeREQ.setFinishedCall(true);
            executeREQ.setParam(BeanUtil.copyToList(req.getParam(), RatingParamREQ.class));
            execute(executeREQ);
        }

        // 用户答题记录表单
        if(!ProcessStatus.UNDER_APPROVAL.name().equals(ratingAmount.getProcessStatus())) { // 若状态为流程中的更新需要保留原来的快照
            ratingSnapshotService.update(Wrappers.<RatingSnapshot>lambdaUpdate()
                    .eq(RatingSnapshot::getId, ratingAmount.getSnapshotId())
                    .set(RatingSnapshot::getResult, JSON.toJSONString(req.getParam())));
        }
        this.update(Wrappers.<RatingAmount>lambdaUpdate().eq(RatingAmount::getId,req.getId())
                .set(RatingAmount::getIsRealEstateAdjust, req.getIsRealEstateAdjust())
                .set(RatingAmount::getIsStockRightsAdjust, req.getIsStockRightsAdjust()));
        if(req.isOperationType()){
            // 完成评级 -> 生成评级报告
            RatingSnapshotDetailDuoRSP snapshotDuoDetail = ratingSnapshotService.getSnapshotDuoDetail(ratingAmount.getSnapshotId());
//            RatingParamInfoDuoRSP ratingParamInfoRSP = ratingSnapshotService.getSnapshotInfoByGroupNameDuo(ratingAmount.getSnapshotId());
            Map<String, Map<String,List<RatingParamFieldRSP>>> snapshotInfo = snapshotDuoDetail.getContentRsp();
//            if(ratingParamInfoRSP != null){
//                snapshotInfo = ratingParamInfoRSP.getInfo();
//            }
            if(snapshotDuoDetail.getScoreRsp() == null){
                throw new MithrasException("未试算不能完成评级");
            }
            Map<String, RatingParamRSP> resultMap;
            if(!ProcessStatus.UNDER_APPROVAL.name().equals(ratingAmount.getProcessStatus())) {
                resultMap = snapshotDuoDetail.getResultRsp();
//                resultMap = ratingSnapshotService.getSnapshotResult(ratingAmount.getSnapshotId());
            }else{
                resultMap = req.getParam().stream().collect(Collectors.toMap(RatingParamRSP::getFieldName, Function.identity(),(m1, m2)->m2));
            }
            Map<String,List<RatingEvaluateBaseRSP>> evaluateBaseList = new HashMap<>();
            Map<String,List<RatingCreditMeasureRSP>> creditMeasureListMap = new HashMap<>();
            Map<String, List<RatingParamFieldRSP>> evaluateBaseMap = snapshotInfo.get("评估基准");
            Map<String, List<RatingParamFieldRSP>> creditMeasureMap = snapshotInfo.get("增信措施");
            // 评估基准
            if(CollectionUtils.isNotEmpty(evaluateBaseMap)) {
                evaluateBaseMap.forEach((key, value) -> {
                    List<RatingEvaluateBaseRSP> evaluateBaseRSPList = value.stream().map(param -> {
                        RatingEvaluateBaseRSP evaluateBaseRSP = new RatingEvaluateBaseRSP();
                        evaluateBaseRSP.setFieldName(param.getFieldName());
                        evaluateBaseRSP.setDataType(param.getDataType());
                        evaluateBaseRSP.setEnumList(param.getEnumList());
                        evaluateBaseRSP.setGroupName(param.getGroupName());
                        evaluateBaseRSP.setValue(Optional.ofNullable(resultMap.get(param.getFieldName())).map(RatingParamRSP::getFieldValue).orElse(""));
                        evaluateBaseRSP.setFieldComment(param.getFieldComment());
                        return evaluateBaseRSP;
                    }).collect(Collectors.toList());
                    evaluateBaseList.put(key, evaluateBaseRSPList);
                });
            }
            // 增信措施
            if(CollectionUtils.isNotEmpty(creditMeasureMap)) {
                creditMeasureMap.forEach((key, value) -> {
                    List<RatingCreditMeasureRSP> creditMeasureRSPList = value.stream().map(param -> {
                        RatingCreditMeasureRSP creditMeasureRSP = new RatingCreditMeasureRSP();
                        creditMeasureRSP.setFieldName(param.getFieldName());
                        creditMeasureRSP.setGroupName(param.getGroupName());
                        creditMeasureRSP.setDataType(param.getDataType());
                        creditMeasureRSP.setEnumList(param.getEnumList());
                        creditMeasureRSP.setValue(Optional.ofNullable(resultMap.get(param.getFieldName())).map(RatingParamRSP::getFieldValue).orElse(""));
                        creditMeasureRSP.setFieldComment(param.getFieldComment());
                        return creditMeasureRSP;
                    }).collect(Collectors.toList());
                    creditMeasureListMap.put(key, creditMeasureRSPList);
                });
            }
            RatingReport ratingReport = new RatingReport();
            if(CollectionUtils.isNotEmpty(evaluateBaseList)) {
                ratingReport.setEvaluateBase(JSON.toJSONString(evaluateBaseList));
            }
            if(CollectionUtils.isNotEmpty(creditMeasureListMap)) {
                ratingReport.setCreditMeasure(JSON.toJSONString(creditMeasureListMap));
            }
            ratingReport.setServiceCode(ratingAmount.getModelCode());
            if(ratingAmount.getReportId() == null) {
                ratingReportMapper.insert(ratingReport);
            }else{
                ratingReport.setId(ratingAmount.getReportId());
                ratingReportMapper.updateById(ratingReport);
            }

            // 关联评级主表与评级报告
            this.update(Wrappers.<RatingAmount>lambdaUpdate().eq(RatingAmount::getId,req.getId())
                    .set(RatingAmount::getReportId,ratingReport.getId()));
        }
//        else{
//            if(ProcessStatus.UN_SUBMIT.name().equals(ratingAmount.getProcessStatus())){
//                return;
//            }
//            Long belongSponsorUserId = ratingAmount.getBelongSponsorUserId();
//            if(belongSponsorUserId == null){
//                throw new MithrasException("主办用户不存在，保存草稿失败");
//            }
//            String clientName = id2NameService.clientId2NameSingle(ratingAmount.getClientId());
//            int count = this.count(Wrappers.<RatingAmount>lambdaQuery().eq(RatingAmount::getProjReviewId, ratingAmount.getProjReviewId())
//                    .eq(RatingAmount::getEvaluationSubjectId,ratingAmount.getEvaluationSubjectId()).eq(RatingAmount::getRatingStatus, true));
//
//            CommonProcessPrepare prepare = CommonProcessPrepare.builder()
//                    .processType(count > 0 ? ProcessModelTypeEnum.RatingAmountUpdateFlow.name() : ProcessModelTypeEnum.RatingAmountCreateFlow.name())
//                    .businessId(String.valueOf(ratingAmount.getId()))
//                    .formName(clientName)
//                    .projName(null)
//                    .clientName(clientName)
//                    .currentAssignee(JSON.toJSONString(Collections.singletonList(ratingAmount.getBelongSponsorUserId())))
//                    .currentNode("项目经理确认")
//                    .applyTime(LocalDateTime.now())
//                    .status(CommonProcessPrepareStatus.PEND_COMMIT.name())
//                    .build();
//            commonProcessPrepareMapper.insert(prepare);
//
//            update(Wrappers.<RatingAmount>lambdaUpdate().eq(RatingAmount::getId,ratingAmount.getId())
//                    .set(RatingAmount::getProcessStatus,ProcessStatus.UN_SUBMIT.name()));
//        }

    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void processEnd(Long id, Integer endType, Long startUserId, String processInstanceId, String modelKey) {
//        RatingAmount ratingAmount = this.getById(id);
//        if(ratingAmount == null){
//            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
//        }
//        boolean processPass = ProcessBusinessStatusEnum.success(endType);
//        if(processPass){
//            ratingAmount.setEffectTime(LocalDate.now());
//            ratingAmount.setRatingStatus(true);
//            ratingAmount.setProcessStatus(ProcessStatus.APPROVAL_PASS.name());
//            // 将原版本设置为失效
//            update(Wrappers.<RatingAmount>lambdaUpdate().eq(RatingAmount::getEvaluationSubjectId,ratingAmount.getEvaluationSubjectId())
//                    .eq(RatingAmount::getProjReviewId, ratingAmount.getProjReviewId())
//                    .eq(RatingAmount::getRatingStatus,true)
//                    .set(RatingAmount::getRatingStatus,false)
//                    .set(RatingAmount::getAbandonTime,LocalDate.now()));
//            this.updateById(ratingAmount);
//            versionService.recordVersion(id, VersionTypeEnum.APPROVAL, startUserId, processInstanceId, VersionTypeConstants.NORMAL);
//        }else{
//            // 重置试算次数
//            ratingSnapshotService.update(Wrappers.<RatingSnapshot>lambdaUpdate()
//                    .eq(RatingSnapshot::getId, ratingAmount.getSnapshotId())
//                    .set(RatingSnapshot::getExecuteCount, 0));
//
//            ratingAmount.setProcessStatus(ProcessStatus.APPROVAL_REJECT.name());
//            this.updateById(ratingAmount);
//            versionService.recordVersion(id, VersionTypeEnum.APPROVAL, startUserId, processInstanceId, VersionTypeConstants.INVALID);
//            versionService.reset(id);
//        }


    }



    private void notNullValid(Object obj) {
        boolean flag = true;
        Class<?> clazz = obj.getClass();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true); // 允许访问私有字段
            try {
                Object value = field.get(obj);
                if (value == null) {
                    flag = false;// 只要有一个字段为空，就返回false
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException();
            }
        }
        if(!flag){
            throw new MithrasException("项目基本信息不完整，请维护基本信息后再发起评级");
        }
    }

    public boolean areEqual(Object obj1, Object obj2){
        if (obj1 == obj2) {
            return true;
        }
        if (obj1 == null || obj2 == null || obj1.getClass() != obj2.getClass()) {
            return false;
        }
        Field[] fields = obj1.getClass().getDeclaredFields();
        return Arrays.stream(fields)
                .allMatch(field -> {
                    field.setAccessible(true);
                    try {
                        return Objects.equals(field.get(obj1), field.get(obj2));
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                });
    }



    public RatingAmountModelQueryRSP ratingAmountModelQuery(RatingAmountModelQueryREQ req) {
        DecisionModelREQ modelREQ = new DecisionModelREQ();
        if(req.getProjSystem()){
            modelREQ.setName("债项评级模型-项目制");
        }else{
            int count = ratingClientService.count(Wrappers.<RatingClient>lambdaQuery()
                    .in(RatingClient::getModelName, Arrays.asList("政信主体区县级模型","政信主体地级市模型"))
                    .eq(RatingClient::getClientId, req.getEvaluationSubjectId())
                    .eq(RatingClient::getRatingStatus, true));
            if(count > 0) {
                modelREQ.setName("债项评级模型-政信类");
            }else{
                modelREQ.setName("债项评级模型-通用类");
            }
        }
        List<DecisionModelRSP> decisionModelRSPS = decisionService.modelQuery(modelREQ);
        if(CollectionUtils.isNotEmpty(decisionModelRSPS)){
            DecisionModelRSP decisionModelRSP = decisionModelRSPS.get(0);
            return BeanUtil.copyProperties(decisionModelRSP, RatingAmountModelQueryRSP.class);
        }
        return null;
    }


    public RatingAmountUpdateRSP ratingAmountUpdate(RatingAmountUpdateREQ req) {
        RatingAmount ratingAmount = this.getById(req.getId());
        if(ratingAmount == null){
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        if(!Objects.equals(AccountUtil.getLoginInfo().getId(), ratingAmount.getCreateBy())){
            throw new MithrasException("只允许创建人进行编辑操作");
        }
        if(ratingAmount.getRatingStatus()){
            throw new MithrasException("不能修改生效的评级数据");
        }
//        if(findRelatedProcess(req.getId()) != null){
//            throw new MithrasException("不能修改处于流程中的评级数据");
//        }
        Client client = clientService.getById(req.getEvaluationSubjectId());
        if(client == null){
            throw new MithrasException("该客户不存在");
        }
        RatingAmount ratingAmountNew = BeanUtil.copyProperties(req, RatingAmount.class);
        ratingAmountNew.setModelName(req.getName());
        ratingAmountNew.setModelCode(req.getCode());
        ratingAmountNew.setEvaluationSubjectUscc(client.getUscCode());
        ratingAmountMapper.updateAnnotationIncludeNullById(ratingAmountNew);
        return new RatingAmountUpdateRSP(ratingAmountNew.getId());
    }


    public void ratingAmountDelete(RatingAmountDeleteREQ req) {
        RatingAmount ratingAmount = this.getById(req.getId());
        if(ratingAmount == null){
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        if(!Objects.equals(AccountUtil.getLoginInfo().getId(), ratingAmount.getCreateBy())){
            throw new MithrasException("只允许创建人进行删除操作");
        }
        if(ratingAmount.getRatingStatus()){
            throw new MithrasException("不能删除生效的评级数据");
        }
        if(ratingAmount.getProcessStatus() != null && !ProcessStatus.UN_SUBMIT.name().equals(ratingAmount.getProcessStatus())){
            throw new MithrasException("只能删除草稿数据");
        }
        update(Wrappers.<RatingAmount>lambdaUpdate().eq(RatingAmount::getId,req.getId())
                .set(RatingAmount::getDeleted,true));
    }

    public RatingAmountProjDetailRSP getEffectRating(Integer day, Long projReviewId,List<Long> userList, Date startTime) {
        LocalDate endDate = null;
        if(day != null) {
            endDate = LocalDate.now().minusDays(day);
        }
        RatingAmountLib ratingAmountLib = libService.getOne(Wrappers.<RatingAmountLib>lambdaQuery()
                .eq(RatingAmountLib::getProjReviewId,projReviewId)
                .ge(endDate != null, RatingAmountLib::getCreateTime, endDate)
                .in(CollectionUtils.isNotEmpty(userList), RatingAmountLib::getCreateBy, userList)
                .eq(RatingAmountLib::getRatingStatus, true)
                .le(Objects.nonNull(startTime),RatingAmountLib::getCreateTime,startTime)
                .orderByDesc(RatingAmountLib::getCreateTime)
                .last("limit 1"));
        RatingAmountProjDetailRSP amountRSP = new RatingAmountProjDetailRSP();
        if(ratingAmountLib != null) {
            RatingAmount ratingAmount = this.getById(ratingAmountLib.getOriginId());
            double projQuota = generateProjQuota(ratingAmount, ratingAmountLib.getClientQuota());
            String quotaWanYuan = transformWanYuan(projQuota);
            amountRSP.setId(ratingAmountLib.getOriginId());
            amountRSP.setRatingQuota(quotaWanYuan);
        }
        return amountRSP;

    }

    /**
     * 1.校验项目评估主体是否存在客户评级和债项评级
     * 2.校验评估主体是否更新或修改了 主承租人，租赁类型，评估主体名称，评估主体营业收入，评估主体区域，地区分类，行业分类，资金用途字段
     * @param evaluationSubjectId
     */
    public void effectProjCheck(Long evaluationSubjectId, Long clientId,Long projReviewId) {
//        int ratingClientEvaCount = ratingClientService.count(Wrappers.<RatingClient>lambdaQuery()
//                .eq(RatingClient::getClientId, evaluationSubjectId)
//                .eq(RatingClient::getRatingStatus, true));
//
//        int ratingClientMainCount = ratingClientService.count(Wrappers.<RatingClient>lambdaQuery()
//                .eq(RatingClient::getClientId, clientId)
//                .eq(RatingClient::getRatingStatus, true));

        RatingAmount ratingAmount = this.getOne(Wrappers.<RatingAmount>lambdaQuery()
                .eq(RatingAmount::getEvaluationSubjectId, evaluationSubjectId)
                .eq(RatingAmount::getProjReviewId, projReviewId)
                .eq(RatingAmount::getRatingStatus, true));

        int ratingAmountMainCount = this.count(Wrappers.<RatingAmount>lambdaQuery()
                .eq(RatingAmount::getProjReviewId, projReviewId)
                .eq(RatingAmount::getRatingStatus, true));


        RatingClientProjDetailRSP rsp1 = ratingClientService.getEffectRating(90, evaluationSubjectId, null, new Date());
        if (Objects.isNull(rsp1) || StrUtil.isBlank(rsp1.getRatingFinalScore())) {
            throw new MithrasException("无90天内有效的评估主体客户评级信息，请完成或更新评级后再提交流程！");
        }
        RatingClientProjDetailRSP rsp2 = ratingClientService.getEffectRating(90, clientId, null, new Date());
        if (Objects.isNull(rsp2) || StrUtil.isBlank(rsp2.getRatingFinalScore())) {
            throw new MithrasException("无90天内有效的主承租人客户评级信息，请完成或更新评级后再提交流程！");
        }
        if (ratingAmountMainCount == 0) {
            throw new MithrasException("无90天内有效的债项评级信息，请完成或更新评级后再提交流程！");
        }

//        if (ratingClientEvaCount == 0 || ratingClientMainCount == 0 || ratingAmountMainCount == 0) {
//            throw new MithrasException("无90天内有效的主承租人/评估主体客户评级信息或有效的债项评级信息，请完成或更新评级后再提交流程！");
//        }

//        LocalDate effectTime = ratingAmount.getEffectTime();
//        // 查找评审生效时间在债项生效之前的数据
//        ProjReviewBaseInfoLib effectBefore = projReviewBaseInfoLibService.getOne(Wrappers.<ProjReviewBaseInfoLib>lambdaQuery()
//                .eq(ProjReviewBaseInfoLib::getOriginId, projReviewId)
//                .le(ProjReviewBaseInfoLib::getCreateTime, effectTime)
//                .orderByDesc(ProjReviewBaseInfoLib::getVersion)
//                .last("limit 1"));
//        // 查找最新的评审数据
//        ProjReviewBaseInfoLib effectAfter = projReviewBaseInfoLibService.getOne(Wrappers.<ProjReviewBaseInfoLib>lambdaQuery()
//                .eq(ProjReviewBaseInfoLib::getOriginId, projReviewId)
//                .orderByDesc(ProjReviewBaseInfoLib::getVersion)
//                .last("limit 1"));

        if(ratingAmount != null && ratingAmount.getRelationProjInfo() != null) {
            RARelationProjInfoRSP projInfoBefore = JSON.parseObject(ratingAmount.getRelationProjInfo(), RARelationProjInfoRSP.class);

            ProjReviewBaseInfo projReview = projReviewBaseInfoService.getById(projReviewId);
            RARelationProjInfoRSP projInfoNow = BeanUtil.copyProperties(projReview, RARelationProjInfoRSP.class);
            CorpSubjectItem subjectItemAfter = subjectItemMapper.selectOne(Wrappers.<CorpSubjectItem>lambdaQuery()
                    .eq(CorpSubjectItem::getClientId,ratingAmount.getEvaluationSubjectId())
                    .eq(CorpSubjectItem::getSubjectCode, "H9170")
                    .eq(CorpSubjectItem::getSubjectType, PROFIT.name())
                    .orderByDesc(CorpSubjectItem::getYear).orderByDesc(CorpSubjectItem::getQuarter)
                    .last("limit 1"));
            // 地区
            if(projReview.getProvince() == null || projReview.getCity() == null || projReview.getDistrict() == null){
                throw new MithrasException("请先维护评估主体地址信息");
            }
            List<String> codeList = Stream.of(projReview.getDistrict(), projReview.getCity(), projReview.getProvince()).collect(Collectors.toList());
            Map<String, String> nameMap = addressDictionaryMapper.selectList(Wrappers.<AddressDictionary>lambdaQuery()
                            .in(CollectionUtils.isNotEmpty(codeList),AddressDictionary::getCode, codeList))
                    .stream().collect(Collectors.toMap(AddressDictionary::getCode, AddressDictionary::getDisplay,(m1,m2)->m1));
            String areaName = String.format("%s%s%s",nameMap.get(projReview.getProvince()),nameMap.get(projReview.getCity()),nameMap.get(projReview.getDistrict()));
            projInfoNow.setEvaluationSubjectAreaName(areaName);
            projInfoNow.setEvaluationSubjectOperatingIncome(Optional.ofNullable(subjectItemAfter).map(CorpSubjectItem::getSubjectValue).orElse(null));
            projInfoNow.setEvaluationSubjectName(id2NameService.clientId2NameSingle(projInfoNow.getEvaluationSubjectId()));

            if(!areEqual(projInfoBefore,projInfoNow)){
                throw new MithrasException("项目重要信息变更，请更新债项评级！");
            }
        }

//        if (effectAfter != null && effectBefore != null) {
//            String evaluationSubjectName = id2NameService.clientId2NameSingle(effectBefore.getEvaluationSubjectId());
//            if (Objects.equals(effectBefore.getLeaseTypes(), effectAfter.getLeaseTypes()) &&
//                    Objects.equals(effectBefore.getEvaluationSubjectId(), effectAfter.getEvaluationSubjectId()) &&
//                    Objects.equals(effectBefore.getClientId(), effectAfter.getClientId()) &&
//                    Objects.equals(effectBefore.getProjectClassify(), effectAfter.getProjectClassify()) &&
//                    Objects.equals(effectBefore.getRegionalProjectClassify(), effectAfter.getRegionalProjectClassify()) &&
//                    Objects.equals(effectBefore.getFundsPurpose(), effectAfter.getFundsPurpose()) &&
//                    Objects.equals(evaluationSubjectName, ratingAmount.getEvaluationSubjectName())) {
//            }else{
//                throw new MithrasException("项目重要信息变更，请更新债项评级！");
//            }
//            CorpSubjectItem subjectItemBefore = subjectItemMapper.selectOne(Wrappers.<CorpSubjectItem>lambdaQuery()
//                    .eq(CorpSubjectItem::getSubjectCode, "H9170")
//                    .eq(CorpSubjectItem::getSubjectType, PROFIT.name())
//                    .orderByDesc(CorpSubjectItem::getYear).orderByDesc(CorpSubjectItem::getQuarter)
//                    .le(CorpSubjectItem::getCreateTime,effectTime)
//                    .last("limit 1"));
//
//            CorpSubjectItem subjectItemAfter = subjectItemMapper.selectOne(Wrappers.<CorpSubjectItem>lambdaQuery()
//                    .eq(CorpSubjectItem::getSubjectCode, "H9170")
//                    .eq(CorpSubjectItem::getSubjectType, PROFIT.name())
//                    .orderByDesc(CorpSubjectItem::getYear).orderByDesc(CorpSubjectItem::getQuarter)
//                    .last("limit 1"));
//
//            if(subjectItemAfter != null && subjectItemBefore != null){
//                if(!Objects.equals(subjectItemAfter.getSubjectValue(),subjectItemBefore.getSubjectValue())){
//                    throw new MithrasException("项目重要信息变更，请更新债项评级！");
//                }
//            }
//        }
    }

    /**
     * 评级生效开始一年过期，过期前一个月向相关项目经理发起评级失效通知
     */
    @XxlJob("ratingOverDueScan")
    @Transactional(rollbackFor = Throwable.class)
    public void ratingAbandon(){
        // 过期时间
        LocalDate overdueDate = LocalDate.now().minusYears(1);
        // 通知时间
        LocalDate informDate = overdueDate.plusMonths(1);

        List<RatingAmount> informAmountList = this.list(Wrappers.<RatingAmount>lambdaQuery()
                .eq(RatingAmount::getRatingStatus, true)
                .eq(RatingAmount::getEffectTime,informDate)
                .isNull(RatingAmount::getAbandonTime));
        List<RatingClient> informClientList = ratingClientService.list(Wrappers.<RatingClient>lambdaQuery()
                .eq(RatingClient::getRatingStatus, true)
                .eq(RatingClient::getEffectTime, informDate)
                .isNull(RatingClient::getAbandonTime));
        if(CollectionUtils.isNotEmpty(informAmountList)){
            for (RatingAmount ratingAmount : informAmountList) {
                MessageAddREQ messageAddREQ = new MessageAddREQ();
                messageAddREQ.setFrom("系统通知");
                messageAddREQ.setTo(Collections.singletonList(ratingAmount.getBelongSponsorUserId()));
                messageAddREQ.setFlowid(String.valueOf(ratingAmount.getId()));
                String clientId = id2NameService.clientId2NameSingle(ratingAmount.getEvaluationSubjectId());
                ProjReviewBaseInfo projReviewBaseInfo = projReviewBaseInfoService.getById(ratingAmount.getProjReviewId());
                messageAddREQ.setContent(String.format("%s项目评估主体为%s的债项评级将在一个月后过期,请及时更新",Optional.ofNullable(projReviewBaseInfo).map(ProjReviewBaseInfo::getProjName).orElse(""),clientId));
                messageAddREQ.setNeedOa(false);
                messageAddREQ.setRelation(String.format("%s的债项评级将在一个月后过期",clientId));
                messageAddREQ.setNoticeSource(NoticeSourceENUM.RATING_AMOUNT_UPDATE.name());
                messageAddREQ.setMessageType(MessageTypeEnum.RATING_AMOUNT_OVER_DUE.name());
                if(projReviewBaseInfo != null) {
                    messageAddREQ.setPcurl(String.format("/project/review/detail/%s?typeId=review&bizType=%s&modal=amount",
                            ratingAmount.getProjReviewId(),projReviewBaseInfo.getBizType()));
                }
                messageAddREQ.setBusinessId(String.valueOf(ratingAmount.getId()));
                messageService.sendMessage(messageConver.reqToMessage(messageAddREQ));
            }
        }
        if(CollectionUtils.isNotEmpty(informClientList)){
            for (RatingClient ratingClient : informClientList) {
                MessageAddREQ messageAddREQ = new MessageAddREQ();
                messageAddREQ.setFrom("系统通知");
                messageAddREQ.setTo(Collections.singletonList(ratingClient.getBelongSponsorUserId()));
                messageAddREQ.setFlowid(String.valueOf(ratingClient.getId()));
                String clientName = id2NameService.clientId2NameSingle(ratingClient.getClientId());
                messageAddREQ.setContent(String.format("%s的客户评级将在一个月后过期,请及时更新",clientName));
                messageAddREQ.setNeedOa(false);
                messageAddREQ.setRelation(String.format("%s的客户评级将在一个月后过期",clientName));
                messageAddREQ.setNoticeSource(NoticeSourceENUM.RATING_CLIENT_UPDATE.name());
                messageAddREQ.setMessageType(MessageTypeEnum.RATING_CLIENT_OVER_DUE.name());
                messageAddREQ.setPcurl(String.format("/customer/customerRat?search={\"clientName\":\"%s\"}",clientName));
                messageAddREQ.setBusinessId(String.valueOf(ratingClient.getId()));
                messageService.sendMessage(messageConver.reqToMessage(messageAddREQ));
            }
        }


//        List<RatingAmount> overdueAmountList = this.list(Wrappers.<RatingAmount>lambdaQuery()
//                .eq(RatingAmount::getRatingStatus, true)
//                .eq(RatingAmount::getEffectTime,overdueDate)
//                .isNull(RatingAmount::getAbandonTime));
        List<RatingClient> overdueAllClientList = ratingClientService.list(Wrappers.<RatingClient>lambdaQuery()
                .eq(RatingClient::getRatingStatus, true)
                .eq(RatingClient::getEffectTime, overdueDate)
                .isNull(RatingClient::getAbandonTime));

        //剔除剩余本金为0客户
        List<RatingClient> overdueClientList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(overdueAllClientList)) {
            Map<Long, Long> clientRemainingPrincipalMap = clientService.getClientRemainingPrincipalMap(overdueAllClientList.stream().map(RatingClient::getClientId).collect(Collectors.toList()));
            overdueAllClientList.forEach(e -> {
                if (clientRemainingPrincipalMap.getOrDefault(e.getClientId(), 0L) > 0) {
                    overdueClientList.add(e);
                }
            });
        }
        // 过期，评级失效-并自动触发评级更新流程
        if(CollectionUtils.isNotEmpty(overdueClientList)) {
            // 取未结清客户
            List<Client> clientList = clientMapper.selectBatchIds(overdueClientList.stream().map(RatingClient::getClientId).collect(Collectors.toList()));
            List<ClientListRSP> clientListRSPS = BeanUtil.copyToList(clientList, ClientListRSP.class);
            clientService.fillOtherInfo(clientListRSPS, false);
            List<Long> clientIdList = clientListRSPS.stream().filter(f -> Optional.ofNullable(f.getLastPrincipal()).orElse(0L) > 0).map(ClientListRSP::getId).collect(Collectors.toList());
            List<RatingClient> overdueNotSettleClientList = overdueClientList.stream().filter(f -> clientIdList.contains(f.getClientId())).collect(Collectors.toList());

            if(CollectionUtils.isNotEmpty(overdueNotSettleClientList)) {
                List<Long> overdueClientIdList = overdueNotSettleClientList.stream().map(RatingClient::getId).collect(Collectors.toList());
                ratingClientService.update(Wrappers.<RatingClient>lambdaUpdate().in(RatingClient::getId, overdueClientIdList)
                        .set(RatingClient::getRatingStatus, false)
                        .set(RatingClient::getAbandonTime, LocalDate.now()));

                for (RatingClient ratingClient : overdueNotSettleClientList) {
                    Long belongSponsorUserId = ratingClient.getBelongSponsorUserId();
                    if (belongSponsorUserId == null) {
                        log.error("主办用户不存在，客户评级自动触发评级更新流程失败[ratingClientId:{}]", ratingClient.getId());
                        continue;
                    }
                    String clientName = id2NameService.clientId2NameSingle(ratingClient.getClientId());
                    RatingClient ratingClientNew = saveCopyRatingClient(ratingClient);
                    CommonProcessPrepare prepare = CommonProcessPrepare.builder()
                            .processType(ProcessModelTypeEnum.RatingClientUpdateFlow.name())
                            .businessId(String.valueOf(ratingClientNew.getId()))
                            .formName(clientName)
                            .projName(null)
                            .clientName(clientName)
                            .currentAssignee(JSON.toJSONString(Collections.singletonList(ratingClient.getBelongSponsorUserId())))
                            .currentNode("项目经理确认")
                            .applyTime(LocalDateTime.now())
                            .status(CommonProcessPrepareStatus.PEND_COMMIT.name())
                            .build();
                    commonProcessPrepareMapper.insert(prepare);

                }
            }
        }
    }

    private RatingClient saveCopyRatingClient(RatingClient ratingClient) {
        RatingClient ratingClientNew = new RatingClient();
        ratingClientNew.setClientId(ratingClient.getClientId());
        ratingClientNew.setClientCode(ratingClient.getClientCode());
        ratingClientNew.setClientName(ratingClient.getClientName());
        ratingClientNew.setUscc(ratingClient.getUscc());
        ratingClientNew.setModelCode(ratingClient.getModelCode());
        ratingClientNew.setModelName(ratingClient.getModelName());
        ratingClientNew.setBelongDeptId(ratingClient.getBelongDeptId());
        ratingClientNew.setBelongSponsorUserId(ratingClient.getBelongSponsorUserId());
        ratingClientNew.setProcessStatus(ProcessStatus.UN_SUBMIT.name());
        ratingClientNew.setCreateBy(ratingClient.getCreateBy());
        ratingClientNew.setUpdateBy(ratingClient.getUpdateBy());
        ratingClientService.save(ratingClientNew);
        // 不管是否是区县类模型都初始化一份区域指标的系统数据，这样相对简单，不用考虑模型类型和模型切换带来的额外逻辑
        Long areaUniCode = SpringUtil.getBean(RatingClientService.class).getClientAreaUniCode(ratingClientNew.getClientId(), ratingClientNew.getModelName());
        SpringUtil.getBean(RatingClientAreaIndicatorService.class).create(ratingClientNew.getId(), areaUniCode, LocalDate.now().getYear());
        return ratingClientNew;
    }

    public RatingAmountAccessCheckRSP ratingAmountAccessCheck(RatingAmountAccessCheckREQ req) {
        // 新增前再校验一次
        ratingAmountInfo(new RatingAmountInfoREQ(req.getProjReviewId()));

        RatingAmountAccessCheckRSP rsp = new RatingAmountAccessCheckRSP();
//        if(!req.getMaterialLeaseItem()){
//            Map<String,Object> param = new HashMap<>();
//            param.put("client_id",req.getEvaluationSubjectId());
//            RatingAccessCheckRSP ratingAccessCheckRSP = decisionService.accessCheckAmount(param);
//            if(!ratingAccessCheckRSP.getClientRankAccess()) {
//                throw new MithrasException("客户评级结果不在准入范围，无法进行债项评级");
//            }
//        }
        int count = this.count(Wrappers.<RatingAmount>lambdaQuery().eq(RatingAmount::getProjReviewId, req.getProjReviewId())
                    .eq(RatingAmount::getEvaluationSubjectId,req.getEvaluationSubjectId())
                    .eq(RatingAmount::getRatingStatus, true));
        rsp.setExist(count > 0);
        return rsp;
    }

    public RatingAmountDetailRSP ratingAmountDetail(RatingAmountDetailREQ req) {
        RatingAmount ratingAmount = this.getById(req.getId());
        if(ratingAmount == null){
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        RatingAmountDetailRSP rsp = BeanUtil.copyProperties(ratingAmount, RatingAmountDetailRSP.class);
        if(ratingAmount.getClientQuota() != null) {
            rsp.setProjQuota(transformWanYuan(generateProjQuota(ratingAmount,ratingAmount.getClientQuota())));
        }
        return rsp;
    }

    public void ratingAmountIndexApproval(RatingReportApprovalRSP req) {
        RatingAmount ratingAmount = getById(req.getId());
        if(ratingAmount == null){
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        RatingReport ratingReport = ratingReportMapper.selectById(ratingAmount.getReportId());
        if(ratingReport != null) {
            // 评级审批结果集
            List<RatingReportApprovalRSP.RatingApprovalRSP> approvalRSPList = new ArrayList<>();
            RatingReportApprovalRSP.RatingApprovalRSP newApproval = req.getRatingApprovalRSP();
            if(newApproval == null){
                return;
            }
            // 标记是否为已存在的指标
            boolean flag = false;
            if(ratingReport.getApprovalInfo() != null){
                approvalRSPList = JSON.parseArray(ratingReport.getApprovalInfo(), RatingReportApprovalRSP.RatingApprovalRSP.class);
                for (int i = 0; i < approvalRSPList.size(); i++) {
                    if(Objects.equals(approvalRSPList.get(i).getFieldName(),newApproval.getFieldName())){
                        if(newApproval.getApprovalStatus() != null){
                            approvalRSPList.get(i).setApprovalStatus(newApproval.getApprovalStatus());
                        }
                        if(newApproval.getApprovalOpinion() != null){
                            approvalRSPList.get(i).setApprovalOpinion(newApproval.getApprovalOpinion());
                        }
                        flag = true;
                        break;
                    }
                }

            }
            if(!flag) {
                approvalRSPList.add(newApproval);
            }
            ratingReport.setApprovalInfo(JSON.toJSONString(approvalRSPList));
            ratingReportMapper.updateById(ratingReport);
        }
    }
}
