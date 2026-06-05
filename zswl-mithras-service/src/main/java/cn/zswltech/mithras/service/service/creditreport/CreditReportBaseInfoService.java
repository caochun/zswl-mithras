package cn.zswltech.mithras.creditreport.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.zswltech.flow.core.api.FlowProcessApiService;
import cn.zswltech.flow.core.api.FlowTaskApiService;
import cn.zswltech.flow.core.domain.req.StartProcessReq;
import cn.zswltech.flow.core.domain.req.task.ProcessPageReq;
import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.flow.core.enums.ProcessBusinessStatusEnum;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.gruul.dao.dal.dao.UserOrgJobDOMapper;
import cn.zswltech.gruul.dao.dal.entity.OrgDO;
import cn.zswltech.gruul.dao.dal.entity.UserOrgJobDO;
import cn.zswltech.gruul.dao.dal.vo.AccountVO;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.client.client.ClientInfo;
import cn.zswltech.mithras.dto.creditreport.*;
import cn.zswltech.mithras.creditreport.constant.CreditReportConstants;
import cn.zswltech.mithras.service.constant.GlobalConstants;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.creditreport.convert.CreditSearchConvert;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.enums.JobEnum;
import cn.zswltech.mithras.service.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.creditreport.enums.CreditReportMaterialSubTypeEnum;
import cn.zswltech.mithras.creditreport.enums.CreditReportMaterialTypeEnum;
import cn.zswltech.mithras.creditreport.enums.CreditSearchStatusEnum;
import cn.zswltech.mithras.creditreport.enums.SearchGoalEnum;
import cn.zswltech.mithras.service.enums.fund.receiptrepay.ProcessState;
import cn.zswltech.mithras.projectprocess.enums.projreview.ReviewRelationDataType;
import cn.zswltech.mithras.creditreport.excel.exporter.CreditSearchListExcelExporter;
import cn.zswltech.mithras.creditreport.excel.CreditSearchExcelModel;
import cn.zswltech.mithras.service.mapper.MaterialsListMapper;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.corp.CorpCommerceInfoMapper;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.corp.CorpShareholderInfoMapper;
import cn.zswltech.mithras.creditreport.mapper.CreditReportBaseInfoMapper;
import cn.zswltech.mithras.creditreport.mapper.CreditReportRecordDetailsMapper;
import cn.zswltech.mithras.creditreport.mapper.dto.credit.XJCreditReportJsonDTO;
import cn.zswltech.mithras.service.mapper.model.MaterialsList;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.Client;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.CorpCommerceInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.creditreport.mapper.model.CreditReportBaseInfo;
import cn.zswltech.mithras.creditreport.mapper.model.CreditReportClientItem;
import cn.zswltech.mithras.creditreport.mapper.model.CreditReportRecordDetails;
import cn.zswltech.mithras.credit.infrastructure.persistence.groupcredit.establish.model.GroupCreditEstablishBaseInfo;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.model.PaymentBaseInfo;
import cn.zswltech.mithras.projectprocess.mapper.model.projestablish.ProjEstablishBaseInfo;
import cn.zswltech.mithras.projectprocess.mapper.model.projreview.ProjReviewBaseInfo;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.others.SpringContextHolder;
import cn.zswltech.mithras.system.service.Id2NameService;
import cn.zswltech.mithras.system.service.SysUserService;
import cn.zswltech.mithras.customer.application.client.ClientBusinessHistoryService;
import cn.zswltech.mithras.service.service.client.ClientService;
import cn.zswltech.mithras.service.service.contract.ContractBaseInfoService;
import cn.zswltech.mithras.service.service.contract.ContractTradeStructureService;
import cn.zswltech.mithras.creditreport.service.resp.CreditReportObtainResultPDFResp;
import cn.zswltech.mithras.creditreport.service.CreditReportQueryService;
import cn.zswltech.mithras.service.service.groupcreditestablish.GroupCreditEstablishBaseInfoService;
import cn.zswltech.mithras.service.service.materialsfile.MaterialsListService;
import cn.zswltech.mithras.service.service.payment.PaymentBaseInfoService;
import cn.zswltech.mithras.service.service.projestablish.ProjEstablishBaseInfoService;
import cn.zswltech.mithras.service.service.projestablish.ProjEstablishTradeStructureService;
import cn.zswltech.mithras.service.service.projreview.ProjReviewBaseInfoService;
import cn.zswltech.mithras.service.service.projreview.ProjReviewTradeStructureService;
import cn.zswltech.mithras.basedata.util.DateUtil;
import cn.zswltech.mithras.service.util.StringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static cn.hutool.core.util.ObjectUtil.isNull;
import static cn.zswltech.mithras.service.constant.ResultMsg.RECORD_NOT_EXIST;
import static cn.zswltech.mithras.service.enums.common.RecordStatus.EXPIRE;
import static cn.zswltech.mithras.contract.enums.contract.ProjItemStatus.CLOSED;
import static cn.zswltech.mithras.contract.enums.contract.ProjItemStatus.INVALID;

/**
 * @author vico
 * @description 征信报告基本信息表
 * @date 2025-11-24
 */
@Service
@Slf4j
public class CreditReportBaseInfoService extends ServiceImpl<CreditReportBaseInfoMapper, CreditReportBaseInfo> implements CreditReportQueryService {

    @Resource
    private CreditReportBaseInfoMapper creditReportBaseInfoMapper;
    @Resource
    private SysUserService sysUserService;
    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private ProjReviewBaseInfoService projReviewBaseInfoService;
    @Resource
    private CreditReportClientItemService creditReportClientItemService;
    @Resource
    private ClientService clientService;
    @Resource
    private Id2NameService id2NameService;
    //审批流相关
    @Resource
    private FlowTaskApiService taskApiService;
    @Resource
    private FlowProcessApiService processApiService;
    @Resource
    private MaterialsListMapper materialsListMapper;
    @Resource
    private CreditReportApiService creditReportApiService;
    @Autowired
    private List<CreditReportParseInterface> creditReportParseInterfaces;
    @Resource
    private MaterialsListService materialsListService;
    @Resource
    private CorpCommerceInfoMapper corpCommerceInfoMapper;
    @Resource
    private CreditSearchListExcelExporter creditSearchListExcelExporter;
    @Resource
    private CorpShareholderInfoMapper corpShareholderInfoMapper;
    @Resource
    private ClientBusinessHistoryService clientBusinessHistoryService;
    @Autowired
    private CreditReportRecordDetailsMapper creditReportRecordDetailsMapper;


