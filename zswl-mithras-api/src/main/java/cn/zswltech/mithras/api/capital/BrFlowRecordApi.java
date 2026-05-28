package cn.zswltech.mithras.api.capital;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.capital.BrFlowRecordCountRSP;
import cn.zswltech.mithras.dto.capital.BrFlowRecordListREQ;
import cn.zswltech.mithras.dto.capital.BrFlowRecordListRSP;
import cn.zswltech.mithras.dto.capital.BrFlowRecordRemoveREQ;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
* @description 保融流水表
* @author vico
* @date 2024-06-17
*/
@Api(tags = "保融流水表-接口")
public interface BrFlowRecordApi {

    @ApiOperation("保融流水表列表")
    @PostMapping("/br/flow/record/list")
    R<PageR<BrFlowRecordListRSP>> list(@RequestBody @Valid BrFlowRecordListREQ req);

    @ApiOperation("保融流水表统计")
    @PostMapping("/br/flow/record/count")
    R<BrFlowRecordCountRSP> listCount();

    @ApiOperation("删除保融流水表")
    @PostMapping("/br/flow/record/remove")
    R<Void> remove(@RequestBody @Valid BrFlowRecordRemoveREQ req);

    @ApiOperation("忽略保融流水表")
    @PostMapping("/br/flow/record/ignore")
    R<Void> ignore(@RequestBody @Valid BrFlowRecordRemoveREQ req);

}