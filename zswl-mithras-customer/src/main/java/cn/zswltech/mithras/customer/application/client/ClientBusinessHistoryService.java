package cn.zswltech.mithras.customer.application.client;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.mithras.customer.mapper.client.ClientBusinessHistoryMapper;
import cn.zswltech.mithras.customer.mapper.model.client.ClientBusinessHistory;
import cn.zswltech.mithras.customer.application.client.bo.ClientBusinessHistoryBO;
import cn.zswltech.mithras.customer.application.client.dto.MithrasShareholderInfo;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
* @description 客户工商信息历史表
* @author vico
* @date 2023-09-07
*/
@Service
public class ClientBusinessHistoryService extends ServiceImpl<ClientBusinessHistoryMapper, ClientBusinessHistory> {

    @Resource
    private ClientBusinessHistoryMapper clientBusinessHistoryMapper;

    public Map<Long, ClientBusinessHistoryBO> getHistoryBoByClientIds(List<Long> clientIds) {
        if (clientIds == null) {
            return MapUtil.empty();
        }
        List<ClientBusinessHistory> lastByClientIds = clientBusinessHistoryMapper.getLastByClientIds(clientIds);
        if (CollectionUtil.isEmpty(lastByClientIds)) {
            return MapUtil.empty();
        }
        Map<Long, ClientBusinessHistoryBO> map = new HashMap<>();
        lastByClientIds.forEach(base -> {
            ClientBusinessHistoryBO businessHistoryBO = new ClientBusinessHistoryBO();
            businessHistoryBO.setId(base.getId());
            businessHistoryBO.setClientId(base.getClientId());
            businessHistoryBO.setTycName(base.getTycName());
            businessHistoryBO.setTycCorpRepresent(base.getTycCorpRepresent());
            businessHistoryBO.setTycShareHolderInfo(JSONUtil.toList(base.getTycShareHolderInfo(), MithrasShareholderInfo.class));
            map.put(base.getClientId(), businessHistoryBO);
        });
        return map;
    }
}
