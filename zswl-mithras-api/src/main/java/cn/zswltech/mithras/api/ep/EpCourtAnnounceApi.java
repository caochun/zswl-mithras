package cn.zswltech.mithras.api.ep;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.ep.EpCourtAnnounceRSP;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 法院公告
 *
 * @author ZHANGXIN
 */
@Api(tags = "法院公告-接口")
public interface EpCourtAnnounceApi {


    @ApiOperation("法院公告详情")
    @GetMapping("/risk/opinion/info/extra/courtAnnounce")
    R<EpCourtAnnounceRSP> getEpCourtAnnounce(@RequestParam Long id);
}
