package cn.zswltech.mithras.api.associationreport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import javax.validation.Valid;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.associationreport.AssociationBusinessSituationAddREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationBusinessSituationModifyREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationBusinessSituationListREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationBusinessSituationListRSP;
import cn.zswltech.mithras.dto.associationreport.AssociationBusinessSituationRemoveREQ;

/**
* @description 业务情况表
* @author vico
* @date 2025-04-18
*/
@Api(tags = "业务情况表-接口")
public interface AssociationBusinessSituationApi {

    @ApiOperation("新增业务情况表")
    @PostMapping("/association/business/situation/add")
    R<Void> add(@RequestBody @Valid AssociationBusinessSituationAddREQ req);

    @ApiOperation("修改业务情况表")
    @PostMapping("/association/business/situation/modify")
    R<Void> modify(@RequestBody @Valid AssociationBusinessSituationModifyREQ req);

    @ApiOperation("业务情况表列表")
    @PostMapping("/association/business/situation/list")
    R<PageR<AssociationBusinessSituationListRSP>> list(@RequestBody @Valid AssociationBusinessSituationListREQ req);

    @ApiOperation("删除业务情况表")
    @PostMapping("/association/business/situation/remove")
    R<Void> remove(@RequestBody @Valid AssociationBusinessSituationRemoveREQ req);

}