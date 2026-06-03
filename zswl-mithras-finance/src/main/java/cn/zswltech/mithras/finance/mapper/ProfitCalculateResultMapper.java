package cn.zswltech.mithras.finance.mapper;

import cn.zswltech.mithras.finance.mapper.model.ProfitCalculateResult;
import cn.zswltech.mithras.finance.mapper.query.ProfitCalculateResultQuery;
import cn.zswltech.mithras.service.plugin.CustomBaseMapper;
import cn.zswltech.mithras.finance.bo.ProfitCalculateResultBO;
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
