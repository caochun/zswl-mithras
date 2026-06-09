package cn.zswltech.mithras.service.service.filingmaterials;
import cn.zswltech.mithras.workflow.application.flow.enums.ProcessModelTypeEnum;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.zswl.oss.core.OssClient;
import cn.zswltech.flow.core.api.FlowProcessApiService;
import cn.zswltech.flow.core.api.FlowTaskApiService;
import cn.zswltech.flow.core.domain.req.ProcessHistoryReq;
import cn.zswltech.flow.core.domain.req.StartProcessReq;
import cn.zswltech.flow.core.domain.req.task.ProcessPageReq;
import cn.zswltech.flow.core.domain.resp.ProcessHistoryResp;
import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.flow.core.enums.ProcessBusinessStatusEnum;
import cn.zswltech.flow.core.util.Page;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.gruul.dao.dal.entity.OrgDO;
import cn.zswltech.gruul.dao.dal.vo.AccountVO;
import cn.zswltech.mithras.dto.filingmaterials.AfterLeasingRenderDTO;
import cn.zswltech.mithras.dto.filingmaterials.FilingBaseREQ;
import cn.zswltech.mithras.dto.flow.execution.ExecutionProcessBaseREQ;
import cn.zswltech.mithras.service.config.redis.RedisDistLock;
import cn.zswltech.mithras.filingmaterials.domain.constant.FilingMaterialsConstants;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.workflow.application.flow.convert.FlowProcessConvert;
import cn.zswltech.mithras.service.enums.*;
import cn.zswltech.mithras.afterlease.domain.enums.AfterLeaseCheckReportMaterialsEnum;
import cn.zswltech.mithras.afterlease.domain.enums.AfterLeaseCheckWayEnum;
import cn.zswltech.mithras.afterlease.domain.enums.NewAfterLeaseCheckMaterialsEnum;
import cn.zswltech.mithras.customer.domain.enums.app.VisitPhaseStatus;
import cn.zswltech.mithras.customer.domain.enums.app.VisitRecordStatus;
import cn.zswltech.mithras.contract.enums.contract.ContractStatus;
import cn.zswltech.mithras.filingmaterials.domain.enums.BusinessMaterialsDocNameEnum;
import cn.zswltech.mithras.filingmaterials.domain.enums.FilingMaterialsInitiationMethodEnum;
import cn.zswltech.mithras.filingmaterials.domain.enums.FilingMaterialsProcessStatusEnum;
import cn.zswltech.mithras.service.gendoc.render.BusinessMaterialsAfterRender;
import cn.zswltech.mithras.service.mapper.MaterialsListMapper;
import cn.zswltech.mithras.afterlease.infrastructure.persistence.mapper.NewAfterLeaseCheckPlanBaseMapper;
import cn.zswltech.mithras.afterlease.infrastructure.persistence.mapper.NewAfterLeaseCheckPlanClientMapper;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.app.VisitRecordMapper;
import cn.zswltech.mithras.filingmaterials.infrastructure.persistence.mapper.FilingMaterialsMapper;
import cn.zswltech.mithras.service.mapper.model.MaterialsList;
import cn.zswltech.mithras.afterlease.infrastructure.persistence.mapper.model.NewAfterLeaseCheckPlanBase;
import cn.zswltech.mithras.afterlease.infrastructure.persistence.mapper.model.NewAfterLeaseCheckPlanClient;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.app.VisitRecord;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.filingmaterials.infrastructure.persistence.mapper.model.FilingMaterials;
import cn.zswltech.mithras.workflow.infrastructure.persistence.mapper.process.prepare.CommonProcessPrepareMapper;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.workflow.application.process.BizProcessDataService;
import cn.zswltech.mithras.workflow.application.process.FlowAssistService;
import cn.zswltech.mithras.system.user.Id2NameService;
import cn.zswltech.mithras.system.user.SysUserService;
import cn.zswltech.mithras.afterlease.application.AfterLeaseCheckPlanBaseService;
import cn.zswltech.mithras.afterlease.application.AfterLeaseCheckPlanClientService;
import cn.zswltech.mithras.contract.core.application.ContractBaseInfoService;
import cn.zswltech.mithras.service.service.flow.ExecutionService;
import cn.zswltech.mithras.service.service.materialsfile.MaterialsListService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static cn.hutool.extra.spring.SpringUtil.getBean;
import static cn.zswltech.mithras.service.enums.JobEnum.businesshead;
import static java.util.stream.Collectors.toList;

/**
 * 项目端租后资料归档
 *
 * @author lllin
 * @date 2026-01-07
 */
