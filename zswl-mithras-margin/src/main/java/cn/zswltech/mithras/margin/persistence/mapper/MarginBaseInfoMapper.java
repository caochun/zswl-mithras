package cn.zswltech.mithras.margin.persistence.mapper;

import cn.zswltech.mithras.dto.margin.MarginBaseInfoListREQ;
import cn.zswltech.mithras.margin.persistence.model.DepositCollectRefund;
import cn.zswltech.mithras.margin.persistence.model.MarginBaseInfo;
import cn.zswltech.mithras.foundation.persistence.plugin.CustomBaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * @create: 2022-08-17
 **/
public interface MarginBaseInfoMapper extends CustomBaseMapper<MarginBaseInfo> {

    Page<MarginBaseInfo> pageList(Page<MarginBaseInfo> page, @Param("dto") MarginBaseInfoListREQ req);

    int updateStatus(@Param("id") Long id);

    List<DepositCollectRefund> getMargins(@Param("contractIds") Set<Long> contractIds);
}
