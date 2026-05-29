package cn.zswltech.mithras.service.service.filingmaterials;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.extra.spring.SpringUtil;
import cn.zswl.oss.core.OssClient;
import cn.zswltech.flow.core.api.FlowProcessApiService;
import cn.zswltech.flow.core.api.FlowTaskApiService;
import cn.zswltech.flow.core.domain.req.ProcessHistoryReq;
import cn.zswltech.flow.core.domain.req.StartProcessReq;
import cn.zswltech.flow.core.domain.resp.ProcessHistoryResp;
import cn.zswltech.flow.core.enums.ProcessBusinessStatusEnum;
import cn.zswltech.gruul.biz.service.UserService;
import cn.zswltech.gruul.common.constant.OrgConstants;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.gruul.dao.dal.entity.OrgDO;
import cn.zswltech.gruul.dao.dal.vo.AccountVO;
import cn.zswltech.gruul.dao.dal.vo.OrgJobVO;
import cn.zswltech.gruul.dao.dal.vo.UserVO;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.filingmaterials.*;
import cn.zswltech.mithras.dto.flow.execution.ExecutionProcessBaseREQ;
import cn.zswltech.mithras.service.config.redis.RedisDistLock;
import cn.zswltech.mithras.service.constant.FilingMaterialsConstants;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.service.convert.filingmaterials.OtherFilingConverter;
import cn.zswltech.mithras.service.convert.flow.FlowProcessConvert;
import cn.zswltech.mithras.service.enums.*;
import cn.zswltech.mithras.common.enums.RecordStatus;
import cn.zswltech.mithras.service.enums.filingmaterials.BusinessMaterialsDocNameEnum;
import cn.zswltech.mithras.service.enums.filingmaterials.FilingMaterialsFilingTypeEnum;
import cn.zswltech.mithras.service.enums.filingmaterials.FilingMaterialsInitiationMethodEnum;
import cn.zswltech.mithras.service.enums.filingmaterials.FilingMaterialsProcessStatusEnum;
import cn.zswltech.mithras.service.excel.exporter.OtherFilingLedgerManageExcelExporter;
import cn.zswltech.mithras.service.excel.model.OtherFilingLedgerManageExcelModel;
import cn.zswltech.mithras.service.gendoc.render.BusinessMaterialsOtherRender;
import cn.zswltech.mithras.service.mapper.MaterialsListMapper;
import cn.zswltech.mithras.service.mapper.filingmaterials.FilingMaterialsMapper;
import cn.zswltech.mithras.service.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.service.mapper.model.filingmaterials.FilingMaterials;
import cn.zswltech.mithras.service.mapper.model.projreview.ProjReviewBaseInfo;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.BizProcessDataService;
import cn.zswltech.mithras.service.service.FlowAssistService;
import cn.zswltech.mithras.service.service.Id2NameService;
import cn.zswltech.mithras.service.service.SysUserService;
import cn.zswltech.mithras.service.service.flow.ExecutionService;
import cn.zswltech.mithras.service.service.materialsfile.MaterialsListService;
import cn.zswltech.mithras.service.service.projreview.ProjReviewBaseInfoService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static cn.hutool.extra.spring.SpringUtil.getBean;
import static cn.zswltech.mithras.service.constant.ResultMsg.CONCURRENT_OPERATION;
import static cn.zswltech.mithras.service.enums.JobEnum.businesshead;


/**
 * 项目端其他资料归档
 *
 * @author lllin
 * @date 2026-01-07
 */
