package cn.zswltech.mithras.api.newftp.config;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.newftp.NewFtpParameterSettingConfigListRSP;
import cn.zswltech.mithras.dto.newftp.NewFtpParameterSettingListREQ;
import cn.zswltech.mithras.dto.newftp.NewFtpParameterSettingModifyREQ;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * @author yangxiong
 * @date 2024/3/29/15:15
 * @description
 */
@Api(tags = "FTP-参数设置API")
public interface NewFtpParameterSettingConfigApi {

    @ApiOperation("修改ftp参数配置设定表")
    @PostMapping("/new/ftp/parameter/setting/modify")
    R<Void> modify(@RequestBody @Valid List<NewFtpParameterSettingModifyREQ> reqs);

    @ApiOperation("ftp参数配置设定表列表")
    @PostMapping("/new/ftp/parameter/setting/list")
    R<Map<String, List<NewFtpParameterSettingConfigListRSP>>> list(@RequestBody @Valid NewFtpParameterSettingListREQ req);
}
