package cn.zswltech.mithras.api;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.ProfitCalculateResultListREQ;
import cn.zswltech.mithras.dto.ProfitCalculateResultListRSP;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

/**
 * @author dingqi
 * @date 2023/6/25
 * @description
 */
@Api(tags = "会计利润测算")
@RequestMapping(path = "/profitcalculate")
public interface ProfitCalculateResultApi {
    @ApiOperation("分页列表")
    @PostMapping(path = "/pagelist")
    R<ProfitCalculateResultListRSP> pageList(@RequestBody @Valid ProfitCalculateResultListREQ req);

    @ApiOperation("导出")
    @PostMapping(path = "/export")
    void exportExcel(@RequestBody @Valid ProfitCalculateResultListREQ req);
}
