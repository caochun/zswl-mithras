package cn.zswltech.mithras.api.associationreport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import javax.validation.Valid;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.associationreport.AssociationBusinessSituationLibAddREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationBusinessSituationLibModifyREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationBusinessSituationLibListREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationBusinessSituationLibListRSP;
import cn.zswltech.mithras.dto.associationreport.AssociationBusinessSituationLibRemoveREQ;

/**
* @description 业务情况表(流程节点记录版本表)
* @author hspcadmin
* @date 2025-09-14
*/
@Api(tags = "业务情况表(流程节点记录版本表)-接口")
public interface AssociationBusinessSituationLibApi {

    @ApiOperation("新增业务情况表(流程节点记录版本表)")
    @PostMapping("/association/business/situation/lib/add")
    R<Void> add(@RequestBody @Valid AssociationBusinessSituationLibAddREQ req);

    @ApiOperation("修改业务情况表(流程节点记录版本表)")
    @PostMapping("/association/business/situation/lib/modify")
    R<Void> modify(@RequestBody @Valid AssociationBusinessSituationLibModifyREQ req);

    @ApiOperation("业务情况表(流程节点记录版本表)列表")
    @PostMapping("/association/business/situation/lib/list")
    R<PageR<AssociationBusinessSituationLibListRSP>> list(@RequestBody @Valid AssociationBusinessSituationLibListREQ req);

    @ApiOperation("删除业务情况表(流程节点记录版本表)")
    @PostMapping("/association/business/situation/lib/remove")
    R<Void> remove(@RequestBody @Valid AssociationBusinessSituationLibRemoveREQ req);

}