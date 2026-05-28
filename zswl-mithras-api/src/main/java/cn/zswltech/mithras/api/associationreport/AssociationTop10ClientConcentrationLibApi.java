package cn.zswltech.mithras.api.associationreport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import javax.validation.Valid;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.associationreport.AssociationTop10ClientConcentrationLibAddREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationTop10ClientConcentrationLibModifyREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationTop10ClientConcentrationLibListREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationTop10ClientConcentrationLibListRSP;
import cn.zswltech.mithras.dto.associationreport.AssociationTop10ClientConcentrationLibRemoveREQ;

/**
* @description 金融协会报送-最大10家客户（含集团）集中度统计表(流程节点记录版本表)
* @author hspcadmin
* @date 2025-09-14
*/
@Api(tags = "金融协会报送-最大10家客户（含集团）集中度统计表(流程节点记录版本表)-接口")
public interface AssociationTop10ClientConcentrationLibApi {

    @ApiOperation("新增金融协会报送-最大10家客户（含集团）集中度统计表(流程节点记录版本表)")
    @PostMapping("/association/top10/client/concentration/lib/add")
    R<Void> add(@RequestBody @Valid AssociationTop10ClientConcentrationLibAddREQ req);

    @ApiOperation("修改金融协会报送-最大10家客户（含集团）集中度统计表(流程节点记录版本表)")
    @PostMapping("/association/top10/client/concentration/lib/modify")
    R<Void> modify(@RequestBody @Valid AssociationTop10ClientConcentrationLibModifyREQ req);

    @ApiOperation("金融协会报送-最大10家客户（含集团）集中度统计表(流程节点记录版本表)列表")
    @PostMapping("/association/top10/client/concentration/lib/list")
    R<PageR<AssociationTop10ClientConcentrationLibListRSP>> list(@RequestBody @Valid AssociationTop10ClientConcentrationLibListREQ req);

    @ApiOperation("删除金融协会报送-最大10家客户（含集团）集中度统计表(流程节点记录版本表)")
    @PostMapping("/association/top10/client/concentration/lib/remove")
    R<Void> remove(@RequestBody @Valid AssociationTop10ClientConcentrationLibRemoveREQ req);

}