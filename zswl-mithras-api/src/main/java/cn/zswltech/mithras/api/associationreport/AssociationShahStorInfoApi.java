package cn.zswltech.mithras.api.associationreport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import javax.validation.Valid;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.associationreport.AssociationShahStorInfoAddREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationShahStorInfoModifyREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationShahStorInfoListREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationShahStorInfoListRSP;
import cn.zswltech.mithras.dto.associationreport.AssociationShahStorInfoRemoveREQ;

/**
* @description 股东股权信息一览表-股东股权信息
* @author hspcadmin
* @date 2025-08-25
*/
@Api(tags = "股东股权信息一览表-股东股权信息-接口")
public interface AssociationShahStorInfoApi {

    @ApiOperation("新增股东股权信息一览表-股东股权信息")
    @PostMapping("/association/shah/stor/info/add")
    R<Void> add(@RequestBody @Valid AssociationShahStorInfoAddREQ req);

    @ApiOperation("修改股东股权信息一览表-股东股权信息")
    @PostMapping("/association/shah/stor/info/modify")
    R<Void> modify(@RequestBody @Valid AssociationShahStorInfoModifyREQ req);

    @ApiOperation("股东股权信息一览表-股东股权信息列表")
    @PostMapping("/association/shah/stor/info/list")
    R<PageR<AssociationShahStorInfoListRSP>> list(@RequestBody @Valid AssociationShahStorInfoListREQ req);

    @ApiOperation("删除股东股权信息一览表-股东股权信息")
    @PostMapping("/association/shah/stor/info/remove")
    R<Void> remove(@RequestBody @Valid AssociationShahStorInfoRemoveREQ req);

}