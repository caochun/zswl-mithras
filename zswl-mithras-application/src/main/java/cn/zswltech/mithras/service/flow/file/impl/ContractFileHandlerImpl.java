package cn.zswltech.mithras.service.flow.file.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.flow.core.domain.entity.AddSignRecord;
import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.flow.core.service.impl.FlowAddSignRecordService;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.contract.enums.contract.ContractTypeEnum;
import cn.zswltech.mithras.service.flow.file.IFileHandler;
import cn.zswltech.mithras.service.mapper.model.MaterialsList;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.materialsfile.MaterialsListService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * @author yangxiong
 * @date 2024/4/22/15:22
 * @description
 */
@Component
public class ContractFileHandlerImpl implements IFileHandler {
    @Resource
    private MaterialsListService materialsListService;
    @Resource
    private FlowAddSignRecordService signRecordService;

    @Override
    public void uploadCheck(ProcessResp processResp, String materialsType, String taskId) {

    }

    @Override
    public void removeCheck(ProcessResp processResp, MaterialsList materialsList, String taskId) {

    }

    @Override
    public void afterUploadHook(ProcessResp processResp, String taskId) {

    }

    @Override
    public void afterRemoveHook(ProcessResp processResp, String taskId) {

    }

    @Override
    public List<MaterialsList> listFile(ProcessResp processResp, List<String> materialsTypeList) {
        return materialsListService.list(BusinessModuleEnum.CONTRACT.name(), materialsTypeList, Collections.singletonList(Long.valueOf(processResp.getBusinessKey())));
    }

    @Override
    public BusinessModuleEnum businessModule() {
        return BusinessModuleEnum.CONTRACT;
    }

    @Override
    public String convertMaterialsType(String materialsType) {
        return Optional.ofNullable(ContractTypeEnum.getByName(materialsType)).map(ContractTypeEnum::getDisplay).orElse(null);
    }
}