@Slf4j
@Service("AfterFilingMaterialsService")
public class AfterFilingMaterialsService extends AbstractFilingMaterialsService<FilingMaterialsMapper, FilingMaterials> {
    @Resource
    private FlowTaskApiService taskApiService;
    @Resource
    private FlowProcessApiService processApiService;
    @Resource
    private Id2NameService id2NameService;
    @Resource
    private CommonProcessPrepareMapper commonProcessPrepareMapper;
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
    private NewAfterLeaseCheckPlanBaseMapper newAfterLeaseCheckPlanBaseMapper;
    @Resource
    private BusinessMaterialsAfterRender businessMaterialsAfterRender;
    @Resource
    private AfterLeaseCheckPlanClientService afterLeaseCheckPlanClientService;
    @Resource
    private AfterLeaseCheckPlanBaseService afterLeaseCheckPlanBaseService;
    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private BizProcessDataService bizProcessDataService;
    @Resource
    private NewAfterLeaseCheckPlanClientMapper newAfterLeaseCheckPlanClientMapper;

    Map<Long, String> assetManagerMap = new HashMap<>();


    @Override
    public String startProcess(FilingBaseREQ filingBaseREQ) {
        FilingMaterials filingMaterials = this.getById(filingBaseREQ.getId());
        NewAfterLeaseCheckPlanClient checkPlanClient = afterLeaseCheckPlanClientService.getById(filingMaterials.getObjectId());
        NewAfterLeaseCheckPlanBase newAfterLeaseCheckPlanBase = newAfterLeaseCheckPlanBaseMapper.selectById(checkPlanClient.getPlanId());

        AfterLeaseCheckWayEnum afterLeaseCheckWayEnum = AfterLeaseCheckWayEnum.find(checkPlanClient.getCheckWay());
        //检查形式
        String checkWay = Optional.ofNullable(afterLeaseCheckWayEnum).map(AfterLeaseCheckWayEnum::getDisplay).orElse("");
        //资产管理岗审批人
        String assetApprovedUser = assetManagerMap.get(filingBaseREQ.getId());
        if (CharSequenceUtil.isEmpty(assetApprovedUser)) {
            log.error("获取资产管理岗审批人失败" + filingMaterials.getObjectId());
            return null;
        }
        List<ContractBaseInfo> list = contractBaseInfoService.list(Wrappers.<ContractBaseInfo>lambdaQuery()
                .eq(ContractBaseInfo::getClientId, checkPlanClient.getClientId())
                .in(ContractBaseInfo::getContractStatus, ListUtil.toList(ContractStatus.TAKE_EFFECT.name(), ContractStatus.START_RENT.name(), ContractStatus.SETTLE.name())));
        //3、生成资料清单
        AfterLeasingRenderDTO afterLeasingRenderDTO = new AfterLeasingRenderDTO();
        afterLeasingRenderDTO.setPlanName(newAfterLeaseCheckPlanBase.getPlanName());
        afterLeasingRenderDTO.setCheckWay(checkWay);
        afterLeasingRenderDTO.setAssetUserId(Long.parseLong(assetApprovedUser));
        if (CollUtil.isNotEmpty(list)) {
            afterLeasingRenderDTO.setContractCode(list.stream().map(ContractBaseInfo::getContractCode).distinct().collect(Collectors.joining("、")));
            afterLeasingRenderDTO.setProjName(list.stream().map(ContractBaseInfo::getProjName).distinct().collect(Collectors.joining("、")));
        }
        getPlanYear(newAfterLeaseCheckPlanBase.getPlanName(), afterLeasingRenderDTO);
        HashMap<String, Object> map = new HashMap<>();
        BusinessMaterialsDocNameEnum businessMaterialsDocNameEnum = Objects.equals(AfterLeaseCheckWayEnum.SITE.name(), checkPlanClient.getCheckWay()) ?
                BusinessMaterialsDocNameEnum.AFTER_SITE : BusinessMaterialsDocNameEnum.AFTER_OFFSITE;
        map.put(FilingMaterialsConstants.TEMPLATE_TYPE, businessMaterialsDocNameEnum);
        map.put(FilingMaterialsConstants.OBJECT, afterLeasingRenderDTO);
        generateAfterBasicInformation(map, filingMaterials.getId(), FilingMaterialsConstants.BASIC_INFORMATION, BusinessModuleEnum.AFTER_LEASING_FILING.name());
        /*初始化流程发起对象*/
        StartProcessReq startProcessReq = new StartProcessReq();
        startProcessReq.setModelKey(ProcessModelTypeEnum.AfterFilingMaterialsApplyFlow.name());
        startProcessReq.setBusinessKey(String.valueOf(filingBaseREQ.getId()));
        startProcessReq.setProcessInstanceName(String.format("%s-%s", checkWay, newAfterLeaseCheckPlanBase.getPlanName()));
        List<OrgDO> allOrg = SpringUtil.getBean(SysUserService.class).listAllDept();
        //资产保全部
        Optional<OrgDO> optional = allOrg.stream().filter(e -> Objects.equals(e.getCode(), "FLHGB_ZCBQ")).findFirst();
        /*发起人、发起部门*/
        startProcessReq.setStartUserId(assetApprovedUser);
        if (optional.isPresent()) {
            startProcessReq.setStartUserDeptId(String.valueOf(optional.get().getId()));
        }
        Map<String, Object> varMap = new HashMap<>(1);
        varMap.put("assetManager", ListUtil.toList(assetApprovedUser));
        /*复审岗*/
        varMap.put("afterReview", super.getReviewJobUser());
        startProcessReq.setVariables(varMap);
        String processInstanceId = processApiService.start(startProcessReq);
        bizProcessDataService.recordBizData(processInstanceId, filingMaterials.getClientId());
        /*更新申请表*/
        filingMaterials = this.getById(filingBaseREQ.getId());
        filingMaterials.setFlowId(processInstanceId);
        filingMaterials.setApproveStatus(FilingMaterialsProcessStatusEnum.UNDER_APPROVAL.name());
        filingMaterials.setUserId(Long.parseLong(startProcessReq.getStartUserId()));
        filingMaterials.setDeptId(Long.parseLong(startProcessReq.getStartUserDeptId()));
        this.updateById(filingMaterials);
        assetManagerMap.remove(filingMaterials.getId());
        return processInstanceId;

    }

