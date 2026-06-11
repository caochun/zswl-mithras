package cn.zswltech.mithras.api.creditreport;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.creditreport.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

/**
* @description 征信报告-信贷记录明细表
* @author vico
* @date 2025-11-28
*/
@Api(tags = "征信报告-信贷记录明细表-接口")
public interface CreditReportRecordDetailsApi {

    @ApiOperation("新增征信报告-信贷记录明细表")
    @PostMapping("/credit/report/record/details/add")
    R<Void> add(@RequestBody @Valid CreditReportRecordDetailsAddREQ req);

    @ApiOperation("修改征信报告-信贷记录明细表")
    @PostMapping("/credit/report/record/details/modify")
    R<Void> modify(@RequestBody @Valid CreditReportRecordDetailsModifyREQ req);

    @ApiOperation("征信报告-信贷记录明细表列表")
    @PostMapping("/credit/report/record/details/list")
    R<List<CreditReportRecordDetailsListRSP>> list(@RequestBody @Valid CreditReportRecordDetailsListREQ req);

    @ApiOperation("删除征信报告-信贷记录明细表")
    @PostMapping("/credit/report/record/details/remove")
    R<Void> remove(@RequestBody @Valid CreditReportRecordDetailsRemoveREQ req);

}