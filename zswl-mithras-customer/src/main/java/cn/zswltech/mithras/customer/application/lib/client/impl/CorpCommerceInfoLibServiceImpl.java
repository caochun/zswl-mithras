package cn.zswltech.mithras.customer.application.lib.client.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import cn.zswltech.mithras.dto.client.commerceinfo.CorpCommerceInfoDetailRSP;
import cn.zswltech.mithras.customer.application.lib.client.CorpCommerceInfoLibService;
import cn.zswltech.mithras.service.constant.VersionTypeConstants;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.lib.client.CorpCommerceInfoLibMapper;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.client.ClientMapper;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.Client;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.ClientBaseModel;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.CorpCommerceInfoLib;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.customer.application.lib.client.handler.impl.CorpCommerceInfoLibHandlerImpl;
import cn.zswltech.mithras.customer.application.riskcontrol.dto.CorpCommerceInfoLibDto;
import cn.zswltech.mithras.service.util.StringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 *
 * @author wangchuanhao
 * @date 2022/6/22 4:11 PM
 */
@Service
public class CorpCommerceInfoLibServiceImpl extends ServiceImpl<CorpCommerceInfoLibMapper, CorpCommerceInfoLib> implements CorpCommerceInfoLibService {

    @Resource
    private ClientMapper clientMapper;
    @Resource
    private CorpCommerceInfoLibHandlerImpl corpCommerceInfoLibHandler;

    @Override
    public CorpCommerceInfoDetailRSP detail(Long clientId, String version) {
        CorpCommerceInfoLib versionLib = baseMapper.selectOne(Wrappers.<CorpCommerceInfoLib>lambdaQuery().eq(CorpCommerceInfoLib::getClientId, clientId).eq(CorpCommerceInfoLib::getVersion, version).last("LIMIT 1"));
        return Optional.ofNullable(versionLib).map(corpCommerceInfoLibHandler::actualLib2Rsp).orElse(new CorpCommerceInfoDetailRSP());
    }

    @Override
    public List<CorpCommerceInfoLib> listNewestCommerceInfo(CorpCommerceInfoLibDto dto) {
        return baseMapper.listNewestCommerceInfo(dto);
    }

    @Override
    public CorpCommerceInfoLib getNewestOne(Long clientId) {
        Client client = clientMapper.selectById(clientId);
        Assert.notNull(client, () -> MithrasException.newException("客户信息不存在: " + clientId));
        return this.getNewestOne(client);
    }

    @Override
    public CorpCommerceInfoLib getNewestOne(Client client) {
        Assert.notBlank(client.getNewestVersion(), () -> MithrasException.newException("客户没有最新生效的数据"));
        LambdaQueryWrapper<CorpCommerceInfoLib> query = Wrappers.lambdaQuery();
        query.eq(ClientBaseModel::getClientId, client.getId());
        query.eq(CorpCommerceInfoLib::getVersion, client.getNewestVersion());
        query.eq(CorpCommerceInfoLib::getVersionType, VersionTypeConstants.NORMAL);
        query.orderByDesc(CorpCommerceInfoLib::getVersion);
        query.last(StringUtil.mysqlLimitOne());
        return this.getOne(query);
    }

    @Override
    public Map<Long, CorpCommerceInfoLib> getSpecificVersionMap(Map<Long, String> clientVersionMap) {
        LambdaQueryWrapper<CorpCommerceInfoLib> query = Wrappers.lambdaQuery();
        query.in(ClientBaseModel::getClientId, clientVersionMap.keySet());
        List<CorpCommerceInfoLib> list = this.list(query);
        if (CollectionUtil.isEmpty(list)) {
            return Collections.emptyMap();
        }
        Map<Long, List<CorpCommerceInfoLib>> libMap = list.stream().collect(Collectors.groupingBy(ClientBaseModel::getClientId));
        Map<Long, CorpCommerceInfoLib> resultMap = new HashMap<>();
        for (Map.Entry<Long, String> entry : clientVersionMap.entrySet()) {
            List<CorpCommerceInfoLib> libs = libMap.get(entry.getKey());
            if (CollectionUtil.isEmpty(libs)) {
                continue;
            }
            for (CorpCommerceInfoLib corpCommerceInfoLib : libs) {
                if (Objects.equals(corpCommerceInfoLib.getVersion(), entry.getValue())) {
                    resultMap.put(entry.getKey(), corpCommerceInfoLib);
                    break;
                }
            }
        }
        return resultMap;
    }

}
