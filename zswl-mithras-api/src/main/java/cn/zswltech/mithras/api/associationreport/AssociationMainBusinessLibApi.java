package cn.zswltech.mithras.api.associationreport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import javax.validation.Valid;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.associationreport.AssociationMainBusinessLibAddREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationMainBusinessLibModifyREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationMainBusinessLibListREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationMainBusinessLibListRSP;
import cn.zswltech.mithras.dto.associationreport.AssociationMainBusinessLibRemoveREQ;

/**
* @description 金融协会报送-主要业务清单表(流程节点记录版本表)
* @author hspcadmin
* @date 2025-09-14
*/
@Api(tags = "金融协会报送-主要业务清单表(流程节点记录版本表)-接口")
public interface AssociationMainBusinessLibApi {

    @ApiOperation("新增金融协会报送-主要业务清单表(流程节点记录版本表)")
    @PostMapping("/association/main/business/lib/add")
    R<Void> add(@RequestBody @Valid AssociationMainBusinessLibAddREQ req);

    @ApiOperation("修改金融协会报送-主要业务清单表(流程节点记录版本表)")
    @PostMapping("/association/main/business/lib/modify")
    R<Void> modify(@RequestBody @Valid AssociationMainBusinessLibModifyREQ req);

    @ApiOperation("金融协会报送-主要业务清单表(流程节点记录版本表)列表")
    @PostMapping("/association/main/business/lib/list")
    R<PageR<AssociationMainBusinessLibListRSP>> list(@RequestBody @Valid AssociationMainBusinessLibListREQ req);

    @ApiOperation("删除金融协会报送-主要业务清单表(流程节点记录版本表)")
    @PostMapping("/association/main/business/lib/remove")
    R<Void> remove(@RequestBody @Valid AssociationMainBusinessLibRemoveREQ req);

}