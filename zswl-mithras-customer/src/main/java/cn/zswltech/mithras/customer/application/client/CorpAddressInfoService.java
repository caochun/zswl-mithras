package cn.zswltech.mithras.customer.application.client;

import cn.zswltech.mithras.customer.application.client.ClientDataSaveCheckInterface;
import cn.zswltech.mithras.customer.enums.InfoModule;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.gruul.dao.dal.vo.AccountVO;
import cn.zswltech.mithras.dto.client.addressinfo.CorpAddressInfoAddREQ;
import cn.zswltech.mithras.dto.client.addressinfo.CorpAddressInfoListREQ;
import cn.zswltech.mithras.dto.client.addressinfo.CorpAddressInfoModifyREQ;
import cn.zswltech.mithras.foundation.constant.ResultMsg;
import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.basedata.mapper.AddressDictionaryMapper;
import cn.zswltech.mithras.customer.mapper.client.ClientMapper;
import cn.zswltech.mithras.customer.mapper.corp.CorpAddressInfoMapper;
import cn.zswltech.mithras.basedata.mapper.GeneralDictionaryMapper;
import cn.zswltech.mithras.customer.mapper.corp.NewCorpAddressInfoMapper;
import cn.zswltech.mithras.basedata.mapper.model.AddressDictionary;
import cn.zswltech.mithras.basedata.mapper.model.GeneralDictionary;
import cn.zswltech.mithras.customer.model.client.Client;
import cn.zswltech.mithras.customer.model.client.ClientBaseModel;
import cn.zswltech.mithras.customer.model.client.CorpAddressInfo;
import cn.zswltech.mithras.customer.model.client.NewCorpAddressInfo;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.customer.application.client.model.ClientCopyInfoBO;
import cn.zswltech.mithras.customer.application.client.copyhandler.ClientOldDataHelper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static cn.hutool.core.bean.BeanUtil.copyProperties;
import static cn.hutool.core.util.ObjectUtil.isNull;
import static cn.zswltech.mithras.foundation.constant.ResultMsg.RECORD_NOT_EXIST;
import static cn.zswltech.mithras.customer.enums.CorpAddressType.REGISTRY_ADDRESS;
import static cn.zswltech.mithras.foundation.util.Const.ENUM_TYC_PROVINCE;

/**
 * @author luyi
 */
@Service
public class CorpAddressInfoService extends ServiceImpl<CorpAddressInfoMapper, CorpAddressInfo> implements ClientDataSaveCheckInterface<NewCorpAddressInfo>, ClientOldDataHelper<CorpAddressInfo> {

    @Resource
    private CorpAddressInfoMapper addressInfoMapper;
    @Resource
    private NewCorpAddressInfoMapper newAddressInfoMapper;
    @Resource
    private AddressDictionaryMapper addressDictionaryMapper;
    @Resource
    private GeneralDictionaryMapper generalDictionaryMapper;
    @Resource
    private ClientMapper clientMapper;
    @Resource
    private ClientAuthorityDataPort authorityUtil;

    public Map<Long, List<CorpAddressInfo>> getRegistryAddressMap(Collection<Long> clientIds) {
        LambdaQueryWrapper<CorpAddressInfo> query = Wrappers.lambdaQuery();
        query.in(ClientBaseModel::getClientId, clientIds);
        query.eq(CorpAddressInfo::getAddressType, REGISTRY_ADDRESS.name());
        query.orderByDesc(ClientBaseModel::getId);
        return this.list(query).stream().collect(Collectors.groupingBy(ClientBaseModel::getClientId));
    }

    @Override
    public List<CorpAddressInfo> findByClientId(Long clientId) {
        LambdaQueryWrapper<CorpAddressInfo> query = Wrappers.lambdaQuery();
        query.eq(ClientBaseModel::getClientId, clientId);
        return this.list(query);
    }