    private void startProcessPreCheck(Long objectId) {
        NewAfterLeaseCheckPlanClient checkPlanClient = afterLeaseCheckPlanClientService.getById(objectId);
        Assert.notNull(checkPlanClient, () -> MithrasException.newException("检查计划中没有找到客户记录"));
        NewAfterLeaseCheckPlanBase planBase = afterLeaseCheckPlanBaseService.getById(checkPlanClient.getPlanId());
        Assert.notNull(planBase, () -> MithrasException.newException(ResultMsg.RECORD_NOT_EXIST));
    }

    private void getPlanYear(String planName, AfterLeasingRenderDTO afterLeasingRenderDTO) {
        String regex = "【(\\d+)】";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(planName);

        // 存储提取的数字
        String year = null;
        String times = null;
        int index = 0;
        while (matcher.find()) {
            String group = matcher.group(1);
            if (index == 0) {
                year = group;
            } else if (index == 1) {
                times = group;
            }
            index++;
        }
        afterLeasingRenderDTO.setYear(CharSequenceUtil.isNotEmpty(year) ? year : "");
        afterLeasingRenderDTO.setPhase(CharSequenceUtil.isNotEmpty(times) ? times : "");
    }

    private void getAssetApprovedUser(Long id,Long objectId,String processInstanceId) {
        String assetManager = null;
        if (Objects.isNull(processInstanceId)) {
            ProcessPageReq processPageReq = new ProcessPageReq();
            processPageReq.setPageIndex(1);
            processPageReq.setPageSize(999);
            processPageReq.setProcessStatusList(Arrays.asList(ProcessBusinessStatusEnum.PASS_ALL.getType(), ProcessBusinessStatusEnum.PASS.getType()));
            processPageReq.setBusinessKey(String.valueOf(objectId));
            processPageReq.setModelKeyList(Arrays.asList(ProcessModelTypeEnum.NewAfterLeaseCheckReportCommonlyFlow.name()));
            List<ProcessResp> contents = taskApiService.queryProcess(processPageReq).getContents();
            if (CollUtil.isNotEmpty(contents)) {
                List<ProcessResp> collect = contents.stream().sorted(Comparator.comparing(ProcessResp::getEndTime,
                        Comparator.nullsLast(Comparator.reverseOrder()))).collect(toList());
                processInstanceId = collect.get(0).getProcessInstanceId();
            }

        }
        if (Objects.nonNull(processInstanceId)) {
            ProcessHistoryReq flowReq = new ProcessHistoryReq();
            flowReq.setPageIndex(1);
            flowReq.setPageSize(999);
            flowReq.setProcessInstanceId(processInstanceId);
            Page<ProcessHistoryResp> historyRespPage = processApiService.history(flowReq);
            List<ProcessHistoryResp> processHistoryRespList = historyRespPage.getContents().stream().filter(e -> Objects.equals("userTask_assetManager", e.getTaskActivityId()))
                    .sorted(Comparator.comparing(ProcessHistoryResp::getOperateTime,
                            Comparator.nullsLast(Comparator.reverseOrder())))
                    .collect(toList());
            if (CollUtil.isNotEmpty(processHistoryRespList)) {
                assetManager = processHistoryRespList.get(0).getOperatorId();
            }
        }

        assetManagerMap.put(id, assetManager);

    }

