package cn.zswltech.mithras.customer.application.lib.client;

import cn.zswltech.mithras.dto.client.commerceinfo.NewCorpCommerceInfoDetailRSP;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.Client;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.NewCorpCommerceInfoLib;
import cn.zswltech.mithras.customer.application.riskcontrol.dto.CorpCommerceInfoLibDto;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * @author wangchuanhao
 * @date 2022/6/22 4:11 PM
 */
public interface NewCorpCommerceInfoLibService extends IService<NewCorpCommerceInfoLib> {
    NewCorpCommerceInfoDetailRSP detail(Long clientId, String version);

    List<NewCorpCommerceInfoLib> listNewestCommerceInfo(CorpCommerceInfoLibDto dto);

    NewCorpCommerceInfoLib getNewestOne(Long clientId);

    NewCorpCommerceInfoLib getNewestOne(Client client);

    Map<Long, NewCorpCommerceInfoLib> getSpecificVersionMap(Map<Long, String> clientVersionMap);
}
