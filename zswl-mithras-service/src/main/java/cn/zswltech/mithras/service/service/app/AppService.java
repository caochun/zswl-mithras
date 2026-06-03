package cn.zswltech.mithras.service.service.app;
import cn.zswltech.mithras.customer.domain.enums.app.VisitPhaseStatus;
import cn.zswltech.mithras.customer.domain.enums.app.VisitRecordStatus;
import cn.zswltech.mithras.customer.domain.enums.app.VisitDownloadTaskStatusEnum;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.gruul.biz.service.SystemConfigService;
import cn.zswltech.gruul.common.constant.OrgConstants;
import cn.zswltech.gruul.common.result.Response;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.gruul.dao.dal.dao.UserOrgJobDOMapper;
import cn.zswltech.gruul.dao.dal.entity.OrgDO;
import cn.zswltech.gruul.dao.dal.entity.UserOrgJobDO;
import cn.zswltech.gruul.dao.dal.vo.AccountVO;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.app.*;
import cn.zswltech.mithras.dto.client.client.ClientCorpAddREQ;
import cn.zswltech.mithras.dto.client.commerceinfo.CorpCommerceInfoAddREQ;
import cn.zswltech.mithras.dto.contract.baseinfo.ContractBaseInfoDetailREQ;
import cn.zswltech.mithras.dto.contract.baseinfo.ContractBaseInfoDetailRSP;
import cn.zswltech.mithras.dto.contract.rent.ContractRentActualListRSP;
import cn.zswltech.mithras.dto.dashboard.ValueUnitDTO;
import cn.zswltech.mithras.dto.file.FileDownLoadRSP;
import cn.zswltech.mithras.dto.groupcreditestablish.GroupCreditEstablishListRSP;
import cn.zswltech.mithras.dto.groupcreditreview.GroupCreditReviewListRSP;
import cn.zswltech.mithras.dto.projestablish.baseinfo.ProjEstablishBaseInfoListRSP;
import cn.zswltech.mithras.dto.projreview.baseinfo.ProjReviewBaseInfoListRSP;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.service.controller.client.ClientController;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.enums.CashFlowItemEnum;
import cn.zswltech.mithras.service.enums.JobEnum;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.service.enums.app.*;
import cn.zswltech.mithras.customer.domain.enums.client.DomesticOrAbroad;
import cn.zswltech.mithras.service.enums.common.ProcessStatus;
import cn.zswltech.mithras.contract.enums.contract.ContractStatus;
import cn.zswltech.mithras.contract.enums.contract.ContractTypeEnum;
import cn.zswltech.mithras.payment.domain.enums.app.AppContractSubTypeEnum;
import cn.zswltech.mithras.payment.domain.enums.app.AppPaymentStatus;
import cn.zswltech.mithras.payment.domain.enums.LendingMaterialType;
import cn.zswltech.mithras.payment.domain.enums.WriteOffStatus;
import cn.zswltech.mithras.service.mapper.MaterialsListMapper;
import cn.zswltech.mithras.afterlease.infrastructure.persistence.mapper.NewAfterLeaseCheckPlanBaseMapper;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.app.AppContractSignMapper;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.app.VisitDownloadTaskRecordMapper;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.app.VisitRecordMapper;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.client.ClientMapper;
import cn.zswltech.mithras.service.mapper.collection.CollectionBaseInfoMapper;
import cn.zswltech.mithras.contract.mapper.contract.ContractBaseInfoMapper;
import cn.zswltech.mithras.contract.mapper.contract.ContractSignInfoMapper;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.dto.VisitRecordListParam;
import cn.zswltech.mithras.credit.infrastructure.persistence.groupcredit.establish.mapper.GroupCreditEstablishBaseInfoMapper;
import cn.zswltech.mithras.credit.infrastructure.persistence.groupcredit.review.mapper.GroupCreditReviewBaseInfoMapper;
import cn.zswltech.mithras.service.mapper.model.MaterialsList;
import cn.zswltech.mithras.afterlease.infrastructure.persistence.mapper.model.NewAfterLeaseCheckPlanBase;
import cn.zswltech.mithras.afterlease.infrastructure.persistence.mapper.model.NewAfterLeaseCheckPlanClient;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.app.AppContractSign;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.app.VisitDownloadTaskRecord;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.app.VisitRecord;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.*;
import cn.zswltech.mithras.service.mapper.model.collection.CollectionBaseInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractSignInfo;
import cn.zswltech.mithras.credit.infrastructure.persistence.groupcredit.establish.model.GroupCreditEstablishBaseInfo;
import cn.zswltech.mithras.credit.infrastructure.persistence.groupcredit.review.model.GroupCreditReviewBaseInfo;
import cn.zswltech.mithras.service.mapper.model.leaseholdproperty.LeaseItemInfo;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.model.PaymentActualDetail;
import cn.zswltech.mithras.service.mapper.model.projestablish.ProjEstablishBaseInfo;
import cn.zswltech.mithras.service.mapper.model.projestablish.ProjEstablishLeasePrice;
import cn.zswltech.mithras.service.mapper.model.projreview.ProjReviewBaseInfo;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.PaymentActualDetailMapper;
import cn.zswltech.mithras.service.mapper.projestablish.ProjEstablishBaseInfoMapper;
import cn.zswltech.mithras.service.mapper.projreview.ProjReviewBaseInfoMapper;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.Id2NameService;
import cn.zswltech.mithras.service.service.SysUserService;
import cn.zswltech.mithras.service.service.afterlese.AfterLeaseCheckPlanClientService;
import cn.zswltech.mithras.service.service.contract.ContractBaseInfoService;
import cn.zswltech.mithras.service.service.contract.ContractSignInfoService;
import cn.zswltech.mithras.service.service.leaseholdproperty.LeaseItemInfoService;
import cn.zswltech.mithras.service.service.materialsfile.MaterialsListService;
import cn.zswltech.mithras.third.tianyancha.application.TycService;
import cn.zswltech.mithras.third.tianyancha.application.dto.MithrasCompanyInfo;
import cn.zswltech.mithras.third.tianyancha.application.dto.TycQueryCompanyReq;
import cn.zswltech.mithras.service.util.LongUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static cn.hutool.core.bean.BeanUtil.copyProperties;
import static cn.hutool.core.util.ObjectUtil.*;

/**
 * @author junke
 */
@Slf4j
@Service
public class AppService extends ServiceImpl<VisitRecordMapper, VisitRecord> {

    @Resource
    private VisitRecordMapper visitRecordMapper;
    @Resource
    private MaterialsListService materialsListService;
    @Resource
    private SysUserService sysUserService;
    @Resource
    private Id2NameService id2NameService;
    @Resource
    private ContractBaseInfoMapper contractBaseInfoMapper;
    @Resource
    private PaymentActualDetailMapper actualDetailMapper;
    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private ClientMapper clientMapper;
    @Resource
    private CollectionBaseInfoMapper baseInfoMapper;
    @Resource
    private AppContractSignMapper contractSignMapper;
    @Resource
    private ContractSignInfoMapper contractSignInfoMapper;
    @Resource
    private ProjEstablishBaseInfoMapper projEstablishBaseInfoMapper;
    @Resource
    private ProjReviewBaseInfoMapper projReviewBaseInfoMapper;
    @Resource
    private GroupCreditEstablishBaseInfoMapper groupCreditEstablishBaseInfoMapper;
    @Resource
    private GroupCreditReviewBaseInfoMapper groupCreditReviewBaseInfoMapper;
    @Resource
    private NewAfterLeaseCheckPlanBaseMapper leaseCheckPlanBaseMapper;
    @Resource
    private MaterialsListMapper materialsListMapper;
    @Resource
    private ContractSignInfoService contractSignInfoService;
    @Resource
    private LeaseItemInfoService leaseItemInfoService;
    @Resource
    private SystemConfigService systemConfigService;

    public List<AppContractQueryRSP> listContract(AppContractQueryREQ req) {
        LambdaQueryWrapper<ContractBaseInfo> query = Wrappers.lambdaQuery();
        query.eq(ContractBaseInfo::getProjCode, req.getProjCode());
        query.in(ContractBaseInfo::getContractStatus, ListUtil.toList(ContractStatus.TAKE_EFFECT.name(), ContractStatus.START_RENT.name()));
        query.orderByAsc(ContractBaseInfo::getContractCode);
        List<ContractBaseInfo> dbList = SpringUtil.getBean(ContractBaseInfoService.class).list(query);
        if (CollectionUtil.isEmpty(dbList)) {
            return Collections.emptyList();
        }
        return dbList.stream().map(e -> {
            AppContractQueryRSP rsp = new AppContractQueryRSP();
            rsp.setContractId(e.getId());
            rsp.setContractCode(e.getContractCode());
            return rsp;
        }).collect(Collectors.toList());
    }

    public String batchDownload(AppVisitFileBatchDownloadREQ req) {
        // 查询是否存在执行中的任务
        int taskDoingCount = SpringUtil.getBean(VisitDownloadTaskRecordMapper.class).selectCount(Wrappers.<VisitDownloadTaskRecord>lambdaQuery().eq(VisitDownloadTaskRecord::getTaskStatus, VisitDownloadTaskStatusEnum.DOING.name()));
        if (taskDoingCount > 0) {
            throw new MithrasException("当前存在正在执行中的导出任务，请稍后再试");
        }
        List<Long> visitRecordIds = this.listTargetRecordIds(req);
        if (CollectionUtil.isEmpty(visitRecordIds)) {
            throw new MithrasException("没有可下载的文件");
        }
        // 先创建任务
        String fileName = "拜访照片_" + LocalDateTimeUtil.format(LocalDateTime.now(), DatePattern.PURE_DATETIME_PATTERN) + ".zip";
        String remoteFilePath = "/TMP/VISIT_DOWNLOAD_TASK/" + LocalDateTimeUtil.format(LocalDate.now(), DatePattern.SIMPLE_MONTH_PATTERN) + "/" + fileName;
        // 生成任务记录
        VisitDownloadTaskRecord taskRecord = new VisitDownloadTaskRecord();
        taskRecord.setUserId(AccountUtil.getLoginInfo().getId());
        taskRecord.setReqParams(JSONUtil.toJsonStr(visitRecordIds));
        taskRecord.setTaskStatus(VisitDownloadTaskStatusEnum.DOING.name());
        taskRecord.setFilePath(remoteFilePath);
        taskRecord.setFileName(fileName);
        SpringUtil.getBean(VisitDownloadTaskRecordMapper.class).insert(taskRecord);
        try {
            VisitDownloadTask visitDownloadTask = new VisitDownloadTask(taskRecord.getId());
            // 需求方不允许异步，这里直接调用run方法，后续如果要异步则改成多线程
            visitDownloadTask.run();
        } catch (Exception e) {
            // 更新任务状态
            taskRecord.setTaskStatus(VisitDownloadTaskStatusEnum.FAILURE.name());
            taskRecord.setRemark(e.getMessage());
            taskRecord.setFinishTime(LocalDateTime.now());
            SpringUtil.getBean(VisitDownloadTaskRecordMapper.class).updateById(taskRecord);
        } finally {
            // 更新任务状态
            taskRecord.setTaskStatus(VisitDownloadTaskStatusEnum.SUCCESS.name());
            taskRecord.setFinishTime(LocalDateTime.now());
            SpringUtil.getBean(VisitDownloadTaskRecordMapper.class).updateById(taskRecord);
        }
        return SpringUtil.getBean(MaterialsListService.class).getPreviewUrl(taskRecord.getFilePath(), 3600);
    }

