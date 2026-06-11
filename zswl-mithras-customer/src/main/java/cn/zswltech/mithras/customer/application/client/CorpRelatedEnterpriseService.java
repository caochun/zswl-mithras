package cn.zswltech.mithras.customer.application.client;


import cn.zswltech.mithras.customer.application.client.ClientDataSaveCheckInterface;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.gruul.dao.dal.vo.AccountVO;
import cn.zswltech.mithras.dto.client.relatedenterprise.CorpRelatedEnterpriseAddREQ;
import cn.zswltech.mithras.dto.client.relatedenterprise.CorpRelatedEnterpriseListREQ;
import cn.zswltech.mithras.dto.client.relatedenterprise.CorpRelatedEnterpriseModifyREQ;
import cn.zswltech.mithras.foundation.constant.ResultMsg;
import cn.zswltech.mithras.customer.enums.InfoModule;
import cn.zswltech.mithras.customer.mapper.client.ClientMapper;
import cn.zswltech.mithras.customer.mapper.corp.CorpRelatedEnterpriseMapper;
import cn.zswltech.mithras.customer.mapper.corp.NewCorpRelatedEnterpriseMapper;
import cn.zswltech.mithras.customer.model.client.*;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.customer.application.client.bo.ClientCopyInfoBO;
import cn.zswltech.mithras.customer.application.client.copyhandler.ClientOldDataHelper;
import cn.zswltech.mithras.customer.application.client.ClientAuthorityDataPort;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static cn.hutool.core.bean.BeanUtil.copyProperties;
import static cn.hutool.core.util.ObjectUtil.isNotNull;
import static cn.hutool.core.util.ObjectUtil.isNull;
import static cn.zswltech.mithras.foundation.constant.ResultMsg.RECORD_NOT_EXIST;
import static cn.zswltech.mithras.customer.enums.OrderByType.ascend;

/**
 * @author luyi
 */
@Slf4j
@Service
public class CorpRelatedEnterpriseService extends ServiceImpl<CorpRelatedEnterpriseMapper, CorpRelatedEnterprise> implements ClientDataSaveCheckInterface<NewCorpRelatedEnterprise>, ClientOldDataHelper<CorpRelatedEnterprise> {

    @Resource
    private CorpRelatedEnterpriseMapper relatedEnterpriseMapper;

    @Resource
    private NewCorpRelatedEnterpriseMapper newRelatedEnterpriseMapper;

    @Resource
    private ClientMapper clientMapper;

    @Resource
    private ClientAuthorityDataPort authorityUtil;

    @Override
    public List<CorpRelatedEnterprise> findByClientId(Long clientId) {
        LambdaQueryWrapper<CorpRelatedEnterprise> query = Wrappers.lambdaQuery();
        query.eq(ClientBaseModel::getClientId, clientId);
        return this.list(query);
    }

