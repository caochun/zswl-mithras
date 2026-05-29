package cn.zswltech.mithras.service.service.client;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.gruul.dao.dal.vo.AccountVO;
import cn.zswltech.mithras.dto.client.bankaccount.CorpBankAccountAddREQ;
import cn.zswltech.mithras.dto.client.bankaccount.CorpBankAccountListREQ;
import cn.zswltech.mithras.dto.client.bankaccount.CorpBankAccountModifyREQ;
import cn.zswltech.mithras.dto.client.bankaccount.CorpVersionedBankAccountListREQ;
import cn.zswltech.mithras.common.constant.ResultMsg;
import cn.zswltech.mithras.common.constant.VersionTypeConstants;
import cn.zswltech.mithras.service.mapper.client.ClientMapper;
import cn.zswltech.mithras.service.mapper.corp.CorpBankAccountMapper;
import cn.zswltech.mithras.service.mapper.corp.NewCorpBankAccountMapper;
import cn.zswltech.mithras.service.mapper.lib.client.CorpBankAccountLibMapper;
import cn.zswltech.mithras.service.mapper.model.client.*;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.bo.ClientCopyInfoBO;
import cn.zswltech.mithras.service.service.client.copyhandler.ClientOldDataHelper;
import cn.zswltech.mithras.service.util.ClientAuthorityUtil;
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
import static cn.hutool.core.text.CharSequenceUtil.isBlank;
import static cn.hutool.core.util.ObjectUtil.isNull;
import static cn.zswltech.mithras.common.constant.ResultMsg.RECORD_NOT_EXIST;

/**
 * @author luyi
 */
@Service
public class CorpBankAccountService extends ServiceImpl<CorpBankAccountMapper, CorpBankAccount> implements ClientDataSaveCheckInterface<NewCorpBankAccount>, ClientOldDataHelper<CorpBankAccount> {
    @Resource
    private NewCorpBankAccountMapper newBankAccountMapper;
    @Resource
    private CorpBankAccountLibMapper bankAccountLibMapper;
    @Resource
    private ClientMapper clientMapper;
    @Resource
    private ClientAuthorityUtil authorityUtil;

    @Override
    public List<CorpBankAccount> findByClientId(Long clientId) {
        LambdaQueryWrapper<CorpBankAccount> query = Wrappers.lambdaQuery();
        query.eq(ClientBaseModel::getClientId, clientId);
        return this.list(query);
    }

