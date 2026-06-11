package cn.zswltech.mithras.application.orchestration.document.file.impl;

import cn.zswltech.flow.core.api.FlowTaskApiService;
import cn.zswltech.mithras.dto.file.FileListRSP;
import cn.zswltech.mithras.application.orchestration.enums.BusinessModuleEnum;
import cn.zswltech.mithras.workflow.enums.trackevent.TrackEventMaterialsEnum;
import cn.zswltech.mithras.application.orchestration.document.file.AbstractFileListProvider;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Optional;

/**
 * 客户评级
 */
@Component
public class RatingClientFileListProvider extends AbstractFileListProvider {

    @Resource
    private FlowTaskApiService flowTaskApiService;

    @Override
    public BusinessModuleEnum getBusinessModule() {
        return BusinessModuleEnum.RATING_CLIENT;
    }

    @Override
    protected int getGroupFileSort(FileListRSP rsp) {
        return Optional.ofNullable(TrackEventMaterialsEnum.getByName(rsp.getMaterialsType())).map(TrackEventMaterialsEnum::getSort).orElse(Integer.MAX_VALUE);
    }

}
