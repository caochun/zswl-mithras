package cn.zswltech.mithras.service.service.leaseholdproperty.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.Pair;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.thread.NamedThreadFactory;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.ZipUtil;
import cn.hutool.json.JSONConfig;
import cn.hutool.json.JSONUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import cn.zswl.oss.core.minio.MinioOssClient;
import cn.zswltech.flow.core.api.FlowTaskApiService;
import cn.zswltech.flow.core.domain.req.task.ProcessPageReq;
import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.flow.core.enums.ProcessBusinessStatusEnum;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.client.client.ClientInfo;
import cn.zswltech.mithras.dto.file.FileUploadRSP;
import cn.zswltech.mithras.dto.leaseholdproperty.*;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.enums.JobEnum;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.service.enums.common.ProcessStatus;
import cn.zswltech.mithras.service.enums.common.ProjectBizType;
import cn.zswltech.mithras.contract.enums.contract.ContractStatus;
import cn.zswltech.mithras.leaseholdproperty.domain.enums.*;
import cn.zswltech.mithras.riskcontrol.common.RiskControlIndustryClassify;
import cn.zswltech.mithras.leaseholdproperty.excel.exporter.LeaseLedgerManageExcelExporter;
import cn.zswltech.mithras.leaseholdproperty.excel.model.LeaseLedgerManageExcelModel;
import cn.zswltech.mithras.service.gendoc.render.LeaseItemTextRender;
import cn.zswltech.mithras.service.mapper.file.template.model.FileTemplate;
import cn.zswltech.mithras.leaseholdproperty.infrastructure.persistence.mapper.LeaseItemAppraisalRelationMapper;
import cn.zswltech.mithras.leaseholdproperty.infrastructure.persistence.mapper.LeaseItemInfoMapper;
import cn.zswltech.mithras.service.mapper.model.MaterialsList;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.leaseholdproperty.infrastructure.persistence.mapper.model.*;
import cn.zswltech.mithras.projectprocess.mapper.model.projreview.ProjReviewBaseInfo;
import cn.zswltech.mithras.service.others.AuthCheckException;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.system.user.Id2NameService;
import cn.zswltech.mithras.system.lease.LeaseItemCommonService;
import cn.zswltech.mithras.system.user.SysUserService;
import cn.zswltech.mithras.contract.core.application.ContractBaseInfoService;
import cn.zswltech.mithras.document.application.file.template.FileTemplateService;
import cn.zswltech.mithras.leaseholdproperty.application.LeaseItemInfoService;
import cn.zswltech.mithras.leaseholdproperty.application.LeaseItemListRowDataService;
import cn.zswltech.mithras.leaseholdproperty.application.LeaseItemVatInvoiceService;
import cn.zswltech.mithras.leaseholdproperty.application.LeaseVehicleRegistrationService;
import cn.zswltech.mithras.service.service.materialsfile.MaterialsListService;
import cn.zswltech.mithras.service.service.projreview.ProjReviewBaseInfoService;
import cn.zswltech.mithras.service.util.StringUtil;
import cn.zswltech.mithras.service.util.WatermarkUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static cn.hutool.extra.spring.SpringUtil.getBean;

/**
 * @author yangxiong
 * @description 针对表【lease_item_info(租赁物管理信息)】的数据库操作Service实现
 * @createDate 2023-09-19 16:09:40
 */