    private List<Long> listTargetRecordIds(AppVisitFileBatchDownloadREQ req) {
        int limit = 100;
        Response<Object> r = systemConfigService.getConfigValue("visit_file_download_limit");
        if (r.isSuccess() && Objects.nonNull(r.getData())) {
            limit = Integer.parseInt(r.getData().toString());
        }
        List<Long> visitRecordIds;
        if (CollectionUtil.isNotEmpty(req.getVisitRecordIds())) {
            // 如果指定了拜访记录则只取指定的
            if (req.getVisitRecordIds().size() > limit) {
                throw new MithrasException(String.format("只允许一次性导出最多%s条拜访记录的文件", limit));
            }
            visitRecordIds = req.getVisitRecordIds();
        } else {
            // 如果没有指定拜访记录则和列表保持一致
            PageR<AppPCVisitRecordRSP> pageResult = this.pcList(req);
            if (pageResult.getTotal() > limit) {
                throw new MithrasException(String.format("只允许一次性导出最多%s条拜访记录的文件", limit));
            }
            // 用总数量再查询一次
            req.setPageSize((int) pageResult.getTotal());
            pageResult = this.pcList(req);
            visitRecordIds = pageResult.getList().stream().map(AppPCVisitRecordRSP::getId).collect(Collectors.toList());
        }
        return visitRecordIds;
    }

    public void checkInUpload(AppCheckInREQ req) {
        if (Objects.isNull(req.getClientId())) {
            // 先创建客户
            TycQueryCompanyReq tycReq = new TycQueryCompanyReq();
            tycReq.setCompanyName(req.getClientName());
            tycReq.setPage(1);
            tycReq.setPageSize(1);
            List<MithrasCompanyInfo> baseInfoList = SpringUtil.getBean(TycService.class).queryByCompanyName(tycReq);
            if (CollectionUtil.isEmpty(baseInfoList)) {
                throw new MithrasException(String.format("没有找到<%s>的客户信息", req.getClientName()));
            }
            MithrasCompanyInfo companyInfo = baseInfoList.get(0);
            ClientCorpAddREQ addREQ = new ClientCorpAddREQ();
            addREQ.setClientName(companyInfo.getCompanyName());
            addREQ.setUscCode(companyInfo.getCreditCode());
            addREQ.setDomesticOrAbroad(DomesticOrAbroad.DOMESTIC.name());
            // FIXME 不合理，为了复用，service调用了controller，应该调整代码逻辑下沉
            R<CorpCommerceInfoAddREQ> r = SpringUtil.getBean(ClientController.class).addCorporation(addREQ);
            if (!r.isSuccess()) {
                log.error("APP端创建客户失败[{}]", r.getMsg());
                throw new MithrasException("创建客户失败");
            }
            req.setClientId(r.getData().getClientId());
        }
        VisitRecord visitRecord = copyProperties(req, VisitRecord.class);
        if (Objects.nonNull(req.getContractId())) {
            ContractBaseInfo contractBaseInfo = contractBaseInfoMapper.selectById(req.getContractId());
            visitRecord.setContractCode(Optional.ofNullable(contractBaseInfo).map(ContractBaseInfo::getContractCode).orElse(null));
        }
        Long userId = Optional.ofNullable(AccountUtil.getLoginInfo())
                .map(AccountVO::getId)
                .orElseThrow(() -> new MithrasException(ResultMsg.USER_NOT_LOGIN));
        visitRecord.setUserId(userId);
        Long deptId = 0L;
        List<OrgDO> deptList = sysUserService.getSpecificUserDeptList(userId);
        Optional<OrgDO> first = deptList.stream().filter(e -> OrgConstants.BUSINESS_DEPT == e.getType()).findFirst();
        if (first.isPresent()) {
            //首选业务部门
            deptId = first.get().getId();
        } else if (!deptList.isEmpty()) {
            deptId = deptList.get(0).getId();
        } else {
            throw new MithrasException("用户部门不存在");
        }
        visitRecord.setDeptId(deptId);
        visitRecord.setStatus(VisitRecordStatus.PASSED.name());
        visitRecordMapper.insert(visitRecord);

        // XMX-36 现场检查时间同步到租后报告-检查时间
        if (VisitPhaseStatus.AFTER_LEASE_CHECK_PLAN.name().equalsIgnoreCase(req.getVisitPhase())) {
            SpringUtil.getBean(AfterLeaseCheckPlanClientService.class).update((Wrappers.<NewAfterLeaseCheckPlanClient>lambdaUpdate()
                    .set(NewAfterLeaseCheckPlanClient::getCheckTime, visitRecord.getCheckInDate().toLocalDate()))
                    .eq(NewAfterLeaseCheckPlanClient::getClientId, visitRecord.getClientId())
                    .eq(NewAfterLeaseCheckPlanClient::getPlanId, visitRecord.getCheckPlanId()));
        }

        String fileName = null;
        String materialsType = null;
        if (VisitPhaseStatus.MARKETING_VISIT.name().equalsIgnoreCase(req.getVisitPhase())) {
            fileName = VisitPhaseStatus.MARKETING_VISIT.display();
            materialsType = VisitPhaseStatus.MARKETING_VISIT.name();
        } else if (VisitPhaseStatus.AFTER_LEASE_CHECK_PLAN.name().equalsIgnoreCase(req.getVisitPhase())) {
            fileName = VisitPhaseStatus.AFTER_LEASE_CHECK_PLAN.display();
            materialsType = VisitPhaseStatus.AFTER_LEASE_CHECK_PLAN.name();
        } else if (VisitPhaseStatus.ON_SITE_DUE_DILIGENCE.name().equalsIgnoreCase(req.getVisitPhase())) {
            fileName = VisitPhaseStatus.ON_SITE_DUE_DILIGENCE.display();
            materialsType = VisitPhaseStatus.ON_SITE_DUE_DILIGENCE.name();
        }
        try {
            String count = "01";
            for (MultipartFile file : req.getFiles()) {
                Long fileId = materialsListService.add(file.getInputStream(), fileName + "-" + count + ".png",
                        visitRecord.getId(), materialsType, "VISIT_RECORD");
                count = generateVersion(count);
            }
        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            log.error("上传APP图片发生未知异常[{}]", visitRecord.getId(), e);
            throw new MithrasException("上传APP图片发生未知异常发生未知异常");
        }
    }

    public PageR<AppVisitRecordRSP> list(AppVisitRecordREQ req) {
        LocalDateTime from = isNull(req.getVisitTimeFrom()) ? null : req.getVisitTimeFrom().atStartOfDay();
        LocalDateTime to = isNull(req.getVisitTimeTo()) ? null : req.getVisitTimeTo().plusDays(1).atStartOfDay();
        VisitRecordListParam listParam = new VisitRecordListParam()
                .setClientName(req.getClientName())
                .setVisitTimeFrom(from)
                .setVisitTimeTo(to)
                .setVisitWays(req.getVisitWays())
                .setVisitPhases(req.getVisitPhases())
                .setVisitTypes(req.getVisitTypes())
                .setStatuses(req.getStatuses());
        if (Objects.nonNull(req.getClientId())) {
            listParam.setClientIds(ListUtil.toList(req.getClientId()));
        }
        // 需要按照不同登陆角色处理
        Set<Long> targetVisitRecordIds = new HashSet<>();
        targetVisitRecordIds.add(-100L);
        Long currentUserId = AccountUtil.getLoginInfo().getId();
        List<UserOrgJobDO> userOrgJobList = SpringUtil.getBean(UserOrgJobDOMapper.class).selectJobCodeByUserId(Collections.singletonList(currentUserId));
        if (CollectionUtil.isEmpty(userOrgJobList)) {
            return new PageR<>();
        }
        // 过滤出业务部门的岗位
        List<OrgDO> orgList = SpringUtil.getBean(SysUserService.class).listBizDept();
        Set<Long> bizDeptIds = orgList.stream().map(OrgDO::getId).collect(Collectors.toSet());
        userOrgJobList.removeIf(e -> !bizDeptIds.contains(e.getOrgId()));
        Map<String, List<UserOrgJobDO>> userOrgMap = userOrgJobList.stream().collect(Collectors.groupingBy(UserOrgJobDO::getJobCode));
        List<UserOrgJobDO> projmanagerList = userOrgMap.get(JobEnum.projmanager.name());
        List<UserOrgJobDO> businessheadList = userOrgMap.get(JobEnum.businesshead.name());
        List<UserOrgJobDO> leaderinchargeList = userOrgMap.get(JobEnum.leaderincharge.name());
        // 中后台看所有
        if (CollectionUtil.isEmpty(projmanagerList) && CollectionUtil.isEmpty(businessheadList) && CollectionUtil.isEmpty(leaderinchargeList)) {
            Page<VisitRecord> visitRecordPage = visitRecordMapper.myList(new Page<>(req.getPage(), req.getPageSize()), listParam);
            List<VisitRecord> visitRecords = visitRecordPage.getRecords();
            List<AppVisitRecordRSP> list = BeanUtil.copyToList(visitRecords, AppVisitRecordRSP.class);
            list.forEach(e -> {
                e.setCreatedName(id2NameService.sysUserId2NameSingle(e.getUserId()));
            });
            List<AppVisitRecordRSP> res = sortAppFunc(list);
            return PageR.of(res, visitRecordPage.getTotal(),
                    visitRecordPage.getPages(),
                    visitRecordPage.getCurrent(),
                    visitRecordPage.getSize());
        }
        // 项目经理
        if (CollectionUtil.isNotEmpty(projmanagerList)) {
            Set<Long> projmanagerVisitRecordIds = this.findVisitRecordIdsByUserId(currentUserId);
            if (CollectionUtil.isNotEmpty(projmanagerVisitRecordIds)) {
                targetVisitRecordIds.addAll(projmanagerVisitRecordIds);
            }
        }
        // 业务负责人
        if (CollectionUtil.isNotEmpty(businessheadList)) {
            List<Long> deptIds = businessheadList.stream().map(UserOrgJobDO::getOrgId).collect(Collectors.toList());
            Set<Long> businessheadTargetClientIds = this.findVisitRecordIdsByDeptIds(deptIds);
            if (CollectionUtil.isNotEmpty(businessheadTargetClientIds)) {
                targetVisitRecordIds.addAll(businessheadTargetClientIds);
            }
        }
        // 分管领导
        if (CollectionUtil.isNotEmpty(leaderinchargeList)) {
            List<Long> deptIds = leaderinchargeList.stream().map(UserOrgJobDO::getOrgId).collect(Collectors.toList());
            Set<Long> leaderinchargeTargetClientIds = this.findVisitRecordIdsByDeptIds(deptIds);
            if (CollectionUtil.isNotEmpty(leaderinchargeTargetClientIds)) {
                targetVisitRecordIds.addAll(leaderinchargeTargetClientIds);
            }
        }
        // 填充条件
        listParam.setTargetVisitRecordIds(targetVisitRecordIds);
        Page<VisitRecord> visitRecordPage = visitRecordMapper.myList(new Page<>(req.getPage(), req.getPageSize()), listParam);
        List<VisitRecord> visitRecords = visitRecordPage.getRecords();
        List<AppVisitRecordRSP> list = BeanUtil.copyToList(visitRecords, AppVisitRecordRSP.class);
        list.forEach(e -> {
            e.setCreatedName(id2NameService.sysUserId2NameSingle(e.getUserId()));
        });
        List<AppVisitRecordRSP> res = sortAppFunc(list);
        return PageR.of(res, visitRecordPage.getTotal(),
                visitRecordPage.getPages(),
                visitRecordPage.getCurrent(),
                visitRecordPage.getSize());
    }


