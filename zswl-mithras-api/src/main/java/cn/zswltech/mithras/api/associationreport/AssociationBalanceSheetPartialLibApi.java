package cn.zswltech.mithras.api.associationreport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import javax.validation.Valid;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.associationreport.AssociationBalanceSheetPartialLibAddREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationBalanceSheetPartialLibModifyREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationBalanceSheetPartialLibListREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationBalanceSheetPartialLibListRSP;
import cn.zswltech.mithras.dto.associationreport.AssociationBalanceSheetPartialLibRemoveREQ;

/**
* @description 资产负债表(流程节点记录版本表)
* @author hspcadmin
* @date 2025-09-14
*/
@Api(tags = "资产负债表(流程节点记录版本表)-接口")
public interface AssociationBalanceSheetPartialLibApi {

    @ApiOperation("新增资产负债表(流程节点记录版本表)")
    @PostMapping("/association/balance/sheet/partial/lib/add")
    R<Void> add(@RequestBody @Valid AssociationBalanceSheetPartialLibAddREQ req);

    @ApiOperation("修改资产负债表(流程节点记录版本表)")
    @PostMapping("/association/balance/sheet/partial/lib/modify")
    R<Void> modify(@RequestBody @Valid AssociationBalanceSheetPartialLibModifyREQ req);

    @ApiOperation("资产负债表(流程节点记录版本表)列表")
    @PostMapping("/association/balance/sheet/partial/lib/list")
    R<PageR<AssociationBalanceSheetPartialLibListRSP>> list(@RequestBody @Valid AssociationBalanceSheetPartialLibListREQ req);

    @ApiOperation("删除资产负债表(流程节点记录版本表)")
    @PostMapping("/association/balance/sheet/partial/lib/remove")
    R<Void> remove(@RequestBody @Valid AssociationBalanceSheetPartialLibRemoveREQ req);

}