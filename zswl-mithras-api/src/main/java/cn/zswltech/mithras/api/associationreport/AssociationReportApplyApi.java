package cn.zswltech.mithras.api.associationreport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import javax.validation.Valid;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.associationreport.AssociationReportApplyAddREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationReportApplyModifyREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationReportApplyListREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationReportApplyListRSP;
import cn.zswltech.mithras.dto.associationreport.AssociationReportApplyRemoveREQ;

/**
* @description 金融局报表申请表
* @author hspcadmin
* @date 2025-09-14
*/
@Api(tags = "金融局报表申请表-接口")
public interface AssociationReportApplyApi {

    @ApiOperation("新增金融局报表申请表")
    @PostMapping("/association/report/apply/add")
    R<Void> add(@RequestBody @Valid AssociationReportApplyAddREQ req);

    @ApiOperation("修改金融局报表申请表")
    @PostMapping("/association/report/apply/modify")
    R<Void> modify(@RequestBody @Valid AssociationReportApplyModifyREQ req);

    @ApiOperation("金融局报表申请表列表")
    @PostMapping("/association/report/apply/list")
    R<PageR<AssociationReportApplyListRSP>> list(@RequestBody @Valid AssociationReportApplyListREQ req);

    @ApiOperation("删除金融局报表申请表")
    @PostMapping("/association/report/apply/remove")
    R<Void> remove(@RequestBody @Valid AssociationReportApplyRemoveREQ req);

}