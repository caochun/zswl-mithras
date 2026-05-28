package cn.zswltech.mithras.api.associationreport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import javax.validation.Valid;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.associationreport.AssociationMajorMattersBasicReportLibAddREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationMajorMattersBasicReportLibModifyREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationMajorMattersBasicReportLibListREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationMajorMattersBasicReportLibListRSP;
import cn.zswltech.mithras.dto.associationreport.AssociationMajorMattersBasicReportLibRemoveREQ;

/**
* @description 重大事项报告表-基本信息(流程节点记录版本表)
* @author hspcadmin
* @date 2025-09-14
*/
@Api(tags = "重大事项报告表-基本信息(流程节点记录版本表)-接口")
public interface AssociationMajorMattersBasicReportLibApi {

    @ApiOperation("新增重大事项报告表-基本信息(流程节点记录版本表)")
    @PostMapping("/association/major/matters/basic/report/lib/add")
    R<Void> add(@RequestBody @Valid AssociationMajorMattersBasicReportLibAddREQ req);

    @ApiOperation("修改重大事项报告表-基本信息(流程节点记录版本表)")
    @PostMapping("/association/major/matters/basic/report/lib/modify")
    R<Void> modify(@RequestBody @Valid AssociationMajorMattersBasicReportLibModifyREQ req);

    @ApiOperation("重大事项报告表-基本信息(流程节点记录版本表)列表")
    @PostMapping("/association/major/matters/basic/report/lib/list")
    R<PageR<AssociationMajorMattersBasicReportLibListRSP>> list(@RequestBody @Valid AssociationMajorMattersBasicReportLibListREQ req);

    @ApiOperation("删除重大事项报告表-基本信息(流程节点记录版本表)")
    @PostMapping("/association/major/matters/basic/report/lib/remove")
    R<Void> remove(@RequestBody @Valid AssociationMajorMattersBasicReportLibRemoveREQ req);

}