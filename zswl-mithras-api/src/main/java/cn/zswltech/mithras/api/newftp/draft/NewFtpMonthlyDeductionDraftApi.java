package cn.zswltech.mithras.api.newftp.draft;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.newftp.NewFtpMonthlyDeductionListREQ;
import cn.zswltech.mithras.dto.newftp.NewFtpMonthlyDeductionListRSP;
import cn.zswltech.mithras.dto.newftp.NewFtpMonthlyDeductionModifyREQ;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

/**
 * @author yangxiong
 * @date 2024/3/29/15:31
 * @description
 */
@Api(tags = "FTP-月度指导编辑区API")
public interface NewFtpMonthlyDeductionDraftApi {

    @ApiOperation("修改月度计价指导")
    @PostMapping("/new/ftp/monthly/deduction/modify")
    R<Void> modify(@RequestBody @Valid NewFtpMonthlyDeductionModifyREQ req);

    @ApiOperation("月度计价指导列表")
    @PostMapping("/new/ftp/monthly/deduction/list")
    R<List<NewFtpMonthlyDeductionListRSP>> list(@RequestBody @Valid NewFtpMonthlyDeductionListREQ req);

    @ApiOperation("刷新月度计价指导")
    @PostMapping("/new/ftp/monthly/deduction/refresh")
    R<Void> refresh(@RequestBody @Valid NewFtpMonthlyDeductionListREQ req);

    @ApiOperation("新增月度指导")
    @PostMapping("/new/ftp/monthly/deduction/add")
    R<Void> addMonthlyDeduction(@RequestBody @Valid NewFtpMonthlyDeductionListREQ req);
}
