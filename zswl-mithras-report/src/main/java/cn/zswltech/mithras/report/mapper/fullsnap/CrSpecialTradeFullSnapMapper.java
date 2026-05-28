package cn.zswltech.mithras.report.mapper.fullsnap;
import cn.zswltech.mithras.report.mapper.fullsnap.model.CrSpecialTradeFullSnap;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
* @description 征信报送-特定交易表
* @author wang
* @date 2022-10-08
*/
public interface CrSpecialTradeFullSnapMapper extends BaseMapper<CrSpecialTradeFullSnap> {

    void copyFromEffect(@Param("batchId") Long batchId, @Param("batchNo") String batchNo);

}