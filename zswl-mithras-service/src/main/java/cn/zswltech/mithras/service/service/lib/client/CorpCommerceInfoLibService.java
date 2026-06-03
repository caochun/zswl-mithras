package cn.zswltech.mithras.service.service.lib.client;

import cn.zswltech.mithras.dto.client.commerceinfo.CorpCommerceInfoDetailRSP;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.Client;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.CorpCommerceInfoLib;
import cn.zswltech.mithras.customer.application.riskcontrol.dto.CorpCommerceInfoLibDto;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * @author wangchuanhao
 * @date 2022/6/22 4:11 PM
 */
public interface CorpCommerceInfoLibService extends IService<CorpCommerceInfoLib> {

    CorpCommerceInfoDetailRSP detail(Long clientId, String version);

    List<CorpCommerceInfoLib> listNewestCommerceInfo(CorpCommerceInfoLibDto dto);

    CorpCommerceInfoLib getNewestOne(Long clientId);

    CorpCommerceInfoLib getNewestOne(Client client);

    Map<Long, CorpCommerceInfoLib> getSpecificVersionMap(Map<Long, String> clientVersionMap);
}
