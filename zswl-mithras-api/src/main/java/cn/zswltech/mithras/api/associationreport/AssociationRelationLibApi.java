package cn.zswltech.mithras.api.associationreport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import javax.validation.Valid;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.associationreport.AssociationRelationLibAddREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationRelationLibModifyREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationRelationLibListREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationRelationLibListRSP;
import cn.zswltech.mithras.dto.associationreport.AssociationRelationLibRemoveREQ;

/**
* @description 金融协会报送-关联方信息汇总表(流程节点记录版本表)
* @author hspcadmin
* @date 2025-09-14
*/
@Api(tags = "金融协会报送-关联方信息汇总表(流程节点记录版本表)-接口")
public interface AssociationRelationLibApi {

    @ApiOperation("新增金融协会报送-关联方信息汇总表(流程节点记录版本表)")
    @PostMapping("/association/relation/lib/add")
    R<Void> add(@RequestBody @Valid AssociationRelationLibAddREQ req);

    @ApiOperation("修改金融协会报送-关联方信息汇总表(流程节点记录版本表)")
    @PostMapping("/association/relation/lib/modify")
    R<Void> modify(@RequestBody @Valid AssociationRelationLibModifyREQ req);

    @ApiOperation("金融协会报送-关联方信息汇总表(流程节点记录版本表)列表")
    @PostMapping("/association/relation/lib/list")
    R<PageR<AssociationRelationLibListRSP>> list(@RequestBody @Valid AssociationRelationLibListREQ req);

    @ApiOperation("删除金融协会报送-关联方信息汇总表(流程节点记录版本表)")
    @PostMapping("/association/relation/lib/remove")
    R<Void> remove(@RequestBody @Valid AssociationRelationLibRemoveREQ req);

}