@Slf4j
@Service("OtherFilingMaterialsService")
public class OtherFilingMaterialsService extends AbstractFilingMaterialsService<FilingMaterialsMapper, FilingMaterials> {
    @Resource
    private FlowTaskApiService taskApiService;
    @Resource
    private FlowProcessApiService processApiService;
    @Resource
    private Id2NameService id2NameService;
    @Resource
    private MaterialsListService materialsListService;
    @Resource
    private OssClient ossClient;
    @Resource
    private FlowProcessConvert flowProcessConvert;
    @Resource
    private SysUserService sysUserService;
    @Resource
    private RedisDistLock redisDistLock;
    @Resource
    private MaterialsListMapper materialsListMapper;
    @Resource
    private BusinessMaterialsOtherRender businessMaterialsOtherRender;
    @Resource
    private BizProcessDataService bizProcessDataService;
    @Resource
    private ProjReviewBaseInfoService projReviewBaseInfoService;
    @Resource
    private OtherFilingConverter otherFilingConverter;
    @Resource
    private OtherFilingLedgerManageExcelExporter otherFilingLedgerManageExcelExporter;
    @Resource(name = "userServiceAPI")
    private UserService userService;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public String startProcess(FilingBaseREQ filingBaseREQ) {
        // 防止重复发起流程
        String lockKey = CacheEnum.EFFECT_SUBMIT_LOCK.buildKey(ProcessModelTypeEnum.OtherFilingMaterialsApplyFlow.name(), filingBaseREQ.getId());
        boolean getLockFlag = redisDistLock.tryLockWithoutReleaseTime(lockKey, 1000L);
        if (!getLockFlag) {
            throw new MithrasException(CONCURRENT_OPERATION);
        }
        try {
            FilingMaterials filingMaterials = this.getById(filingBaseREQ.getId());
            if (!Objects.equals(FilingMaterialsProcessStatusEnum.UN_SUBMIT.name(), filingMaterials.getApproveStatus())) {
                throw new MithrasException("流程已提交审批，不可重复提交");
            }
            ProjReviewBaseInfo reviewBaseInfo = projReviewBaseInfoService.getById(filingMaterials.getObjectId());
            Long currentUserId = AccountUtil.getLoginInfo().getId();
            if (Objects.isNull(currentUserId)) {
                throw new MithrasException(ResultMsg.USER_NOT_LOGIN);
            }
            /*初始化流程发起对象*/
            StartProcessReq startProcessReq = new StartProcessReq();
            startProcessReq.setModelKey(ProcessModelTypeEnum.OtherFilingMaterialsApplyFlow.name());
            startProcessReq.setStartUserId(String.valueOf(currentUserId));
            startProcessReq.setBusinessKey(String.valueOf(filingBaseREQ.getId()));
            startProcessReq.setProcessInstanceName(String.format("%s-其他资料归档", reviewBaseInfo.getProjName()));
            startProcessReq.setStartUserDeptId(Optional.ofNullable(sysUserService.getUserDept()).map(OrgDO::getId).map(Objects::toString).orElse(""));

            Map<String, Object> varMap = new HashMap<>(1);
            //项目经理/运营经理
            boolean falg = sysUserService.userIsSpecificRole(Long.parseLong(startProcessReq.getStartUserId()), FilingMaterialsConstants.XMJL,
                    FilingMaterialsConstants.YYGLB_YYJL,FilingMaterialsConstants.PSHMS);
            varMap.put("otherFlag", falg);
            List<String> initApproveUser = super.getInitApproveUser();
            varMap.put("otherInitReview", initApproveUser);
            /*复审岗*/
            List<String> reviewJobUser = super.getReviewJobUser();
            varMap.put("otherReview", reviewJobUser);
            startProcessReq.setVariables(varMap);
            String processInstanceId = processApiService.start(startProcessReq);
            bizProcessDataService.recordBizData(processInstanceId, filingMaterials.getClientId());
            /*更新申请表*/
            filingMaterials = this.getById(filingBaseREQ.getId());
            filingMaterials.setFlowId(processInstanceId);
            filingMaterials.setFirstCommitDate(LocalDateTime.now());
            filingMaterials.setApproveStatus(FilingMaterialsProcessStatusEnum.UNDER_APPROVAL.name());
            this.updateById(filingMaterials);
            return processInstanceId;
        } finally {
            redisDistLock.unlock(lockKey);
        }
    }


