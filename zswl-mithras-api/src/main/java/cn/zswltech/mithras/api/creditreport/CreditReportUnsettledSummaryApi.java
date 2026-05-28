package cn.zswltech.mithras.api.creditreport;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.creditreport.CreditReportUnsettledSummaryListREQ;
import cn.zswltech.mithras.dto.creditreport.CreditReportUnsettledSummaryListRSP;
import cn.zswltech.mithras.dto.creditreport.CreditReportUnsettledSummaryModifyREQ;
import cn.zswltech.mithras.dto.creditreport.CreditReportUnsettledSummaryRemoveREQ;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

/**
* @description 征信报告-未结清信贷及授信信息表
* @author vico
* @date 2025-11-14
*/
@Api(tags = "征信报告-未结清信贷及授信信息表-接口")
public interface CreditReportUnsettledSummaryApi {

    @ApiOperation("修改征信报告-未结清信贷及授信信息表")
    @PostMapping("/credit/report/unsettled/summary/modify")
    R<Void> modify(@RequestBody @Valid CreditReportUnsettledSummaryModifyREQ req);

    @ApiOperation("征信报告-未结清信贷及授信信息表列表")
    @PostMapping("/credit/report/unsettled/summary/list")
    R<List<CreditReportUnsettledSummaryListRSP>> list(@RequestBody @Valid CreditReportUnsettledSummaryListREQ req);

    @ApiOperation("删除征信报告-未结清信贷及授信信息表")
    @PostMapping("/credit/report/unsettled/summary/remove")
    R<Void> remove(@RequestBody @Valid CreditReportUnsettledSummaryRemoveREQ req);

}