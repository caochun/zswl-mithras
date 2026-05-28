package cn.zswltech.mithras.api.associationreport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import javax.validation.Valid;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.associationreport.AssociationSeniorExecutiveInfoLibAddREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationSeniorExecutiveInfoLibModifyREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationSeniorExecutiveInfoLibListREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationSeniorExecutiveInfoLibListRSP;
import cn.zswltech.mithras.dto.associationreport.AssociationSeniorExecutiveInfoLibRemoveREQ;

/**
* @description 高管信息一览表(流程节点记录版本表)
* @author hspcadmin
* @date 2025-09-14
*/
@Api(tags = "高管信息一览表(流程节点记录版本表)-接口")
public interface AssociationSeniorExecutiveInfoLibApi {

    @ApiOperation("新增高管信息一览表(流程节点记录版本表)")
    @PostMapping("/association/senior/executive/info/lib/add")
    R<Void> add(@RequestBody @Valid AssociationSeniorExecutiveInfoLibAddREQ req);

    @ApiOperation("修改高管信息一览表(流程节点记录版本表)")
    @PostMapping("/association/senior/executive/info/lib/modify")
    R<Void> modify(@RequestBody @Valid AssociationSeniorExecutiveInfoLibModifyREQ req);

    @ApiOperation("高管信息一览表(流程节点记录版本表)列表")
    @PostMapping("/association/senior/executive/info/lib/list")
    R<PageR<AssociationSeniorExecutiveInfoLibListRSP>> list(@RequestBody @Valid AssociationSeniorExecutiveInfoLibListREQ req);

    @ApiOperation("删除高管信息一览表(流程节点记录版本表)")
    @PostMapping("/association/senior/executive/info/lib/remove")
    R<Void> remove(@RequestBody @Valid AssociationSeniorExecutiveInfoLibRemoveREQ req);

}