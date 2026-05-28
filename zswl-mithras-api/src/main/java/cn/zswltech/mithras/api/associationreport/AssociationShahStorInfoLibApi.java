package cn.zswltech.mithras.api.associationreport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import javax.validation.Valid;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.associationreport.AssociationShahStorInfoLibAddREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationShahStorInfoLibModifyREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationShahStorInfoLibListREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationShahStorInfoLibListRSP;
import cn.zswltech.mithras.dto.associationreport.AssociationShahStorInfoLibRemoveREQ;

/**
* @description 股东股权信息一览表-股东股权信息(流程节点记录版本表)
* @author hspcadmin
* @date 2025-09-14
*/
@Api(tags = "股东股权信息一览表-股东股权信息(流程节点记录版本表)-接口")
public interface AssociationShahStorInfoLibApi {

    @ApiOperation("新增股东股权信息一览表-股东股权信息(流程节点记录版本表)")
    @PostMapping("/association/shah/stor/info/lib/add")
    R<Void> add(@RequestBody @Valid AssociationShahStorInfoLibAddREQ req);

    @ApiOperation("修改股东股权信息一览表-股东股权信息(流程节点记录版本表)")
    @PostMapping("/association/shah/stor/info/lib/modify")
    R<Void> modify(@RequestBody @Valid AssociationShahStorInfoLibModifyREQ req);

    @ApiOperation("股东股权信息一览表-股东股权信息(流程节点记录版本表)列表")
    @PostMapping("/association/shah/stor/info/lib/list")
    R<PageR<AssociationShahStorInfoLibListRSP>> list(@RequestBody @Valid AssociationShahStorInfoLibListREQ req);

    @ApiOperation("删除股东股权信息一览表-股东股权信息(流程节点记录版本表)")
    @PostMapping("/association/shah/stor/info/lib/remove")
    R<Void> remove(@RequestBody @Valid AssociationShahStorInfoLibRemoveREQ req);

}