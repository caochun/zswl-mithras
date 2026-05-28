package cn.zswltech.mithras.api.associationreport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import javax.validation.Valid;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.associationreport.AssociationReportDataAccessAddREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationReportDataAccessModifyREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationReportDataAccessListREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationReportDataAccessListRSP;
import cn.zswltech.mithras.dto.associationreport.AssociationReportDataAccessRemoveREQ;

/**
* @description 金融局报表数据权限
* @author hspcadmin
* @date 2025-09-25
*/
@Api(tags = "金融局报表数据权限-接口")
public interface AssociationReportDataAccessApi {

    @ApiOperation("新增金融局报表数据权限")
    @PostMapping("/association/report/data/access/add")
    R<Void> add(@RequestBody @Valid AssociationReportDataAccessAddREQ req);

    @ApiOperation("修改金融局报表数据权限")
    @PostMapping("/association/report/data/access/modify")
    R<Void> modify(@RequestBody @Valid AssociationReportDataAccessModifyREQ req);

    @ApiOperation("金融局报表数据权限列表")
    @PostMapping("/association/report/data/access/list")
    R<PageR<AssociationReportDataAccessListRSP>> list(@RequestBody @Valid AssociationReportDataAccessListREQ req);

    @ApiOperation("删除金融局报表数据权限")
    @PostMapping("/association/report/data/access/remove")
    R<Void> remove(@RequestBody @Valid AssociationReportDataAccessRemoveREQ req);

}