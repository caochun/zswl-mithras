package cn.zswltech.mithras.customer.application.client;


import cn.zswltech.mithras.customer.application.client.ClientDataSaveCheckInterface;
import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.gruul.dao.dal.vo.AccountVO;
import cn.zswltech.mithras.dto.client.shareholder.CorpShareholderInfoAddREQ;
import cn.zswltech.mithras.dto.client.shareholder.CorpShareholderInfoListREQ;
import cn.zswltech.mithras.dto.client.shareholder.CorpShareholderInfoModifyREQ;
import cn.zswltech.mithras.foundation.constant.ResultMsg;
import cn.zswltech.mithras.customer.enums.InfoModule;
import cn.zswltech.mithras.customer.mapper.client.ClientMapper;
import cn.zswltech.mithras.customer.mapper.corp.CorpShareholderInfoMapper;
import cn.zswltech.mithras.customer.mapper.corp.NewCorpShareholderInfoMapper;
import cn.zswltech.mithras.customer.model.client.Client;
import cn.zswltech.mithras.customer.model.client.ClientBaseModel;
import cn.zswltech.mithras.customer.model.client.CorpShareholderInfo;
import cn.zswltech.mithras.customer.model.client.NewCorpShareholderInfo;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.customer.application.client.model.ClientCopyInfoBO;
import cn.zswltech.mithras.customer.application.client.copyhandler.ClientOldDataHelper;
import cn.zswltech.mithras.customer.application.client.ClientAuthorityDataPort;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static cn.hutool.core.bean.BeanUtil.copyProperties;
import static cn.hutool.core.util.ObjectUtil.isNull;
import static cn.zswltech.mithras.foundation.constant.ResultMsg.RECORD_NOT_EXIST;
import static cn.zswltech.mithras.customer.enums.OrderByType.ascend;

/**
 * @author luyi
 */
@Service
public class CorpShareHolderInfoService extends ServiceImpl<CorpShareholderInfoMapper, CorpShareholderInfo> implements ClientDataSaveCheckInterface<NewCorpShareholderInfo>, ClientOldDataHelper<CorpShareholderInfo> {
    @Resource
    private CorpShareholderInfoMapper shareholderInfoMapper;

    @Resource
    private NewCorpShareholderInfoMapper newShareholderInfoMapper;

    @Resource
    private ClientMapper clientMapper;

    @Resource
    private ClientAuthorityDataPort authorityUtil;

    @Override
    public List<CorpShareholderInfo> findByClientId(Long clientId) {
        LambdaQueryWrapper<CorpShareholderInfo> query = Wrappers.lambdaQuery();
        query.eq(ClientBaseModel::getClientId, clientId);
        return this.list(query);
    }

    @Override
    public void removeByClientId(Long clientId) {
        LambdaQueryWrapper<CorpShareholderInfo> query = Wrappers.lambdaQuery();
        query.eq(ClientBaseModel::getClientId, clientId);
        this.remove(query);
    }

    public void add(CorpShareholderInfoAddREQ req) {
        try {
            Client existClient = clientMapper.selectById(req.getClientId());
            if (existClient == null) {
                throw new MithrasException("客户已不存在");
            }
            Long startUserId = Optional.ofNullable(AccountUtil.getLoginInfo())
                    .map(AccountVO::getId)
                    .orElseThrow(() -> new MithrasException(ResultMsg.USER_NOT_LOGIN));
            NewCorpShareholderInfo info = copyProperties(req, NewCorpShareholderInfo.class);
            info.setUserId(startUserId);
            check(info);
//            setClientAuthority(info, existClient, startUserId);
            newShareholderInfoMapper.insert(info);
            recordClientStatus(info);
            // 新表抄老表
            ClientCopyInfoBO clientCopyInfoBO = ClientCopyInfoBO.builder()
                    .clientId(existClient.getId())
                    .currentUserId(startUserId)
//                    .moduleList(Collections.singletonList(InfoModule.CORP_SHAREHOLDER))
                    .build();
            authorityUtil.copyFromNewToOld(clientCopyInfoBO);
//            existClient.setLatestUserId(startUserId);
//            clientMapper.updateAnnotationIncludeNullById(existClient);
        } catch (DuplicateKeyException e) {
            throw new MithrasException("同名股东已存在");
        }
    }

