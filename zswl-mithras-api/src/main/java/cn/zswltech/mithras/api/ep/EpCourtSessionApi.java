package cn.zswltech.mithras.api.ep;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.ep.EpCourtSessionRSP;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 开庭公告
 *
 * @author ZHANGXIN
 */
@Api(tags = "开庭公告-接口")
public interface EpCourtSessionApi {

    @ApiOperation("开庭公告详情")
    @GetMapping("/risk/opinion/info/extra/courtSession")
    R<EpCourtSessionRSP> getEpCourtSession(@RequestParam Long id);
}
