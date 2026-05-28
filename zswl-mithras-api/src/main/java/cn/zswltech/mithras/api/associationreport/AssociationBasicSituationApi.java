package cn.zswltech.mithras.api.associationreport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import javax.validation.Valid;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.associationreport.AssociationBasicSituationAddREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationBasicSituationModifyREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationBasicSituationListREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationBasicSituationListRSP;
import cn.zswltech.mithras.dto.associationreport.AssociationBasicSituationRemoveREQ;

/**
* @description 基本情况统计表
* @author hspcadmin
* @date 2025-08-22
*/
@Api(tags = "基本情况统计表-接口")
public interface AssociationBasicSituationApi {

    @ApiOperation("新增基本情况统计表")
    @PostMapping("/association/basic/situation/add")
    R<Void> add(@RequestBody @Valid AssociationBasicSituationAddREQ req);

    @ApiOperation("修改基本情况统计表")
    @PostMapping("/association/basic/situation/modify")
    R<Void> modify(@RequestBody @Valid AssociationBasicSituationModifyREQ req);

    @ApiOperation("基本情况统计表列表")
    @PostMapping("/association/basic/situation/list")
    R<PageR<AssociationBasicSituationListRSP>> list(@RequestBody @Valid AssociationBasicSituationListREQ req);

    @ApiOperation("删除基本情况统计表")
    @PostMapping("/association/basic/situation/remove")
    R<Void> remove(@RequestBody @Valid AssociationBasicSituationRemoveREQ req);

}