package cn.zswltech.mithras.api.report;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.report.overduerecord.OverdueRecordListREQ;
import cn.zswltech.mithras.dto.report.overduerecord.OverdueRecordListRSP;
import cn.zswltech.mithras.dto.report.overduerecord.OverdueRecordModifyREQ;
import cn.zswltech.mithras.dto.report.overduerecord.OverdueRecordRemoveREQ;
import cn.zswltech.mithras.dto.version.DiffFile;
import cn.zswltech.mithras.dto.version.DiffValue;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.Map;

/**
 * 征信报送-逾期表接口
 *
 * @author wangchuanhao
 * @date 2023/1/11 3:16 PM
 */
@Api("征信报送-逾期表接口")
public interface CrOverdueRecordApi {

    @ApiOperation("逾期表列表查询")
    @PostMapping("/cr/overdue/record/list")
    R<PageR<Map<String, DiffValue>>> list(@RequestBody @Valid OverdueRecordListREQ req);

    @ApiOperation("逾期表编辑")
    @PostMapping("/cr/overdue/record/modify")
    R<Void> modify(@RequestBody @Valid OverdueRecordModifyREQ req);

    @ApiOperation("逾期表删除")
    @PostMapping("/cr/overdue/record/remove")
    R<Void> remove(@RequestBody @Valid OverdueRecordRemoveREQ req);

    @ApiOperation("逾期表取消删除")
    @PostMapping("/cr/overdue/record/cancel/remove")
    R<Void> cancelRemove(@RequestBody @Valid OverdueRecordRemoveREQ req);
}
