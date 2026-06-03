package cn.zswltech.mithras.afterlease.infrastructure.persistence.mapper;

import cn.zswltech.mithras.afterlease.infrastructure.persistence.mapper.model.NewAfterLeaseCheckReportBase;
import cn.zswltech.mithras.service.plugin.CustomBaseMapper;
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
