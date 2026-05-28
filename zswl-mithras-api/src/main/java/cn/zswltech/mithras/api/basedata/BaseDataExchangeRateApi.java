package cn.zswltech.mithras.api.basedata;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.SinglePkREQ;
import cn.zswltech.mithras.dto.basedata.BaseDataExchangeRateQueryREQ;
import cn.zswltech.mithras.dto.basedata.BaseDataExchangeREQ;
import cn.zswltech.mithras.dto.basedata.BaseDataExchangeRSP;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

/**
 * @author dingqi
 * @date 2025/9/24
 * @description
 */
@Api(tags = "财务管理-汇率设置")
@RequestMapping(path = "/baseData/exchangeRate")
public interface BaseDataExchangeRateApi {
    @ApiOperation(value = "财务管理-汇率设置-分页列表")
    @PostMapping(path = "/pageList")
    R<PageR<BaseDataExchangeRSP>> pageList(@RequestBody @Valid BaseDataExchangeRateQueryREQ req);

    @ApiOperation(value = "财务管理-汇率设置-新增")
    @PostMapping(path = "/add")
    R<Long> add(@RequestBody @Valid BaseDataExchangeREQ req);

    @ApiOperation(value = "财务管理-汇率设置-编辑")
    @PostMapping(path = "/modify")
    R<Long> modify(@RequestBody @Valid BaseDataExchangeREQ req);

    @ApiOperation(value = "财务管理-汇率设置-删除")
    @PostMapping(path = "/delete")
    R<Void> delete(@RequestBody @Valid SinglePkREQ req);
}
