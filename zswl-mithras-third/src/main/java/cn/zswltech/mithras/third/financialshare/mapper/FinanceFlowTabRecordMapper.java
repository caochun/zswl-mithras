package cn.zswltech.mithras.third.financialshare.mapper;

import cn.zswltech.mithras.third.financialshare.mapper.model.FinanceFlowTabRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author bigbear
* @description 针对表【finance_flow_tab_record(tab中流水详情记录)】的数据库操作Mapper
* @createDate 2024-09-24 16:40:33
* @Entity cn.zswltech.mithras.third.financialshare.mapper.model.FinanceFlowTabRecord
*/
public interface FinanceFlowTabRecordMapper extends BaseMapper<FinanceFlowTabRecord> {

    List<FinanceFlowTabRecord> findWithLogicDelete(@Param("tabIdList") List<Long> tabIdList);
}