    @Override
    public void removeByClientId(Long clientId) {
        LambdaQueryWrapper<CorpAddressInfo> query = Wrappers.lambdaQuery();
        query.eq(ClientBaseModel::getClientId, clientId);
        this.remove(query);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void add(CorpAddressInfoAddREQ req) {
        Client existClient = clientMapper.selectById(req.getClientId());
        if (existClient == null) {
            throw new MithrasException("客户已不存在");
        }
        CorpAddressInfo info = copyProperties(req, CorpAddressInfo.class);
        //增加新表数据
        NewCorpAddressInfo newCorpAddressInfo = copyProperties(info, NewCorpAddressInfo.class);
        Long startUserId = Optional.ofNullable(AccountUtil.getLoginInfo())
                .map(AccountVO::getId)
                .orElseThrow(() -> new MithrasException(ResultMsg.USER_NOT_LOGIN));
        newCorpAddressInfo.setUserId(startUserId);
//        setClientAuthority(newCorpAddressInfo, existClient, startUserId);
        newAddressInfoMapper.insert(newCorpAddressInfo);
        recordClientStatus(newCorpAddressInfo);
        // 新表抄老表
        ClientCopyInfoBO clientCopyInfoBO = ClientCopyInfoBO.builder()
                .clientId(existClient.getId())
                .currentUserId(startUserId)
//                .moduleList(Collections.singletonList(InfoModule.CORP_ADDRESS))
                .build();
        authorityUtil.copyFromNewToOld(clientCopyInfoBO);
//        existClient.setLatestUserId(startUserId);
//        clientMapper.updateAnnotationIncludeNullById(existClient);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void modify(CorpAddressInfoModifyREQ req) {
        Client existClient = clientMapper.selectById(req.getClientId());
        if (existClient == null) {
            throw new MithrasException("客户已不存在");
        }
        NewCorpAddressInfo originalInfo = newAddressInfoMapper.selectById(req.getId());
        if (isNull(originalInfo)) {
            throw new MithrasException(RECORD_NOT_EXIST);
        }
        Long currentUserId = AccountUtil.getLoginInfo().getId();
        if (!Objects.equals(originalInfo.getUserId(), currentUserId)) {
            throw new MithrasException("无权修改数据");
        }
        NewCorpAddressInfo info = copyProperties(req, NewCorpAddressInfo.class);
        check(info);
        newAddressInfoMapper.updateAnnotationIncludeNullById(info);
        recordClientStatus(info);
        // 新表抄老表
        ClientCopyInfoBO clientCopyInfoBO = ClientCopyInfoBO.builder()
                .clientId(info.getClientId())
                .currentUserId(currentUserId)
//                .moduleList(Collections.singletonList(InfoModule.CORP_ADDRESS))
                .build();
        authorityUtil.copyFromNewToOld(clientCopyInfoBO);
//        existClient.setLatestUserId(currentUserId);
//        clientMapper.updateAnnotationIncludeNullById(existClient);
    }

    public Page<CorpAddressInfo> list(CorpAddressInfoListREQ req) {
        Client existClient = clientMapper.selectById(req.getClientId());
        if (existClient == null) {
            throw new MithrasException("客户已不存在");
        }
        Long startUserId = req.getStartUserId();
        LambdaQueryWrapper<NewCorpAddressInfo> query = Wrappers.lambdaQuery();
        query.eq(ClientBaseModel::getClientId, req.getClientId());
        if (Objects.nonNull(startUserId)) {
            // 流程中就看流程发起人
            query.eq(NewCorpAddressInfo::getUserId, startUserId);
        } else {
            Long userId = authorityUtil.ensureNoProcessViewWhichUserData(req.getClientId());
            if (Objects.isNull(userId)) {
                // 说明没有可看的数据
                return new Page<>(req.getPage(), req.getPageSize());
            } else {
                query.eq(NewCorpAddressInfo::getUserId, userId);
            }
        }
        Page<NewCorpAddressInfo> data = newAddressInfoMapper.selectPage(new Page<>(req.getPage(), req.getPageSize()), query);
        List<CorpAddressInfo> list = BeanUtil.copyToList(data.getRecords(), CorpAddressInfo.class);
        return new Page<CorpAddressInfo>().setRecords(list).setCurrent(data.getCurrent()).setSize(data.getSize()).setTotal(data.getTotal());
    }

    @Transactional(rollbackFor = Throwable.class)
    public void remove(Long id) {
        NewCorpAddressInfo originalInfo = newAddressInfoMapper.selectById(id);
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
        newAddressInfoMapper.deleteById(id);
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


    public void addByCommerceInfo(Long clientId, CorpAddressTycInfo mithrasBaseInfo) {
        Long startUserId = Optional.ofNullable(AccountUtil.getLoginInfo())
                .map(AccountVO::getId)
                .orElseThrow(() -> new MithrasException(ResultMsg.USER_NOT_LOGIN));
        Client existClient = clientMapper.selectById(clientId);
        if (existClient == null) {
            throw new MithrasException("客户已不存在");
        }
        if (newAddressInfoMapper.selectList(Wrappers.<NewCorpAddressInfo>lambdaQuery()
                .eq(NewCorpAddressInfo::getClientId, clientId)
                .eq(NewCorpAddressInfo::getUserId, startUserId)
                .eq(NewCorpAddressInfo::getAddressType, REGISTRY_ADDRESS.name())).isEmpty()) {

            NewCorpAddressInfo info = new NewCorpAddressInfo();
            info.setCountry("156");//默认中国
            info.setAddressType(REGISTRY_ADDRESS.name());
            GeneralDictionary tycProvince = generalDictionaryMapper.selectOne(Wrappers.<GeneralDictionary>lambdaQuery()
                    .eq(GeneralDictionary::getDictKey, ENUM_TYC_PROVINCE)
                    .eq(GeneralDictionary::getCode, mithrasBaseInfo.getBase())
            );
            if (null != tycProvince) {
                AddressDictionary province = addressDictionaryMapper.selectOne(
                        Wrappers.<AddressDictionary>lambdaQuery()
                                .eq(AddressDictionary::getDisplay, tycProvince.getDisplay())
                                .eq(AddressDictionary::getHistory, YesOrNoNumberEnum.NO.getCode())
                );
                if (null != province) {
                    info.setProvince(province.getCode());
                    AddressDictionary city = addressDictionaryMapper.selectOne(
                            Wrappers.<AddressDictionary>lambdaQuery()
                                    .eq(AddressDictionary::getParentId, province.getId())
                                    .eq(AddressDictionary::getDisplay, mithrasBaseInfo.getCity())
                                    .eq(AddressDictionary::getHistory, YesOrNoNumberEnum.NO.getCode())
                    );
                    if (null != city) {
                        info.setCity(city.getCode());
                        AddressDictionary district = addressDictionaryMapper.selectOne(
                                Wrappers.<AddressDictionary>lambdaQuery()
                                        .eq(AddressDictionary::getParentId, city.getId())
                                        .eq(AddressDictionary::getDisplay, mithrasBaseInfo.getDistrict())
                                        .eq(AddressDictionary::getHistory, YesOrNoNumberEnum.NO.getCode())
                        );
                        if (null != district) {
                            info.setDistrict(district.getCode());
                            info.setRegionCode(district.getCode());
                        }
                    }
                }
            }
            info.setDetail(mithrasBaseInfo.getRegLocation());
            info.setClientId(clientId);
            info.setUserId(startUserId);
//            setClientAuthority(info, existClient, startUserId);
            newAddressInfoMapper.insert(info);
            // 新表抄老表
            ClientCopyInfoBO clientCopyInfoBO = ClientCopyInfoBO.builder()
                    .clientId(clientId)
                    .currentUserId(startUserId)
//                    .moduleList(Collections.singletonList(InfoModule.CORP_ADDRESS))
                    .build();
            authorityUtil.copyFromNewToOld(clientCopyInfoBO);
//            existClient.setLatestUserId(startUserId);
//            clientMapper.updateAnnotationIncludeNullById(existClient);
        }
    }

    public List<CorpAddressInfo> listCorpAddressInfo(Long clientId) {
        LambdaQueryWrapper<CorpAddressInfo> query = Wrappers.lambdaQuery();
        query.eq(ClientBaseModel::getClientId, clientId);
        return addressInfoMapper.selectList(query);
    }

//    private void setClientAuthority(NewCorpAddressInfo newCorpAddressInfo, Client existClient, Long startUserId) {
//        ClientAuthority clientAuthority = clientAuthorityMapper.selectOne(Wrappers.<ClientAuthority>lambdaQuery()
//                .eq(ClientAuthority::getClientId, existClient.getId())
//                .eq(ClientAuthority::getUserId, startUserId)
//                .eq(ClientAuthority::getDeleted, 0));
//        newCorpAddressInfo.setUserId(startUserId);
//        newCorpAddressInfo.setClientStatus(existClient.getClientStatus());
//        newCorpAddressInfo.setIsReleased(existClient.getIsReleased());
//        newCorpAddressInfo.setLevel(clientAuthority != null ? clientAuthority.getLevel() : 0);
//    }
}
