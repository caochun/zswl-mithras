package cn.zswltech.mithras.api.associationreport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import javax.validation.Valid;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.associationreport.AssociationCompanyProfitStatementAddREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationCompanyProfitStatementModifyREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationCompanyProfitStatementListREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationCompanyProfitStatementListRSP;
import cn.zswltech.mithras.dto.associationreport.AssociationCompanyProfitStatementRemoveREQ;

/**
* @description 公司利润表数据表
* @author vico
* @date 2025-04-18
*/
@Api(tags = "公司利润表数据表-接口")
public interface AssociationCompanyProfitStatementApi {

    @ApiOperation("新增公司利润表数据表")
    @PostMapping("/association/company/profit/statement/add")
    R<Void> add(@RequestBody @Valid AssociationCompanyProfitStatementAddREQ req);

    @ApiOperation("修改公司利润表数据表")
    @PostMapping("/association/company/profit/statement/modify")
    R<Void> modify(@RequestBody @Valid AssociationCompanyProfitStatementModifyREQ req);

    @ApiOperation("公司利润表数据表列表")
    @PostMapping("/association/company/profit/statement/list")
    R<PageR<AssociationCompanyProfitStatementListRSP>> list(@RequestBody @Valid AssociationCompanyProfitStatementListREQ req);

    @ApiOperation("删除公司利润表数据表")
    @PostMapping("/association/company/profit/statement/remove")
    R<Void> remove(@RequestBody @Valid AssociationCompanyProfitStatementRemoveREQ req);

}