@Service
public class LeaseItemInfoServiceImpl extends ServiceImpl<LeaseItemInfoMapper, LeaseItemInfo>
        implements LeaseItemInfoService {
    private static final List<String> DEFAULT_MONEY_HEADER = Arrays.asList("账面原值（元）", "账面净值（元）", "评估原值（元）", "评估净值（元）");

    @Resource
    private LeaseItemListRowDataService leaseItemListRowDataService;
    @Resource
    private Id2NameService id2NameService;
    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private ProjReviewBaseInfoService projReviewBaseInfoService;
    @Resource
    private LeaseLedgerManageExcelExporter leaseLedgerManageExcelExporter;
    @Resource
    private FlowTaskApiService flowTaskApiService;
    @Resource
    private SysUserService sysUserService;
    @Resource
    private MaterialsListService materialsListService;
    @Resource
    private FileTemplateService fileTemplateService;
    @Resource
    private LeaseItemTextRender leaseItemTextRender;
    @Resource
    private LeaseItemCommonService leaseItemCommonService;
    @Resource
    private LeaseVehicleRegistrationService vehicleRegistrationService;
    @Resource
    private LeaseItemVatInvoiceService leaseItemVatInvoiceService;
    @Resource
    private LeaseItemAppraisalRelationMapper relationMapper;

    private final static String WATERMARK = "浙商租赁";
    ExecutorService fixedThreadPool = new ThreadPoolExecutor(1, 5, 60L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(), new NamedThreadFactory("LeaseItemInfoThread-", false));

    @Override
    public R<PageR<LeaseLedgerMainRSP>> getPage(LeaseLedgerMainREQ param) {
//        Set<Long> contractIds = null;
        //如果合同编号不为空，先查询合同信息
        Set<Long> leaseItemInfoIds = new HashSet<>();
        if (CollectionUtil.isNotEmpty(param.getIds())) {
            leaseItemInfoIds.addAll(param.getIds());
        }
        Map<Long, List<ContractBaseInfo>> contractMap = null;
        if (CharSequenceUtil.isNotEmpty(param.getContractCode())) {
//            List<ContractBaseInfo> list = contractBaseInfoService.lambdaQuery().like(ContractBaseInfo::getContractCode, param.getContractCode()).list();
//            if (CollUtil.isEmpty(list)) {
//                return R.ok(PageR.empty(param.getPage(), param.getPageSize()));
//            }
//            contractIds = list.stream().map(ContractBaseInfo::getId).collect(Collectors.toSet());
            LambdaQueryWrapper<ContractBaseInfo> query = Wrappers.lambdaQuery();
            query.like(ContractBaseInfo::getContractCode, param.getContractCode());
            List<ContractBaseInfo> contractBaseInfoList = contractBaseInfoService.list(query);
            if (CollectionUtil.isEmpty(contractBaseInfoList)) {
                return R.ok(PageR.empty(param.getPage(), param.getPageSize()));
            }
            contractMap = contractBaseInfoList.stream().filter(e -> Objects.nonNull(e.getLeaseItemInfoId())).collect(Collectors.groupingBy(ContractBaseInfo::getLeaseItemInfoId));
            if (CollectionUtil.isNotEmpty(contractMap)) {
                leaseItemInfoIds.addAll(contractMap.keySet());
            }
        }
        Page<LeaseItemInfo> leaseItemInfoPage = new Page<>();
        leaseItemInfoPage.setCurrent(param.getPage());
        leaseItemInfoPage.setSize(param.getPageSize());
//        LambdaQueryWrapper<LeaseItemInfo> queryWrapper = new LambdaQueryWrapper<>();
        LambdaQueryWrapper<LeaseItemInfo> queryWrapper = this.initDefaultQuery();
        queryWrapper.like(CharSequenceUtil.isNotEmpty(param.getLeaseAuditFlowNumber()), LeaseItemInfo::getFlowId, param.getLeaseAuditFlowNumber())
//                .in(CollUtil.isNotEmpty(param.getIds()), LeaseItemInfo::getId, param.getIds())
                .in(CollectionUtil.isNotEmpty(leaseItemInfoIds), LeaseItemInfo::getId, leaseItemInfoIds)
                .like(CharSequenceUtil.isNotEmpty(param.getProjectName()), LeaseItemInfo::getProjName, param.getProjectName())
                .eq(CharSequenceUtil.isNotEmpty(param.getLeaseStatus()), LeaseItemInfo::getApprovalStatus, param.getLeaseStatus())
                .like(Objects.nonNull(param.getClientId()), LeaseItemInfo::getClientId, param.getClientId())
                .eq(Objects.nonNull(param.getProjectOrganizerId()), LeaseItemInfo::getProjSponsorUserId, param.getProjectOrganizerId())
                .apply(Objects.nonNull(param.getProjectCoOrganizerIds()), " json_contains(proj_cosponsor_user_ids, CONVERT ({0}, CHAR ))", param.getProjectCoOrganizerIds())
//                .apply(CollUtil.isNotEmpty(contractIds), " json_contains(contract_ids, CONVERT ({0}, CHAR ))", contractIds)
                .orderByDesc(LeaseItemInfo::getCreateTime);
        //查询基础数据
        Page<LeaseItemInfo> itemInfoPage = baseMapper.selectPage(leaseItemInfoPage, queryWrapper);
        if (CollUtil.isEmpty(itemInfoPage.getRecords())) {
            return R.ok(PageR.empty(param.getPage(), param.getPageSize()));
        }
        if (CollectionUtil.isEmpty(contractMap)) {
            LambdaQueryWrapper<ContractBaseInfo> query = Wrappers.lambdaQuery();
            query.in(ContractBaseInfo::getLeaseItemInfoId, itemInfoPage.getRecords().stream().map(LeaseItemInfo::getId).collect(Collectors.toSet()));
            List<ContractBaseInfo> contractBaseInfoList = contractBaseInfoService.list(query);
            if (CollectionUtil.isNotEmpty(contractBaseInfoList)) {
                contractMap = contractBaseInfoList.stream().collect(Collectors.groupingBy(ContractBaseInfo::getLeaseItemInfoId));
            }
        }
        List<LeaseLedgerMainRSP> result = handlerPageResult(itemInfoPage, contractMap);
        return R.ok(PageR.of(result, leaseItemInfoPage.getTotal(), leaseItemInfoPage.getCurrent(), leaseItemInfoPage.getSize()));
    }

    @Override
    public R<LedgerContractDetailRSP> getContractInfoById(Long id) {
        LeaseItemInfo leaseItemInfo = baseMapper.selectById(id);
        Assert.notNull(leaseItemInfo, () -> new MithrasException("台账记录不存在"));
        ProjReviewBaseInfo reviewBaseInfo = projReviewBaseInfoService.getById(leaseItemInfo.getProjReviewId());
        Assert.notNull(reviewBaseInfo, () -> new MithrasException("评审记录不存在"));
        List<ContractBaseInfo> contractBaseInfo = contractBaseInfoService.getBaseMapper().selectList(Wrappers.<ContractBaseInfo>lambdaQuery().eq(ContractBaseInfo::getProjReviewId, reviewBaseInfo.getId()));
        //buildData
        return R.ok(LedgerContractDetailRSP.builder()
                .projectName(leaseItemInfo.getProjName())
                .projectCode(reviewBaseInfo.getProjCode())
                .businessType(ProjectBizType.valueOf(reviewBaseInfo.getBizType()).display)
                .leaseType(JSON.parseArray(reviewBaseInfo.getLeaseTypes(), String.class))
                .tenant(JSON.parseArray(reviewBaseInfo.getLesseeInfo(), ClientInfo.class))
                .contractCode(CollUtil.isEmpty(contractBaseInfo) ? Collections.emptySet() : contractBaseInfo.stream().collect(Collectors.toMap(ContractBaseInfo::getContractCode, Function.identity(), (a, b) -> a)).keySet())
                .riskControlIndustryType(CharSequenceUtil.isNotEmpty(reviewBaseInfo.getRiskControlIndustryClassify()) ? RiskControlIndustryClassify.of(reviewBaseInfo.getRiskControlIndustryClassify()).display() : null)
                .projectOrganizer(id2NameService.sysUserId2NameSingle(leaseItemInfo.getProjSponsorUserId()))
                .projectCoOrganizer(new ArrayList<>(id2NameService.sysUserId2Name(JSON.parseArray(leaseItemInfo.getProjCosponsorUserIds(), Long.class)).values()))
                .businessDepartment(id2NameService.deptId2NameSingle(reviewBaseInfo.getBizDeptId()))
                .businessDepartmentHead(id2NameService.sysUserId2NameSingle(reviewBaseInfo.getBizDeptLeaderId()))
                .projReviewId(ObjectUtil.isEmpty(reviewBaseInfo) ? null : reviewBaseInfo.getId())
                .build());
    }

    @Override
    public R<LeaseCheckRepeatRSP> getLeaseCheckRepeatById(Long id) {
        LeaseItemInfo leaseItemInfo = baseMapper.selectById(id);
        Assert.notNull(leaseItemInfo, () -> new MithrasException("台账记录不存在"));
        return R.ok(LeaseCheckRepeatRSP.builder()
                .repeatDate(DateUtil.format(leaseItemInfo.getDuplicateCheckingDate(), "yyyy/MM/dd"))
                .relevanceFlowId(leaseItemInfo.getRelevanceFlowId())
                .build());
    }

    @Override
    public void download(ServletOutputStream outputStream, List<Long> ids) {
        LeaseLedgerMainREQ leaseLedgerMainReq = LeaseLedgerMainREQ.builder().ids(ids).build();
        leaseLedgerMainReq.setPage(1);
        leaseLedgerMainReq.setPageSize(5000);
        List<LeaseLedgerMainRSP> list = this.getPage(leaseLedgerMainReq).getData().getList();
        if (CollUtil.isEmpty(list)) {
            throw new MithrasException("不存在数据，导出失败");
        }
        ArrayList<LeaseLedgerManageExcelModel> excelModels = new ArrayList<>();
        list.forEach(one -> excelModels.add(LeaseLedgerManageExcelModel.builder()
                .leaseAuditFlowNumber(one.getLeaseAuditFlowNumber())
                .clientName(one.getClientName())
                .projectName(one.getProjectName())
                .projectOrganizer(one.getProjectOrganizer())
                .projectCoOrganizer(one.getProjectCoOrganizer().toString().substring(1, one.getProjectCoOrganizer().toString().length() - 1))
                .leaseStatus(Objects.requireNonNull(ProcessStatus.of(one.getLeaseStatus())).display)
                .projectNumber(one.getProjCode())
                .contractNumber(one.getContractCode().toString().substring(1, one.getContractCode().toString().length() - 1))
                .createTime(one.getCreateTime())
                .build()));
        leaseLedgerManageExcelExporter.exportExcel(excelModels, outputStream);
    }

    /**
     * 流程中上传文件加水印
     * 项目经理、运营和法务可删除和上传
     **/
    @Override
    public List<FileUploadRSP> flowUpdate(LeaseFlowUploadREQ param) {
        List<Pair<String, InputStream>> byteArrayInputStreams = new ArrayList<>();

        //判断文件类型
        if (StrUtil.equalsAny(param.getMaterialsType(), LeaseFileTypeEnums.OPERATION_MANAGER_UPLOAD_OWNERSHIP.name(), LeaseFileTypeEnums.OPERATION_MANAGER_UPLOAD_DUPLICATE_CHECK.name(), LeaseFileTypeEnums.OPERATION_MANAGER_UPLOAD_OTHER.name())) {
            checkFlowFile(param.getLeaseholdId());
            if (!sysUserService.currentUserIsSpecificJob(JobEnum.yunYingGuanLi.name()) && !sysUserService.currentUserIsSpecificJob(JobEnum.operationManagementReview.name()) && !sysUserService.currentUserIsSpecificJob(JobEnum.operationManagement.name())) {
                throw new AuthCheckException("非运营经理,不可上传");
            }
            //加水印
            if (YesOrNoNumberEnum.YES.getCode().equals(param.getNeedWatermark())) {
                CompletionService<Pair<String, InputStream>> completionService = new ExecutorCompletionService<>(fixedThreadPool);
                for (int i = 0; i < param.getFiles().size(); i++) {
                    MultipartFile file = param.getFiles().get(i);
                    completionService.submit(() -> WatermarkUtil.watermark(file.getInputStream(), WATERMARK, file.getOriginalFilename()));
                }
                for (int i = 0; i < param.getFiles().size(); i++) {
                    try {
                        Pair<String, InputStream> stringByteArrayInputStreamPair = completionService.take().get();
                        if (ObjectUtil.isNotNull(stringByteArrayInputStreamPair)) {
                            byteArrayInputStreams.add(stringByteArrayInputStreamPair);
                        } else {
                            throw new MithrasException("生成水印失败, 未知文件类型或无文本内容");
                        }
                    } catch (InterruptedException | ExecutionException e) {
                        log.error("生成水印失败", e);
                        Thread.currentThread().interrupt();
                        throw new MithrasException("生成水印失败");
                    }
                }
            }
        } else if (StrUtil.equalsAny(param.getMaterialsType(), LeaseFileTypeEnums.PROJECT_MANAGER_UPLOAD_OWNERSHIP.name(), LeaseFileTypeEnums.PROJECT_MANAGER_UPLOAD_DUPLICATE_CHECK.name(), LeaseFileTypeEnums.PROJECT_MANAGER_UPLOAD_OTHER.name())) {
            checkFlowFile(param.getLeaseholdId());
            if (!sysUserService.currentUserIsSpecificJob(JobEnum.projmanager.name())) {
                throw new AuthCheckException("非项目经理,不可上传");
            }
            for (int i = 0; i < param.getFiles().size(); i++) {
                MultipartFile file = param.getFiles().get(i);
                try {
                    byteArrayInputStreams.add(Pair.of(file.getOriginalFilename(), file.getInputStream()));
                } catch (IOException e) {
                    log.error("项目经理上传文件失败", e);
                    throw new MithrasException("项目经理上传文件失败");
                }
            }
        }
        //上传文件
        List<FileUploadRSP> rsps = new ArrayList<>();
        if (CollUtil.isNotEmpty(byteArrayInputStreams)) {
            byteArrayInputStreams.forEach(pair -> {
                Long fileId = materialsListService.add(pair.getValue(), pair.getKey(),
                        param.getLeaseholdId(), param.getMaterialsType(), param.getMaterialsSubType(), "LEASE_DATA_LIST");
                FileUploadRSP rsp = new FileUploadRSP();
                rsp.setFileId(fileId);
                rsp.setMainId(param.getLeaseholdId());
                rsp.setModuleType("LEASE_DATA_LIST");
                rsp.setMaterialsType(param.getMaterialsType());
                rsp.setMaterialsSubType(param.getMaterialsSubType());
                rsps.add(rsp);
            });
        }

        return rsps;
    }

    @Override
    public LeaseItemInfo getNewestOne(Long projReviewId) {
        LambdaQueryWrapper<LeaseItemInfo> query = Wrappers.lambdaQuery();
        query.eq(LeaseItemInfo::getProjReviewId, projReviewId);
        query.eq(LeaseItemInfo::getApprovalStatus, ProcessStatus.APPROVAL_PASS.name());
        query.orderByDesc(LeaseItemInfo::getId);
        query.last(StringUtil.mysqlLimitOne());
        return this.getOne(query);
    }

    @Override
    public void checkData(Long id) {
        LeaseItemInfo leaseItemInfo = this.getById(id);
        if (Objects.isNull(leaseItemInfo)) {
            throw new MithrasException("租赁物审核管理数据不存在");
        }
//        if (Objects.isNull(leaseItemInfo.getTotalAmountOfLeaseItem())) {
//            throw new MithrasException("租赁物清单中<租赁物总额>不能为空");
//        }
        if (Objects.isNull(leaseItemInfo.getDuplicateCheckingDate())) {
            throw new MithrasException("中登网查重日期不能为空");
        }
        if (StrUtil.isBlank(leaseItemInfo.getItemListHeader())) {
            throw new MithrasException("数据异常，租赁物清单表头数据不存在");
        }
        // 校验类型和导入表格是否一致
        this.checkHeader(leaseItemInfo, JSONUtil.toList(leaseItemInfo.getItemListHeader(), String.class));
        // 查询必传文件
        List<MaterialsList> materialsList = materialsListService.list(
                BusinessModuleEnum.LEASE_DATA_LIST.name(),
                Arrays.asList(LeaseFileTypeEnums.PROJECT_MANAGER_UPLOAD_OWNERSHIP.name(),
                        LeaseFileTypeEnums.OPERATION_MANAGER_UPLOAD_OWNERSHIP.name(),
                        LeaseFileTypeEnums.OPERATION_MANAGER_UPLOAD_DUPLICATE_CHECK.name(),
                        LeaseFileTypeEnums.PROJECT_MANAGER_UPLOAD_DUPLICATE_CHECK.name()),
                Collections.singletonList(id)
        );
        // 校验法务节点是否选定评估机构
        checkAppraisal(id);

        // 修改一下逻辑，权属文件和查重文件不能为空
        boolean existOwnership = false;
        boolean existDuplicateCheck = false;
        if (CollectionUtil.isNotEmpty(materialsList)) {
            for (MaterialsList materials : materialsList) {
                if (CharSequenceUtil.equalsAny(materials.getMaterialsType(),
                        LeaseFileTypeEnums.OPERATION_MANAGER_UPLOAD_OWNERSHIP.name(),
                        LeaseFileTypeEnums.PROJECT_MANAGER_UPLOAD_OWNERSHIP.name())) {
                    existOwnership = true;
                }
                if (CharSequenceUtil.equalsAny(materials.getMaterialsType(),
                        LeaseFileTypeEnums.OPERATION_MANAGER_UPLOAD_DUPLICATE_CHECK.name(),
                        LeaseFileTypeEnums.PROJECT_MANAGER_UPLOAD_DUPLICATE_CHECK.name())) {
                    existDuplicateCheck = true;
                }
            }
        }
        if (!existOwnership || !existDuplicateCheck) {
            throw new MithrasException("资料清单中<权属文件>和<查重文件>不能为空");
        }
    }

    private void checkAppraisal(Long mainId) {
        BusinessModuleEnum businessModule = BusinessModuleEnum.LEASE_TEXT;
        ProcessPageReq req = new ProcessPageReq();
        req.setBusinessKey(String.valueOf(mainId));
        req.setPageIndex(1);
        req.setPageSize(1);
        req.setModelKeyList(businessModule.getModelKeyList());
        req.setProcessStatusList(Collections.singletonList(ProcessBusinessStatusEnum.RUNNING.getType()));
        ProcessResp processResp = flowTaskApiService.queryProcess(req).getContents()
                .stream().findFirst().orElse(null);
        if(processResp == null) return;
        if (Objects.equals(processResp.getCurTaskActivityIds(), "legalManagerUser")) {
            // 法务经理节点校验是否选定评估机构
            List<LeaseItemAppraisalRelation> relationList = relationMapper.selectList(Wrappers.<LeaseItemAppraisalRelation>lambdaQuery()
                    .eq(LeaseItemAppraisalRelation::getLeaseItemId, mainId));
            if(CollectionUtil.isNotEmpty(relationList)) {
                for (LeaseItemAppraisalRelation relation : relationList) {
                    if (Objects.equals(relation.getSelectType(), LeaseAppraisalSelectEnum.SELECTED.name())) {
                        return;
                    }
                }
                throw new MithrasException("至少需要有一个选定的评估机构");
            }
        }
    }

    public void checkFlowFile(Long mainId) {
        BusinessModuleEnum businessModule = BusinessModuleEnum.LEASE_TEXT;
        ProcessPageReq req = new ProcessPageReq();
        req.setBusinessKey(String.valueOf(mainId));
        req.setPageIndex(1);
        req.setPageSize(1);
        req.setModelKeyList(businessModule.getModelKeyList());
        req.setProcessStatusList(Collections.singletonList(ProcessBusinessStatusEnum.RUNNING.getType()));
        ProcessResp processResp = flowTaskApiService.queryProcess(req).getContents()
                .stream().findFirst().orElse(null);
        if (Objects.isNull(processResp)) {
            throw new AuthCheckException("流程已结束，不允许上传文件");
        }
    }

    @Override
    public R<Boolean> checkRepeatSave(LeaseCheckRepeatREQ param) {
        Assert.notNull(baseMapper.selectById(param.getId()), () -> new MithrasException("记录不存在！"));
        try {
            DateTime dateTime = DateUtil.parse(param.getCheckRepeatDate());
            LambdaUpdateWrapper<LeaseItemInfo> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(LeaseItemInfo::getId, param.getId())
                    .set(LeaseItemInfo::getDuplicateCheckingDate, dateTime);
            return R.ok(this.update(updateWrapper));
        } catch (Exception e) {
            throw new MithrasException("中登网查重日期格式错误");
        }
    }

    @Override
    public Boolean updateContractIds(Long targetId, Long contractId, LeaseOperationTypeEnum type) {
        LeaseItemInfo itemInfo = getById(targetId);
        if (Objects.isNull(itemInfo)) {
            throw new MithrasException("不存在租赁物审核记录，targetId可能存在错误！");
        }
        List<Long> currentIds = JSONUtil.toList(itemInfo.getContractIds(), Long.class);
        Set<Long> ids = new LinkedHashSet<>(currentIds);
        if (LeaseOperationTypeEnum.INSERT.equals(type)) {
            ids.add(contractId);
        } else if (LeaseOperationTypeEnum.REMOVE.equals(type)) {
            ids.remove(contractId);
        } else {
            throw new MithrasException("操作类型错误，请检查！");
        }
        LeaseItemInfo build = LeaseItemInfo.builder().id(targetId).contractIds(ids.toString()).build();
        return updateById(build);
    }

    @Override
    public LeaseItemMetadataRSP getLeaseItemMetadata(Long id) {
        LeaseItemInfo leaseItemInfo = this.getById(id);
        if (Objects.isNull(leaseItemInfo)) {
            throw new MithrasException("租赁物审核管理数据不存在");
        }
        LeaseItemMetadataRSP rsp = new LeaseItemMetadataRSP();
        rsp.setLeaseItemTypes(Optional.ofNullable(leaseItemInfo.getLeaseItemTypes()).map(e -> JSONUtil.toList(e, String.class)).orElse(Collections.emptyList()));
        rsp.setOwnershipFileTypes(Optional.ofNullable(leaseItemInfo.getOwnershipFileTypes()).map(e -> JSONUtil.toList(e, String.class)).orElse(Collections.emptyList()));
        rsp.setValueIdentificationFiles(Optional.ofNullable(leaseItemInfo.getValueIdentificationFiles()).map(e -> JSONUtil.toList(e, String.class)).orElse(Collections.emptyList()));
        return rsp;
    }

    @Override
    public void saveLeaseItemMetadata(LeaseItemMetadataREQ req) {
        LeaseItemInfo leaseItemInfo = this.getById(req.getId());
        if (Objects.isNull(leaseItemInfo)) {
            throw new MithrasException("租赁物审核管理数据不存在");
        }
        leaseItemInfo.setLeaseItemTypes(JSONUtil.toJsonStr(req.getLeaseItemTypes()));
        leaseItemInfo.setLeaseItemCategories(JSONUtil.toJsonStr(convertToLeaseItemCategories(req.getLeaseItemTypes())));
        leaseItemInfo.setOwnershipFileTypes(JSONUtil.toJsonStr(req.getOwnershipFileTypes()));
        leaseItemInfo.setValueIdentificationFiles(JSONUtil.toJsonStr(req.getValueIdentificationFiles()));
        this.updateById(leaseItemInfo);
    }

    @Override
    public void init() {
        List<LeaseItemInfo> leaseItemInfos = baseMapper.selectList(Wrappers.<LeaseItemInfo>lambdaQuery()
                .isNotNull(LeaseItemInfo::getLeaseItemTypes));
        for (LeaseItemInfo leaseItemInfo : leaseItemInfos) {
            List<String> leaseItemTypes = JSONUtil.toList(leaseItemInfo.getLeaseItemTypes(), String.class);
            List<String> leaseItemCategories = convertToLeaseItemCategories(leaseItemTypes);
            leaseItemInfo.setLeaseItemCategories(JSONUtil.toJsonStr(leaseItemCategories));
        }
        this.updateBatchById(leaseItemInfos);
    }

    @Override
    public void downloadLeaseItemTemplate(Long id, OutputStream outputStream) {
        LeaseItemInfo leaseItemInfo = this.getById(id);
        if (Objects.isNull(leaseItemInfo)) {
            throw new MithrasException("租赁物审核管理数据不存在");
        }
        if (StrUtil.isBlank(leaseItemInfo.getLeaseItemTypes())) {
            throw new MithrasException("租赁物类型未保存");
        }
        List<String> types = JSONUtil.toList(leaseItemInfo.getLeaseItemTypes(), String.class);
        Set<String> fileNames = new HashSet<>();
        for (String type : types) {
            fileNames.add(this.ensureTemplateName(type));
        }
        LambdaQueryWrapper<FileTemplate> query = Wrappers.lambdaQuery();
        query.eq(FileTemplate::getTemplateType, "租赁物管理-租赁物清单");
        query.in(FileTemplate::getFilename, fileNames);
        query.eq(FileTemplate::getOutdated, false);
        List<FileTemplate> fileTemplateList = fileTemplateService.list(query);
        if (CollectionUtil.isEmpty(fileTemplateList)) {
            throw new MithrasException("没有符合条件的租赁物清单模板");
        }
        Map<Long, FileTemplate> fileTemplateMap = fileTemplateList.stream().collect(Collectors.toMap(FileTemplate::getId, e -> e));
        List<MaterialsList> materialsListList = materialsListService.list(BusinessModuleEnum.FILE_TEMPLATE.name(), null, new LinkedList<>(fileTemplateMap.keySet()));
        if (CollectionUtil.isEmpty(materialsListList)) {
            throw new MithrasException("没有符合条件的租赁物清单模板文件");
        }
        String[] paths = new String[materialsListList.size()];
        InputStream[] inputStreams = new InputStream[materialsListList.size()];
        for (int i = 0; i < inputStreams.length; i++) {
            MaterialsList materials = materialsListList.get(i);
            inputStreams[i] = getBean(MinioOssClient.class).downLoad(materials.getOssFilename());
            paths[i] = fileTemplateMap.get(materials.getBelongId()).getFilename();
        }
        ZipUtil.zip(outputStream, paths, inputStreams);
    }

//    @Transactional(rollbackFor = Throwable.class)
    @Override
    public void importItemList(Long id, InputStream inputStream) {
        LeaseItemInfo leaseItemInfo = this.getById(id);
        if (Objects.isNull(leaseItemInfo)) {
            throw new MithrasException("租赁物审核管理数据不存在");
        }
        // 清除历史数据
        leaseItemListRowDataService.removeByLeaseItemInfoId(id);
        // 读取文件处理
        ExcelReader excelReader = ExcelUtil.getReader(inputStream);
        // 总行数
        int total = excelReader.getRowCount();
        if (total > 15000) {
            throw new MithrasException("最多支持导入15000条数据");
        }
        // 读取表头
        List<Object> firstRow = excelReader.readRow(0);
        if (CollectionUtil.isEmpty(firstRow)) {
            throw new MithrasException("没有从文件中获取到表头，请检查导入文件");
        }
        // 忽略序号字段
        List<String> headerList = firstRow.stream().map(e -> Optional.ofNullable(e).map(Object::toString).orElse("")).filter(e -> !Objects.equals(e, "序号")).collect(Collectors.toList());
        // 校验表头
        this.checkHeader(leaseItemInfo, headerList);
        leaseItemInfo.setItemListHeader(JSONUtil.toJsonStr(headerList));
        this.updateById(leaseItemInfo);
        // 读取数据
        List<Map<String, Object>> dataList = excelReader.readAll();
        if (CollectionUtil.isNotEmpty(dataList)) {
            List<LeaseItemListRowData> itemListRowDataList = new LinkedList<>();
            // 过滤带星号表头
            Set<String> nonNullHeaderNames = headerList.stream().filter(e -> StrUtil.isNotBlank(e) && e.endsWith("*")).collect(Collectors.toSet());
            int i = 0;
            for (Map<String, Object> dataMap : dataList) {
                // 预处理
                leaseItemCommonService.preHandle(dataMap);
                i++;
                checkData(dataMap, i, nonNullHeaderNames);
                LeaseItemListRowData leaseItemListRowData = new LeaseItemListRowData();
                leaseItemListRowData.setLeaseItemInfoId(id);
                leaseItemListRowData.setRowData(JSONUtil.toJsonStr(dataMap, JSONConfig.create().setIgnoreNullValue(false)));
                itemListRowDataList.add(leaseItemListRowData);
            }
            leaseItemListRowDataService.saveBatch(itemListRowDataList);
        }
        // 自动生成租赁物清单文本
        this.generateLeaseItemFile(leaseItemInfo);
    }

    @Override
    public void generateLeaseItemFile(LeaseItemInfo leaseItemInfo) {
        List<MaterialsList> materialsListList = materialsListService.list(BusinessModuleEnum.LEASE_TEXT.name(), Collections.singletonList(LeaseTextFileEnum.LEASE_ITEM.name()), Collections.singletonList(leaseItemInfo.getId()));
        if (CollectionUtil.isNotEmpty(materialsListList)) {
            // 删除自动生成的
            Set<Long> ids = materialsListList.stream().filter(e -> Objects.equals(e.getSystemGenerate(), YesOrNoNumberEnum.YES.getCode()) &&
                    Objects.equals(e.getIsEdit(), YesOrNoNumberEnum.NO.getCode())).map(MaterialsList::getId).collect(Collectors.toSet());
            materialsListService.removeByIds(ids);
        }
        List<LeaseItemListRowData> leaseItemListRowDataList = leaseItemListRowDataService.listByLeaseItemInfoId(leaseItemInfo.getId());
        if (CollectionUtil.isEmpty(leaseItemListRowDataList)) {
            return;
        }
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            String fileName = leaseItemTextRender.render(outputStream, leaseItemInfo);
            try (InputStream is = IoUtil.toStream(outputStream)) {
                materialsListService.add(is, fileName, leaseItemInfo.getId(), LeaseTextFileEnum.LEASE_ITEM.name(), null, BusinessModuleEnum.LEASE_TEXT.name(), YesOrNoNumberEnum.YES);
            }
        } catch (Exception e) {
            log.error("自动生成租赁物文本-租赁物清单发生未知异常", e);
        }
    }

    private void checkHeader(LeaseItemInfo leaseItemInfo, List<String> headerList) {
        if (StrUtil.isBlank(leaseItemInfo.getLeaseItemTypes())) {
            throw new MithrasException("请先勾选并保存租赁物类型");
        }
        List<String> types = JSONUtil.toList(leaseItemInfo.getLeaseItemTypes(), String.class);
        for (String type : types) {
            LeaseItemManagerLeaseItemType leaseItemManagerLeaseItemType = LeaseItemManagerLeaseItemType.of(type);
            if (Objects.isNull(leaseItemManagerLeaseItemType)) {
                continue;
            }
            List<String> templateHeaderList = leaseItemManagerLeaseItemType.getTemplateHeaderList();
            int count = 0;
            for (String s : headerList) {
                if (templateHeaderList.contains(s)) {
                    count++;
                }
            }
            if (count >= templateHeaderList.size()) {
                return;
            }
        }
        throw new MithrasException("导入文件不符合勾选的租赁物类型");
    }

    private void checkData(Map<String, Object> dataMap, int rowNum, Set<String> nonNullHeaderNames) {
        for (String headerName : DEFAULT_MONEY_HEADER) {
            Object cellValue = dataMap.get(headerName);
            if (Objects.isNull(cellValue)) {
                continue;
            }
            if (cellValue instanceof String && StrUtil.isBlank(cellValue.toString())) {
                continue;
            }
            // 为了踢掉科学技术法，把E替换成其他字母
            if (!NumberUtil.isNumber(cellValue.toString().replace("E", "A").replace("e", "A"))) {
                throw new MithrasException(String.format("第%s行，租赁物信息有误(%s)，请检查!", rowNum, headerName));
            }
        }
        if (CollectionUtil.isEmpty(nonNullHeaderNames)) {
            return;
        }
        for (String headerName : nonNullHeaderNames) {
            Object cellValue = dataMap.get(headerName);
            if (Objects.isNull(cellValue)) {
                throw new MithrasException(String.format("第%s行，<%s>不能为空", rowNum, headerName));
            }
            if (cellValue instanceof String) {
                String s = (String) cellValue;
                if (StrUtil.isBlank(s.trim())) {
                    throw new MithrasException(String.format("第%s行，<%s>不能为空", rowNum, headerName));
                }
            }
        }
    }

    @Override
    public void exportItemList(LeaseItemListExportREQ req, OutputStream outputStream) {
        LeaseItemInfo leaseItemInfo = this.getById(req.getId());
        if (Objects.isNull(leaseItemInfo)) {
            throw new MithrasException("租赁物审核管理数据不存在");
        }
        if (StrUtil.isBlank(leaseItemInfo.getItemListHeader())) {
            throw new MithrasException("暂无可导出的数据");
        }
        LambdaQueryWrapper<LeaseItemListRowData> query = Wrappers.lambdaQuery();
        query.eq(LeaseItemListRowData::getLeaseItemInfoId, leaseItemInfo.getId());
        if (CollectionUtil.isNotEmpty(req.getItemIds())) {
            query.in(LeaseItemListRowData::getId, req.getItemIds());
        }
        List<LeaseItemListRowData> dataList = leaseItemListRowDataService.list(query);
        // 写文件
        ExcelWriter excelWriter = ExcelUtil.getWriter(true);
        // 写表头
        excelWriter.writeHeadRow(leaseItemCommonService.preHandleHeader(JSONUtil.toList(leaseItemInfo.getItemListHeader(), String.class)));
        // 写数据
        if (CollectionUtil.isNotEmpty(dataList)) {
            for (int i = 0; i < dataList.size(); i++) {
                LeaseItemListRowData leaseItemListRowData = dataList.get(i);
                Map<String, Object> map = JSONUtil.toBean(leaseItemListRowData.getRowData().replace("\n", "\\n"), Map.class);
                map.put("序号", i + 1);
                excelWriter.writeRow(map, false);
            }
        }
        excelWriter.autoSizeColumnAll();
        // 写出到输出流
        excelWriter.flush(outputStream, true);
    }

    @Override
    public LeaseItemListRSP listItemWithPage(LeaseItemListREQ req) {
        LeaseItemInfo leaseItemInfo = this.getById(req.getId());
        if (Objects.isNull(leaseItemInfo)) {
            throw new MithrasException("租赁物审核管理数据不存在");
        }
        LeaseItemListRSP rsp = new LeaseItemListRSP();
        //rsp.setLeaseItemTotalAmount(leaseItemInfo.getTotalAmountOfLeaseItem());
        // 查看是否有合同
        List<ContractBaseInfo> contractBaseInfoList = contractBaseInfoService.selectListByProjId(leaseItemInfo.getProjReviewId());
        if (CollectionUtil.isEmpty(contractBaseInfoList)) {
            // 查看评审
            ProjReviewBaseInfo projReviewBaseInfo = projReviewBaseInfoService.getById(leaseItemInfo.getProjReviewId());
            //rsp.setTotalCost(projReviewBaseInfo.getDeclaredAmount());
        } else {
            contractBaseInfoList.removeIf(e -> StrUtil.equalsAny(e.getContractStatus(), ContractStatus.CLOSED.name(), ContractStatus.INVALID.name()));
            //rsp.setTotalCost(contractBaseInfoList.stream().mapToLong(ContractBaseInfo::getApplyCreditAmount).sum());
        }
        if (StrUtil.isNotBlank(leaseItemInfo.getItemListHeader())) {
            List<String> oldHeaderList = JSONUtil.toList(leaseItemInfo.getItemListHeader(), String.class);
            rsp.setHeaderList(leaseItemCommonService.preHandleHeader(oldHeaderList));
        }
        // 查询租赁物清单数据
        Page<LeaseItemListRowData> pageQuery = new Page<>(req.getPage(), req.getPageSize());
        LambdaQueryWrapper<LeaseItemListRowData> conditionQuery = Wrappers.<LeaseItemListRowData>lambdaQuery().eq(LeaseItemListRowData::getLeaseItemInfoId, req.getId());
        Page<LeaseItemListRowData> dbResult = leaseItemListRowDataService.page(pageQuery, conditionQuery);
        if (CollectionUtil.isEmpty(dbResult.getRecords())) {
            rsp.setPageList(PageR.empty(req.getPage(), req.getPageSize()));
        } else {
            List<LeaseItemListRSP.RowDataModel> dataList = new ArrayList<>(dbResult.getRecords().size());
            for (int i = 0; i < dbResult.getRecords().size(); i++) {
                LeaseItemListRowData leaseItemListRowData = dbResult.getRecords().get(i);
                Map<String, Object> dataMap = JSONUtil.toBean(leaseItemListRowData.getRowData().replace("\n", "\\n"), Map.class);
                // 序号处理
                dataMap.put("序号", i + 1);
                LeaseItemListRSP.RowDataModel rowDataModel = new LeaseItemListRSP.RowDataModel();
                List<LeaseItemListRSP.CellDataModel> cellDataModelList = new LinkedList<>();
                rowDataModel.setItemId(leaseItemListRowData.getId());
                rowDataModel.setMatchColumns(leaseItemListRowData.getMatchColumns());
                for (Map.Entry<String, Object> entry : dataMap.entrySet()) {
                    cellDataModelList.add(new LeaseItemListRSP.CellDataModel(entry.getKey(), entry.getValue()));
                }
                rowDataModel.setDataList(cellDataModelList);
                dataList.add(rowDataModel);
            }
            rsp.setPageList(PageR.of(dataList, dbResult.getTotal(), req.getPage(), req.getPageSize()));
        }
        LambdaQueryWrapper<LeaseItemVehicleRegistrationCertificate> query = Wrappers.lambdaQuery();
        query.eq(LeaseItemVehicleRegistrationCertificate::getDeleted, 0L)
                .eq(LeaseItemVehicleRegistrationCertificate::getLeaseItemInfoId, req.getId());
        List<LeaseItemVehicleRegistrationCertificate> vehicleList = vehicleRegistrationService.list(query);
        if (vehicleList != null && !vehicleList.isEmpty()) {
            rsp.setUploadedOcrFile(true);
            return rsp;
        } else {
            LambdaQueryWrapper<LeaseItemVatInvoice> vatQuery = Wrappers.lambdaQuery();
            vatQuery.eq(LeaseItemVatInvoice::getDeleted, 0L)
                    .eq(LeaseItemVatInvoice::getLeaseItemInfoId, req.getId());;
            List<LeaseItemVatInvoice> leaseItemVatInvoiceList = leaseItemVatInvoiceService.list(vatQuery);
            if (leaseItemVatInvoiceList != null && !leaseItemVatInvoiceList.isEmpty()) {
                rsp.setUploadedOcrFile(true);
                return rsp;
            }
        }
        rsp.setUploadedOcrFile(false);
        return rsp;
    }

    private List<LeaseLedgerMainRSP> handlerPageResult(Page<LeaseItemInfo> itemInfoPage, Map<Long, List<ContractBaseInfo>> contractMap) {
        List<Long> reviewIds = itemInfoPage.getRecords().stream().map(LeaseItemInfo::getProjReviewId).collect(Collectors.toList());
//        List<ContractBaseInfo> baseInfos = contractBaseInfoService.lambdaQuery().in(CollUtil.isNotEmpty(reviewIds), ContractBaseInfo::getProjReviewId, reviewIds).list();
        List<ProjReviewBaseInfo> projReviewBaseInfos = projReviewBaseInfoService.lambdaQuery().in(CollUtil.isNotEmpty(reviewIds), ProjReviewBaseInfo::getId, reviewIds).list();
        Map<Long, List<ContractBaseInfo>> infoMap = null;
        Map<Long, ProjReviewBaseInfo> projReviewBaseInfoMap = null;
//        if (CollUtil.isNotEmpty(baseInfos)) {
//            infoMap = baseInfos.stream().collect(Collectors.groupingBy(ContractBaseInfo::getProjReviewId));
//        }
        if (CollUtil.isNotEmpty(projReviewBaseInfos)) {
            projReviewBaseInfoMap = projReviewBaseInfos.stream().collect(Collectors.toMap(ProjReviewBaseInfo::getId, Function.identity(), (k1, k2) -> k1));
        }
        List<LeaseLedgerMainRSP> res = new ArrayList<>();
//        Map<Long, List<ContractBaseInfo>> finalInfoMap = infoMap;
        Map<Long, ProjReviewBaseInfo> finalProjectMap = projReviewBaseInfoMap;
        //先拿出客户信息，避免多次查询数据库
        List<Long> clientIds = itemInfoPage.getRecords().stream().map(LeaseItemInfo::getClientId).collect(Collectors.toList());
        Set<Long> userIds = itemInfoPage.getRecords().stream().map(LeaseItemInfo::getProjSponsorUserId).collect(Collectors.toSet());
        itemInfoPage.getRecords().forEach(dto -> {
            List<Long> array = JSON.parseArray(dto.getProjCosponsorUserIds(), Long.class);
            if (CollUtil.isNotEmpty(array)) {
                userIds.addAll(array);
            }
        });
        Map<Long, String> userMap = id2NameService.sysUserId2Name(userIds);
        Map<Long, String> clientMap = id2NameService.clientId2Name(clientIds);

        itemInfoPage.getRecords().forEach(one -> {
            LeaseLedgerMainRSP ledgerMainRes = LeaseLedgerMainRSP.builder()
                    .id(one.getId())
                    .leaseAuditFlowNumber(one.getFlowId())
                    .clientName(clientMap.get(one.getClientId()))
                    .projectName(one.getProjName())
                    .projectOrganizer(userMap.get(one.getProjSponsorUserId()))
                    .leaseStatus(one.getApprovalStatus())
                    .projCode("-")
                    .projectCoOrganizer(Collections.emptyList())
                    .contractCode(Collections.emptySet())
                    .createTime(DateUtil.format(one.getCreateTime(), "yyyy-MM-dd HH:mm:ss"))
                    .build();
            if (CharSequenceUtil.isNotEmpty(one.getProjCosponsorUserIds())) {
                List<Long> ids = JSON.parseArray(one.getProjCosponsorUserIds(), Long.class);
                List<String> nameList = new ArrayList<>();
                ids.forEach(id -> nameList.add(userMap.get(id)));
                ledgerMainRes.setProjectCoOrganizer(nameList);
            }
            if (CollUtil.isNotEmpty(contractMap)) {
                List<ContractBaseInfo> list = contractMap.get(one.getId());
                if (CollectionUtil.isNotEmpty(list)) {
                    ledgerMainRes.setContractCode(list.stream().map(ContractBaseInfo::getContractCode).collect(Collectors.toSet()));
                }
//                ledgerMainRes.setContractCode(CollUtil.isEmpty(finalInfoMap.get(one.getProjReviewId())) ? Collections.emptySet() : finalInfoMap.get(one.getProjReviewId()).stream().collect(Collectors.toMap(ContractBaseInfo::getContractCode, Function.identity(), (a, b) -> a)).keySet());
            }
            if (CollUtil.isNotEmpty(finalProjectMap)) {
                ledgerMainRes.setProjCode(Objects.isNull(finalProjectMap.get(one.getProjReviewId())) ? "-" : finalProjectMap.get(one.getProjReviewId()).getProjCode());
            }
            res.add(ledgerMainRes);
        });
        return res;
    }

    private String ensureTemplateName(String type) {
        if (Objects.equals(type, LeaseItemManagerLeaseItemType.BUS.name())) {
            return "租赁物管理_租赁物清单模板_公交车.xlsx";
        }
        if (Objects.equals(type, LeaseItemManagerLeaseItemType.MOTOR_VEHICLE.name())) {
            return "租赁物管理_租赁物清单模板_经销商.xlsx";
        }
        if (Objects.equals(type, LeaseItemManagerLeaseItemType.VESSEL.name())) {
            return "租赁物管理_租赁物清单模板_船舶.xlsx";
        }
        if (Objects.equals(type, LeaseItemManagerLeaseItemType.COMMUNICATION_BASE_STATION.name())) {
            return "租赁物管理_租赁物清单模板_通信基站.xlsx";
        }
        if (Objects.equals(type, LeaseItemManagerLeaseItemType.PUBLIC.name())) {
            return "租赁物管理_租赁物清单模板_公用事业.xlsx";
        }
        if (Objects.equals(type, LeaseItemManagerLeaseItemType.PRODUCTION_LINE.name())) {
            return "租赁物管理_租赁物清单模板_生产线及非生产线设备.xlsx";
        }
        return "租赁物管理_租赁物清单模板_生产设备.xlsx";
    }


    private List<String> convertToLeaseItemCategories(List<String> leaseItemTypes) {
        if (leaseItemTypes == null || leaseItemTypes.isEmpty()) {
            return new ArrayList<>();
        }
        List<String> leaseItemCategories = new ArrayList<>();
        for (String leaseItemType : leaseItemTypes) {
            String leaseItemCategory = Optional.ofNullable(LeaseItemManagerLeaseItemType.of(leaseItemType)).map(LeaseItemManagerLeaseItemType::getDisplay).orElse(null);
            //??？ 不理解为什么这样写
            /*if (LeaseItemManagerLeaseItemType.BUS.name().equalsIgnoreCase(leaseItemType)) {
                leaseItemCategory = LeaseItemManagerLeaseItemType.BUS.display();
            } else if (LeaseItemManagerLeaseItemType.MOTOR_VEHICLE.name().equalsIgnoreCase(leaseItemType)) {
                leaseItemCategory = LeaseItemManagerLeaseItemType.MOTOR_VEHICLE.display();
            } else if (LeaseItemManagerLeaseItemType.NO_PRODUCTION_LINE_EQUIPMENT.name().equalsIgnoreCase(leaseItemType)) {
                leaseItemCategory = LeaseItemManagerLeaseItemType.NO_PRODUCTION_LINE_EQUIPMENT.display();
            } else if (LeaseItemManagerLeaseItemType.PRODUCTION_LINE.name().equalsIgnoreCase(leaseItemType)) {
                leaseItemCategory = LeaseItemManagerLeaseItemType.PRODUCTION_LINE.display();
            } else if (LeaseItemManagerLeaseItemType.VESSEL.name().equalsIgnoreCase(leaseItemType)) {
                leaseItemCategory = LeaseItemManagerLeaseItemType.VESSEL.display();
            } else if (LeaseItemManagerLeaseItemType.COMMUNICATION_BASE_STATION.name().equalsIgnoreCase(leaseItemType)) {
                leaseItemCategory = LeaseItemManagerLeaseItemType.COMMUNICATION_BASE_STATION.display();
            } else if (LeaseItemManagerLeaseItemType.PUBLIC.name().equalsIgnoreCase(leaseItemType)) {
                leaseItemCategory = LeaseItemManagerLeaseItemType.PUBLIC.display();
            } else if (LeaseItemManagerLeaseItemType.OTHER.name().equalsIgnoreCase(leaseItemType)) {
                leaseItemCategory = LeaseItemManagerLeaseItemType.OTHER.display();
            }*/
            leaseItemCategories.add(leaseItemCategory);
        }
        return leaseItemCategories;
    }

    private LambdaQueryWrapper<LeaseItemInfo> initDefaultQuery() {
        Long currentUserId = AccountUtil.getLoginInfo().getId();
        LambdaQueryWrapper<LeaseItemInfo> query = Wrappers.lambdaQuery();
        boolean isProjManager = sysUserService.userIsSpecificJob(currentUserId, JobEnum.projmanager.name());
        boolean isBusinesshead = sysUserService.userIsSpecificJob(currentUserId, JobEnum.businesshead.name());
        boolean isLeaderincharge = sysUserService.userIsSpecificJob(currentUserId, JobEnum.leaderincharge.name());
        if (isProjManager && !isBusinesshead && !isLeaderincharge) {
            query.eq(LeaseItemInfo::getProjSponsorUserId, currentUserId);
            return query;
        }
        List<Long> canViewDeptIds = sysUserService.canViewDeptIds();
        if (Objects.isNull(canViewDeptIds)) {
            // 不限制
            return query;
        }
        if (canViewDeptIds.isEmpty()) {
            // 没有可看的部门，填一个不可能的部门id即可
            query.eq(LeaseItemInfo::getBizDeptId, -100);
            return query;
        }
        // 可以看指定部门
        query.in(LeaseItemInfo::getBizDeptId, canViewDeptIds);
        return query;
    }
}




