package cn.zswltech.mithras.service.mapper.third;

import cn.zswltech.mithras.service.mapper.model.third.FinanceFlowMatchResult;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author bigbear
* @description 针对表【finance_flow_match_result(自动核销预核销记录表)】的数据库操作Mapper
* @createDate 2024-09-24 16:40:33
* @Entity cn.zswltech.mithras.service.mapper.model.third.FinanceFlowMatchResult
*/
public interface FinanceFlowMatchResultMapper extends BaseMapper<FinanceFlowMatchResult> {

    List<FinanceFlowMatchResult> selectWithLogicDelete(@Param("mainId") Long mainId);
}




