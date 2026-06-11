package cn.zswltech.mithras.third.financialshare.mapper;

import cn.zswltech.mithras.third.financialshare.model.FinanceFlowTabMainInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author bigbear
* @description 针对表【finance_flow_tab_main_info(流水核销tab信息主表)】的数据库操作Mapper
* @createDate 2024-09-24 16:40:33
* @Entity cn.zswltech.mithras.third.financialshare.model.FinanceFlowTabMainInfo
*/
public interface FinanceFlowTabMainInfoMapper extends BaseMapper<FinanceFlowTabMainInfo> {

    List<FinanceFlowTabMainInfo> findWithLogicDelete(@Param("batchNumber") String batchNumber);
}