    /**
     * 初始化申请表信息
     */
    @Transactional(rollbackFor = Exception.class)
    public FilingMaterials initFilingMaterials(Long projReviewId) {
        ProjReviewBaseInfo projReviewBaseInfo = projReviewBaseInfoService.getById(projReviewId);
        Assert.notNull(projReviewBaseInfo, () -> MithrasException.newException("项目评审不存在"));
        /*1、初始化信息*/
        FilingMaterials filingMaterials = FilingMaterials.initFilingMaterials(projReviewBaseInfo.getClientId(), null, projReviewBaseInfo.getProjCode(),
                FilingMaterialsFilingTypeEnum.OTHER.name(), FilingMaterialsInitiationMethodEnum.MANUAL.name(), null, projReviewId,
                FilingMaterialsConstants.OBJECT_TYPE_PROJECT);
        Long userId = AccountUtil.getLoginInfo().getId();
        List<OrgDO> orgList = sysUserService.getSpecificUserDeptList(userId);
        if (CollectionUtil.isEmpty(orgList)) {
            throw new MithrasException("当前用户没有所属部门");
        }
        Long deptId = orgList.stream().filter(e -> Objects.equals(e.getType(), OrgConstants.BUSINESS_DEPT)).findFirst().map(OrgDO::getId).orElse(null);
        if (Objects.isNull(deptId)) {
            filingMaterials.setDeptId(orgList.get(0).getId());
        } else {
            filingMaterials.setDeptId(deptId);
        }
        filingMaterials.setUserId(userId);
        this.save(filingMaterials);
        //生成资料清单
        ProjReviewBaseInfo reviewBaseInfo = projReviewBaseInfoService.getById(filingMaterials.getObjectId());
        OtherFilingRenderDTO otherFilingRenderDTO = new OtherFilingRenderDTO();
        otherFilingRenderDTO.setProjName(reviewBaseInfo.getProjName());
        otherFilingRenderDTO.setBelongUserId(AccountUtil.getLoginInfo().getId());
        HashMap<String, Object> map = new HashMap<>();
        map.put(FilingMaterialsConstants.TEMPLATE_TYPE, BusinessMaterialsDocNameEnum.OTHER_FILING);
        map.put(FilingMaterialsConstants.OBJECT, otherFilingRenderDTO);
        generateOtherBasicInformation(map, filingMaterials.getId(), FilingMaterialsConstants.BASIC_INFORMATION, BusinessModuleEnum.OTHER_FILING.name());
        return filingMaterials;
    }

    public void cancel(Long id) {
        FilingMaterials filingMaterials = this.getById(id);
        Assert.notNull(filingMaterials, () -> MithrasException.newException("记录不存在"));
        filingMaterials.setApproveStatus(FilingMaterialsProcessStatusEnum.ABOLISH.name());
        filingMaterials.setApproveDate(LocalDateTime.now());
        this.updateById(filingMaterials);
    }


