package cn.zswltech.mithras.service.overdue.application.service;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import cn.zswltech.flow.core.api.FlowProcessApiService;
import cn.zswltech.flow.core.domain.req.StartProcessReq;
import cn.zswltech.flow.core.enums.ProcessBusinessStatusEnum;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.gruul.dao.dal.entity.UserDO;
import cn.zswltech.gruul.dao.dal.vo.AccountVO;
import cn.zswltech.mithras.api.flow.ExecutionApi;
import cn.zswltech.mithras.dto.SinglePkREQ;
import cn.zswltech.mithras.dto.flow.execution.ExecutionProcessBaseREQ;
import cn.zswltech.mithras.service.constant.GlobalConstants;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.service.constant.VersionTypeConstants;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.enums.JobEnum;
import cn.zswltech.mithras.service.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.service.enums.VersionTypeEnum;
import cn.zswltech.mithras.common.enums.ProcessStatus;
import cn.zswltech.mithras.service.enums.overdue.LetterType;
import cn.zswltech.mithras.service.enums.overdue.OverdueCollectionType;
import cn.zswltech.mithras.service.mapper.model.MaterialsList;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.overdue.application.assembler.CollectionAssembler;
import cn.zswltech.mithras.service.overdue.application.command.CollectionActionSubmitCommand;
import cn.zswltech.mithras.service.overdue.application.dto.CollectionActionDto;
import cn.zswltech.mithras.service.overdue.application.dto.CollectionDetailDto;
import cn.zswltech.mithras.service.overdue.application.dto.CollectionListDto;
import cn.zswltech.mithras.service.overdue.application.query.CollectionPageQuery;
import cn.zswltech.mithras.service.overdue.domain.collection.Collection;
import cn.zswltech.mithras.service.overdue.domain.collection.*;
import cn.zswltech.mithras.service.service.SysUserService;
import cn.zswltech.mithras.service.service.materialsfile.MaterialsListService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

import static cn.hutool.extra.spring.SpringUtil.getBean;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2024/10/22 14:01
 */
@Service
@Slf4j
public class CollectionApplicationService {
    @Resource
    private CollectionRepository collectionRepository;
    @Resource
    private CollectionAssembler collectionAssembler;
    @Resource
    private CollectionQueryService collectionQueryService;
    @Resource
    private CollectionLetterGenService collectionLetterGenService;
    @Autowired
    private HttpServletResponse response;
    @Resource
    private FlowProcessApiService flowProcessApiService;
    @Resource
    private SysUserService sysUserService;
    @Resource
    private CollectionVersionService collectionVersionService;
    @Resource
    private MaterialsListService materialsListService;