    @Transactional(rollbackFor = Throwable.class)
    public void add(CreditReportAddCmd req) {
        CreditReportBaseInfo creditReportDO = BeanUtil.copyProperties(req, CreditReportBaseInfo.class);

        OrgDO orgDO = sysUserService.getUserDeptList().get(0);
        creditReportDO.setCreditCode(generateCreditCodeSimple());
        creditReportDO.setApplyOrg(orgDO.getId());
        creditReportDO.setApplyStatus(ProcessState.UN_SUBMIT.name());
        creditReportDO.setSelectVersion(req.getSelectVersion());
        creditReportDO.setReportFormat(req.getReportFormat());
        creditReportDO.setProjCode(req.getProjCode());
        creditReportDO.setProjName(req.getProjName());
        //补充授信结束时间 项目下最后
        List<ContractBaseInfo> contractByes = getContractByesId(req.getProjIdDataType(), req.getProjId());
        LocalDate oneYearAfter = LocalDate.now().plusYears(1);
        if (CollectionUtil.isEmpty(contractByes)) {
            creditReportDO.setAuthorizationEndDate(oneYearAfter);
        } else {
            Map<Long, LocalDate> contractExpirationDateByRent = contractBaseInfoService.getContractExpirationDateByRent(contractByes.stream().map(ContractBaseInfo::getId).collect(Collectors.toList()));
            if (ObjectUtil.isEmpty(contractExpirationDateByRent)) {
                creditReportDO.setAuthorizationEndDate(oneYearAfter);
            } else {
                //获取最晚的
                contractExpirationDateByRent.values().forEach(e -> {
                            if (ObjectUtil.isEmpty(creditReportDO.getAuthorizationEndDate()) || (ObjectUtil.isNotEmpty(e) && e.isAfter(creditReportDO.getAuthorizationEndDate()))) {
                                creditReportDO.setAuthorizationEndDate(e);
                            }
                        }
                );
            }
        }
        save(creditReportDO);
        //保存客户信息
        CreditReportClientInfo clientItemAddREQ = BeanUtil.copyProperties(req, CreditReportClientInfo.class);
        clientItemAddREQ.setCreditReportBaseInfoId(creditReportDO.getId());
        creditReportClientItemService.addBatch(Collections.singletonList(clientItemAddREQ));

    }

