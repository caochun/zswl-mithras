package cn.zswltech.mithras.api.report;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.report.ReportChangeREQ;
import cn.zswltech.mithras.dto.report.specialtrade.SpecialTradeListREQ;
import cn.zswltech.mithras.dto.report.specialtrade.SpecialTradeModifyREQ;
import cn.zswltech.mithras.dto.version.DiffValue;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.Map;

/**
 * 征信报送-特定交易表接口
 *
 * @author wangchuanhao
 * @date 2023/1/11 3:16 PM
 */
@Api("征信报送-特定交易表接口")
public interface CrSpecialTradeApi {

    @ApiOperation("特定交易表列表查询")
    @PostMapping("/cr/special/trade/list")
    R<PageR<Map<String, DiffValue>>> list(@RequestBody @Valid SpecialTradeListREQ req);

    @ApiOperation("特定交易表编辑")
    @PostMapping("/cr/special/trade/modify")
    R<Void> modify(@RequestBody @Valid SpecialTradeModifyREQ req);

    @ApiOperation("特定交易表报送状态变更")
    @PostMapping("/cr/special/trade/reportChange")
    R<Void> reportChange(@RequestBody @Valid ReportChangeREQ req);

}
