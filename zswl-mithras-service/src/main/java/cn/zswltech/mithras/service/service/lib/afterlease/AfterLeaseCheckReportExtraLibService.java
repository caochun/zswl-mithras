package cn.zswltech.mithras.service.service.lib.afterlease;

import cn.zswltech.mithras.service.mapper.lib.afterlease.NewAfterLeaseCheckReportExtraLibMapper;
import cn.zswltech.mithras.service.mapper.model.afterlease.NewAfterLeaseCheckReportExtra;
import cn.zswltech.mithras.service.mapper.model.afterlease.NewAfterLeaseCheckReportExtraLib;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author dingqi
 * @date 2022/12/15
 * @description
 */
@Service
public class AfterLeaseCheckReportExtraLibService extends ServiceImpl<NewAfterLeaseCheckReportExtraLibMapper, NewAfterLeaseCheckReportExtraLib> {
    public List<NewAfterLeaseCheckReportExtraLib> listByCheckProjectIdAndVersion(Long checkClientId, String version) {
        LambdaQueryWrapper<NewAfterLeaseCheckReportExtraLib> query = Wrappers.lambdaQuery();
        query.eq(NewAfterLeaseCheckReportExtra::getCheckPlanClientId, checkClientId);
        query.eq(NewAfterLeaseCheckReportExtraLib::getVersion, version);
        return this.list(query);
    }
}
