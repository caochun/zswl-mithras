package cn.zswltech.mithras.service.mapper.riskcontrol;

import cn.zswltech.mithras.service.mapper.dto.RiskControlWarnMonitorPageWarnDTO;
import cn.zswltech.mithras.service.mapper.dto.RiskWarnCardDTO;
import cn.zswltech.mithras.service.mapper.model.riskcontrol.RiskControlWarnMonitor;
import cn.zswltech.mithras.service.providence.dto.ClientMonitorWarnDetailRsp;
import cn.zswltech.mithras.service.providence.dto.DeptPieDataDto;
import cn.zswltech.mithras.service.providence.dto.WarnCountDto;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
* @description 风控预警监测
* @author vico
* @date 2024-12-23
*/
public interface RiskControlWarnMonitorMapper extends BaseMapper<RiskControlWarnMonitor> {

    List<WarnCountDto> selectCountByClient(@Param("usccs")Set<String> usccs,
                                           @Param("warnLevel") String warnLevel);

    Integer countDealingClient();

    Integer countNewWarnClient();

    Integer countCloseWarnClient();

    List<RiskControlWarnMonitor> selectLatest5Day();

    List<DeptPieDataDto> selectHandlingWarn();

    List<ClientMonitorWarnDetailRsp> selectMonitorWarnDetail(@Param("clientId") Long clientId);

    Page<RiskControlWarnMonitor> pageWarn(Page<RiskControlWarnMonitor> page,  @Param("param")RiskControlWarnMonitorPageWarnDTO param);

    Integer countDealing(@Param("warnLevel") Integer warnLevel);

    Integer countNewWarn(@Param("warnLevel") Integer warnLevel);

    Integer countCloseWarn(@Param("warnLevel") Integer warnLevel);

    List<RiskWarnCardDTO> totalRiskType();

    String selectMaxWarnCodeByDate(@Param("dateStr") String dateStr);

    /**
     * 查询已存在的记录
     * 根据title, credit_code, data_time三个字段
     */
    List<RiskControlWarnMonitor> selectExistingRecords(@Param("params") List<Map<String, Object>> params);
}