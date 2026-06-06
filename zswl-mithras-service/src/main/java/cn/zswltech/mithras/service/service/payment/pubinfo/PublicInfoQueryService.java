package cn.zswltech.mithras.payment.application.pubinfo;
import cn.zswltech.mithras.contract.core.application.ContractTenantryService;
import cn.zswltech.mithras.contract.core.application.ContractPledgeService;
import cn.zswltech.mithras.contract.core.application.ContractMortgageService;
import cn.zswltech.mithras.contract.core.application.ContractGuarantorService;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.json.JSONUtil;
import cn.zswl.oss.core.OssClient;
import cn.zswltech.flow.core.api.FlowTaskApiService;
import cn.zswltech.flow.core.domain.req.task.ProcessPageReq;
import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.flow.core.enums.ProcessBusinessStatusEnum;
import cn.zswltech.flow.core.util.Page;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.gruul.dao.dal.entity.UserDO;
import cn.zswltech.gruul.dao.dal.vo.AccountVO;
import cn.zswltech.mithras.api.payment.dto.pubinfo.*;
import cn.zswltech.mithras.dto.file.FileListRSP;
import cn.zswltech.mithras.payment.application.job.PaymentPublicInfoCopyRetryService;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.enums.JobEnum;
import cn.zswltech.mithras.service.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.payment.domain.enums.PaymentStatusEnum;
import cn.zswltech.mithras.payment.domain.enums.pubinfo.InvestigationResultEnum;
import cn.zswltech.mithras.payment.domain.enums.pubinfo.PublicInfoClientTypeEnum;
import cn.zswltech.mithras.payment.domain.enums.pubinfo.PublicInfoFileTypeEnum;
import cn.zswltech.mithras.payment.domain.enums.pubinfo.PublicInfoRowKeyEnum;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.client.ClientMapper;
import cn.zswltech.mithras.service.mapper.model.BaseModel;
import cn.zswltech.mithras.service.mapper.model.MaterialsList;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.Client;
import cn.zswltech.mithras.contract.mapper.model.contract.*;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.model.PaymentBaseInfo;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.model.pubinfo.PublicInfoConfig;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.model.pubinfo.PublicInfoQuery;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.model.pubinfo.PublicInfoRecord;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.PaymentBaseInfoMapper;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.pubinfo.PublicInfoQueryMapper;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.third.providence.entity.OuterInfoRecord;
import cn.zswltech.mithras.third.providence.service.impl.OuterInfoRecordService;
import cn.zswltech.mithras.system.service.Id2NameService;
import cn.zswltech.mithras.system.service.SysUserService;
import cn.zswltech.mithras.service.service.contract.*;
import cn.zswltech.mithras.service.service.materialsfile.FileService;
import cn.zswltech.mithras.service.service.materialsfile.MaterialsListService;
import cn.zswltech.mithras.payment.application.pubinfo.model.PublicInfoExcelModel;
import cn.zswltech.mithras.service.util.StringUtil;
import com.alibaba.excel.EasyExcelFactory;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.io.ByteStreams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author bigbear
 * @description 针对表【public_info_query(公开信息查询结果表)】的数据库操作Service实现
 * @createDate 2024-09-10 19:54:34
 */
@Slf4j
@Service
public class PublicInfoQueryService extends ServiceImpl<PublicInfoQueryMapper, PublicInfoQuery> implements PaymentPublicInfoCopyRetryService {

    @Resource
    private HttpServletResponse response;
    @Resource
    private FileService fileService;
    @Resource
    private OssClient ossClient;
    @Resource
    private FlowTaskApiService flowTaskApiService;
    @Resource
    private SysUserService sysUserService;
    @Resource
    private Id2NameService id2NameService;
    @Resource
    private PaymentBaseInfoMapper paymentBaseInfoMapper;
    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private ContractTenantryService contractTenantryService;
    @Resource
    private ContractGuarantorService contractGuarantorService;
    @Resource
    private ContractPledgeService contractPledgeService;
    @Resource
    private ContractMortgageService contractMortgageService;
    @Resource
    private PublicInfoConfigService publicInfoConfigService;
    @Resource
    private PublicInfoRecordService publicInfoRecordService;
    @Resource
    private MaterialsListService materialsListService;
    @Resource
    private PublicInfoQueryService thisService;
    @Resource
    private OuterInfoRecordService outerInfoRecordService;
    @Resource
    private TransactionTemplate transactionTemplate;
    @Resource
    private ClientMapper clientMapper;

    public List<PublicInfoClientListRSP> clientList(PublicInfoClientListREQ req) {
        log.info("公开信息-客户列表查询请求参数req: {}", req);
        PaymentBaseInfo paymentBaseInfo = paymentBaseInfoMapper.selectById(req.getPaymentId());
        Assert.notNull(paymentBaseInfo, () -> MithrasException.newException("付款申请" + ResultMsg.RECORD_NOT_EXIST));
        Assert.notNull(paymentBaseInfo.getContractId(), () -> MithrasException.newException("合同" + ResultMsg.RECORD_NOT_EXIST));
        ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(paymentBaseInfo.getContractId());
        Assert.notNull(contractBaseInfo, () -> MithrasException.newException("合同" + ResultMsg.RECORD_NOT_EXIST));
        Client client = clientMapper.selectById(contractBaseInfo.getClientId());
        // 多线程找到客户信息
        List<PublicInfoClientListRSP> publicInfoClientListRSPS = multiThreadQueryClientInfo(contractBaseInfo.getId(), paymentBaseInfo.getId());
        if (ObjectUtil.isNotEmpty(publicInfoClientListRSPS)) {
            publicInfoClientListRSPS.forEach(publicInfoClientListRSP -> {
                publicInfoClientListRSP.setOriginClientType(client.getClientType());
            });
        }
        return publicInfoClientListRSPS;
    }

