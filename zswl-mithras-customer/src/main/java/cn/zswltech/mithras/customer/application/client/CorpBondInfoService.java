package cn.zswltech.mithras.customer.application.client;

import cn.zswltech.mithras.customer.application.client.ClientDataSaveCheckInterface;
import cn.zswltech.mithras.customer.domain.enums.InfoModule;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.gruul.dao.dal.vo.AccountVO;
import cn.zswltech.mithras.dto.client.bondinfo.CorpBondInfoAddREQ;
import cn.zswltech.mithras.dto.client.bondinfo.CorpBondInfoListREQ;
import cn.zswltech.mithras.dto.client.bondinfo.CorpBondInfoModifyREQ;
import cn.zswltech.mithras.dto.client.bondinfo.CorpBondInfoRemoveREQ;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.client.ClientMapper;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.corp.CorpBondInfoMapper;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.corp.NewCorpBondInfoMapper;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.*;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.customer.application.bo.ClientCopyInfoBO;
import cn.zswltech.mithras.customer.application.client.copyhandler.ClientOldDataHelper;
import cn.zswltech.mithras.customer.application.client.ClientAuthorityDataPort;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static cn.hutool.core.bean.BeanUtil.copyProperties;
import static cn.hutool.core.util.ObjectUtil.isNull;
import static cn.zswltech.mithras.service.constant.ResultMsg.RECORD_NOT_EXIST;

/**
 * @author luyi
 */
@Service
public class CorpBondInfoService extends ServiceImpl<CorpBondInfoMapper, CorpBondInfo> implements ClientDataSaveCheckInterface<NewCorpBondInfo>, ClientOldDataHelper<CorpBondInfo> {
    @Resource
    private CorpBondInfoMapper bondInfoMapper;
    @Resource
    private NewCorpBondInfoMapper newBondInfoMapper;
    @Resource
    private ClientMapper clientMapper;
    @Resource
    private ClientAuthorityDataPort authorityUtil;

    @Override
    public List<CorpBondInfo> findByClientId(Long clientId) {
        LambdaQueryWrapper<CorpBondInfo> query = Wrappers.lambdaQuery();
        query.eq(ClientBaseModel::getClientId, clientId);
        return this.list(query);
    }

    @Override
    public void removeByClientId(Long clientId) {
        LambdaQueryWrapper<CorpBondInfo> query = Wrappers.lambdaQuery();
        query.eq(ClientBaseModel::getClientId, clientId);
        this.remove(query);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void add(CorpBondInfoAddREQ req) {
        Client existClient = clientMapper.selectById(req.getClientId());
        if (existClient == null) {
            throw new MithrasException("客户已不存在");
        }
        //增加新表数据
        NewCorpBondInfo info = copyProperties(req, NewCorpBondInfo.class);
        Long startUserId = Optional.ofNullable(AccountUtil.getLoginInfo())
                .map(AccountVO::getId)
                .orElseThrow(() -> new MithrasException(ResultMsg.USER_NOT_LOGIN));
        info.setUserId(startUserId);
        check(info);
//        setClientAuthority(info, existClient, startUserId);
        newBondInfoMapper.insert(info);
        recordClientStatus(info);
        // 新表抄老表
        ClientCopyInfoBO clientCopyInfoBO = ClientCopyInfoBO.builder()
                .clientId(existClient.getId())
                .currentUserId(startUserId)
//                .moduleList(Collections.singletonList(InfoModule.CORP_BOND))
                .build();
        authorityUtil.copyFromNewToOld(clientCopyInfoBO);
//        existClient.setLatestUserId(startUserId);
//        clientMapper.updateAnnotationIncludeNullById(existClient);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void modify(CorpBondInfoModifyREQ req) {
        NewCorpBondInfo originalInfo = newBondInfoMapper.selectById(req.getId());
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
        NewCorpBondInfo info = copyProperties(req, NewCorpBondInfo.class);
        check(info);
        newBondInfoMapper.updateAnnotationIncludeNullById(info);
        recordClientStatus(info);
        // 新表抄老表
        ClientCopyInfoBO clientCopyInfoBO = ClientCopyInfoBO.builder()
                .clientId(info.getClientId())
                .currentUserId(currentUserId)
//                .moduleList(Collections.singletonList(InfoModule.CORP_BOND))
                .build();
        authorityUtil.copyFromNewToOld(clientCopyInfoBO);
//        existClient.setLatestUserId(currentUserId);
//        clientMapper.updateAnnotationIncludeNullById(existClient);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void remove(CorpBondInfoRemoveREQ req) {
        NewCorpBondInfo originalInfo = newBondInfoMapper.selectById(req.getId());
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
        newBondInfoMapper.deleteById(req.getId());
        recordClientStatus(originalInfo);
        // 新表抄老表
        ClientCopyInfoBO clientCopyInfoBO = ClientCopyInfoBO.builder()
                .clientId(existClient.getId())
                .currentUserId(currentUserId)
                .build();
        authorityUtil.copyFromNewToOld(clientCopyInfoBO);
//        existClient.setLatestUserId(currentUserId);
//        clientMapper.updateAnnotationIncludeNullById(existClient);
    }

    public Page<CorpBondInfo> list(CorpBondInfoListREQ req) {
        Client existClient = clientMapper.selectById(req.getClientId());
        if (existClient == null) {
            throw new MithrasException("客户已不存在");
        }
        Long startUserId = req.getStartUserId();
        LambdaQueryWrapper<NewCorpBondInfo> query = Wrappers.lambdaQuery();
        query.eq(ClientBaseModel::getClientId, req.getClientId());
        if (Objects.nonNull(startUserId)) {
            // 流程中就看流程发起人
            query.eq(NewCorpBondInfo::getUserId, startUserId);
        } else {
            Long userId = authorityUtil.ensureNoProcessViewWhichUserData(req.getClientId());
            if (Objects.isNull(userId)) {
                // 说明没有可看的数据
                return new Page<>(req.getPage(), req.getPageSize());
            } else {
                query.eq(NewCorpBondInfo::getUserId, userId);
            }
        }
        Page<NewCorpBondInfo> data = newBondInfoMapper.selectPage(new Page<>(req.getPage(), req.getPageSize()), query);
        List<CorpBondInfo> list = BeanUtil.copyToList(data.getRecords(), CorpBondInfo.class);
        return new Page<CorpBondInfo>().setRecords(list).setCurrent(data.getCurrent()).setSize(data.getSize()).setTotal(data.getTotal());
    }

//    private void setClientAuthority(NewCorpBondInfo newCorpBondInfo, Client existClient, Long startUserId) {
//        ClientAuthority clientAuthority = clientAuthorityMapper.selectOne(Wrappers.<ClientAuthority>lambdaQuery()
//                .eq(ClientAuthority::getClientId, existClient.getId())
//                .eq(ClientAuthority::getUserId, startUserId)
//                .eq(ClientAuthority::getDeleted, 0));
//        newCorpBondInfo.setUserId(startUserId);
//        newCorpBondInfo.setClientStatus(existClient.getClientStatus());
//        newCorpBondInfo.setIsReleased(existClient.getIsReleased());
//        newCorpBondInfo.setLevel(clientAuthority != null ? clientAuthority.getLevel() : 0);
//    }
}