    @Override
    public void removeByClientId(Long clientId) {
        LambdaQueryWrapper<CorpRelatedEnterprise> query = Wrappers.lambdaQuery();
        query.eq(ClientBaseModel::getClientId, clientId);
        this.remove(query);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void add(CorpRelatedEnterpriseAddREQ req) {
        Client existClient = clientMapper.selectById(req.getClientId());
        if (existClient == null) {
            throw new MithrasException("客户已不存在");
        }
        Long startUserId = Optional.ofNullable(AccountUtil.getLoginInfo())
                .map(AccountVO::getId)
                .orElseThrow(() -> new MithrasException(ResultMsg.USER_NOT_LOGIN));
        if (CollectionUtil.isNotEmpty(newRelatedEnterpriseMapper.selectList(Wrappers.<NewCorpRelatedEnterprise>lambdaQuery()
                .eq(NewCorpRelatedEnterprise::getClientId, req.getClientId())
                .eq(NewCorpRelatedEnterprise::getUserId, startUserId)
                .eq(NewCorpRelatedEnterprise::getEnterpriseName, req.getEnterpriseName())))) {
            throw new MithrasException("该关联企业已经存在");
        }
        NewCorpRelatedEnterprise info = copyProperties(req, NewCorpRelatedEnterprise.class);
        info.setUserId(startUserId);
        check(info);
//        setClientAuthority(info, existClient, startUserId);
        newRelatedEnterpriseMapper.insert(info);
        recordClientStatus(info);
        // 新表抄老表
        ClientCopyInfoBO clientCopyInfoBO = ClientCopyInfoBO.builder()
                .clientId(existClient.getId())
                .currentUserId(startUserId)
//                .moduleList(Collections.singletonList(InfoModule.CORP_RELATED_ENTERPRISE))
                .build();
        authorityUtil.copyFromNewToOld(clientCopyInfoBO);
//        existClient.setLatestUserId(startUserId);
//        clientMapper.updateAnnotationIncludeNullById(existClient);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void modify(CorpRelatedEnterpriseModifyREQ req) {
        NewCorpRelatedEnterprise originalInfo = newRelatedEnterpriseMapper.selectById(req.getId());
        if (isNull(originalInfo)) {
            throw new MithrasException(RECORD_NOT_EXIST);
        }
        Long startUserId = Optional.ofNullable(AccountUtil.getLoginInfo())
                .map(AccountVO::getId)
                .orElseThrow(() -> new MithrasException(ResultMsg.USER_NOT_LOGIN));
        if (!Objects.equals(originalInfo.getUserId(), startUserId)) {
            throw new MithrasException("无权修改数据");
        }
        Client existClient = clientMapper.selectById(req.getClientId());
        if (existClient == null) {
            throw new MithrasException("客户已不存在");
        }
        NewCorpRelatedEnterprise info = copyProperties(req, NewCorpRelatedEnterprise.class);
        check(info);
        if (isNotNull(req.getId())) {
            newRelatedEnterpriseMapper.updateAnnotationIncludeNullById(info);
        } else {
            newRelatedEnterpriseMapper.update(info, Wrappers.<NewCorpRelatedEnterprise>lambdaUpdate()
                    .eq(NewCorpRelatedEnterprise::getEnterpriseName, req.getEnterpriseName())
                    .eq(NewCorpRelatedEnterprise::getUserId, startUserId));
        }
        recordClientStatus(info);
        // 新表抄老表
        ClientCopyInfoBO clientCopyInfoBO = ClientCopyInfoBO.builder()
                .clientId(existClient.getId())
                .currentUserId(startUserId)
//                .moduleList(Collections.singletonList(InfoModule.CORP_RELATED_ENTERPRISE))
                .build();
        authorityUtil.copyFromNewToOld(clientCopyInfoBO);
//        existClient.setLatestUserId(startUserId);
//        clientMapper.updateAnnotationIncludeNullById(existClient);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void remove(Long id) {
        NewCorpRelatedEnterprise originalInfo = newRelatedEnterpriseMapper.selectById(id);
        if (isNull(originalInfo)) {
            throw new MithrasException(RECORD_NOT_EXIST);
        }
        Long currentUserId = AccountUtil.getLoginInfo().getId();
        if (!Objects.equals(originalInfo.getUserId(), currentUserId)) {
            throw new MithrasException("无权删除数据");
        }
        Client existClient = clientMapper.selectById(originalInfo.getClientId());
        if (existClient == null) {
            throw new MithrasException("客户已不存在");
        }
        check(originalInfo);
        newRelatedEnterpriseMapper.deleteById(id);
        recordClientStatus(originalInfo);
        ClientCopyInfoBO clientCopyInfoBO = ClientCopyInfoBO.builder()
                .clientId(existClient.getId())
                .currentUserId(currentUserId)
                .build();
        authorityUtil.copyFromNewToOld(clientCopyInfoBO);
//        existClient.setLatestUserId(currentUserId);
//        clientMapper.updateAnnotationIncludeNullById(existClient);
    }

    public Page<CorpRelatedEnterprise> list(CorpRelatedEnterpriseListREQ req, SFunction<NewCorpRelatedEnterprise, ?> orderBy, String orderType) {
        Client existClient = clientMapper.selectById(req.getClientId());
        if (existClient == null) {
            throw new MithrasException("客户已不存在");
        }
        Long startUserId = req.getStartUserId();
        LambdaQueryWrapper<NewCorpRelatedEnterprise> query = Wrappers.lambdaQuery();
        query.eq(ClientBaseModel::getClientId, req.getClientId());
        if (Objects.nonNull(startUserId)) {
            // 流程中就看流程发起人
            query.eq(NewCorpRelatedEnterprise::getUserId, startUserId);
        } else {
            Long userId = authorityUtil.ensureNoProcessViewWhichUserData(req.getClientId());
            if (Objects.isNull(userId)) {
                // 说明没有可看的数据
                return new Page<>(req.getPage(), req.getPageSize());
            } else {
                query.eq(NewCorpRelatedEnterprise::getUserId, userId);
            }
        }
        Page<NewCorpRelatedEnterprise> data = newRelatedEnterpriseMapper.selectPage(new Page<>(req.getPage(), req.getPageSize()), query);
        List<CorpRelatedEnterprise> list = BeanUtil.copyToList(data.getRecords(), CorpRelatedEnterprise.class);
        return new Page<CorpRelatedEnterprise>().setRecords(list).setCurrent(data.getCurrent()).setSize(data.getSize()).setTotal(data.getTotal());
    }

    public Page<NewCorpRelatedEnterprise> newList(CorpRelatedEnterpriseListREQ req, SFunction<NewCorpRelatedEnterprise, ?> orderBy, String orderType) {
        Long startUserId = Optional.ofNullable(AccountUtil.getLoginInfo())
                .map(AccountVO::getId)
                .orElseThrow(() -> new MithrasException(ResultMsg.USER_NOT_LOGIN));
        LambdaQueryWrapper<NewCorpRelatedEnterprise> w = Wrappers.<NewCorpRelatedEnterprise>lambdaQuery()
                .eq(NewCorpRelatedEnterprise::getClientId, req.getClientId())
                .eq(NewCorpRelatedEnterprise::getUserId, startUserId);
        if (ascend.name().equals(orderType)) {
            w.orderByAsc(orderBy);
        } else {
            w.orderByDesc(orderBy);
        }
        return newRelatedEnterpriseMapper.selectPage(new Page<>(req.getPage(), req.getPageSize()), w);
    }

    public void removeByName(Long clientId, String enterpriseName) {
        Long startUserId = Optional.ofNullable(AccountUtil.getLoginInfo())
                .map(AccountVO::getId)
                .orElseThrow(() -> new MithrasException(ResultMsg.USER_NOT_LOGIN));
        Client existClient = clientMapper.selectById(clientId);
        if (existClient == null) {
            throw new MithrasException("客户已不存在");
        }
        checkByClientId(clientId);
        newRelatedEnterpriseMapper.delete(Wrappers.<NewCorpRelatedEnterprise>lambdaQuery()
                .eq(NewCorpRelatedEnterprise::getClientId, clientId)
                .eq(NewCorpRelatedEnterprise::getUserId, startUserId)
                .eq(NewCorpRelatedEnterprise::getEnterpriseName, enterpriseName));
        recordClientStatusByClientId(clientId);
        ClientCopyInfoBO clientCopyInfoBO = ClientCopyInfoBO.builder()
                .clientId(existClient.getId())
                .currentUserId(startUserId)
                .build();
        authorityUtil.copyFromNewToOld(clientCopyInfoBO);
//        existClient.setLatestUserId(startUserId);
//        clientMapper.updateAnnotationIncludeNullById(existClient);
    }

    public void batchAdd(Long clientId, List<NewCorpRelatedEnterprise> list) {
        list.forEach(e -> {
            e.setClientId(clientId);
            e.setUserId(AccountUtil.getLoginInfo().getId());
            newRelatedEnterpriseMapper.insert(e);
        });
        // 新表抄老表
        ClientCopyInfoBO clientCopyInfoBO = ClientCopyInfoBO.builder()
                .clientId(clientId)
                .currentUserId(AccountUtil.getLoginInfo().getId())
                .moduleList(Collections.singletonList(InfoModule.CORP_RELATED_ENTERPRISE))
                .build();
        authorityUtil.copyFromNewToOld(clientCopyInfoBO);
    }

//    private void setClientAuthority(NewCorpRelatedEnterprise newCorpRelatedEnterprise, Client existClient, Long startUserId) {
//        ClientAuthority clientAuthority = clientAuthorityMapper.selectOne(Wrappers.<ClientAuthority>lambdaQuery()
//                .eq(ClientAuthority::getClientId, existClient.getId())
//                .eq(ClientAuthority::getUserId, startUserId)
//                .eq(ClientAuthority::getDeleted, 0));
//        newCorpRelatedEnterprise.setUserId(startUserId);
//        newCorpRelatedEnterprise.setClientStatus(existClient.getClientStatus());
//        newCorpRelatedEnterprise.setIsReleased(existClient.getIsReleased());
//        newCorpRelatedEnterprise.setLevel(clientAuthority != null ? clientAuthority.getLevel() : 0);
//    }
}
