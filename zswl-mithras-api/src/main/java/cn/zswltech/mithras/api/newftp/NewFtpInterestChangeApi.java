package cn.zswltech.mithras.api.newftp;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.SinglePkREQ;
import cn.zswltech.mithras.dto.newftp.NewFtpInterestChangeApplyRecordRSP;
import cn.zswltech.mithras.dto.newftp.NewFtpInterestChangeApplySaveREQ;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * @author dingqi
 * @date 2025/3/4
 * @description
 */
@Api(tags = "FTP计息变更")
public interface NewFtpInterestChangeApi {
    @ApiOperation("FTP计息变更-申请信息-详情")
    @PostMapping(path = "/new/ftp/interest/change/apply/detail")
    R<NewFtpInterestChangeApplyRecordRSP> detail(@RequestBody SinglePkREQ req);

    @ApiOperation("FTP计息变更-申请信息-保存")
    @PostMapping(path = "/new/ftp/interest/change/apply/save")
    R<Long> save(@RequestBody @Valid NewFtpInterestChangeApplySaveREQ req);

    @ApiOperation("FTP计息变更-申请信息-提交审批")
    @PostMapping("/new/ftp/interest/change/apply/submit")
    R<String> submit(@RequestBody @Valid SinglePkREQ req);
}
