package cn.zswltech.mithras.blackgray.persistence.mapper;

import cn.zswltech.mithras.blackgray.dto.req.BlackGrayApprovalTaskREQ;
import cn.zswltech.mithras.blackgray.dto.rsp.BlackGrayManualOutboundApprovalTaskRSP;
import cn.zswltech.mithras.blackgray.persistence.model.BlackGrayManualOutbound;
import cn.zswltech.gruul.dao.dal.tkmybatis.IMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface BlackGrayManualOutboundMapper extends IMapper<BlackGrayManualOutbound> {

    List<BlackGrayManualOutboundApprovalTaskRSP> queryForAudit(@Param("param") BlackGrayApprovalTaskREQ param);

}