package cn.zswltech.mithras.api.newftp.draft;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.newftp.NewFtpCommonDetailReq;
import cn.zswltech.mithras.dto.newftp.NewFtpDetailTreasuryBondYieldListRSP;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;

/**
 * @author yangxiong
 * @date 2024/3/29/15:18
 * @description
 */
@Api(tags = "FTP-一年期 shibor 利率编辑区API")
public interface NewFtpShiborInterestRateDraftApi {

    @ApiOperation("详情页-1年期SHIBOR利率列表")
    @PostMapping("/new/ftp/detail/shibor/interest/rate")
    R<PageR<NewFtpDetailTreasuryBondYieldListRSP>> shiborInterest(@RequestBody @Valid NewFtpCommonDetailReq req);

    @ApiOperation("新增1年期SHIBOR利率")
    @PostMapping("/new/ftp/shibor/interest/rate/import")
    R<Void> importFile(@RequestParam("file") MultipartFile file, Long mainId) throws IOException;
}
