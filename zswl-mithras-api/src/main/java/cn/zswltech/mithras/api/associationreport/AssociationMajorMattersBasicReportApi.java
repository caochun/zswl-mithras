package cn.zswltech.mithras.api.associationreport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import javax.validation.Valid;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.associationreport.AssociationMajorMattersBasicReportAddREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationMajorMattersBasicReportModifyREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationMajorMattersBasicReportListREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationMajorMattersBasicReportListRSP;
import cn.zswltech.mithras.dto.associationreport.AssociationMajorMattersBasicReportRemoveREQ;

/**
* @description 重大事项报告表-基本信息
* @author hspcadmin
* @date 2025-08-27
*/
@Api(tags = "重大事项报告表-基本信息-接口")
public interface AssociationMajorMattersBasicReportApi {

    @ApiOperation("新增重大事项报告表-基本信息")
    @PostMapping("/association/major/matters/basic/report/add")
    R<Void> add(@RequestBody @Valid AssociationMajorMattersBasicReportAddREQ req);

    @ApiOperation("修改重大事项报告表-基本信息")
    @PostMapping("/association/major/matters/basic/report/modify")
    R<Void> modify(@RequestBody @Valid AssociationMajorMattersBasicReportModifyREQ req);

    @ApiOperation("重大事项报告表-基本信息列表")
    @PostMapping("/association/major/matters/basic/report/list")
    R<PageR<AssociationMajorMattersBasicReportListRSP>> list(@RequestBody @Valid AssociationMajorMattersBasicReportListREQ req);

    @ApiOperation("删除重大事项报告表-基本信息")
    @PostMapping("/association/major/matters/basic/report/remove")
    R<Void> remove(@RequestBody @Valid AssociationMajorMattersBasicReportRemoveREQ req);

}