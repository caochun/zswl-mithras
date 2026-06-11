package cn.zswltech.mithras.api.report;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.report.repay.RepayListREQ;
import cn.zswltech.mithras.dto.report.repay.RepayModifyREQ;
import cn.zswltech.mithras.dto.version.DiffValue;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.Map;

/**
 * 征信报送-还款表接口
 *
 * @author wangchuanhao
 * @date 2023/1/11 3:16 PM
 */
@Api("征信报送-还款表接口")
public interface CrRepayApi {

    @ApiOperation("还款表列表查询")
    @PostMapping("/cr/repay/list")
    R<PageR<Map<String, DiffValue>>> list(@RequestBody @Valid RepayListREQ req);

    @ApiOperation("还款表编辑")
    @PostMapping("/cr/repay/modify")
    R<Void> modify(@RequestBody @Valid RepayModifyREQ req);

}
