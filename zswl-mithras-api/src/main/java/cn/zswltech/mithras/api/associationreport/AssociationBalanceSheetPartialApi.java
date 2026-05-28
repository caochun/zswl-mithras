package cn.zswltech.mithras.api.associationreport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import javax.validation.Valid;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.associationreport.AssociationBalanceSheetPartialAddREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationBalanceSheetPartialModifyREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationBalanceSheetPartialListREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationBalanceSheetPartialListRSP;
import cn.zswltech.mithras.dto.associationreport.AssociationBalanceSheetPartialRemoveREQ;

/**
* @description 资产负债表
* @author vico
* @date 2025-04-18
*/
@Api(tags = "资产负债表-接口")
public interface AssociationBalanceSheetPartialApi {

    @ApiOperation("新增资产负债表")
    @PostMapping("/association/balance/sheet/partial/add")
    R<Void> add(@RequestBody @Valid AssociationBalanceSheetPartialAddREQ req);

    @ApiOperation("修改资产负债表")
    @PostMapping("/association/balance/sheet/partial/modify")
    R<Void> modify(@RequestBody @Valid AssociationBalanceSheetPartialModifyREQ req);

    @ApiOperation("资产负债表列表")
    @PostMapping("/association/balance/sheet/partial/list")
    R<PageR<AssociationBalanceSheetPartialListRSP>> list(@RequestBody @Valid AssociationBalanceSheetPartialListREQ req);

    @ApiOperation("删除资产负债表")
    @PostMapping("/association/balance/sheet/partial/remove")
    R<Void> remove(@RequestBody @Valid AssociationBalanceSheetPartialRemoveREQ req);

}