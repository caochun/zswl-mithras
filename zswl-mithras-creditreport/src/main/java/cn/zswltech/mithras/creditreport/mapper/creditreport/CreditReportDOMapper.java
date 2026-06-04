/*
package cn.zswltech.mithras.creditreport.mapper;

import cn.zswltech.mithras.dto.client.client.ClientInfo;
import cn.zswltech.mithras.dto.creditreport.CreditReportListREQ;
import cn.zswltech.mithras.creditreport.mapper.model.CreditReportDO;
import cn.zswltech.mithras.service.plugin.CustomBaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

public interface CreditReportDOMapper extends CustomBaseMapper<CreditReportDO> {


    List<ClientInfo> selectClientInfo(@Param("clientName") String clientName, @Param("bizDeptIds") Set<Long> bizDeptIds, @Param("userId") Long userId);

    List<ClientInfo> selectClientInfoAll(@Param("clientName") String clientName);

    List<CreditReportDO> list(@Param("page") Page<CreditReportDO> page, @Param("req") CreditReportListREQ req);
}
*/
