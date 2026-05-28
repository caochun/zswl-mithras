package cn.zswltech.mithras.api.associationreport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import javax.validation.Valid;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.associationreport.AssociationLawInvolvedVisitRelatedInfoLibAddREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationLawInvolvedVisitRelatedInfoLibModifyREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationLawInvolvedVisitRelatedInfoLibListREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationLawInvolvedVisitRelatedInfoLibListRSP;
import cn.zswltech.mithras.dto.associationreport.AssociationLawInvolvedVisitRelatedInfoLibRemoveREQ;

/**
* @description 涉法涉讼涉访信息表(流程节点记录版本表)
* @author hspcadmin
* @date 2025-09-14
*/
@Api(tags = "涉法涉讼涉访信息表(流程节点记录版本表)-接口")
public interface AssociationLawInvolvedVisitRelatedInfoLibApi {

    @ApiOperation("新增涉法涉讼涉访信息表(流程节点记录版本表)")
    @PostMapping("/association/law/involved/visit/related/info/lib/add")
    R<Void> add(@RequestBody @Valid AssociationLawInvolvedVisitRelatedInfoLibAddREQ req);

    @ApiOperation("修改涉法涉讼涉访信息表(流程节点记录版本表)")
    @PostMapping("/association/law/involved/visit/related/info/lib/modify")
    R<Void> modify(@RequestBody @Valid AssociationLawInvolvedVisitRelatedInfoLibModifyREQ req);

    @ApiOperation("涉法涉讼涉访信息表(流程节点记录版本表)列表")
    @PostMapping("/association/law/involved/visit/related/info/lib/list")
    R<PageR<AssociationLawInvolvedVisitRelatedInfoLibListRSP>> list(@RequestBody @Valid AssociationLawInvolvedVisitRelatedInfoLibListREQ req);

    @ApiOperation("删除涉法涉讼涉访信息表(流程节点记录版本表)")
    @PostMapping("/association/law/involved/visit/related/info/lib/remove")
    R<Void> remove(@RequestBody @Valid AssociationLawInvolvedVisitRelatedInfoLibRemoveREQ req);

}