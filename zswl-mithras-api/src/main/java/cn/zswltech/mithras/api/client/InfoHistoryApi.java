package cn.zswltech.mithras.api.client;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.client.infohistory.InfoHistoryDetailREQ;
import cn.zswltech.mithras.dto.client.infohistory.InfoHistoryDetailRSP;
import cn.zswltech.mithras.dto.client.infohistory.InfoHistoryListREQ;
import cn.zswltech.mithras.dto.client.infohistory.InfoHistoryListRSP;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * @author luyi
 */
//@Api("变更历史-接口")
public interface InfoHistoryApi {

    @ApiOperation("变更历史列表")
    @PostMapping("/corp/info/history/list")
    R<PageR<InfoHistoryListRSP>> list(@RequestBody @Valid InfoHistoryListREQ req);

    @ApiOperation("变更历史详情")
    @PostMapping("/corp/info/history/detail")
    R<InfoHistoryDetailRSP> detail(@RequestBody @Valid InfoHistoryDetailREQ req);
}
