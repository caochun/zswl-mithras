package cn.zswltech.mithras.creditreport.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
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
import cn.zswltech.gruul.dao.dal.entity.OrgDO;
import cn.zswltech.gruul.dao.dal.vo.AccountVO;
import cn.zswltech.mithras.dto.creditreport.CreditReportAddCmd;
import cn.zswltech.mithras.dto.creditreport.CreditReportClientAddDTO;
import cn.zswltech.mithras.dto.creditreport.CreditReportClientInfo;
import cn.zswltech.mithras.dto.creditreport.CreditReportDetailDTO;
import cn.zswltech.mithras.dto.creditreport.CreditReportFileDTO;
import cn.zswltech.mithras.dto.creditreport.CreditReportListDTO;
import cn.zswltech.mithras.dto.creditreport.CreditReportListREQ;
import cn.zswltech.mithras.dto.creditreport.CreditReportProjectInfo;
import cn.zswltech.mithras.dto.creditreport.CreditReportSaveCmd;
import cn.zswltech.mithras.dto.creditreport.CreditReportSubmitCmd;
import cn.zswltech.mithras.dto.creditreport.CreditReportSubmitDTO;
import cn.zswltech.mithras.dto.creditreport.CreditSearchCompareBusinessDTO;
import cn.zswltech.mithras.foundation.constant.GlobalConstants;
import cn.zswltech.mithras.foundation.constant.ResultMsg;
import cn.zswltech.mithras.foundation.constant.VersionTypeConstants;
import cn.zswltech.mithras.creditreport.enums.CreditReportBusinessModule;
import cn.zswltech.mithras.workflow.flow.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.foundation.enums.VersionTypeEnum;
import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.customer.enums.client.ClientType;
import cn.zswltech.mithras.creditreport.enums.CreditReportMaterialSubTypeEnum;
import cn.zswltech.mithras.creditreport.enums.CreditReportMaterialTypeEnum;
import cn.zswltech.mithras.workflow.flow.enums.ProcessState;
import cn.zswltech.mithras.projectprocess.enums.projreview.ReviewRelationDataType;
import cn.zswltech.mithras.creditreport.excel.exporter.CreditSearchListExcelExporter;
import cn.zswltech.mithras.document.mapper.MaterialsListMapper;
import cn.zswltech.mithras.customer.mapper.client.ClientMapper;
import cn.zswltech.mithras.customer.mapper.corp.CorpCommerceInfoMapper;
import cn.zswltech.mithras.customer.mapper.corp.CorpShareholderInfoMapper;
import cn.zswltech.mithras.document.model.MaterialsList;
import cn.zswltech.mithras.customer.model.client.Client;
import cn.zswltech.mithras.customer.model.client.CorpCommerceInfo;
import cn.zswltech.mithras.customer.model.client.CorpShareholderInfo;
import cn.zswltech.mithras.creditreport.model.CreditReportBaseInfo;
import cn.zswltech.mithras.creditreport.model.CreditReportClientItem;
import cn.zswltech.mithras.credit.groupcredit.establish.mapper.GroupCreditEstablishBaseInfoMapper;
import cn.zswltech.mithras.credit.groupcredit.establish.model.GroupCreditEstablishBaseInfo;
import cn.zswltech.mithras.projectprocess.mapper.projestablish.ProjEstablishBaseInfoMapper;
import cn.zswltech.mithras.projectprocess.model.projestablish.ProjEstablishBaseInfo;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.foundation.context.SpringContextHolder;
import cn.zswltech.mithras.system.user.Id2NameService;
import cn.zswltech.mithras.system.user.SysUserService;
import cn.zswltech.mithras.customer.application.client.bo.ClientBusinessHistoryBO;
import cn.zswltech.mithras.customer.application.client.ClientBusinessHistoryService;
import cn.zswltech.mithras.contract.core.ContractTradeStructureService;
import cn.zswltech.mithras.creditreport.service.CreditReportBaseInfoService;
import cn.zswltech.mithras.creditreport.service.CreditReportClientItemService;
import cn.zswltech.mithras.creditreport.service.CreditReportClientSupportPort;
import cn.zswltech.mithras.creditreport.service.CreditReportService;
import cn.zswltech.mithras.projectprocess.application.projestablish.ProjEstablishTradeStructureService;
import cn.zswltech.mithras.customer.application.client.dto.MithrasShareholderInfo;
import cn.zswltech.mithras.foundation.util.CompareUtil;
import cn.zswltech.mithras.foundation.util.LongUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.bitbucket.cowwoc.diffmatchpatch.DiffMatchPatch;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.annotation.Resource;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CreditReportSeriveImpl implements CreditReportService {


    /*@Resource
    private CreditReportItemService creditReportItemService;*/

    @Resource
    private CreditReportClientItemService creditReportClientItemService;

    @Resource
    private ProjEstablishBaseInfoMapper projEstablishBaseInfoMapper;
    @Resource
    private GroupCreditEstablishBaseInfoMapper groupCreditEstablishBaseInfoMapper;

    //审批流相关
    @Resource
    private FlowTaskApiService taskApiService;

    @Resource
    private FlowProcessApiService processApiService;

    @Resource
    private Id2NameService id2NameService;

    @Resource
    private SysUserService sysUserService;

    //客户相关
    @Resource
    private CreditReportVersionService creditReportVersionService;

    @Resource
    private ClientMapper clientMapper;
    @Resource
    private CreditReportClientSupportPort creditReportClientSupportPort;

    @Resource
    private CorpCommerceInfoMapper corpCommerceInfoMapper;

    @Resource
    private CorpShareholderInfoMapper corpShareholderInfoMapper;

    @Resource
    private ClientBusinessHistoryService clientBusinessHistoryService;

    @Resource
    private MaterialsListMapper materialsListMapper;

    @Resource
    private CreditSearchListExcelExporter creditSearchListExcelExporter;

    @Resource
    private CreditReportBaseInfoService creditReportBaseInfoService;

   /* @Override
    @Transactional(rollbackFor = Exception.class)
    public void add(CreditReportAddCmd cmd) {
        CreditReportDO creditReportDO = new CreditReportDO();
        BeanUtils.copyProperties(cmd, creditReportDO);
        AccountVO loginInfo = AccountUtil.getLoginInfo();
        Long userId = loginInfo.getId();
        creditReportDO.setApplyUser(userId);
        OrgDO orgDO = sysUserService.getUserDeptList().get(0);
        creditReportDO.setApplyOrg(orgDO.getId());
        creditReportDO.setApplyStatus(ProcessState.UN_SUBMIT.name());
        String creditCode = generateCreditCodeSimple();
        creditReportDO.setCreditCode(creditCode);
        creditReportDOMapper.insert(creditReportDO);
        CreditReportItemDO creditReportItemDO = new CreditReportItemDO();
        BeanUtils.copyProperties(creditReportDO, creditReportItemDO);
        creditReportItemDO.setId(null);
        creditReportItemDO.setCreditReportId(creditReportDO.getId());
        creditReportItemService.save(creditReportItemDO);
    }

    //唯一的编号
    public String generateCreditCodeSimple() {
        DateTimeFormatter ofPattern = DateTimeFormatter.ofPattern("yyyyMMdd");
        String localDate = LocalDateTime.now().format(ofPattern);

        CreditReportDO todayLastRecord = creditReportDOMapper.selectOne(Wrappers.<CreditReportDO>lambdaQuery().ge(CreditReportDO::getCreateTime, LocalDateTimeUtil.beginOfDay(LocalDateTime.now())).le(CreditReportDO::getCreateTime, LocalDateTimeUtil.endOfDay(LocalDateTime.now())).orderByDesc(CreditReportDO::getCreateTime).last("LIMIT 1"));

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

    @Override
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
            clientInfoList = creditReportDOMapper.selectClientInfoAll(clientName);
            // 去重
            Map<Long, ClientInfo> map = clientInfoList.stream().collect(Collectors.toMap(ClientInfo::getClientId, e -> e, (a, b) -> b));
            clientInfoList = new LinkedList<>(map.values());
        } else {
            clientInfoList = creditReportDOMapper.selectClientInfo(clientName, bizDeptIds, userId);
        }
        if (Objects.nonNull(creditReportId)) {
            CreditReportDO creditReportDO = this.getById(creditReportId);
            if (StrUtil.equals(creditReportDO.getProjIdDataType(), CreditReportBusinessModule.PROJ_ESTABLISH.name())) {
                // 指定id的情况只查询对应项目下的客户
                List<Long> projClientIds = SpringUtil.getBean(ProjEstablishTradeStructureService.class).listClientIdsByProjEstablishId(creditReportDO.getProjId());
                clientInfoList.removeIf(e -> !projClientIds.contains(e.getClientId()));
            }
            if (StrUtil.equals(creditReportDO.getProjIdDataType(), CreditReportBusinessModule.PROJ_REVIEW.name())) {
                // 指定id的情况只查询对应项目下的客户
                List<Long> projClientIds = SpringUtil.getBean(ProjReviewTradeStructureService.class).listClientIdsByProjReviewId(creditReportDO.getProjId());
                clientInfoList.removeIf(e -> !projClientIds.contains(e.getClientId()));
            }
            if (StrUtil.equals(creditReportDO.getProjIdDataType(), CreditReportBusinessModule.PAYMENT.name())) {
                PaymentBaseInfo paymentBaseInfo = SpringUtil.getBean(PaymentBaseInfoService.class).getById(creditReportDO.getProjId());
                List<Long> projClientIds = SpringUtil.getBean(ContractTradeStructureService.class).listClientIdsByContractId(paymentBaseInfo.getContractId());
                clientInfoList.removeIf(e -> !projClientIds.contains(e.getClientId()));
            }
            if (StrUtil.equalsAny(creditReportDO.getProjIdDataType(), CreditReportBusinessModule.GROUP_CREDIT_ESTABLISH.name(), CreditReportBusinessModule.GROUP_CREDIT_REVIEW.name())) {
                // 授信立项/评审只会有一个客户，从业务流程上来说一定是创建时候选择的那个，不会有其他选项，无需返回可选客户
                return Collections.emptyList();
            }
        }
        return clientInfoList;
    }*/

    /*@Override
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
    }*/

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
            projEstablishBaseInfoList = projEstablishBaseInfoMapper.selectBatchIds(projEstablishIds);
        }
        // 查询授信评审
        //List<GroupCreditReviewBaseInfo> groupReviewList = SpringUtil.getBean(GroupCreditReviewBaseInfoService.class).list(Wrappers.<GroupCreditReviewBaseInfo>lambdaQuery().eq(GroupCreditReviewBaseInfo::getClientId, clientId));
        // 查询授信立项
        List<GroupCreditEstablishBaseInfo> groupEstablishList = groupCreditEstablishBaseInfoMapper.selectList(Wrappers.<GroupCreditEstablishBaseInfo>lambdaQuery().eq(GroupCreditEstablishBaseInfo::getClientId, clientId));
        // 去重合并成唯一的项目信息
        List<CreditReportProjectInfo> result = new LinkedList<>();
        Set<Long> ignoreProjEstablishIds = new HashSet<>();
        Set<Long> ignoreGroupReviewIds = new HashSet<>();
        Set<Long> ignoreGroupEstablishIds = new HashSet<>();
        /*for (ProjReviewBaseInfo projReviewBaseInfo : projReviewBaseInfoList) {
            result.add(new CreditReportProjectInfo().setProjId(projReviewBaseInfo.getId()).setProjIdDataType(CreditReportBusinessModule.PROJ_REVIEW.name()).setProjCode(projReviewBaseInfo.getProjCode()).setProjName(projReviewBaseInfo.getProjName()));
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
            result.add(new CreditReportProjectInfo().setProjId(groupCreditReviewBaseInfo.getId()).setProjIdDataType(CreditReportBusinessModule.GROUP_CREDIT_REVIEW.name()).setProjCode(groupCreditReviewBaseInfo.getProjCode()).setProjName(groupCreditReviewBaseInfo.getProjName()));
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

    /*@Override
    @Transactional(rollbackFor = Exception.class)
    public void saveCreditReport(CreditReportSaveCmd cmd) {
        //参数校验
        validateCommand(cmd);

        List<CreditReportClientInfo> clientInfos = cmd.getClientInfos();

        //获取登录信息
        AccountVO loginInfo = AccountUtil.getLoginInfo();
        Long userId = loginInfo.getId();
        OrgDO orgDO = sysUserService.getUserDeptList().get(0);

        //处理主客户
        CreditReportClientInfo primaryClient = clientInfos.get(0);
        updateCreditReportAndPrimaryItem(cmd, primaryClient, userId, orgDO);

        //处理附加客户
        if (clientInfos.size() > 1) {
            saveAdditionalClients(cmd, clientInfos, userId, orgDO);
        }
    }*/

    private void validateCommand(CreditReportSaveCmd cmd) {
        if (CollectionUtils.isEmpty(cmd.getClientInfos())) {
            throw new MithrasException("征信报告保存时客户信息不能为空");
        }
        if (cmd.getId() == null) {
            throw new MithrasException("征信报告ID不能为空");
        }
    }

    /*private void updateCreditReportAndPrimaryItem(CreditReportSaveCmd cmd,
                                                  CreditReportClientInfo client,
                                                  Long userId, OrgDO orgDO) {
        //查询征信报告
        CreditReportDO reportDO = creditReportDOMapper.selectById(client.getReportId());
        if (reportDO == null) {
            throw new MithrasException("征信报告不存在");
        }

        //删除征信报告详细表数据
        creditReportItemService.remove(Wrappers.<CreditReportItemDO>lambdaQuery().eq(CreditReportItemDO::getCreditReportId, cmd.getId()));

        // 更新征信报告主表
        updateCreditReportIfNeeded(reportDO, client);

        // 更新征信报告详细表
        CreditReportItemDO primaryItem = new CreditReportItemDO();
        primaryItem.setId(cmd.getId());
        copyClientInfoToItem(primaryItem, client);
        primaryItem.setApplyUser(userId);
        primaryItem.setApplyOrg(orgDO.getId());
        primaryItem.setProjId(cmd.getProjId());
        primaryItem.setProjName(cmd.getProjName());
        primaryItem.setProjCode(cmd.getProjCode());
        primaryItem.setSelectVersion(reportDO.getSelectVersion());
        primaryItem.setReportFormat(reportDO.getReportFormat());

        creditReportItemService.save(primaryItem);
    }

    private void updateCreditReportIfNeeded(CreditReportDO reportDO, CreditReportClientInfo client) {
        if (!Objects.equals(reportDO.getClientId(), client.getClientId())) {
            CreditReportDO updateDO = new CreditReportDO();
            updateDO.setId(client.getReportId());
            updateDO.setClientId(client.getClientId());
            updateDO.setClientName(client.getClientName());
            updateDO.setCscCode(client.getCscCode());
            updateDO.setZhongZhengCode(client.getZhongZhengCode());
            updateDO.setSelectGoal(client.getSelectGoal());

            creditReportDOMapper.updateById(updateDO);
        }
    }

    private void saveAdditionalClients(CreditReportSaveCmd cmd,
                                       List<CreditReportClientInfo> clientInfos,
                                       Long userId, OrgDO orgDO) {
        // 查询征信报告信息用于填充
        CreditReportDO reportDO = creditReportDOMapper.selectById(clientInfos.get(0).getReportId());

        //删除征信报告详细表数据
        creditReportItemService.remove(Wrappers.<CreditReportItemDO>lambdaQuery().eq(CreditReportItemDO::getCreditReportId, cmd.getId()));

        List<CreditReportItemDO> additionalItems = clientInfos.stream()
                .map(clientInfo -> createAdditionalItem(cmd, clientInfo, reportDO, userId, orgDO))
                .collect(Collectors.toList());

        creditReportItemService.saveBatch(additionalItems);
    }*/

    /*private CreditReportItemDO createAdditionalItem(CreditReportSaveCmd cmd,
                                                    CreditReportClientInfo clientInfo,
                                                    CreditReportDO reportDO,
                                                    Long userId, OrgDO orgDO) {
        CreditReportItemDO itemDO = new CreditReportItemDO();
        itemDO.setCreditReportId(cmd.getId());
        copyClientInfoToItem(itemDO, clientInfo);

        // 设置公共字段
        itemDO.setApplyUser(userId);
        itemDO.setApplyOrg(orgDO.getId());
        itemDO.setApplyStatus(ProcessState.UN_SUBMIT.name());
        itemDO.setCreditCode(reportDO.getCreditCode());
        itemDO.setProjId(reportDO.getProjId());
        itemDO.setProjName(reportDO.getProjName());
        itemDO.setProjCode(reportDO.getProjCode());
        itemDO.setProjIdDataType(reportDO.getProjIdDataType());
        itemDO.setReportFormat(reportDO.getReportFormat());
        itemDO.setSelectVersion(reportDO.getSelectVersion());

        return itemDO;
    }


    private void copyClientInfoToItem(CreditReportItemDO itemDO, CreditReportClientInfo clientInfo) {
        itemDO.setClientId(clientInfo.getClientId());
        itemDO.setClientName(clientInfo.getClientName());
        itemDO.setCscCode(clientInfo.getCscCode());
        itemDO.setZhongZhengCode(clientInfo.getZhongZhengCode());
        itemDO.setSelectGoal(clientInfo.getSelectGoal());
        itemDO.setCreditReportId(clientInfo.getReportId());
    }*/


    /*@Override
    @Transactional(rollbackFor=Exception.class)
    public List<CreditReportSubmitDTO> submit(CreditReportSubmitCmd cmd) {
        CreditReportDO creditReport = creditReportDOMapper.selectById(cmd.getId());
        if (isNull(creditReport)) {
            throw new MithrasException(RECORD_NOT_EXIST);
        }
        ProcessResp relatedProcess = findRelatedProcess(cmd.getId());
        if (ObjectUtil.isNotEmpty(relatedProcess)) {
            ProcessModelTypeEnum modelTypeEnum = ProcessModelTypeEnum.valueOf(relatedProcess.getModelKey());
            throw new MithrasException(String.format("已处于'%s'中，提交审批失败", modelTypeEnum.getDisplay()));
        }
        //校验企业资料是否上传
        List<CreditReportSubmitDTO> submitList = checkEnterprise(cmd.getId(), cmd.getClientId(), CreditReportMaterialTypeEnum.ENTERPRISE_CREDIT_REPORT.name(), CreditReportBusinessModule.CREDIT_REPORT_SELECT.name());
        for (CreditReportSubmitDTO submitDTO : submitList) {
            if (CollectionUtil.isNotEmpty(submitDTO.getCreditReportFiles())) {
                return submitList;
            }
        }
        creditReport.setApplyStatus(ProcessState.COMMIT.name());
        creditReportDOMapper.updateById(creditReport);
        // 查询并更新所有子项
        List<CreditReportItemDO> items = creditReportItemService.list(
                Wrappers.<CreditReportItemDO>lambdaQuery()
                        .eq(CreditReportItemDO::getCreditReportId, cmd.getId())
        );

        for (CreditReportItemDO item : items) {
            item.setApplyStatus(ProcessState.COMMIT.name());
        }
        // 批量更新子表
        creditReportItemService.saveOrUpdateBatch(items);
        submitOne(creditReport.getId());
        return new ArrayList<>();
    }*/

    private List<CreditReportSubmitDTO> checkEnterprise(Long id, List<Long> clientIds, String materialsType, String businessType) {
        List<CreditReportSubmitDTO> submits = new ArrayList<>();
        // 获取所有需要的子类型
        List<CreditReportMaterialSubTypeEnum> requiredSubTypes = CreditReportMaterialSubTypeEnum.listSub(CreditReportMaterialTypeEnum.ENTERPRISE_CREDIT_REPORT);
        for (Long clientId : clientIds) {
            CreditReportSubmitDTO creditReportSubmitDTO = new CreditReportSubmitDTO();
            String clientName = id2NameService.clientId2NameSingle(clientId);
            creditReportSubmitDTO.setClientName(clientName);
            LambdaQueryWrapper<MaterialsList> wrapper = Wrappers.<MaterialsList>lambdaQuery().eq(MaterialsList::getMaterialsType, materialsType).eq(MaterialsList::getBusinessType, businessType).eq(MaterialsList::getBelongId, id)
                    .eq(MaterialsList::getSourceBusinessKey, clientId);
            List<MaterialsList> existingMaterials = materialsListMapper.selectList(wrapper);

            // 提取已存在的 materialSubType 集合
            Set<String> existingSubTypes = existingMaterials.stream().map(MaterialsList::getMaterialSubType).collect(Collectors.toSet());

            List<CreditReportFileDTO> files = new ArrayList<>();
            // 遍历所有应有的子类型
            for (CreditReportMaterialSubTypeEnum item : requiredSubTypes) {
                if (!existingSubTypes.contains(item.name())) {
                    CreditReportFileDTO creditReportFileDTO = new CreditReportFileDTO();
                    creditReportFileDTO.setMaterialsTypeName(item.getDisplay());
                    files.add(creditReportFileDTO);
                }
            }
            if (CollectionUtil.isEmpty(files)) {
                continue;
            }
            creditReportSubmitDTO.setCreditReportFiles(files);
            submits.add(creditReportSubmitDTO);
        }
        return submits;
    }

   /* @Override
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
        List<CreditReportDO> credits = new ArrayList<>();
        Page<CreditReportDO> page = new Page<>(req.getPage(), req.getPageSize());
        // 中后台看所有
        if (CollectionUtil.isEmpty(projmanagerList) && CollectionUtil.isEmpty(businessheadList) && CollectionUtil.isEmpty(leaderinchargeList)) {
            credits = creditReportDOMapper.list(page, req);
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
            credits = creditReportDOMapper.list(page, req);
        }
        List<CreditReportListDTO> creditReportListDTOs = convertCreditReport(credits);
        return PageR.of(creditReportListDTOs, page.getTotal(), page.getCurrent(), page.getSize());
    }

    @Override
    public CreditReportDetailDTO detail(Long id) {
        CreditReportDetailDTO creditReportDetailDTO = new CreditReportDetailDTO();
        List<CreditReportItemDO> creditItems = creditReportItemService.list(Wrappers.<CreditReportItemDO>lambdaQuery().eq(CreditReportItemDO::getCreditReportId, id));
        CreditReportDO reportDO = creditReportDOMapper.selectById(id);
        creditReportDetailDTO
                .setCreditCode(reportDO.getCreditCode()).setClientId(reportDO.getClientId()).setClientName(reportDO.getClientName()).setCscCode(reportDO.getCscCode()).setZhongZhengCode(reportDO.getZhongZhengCode()).setSelectGoal(reportDO.getSelectGoal())
                .setId(reportDO.getId()).setProjName(reportDO.getProjName()).setProjCode(reportDO.getProjCode()).setSelectVersion(reportDO.getSelectVersion()).setReportFormat(reportDO.getReportFormat())
                .setAuditStatus(reportDO.getApplyStatus())
                .setSearchStatus(reportDO.getSelectStatus());
        List<CreditReportClientInfo> clientInfos = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(creditItems)) {
            clientInfos = creditItems.stream().map(item -> {
                return new CreditReportClientInfo().setReportId(item.getCreditReportId()).setId(item.getId()).setClientId(item.getClientId()).setClientName(item.getClientName()).setCscCode(item.getCscCode()).setZhongZhengCode(item.getZhongZhengCode()).setSelectGoal(item.getSelectGoal());
            }).collect(Collectors.toList());
        }
        creditReportDetailDTO.setClientInfos(clientInfos);
        return creditReportDetailDTO;
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void delete(Long id) {
        //查询征信查询数据是否存在
        CreditReportDO reportDO = creditReportDOMapper.selectById(id);
        if (Objects.isNull(reportDO)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        if (!ProcessState.UN_SUBMIT.name().equals(reportDO.getApplyStatus())) {
            throw new MithrasException("征信查询申请状态不是待提交，无法删除");
        }
        //删除征信查询主表
        creditReportDOMapper.deleteById(id);
        //删除征信查询详细表
        creditReportItemService.remove(Wrappers.<CreditReportItemDO>lambdaQuery()
                .eq(CreditReportItemDO::getCreditReportId, id)
        );
    }*/

    @Override
    public List<CreditSearchCompareBusinessDTO> compareBusiness(Long creditSearchId, Boolean isHistory) {
        //查询历史数据比对
        Map<Long, ClientBusinessHistoryBO> clientBusinessHistoryBOMap;
        //获取合同承租人、担保人
        //List<CreditReportItemDO> creditItems = creditReportItemService.list(Wrappers.<CreditReportItemDO>lambdaQuery().eq(CreditReportItemDO::getCreditReportId, creditSearchId));
        List<CreditReportClientItem> creditItems = creditReportClientItemService.listByBaseInfoId(creditSearchId);

        List<Long> clientIds = new ArrayList<>();
        List<CreditSearchCompareBusinessDTO> rsp = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(creditItems)) {
            clientIds = creditItems.stream().map(CreditReportClientItem::getClientId).collect(Collectors.toList());
        }
        //查询系统内客户信息
        Map<Long, Client> clientMap = clientMapper.selectBatchIds(clientIds).stream().collect(Collectors.toMap(Client::getId, e -> e, (a, b) -> a));
        //客户工商信息
        Map<Long, CorpCommerceInfo> corpCommerceMap = corpCommerceInfoMapper.selectList(Wrappers.<CorpCommerceInfo>lambdaQuery()
                .in(CorpCommerceInfo::getClientId, clientIds)).stream().collect(Collectors.toMap(CorpCommerceInfo::getClientId, e -> e, (a, b) -> a));
        Map<Long, List<CorpShareholderInfo>> clientShareholderMap = corpShareholderInfoMapper.selectList(Wrappers.<CorpShareholderInfo>lambdaQuery()
                .in(CorpShareholderInfo::getClientId, clientIds)).stream().collect(Collectors.groupingBy(CorpShareholderInfo::getClientId));
        //查询比对信息
        if (isHistory) {
            clientBusinessHistoryBOMap = clientBusinessHistoryService.getHistoryBoByClientIds(clientIds);
        } else {
            //查询天眼查数据
            clientBusinessHistoryBOMap = creditReportClientSupportPort.compareBusiness(clientIds);
        }
        //比对数据
        //承租人
        if (CollectionUtil.isNotEmpty(clientIds)) {
            clientIds.forEach(clientId -> {
                Client client = clientMap.get(clientId);
                if (ClientType.CORPORATION.name().equals(client.getClientType())) {
                    rsp.add(buildCreditSearchCompareBusinessDTO(clientBusinessHistoryBOMap.get(clientId), client, corpCommerceMap.get(clientId),
                            clientShareholderMap.get(clientId), "承租人"));
                }
            });
        }
        return rsp;
    }

    /*@Override
    public void export(ServletOutputStream outputStream, CreditReportListREQ req) {
//        PageR<CreditReportListDTO> result = creditReportItemService.selectList(req);
        req.setPage(1);
        req.setPageSize(5000);
        PageR<CreditReportListDTO> result = this.list(req);
        if (CollectionUtil.isEmpty(result.getList())) {
            throw new MithrasException("暂无导出数据");
        }
        // 查询明细
        List<CreditReportItemDO> itemList = creditReportItemService.list(Wrappers.<CreditReportItemDO>lambdaQuery().in(CreditReportItemDO::getCreditReportId, result.getList().stream().map(CreditReportListDTO::getId).collect(Collectors.toSet())).orderByDesc(CreditReportItemDO::getCreditReportId));
        Map<Long, List<CreditReportItemDO>> itemMap = itemList.stream().collect(Collectors.groupingBy(CreditReportItemDO::getCreditReportId));
        // 按客户拆行
        List<CreditSearchExcelModel> excelModelList = new LinkedList<>();
        for (CreditReportListDTO dto : result.getList()) {
            List<CreditReportItemDO> itemDOList = itemMap.get(dto.getId());
            if (CollectionUtil.isEmpty(itemDOList)) {
                continue;
            }
            for (CreditReportItemDO creditReportItemDO : itemDOList) {
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
*/

    public CreditSearchCompareBusinessDTO buildCreditSearchCompareBusinessDTO(ClientBusinessHistoryBO bo, Client client, CorpCommerceInfo corpCommerceInfo,
                                                                              List<CorpShareholderInfo> corpShareholderInfos, String clientType) {
        boolean nodiff = true;
        CreditSearchCompareBusinessDTO dto = new CreditSearchCompareBusinessDTO();
        dto.setClientType(clientType);
        dto.setClientName(client.getClientName());
        dto.setCorpRepresent(corpCommerceInfo == null ? null : corpCommerceInfo.getCorpRepresent());
        //股东名称
        Set<String> shareholderNameSet = new HashSet<>();
        if (ObjectUtil.isNotNull(bo)) {
            //客户名称
            dto.setClientTycName(bo.getTycName());
            boolean operationFlag;
            LinkedList<DiffMatchPatch.Diff> clientNameDiffs = CompareUtil.compareStringIgnoreExclude(client.getClientName(), bo.getTycName(), GlobalConstants.MATCH_IGNORE_STR);
            operationFlag = diffHasChanges(clientNameDiffs);
            if (operationFlag) {
                dto.setClientNameCompare("一致");
            } else {
                nodiff = false;
                dto.setClientNameCompare(CompareUtil.string2HtmlString(clientNameDiffs, "<span style=\"color:red\">", "</span>"));
            }
            //法人代表信息
            dto.setCorpTycRepresent(bo.getTycCorpRepresent());
            LinkedList<DiffMatchPatch.Diff> corpRepresentDiffs = CompareUtil.compareStringIgnoreExclude(dto.getCorpRepresent(), bo.getTycCorpRepresent(), GlobalConstants.MATCH_IGNORE_STR);
            operationFlag = diffHasChanges(corpRepresentDiffs);
            if (operationFlag) {
                dto.setCorpRepresentCompare("一致");
            } else {
                nodiff = false;
                dto.setCorpRepresentCompare(CompareUtil.string2HtmlString(corpRepresentDiffs, "<span style=\"color:red\">", "</span>"));
            }
            //比对股东信息
            if (CollectionUtil.isNotEmpty(bo.getTycShareHolderInfo())) {
                Map<String, CorpShareholderInfo> corpShareholderMap = new HashMap<>();
                Map<String, CorpShareholderInfo> corpShareholderWithoutSpecialTextMap = new HashMap<>();
                if (CollectionUtil.isNotEmpty(corpShareholderInfos)) {
                    corpShareholderMap = corpShareholderInfos.stream().collect(Collectors.toMap(CorpShareholderInfo::getShareholderName, e -> e, (a, b) -> a));
                    corpShareholderWithoutSpecialTextMap = corpShareholderInfos.stream().collect(Collectors.toMap(e -> StrUtil.removeAll(e.getShareholderName(), GlobalConstants.MATCH_IGNORE_STR), e -> e, (a, b) -> a));
                }
                CorpShareholderInfo corpShareholderInfo;
                String shareholderName;
                String capitalPercent;
                String capitalPercentTyc;
                boolean isConsistent = true;
                for (MithrasShareholderInfo boShareHolderInfo : bo.getTycShareHolderInfo()) {
                    corpShareholderInfo = corpShareholderMap.get(boShareHolderInfo.getShareholderName());
                    if (Objects.isNull(corpShareholderInfo)) {
                        // 找不到的话从去掉特殊字符的map里试试
                        corpShareholderInfo = corpShareholderWithoutSpecialTextMap.get(StrUtil.removeAll(boShareHolderInfo.getShareholderName(), GlobalConstants.MATCH_IGNORE_STR));
                    }
                    shareholderName = Optional.ofNullable(corpShareholderInfo).map(CorpShareholderInfo::getShareholderName).orElse(null);
                    capitalPercentTyc = " " + LongUtil.tenThousand2Dollar(String.valueOf(Optional.ofNullable(boShareHolderInfo.getCapitalPercent()).orElse(0L))).setScale(2, RoundingMode.HALF_UP).toPlainString() + "%";
                    capitalPercent = " " + LongUtil.tenThousand2Dollar(String.valueOf(Optional.ofNullable(corpShareholderInfo).map(CorpShareholderInfo::getCapitalPercent).orElse(0L))).setScale(2, RoundingMode.HALF_UP).toPlainString() + "%";
                    List<String> shareHolderInfo = Optional.ofNullable(dto.getShareHolderInfo()).orElse(new ArrayList<>());
                    if (ObjectUtil.isNotEmpty(shareholderName)) {
                        shareHolderInfo.add(shareholderName + capitalPercent);
                    }
                    dto.setShareHolderInfo(shareHolderInfo);
                    List<String> shareHolderTycInfo = Optional.ofNullable(dto.getShareHolderTycInfo()).orElse(new ArrayList<>());
                    shareHolderTycInfo.add(boShareHolderInfo.getShareholderName() + capitalPercentTyc);
                    dto.setShareHolderTycInfo(shareHolderTycInfo);
                    List<String> shareHolderInfoCompare = Optional.ofNullable(dto.getShareHolderInfoCompare()).orElse(new ArrayList<>());
                    LinkedList<DiffMatchPatch.Diff> shareholderNameDiffs = CompareUtil.compareStringIgnoreExclude(shareholderName, boShareHolderInfo.getShareholderName(), GlobalConstants.MATCH_IGNORE_STR);
                    LinkedList<DiffMatchPatch.Diff> capitalPercentDiffs = CompareUtil.compareStringIgnoreExclude(capitalPercent, capitalPercentTyc, GlobalConstants.MATCH_IGNORE_STR);
                    //工商信息
                    operationFlag = diffHasChanges(shareholderNameDiffs);
                    if (operationFlag) {
                        operationFlag = diffHasChanges(capitalPercentDiffs);
                    }
                    if (operationFlag) {
                        //shareHolderInfoCompare.add(boShareHolderInfo.getShareholderName() + capitalPercentTyc);
                    } else {
                        nodiff = false;
                        isConsistent = false;
                        shareHolderInfoCompare.add(
                                CompareUtil.string2HtmlString(shareholderNameDiffs, "<span style=\"color:red\">", "</span>") + " " +
                                        CompareUtil.string2HtmlString(capitalPercentDiffs, "<span style=\"color:red\">", "</span>")
                        );
                    }
                    dto.setShareHolderInfoCompare(shareHolderInfoCompare);
                    shareholderNameSet.add(shareholderName);
                }
                if (isConsistent) {
                    dto.setShareHolderInfoCompare(ListUtil.toList("一致"));
                }
            }
        } else {
            nodiff = false;
        }
        //补充未比对信息
        if (CollectionUtil.isNotEmpty(corpShareholderInfos)) {
            corpShareholderInfos.forEach(corpShareholderInfo -> {
                if (!shareholderNameSet.contains(corpShareholderInfo.getShareholderName())) {
                    List<String> list = Optional.ofNullable(dto.getShareHolderInfo()).orElse(new ArrayList<>());
                    list.add(corpShareholderInfo.getShareholderName() + " " + LongUtil.tenThousand2Dollar(String.valueOf(LongUtil.null2zero(corpShareholderInfo.getCapitalPercent()))).setScale(2,
                            RoundingMode.HALF_UP).toPlainString() + "%");
                    dto.setShareHolderInfo(list);
                }
            });
        }
        dto.setChangeFlag(nodiff ? YesOrNoNumberEnum.NO.getCode() : YesOrNoNumberEnum.YES.getCode());
        return dto;
    }

    private boolean diffHasChanges(LinkedList<DiffMatchPatch.Diff> diffs) {
        for (DiffMatchPatch.Diff base : diffs) {
            if (!DiffMatchPatch.Operation.EQUAL.equals(base.operation)) {
                return false;
            }
        }
        return true;
    }


    /*private List<CreditReportListDTO> convertCreditReport(List<CreditReportDO> credits) {
        if (CollectionUtils.isEmpty(credits)) {
            return Collections.emptyList();
        }

        // 1. 提取用户和部门 ID 集合
        List<Long> userIdList = credits.stream()
                .map(CreditReportDO::getApplyUser)
                .distinct()
                .collect(Collectors.toList());

        Set<Long> deptIdList = credits.stream()
                .map(CreditReportDO::getApplyOrg)
                .collect(Collectors.toSet());

        // 2. 批量获取用户名和部门名
        Map<Long, String> userId2Name = id2NameService.sysUserId2Name(userIdList);
        Map<Long, String> deptId2Name = id2NameService.deptId2Name(deptIdList);

        // 3. 提取所有 creditReportId，用于批量查询 CreditReportItemDO
        List<Long> reportIds = credits.stream()
                .map(CreditReportDO::getId)
                .collect(Collectors.toList());

        // 4. 批量查询所有相关 items
        List<CreditReportItemDO> allItems = creditReportItemService.list(
                Wrappers.<CreditReportItemDO>lambdaQuery()
                        .in(CreditReportItemDO::getCreditReportId, reportIds)
        );

        // 5. 构建 reportId -> List<Item> 映射
        Map<Long, List<CreditReportItemDO>> reportIdToItemsMap = allItems.stream()
                .collect(Collectors.groupingBy(CreditReportItemDO::getCreditReportId));

        // 6. 最终转换为 DTO 列表
        return credits.stream().map(item -> {
            Long reportId = item.getId();
            List<CreditReportItemDO> items = reportIdToItemsMap.getOrDefault(reportId, Collections.emptyList());

            // 提取字段列表
            List<String> clientName = new ArrayList<>();
            List<String> cscCodeList = new ArrayList<>();
            List<String> zhongZhengCodeList = new ArrayList<>();
            List<String> selectGoalList = new ArrayList<>();

            for (CreditReportItemDO itemDO : items) {
                clientName.add(itemDO.getClientName());
                cscCodeList.add(itemDO.getCscCode());
                zhongZhengCodeList.add(itemDO.getZhongZhengCode());
                selectGoalList.add(itemDO.getSelectGoal());
            }

            // 构造 DTO
            CreditReportListDTO listDTO = new CreditReportListDTO();
            BeanUtils.copyProperties(item, listDTO);
            listDTO.setClientNameList(clientName);
            listDTO.setCscCodeList(cscCodeList);
            listDTO.setZhongZhengCodeList(zhongZhengCodeList);
            listDTO.setSelectGoalList(selectGoalList);

            // 设置用户和部门名称（带默认值）
            listDTO.setApplyUserName(userId2Name.getOrDefault(item.getApplyUser(), "未知用户"));
            listDTO.setApplyOrgName(deptId2Name.getOrDefault(item.getApplyOrg(), "未知部门"));

            return listDTO;
        }).collect(Collectors.toList());
    }*/

    @Override
    public void processEnd(Long id, Integer endType, Long startUserId, String processInstanceId, String modelKey) {
        boolean processPass = ProcessBusinessStatusEnum.success(endType);
        if (processPass) {
            updateApplyStatus(id, ProcessState.PASS.name());
            // 审批通过 新增版本
            creditReportVersionService.recordVersion(id, VersionTypeEnum.APPROVAL, Long.valueOf(startUserId), processInstanceId, VersionTypeConstants.NORMAL);
        } else {
            if (ProcessBusinessStatusEnum.CANCEL.getType().equals(endType)) {
                updateApplyStatus(id, ProcessState.CANCEL.name());
            }
            if (ProcessBusinessStatusEnum.REJECT.getType().equals(endType) || ProcessBusinessStatusEnum.REJECT_ALL.getType().equals(endType)) {
                updateApplyStatus(id, ProcessState.REJECT.name());
            }
            creditReportVersionService.recordVersion(id, VersionTypeEnum.APPROVAL, Long.valueOf(startUserId), processInstanceId, VersionTypeConstants.INVALID);
            creditReportVersionService.reset(id);
        }

        //这里补充解析的数据,应该异步的去处理不可阻塞主流程
        if (processPass) {
            // 异步生成评审会的汇总审批表
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    new Thread(() -> {
                        try {
                            SpringContextHolder.getBean(CreditReportBaseInfoService.class).remoteXJManage(id);
                        } catch (Exception e) {
                            log.error("征信报告解析远程查询接口异常 creditReportId = {}", id, e);
                        }
                    }).start();
                }
            });
        }
    }

    public void updateApplyStatus(Long id, String applyStatus) {
        // 查询主表记录
        CreditReportBaseInfo creditReport = creditReportBaseInfoService.getById(id);
        if (creditReport == null) {
            throw new MithrasException("未找到ID为 " + id + "的征信报告");
        }

        // 设置主表状态和时间
        creditReport.setApplyStatus(applyStatus);
        LocalDateTime applyTime = ProcessState.PASS.name().equals(applyStatus) ? LocalDateTime.now() : null;
        creditReport.setApplyTime(applyTime);

        /*// 查询并更新所有子项
        List<CreditReportItemDO> items = creditReportItemService.list(
                Wrappers.<CreditReportItemDO>lambdaQuery()
                        .eq(CreditReportItemDO::getCreditReportId, id)
        );

        for (CreditReportItemDO item : items) {
            item.setApplyStatus(applyStatus);
            item.setApplyTime(applyTime);
        }
        // 批量更新子表
        creditReportItemService.saveOrUpdateBatch(items);
        */


        // 更新主表
        creditReportBaseInfoService.updateById(creditReport);
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

    public ProcessResp findRelatedProcess(Long guidanceId) {
        ProcessPageReq processPageReq = new ProcessPageReq();
        processPageReq.setPageIndex(1);
        processPageReq.setPageSize(1);
        processPageReq.setBusinessKey(String.valueOf(guidanceId));
        processPageReq.setModelKeyList(CreditReportBusinessModule.CREDIT_REPORT_SELECT.getModelKeyList());
        processPageReq.setProcessStatusList(Arrays.asList(ProcessBusinessStatusEnum.RUNNING.getType(), ProcessBusinessStatusEnum.SUSPEND.getType()));
        cn.zswltech.flow.core.util.Page<ProcessResp> processRespPage = taskApiService.queryProcess(processPageReq);
        return processRespPage.getContents().stream().findFirst().orElse(null);
    }
}