    public void modify(CorpShareholderInfoModifyREQ req) {
        NewCorpShareholderInfo originalInfo = newShareholderInfoMapper.selectById(req.getId());
        if (isNull(originalInfo)) {
            throw new MithrasException(RECORD_NOT_EXIST);
        }
        Long currentUserId = AccountUtil.getLoginInfo().getId();
        if (!Objects.equals(originalInfo.getUserId(), currentUserId)) {
            throw new MithrasException("无权修改数据");
        }
        Client existClient = clientMapper.selectById(req.getClientId());
        if (existClient == null) {
            throw new MithrasException("客户已不存在");
        }
        NewCorpShareholderInfo info = copyProperties(req, NewCorpShareholderInfo.class);
        check(info);
        newShareholderInfoMapper.updateAnnotationIncludeNullById(info);
        recordClientStatus(info);
        // 新表抄老表
        ClientCopyInfoBO clientCopyInfoBO = ClientCopyInfoBO.builder()
                .clientId(info.getClientId())
                .currentUserId(currentUserId)
//                .moduleList(Collections.singletonList(InfoModule.CORP_SHAREHOLDER))
                .build();
        authorityUtil.copyFromNewToOld(clientCopyInfoBO);
//        existClient.setLatestUserId(currentUserId);
//        clientMapper.updateAnnotationIncludeNullById(existClient);
    }

    public void remove(Long id) {
        NewCorpShareholderInfo originalInfo = newShareholderInfoMapper.selectById(id);
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
        newShareholderInfoMapper.deleteById(id);
        recordClientStatus(originalInfo);
        ClientCopyInfoBO clientCopyInfoBO = ClientCopyInfoBO.builder()
                .clientId(existClient.getId())
                .currentUserId(currentUserId)
                .build();
        authorityUtil.copyFromNewToOld(clientCopyInfoBO);
//        existClient.setLatestUserId(currentUserId);
//        clientMapper.updateAnnotationIncludeNullById(existClient);
    }

    public Page<CorpShareholderInfo> list(CorpShareholderInfoListREQ req, SFunction<NewCorpShareholderInfo, ?> orderBy, String orderType) {
        Client existClient = clientMapper.selectById(req.getClientId());
        if (existClient == null) {
            throw new MithrasException("客户已不存在");
        }
        Long startUserId = req.getStartUserId();
        LambdaQueryWrapper<NewCorpShareholderInfo> query = Wrappers.lambdaQuery();
        query.eq(ClientBaseModel::getClientId, req.getClientId());
        if (Objects.nonNull(startUserId)) {
            // 流程中就看流程发起人
            query.eq(NewCorpShareholderInfo::getUserId, startUserId);
        } else {
            Long userId = authorityUtil.ensureNoProcessViewWhichUserData(req.getClientId());
            if (Objects.isNull(userId)) {
                // 说明没有可看的数据
                return new Page<>(req.getPage(), req.getPageSize());
            } else {
                query.eq(NewCorpShareholderInfo::getUserId, userId);
            }
        }
        Page<NewCorpShareholderInfo> data = newShareholderInfoMapper.selectPage(new Page<>(req.getPage(), req.getPageSize()), query);
        List<CorpShareholderInfo> list = BeanUtil.copyToList(data.getRecords(), CorpShareholderInfo.class);
        return new Page<CorpShareholderInfo>().setRecords(list).setCurrent(data.getCurrent()).setSize(data.getSize()).setTotal(data.getTotal());
    }

