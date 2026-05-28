package cn.zswltech.mithras.api.creditreport;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.creditreport.CreditReportLimitListREQ;
import cn.zswltech.mithras.dto.creditreport.CreditReportLimitListRSP;
import cn.zswltech.mithras.dto.creditreport.CreditReportLimitModifyREQ;
import cn.zswltech.mithras.dto.creditreport.CreditReportLimitRemoveREQ;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
* @description 征信报告-信用额度表
* @author vico
* @date 2025-11-14
*/
@Api(tags = "征信报告-信用额度表-接口")
public interface CreditReportLimitApi {

    @ApiOperation("修改征信报告-信用额度表")
    @PostMapping("/credit/report/limit/modify")
    R<Void> modify(@RequestBody @Valid CreditReportLimitModifyREQ req);

    @ApiOperation("征信报告-信用额度表列表")
    @PostMapping("/credit/report/limit/list")
    R<PageR<CreditReportLimitListRSP>> list(@RequestBody @Valid CreditReportLimitListREQ req);

    @ApiOperation("删除征信报告-信用额度表")
    @PostMapping("/credit/report/limit/remove")
    R<Void> remove(@RequestBody @Valid CreditReportLimitRemoveREQ req);

}