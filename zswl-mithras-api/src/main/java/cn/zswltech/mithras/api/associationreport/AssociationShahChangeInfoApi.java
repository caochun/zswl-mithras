package cn.zswltech.mithras.api.associationreport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import javax.validation.Valid;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.associationreport.AssociationShahChangeInfoAddREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationShahChangeInfoModifyREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationShahChangeInfoListREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationShahChangeInfoListRSP;
import cn.zswltech.mithras.dto.associationreport.AssociationShahChangeInfoRemoveREQ;

/**
* @description 股东股权信息一览表-股东变更记录
* @author hspcadmin
* @date 2025-08-25
*/
@Api(tags = "股东股权信息一览表-股东变更记录-接口")
public interface AssociationShahChangeInfoApi {

    @ApiOperation("新增股东股权信息一览表-股东变更记录")
    @PostMapping("/association/shah/change/info/add")
    R<Void> add(@RequestBody @Valid AssociationShahChangeInfoAddREQ req);

    @ApiOperation("修改股东股权信息一览表-股东变更记录")
    @PostMapping("/association/shah/change/info/modify")
    R<Void> modify(@RequestBody @Valid AssociationShahChangeInfoModifyREQ req);

    @ApiOperation("股东股权信息一览表-股东变更记录列表")
    @PostMapping("/association/shah/change/info/list")
    R<PageR<AssociationShahChangeInfoListRSP>> list(@RequestBody @Valid AssociationShahChangeInfoListREQ req);

    @ApiOperation("删除股东股权信息一览表-股东变更记录")
    @PostMapping("/association/shah/change/info/remove")
    R<Void> remove(@RequestBody @Valid AssociationShahChangeInfoRemoveREQ req);

}