    private List<PublicInfoClientListRSP> multiThreadQueryClientInfo(Long contractId, Long paymentId) {
        try {
            // 承租人
            CompletableFuture<List<Long>> tenantryIdsFuture = getTenantryIdsFuture(contractId);
            // 担保人
            CompletableFuture<List<Long>> guarantorIdsFuture = getGuarantorIdsFuture(contractId);
            // 抵押人
            CompletableFuture<List<Long>> mortgageIdsFuture = getMortgageIdsFuture(contractId);
            // 质押人
            CompletableFuture<List<Long>> pledgeIdsFuture = getPledgeIdsFuture(contractId);

            // 将各自的结果取出来
            List<Long> tenantryIds = tenantryIdsFuture.get();
            List<Long> guarantorIds = guarantorIdsFuture.get();
            List<Long> mortgageIds = mortgageIdsFuture.get();
            List<Long> pledgeIds = pledgeIdsFuture.get();

            // 合并
            List<Long> clientIds = CollUtil.unionAll(tenantryIds, guarantorIds, mortgageIds, pledgeIds).stream().distinct().collect(Collectors.toList());
            List<PublicInfoQuery> publicInfoQueries = thisService.list(Wrappers.<PublicInfoQuery>lambdaQuery()
                    .eq(PublicInfoQuery::getPaymentId, paymentId)
                    .in(PublicInfoQuery::getClientId, clientIds)
            );
            if (CollUtil.isEmpty(publicInfoQueries)) {
                Map<Long, String> clientId2Name = id2NameService.clientId2Name(clientIds);
                log.info("公开信息-客户列表查询结果为空， 封装客户标签返回");
                return clientIds.stream().map(clientId -> {
                    PublicInfoClientListRSP rsp = new PublicInfoClientListRSP();
                    buildNonUseUser(clientId, tenantryIds, rsp, clientId2Name);
                    return rsp;
                }).collect(Collectors.toList());
            }
            // 找到子表信息
            Map<Long, Map<String, PublicInfoRecord>> publicInfoRecordMap = new HashMap<>(8);
            List<PublicInfoRecord> publicInfoRecordList = publicInfoRecordService.list(Wrappers.<PublicInfoRecord>lambdaQuery()
                    .in(PublicInfoRecord::getPublicInfoQueryId, publicInfoQueries.stream().map(PublicInfoQuery::getId).collect(Collectors.toList())));
            Map<Long, List<PublicInfoRecord>> infoRecordMap = publicInfoRecordList.stream().collect(Collectors.groupingBy(PublicInfoRecord::getPublicInfoQueryId));
            infoRecordMap.forEach((publicInfoQueryId, publicInfoRecords) -> {
                Map<String, PublicInfoRecord> recordMap = publicInfoRecords.stream().collect(Collectors.toMap(PublicInfoRecord::getConfigKey, Function.identity(), (o1, o2) -> o1));
                publicInfoRecordMap.put(publicInfoQueryId, recordMap);
            });

            // 封装结果返回
            Map<Long, String> clientId2NameMap = id2NameService.clientId2Name(clientIds);
            // 因为配置是只有一份，直接遍历就好了
            List<PublicInfoClientListRSP> publicInfoClientListRspList = new LinkedList<>();
            Map<Long, List<PublicInfoQuery>> listMap = publicInfoQueries.stream().collect(Collectors.groupingBy(PublicInfoQuery::getClientId));
            // 这里就算只加了一份，但是处理结果的时候还是要以上面的总的客户来遍历
            for (Long currentClientId : clientIds) {
                PublicInfoClientListRSP rsp = new PublicInfoClientListRSP();
                List<PublicInfoQuery> queryList = listMap.get(currentClientId);
                if (CollUtil.isEmpty(queryList)) {
                    buildNonUseUser(currentClientId, tenantryIds, rsp, clientId2NameMap);
                    publicInfoClientListRspList.add(rsp);
                    continue;
                }
                PublicInfoQuery publicInfoQuery = queryList.get(0);
                rsp.setClientId(publicInfoQuery.getClientId());
                rsp.setClientName(clientId2NameMap.get(publicInfoQuery.getClientId()));
                if (tenantryIds.contains(publicInfoQuery.getClientId())) {
                    // 只有是在承租人列表里面的才是承租人
                    rsp.setClientType(PublicInfoClientTypeEnum.TENANTRY.name());
                } else {
                    rsp.setClientType(PublicInfoClientTypeEnum.GUARANTOR.name());
                }

                rsp.setQueryIntervalList(queryList.stream().map(obj -> {
                    PublicInfoClientListRSP.QueryIntervalListRSP interval = new PublicInfoClientListRSP.QueryIntervalListRSP();
                    interval.setQueryFrom(obj.getQueryFrom().format(DateTimeFormatter.ofPattern(DatePattern.NORM_DATE_PATTERN)));
                    interval.setQueryTo(obj.getQueryTo().format(DateTimeFormatter.ofPattern(DatePattern.NORM_DATE_PATTERN)));
                    interval.setId(obj.getId());
                    // 设置有必填未填
                    interval.setIsExistRequiredNotFill(checkIsExistRequiredNotFill(publicInfoQuery, publicInfoRecordMap.get(obj.getId())));
                    return interval;
                }).collect(Collectors.toList()));
                rsp.setIsExistRequiredNotFill(rsp.getQueryIntervalList().stream().map(PublicInfoClientListRSP.QueryIntervalListRSP::getIsExistRequiredNotFill).collect(Collectors.toList()).contains(true));
                publicInfoClientListRspList.add(rsp);
            }
            return publicInfoClientListRspList;

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("线程中断异常", e);
            throw new MithrasException(e.getMessage());
        } catch (ExecutionException e) {
            log.error("线程执行异常", e);
            throw new MithrasException(e.getMessage());
        }
    }

    public CompletableFuture<List<Long>> getPledgeIdsFuture(Long contractId) {
        return CompletableFuture.supplyAsync(() -> {
            List<ContractPledge> contractPledges = contractPledgeService.list(Wrappers.<ContractPledge>lambdaQuery()
                    .eq(ContractPledge::getContractId, contractId));
            if (CollUtil.isEmpty(contractPledges)) {
                return Collections.emptyList();
            }
            List<Long> pledgeIds = new LinkedList<>();
            contractPledges.forEach(contractPledge -> {
                List<Long> ids = JSONUtil.toList(contractPledge.getPledgeIds(), Long.class);
                pledgeIds.addAll(ids);
            });
            return pledgeIds;
        });
    }

    public CompletableFuture<List<Long>> getMortgageIdsFuture(Long contractId) {
        return CompletableFuture.supplyAsync(() -> {
            List<ContractMortgage> contractMortgages = contractMortgageService.list(Wrappers.<ContractMortgage>lambdaQuery()
                    .eq(ContractMortgage::getContractId, contractId));
            if (CollUtil.isEmpty(contractMortgages)) {
                return Collections.emptyList();
            }
            List<Long> mortgageIds = new LinkedList<>();
            contractMortgages.forEach(contractMortgage -> {
                List<Long> ids = JSONUtil.toList(contractMortgage.getMortgageIds(), Long.class);
                mortgageIds.addAll(ids);
            });
            return mortgageIds;
        });
    }

    public CompletableFuture<List<Long>> getGuarantorIdsFuture(Long contractId) {
        return CompletableFuture.supplyAsync(() -> {
            List<ContractGuarantor> contractGuarantors = contractGuarantorService.list(Wrappers.<ContractGuarantor>lambdaQuery()
                    .eq(ContractGuarantor::getContractId, contractId));
            if (CollUtil.isEmpty(contractGuarantors)) {
                return Collections.emptyList();
            }
            List<Long> guarantorIds = new LinkedList<>();
            contractGuarantors.forEach(contractGuarantor -> {
                List<Long> ids = JSONUtil.toList(contractGuarantor.getGuarantorIds(), Long.class);
                guarantorIds.addAll(ids);
            });
            return guarantorIds;
        });
    }

    public CompletableFuture<List<Long>> getTenantryIdsFuture(Long contractId) {
        return CompletableFuture.supplyAsync(() -> {
            List<ContractTenantry> contractTenancies = contractTenantryService.list(Wrappers.<ContractTenantry>lambdaQuery()
                    .eq(ContractTenantry::getContractId, contractId));
            if (CollUtil.isEmpty(contractTenancies)) {
                return Collections.emptyList();
            }
            return contractTenancies.stream().map(ContractTenantry::getLesseeId).collect(Collectors.toList());
        });
    }

