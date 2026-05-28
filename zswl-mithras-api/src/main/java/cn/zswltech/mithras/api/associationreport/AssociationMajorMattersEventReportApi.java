package cn.zswltech.mithras.api.associationreport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import javax.validation.Valid;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.associationreport.AssociationMajorMattersEventReportAddREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationMajorMattersEventReportModifyREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationMajorMattersEventReportListREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationMajorMattersEventReportListRSP;
import cn.zswltech.mithras.dto.associationreport.AssociationMajorMattersEventReportRemoveREQ;

/**
* @description 重大事项报告表-重大事项报告情况
* @author hspcadmin
* @date 2025-08-27
*/
@Api(tags = "重大事项报告表-重大事项报告情况-接口")
public interface AssociationMajorMattersEventReportApi {

    @ApiOperation("新增重大事项报告表-重大事项报告情况")
    @PostMapping("/association/major/matters/event/report/add")
    R<Void> add(@RequestBody @Valid AssociationMajorMattersEventReportAddREQ req);

    @ApiOperation("修改重大事项报告表-重大事项报告情况")
    @PostMapping("/association/major/matters/event/report/modify")
    R<Void> modify(@RequestBody @Valid AssociationMajorMattersEventReportModifyREQ req);

    @ApiOperation("重大事项报告表-重大事项报告情况列表")
    @PostMapping("/association/major/matters/event/report/list")
    R<PageR<AssociationMajorMattersEventReportListRSP>> list(@RequestBody @Valid AssociationMajorMattersEventReportListREQ req);

    @ApiOperation("删除重大事项报告表-重大事项报告情况")
    @PostMapping("/association/major/matters/event/report/remove")
    R<Void> remove(@RequestBody @Valid AssociationMajorMattersEventReportRemoveREQ req);

}