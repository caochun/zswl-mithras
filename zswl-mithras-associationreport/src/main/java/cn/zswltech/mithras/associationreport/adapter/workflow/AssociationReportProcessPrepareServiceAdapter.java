package cn.zswltech.mithras.associationreport.adapter.workflow;

import cn.zswltech.mithras.associationreport.service.job.AssociationReportProcessPrepareService;
import cn.zswltech.mithras.workflow.process.prepare.CommonProcessPrepareService;
import cn.zswltech.mithras.workflow.persistence.model.prepare.CommonProcessPrepare;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class AssociationReportProcessPrepareServiceAdapter implements AssociationReportProcessPrepareService {

    @Resource
    private CommonProcessPrepareService commonProcessPrepareService;

    @Override
    public boolean save(CommonProcessPrepare commonProcessPrepare) {
        return commonProcessPrepareService.save(commonProcessPrepare);
    }

    @Override
    public int count(Wrapper<CommonProcessPrepare> queryWrapper) {
        return commonProcessPrepareService.count(queryWrapper);
    }
}
