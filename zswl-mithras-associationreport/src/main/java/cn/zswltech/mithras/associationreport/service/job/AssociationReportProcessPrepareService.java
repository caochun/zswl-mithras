package cn.zswltech.mithras.associationreport.service.job;

import cn.zswltech.mithras.workflow.persistence.model.CommonProcessPrepare;
import com.baomidou.mybatisplus.core.conditions.Wrapper;

/**
 * 金融局报送任务生成待办时依赖的流程准备记录端口。
 */
public interface AssociationReportProcessPrepareService {

    boolean save(CommonProcessPrepare commonProcessPrepare);

    int count(Wrapper<CommonProcessPrepare> queryWrapper);
}
