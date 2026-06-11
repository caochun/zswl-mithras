package cn.zswltech.mithras.customer.application.client;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.zswltech.gruul.common.constant.OrgConstants;
import cn.zswltech.gruul.dao.dal.entity.OrgDO;
import cn.zswltech.mithras.customer.mapper.client.ClientUserRefMapper;
import cn.zswltech.mithras.customer.model.client.ClientUserRef;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @author dingqi
 * @date 2024/9/11
 * @description
 */
@Slf4j
@Service
public class ClientUserRefService extends ServiceImpl<ClientUserRefMapper, ClientUserRef> {
    @Resource
    private ClientUserDeptPort clientUserDeptPort;

    public void create(Long clientId, Long userId) {
        ClientUserRef clientUserRef = new ClientUserRef();
        clientUserRef.setClientId(clientId);
        clientUserRef.setUserId(userId);
        clientUserRef.setLastOperateTime(LocalDateTime.now());
        clientUserRef.setLastOperateType(ClientUserRef.OperateTypeEnum.INIT.name());
        List<OrgDO> orgList = clientUserDeptPort.getSpecificUserDeptList(userId);
        if (CollectionUtil.isNotEmpty(orgList)) {
            Optional<OrgDO> optional = orgList.stream().filter(e -> Objects.equals(e.getType(), OrgConstants.BUSINESS_DEPT)).findFirst();
            optional.ifPresent(orgDO -> clientUserRef.setDeptId(orgDO.getId()));
        }
        try {
            this.save(clientUserRef);
        } catch (Exception e) {
            log.error("客户数据副本新增关联记录发生异常[clientId:{}, userId:{}]", clientId, userId, e);
        }
    }

    public int countByClientUser(Long clientId, Long userId) {
        LambdaQueryWrapper<ClientUserRef> query = Wrappers.lambdaQuery();
        query.eq(ClientUserRef::getClientId, clientId);
        query.eq(ClientUserRef::getUserId, userId);
        return this.count(query);
    }

    public void updateLastOperateTime(Long clientId, Long userId, ClientUserRef.OperateTypeEnum operateType) {
        LambdaUpdateWrapper<ClientUserRef> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(ClientUserRef::getClientId, clientId);
        updateWrapper.eq(ClientUserRef::getUserId, userId);
        ClientUserRef clientUserRef = new ClientUserRef();
        clientUserRef.setLastOperateType(operateType.name());
        clientUserRef.setLastOperateTime(LocalDateTime.now());
        this.update(clientUserRef, updateWrapper);
    }

    public List<ClientUserRef> listByDeptIds(Long clientId, Collection<Long> deptIds) {
        if (CollectionUtil.isEmpty(deptIds)) {
            return Collections.emptyList();
        }
        LambdaQueryWrapper<ClientUserRef> query = Wrappers.lambdaQuery();
        query.eq(ClientUserRef::getClientId, clientId);
        query.in(ClientUserRef::getDeptId, deptIds);
        query.in(ClientUserRef::getLastOperateType, ListUtil.of(ClientUserRef.OperateTypeEnum.WRITE.name(), ClientUserRef.OperateTypeEnum.INIT.name()));
        query.orderByDesc(ClientUserRef::getLastOperateTime);
        return this.list(query);
    }

    public ClientUserRef findLastOperateByDeptIds(Long clientId, Collection<Long> deptIds) {
        List<ClientUserRef> list = this.listByDeptIds(clientId, deptIds);
        if (CollectionUtil.isEmpty(list)) {
            return null;
        }
        return list.get(0);
    }

    public void removeByClientUser(Long clientId, Long userId) {
        LambdaQueryWrapper<ClientUserRef> query = Wrappers.lambdaQuery();
        query.eq(ClientUserRef::getClientId, clientId);
        query.in(ClientUserRef::getUserId, userId);
        this.remove(query);
    }
}
