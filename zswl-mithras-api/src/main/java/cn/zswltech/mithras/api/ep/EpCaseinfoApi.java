package cn.zswltech.mithras.api.ep;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.ep.EpCaseInfoRSP;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 立案信息
 *
 * @author ZHANGXIN
 */
@Api(tags = "立案信息-接口")
public interface EpCaseinfoApi {

    @ApiOperation("立案信息详情")
    @GetMapping("/risk/opinion/info/extra/caseInfo")
    R<EpCaseInfoRSP> getCaseInfo(@RequestParam Long id);

}
