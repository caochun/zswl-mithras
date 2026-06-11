package cn.zswltech.mithras.afterlease.application.impl;

import cn.zswltech.mithras.afterlease.mapper.NewAfterLeaseCheckReportTemplateMapper;
import cn.zswltech.mithras.afterlease.model.NewAfterLeaseCheckReportTemplate;
import cn.zswltech.mithras.afterlease.application.AfterLeaseCheckReportTemplateService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author dingqi
 * @date 2022/11/16
 * @description
 */
@Service
public class AfterLeaseCheckReportTemplateServiceImpl extends ServiceImpl<NewAfterLeaseCheckReportTemplateMapper, NewAfterLeaseCheckReportTemplate> implements AfterLeaseCheckReportTemplateService {
    @Override
    public List<NewAfterLeaseCheckReportTemplate> listBy(String reportType, String areaType) {
        LambdaQueryWrapper<NewAfterLeaseCheckReportTemplate> query = Wrappers.lambdaQuery();
        query.eq(NewAfterLeaseCheckReportTemplate::getReportType, reportType);
        query.eq(NewAfterLeaseCheckReportTemplate::getAreaType, areaType);
        query.orderByAsc(NewAfterLeaseCheckReportTemplate::getOrderNum);
        return this.list(query);
    }
}