    public PageR<AppPCVisitRecordRSP> pcList(AppPCVisitRecordREQ req) {
        LocalDateTime from = isNull(req.getVisitTimeFrom()) ? null : req.getVisitTimeFrom().atStartOfDay();
        LocalDateTime to = isNull(req.getVisitTimeTo()) ? null : req.getVisitTimeTo().plusDays(1).atStartOfDay();
        VisitRecordListParam listParam = new VisitRecordListParam()
                .setClientName(req.getClientName())
                .setVisitTimeFrom(from)
                .setVisitTimeTo(to)
                .setStatuses(ListUtil.toList(VisitRecordStatus.PASSED.name()));
        if (Objects.nonNull(req.getUserId())) {
            listParam.setUserIds(ListUtil.toList(req.getUserId()));
        }
        if (StringUtils.isNotBlank(req.getVisitWay())) {
            listParam.setVisitWays(ListUtil.toList(req.getVisitWay()));
        }
        if (StringUtils.isNotBlank(req.getVisitPhase())) {
            listParam.setVisitPhases(ListUtil.toList(req.getVisitPhase()));
        }
        if (StringUtils.isNotBlank(req.getVisitType())) {
            listParam.setVisitTypes(ListUtil.toList(req.getVisitType()));
        }
        // 需要按照不同登陆角色处理
        Set<Long> targetVisitRecordIds = new HashSet<>();
        targetVisitRecordIds.add(-100L);
        Long currentUserId = AccountUtil.getLoginInfo().getId();
        List<UserOrgJobDO> userOrgJobList = SpringUtil.getBean(UserOrgJobDOMapper.class).selectJobCodeByUserId(Collections.singletonList(currentUserId));
        if (CollectionUtil.isEmpty(userOrgJobList)) {
            return new PageR<>();
        }
        // 过滤出业务部门的岗位
        List<OrgDO> orgList = SpringUtil.getBean(SysUserService.class).listBizDept();
        Set<Long> bizDeptIds = orgList.stream().map(OrgDO::getId).collect(Collectors.toSet());
        userOrgJobList.removeIf(e -> !bizDeptIds.contains(e.getOrgId()));
        Map<String, List<UserOrgJobDO>> userOrgMap = userOrgJobList.stream().collect(Collectors.groupingBy(UserOrgJobDO::getJobCode));
        List<UserOrgJobDO> projmanagerList = userOrgMap.get(JobEnum.projmanager.name());
        List<UserOrgJobDO> businessheadList = userOrgMap.get(JobEnum.businesshead.name());
        List<UserOrgJobDO> leaderinchargeList = userOrgMap.get(JobEnum.leaderincharge.name());
        // 中后台看所有
        if (CollectionUtil.isEmpty(projmanagerList) && CollectionUtil.isEmpty(businessheadList) && CollectionUtil.isEmpty(leaderinchargeList)) {
            Page<VisitRecord> visitRecordPage = visitRecordMapper.myList(new Page<>(req.getPage(), req.getPageSize()), listParam);
            List<VisitRecord> visitRecords = visitRecordPage.getRecords();
            List<AppPCVisitRecordRSP> list = BeanUtil.copyToList(visitRecords, AppPCVisitRecordRSP.class);
            List<Long> planIds = new ArrayList<>();
            for (AppPCVisitRecordRSP appPCVisitRecordRSP : list) {
                if (Objects.nonNull(appPCVisitRecordRSP.getCheckPlanId())) {
                    planIds.add(appPCVisitRecordRSP.getCheckPlanId());
                }
            }
            Map<Long, List<NewAfterLeaseCheckPlanBase>> groupByCheckPlan;
            if (!planIds.isEmpty()) {
                List<NewAfterLeaseCheckPlanBase> planBaseList = leaseCheckPlanBaseMapper.selectList(Wrappers.<NewAfterLeaseCheckPlanBase>lambdaQuery()
                        .in(NewAfterLeaseCheckPlanBase::getId, planIds));
                groupByCheckPlan = planBaseList.stream().collect(Collectors.groupingBy(NewAfterLeaseCheckPlanBase::getId));
            } else {
                groupByCheckPlan = new HashMap<>();
            }
            list.forEach(e -> {
                e.setCreatedName(id2NameService.sysUserId2NameSingle(e.getUserId()));
                e.setDeptName(id2NameService.deptId2NameSingle(e.getDeptId()));
                if (groupByCheckPlan.containsKey(e.getCheckPlanId())) {
                    e.setCheckPlanName(groupByCheckPlan.get(e.getCheckPlanId()).get(0).getPlanName());
                }
            });
            List<AppPCVisitRecordRSP> res = sortPCFunc(list);
            return PageR.of(res, visitRecordPage.getTotal(),
                    visitRecordPage.getPages(),
                    visitRecordPage.getCurrent(),
                    visitRecordPage.getSize());
        }
        // 项目经理
        if (CollectionUtil.isNotEmpty(projmanagerList)) {
            Set<Long> projmanagerVisitRecordIds = this.findVisitRecordIdsByUserId(currentUserId);
            if (CollectionUtil.isNotEmpty(projmanagerVisitRecordIds)) {
                targetVisitRecordIds.addAll(projmanagerVisitRecordIds);
            }
        }
        // 业务负责人
        if (CollectionUtil.isNotEmpty(businessheadList)) {
            List<Long> deptIds = businessheadList.stream().map(UserOrgJobDO::getOrgId).collect(Collectors.toList());
            Set<Long> businessheadTargetClientIds = this.findVisitRecordIdsByDeptIds(deptIds);
            if (CollectionUtil.isNotEmpty(businessheadTargetClientIds)) {
                targetVisitRecordIds.addAll(businessheadTargetClientIds);
            }
        }
        // 分管领导
        if (CollectionUtil.isNotEmpty(leaderinchargeList)) {
            List<Long> deptIds = leaderinchargeList.stream().map(UserOrgJobDO::getOrgId).collect(Collectors.toList());
            Set<Long> leaderinchargeTargetClientIds = this.findVisitRecordIdsByDeptIds(deptIds);
            if (CollectionUtil.isNotEmpty(leaderinchargeTargetClientIds)) {
                targetVisitRecordIds.addAll(leaderinchargeTargetClientIds);
            }
        }
        // 填充条件
        listParam.setTargetVisitRecordIds(targetVisitRecordIds);
        Page<VisitRecord> visitRecordPage = visitRecordMapper.myList(new Page<>(req.getPage(), req.getPageSize()), listParam);
        List<VisitRecord> visitRecords = visitRecordPage.getRecords();
        List<AppPCVisitRecordRSP> list = BeanUtil.copyToList(visitRecords, AppPCVisitRecordRSP.class);
        List<Long> planIds = new ArrayList<>();
        for (AppPCVisitRecordRSP appPCVisitRecordRSP : list) {
            if (Objects.nonNull(appPCVisitRecordRSP.getCheckPlanId())) {
                planIds.add(appPCVisitRecordRSP.getCheckPlanId());
            }
        }
        Map<Long, List<NewAfterLeaseCheckPlanBase>> groupByCheckPlan;
        if (!planIds.isEmpty()) {
            List<NewAfterLeaseCheckPlanBase> planBaseList = leaseCheckPlanBaseMapper.selectList(Wrappers.<NewAfterLeaseCheckPlanBase>lambdaQuery()
                    .in(NewAfterLeaseCheckPlanBase::getId, planIds));
            groupByCheckPlan = planBaseList.stream().collect(Collectors.groupingBy(NewAfterLeaseCheckPlanBase::getId));
        } else {
            groupByCheckPlan = new HashMap<>();
        }
        list.forEach(e -> {
            e.setCreatedName(id2NameService.sysUserId2NameSingle(e.getUserId()));
            e.setDeptName(id2NameService.deptId2NameSingle(e.getDeptId()));
            if (groupByCheckPlan.containsKey(e.getCheckPlanId())) {
                e.setCheckPlanName(groupByCheckPlan.get(e.getCheckPlanId()).get(0).getPlanName());
            }
        });
        List<AppPCVisitRecordRSP> res = sortPCFunc(list);
        return PageR.of(res, visitRecordPage.getTotal(),
                visitRecordPage.getPages(),
                visitRecordPage.getCurrent(),
                visitRecordPage.getSize());
    }


    public List<AppPCVisitSummaryRSP> pcSummary(AppPCVisitSummaryREQ req) {
        LocalDateTime from = isNull(req.getVisitTimeFrom()) ? null : req.getVisitTimeFrom().atStartOfDay();
        LocalDateTime to = isNull(req.getVisitTimeTo()) ? null : req.getVisitTimeTo().plusDays(1).atStartOfDay();
        VisitRecordListParam listParam = new VisitRecordListParam()
                .setVisitTimeFrom(from)
                .setVisitTimeTo(to)
                .setStatuses(ListUtil.toList(VisitRecordStatus.PASSED.name()));
        if (StringUtils.isNotBlank(req.getVisitWay())) {
            listParam.setVisitWays(ListUtil.toList(req.getVisitWay()));
        }
        if (StringUtils.isNotBlank(req.getVisitPhase())) {
            listParam.setVisitPhases(ListUtil.toList(req.getVisitPhase()));
        }
        if (StringUtils.isNotBlank(req.getVisitType())) {
            listParam.setVisitTypes(ListUtil.toList(req.getVisitType()));
        }
        // 需要按照不同登陆角色处理
        Set<Long> targetVisitRecordIds = new HashSet<>();
        targetVisitRecordIds.add(-100L);
        Long currentUserId = AccountUtil.getLoginInfo().getId();
        List<UserOrgJobDO> userOrgJobList = SpringUtil.getBean(UserOrgJobDOMapper.class).selectJobCodeByUserId(Collections.singletonList(currentUserId));
        if (CollectionUtil.isEmpty(userOrgJobList)) {
            return new ArrayList<>();
        }
        // 过滤出业务部门的岗位
        List<OrgDO> orgList = SpringUtil.getBean(SysUserService.class).listBizDept();
        Set<Long> bizDeptIds = orgList.stream().map(OrgDO::getId).collect(Collectors.toSet());
        userOrgJobList.removeIf(e -> !bizDeptIds.contains(e.getOrgId()));
        Map<String, List<UserOrgJobDO>> userOrgMap = userOrgJobList.stream().collect(Collectors.groupingBy(UserOrgJobDO::getJobCode));
        List<UserOrgJobDO> projmanagerList = userOrgMap.get(JobEnum.projmanager.name());
        List<UserOrgJobDO> businessheadList = userOrgMap.get(JobEnum.businesshead.name());
        List<UserOrgJobDO> leaderinchargeList = userOrgMap.get(JobEnum.leaderincharge.name());
        // 中后台看所有
        if (CollectionUtil.isEmpty(projmanagerList) && CollectionUtil.isEmpty(businessheadList) && CollectionUtil.isEmpty(leaderinchargeList)) {
            Page<VisitRecord> visitRecordPage = visitRecordMapper.myList(new Page<>(1, Integer.MAX_VALUE), listParam);
            List<VisitRecord> visitRecords = visitRecordPage.getRecords();
            return getPCVisitSummaryList(visitRecords);
        }
        // 项目经理
        if (CollectionUtil.isNotEmpty(projmanagerList)) {
            Set<Long> projmanagerVisitRecordIds = this.findVisitRecordIdsByUserId(currentUserId);
            if (CollectionUtil.isNotEmpty(projmanagerVisitRecordIds)) {
                targetVisitRecordIds.addAll(projmanagerVisitRecordIds);
            }
        }
        // 业务负责人
        if (CollectionUtil.isNotEmpty(businessheadList)) {
            List<Long> deptIds = businessheadList.stream().map(UserOrgJobDO::getOrgId).collect(Collectors.toList());
            Set<Long> businessheadTargetClientIds = this.findVisitRecordIdsByDeptIds(deptIds);
            if (CollectionUtil.isNotEmpty(businessheadTargetClientIds)) {
                targetVisitRecordIds.addAll(businessheadTargetClientIds);
            }
        }
        // 分管领导
        if (CollectionUtil.isNotEmpty(leaderinchargeList)) {
            List<Long> deptIds = leaderinchargeList.stream().map(UserOrgJobDO::getOrgId).collect(Collectors.toList());
            Set<Long> leaderinchargeTargetClientIds = this.findVisitRecordIdsByDeptIds(deptIds);
            if (CollectionUtil.isNotEmpty(leaderinchargeTargetClientIds)) {
                targetVisitRecordIds.addAll(leaderinchargeTargetClientIds);
            }
        }
        // 填充条件
        listParam.setTargetVisitRecordIds(targetVisitRecordIds);
        Page<VisitRecord> visitRecordPage = visitRecordMapper.myList(new Page<>(1, Integer.MAX_VALUE), listParam);
        List<VisitRecord> visitRecords = visitRecordPage.getRecords();
        return getPCVisitSummaryList(visitRecords);
    }


