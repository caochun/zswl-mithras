package cn.zswltech.mithras.service.service.flow;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.zswltech.flow.core.api.FlowTaskApiService;
import cn.zswltech.flow.core.domain.entity.AddSignRecord;
import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.flow.core.enums.ProcessBusinessStatusEnum;
import cn.zswltech.flow.core.service.impl.FlowAddSignRecordService;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.mithras.dto.flow.file.FlowFileListREQ;
import cn.zswltech.mithras.dto.flow.file.FlowFileListRSP;
import cn.zswltech.mithras.dto.flow.file.FlowFileRemoveREQ;
import cn.zswltech.mithras.common.constant.ResultMsg;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.enums.FilePolicyAdapterEnum;
import cn.zswltech.mithras.service.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.service.flow.file.FileHandlerFactory;
import cn.zswltech.mithras.service.flow.file.IFileHandler;
import cn.zswltech.mithras.service.mapper.model.MaterialsList;
import cn.zswltech.mithras.service.mapper.model.client.ClientTransferApply;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.Id2NameService;
import cn.zswltech.mithras.service.service.client.ClientTransferApplyService;
import cn.zswltech.mithras.service.service.materialsfile.MaterialsListService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 审批流里 动态表单 上传文件
 *
 * @author wangchuanhao
 * @date 2022/11/23 10:53 AM
 */
@Service
public class FlowFileService {

    @Resource
    private FlowTaskApiService taskApiService;
    @Resource
    private MaterialsListService materialsListService;
    @Resource
    private Id2NameService id2NameService;
    @Resource
    private FileHandlerFactory fileHandlerFactory;
    @Resource
    private FlowAddSignRecordService addSignRecordService;

    @Transactional(rollbackFor = Exception.class)
    public void uploadApproval(MultipartFile file, String materialsType, String processInstanceId, String taskId) {
        ProcessResp processResp = taskApiService.queryProcessById(processInstanceId);
        checkProcess(processResp);
        BusinessModuleEnum businessModuleEnum = Optional.ofNullable(ProcessModelTypeEnum.getByName(processResp.getModelKey()))
                .map(ProcessModelTypeEnum::getBusinessModuleName)
                .map(BusinessModuleEnum::of)
                .orElseThrow(() -> new MithrasException("modelKey未登记，请联系管理员进行处理"));
        // 额外的校验、额外的处理
        IFileHandler fileHandler = fileHandlerFactory.getFileHandler(businessModuleEnum);
        if (Objects.isNull(fileHandler)) {
            throw new MithrasException("该模块不允许在审批流程中上传文件，请联系管理员进行处理");
        }
        fileHandler.uploadCheck(processResp, materialsType, taskId);
        //这里判断是否是协同流程上传文件
        AddSignRecord addSignRecord = addSignRecordService.findByTaskId(taskId);
        // 特殊处理客户移交的协同文件
        if (Objects.nonNull(addSignRecord) && businessModuleEnum == BusinessModuleEnum.CLIENT_TRANSFER) {
            // 客户移交是字符串的批次号，文件记录表的belong_id是长整形，防止以后发生溢出，此处做一下转换
            ClientTransferApply clientTransferApply = SpringUtil.getBean(ClientTransferApplyService.class).findByBatchNo(processResp.getBusinessKey());
            if (Objects.isNull(clientTransferApply)) {
                throw new MithrasException("客户移交申请记录不存在");
            }
            materialsListService.add(file, clientTransferApply.getId(), materialsType, businessModuleEnum.name());
            return;
        }
        //这里判断是否是协同流程上传文件
        if (ObjectUtil.isNotNull(addSignRecord)) {
            //判断是否有其它类型的文件标签
            Enum[] fileTypeEnumByModuleEnum = FilePolicyAdapterEnum.getFileTypeEnumByModuleEnum(BusinessModuleEnum.of(ProcessModelTypeEnum.getByName(processResp.getModelKey()).getBusinessModuleName()));
            if (ObjectUtil.isEmpty(fileTypeEnumByModuleEnum)) {
                throw new MithrasException("流程不支持上传【其它】类型文件");
            }
            Enum fileType = null;
            for (Enum anEnum : fileTypeEnumByModuleEnum) {
                if ("OTHER".equals(anEnum.name()) || "OTHER_CONTRACT".equals(anEnum.name())) {
                    fileType = anEnum;
                }
            }
            if (ObjectUtil.isNull(fileType)) {
                throw new MithrasException("流程不支持上传【其它】类型文件");
            }
            Long add = materialsListService.add(file, Long.valueOf(processResp.getBusinessKey()), materialsType, businessModuleEnum.name());
            MaterialsList materials = new MaterialsList();
            materials.setMaterialSubType("COLLABORATE");
            materials.setId(add);
            materialsListService.updateById(materials);
        } else {
            materialsListService.add(file, Long.valueOf(processResp.getBusinessKey()), materialsType, businessModuleEnum.name());
        }
        fileHandler.afterUploadHook(processResp, taskId);
    }

