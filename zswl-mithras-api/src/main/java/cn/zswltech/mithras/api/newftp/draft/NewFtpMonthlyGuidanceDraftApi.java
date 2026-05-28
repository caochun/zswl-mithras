package cn.zswltech.mithras.api.newftp.draft;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.newftp.NewFtpDetailReq;
import cn.zswltech.mithras.dto.newftp.NewFtpMonthlyGuidanceDetaiRsp;
import cn.zswltech.mithras.dto.newftp.NewFtpMonthlyGuidanceModifyREQ;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * @author yangxiong
 * @date 2024/3/29/15:33
 * @description
 */
@Api(tags = "FTP-月度报价编辑区API")
public interface NewFtpMonthlyGuidanceDraftApi {

    @ApiOperation("修改ftp报价表")
    @PostMapping("/new/ftp/monthly/guidance/modify")
    R<Void> modify(@RequestBody @Valid NewFtpMonthlyGuidanceModifyREQ req);

    @ApiOperation("ftp报价表列表")
    @PostMapping("/new/ftp/monthly/guidance/detail")
    R<NewFtpMonthlyGuidanceDetaiRsp> detail(@RequestBody @Valid NewFtpDetailReq req);

    @ApiOperation("ftp报价新增")
    @PostMapping("/new/ftp/monthly/guidance/add")
    R<Void> monthlyGuidanceAdd(@NotNull @RequestBody  NewFtpDetailReq req);
}
