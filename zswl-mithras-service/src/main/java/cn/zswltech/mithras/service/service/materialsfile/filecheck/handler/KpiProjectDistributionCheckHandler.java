package cn.zswltech.mithras.service.service.materialsfile.filecheck.handler;


import cn.hutool.core.lang.Pair;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.flow.core.api.FlowTaskApiService;
import cn.zswltech.mithras.dto.file.FileListRSP;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.mapper.model.MaterialsList;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.system.service.SysUserService;
import cn.zswltech.mithras.service.service.materialsfile.filecheck.FileModuleCheck;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;


@Component
public class KpiProjectDistributionCheckHandler extends FileModuleCheck {

    @Resource
    private FlowTaskApiService flowTaskApiService;
    @Resource
    private SysUserService sysUserService;

    @Override
    public String getModuleKey() {
        return BusinessModuleEnum.KPI_PROJECT_DISTRIBUTION.name();
    }

    @Override
    public void checkUpload(String moduleKey, Long mainId, String materialsType) {
        checkFlowFile(mainId);
    }



    @Override
    public void checkRemove(String moduleKey, Long fileId) {
        MaterialsList materials = getMaterials(fileId);
        if(ObjectUtil.isEmpty(materials)){
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        checkFlowFile(materials.getMainId());
    }

    @Override
    public void checkRemove(String moduleKey, List<Long> fileIds) {
        if(ObjectUtil.isEmpty(fileIds)){
            return;
        }
        List<MaterialsList> byIds = materialsListService.getByIds(fileIds);
        byIds.forEach( materials -> {
            if(ObjectUtil.isEmpty(materials)){
                throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
            }
            checkFlowFile(materials.getMainId());
        });
    }

    @Override
    public void checkList(String moduleKey, Long fileId) {
    }

    @Override
    public void checkDownload(String moduleKey, Long mainId, List<Long> fileId) {
    }

    private void checkFlowFile( Long mainId) {

    }
}