    @Override
    public void removeByClientId(Long clientId) {
        LambdaQueryWrapper<CorpBankAccount> query = Wrappers.lambdaQuery();
        query.eq(ClientBaseModel::getClientId, clientId);
        this.remove(query);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void add(CorpBankAccountAddREQ req) {
        Client existClient = clientMapper.selectById(req.getClientId());
        if (existClient == null) {
            throw new MithrasException("客户已不存在");
        }
        //增加新表数据
        NewCorpBankAccount info = copyProperties(req, NewCorpBankAccount.class);
        Long startUserId = Optional.ofNullable(AccountUtil.getLoginInfo())
                .map(AccountVO::getId)
                .orElseThrow(() -> new MithrasException(ResultMsg.USER_NOT_LOGIN));
        info.setUserId(startUserId);
        check(info);
//        setClientAuthority(info, existClient, startUserId);
        newBankAccountMapper.insert(info);
        recordClientStatus(info);
        // 新表抄老表
        ClientCopyInfoBO clientCopyInfoBO = ClientCopyInfoBO.builder()
                .clientId(existClient.getId())
                .currentUserId(startUserId)
//                .moduleList(Collections.singletonList(InfoModule.CORP_BANK_ACCOUNT))
                .build();
        authorityUtil.copyFromNewToOld(clientCopyInfoBO);
//        existClient.setLatestUserId(startUserId);
//        clientMapper.updateAnnotationIncludeNullById(existClient);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void modify(CorpBankAccountModifyREQ req) {
        NewCorpBankAccount originalInfo = newBankAccountMapper.selectById(req.getId());
        if (isNull(originalInfo)) {
            throw new MithrasException(RECORD_NOT_EXIST);
        }
        Client existClient = clientMapper.selectById(req.getClientId());
        if (existClient == null) {
            throw new MithrasException("客户已不存在");
        }
        Long currentUserId = AccountUtil.getLoginInfo().getId();
        if (!Objects.equals(originalInfo.getUserId(), currentUserId)) {
            throw new MithrasException("无权修改数据");
        }
        NewCorpBankAccount info = copyProperties(req, NewCorpBankAccount.class);
        check(info);
        newBankAccountMapper.updateAnnotationIncludeNullById(info);
        recordClientStatus(info);
        // 新表抄老表
        ClientCopyInfoBO clientCopyInfoBO = ClientCopyInfoBO.builder()
                .clientId(info.getClientId())
                .currentUserId(currentUserId)
//                .moduleList(Collections.singletonList(InfoModule.CORP_BANK_ACCOUNT))
                .build();
        authorityUtil.copyFromNewToOld(clientCopyInfoBO);
//        existClient.setLatestUserId(currentUserId);
//        clientMapper.updateAnnotationIncludeNullById(existClient);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void remove(Long id) {
        NewCorpBankAccount originalInfo = newBankAccountMapper.selectById(id);
        if (isNull(originalInfo)) {
            throw new MithrasException(RECORD_NOT_EXIST);
        }
        Client existClient = clientMapper.selectById(originalInfo.getClientId());
        if (existClient == null) {
            throw new MithrasException("客户已不存在");
        }
        Long currentUserId = AccountUtil.getLoginInfo().getId();
        if (!Objects.equals(originalInfo.getUserId(), currentUserId)) {
            throw new MithrasException("无权删除数据");
        }
        check(originalInfo);
        newBankAccountMapper.deleteById(id);
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

    public Page<CorpBankAccount> list(CorpBankAccountListREQ req) {

        Client existClient = clientMapper.selectById(req.getClientId());
        if (existClient == null) {
            throw new MithrasException("客户已不存在");
        }
        Long startUserId = req.getStartUserId();
        LambdaQueryWrapper<NewCorpBankAccount> query = Wrappers.lambdaQuery();
        query.eq(ClientBaseModel::getClientId, req.getClientId());
        if (Objects.nonNull(startUserId)) {
            // 流程中就看流程发起人
            query.eq(NewCorpBankAccount::getUserId, startUserId);
        } else {
            Long userId = authorityUtil.ensureNoProcessViewWhichUserData(req.getClientId());
            if (Objects.isNull(userId)) {
                // 说明没有可看的数据
                return new Page<>(req.getPage(), req.getPageSize());
            } else {
                query.eq(NewCorpBankAccount::getUserId, userId);
            }
        }
        Page<NewCorpBankAccount> data = newBankAccountMapper.selectPage(new Page<>(req.getPage(), req.getPageSize()), query);
        List<CorpBankAccount> list = BeanUtil.copyToList(data.getRecords(), CorpBankAccount.class);
        return new Page<CorpBankAccount>().setRecords(list).setCurrent(data.getCurrent()).setSize(data.getSize()).setTotal(data.getTotal());
    }

    public Page<CorpBankAccountLib> versionedList(CorpVersionedBankAccountListREQ req) {
        String version = req.getVersion();
        if (isBlank(version)) {
            List<CorpBankAccountLib> list = bankAccountLibMapper.selectList(
                    Wrappers.<CorpBankAccountLib>lambdaQuery()
                            .eq(CorpBankAccountLib::getClientId, req.getClientId())
                            .eq(CorpBankAccountLib::getVersionType, VersionTypeConstants.NORMAL)
                            .orderByDesc(CorpBankAccountLib::getVersion)
                            .last(" limit 1")
            );
            if (!list.isEmpty()) {
                version = list.get(0).getVersion();
            }
        }
        return bankAccountLibMapper.selectPage(new Page<>(req.getPage(), req.getPageSize()),
                Wrappers.<CorpBankAccountLib>lambdaQuery()
                        .eq(CorpBankAccountLib::getClientId, req.getClientId())
                        .eq(CorpBankAccountLib::getVersion, version)
                        .orderByDesc(CorpBankAccount::getUpdateTime)
        );
    }

//    private void setClientAuthority(NewCorpBankAccount newCorpBankAccount, Client existClient, Long startUserId) {
//        ClientAuthority clientAuthority = clientAuthorityMapper.selectOne(Wrappers.<ClientAuthority>lambdaQuery()
//                .eq(ClientAuthority::getClientId, existClient.getId())
//                .eq(ClientAuthority::getUserId, startUserId)
//                .eq(ClientAuthority::getDeleted, 0));
//        newCorpBankAccount.setUserId(startUserId);
//        newCorpBankAccount.setClientStatus(existClient.getClientStatus());
//        newCorpBankAccount.setIsReleased(existClient.getIsReleased());
//        newCorpBankAccount.setLevel(clientAuthority != null ? clientAuthority.getLevel() : 0);
//    }
}