    private static void buildNonUseUser(Long clientId, List<Long> tenantryIds, PublicInfoClientListRSP rsp, Map<Long, String> clientId2Name) {
        if (tenantryIds.contains(clientId)) {
            rsp.setClientType(PublicInfoClientTypeEnum.TENANTRY.name());
        } else {
            rsp.setClientType(PublicInfoClientTypeEnum.GUARANTOR.name());
        }
        rsp.setClientId(clientId);
        rsp.setClientName(clientId2Name.get(clientId));
        rsp.setQueryIntervalList(Collections.emptyList());
        rsp.setIsExistRequiredNotFill(false);
    }

    /**
     * 校验是否有必填未填
     *
     * @param publicInfoQuery 公开信息
     * @param recordMap       公开信息记录
     * @return Boolean 返回false说明没有问题，返回true则说明有必填未填
     */
    public Boolean checkIsExistRequiredNotFill(PublicInfoQuery publicInfoQuery, Map<String, PublicInfoRecord> recordMap) {
        if (CharSequenceUtil.isBlank(publicInfoQuery.getProcessInstanceId())) {
            return false;
        }
        // 校验一次找一次流程列表
        ProcessResp processResp = flowTaskApiService.queryProcessById(publicInfoQuery.getProcessInstanceId());
        Assert.notNull(processResp, () -> MithrasException.newException("流程id: " + publicInfoQuery.getProcessInstanceId() + ResultMsg.RECORD_NOT_EXIST));
        // 判断当前节点是不是【项目经理节点】
        if ("userTask_projectmanager".equals(processResp.getCurTaskActivityIds())) {
            // 判断是否填写了【项目经理意见】
            for (Map.Entry<String, PublicInfoRecord> recordEntry : recordMap.entrySet()) {
                // 选择了需要解释说明，看看项目经理是不是填了东西，如果存在未填的的就返回true
                PublicInfoRecord infoRecord = recordEntry.getValue();
                if (InvestigationResultEnum.WITH_EXPLANATION.name().equals(infoRecord.getInvestigationType()) &&
                        CharSequenceUtil.isBlank(infoRecord.getProjectmanagerExplain())) {
                    return true;
                }
            }
        }
        return false;
    }

    public Boolean queryTableCheck(PublicInfoSubmitCheckREQ req) {
        // 校验一次找一次流程列表
        ProcessResp processResp = flowTaskApiService.queryProcessById(req.getProcessInstanceId());
        Assert.notNull(processResp, () -> MithrasException.newException("流程id：" + req.getProcessInstanceId() + ResultMsg.RECORD_NOT_EXIST));
        // 找到公开信息
        List<PublicInfoQuery> publicInfoQueries = thisService.list(Wrappers.<PublicInfoQuery>lambdaQuery().eq(PublicInfoQuery::getProcessInstanceId, req.getProcessInstanceId()));
        if (CollUtil.isEmpty(publicInfoQueries)) {
            return true;
        }
        // 找到子表信息
        Map<Long, Map<String, PublicInfoRecord>> publicInfoRecordMap = new HashMap<>(8);
        publicInfoRecordService.list(Wrappers.<PublicInfoRecord>lambdaQuery()
                        .in(PublicInfoRecord::getPublicInfoQueryId, publicInfoQueries.stream().map(PublicInfoQuery::getId).collect(Collectors.toList()))
                ).stream().collect(Collectors.groupingBy(PublicInfoRecord::getPublicInfoQueryId))
                .forEach((publicInfoQueryId, publicInfoRecords) -> {
                    Map<String, PublicInfoRecord> recordMap = publicInfoRecords.stream().collect(Collectors.toMap(PublicInfoRecord::getConfigKey, Function.identity(), (o1, o2) -> o1));
                    publicInfoRecordMap.put(publicInfoQueryId, recordMap);
                });

        for (PublicInfoQuery publicInfoQuery : publicInfoQueries) {
            // 存在一个必填未填直接返回true
            if (Boolean.TRUE.equals(checkIsExistRequiredNotFill(publicInfoQuery, publicInfoRecordMap.get(publicInfoQuery.getId())))) {
                return false;
            }
        }
        return true;
    }