    public Page<NewCorpShareholderInfo> newList(CorpShareholderInfoListREQ req, SFunction<NewCorpShareholderInfo, ?> orderBy, String orderType) {
        Long startUserId = Optional.ofNullable(AccountUtil.getLoginInfo())
                .map(AccountVO::getId)
                .orElseThrow(() -> new MithrasException(ResultMsg.USER_NOT_LOGIN));
        LambdaQueryWrapper<NewCorpShareholderInfo> w = Wrappers.<NewCorpShareholderInfo>lambdaQuery()
                .eq(NewCorpShareholderInfo::getClientId, req.getClientId())
                .eq(NewCorpShareholderInfo::getUserId, startUserId);
        if (ascend.name().equals(orderType)) {
            w.orderByAsc(orderBy);
        } else {
            w.orderByDesc(orderBy);
        }
        return newShareholderInfoMapper.selectPage(new Page<>(req.getPage(), req.getPageSize()), w);
    }

    public void removeByName(Long clientId, String shareholderName) {
        Long startUserId = Optional.ofNullable(AccountUtil.getLoginInfo())
                .map(AccountVO::getId)
                .orElseThrow(() -> new MithrasException(ResultMsg.USER_NOT_LOGIN));
        checkByClientId(clientId);
        Client existClient = clientMapper.selectById(clientId);
        if (existClient == null) {
            throw new MithrasException("客户已不存在");
        }
        newShareholderInfoMapper.delete(Wrappers.<NewCorpShareholderInfo>lambdaQuery()
                .eq(NewCorpShareholderInfo::getShareholderName, shareholderName)
                .eq(NewCorpShareholderInfo::getClientId, clientId)
                .eq(NewCorpShareholderInfo::getUserId, startUserId));
        recordClientStatusByClientId(clientId);
        ClientCopyInfoBO clientCopyInfoBO = ClientCopyInfoBO.builder()
                .clientId(existClient.getId())
                .currentUserId(startUserId)
                .build();
        authorityUtil.copyFromNewToOld(clientCopyInfoBO);
//        existClient.setLatestUserId(startUserId);
//        clientMapper.updateAnnotationIncludeNullById(existClient);
    }

    public void batchAdd(Long clientId, List<NewCorpShareholderInfo> list) {
        Client existClient = clientMapper.selectById(clientId);
        if (existClient == null) {
            throw new MithrasException("客户已不存在");
        }
        Long startUserId = Optional.ofNullable(AccountUtil.getLoginInfo())
                .map(AccountVO::getId)
                .orElseThrow(() -> new MithrasException(ResultMsg.USER_NOT_LOGIN));
        list.forEach(e -> {
            e.setClientId(clientId);
            e.setUserId(startUserId);
//            setClientAuthority(e, existClient, startUserId);
            newShareholderInfoMapper.insert(e);
        });
        // 新表抄老表
        ClientCopyInfoBO clientCopyInfoBO = ClientCopyInfoBO.builder()
                .clientId(clientId)
                .currentUserId(AccountUtil.getLoginInfo().getId())
                .moduleList(Collections.singletonList(InfoModule.CORP_SHAREHOLDER))
                .build();
        authorityUtil.copyFromNewToOld(clientCopyInfoBO);
    }

//    private void setClientAuthority(NewCorpShareholderInfo newCorpShareholderInfo, Client existClient, Long startUserId) {
//        ClientAuthority clientAuthority = clientAuthorityMapper.selectOne(Wrappers.<ClientAuthority>lambdaQuery()
//                .eq(ClientAuthority::getClientId, existClient.getId())
//                .eq(ClientAuthority::getUserId, startUserId)
//                .eq(ClientAuthority::getDeleted, 0));
//        newCorpShareholderInfo.setUserId(startUserId);
//        newCorpShareholderInfo.setClientStatus(existClient.getClientStatus());
//        newCorpShareholderInfo.setIsReleased(existClient.getIsReleased());
//        newCorpShareholderInfo.setLevel(clientAuthority != null ? clientAuthority.getLevel() : 0);
//    }
}
