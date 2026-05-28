package cn.zswltech.mithras.api.kpi;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.kpi.KpiProjectDistributionBaseInfoModifyREQ;
import cn.zswltech.mithras.dto.kpi.KpiProjectDistributionBaseInfoREQ;
import cn.zswltech.mithras.dto.kpi.KpiProjectDistributionBaseInfoRSP;
import cn.zswltech.mithras.dto.kpi.KpiProjectDistributionGetProcessRSP;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

/**
 * @author dingqi
 * @date 2023/6/14
 * @description
 */
@Api(tags = "绩效考核-项目分配-基本信息")
@RequestMapping(path = "/kpi/projectdistribution/baseinfo")
public interface KpiProjectDistributionBaseInfoApi {
    @ApiOperation("绩效考核-项目分配-基本信息-详情")
    @PostMapping("/detail")
    R<KpiProjectDistributionBaseInfoRSP> detail(@RequestBody @Valid KpiProjectDistributionBaseInfoREQ req);

    @ApiOperation("绩效考核-项目分配-基本信息-修改")
    @PostMapping("/modify")
    R<Void> modify(@RequestBody @Valid KpiProjectDistributionBaseInfoModifyREQ req);

    @ApiOperation("绩效考核-项目分配-基本信息-流程信息")
    @PostMapping("/get/process")
    R<KpiProjectDistributionGetProcessRSP> getProcess(@RequestBody @Valid KpiProjectDistributionBaseInfoREQ req);

}