    public List<AppClientFrequentRSP> frequentList(AppClientFrequentREQ req) {
        VisitRecordListParam listParam = new VisitRecordListParam()
                .setStatuses(ListUtil.toList(VisitRecordStatus.PASSED.name()));
        // 需要按照不同登陆角色处理
        Set<Long> targetVisitRecordIds = new HashSet<>();
        targetVisitRecordIds.add(-100L);
        Long currentUserId = AccountUtil.getLoginInfo().getId();
        List<UserOrgJobDO> userOrgJobList = SpringUtil.getBean(UserOrgJobDOMapper.class).selectJobCodeByUserId(Collections.singletonList(currentUserId));
        if (CollectionUtil.isEmpty(userOrgJobList)) {
            return new ArrayList<>();
        }
        // 过滤出业务部门的岗位
        List<OrgDO> orgList = SpringUtil.getBean(SysUserService.class).listBizDept();
        Set<Long> bizDeptIds = orgList.stream().map(OrgDO::getId).collect(Collectors.toSet());
        userOrgJobList.removeIf(e -> !bizDeptIds.contains(e.getOrgId()));
        Map<String, List<UserOrgJobDO>> userOrgMap = userOrgJobList.stream().collect(Collectors.groupingBy(UserOrgJobDO::getJobCode));
        List<UserOrgJobDO> projmanagerList = userOrgMap.get(JobEnum.projmanager.name());
        List<UserOrgJobDO> businessheadList = userOrgMap.get(JobEnum.businesshead.name());
        List<UserOrgJobDO> leaderinchargeList = userOrgMap.get(JobEnum.leaderincharge.name());
        // 中后台看所有
        if (CollectionUtil.isEmpty(projmanagerList) && CollectionUtil.isEmpty(businessheadList) && CollectionUtil.isEmpty(leaderinchargeList)) {
            Page<VisitRecord> visitRecordPage = visitRecordMapper.myList(new Page<>(1, Integer.MAX_VALUE), listParam);
            List<VisitRecord> visitRecords = visitRecordPage.getRecords();
            return getClientFrequentList(visitRecords);
        }
        // 项目经理
        if (CollectionUtil.isNotEmpty(projmanagerList)) {
            Set<Long> projmanagerVisitRecordIds = this.findVisitRecordIdsByUserId(currentUserId);
            if (CollectionUtil.isNotEmpty(projmanagerVisitRecordIds)) {
                targetVisitRecordIds.addAll(projmanagerVisitRecordIds);
            }
        }
        // 业务负责人
        if (CollectionUtil.isNotEmpty(businessheadList)) {
            List<Long> deptIds = businessheadList.stream().map(UserOrgJobDO::getOrgId).collect(Collectors.toList());
            Set<Long> businessheadTargetClientIds = this.findVisitRecordIdsByDeptIds(deptIds);
            if (CollectionUtil.isNotEmpty(businessheadTargetClientIds)) {
                targetVisitRecordIds.addAll(businessheadTargetClientIds);
            }
        }
        // 分管领导
        if (CollectionUtil.isNotEmpty(leaderinchargeList)) {
            List<Long> deptIds = leaderinchargeList.stream().map(UserOrgJobDO::getOrgId).collect(Collectors.toList());
            Set<Long> leaderinchargeTargetClientIds = this.findVisitRecordIdsByDeptIds(deptIds);
            if (CollectionUtil.isNotEmpty(leaderinchargeTargetClientIds)) {
                targetVisitRecordIds.addAll(leaderinchargeTargetClientIds);
            }
        }
        // 填充条件
        listParam.setTargetVisitRecordIds(targetVisitRecordIds);
        Page<VisitRecord> visitRecordPage = visitRecordMapper.myList(new Page<>(1, Integer.MAX_VALUE), listParam);
        List<VisitRecord> visitRecords = visitRecordPage.getRecords();
        return getClientFrequentList(visitRecords);
    }

    private List<AppClientFrequentRSP> getClientFrequentList(List<VisitRecord> visitRecords) {
        Collections.sort(visitRecords, new Comparator<VisitRecord>() {
            @Override
            public int compare(VisitRecord o1, VisitRecord o2) {
                return o2.getCreateTime().compareTo(o1.getCreateTime());
            }
        });
        List<VisitRecord> finalVisitRecordList = new ArrayList<>();
        if (visitRecords.size() <= 10) {
            finalVisitRecordList.addAll(visitRecords);
        } else {
            for (int i = 0; i < 10; i++) {
                finalVisitRecordList.add(visitRecords.get(i));
            }
        }
        Map<Long, List<VisitRecord>> clientMap = new HashMap<>();
        for (VisitRecord visitRecord : finalVisitRecordList) {
            clientMap.putIfAbsent(visitRecord.getClientId(), new ArrayList<>());
            clientMap.get(visitRecord.getClientId()).add(visitRecord);
        }
        List<AppClientFrequentRSP> res = new ArrayList<>();
        List<Map.Entry<Long, List<VisitRecord>>> list = new ArrayList<>(clientMap.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<Long, List<VisitRecord>>>() {
            @Override
            public int compare(Map.Entry<Long, List<VisitRecord>> o1, Map.Entry<Long, List<VisitRecord>> o2) {
                return o2.getValue().size() - o1.getValue().size();
            }
        });
        for (Map.Entry<Long, List<VisitRecord>> entry : list) {
            Long clientId = entry.getKey();
            List<VisitRecord> visitRecordList = entry.getValue();
            AppClientFrequentRSP rsp = new AppClientFrequentRSP();
            rsp.setClientId(clientId);
            if (!visitRecordList.isEmpty()) {
                rsp.setCompanyName(visitRecordList.get(0).getClientName());
            }
            rsp.setVisitCount(visitRecordList.size());
            res.add(rsp);
        }
        return res;
    }


    private List<AppPCVisitSummaryRSP> getPCVisitSummaryList(List<VisitRecord> visitRecords) {
        Map<Long, List<VisitRecord>> deptMap = new HashMap<>();
        for (VisitRecord visitRecord : visitRecords) {
            deptMap.putIfAbsent(visitRecord.getDeptId(), new ArrayList<>());
            deptMap.get(visitRecord.getDeptId()).add(visitRecord);
        }
        List<AppPCVisitSummaryRSP> res = new ArrayList<>();
        for (Map.Entry<Long, List<VisitRecord>> deptEntry : deptMap.entrySet()) {
            Long deptId = deptEntry.getKey();
            List<VisitRecord> deptVisitRecordList = deptEntry.getValue();
            AppPCVisitSummaryRSP rsp = new AppPCVisitSummaryRSP();
            rsp.setDeptId(deptId);
            rsp.setDeptName(id2NameService.deptId2NameSingle(deptId));
            rsp.setVisitCount(new ValueUnitDTO(String.valueOf(deptVisitRecordList.size()), "次数"));
            Map<Long, List<VisitRecord>> userMap = new HashMap<>();
            for (VisitRecord visitRecord : deptVisitRecordList) {
                userMap.putIfAbsent(visitRecord.getUserId(), new ArrayList<>());
                userMap.get(visitRecord.getUserId()).add(visitRecord);
            }
            List<AppPCVisitSummaryRSP.ObjectInfo> objectInfoList = new ArrayList<>();
            rsp.setObjectInfoList(objectInfoList);
            int clientCount = 0;
            for (Map.Entry<Long, List<VisitRecord>> userEntry : userMap.entrySet()) {
                Long userId = userEntry.getKey();
                List<VisitRecord> userVisitRecordList = userEntry.getValue();
                AppPCVisitSummaryRSP.ObjectInfo objectInfo = new AppPCVisitSummaryRSP.ObjectInfo();
                objectInfo.setUserId(userId);
                objectInfo.setCreatedName(id2NameService.sysUserId2NameSingle(userId));
                objectInfo.setVisitCount(userVisitRecordList.size());
                Map<Long, List<VisitRecord>> clientMap = new HashMap<>();
                for (VisitRecord visitRecord : userVisitRecordList) {
                    clientMap.putIfAbsent(visitRecord.getClientId(), new ArrayList<>());
                    clientMap.get(visitRecord.getClientId()).add(visitRecord);
                }
                objectInfo.setClientCount(clientMap.size());
                objectInfoList.add(objectInfo);
                clientCount += clientMap.size();
            }
            rsp.setClientCount(new ValueUnitDTO(String.valueOf(clientCount), "次数"));
            res.add(rsp);
        }
        return res;
    }


    private List<AppVisitRecordRSP> sortAppFunc(List<AppVisitRecordRSP> list) {
        List<AppVisitRecordRSP> res = new ArrayList<>();
        List<AppVisitRecordRSP> passed = new ArrayList<>();
        List<AppVisitRecordRSP> invalid = new ArrayList<>();
        for (AppVisitRecordRSP recordRSP : list) {
            if (VisitRecordStatus.PASSED.name().equalsIgnoreCase(recordRSP.getStatus())) {
                passed.add(recordRSP);
            } else if (VisitRecordStatus.INVALID.name().equalsIgnoreCase(recordRSP.getStatus())) {
                invalid.add(recordRSP);
            }
        }
        if (!passed.isEmpty()) {
            passed.stream().sorted(Comparator.comparing(AppVisitRecordRSP::getCheckInDate).reversed()).collect(Collectors.toList());;
            res.addAll(passed);
        }
        if (!invalid.isEmpty()) {
            invalid.stream().sorted(Comparator.comparing(AppVisitRecordRSP::getCheckInDate).reversed()).collect(Collectors.toList());;
            res.addAll(invalid);
        }
        return res;
    }


