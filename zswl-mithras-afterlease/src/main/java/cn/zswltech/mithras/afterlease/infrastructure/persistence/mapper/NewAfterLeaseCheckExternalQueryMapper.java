package cn.zswltech.mithras.afterlease.infrastructure.persistence.mapper;

import cn.zswltech.mithras.dto.afterlease.AfterLeaseCheckExternalQueryDto;
import cn.zswltech.mithras.afterlease.infrastructure.persistence.mapper.model.NewAfterLeaseCheckExternalQuery;
import cn.zswltech.mithras.service.plugin.CustomBaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

/**
 * @author zhaozhengkang
 * @description 租后检查外部查询任务
 * @date 2022-11-17
 */
public interface NewAfterLeaseCheckExternalQueryMapper extends CustomBaseMapper<NewAfterLeaseCheckExternalQuery> {

    Page<NewAfterLeaseCheckExternalQuery> myList(Page<NewAfterLeaseCheckExternalQuery> page,
                                                 @Param("dto") AfterLeaseCheckExternalQueryDto selectDTO);

    int myListStatistics(@Param("dto") AfterLeaseCheckExternalQueryDto selectDTO);
}