package cn.zswltech.mithras.report.mapper.fullsnap;
import cn.zswltech.mithras.report.mapper.fullsnap.model.CrPledgeFullSnap;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
* @description 征信报送-质押表
* @author wang
* @date 2022-10-08
*/
public interface CrPledgeFullSnapMapper extends BaseMapper<CrPledgeFullSnap> {

    void copyFromEffect(@Param("batchId") Long batchId, @Param("batchNo") String batchNo);

}