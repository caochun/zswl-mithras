package cn.zswltech.mithras.report.mapper.fullsnap;
import cn.zswltech.mithras.report.mapper.fullsnap.model.CrRepayPlanFullSnap;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
* @description 征信报送-还款计划表
* @author wang
* @date 2022-10-08
*/
public interface CrRepayPlanFullSnapMapper extends BaseMapper<CrRepayPlanFullSnap> {

    void copyFromEffect(@Param("batchId") Long batchId, @Param("batchNo") String batchNo);

}