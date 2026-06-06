package cn.zswltech.mithras.associationreport.service.job;

import cn.zswltech.mithras.associationreport.mapper.model.AssociationReport;
import cn.zswltech.mithras.dto.associationreport.AssociationReportCreateREQ;
import com.baomidou.mybatisplus.core.conditions.Wrapper;

import java.util.List;

/**
 * 金融局报送任务依赖的报表记录端口。
 */
public interface AssociationReportJobService {

    String create(AssociationReportCreateREQ req);

    int count(Wrapper<AssociationReport> queryWrapper);

    List<AssociationReport> list(Wrapper<AssociationReport> queryWrapper);
}
