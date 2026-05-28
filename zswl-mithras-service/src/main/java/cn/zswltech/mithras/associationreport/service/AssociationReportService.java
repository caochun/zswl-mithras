package cn.zswltech.mithras.associationreport.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.zswltech.flow.core.api.FlowProcessApiService;
import cn.zswltech.flow.core.domain.req.StartProcessReq;
import cn.zswltech.flow.core.enums.ProcessBusinessStatusEnum;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.gruul.dao.dal.entity.OrgDO;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.associationreport.AssociationReportException;
import cn.zswltech.mithras.associationreport.DeleteDataSelector;
import cn.zswltech.mithras.associationreport.StoreDataSelector;
import cn.zswltech.mithras.associationreport.excel.AssociationReportBaseModel;
import cn.zswltech.mithras.dto.associationreport.*;
import cn.zswltech.mithras.dto.flow.execution.ExecutionProcessBaseREQ;
import cn.zswltech.mithras.service.constant.GlobalConstants;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.service.constant.VersionTypeConstants;
import cn.zswltech.mithras.service.enums.*;
import cn.zswltech.mithras.service.enums.associationreport.AssociationProcessStatusEnum;
import cn.zswltech.mithras.service.enums.associationreport.AssociationReportCategoryEnum;
import cn.zswltech.mithras.service.enums.associationreport.AssociationReportPeriodCategoryEnum;
import cn.zswltech.mithras.service.enums.associationreport.AssociationReportStatusEnum;
import cn.zswltech.mithras.service.mapper.associationreport.AssociationReportMapper;
import cn.zswltech.mithras.service.mapper.associationreport.AssociationReportSortMapper;
import cn.zswltech.mithras.service.mapper.file.template.model.FileTemplate;
import cn.zswltech.mithras.service.mapper.model.MaterialsList;
import cn.zswltech.mithras.service.mapper.model.associationreport.AssociationReport;
import cn.zswltech.mithras.service.mapper.model.associationreport.AssociationReportApply;
import cn.zswltech.mithras.service.mapper.model.associationreport.AssociationReportSort;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.others.SpringContextHolder;
import cn.zswltech.mithras.service.service.SysUserService;
import cn.zswltech.mithras.service.service.file.template.FileTemplateService;
import cn.zswltech.mithras.service.service.flow.ExecutionService;
import cn.zswltech.mithras.service.service.flow.FlowEndEventProcessor;
import cn.zswltech.mithras.service.service.lib.association.impl.AssociationReportVersionServiceServiceImpl;
import cn.zswltech.mithras.service.service.materialsfile.MaterialsListService;
import cn.zswltech.mithras.service.util.StringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author dingqi
 * @date 2025/4/21
 * @description
 */
@Slf4j
@Service
public class AssociationReportService extends ServiceImpl<AssociationReportMapper, AssociationReport> implements FlowEndEventProcessor {
    @Resource
    private MaterialsListService materialsListService;
    @Resource
    private AssociationFileHandleReportService associationFileHandleReportService;
    @Resource
    private AssociationReportSortMapper associationReportSortMapper;
    @Value("${association.zlAccount:}")
    private String zlAccount;
    @Resource
    private AssociationReportApplyService associationReportApplyService;
    @Resource
    private AssociationReportVersionServiceServiceImpl associationReportVersionServiceService;

    @Value("${association.realtimeMinusMonths:}")
    private long realtimeMinusMonths;

    public AssociationReport findByCategoryYearPeriod(String category, int year, int period) {
        LambdaQueryWrapper<AssociationReport> query = Wrappers.lambdaQuery();
        query.eq(AssociationReport::getReportCategoryCode, category);
        query.eq(AssociationReport::getReportYear, year);
        query.eq(AssociationReport::getReportPeriod, period);
        query.eq(AssociationReport::getReportStatus, AssociationReportStatusEnum.SUCCESS.name());
        query.orderByDesc(AssociationReport::getReportTime);
        query.last(StringUtil.mysqlLimitOne());
        return this.getOne(query);
    }

