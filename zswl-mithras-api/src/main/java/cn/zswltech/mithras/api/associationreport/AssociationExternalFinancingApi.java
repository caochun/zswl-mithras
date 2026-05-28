package cn.zswltech.mithras.api.associationreport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import javax.validation.Valid;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.associationreport.AssociationExternalFinancingAddREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationExternalFinancingModifyREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationExternalFinancingListREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationExternalFinancingListRSP;
import cn.zswltech.mithras.dto.associationreport.AssociationExternalFinancingRemoveREQ;

/**
* @description 金融局报送-对外融资信息清单表
* @author vico
* @date 2025-04-18
*/
@Api(tags = "金融局报送-对外融资信息清单表-接口")
public interface AssociationExternalFinancingApi {

    @ApiOperation("新增金融局报送-对外融资信息清单表")
    @PostMapping("/association/external/financing/add")
    R<Void> add(@RequestBody @Valid AssociationExternalFinancingAddREQ req);

    @ApiOperation("修改金融局报送-对外融资信息清单表")
    @PostMapping("/association/external/financing/modify")
    R<Void> modify(@RequestBody @Valid AssociationExternalFinancingModifyREQ req);

    @ApiOperation("金融局报送-对外融资信息清单表列表")
    @PostMapping("/association/external/financing/list")
    R<PageR<AssociationExternalFinancingListRSP>> list(@RequestBody @Valid AssociationExternalFinancingListREQ req);

    @ApiOperation("删除金融局报送-对外融资信息清单表")
    @PostMapping("/association/external/financing/remove")
    R<Void> remove(@RequestBody @Valid AssociationExternalFinancingRemoveREQ req);

}