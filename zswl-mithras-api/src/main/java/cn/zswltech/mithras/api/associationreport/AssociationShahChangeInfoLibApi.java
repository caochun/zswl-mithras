package cn.zswltech.mithras.api.associationreport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import javax.validation.Valid;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.associationreport.AssociationShahChangeInfoLibAddREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationShahChangeInfoLibModifyREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationShahChangeInfoLibListREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationShahChangeInfoLibListRSP;
import cn.zswltech.mithras.dto.associationreport.AssociationShahChangeInfoLibRemoveREQ;

/**
* @description 股东股权信息一览表-股东变更记录(流程节点记录版本表)
* @author hspcadmin
* @date 2025-09-14
*/
@Api(tags = "股东股权信息一览表-股东变更记录(流程节点记录版本表)-接口")
public interface AssociationShahChangeInfoLibApi {

    @ApiOperation("新增股东股权信息一览表-股东变更记录(流程节点记录版本表)")
    @PostMapping("/association/shah/change/info/lib/add")
    R<Void> add(@RequestBody @Valid AssociationShahChangeInfoLibAddREQ req);

    @ApiOperation("修改股东股权信息一览表-股东变更记录(流程节点记录版本表)")
    @PostMapping("/association/shah/change/info/lib/modify")
    R<Void> modify(@RequestBody @Valid AssociationShahChangeInfoLibModifyREQ req);

    @ApiOperation("股东股权信息一览表-股东变更记录(流程节点记录版本表)列表")
    @PostMapping("/association/shah/change/info/lib/list")
    R<PageR<AssociationShahChangeInfoLibListRSP>> list(@RequestBody @Valid AssociationShahChangeInfoLibListREQ req);

    @ApiOperation("删除股东股权信息一览表-股东变更记录(流程节点记录版本表)")
    @PostMapping("/association/shah/change/info/lib/remove")
    R<Void> remove(@RequestBody @Valid AssociationShahChangeInfoLibRemoveREQ req);

}