    /**
     * 审批结束
     *
     * @param id
     * @param endType
     * @param processInstanceId
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void processEnd(Long id, Integer endType, String processInstanceId) {
        FilingMaterials filingMaterials = this.getById(id);
        /*更新审批状态*/
        boolean processPass = ProcessBusinessStatusEnum.success(endType);
        // 更新流程状态
        FilingMaterialsProcessStatusEnum processState;
        if (processPass) {
            processState = FilingMaterialsProcessStatusEnum.APPROVAL_PASS;
        } else {
            processState = ProcessBusinessStatusEnum.CANCEL.getType().equals(endType) ? FilingMaterialsProcessStatusEnum.CANCEL : FilingMaterialsProcessStatusEnum.APPROVAL_REJECT;
        }
        filingMaterials.setApproveStatus(processState.name());
        filingMaterials.setApproveDate(LocalDateTime.now());
        this.updateById(filingMaterials);
        Long yyglbDeptLeader = SpringUtil.getBean(FlowAssistService.class).deptLeader(FlowAssistService.YYGLB, FlowAssistService.YYGLB_DESC, businesshead.name());
        if (processPass && checkTaskNode(processInstanceId) && Objects.nonNull(yyglbDeptLeader)) {
            ProjReviewBaseInfo projReviewBaseInfo = projReviewBaseInfoService.getById(filingMaterials.getObjectId());
            if (Objects.isNull(projReviewBaseInfo)) {
                return;
            }
            ExecutionProcessBaseREQ req = new ExecutionProcessBaseREQ();
            req.setProcessInstanceId(processInstanceId);
            req.setCcUserIdList(Arrays.asList(yyglbDeptLeader));
            req.setMessage(String.format("【%s】-其他资料归档已审批通过", projReviewBaseInfo.getProjCode()));
            getBean(ExecutionService.class).cc(req);
        }
    }

    private boolean checkTaskNode(String processInstanceId){
        ProcessHistoryReq flowReq = new ProcessHistoryReq();
        flowReq.setPageIndex(1);
        flowReq.setPageSize(999);
        flowReq.setProcessInstanceId(processInstanceId);
        cn.zswltech.flow.core.util.Page<ProcessHistoryResp> historyRespPage = processApiService.history(flowReq);
        return historyRespPage.getContents().stream().anyMatch(e -> Objects.equals(e.getTaskActivityId(), FilingMaterialsConstants.USER_TASK_OTHER_REVIEW_02));
    }


    private void generateOtherBasicInformation(HashMap hashMap, Long belongId, String materialsType, String businessType) {
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            String fileName = businessMaterialsOtherRender.render(os, hashMap);
            if (fileName == null || fileName.trim().isEmpty()) {
                throw new IllegalStateException("其他资料清单渲染生成的文件名不能为空");
            }
            // 4. 生成输入流（基于渲染后的字节数组）
            materialsListService.add(
                    new ByteArrayInputStream(os.toByteArray()),
                    fileName,
                    belongId,
                    materialsType,
                    "",
                    businessType,
                    YesOrNoNumberEnum.YES,
                    null,
                    null,
                    null
            );
        } catch (IOException e) {
            log.error("渲染其他资料清单失败:" + e.getMessage());
            throw new RuntimeException("渲染其他资料清单失败，IO异常", e);
        } catch (IllegalArgumentException e) {
            log.error("渲染其他资料清单失败:" + e.getMessage());
            throw new IllegalArgumentException("渲染其他资料清单失败，渲染参数非法", e);
        } catch (Exception e) {
            log.error("渲染其他资料清单失败:" + e.getMessage());
            throw new RuntimeException("渲染其他资料清单未知错误", e);
        }
    }


    @Override
    public void generateRunByKey(XWPFDocument document, String key, String name) {
        for (XWPFParagraph paragraph : document.getParagraphs()) {
            String paraText = paragraph.getText();
            if (!paraText.contains(key)) {
                continue;
            }
            List<XWPFRun> runs = paragraph.getRuns();
            if (runs.isEmpty()) {
                continue;
            }
            StringBuilder fullText = new StringBuilder();

            for (XWPFRun run : runs) {
                String text = run.getText(0);
                if (text != null) {
                    fullText.append(text);
                }
            }
            String allText = fullText.toString().replace("null", "");
            String actualName = Objects.isNull(name) ? "" : name;
            allText = replacePrefixValue(allText, key, actualName, null);
            while (!paragraph.getRuns().isEmpty()) {
                paragraph.removeRun(0);
            }
            // 新建一个Run，写入替换后的完整文本
            XWPFRun newRun = paragraph.createRun();
            newRun.setText(allText);
            newRun.setBold(true);
            newRun.setFontSize(10.5);
            newRun.setFontFamily("仿宋_GB2312");
        }
    }


    public String getMaterialsDescById(Long id) {
        FilingMaterials filingMaterials = this.getById(id);
        return filingMaterials.getMaterialsDesc();
    }

    public void saveMaterialsDescById(Long id, String materialsDesc) {
        FilingMaterials filingMaterials = this.getById(id);
        filingMaterials.setMaterialsDesc(materialsDesc);
        this.updateById(filingMaterials);
    }

    public Boolean checkMaterialsDesc(Long id) {
        FilingMaterials filingMaterials = this.getById(id);
        Assert.notNull(filingMaterials, () -> MithrasException.newException("记录不存在"));
        Assert.notEmpty(filingMaterials.getMaterialsDesc(), () -> MithrasException.newException("请完成填写资料类型后提交！"));
        return true;
    }

    public PageR<OtherPageListRSP> list(OtherPageListREQ req) {
        OtherPageSelectDTO otherPageSelectDTO = otherFilingConverter.listReqToListDto(req);
        List<Long> canViewDeptIds = this.canViewDeptIds(AccountUtil.getLoginInfo());
        boolean isBizUser = null != canViewDeptIds;
        otherPageSelectDTO.setFilingType(FilingMaterialsFilingTypeEnum.OTHER.name());
        otherPageSelectDTO.setObjectType(FilingMaterialsConstants.OBJECT_TYPE_PROJECT);
        otherPageSelectDTO.setIsBizUser(isBizUser);
        otherPageSelectDTO.setDeptIdList(canViewDeptIds);
        otherPageSelectDTO.setCurrentUserId(AccountUtil.getLoginInfo().getId());
        if (isBizUser && canViewDeptIds.isEmpty()) {
            //防止sql in报错
            canViewDeptIds.add(Long.MIN_VALUE);
        }
        Page<FilingMaterials> page = baseMapper.otherPageList(new Page<>(req.getPage(), req.getPageSize()), otherPageSelectDTO);
        List<OtherPageListRSP> resPageData = new ArrayList<>();
        Set<Long> clientIds = new HashSet<>();
        Set<Long> sysUserIds = new HashSet<>();
        Set<Long> deptIds = new HashSet<>();
        int index = (req.getPage()-1) * req.getPageSize() + 1;
        for (FilingMaterials record : page.getRecords()) {
            OtherPageListRSP otherPageListRSP = new OtherPageListRSP();
            otherPageListRSP.setIndex(index);
            index++;
            otherPageListRSP.setApproveDate(record.getApproveDate());
            otherPageListRSP.setProjCode(record.getProjCode());
            ProjReviewBaseInfo projReviewBaseInfo = projReviewBaseInfoService.getById(record.getObjectId());
            otherPageListRSP.setProjName(Optional.ofNullable(projReviewBaseInfo).map(ProjReviewBaseInfo::getProjName).orElse(""));
            otherPageListRSP.setId(record.getId());
            otherPageListRSP.setCreateBy(record.getUserId());
            otherPageListRSP.setApproveStatus(record.getApproveStatus());
            otherPageListRSP.setStartDate(record.getFirstCommitDate());
            otherPageListRSP.setClientId(record.getClientId());
            otherPageListRSP.setBizDeptId(record.getDeptId());
            if (Objects.equals(FilingMaterialsProcessStatusEnum.APPROVAL_PASS.name(), record.getApproveStatus())) {
                otherPageListRSP.setMaterialsDesc(record.getMaterialsDesc());
            }
            resPageData.add(otherPageListRSP);
            sysUserIds.add(record.getUserId());
            clientIds.add(record.getClientId());
            deptIds.add(record.getDeptId());
        }

        Map<Long, String> clientMap = id2NameService.clientId2Name(clientIds);
        Map<Long, String> sysUserMap = id2NameService.sysUserId2Name(sysUserIds);
        Map<Long, String> deptId2Name = id2NameService.deptId2Name(new ArrayList<>(deptIds));

        for (OtherPageListRSP resp : resPageData) {
            resp.setClientName(clientMap.get(resp.getClientId()));
            resp.setCreateByName(sysUserMap.get(resp.getCreateBy()));
            resp.setDeptName(deptId2Name.get(resp.getBizDeptId()));
        }
        return PageR.of(page, resPageData);
    }

    public List<Long> canViewDeptIds(AccountVO accountVO) {
        Long userId = accountVO.getId();
        UserVO userVO = userService.getUserInfoById(userId).getData();
        Map<Long, Set<String>> orgJobCodeMap = Optional.ofNullable(userVO.getJobsName()).orElse(new ArrayList<>())
                .stream().collect(Collectors.toMap(OrgJobVO::getOrgId, o -> o.getJobNames().stream().map(OrgJobVO.Job::getJobCode).collect(Collectors.toSet())));
        List<OrgDO> userDeptList = sysUserService.getUserDeptList(accountVO);
        List<Long> bizOrgIdList = new ArrayList<>();
        for (OrgDO orgDO : userDeptList) {
            Set<String> curOrgJobCodeSet = orgJobCodeMap.get(orgDO.getId());
            if (Objects.equals(orgDO.getCode(), FilingMaterialsConstants.YYGLB)
                    || curOrgJobCodeSet.contains(JobEnum.admin.name())) {
                log.info("当前用户为运营管理部门或管理员，可查看所有");
                return null;
            } else if (OrgConstants.BUSINESS_DEPT == orgDO.getType()) {
                if (curOrgJobCodeSet.contains(JobEnum.leaderincharge.name()) || curOrgJobCodeSet.contains(JobEnum.businesshead.name())) {
                    // 只有业务分管领导 和 业务负责人 能看到该部门情况
                    bizOrgIdList.add(orgDO.getId());
                }
            }
        }
        return bizOrgIdList;
    }


    /**
     * 获取项目评审的数据
     *
     * @return
     */
    public List<OtherProjectListRESP> getReviewProject() {
        List<ProjReviewBaseInfo> projReviewBaseInfoList = projReviewBaseInfoService.list();
        if (CollUtil.isEmpty(projReviewBaseInfoList)) {
            return Collections.emptyList();
        }
        List<OtherProjectListRESP> otherProjectListRESPS = new ArrayList<>();
        projReviewBaseInfoList.stream().forEach(e -> {
            OtherProjectListRESP otherProjectListRESP = new OtherProjectListRESP();
            otherProjectListRESP.setProjCode(e.getProjCode());
            otherProjectListRESP.setProjName(e.getProjName());
            otherProjectListRESP.setProjReviewId(e.getId());
            otherProjectListRESP.setClientName(id2NameService.clientId2NameSingle(e.getClientId()));
            otherProjectListRESPS.add(otherProjectListRESP);
        });
        return otherProjectListRESPS;
    }


    public void exportExcel(ServletOutputStream outputStream, List<Long> ids) {
        OtherPageListREQ pageListREQ = new OtherPageListREQ();
        pageListREQ.setIds(ids);
        pageListREQ.setPage(1);
        pageListREQ.setPageSize(5000);
        List<OtherPageListRSP> list = this.list(pageListREQ).getList();
        if (CollUtil.isEmpty(list)) {
            throw new MithrasException("不存在数据，导出失败");
        }
        ArrayList<OtherFilingLedgerManageExcelModel> excelModels = new ArrayList<>();
        list.forEach(one -> {
            OtherFilingLedgerManageExcelModel build = OtherFilingLedgerManageExcelModel.builder()
                    .clientName(one.getClientName())
                    .projectName(one.getProjName())
                    .projectNumber(one.getProjCode())
                    .materialsDesc(one.getMaterialsDesc())
                    .approveStatus(Objects.requireNonNull(FilingMaterialsProcessStatusEnum.of(one.getApproveStatus())).display)
                    .startUserName(one.getCreateByName())
                    .startDeptName(one.getDeptName())
                    .build();
            if (Objects.nonNull(one.getStartDate())) {
                build.setStartTime(one.getStartDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            }
            if (Objects.nonNull(one.getApproveDate())) {
                build.setEndTime(one.getApproveDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            }
            excelModels.add(build);
        });
        otherFilingLedgerManageExcelExporter.exportExcel(excelModels, outputStream);
    }

}
