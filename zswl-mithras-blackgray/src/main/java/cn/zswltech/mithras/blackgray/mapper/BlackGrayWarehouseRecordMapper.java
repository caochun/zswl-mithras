package cn.zswltech.mithras.blackgray.mapper;

import cn.zswltech.gruul.dao.dal.tkmybatis.IMapper;
import cn.zswltech.mithras.blackgray.dto.req.BlackGrayApprovalTaskREQ;
import cn.zswltech.mithras.blackgray.dto.rsp.BlackGrayWarehouseApprovalTaskRSP;
import cn.zswltech.mithras.blackgray.model.BlackGrayWarehouseRecord;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface BlackGrayWarehouseRecordMapper extends IMapper<BlackGrayWarehouseRecord> {

    List<BlackGrayWarehouseApprovalTaskRSP> queryForAudit(@Param("param") BlackGrayApprovalTaskREQ param);

    void updateAuditStatus(@Param("taskNum") String taskNum, @Param("auditStatus") int auditStatus);

    int countByApplyReasonType(@Param("applyReasonType") String applyReasonType);

}