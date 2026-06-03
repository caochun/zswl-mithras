package cn.zswltech.mithras.service.service.assetclassify;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.date.StopWatch;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.flow.core.api.FlowTaskApiService;
import cn.zswltech.flow.core.domain.req.task.ProcessPageReq;
import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.flow.core.enums.ProcessBusinessStatusEnum;
import cn.zswltech.flow.core.util.Page;
import cn.zswltech.gruul.biz.service.UserService;
import cn.zswltech.gruul.dao.dal.entity.UserDO;
import cn.zswltech.mithras.dto.assetclassify.*;
import cn.zswltech.mithras.dto.message.MessageAddREQ;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.service.constant.VersionTypeConstants;
import cn.zswltech.mithras.service.convert.MessageConver;
import cn.zswltech.mithras.service.convert.assetclassify.AssetClassifyConvert;
import cn.zswltech.mithras.service.enums.*;
import cn.zswltech.mithras.service.enums.assetclassify.*;
import cn.zswltech.mithras.service.enums.kpi.config.ProvisionRadioEnum;
import cn.zswltech.mithras.service.enums.notice.MessageTypeEnum;
import cn.zswltech.mithras.service.enums.notice.NoticeSourceENUM;
import cn.zswltech.mithras.riskcontrol.common.RiskControlIndustryClassify;
import cn.zswltech.mithras.service.gendoc.render.AssetClassifySummaryRender;
import cn.zswltech.mithras.service.mapper.assetclassify.AssetClassifyMapper;
import cn.zswltech.mithras.contract.mapper.contract.ContractBaseInfoMapper;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.dto.ClientMaxLeaseMonthDTO;
import cn.zswltech.mithras.service.mapper.lib.CommonVersionMapper;
import cn.zswltech.mithras.service.mapper.model.BaseModel;
import cn.zswltech.mithras.service.mapper.model.CommonVersion;
import cn.zswltech.mithras.service.mapper.model.assetclassify.AssetClassify;
import cn.zswltech.mithras.service.mapper.model.assetclassify.AssetClassifyClient;
import cn.zswltech.mithras.service.mapper.model.assetclassify.AssetClassifyClientRiskFactorTemplate;
import cn.zswltech.mithras.service.mapper.model.assetclassify.AssetClassifyNodeRecord;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.Client;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.CorpCommerceInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractReceipt;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractRentActual;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.model.PaymentBaseInfo;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.others.SpringContextHolder;
import cn.zswltech.mithras.service.service.Id2NameService;
import cn.zswltech.mithras.service.service.afterlese.RentCollectionDetailService;
import cn.zswltech.mithras.service.service.client.ClientService;
import cn.zswltech.mithras.service.service.client.CorpCommerceInfoService;
import cn.zswltech.mithras.service.service.collection.CollectionBaseInfoService;
import cn.zswltech.mithras.service.service.collection.CollectionService;
import cn.zswltech.mithras.service.service.contract.ContractBaseInfoService;
import cn.zswltech.mithras.service.service.contract.ContractReceiptService;
import cn.zswltech.mithras.service.service.contract.ContractRentActualService;
import cn.zswltech.mithras.kpi.service.KpiParameterConfigService;
import cn.zswltech.mithras.service.service.lib.assetclassify.AssetClassifyLibVersionService;
import cn.zswltech.mithras.service.service.materialsfile.MaterialsListService;
import cn.zswltech.mithras.service.service.message.MessageService;
import cn.zswltech.mithras.service.service.payment.PaymentActualDetailService;
import cn.zswltech.mithras.service.service.payment.PaymentBaseInfoService;
import cn.zswltech.mithras.service.util.DateUtil;
import cn.zswltech.mithras.service.util.LongUtil;
import cn.zswltech.mithras.service.util.StringUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import static cn.hutool.core.util.ObjectUtil.isNull;
import static cn.zswltech.mithras.service.others.MithrasException.err;

/**
 * @author dingqi
 * @date 2023/1/3
 * @description
 */
@Service
@Slf4j
public class AssetClassifyService extends ServiceImpl<AssetClassifyMapper, AssetClassify> {

    @Resource
    private AssetClassifyConvert assetClassifyConvert;
    @Resource
    private AssetClassifyClientService assetClassifyClientService;
    @Resource
    private UserService userService;
    @Resource
    private FlowTaskApiService flowTaskApiService;
    @Resource
    private MessageService messageService;
    @Resource
    private MessageConver messageConver;
    @Resource
    private AssetClassifySummaryRender assetClassifySummaryRender;
    @Resource
    private MaterialsListService materialsListService;
    @Resource
    private ContractRentActualService contractRentActualService;
    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private ClientService clientService;
    @Resource
    private AssetClassifyService assetClassifyService;
    @Resource
    private AssetClassifyNodeRecordService assetClassifyNodeRecordService;
    @Resource
    private PaymentBaseInfoService paymentBaseInfoService;
    @Resource
    private PaymentActualDetailService paymentActualDetailService;
    @Resource
    private CorpCommerceInfoService corpCommerceInfoService;
    @Resource
    private CollectionBaseInfoService collectionBaseInfoService;
    @Resource
    private CollectionService collectionService;
    @Resource
    private RentCollectionDetailService rentCollectionDetailService;
    @Resource
    private Id2NameService id2NameService;
    @Resource
    private AssetClassifyLibVersionService assetClassifyLibVersionService;
    @Resource
    private ContractBaseInfoMapper contractBaseInfoMapper;
    @Resource
    private AssetClassifyClientRiskFactorTemplateService assetClassifyClientRiskFactorTemplateService;
    @Resource
    private ContractReceiptService contractReceiptService;
    @Resource
    private KpiParameterConfigService kpiParameterConfigService;

    private static final ThreadPoolExecutor threadPool = new ThreadPoolExecutor(1, Runtime.getRuntime().availableProcessors() + 1, 30, TimeUnit.SECONDS, new LinkedBlockingQueue<>(500));

