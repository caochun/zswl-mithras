package cn.zswltech.mithras.api.newftp.draft;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.newftp.NewFtpDetailReq;
import cn.zswltech.mithras.dto.newftp.NewFtpQuarterlyBasePricingDetailRsp;
import cn.zswltech.mithras.dto.newftp.NewFtpQuarterlyBasePricingModifyREQ;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * @author yangxiong
 * @date 2024/3/29/15:36
 * @description
 */
@Api(tags = "FTP-季度报价编辑区API")
public interface NewFtpQuarterlyBasePricingDraftApi {

    @ApiOperation("修改季度指导基础定价")
    @PostMapping("/new/ftp/quarterly/base/pricing/modify")
    R<Void> modify(@RequestBody @Valid NewFtpQuarterlyBasePricingModifyREQ req);

    @ApiOperation("季度指导基础定价列表")
    @PostMapping("/new/ftp/quarterly/base/pricing/detail")
    R<NewFtpQuarterlyBasePricingDetailRsp> detail(@RequestBody @Valid NewFtpDetailReq req);

    @ApiOperation("季度指导基础定价列表")
    @GetMapping("/new/ftp/base/pricing/downLoad")
    R<Void> downLoad(@Valid NewFtpDetailReq req);
}
