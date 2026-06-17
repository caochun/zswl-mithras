package cn.zswltech.mithras.customer.mapper.client;

import cn.zswltech.mithras.customer.dto.ClientListParam;
import cn.zswltech.mithras.customer.dto.client.ClientBasicInfoDTO;
import cn.zswltech.mithras.customer.model.client.Client;
import cn.zswltech.mithras.customer.dto.client.ClientBasicPageQuery;
import cn.zswltech.mithras.foundation.persistence.plugin.CustomBaseMapper;
import cn.zswltech.mithras.customer.application.monitor.dto.ClientMonitorListRsp;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;

/**
 * @author junke
 */
public interface ClientMapper extends CustomBaseMapper<Client> {
    Page<Client> myList(Page page, @Param("p") ClientListParam param);

    Page<ClientBasicInfoDTO> pageClientBasicInfo(Page page, @Param("p") ClientBasicPageQuery req);

    Page<Client> listNoAuthority(Page<Client> page, @Param("clientName") String clientName, @Param("userId") Long userId);

    Page<ClientMonitorListRsp> listMonitorClient(Page<ClientMonitorListRsp> page,
                                                 @Param("clientName") String clientName,
                                                 @Param("clientIds") Collection<Long> clientIds);

    Integer countMonitorClient();

    List<Client> listRelatedClient();
}
