package cn.zswltech.mithras.api;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.SystemUserAuthCodeRSP;
import cn.zswltech.mithras.dto.SystemUserOperateLogREQ;
import cn.zswltech.mithras.dto.SystemUserOperateLogRSP;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * @author dingqi
 * @date 2024/9/10
 * @description
 */
@Api(tags = "系统用户接口")
public interface SystemUserOperateLogApi {
    @ApiOperation("用户操作日志")
    @PostMapping(path = "/system/operatelog")
    R<PageR<SystemUserOperateLogRSP>> pageList(@RequestBody @Valid SystemUserOperateLogREQ req);


}