    private List<AppPCVisitRecordRSP> sortPCFunc(List<AppPCVisitRecordRSP> list) {
        return list.stream().sorted(Comparator.comparing(AppPCVisitRecordRSP::getCheckInDate).reversed()).collect(Collectors.toList());
    }


    private String generateVersion(String prev) {
        int num = Integer.valueOf(prev);
        StringBuilder sb = new StringBuilder();
        if (num < 9) {
            num += 1;
            sb.append("0");
            sb.append(num);
        } else {
            sb.append(num);
        }
        return sb.toString();
    }


    public void invalidRecord(AppInvalidREQ req) {
        if (ObjectUtil.isNull(req.getId())) {
            throw new MithrasException("拜访记录id为空");
        }
        VisitRecord visitRecord = visitRecordMapper.selectById(req.getId());
        visitRecord.setStatus(VisitRecordStatus.INVALID.name());
        visitRecordMapper.updateAnnotationIncludeNullById(visitRecord);
    }

    public AppRecordDetailRSP detail(AppRecordDetailREQ req) {
        if (ObjectUtil.isNull(req.getId())) {
            throw new MithrasException("拜访记录id为空");
        }
        VisitRecord visitRecord = visitRecordMapper.selectById(req.getId());
        visitRecord.setStatus(VisitRecordStatus.INVALID.name());
        AppRecordDetailRSP appRecordDetailRSP = BeanUtil.copyProperties(visitRecord, AppRecordDetailRSP.class);
        appRecordDetailRSP.setCreatedName(id2NameService.sysUserId2NameSingle(appRecordDetailRSP.getUserId()));
        if (StrUtil.equalsAny(visitRecord.getVisitPhase(), VisitPhaseStatus.ON_SITE_DUE_DILIGENCE.name(), VisitPhaseStatus.CONTRACT_SIGN_OFFLINE.name())
                && StringUtils.isNotBlank(visitRecord.getProjCode())) {
            List<ProjReviewBaseInfo> projReviewBaseInfoList = projReviewBaseInfoMapper
                    .selectList(Wrappers.<ProjReviewBaseInfo>lambdaQuery().eq(ProjReviewBaseInfo::getProjCode, visitRecord.getProjCode()));
            if (!projReviewBaseInfoList.isEmpty()) {
                appRecordDetailRSP.setProjName(projReviewBaseInfoList.get(0).getProjName());
            } else {
                List<ProjEstablishBaseInfo> projEstablishBaseInfoList = projEstablishBaseInfoMapper
                        .selectList(Wrappers.<ProjEstablishBaseInfo>lambdaQuery().eq(ProjEstablishBaseInfo::getProjCode, visitRecord.getProjCode()));
                if (!projEstablishBaseInfoList.isEmpty()) {
                    appRecordDetailRSP.setProjName(projEstablishBaseInfoList.get(0).getProjName());
                } else {
                    List<GroupCreditReviewBaseInfo> groupCreditReviewBaseInfoList = groupCreditReviewBaseInfoMapper
                            .selectList(Wrappers.<GroupCreditReviewBaseInfo>lambdaQuery().eq(GroupCreditReviewBaseInfo::getProjCode, visitRecord.getProjCode()));
                    if (!groupCreditReviewBaseInfoList.isEmpty()) {
                        appRecordDetailRSP.setProjName(groupCreditReviewBaseInfoList.get(0).getProjName());
                    } else {
                        List<GroupCreditEstablishBaseInfo> groupCreditEstablishBaseInfoList = groupCreditEstablishBaseInfoMapper
                                .selectList(Wrappers.<GroupCreditEstablishBaseInfo>lambdaQuery().eq(GroupCreditEstablishBaseInfo::getProjCode, visitRecord.getProjCode()));
                        if (!groupCreditEstablishBaseInfoList.isEmpty()) {
                            appRecordDetailRSP.setProjName(groupCreditEstablishBaseInfoList.get(0).getProjName());
                        }
                    }
                }
            }
        } else if (VisitPhaseStatus.AFTER_LEASE_CHECK_PLAN.name().equalsIgnoreCase(visitRecord.getVisitPhase())
                && ObjectUtil.isNotNull(visitRecord.getCheckPlanId())) {
            List<NewAfterLeaseCheckPlanBase> newAfterLeaseCheckPlanBaseList = leaseCheckPlanBaseMapper
                    .selectList(Wrappers.<NewAfterLeaseCheckPlanBase>lambdaQuery().eq(NewAfterLeaseCheckPlanBase::getId, visitRecord.getCheckPlanId()));
            appRecordDetailRSP.setCheckPlanName(newAfterLeaseCheckPlanBaseList.get(0).getPlanName());
        }
        List<MaterialsList> materialsLists = materialsListService.listBy("VISIT_RECORD", req.getId());
        if (!materialsLists.isEmpty()) {
            List<String> fileUrls = new ArrayList<>();
            for (MaterialsList materialsList : materialsLists) {
                FileDownLoadRSP download = materialsListService.download(materialsList.getId());
                fileUrls.add(download.getFileUrl());
            }
            appRecordDetailRSP.setFileUrls(fileUrls);
        }
        return appRecordDetailRSP;
    }

    public void appFillOtherInfo(List<AppClientListRSP> list) {
        if (list.isEmpty()) {
            return;
        }
        Set<Long> clientIds = list.stream().map(AppClientListRSP::getId).collect(Collectors.toSet());
        List<VisitRecord> visitRecordList = visitRecordMapper.selectList(
                Wrappers.<VisitRecord>lambdaQuery()
                        .in(VisitRecord::getClientId, clientIds)
                        .eq(VisitRecord::getStatus, VisitRecordStatus.PASSED.name())
                        .orderByDesc(VisitRecord::getCheckInDate)
        );
        Map<Long, List<VisitRecord>> visitRecordMap = visitRecordList.stream().collect(Collectors.groupingBy(VisitRecord::getClientId));
        for (AppClientListRSP e : list) {
            List<VisitRecord> visitRecords = visitRecordMap.get(e.getId());
            if (visitRecords == null || visitRecords.isEmpty()) {
                continue;
            }
            fillVisitRecords(null, e, visitRecords);
        }
    }


    public List<AppContractListRSP> list(AppContractListREQ req) {
        List<ContractBaseInfo> contractBaseInfoList = contractBaseInfoMapper.selectList(Wrappers.<ContractBaseInfo>lambdaQuery()
                .eq(ContractBaseInfo::getClientId, req.getClientId())
                .orderByDesc(ContractBaseInfo::getCreateTime));
        if (contractBaseInfoList.isEmpty()) {
            return new ArrayList<>();
        }
        List<AppContractListRSP> res = new ArrayList<>();
        for (ContractBaseInfo contractBaseInfo : contractBaseInfoList) {
            ContractBaseInfoDetailREQ detailREQ = new ContractBaseInfoDetailREQ();
            detailREQ.setId(contractBaseInfo.getId());
            ContractBaseInfoDetailRSP rsp = contractBaseInfoService.detail(detailREQ);
            AppContractListRSP appRsp = BeanUtil.copyProperties(rsp, AppContractListRSP.class);
            res.add(appRsp);
        }
        for (AppContractListRSP appContractListRSP : res) {
            List<PaymentActualDetail> actualDetails = actualDetailMapper.selectList(Wrappers.<PaymentActualDetail>lambdaQuery().eq(PaymentActualDetail::getWriteOffStatus, WriteOffStatus.WRITTEN_OFF.name()).in(PaymentActualDetail::getContractId, appContractListRSP.getId()));
            if (actualDetails.isEmpty()) {
                appContractListRSP.setPaymentStatus(AppPaymentStatus.NO_PAID.name());
            } else {
                appContractListRSP.setPaymentStatus(AppPaymentStatus.PAID.name());
            }
            appContractListRSP.setContractRemaining(getContractRemaining(appContractListRSP.getId()));
        }
        return res;
    }

    private Long getContractRemaining(Long contractId) {
        Long contractRemaining = 0L;
        List<CollectionBaseInfo> collectionBaseInfoList = baseInfoMapper.selectList(Wrappers.<CollectionBaseInfo>lambdaQuery()
                .eq(CollectionBaseInfo::getContractId, contractId)
                .eq(CollectionBaseInfo::getCashFlowItem, CashFlowItemEnum.RENT.name())
                .ne(CollectionBaseInfo::getPhase, 0));
        for (CollectionBaseInfo collectionBaseInfo : collectionBaseInfoList) {
            contractRemaining += LongUtil.null2zero(collectionBaseInfo.getPrincipal()) - LongUtil.null2zero(collectionBaseInfo.getCollectionPrincipal());
        }
        return contractRemaining;
    }


    public AppClientDetailRSP detail(AppClientDetailREQ req) {
        Client client = clientMapper.selectById(req.getClientId());
        if (client == null) {
            return new AppClientDetailRSP();
        }
        AppClientDetailRSP res = BeanUtil.copyProperties(client, AppClientDetailRSP.class);
        List<VisitRecord> visitRecordList = visitRecordMapper.selectList(
                Wrappers.<VisitRecord>lambdaQuery()
                        .eq(VisitRecord::getClientId, client.getId())
                        .eq(VisitRecord::getStatus, VisitRecordStatus.PASSED.name())
                        .orderByDesc(VisitRecord::getCheckInDate)
        );
        Map<Long, List<VisitRecord>> visitRecordMap = visitRecordList.stream().collect(Collectors.groupingBy(VisitRecord::getClientId));
        List<VisitRecord> visitRecords = visitRecordMap.get(req.getClientId());
        if (visitRecords == null || visitRecords.isEmpty()) {
            return res;
        }
        fillVisitRecords(res, null, visitRecords);
        return res;
    }

    public void imageToPdf(AppClientPdfREQ req) {
        List<MultipartFile> imageFiles = req.getImageFiles();
        if (CollectionUtils.isEmpty(imageFiles)) {
            throw new IllegalArgumentException("No images found.");
        }
        ByteArrayOutputStream bos = pdf(imageFiles);
        byte[] bytes = bos.toByteArray();
        materialsListService.add(new ByteArrayInputStream(bytes), "客户附件.pdf", req.getBelongId(), req.getMaterialsType(), req.getMaterialsSubType(), req.getBusinessType());
    }


