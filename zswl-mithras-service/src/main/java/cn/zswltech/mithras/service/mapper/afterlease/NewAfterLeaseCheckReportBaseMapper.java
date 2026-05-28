package cn.zswltech.mithras.service.mapper.afterlease;

import cn.zswltech.mithras.service.mapper.model.afterlease.NewAfterLeaseCheckReportBase;
import cn.zswltech.mithras.service.plugin.CustomBaseMapper;
import cn.zswltech.mithras.service.service.bo.AfterLeaseListExpandBO;
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