    public List<ContractBaseInfo> getContractByesId(String projIdDataType, Long projId) {
        List<Long> projReviewId = projReviewBaseInfoService.list(Wrappers.<ProjReviewBaseInfo>lambdaQuery()
                .notIn(ProjReviewBaseInfo::getProjReviewStatus, CLOSED.name(), INVALID.name(), EXPIRE.name())
                .eq(ObjectUtil.isNotEmpty(projId), ProjReviewBaseInfo::getProjEstablishId, projId)
                .eq(ObjectUtil.isNotEmpty(projIdDataType), ProjReviewBaseInfo::getRelationDataType, projIdDataType)
        ).stream().map(ProjReviewBaseInfo::getId).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(projReviewId)) {
            return null;
        }
        return contractBaseInfoService.listByProjReviewIds(projReviewId);
    }

    //唯一的编号
    public String generateCreditCodeSimple() {
        DateTimeFormatter ofPattern = DateTimeFormatter.ofPattern("yyyyMMdd");
        String localDate = LocalDateTime.now().format(ofPattern);

        CreditReportBaseInfo todayLastRecord =
                this.getOne(Wrappers.<CreditReportBaseInfo>lambdaQuery().ge(CreditReportBaseInfo::getCreateTime, LocalDateTimeUtil.beginOfDay(LocalDateTime.now())).le(CreditReportBaseInfo::getCreateTime, LocalDateTimeUtil.endOfDay(LocalDateTime.now())).orderByDesc(CreditReportBaseInfo::getCreateTime).last("LIMIT 1"));

        int batchSeq = Optional.ofNullable(todayLastRecord).map(record -> {
            String creditCode = record.getCreditCode();
            if (StrUtil.isBlank(creditCode) || creditCode.length() <= 8) {
                return 1;
            }
            try {
                String suffix = creditCode.substring(8);
                return Integer.parseInt(suffix) + 1;
            } catch (NumberFormatException e) {
                return 1;
            }
        }).orElse(1);

        return localDate + String.format("%02d", batchSeq);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void modify(CreditReportSaveCmd req) {

        List<CreditReportClientInfo> clientInfos = req.getClientInfos();
        checkClient(clientInfos);
        //处理主表
        CreditReportBaseInfo baseInfo = this.getById(req.getId());
        if (ObjectUtil.isEmpty(baseInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        LambdaUpdateWrapper<CreditReportBaseInfo> updateWrapper = new LambdaUpdateWrapper<CreditReportBaseInfo>();
        if (ObjectUtil.isNotEmpty(req.getSelectVersion())) {
            updateWrapper.set(CreditReportBaseInfo::getSelectVersion, req.getSelectVersion());
        }
        if (ObjectUtil.isNotEmpty(req.getReportFormat())) {
            updateWrapper.set(CreditReportBaseInfo::getReportFormat, req.getReportFormat());
        }

        if (ObjectUtil.isNotEmpty(req.getAuthorizationBeganDate())) {
            updateWrapper.set(CreditReportBaseInfo::getAuthorizationBeganDate, req.getAuthorizationBeganDate());
        }
        updateWrapper.eq(CreditReportBaseInfo::getId, req.getId());
        if (ObjectUtil.isNotEmpty(req.getSelectVersion()) || ObjectUtil.isNotEmpty(req.getReportFormat()) || ObjectUtil.isNotEmpty(req.getAuthorizationBeganDate())) {
            creditReportBaseInfoMapper.update(null, updateWrapper);
        }
        //处理子表数据

        //需要新增数据
        List<CreditReportClientInfo> addRecords = clientInfos.stream().filter(e -> ObjectUtil.isEmpty(e.getId())).collect(Collectors.toList());
        //需要更新数据
        List<CreditReportClientInfo> updateRecords = clientInfos.stream().filter(e -> ObjectUtil.isNotEmpty(e.getId())).collect(Collectors.toList());
        //先删除
        List<Long> surviveClientInfo = null;
        if (ObjectUtil.isNotEmpty(updateRecords)) {
            surviveClientInfo = updateRecords.stream().map(CreditReportClientInfo::getId).collect(Collectors.toList());
        }
        creditReportClientItemService.remove(req.getId(), surviveClientInfo);
        //新增
        addRecords.forEach(e -> e.setCreditReportBaseInfoId(req.getId()));
        creditReportClientItemService.addBatch(addRecords);
        //更新
        creditReportClientItemService.updateBatch(updateRecords);
    }

    private void checkClient(List<CreditReportClientInfo> clientInfos) {
        Set<Long> content = new HashSet<>();
        clientInfos.forEach(e -> {
            if (content.contains(e.getClientId())) {
                throw new MithrasException("不支持重复添加客户");
            }
            content.add(e.getClientId());
        });
    }

    public PageR<CreditReportListDTO> list(CreditReportListREQ req) {
        req.setSearchTimeFrom(DateUtil.startOfDay(req.getCreateFrom()));
        req.setSearchTimeTo(DateUtil.endOfDay(req.getCreateTo()));

        // 需要按照不同登陆角色处理
        Set<Long> targetClientIds = new HashSet<>();
        // 默认先填一个不可能的值，如果后续逻辑没有往里填充说明没有可看的客户
        targetClientIds.add(-100L);
        Long currentUserId = AccountUtil.getLoginInfo().getId();
        List<UserOrgJobDO> userOrgJobList = SpringUtil.getBean(UserOrgJobDOMapper.class).selectJobCodeByUserId(Collections.singletonList(currentUserId));
        if (CollectionUtil.isEmpty(userOrgJobList)) {
            return null;
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
            req.setTargetClientIds(null);
        } else {
            // 项目经理
            if (CollectionUtil.isNotEmpty(projmanagerList)) {
                Set<Long> projmanagerTargetClientIds = clientService.findTargetClientIdsByUserId(currentUserId);
                if (CollectionUtil.isNotEmpty(projmanagerTargetClientIds)) {
                    targetClientIds.addAll(projmanagerTargetClientIds);
                }
            }
            // 业务负责人
            if (CollectionUtil.isNotEmpty(businessheadList)) {
                List<Long> deptIds = businessheadList.stream().map(UserOrgJobDO::getOrgId).collect(Collectors.toList());
                Set<Long> businessheadTargetClientIds = clientService.findTargetClientIdsByDeptIds(deptIds);
                if (CollectionUtil.isNotEmpty(businessheadTargetClientIds)) {
                    targetClientIds.addAll(businessheadTargetClientIds);
                }
            }
            // 分管领导
            if (CollectionUtil.isNotEmpty(leaderinchargeList)) {
                List<Long> deptIds = leaderinchargeList.stream().map(UserOrgJobDO::getOrgId).collect(Collectors.toList());
                Set<Long> leaderinchargeTargetClientIds = clientService.findTargetClientIdsByDeptIds(deptIds);
                if (CollectionUtil.isNotEmpty(leaderinchargeTargetClientIds)) {
                    targetClientIds.addAll(leaderinchargeTargetClientIds);
                }
            }
            // 填充条件
            req.setTargetClientIds(targetClientIds);
        }
        //补充客户名称
        if (ObjectUtil.isNotEmpty(req.getClientName()) || ObjectUtil.isNotEmpty(req.getCscCode()) || ObjectUtil.isNotEmpty(req.getSelectGoal())) {
            req.setCreditReportBaseInfoIds(creditReportClientItemService.list(Wrappers.<CreditReportClientItem>lambdaQuery()
                    .like(ObjectUtil.isNotEmpty(req.getClientName()), CreditReportClientItem::getClientName, req.getClientName())
                    .like(ObjectUtil.isNotEmpty(req.getCscCode()), CreditReportClientItem::getCscCode, req.getCscCode())
                    .eq(ObjectUtil.isNotEmpty(req.getSelectGoal()), CreditReportClientItem::getSelectGoal, req.getSelectGoal())
            ).stream().map(CreditReportClientItem::getCreditReportBaseInfoId).collect(Collectors.toList()));
        }

        Page<CreditReportBaseInfo> page = this.page(new Page<>(req.getPage(), req.getPageSize()), Wrappers.<CreditReportBaseInfo>lambdaQuery()
                .like(ObjectUtil.isNotEmpty(req.getCreditCode()), CreditReportBaseInfo::getCreditCode, req.getCreditCode())
                .in(ObjectUtil.isNotEmpty(req.getCreditReportBaseInfoIds()), CreditReportBaseInfo::getId, req.getCreditReportBaseInfoIds())
                .like(ObjectUtil.isNotEmpty(req.getProjName()), CreditReportBaseInfo::getProjName, req.getProjName())
                .eq(ObjectUtil.isNotEmpty(req.getProjCode()), CreditReportBaseInfo::getProjCode, req.getProjCode())
                .eq(ObjectUtil.isNotEmpty(req.getApplyUser()), CreditReportBaseInfo::getCreateBy, req.getApplyUser())
                .eq(ObjectUtil.isNotEmpty(req.getApplyOrg()), CreditReportBaseInfo::getApplyOrg, req.getApplyOrg())
                .ge(ObjectUtil.isNotEmpty(req.getCreateFrom()), CreditReportBaseInfo::getSelectTime, req.getCreateFrom())
                .le(ObjectUtil.isNotEmpty(req.getCreateTo()), CreditReportBaseInfo::getSelectTime, req.getCreateTo())
                .orderByDesc(CreditReportBaseInfo::getId)
        );
        List<CreditReportBaseInfo> records = page.getRecords();
        if (ObjectUtil.isEmpty(records)) {
            return null;
        }
        List<CreditReportClientItem> clientItems = creditReportClientItemService.list(Wrappers.<CreditReportClientItem>lambdaQuery()
                .in(CreditReportClientItem::getCreditReportBaseInfoId, records.stream().map(CreditReportBaseInfo::getId).collect(Collectors.toSet())));
        if (ObjectUtil.isEmpty(clientItems)) {
            return null;
        }
        Map<Long, List<CreditReportClientItem>> baseId2Item = clientItems.stream().collect(Collectors.groupingBy(CreditReportClientItem::getCreditReportBaseInfoId));
        //查询文件列表
        List<Long> reportClientIds = clientItems.stream().map(CreditReportClientItem::getId).collect(Collectors.toList());
        Map<Long, List<MaterialsList>> belongId2FileList = materialsListService.list(BusinessModuleEnum.CREDIT_REPORT_SELECT.name(), Collections.singletonList(CreditReportMaterialTypeEnum.CLIENT_CREDIT_REPORT.name()), reportClientIds)
                .stream().collect(Collectors.groupingBy(MaterialsList::getBelongId));
        //转换名称
        Map<Long, String> userId2Name = id2NameService.sysUserId2Name(records.stream().map(CreditReportBaseInfo::getCreateBy).collect(Collectors.toList()));
        Map<Long, String> orgId2name = id2NameService.deptId2Name(records.stream().map(CreditReportBaseInfo::getApplyOrg).collect(Collectors.toList()));
        //封装请求
        List<CreditReportListDTO> creditReportListDTOs = new ArrayList<>();
        records.forEach(record -> {
            List<CreditReportClientItem> creditReportClientItems = baseId2Item.get(record.getId());
            //
            CreditReportListDTO dto = BeanUtil.copyProperties(record, CreditReportListDTO.class);
            dto.setApplyUser(record.getCreateBy());
            dto.setApplyUserName(userId2Name.get(dto.getApplyUser()));
            dto.setApplyOrgName(orgId2name.get(dto.getApplyOrg()));
            if (ObjectUtil.isNotEmpty(creditReportClientItems)) {
                //兼容之前的设计
                CreditReportClientItem client = creditReportClientItems.get(0);
                dto.setClientName(client.getClientName());
                dto.setCscCode(client.getCscCode());
                dto.setZhongZhengCode(client.getZhongZhengCode());
                dto.setSelectGoal(client.getSelectGoal());
                // 提取字段列表
                List<String> clientName = new ArrayList<>();
                List<String> cscCodeList = new ArrayList<>();
                List<String> zhongZhengCodeList = new ArrayList<>();
                List<String> selectGoalList = new ArrayList<>();
                List<Long> materialsLists = new ArrayList<>();
                for (CreditReportClientItem itemDO : creditReportClientItems) {
                    clientName.add(itemDO.getClientName());
                    cscCodeList.add(itemDO.getCscCode());
                    zhongZhengCodeList.add(itemDO.getZhongZhengCode());
                    selectGoalList.add(itemDO.getSelectGoal());
                    if (ObjectUtil.isNotEmpty(belongId2FileList.get(itemDO.getId()))) {
                        materialsLists.addAll(belongId2FileList.get(itemDO.getId()).stream().map(MaterialsList::getId).collect(Collectors.toList()));
                    }
                }
                dto.setClientNameList(clientName);
                dto.setCscCodeList(cscCodeList);
                dto.setZhongZhengCodeList(zhongZhengCodeList);
                dto.setSelectGoalList(selectGoalList);
                dto.setReportFileIds(materialsLists);
            }
            creditReportListDTOs.add(dto);
        });

        return PageR.of(creditReportListDTOs, page.getTotal(), page.getCurrent(), page.getSize());
    }

    @Transactional(rollbackFor = Throwable.class)
    public void remove(Long id) {
        CreditReportBaseInfo originalInfo = creditReportBaseInfoMapper.selectById(id);
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        if (!ProcessState.UN_SUBMIT.name().equals(originalInfo.getApplyStatus())) {
            throw new MithrasException("征信查询申请状态不是待提交，无法删除");
        }
        LambdaUpdateWrapper<CreditReportBaseInfo> wrapper = new LambdaUpdateWrapper<CreditReportBaseInfo>();
        wrapper.set(CreditReportBaseInfo::getDeleted, YesOrNoNumberEnum.YES.getCode());
        wrapper.eq(CreditReportBaseInfo::getId, id);
        creditReportBaseInfoMapper.update(null, wrapper);
        creditReportClientItemService.remove(id, null);
    }

    public CreditReportDetailDTO detail(Long id) {
        CreditReportDetailDTO creditReportDetailDTO = new CreditReportDetailDTO();
        CreditReportBaseInfo reportDO = this.getById(id);
        List<CreditReportClientItem> clientItems = creditReportClientItemService.list(Wrappers.<CreditReportClientItem>lambdaQuery().eq(CreditReportClientItem::getCreditReportBaseInfoId, id));
        creditReportDetailDTO
                .setCreditCode(reportDO.getCreditCode())
                .setId(reportDO.getId()).setProjName(reportDO.getProjName()).setProjCode(reportDO.getProjCode()).setSelectVersion(reportDO.getSelectVersion()).setReportFormat(reportDO.getReportFormat())
                .setAuditStatus(reportDO.getApplyStatus())
                .setSearchStatus(reportDO.getSelectStatus())
                .setAuthorizationBeganDate(reportDO.getAuthorizationBeganDate())
                .setAuthorizationEndDate(reportDO.getAuthorizationEndDate());
        List<CreditReportClientInfo> clientInfos = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(clientItems)) {
            CreditReportClientItem clientItem = clientItems.get(0);
            creditReportDetailDTO.setClientId(clientItem.getClientId());
            creditReportDetailDTO.setClientName(clientItem.getClientName());
            creditReportDetailDTO.setCscCode(clientItem.getCscCode());
            creditReportDetailDTO.setSelectGoal(clientItem.getSelectGoal());
            clientInfos = clientItems.stream().map(item -> {
                return new CreditReportClientInfo().setReportId(item.getCreditReportBaseInfoId()).setId(item.getId()).setClientId(item.getClientId()).setClientName(item.getClientName()).setCscCode(item.getCscCode()).setZhongZhengCode(item.getZhongZhengCode()).setSelectGoal(item.getSelectGoal());
            }).collect(Collectors.toList());
        }
        creditReportDetailDTO.setClientInfos(clientInfos);
        return creditReportDetailDTO;
    }

    public List<CreditReportSubmitDTO> submit(CreditReportSubmitCmd cmd) {
        CreditReportBaseInfo creditReport = this.getById(cmd.getId());
        if (isNull(creditReport)) {
            throw new MithrasException(RECORD_NOT_EXIST);
        }
        ProcessResp relatedProcess = findRelatedProcess(cmd.getId());
        if (ObjectUtil.isNotEmpty(relatedProcess)) {
            ProcessModelTypeEnum modelTypeEnum = ProcessModelTypeEnum.valueOf(relatedProcess.getModelKey());
            throw new MithrasException(String.format("已处于'%s'中，提交审批失败", modelTypeEnum.getDisplay()));
        }
        //查询客户数据
        List<CreditReportClientItem> clientItems = creditReportClientItemService.listByBaseInfoId(cmd.getId());
        //校验企业资料是否上传
        Map<String, List<String>> map = checkEnterprise(clientItems, CreditReportMaterialTypeEnum.ENTERPRISE_CREDIT_REPORT.name(), BusinessModuleEnum.CREDIT_REPORT_SELECT.name());
        if (map.size() > 0) {
            List<CreditReportSubmitDTO> list = new ArrayList<>();
            map.forEach((k,v) -> {
                CreditReportSubmitDTO dto = new CreditReportSubmitDTO();
                dto.setClientName(k);
                dto.setCreditReportFiles(new ArrayList<>());
                v.forEach(e -> {
                    dto.getCreditReportFiles().add(new CreditReportFileDTO(e));
                });
                list.add(dto);
            });
            return list;
        }

        creditReport.setApplyStatus(ProcessState.COMMIT.name());
        creditReportBaseInfoMapper.updateById(creditReport);

        submitOne(creditReport.getId());
        return new ArrayList<>();
    }

    public void submitOne(Long id) {
        StartProcessReq startProcessReq = new StartProcessReq();
        startProcessReq.setModelKey(ProcessModelTypeEnum.CreditReportSelectFlow.name());
        startProcessReq.setStartUserId(Optional.ofNullable(AccountUtil.getLoginInfo()).map(AccountVO::getId).map(String::valueOf).orElseThrow(() -> new MithrasException(ResultMsg.USER_NOT_LOGIN)));
        startProcessReq.setBusinessKey(String.valueOf(id));
        startProcessReq.setProcessInstanceName(String.format("%s征信报告查询", LocalDateTimeUtil.format(LocalDate.now(), "yyyyMMdd")));
        startProcessReq.setStartUserDeptId(Optional.ofNullable(sysUserService.getUserDept()).map(OrgDO::getId).map(Objects::toString).orElse(""));
        processApiService.start(startProcessReq);
    }

    public Map<String, List<String>> checkEnterprise(List<CreditReportClientItem> clientItems, String materialsType, String businessType) {
        if (ObjectUtil.isEmpty(clientItems)) {
            return null;
        }
        Map<String, List<String>> map = new HashMap<>();
        List<CreditReportSubmitDTO> submits = new ArrayList<>();
        LambdaQueryWrapper<MaterialsList> wrapper = Wrappers.<MaterialsList>lambdaQuery().eq(MaterialsList::getMaterialsType, materialsType).eq(MaterialsList::getBusinessType, businessType)
                .in(MaterialsList::getBelongId, clientItems.stream().map(CreditReportClientItem::getId).collect(Collectors.toList()));
        List<MaterialsList> existingMaterials = materialsListMapper.selectList(wrapper);
        if(ObjectUtil.isEmpty(existingMaterials)) {
            throw new MithrasException("未上传企业资料");
        }
        Map<Long, List<MaterialsList>> id2List = existingMaterials.stream().collect(Collectors.groupingBy(MaterialsList::getBelongId));
        // 提取已存在的 materialSubType 集合
        Set<String> existingSubTypes;
        // 获取所有需要的子类型
        List<CreditReportMaterialSubTypeEnum> requiredSubTypes = CreditReportMaterialSubTypeEnum.listSub(CreditReportMaterialTypeEnum.ENTERPRISE_CREDIT_REPORT);
        List<String> targetList;
        for (CreditReportClientItem clientItem : clientItems) {
            targetList = new ArrayList<>();
            List<MaterialsList> materialsLists = id2List.get(clientItem.getId());
            if (ObjectUtil.isEmpty(materialsLists)) {
                targetList.addAll(requiredSubTypes.stream().map(CreditReportMaterialSubTypeEnum::getDisplay).collect(Collectors.toList()));
                continue;
            }
            materialsLists.stream().collect(Collectors.groupingBy(MaterialsList::getMaterialSubType))
                    .forEach((k, v) -> {
                        if (ObjectUtil.isNotEmpty(v) && v.size() > 1) {
                            throw new MithrasException("每个资料项下只能上传一份文件");
                        }
                    });
            existingSubTypes = materialsLists.stream().map(MaterialsList::getMaterialSubType).collect(Collectors.toSet());
            // 遍历所有应有的子类型
            for (CreditReportMaterialSubTypeEnum item : requiredSubTypes) {
                if (!existingSubTypes.contains(item.name())) {
                    targetList.add(item.getDisplay());
                }
            }
            if (targetList.size() > 0) {
                map.put(clientItem.getClientName(), targetList);
            }
        }
        return map;
    }

    public ProcessResp findRelatedProcess(Long guidanceId) {
        ProcessPageReq processPageReq = new ProcessPageReq();
        processPageReq.setPageIndex(1);
        processPageReq.setPageSize(1);
        processPageReq.setBusinessKey(String.valueOf(guidanceId));
        processPageReq.setModelKeyList(BusinessModuleEnum.CREDIT_REPORT_SELECT.getModelKeyList());
        processPageReq.setProcessStatusList(Arrays.asList(ProcessBusinessStatusEnum.RUNNING.getType(), ProcessBusinessStatusEnum.SUSPEND.getType()));
        cn.zswltech.flow.core.util.Page<ProcessResp> processRespPage = taskApiService.queryProcess(processPageReq);
        return processRespPage.getContents().stream().findFirst().orElse(null);
    }

    //调用信加接口
    public void remoteXJManage(Long id) {
        //查询所有客户
        List<CreditReportClientItem> clientItems = creditReportClientItemService.listByBaseInfoId(id);
        if (ObjectUtil.isEmpty(clientItems)) {
            return;
        }
        List<Long> clientIds = clientItems.stream().map(CreditReportClientItem::getClientId).collect(Collectors.toList());
        //Map<Long, Boolean> clientNeedArchive = creditReportClientItemService.needAddArchive(clientIds, id);

        //需要添加档案客户
        clientItems.forEach(clientItem -> {
            //这里要求每次都带档案编号，故每次查询均添加档案
           /* if (clientNeedArchive.getOrDefault(clientItem.getClientId(), Boolean.TRUE)) {
            }*/
            try {
                creditReportApiService.addArchive(clientItem.getId());
            } catch (Exception e) {
                log.warn("信加注册档案异常", e);
            }
        });
        //查询客户
        clientItems.forEach(clientItem -> {
            creditReportApiService.queryReport(clientItem.getId());
        });
    }

    @Transactional(rollbackFor = Throwable.class)
    public void modifySelectStatus(Long id) {
        List<CreditReportClientItem> clientItems = creditReportClientItemService.listByBaseInfoId(id);
        if (ObjectUtil.isEmpty(clientItems)) {
            return;
        }
        String status = CreditSearchStatusEnum.SUCCESS.name();
        for(CreditReportClientItem item: clientItems) {
            if (CreditSearchStatusEnum.FAIL.name().equals(item.getSelectStatus())) {
                status = item.getSelectStatus();
                break;
            }
            if (CreditSearchStatusEnum.SEARCHING.name().equals(item.getSelectStatus())) {
                status = item.getSelectStatus();
            }
        }
        LambdaUpdateWrapper<CreditReportBaseInfo> updateWrapper = new LambdaUpdateWrapper<CreditReportBaseInfo>();
        updateWrapper.set(CreditReportBaseInfo::getSelectStatus, status);
        updateWrapper.eq(CreditReportBaseInfo::getId, id);
        creditReportBaseInfoMapper.update(null, updateWrapper);
    }

    @Transactional(rollbackFor = Throwable.class)
    public boolean importByXJCreditReportJsonDTO(XJCreditReportJsonDTO xjCreditReportJsonDTO, Long creditReportBaseId, Long creditReportClienntId) {
        if (ObjectUtil.isEmpty(xjCreditReportJsonDTO)) {
            return false;
        }
        for(CreditReportParseInterface reportParseInterface : creditReportParseInterfaces){
            reportParseInterface.clear(creditReportClienntId);
            reportParseInterface.execute(xjCreditReportJsonDTO, creditReportBaseId, creditReportClienntId);
        }
        return true;
    }

    @Transactional(rollbackFor = Throwable.class)
    public boolean importByXJCreditReportObtainResultPDFResp(CreditReportObtainResultPDFResp creditReportObtainResultPDFResp, Long creditClientId) {
        CreditReportClientItem reportClientItem = creditReportClientItemService.getById(creditClientId);
        if (ObjectUtil.isEmpty(creditReportObtainResultPDFResp) || ObjectUtil.isEmpty(creditReportObtainResultPDFResp.getPdf()) || ObjectUtil.isEmpty(reportClientItem)) {
            return false;
        }
        // 查询一下信息备用
        CreditReportBaseInfo creditReportBaseInfo = this.getById(reportClientItem.getCreditReportBaseInfoId());
        try {
            InputStream inputStream = creditReportObtainResultPDFResp.getMultipartFile().getInputStream();
//            InputStream inputStream = FileUtil.getInputStream("/Users/dingqi/test.pdf");
            // 报告名称改成客户全称+查询日期
            LocalDate ld;
            if (Objects.nonNull(creditReportBaseInfo) && Objects.nonNull(creditReportBaseInfo.getApplyTime())) {
                ld = creditReportBaseInfo.getApplyTime().toLocalDate();
            } else {
                ld = LocalDate.now();
            }
            String fileName = reportClientItem.getClientName() + "_" + LocalDateTimeUtil.format(ld, DatePattern.NORM_DATE_PATTERN) + GlobalConstants.OFFICE_PDF_SUFFIX;
            // 查询并删除老的文件记录
            List<MaterialsList> oldList = materialsListService.list(BusinessModuleEnum.CREDIT_REPORT_SELECT.name(), Collections.singletonList(CreditReportMaterialTypeEnum.CLIENT_CREDIT_REPORT.name()), Collections.singletonList(creditClientId));
            if (CollectionUtil.isNotEmpty(oldList)) {
                materialsListService.removeByIds(oldList.stream().map(MaterialsList::getId).collect(Collectors.toSet()));
            }
            materialsListService.add(inputStream, fileName, creditClientId, CreditReportMaterialTypeEnum.CLIENT_CREDIT_REPORT.name(), BusinessModuleEnum.CREDIT_REPORT_SELECT.name());
        } catch (Exception e) {
            log.warn("获取信加pdf失败 {}", creditClientId, e);
            return false;
        }
        return true;

    }

    @Transactional(rollbackFor = Throwable.class)
    public void modifyClientItemStatus(List<Long> clientItemIds, String selectStatus){
        if (ObjectUtil.isEmpty(clientItemIds) || ObjectUtil.isEmpty(selectStatus)) {
            return;
        }
        creditReportClientItemService.modifySelectStatus(clientItemIds, selectStatus);
        List<CreditReportClientItem> clientItems = creditReportClientItemService.listByIds(clientItemIds);
        List<Long> creditReportBaseInfoIds = clientItems.stream().map(CreditReportClientItem::getCreditReportBaseInfoId).collect(Collectors.toList());
        Map<Long, List<CreditReportClientItem>> reportId2ClientItem = creditReportClientItemService.list(Wrappers.<CreditReportClientItem>lambdaQuery()
                .in(CreditReportClientItem::getCreditReportBaseInfoId, creditReportBaseInfoIds)).stream()
                .collect(Collectors.groupingBy(CreditReportClientItem::getCreditReportBaseInfoId));
        List<CreditReportBaseInfo> creditReportBaseInfos = this.listByIds(creditReportBaseInfoIds);
        List<CreditReportClientItem> clientItems1;
        String status;
        LocalDateTime now = LocalDateTime.now();
        for (CreditReportBaseInfo creditReportBaseInfo: creditReportBaseInfos) {
            status = CreditSearchStatusEnum.SUCCESS.name();
            clientItems1 = reportId2ClientItem.get(creditReportBaseInfo.getId());
            if (ObjectUtil.isEmpty(clientItems1)) {
                continue;
            }
            for(CreditReportClientItem item: clientItems1) {
                if (CreditSearchStatusEnum.FAIL.name().equals(item.getSelectStatus())) {
                    status = item.getSelectStatus();
                    break;
                }
                if (CreditSearchStatusEnum.SEARCHING.name().equals(item.getSelectStatus())) {
                    status = item.getSelectStatus();
                }
            }
            creditReportBaseInfo.setSelectStatus(status);
            creditReportBaseInfo.setSelectTime(now);
        }
        SpringContextHolder.getBean(CreditReportBaseInfoService.class).updateBatchById(creditReportBaseInfos);
    }


    public List<ClientInfo> getClientInfo(String clientName, Long creditReportId) {
        //获取当前登录用户id
        AccountVO loginInfo = AccountUtil.getLoginInfo();
        Long userId = loginInfo.getId();
        List<UserOrgJobDO> userOrgJobList = SpringUtil.getBean(UserOrgJobDOMapper.class).selectJobCodeByUserId(Collections.singletonList(userId));
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
        List<ClientInfo> clientInfoList = new ArrayList<>();
        // 中后台看所有
        if (CollectionUtil.isEmpty(projmanagerList) && CollectionUtil.isEmpty(businessheadList) && CollectionUtil.isEmpty(leaderinchargeList)) {
            clientInfoList = creditReportBaseInfoMapper.selectClientInfoAll(clientName);
        } else {
            clientInfoList = creditReportBaseInfoMapper.selectClientInfo(clientName, bizDeptIds, userId);
        }
        // 去重
        Map<Long, ClientInfo> map = clientInfoList.stream().collect(Collectors.toMap(ClientInfo::getClientId, e -> e, (a, b) -> b));
        clientInfoList = new LinkedList<>(map.values());
        if (Objects.nonNull(creditReportId)) {
            CreditReportBaseInfo creditReportDO = this.getById(creditReportId);
            if (StrUtil.equals(creditReportDO.getProjIdDataType(), BusinessModuleEnum.PROJ_ESTABLISH.name())) {
                // 指定id的情况只查询对应项目下的客户
                List<Long> projClientIds = SpringUtil.getBean(ProjEstablishTradeStructureService.class).listClientIdsByProjEstablishId(creditReportDO.getProjId());
                clientInfoList.removeIf(e -> !projClientIds.contains(e.getClientId()));
            }
            if (StrUtil.equals(creditReportDO.getProjIdDataType(), BusinessModuleEnum.PROJ_REVIEW.name())) {
                // 指定id的情况只查询对应项目下的客户
                List<Long> projClientIds = SpringUtil.getBean(ProjReviewTradeStructureService.class).listClientIdsByProjReviewId(creditReportDO.getProjId());
                clientInfoList.removeIf(e -> !projClientIds.contains(e.getClientId()));
            }
            if (StrUtil.equals(creditReportDO.getProjIdDataType(), BusinessModuleEnum.PAYMENT.name())) {
                PaymentBaseInfo paymentBaseInfo = SpringUtil.getBean(PaymentBaseInfoService.class).getById(creditReportDO.getProjId());
                List<Long> projClientIds = SpringUtil.getBean(ContractTradeStructureService.class).listClientIdsByContractId(paymentBaseInfo.getContractId());
                clientInfoList.removeIf(e -> !projClientIds.contains(e.getClientId()));
            }
            if (StrUtil.equalsAny(creditReportDO.getProjIdDataType(), BusinessModuleEnum.GROUP_CREDIT_ESTABLISH.name(), BusinessModuleEnum.GROUP_CREDIT_REVIEW.name())) {
                // 授信立项/评审只会有一个客户，从业务流程上来说一定是创建时候选择的那个，不会有其他选项，无需返回可选客户
                return Collections.emptyList();
            }
        }
        return clientInfoList;
    }

    public CreditReportClientAddDTO showCreditReportByClientId(Long clientId) {
        CreditReportClientAddDTO addRsp = new CreditReportClientAddDTO();
        //获取统一社会信用代码和客户名称
        Client client = clientService.getById(clientId);
        Optional.ofNullable(client).ifPresent(c -> addRsp.setCscCode(c.getUscCode()).setClientName(c.getClientName()).setClientId(c.getId()));
        //获取项目信息
//        ClientLifeCycleDetailReq req = new ClientLifeCycleDetailReq();
//        req.setClientId(clientId);
//        List<ClientLifeCycleProjectListRsp> projInfoList = clientLifeCycleService.projectList(req);
//        List<CreditReportProjectInfo> projectInfos = Optional.ofNullable(projInfoList).
//                orElse(Collections.emptyList()).stream()
//                .map(proj -> new CreditReportProjectInfo()
//                        .setProjId(proj.getProjectId())
//                        .setProjIdDataType(proj.getDataType())
//                        .setProjName(proj.getProjectName())
//                        .setProjCode(proj.getProjectCode()))
//                .collect(Collectors.toList());
        List<CreditReportProjectInfo> projectInfos = this.findProjectInfoByClientId(clientId);
        addRsp.setProjectInfos(projectInfos);
        //获取中征码
        CorpCommerceInfo corpCommerceInfo = corpCommerceInfoMapper.selectOne(Wrappers.<CorpCommerceInfo>lambdaQuery().eq(CorpCommerceInfo::getClientId, clientId));
        Optional.ofNullable(corpCommerceInfo).map(CorpCommerceInfo::getZhongZhengCode).filter(StringUtils::isNotBlank).ifPresent(addRsp::setZhongZhengCode);

        addRsp.setReportFormat(CreditReportConstants.REPORT_FORMAT);
        addRsp.setSelectVersion(CreditReportConstants.SELECT_VERSION);
        return addRsp;
    }

    private List<CreditReportProjectInfo> findProjectInfoByClientId(Long clientId) {
        // 查询项目评审
        /*Set<Long> projReviewIds = SpringUtil.getBean(ProjReviewTradeStructureService.class).listProjReviewIdsByClientId(clientId);
        List<ProjReviewBaseInfo> projReviewBaseInfoList;
        if (CollectionUtil.isEmpty(projReviewIds)) {
            projReviewBaseInfoList = Collections.emptyList();
        } else {
            projReviewBaseInfoList = SpringUtil.getBean(ProjReviewBaseInfoService.class).listByIds(projReviewIds);
        }*/
        // 查询项目立项
        Set<Long> projEstablishIds = SpringUtil.getBean(ProjEstablishTradeStructureService.class).listProjEstablishIdsByClientId(clientId);
        List<ProjEstablishBaseInfo> projEstablishBaseInfoList;
        if (CollectionUtil.isEmpty(projEstablishIds)) {
            projEstablishBaseInfoList = Collections.emptyList();
        } else {
            projEstablishBaseInfoList = SpringUtil.getBean(ProjEstablishBaseInfoService.class).listByIds(projEstablishIds);
        }
        // 查询授信评审
        //List<GroupCreditReviewBaseInfo> groupReviewList = SpringUtil.getBean(GroupCreditReviewBaseInfoService.class).list(Wrappers.<GroupCreditReviewBaseInfo>lambdaQuery().eq(GroupCreditReviewBaseInfo::getClientId, clientId));
        // 查询授信立项
        List<GroupCreditEstablishBaseInfo> groupEstablishList = SpringUtil.getBean(GroupCreditEstablishBaseInfoService.class).list(Wrappers.<GroupCreditEstablishBaseInfo>lambdaQuery().eq(GroupCreditEstablishBaseInfo::getClientId, clientId));
        // 去重合并成唯一的项目信息
        List<CreditReportProjectInfo> result = new LinkedList<>();
        Set<Long> ignoreProjEstablishIds = new HashSet<>();
        Set<Long> ignoreGroupReviewIds = new HashSet<>();
        Set<Long> ignoreGroupEstablishIds = new HashSet<>();
        /*for (ProjReviewBaseInfo projReviewBaseInfo : projReviewBaseInfoList) {
            result.add(new CreditReportProjectInfo().setProjId(projReviewBaseInfo.getId()).setProjIdDataType(BusinessModuleEnum.PROJ_REVIEW.name()).setProjCode(projReviewBaseInfo.getProjCode()).setProjName(projReviewBaseInfo.getProjName()));
            if (Objects.nonNull(projReviewBaseInfo.getProjEstablishId())) {
                ignoreProjEstablishIds.add(projReviewBaseInfo.getProjEstablishId());
            }
            if (Objects.nonNull(projReviewBaseInfo.getGroupCreditReviewId())) {
                ignoreGroupReviewIds.add(projReviewBaseInfo.getGroupCreditReviewId());
            }
        }*/
        for (ProjEstablishBaseInfo projEstablishBaseInfo : projEstablishBaseInfoList) {
            if (ignoreProjEstablishIds.contains(projEstablishBaseInfo.getId())) {
                continue;
            }
            result.add(new CreditReportProjectInfo().setProjId(projEstablishBaseInfo.getId()).setProjIdDataType(ReviewRelationDataType.PROJ_ESTABLISH.name()).setProjCode(projEstablishBaseInfo.getProjCode()).setProjName(projEstablishBaseInfo.getProjName()));
        }
/*
        for (GroupCreditReviewBaseInfo groupCreditReviewBaseInfo : groupReviewList) {
            if (ignoreGroupReviewIds.contains(groupCreditReviewBaseInfo.getId())) {
                continue;
            }
            ignoreGroupEstablishIds.add(groupCreditReviewBaseInfo.getGroupCreditEstablishId());
            result.add(new CreditReportProjectInfo().setProjId(groupCreditReviewBaseInfo.getId()).setProjIdDataType(BusinessModuleEnum.GROUP_CREDIT_REVIEW.name()).setProjCode(groupCreditReviewBaseInfo.getProjCode()).setProjName(groupCreditReviewBaseInfo.getProjName()));
        }
*/
        for (GroupCreditEstablishBaseInfo groupCreditEstablishBaseInfo : groupEstablishList) {
            if (ignoreGroupEstablishIds.contains(groupCreditEstablishBaseInfo.getId())) {
                continue;
            }
            result.add(new CreditReportProjectInfo().setProjId(groupCreditEstablishBaseInfo.getId()).setProjIdDataType(ReviewRelationDataType.GROUP_CREDIT_REVIEW.name()).setProjCode(groupCreditEstablishBaseInfo.getProjCode()).setProjName(groupCreditEstablishBaseInfo.getProjName()));
        }
        return result;
    }

    public void export(ServletOutputStream outputStream, CreditReportListREQ req) {
//        PageR<CreditReportListDTO> result = creditReportItemService.selectList(req);
        req.setPage(1);
        req.setPageSize(5000);
        PageR<CreditReportListDTO> result = this.list(req);
        if (CollectionUtil.isEmpty(result.getList())) {
            throw new MithrasException("暂无导出数据");
        }
        // 查询明细
        List<CreditReportClientItem> itemList =
                creditReportClientItemService.list(Wrappers.<CreditReportClientItem>lambdaQuery().in(CreditReportClientItem::getCreditReportBaseInfoId, result.getList().stream().map(CreditReportListDTO::getId).collect(Collectors.toSet())).orderByDesc(CreditReportClientItem::getCreditReportBaseInfoId));
        Map<Long, List<CreditReportClientItem>> itemMap = itemList.stream().collect(Collectors.groupingBy(CreditReportClientItem::getCreditReportBaseInfoId));
        // 按客户拆行
        List<CreditSearchExcelModel> excelModelList = new LinkedList<>();
        for (CreditReportListDTO dto : result.getList()) {
            List<CreditReportClientItem> itemDOList = itemMap.get(dto.getId());
            if (CollectionUtil.isEmpty(itemDOList)) {
                continue;
            }
            for (CreditReportClientItem creditReportItemDO : itemDOList) {
                CreditSearchExcelModel creditSearchExcelModel = CreditSearchConvert.creditSearchListRSPExcelModel(dto);
                creditSearchExcelModel.setClientName(creditReportItemDO.getClientName());
                creditSearchExcelModel.setCscCode(creditReportItemDO.getCscCode());
                SearchGoalEnum searchGoalEnum = SearchGoalEnum.finaByName(creditReportItemDO.getSelectGoal());
                creditSearchExcelModel.setSearchReason(Optional.ofNullable(searchGoalEnum).map(SearchGoalEnum::display).orElse("未知"));
                excelModelList.add(creditSearchExcelModel);
            }
        }
//        List<CreditSearchExcelModel> excelModelList = result.getList().stream().map(CreditSearchConvert::creditSearchListRSPExcelModel).collect(Collectors.toList());
        creditSearchListExcelExporter.exportExcel(excelModelList, outputStream);
    }




}
