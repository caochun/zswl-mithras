package cn.zswltech.mithras.blackgray.mapper;

import cn.zswltech.gruul.dao.dal.tkmybatis.IMapper;
import cn.zswltech.mithras.blackgray.dto.req.BlackGrayApprovalTaskREQ;
import cn.zswltech.mithras.blackgray.dto.rsp.BlackGrayBreakBusinessApprovalTaskRSP;
import cn.zswltech.mithras.blackgray.model.BlackGrayBreakBusiness;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlackGrayBreakBusinessMapper extends IMapper<BlackGrayBreakBusiness> {

    List<BlackGrayBreakBusinessApprovalTaskRSP> queryForAudit(@Param("param") BlackGrayApprovalTaskREQ param);

}