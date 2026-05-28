package cn.zswltech.mithras.api.report;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.report.batch.BatchHeadREQ;
import cn.zswltech.mithras.dto.report.batch.BatchHeadRSP;
import cn.zswltech.mithras.dto.report.batch.BatchListREQ;
import cn.zswltech.mithras.dto.report.batch.BatchListRSP;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * 征信报送-批次接口
 *
 * @author wangchuanhao
 * @date 2023/1/13 9:39 AM
 */
@Api("征信报送-批次接口")
public interface CrBatchApi {

    @ApiOperation("批次列表查询")
    @PostMapping("/cr/batch/incre/list")
    R<PageR<BatchListRSP>> increList(@RequestBody @Valid BatchListREQ req);

    @ApiOperation(value = "批次提交原因")
    @PostMapping(path = "/cr/batch/reason")
    R<BatchHeadRSP> batchReason(@RequestBody BatchHeadREQ req);

}
