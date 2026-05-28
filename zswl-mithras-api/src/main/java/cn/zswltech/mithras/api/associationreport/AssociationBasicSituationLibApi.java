package cn.zswltech.mithras.api.associationreport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import javax.validation.Valid;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.associationreport.AssociationBasicSituationLibAddREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationBasicSituationLibModifyREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationBasicSituationLibListREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationBasicSituationLibListRSP;
import cn.zswltech.mithras.dto.associationreport.AssociationBasicSituationLibRemoveREQ;

/**
* @description 基本情况统计((流程节点记录版本表))
* @author hspcadmin
* @date 2025-09-14
*/
@Api(tags = "基本情况统计((流程节点记录版本表))-接口")
public interface AssociationBasicSituationLibApi {

    @ApiOperation("新增基本情况统计((流程节点记录版本表))")
    @PostMapping("/association/basic/situation/lib/add")
    R<Void> add(@RequestBody @Valid AssociationBasicSituationLibAddREQ req);

    @ApiOperation("修改基本情况统计((流程节点记录版本表))")
    @PostMapping("/association/basic/situation/lib/modify")
    R<Void> modify(@RequestBody @Valid AssociationBasicSituationLibModifyREQ req);

    @ApiOperation("基本情况统计((流程节点记录版本表))列表")
    @PostMapping("/association/basic/situation/lib/list")
    R<PageR<AssociationBasicSituationLibListRSP>> list(@RequestBody @Valid AssociationBasicSituationLibListREQ req);

    @ApiOperation("删除基本情况统计((流程节点记录版本表))")
    @PostMapping("/association/basic/situation/lib/remove")
    R<Void> remove(@RequestBody @Valid AssociationBasicSituationLibRemoveREQ req);

}