    public String getTemplateFileUrl(String reportCategoryCode) {
        AssociationReportCategoryEnum category = AssociationReportCategoryEnum.findByName(reportCategoryCode);
        if (Objects.isNull(category)) {
            throw new MithrasException("未定义的报表类型");
        }
        String fileTemplateType = "金融协会报送";
        String fileTemplateName;
        switch (category) {
            case J0001: {
                fileTemplateName = "融资租赁公司基本情况统计表.xlsx";
                break;
            }
            case J0002: {
                fileTemplateName = "股东股权信息一览表-股东股权信息.xlsx";
                break;
            }
            case J0003: {
                fileTemplateName = "股东股权信息一览表-股东变更记录.xlsx";
                break;
            }
            case J0004: {
                fileTemplateName = "高管信息一览表.xlsx";
                break;
            }
            case J0005: {
                fileTemplateName = "融资租赁公司业务情况表.xlsx";
                break;
            }
            case J0006: {
                fileTemplateName = "融资租赁公司服务实体经济情况.xlsx";
                break;
            }
            case J0007: {
                fileTemplateName = "融资租赁公司资产负债表.xlsx";
                break;
            }
            case J0008: {
                fileTemplateName = "融资租赁公司利润表.xlsx";
                break;
            }
            case J0009: {
                fileTemplateName = "融资租赁公司主要业务清单.xlsx";
                break;
            }
            case J0010: {
                fileTemplateName = "融资租赁公司对外融资清单（季报）.xlsx";
                break;
            }
            case J0011: {
                fileTemplateName = "融资租赁公司最大十家客户（含集团）集中度统计表.xlsx";
                break;
            }
            case J0012: {
                fileTemplateName = "融资租赁公司关联方信息汇总表.xlsx";
                break;
            }
            case J0013: {
                fileTemplateName = "涉法涉讼涉访信息表.xlsx";
                break;
            }
            case J0014: {
                fileTemplateName = "重大事项报告表-基本信息.xlsx";
                break;
            }
            case J0015: {
                fileTemplateName = "重大事项报告表-重大事项报告情况.xlsx";
                break;
            }
            default: {
                throw new MithrasException("暂不支持的报表类型");
            }
        }
        FileTemplate fileTemplate = SpringUtil.getBean(FileTemplateService.class).getTemplateRecord(fileTemplateType, fileTemplateName);
        if (Objects.isNull(fileTemplate)) {
            throw new MithrasException("模板不存在");
        }
        List<MaterialsList> materialsList = materialsListService.list(BusinessModuleEnum.FILE_TEMPLATE.name(), null, ListUtil.of(fileTemplate.getId()));
        MaterialsList materials = materialsList.get(0);
        return materialsListService.download(materials.getId()).getFileUrl();
    }

    public String create(AssociationReportCreateREQ req) {
        //如果是实时报表，周期取月日
        if (req.getPeriodCategory().equals(AssociationReportPeriodCategoryEnum.REALTIME.name())) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMdd");
            String formattedDateTime = LocalDateTime.now().format(formatter);
            req.setPeriod(Integer.valueOf(formattedDateTime));
        }
        AssociationReportCategoryEnum associationReportCategoryEnum = AssociationReportCategoryEnum.findByName(req.getReportCategoryCode());
        if (Objects.isNull(associationReportCategoryEnum)) {
            throw new MithrasException("未定义的报表类型");
        }
        LambdaQueryWrapper<AssociationReport> query = Wrappers.lambdaQuery();
        query.eq(AssociationReport::getReportCategoryCode, req.getReportCategoryCode());
        query.eq(AssociationReport::getReportYear, req.getYear());
        query.eq(AssociationReport::getReportPeriod, req.getPeriod());
        query.orderByDesc(AssociationReport::getBatchNo);
        List<AssociationReport> exist = this.list(query);
        if ( !req.getPeriodCategory().equals(AssociationReportPeriodCategoryEnum.REALTIME.name()) && CollectionUtil.isNotEmpty(exist) && req.getCheckExist()) {//非实时报表，看一下是否有未报送的（包含待报送和报送失败），如果有的话不允许新增
            // 看一下是否有未报送的（包含待报送和报送失败），如果有的话不允许新增
            Optional<AssociationReport> optional = exist.stream().filter(e -> !Objects.equals(e.getReportStatus(), AssociationReportStatusEnum.SUCCESS.name())).findFirst();
            if (optional.isPresent()) {
                throw new MithrasException("待报送中已存在该周期的数据，不允许再次新增");
            }
        }
        LocalDateTime now = LocalDateTime.now();
        String reportInstanceId = LocalDateTimeUtil.format(now, DatePattern.PURE_DATETIME_MS_PATTERN);

