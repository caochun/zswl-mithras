package cn.zswltech.mithras.api.flow;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.flow.model.ModelBaseREQ;
import cn.zswltech.mithras.dto.flow.model.ModelConfigRSP;
import cn.zswltech.mithras.dto.flow.model.ModelDetailRSP;
import cn.zswltech.mithras.dto.flow.model.ModelListREQ;
import cn.zswltech.mithras.dto.flow.model.ModelListRSP;
import cn.zswltech.mithras.dto.flow.model.SaveModelREQ;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

/**
 * 审批流模型
 *
 * @author wangchuanhao
 * @date 2022/10/24 10:02 PM
 */
@Api(tags = "流程相关-模型-接口")
@RequestMapping("/flow/model")
public interface ModelApi {

    @ApiOperation("保存模型")
    @PostMapping("/save")
    R<String> save(@RequestBody @Valid SaveModelREQ req);

    @ApiOperation("查看模型xml")
    @PostMapping("/detail")
    R<ModelDetailRSP> detail(@RequestBody @Valid ModelBaseREQ req);

    @ApiOperation("流程模型列表")
    @PostMapping("/list")
    R<PageR<ModelListRSP>> list(@RequestBody @Valid ModelListREQ req);

    @ApiOperation("发布流程模型")
    @PostMapping("/deploy")
    R<Void> deploy(@RequestBody @Valid ModelBaseREQ req);

    @ApiOperation("删除流程模型")
    @PostMapping("/delete")
    R<Void> delete(@RequestBody @Valid ModelBaseREQ req);

    @ApiOperation("流程模型节点字段配置")
    @PostMapping("/modelConfig")
    R<ModelConfigRSP> modelConfig();



}
