package cn.zswltech.mithras.afterlease.mapper.lib;

import cn.zswltech.mithras.afterlease.model.NewAfterLeaseCheckReportMetaLib;
import cn.zswltech.mithras.foundation.persistence.plugin.CustomBaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import javax.validation.constraints.NotEmpty;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

/**
 * @author dingqi
 * @date 2022/12/16
 * @description
 */
public interface NewAfterLeaseCheckReportMetaLibMapper extends CustomBaseMapper<NewAfterLeaseCheckReportMetaLib> {

    /**
     * 根据计划客户ids查询最新版本数据
     * @param planClientIdList
     * @return
     */
    List<NewAfterLeaseCheckReportMetaLib> selectLatestListByPlanClientId(@NotEmpty @Param("planClientIdList") Collection<Long> planClientIdList);
}
