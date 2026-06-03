package cn.zswltech.mithras.service.mapper.lib;

import cn.zswltech.mithras.dto.fund.directfinancing.FundFinancingPropertyRSP;
import cn.zswltech.mithras.dto.property.PutPropertyBaseInfoListREQ;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * @author ylzhang5
 */
public interface PropertyMapper extends BaseMapper<FundFinancingPropertyRSP> {

    /**
     * 根据融资编号查询投放资产
     * @param financingCode
     * @return
     */
    List<FundFinancingPropertyRSP> getPropertyListByFinancingCode(@Param("financingCode") String financingCode);

    /**
     * 查询所有投放资产
     * @return
     */
    List<FundFinancingPropertyRSP> getPropertyList(@Param("query") PutPropertyBaseInfoListREQ query);


}
