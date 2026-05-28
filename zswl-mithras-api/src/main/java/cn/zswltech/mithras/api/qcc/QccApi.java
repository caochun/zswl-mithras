package cn.zswltech.mithras.api.qcc;

import cn.zswltech.mithras.api.common.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Map;

@Api(tags = "企查查接口")
public interface QccApi {

    @ApiOperation("获取token")
    @PostMapping("/qccApi/generateToken")
    R<Map<String, String>> generateToken() throws Exception;

}


