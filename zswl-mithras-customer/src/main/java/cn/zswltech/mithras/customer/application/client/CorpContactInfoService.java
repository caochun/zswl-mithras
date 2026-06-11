package cn.zswltech.mithras.customer.application.client;

import cn.zswltech.mithras.customer.application.client.ClientDataSaveCheckInterface;
import cn.zswltech.mithras.customer.enums.InfoModule;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.gruul.dao.dal.vo.AccountVO;
import cn.zswltech.mithras.dto.client.contactinfo.CorpContactAddInfoREQ;
import cn.zswltech.mithras.dto.client.contactinfo.CorpContactInfoListREQ;
import cn.zswltech.mithras.dto.client.contactinfo.CorpContactInfoModifyREQ;
import cn.zswltech.mithras.foundation.constant.ResultMsg;
import cn.zswltech.mithras.customer.mapper.client.ClientMapper;
import cn.zswltech.mithras.customer.mapper.corp.CorpContactInfoMapper;
import cn.zswltech.mithras.customer.mapper.corp.NewCorpContactInfoMapper;
import cn.zswltech.mithras.customer.mapper.lib.client.CorpContactInfoLibMapper;
import cn.zswltech.mithras.customer.model.client.*;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.customer.application.client.bo.ClientCopyInfoBO;
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
import static cn.zswltech.mithras.foundation.constant.ResultMsg.RECORD_NOT_EXIST;

/**
 * @author luyi
 */
@Service
public class CorpContactInfoService extends ServiceImpl<CorpContactInfoMapper, CorpContactInfo> implements ClientDataSaveCheckInterface<NewCorpContactInfo>, ClientOldDataHelper<CorpContactInfo> {

    @Resource
    private NewCorpContactInfoMapper newContactInfoMapper;

    @Resource
    private CorpContactInfoLibMapper contactInfoLibMapper;

    @Resource
    private ClientMapper clientMapper;

    @Resource
    private ClientAuthorityDataPort authorityUtil;

    @Override
    public List<CorpContactInfo> findByClientId(Long clientId) {
        LambdaQueryWrapper<CorpContactInfo> query = Wrappers.lambdaQuery();
        query.eq(ClientBaseModel::getClientId, clientId);
        return this.list(query);
    }

