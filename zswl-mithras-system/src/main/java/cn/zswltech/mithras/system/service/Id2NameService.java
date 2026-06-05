package cn.zswltech.mithras.system.service;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.gruul.dao.dal.dao.OrgDOMapper;
import cn.zswltech.gruul.dao.dal.dao.UserDOMapper;
import cn.zswltech.gruul.dao.dal.entity.OrgDO;
import cn.zswltech.gruul.dao.dal.entity.UserDO;
import cn.zswltech.gruul.dao.dal.query.UserQuery;
import cn.zswltech.mithras.dto.client.client.ClientInfo;
import cn.zswltech.mithras.dto.contract.ContractInfo;
import cn.zswltech.mithras.customer.hymx.infrastructure.mapper.ClientHymxMapper;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.client.ClientMapper;
import cn.zswltech.mithras.contract.mapper.contract.ContractBaseInfoMapper;
import cn.zswltech.mithras.contract.mapper.contract.ContractReceiptMapper;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.Client;
import cn.zswltech.mithras.customer.hymx.infrastructure.model.ClientHymx;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractReceipt;
import cn.zswltech.mithras.projectprocess.service.ProjectProcessNameResolver;
import cn.zswltech.mithras.service.service.UserNameResolver;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static cn.hutool.core.collection.CollUtil.isNotEmpty;

/**
 * @author junke
 */
@Service
public class Id2NameService implements UserNameResolver, ProjectProcessNameResolver {
    @Resource
    private ClientMapper clientMapper;
    @Resource
    private ClientHymxMapper clientHymxMapper;
    @Resource
    private OrgDOMapper orgDOMapper;
    @Resource
    private UserDOMapper userDOMapper;
    @Resource
    private ContractBaseInfoMapper contractBaseInfoMapper;
    @Resource
    private ContractReceiptMapper contractReceiptMapper;

    public String clientId2NameSingle(Long clientId) {
        return clientId2Name(Collections.singletonList(clientId)).get(clientId);
    }

    public Map<Long, String> clientId2Name(Collection<Long> clientIds) {
        Map<Long, String> result = new HashMap<>();
        if (isNotEmpty(clientIds)) {
            Set<Long> ids = new HashSet<>(clientIds); /*这边去重*/
            result = clientMapper.selectList(Wrappers.<Client>lambdaQuery()
                    .in(Client::getId, ids)
            ).stream().collect(Collectors.toMap(Client::getId, Client::getClientName));

            if(ids.size() > result.size()){  /*部分客户来源于航运客户*/
                Map<Long, String> hymxReslut = new HashMap<>();
                hymxReslut = clientHymxMapper.selectList(Wrappers.<ClientHymx>lambdaQuery()
                        .in(ClientHymx::getId, ids)
                ).stream().collect(Collectors.toMap(ClientHymx::getId, ClientHymx::getClientName));
                result.putAll(hymxReslut);
            }
        }
        return result;
    }

    public Map<Long, ClientInfo> clientId2CLient(Collection<Long> clientIds) {
        Map<Long, ClientInfo> result = new HashMap<>();
        if (isNotEmpty(clientIds)) {
            clientMapper.selectList(Wrappers.<Client>lambdaQuery()
                    .in(Client::getId, clientIds)
            ).stream().forEach(client -> {
                ClientInfo clientInfo = new ClientInfo();
                BeanUtil.copyProperties(client, clientInfo);
                clientInfo.setClientId(client.getId());
                result.put(client.getId(), clientInfo);
            });
        }
        return result;
    }

    public Map<Long, String> sysUserId2Name(Collection<Long> userIds) {
        Map<Long, String> result = new HashMap<>();
        if (isNotEmpty(userIds)) {
            UserQuery userQuery = new UserQuery();
            userQuery.setIdList(new ArrayList<>(userIds));
            userQuery.setPageSize(Integer.MAX_VALUE);
            result = userDOMapper.queryPage(userQuery).stream().collect(Collectors.toMap(UserDO::getId, UserDO::getUserName));
        }
        return result;
    }

    public Map<Long, String> sysUserId2NameNotLimitSize(Collection<Long> userIds) {
        Map<Long, String> result = new HashMap<>();
        if (isNotEmpty(userIds)) {
            result = userDOMapper.selectByIds(new ArrayList<>(userIds)).stream().collect(Collectors.toMap(UserDO::getId, UserDO::getUserName));
        }
        return result;
    }



    public Map<Long, String> sysUserId2Tel(Collection<Long> userIds) {
        Map<Long, String> result = new HashMap<>();
        if (isNotEmpty(userIds)) {
            UserQuery userQuery = new UserQuery();
            userQuery.setIdList(new ArrayList<>(userIds));
            userQuery.setPageSize(Integer.MAX_VALUE);
            result = userDOMapper.queryPage(userQuery).stream().collect(Collectors.toMap(UserDO::getId, UserDO::getPhone));
        }
        return result;
    }

    public String sysUserId2NameSingle(Long userId) {
        return sysUserId2Name(Arrays.asList(userId)).get(userId);
    }

    public Map<Long, String> deptId2Name(Collection<Long> deptIds) {
        if (isNotEmpty(deptIds)) {
            return orgDOMapper.selectByIds(new ArrayList<>(deptIds), null).stream().collect(Collectors.toMap(OrgDO::getId, OrgDO::getName));
        }
        return new HashMap<>();
    }

    public String deptId2NameSingle(Long deptId) {
        return deptId2Name(Collections.singletonList(deptId)).get(deptId);
    }

    public Map<Long, String> contractId2Name(Collection<Long> ids) {
        if (isNotEmpty(ids)) {
            return contractBaseInfoMapper.selectIds(new ArrayList<>(ids)).stream().collect(Collectors.toMap(ContractInfo::getId, ContractInfo::getContractCode));
        }
        return new HashMap<>();
    }

    public Map<Long, String> receiptId2Name(Collection<Long> ids) {
        if (isNotEmpty(ids)) {
            return contractReceiptMapper.selectBatchIds(new ArrayList<>(ids)).stream().collect(Collectors.toMap(ContractReceipt::getId, ContractReceipt::getReceiptCode));
        }
        return new HashMap<>();
    }

    public Map<Long, ContractInfo> contractId2Info(Collection<Long> ids) {
        if (isNotEmpty(ids)) {
            return contractBaseInfoMapper.selectIds(new ArrayList<>(ids)).stream().collect(Collectors.toMap(ContractInfo::getId, contractInfo -> contractInfo));
        }
        return new HashMap<>();
    }

    public Map<Long, Long> clientId2DeptId(Collection<Long> clientIds) {
        Map<Long, Long> result = new HashMap<>();
        if (isNotEmpty(clientIds)) {
            clientMapper.selectList(Wrappers.<Client>lambdaQuery()
                    .in(Client::getId, clientIds)
            ).forEach(client -> {
                result.put(client.getId(), client.getBelongDeptId());
            });
        }
        return result;
    }
}
