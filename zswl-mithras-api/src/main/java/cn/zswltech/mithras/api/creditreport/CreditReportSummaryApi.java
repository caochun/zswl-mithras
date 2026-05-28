package cn.zswltech.mithras.api.creditreport;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.creditreport.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
* @description 征信报告-信息概要表
* @author vico
* @date 2025-11-14
*/
@Api(tags = "征信报告-信息概要表-接口")
public interface CreditReportSummaryApi {

    @ApiOperation("新增征信报告-信息概要表")
    @PostMapping("/credit/report/summary/add")
    R<Void> add(@RequestBody @Valid CreditReportSummaryAddREQ req);

    @ApiOperation("修改征信报告-信息概要表")
    @PostMapping("/credit/report/summary/modify")
    R<Void> modify(@RequestBody @Valid CreditReportSummaryModifyREQ req);

    @ApiOperation("征信报告-信息概要表列表")
    @PostMapping("/credit/report/summary/list")
    R<PageR<CreditReportSummaryListRSP>> list(@RequestBody @Valid CreditReportSummaryListREQ req);

    @ApiOperation("删除征信报告-信息概要表")
    @PostMapping("/credit/report/summary/remove")
    R<Void> remove(@RequestBody @Valid CreditReportSummaryRemoveREQ req);

    @ApiOperation("征信报告-信息概要表详情")
    @PostMapping("/credit/report/summary/detail")
    R<CreditReportSummaryDetailRSP> detail(@RequestBody @Valid CreditReportBaseDetailREQ req);


}