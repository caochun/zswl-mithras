package cn.zswltech.mithras.dashboard.guanbao.controller;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.gruul.biz.service.SystemConfigService;
import cn.zswltech.gruul.common.result.Response;
import cn.zswltech.gruul.dao.dal.entity.SystemConfigDO;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.guanbao.ManagementReportApi;
import cn.zswltech.mithras.dto.ReportGroupListRSP;
import cn.zswltech.mithras.dto.ReportSelectRSP;
import cn.zswltech.mithras.dashboard.guanbao.service.ManagementReportService;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

import static cn.hutool.core.thread.ThreadUtil.sleep;
import static cn.hutool.extra.spring.SpringUtil.getBean;
import static cn.hutool.http.HttpUtil.get;

/**
 * @create: 2023-01-04
 **/
@RestController
public class ManagementReportController implements ManagementReportApi {

    @Resource
    private ManagementReportService managementReportService;

    @Override
    public R<List<ReportSelectRSP>> reportList() {
        // 过滤掉没有value的数据（老的管报前端没有支持自研页面，只支持观远嵌入）
        List<ReportSelectRSP> list = managementReportService.reportList();
        list.removeIf(e -> StrUtil.isBlank(e.getValue()));
        return R.ok(list);
    }

    @Override
    public R<List<ReportGroupListRSP>> reportGroupList() {
        return R.ok(managementReportService.reportGroupList());
    }

    @Override
    public R<Void> refresh(String reportName) {
        Response<SystemConfigDO> rsp = getBean(SystemConfigService.class).getConfig("guanyuan." + reportName);
        SystemConfigDO data = rsp.getData();
        if (data == null) {
            return R.fail("此报表不支持手动更新");
        }
        String configValue = data.getConfigValue();
        List<String> urlList = JSONUtil.toList(configValue, String.class);
        for (String url : urlList) {
            get(url);
            sleep(500);
        }
        return R.ok();
    }

    @Override
    public R<Boolean> showRefreshBtn(String reportName) {
        Response<SystemConfigDO> rsp = getBean(SystemConfigService.class).getConfig("guanyuanReportRefreshBtn");
        SystemConfigDO data = rsp.getData();
        if (Objects.isNull(data)) {
            return R.ok(Boolean.FALSE);
        }
        if (StrUtil.isBlank(data.getConfigValue())) {
            return R.ok(Boolean.FALSE);
        }
        List<String> nameList = JSONUtil.toList(data.getConfigValue(), String.class);
        return R.ok(nameList.contains(reportName));
    }
}
