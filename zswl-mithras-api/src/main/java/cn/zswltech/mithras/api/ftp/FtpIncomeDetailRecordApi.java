package cn.zswltech.mithras.api.ftp;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import javax.validation.Valid;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.ftp.FtpIncomeDetailRecordListREQ;
import cn.zswltech.mithras.dto.ftp.FtpIncomeDetailRecordListRSP;
import cn.zswltech.mithras.dto.ftp.FtpIncomeDetailRecordRemoveREQ;

/**
* @description 资金管理-融资管理-ftp收益记录表
* @author vico
* @date 2025-07-15
*/
@Api(tags = "资金管理-融资管理-ftp收益记录表-接口")
public interface FtpIncomeDetailRecordApi {

    @ApiOperation("删除资金管理-融资管理-ftp收益记录表")
    @PostMapping("/ftp/income/detail/record/remove")
    R<Void> remove(@RequestBody @Valid FtpIncomeDetailRecordRemoveREQ req);

}