    public List<CollectionListDto> page(CollectionPageQuery query) {
        return collectionQueryService.page(query);
    }
    public CollectionDetailDto detail(CollectionId id) {
        Collection collection = collectionRepository.find(id);
        return collectionAssembler.toDetailDto(collection);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void update(CollectionDetailDto dto) {
        Collection collection = collectionAssembler.detailToAggregate(dto);
        collectionRepository.find(collection.getCollectionId());
        collectionRepository.save(collection);
    }

    @Transactional(rollbackFor = Throwable.class)
    public Long addAction(CollectionActionDto dto) {
        Collection collection = collectionRepository.find(new CollectionId(dto.getOcId()));
        if(dto.getType().equals(OverdueCollectionType.SEND_LETTER.name())){
            // 判断是否存在未提交审批的发函催收记录
            if (ObjectUtil.isNotEmpty(collection.getCollectionActionList())) {
                Optional<Long> target = collection.getCollectionActionList().stream()
                        .filter(action -> action.getType().equals(OverdueCollectionType.SEND_LETTER) && ObjectUtil.isEmpty(action.getProcessStatus()))
                        .map(CollectionAction::getActionId)
                        .map(CollectionActionId::getId)
                        .max(Comparator.naturalOrder());
                if (target.isPresent()) {
                    // 存在则返回id，不新增
                    return target.get();
                }
            }
        }

        // 按照code倒序排序，取第一个作为前一code
        ActionCode preCode = null;
        List<CollectionAction> collectionActionList = collection.getCollectionActionList();
        if (ObjectUtil.isNotEmpty(collectionActionList)) {
            preCode = collectionActionList.get(0).getActionCode();
        }
        ActionCode actionCode = preCode == null ? new ActionCode() : preCode.nextCode();
        dto.setCode(actionCode.getCode());
        return collectionRepository.addAction(collectionAssembler.actionListDto2Entity(dto));
    }

    public void updateAction(CollectionActionDto dto) {
        collectionRepository.updateAction(collectionAssembler.actionListDto2Entity(dto));
    }

    public void delete(CollectionActionId id) {
        //check
        CollectionAction action = collectionRepository.findAction(id);
        if(ObjectUtil.isEmpty(action)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        //发函催收需判断是否在流程中或流程已通过
        if(ObjectUtil.equals(action.getType(), OverdueCollectionType.SEND_LETTER) && StrUtil.equalsAny(action.getProcessStatus(), ProcessStatus.UNDER_APPROVAL.name(), ProcessStatus.APPROVAL_PASS.name())) {
            //判断流程
            throw new MithrasException("发函催收在流程中或流程已通过, 不允许删除");
        }
        collectionRepository.deleteAction(id);
    }

    public void genLetter(CollectionActionId id) {
        collectionLetterGenService.genLetter(id);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void actionSubmit(CollectionActionSubmitCommand command) {
        Collection collection = collectionRepository.find(new CollectionId(command.getOcId()));
        Optional<CollectionAction> action = collection.getCollectionActionList().stream().filter(a -> a.getActionId().equals(new CollectionActionId(command.getId()))).findFirst();
        if(!action.isPresent()){
            throw new MithrasException("未找到对应的催收记录");
        }
        // 校验是否上传了相关材料
        List<MaterialsList> list = materialsListService.listBy(BusinessModuleEnum.OVERDUE_COLLECTION_ACTION.name(), command.getId());
        if (ObjectUtil.isEmpty(list)) {
            throw new MithrasException("请先生成或上传相关材料");
        }
        CollectionAction targetAction = action.get();
        StartProcessReq startProcessReq = new StartProcessReq();
        startProcessReq.setBusinessKey(String.valueOf(command.getId()));
        startProcessReq.setStartUserId(Optional.ofNullable(AccountUtil.getLoginInfo())
                .map(AccountVO::getId)
                .map(String::valueOf)
                .orElseThrow(() -> new MithrasException(ResultMsg.USER_NOT_LOGIN)));
        startProcessReq.setStartUserDeptId(Optional.ofNullable(collection.getBizDept()).map(String::valueOf).orElse(null));
        if(Objects.equals(targetAction.getLetterType(), LetterType.Lawyer)) {
            startProcessReq.setModelKey(ProcessModelTypeEnum.OverdueLawerLetterAuditFlow.name());
            startProcessReq.setProcessInstanceName(String.format("%s的律师函发函审批", collection.getClientName()));
        }else if(Objects.equals(targetAction.getLetterType(), LetterType.Collection)){
            startProcessReq.setModelKey(ProcessModelTypeEnum.OverdueCollectionLetterAuditFlow.name());
            startProcessReq.setProcessInstanceName(String.format("%s的催收函及相关函件发函审批", collection.getClientName()));
        }else {
            throw new MithrasException("未知的催收函类型");
        }

        List<UserDO> bizDeptLeaderUserList = sysUserService.listSpecificOrgJobUser(collection.getBizDept(), JobEnum.businesshead.name());
        List<UserDO> divisionLeaderUserList = sysUserService.listSpecificOrgJobUser(collection.getBizDept(), JobEnum.leaderincharge.name());
        Map<String, Object> varMap = new HashMap<>();
        varMap.put("bizDeptLeader", bizDeptLeaderUserList.stream().map(UserDO::getId).map(String::valueOf).collect(Collectors.toList()));
        varMap.put("bizLeaderInCharge",divisionLeaderUserList.stream().map(UserDO::getId).map(String::valueOf).collect(Collectors.toList()));
        startProcessReq.setVariables(varMap);
        String processInstanceId = flowProcessApiService.start(startProcessReq);
        targetAction.setProcessStatus(ProcessStatus.UNDER_APPROVAL.name());
        targetAction.setProcessId(processInstanceId);
        collectionRepository.updateAction(targetAction);
    }

    public void downloadAction(CollectionId collectionId) {
        Collection collection = collectionRepository.find(new CollectionId(collectionId.getId()));
        try {
            List<CollectionActionDto> orginalList =
                collectionAssembler.entity2ActionListDto(collection.getCollectionActionList());
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ExcelWriter w = ExcelUtil.getWriter(true);
            w.writeHeadRow(ListUtil.of("催收记录编号", "催收类型", "催收日期", "催收人员",
                    "催收进展"));

            for (CollectionActionDto c : orginalList) {
                w.writeRow(ListUtil.of(
                        c.getCode(),
                        OverdueCollectionType.valueOf(c.getType()).display(),
                        c.getDate(),
                        c.getProcessPerson(),
                        c.getDescribe()
                ));
            }
            w.flush(bos, true);
            ServletOutputStream outputStream = response.getOutputStream();
            response.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(collection.getClientName() + "逾期催收记录" + GlobalConstants.OFFICE_EXCEL_SUFFIX, StandardCharsets.UTF_8.name()));
            outputStream.write(bos.toByteArray());
        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            log.error("导出逾期催收记录发生未知异常", e);
            throw new MithrasException("导出逾期催收记录发生未知异常");
        }
    }

    public void processEnd(Long id, Integer endType, Long startUserId, String processInstanceId, String modelKey) {
        CollectionAction action = collectionRepository.findAction(new CollectionActionId(id));
        boolean processPass = ProcessBusinessStatusEnum.success(endType);
        if (processPass) {
            action.setProcessStatus(ProcessStatus.APPROVAL_PASS.name());
            collectionRepository.updateAction(action);
            collectionVersionService.recordVersion(id, VersionTypeEnum.APPROVAL, startUserId, processInstanceId, VersionTypeConstants.NORMAL);
            // 催收函流程最后抄送给综合部经办岗用户，综合部负责人用户
            if (modelKey.equals(ProcessModelTypeEnum.OverdueCollectionLetterAuditFlow.name())) {
                ExecutionProcessBaseREQ req = new ExecutionProcessBaseREQ();
                List<Long> comprehensiveDeptUserIds = sysUserService.jobUsers(new HashSet<>(Collections.singletonList(JobEnum.comprehensiveDept.name())));
                List<Long> compreManagerUserIds = sysUserService.jobUsers(new HashSet<>(Collections.singletonList(JobEnum.compremanager.name())));
                Set<Long> userIds = new HashSet<>();
                userIds.addAll(comprehensiveDeptUserIds);
                userIds.addAll(compreManagerUserIds);
                req.setProcessInstanceId(processInstanceId);
                req.setCcUserIdList(new ArrayList<>(userIds));
                getBean(ExecutionApi.class).cc(req);
            }
        } else {
            action.setProcessStatus(ProcessStatus.APPROVAL_REJECT.name());
            collectionRepository.updateAction(action);
            collectionVersionService.recordVersion(id, VersionTypeEnum.APPROVAL, startUserId, processInstanceId, VersionTypeConstants.INVALID);
        }

    }

    public CollectionActionDto actionDetail(SinglePkREQ req) {
        if (ObjectUtil.isNotEmpty(req.getVersion())) {
            CollectionAction actionLib = collectionRepository.findActionLib(new CollectionActionId(req.getId()), req.getVersion());
            return collectionAssembler.entity2ActionListDto(actionLib);
        } else {
            CollectionAction action = collectionRepository.findAction(new CollectionActionId(req.getId()));
            return collectionAssembler.entity2ActionListDto(action);
        }

    }
}
