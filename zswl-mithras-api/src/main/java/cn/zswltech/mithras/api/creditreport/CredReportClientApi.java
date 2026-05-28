package cn.zswltech.mithras.api.creditreport;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.creditreport.CreditReportListDTO;
import cn.zswltech.mithras.dto.creditreport.CreditSearchClientQuery;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

@Api(tags = "客户管理-征信报告查询-接口")
@RequestMapping(path = "/creditreport/client")
public interface CredReportClientApi {

    @ApiOperation("征信报告查询列表")
    @PostMapping("/list")
    R<PageR<CreditReportListDTO>> list(@RequestBody CreditSearchClientQuery query);

    @ApiOperation("征信查询删除")
    @GetMapping("/delete")
    R<Void> delete(@Valid @Param("id") Long id);
}