    /**
     * 初始化申请表信息
     */
    @Transactional(rollbackFor = Exception.class,propagation = Propagation.REQUIRES_NEW)
    public void initFilingMaterials(Long objectId,String processInstanceId) {
        startProcessPreCheck(objectId);
        NewAfterLeaseCheckPlanClient checkPlanClient = afterLeaseCheckPlanClientService.getById(objectId);
        AfterLeaseCheckWayEnum afterLeaseCheckWayEnum = AfterLeaseCheckWayEnum.find(checkPlanClient.getCheckWay());

        //检查形式
        String checkWay = Optional.ofNullable(afterLeaseCheckWayEnum).map(AfterLeaseCheckWayEnum::name).orElse("");
        /*1、初始化信息*/
        FilingMaterials filingMaterials = FilingMaterials.initFilingMaterials(checkPlanClient.getClientId(), null, null,
                checkWay, FilingMaterialsInitiationMethodEnum.SYSTEM.name(), null, objectId,
                FilingMaterialsConstants.OBJECT_TYPE_PROJECT);
        this.save(filingMaterials);
        //获取资产经理
        this.getAssetApprovedUser(filingMaterials.getId(),filingMaterials.getObjectId(),processInstanceId);
        //2、资料拷贝
        this.copyFile(objectId, BusinessModuleEnum.NEW_AFTER_LEASE_CHECK_REPORT.name(), filingMaterials.getId(), BusinessModuleEnum.AFTER_LEASING_FILING.name());
        this.copyVisitRecordFile(objectId, filingMaterials.getId(), BusinessModuleEnum.AFTER_LEASING_FILING.name());
        this.startProcess(new FilingBaseREQ(filingMaterials.getId()));
    }

    @Override
    public void copyFile(Long oldBelongId, String oldBusinessType, Long targetId, String businessType) {
        List<MaterialsList> fileList = materialsListService.list(
                Wrappers.<MaterialsList>lambdaQuery()
                        .eq(MaterialsList::getBusinessType, oldBusinessType)
                        .eq(MaterialsList::getMaterialsType, NewAfterLeaseCheckMaterialsEnum.CHECK_REPORT_NON_PUBLIC_ATTACHMENT.name())
                        .eq(MaterialsList::getBelongId, oldBelongId));
        List<MaterialsList> targetList = new LinkedList<>();
        fileList.forEach(item -> {
            MaterialsList newMaterial = new MaterialsList();
            newMaterial.setBelongId(targetId);
            newMaterial.setBusinessType(businessType);
            newMaterial.setMaterialsType(item.getMaterialSubType());
            newMaterial.setMaterialSubType(null);
            newMaterial.setOssFilename(item.getOssFilename());
            newMaterial.setSuffix(item.getSuffix());
            newMaterial.setFilename(item.getFilename());
            newMaterial.setFilePath(item.getFilePath());
            newMaterial.setSystemGenerate(item.getSystemGenerate());
            newMaterial.setSourceBusinessKey(item.getSourceBusinessKey());
            newMaterial.setCreateBy(item.getCreateBy());
            newMaterial.setCreateTime(item.getCreateTime());
            newMaterial.setUpdateBy(item.getUpdateBy());
            newMaterial.setUpdateTime(item.getUpdateTime());
            newMaterial.setLocation(item.getLocation());
            targetList.add(newMaterial);
        });
        if (CollUtil.isNotEmpty(targetList)) {
            materialsListService.saveBatch(targetList);
        }
    }

