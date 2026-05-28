package cn.zswltech.mithras.api.stampduty;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.SelectRSP;
import cn.zswltech.mithras.dto.stampduty.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;


@Api(tags = "印花税缴纳明细表")
@RestController
public interface StampDutyApi {

    @ApiOperation("列表")
    @PostMapping("/stampDuty/list")
    R<StampDutyListRSP> listPage(@RequestBody @Valid StampDutyListREQ req);

    @ApiOperation("新增")
    @PostMapping("/stampDuty/add")
    R<StampDutyDetailREQ> add(@RequestBody @Valid StampDutyDetailREQ req);

    @ApiOperation("删除印花税缴纳明细")
    @PostMapping("/stampDuty/delete")
    R<Void> remove(@RequestBody @Valid StampDutyBatchREQ req);

    @ApiOperation("导入印花税缴纳明细")
    @PostMapping("/stampDuty/import")
    R<Void> importExcel(@Valid StampDutyImportREQ stampDutyImportREQ);

    @ApiOperation("检索合同、融资合同信息")
    @PostMapping("/stampDuty/contract/list")
    R<StampDutyListRSP> contracts(@RequestBody @Valid StampDutyContractREQ req);

    @ApiOperation("导出印花税缴纳明细")
    @PostMapping("/stampDuty/export")
    void export(@RequestBody @Valid StampDutyBatchREQ req);

    @ApiOperation("获取印花税部门列表")
    @GetMapping("/stampDuty/orgs")
    R<List<SelectRSP>> orgList(@ApiParam("部门名称") String name, @ApiParam("部门类型：0 公司， 1 业务部门，2 领导层") Integer type);

}
