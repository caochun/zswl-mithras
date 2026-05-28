package cn.zswltech.mithras.api.associationreport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import javax.validation.Valid;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.associationreport.AssociationCompanyProfitStatementLibAddREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationCompanyProfitStatementLibModifyREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationCompanyProfitStatementLibListREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationCompanyProfitStatementLibListRSP;
import cn.zswltech.mithras.dto.associationreport.AssociationCompanyProfitStatementLibRemoveREQ;

/**
* @description 公司利润表数据表(流程节点记录版本表)
* @author hspcadmin
* @date 2025-09-14
*/
@Api(tags = "公司利润表数据表(流程节点记录版本表)-接口")
public interface AssociationCompanyProfitStatementLibApi {

    @ApiOperation("新增公司利润表数据表(流程节点记录版本表)")
    @PostMapping("/association/company/profit/statement/lib/add")
    R<Void> add(@RequestBody @Valid AssociationCompanyProfitStatementLibAddREQ req);

    @ApiOperation("修改公司利润表数据表(流程节点记录版本表)")
    @PostMapping("/association/company/profit/statement/lib/modify")
    R<Void> modify(@RequestBody @Valid AssociationCompanyProfitStatementLibModifyREQ req);

    @ApiOperation("公司利润表数据表(流程节点记录版本表)列表")
    @PostMapping("/association/company/profit/statement/lib/list")
    R<PageR<AssociationCompanyProfitStatementLibListRSP>> list(@RequestBody @Valid AssociationCompanyProfitStatementLibListREQ req);

    @ApiOperation("删除公司利润表数据表(流程节点记录版本表)")
    @PostMapping("/association/company/profit/statement/lib/remove")
    R<Void> remove(@RequestBody @Valid AssociationCompanyProfitStatementLibRemoveREQ req);

}