package cn.zswltech.mithras.customer.infrastructure.persistence.mapper.client;

import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.dto.ClientListParam;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.dto.client.DashboardClientBasicDTO;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.Client;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.dashboard.DashboardClientOverviewAllQuery;
import cn.zswltech.mithras.service.plugin.CustomBaseMapper;
import cn.zswltech.mithras.customer.interfaces.providence.dto.ClientMonitorListRsp;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;

/**
 * @author junke
 */
public interface ClientMapper extends CustomBaseMapper<Client> {
    Page<Client> myList(Page page, @Param("p") ClientListParam param);

    Page<DashboardClientBasicDTO> dashboardAllPageList(Page page, @Param("p") DashboardClientOverviewAllQuery req);

    Page<Client> listNoAuthority(Page<Client> page, @Param("clientName") String clientName, @Param("userId") Long userId);

    Page<ClientMonitorListRsp> listMonitorClient(Page<ClientMonitorListRsp> page,
                                                 @Param("clientName") String clientName,
                                                 @Param("onlyWarn") Integer onlyWarn,
                                                 @Param("clientIds")Collection<Long> clientIds);

    Integer countMonitorClient();

    List<Client> listRelatedClient();
}