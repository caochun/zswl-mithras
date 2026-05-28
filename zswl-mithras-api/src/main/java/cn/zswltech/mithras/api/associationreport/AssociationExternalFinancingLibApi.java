package cn.zswltech.mithras.api.associationreport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import javax.validation.Valid;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.associationreport.AssociationExternalFinancingLibAddREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationExternalFinancingLibModifyREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationExternalFinancingLibListREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationExternalFinancingLibListRSP;
import cn.zswltech.mithras.dto.associationreport.AssociationExternalFinancingLibRemoveREQ;

/**
* @description 金融局报送-对外融资信息清单表(流程节点记录版本表)
* @author hspcadmin
* @date 2025-09-14
*/
@Api(tags = "金融局报送-对外融资信息清单表(流程节点记录版本表)-接口")
public interface AssociationExternalFinancingLibApi {

    @ApiOperation("新增金融局报送-对外融资信息清单表(流程节点记录版本表)")
    @PostMapping("/association/external/financing/lib/add")
    R<Void> add(@RequestBody @Valid AssociationExternalFinancingLibAddREQ req);

    @ApiOperation("修改金融局报送-对外融资信息清单表(流程节点记录版本表)")
    @PostMapping("/association/external/financing/lib/modify")
    R<Void> modify(@RequestBody @Valid AssociationExternalFinancingLibModifyREQ req);

    @ApiOperation("金融局报送-对外融资信息清单表(流程节点记录版本表)列表")
    @PostMapping("/association/external/financing/lib/list")
    R<PageR<AssociationExternalFinancingLibListRSP>> list(@RequestBody @Valid AssociationExternalFinancingLibListREQ req);

    @ApiOperation("删除金融局报送-对外融资信息清单表(流程节点记录版本表)")
    @PostMapping("/association/external/financing/lib/remove")
    R<Void> remove(@RequestBody @Valid AssociationExternalFinancingLibRemoveREQ req);

}