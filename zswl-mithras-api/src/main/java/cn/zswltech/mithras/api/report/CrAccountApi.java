package cn.zswltech.mithras.api.report;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.report.ReportChangeREQ;
import cn.zswltech.mithras.dto.report.account.AccountListREQ;
import cn.zswltech.mithras.dto.report.account.AccountListRSP;
import cn.zswltech.mithras.dto.report.account.AccountModifyREQ;
import cn.zswltech.mithras.dto.version.DiffValue;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 征信报送-账户表接口
 *
 * @author wangchuanhao
 * @date 2023/1/11 3:16 PM
 */
@Api("征信报送-账户表接口")
public interface CrAccountApi {

    @ApiOperation("账户表列表查询")
    @PostMapping("/cr/account/list")
    R<PageR<Map<String, DiffValue>>> list(@RequestBody @Valid AccountListREQ req);

    @ApiOperation("账户表编辑")
    @PostMapping("/cr/account/modify")
    R<Void> modify(@RequestBody @Valid AccountModifyREQ req);

    @ApiOperation("账户表报送状态变更")
    @PostMapping("/cr/account/reportChange")
    R<Void> reportChange(@RequestBody @Valid ReportChangeREQ req);

}
