package cn.zswltech.mithras.service.overdue.infrastructure.dao.mapper;

import cn.zswltech.mithras.service.mapper.model.fund.receiptrepay.FundReceiptRepayBaseInfo;
import cn.zswltech.mithras.service.overdue.application.dto.CollectionListDto;
import cn.zswltech.mithras.service.overdue.application.query.CollectionPageQuery;
import cn.zswltech.mithras.service.overdue.infrastructure.dao.model.OverdueCollection;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2024/10/21 16:28
 */
public interface OverdueCollectionMapper extends BaseMapper<OverdueCollection> {

    Page<CollectionListDto> advancedList(Page<CollectionListDto> page,
                                         @Param("query") CollectionPageQuery query);
}
