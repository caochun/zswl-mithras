package cn.zswltech.mithras.api.ep;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.ep.EpChangeInfoRSP;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 企业信息变更
 *
 * @author ZHANGXIN
 */
@Api(tags = "企业信息变更-接口")
public interface EpChangeInfoApi {


    @ApiOperation("企业信息变更详情")
    @GetMapping("/risk/opinion/info/extra/changeInfo")
    R<EpChangeInfoRSP> getEpChangeInfo(@RequestParam Long id);

}
