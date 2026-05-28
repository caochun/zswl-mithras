package cn.zswltech.mithras.api;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.process.modify.remark.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * @author yibin
 */
@Api(value = "变更类流程-附加标记信息-接口")
public interface ProcessModifyRemarkApi {

    @ApiOperation("变更流程-附加标记信息-新增")
    @PostMapping("/process/modify/remark/add")
    R<Void> add(@Valid @RequestBody ProcessModifyRemarkAddREQ req);

    @ApiOperation("变更流程-附加标记信息-修改")
    @PostMapping("/process/modify/remark/modify")
    R<Void> modify(@Valid @RequestBody ProcessModifyRemarkModifyREQ req);

    @ApiOperation("变更流程-附加标记信息-详情")
    @PostMapping("/process/modify/remark/detail")
    R<ProcessModifyRemarkDetailRSP> detail(@Valid @RequestBody ProcessModifyRemarkDetailREQ req);


    @ApiOperation(value = "变更流程-附加标记信息-所有（包括历史）", notes = "流程中获取使用")
    @PostMapping("/process/modify/remark/all")
    R<ProcessModifyRemarkAllRSP> all(@Valid @RequestBody ProcessModifyRemarkAllREQ req);
}
