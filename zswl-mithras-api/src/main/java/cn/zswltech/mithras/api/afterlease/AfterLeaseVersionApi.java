package cn.zswltech.mithras.api.afterlease;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.afterlease.AfterLeaseAdjustDetailREQ;
import cn.zswltech.mithras.dto.afterlease.AfterLeaseCancelREQ;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

@Api(tags = "租后管理-版本管理接口")
public interface AfterLeaseVersionApi {

    /**
     * 租后管理-项目调整信息生效（或提交审批）
     * @param req
     * @return
     */
    @ApiOperation("租后管理-项目调整信息生效（或提交审批）")
    @PostMapping("/after/lease/adjust/effect")
    R<Void> effect(@RequestBody @Valid AfterLeaseAdjustDetailREQ req);


    /**
     * 租后管理-项目调整取消审批
     * @param req
     * @return
     */
    @ApiOperation("租后管理-项目取消")
    @PostMapping("/after/lease/adjust/cancel")
    R<Void> adjustCancel(@RequestBody @Valid AfterLeaseCancelREQ req);


}
