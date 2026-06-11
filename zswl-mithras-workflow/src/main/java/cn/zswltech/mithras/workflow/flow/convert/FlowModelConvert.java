package cn.zswltech.mithras.workflow.flow.convert;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.zswltech.flow.core.domain.req.ModelPageReq;
import cn.zswltech.flow.core.domain.resp.ModelResp;
import cn.zswltech.mithras.dto.flow.model.ModelListREQ;
import cn.zswltech.mithras.dto.flow.model.ModelListRSP;

import java.util.Optional;

/**
 * 转换
 *
 * @author wangchuanhao
 * @date 2022/10/25 2:07 PM
 */
public class FlowModelConvert {

    public static ModelPageReq REQ2FlowReq(ModelListREQ req) {
        ModelPageReq modelPageReq = new ModelPageReq();
        modelPageReq.setModelKey(req.getModelKey());
        modelPageReq.setModelName(req.getModelName());
        modelPageReq.setPageIndex(req.getPage());
        modelPageReq.setPageSize(req.getPageSize());
        return modelPageReq;
    }

    public static ModelListRSP flowResp2RSP(ModelResp modelResp) {
        ModelListRSP modelListRSP = new ModelListRSP();
        modelListRSP.setModelId(modelResp.getModelId());
        modelListRSP.setModelKey(modelResp.getModelKey());
        modelListRSP.setModelName(modelResp.getModelName());
        modelListRSP.setVersion(modelResp.getVersion());
        modelListRSP.setUpdateTime(Optional.ofNullable(modelResp.getUpdateTime()).map(LocalDateTimeUtil::of).orElse(null));
        modelListRSP.setDeploymentId(modelResp.getDeploymentId());
        modelListRSP.setDeployFlag(modelResp.getDeployFlag());
        return modelListRSP;

    }

}
