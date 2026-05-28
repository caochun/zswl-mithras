package cn.zswltech.mithras.api.ftp;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.ftp.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

/**
* @description 资金管理-融资管理-ftp收益表
* @author vico
* @date 2025-07-15
*/
@Api(tags = "资金管理-融资管理-ftp收益表-接口")
public interface FtpIncomeBaseInfoApi {

    @ApiOperation("资金管理-融资管理-ftp收益表列表")
    @PostMapping("/ftp/income/base/info/list")
    R<PageR<FtpIncomeBaseInfoListRSP>> list(@RequestBody @Valid FtpIncomeBaseInfoListREQ req);

    @ApiOperation("资金管理-融资管理-ftp收益表统计")
    @PostMapping("/ftp/income/base/info/count")
    R<FtpIncomeBaseInfoListRSP> count(@RequestBody @Valid FtpIncomeBaseInfoListREQ req);

    @ApiOperation("资金管理-融资管理-ftp收益表详情")
    @PostMapping("/ftp/income/base/info/detail")
    R<FtpIncomeBaseInfoListRSP> detail(@RequestBody @Valid FtpIncomeDetailRecordListREQ req);

    @ApiOperation("资金管理-融资管理-ftp收益记录表列表")
    @PostMapping("/ftp/income/detail/record/list")
    R<List<FtpIncomeDetailRecordListRSP>> recordList(@RequestBody @Valid FtpIncomeDetailRecordListREQ req);

    @ApiOperation("资金管理-融资管理-ftp收益记录获取融资机构")
    @PostMapping("/ftp/income/organization/list")
    R<List<FtpIncomeOrganizationListRSP>> organizationList(@RequestBody @Valid FtpIncomeOrganizationListREQ req);


}