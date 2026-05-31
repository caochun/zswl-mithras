package cn.zswltech.mithras.blackgray.mapper;

import cn.zswltech.mithras.blackgray.dto.req.BlackGrayWarehouseApprovalTaskREQ;
import cn.zswltech.mithras.blackgray.dto.rsp.BlackGrayWarehouseTaskApprovalTaskRSP;
import cn.zswltech.mithras.blackgray.model.BlackGrayWarehouseTask;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
* @description 黑灰名单任务表
* @author 
* @date 2024-01-16
*/
@Repository
public interface BlackGrayWarehouseTaskMapper extends BaseMapper<BlackGrayWarehouseTask> {

    List<BlackGrayWarehouseTaskApprovalTaskRSP> queryForAudit(@Param("param") BlackGrayWarehouseApprovalTaskREQ param);

    int blackGrayCheckTaskTimeoutJob();

    BlackGrayWarehouseTask queryByTaskNum(@Param("taskNum") String taskNum);

    int updateIsSupplyGroupInfoInt(@Param("id") Long id, @Param("isSupplyGroupInfo") Boolean isSupplyGroupInfo);
}