package cn.zswltech.mithras.api.riskcontrol;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.riskcontrol.model.gljy.report.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

/**
 * @author yibin
 */
@Api(tags = "关联交易报送-Api")
public interface RiskControlGljyReportApi {

    @ApiOperation("手动新增关联交易记录")
    @PostMapping("/risk/control/gljy/report/add")
    R<Void> addManually(@RequestBody @Valid GljyReportAddREQ req);

    @ApiOperation("删除关联交易记录")
    @PostMapping("/risk/control/gljy/report/remove")
    R<Void> remove(@RequestBody @Valid GljyReportRemoveREQ req);

    @ApiOperation("修改关联交易记录；如果是已报送记录，高度提示")
    @PostMapping("/risk/control/gljy/report/modify")
    R<Void> modify(@RequestBody @Valid GljyReportModifyREQ req);

    @ApiOperation("关联交易记录列表")
    @PostMapping("/risk/control/gljy/report/list")
    R<PageR<GljyReportListRSP>> list(@RequestBody @Valid GljyReportListREQ req);

    @ApiOperation("关联交易批量报送")
    @PostMapping("/risk/control/gljy/report/submit")
    R<Void> submit(@RequestBody @Valid GljyReportSubmitREQ req);


    @ApiOperation("关联交易关联方查询；返回最多50条")
    @PostMapping("/risk/control/gljy/report/related/clients")
    R<List<String>> relatedClientList(@RequestBody @Valid GljyReportRelatedClientREQ req);
}