    private void copyVisitRecordFile(Long objectId, Long targetId, String businessType) {
        NewAfterLeaseCheckPlanClient checkPlanClient = afterLeaseCheckPlanClientService.getById(objectId);
        if (checkPlanClient != null && checkPlanClient.getPlanId() != null) {
            List<VisitRecord> visitRecordList = getBean(VisitRecordMapper.class).selectList(
                    Wrappers.<VisitRecord>lambdaQuery()
                            .in(VisitRecord::getCheckPlanId, checkPlanClient.getPlanId())
                            .eq(VisitRecord::getClientId, checkPlanClient.getClientId())
                            .eq(VisitRecord::getStatus, VisitRecordStatus.PASSED.name())
                            .eq(VisitRecord::getDeleted, 0)
                            .orderByDesc(VisitRecord::getCheckInDate));
            if (!visitRecordList.isEmpty()) {
                Set<Long> visitIds = visitRecordList.stream().map(VisitRecord::getId).collect(Collectors.toSet());
                List<MaterialsList> dataList = materialsListService.getBaseMapper().selectList(Wrappers.<MaterialsList>lambdaQuery()
                        .eq(MaterialsList::getBusinessType, "VISIT_RECORD")
                        .in(MaterialsList::getBelongId, visitIds)
                        .eq(MaterialsList::getMaterialsType, VisitPhaseStatus.AFTER_LEASE_CHECK_PLAN.name())
                );
                for (VisitRecord visitRecord : visitRecordList) {
                    for (MaterialsList materialsList : dataList) {
                        if (visitRecord.getId().equals(materialsList.getBelongId())) {
                            materialsList.setLocation(visitRecord.getCheckInLocation());
                        }
                    }
                }
                List<MaterialsList> targetList = new LinkedList<>();
                dataList.forEach(item -> {
                    MaterialsList newMaterial = new MaterialsList();
                    newMaterial.setBelongId(targetId);
                    newMaterial.setBusinessType(businessType);
                    newMaterial.setMaterialsType(AfterLeaseCheckReportMaterialsEnum.KHHY.name());
                    newMaterial.setMaterialSubType(item.getMaterialSubType());
                    newMaterial.setOssFilename(item.getOssFilename());
                    newMaterial.setSuffix(item.getSuffix());
                    newMaterial.setFilename(item.getFilename());
                    newMaterial.setFilePath(item.getFilePath());
                    newMaterial.setSystemGenerate(item.getSystemGenerate());
                    newMaterial.setSourceBusinessKey(item.getSourceBusinessKey());
                    newMaterial.setCreateBy(item.getCreateBy());
                    newMaterial.setCreateTime(item.getCreateTime());
                    newMaterial.setUpdateBy(item.getUpdateBy());
                    newMaterial.setUpdateTime(item.getUpdateTime());
                    newMaterial.setLocation(item.getLocation());
                    targetList.add(newMaterial);
                });
                if (CollUtil.isNotEmpty(targetList)) {
                    materialsListService.saveBatch(targetList);
                }
            }
        }
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
        /*审批通过抄送运营管理部负责人*/
        Long yyglbDeptLeader = SpringUtil.getBean(FlowAssistService.class).deptLeader(FlowAssistService.YYGLB, FlowAssistService.YYGLB_DESC, businesshead.name());
        if (processPass && Objects.nonNull(yyglbDeptLeader)) {
            NewAfterLeaseCheckPlanClient checkPlanClient = afterLeaseCheckPlanClientService.getById(filingMaterials.getObjectId());
            if (checkPlanClient == null || checkPlanClient.getPlanId() == null) {
                log.error("获取租后检查客户失败：" + filingMaterials.getObjectId());
                return;
            }
            NewAfterLeaseCheckPlanBase newAfterLeaseCheckPlanBase = afterLeaseCheckPlanBaseService.getById(checkPlanClient.getPlanId());
            ExecutionProcessBaseREQ req = new ExecutionProcessBaseREQ();
            req.setProcessInstanceId(processInstanceId);
            req.setCcUserIdList(Arrays.asList(yyglbDeptLeader));
            req.setMessage(String.format("【%s】-租后资料归档已审批通过", newAfterLeaseCheckPlanBase.getPlanName()));
            getBean(ExecutionService.class).cc(req);
        }
    }


    private void generateAfterBasicInformation(HashMap hashMap, Long belongId, String materialsType, String businessType) {
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            String fileName = businessMaterialsAfterRender.render(os, hashMap);
            if (fileName == null || fileName.trim().isEmpty()) {
                throw new IllegalStateException("租后资料清单渲染生成的文件名不能为空");
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
            log.error("渲染租后资料清单失败:" + e.getMessage());
            throw new RuntimeException("渲染租后资料清单失败，IO异常", e);
        } catch (IllegalArgumentException e) {
            log.error("渲染租后资料清单失败:" + e.getMessage());
            throw new IllegalArgumentException("渲染租后资料清单失败，渲染参数非法", e);
        } catch (Exception e) {
            log.error("渲染租后资料清单失败:" + e.getMessage());
            throw new RuntimeException("渲染租后资料清单未知错误", e);
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

    public List<NewAfterLeaseCheckPlanClient> query2026ApprovePassPlanClient(){
        return newAfterLeaseCheckPlanClientMapper.query2026ApprovePassPlanClient();
    }

}
