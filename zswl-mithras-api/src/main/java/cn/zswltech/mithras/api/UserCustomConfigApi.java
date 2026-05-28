package cn.zswltech.mithras.api;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.usercustomconfig.UserCustomConfigDetailREQ;
import cn.zswltech.mithras.dto.usercustomconfig.UserCustomConfigDetailRSP;
import cn.zswltech.mithras.dto.usercustomconfig.UserCustomConfigSaveREQ;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.util.List;

/**
 * @author dingqi
 * @date 2025/2/11
 * @description
 */
@Api(tags = "用户自定义配置")
@RequestMapping(path = "/user/custom/config")
public interface UserCustomConfigApi {
    @ApiOperation(value = "用户自定义配置-保存")
    @PostMapping(path = "/save")
    R<Void> save(@RequestBody @Valid UserCustomConfigSaveREQ req);

    @ApiOperation(value = "用户自定义配置-查询")
    @PostMapping(path = "/query")
    R<List<UserCustomConfigDetailRSP>> query(@RequestBody @Valid UserCustomConfigDetailREQ req);
}
