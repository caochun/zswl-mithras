package cn.zswltech.mithras.customer.application.lib.client.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import cn.zswltech.mithras.dto.client.commerceinfo.NewCorpCommerceInfoDetailRSP;
import cn.zswltech.mithras.customer.application.lib.client.NewCorpCommerceInfoLibService;
import cn.zswltech.mithras.service.constant.VersionTypeConstants;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.lib.client.NewCorpCommerceInfoLibMapper;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.client.ClientMapper;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.Client;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.ClientBaseModel;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.NewCorpCommerceInfoLib;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.customer.application.lib.client.handler.impl.NewCorpCommerceInfoLibHandlerImpl;
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
public class NewCorpCommerceInfoLibServiceImpl extends ServiceImpl<NewCorpCommerceInfoLibMapper, NewCorpCommerceInfoLib> implements NewCorpCommerceInfoLibService {

    @Resource
    private ClientMapper clientMapper;
    @Resource
    private NewCorpCommerceInfoLibHandlerImpl newCorpCommerceInfoLibHandler;

    @Override
    public NewCorpCommerceInfoDetailRSP detail(Long clientId, String version) {
        NewCorpCommerceInfoLib versionLib = baseMapper.selectOne(Wrappers.<NewCorpCommerceInfoLib>lambdaQuery().eq(NewCorpCommerceInfoLib::getClientId, clientId).eq(NewCorpCommerceInfoLib::getVersion, version).last("LIMIT 1"));
        return Optional.ofNullable(versionLib).map(newCorpCommerceInfoLibHandler::actualLib2Rsp).orElse(new NewCorpCommerceInfoDetailRSP());
    }

    @Override
    public List<NewCorpCommerceInfoLib> listNewestCommerceInfo(CorpCommerceInfoLibDto dto) {
        return baseMapper.listNewestCommerceInfo(dto);
    }

    @Override
    public NewCorpCommerceInfoLib getNewestOne(Long clientId) {
        Client client = clientMapper.selectById(clientId);
        Assert.notNull(client, () -> MithrasException.newException("客户信息不存在: " + clientId));
        return this.getNewestOne(client);
    }

    @Override
    public NewCorpCommerceInfoLib getNewestOne(Client client) {
        Assert.notBlank(client.getNewestVersion(), () -> MithrasException.newException("客户没有最新生效的数据"));
        LambdaQueryWrapper<NewCorpCommerceInfoLib> query = Wrappers.lambdaQuery();
        query.eq(ClientBaseModel::getClientId, client.getId());
        query.eq(NewCorpCommerceInfoLib::getVersion, client.getNewestVersion());
        query.eq(NewCorpCommerceInfoLib::getVersionType, VersionTypeConstants.NORMAL);
        query.orderByDesc(NewCorpCommerceInfoLib::getVersion);
        query.last(StringUtil.mysqlLimitOne());
        return this.getOne(query);
    }

    @Override
    public Map<Long, NewCorpCommerceInfoLib> getSpecificVersionMap(Map<Long, String> clientVersionMap) {
        LambdaQueryWrapper<NewCorpCommerceInfoLib> query = Wrappers.lambdaQuery();
        query.in(ClientBaseModel::getClientId, clientVersionMap.keySet());
        List<NewCorpCommerceInfoLib> list = this.list(query);
        if (CollectionUtil.isEmpty(list)) {
            return Collections.emptyMap();
        }
        Map<Long, List<NewCorpCommerceInfoLib>> libMap = list.stream().collect(Collectors.groupingBy(ClientBaseModel::getClientId));
        Map<Long, NewCorpCommerceInfoLib> resultMap = new HashMap<>();
        for (Map.Entry<Long, String> entry : clientVersionMap.entrySet()) {
            List<NewCorpCommerceInfoLib> libs = libMap.get(entry.getKey());
            if (CollectionUtil.isEmpty(libs)) {
                continue;
            }
            for (NewCorpCommerceInfoLib corpCommerceInfoLib : libs) {
                if (Objects.equals(corpCommerceInfoLib.getVersion(), entry.getValue())) {
                    resultMap.put(entry.getKey(), corpCommerceInfoLib);
                    break;
                }
            }
        }
        return resultMap;
    }
}