    public List<FlowFileListRSP> listApproval(FlowFileListREQ req) {
        ProcessResp processResp = taskApiService.queryProcessById(req.getProcessInstanceId());
        if (Objects.isNull(processResp)) {
            throw new MithrasException("流程不存在");
        }
        BusinessModuleEnum businessModuleEnum = Optional.ofNullable(ProcessModelTypeEnum.getByName(processResp.getModelKey()))
                .map(ProcessModelTypeEnum::getBusinessModuleName)
                .map(BusinessModuleEnum::of)
                .orElseThrow(() -> new MithrasException("modelKey未登记，请联系管理员进行处理"));
        List<String> materialsTypeList = CollectionUtils.isEmpty(req.getMaterialsTypeList()) ? new ArrayList<>() : req.getMaterialsTypeList();
        if (StringUtils.isNotBlank(req.getMaterialsType())) {
            materialsTypeList.addAll(Stream.of(req.getMaterialsType().split(",")).collect(Collectors.toList()));
        }
        IFileHandler fileHandler = fileHandlerFactory.getFileHandler(businessModuleEnum);
        if (Objects.isNull(fileHandler)) {
            throw new MithrasException("该模块不允许在审批流程中上传文件，请联系管理员进行处理");
        }
        List<MaterialsList> materialsList = fileHandler.listFile(processResp, materialsTypeList);
        Map<Long, String> userNameMap = id2NameService.sysUserId2Name(materialsList.stream().map(MaterialsList::getCreateBy).filter(Objects::nonNull).collect(Collectors.toSet()));
        return materialsList.stream().map(item -> {
            FlowFileListRSP rsp = new FlowFileListRSP();
            rsp.setId(item.getId());
            rsp.setTypeName(fileHandler.convertMaterialsType(item.getMaterialsType()));
            rsp.setMaterialsType(item.getMaterialsType());
            rsp.setFileName(item.getFilename());
            rsp.setCreateTime(LocalDateTimeUtil.format(item.getCreateTime(), DatePattern.NORM_DATETIME_PATTERN));
            rsp.setCreator(userNameMap.get(item.getCreateBy()));
            return rsp;
        }).collect(Collectors.toList());
    }

    @Transactional(rollbackFor = Exception.class)
    public void removeApproval(FlowFileRemoveREQ req) {
        ProcessResp processResp = taskApiService.queryProcessById(req.getProcessInstanceId());
        checkProcess(processResp);
        BusinessModuleEnum businessModuleEnum = Optional.ofNullable(ProcessModelTypeEnum.getByName(processResp.getModelKey()))
                .map(ProcessModelTypeEnum::getBusinessModuleName)
                .map(BusinessModuleEnum::of)
                .orElseThrow(() -> new MithrasException("modelKey未登记，请联系管理员进行处理"));
        // 校验文件、模块对应
        MaterialsList materialsList = materialsListService.getById(req.getId());
        if (Objects.isNull(materialsList)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        if (!Objects.equals(processResp.getModelKey(), ProcessModelTypeEnum.ClientTransferFlow.name())) {
            // 客户移交因为belongId的特殊性，暂时放开校验
            if (!Objects.equals(Long.valueOf(processResp.getBusinessKey()), materialsList.getBelongId())
                    || !businessModuleEnum.name().equals(materialsList.getBusinessType())) {
                throw new MithrasException("该条审批中不允许操作其他业务数据的文件");
            }
        }
        // 额外的校验、额外的处理
        IFileHandler fileHandler = fileHandlerFactory.getFileHandler(businessModuleEnum);
        if (Objects.isNull(fileHandler)) {
            throw new MithrasException("该模块不允许在审批流程中删除文件，请联系管理员进行处理");
        }
        fileHandler.removeCheck(processResp, materialsList, null);
        materialsListService.remove(req.getId());
        fileHandler.afterRemoveHook(processResp, null);
    }

    /**
     * 流程内修改数据的校验
     *
     * @param processResp
     */
    private void checkProcess(ProcessResp processResp) {
        if (Objects.isNull(processResp)) {
            throw new MithrasException("流程不存在");
        }
        if (!ProcessBusinessStatusEnum.RUNNING.getType().equals(processResp.getProcessStatus())) {
            throw new MithrasException("流程已结束，不可上传文件");
        }
        Set<Long> curAssigneeSet = StringUtils.isBlank(processResp.getCurAssigneeIds()) ? new HashSet<>() : Stream.of(processResp.getCurAssigneeIds().split(",")).map(Long::valueOf).collect(Collectors.toSet());
        if (!curAssigneeSet.contains(AccountUtil.getLoginInfo().getId())) {
            throw new MithrasException("您不是流程中当前审批人，无法进行操作");
        }
    }

}
