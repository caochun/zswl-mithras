package cn.zswltech.mithras.finance.mapper.stampduty;
import cn.zswltech.mithras.finance.mapper.model.stampduty.StampDutyDetail;
import cn.zswltech.mithras.finance.bo.StampDutyBO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
* @description 印花税缴纳明细表
* @author luyujie
* @date 2026-01-23
*/
@Repository
public interface StampDutyMapper extends BaseMapper<StampDutyDetail> {

    List<StampDutyBO> queryPreApprovedProcess(@Param("preDate") String preDate, @Param("modelKeyList") List<String> modelKeyList);

}