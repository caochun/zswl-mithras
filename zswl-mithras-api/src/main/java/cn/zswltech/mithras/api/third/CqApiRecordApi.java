package cn.zswltech.mithras.api.third;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.SinglePkREQ;
import cn.zswltech.mithras.dto.third.financial.CqApiRecordREQ;
import cn.zswltech.mithras.dto.third.financial.CqApiRecordRSP;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

/**
 * @author dingqi
 * @date 2024/7/17
 * @description
 */
@Api(tags = "苍穹接口调用记录-人工处理相关接口")
@RequestMapping(path = "/third/cq/record")
public interface CqApiRecordApi {
    @ApiOperation("苍穹接口调用记录-分页列表")
    @PostMapping(path = "/pagelist")
    R<PageR<CqApiRecordRSP>> pageList(@RequestBody @Valid CqApiRecordREQ req);

    @ApiOperation("苍穹接口调用记录-推送")
    @PostMapping(path = "/push")
    R<Void> push(@RequestBody @Valid SinglePkREQ req);

    @ApiOperation("苍穹接口调用记录-忽略")
    @PostMapping(path = "/ignore")
    R<Void> ignore(@RequestBody @Valid SinglePkREQ req);
}
