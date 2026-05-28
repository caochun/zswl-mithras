package cn.zswltech.mithras.api;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.QLExpressREQ;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

/**
 * @author dingqi
 * @date 2023/2/15
 * @description
 */
@Api(tags = "QL表达式相关接口")
@RequestMapping(path = "/qlexpress")
public interface QLExpressApi {
    @ApiOperation("执行表达式")
    @PostMapping("/execute")
    R<String> execute(@RequestBody @Valid QLExpressREQ req);
}
