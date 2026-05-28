package cn.zswltech.mithras.service.mapper.riskcontrol;

import cn.zswltech.mithras.service.mapper.model.riskcontrol.RiskControlOpinionMonitor;
import cn.zswltech.mithras.service.providence.dto.ClientMonitorOpinionDetailRsp;
import cn.zswltech.mithras.service.providence.dto.DeptPieDataDto;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author vico
 * @description risk_control_opinion_monitor
 * @date 2023-03-09
 */
public interface RiskControlOpinionMonitorMapper extends BaseMapper<RiskControlOpinionMonitor> {

    @Deprecated
    Page<RiskControlOpinionMonitor> listBySponsor(Page<RiskControlOpinionMonitor> page, @Param("sponsorId") Long currentUserId);

    Integer countDealingClient();

    Integer countNewOpClient();

    Integer countCloseOpClient();

    List<RiskControlOpinionMonitor> selectLatest5Day();

    List<DeptPieDataDto> selectHandlingOpinion();

    List<ClientMonitorOpinionDetailRsp> selectMonitorOpDetail(@Param("clientId") Long clientId);

    Integer countDealing();

    Integer countNewOp();

    Integer countCloseOp();

    /**
     * 查询已存在的记录
     * 根据title, credit_code, info_publ_date三个字段
     */
    List<RiskControlOpinionMonitor> selectExistingRecords(@Param("params") List<Map<String, Object>> params);

}