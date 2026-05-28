package cn.zswltech.mithras.api.newftp.draft;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.ftp.FtpGuidanceIdReq;
import cn.zswltech.mithras.dto.newftp.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * @author dingqi
 * @date 2025/7/16
 * @description
 */
@Api(tags = "FTP-季度最低收益率扩表表（下半部分）API")
public interface NewFtpQuarterlyBasePricingExtDraftApi {
    @ApiOperation("修改ftp季度最低收益率扩展表（下半部分）")
    @PostMapping("/new/ftp/quarterly/base/pricing/ext/modify")
    R<Void> modify(@RequestBody @Valid NewFtpQuarterlyBasePricingExtDraftModifyREQ req);

    @ApiOperation("ftp季度最低收益率扩展表（下半部分）")
    @PostMapping("/new/ftp/quarterly/base/pricing/ext/detail")
    R<NewFtpQuarterlyBasePricingExtDraftDetailRSP> detail(@RequestBody @Valid FtpGuidanceIdReq req);
}