    public PublicInfoQueryRSP queryTableResult(PublicInfoQueryREQ req) {
        PublicInfoQuery publicInfoQuery = thisService.getById(req.getId());
        Assert.notNull(publicInfoQuery, () -> MithrasException.newException("公开信息" + ResultMsg.RECORD_NOT_EXIST));
        PublicInfoQueryRSP rsp = new PublicInfoQueryRSP();
        rsp.setId(publicInfoQuery.getId());
        rsp.setQueryFrom(publicInfoQuery.getQueryFrom().format(DateTimeFormatter.ofPattern(DatePattern.NORM_DATE_PATTERN)));
        rsp.setQueryTo(publicInfoQuery.getQueryTo().format(DateTimeFormatter.ofPattern(DatePattern.NORM_DATE_PATTERN)));
        rsp.setClientId(publicInfoQuery.getClientId());
        rsp.setConfirmBy(publicInfoQuery.getConfirmedBy());
        rsp.setConfirmTime(Optional.ofNullable(publicInfoQuery.getConfirmedTime()).map(obj -> obj.format(DateTimeFormatter.ofPattern(DatePattern.NORM_DATETIME_PATTERN))).orElse(null));
        List<PublicInfoQueryRSP.RowStructure> rowList = new LinkedList<>();
        // 查询行配置表
        Map<String, PublicInfoConfig> configMap = publicInfoConfigService.list().stream().collect(Collectors.toMap(PublicInfoConfig::getConfigKey, Function.identity(), (o1, o2) -> o1));
        List<PublicInfoRecord> publicInfoRecords = publicInfoRecordService.list(Wrappers.<PublicInfoRecord>lambdaQuery().eq(PublicInfoRecord::getPublicInfoQueryId, publicInfoQuery.getId()));
        // 材料的通用查询接口不满足这里的查询需求，自己自定义方法查询
        List<MaterialsList> materialsList;
        if (CollUtil.isEmpty(publicInfoRecords)) {
            materialsList = new ArrayList<>();
        } else {
            materialsList = materialsListService.list(Wrappers.<MaterialsList>lambdaQuery()
                    .in(MaterialsList::getBelongId, publicInfoRecords.stream().map(PublicInfoRecord::getId).collect(Collectors.toList()))
                    .eq(MaterialsList::getBusinessType, BusinessModuleEnum.PUBLIC_INFO.name())
                    .in(MaterialsList::getMaterialSubType, Arrays.stream(PublicInfoFileTypeEnum.values()).map(PublicInfoFileTypeEnum::name).collect(Collectors.toList())));
        }
        Map<Long, Map<String, List<FileListRSP>>> materialsListMap = new HashMap<>(16);
        List<Long> clientIds = new LinkedList<>();
        List<Long> userIds = new LinkedList<>();
        if (CollUtil.isNotEmpty(materialsList)) {
            // 找到上传人更新人信息
            userIds.addAll(materialsList.stream().map(MaterialsList::getCreateBy).collect(Collectors.toList()));
            userIds.addAll(materialsList.stream().map(MaterialsList::getUpdateBy).collect(Collectors.toList()));
        }
        if (Objects.nonNull(publicInfoQuery.getConfirmedBy())) {
            userIds.add(publicInfoQuery.getConfirmedBy());
        }
        clientIds.add(publicInfoQuery.getClientId());
        Map<Long, String> clientId2NameMap = id2NameService.clientId2Name(clientIds);
        Map<Long, String> userId2NameMap = id2NameService.sysUserId2Name(userIds);
        rsp.setConfirmName(Optional.ofNullable(publicInfoQuery.getConfirmedBy()).map(obj -> userId2NameMap.get(publicInfoQuery.getConfirmedBy())).orElse(null));
        rsp.setClientName(Optional.ofNullable(publicInfoQuery.getClientId()).map(obj -> clientId2NameMap.get(publicInfoQuery.getClientId())).orElse(null));
        if (CollUtil.isNotEmpty(materialsList)) {
            materialsList.stream().collect(Collectors.groupingBy(MaterialsList::getBelongId))
                    .forEach((belongId, materialsListList) -> {
                        Map<String, List<FileListRSP>> listMap = materialsListList.stream().map(obj -> {
                            FileListRSP fileListRsp = new FileListRSP();
                            // 填充属性
                            fileService.fillFiledValue(obj, fileListRsp);
                            fileListRsp.setUpdateByName(userId2NameMap.get(obj.getUpdateBy()));
                            fileListRsp.setCreateByName(userId2NameMap.get(obj.getCreateBy()));
                            return fileListRsp;
                        }).collect(Collectors.groupingBy(FileListRSP::getMaterialSubType));
                        materialsListMap.put(belongId, listMap);
                    });
        }
        publicInfoRecords.stream().sorted(Comparator.comparing(obj -> configMap.get(obj.getConfigKey()).getSortPriority()))
                .forEach(publicInfoRecord -> {
                    PublicInfoQueryRSP.RowStructure rowStructure = new PublicInfoQueryRSP.RowStructure();
                    PublicInfoConfig publicInfoConfig = configMap.get(publicInfoRecord.getConfigKey());
                    rowStructure.setTitle(publicInfoConfig.getTitle());
                    rowStructure.setDescription(publicInfoConfig.getDescription());
                    rowStructure.setRowKey(publicInfoConfig.getConfigKey());
                    rowStructure.setInvestigationType(publicInfoRecord.getInvestigationType());
                    rowStructure.setInvestigationExplain(publicInfoRecord.getInvestigationExplain());
                    rowStructure.setProjectManagerExplain(publicInfoRecord.getProjectmanagerExplain());
                    rowStructure.setId(publicInfoRecord.getId());
                    rowStructure.setResultFileList(Optional.ofNullable(materialsListMap.get(publicInfoRecord.getId()))
                            .map(obj -> obj.get(publicInfoRecord.getConfigKey() + PublicInfoFileTypeEnum.OPERATOR_SUFFIX)).orElse(Collections.emptyList()));
                    rowStructure.setProjectManagerFileList(Optional.ofNullable(materialsListMap.get(publicInfoRecord.getId()))
                            .map(obj -> obj.get(publicInfoRecord.getConfigKey() + PublicInfoFileTypeEnum.PROJ_MANAGER_SUFFIX)).orElse(Collections.emptyList()));
                    rowList.add(rowStructure);
                });
        rsp.setRowList(rowList);

        // 查询外部公开信息
        OuterInfoRecord preOuter = outerInfoRecordService.getOne(Wrappers.<OuterInfoRecord>lambdaQuery()
                .eq(OuterInfoRecord::getPublicInfoQueryId, publicInfoQuery.getId())
                .orderByDesc(OuterInfoRecord::getVersion)
                .last("limit 1"));
        if (ObjectUtil.isNotNull(preOuter)) {
            rsp.setPublicQueryTime(preOuter.getCreateTime());
            Map<String, List<OuterInfoRecord>> outerResultMap = outerInfoRecordService.list(Wrappers.<OuterInfoRecord>lambdaQuery()
                    .eq(OuterInfoRecord::getPublicInfoQueryId, req.getId())
                    .eq(OuterInfoRecord::getVersion, preOuter.getVersion())).stream().collect(Collectors.groupingBy(OuterInfoRecord::getConfigKey));

            for (PublicInfoQueryRSP.RowStructure rowStructure : rowList) {
                List<OuterInfoRecord> orDefault = outerResultMap.getOrDefault(rowStructure.getRowKey(), Collections.emptyList());
                if (ObjectUtil.isEmpty(orDefault)) {
                    continue;
                }
                String collect = orDefault.stream().map(obj -> String.join(".", obj.getIndex().toString(), obj.getQueryResult())).collect(Collectors.joining("\n"));
                rowStructure.setOuterQueryResult(collect);
            }
        }
        return rsp;
    }

    @Transactional(rollbackFor = Throwable.class)
    public void modifyTableContent(PublicInfoModifyContentREQ req) {
        // 这里存在权限控制
        PublicInfoQuery publicInfoQuery = thisService.getById(req.getId());
        Assert.notNull(publicInfoQuery, () -> MithrasException.newException("id: " + req.getId() + ResultMsg.RECORD_NOT_EXIST));

        // 校验当前用户是否有权限
        AccountVO loginInfo = AccountUtil.getLoginInfo();
        Assert.notNull(loginInfo, () -> MithrasException.newException(ResultMsg.USER_NOT_LOGIN));

        // 判断当前信息是否在流程中
        if (CharSequenceUtil.isBlank(publicInfoQuery.getProcessInstanceId())) {
            // 当前不在流程中，只有运营经办可以修改记录
            if (sysUserService.getUserByDeptCode(JobEnum.yunYingGuanLi.name())
                    .stream().map(UserDO::getId).noneMatch(obj -> obj.equals(loginInfo.getId()))) {
                throw MithrasException.newException("非【运营经办】没有变更权限");
            }
        } else {
            // 找到当前流程实例
            ProcessResp processResp = flowTaskApiService.queryProcessById(publicInfoQuery.getProcessInstanceId());
            Assert.notNull(processResp, () -> MithrasException.newException("id: " + publicInfoQuery.getProcessInstanceId() + "流程实例不存在"));
            // 当前流程一定要是【付款申请】流程
            Assert.isTrue(processResp.getModelKey().equals(ProcessModelTypeEnum.PaymentCreateFlow.name()), () -> MithrasException.newException("当前流程不是【付款申请】流程，不能修改"));
            // 判断当前节点是运营经办还是项目经理 -> 20241224变更：不再按审批节点区分编辑，判断当前审批节点审批人为合同主办时可编辑
            // 找到当前付款申请的项目主办
            PaymentBaseInfo paymentBaseInfo = paymentBaseInfoMapper.selectById(publicInfoQuery.getPaymentId());
            Assert.notNull(paymentBaseInfo, "未找到付款申请信息");
            // 找到合同主办
            ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(paymentBaseInfo.getContractId());
            Assert.notNull(contractBaseInfo, "未找到合同信息");
            if (contractBaseInfo.getProjSponsorUserId().equals(loginInfo.getId()) && processResp.getCurAssigneeIds().contains(loginInfo.getId().toString())) {
                // 如果是合同主办，并且当前结点审批人是合同主办，什么也不干
            } else if (!processResp.getCurTaskActivityIds().contains("userTask_startUser")) {
                throw MithrasException.newException("当前流程节点不支持修改");
            }
        }

        // 权限校验通过，开始更新数据
        List<PublicInfoRecord> needUpdateList = new LinkedList<>();
        req.getRowList().forEach(row -> {
            PublicInfoRecord publicInfoRecord = publicInfoRecordService.getById(row.getId());
            Assert.notNull(publicInfoRecord, () -> MithrasException.newException("id: " + row.getId() + ResultMsg.RECORD_NOT_EXIST));
            publicInfoRecord.setInvestigationType(row.getInvestigationType());
            publicInfoRecord.setInvestigationExplain(row.getInvestigationExplain());
            publicInfoRecord.setProjectmanagerExplain(row.getProjectManagerExplain());
            needUpdateList.add(publicInfoRecord);
        });

        if (!needUpdateList.isEmpty()) {
            publicInfoRecordService.updateBatchById(needUpdateList);
        }
    }

