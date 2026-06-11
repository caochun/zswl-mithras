package cn.zswltech.mithras.finance.mapper.finance;

import cn.zswltech.mithras.finance.mapper.finance.query.FinanceProjectProfitDetailQuery;
import cn.zswltech.mithras.finance.mapper.model.finance.FinanceProjectProfitDetail;
import cn.zswltech.mithras.foundation.persistence.plugin.CustomBaseMapper;
import cn.zswltech.mithras.finance.bo.FinanceProjectProfitDetailBO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author dingqi
 * @date 2023/6/16
 * @description
 */
public interface FinanceProjectProfitDetailMapper extends CustomBaseMapper<FinanceProjectProfitDetail> {
    List<FinanceProjectProfitDetailBO> myPageList(@Param("query") FinanceProjectProfitDetailQuery query);

    int myPageListCount(@Param("query") FinanceProjectProfitDetailQuery query);


}
