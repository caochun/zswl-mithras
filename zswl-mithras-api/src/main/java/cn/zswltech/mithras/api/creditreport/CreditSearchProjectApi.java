package cn.zswltech.mithras.api.creditreport;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.creditreport.CreditReportListDTO;
import cn.zswltech.mithras.dto.creditreport.CreditReportProjectReviewAddDTO;
import cn.zswltech.mithras.dto.creditreport.CreditSearchProjectCmd;
import cn.zswltech.mithras.dto.creditreport.CreditSearchProjectQuery;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

@Api(tags = "项目审计-征信查询-接口")
@RequestMapping(path = "/creditreport/project")
public interface CreditSearchProjectApi {


    @ApiOperation("根据项目id反显项目信息和关联客户信息")
    @PostMapping("/showCreditReportByProjId")
    R<CreditReportProjectReviewAddDTO> showCreditReportByProjId(@RequestBody CreditSearchProjectCmd cmd);

    @ApiOperation("征信查询列表")
    @PostMapping("/list")
    R<PageR<CreditReportListDTO>> list(@RequestBody CreditSearchProjectQuery query);

    @ApiOperation("征信查询删除")
    @GetMapping("/delete")
    R<Void> delete(@Valid @Param("id") Long id);

}
