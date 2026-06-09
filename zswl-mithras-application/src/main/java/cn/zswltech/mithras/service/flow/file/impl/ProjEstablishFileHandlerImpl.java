package cn.zswltech.mithras.service.flow.file.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.flow.core.domain.entity.AddSignRecord;
import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.flow.core.service.impl.FlowAddSignRecordService;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.projectprocess.enums.projestablish.ProjEstablishMaterialsEnum;
import cn.hutool.core.lang.Assert;
import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.projectprocess.enums.projestablish.ProjEstablishMaterialsApproveEnum;
import cn.zswltech.mithras.projectprocess.enums.projreview.ProjReviewMaterialsEnum;
import cn.zswltech.mithras.service.flow.file.IFileHandler;
import cn.zswltech.mithras.service.mapper.model.MaterialsList;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.materialsfile.MaterialsListService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @author yangxiong
 * @date 2024/4/22/15:22
 * @description
 */
@Component
public class ProjEstablishFileHandlerImpl implements IFileHandler {
    @Resource
    private MaterialsListService materialsListService;
    @Resource
    private FlowAddSignRecordService signRecordService;

    @Override
    public void uploadCheck(ProcessResp processResp, String materialsType, String taskId) {
        fileTypeCheck(processResp, materialsType);
    }

    @Override
    public void removeCheck(ProcessResp processResp, MaterialsList materialsList, String taskId) {
        fileTypeCheck(processResp, materialsList.getMaterialsType());
    }

    @Override
    public List<MaterialsList> listFile(ProcessResp processResp, List<String> materialsTypeList) {
        return materialsListService.list(BusinessModuleEnum.PROJ_ESTABLISH.name(), materialsTypeList, Collections.singletonList(Long.valueOf(processResp.getBusinessKey())));
    }

    @Override
    public BusinessModuleEnum businessModule() {
        return BusinessModuleEnum.PROJ_ESTABLISH;
    }

    private void fileTypeCheck(ProcessResp processResp, String materialsType) {
        ProjEstablishMaterialsApproveEnum projEstablishMaterialsApproveEnum = ProjEstablishMaterialsApproveEnum.getByName(materialsType);
        if (Objects.isNull(projEstablishMaterialsApproveEnum)) {
            throw new MithrasException("此文件类型无法在该节点操作");
        }
    }

    @Override
    public String convertMaterialsType(String materialsType) {
        return Optional.ofNullable(ProjEstablishMaterialsApproveEnum.getByName(materialsType)).map(ProjEstablishMaterialsApproveEnum::getDisplay).orElse(
                Optional.ofNullable(ProjEstablishMaterialsEnum.getByName(materialsType)).map(ProjEstablishMaterialsEnum::getDisplay).orElse(null));
    }


}
