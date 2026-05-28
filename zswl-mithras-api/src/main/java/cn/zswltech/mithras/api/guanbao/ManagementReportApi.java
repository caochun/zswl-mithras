package cn.zswltech.mithras.api.guanbao;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.ReportGroupListRSP;
import cn.zswltech.mithras.dto.ReportSelectRSP;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * @create: 2023-01-04
 **/
@Api(tags = "管报接口")
public interface ManagementReportApi {
    @ApiOperation("获取管报列表")
    @GetMapping("/management/report/list")
    R<List<ReportSelectRSP>> reportList();

    @ApiOperation("获取管报列表-分组")
    @GetMapping("/management/report/group/list")
    R<List<ReportGroupListRSP>> reportGroupList();

    @ApiOperation("获取管报列表")
    @GetMapping("/management/report/refresh")
    R<Void> refresh(String reportName);

    @ApiOperation("是否展示刷新按钮")
    @GetMapping("/management/report/showRefreshBtn")
    R<Boolean> showRefreshBtn(String reportName);
}