    @Transactional(rollbackFor = Throwable.class)
    public Long createIntervalTable(PublicInfoCreateREQ req) {
        PublicInfoQuery publicInfoQuery = thisService.getOne(Wrappers.<PublicInfoQuery>lambdaQuery()
                .eq(PublicInfoQuery::getPaymentId, req.getPaymentId())
                .eq(PublicInfoQuery::getClientId, req.getClientId())
                .eq(PublicInfoQuery::getClientType, req.getClientType())
                .eq(PublicInfoQuery::getQueryFrom, req.getQueryFrom())
                .eq(PublicInfoQuery::getQueryTo, req.getQueryTo())
        );
        Assert.isNull(publicInfoQuery, () -> MithrasException.newException("该客户当前时间段已经存在记录，不能重复创建"));

        // 找到当前流程实例
        ProcessPageReq processPageReq = new ProcessPageReq();
        processPageReq.setBusinessKey(String.valueOf(req.getPaymentId()));
        processPageReq.setProcessStatusList(Collections.singletonList(ProcessBusinessStatusEnum.RUNNING.getType()));
        processPageReq.setModelKeyList(ListUtil.of(ProcessModelTypeEnum.PaymentCreateFlow.name()));
        Page<ProcessResp> processRespPage = flowTaskApiService.queryProcess(processPageReq);
        // 开始构建实体类
        PublicInfoQuery needInsert = PublicInfoQuery.builder()
                .paymentId(req.getPaymentId())
                .clientId(req.getClientId())
                .clientType(req.getClientType())
                .queryFrom(LocalDate.parse(req.getQueryFrom(), DateTimeFormatter.ofPattern(DatePattern.NORM_DATE_PATTERN)))
                .queryTo(LocalDate.parse(req.getQueryTo(), DateTimeFormatter.ofPattern(DatePattern.NORM_DATE_PATTERN)))
                .build();
        if (processRespPage.getTotal() > 1) {
            throw new MithrasException("当前付款申请存在两个流程");
        }
        if (processRespPage.getTotal() > 0) {
            ProcessResp processResp = processRespPage.getContents().get(0);
            if (Objects.nonNull(processResp)) {
                needInsert.setProcessInstanceId(processResp.getProcessInstanceId());
            }
        }
        thisService.save(needInsert);

        // 构建行记录
        List<PublicInfoRecord> publicInfoRecords = buildCommonRecord(needInsert);
        publicInfoRecordService.saveBatch(publicInfoRecords);
        return needInsert.getId();
    }

    /**
     * 通用的构建行记录的方法
     *
     * @param publicInfoQuery 基本信息记录
     * @return List<PublicInfoRecord> 返回构建的通用行记录
     */
    private List<PublicInfoRecord> buildCommonRecord(PublicInfoQuery publicInfoQuery) {
        List<PublicInfoRecord> recordList = new LinkedList<>();
        // 获取配置信息
        List<PublicInfoConfig> configList = publicInfoConfigService.list();
        configList.forEach(config -> {
            // 担保和承租分别有两个类型是没有的
            if (PublicInfoClientTypeEnum.TENANTRY.name().equals(publicInfoQuery.getClientType()) &&
                    // 承租人无需这些信息
                    PublicInfoRowKeyEnum.QG_COURT_EXECUTIONER.name().equals(config.getConfigKey())) {
                return;
            }
            if (PublicInfoClientTypeEnum.GUARANTOR.name().equals(publicInfoQuery.getClientType()) &&
                    // 担保人无需这些信息
                    CharSequenceUtil.equalsAny(config.getConfigKey(), PublicInfoRowKeyEnum.ZDW_REGISTRATION_DETAILS.name())) {
                return;
            }
            recordList.add(PublicInfoRecord.builder()
                    .paymentId(publicInfoQuery.getPaymentId())
                    .publicInfoQueryId(publicInfoQuery.getId())
                    .configKey(config.getConfigKey())
                    .build());
        });
        return recordList;
    }

