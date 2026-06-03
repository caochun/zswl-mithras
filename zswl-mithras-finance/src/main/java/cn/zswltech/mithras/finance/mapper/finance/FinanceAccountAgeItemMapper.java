package cn.zswltech.mithras.finance.mapper.finance;

import cn.zswltech.mithras.finance.mapper.finance.query.FinanceAccountAgeitemCountQuery;
import cn.zswltech.mithras.finance.mapper.model.finance.FinanceAccountAgeItem;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @description 帐龄-详情表
* @author vico
* @date 2024-09-10
*/
public interface FinanceAccountAgeItemMapper extends BaseMapper<FinanceAccountAgeItem> {

    List<FinanceAccountAgeItem> getLastAccountAgeItemByCollectionIds(@Param("collectionIds") List<Long> collectionIds);

    FinanceAccountAgeItem countAmount(@Param("req") FinanceAccountAgeitemCountQuery req);

}