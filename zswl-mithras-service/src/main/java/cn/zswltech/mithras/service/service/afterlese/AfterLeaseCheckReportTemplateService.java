package cn.zswltech.mithras.service.service.afterlese;

import cn.zswltech.mithras.service.mapper.model.afterlease.NewAfterLeaseCheckReportTemplate;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author dingqi
 * @date 2022/11/16
 * @description
 */
public interface AfterLeaseCheckReportTemplateService extends IService<NewAfterLeaseCheckReportTemplate> {
    List<NewAfterLeaseCheckReportTemplate> listBy(String reportType, String areaType);
}
