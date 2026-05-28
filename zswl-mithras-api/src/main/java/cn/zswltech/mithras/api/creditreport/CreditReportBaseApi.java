package cn.zswltech.mithras.api.creditreport;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.client.client.ClientInfo;
import cn.zswltech.mithras.dto.creditreport.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.util.List;

@Api(tags = "征信管理-征信报告查询-接口")
@RequestMapping(path = "/creditreport/base")
public interface CreditReportBaseApi {

    @ApiOperation("查询有征信报告查询权限的客户列表信息")
    @GetMapping("/clientInfo")
    R<List<ClientInfo>> getClientInfo(@Param("clientName") String clientName, @Param("creditReportId") Long creditReportId);

    @ApiOperation("根据客户id反显客户信息和关联项目信息")
    @GetMapping("/showCreditReportByClientId")
    R<CreditReportClientAddDTO> showCreditReportByClientId(@Param("clientId") Long clientId);

    @ApiOperation("新增征信报告查询")
    @PostMapping("/add")
    R<Void> add(@Valid @RequestBody CreditReportAddCmd cmd);

    @ApiOperation("征信报告查询列表")
    @PostMapping("/list")
    R<PageR<CreditReportListDTO>> list(@RequestBody CreditReportListREQ req);

    @ApiOperation("征信报告查询详情")
    @GetMapping("/detail")
    R<CreditReportDetailDTO> detail(@Param("id") Long id);


    @ApiOperation("征信报告查询详情保存")
    @PostMapping("/save")
    R<Void> save(@Valid @RequestBody CreditReportSaveCmd cmd);

    @ApiOperation("征信报告查询提交")
    @PostMapping("/submit")
    R<List<CreditReportSubmitDTO>> submit(@RequestBody CreditReportSubmitCmd cmd);

    @ApiOperation("征信查询删除")
    @GetMapping("/delete")
    R<Void> delete(@Valid @Param("id") Long id);


    @ApiOperation("客户比对承租人及担保人工商信息")
    @PostMapping("/creditSearch/client/compare/business")
    R<List<CreditSearchCompareBusinessDTO>> compareBusiness(@RequestBody @Valid CreditSearchCompareBusinessCmd cmd);


    @ApiOperation("征信查询批量导出")
    @PostMapping("/export")
    void export(@RequestBody @Valid CreditReportListREQ req);
}
