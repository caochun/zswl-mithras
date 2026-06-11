package cn.zswltech.mithras.afterlease.mapper;

import cn.zswltech.mithras.afterlease.model.NewAfterLeaseCheckReportBase;
import cn.zswltech.mithras.foundation.persistence.plugin.CustomBaseMapper;
import cn.zswltech.mithras.afterlease.application.bo.AfterLeaseListExpandBO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author dingqi
 * @date 2022/11/16
 * @description
 */
public interface NewAfterLeaseCheckReportBaseMapper extends CustomBaseMapper<NewAfterLeaseCheckReportBase> {

    List<AfterLeaseListExpandBO> getAfterLeaseListExpandBO(@Param("planIds") List<Long> planIds);
}
