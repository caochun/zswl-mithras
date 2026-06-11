package cn.zswltech.mithras.application.orchestration.document.file.impl;

import cn.hutool.core.lang.Pair;
import cn.zswltech.mithras.dto.file.FileListREQ;
import cn.zswltech.mithras.dto.file.FileListRSP;
import cn.zswltech.mithras.dto.file.ext.FileListREQAdjustExt;
import cn.zswltech.mithras.application.orchestration.enums.BusinessModuleEnum;
import cn.zswltech.mithras.afterlease.enums.AfterLeaseAdjustMaterialsEnum;
import cn.zswltech.mithras.application.orchestration.document.file.AbstractFileListProvider;
import cn.zswltech.mithras.document.file.bo.FileListExtQuery;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 租后调整
 *
 * @author wangchuanhao
 * @date 2023/2/6 2:07 PM
 */
@Component
public class AdjustFileListProvider extends AbstractFileListProvider {

    @Override
    public BusinessModuleEnum getBusinessModule() {
        return BusinessModuleEnum.ADJUST;
    }

    @Override
    public List<Pair<String, List<FileListRSP>>> listGroup(FileListREQ req) {
        FileListExtQuery extQuery = new FileListExtQuery();
        extQuery.setMaterialsTypes(AfterLeaseAdjustMaterialsEnum.listAll());
        if (Objects.nonNull(req.getExt())) {
            FileListREQAdjustExt extREQ = JSONObject.parseObject(JSON.toJSONString(req.getExt()), FileListREQAdjustExt.class);
            if (Objects.nonNull(extREQ.getProcessInstanceId())) {
                // 评审流程详情页展示其他文件
                extQuery.setMaterialsTypes(AfterLeaseAdjustMaterialsEnum.listAdjust());
            }
        }
        return listGroup(req, extQuery);
    }

    @Override
    protected int getGroupFileSort(FileListRSP rsp) {
        return Optional.ofNullable(AfterLeaseAdjustMaterialsEnum.getByName(rsp.getMaterialsType())).map(AfterLeaseAdjustMaterialsEnum::getSort).orElse(Integer.MAX_VALUE);
    }

}
