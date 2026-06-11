package cn.zswltech.mithras.creditreport.mapper;

import cn.zswltech.mithras.dto.client.client.ClientInfo;
import cn.zswltech.mithras.creditreport.mapper.model.CreditReportBaseInfo;
import cn.zswltech.mithras.foundation.persistence.plugin.CustomBaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
* @description 征信报告基本信息表
* @author vico
* @date 2025-11-24
*/
public interface CreditReportBaseInfoMapper extends CustomBaseMapper<CreditReportBaseInfo> {
    List<ClientInfo> selectClientInfo(@Param("clientName") String clientName, @Param("bizDeptIds") Set<Long> bizDeptIds, @Param("userId") Long userId);

    List<ClientInfo> selectClientInfoAll(@Param("clientName") String clientName);

}