    public void export(PublicInfoExportREQ req) throws IOException {
        // 找到所有数据
        PaymentBaseInfo paymentBaseInfo = paymentBaseInfoMapper.selectById(req.getPaymentId());
        Assert.notNull(paymentBaseInfo, () -> MithrasException.newException("付款申请不存在"));
        // 查找基本信息
        List<PublicInfoQuery> publicInfoQueryList = thisService.list(Wrappers.<PublicInfoQuery>lambdaQuery().eq(PublicInfoQuery::getPaymentId, paymentBaseInfo.getId()));
        Assert.notEmpty(publicInfoQueryList, () -> MithrasException.newException("该客户当前不存在可以导出的数据"));
        // 找到所有行记录
        List<PublicInfoRecord> publicInfoRecordList = publicInfoRecordService.list(Wrappers.<PublicInfoRecord>lambdaQuery().in(PublicInfoRecord::getPublicInfoQueryId, publicInfoQueryList.stream().map(PublicInfoQuery::getId).collect(Collectors.toList())));
        Assert.notEmpty(publicInfoRecordList, () -> MithrasException.newException("该客户当前不存在可以导出的数据"));
        // 收集所有可能用到的clientId
        List<Long> clientIdList = publicInfoQueryList.stream().map(PublicInfoQuery::getClientId).distinct().collect(Collectors.toList());
        List<Long> userIdList = new LinkedList<>();
        userIdList.addAll(publicInfoQueryList.stream().map(PublicInfoQuery::getConfirmedBy).collect(Collectors.toList()));
        userIdList.addAll(publicInfoQueryList.stream().map(PublicInfoQuery::getCreateBy).collect(Collectors.toList()));
        userIdList.addAll(publicInfoRecordList.stream().map(PublicInfoRecord::getUpdateBy).collect(Collectors.toList()));
        Map<Long, String> clientId2NameMap = id2NameService.clientId2Name(clientIdList);
        Map<Long, String> userId2NameMap = id2NameService.sysUserId2Name(userIdList);
        // 将行记录分组
        Map<Long, Map<String, PublicInfoRecord>> recordGroupMap = new HashMap<>();
        Map<Long, Map<Long, PublicInfoRecord>> recordIdMap = new HashMap<>();
        Map<Long, List<PublicInfoRecord>> recordMap = publicInfoRecordList.stream().collect(Collectors.groupingBy(PublicInfoRecord::getPublicInfoQueryId));
        recordMap.forEach((key, value) -> {
            recordGroupMap.put(key, value.stream().collect(Collectors.toMap(PublicInfoRecord::getConfigKey, Function.identity(), (v1, v2) -> v1)));
            recordIdMap.put(key, value.stream().collect(Collectors.toMap(PublicInfoRecord::getId, Function.identity(), (v1, v2) -> v1)));
        });
        ZipOutputStream zipOutputStream = null;
        try {
            //下载
            response.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
            String zipName = paymentBaseInfo.getPaymentCode() + "-公开信息-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern(DatePattern.PURE_DATETIME_PATTERN)) + ".zip";
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(zipName, StandardCharsets.UTF_8.name()));
            zipOutputStream = new ZipOutputStream(response.getOutputStream());
            Map<Long, List<PublicInfoQuery>> listMap = publicInfoQueryList.stream().collect(Collectors.groupingBy(PublicInfoQuery::getClientId));
            // 定义一个root路径, 单个用户的所有时间段的文件全部放在这里面
            String path = File.separator + "公开信息报告";
            for (Map.Entry<Long, List<PublicInfoQuery>> entry : listMap.entrySet()) {
                Long clientId = entry.getKey();
                List<PublicInfoQuery> queries = entry.getValue();
                String clientTypeDisplay = Optional.ofNullable(PublicInfoClientTypeEnum.find(queries.get(0).getClientType())).map(PublicInfoClientTypeEnum::getDisplay).orElse("未知类型");
                String rootPath = path + File.separator + Optional.ofNullable(clientId2NameMap.get(clientId)).map(obj -> obj + "-" + clientTypeDisplay).orElse("未知用户");
                for (PublicInfoQuery query : queries) {
                    // 创建子文件路径，界面报表放在该路径下 /${用户名-用户类型}/开始时间-结束时间
                    String intervalPath = rootPath + File.separator + query.getQueryFrom().format(DateTimeFormatter.ofPattern(DatePattern.PURE_DATE_PATTERN)) +
                            "~" + query.getQueryTo().format(DateTimeFormatter.ofPattern(DatePattern.PURE_DATE_PATTERN));
                    // 现在已经来到了单个表格的地方， 先构建Excel文件
                    Map<String, PublicInfoRecord> stringPublicInfoRecordMap = recordGroupMap.get(query.getId());
                    PublicInfoExcelModel publicInfoExcelModel = buildExcel(query, userId2NameMap, clientId2NameMap, stringPublicInfoRecordMap);
                    // 根据不同的客户类型选择不同的导出模版
                    PublicInfoClientTypeEnum clientTypeEnum = PublicInfoClientTypeEnum.find(query.getClientType());
                    if (Objects.isNull(clientTypeEnum)) {
                        throw MithrasException.newException("公开信息导出模版不存在");
                    }
                    InputStream templateFile;
                    switch (clientTypeEnum) {
                        case TENANTRY: {
                            templateFile = this.getClass().getResourceAsStream("/doc/公开信息查询报告-承租人.xlsx");
                            break;
                        }
                        case GUARANTOR: {
                            templateFile = this.getClass().getResourceAsStream("/doc/公开信息查询报告-担保人.xlsx");
                            break;
                        }
                        default:
                            throw MithrasException.newException("公开信息导出模版不存在");
                    }
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    EasyExcelFactory.write(outputStream).withTemplate(templateFile).sheet(0).doFill(publicInfoExcelModel);
                    // 将文件放到zip中
                    zipOutputStream.putNextEntry(new ZipEntry(intervalPath + File.separator + "公开信息查询报告.xlsx"));
                    zipOutputStream.write(outputStream.toByteArray());
                    zipOutputStream.closeEntry();
                    if (CollUtil.isEmpty(stringPublicInfoRecordMap)) {
                        return;
                    }
                    // 将minio上的文件下载下来打包, 找到所有的文件记录并根据记录ID分组
                    Map<Long, Map<String, List<FileListRSP>>> fileListMap = thisService.queryFileList(stringPublicInfoRecordMap);
                    // 遍历文件记录
                    for (Map.Entry<Long, Map<String, List<FileListRSP>>> fileEntry : fileListMap.entrySet()) {
                        Long recordId = fileEntry.getKey();
                        PublicInfoRecord publicInfoRecord = recordIdMap.get(query.getId()).get(recordId);
                        Assert.notNull(publicInfoRecord, "公开信息记录不存在");
                        Map<String, List<FileListRSP>> map = fileEntry.getValue();
                        for (Map.Entry<String, List<FileListRSP>> fileListEntry : map.entrySet()) {
                            // 这一层是小格子
                            List<FileListRSP> fileList = fileListEntry.getValue();
                            String currentPath;
                            if (Objects.equals(fileListEntry.getKey(), publicInfoRecord.getConfigKey() + "_YY")) {
                                currentPath = intervalPath + File.separator +
                                        Optional.ofNullable(PublicInfoFileTypeEnum.find(publicInfoRecord.getConfigKey() + "_YY"))
                                                .map(PublicInfoFileTypeEnum::getDisplay).orElse("") + File.separator + "运营经办";
                            } else {
                                currentPath = intervalPath + File.separator +
                                        Optional.ofNullable(PublicInfoFileTypeEnum.find(publicInfoRecord.getConfigKey() + "_XMJL"))
                                                .map(PublicInfoFileTypeEnum::getDisplay).orElse("") + File.separator + "项目经理";
                            }
                            // 遍历文件列表，下载文件，并写入到zip中
                            for (int i = 0; i < fileList.size(); i++) {
                                // 这里可能文件太大，单个流是放不下的所以每个小框的单独开一个流，放到一个列表里面，最后再写到zip
                                FileListRSP fileListRsp = fileList.get(i);
                                ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
                                // 下载文件
                                ossClient.downLoad(arrayOutputStream, fileListRsp.getOssFilename());
                                zipOutputStream.putNextEntry(new ZipEntry(currentPath + File.separator + i + "-" + fileListRsp.getFilename()));
                                zipOutputStream.write(arrayOutputStream.toByteArray());
                                arrayOutputStream.close();
                                zipOutputStream.closeEntry();
                            }
                        }
                    }
                }
            }
            zipOutputStream.flush();
        } catch (IOException e) {
            log.error("导出公开信息异常", e);
            throw MithrasException.newException(e.getMessage());
        } finally {
            if (zipOutputStream != null) {
                zipOutputStream.close();
            }
        }
    }

    /**
     * 获取每个格子的文件列表
     *
     * @param stringPublicInfoRecordMap 当前付款申请的所有行记录
     * @return Map<Long, Map < String, List < FileListRSP>>> 所有的文件分组
     */
    public Map<Long, Map<String, List<FileListRSP>>> queryFileList(Map<String, PublicInfoRecord> stringPublicInfoRecordMap) {
        List<MaterialsList> materialsLists = materialsListService.list(Wrappers.<MaterialsList>lambdaQuery()
                .eq(MaterialsList::getBusinessType, BusinessModuleEnum.PUBLIC_INFO.name())
                .in(MaterialsList::getBelongId, stringPublicInfoRecordMap.values().stream().map(PublicInfoRecord::getId).collect(Collectors.toList()))
        );
        if (CollUtil.isEmpty(materialsLists)) {
            return Collections.emptyMap();
        }
        Map<Long, Map<String, List<FileListRSP>>> result = new HashMap<>();
        materialsLists.stream().map(obj -> {
                    FileListRSP fileListRsp = new FileListRSP();
                    // 填充属性
                    fileService.fillFiledValue(obj, fileListRsp);
                    return fileListRsp;
                }).collect(Collectors.groupingBy(FileListRSP::getBelongId))
                .forEach((key, value) -> result.put(key, value.stream().collect(Collectors.groupingBy(FileListRSP::getMaterialSubType))));
        return result;
    }

    private PublicInfoExcelModel buildExcel(PublicInfoQuery query, Map<Long, String> userId2NameMap,
                                            Map<Long, String> clientId2NameMap,
                                            Map<String, PublicInfoRecord> stringPublicInfoRecordMap) {
        // 构建模版填充实体类
        PublicInfoExcelModel excelModel = PublicInfoExcelModel.builder()
                .confirmedName(userId2NameMap.get(query.getConfirmedBy()))
                .tenantryName(clientId2NameMap.get(query.getClientId()))
                .queryFrom(query.getQueryFrom().format(DateTimeFormatter.ofPattern(DatePattern.NORM_DATE_PATTERN)))
                .queryTo(query.getQueryTo().format(DateTimeFormatter.ofPattern(DatePattern.NORM_DATE_PATTERN)))
                .confirmedTime(Optional.ofNullable(query.getConfirmedTime()).map(o -> o.format(DateTimeFormatter.ofPattern(DatePattern.NORM_DATETIME_PATTERN))).orElse(null))
                .build();
        // 遍历使用反射填充数据，因为比较通用
        stringPublicInfoRecordMap.forEach((configKey, publicInfoRecord) -> {
            // 根据configKey 找到对应的枚举，然后填充数据
            ReflectUtil.setFieldValue(excelModel, configKey + "_ENUM", Optional.ofNullable(InvestigationResultEnum.find(publicInfoRecord.getInvestigationType())).map(InvestigationResultEnum::getDisplay).orElse(null));
            ReflectUtil.setFieldValue(excelModel, configKey + "_YY", publicInfoRecord.getInvestigationExplain());
            ReflectUtil.setFieldValue(excelModel, configKey + "_XMJL", publicInfoRecord.getProjectmanagerExplain());
        });
        return excelModel;
    }

    @Transactional(rollbackFor = Throwable.class)
    public void deleteIntervalTable(PublicInfoDeleteREQ req) {
        // 查看原始数据是否存在
        PublicInfoQuery publicInfoQuery = thisService.getById(req.getId());
        Assert.notNull(publicInfoQuery, () -> MithrasException.newException("该客户当前时间段不存在记录，不能删除"));
        thisService.removeById(publicInfoQuery.getId());
        // 删除字表记录
        publicInfoRecordService.remove(Wrappers.<PublicInfoRecord>lambdaQuery().eq(PublicInfoRecord::getPublicInfoQueryId, publicInfoQuery.getId()));
    }

    @Override
    public void copyIntervalTable(Long paymentId) {
        // 打印日志
        log.info("拷贝公开信息开始>>>>>>>>>>>>>>>>>>>>>付款申请ID：{}", paymentId);
        PaymentBaseInfo paymentBaseInfo = paymentBaseInfoMapper.selectById(paymentId);
        Assert.notNull(paymentBaseInfo, () -> MithrasException.newException("付款申请不存在"));
        // 检查是不死已经初始化过了
        if (Objects.equals(YesOrNoNumberEnum.YES.getCode(), paymentBaseInfo.getIsInitPublicInfo())) {
            return;
        }
        // 根据里面的合同ID找到项目ID，再找到所有的合同
        Long contractId = paymentBaseInfo.getContractId();
        ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(contractId);
        Assert.notNull(contractBaseInfo, () -> MithrasException.newException("合同不存在"));
        List<ContractBaseInfo> contractBaseInfoList = contractBaseInfoService.listByProjReviewIds(Collections.singletonList(contractBaseInfo.getProjReviewId()));
        // 然后拿到所有的付款申请
        if (CollUtil.isEmpty(contractBaseInfoList)) {
            // 没有合同，说明是新合同，直接返回比较结果
            throw MithrasException.newException("该合同没有付款申请，不能复制");
        }
        List<PaymentBaseInfo> paymentBaseInfoList = paymentBaseInfoMapper.selectList(Wrappers.<PaymentBaseInfo>lambdaQuery()
                .in(PaymentBaseInfo::getPaymentStatus, PaymentStatusEnum.TAKE_EFFECT.name(), PaymentStatusEnum.FINISHED.name(), PaymentStatusEnum.NEW.name())
                .in(PaymentBaseInfo::getContractId, contractBaseInfoList.stream().map(ContractBaseInfo::getId).collect(Collectors.toList())));
        // 查询公开信息
        List<PublicInfoQuery> publicInfoQueryList = thisService.list(Wrappers.<PublicInfoQuery>lambdaQuery()
                .in(PublicInfoQuery::getPaymentId, paymentBaseInfoList.stream().map(PaymentBaseInfo::getId).collect(Collectors.toList())));
        if (CollUtil.isEmpty(publicInfoQueryList)) {
            // 没有公开信息，直接返回
            return;
        }
        // 承租人
        CompletableFuture<List<Long>> tenantryIdsFuture = getTenantryIdsFuture(contractId);
        // 担保人
        CompletableFuture<List<Long>> guarantorIdsFuture = getGuarantorIdsFuture(contractId);
        // 抵押人
        CompletableFuture<List<Long>> mortgageIdsFuture = getMortgageIdsFuture(contractId);
        // 质押人
        CompletableFuture<List<Long>> pledgeIdsFuture = getPledgeIdsFuture(contractId);

        // 等待所有的任务执行完成
        CompletableFuture.allOf(tenantryIdsFuture, guarantorIdsFuture, mortgageIdsFuture, pledgeIdsFuture).join();

        // 合并
        try {
            List<Long> tenantryIds = tenantryIdsFuture.get();
            List<Long> guarantorIds = guarantorIdsFuture.get();
            List<Long> mortgageIds = mortgageIdsFuture.get();
            List<Long> pledgeIds = pledgeIdsFuture.get();
            transactionTemplate.executeWithoutResult(transactionStatus -> {
                List<Long> clientIds = CollUtil.unionAll(tenantryIds, guarantorIds, mortgageIds, pledgeIds).stream().distinct().collect(Collectors.toList());
                // 拿到客户信息之后，开始拷贝
                List<PublicInfoQuery> infoQueries = thisService.list(Wrappers.<PublicInfoQuery>lambdaQuery()
                        .in(PublicInfoQuery::getPaymentId, paymentBaseInfoList.stream().map(PaymentBaseInfo::getId).collect(Collectors.toList()))
                        .in(PublicInfoQuery::getClientId, clientIds));
                if (CollUtil.isEmpty(infoQueries)) {
                    return;
                }
                PublicInfoRecord infoRecord = publicInfoRecordService.getOne(Wrappers.<PublicInfoRecord>lambdaQuery()
                        .in(PublicInfoRecord::getPublicInfoQueryId, infoQueries.stream().map(PublicInfoQuery::getId).collect(Collectors.toList()))
                        .orderByDesc(BaseModel::getUpdateTime)
                        .last(StringUtil.mysqlLimitOne()));
                if (ObjectUtil.isNull(infoRecord)) {
                    return;
                }
                infoQueries = infoQueries.stream().filter(obj -> Objects.equals(infoRecord.getPaymentId(), obj.getPaymentId())).collect(Collectors.toList());
                Map<Long, List<PublicInfoQuery>> clientIdListMap = infoQueries.stream().collect(Collectors.groupingBy(PublicInfoQuery::getClientId));
                Map<Long, List<PublicInfoRecord>> publicRecordListMap = publicInfoRecordService.list(Wrappers.<PublicInfoRecord>lambdaQuery()
                                .in(PublicInfoRecord::getPublicInfoQueryId, infoQueries.stream().map(PublicInfoQuery::getId).collect(Collectors.toList())))
                        .stream().collect(Collectors.groupingBy(PublicInfoRecord::getPublicInfoQueryId));
                // 需要对客户的数据根据区间去重，拿到最新的
                List<PublicInfoRecord> newPublicInfoRecords = new ArrayList<>();
                List<PublicInfoQuery> newPublicInfoQueries = new ArrayList<>();
                for (Map.Entry<Long, List<PublicInfoQuery>> longListEntry : clientIdListMap.entrySet()) {
                    for (PublicInfoQuery query : longListEntry.getValue()) {
                        PublicInfoQuery infoQuery = PublicInfoQuery.builder()
                                .clientId(query.getClientId())
                                .clientType(query.getClientType())
                                .paymentId(paymentBaseInfo.getId())
                                .queryFrom(query.getQueryFrom())
                                .queryTo(query.getQueryTo())
                                .confirmedTime(query.getConfirmedTime())
                                .confirmedBy(query.getConfirmedBy())
                                .build();
                        thisService.save(infoQuery);

                        // 保存下面的行记录
                        List<PublicInfoRecord> publicInfoRecords = publicRecordListMap.get(query.getId());
                        if (CollUtil.isNotEmpty(publicInfoRecords)) {
                            newPublicInfoQueries.add(infoQuery);
                            // 拷贝文件
                            List<MaterialsList> materialsLists = materialsListService.list(Wrappers.<MaterialsList>lambdaQuery()
                                    .in(MaterialsList::getBelongId, publicInfoRecords.stream().map(PublicInfoRecord::getId).collect(Collectors.toList()))
                                    .eq(MaterialsList::getBusinessType, BusinessModuleEnum.PUBLIC_INFO.name()));
                            Map<Long, List<MaterialsList>> materailListMap = new HashMap<>();
                            if (CollUtil.isNotEmpty(materialsLists)) {
                                materailListMap = materialsLists.stream().collect(Collectors.groupingBy(MaterialsList::getBelongId));
                            }
                            for (PublicInfoRecord record : publicInfoRecords) {
                                PublicInfoRecord newRecord = PublicInfoRecord.builder()
                                        .publicInfoQueryId(infoQuery.getId())
                                        .configKey(record.getConfigKey())
                                        .investigationType(record.getInvestigationType())
                                        .paymentId(paymentBaseInfo.getId())
                                        .investigationExplain(record.getInvestigationExplain())
                                        .projectmanagerExplain(record.getProjectmanagerExplain())
                                        .build();
                                publicInfoRecordService.save(newRecord);
                                // 看看有没有文件需要处理
                                List<MaterialsList> list = materailListMap.get(record.getId());
                                if (CollUtil.isNotEmpty(list)) {
                                    for (MaterialsList materialsList : list) {
                                        try {
                                            InputStream inputStream = ossClient.downLoad(materialsList.getOssFilename());
                                            if (inputStream != null) {
                                                byte[] byteArray = ByteStreams.toByteArray(inputStream);
                                                materialsListService.add(
                                                        new ByteArrayInputStream(byteArray),
                                                        materialsList.getFilename(),
                                                        newRecord.getId(),
                                                        materialsList.getMaterialsType(),
                                                        materialsList.getMaterialSubType(),
                                                        BusinessModuleEnum.PUBLIC_INFO.name(),
                                                        YesOrNoNumberEnum.YES
                                                );
                                                inputStream.close();
                                            }
                                        } catch (IOException e) {
                                            log.error("关闭流失败", e);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                publicInfoRecordService.saveBatch(newPublicInfoRecords);
                PaymentBaseInfo entity = new PaymentBaseInfo();
                entity.setId(paymentBaseInfo.getId());
                entity.setIsInitPublicInfo(1);
                paymentBaseInfoMapper.updateById(entity);
                log.info("付款申请：{}公开信息拷贝完毕！一共拷贝了{}条记录", paymentBaseInfo.getPaymentCode(), newPublicInfoQueries.size());
            });
        } catch (
                InterruptedException e) {
            log.error("公开信息拷贝，异步查询交易结构失败");
            Thread.currentThread().interrupt();
        } catch (
                ExecutionException e) {
            log.error("公开信息拷贝，异步查询交易结构失败");
        }
    }

    public List<String> check(PublicInfoCheckREQ req) {
        PaymentBaseInfo paymentBaseInfo = paymentBaseInfoMapper.selectById(req.getPaymentId());
        Assert.notNull(paymentBaseInfo, () -> MithrasException.newException("付款申请不存在"));
        // 找到所有的交易结构信息
        Long contractId = paymentBaseInfo.getContractId();
        // 承租人
        CompletableFuture<List<Long>> tenantryIdsFuture = getTenantryIdsFuture(contractId);
        // 担保人
        CompletableFuture<List<Long>> guarantorIdsFuture = getGuarantorIdsFuture(contractId);
        // 抵押人
        CompletableFuture<List<Long>> mortgageIdsFuture = getMortgageIdsFuture(contractId);
        // 质押人
        CompletableFuture<List<Long>> pledgeIdsFuture = getPledgeIdsFuture(contractId);

        // 等待所有的任务执行完成
        CompletableFuture.allOf(tenantryIdsFuture, guarantorIdsFuture, mortgageIdsFuture, pledgeIdsFuture).join();
        try {
            List<Long> tenantryIds = tenantryIdsFuture.get();
            List<Long> guarantorIds = guarantorIdsFuture.get();
            List<Long> mortgageIds = mortgageIdsFuture.get();
            List<Long> pledgeIds = pledgeIdsFuture.get();

            List<Long> clientIds = CollUtil.unionAll(tenantryIds, guarantorIds, mortgageIds, pledgeIds).stream().distinct().collect(Collectors.toList());
            List<PublicInfoQuery> infoQueries = thisService.list(Wrappers.<PublicInfoQuery>lambdaQuery()
                    .eq(PublicInfoQuery::getPaymentId, paymentBaseInfo.getId()));
            if (CollUtil.isEmpty(infoQueries)) {
                Map<Long, String> longStringMap = id2NameService.clientId2Name(clientIds.stream().distinct().collect(Collectors.toList()));
                return ListUtil.toList(longStringMap.values());
            } else {
                List<Long> list = infoQueries.stream().map(PublicInfoQuery::getClientId).distinct().collect(Collectors.toList());
                clientIds.removeIf(list::contains);
                if (CollUtil.isEmpty(clientIds)) {
                    return Collections.emptyList();
                }
                Map<Long, String> longStringMap = id2NameService.clientId2Name(clientIds);
                return ListUtil.toList(longStringMap.values());
            }
        } catch (ExecutionException | InterruptedException e) {
            log.error("获取交易结构信息失败");
            throw MithrasException.newException("获取交易结构信息失败");
        }
    }
}


