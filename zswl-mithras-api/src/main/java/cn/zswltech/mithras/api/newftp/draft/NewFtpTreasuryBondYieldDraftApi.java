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
 * @date 2024/3/29/15:28
 * @description
 */
@Api(tags = "FTP-十年期国债编辑区API")
public interface NewFtpTreasuryBondYieldDraftApi {

    @ApiOperation("详情页-十年期国债收益率")
    @PostMapping("/new/ftp/detail/treasury/bond/yield")
    R<PageR<NewFtpDetailTreasuryBondYieldListRSP>> treasuryBondYield(@RequestBody @Valid NewFtpCommonDetailReq req);

    @ApiOperation("导入10年期国债收益率")
    @PostMapping("/new/ftp/treasury/bond/yield/import")
    R<Void> importFile(@RequestParam("file") MultipartFile file, Long mainId) throws IOException;
}