    @Override
    public void removeByClientId(Long clientId) {
        LambdaQueryWrapper<CorpContactInfo> query = Wrappers.lambdaQuery();
        query.eq(ClientBaseModel::getClientId, clientId);
        this.remove(query);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void add(CorpContactAddInfoREQ req) {
        Client existClient = clientMapper.selectById(req.getClientId());
        if (existClient == null) {
            throw new MithrasException("客户已不存在");
        }
        NewCorpContactInfo info = copyProperties(req, NewCorpContactInfo.class);
        Long startUserId = Optional.ofNullable(AccountUtil.getLoginInfo())
                .map(AccountVO::getId)
                .orElseThrow(() -> new MithrasException(ResultMsg.USER_NOT_LOGIN));
        info.setUserId(startUserId);
        check(info);
//        setClientAuthority(info, existClient, startUserId);
        newContactInfoMapper.insert(info);
        recordClientStatus(info);
        // 新表抄老表
        ClientCopyInfoBO clientCopyInfoBO = ClientCopyInfoBO.builder()
                .clientId(existClient.getId())
                .currentUserId(startUserId)
//                .moduleList(Collections.singletonList(InfoModule.CORP_CONTACT))
                .build();
        authorityUtil.copyFromNewToOld(clientCopyInfoBO);
//        existClient.setLatestUserId(startUserId);
//        clientMapper.updateAnnotationIncludeNullById(existClient);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void modify(CorpContactInfoModifyREQ req) {
        NewCorpContactInfo originalInfo = newContactInfoMapper.selectById(req.getId());
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
        NewCorpContactInfo info = copyProperties(req, NewCorpContactInfo.class);
        check(info);
        newContactInfoMapper.updateAnnotationIncludeNullById(info);
        recordClientStatus(info);
        // 新表抄老表
        ClientCopyInfoBO clientCopyInfoBO = ClientCopyInfoBO.builder()
                .clientId(info.getClientId())
                .currentUserId(currentUserId)
//                .moduleList(Collections.singletonList(InfoModule.CORP_CONTACT))
                .build();
        authorityUtil.copyFromNewToOld(clientCopyInfoBO);
//        existClient.setLatestUserId(currentUserId);
//        clientMapper.updateAnnotationIncludeNullById(existClient);
    }

    public Page<CorpContactInfo> list(CorpContactInfoListREQ req) {
        Client existClient = clientMapper.selectById(req.getClientId());
        if (existClient == null) {
            throw new MithrasException("客户已不存在");
        }
        Long startUserId = req.getStartUserId();
        LambdaQueryWrapper<NewCorpContactInfo> query = Wrappers.lambdaQuery();
        query.eq(ClientBaseModel::getClientId, req.getClientId());
        if (Objects.nonNull(startUserId)) {
            // 流程中就看流程发起人
            query.eq(NewCorpContactInfo::getUserId, startUserId);
        } else {
            Long userId = authorityUtil.ensureNoProcessViewWhichUserData(req.getClientId());
            if (Objects.isNull(userId)) {
                // 说明没有可看的数据
                return new Page<>(req.getPage(), req.getPageSize());
            } else {
                query.eq(NewCorpContactInfo::getUserId, userId);
            }
        }
        Page<NewCorpContactInfo> data = newContactInfoMapper.selectPage(new Page<>(req.getPage(), req.getPageSize()), query);
        List<CorpContactInfo> list = BeanUtil.copyToList(data.getRecords(), CorpContactInfo.class);
        return new Page<CorpContactInfo>().setRecords(list).setCurrent(data.getCurrent()).setSize(data.getSize()).setTotal(data.getTotal());
    }

    public Page<CorpContactInfoLib> listOld(CorpContactInfoListREQ req) {
        Client client = clientMapper.selectOne(Wrappers.<Client>lambdaQuery()
                .eq(Client::getId, req.getClientId())
                .orderByDesc(Client::getNewestVersion)
                .last("LIMIT 1"));
        if (ObjectUtil.isNull(client)) {
            throw new MithrasException("此数据无版本信息");
        }
        Page<CorpContactInfoLib> corpContactInfoLibPage = contactInfoLibMapper.selectPage(new Page<>(req.getPage(), req.getPageSize()),
                Wrappers.<CorpContactInfoLib>lambdaQuery().eq(CorpContactInfoLib::getClientId, req.getClientId())
                        .eq(CorpContactInfoLib::getVersion, client.getNewestVersion())
                        .orderByDesc(CorpContactInfoLib::getUpdateTime)
        );
        if (ObjectUtil.isNull(corpContactInfoLibPage.getRecords()) || corpContactInfoLibPage.getRecords().size() == 0) {
            throw new MithrasException("无生效状态联系人，请至客户管理页面维护");
        }
        return corpContactInfoLibPage;
    }

    @Transactional(rollbackFor = Throwable.class)
    public void remove(Long id) {
        NewCorpContactInfo originalInfo = newContactInfoMapper.selectById(id);
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
        newContactInfoMapper.deleteById(id);
        recordClientStatus(originalInfo);
        ClientCopyInfoBO clientCopyInfoBO = ClientCopyInfoBO.builder()
                .clientId(existClient.getId())
                .currentUserId(currentUserId)
                .build();
        authorityUtil.copyFromNewToOld(clientCopyInfoBO);
//        existClient.setLatestUserId(currentUserId);
//        clientMapper.updateAnnotationIncludeNullById(existClient);
    }

//    private void setClientAuthority(NewCorpContactInfo newCorpContactInfo, Client existClient, Long startUserId) {
//        ClientAuthority clientAuthority = clientAuthorityMapper.selectOne(Wrappers.<ClientAuthority>lambdaQuery()
//                .eq(ClientAuthority::getClientId, existClient.getId())
//                .eq(ClientAuthority::getUserId, startUserId)
//                .eq(ClientAuthority::getDeleted, 0));
//        newCorpContactInfo.setUserId(startUserId);
//        newCorpContactInfo.setClientStatus(existClient.getClientStatus());
//        newCorpContactInfo.setIsReleased(existClient.getIsReleased());
//        newCorpContactInfo.setLevel(clientAuthority != null ? clientAuthority.getLevel() : 0);
//    }
}
