package cn.zswltech.mithras.contract.overdue.application.collection;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.zswltech.mithras.contract.enums.overdue.LetterType;
import cn.zswltech.mithras.contract.enums.overdue.OverdueCollectionType;
import cn.zswltech.mithras.contract.overdue.application.OverdueBusinessModule;
import cn.zswltech.mithras.contract.overdue.application.assembler.CollectionAssembler;
import cn.zswltech.mithras.contract.overdue.application.command.CollectionActionSubmitCommand;
import cn.zswltech.mithras.contract.overdue.application.dto.CollectionActionDto;
import cn.zswltech.mithras.contract.overdue.application.dto.CollectionDetailDto;
import cn.zswltech.mithras.contract.overdue.application.dto.CollectionListDto;
import cn.zswltech.mithras.contract.overdue.application.query.CollectionPageQuery;
import cn.zswltech.mithras.contract.overdue.application.collection.CollectionQueryService;
import cn.zswltech.mithras.contract.overdue.domain.collection.ActionCode;
import cn.zswltech.mithras.contract.overdue.domain.collection.Collection;
import cn.zswltech.mithras.contract.overdue.domain.collection.CollectionAction;
import cn.zswltech.mithras.contract.overdue.domain.collection.CollectionActionId;
import cn.zswltech.mithras.contract.overdue.domain.collection.CollectionId;
import cn.zswltech.mithras.contract.overdue.domain.collection.CollectionRepository;
import cn.zswltech.mithras.dto.SinglePkREQ;
import cn.zswltech.mithras.foundation.constant.ResultMsg;
import cn.zswltech.mithras.foundation.constant.VersionTypeConstants;
import cn.zswltech.mithras.foundation.enums.JobEnum;
import cn.zswltech.mithras.workflow.flow.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.foundation.enums.VersionTypeEnum;
import cn.zswltech.mithras.foundation.enums.common.ProcessStatus;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.contract.overdue.application.collection.BusinessMaterialResolver;
import cn.zswltech.mithras.contract.overdue.application.collection.CollectionLetterGenerator;
import cn.zswltech.mithras.foundation.port.CurrentUserResolver;
import cn.zswltech.mithras.foundation.port.JobUserResolver;
import cn.zswltech.mithras.foundation.port.OrgJobUserResolver;
import cn.zswltech.mithras.foundation.port.ProcessCcNotifier;
import cn.zswltech.mithras.foundation.port.ProcessVariableStarter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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
    private CollectionLetterGenerator collectionLetterGenerator;
    @Resource
    private CurrentUserResolver currentUserResolver;
    @Resource
    private CollectionVersionService collectionVersionService;
    @Resource
    private BusinessMaterialResolver businessMaterialResolver;
    @Resource
    private ProcessVariableStarter processVariableStarter;
    @Resource
    private OrgJobUserResolver orgJobUserResolver;
    @Resource
    private JobUserResolver jobUserResolver;
    @Resource
    private ProcessCcNotifier processCcNotifier;

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
        if (dto.getType().equals(OverdueCollectionType.SEND_LETTER.name())) {
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
        // check
        CollectionAction action = collectionRepository.findAction(id);
        if (ObjectUtil.isEmpty(action)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        // 发函催收需判断是否在流程中或流程已通过
        if (ObjectUtil.equals(action.getType(), OverdueCollectionType.SEND_LETTER)
                && StrUtil.equalsAny(action.getProcessStatus(), ProcessStatus.UNDER_APPROVAL.name(), ProcessStatus.APPROVAL_PASS.name())) {
            throw new MithrasException("发函催收在流程中或流程已通过, 不允许删除");
        }
        collectionRepository.deleteAction(id);
    }

    public void genLetter(CollectionActionId id) {
        collectionLetterGenerator.genLetter(id.getId());
    }

    @Transactional(rollbackFor = Throwable.class)
    public void actionSubmit(CollectionActionSubmitCommand command) {
        Collection collection = collectionRepository.find(new CollectionId(command.getOcId()));
        Optional<CollectionAction> action = collection.getCollectionActionList().stream()
                .filter(a -> a.getActionId().equals(new CollectionActionId(command.getId())))
                .findFirst();
        if (!action.isPresent()) {
            throw new MithrasException("未找到对应的催收记录");
        }
        // 校验是否上传了相关材料
        if (!businessMaterialResolver.hasMaterials(OverdueBusinessModule.OVERDUE_COLLECTION_ACTION.name(), command.getId())) {
            throw new MithrasException("请先生成或上传相关材料");
        }
        CollectionAction targetAction = action.get();
        Long startUserId = Optional.ofNullable(currentUserResolver.currentUserId())
                .orElseThrow(() -> new MithrasException(ResultMsg.USER_NOT_LOGIN));

        String modelKey;
        String processInstanceName;
        if (Objects.equals(targetAction.getLetterType(), LetterType.Lawyer)) {
            modelKey = ProcessModelTypeEnum.OverdueLawerLetterAuditFlow.name();
            processInstanceName = String.format("%s的律师函发函审批", collection.getClientName());
        } else if (Objects.equals(targetAction.getLetterType(), LetterType.Collection)) {
            modelKey = ProcessModelTypeEnum.OverdueCollectionLetterAuditFlow.name();
            processInstanceName = String.format("%s的催收函及相关函件发函审批", collection.getClientName());
        } else {
            throw new MithrasException("未知的催收函类型");
        }

        Map<String, Object> varMap = new HashMap<>();
        varMap.put("bizDeptLeader", orgJobUserResolver.orgJobUsers(collection.getBizDept(), JobEnum.businesshead.name()).stream()
                .map(String::valueOf)
                .collect(Collectors.toList()));
        varMap.put("bizLeaderInCharge", orgJobUserResolver.orgJobUsers(collection.getBizDept(), JobEnum.leaderincharge.name()).stream()
                .map(String::valueOf)
                .collect(Collectors.toList()));
        String processInstanceId = processVariableStarter.start(String.valueOf(command.getId()), startUserId,
                collection.getBizDept(), modelKey, processInstanceName, varMap);
        targetAction.setProcessStatus(ProcessStatus.UNDER_APPROVAL.name());
        targetAction.setProcessId(processInstanceId);
        collectionRepository.updateAction(targetAction);
    }

    public void processEnd(Long id, boolean processPass, Long startUserId, String processInstanceId, String modelKey) {
        CollectionAction action = collectionRepository.findAction(new CollectionActionId(id));
        if (processPass) {
            action.setProcessStatus(ProcessStatus.APPROVAL_PASS.name());
            collectionRepository.updateAction(action);
            collectionVersionService.recordVersion(id, VersionTypeEnum.APPROVAL, startUserId, processInstanceId, VersionTypeConstants.NORMAL);
            // 催收函流程最后抄送给综合部经办岗用户，综合部负责人用户
            if (modelKey.equals(ProcessModelTypeEnum.OverdueCollectionLetterAuditFlow.name())) {
                Set<Long> userIds = new HashSet<>();
                userIds.addAll(jobUserResolver.jobUsers(new HashSet<>(Collections.singletonList(JobEnum.comprehensiveDept.name()))));
                userIds.addAll(jobUserResolver.jobUsers(new HashSet<>(Collections.singletonList(JobEnum.compremanager.name()))));
                processCcNotifier.cc(processInstanceId, new ArrayList<>(userIds));
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