    @Transactional(rollbackFor = Throwable.class)
    public void generateSummaryFile(Long assetClassifyId, AssetClassifyBizNodeEnum assetClassifyBizNodeEnum) {
        AssetClassify assetClassify = this.getById(assetClassifyId);
        if (Objects.isNull(assetClassify)) {
            throw new MithrasException("资产五级分类数据不存在");
        }
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            String fileName = assetClassifySummaryRender.render(outputStream, assetClassify);
            // 加工一下文件名称
            int index = fileName.lastIndexOf(".");
            String s1 = fileName.substring(0, index);
            String s2 = fileName.substring(index);
            fileName = s1 + "_" + assetClassifyBizNodeEnum.getDisplay() + s2;
            // 上传并生成文件记录
            try (ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray())) {
                String mainType = null;
                if (assetClassifyBizNodeEnum == AssetClassifyBizNodeEnum.REVIEW_MEETING) {
                    mainType = AssetClassifyMeetingFileMaterialsEnum.ASSET_CLASSIFY_REVIEW_MEETING.name();
                }
                if (assetClassifyBizNodeEnum == AssetClassifyBizNodeEnum.RISK_MEETING) {
                    mainType = AssetClassifyMeetingFileMaterialsEnum.ASSET_CLASSIFY_RISK_MEETING.name();
                }
                if (StrUtil.isBlank(mainType)) {
                    throw new MithrasException("无法确定认定汇总表的流程阶段类型，生成失败");
                }
                materialsListService.newAdd(inputStream, fileName, assetClassify.getId(), mainType, AssetClassifyMaterialsEnum.ASSET_CLASSIFY_SUMMARY.name(),  BusinessModuleEnum.ASSET_CLASSIFY.name(), YesOrNoNumberEnum.YES);
            }
        } catch (Exception e) {
            log.error("生成<资产五级分类认定汇总审批表>发生异常", e);
            throw new MithrasException("生成<资产五级分类认定汇总审批表>发生异常");
        }
    }

    public boolean exist(Integer year, Integer quarter) {
        LambdaQueryWrapper<AssetClassify> query = Wrappers.lambdaQuery();
        query.eq(AssetClassify::getYear, year);
        query.eq(AssetClassify::getQuarter, quarter);
        return CollectionUtil.isNotEmpty(this.list(query));
    }

    @Transactional(rollbackFor = Throwable.class)
    public void finish(Long assetClassifyId) {
        // 拷贝建议分类作为本次分类结果
        List<AssetClassifyClient> assetClassifyClientList = assetClassifyClientService.listByAssetClassifyId(assetClassifyId);
        assetClassifyClientList.forEach(e -> {
            if (StrUtil.isNotBlank(e.getSuggestResult()) && !Objects.equals(e.getSuggestResult(), AssetClassifySuggestEnum.UNANIMITY.name())) {
                e.setClassifyResult(e.getSuggestResult());
            }
        });
        if (!assetClassifyClientList.isEmpty()) {
            assetClassifyClientService.updateBatchById(assetClassifyClientList);
        }
        // 本次五级分类结束
        AssetClassify assetClassify = this.getById(assetClassifyId);
        Assert.notNull(assetClassify, () -> MithrasException.newException(ResultMsg.RECORD_NOT_EXIST));
        assetClassify.setFinish(YesOrNoNumberEnum.YES.getCode());
        this.updateById(assetClassify);
    }

    /**
     * 查询某一年的季度数据
     *
     * @author: jackerhe
     * @date: 2023/1/4 3:43 下午
     **/
    public List<QuarterDetailRSP> quarterSelect(Integer year) {
        List<AssetClassify> assetClassifies = baseMapper.selectList(Wrappers.<AssetClassify>lambdaQuery()
                .eq(AssetClassify::getYear, year)
                .orderByAsc(AssetClassify::getQuarter));
        if (ObjectUtil.isEmpty(assetClassifies)) return null;
        // 按照季度分组
        Map<Integer, List<AssetClassify>> QuarterMap = assetClassifies.stream().collect(Collectors.groupingBy(AssetClassify::getQuarter));
        // 取每个季度的最新一条
        List<AssetClassify> latestAssetClassifies = QuarterMap.values().stream()
                .map(list -> list.stream()
                        .max(Comparator.comparing(AssetClassify::getCreateTime))
                        .orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        // 按照ID分组
        Map<Long, List<AssetClassify>> latestAssetClassifyMap = latestAssetClassifies.stream().collect(Collectors.groupingBy(AssetClassify::getId));
        // 实体类转换
        List<QuarterDetailRSP> rsps = latestAssetClassifies.stream().map(assetClassifyConvert::assetClassify2QuarterDetailRSP).collect(Collectors.toList());
        //如果是最后一季度动态获取
        rsps.forEach(res -> {
            //if(ObjectUtil.isEmpty(res.getClassificationAmounts())){
            res.setClassificationAmounts(countQuarterAmount(res.getId()));
            //}
            // 根据ID获取每个季度的最新的初分类型
            if (latestAssetClassifyMap.containsKey(res.getId())){
                String initType = latestAssetClassifyMap.get(res.getId()).get(0).getInitType();
                Long assetClassId = latestAssetClassifyMap.get(res.getId()).get(0).getId();
                res.setInitType(initType);
                // 如果是季中初分，查询节点信息
                if (initType.equals(AssetClassifyInitTypeEnum.QUARTER_MID.name())){
                    AssetClassifyNodeRecord riskMeetingNode = assetClassifyNodeRecordService.getOne(
                            Wrappers.<AssetClassifyNodeRecord>lambdaQuery()
                                    .eq(AssetClassifyNodeRecord::getAssetClassifyId, assetClassId)
                                    .eq(AssetClassifyNodeRecord::getNodeName, AssetClassifyBizNodeEnum.RISK_MEETING.name()).last("limit 1"));
                    if (ObjectUtil.isNotEmpty(riskMeetingNode) && riskMeetingNode.getNodeStatue().equals(AssetClassifyStatusEnum.FINISH.name())) {
                        // 如果流程完成，initStatue 设置为FINISH,表示可以进行季中初分与季末初分
                        res.setMidInitStatue(AssetClassifyStatusEnum.FINISH.name());
                    }else {
                        // 流程没有完成，initStatue 设置为PROCESS,表示只能进行季中初分，不能季末初分
                        res.setMidInitStatue(AssetClassifyStatusEnum.PROCESS.name());
                    }
                }
            }
        });
        /*QuarterDetailRSP quarterDetailRSP = rsps.get(rsps.size() - 1);
        quarterDetailRSP.setClassificationAmounts(countQuarterAmount(assetClassifies.get(assetClassifies.size() - 1).getId()));*/
        return rsps;
    }

    /**
     * 剩余工作日提醒
     *
     * @author: jackerhe
     * @date: 2023/1/9 11:28 上午
     **/
    public void weekdayRemind(Integer days) {
        AssetClassify assetClassify = this.getOne(Wrappers.<AssetClassify>lambdaQuery()
                .eq(AssetClassify::getFinish, YesOrNoNumberEnum.NO.getCode())
                .orderByDesc(AssetClassify::getId)
                .last(StringUtil.mysqlLimitOne()));
        if (ObjectUtil.isNull(assetClassify)) {
            return;
        }
        LocalDate beganDate = LocalDate.of(assetClassify.getYear() + (ObjectUtil.equals(4,
                assetClassify.getQuarter()) ? 1 : 0),
                (assetClassify.getQuarter() % 4) * 3 + 1, 1);
        //剩余五天工作日，发送通知
        if (ObjectUtil.equals(ObjectUtil.equals(0, days) ? 5 : days, (20 - DateUtil.countWorkdayNumber(beganDate, LocalDate.now())))) {
            List<UserDO> userDOS = new ArrayList<>();
            userDOS.addAll(userService.getUsersByjobcod(JobEnum.assetmanagement.name()));
            userDOS.addAll(userService.getUsersByjobcod(JobEnum.riskdeptmanager.name()));
            Set<Long> idSet = userDOS.stream().map(UserDO::getId).collect(Collectors.toSet());
            if (ObjectUtil.isNotEmpty(idSet)) {
                MessageAddREQ messageAddREQ = buildMessageAddREQ(new ArrayList<>(idSet), assetClassify);
                messageService.sendMessage(messageConver.reqToMessage(messageAddREQ));
            }
        }
    }

    private MessageAddREQ buildMessageAddREQ(List<Long> toIds, AssetClassify assetClassify) {
        MessageAddREQ messageAddREQ = new MessageAddREQ();
        MessageUrlEnum messageUrlEnum = MessageUrlEnum.ASSET_CLASSIFY_REMIND;
        messageAddREQ.setFrom("系统通知");
        messageAddREQ.setTo(toIds);
        messageAddREQ.setContent(String.valueOf(assetClassify.getId()));
        messageAddREQ.setFlowid(String.valueOf(assetClassify.getId()));
        messageAddREQ.setRelation(String.format("资产分类%s年第%s季度请尽快完成审核", assetClassify.getYear(), assetClassify.getQuarter()));
        messageAddREQ.setNeedOa(Boolean.TRUE);
        messageAddREQ.setNoticeSource(NoticeSourceENUM.ASSET_CLASSIFY.name());
        messageAddREQ.setMessageType(MessageTypeEnum.CC.name());
        messageAddREQ.setPcurl(messageUrlEnum.pcUrl);
        messageAddREQ.setAppurl(messageUrlEnum.appUrl);
        messageAddREQ.setBusinessId(String.valueOf(assetClassify.getId()));
        return messageAddREQ;
    }

    /**
     * 计算五级分类具体数据
     *
     * @author: jackerhe
     * @date: 2023/1/4 2:35 下午
     **/
    public List<QuarterDetailRSP.ClassificationAmount> countQuarterAmount(Long assetClassifyId) {
        List<AssetClassifyClient> list = assetClassifyClientService.list(Wrappers.<AssetClassifyClient>lambdaQuery()
                .eq(AssetClassifyClient::getAssetClassifyId, assetClassifyId));
        Map<String, Integer> classifyResultMap = new HashMap<>();
        QuarterDetailRSP quarterDetailRSP = new QuarterDetailRSP();
        List<QuarterDetailRSP.ClassificationAmount> amounts = new ArrayList<>();
        //初始化，一点是五级分类
        for (AssetClassifyResultEnum resultEnum : AssetClassifyResultEnum.values()) {
            classifyResultMap.put(resultEnum.name(), 0);
        }
        list.forEach(assetClassifyClient -> classifyResultMap.put(assetClassifyClient.getClassifyResult(), classifyResultMap.get(assetClassifyClient.getClassifyResult()) + 1));
        classifyResultMap.forEach((key, value) -> {
            QuarterDetailRSP.ClassificationAmount classificationAmount = quarterDetailRSP.new ClassificationAmount();
            classificationAmount.setClassifyResult(key);
            classificationAmount.setClassifyAmount(value);
            amounts.add(classificationAmount);
        });
        return amounts.stream().peek(rsp ->
                rsp.setSort(AssetClassifyResultEnum.of(rsp.getClassifyResult()).getOrder())).sorted(Comparator.comparing(QuarterDetailRSP.ClassificationAmount::getSort)).collect(Collectors.toList());
    }

    public Integer residueWorkday(Long assetClassifyId) {
        AssetClassify assetClassify = baseMapper.selectById(assetClassifyId);
        if (ObjectUtil.isNull(assetClassify)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        //已经结束
        if (ObjectUtil.equals(YesOrNoNumberEnum.YES.getCode(), assetClassify.getFinish())) {
            return null;
        }
        LocalDate beganDate = LocalDate.of(assetClassify.getYear() + (ObjectUtil.equals(4,
                assetClassify.getQuarter()) ? 1 : 0),
                (assetClassify.getQuarter() % 4) * 3 + 1, 1);
        return Math.max(0, 20 - DateUtil.countWorkdayNumber(beganDate, LocalDate.now()));
    }

    /**
     * 获取指定时间最近的已完成的五级分类
     */
    public Optional<AssetClassify> currentClassify(LocalDate date) {
        return Optional.ofNullable(getOne(Wrappers.<AssetClassify>lambdaQuery()
                .le(date != null, BaseModel::getCreateTime, date)
                .eq(AssetClassify::getFinish, 1)
                .orderByDesc(AssetClassify::getYear)
                .orderByDesc(AssetClassify::getQuarter)
                .last("limit 1")));
    }

    public Optional<AssetClassify> currentClassify() {
        return currentClassify(null);
    }

    @Resource
    private CommonVersionMapper commonVersionMapper;

    public String lastestVersionCode(Long id) {
        CommonVersion lastVersion = commonVersionMapper.selectOne(
                Wrappers.<CommonVersion>lambdaQuery()
                        .eq(CommonVersion::getModule, BusinessModuleEnum.ASSET_CLASSIFY.name())
                        .eq(CommonVersion::getMainId, id)
                        .eq(CommonVersion::getVersionType, VersionTypeConstants.NORMAL)
                        .orderByDesc(CommonVersion::getVersion)
                        .last("limit 1"));
        return lastVersion == null ? null : lastVersion.getVersion();
    }

    /**
     * 进行初分，返回比对信息
     **/
    @Transactional(rollbackFor = Throwable.class)
    public AssetManualDivisionRSP init(Integer year, Integer quarter) {
        log.info("AssetClassifyService init year {}, quarter {}", year, quarter);
        StopWatch stopWatch = new StopWatch("五级分类-初分");
        stopWatch.start("筛选客户");
        // 找到所有起租的合同
        Map<Long, Long> startRentContractMap = collectionService.listContractAmountByContractIds(null);
        List<Long> startRentContractIds = new ArrayList<>();
        startRentContractMap.forEach((key, value) -> {
            if(LongUtil.null2zero(value) > 0){
                startRentContractIds.add(key) ;
            }
        });
        if(CollectionUtil.isEmpty(startRentContractIds)){
            log.info("没有剩余本金为0的合同");
            return null;
        }
        List<ContractBaseInfo> startRentContractList = contractBaseInfoService.listByIds(startRentContractIds);
        Long oldAssetClassifyId = null;
        int fistFlag = YesOrNoNumberEnum.YES.getCode();
        // 校验是否已有数据
        AssetClassify assetClassify = assetClassifyService.getOne(Wrappers.<AssetClassify>lambdaQuery()
                .eq(AssetClassify::getYear, year)
                .eq(AssetClassify::getQuarter, quarter)
                .eq(AssetClassify::getInitType,AssetClassifyInitTypeEnum.QUARTER_END.name())//增加季末初分的类型
                .orderByDesc(AssetClassify::getId)
                .last(StringUtil.mysqlLimitOne()));
        List<AssetClassifyClient> oldAssetClassifyClient = null;
        //保存上季度快照, 最后判断节点状态
        if (ObjectUtil.isEmpty(assetClassify)) {
            assetClassify = new AssetClassify();
            assetClassify.setYear(year);
            assetClassify.setQuarter(quarter);
            assetClassify.setFinish(YesOrNoNumberEnum.NO.getCode());
            assetClassifyService.save(assetClassify);
        } else {
            //非第一次初分，判断是否有审批中数据
            fistFlag = YesOrNoNumberEnum.NO.getCode();
            ProcessPageReq processPageReq = new ProcessPageReq();
            processPageReq.setPageIndex(1);
            processPageReq.setPageSize(10);
            processPageReq.setModelKeyList(BusinessModuleEnum.ASSET_CLASSIFY_REVIEW.getModelKeyList());
            processPageReq.setProcessStatusList(Arrays.asList(ProcessBusinessStatusEnum.RUNNING.getType(),
                    ProcessBusinessStatusEnum.SUSPEND.getType()));
            Page<ProcessResp> processRespPage = flowTaskApiService.queryProcess(processPageReq);
            if (CollectionUtil.isNotEmpty(processRespPage.getContents())) {
                throw new MithrasException("存在审批中的资产五级分类复核流程，无法进行系统初分！");
            }
            oldAssetClassifyClient = assetClassifyClientService.listByAssetClassifyId(assetClassify.getId());
            oldAssetClassifyId = assetClassify.getId();
                    //清理上次的分类数据
            assetClassifyNodeRecordService.remove(Wrappers.<AssetClassifyNodeRecord>lambdaQuery()
                    .eq(AssetClassifyNodeRecord::getAssetClassifyId, assetClassify.getId()));
        }
        List<AssetClassifyClient> nowClientList = new LinkedList<>();
        List<AssetClassifyClient> toSaveClientList = new LinkedList<>();
        List<AssetClassifyClient> toUpdateClientList = new LinkedList<>();
        List<AssetClassifyClient> toDeleteClientList = new LinkedList<>();
        if (CollectionUtil.isEmpty(startRentContractList)) {
            log.info("没有处于起租状态的合同");
            return compareAssetClassify(null, oldAssetClassifyClient, toSaveClientList, toUpdateClientList, toDeleteClientList);
        }
        LocalDateTime targetDateTime = LocalDateTime.now();
        // 按照客户id分组
        Map<Long, List<ContractBaseInfo>> contractMap = startRentContractList.stream().collect(Collectors.groupingBy(ContractBaseInfo::getClientId));
        //查询客户下最长租期
        Map<Long, Long> clientId2MaxMonthMap = contractBaseInfoMapper.clientMaxLeaseMonth(contractMap.keySet()).stream().collect(Collectors.toMap(ClientMaxLeaseMonthDTO::getClientId,
                ClientMaxLeaseMonthDTO::getLeaseMonthCount, (a, b) -> a));
        //查询客户风险
        Map<Long, CorpCommerceInfo> clientId2CorpCommerceInfoMap = corpCommerceInfoService.list(contractMap.keySet()).stream().collect(Collectors.toMap(CorpCommerceInfo::getClientId, e -> e, (a, b) -> a));
        stopWatch.stop();
        stopWatch.start("客户分类");
        //客户分类
        CompletionService<AssetClassifyClient> completionService = new ExecutorCompletionService<>(threadPool);
        Long assetClassifyId = assetClassify.getId();
        for (Map.Entry<Long, List<ContractBaseInfo>> entry : contractMap.entrySet()) {
            Long finalOldAssetClassifyId = oldAssetClassifyId;
            completionService.submit(() -> {
                AssetClassifyClient assetClassifyClient = SpringContextHolder.getBean(AssetClassifyService.class).handleClient(entry.getKey(), entry.getValue(),
                        clientId2MaxMonthMap.getOrDefault(entry.getKey(), 0L), clientId2CorpCommerceInfoMap.getOrDefault(entry.getKey(), new CorpCommerceInfo()), startRentContractMap);
                if (Objects.nonNull(assetClassifyClient)) {
                    // 查询一下是否有上一次分类结果
                    AssetClassifyClient lastOne = assetClassifyClientService.getLastOneByClientId(entry.getKey(), finalOldAssetClassifyId);
                    if (Objects.nonNull(lastOne)) {
                        assetClassifyClient.setLastClassifyResult(lastOne.getClassifyResult());
                    }
                    assetClassifyClient.setAssetClassifyId(assetClassifyId);
                }
                return assetClassifyClient;
            });
        }
        try {
            for (int i = 0; i < contractMap.entrySet().size(); i++) {
                Future<AssetClassifyClient> f = completionService.take();
                nowClientList.add(f.get());
            }
        } catch (InterruptedException | ExecutionException e) {
            log.error("初分错误", e);
            Thread.currentThread().interrupt();
            throw new MithrasException("初分失败");
        }
        stopWatch.stop();
        stopWatch.start("保存数据");
        //比对新老数据，判断新增，更新，删除
        AssetManualDivisionRSP rsp = compareAssetClassify(nowClientList, oldAssetClassifyClient, toSaveClientList, toUpdateClientList, toDeleteClientList);
        // 保存资产五级分类客户表
        if(CollectionUtil.isNotEmpty(toSaveClientList)){
            assetClassifyClientService.saveBatch(toSaveClientList);
        }
        if(CollectionUtil.isNotEmpty(toUpdateClientList)){
            assetClassifyClientService.updateBatchById(toUpdateClientList);
        }
        if(CollectionUtil.isNotEmpty(toDeleteClientList)){
            assetClassifyClientService.removeByIds(toDeleteClientList.stream().map(AssetClassifyClient::getId).collect(Collectors.toList()));
        }
        // 保存资产五级分类业务节点信息
        AssetClassifyNodeRecord initNodeRecord = new AssetClassifyNodeRecord();
        initNodeRecord.setAssetClassifyId(assetClassify.getId());
        initNodeRecord.setNodeName(AssetClassifyBizNodeEnum.INIT.name());
        initNodeRecord.setStartTime(targetDateTime);
        initNodeRecord.setEndTime(LocalDateTime.now());
        initNodeRecord.setNodeStatue(AssetClassifyStatusEnum.FINISH.name());
        assetClassifyNodeRecordService.save(initNodeRecord);
        AssetClassifyNodeRecord reviewNodeRecord = new AssetClassifyNodeRecord();
        reviewNodeRecord.setAssetClassifyId(assetClassify.getId());
        reviewNodeRecord.setNodeName(AssetClassifyBizNodeEnum.REVIEW.name());
        reviewNodeRecord.setStartTime(targetDateTime);
        reviewNodeRecord.setNodeStatue(AssetClassifyStatusEnum.WAIT.name());
        assetClassifyNodeRecordService.save(reviewNodeRecord);
        // 生成版本
        assetClassifyLibVersionService.recordVersion(assetClassify.getId(), VersionTypeEnum.EFFECT, null, null, VersionTypeConstants.NORMAL);
        //填充是否第一次标识
        rsp.setFirstFlag(fistFlag);
        stopWatch.stop();
        log.info(stopWatch.prettyPrint(TimeUnit.SECONDS));
        return rsp;
    }

    /**
     * 比较资产分类客户数据
     * @param nowClientList             当前的资产分类客户列表
     * @param oldAssetClassifyClients   已有的资产分类客户列表
     * @param toSaveClientList          需要保存的资产分类客户列表
     * @param toUpdateClientList        需要更新的资产分类客户列表
     * @param toDeleteClientList        需要删除的资产分类客户列表
     **/
    private AssetManualDivisionRSP compareAssetClassify(List<AssetClassifyClient> nowClientList,
                                                        List<AssetClassifyClient> oldAssetClassifyClients,
                                                        List<AssetClassifyClient> toSaveClientList,
                                                        List<AssetClassifyClient> toUpdateClientList,
                                                        List<AssetClassifyClient> toDeleteClientList) {
        AssetManualDivisionRSP rsp = new AssetManualDivisionRSP();
        rsp.setAddClientNames(new ArrayList<>());
        rsp.setDeleteClientNames(new ArrayList<>());
        rsp.setChangeClientNames(new ArrayList<>());
        Set<Long> comClientIdSet = new HashSet<>();
        if (CollectionUtil.isEmpty(nowClientList) && CollectionUtil.isEmpty(oldAssetClassifyClients)) {
            return rsp;
        } else if (CollectionUtil.isEmpty(nowClientList)) {
            toDeleteClientList.addAll(oldAssetClassifyClients);
            rsp.setDeleteClientNames(oldAssetClassifyClients.stream().map(AssetClassifyClient::getClientName).collect(Collectors.toList()));
        } else if (CollectionUtil.isEmpty(oldAssetClassifyClients)) {
            toSaveClientList.addAll(nowClientList);
            rsp.setAddClientNames(nowClientList.stream().map(AssetClassifyClient::getClientName).collect(Collectors.toList()));
        } else {
            //比对新老差异
            Map<Long, AssetClassifyClient> oldAssetClassifyMap = oldAssetClassifyClients.stream().collect(Collectors.toMap(AssetClassifyClient::getClientId, e -> e, (a, b) -> a));
            for (AssetClassifyClient base : nowClientList) {
                AssetClassifyClient assetClassifyClient = null;
                if (ObjectUtil.isNotEmpty(oldAssetClassifyMap)) {
                    assetClassifyClient = oldAssetClassifyMap.get(base.getClientId());
                }
                if (assetClassifyClient == null) {
                    toSaveClientList.add(base);
                    rsp.getAddClientNames().add(base.getClientName());
                } else {
                    if (ObjectUtil.notEqual(base.getClassifyResult(), assetClassifyClient.getInitClassifyResult())) {
                        base.setId(assetClassifyClient.getId());
                        base.setReviewStatus(AssetClassifyReviewStatusEnum.PROCESS.name());
                        toUpdateClientList.add(base);
                        rsp.getChangeClientNames().add(base.getClientName());
                    }
                    if (ObjectUtil.notEqual(base.getAmount(), assetClassifyClient.getAmount()) || ObjectUtil.notEqual(base.getStockRiskExposure(), assetClassifyClient.getStockRiskExposure())) {
                        base.setId(assetClassifyClient.getId());
                        base.setReviewStatus(AssetClassifyReviewStatusEnum.PROCESS.name());
                        toUpdateClientList.add(base);
                        rsp.getAddClientNames().add(base.getClientName());
                    }
                    comClientIdSet.add(base.getClientId());
                }
            }
        }
        //删除多余
        if (CollectionUtil.isNotEmpty(oldAssetClassifyClients)) {
            oldAssetClassifyClients.forEach(old -> {
                if (!comClientIdSet.contains(old.getClientId())) {
                    comClientIdSet.add(old.getClientId());
                    toDeleteClientList.add(old);
                    rsp.getDeleteClientNames().add(old.getClientName());
                }
            });
        }
        return rsp;
    }

    public AssetClassifyClient handleClient(Long clientId, List<ContractBaseInfo> contractBaseInfoList, Long month, CorpCommerceInfo corpCommerceInfo, Map<Long, Long> startRentContractMap) {
        Client client = clientService.getById(clientId);
        AssetClassifyClient assetClassifyClient = new AssetClassifyClient();
        if (Objects.isNull(client)) {
            log.warn("没有找到客户信息[clientId: {}]", clientId);
            assetClassifyClient.setClientId(clientId);
            return assetClassifyClient;
        }
        // 客户基本信息
        assetClassifyClient.setClientId(clientId);
        assetClassifyClient.setClientName(client.getClientName());
        assetClassifyClient.setBelongDeptId(client.getBelongDeptId());
        assetClassifyClient.setBelongSponsorId(client.getBelongSponsorId());
        List<String> nameList = new ArrayList<>();
        List<Long> valueList = new ArrayList<>();
        // 投放金额 = 付款核销之和
        List<Long> contractIds = contractBaseInfoList.stream().map(ContractBaseInfo::getId).collect(Collectors.toList());
        List<PaymentBaseInfo> paymentBaseInfoList = paymentBaseInfoService.detailByContractIds(contractIds);
        if (CollectionUtil.isEmpty(paymentBaseInfoList)) {
            assetClassifyClient.setAmount(0L);
        } else {
            List<Long> paymentIds = paymentBaseInfoList.stream().map(PaymentBaseInfo::getId).collect(Collectors.toList());
            assetClassifyClient.setAmount(paymentActualDetailService.calculatePaidAmount(paymentIds));
        }
        // 剩余租期 = 未核销完毕的租金租期之和
        assetClassifyClient.setRemainingTerm(collectionBaseInfoService.countUnwrittenRentPhase(contractIds));
        // 逾期天数
        int days = rentCollectionDetailService.findLongestDayOverdueRent(contractIds);
        assetClassifyClient.setOverdueDays(days);
        //逾期金额(租金+本金)
        assetClassifyClient.setOverdueAmount(collectionService.listContractAmountByContractIds(contractIds, Boolean.TRUE, Boolean.TRUE).values().stream().mapToLong(Long::longValue).sum());
        //剩余风险敞口 所有剩余的本金+利息 弃用
        //Map<Long, Long> residualExposure = collectionService.listContractAmountByContractIds(contractIds, Boolean.FALSE, Boolean.TRUE);
        //assetClassifyClient.setResidualExposure(residualExposure.values().stream().mapToLong(Long::longValue).sum());
        //存量分险敞口
        assetClassifyClient.setStockRiskExposure(contractBaseInfoService.getStockRiskExposure(client.getId(), null, null));
        //资产余额（亿元)
        assetClassifyClient.setAssetBalance(contractBaseInfoService.getAssetBalance(contractIds, startRentContractMap));
        //合同到期日 最后一笔租金计划收取时间
        assetClassifyClient.setContractExpirationDate(contractBaseInfoService.getContractExpirationDate(contractIds));
        //剩余本金
        Map<Long, Long> residualPrincipal = collectionService.listContractAmountByContractIds(contractIds);
        log.info("AssetClassifyService init residualPrincipal {}", residualPrincipal);
        //合同编号
        Map<Long, String> nameMap = id2NameService.contractId2Name(residualPrincipal.keySet());
        residualPrincipal.forEach((key, value) -> {
            nameList.add(nameMap.get(key));
            valueList.add(value);
        });
        assetClassifyClient.setStartRentContractCodes(JSON.toJSONString(nameList));
        assetClassifyClient.setStartRentContractRemainingPrincipal(JSON.toJSONString(valueList));
        // 确定初分结果
        String initClassifyResult = assetClassifyClientService.ensureResult(days, corpCommerceInfo);
        assetClassifyClient.setInitClassifyResult(initClassifyResult);
        assetClassifyClient.setAwardRatio(assetClassifyClientService.getInitAwardRatio(corpCommerceInfo.getRiskControlIndustryClassify(),
                initClassifyResult, month));
        assetClassifyClient.setClassifyResult(initClassifyResult);
        assetClassifyClient.setReviewStatus(AssetClassifyReviewStatusEnum.PROCESS.name());
        // 初始化拨备数据
        try {
            generateDefaultWithdrawalRatios(assetClassifyClient, nameMap.keySet());
        } catch (Exception e) {
            log.error("初始化拨备计提比例发生异常[{}]", JSONUtil.toJsonStr(assetClassifyClient), e);
        }
        // 初始化风险因子
        try {
            generateRiskFactor(assetClassifyClient);
        } catch (Exception e) {
            log.error("初始化风险因子发生异常[{}]", JSONUtil.toJsonStr(assetClassifyClient), e);
        }
        return assetClassifyClient;
    }

    private void generateRiskFactor(AssetClassifyClient assetClassifyClient) {
        List<AssetClassifyClientRiskFactorTemplate> templates = assetClassifyClientRiskFactorTemplateService.list();
        List<RiskFactorWrapper> wrappers = new ArrayList<>();
        for (AssetClassifyClientRiskFactorTemplate template : templates) {
            RiskFactorWrapper riskFactor = new RiskFactorWrapper();
            riskFactor.setTemplateId(template.getId());
            riskFactor.setHasRisk(template.getHasRisk());
            wrappers.add(riskFactor);
        }
        assetClassifyClient.setRiskFactor(JSON.toJSONString(wrappers));
    }

    /**
     * 初始化默认拨备计提比例
     *
     * @param assetClassifyClient 客户信息
     */
    private void generateDefaultWithdrawalRatios(AssetClassifyClient assetClassifyClient, Set<Long> contractIds) {
        if (ObjectUtil.isEmpty(contractIds)) {
            return;
        }
        // 相关借据
        List<ContractReceipt> receipts = contractReceiptService.list(Wrappers.<ContractReceipt>lambdaQuery()
                .in(ContractReceipt::getContractId, contractIds));
        if (ObjectUtil.isEmpty(receipts)) {
            return;
        }
        List<WithdrawalRatioWrapper> provisions = new ArrayList<>();
        for (ContractReceipt receipt : receipts) {
            WithdrawalRatioWrapper withdrawalRatioRsp = new WithdrawalRatioWrapper();
            withdrawalRatioRsp.setContractId(receipt.getContractId());
            withdrawalRatioRsp.setReceiptId(receipt.getId());
            withdrawalRatioRsp.setReceiptCode(receipt.getReceiptCode());
            withdrawalRatioRsp.setWithdrawalRatio(getDefaultWithdrawalRatio(assetClassifyClient.getInitClassifyResult(), receipt));
            provisions.add(withdrawalRatioRsp);
        }
        assetClassifyClient.setProvisions(JSON.toJSONString(provisions));
    }

    // 根据公式计算拨备数据
    private Integer getDefaultWithdrawalRatio(String assetClassifyResult, ContractReceipt contractReceipt) {
        ContractBaseInfo contractBaseInfo = contractBaseInfoMapper.selectById(contractReceipt.getContractId());
        List<ContractRentActual> contractRentActualList = contractRentActualService.listByReceipt(contractReceipt.getId());
        // 计算剩余租期
        long remainingMonths = 0;
        LocalDate now = LocalDate.now();
        contractRentActualList.removeIf(e -> e.getCashFlowDate().isBefore(now));
        if (CollectionUtil.isNotEmpty(contractRentActualList)) {
            contractRentActualList.sort(Comparator.comparing(ContractRentActual::getCashFlowDate));
            LocalDate last = contractRentActualList.get(contractRentActualList.size() - 1).getCashFlowDate();
            remainingMonths = LocalDateTimeUtil.between(LocalDate.of(now.getYear(), now.getMonthValue(), 1).atStartOfDay(), LocalDate.of(last.getYear(), last.getMonthValue(), last.lengthOfMonth()).atStartOfDay(), ChronoUnit.MONTHS);
        }
        // 确定项目类型
        String projectType = null;
//        CorpCommerceInfo corpCommerceInfo = corpCommerceInfoService.detail(contractBaseInfo.getClientId(), null);
        List<CorpCommerceInfo> corpCommerceInfoList = corpCommerceInfoService.findByClientId(contractBaseInfo.getClientId());
        CorpCommerceInfo corpCommerceInfo = Optional.ofNullable(corpCommerceInfoList).map(e -> e.get(0)).orElse(null);
        if (Objects.nonNull(corpCommerceInfo)) {
            RiskControlIndustryClassify rcic = RiskControlIndustryClassify.of(corpCommerceInfo.getRiskControlIndustryClassify());
            if (Objects.nonNull(rcic)) {
                switch (rcic) {
//                    case ENGINEERING_MACHINERY: {
//                        projectType = ProvisionRadioEnum.CONSTRUCTION_MACHINERY_NORMAL.getProjectClassify();
//                        break;
//                    }
                    case PUBLIC_UTILITIES:
                    case TRAVEL:
                    case CIVIL_CONSUMPTION: {
                        projectType = ProvisionRadioEnum.PUBLIC_NORMAL.getProjectClassify();
                        break;
                    }
                    default: {
                        projectType = ProvisionRadioEnum.OTHER_NORMAL.getProjectClassify();
                    }
                }
            }
        }
        String value = kpiParameterConfigService.ensureProvision(projectType, assetClassifyResult, remainingMonths);
        if (StrUtil.isBlank(value)) {
            return 0;
        } else {
            return new BigDecimal(value).multiply(BigDecimal.valueOf(10000)).intValue();
        }
    }

    @Transactional(rollbackFor = Throwable.class)
    public void initBalance(Integer year, Integer quarter) {
        log.info("initBalance init year {}, quarter {}", year, quarter);
        // 校验是否已有数据
        AssetClassify assetClassify = assetClassifyService.getOne(Wrappers.<AssetClassify>lambdaQuery()
                .eq(AssetClassify::getYear, year)
                .eq(AssetClassify::getQuarter, quarter)
                .orderByDesc(AssetClassify::getId)
                .last(StringUtil.mysqlLimitOne()));
        if (ObjectUtil.isEmpty(assetClassify)) {
            throw new MithrasException("资产五级分类该时间季度不存在");
        }
        //该季度的资产五级分类客户信息
        List<AssetClassifyClient> assetClassifyClientList = assetClassifyClientService.listByAssetClassifyId(assetClassify.getId());
        // 找到所有起租的合同
        Map<Long, Long> startRentContractMap = collectionService.listContractAmountByContractIds(null);
        List<Long> startRentContractIds = new ArrayList<>();
        startRentContractMap.forEach((key, value) -> {
            if(LongUtil.null2zero(value) > 0){
                startRentContractIds.add(key) ;
            }
        });
        if(CollectionUtil.isEmpty(startRentContractIds)){
            log.info("没有剩余本金为0的合同");
            throw new MithrasException("没有剩余本金为0的合同");
        }
        List<ContractBaseInfo> startRentContractList = contractBaseInfoService.listByIds(startRentContractIds);
        if (CollectionUtil.isEmpty(startRentContractList)) {
            log.info("没有处于起租状态的合同");
            throw new MithrasException("没有处于起租状态的合同");
        }
        // 按照客户id分组
        Map<Long, List<ContractBaseInfo>> contractMap = startRentContractList.stream().collect(Collectors.groupingBy(ContractBaseInfo::getClientId));
        List<AssetClassifyClient> toUpdateClientList = new LinkedList<>();
        for (AssetClassifyClient assetClassifyClient : assetClassifyClientList) {
            for (Map.Entry<Long, List<ContractBaseInfo>> entry : contractMap.entrySet()) {
                Long clientId = entry.getKey();
                List<ContractBaseInfo> contractBaseInfoList = entry.getValue();
                if (clientId.equals(assetClassifyClient.getClientId())) {
                    Long assetBalance = 0L;
                    List<Long> contractIds = contractBaseInfoList.stream().map(ContractBaseInfo::getId).collect(Collectors.toList());
                    for (Long contractId : contractIds) {
                        assetBalance += startRentContractMap.getOrDefault(contractId, 0L);
                    }
                    assetClassifyClient.setAssetBalance(assetBalance);
                    toUpdateClientList.add(assetClassifyClient);
                }

            }
        }

        if(CollectionUtil.isNotEmpty(toUpdateClientList)){
            assetClassifyClientService.updateBatchById(toUpdateClientList);
        }
    }

    public void downloadSummaryFile(Long assetClassifyId, OutputStream outputStream) throws Exception {
        AssetClassify assetClassify = this.getById(assetClassifyId);
        if (Objects.isNull(assetClassify)) {
            throw new MithrasException("资产五级分类数据不存在");
        }
        assetClassifySummaryRender.render(outputStream, assetClassify);
    }

    /**
     * 资产五级分类：删除客户
     **/
    @Transactional(rollbackFor = Throwable.class)
    public void deleteClient(AssetClassifyClientRemoveREQ req){
        AssetClassifyClient byId = assetClassifyClientService.getById(req.getId());
        if(ObjectUtil.isEmpty(byId)){
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        if(AssetClassifyReviewStatusEnum.FINISH.name().equals(byId.getReviewStatus())){
            throw new MithrasException("已复核，无法删除");
        }
        ProcessPageReq flowReq = new ProcessPageReq();
        flowReq.setModelKey(ProcessModelTypeEnum.AssetClassifyReviewFlow.name());
        flowReq.setBusinessKey(String.valueOf(req.getAssetClassifyId()));
        flowReq.setProcessStatusList(Arrays.asList(ProcessBusinessStatusEnum.RUNNING.getType(),
                ProcessBusinessStatusEnum.SUSPEND.getType(), ProcessBusinessStatusEnum.PASS.getType(), ProcessBusinessStatusEnum.PASS_ALL.getType()));
        flowReq.setSortType(1);
        Page<ProcessResp> flowRespPage = flowTaskApiService.queryProcess(flowReq);
        if(ObjectUtil.isNotEmpty(flowRespPage) && flowRespPage.getTotal() > 0){
            throw new MithrasException("已发起复核审批，无法删除");
        }
        assetClassifyClientService.removeById(req.getId());
        // 季中初分：增加客户为空判断
        AssetClassify assetClassify = assetClassifyService.getById(byId.getAssetClassifyId());
        if (ObjectUtil.isNotEmpty(assetClassify) && assetClassify.getInitType().equals(AssetClassifyInitTypeEnum.QUARTER_MID.name())){
            Long assetClassifyId = assetClassify.getId();
            List<AssetClassifyClient> clientList = assetClassifyClientService.listByAssetClassifyId(assetClassifyId);
            // 如果客户列表为空则说明，全部客户已经删除，清理节点数据与主表数据，以便重新发起季中初分
            if (CollectionUtil.isEmpty(clientList)){
                // 清理上次的初分的节点数据
                assetClassifyNodeRecordService.remove(Wrappers.<AssetClassifyNodeRecord>lambdaQuery()
                        .eq(AssetClassifyNodeRecord::getAssetClassifyId, assetClassifyId));
                // 清理初分主表数据
                assetClassifyService.removeById(assetClassifyId);
            }
        }
    }

    /**
     * 季中初分，返回比对信息
     **/
    @Transactional(rollbackFor = Throwable.class)
    public AssetManualDivisionRSP midQuarterInit(MidQuarterClientListREQ req) {
        log.info("AssetClassifyService midQuarterInit year {}, quarter {}", req.getYear(), req.getQuarter());
        StopWatch stopWatch = new StopWatch("五级分类-季中初分");
        stopWatch.start("客户校验");
        List<Long> nowClientIds = req.getClientIds();
        err(CollectionUtil.isEmpty(nowClientIds), "请选择一条数据");

        // 找到所有起租的合同
        Map<Long, Long> startRentContractMap = collectionService.listContractAmountByContractIds(null);
        List<Long> startRentContractIds = new ArrayList<>();
        startRentContractMap.forEach((key, value) -> {
            if(LongUtil.null2zero(value) > 0){
                startRentContractIds.add(key) ;
            }
        });
        if(CollectionUtil.isEmpty(startRentContractIds)){
            log.info("季中初分：没有剩余本金为0的合同");
            return null;
        }
        // 查询起租合同列表
        List<ContractBaseInfo> startRentContractList = contractBaseInfoService.listByIds(startRentContractIds);
        // 季中初分：过滤出选定客户的合同
        if (AssetClassifyInitTypeEnum.QUARTER_MID.name().equals(req.getInitType()) && CollectionUtil.isNotEmpty(nowClientIds)) {
            startRentContractList = startRentContractList.stream()
                    .filter(contract -> nowClientIds.contains(contract.getClientId()))
                    .collect(Collectors.toList());
            log.info("季中初分：筛选出{}个选定客户的合同", startRentContractList.size());
        }

        Long oldAssetClassifyId = null;
        int fistFlag = YesOrNoNumberEnum.YES.getCode();
        // 校验是否已有数据
        AssetClassify assetClassify = assetClassifyService.getOne(Wrappers.<AssetClassify>lambdaQuery()
                .eq(AssetClassify::getYear, req.getYear())
                .eq(AssetClassify::getQuarter, req.getQuarter())
                .eq(AssetClassify::getInitType, req.getInitType())
                .orderByDesc(AssetClassify::getId)
                .last(StringUtil.mysqlLimitOne()));
        List<AssetClassifyClient> oldAssetClassifyClient = null;
        //保存季中初分数据
        if (ObjectUtil.isEmpty(assetClassify)) {
            assetClassify = new AssetClassify();
            assetClassify.setYear(req.getYear());
            assetClassify.setQuarter(req.getQuarter());
            assetClassify.setInitType(req.getInitType());
            assetClassify.setFinish(YesOrNoNumberEnum.NO.getCode());
            assetClassifyService.save(assetClassify);
        } else {
            //非第一次季中初分
            fistFlag = YesOrNoNumberEnum.NO.getCode();
            // 校验审批流程
            checkApprovalProcess(assetClassify,nowClientIds);
            // 获取已有的五级分类客户列表数据
            oldAssetClassifyClient = assetClassifyClientService.listByAssetClassifyId(assetClassify.getId());
            oldAssetClassifyId = assetClassify.getId();
            //清理上次的初分的节点数据
            assetClassifyNodeRecordService.remove(Wrappers.<AssetClassifyNodeRecord>lambdaQuery()
                    .eq(AssetClassifyNodeRecord::getAssetClassifyId, assetClassify.getId()));
            // 重置初分主表状态
            assetClassify.setFinish(YesOrNoNumberEnum.NO.getCode());
            assetClassifyService.updateById(assetClassify);
        }
        List<AssetClassifyClient> nowClientList = new LinkedList<>();
        List<AssetClassifyClient> toSaveClientList = new LinkedList<>();
        List<AssetClassifyClient> toUpdateClientList = new LinkedList<>();
        List<AssetClassifyClient> toDeleteClientList = new LinkedList<>();
        if (CollectionUtil.isEmpty(startRentContractList)) {
            log.info("季中初分：没有处于起租状态的合同");
            return compareAssetClassify(null, oldAssetClassifyClient, toSaveClientList, toUpdateClientList, toDeleteClientList);
        }
        LocalDateTime targetDateTime = LocalDateTime.now();
        // 按照客户id分组
        Map<Long, List<ContractBaseInfo>> contractMap = startRentContractList.stream().collect(Collectors.groupingBy(ContractBaseInfo::getClientId));
        //查询客户下最长租期
        Map<Long, Long> clientId2MaxMonthMap = contractBaseInfoMapper.clientMaxLeaseMonth(contractMap.keySet()).stream().collect(Collectors.toMap(ClientMaxLeaseMonthDTO::getClientId,
                ClientMaxLeaseMonthDTO::getLeaseMonthCount, (a, b) -> a));
        //查询客户风险
        Map<Long, CorpCommerceInfo> clientId2CorpCommerceInfoMap = corpCommerceInfoService.list(contractMap.keySet()).stream().collect(Collectors.toMap(CorpCommerceInfo::getClientId, e -> e, (a, b) -> a));
        stopWatch.stop();
        stopWatch.start("客户分类");
        //客户五级分类
        CompletionService<AssetClassifyClient> completionService = new ExecutorCompletionService<>(threadPool);
        Long assetClassifyId = assetClassify.getId();
        for (Map.Entry<Long, List<ContractBaseInfo>> entry : contractMap.entrySet()) {
            Long finalOldAssetClassifyId = oldAssetClassifyId;
            completionService.submit(() -> {
                AssetClassifyClient assetClassifyClient = SpringContextHolder.getBean(AssetClassifyService.class).handleClient(entry.getKey(), entry.getValue(),
                        clientId2MaxMonthMap.getOrDefault(entry.getKey(), 0L), clientId2CorpCommerceInfoMap.getOrDefault(entry.getKey(), new CorpCommerceInfo()), startRentContractMap);
                if (Objects.nonNull(assetClassifyClient)) {
                    // 查询一下是否有上一次分类结果
                    AssetClassifyClient lastOne = assetClassifyClientService.getLastOneByClientId(entry.getKey(), finalOldAssetClassifyId);
                    if (Objects.nonNull(lastOne)) {
                        assetClassifyClient.setLastClassifyResult(lastOne.getClassifyResult());
                    }
                    assetClassifyClient.setAssetClassifyId(assetClassifyId);
                }
                return assetClassifyClient;
            });
        }
        try {
            for (int i = 0; i < contractMap.size(); i++) {
                Future<AssetClassifyClient> f = completionService.take();
                nowClientList.add(f.get());
            }
        } catch (InterruptedException | ExecutionException e) {
            log.error("季中初分错误", e);
            Thread.currentThread().interrupt();
            throw new MithrasException("季中初分失败");
        }
        stopWatch.stop();
        stopWatch.start("保存数据");
        // 季中初分需要保存所有客户数据
        //比对新老数据，判断新增，更新
        AssetManualDivisionRSP rsp = compareAssetClassify(nowClientList, oldAssetClassifyClient, toSaveClientList, toUpdateClientList, toDeleteClientList);
        // 保存资产五级分类客户表
        if(CollectionUtil.isNotEmpty(toSaveClientList)){
            assetClassifyClientService.saveBatch(toSaveClientList);
        }
        if(CollectionUtil.isNotEmpty(toUpdateClientList)){
            assetClassifyClientService.updateBatchById(toUpdateClientList);
        }
        // 保存资产五级分类业务节点信息
        AssetClassifyNodeRecord initNodeRecord = new AssetClassifyNodeRecord();
        initNodeRecord.setAssetClassifyId(assetClassify.getId());
        initNodeRecord.setNodeName(AssetClassifyBizNodeEnum.INIT.name());
        initNodeRecord.setStartTime(targetDateTime);
        initNodeRecord.setEndTime(LocalDateTime.now());
        initNodeRecord.setNodeStatue(AssetClassifyStatusEnum.FINISH.name());
        assetClassifyNodeRecordService.save(initNodeRecord);
        AssetClassifyNodeRecord reviewNodeRecord = new AssetClassifyNodeRecord();
        reviewNodeRecord.setAssetClassifyId(assetClassify.getId());
        reviewNodeRecord.setNodeName(AssetClassifyBizNodeEnum.REVIEW.name());
        reviewNodeRecord.setStartTime(targetDateTime);
        reviewNodeRecord.setNodeStatue(AssetClassifyStatusEnum.WAIT.name());
        assetClassifyNodeRecordService.save(reviewNodeRecord);
        // 生成版本
        assetClassifyLibVersionService.recordVersion(assetClassify.getId(), VersionTypeEnum.EFFECT, null, null, VersionTypeConstants.NORMAL);
        //填充是否第一次标识
        rsp.setFirstFlag(fistFlag);
        stopWatch.stop();
        log.info("季中初分完成: 耗时={}ms",stopWatch.prettyPrint(TimeUnit.SECONDS));
        return rsp;
    }

    private void checkApprovalProcess(AssetClassify assetClassify,List<Long> nowClientIds){
        // 判断所选客户是否处于季中资产分类任一审批流程中，若存在，不支持发起
        // 查询存在的客户数据
        List<AssetClassifyClient> oldClienList = assetClassifyClientService.list(Wrappers.<AssetClassifyClient>lambdaQuery()
                .eq(AssetClassifyClient::getAssetClassifyId,assetClassify.getId()));
                //.in(AssetClassifyClient::getClientId, nowClientIds));
        //.eq(AssetClassifyClient::getReviewStatus, AssetClassifyReviewStatusEnum.PROCESS)
        // 存在的客户数据不为空进行一下校验，为空说明客户全部被删除，不进行校验可发起初分
        if (CollectionUtil.isNotEmpty(oldClienList)){
            // 筛选已存在的客户数据中有当前初分选中的客户
            Map<Boolean, List<AssetClassifyClient>> partitioned = oldClienList.stream()
                    .collect(Collectors.partitioningBy(
                            client -> nowClientIds.contains(client.getClientId())
                    ));
            List<AssetClassifyClient> nowClientList = partitioned.get(true);     // 包含的
            List<AssetClassifyClient> notNowClientList = partitioned.get(false); // 不包含的
            // 1.校验是否存在初分未复合流程
            // 1.1 校验包含的
            List<AssetClassifyClient> clienProcessList = nowClientList.stream()
                    .filter(client -> AssetClassifyReviewStatusEnum.PROCESS.name().equals(client.getReviewStatus()))
                    .collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(clienProcessList)){
                StringBuilder messageBuilder = new StringBuilder();
                messageBuilder.append("客户");
                for (int i = 0; i < clienProcessList.size(); i++) {
                    AssetClassifyClient acc = clienProcessList.get(i);
                    messageBuilder.append("【").append(acc.getClientName()).append("】");
                    // 如果不是最后一个元素，才加逗号
                    if (i < clienProcessList.size() - 1) {
                        messageBuilder.append(",");
                    }
                }
                messageBuilder.append("已初分【").append(AssetClassifyReviewStatusEnum.PROCESS.getDisplay()).append("】,暂不支持发起季中初分");
                throw new MithrasException(messageBuilder.toString());
            }
            // 1.2 校验不包含的
            List<AssetClassifyClient> notClientList = notNowClientList.stream()
                    .filter(client -> AssetClassifyReviewStatusEnum.PROCESS.name().equals(client.getReviewStatus()))
                    .collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(notClientList)){
                StringBuilder messageBuilder = new StringBuilder();
                messageBuilder.append("客户");
                for (int i = 0; i < notClientList.size(); i++) {
                    AssetClassifyClient acc = notClientList.get(i);
                    messageBuilder.append("【").append(acc.getClientName()).append("】");
                    if (i < notClientList.size() - 1) {messageBuilder.append(",");}
                }
                messageBuilder.append("已初分【").append(AssetClassifyReviewStatusEnum.PROCESS.getDisplay()).append("】,暂不支持发起季中初分");
                throw new MithrasException(messageBuilder.toString());
            }
            // 2.校验是否存在复合流程
            AssetClassifyNodeRecord reviewNode = assetClassifyNodeRecordService.getOne(
                    Wrappers.<AssetClassifyNodeRecord>lambdaQuery()
                            .eq(AssetClassifyNodeRecord::getAssetClassifyId, assetClassify.getId())
                            .eq(AssetClassifyNodeRecord::getNodeName, AssetClassifyBizNodeEnum.REVIEW.name()).last("limit 1"));
            if (ObjectUtil.isNotEmpty(reviewNode)) {
                // 复核未完成则提示
                if (!reviewNode.getNodeStatue().equals(AssetClassifyStatusEnum.FINISH.name())){
                    StringBuilder messageBuilder = new StringBuilder();
                    messageBuilder.append("客户");
                    for (int i = 0; i < oldClienList.size(); i++) {
                        AssetClassifyClient acc = oldClienList.get(i);
                        messageBuilder.append("【").append(acc.getClientName()).append("】");
                        if (i < oldClienList.size() - 1) {messageBuilder.append(",");}
                    }
                    messageBuilder.append("已在【").append(AssetClassifyBizNodeEnum.REVIEW.getDisplay()).append("】流程,暂不支持发起季中初分");
                    throw new MithrasException(messageBuilder.toString());
                }
            }else {
                // 不存在复合流程则提示
                StringBuilder messageBuilder = new StringBuilder();
                messageBuilder.append("客户");
                for (int i = 0; i < oldClienList.size(); i++) {
                    AssetClassifyClient acc = oldClienList.get(i);
                    messageBuilder.append("【").append(acc.getClientName()).append("】");
                    if (i < oldClienList.size() - 1) {messageBuilder.append(",");}
                }
                messageBuilder.append("还未进行【").append(AssetClassifyBizNodeEnum.REVIEW.getDisplay()).append("】流程,暂不支持发起季中初分");
                throw new MithrasException(messageBuilder.toString());
            }
            // 3.校验是否存在评审会流程
            AssetClassifyNodeRecord reviewMeetingNode = assetClassifyNodeRecordService.getOne(
                    Wrappers.<AssetClassifyNodeRecord>lambdaQuery()
                            .eq(AssetClassifyNodeRecord::getAssetClassifyId, assetClassify.getId())
                            .eq(AssetClassifyNodeRecord::getNodeName, AssetClassifyBizNodeEnum.REVIEW_MEETING.name()).last("limit 1"));
            if (ObjectUtil.isNotEmpty(reviewMeetingNode)) {
                // 评审未完成则提示
                if (!reviewMeetingNode.getNodeStatue().equals(AssetClassifyStatusEnum.FINISH.name())){
                    StringBuilder messageBuilder = new StringBuilder();
                    messageBuilder.append("客户");
                    for (int i = 0; i < oldClienList.size(); i++) {
                        AssetClassifyClient acc = oldClienList.get(i);
                        messageBuilder.append("【").append(acc.getClientName()).append("】");
                        if (i < oldClienList.size() - 1) {messageBuilder.append(",");}
                    }
                    messageBuilder.append("已在【").append(AssetClassifyBizNodeEnum.REVIEW_MEETING.getDisplay()).append("】流程,暂不支持发起季中初分");
                    throw new MithrasException(messageBuilder.toString());
                }
            }else {
                // 不存在评审会流程则提示
                StringBuilder messageBuilder = new StringBuilder();
                messageBuilder.append("客户");
                for (int i = 0; i < oldClienList.size(); i++) {
                    AssetClassifyClient acc = oldClienList.get(i);
                    messageBuilder.append("【").append(acc.getClientName()).append("】");
                    if (i < oldClienList.size() - 1) {messageBuilder.append(",");}
                }
                messageBuilder.append("还未进行【").append(AssetClassifyBizNodeEnum.REVIEW_MEETING.getDisplay()).append("】流程,暂不支持发起季中初分");
                throw new MithrasException(messageBuilder.toString());
            }
            // 4.校验是否存在风委会流程
            AssetClassifyNodeRecord riskMeetingNode = assetClassifyNodeRecordService.getOne(
                    Wrappers.<AssetClassifyNodeRecord>lambdaQuery()
                            .eq(AssetClassifyNodeRecord::getAssetClassifyId, assetClassify.getId())
                            .eq(AssetClassifyNodeRecord::getNodeName, AssetClassifyBizNodeEnum.RISK_MEETING.name()).last("limit 1"));
            if (ObjectUtil.isNotEmpty(riskMeetingNode)) {
                if (!riskMeetingNode.getNodeStatue().equals(AssetClassifyStatusEnum.FINISH.name())){
                    StringBuilder messageBuilder = new StringBuilder();
                    messageBuilder.append("客户");
                    for (int i = 0; i < oldClienList.size(); i++) {
                        AssetClassifyClient acc = oldClienList.get(i);
                        messageBuilder.append("【").append(acc.getClientName()).append("】");
                        if (i < oldClienList.size() - 1) {messageBuilder.append(",");}
                    }
                    messageBuilder.append("已在【").append(AssetClassifyBizNodeEnum.RISK_MEETING.getDisplay()).append("】流程,暂不支持发起季中初分");
                    throw new MithrasException(messageBuilder.toString());
                }
            }else {
                // 不存在风委会流程则提示
                StringBuilder messageBuilder = new StringBuilder();
                messageBuilder.append("客户");
                for (int i = 0; i < oldClienList.size(); i++) {
                    AssetClassifyClient acc = oldClienList.get(i);
                    messageBuilder.append("【").append(acc.getClientName()).append("】");
                    if (i < oldClienList.size() - 1) {messageBuilder.append(",");}
                }
                messageBuilder.append("还未进行【").append(AssetClassifyBizNodeEnum.RISK_MEETING.getDisplay()).append("】流程,暂不支持发起季中初分");
                throw new MithrasException(messageBuilder.toString());
            }
        }
    }


}