    public static ByteArrayOutputStream pdf(List<MultipartFile> imageFiles) {
        // 创建A4尺寸的PDF文档，页边距设置为0
        Document doc = new Document(PageSize.A4, 0, 0, 0, 0);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            PdfWriter.getInstance(doc, bos); // PDF写入
            doc.open(); // 打开文档

            // 获取A4页面尺寸
            Rectangle pageSize = doc.getPageSize();
            float pageWidth = pageSize.getWidth();
            float pageHeight = pageSize.getHeight();

            for (MultipartFile mfile : imageFiles) {
                // 通过图片路径获取Image对象
                Image image = Image.getInstance(mfile.getBytes());

                // 获取图片宽高
                float imageWidth = image.getWidth();
                float imageHeight = image.getHeight();

                // 计算图片的缩放比例，保持宽高比适应页面
                float widthScale = pageWidth / imageWidth;
                float heightScale = pageHeight / imageHeight;
                float scale = Math.min(widthScale, heightScale); // 选取最小的缩放比例来保持图片的完整

                // 按比例缩放图片
                image.scaleAbsolute(imageWidth * scale, imageHeight * scale);

                // 将图片居中显示在页面上
                float xPosition = (pageWidth - image.getScaledWidth()) / 2;
                float yPosition = (pageHeight - image.getScaledHeight()) / 2;
                image.setAbsolutePosition(xPosition, yPosition);

                // 添加图片到PDF，并为每张图片创建新的一页
                doc.newPage();
                doc.add(image);
            }

            doc.close(); // 关闭文档
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bos;
    }

    private Set<Long> findVisitRecordIdsByUserId(Long currentUserId) {
        Set<Long> targetVisitRecordIds = new HashSet<>();
        List<VisitRecord> visitRecordList = visitRecordMapper.selectList(
                Wrappers.<VisitRecord>lambdaQuery()
                        .eq(VisitRecord::getUserId, currentUserId)
        );
        if (CollectionUtil.isNotEmpty(visitRecordList)) {
            targetVisitRecordIds.addAll(visitRecordList.stream().map(VisitRecord::getId).collect(Collectors.toSet()));
        }
        return targetVisitRecordIds;
    }

    private List<VisitRecord> findVisitRecordIdsByUserIdAndVisitRecordId(Long currentUserId, List<VisitRecord> visitRecordList) {
        Set<Long> targetVisitRecordIds = visitRecordList.stream().map(VisitRecord::getId).collect(Collectors.toSet());
        List<VisitRecord> visitRecords = visitRecordMapper.selectList(
                Wrappers.<VisitRecord>lambdaQuery()
                        .eq(VisitRecord::getUserId, currentUserId)
                        .in(VisitRecord::getId, targetVisitRecordIds)
                        .orderByDesc(VisitRecord::getCheckInDate)
        );
        return visitRecords;
    }


    private Set<Long> findVisitRecordIdsByDeptIds(List<Long> deptIds) {
        Set<Long> targetVisitRecordIds = new HashSet<>();
        List<VisitRecord> visitRecordList = visitRecordMapper.selectList(
                Wrappers.<VisitRecord>lambdaQuery()
                        .in(VisitRecord::getDeptId, deptIds)
        );
        if (CollectionUtil.isNotEmpty(visitRecordList)) {
            targetVisitRecordIds.addAll(visitRecordList.stream().map(VisitRecord::getId).collect(Collectors.toSet()));
        }
        return targetVisitRecordIds;
    }


    private List<VisitRecord> findVisitRecordIdsByDeptIdsAndVisitRecordId(List<Long> deptIds, List<VisitRecord> visitRecordList) {
        Set<Long> targetVisitRecordIds = visitRecordList.stream().map(VisitRecord::getId).collect(Collectors.toSet());
        List<VisitRecord> visitRecords = visitRecordMapper.selectList(
                Wrappers.<VisitRecord>lambdaQuery()
                        .in(VisitRecord::getDeptId, deptIds)
                        .in(VisitRecord::getId, targetVisitRecordIds)
                        .orderByDesc(VisitRecord::getCheckInDate)
        );
        return visitRecords;
    }


    private void fillVisitRecords(AppClientDetailRSP appClientDetailRSP, AppClientListRSP appClientListRSP, List<VisitRecord> visitRecords) {
        Long currentUserId = AccountUtil.getLoginInfo().getId();
        List<UserOrgJobDO> userOrgJobList = SpringUtil.getBean(UserOrgJobDOMapper.class).selectJobCodeByUserId(Collections.singletonList(currentUserId));
        if (CollectionUtil.isEmpty(userOrgJobList)) {
            return;
        }
        // 过滤出业务部门的岗位
        List<OrgDO> orgList = SpringUtil.getBean(SysUserService.class).listBizDept();
        Set<Long> bizDeptIds = orgList.stream().map(OrgDO::getId).collect(Collectors.toSet());
        userOrgJobList.removeIf(e -> !bizDeptIds.contains(e.getOrgId()));
        Map<String, List<UserOrgJobDO>> userOrgMap = userOrgJobList.stream().collect(Collectors.groupingBy(UserOrgJobDO::getJobCode));
        List<UserOrgJobDO> projmanagerList = userOrgMap.get(JobEnum.projmanager.name());
        List<UserOrgJobDO> businessheadList = userOrgMap.get(JobEnum.businesshead.name());
        List<UserOrgJobDO> leaderinchargeList = userOrgMap.get(JobEnum.leaderincharge.name());
        // 中后台看所有
        if (CollectionUtil.isEmpty(projmanagerList) && CollectionUtil.isEmpty(businessheadList) && CollectionUtil.isEmpty(leaderinchargeList)) {
            if (appClientDetailRSP != null) {
                appClientDetailRSP.setLatestVisitor(id2NameService.sysUserId2NameSingle(visitRecords.get(0).getUserId()));
                appClientDetailRSP.setLatestCheckInDate(visitRecords.get(0).getCheckInDate());
                appClientDetailRSP.setVisitCount(visitRecords.size());
            }
            if (appClientListRSP != null) {
                appClientListRSP.setLatestVisitor(id2NameService.sysUserId2NameSingle(visitRecords.get(0).getUserId()));
                appClientListRSP.setLatestCheckInDate(visitRecords.get(0).getCheckInDate());
                appClientListRSP.setVisitCount(visitRecords.size());
            }
            return;
        }
        // 分管领导
        if (CollectionUtil.isNotEmpty(leaderinchargeList)) {
            List<Long> deptIds = leaderinchargeList.stream().map(UserOrgJobDO::getOrgId).collect(Collectors.toList());
            List<VisitRecord> filterVisitRecords = findVisitRecordIdsByDeptIdsAndVisitRecordId(deptIds, visitRecords);
            if (filterVisitRecords.isEmpty()) {
                return;
            }
            if (appClientDetailRSP != null) {
                appClientDetailRSP.setLatestVisitor(id2NameService.sysUserId2NameSingle(filterVisitRecords.get(0).getUserId()));
                appClientDetailRSP.setLatestCheckInDate(filterVisitRecords.get(0).getCheckInDate());
                appClientDetailRSP.setVisitCount(filterVisitRecords.size());
            }
            if (appClientListRSP != null) {
                appClientListRSP.setLatestVisitor(id2NameService.sysUserId2NameSingle(filterVisitRecords.get(0).getUserId()));
                appClientListRSP.setLatestCheckInDate(filterVisitRecords.get(0).getCheckInDate());
                appClientListRSP.setVisitCount(filterVisitRecords.size());
            }
        }
        // 业务负责人
        if (CollectionUtil.isNotEmpty(businessheadList)) {
            List<Long> deptIds = businessheadList.stream().map(UserOrgJobDO::getOrgId).collect(Collectors.toList());
            List<VisitRecord> filterVisitRecords = findVisitRecordIdsByDeptIdsAndVisitRecordId(deptIds, visitRecords);
            if (filterVisitRecords.isEmpty()) {
                return;
            }
            if (appClientDetailRSP != null) {
                appClientDetailRSP.setLatestVisitor(id2NameService.sysUserId2NameSingle(filterVisitRecords.get(0).getUserId()));
                appClientDetailRSP.setLatestCheckInDate(filterVisitRecords.get(0).getCheckInDate());
                appClientDetailRSP.setVisitCount(filterVisitRecords.size());
            }
            if (appClientListRSP != null) {
                appClientListRSP.setLatestVisitor(id2NameService.sysUserId2NameSingle(filterVisitRecords.get(0).getUserId()));
                appClientListRSP.setLatestCheckInDate(filterVisitRecords.get(0).getCheckInDate());
                appClientListRSP.setVisitCount(filterVisitRecords.size());
            }
            return;
        }
        // 项目经理
        if (CollectionUtil.isNotEmpty(projmanagerList)) {
            List<VisitRecord> filterVisitRecords = findVisitRecordIdsByUserIdAndVisitRecordId(currentUserId, visitRecords);
            if (filterVisitRecords.isEmpty()) {
                return;
            }
            if (appClientDetailRSP != null) {
                appClientDetailRSP.setLatestVisitor(id2NameService.sysUserId2NameSingle(filterVisitRecords.get(0).getUserId()));
                appClientDetailRSP.setLatestCheckInDate(filterVisitRecords.get(0).getCheckInDate());
                appClientDetailRSP.setVisitCount(filterVisitRecords.size());
            }
            if (appClientListRSP != null) {
                appClientListRSP.setLatestVisitor(id2NameService.sysUserId2NameSingle(filterVisitRecords.get(0).getUserId()));
                appClientListRSP.setLatestCheckInDate(filterVisitRecords.get(0).getCheckInDate());
                appClientListRSP.setVisitCount(filterVisitRecords.size());
            }
            return;
        }
    }


    public boolean isSameYearAndMonthDefault(String dateString, String targetString) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        boolean isSameYearAndMonth = false;
        try {
            Date date = formatter.parse(dateString);
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);

