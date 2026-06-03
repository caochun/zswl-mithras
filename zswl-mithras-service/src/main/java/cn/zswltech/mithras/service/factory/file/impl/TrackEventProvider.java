package cn.zswltech.mithras.service.factory.file.impl;

import cn.hutool.core.lang.Pair;
import cn.zswltech.flow.core.api.FlowTaskApiService;
import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.mithras.dto.file.FileListREQ;
import cn.zswltech.mithras.dto.file.FileListRSP;
import cn.zswltech.mithras.dto.file.ext.FileListREQProjReviewExt;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.service.enums.projreview.ProjReviewMaterialsEnum;
import cn.zswltech.mithras.workflow.domain.enums.trackevent.TrackEventMaterialsEnum;
import cn.zswltech.mithras.service.factory.file.AbstractFileListProvider;
import cn.zswltech.mithras.service.factory.file.bo.FileListExtQuery;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 跟踪事项
 *
 */
@Component
public class TrackEventProvider extends AbstractFileListProvider {

    @Resource
    private FlowTaskApiService flowTaskApiService;

    @Override
    public BusinessModuleEnum getBusinessModule() {
        return BusinessModuleEnum.TRACK_EVENT;
    }

    @Override
    public List<Pair<String, List<FileListRSP>>> listGroup(FileListREQ req) {
        FileListExtQuery extQuery = new FileListExtQuery();
        extQuery.setMaterialsTypes(TrackEventMaterialsEnum.listAll());
        return listGroup(req, extQuery);
    }

    @Override
    protected int getGroupFileSort(FileListRSP rsp) {
        return Optional.ofNullable(TrackEventMaterialsEnum.getByName(rsp.getMaterialsType())).map(TrackEventMaterialsEnum::getSort).orElse(Integer.MAX_VALUE);
    }

}
