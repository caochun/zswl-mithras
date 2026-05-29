package cn.zswltech.mithras.service.mapper;

import cn.zswltech.mithras.service.mapper.model.ProfitCalculateResult;
import cn.zswltech.mithras.service.mapper.query.ProfitCalculateResultQuery;
import cn.zswltech.mithras.common.plugin.CustomBaseMapper;
import cn.zswltech.mithras.service.service.bo.ProfitCalculateResultBO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author dingqi
 * @date 2023/6/25
 * @description
 */
public interface ProfitCalculateResultMapper extends CustomBaseMapper<ProfitCalculateResult> {
    List<ProfitCalculateResultBO> myPageList(@Param("query") ProfitCalculateResultQuery query);

    int myPageListCount(@Param("query") ProfitCalculateResultQuery query);
}
