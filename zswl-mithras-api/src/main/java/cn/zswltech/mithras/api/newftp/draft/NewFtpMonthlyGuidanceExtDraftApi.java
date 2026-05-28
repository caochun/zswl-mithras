package cn.zswltech.mithras.api.newftp.draft;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.newftp.NewFtpDetailReq;
import cn.zswltech.mithras.dto.newftp.NewFtpMonthlyGuidanceExtDraftDetailRSP;
import cn.zswltech.mithras.dto.newftp.NewFtpMonthlyGuidanceExtDraftModifyREQ;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * @author yangxiong
 * @date 2024/3/29/15:34
 * @description
 */
@Api(tags = "FTP-月度报价文本编辑区API")
public interface NewFtpMonthlyGuidanceExtDraftApi {

    @ApiOperation("修改ftp指导报价扩展表（下半部分）")
    @PostMapping("/new/ftp/monthly/guidance/ext/modify")
    R<Void> modify(@RequestBody @Valid NewFtpMonthlyGuidanceExtDraftModifyREQ req);

    @ApiOperation("ftp指导报价扩展表（下半部分）列表")
    @PostMapping("/new/ftp/monthly/guidance/ext/detail")
    R<NewFtpMonthlyGuidanceExtDraftDetailRSP> detail(@RequestBody @Valid NewFtpDetailReq req);
}
