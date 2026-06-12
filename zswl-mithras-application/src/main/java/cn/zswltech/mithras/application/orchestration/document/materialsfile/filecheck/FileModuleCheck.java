package cn.zswltech.mithras.application.orchestration.document.materialsfile.filecheck;

import cn.hutool.core.lang.Pair;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.dto.file.FileListREQ;
import cn.zswltech.mithras.dto.file.FileListRSP;
import cn.zswltech.mithras.application.orchestration.auth.checker.common.CommonModifyMainAuthCheckerNew;
import cn.zswltech.mithras.application.orchestration.auth.checker.common.CommonRemoveMainAuthCheckerNew;
import cn.zswltech.mithras.application.orchestration.auth.checker.common.CommonViewMainAuthCheckerNew;
import cn.zswltech.mithras.foundation.constant.ResultMsg;
import cn.zswltech.mithras.application.orchestration.enums.BusinessModuleEnum;
import cn.zswltech.mithras.document.persistence.model.MaterialsList;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.application.orchestration.document.materialsfile.MaterialsListService;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @ClassName FileModelCheck
 * 各模块文件检查
 * @Author jackerhe
 * @Date 2022/11/20 11:43 上午
 * @Version 1.0
 **/
public abstract class FileModuleCheck {

    @Resource
    private CommonModifyMainAuthCheckerNew commonModifyMainAuthCheckerNew;
    @Resource
    private CommonRemoveMainAuthCheckerNew commonRemoveMainAuthCheckerNew;
    @Resource
    private CommonViewMainAuthCheckerNew commonViewMainAuthCheckerNew;
    @Resource
    protected MaterialsListService materialsListService;

    public abstract String getModuleKey();

    public void checkUpload(String moduleKey, Long mainId , String materialsType){
        BusinessModuleEnum moduleEnum = Optional.ofNullable(BusinessModuleEnum.of(moduleKey)).orElseThrow(() -> new MithrasException(ResultMsg.UNSUPPORT_TYPE));
        commonModifyMainAuthCheckerNew.check(moduleEnum, null, mainId, null);
    }

    public void appCheckUpload(String moduleKey, Long mainId , String materialsType, Long createdBy){
        BusinessModuleEnum moduleEnum = Optional.ofNullable(BusinessModuleEnum.of(moduleKey)).orElseThrow(() -> new MithrasException(ResultMsg.UNSUPPORT_TYPE));
        commonModifyMainAuthCheckerNew.appCheck(moduleEnum, null, mainId, null, createdBy);
    }

    public void afterUploadHandle(String moduleType, Long mainId, Long fileId, String sourceBusinessKey, Long userId){
    }


    public String beforeUploadHandle(String originalFilename, String moduleType, String materialsType, String materialsSubType, Long mainId, String sourceBusinessKey, Long userId) {
        return null;
    }

    public List<Pair<String, List<FileListRSP>>> afterList(Long mainId, String moduleKey, FileListREQ req) {
        return null;
    }

    public void checkRemove(String moduleKey, Long fileId){
        BusinessModuleEnum moduleEnum = Optional.ofNullable(BusinessModuleEnum.of(moduleKey)).orElseThrow(() -> new MithrasException(ResultMsg.UNSUPPORT_TYPE));
        MaterialsList materials = getMaterials(fileId);
        commonRemoveMainAuthCheckerNew.check(moduleEnum, null, materials.getBelongId(), null);
    }

    public void afterRemoveHandle(Long mainId, List<MaterialsList> fileList, Long userId){
    }

    //批量删除时，有所以文件删除权限方可
    public void checkRemove(String moduleKey, List<Long> fileIds){
        BusinessModuleEnum moduleEnum = Optional.ofNullable(BusinessModuleEnum.of(moduleKey)).orElseThrow(() -> new MithrasException(ResultMsg.UNSUPPORT_TYPE));
        if(ObjectUtil.isEmpty(fileIds)){
            return;
        }
        Set<Long> belongSet = materialsListService.getByIds(fileIds).stream().map(MaterialsList::getBelongId).collect(Collectors.toSet());
        for(Long mainId : belongSet){
            commonRemoveMainAuthCheckerNew.check(moduleEnum, null, mainId, null);
        }
    }

    public void checkList(String moduleKey, Long mainId){
        BusinessModuleEnum moduleEnum = Optional.ofNullable(BusinessModuleEnum.of(moduleKey)).orElseThrow(() -> new MithrasException(ResultMsg.UNSUPPORT_TYPE));
        commonViewMainAuthCheckerNew.check(moduleEnum, null, mainId, null);
    }

    public void checkDownload(String moduleKey, Long mainId, List<Long> fileIds){
        BusinessModuleEnum moduleEnum = Optional.ofNullable(BusinessModuleEnum.of(moduleKey)).orElseThrow(() -> new MithrasException(ResultMsg.UNSUPPORT_TYPE));
        commonViewMainAuthCheckerNew.check(moduleEnum, null, mainId, null);
    }

    public void checkTemplateDownload(String moduleKey, String templateId){
        //模版默认不进行校验
    }

    public Boolean isCheck(String moduleKey){
        return ObjectUtil.equals(moduleKey, getModuleKey());
    }

    protected MaterialsList getMaterials(Long fileId){
        MaterialsList materialsList = materialsListService.getById(fileId);
        if(ObjectUtil.isNull(materialsList)){
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        return materialsList;
    }

    public String getWatermarkSting(){
        return "浙商租赁";
    }

}
