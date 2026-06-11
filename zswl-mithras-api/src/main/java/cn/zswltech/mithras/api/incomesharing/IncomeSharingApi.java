package cn.zswltech.mithras.api.incomesharing;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.incomesharing.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

/**
 * 收入分摊表
 *
 * @author yupengfei
 * @date 2024/6/7 10:42
 */
@Api(tags = "收入分摊表")
@RequestMapping("incomeSharing")
public interface IncomeSharingApi {

    @ApiOperation(value = "列表信息")
    @PostMapping("list")
    R<PageR<IncomeSharingListRSP>> incomeSharingList(@RequestBody @Valid IncomeSharingListREQ req);

    @ApiOperation(value = "收入分摊详细列表")
    @PostMapping("detail")
    R<PageR<IncomeSharingRSP>> incomeSharingDetail(@RequestBody @Valid IncomeSharingDetailREQ req);

    @ApiOperation(value = "列表下载")
    @PostMapping("list/download")
    void incomeSharingDownload(@RequestBody @Valid IncomeSharingListREQ req);

    @ApiOperation(value = "详情下载")
    @PostMapping("detail/download")
    void incomeSharingDetailDownload(@RequestBody @Valid IncomeSharingDetailREQ req);
}
