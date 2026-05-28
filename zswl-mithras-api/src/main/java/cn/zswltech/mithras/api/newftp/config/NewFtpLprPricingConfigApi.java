package cn.zswltech.mithras.api.newftp.config;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.newftp.NewFtpLprPricingAddREQ;
import cn.zswltech.mithras.dto.newftp.NewFtpLprPricingListRSP;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

/**
 * @author yangxiong
 * @date 2024/3/29/15:20
 * @description
 */
@Api(tags = "FTP-LPR定价配置API")
public interface NewFtpLprPricingConfigApi {

    @ApiOperation("LPR定价列表")
    @PostMapping("/new/ftp/lpr/pricing/list")
    R<List<NewFtpLprPricingListRSP>> list();

    @ApiOperation("新增LPR定价")
    @PostMapping("/new/ftp/lpr/pricing/add")
    R<Void> add(@RequestBody @Valid NewFtpLprPricingAddREQ req);
}
