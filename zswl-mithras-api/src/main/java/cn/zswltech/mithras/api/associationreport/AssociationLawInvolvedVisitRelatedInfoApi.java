package cn.zswltech.mithras.api.associationreport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import javax.validation.Valid;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.associationreport.AssociationLawInvolvedVisitRelatedInfoAddREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationLawInvolvedVisitRelatedInfoModifyREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationLawInvolvedVisitRelatedInfoListREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationLawInvolvedVisitRelatedInfoListRSP;
import cn.zswltech.mithras.dto.associationreport.AssociationLawInvolvedVisitRelatedInfoRemoveREQ;

/**
* @description 涉法涉讼涉访信息表
* @author hspcadmin
* @date 2025-08-27
*/
@Api(tags = "涉法涉讼涉访信息表-接口")
public interface AssociationLawInvolvedVisitRelatedInfoApi {

    @ApiOperation("新增涉法涉讼涉访信息表")
    @PostMapping("/association/law/involved/visit/related/info/add")
    R<Void> add(@RequestBody @Valid AssociationLawInvolvedVisitRelatedInfoAddREQ req);

    @ApiOperation("修改涉法涉讼涉访信息表")
    @PostMapping("/association/law/involved/visit/related/info/modify")
    R<Void> modify(@RequestBody @Valid AssociationLawInvolvedVisitRelatedInfoModifyREQ req);

    @ApiOperation("涉法涉讼涉访信息表列表")
    @PostMapping("/association/law/involved/visit/related/info/list")
    R<PageR<AssociationLawInvolvedVisitRelatedInfoListRSP>> list(@RequestBody @Valid AssociationLawInvolvedVisitRelatedInfoListREQ req);

    @ApiOperation("删除涉法涉讼涉访信息表")
    @PostMapping("/association/law/involved/visit/related/info/remove")
    R<Void> remove(@RequestBody @Valid AssociationLawInvolvedVisitRelatedInfoRemoveREQ req);

}