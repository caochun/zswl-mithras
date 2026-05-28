package cn.zswltech.mithras.api.associationreport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import javax.validation.Valid;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.associationreport.AssociationSeniorExecutiveInfoAddREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationSeniorExecutiveInfoModifyREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationSeniorExecutiveInfoListREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationSeniorExecutiveInfoListRSP;
import cn.zswltech.mithras.dto.associationreport.AssociationSeniorExecutiveInfoRemoveREQ;

/**
* @description 高管信息一览表
* @author hspcadmin
* @date 2025-08-26
*/
@Api(tags = "高管信息一览表-接口")
public interface AssociationSeniorExecutiveInfoApi {

    @ApiOperation("新增高管信息一览表")
    @PostMapping("/association/senior/executive/info/add")
    R<Void> add(@RequestBody @Valid AssociationSeniorExecutiveInfoAddREQ req);

    @ApiOperation("修改高管信息一览表")
    @PostMapping("/association/senior/executive/info/modify")
    R<Void> modify(@RequestBody @Valid AssociationSeniorExecutiveInfoModifyREQ req);

    @ApiOperation("高管信息一览表列表")
    @PostMapping("/association/senior/executive/info/list")
    R<PageR<AssociationSeniorExecutiveInfoListRSP>> list(@RequestBody @Valid AssociationSeniorExecutiveInfoListREQ req);

    @ApiOperation("删除高管信息一览表")
    @PostMapping("/association/senior/executive/info/remove")
    R<Void> remove(@RequestBody @Valid AssociationSeniorExecutiveInfoRemoveREQ req);

}