        String batchNo = this.generateBatchNo(exist.size());
        if(req.getPeriodCategory().equals(AssociationReportPeriodCategoryEnum.REALTIME.name()) && CollectionUtil.isNotEmpty(exist)){//实时报表，最大批次号+1
            batchNo = this.generateBatchNo(Integer.parseInt(exist.get(0).getBatchNo()) + 1);
        }
        // 创建新数据
        AssociationReport associationReport = new AssociationReport();
        associationReport.setReportCategoryCode(associationReportCategoryEnum.name());
        associationReport.setReportCategoryName(associationReportCategoryEnum.getDisplay());
        if (req.getPeriodCategory().equals(AssociationReportPeriodCategoryEnum.REALTIME.name())) {
            associationReport.setIsRetry(YesOrNoNumberEnum.NO.getCode());
        }else{
            associationReport.setIsRetry(CollectionUtil.isNotEmpty(exist) ? YesOrNoNumberEnum.YES.getCode() : YesOrNoNumberEnum.NO.getCode());
        }
        associationReport.setReportInstanceId(reportInstanceId);
        associationReport.setReportYear(req.getYear());
        associationReport.setReportPeriodCategory(associationReportCategoryEnum.getPeriod().name());
        associationReport.setReportPeriod(req.getPeriod());
        associationReport.setDataSource(req.getDataSource());
        associationReport.setIsShow(Objects.isNull(req.getIsShow()) ? YesOrNoNumberEnum.YES.getCode() : req.getIsShow());
        associationReport.setBatchNo(batchNo);
        associationReport.setReportStatus(AssociationReportStatusEnum.WAIT.name());
        associationReport.setProcessStatus(AssociationProcessStatusEnum.UN_SUBMIT.name());
        this.save(associationReport);
        return associationReport.getReportInstanceId();
    }

    public AssociationReport findByReportInstanceId(String reportInstanceId) {
        LambdaQueryWrapper<AssociationReport> query = Wrappers.lambdaQuery();
        query.eq(AssociationReport::getReportInstanceId, reportInstanceId);
        return this.getOne(query);
    }

    public AssociationReportImportRSP storeFromExcel(AssociationReportImportREQ req) {
        AssociationReport associationReport = this.findByReportInstanceId(req.getReportInstanceId());
        if (Objects.isNull(associationReport)) {
            throw new MithrasException("报送记录主表不存在");
        }
        if (Objects.equals(associationReport.getReportStatus(), AssociationReportStatusEnum.SUCCESS.name())) {
            throw new MithrasException("已报送的不允许导入数据");
        }
        AssociationReportImportRSP rsp = new AssociationReportImportRSP();
        try {
            StoreDataSelector.getInstance(associationReport.getReportCategoryCode()).storeFromExcel(associationReport.getReportInstanceId(), req.getFile().getInputStream());
            rsp.setImportSuccess(Boolean.TRUE);
            return rsp;
        } catch (AssociationReportException e) {
            rsp.setImportSuccess(Boolean.FALSE);
            rsp.setErrorMessageList(e.getErrorMessageList());
            return rsp;
        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            log.error("金融局报送-导入数据发生未知异常[reportInstanceId:{}]", req.getReportInstanceId(), e);
            throw new MithrasException("导入数据发生未知异常");
        }
    }

    public PageR<AssociationReportListRSP> pageList(AssociationReportListREQ req) {
        Page<AssociationReport> pageQuery = new Page<>(req.getPage(), req.getPageSize());
        LambdaQueryWrapper<AssociationReport> conditionQuery = Wrappers.lambdaQuery();
        conditionQuery.eq(AssociationReport::getIsShow, YesOrNoNumberEnum.YES.getCode());
        if(StringUtils.isNotBlank(req.getReportCategoryName())){
            conditionQuery.like(AssociationReport::getReportCategoryName, req.getReportCategoryName());//报表名称
        }
        if(req.getReportPeriodCategoryList()!= null && req.getReportPeriodCategoryList().size()>0){
            conditionQuery.in(AssociationReport::getReportPeriodCategory, req.getReportPeriodCategoryList());//报表实例周期类型
        }
        if(StringUtils.isNotBlank(req.getReportPeriodCategory())){//报表类型
            conditionQuery.eq(AssociationReport::getReportPeriodCategory, req.getReportPeriodCategory());//报表实例周期类型
        }
        if(req.getReportYear()!=null){//报表实例年份
            conditionQuery.eq(AssociationReport::getReportYear, req.getReportYear());
        }
        if(req.getReportPeriod()!=null){//报表实例周期
            conditionQuery.eq(AssociationReport::getReportPeriod, req.getReportPeriod());
        }
        if(req.getReportStatusList()!=null && req.getReportStatusList().size()>0){
            conditionQuery.in(AssociationReport::getReportStatus, req.getReportStatusList());//报送状态
        }
        if(req.getReportCategoryCodeList()!=null && req.getReportCategoryCodeList().size()>0){
            conditionQuery.in(AssociationReport::getReportCategoryCode, req.getReportCategoryCodeList());//报表类型code
        }
        if(req.getProcessStatusList()!=null && req.getProcessStatusList().size()>0){
            conditionQuery.in(AssociationReport::getProcessStatus, req.getProcessStatusList());//审批状态
        }
        if (Objects.nonNull(req.getReportTimeFrom())) {
            LocalDateTime queryDate = req.getReportTimeFrom().atStartOfDay();
            conditionQuery.ge(AssociationReport::getReportTime, queryDate);
        }
        if (Objects.nonNull(req.getReportTimeTo())) {
            // 小于后一天的开始时间 = 小于等于当天的结束时间
            LocalDateTime queryDate = req.getReportTimeTo().atStartOfDay().plusDays(1);
            conditionQuery.lt(AssociationReport::getReportTime, queryDate);
        }
        if("apply".equals(req.getRefereePage())){//上报申请列表查询
            LocalDate today = LocalDate.now();
            LocalDateTime startOfMonth = LocalDateTime.of(today.getYear(), today.getMonthValue(), 1, 0, 0, 0); // 当月开始时间
//            LocalDateTime endOfMonth = today.plusMonths(1).atStartOfDay().minusDays(1); // 当月结束时间
            //筛选本月创建的
            conditionQuery.ge(AssociationReport::getCreateTime, startOfMonth);
//                    .lt(AssociationReport::getCreateTime, endOfMonth.plusSeconds(1)); // 加一秒是为了包含当月的最后一天
        } else {
            //获取金融局报送可以查看的报表类型
            Set<String> queryReportCategoryCodes = SpringUtil.getBean(AssociationReportDataAccessService.class).getCurrentUserAccessQueryReportCategoryCodes();
            if (CollectionUtil.isEmpty(queryReportCategoryCodes)) {
                throw new MithrasException("无查看报表的权限");
            } else {
                conditionQuery.in(AssociationReport::getReportCategoryCode, queryReportCategoryCodes);//报表实例周期类型
            }
            if ("reported".equals(req.getRefereePage())) {
                conditionQuery.orderByDesc(AssociationReport::getReportTime);//按照上报时间倒序
            } else {
                conditionQuery.orderByDesc(AssociationReport::getCreateTime);//创建时间倒序
            }
        }

        Page<AssociationReport> pageResult = this.page(pageQuery, conditionQuery);
        if (CollectionUtil.isEmpty(pageResult.getRecords())) {
            return PageR.empty(req.getPage(), req.getPageSize());
        }
        List<AssociationReportListRSP> list = BeanUtil.copyToList(pageResult.getRecords(), AssociationReportListRSP.class);
        return PageR.of(list, pageResult.getTotal(), req.getPage(), req.getPageSize());
    }

    @Transactional(rollbackFor = Throwable.class)
    public void deleteById(Long id) {
        AssociationReport associationReport = this.getById(id);
        if (Objects.isNull(associationReport)) {
            throw new MithrasException("报送记录主表数据不存在");
        }
        // 校验状态
        if (Objects.equals(associationReport.getReportStatus(), AssociationReportStatusEnum.SUCCESS.name())) {
            throw new MithrasException("报送成功的数据不允许删除");
        }
        // 金融局报送的待报送，除了流程审批中的，都可以删除
        if (Objects.equals(associationReport.getProcessStatus(), AssociationProcessStatusEnum.UNDER_APPROVAL.name())) {
            throw new MithrasException("流程审批中的数据不允许删除");
        }
        // 删除主表
        this.removeById(id);
        // 删除子表
        AssociationReportCategoryEnum category = AssociationReportCategoryEnum.findByName(associationReport.getReportCategoryName());
        if (! Objects.isNull(category) ) {
            DeleteDataSelector.getInstance(category).deleteByReportInstanceId(associationReport.getReportInstanceId());
        }
        // 实时报表，删除后批次号重新排列
/*        if (Objects.equals(associationReport.getReportPeriodCategory(), AssociationReportPeriodCategoryEnum.REALTIME.name())) {
            LambdaQueryWrapper<AssociationReport> query = Wrappers.lambdaQuery();
            query.eq(AssociationReport::getReportCategoryCode, associationReport.getReportCategoryCode());
            query.eq(AssociationReport::getReportYear, associationReport.getReportYear());
            query.eq(AssociationReport::getReportPeriod, associationReport.getReportPeriod());
            query.orderByAsc(AssociationReport::getCreateTime);
            List<AssociationReport> remainAssociationReports = this.list(query);
            if (! CollectionUtil.isEmpty(remainAssociationReports)) {
                for (int i = 0; i < remainAssociationReports.size(); i++) {
                    AssociationReport updateAssociationReport = remainAssociationReports.get(i);
                    updateAssociationReport.setBatchNo(this.generateBatchNo(i));
                    this.updateById(updateAssociationReport);
                }
            }
        }*/
    }

    public void pushSubmit(List<Long> ids) {
        if (ObjectUtil.isEmpty(ids)) {
            throw new MithrasException("请选择要报送的数据");
        }
        //1.获取所有待报送+报送失败数据
        LambdaQueryWrapper<AssociationReport> queryWrapper = Wrappers.<AssociationReport>lambdaQuery()
                .in(AssociationReport::getId, ids);
        List<AssociationReport> reportList = this.list(queryWrapper);
        if (ObjectUtil.isEmpty(reportList)) {
            throw new MithrasException("暂无可报送数据");
        }
        reportList.forEach(report -> {
            if(report.getReportStatus().equals(AssociationReportStatusEnum.SUCCESS.name())){
                throw new MithrasException("报送状态为待报送或报送失败的数据，才能进行上报");
            }
            if(! report.getProcessStatus().equals(AssociationProcessStatusEnum.APPROVAL_PASS.name())){
                throw new MithrasException("只有审批通过的数据，才能进行上报");
            }
        });
        List<String> errorList = new ArrayList<>();
        for (AssociationReport report : reportList) {
            List<AssociationReportBaseModel> details = getReportList(report.getReportCategoryCode(), report.getReportInstanceId());
            if (ObjectUtil.isEmpty(details)) {
                errorList.add(String.format("%s-%s-%s-%s", report.getReportCategoryName(), report.getReportYear(), AssociationReportPeriodCategoryEnum.getReportCategoryName(AssociationReportPeriodCategoryEnum.findByName(report.getReportPeriodCategory()), report.getReportPeriod()), report.getBatchNo()));
            }
        }
        //检查
        if(!errorList.isEmpty()) {
            throw new AssociationReportException(errorList);
        }
        // 创建流程
        SpringUtil.getBean(AssociationReportService.class).createPushFlow(reportList);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void createPushFlow(List<AssociationReport> reportList) {
        // 创建申请批次
        AssociationReportApply associationReportApply = new AssociationReportApply();
        associationReportApply.setReportInstanceIds(StrUtil.join(",", reportList.stream().map(AssociationReport::getReportInstanceId).collect(Collectors.toList())));
        associationReportApply.setApprovalStatus(AssociationProcessStatusEnum.UNDER_APPROVAL.name());
        associationReportApplyService.save(associationReportApply);
        // 更新上报流程状态
        SpringUtil.getBean(AssociationReportService.class).updatePushProcessStatus(associationReportApply.getId(), AssociationProcessStatusEnum.UNDER_APPROVAL);
        // 创建流程实例
        Long currentUserId = AccountUtil.getLoginInfo().getId();
        OrgDO org = SpringUtil.getBean(SysUserService.class).getUserDept();
        StartProcessReq startProcessReq = new StartProcessReq();
        startProcessReq.setModelKey(ProcessModelTypeEnum.AssociationReportPushFlow.name());
        startProcessReq.setBusinessKey(associationReportApply.getId().toString());
        startProcessReq.setProcessInstanceName(String.format("%s-%s", ProcessModelTypeEnum.AssociationReportPushFlow.getDisplay(), StrUtil.join("/", reportList.stream().sorted(Comparator.comparing(AssociationReport::getReportCategoryCode)).map(AssociationReport::getReportCategoryName).collect(Collectors.toSet()))));
        startProcessReq.setStartUserId(currentUserId.toString());
        if (Objects.nonNull(org)) {
            startProcessReq.setStartUserDeptId(org.getId().toString());
        }
        SpringUtil.getBean(FlowProcessApiService.class).start(startProcessReq);
    }

    public void push(Long applyId) {
        AssociationReportApply associationReportApply = associationReportApplyService.getById(applyId);
        if (Objects.isNull(associationReportApply)) {
            log.error("准备金融局上报推送，但没有找到对应申请批次的数据[{}]", applyId);
            return;
        }
        List<String> reportInstanceIds = ListUtil.toList(associationReportApply.getReportInstanceIds().split(","));
        List<AssociationReport> reportList = this.list(Wrappers.<AssociationReport>lambdaQuery().in(AssociationReport::getReportInstanceId, reportInstanceIds));
        if (CollectionUtil.isEmpty(reportList)) {
            log.error("准备金融局上报推送，但通过申请批次没有找到对应的报表实例[{}]", applyId);
            return;
        }
        List<String> errorList = new ArrayList<>();
        Map<String, List<AssociationReportBaseModel>> reportInstanceId2BeanMap = new HashMap<>();
        for (AssociationReport report : reportList) {
            List<AssociationReportBaseModel> details = getReportList(report.getReportCategoryCode(), report.getReportInstanceId());
            if (ObjectUtil.isEmpty(details)) {
                errorList.add(String.format("%s-%s-%s-%s", report.getReportCategoryName(), report.getReportYear(), AssociationReportPeriodCategoryEnum.getReportCategoryName(AssociationReportPeriodCategoryEnum.findByName(report.getReportPeriodCategory()), report.getReportPeriod()), report.getBatchNo()));
            } else {
                reportInstanceId2BeanMap.put(report.getReportInstanceId(), details);
            }
        }
        if (!errorList.isEmpty()) {
            throw new AssociationReportException(errorList);
        }
        //查询表字段排序
        Map<String, Map<String, Integer>> reportSort = getReportSort();
        //报送
        boolean reportSuccessFlag = true;
        for (AssociationReport report : reportList) {
            String reportFileDirectory = getReportFileDirectory(report);
            String reportFileName = getReportFileName(report);
            List rsps = reportInstanceId2BeanMap.get(report.getReportInstanceId());
            try {
                associationFileHandleReportService.createAndTransferCsv(reportFileName, rsps, reportFileDirectory, reportSort.get(report.getReportCategoryCode()));
                report.setReportTime(LocalDateTime.now());
                report.setReportStatus(AssociationReportStatusEnum.SUCCESS.name());
            } catch (Exception e) {
                log.error("租赁协会上报文件失败", e);
                report.setReportTime(LocalDateTime.now());
                report.setReportStatus(AssociationReportStatusEnum.FAILURE.name());
                reportSuccessFlag = false;

            }
        }
        //更新报送结果
        SpringContextHolder.getBean(AssociationReportService.class).updateBatchById(reportList);
        if (! reportSuccessFlag) {
            throw new MithrasException("租赁协会上报文件失败");
        }
    }

    @Transactional(rollbackFor = Throwable.class)
    public void updatePushProcessStatus(Long applyId, AssociationProcessStatusEnum status) {
        AssociationReportApply associationReportApply = associationReportApplyService.getById(applyId);
        List<AssociationReport> reportList = this.list(Wrappers.<AssociationReport>lambdaQuery().in(AssociationReport::getReportInstanceId, ListUtil.toList(associationReportApply.getReportInstanceIds().split(","))));
        List<AssociationReport> updateList = reportList.stream().map(e -> {
            AssociationReport update = new AssociationReport();
            update.setId(e.getId());
            update.setPushProcessStatus(status.name());
            return update;
        }).collect(Collectors.toList());
        this.updateBatchById(updateList);
    }

    public Map<String, Map<String, Integer>> getReportSort() {
        List<AssociationReportSort> associationReportSorts = associationReportSortMapper.selectList(Wrappers.<AssociationReportSort>lambdaQuery());
        if (ObjectUtil.isEmpty(associationReportSorts)) {
            return MapUtil.empty();
        }
        Map<String, List<AssociationReportSort>> collect = associationReportSorts.stream().collect(Collectors.groupingBy(AssociationReportSort::getReportCategoryCode));
        Map<String, Map<String, Integer>> resMap = new HashMap<>();
        collect.forEach((report, sortList) -> {
            if (CollectionUtil.isNotEmpty(sortList)) {
                resMap.put(report, sortList.stream().collect(Collectors.toMap(AssociationReportSort::getFieldName, AssociationReportSort::getSort)));
            }
        });
        return resMap;
    }

    public List<AssociationReportBaseModel> getReportList(String reportCategoryCode, String reportInstanceId) {
        AssociationReportCategoryEnum categoryEnum = Optional.ofNullable(AssociationReportCategoryEnum.findByName(reportCategoryCode)).orElseThrow(() -> new MithrasException("暂不支持此类型报表"));
        List<AssociationReportBaseModel> rsps = new ArrayList();
        switch (categoryEnum) {
            case J0001: {
                //"融资租赁公司基本情况统计表.xlsx";
                rsps.addAll(SpringContextHolder.getBean(AssociationBasicSituationService.class).listModelByReportInstanceId(reportInstanceId));
                break;
            }
            case J0002: {
                //"股东股权信息一览表-股东股权信息.xlsx";
                rsps.addAll(SpringContextHolder.getBean(AssociationShahStorInfoService.class).listModelByReportInstanceId(reportInstanceId));
                break;
            }
            case J0003: {
                //"股东股权信息一览表-股东变更记录.xlsx";
                rsps.addAll(SpringContextHolder.getBean(AssociationShahChangeInfoService.class).listModelByReportInstanceId(reportInstanceId));
                break;
            }
            case J0004: {
                //"高管信息一览表.xlsx";
                rsps.addAll(SpringContextHolder.getBean(AssociationSeniorExecutiveInfoService.class).listModelByReportInstanceId(reportInstanceId));
                break;
            }
            case J0005: {
                //"融资租赁公司业务情况表.xlsx";
                rsps.addAll(SpringContextHolder.getBean(AssociationBusinessSituationService.class).listModelByReportInstanceId(reportInstanceId));
                break;
            }
            case J0006: {
                //"融资租赁公司服务实体经济情况.xlsx";
                rsps.addAll(SpringContextHolder.getBean(AssociationEntityEconomyServiceService.class).listModelByReportInstanceId(reportInstanceId));
                break;
            }
            case J0007: {
                //"融资租赁公司资产负债表.xlsx";
                rsps.addAll(SpringContextHolder.getBean(AssociationBalanceSheetPartialService.class).listModelByReportInstanceId(reportInstanceId));
                break;
            }
            case J0008: {
                // "融资租赁公司利润表.xlsx";
                rsps.addAll(SpringContextHolder.getBean(AssociationCompanyProfitStatementService.class).listModelByReportInstanceId(reportInstanceId));
                break;
            }
            case J0009: {
                //"融资租赁公司主要业务清单.xlsx";
                rsps.addAll(SpringContextHolder.getBean(AssociationMainBusinessService.class).listModelByReportInstanceId(reportInstanceId));
                break;
            }
            case J0010: {
                //"融资租赁公司对外融资清单（季报）.xlsx";
                rsps.addAll(SpringContextHolder.getBean(AssociationExternalFinancingService.class).listModelByReportInstanceId(reportInstanceId));
                break;
            }
            case J0011: {
                // "融资租赁公司最大十家客户（含集团）集中度统计表.xlsx";
                rsps.addAll(SpringContextHolder.getBean(AssociationTop10ClientConcentrationService.class).listModelByReportInstanceId(reportInstanceId));
                break;
            }
            case J0012: {
                //"融资租赁公司关联方信息汇总表.xlsx";
                rsps.addAll(SpringContextHolder.getBean(AssociationRelationService.class).listModelByReportInstanceId(reportInstanceId));
                break;
            }
            case J0013: {
                //"涉法涉讼涉访信息表.xlsx";
                rsps.addAll(SpringContextHolder.getBean(AssociationLawInvolvedVisitRelatedInfoService.class).listModelByReportInstanceId(reportInstanceId));
                break;
            }
            case J0014: {
                //"重大事项报告表-基本信息.xlsx";
                rsps.addAll(SpringContextHolder.getBean(AssociationMajorMattersBasicReportService.class).listModelByReportInstanceId(reportInstanceId));
                break;
            }
            case J0015: {
                //"重大事项报告表-重大事项报告情况.xlsx";
                rsps.addAll(SpringContextHolder.getBean(AssociationMajorMattersEventReportService.class).listModelByReportInstanceId(reportInstanceId));
                break;
            }
            default: {
                throw new MithrasException("暂不支持的报表类型");
            }
        }
        /*if (ObjectUtil.isNotEmpty(rsps)) {
            rsps.forEach(e -> {
                e.setReportTime(LocalDateTime.now());
                e.setVersion(e.getReportInstancePeriod() + e.getBatchNo());
            });
        }*/
        return rsps;
    }

    //获取文件目录
    public String getReportFileDirectory(AssociationReport associationReport) {
        AssociationReportPeriodCategoryEnum categoryEnum = Optional.ofNullable(AssociationReportPeriodCategoryEnum.findByName(associationReport.getReportPeriodCategory())).orElseThrow(() -> new MithrasException("暂不支持此周期"));
        //return String.format("u%s/send/%s/%s", zlAccount, associationReport.getReportCategoryCode(), AssociationReportPeriodCategoryEnum.getReportCategoryDate(categoryEnum, associationReport.getReportYear(), associationReport.getReportPeriod()));
        return String.format("send/%s/%s", associationReport.getReportCategoryCode(), AssociationReportPeriodCategoryEnum.getReportCategoryDate(categoryEnum, associationReport.getReportYear(), associationReport.getReportPeriod(),realtimeMinusMonths));
    }

    //获取文件名称
    public String getReportFileName(AssociationReport associationReport) {
        //报送类型
        AssociationReportCategoryEnum reportCategoryEnum = Optional.ofNullable(AssociationReportCategoryEnum.findByName(associationReport.getReportCategoryCode())).orElseThrow(() -> new MithrasException("暂不支持此类型"));
        //周期
        AssociationReportPeriodCategoryEnum categoryEnum = Optional.ofNullable(AssociationReportPeriodCategoryEnum.findByName(associationReport.getReportPeriodCategory())).orElseThrow(() -> new MithrasException("暂不支持此周期"));
        return String.format("%s_%s_%s_%s_%s_%s", GlobalConstants.ASSOCIATION_FLC,
                associationReport.getReportCategoryCode(),
                reportCategoryEnum.getVersion(),
                AssociationReportPeriodCategoryEnum.getReportCategoryDate(categoryEnum, associationReport.getReportYear(), associationReport.getReportPeriod(),realtimeMinusMonths),
                associationReport.getBatchNo(),
                AssociationReportPeriodCategoryEnum.getReportCategoryIncrementSuffix(reportCategoryEnum)) + GlobalConstants.OFFICE_CSV_SUFFIX;
    }

    private String generateBatchNo(int count) {
        return String.format("%04d", count);
    }

    //根据报表实例唯一标识获取报表列表
    public List<AssociationReport> getReportList(List<String> reportInstanceIdList) {
        LambdaQueryWrapper<AssociationReport> conditionQuery = Wrappers.lambdaQuery();
        if(reportInstanceIdList!= null && reportInstanceIdList.size()>0){
            conditionQuery.in(AssociationReport::getReportInstanceId, reportInstanceIdList);//报表实例周期类型
        }
        return this.list(conditionQuery);
    }

    public List<AssociationReport> getReportList(AssociationReportApplyREQ req) {
        LambdaQueryWrapper<AssociationReport> conditionQuery = Wrappers.lambdaQuery();
        if(req.getReportPeriodCategoryList()!= null && req.getReportPeriodCategoryList().size()>0){
            conditionQuery.in(AssociationReport::getReportPeriodCategory, req.getReportPeriodCategoryList());//报表实例周期类型
        }
        if(req.getReportCategoryCodeList()!=null && req.getReportCategoryCodeList().size()>0){
            conditionQuery.in(AssociationReport::getReportCategoryCode, req.getReportCategoryCodeList());//报表类型code
        }
        if(req.getReportInstanceIdList()!=null && req.getReportInstanceIdList().size()>0){
            conditionQuery.in(AssociationReport::getReportInstanceId, req.getReportInstanceIdList());//报表实例id
        }
        if(StringUtils.isNotBlank(req.getReportPeriodCategory())){//报表类型
            conditionQuery.eq(AssociationReport::getReportPeriodCategory, req.getReportPeriodCategory());//报表实例周期类型
        }
        if(req.getReportYear()!=null){//报表实例年份
            conditionQuery.eq(AssociationReport::getReportYear, req.getReportYear());
        }
        if(req.getReportPeriod()!=null){//报表实例周期
            conditionQuery.eq(AssociationReport::getReportPeriod, req.getReportPeriod());
        }
        conditionQuery.orderByDesc(AssociationReport::getCreateTime);//创建时间倒序
        return this.list(conditionQuery);
    }


    /**
     * 流程结束 处理流程状态
     *
     * @param reportApplyId
     * @param endType
     */
    @Transactional(rollbackFor = Throwable.class)
    @Override
    public void processEnd(Long reportApplyId, Integer endType, Long startUserId, String processInstanceId, String modelKey) {
        AssociationReportApply associationReportApply = associationReportApplyService.getById(reportApplyId);
        if (ObjectUtil.isNull(associationReportApply)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        String processStatus="";
        ProcessBusinessStatusEnum processBusinessStatusEnum = ProcessBusinessStatusEnum.getByType(endType);
        switch (processBusinessStatusEnum) {
            case PASS:
                processStatus = AssociationProcessStatusEnum.APPROVAL_PASS.name();
                break;
            case PASS_ALL:
                processStatus = AssociationProcessStatusEnum.APPROVAL_PASS.name();
                break;
            case REJECT:
                processStatus = AssociationProcessStatusEnum.APPROVAL_REJECT.name();
                break;
            case REJECT_ALL:
                processStatus = AssociationProcessStatusEnum.APPROVAL_REJECT.name();
                break;
            case CANCEL:
                processStatus = AssociationProcessStatusEnum.CANCEL.name();
                break;
            default:
                break;
        }
        //更新主表流程状态
        associationReportApply.setApprovalStatus(processStatus);
        associationReportApplyService.updateById(associationReportApply);
        String reportInstanceIds  = associationReportApply.getReportInstanceIds();
        List<String> reportInstanceIdList = Arrays.asList(reportInstanceIds.split(","));
        List<AssociationReport> associationReportList = this.getReportList(reportInstanceIdList);
        for(AssociationReport item:associationReportList){
            item.setProcessStatus(processStatus);
        }
        //更新子表流程状态
        this.updateBatchById(associationReportList);
        // 记录版本
        boolean processPass = ProcessBusinessStatusEnum.success(endType);
        int versionType = processPass ? VersionTypeConstants.NORMAL : VersionTypeConstants.INVALID;
        associationReportVersionServiceService.recordVersion(reportApplyId, VersionTypeEnum.APPROVAL, startUserId, processInstanceId, versionType);
        if (processBusinessStatusEnum == ProcessBusinessStatusEnum.PASS || processBusinessStatusEnum == ProcessBusinessStatusEnum.PASS_ALL) {
            // 审批通过抄送给综合部经办
            try {
                List<Long> userIds = SpringUtil.getBean(SysUserService.class).queryJobUserIds(JobEnum.comprehensiveDept.name());
                if (CollectionUtil.isNotEmpty(userIds)) {
                    ExecutionProcessBaseREQ ccReq = new ExecutionProcessBaseREQ();
                    ccReq.setProcessInstanceId(processInstanceId);
                    ccReq.setCcUserIdList(userIds);
                    SpringUtil.getBean(ExecutionService.class).cc(ccReq);
                }
            } catch (Exception e) {
                log.info("金融局报送数据审批流程[{}]结束，抄送综合部经办发生未知异常", processInstanceId, e);
            }
        }
    }

    public void recalculate(Long applyId) {
        AssociationReportApply associationReportApply = associationReportApplyService.getById(applyId);
        if (Objects.isNull(associationReportApply)) {
            throw new MithrasException("批次数据不存在");
        }
        List<String> reportInstanceIds = StrUtil.split(associationReportApply.getReportInstanceIds(), ",");
        if (CollectionUtil.isEmpty(reportInstanceIds)) {
            throw new MithrasException("批次报表实例id不存在");
        }
        List<AssociationReport> associationReportList = this.list(Wrappers.<AssociationReport>lambdaQuery().in(AssociationReport::getReportInstanceId, reportInstanceIds));
        if (CollectionUtil.isEmpty(associationReportList)) {
            throw new MithrasException("目标报表数据不存在");
        }
        for (AssociationReport associationReport : associationReportList) {
            if (StrUtil.equals(associationReport.getDataSource(), DataSource.MANUAL.getDisplay())) {
                continue;
            }
            if (Objects.equals(YesOrNoNumberEnum.YES.getCode(), associationReport.getIsShow())) {
                continue;
            }
            if (Objects.equals(AssociationReportStatusEnum.SUCCESS.name(), associationReport.getReportStatus())) {
                continue;
            }
            if (Objects.equals(AssociationProcessStatusEnum.UNDER_APPROVAL.name(), associationReport.getProcessStatus())) {
                continue;
            }
            StoreDataSelector.getInstance(associationReport.getReportCategoryCode()).storeFromSystemJob(associationReport.getReportInstanceId());
        }
    }
}