            Date target = formatter.parse(targetString);
            Calendar now = Calendar.getInstance();
            now.setTime(target);
            int cYear = now.get(Calendar.YEAR);
            int cMonth = now.get(Calendar.MONTH);
            if (year == cYear && month == cMonth) {
                isSameYearAndMonth = true;
            }
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return isSameYearAndMonth;
    }


    public boolean isSameYearAndMonth(String dateString) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        boolean isSameYearAndMonth = false;
        try {
            Date date = formatter.parse(dateString);
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);

            Date cur = new Date();
            Calendar now = Calendar.getInstance();
            now.setTime(cur);
            int cYear = now.get(Calendar.YEAR);
            int cMonth = now.get(Calendar.MONTH);
            if (year == cYear && month == cMonth) {
                isSameYearAndMonth = true;
            }
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return isSameYearAndMonth;
    }

    public List<ContractRentActualListRSP.TableData> getLatestMonthData(List<ContractRentActualListRSP.TableData> rentActualList) {
        Date now = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        ContractRentActualListRSP.TableData latestData = null;
        for (ContractRentActualListRSP.TableData tableData : rentActualList) {
            try {
                String dateString = tableData.getDate();
                Date date = formatter.parse(dateString);
                if (date.before(now)) {
                    latestData = tableData;
                }
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }
        List<ContractRentActualListRSP.TableData> res = new ArrayList<>();
        for (ContractRentActualListRSP.TableData tableData : rentActualList) {
            if (latestData != null && tableData != null) {
                if (isSameYearAndMonthDefault(latestData.getDate(), tableData.getDate())) {
                    res.add(tableData);
                }
            }
        }
        return res;
    }

    public int monthGap(String actualStartDate) {
        int gap = 0;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = null;
            if (StringUtils.isBlank(actualStartDate)) {
                date = new Date();
            } else {
                date = formatter.parse(actualStartDate);
            }
            Calendar c = Calendar.getInstance();
            c.setTime(date);

            Date cur = new Date();
            Calendar now = Calendar.getInstance();
            now.setTime(cur);
            gap = now.get(Calendar.MONTH) - c.get(Calendar.MONTH) + (now.get(Calendar.YEAR) - c.get(Calendar.YEAR)) * 12;
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return gap;
    }

    public List<AppProjListRSP> copyGroupCreditEstablish(List<GroupCreditEstablishListRSP> groupCreditEstablishStage) {
        List<AppProjListRSP> res = new ArrayList<>();
        for (GroupCreditEstablishListRSP groupCreditEstablishListRSP : groupCreditEstablishStage) {
            AppProjListRSP appProjListRSP = BeanUtil.copyProperties(groupCreditEstablishListRSP, AppProjListRSP.class);
            appProjListRSP.setDeclaredAmount(groupCreditEstablishListRSP.getApplyCreditAmount());
            appProjListRSP.setStage(AppProjStageStatus.GROUP_CREDIT_ESTABLISH.name());
            res.add(appProjListRSP);
        }
        return res;
    }

    public List<AppProjListRSP> copyGroupCreditReview(List<GroupCreditReviewListRSP> groupCreditReviewListStage) {
        List<AppProjListRSP> res = new ArrayList<>();
        for (GroupCreditReviewListRSP groupCreditReviewListRSP : groupCreditReviewListStage) {
            AppProjListRSP appProjListRSP = BeanUtil.copyProperties(groupCreditReviewListRSP, AppProjListRSP.class);
            appProjListRSP.setDeclaredAmount(groupCreditReviewListRSP.getApplyCreditAmount());
            appProjListRSP.setStage(AppProjStageStatus.GROUP_CREDIT_REVIEW.name());
            res.add(appProjListRSP);
        }
        return res;
    }

    public List<AppProjListRSP> copyProjEstablishBase(List<ProjEstablishBaseInfoListRSP> projEstablishBaseListStage, List<ProjEstablishLeasePrice> projEstablishLeasePriceList) {
        Map<Long, ProjEstablishLeasePrice> projEstablishLeaseMap = projEstablishLeasePriceList.stream().collect(Collectors.toMap(ProjEstablishLeasePrice::getProjEstablishId, item -> item));
        List<AppProjListRSP> res = new ArrayList<>();
        for (ProjEstablishBaseInfoListRSP projEstablishBaseInfoListRSP : projEstablishBaseListStage) {
            AppProjListRSP appProjListRSP = BeanUtil.copyProperties(projEstablishBaseInfoListRSP, AppProjListRSP.class);
            if (projEstablishLeaseMap.containsKey(projEstablishBaseInfoListRSP.getId())) {
                appProjListRSP.setDeclaredAmount(projEstablishLeaseMap.get(projEstablishBaseInfoListRSP.getId()).getApplyCreditAmount());
            }
            appProjListRSP.setStage(AppProjStageStatus.PROJ_ESTABLISH_BASE.name());
            res.add(appProjListRSP);
        }
        return res;
    }

    public List<AppProjListRSP> copyProjReviewBase(List<ProjReviewBaseInfoListRSP> projReviewBaseInfoListRSPList) {
        List<AppProjListRSP> res = new ArrayList<>();
        for (ProjReviewBaseInfoListRSP projReviewBaseInfoListRSP : projReviewBaseInfoListRSPList) {
            AppProjListRSP appProjListRSP = BeanUtil.copyProperties(projReviewBaseInfoListRSP, AppProjListRSP.class);
            appProjListRSP.setDeclaredAmount(projReviewBaseInfoListRSP.getDeclaredAmount());
            appProjListRSP.setStage(AppProjStageStatus.PROJ_REVIEW_BASE.name());
            res.add(appProjListRSP);
        }
        return res;
    }

    public boolean isSameYearMonth(String dateStr, LocalDate date) {
        DateTimeFormatter formatterStr = DateTimeFormatter.ofPattern("yyyy-MM");
        YearMonth yearMonth = YearMonth.parse(dateStr, formatterStr);
        int strYear = yearMonth.getYear();
        int strMonth = yearMonth.getMonthValue();

        int dateYear = date.getYear();
        int dateMonth = date.getMonthValue();

        return strYear == dateYear && strMonth == dateMonth;
    }

    public AppContractSignRSP signContract(AppContractSignREQ req) {
        AppContractSignRSP res = new AppContractSignRSP();
        return res;
    }


    public List<AppContractSignExistedRSP> contractExistedSigned(AppContractSignExistedREQ req) {
        ContractBaseInfo contractBaseInfo = contractBaseInfoMapper.selectOne(Wrappers.<ContractBaseInfo>lambdaQuery()
                .eq(ContractBaseInfo::getId, req.getContractId()));
        List<MaterialsList> dataList = materialsListService.getBaseMapper().selectList(Wrappers.<MaterialsList>lambdaQuery()
                .eq(MaterialsList::getBusinessType, BusinessModuleEnum.CONTRACT.name())
                .eq(MaterialsList::getBelongId, contractBaseInfo.getId())
                .in(MaterialsList::getMaterialsType, ListUtil.toList(LendingMaterialType.SIGN_PHOTO_VIDEO.name(), LendingMaterialType.LEASE_RELATED.name()))
        );
        List<AppContractSignExistedRSP> res = new ArrayList<>();
        if (!dataList.isEmpty()) {
            for (MaterialsList materialsList : dataList) {
                AppContractSignExistedRSP appContractSignExistedRSP = new AppContractSignExistedRSP();
                appContractSignExistedRSP.setContractId(contractBaseInfo.getId());
                appContractSignExistedRSP.setFileId(materialsList.getId());
                appContractSignExistedRSP.setBusinessType(materialsList.getBusinessType());
                appContractSignExistedRSP.setMaterialsType(materialsList.getMaterialsType());
                appContractSignExistedRSP.setMaterialSubType(materialsList.getMaterialSubType());
                res.add(appContractSignExistedRSP);
            }
        }
        return res;
    }


    public List<AppContractSignProjExistedRSP> projExistedSigned(AppContractSignProjExistedREQ req) {
        ContractBaseInfo contractBaseInfo = contractBaseInfoMapper.selectOne(Wrappers.<ContractBaseInfo>lambdaQuery()
                .eq(ContractBaseInfo::getId, req.getContractId()));
        List<ContractBaseInfo> contractBaseInfoList = contractBaseInfoMapper.selectList(Wrappers.<ContractBaseInfo>lambdaQuery()
                .eq(ContractBaseInfo::getProjCode, contractBaseInfo.getProjCode()));
        if (contractBaseInfoList.size() <= 1) {
            return new ArrayList<>();
        }
        contractBaseInfoList = contractBaseInfoList.stream().filter(f -> !f.getContractCode().equalsIgnoreCase(contractBaseInfo.getContractCode())).collect(Collectors.toList());
        List<AppContractSignProjExistedRSP> res = new ArrayList<>();
        for (ContractBaseInfo baseInfo : contractBaseInfoList) {
            List<MaterialsList> copyMaterialsLists = materialsListService.getBaseMapper().selectList(Wrappers.<MaterialsList>lambdaQuery()
                    .eq(MaterialsList::getBusinessType, BusinessModuleEnum.CONTRACT.name())
                    .eq(MaterialsList::getBelongId, baseInfo.getId())
                    .in(MaterialsList::getMaterialsType, ListUtil.toList(LendingMaterialType.SIGN_PHOTO_VIDEO.name(), LendingMaterialType.LEASE_RELATED.name())));
            if (!copyMaterialsLists.isEmpty()) {
                AppContractSignProjExistedRSP contractSignExistedRSP = new AppContractSignProjExistedRSP();
                contractSignExistedRSP.setContractCode(baseInfo.getContractCode());
                contractSignExistedRSP.setContractId(baseInfo.getId());
                res.add(contractSignExistedRSP);
            }
        }
        return res;
    }

    public List<AppContractSignCopyRSP> signedCopy(AppContractSignCopyREQ req) {
        ContractBaseInfo contractBaseInfo = contractBaseInfoMapper.selectOne(Wrappers.<ContractBaseInfo>lambdaQuery()
                .eq(ContractBaseInfo::getId, req.getContractId()));
        ContractBaseInfo existedContractBaseInfo = contractBaseInfoMapper.selectOne(Wrappers.<ContractBaseInfo>lambdaQuery()
                .eq(ContractBaseInfo::getId, req.getExistedContractId()));
        List<MaterialsList> copyMaterialsLists = materialsListService.getBaseMapper().selectList(Wrappers.<MaterialsList>lambdaQuery()
                .eq(MaterialsList::getBusinessType, BusinessModuleEnum.CONTRACT.name())
                .eq(MaterialsList::getBelongId, contractBaseInfo.getId())
                .in(MaterialsList::getMaterialsType, ListUtil.toList(LendingMaterialType.SIGN_PHOTO_VIDEO.name(), LendingMaterialType.LEASE_RELATED.name())));
        List<AppContractSignCopyRSP> res = new ArrayList<>();
        if (copyMaterialsLists != null && !copyMaterialsLists.isEmpty()) {
            for (MaterialsList materialsList : copyMaterialsLists) {
                if (LendingMaterialType.SIGN_PHOTO_VIDEO.name().equalsIgnoreCase(materialsList.getMaterialsType())) {
                    AppContractSign appContractSign = contractSignMapper.selectOne(Wrappers.<AppContractSign>lambdaQuery()
                            .eq(AppContractSign::getContractId, materialsList.getBelongId())
                            .eq(AppContractSign::getFileId, materialsList.getId())
                            .eq(AppContractSign::getDeleted, 0));
                    List<ContractSignInfo> contractSignInfoList = contractSignInfoMapper.selectList(Wrappers.<ContractSignInfo>lambdaQuery()
                            .eq(ContractSignInfo::getContractId, existedContractBaseInfo.getId())
                            .eq(ContractSignInfo::getDeleted, 0));
                    Set<Long> userIds = new HashSet<>();
                    if (!contractSignInfoList.isEmpty()) {
                        userIds = contractSignInfoList.stream().map(ContractSignInfo::getSignatory).collect(Collectors.toSet());;
                    }
                    if (userIds.contains(appContractSign.getUserId())) {
                        MaterialsList newMaterialsList = BeanUtil.copyProperties(materialsList, MaterialsList.class, "id");
                        newMaterialsList.setBelongId(existedContractBaseInfo.getId());
                        newMaterialsList.setCreateBy(AccountUtil.getLoginInfo().getId());
                        newMaterialsList.setUpdateBy(AccountUtil.getLoginInfo().getId());
                        newMaterialsList.setCreateTime(LocalDateTime.now());
                        newMaterialsList.setUpdateTime(LocalDateTime.now());
                        materialsListMapper.insert(newMaterialsList);
                        AppContractSign newContractSign = new AppContractSign();
                        newContractSign.setContractId(existedContractBaseInfo.getId());
                        newContractSign.setUserId(appContractSign.getUserId());
                        newContractSign.setFileId(newMaterialsList.getId());
                        contractSignMapper.insert(newContractSign);
                        AppContractSignCopyRSP appContractSignCopyRSP = new AppContractSignCopyRSP();
                        appContractSignCopyRSP.setContractId(existedContractBaseInfo.getId());
                        appContractSignCopyRSP.setFileId(newMaterialsList.getId());
                        appContractSignCopyRSP.setBusinessType(newMaterialsList.getBusinessType());
                        appContractSignCopyRSP.setMaterialsType(newMaterialsList.getMaterialsType());
                        appContractSignCopyRSP.setMaterialSubType(newMaterialsList.getMaterialSubType());
                        res.add(appContractSignCopyRSP);
                    }
                } else if (LendingMaterialType.LEASE_RELATED.name().equalsIgnoreCase(materialsList.getMaterialsType())) {
                    MaterialsList newMaterialsList = BeanUtil.copyProperties(materialsList, MaterialsList.class, "id");
                    newMaterialsList.setBelongId(existedContractBaseInfo.getId());
                    newMaterialsList.setCreateTime(LocalDateTime.now());
                    newMaterialsList.setUpdateTime(LocalDateTime.now());
                    materialsListMapper.insert(newMaterialsList);
                    AppContractSignCopyRSP appContractSignCopyRSP = new AppContractSignCopyRSP();
                    appContractSignCopyRSP.setContractId(existedContractBaseInfo.getId());
                    appContractSignCopyRSP.setFileId(newMaterialsList.getId());
                    appContractSignCopyRSP.setBusinessType(newMaterialsList.getBusinessType());
                    appContractSignCopyRSP.setMaterialsType(newMaterialsList.getMaterialsType());
                    appContractSignCopyRSP.setMaterialSubType(newMaterialsList.getMaterialSubType());
                    res.add(appContractSignCopyRSP);
                }
            }
        }
        return res;
    }


    public List<AppContractSignDetailRSP> signDetail(AppContractSignDetailREQ req) {
        ContractBaseInfo contractBaseInfo = contractBaseInfoMapper.selectOne(Wrappers.<ContractBaseInfo>lambdaQuery()
                .eq(ContractBaseInfo::getId, req.getContractId()));
        LambdaQueryWrapper<ContractSignInfo> query = Wrappers.lambdaQuery();
        query.eq(ContractSignInfo::getContractId, contractBaseInfo.getId());
        // 不展示签约方是租赁的记录
        query.ne(ContractSignInfo::getSignatory, 0L);
        query.eq(ContractSignInfo::getFaceSignShowFlag, YesOrNoNumberEnum.YES.getCode());
        List<ContractSignInfo> result = contractSignInfoService.list(query);
        Map<Long, List<ContractSignInfo>> map = result.stream().collect(Collectors.groupingBy(ContractSignInfo::getSignatory));
        List<MaterialsList> materialsLists = materialsListService.getBaseMapper().selectList(Wrappers.<MaterialsList>lambdaQuery()
                .eq(MaterialsList::getBusinessType, BusinessModuleEnum.CONTRACT.name())
                .eq(MaterialsList::getBelongId, contractBaseInfo.getId())
                .in(MaterialsList::getMaterialsType, ListUtil.toList(LendingMaterialType.SIGN_PHOTO_VIDEO.name(), LendingMaterialType.LEASE_RELATED.name()))
        );
        List<AppContractSignDetailRSP> res = new ArrayList<>();
        for (Map.Entry<Long, List<ContractSignInfo>> entry : map.entrySet()) {
            List<Long> filePhotoIds = new ArrayList<>();
            List<Long> fileVideoIds = new ArrayList<>();
            Long userId = entry.getKey();
            Set<Long> fileIds = entry.getValue().stream().map(ContractSignInfo::getFileId).collect(Collectors.toSet());;
            AppContractSignDetailRSP signDetailRSP = new AppContractSignDetailRSP();
            signDetailRSP.setUserId(userId);
            signDetailRSP.setUserName(SpringUtil.getBean(Id2NameService.class).clientId2NameSingle(userId));
            signDetailRSP.setBusinessType(BusinessModuleEnum.CONTRACT.name());
            signDetailRSP.setMaterialsType(ContractTypeEnum.MAIN_CONTRACT.name());
            signDetailRSP.setMaterialsTypeName(ContractTypeEnum.MAIN_CONTRACT.display());
            signDetailRSP.setContractId(contractBaseInfo.getId());
            signDetailRSP.setFileIds(new ArrayList<>(fileIds));
            List<MaterialsList> fileMaterialsLists = materialsListService.getBaseMapper().selectList(Wrappers.<MaterialsList>lambdaQuery()
                    .in(MaterialsList::getId, fileIds)
            );
            signDetailRSP.setFileNames(fileMaterialsLists.stream().map(MaterialsList::getFilename).collect(Collectors.toList()));
            List<AppContractSign> appContractSignList = contractSignMapper.selectList(Wrappers.<AppContractSign>lambdaQuery()
                    .eq(AppContractSign::getContractId, contractBaseInfo.getId())
                    .eq(AppContractSign::getUserId, userId)
                    .eq(AppContractSign::getDeleted, 0));
            if (!appContractSignList.isEmpty()) {
                for (AppContractSign appContractSign : appContractSignList) {
                    for (MaterialsList materialsList : materialsLists) {
                        if (appContractSign.getFileId().equals(materialsList.getId())
                                && LendingMaterialType.SIGN_PHOTO_VIDEO.name().equalsIgnoreCase(materialsList.getMaterialsType())
                                && AppContractSubTypeEnum.SIGN_LOCATION_PHOTO.name().equalsIgnoreCase(materialsList.getMaterialSubType())) {
                            filePhotoIds.add(materialsList.getId());
                        } else if (appContractSign.getFileId().equals(materialsList.getId())
                                && LendingMaterialType.SIGN_PHOTO_VIDEO.name().equalsIgnoreCase(materialsList.getMaterialsType())
                                && AppContractSubTypeEnum.SIGN_VIDEO.name().equalsIgnoreCase(materialsList.getMaterialSubType())) {
                            fileVideoIds.add(materialsList.getId());
                        }
                    }
                }
                signDetailRSP.setFilePhotoIds(filePhotoIds);
                signDetailRSP.setFileVideoIds(fileVideoIds);
            }
            res.add(signDetailRSP);
        }

        Long projReviewId = contractBaseInfo.getProjReviewId();
        List<LeaseItemInfo> leaseItemInfos = leaseItemInfoService.lambdaQuery()
                .eq(LeaseItemInfo::getProjReviewId, projReviewId)
                .eq(LeaseItemInfo::getApprovalStatus, ProcessStatus.APPROVAL_PASS.name())
                .orderByAsc(LeaseItemInfo::getId).list();
        List<Long> humanMachineIds = new ArrayList<>();
        List<Long> deviceIds = new ArrayList<>();
        for (MaterialsList materialsList : materialsLists) {
            if (LendingMaterialType.LEASE_RELATED.name().equalsIgnoreCase(materialsList.getMaterialsType())
                    && AppContractSubTypeEnum.MAN_MACHINE_PHOTO.name().equalsIgnoreCase(materialsList.getMaterialSubType())) {
                humanMachineIds.add(materialsList.getId());
            } else if (LendingMaterialType.LEASE_RELATED.name().equalsIgnoreCase(materialsList.getMaterialsType())
                    && AppContractSubTypeEnum.DEVICE_PHOTO.name().equalsIgnoreCase(materialsList.getMaterialSubType())) {
                deviceIds.add(materialsList.getId());
            }
        }
        AppContractSignDetailRSP signDetailRSP = new AppContractSignDetailRSP();
        if (!leaseItemInfos.isEmpty() && StringUtils.isNotBlank(leaseItemInfos.get(0).getLeaseItemTypes())) {
            List<String> types = JSONUtil.toList(leaseItemInfos.get(0).getLeaseItemTypes(), String.class);
            signDetailRSP.setLeaseItemTypes(types);
        }
        signDetailRSP.setHumanMachineIds(humanMachineIds);
        signDetailRSP.setDeviceIds(deviceIds);
        signDetailRSP.setBusinessType(BusinessModuleEnum.CONTRACT.name());
        signDetailRSP.setContractId(contractBaseInfo.getId());
        signDetailRSP.setMaterialsTypeName(LendingMaterialType.LEASE_RELATED.name());
        signDetailRSP.setLeaseType(contractBaseInfo.getLeaseType());
        res.add(signDetailRSP);
        return res;
    }


    public AppContractPaySignedRSP isSigned(AppContractPaySignedREQ req) {
        AppContractPaySignedRSP rsp = new AppContractPaySignedRSP();
        ContractBaseInfo contractBaseInfo = contractBaseInfoMapper.selectOne(Wrappers.<ContractBaseInfo>lambdaQuery()
                .eq(ContractBaseInfo::getId, req.getContractId()));
        rsp.setIsSigned(contractBaseInfo.getIsSigned());
        return rsp;
    }


    public void updateSign(AppContractSignUpdateREQ req) {
        ContractBaseInfo contractBaseInfo = contractBaseInfoMapper.selectOne(Wrappers.<ContractBaseInfo>lambdaQuery()
                .eq(ContractBaseInfo::getId, req.getContractId()));
        contractBaseInfo.setIsSigned(req.getIsSigned());
        contractBaseInfoMapper.updateAnnotationIncludeNullById(contractBaseInfo);
    }


    public List<AppAuthorityDetailRSP> authorityDetail(AppAuthorityDetailREQ req) {
        List<AppAuthorityDetailRSP> res = new ArrayList<>();
        AppAuthorityDetailRSP home = new AppAuthorityDetailRSP("首页", "home", false);
        AppAuthorityDetailRSP todo = new AppAuthorityDetailRSP("待办", "todo", false);
        AppAuthorityDetailRSP message = new AppAuthorityDetailRSP("消息", "message", false);
        AppAuthorityDetailRSP visit = new AppAuthorityDetailRSP("发起拜访", "visit", false);
        AppAuthorityDetailRSP customer = new AppAuthorityDetailRSP("我的客户", "customer", false);
        AppAuthorityDetailRSP myProject = new AppAuthorityDetailRSP("我的项目", "myProject", false);
        AppAuthorityDetailRSP paymentCalendar = new AppAuthorityDetailRSP("合同面签", "paymentCalendar", false);
        AppAuthorityDetailRSP offerCalc = new AppAuthorityDetailRSP("报价测算", "offerCalc", false);
        AppAuthorityDetailRSP calendar = new AppAuthorityDetailRSP("还款日历", "calendar", false);
        AppAuthorityDetailRSP process = new AppAuthorityDetailRSP("流程查询", "process", false);

        res.add(home);
        res.add(todo);
        res.add(process);
        res.add(message);
        res.add(visit);
        res.add(customer);
        res.add(offerCalc);
        res.add(paymentCalendar);
        res.add(myProject);
        res.add(calendar);
        List<String> currentUserRoles = sysUserService.getCurrentUserRoles();
        if (currentUserRoles.isEmpty()) {
            return res;
        }
        if (currentUserRoles.contains("CHAKAN") || currentUserRoles.contains("XMJL")) {
            home.setShow(true);
            todo.setShow(true);
            process.setShow(true);
            message.setShow(true);
            visit.setShow(true);
            customer.setShow(true);
            offerCalc.setShow(true);
            paymentCalendar.setShow(true);
            myProject.setShow(true);
            calendar.setShow(true);
            return res;
        }
        if (currentUserRoles.contains("YWFZR")) {
            visit.setShow(true);
            customer.setShow(true);
            myProject.setShow(true);
            paymentCalendar.setShow(true);
            offerCalc.setShow(true);
            calendar.setShow(true);
            return res;
        }
        return res;
    }
}
