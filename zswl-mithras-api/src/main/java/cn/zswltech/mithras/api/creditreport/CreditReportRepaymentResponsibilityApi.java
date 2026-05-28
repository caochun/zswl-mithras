package cn.zswltech.mithras.api.creditreport;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.creditreport.CreditReportRepaymentResponsibilityListREQ;
import cn.zswltech.mithras.dto.creditreport.CreditReportRepaymentResponsibilityListRSP;
import cn.zswltech.mithras.dto.creditreport.CreditReportRepaymentResponsibilityModifyREQ;
import cn.zswltech.mithras.dto.creditreport.CreditReportRepaymentResponsibilityRemoveREQ;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
* @description 征信报告-相关还款责任信息概要表
* @author vico
* @date 2025-11-14
*/
@Api(tags = "征信报告-相关还款责任信息概要表-接口")
public interface CreditReportRepaymentResponsibilityApi {

    @ApiOperation("修改征信报告-相关还款责任信息概要表")
    @PostMapping("/credit/report/repayment/responsibility/modify")
    R<Void> modify(@RequestBody @Valid CreditReportRepaymentResponsibilityModifyREQ req);

    @ApiOperation("征信报告-相关还款责任信息概要表列表")
    @PostMapping("/credit/report/repayment/responsibility/list")
    R<PageR<CreditReportRepaymentResponsibilityListRSP>> list(@RequestBody @Valid CreditReportRepaymentResponsibilityListREQ req);

    @ApiOperation("删除征信报告-相关还款责任信息概要表")
    @PostMapping("/credit/report/repayment/responsibility/remove")
    R<Void> remove(@RequestBody @Valid CreditReportRepaymentResponsibilityRemoveREQ req);

}