package cn.zswltech.mithras.customer.application.client;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.gruul.dao.dal.vo.AccountVO;
import cn.zswltech.mithras.customer.application.client.bo.ClientAuthBO;
import cn.zswltech.mithras.customer.application.client.bo.ClientBusinessHistoryBO;
import cn.zswltech.mithras.customer.application.client.bo.ClientCopyInfoBO;
import cn.zswltech.mithras.customer.application.client.copyhandler.ClientOldDataHelper;
import cn.zswltech.mithras.customer.enums.ClientAuthEnum;
import cn.zswltech.mithras.customer.enums.client.ClientStatus;
import cn.zswltech.mithras.customer.enums.client.EnterpriseNatureEnum;
import cn.zswltech.mithras.customer.enums.client.OwnershipTypeEnum;
import cn.zswltech.mithras.customer.mapper.client.ClientMapper;
import cn.zswltech.mithras.customer.mapper.corp.CorpCommerceInfoMapper;
import cn.zswltech.mithras.customer.mapper.corp.CorpShareholderInfoMapper;
import cn.zswltech.mithras.customer.mapper.corp.NewCorpCommerceInfoMapper;
import cn.zswltech.mithras.customer.model.client.Client;
import cn.zswltech.mithras.customer.model.client.ClientBaseModel;
import cn.zswltech.mithras.customer.model.client.CorpCommerceInfo;
import cn.zswltech.mithras.customer.model.client.CorpShareholderInfo;
import cn.zswltech.mithras.customer.model.client.NewCorpCommerceInfo;
import cn.zswltech.mithras.dto.client.commerceinfo.ClientCorpCommerceInfoValidREQ;
import cn.zswltech.mithras.dto.client.commerceinfo.ClientCorpCommerceInfoValidRSP;
import cn.zswltech.mithras.dto.client.commerceinfo.CorpCommerceInfoAddREQ;
import cn.zswltech.mithras.dto.client.commerceinfo.CorpCommerceInfoModifyREQ;
import cn.zswltech.mithras.dto.contract.ContractCompareBusinessRSP;
import cn.zswltech.mithras.foundation.constant.ResultMsg;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import static cn.hutool.core.bean.BeanUtil.copyProperties;
import static cn.hutool.core.util.ObjectUtil.isEmpty;
import static cn.hutool.core.util.ObjectUtil.isNotEmpty;
import static cn.hutool.core.util.ObjectUtil.isNotNull;
import static cn.hutool.core.util.ObjectUtil.isNull;
import static cn.zswltech.mithras.foundation.constant.ResultMsg.CLIENT_COMMERCE_INFO_EXIST;
import static cn.zswltech.mithras.foundation.constant.ResultMsg.ONLY_CREATOR_MODIFY;
import static cn.zswltech.mithras.foundation.constant.ResultMsg.ORG_CODE_DUPLICATE;
import static cn.zswltech.mithras.foundation.constant.ResultMsg.ZZ_CODE_DUPLICATE;

/**
 * @author junke
 * 工商信息service
 */
@Slf4j
@Service
public class CorpCommerceInfoService extends ServiceImpl<CorpCommerceInfoMapper, CorpCommerceInfo> implements ClientDataSaveCheckInterface<NewCorpCommerceInfo>, ClientOldDataHelper<CorpCommerceInfo> {
    private static final String INTRA_GROUP_COLLABORATION = "INTRA_GROUP_COLLABORATION";
    private static final String COMMERCE_INFO_ZZC_FIELD = "zhong_zheng_code";
    private static final String COMMERCE_INFO_ORG_CODE_FIELD = "org_code";
    private static final String COMMERCE_INFO_CLIENT_ID_FIELD = "client_id";

    @Resource
    private CorpCommerceInfoMapper ccfMapper;
    @Resource
    private NewCorpCommerceInfoMapper newCcfMapper;
    @Resource
    private CorpShareholderInfoMapper corpShareholderInfoMapper;
    @Resource
    private ClientMapper clientMapper;
    @Resource
    private ClientAuthorityDataPort authorityUtil;
    @Resource
    private CorpCommerceInfoSupportPort supportPort;

    public Map<Long, List<CorpCommerceInfo>> getCorpCommerceInfoMap(Collection<Long> clientIds) {
        LambdaQueryWrapper<CorpCommerceInfo> query = Wrappers.lambdaQuery();
        query.in(CorpCommerceInfo::getClientId, clientIds);
        query.orderByDesc(ClientBaseModel::getId);
        return this.list(query).stream().collect(Collectors.groupingBy(ClientBaseModel::getClientId));
    }

    @Override
    public List<CorpCommerceInfo> findByClientId(Long clientId) {
        LambdaQueryWrapper<CorpCommerceInfo> query = Wrappers.lambdaQuery();
        query.eq(ClientBaseModel::getClientId, clientId);
        return this.list(query);
    }

    @Override
    public void removeByClientId(Long clientId) {
        LambdaQueryWrapper<CorpCommerceInfo> query = Wrappers.lambdaQuery();
        query.eq(ClientBaseModel::getClientId, clientId);
        this.remove(query);
    }

    @Transactional(rollbackFor = Exception.class)
    public void add(CorpCommerceInfoAddREQ req) {
        Long startUserId = Optional.ofNullable(AccountUtil.getLoginInfo())
                .map(AccountVO::getId)
                .orElseThrow(() -> new MithrasException(ResultMsg.USER_NOT_LOGIN));
        if (isNotNull(newCcfMapper.selectOne(
                Wrappers.<NewCorpCommerceInfo>lambdaQuery()
                        .eq(NewCorpCommerceInfo::getClientId, req.getClientId())
                        .eq(NewCorpCommerceInfo::getUserId, startUserId)
        ))) {
            throw new MithrasException(CLIENT_COMMERCE_INFO_EXIST);
        }
        Client existClient = clientMapper.selectById(req.getClientId());
        if (ObjectUtil.isNotEmpty(existClient.getBelongSponsorId()) && ObjectUtil.notEqual(existClient.getBelongSponsorId(), AccountUtil.getLoginInfo().getId())) {
            throw new MithrasException(ONLY_CREATOR_MODIFY);
        }

        //1`更新客户名称
        LambdaUpdateWrapper<Client> clientLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        clientLambdaUpdateWrapper.eq(Client::getId, req.getClientId());
        clientLambdaUpdateWrapper.set(Client::getClientName, req.getClientName());
        clientLambdaUpdateWrapper.set(Client::getClientCode, req.getClientCode());
        //2`保存工商信息
        NewCorpCommerceInfo newCorpCommerceInfo = copyProperties(req, NewCorpCommerceInfo.class);
//        setClientAuthority(newCorpCommerceInfo, existClient, startUserId);
        // 转一下 如果是集团公司，所属集团取自己
        if (Objects.equals(1, req.getGroupFlag())) {
            newCorpCommerceInfo.setBelongGroupClientId(existClient.getId());
        }
        check(newCorpCommerceInfo);
        //公海用户修改类型
        if (INTRA_GROUP_COLLABORATION.equals(newCorpCommerceInfo.getRiskControlIndustryClassify())) {
            clientLambdaUpdateWrapper.set(Client::getAuthType, ClientAuthEnum.HIGH_SEAS.name());
        } else {
            ClientAuthBO clientAuthByProj = supportPort.getClientAuthByProj(req.getClientId(), null);
            clientLambdaUpdateWrapper.set(Client::getAuthType, clientAuthByProj.getClientAuthEnum().name());
            if (ObjectUtil.isNotEmpty(clientAuthByProj.getProjEstablishId())) {
                clientLambdaUpdateWrapper.set(Client::getBelongSponsorId, clientAuthByProj.getProjEstablishId());
                clientLambdaUpdateWrapper.set(Client::getBelongDeptId, clientAuthByProj.getBizDeptId());
            }
        }
        clientLambdaUpdateWrapper.set(Client::getLatestUserId, startUserId);
        clientMapper.update(null, clientLambdaUpdateWrapper);
        try {
            newCcfMapper.insert(newCorpCommerceInfo);
        } catch (DuplicateKeyException e) {
            handlerDuplicateKey(e);
        }
        recordClientStatus(newCorpCommerceInfo);
        // 新表抄老表
        ClientCopyInfoBO clientCopyInfoBO = ClientCopyInfoBO.builder()
                .clientId(existClient.getId())
                .currentUserId(startUserId)
//                .moduleList(Collections.singletonList(InfoModule.CORP_COMMERCE))
                .build();
        authorityUtil.copyFromNewToOld(clientCopyInfoBO);
//        existClient.setLatestUserId(startUserId);
//        clientMapper.updateAnnotationIncludeNullById(existClient);
    }

    @Transactional(rollbackFor = Exception.class)
    public void modify(CorpCommerceInfoModifyREQ req) {
        checkOwnerShip(req);
        Client existClient = clientMapper.selectById(req.getClientId());
        /*if (ObjectUtil.notEqual(existClient.getBelongSponsorId(), AccountUtil.getLoginInfo().getId())) {
            throw new MithrasException(ONLY_CREATOR_MODIFY);
        }*/
        Long startUserId = Optional.ofNullable(AccountUtil.getLoginInfo())
                .map(AccountVO::getId)
                .orElseThrow(() -> new MithrasException(ResultMsg.USER_NOT_LOGIN));
        //1`更新客户名称
        LambdaUpdateWrapper<Client> clientLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        clientLambdaUpdateWrapper.eq(Client::getId, req.getClientId());
        clientLambdaUpdateWrapper.set(Client::getClientName, req.getClientName());
        clientLambdaUpdateWrapper.set(Client::getClientCode, req.getClientCode());
        clientLambdaUpdateWrapper.set(Client::getLatestUserId, startUserId);
        clientMapper.update(null, clientLambdaUpdateWrapper);
        //2`保存工商信息
        NewCorpCommerceInfo newCorpCommerceInfo = copyProperties(req, NewCorpCommerceInfo.class);
        // 转一下 如果是集团公司，所属集团取自己
        if (Objects.equals(1, req.getGroupFlag())) {
            newCorpCommerceInfo.setBelongGroupClientId(existClient.getId());
        }
        check(newCorpCommerceInfo);
        newCorpCommerceInfo.setUserId(startUserId);
        NewCorpCommerceInfo originalInfo = newCcfMapper.selectOne(Wrappers.<NewCorpCommerceInfo>lambdaQuery()
                .eq(NewCorpCommerceInfo::getClientId, req.getClientId())
                .eq(NewCorpCommerceInfo::getUserId, startUserId)
        );
        if (Objects.nonNull(originalInfo) && !Objects.equals(originalInfo.getUserId(), startUserId)) {
            throw new MithrasException("无权修改数据");
        }
        if (Objects.nonNull(originalInfo) && Objects.equals(existClient.getClientStatus(), ClientStatus.TAKE_EFFECT.name())) {
            // 生效客户不允许在公海和非公海之间来回转变，此处加限制
            // oldData - 公海（true），newData - 非公海（true） 需要阻断
            // oldData - 非公海（false），newData - 公海（false） 需要阻断
            // 可以统一成oldData和newData
            boolean oldData = Objects.equals(originalInfo.getRiskControlIndustryClassify(), INTRA_GROUP_COLLABORATION);
            boolean newData = !Objects.equals(newCorpCommerceInfo.getRiskControlIndustryClassify(), INTRA_GROUP_COLLABORATION);
            if (oldData == newData) {
                throw new MithrasException("生效客户不允许在公海与非公海间转变");
            }
        }
//        //公海用户修改类型 ->这里需要修改，只有INTRA_GROUP_COLLABORATION转其他才是公海-非公海
//        if (RiskControlIndustryClassify.INTRA_GROUP_COLLABORATION.name().equals(newCorpCommerceInfo.getRiskControlIndustryClassify()) || (ObjectUtil.isNotEmpty(originalInfo) && !RiskControlIndustryClassify.INTRA_GROUP_COLLABORATION.name().equals(originalInfo.getRiskControlIndustryClassify()) && ClientAuthEnum.HIGH_SEAS.name().equals(existClient.getAuthType()))) {
//            clientLambdaUpdateWrapper.set(Client::getAuthType, ClientAuthEnum.HIGH_SEAS.name());
//        } else {
//            ClientAuthBO clientAuthByProj = clientService.getClientAuthByProj(req.getClientId(), null);
//            if (ObjectUtil.isNotEmpty(clientAuthByProj.getProjEstablishId())) {
//                clientLambdaUpdateWrapper.set(Client::getBelongSponsorId, clientAuthByProj.getProjSponsorUserId());
//                clientLambdaUpdateWrapper.set(Client::getBelongDeptId, clientAuthByProj.getBizDeptId());
//                clientLambdaUpdateWrapper.set(Client::getAuthType, ClientAuthEnum.EXCLUSIVE.name());
//            } else {
//                clientLambdaUpdateWrapper.set(Client::getAuthType, ClientAuthEnum.NO_AFFILIATION.name());
//            }
//        }
//        clientLambdaUpdateWrapper.set(Client::getLatestUserId, startUserId);
//        clientMapper.update(null, clientLambdaUpdateWrapper);
        if (isNull(originalInfo)) {
            try {
                newCcfMapper.insert(newCorpCommerceInfo);
            } catch (DuplicateKeyException e) {
                handlerDuplicateKey(e);
            }
        } else {
            try {
                newCorpCommerceInfo.setId(originalInfo.getId());
                newCcfMapper.updateAnnotationIncludeNullById(newCorpCommerceInfo);
            } catch (DuplicateKeyException e) {
                handlerDuplicateKey(e);
            }
        }
        recordClientStatus(newCorpCommerceInfo);
        // 新表抄老表
        ClientCopyInfoBO clientCopyInfoBO = ClientCopyInfoBO.builder()
                .clientId(existClient.getId())
                .currentUserId(startUserId)
//                .moduleList(Collections.singletonList(InfoModule.CORP_COMMERCE))
                .build();
        authorityUtil.copyFromNewToOld(clientCopyInfoBO);
//        existClient.setLatestUserId(startUserId);
//        clientMapper.updateAnnotationIncludeNullById(existClient);
    }

    public static void checkOwnerShip(CorpCommerceInfoAddREQ req) {
        // 校验是否是上市公司控股
        if (CharSequenceUtil.isNotBlank(req.getEnterpriseNature())) {
            EnterpriseNatureEnum enterpriseNatureEnum = EnterpriseNatureEnum.of(req.getEnterpriseNature());
            if (Objects.isNull(enterpriseNatureEnum)) {
                throw new MithrasException("企业性质码值非法");
            }
            switch (enterpriseNatureEnum) {
                case gyfss:
                case myfss:
                case other:
                    if (Objects.isNull(req.getOwnershipType())){
                        throw new MithrasException("请选择控股类型");
                    }
                    OwnershipTypeEnum ownershipTypeEnum = OwnershipTypeEnum.ofName(req.getOwnershipType());
                    if (Objects.isNull(ownershipTypeEnum)){
                        throw new MithrasException("控股类型码值非法");
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private void handlerDuplicateKey(DuplicateKeyException e) {
        String key = extractKey(e);
        if (isNotNull(key)) {
            switch (key) {
                case COMMERCE_INFO_ZZC_FIELD: {
                    throw new MithrasException(ZZ_CODE_DUPLICATE);
                }
                case COMMERCE_INFO_ORG_CODE_FIELD: {
                    throw new MithrasException(ORG_CODE_DUPLICATE);
                }
                case COMMERCE_INFO_CLIENT_ID_FIELD: {
                    throw new MithrasException(CLIENT_COMMERCE_INFO_EXIST);
                }
                default: {
                    throw e;
                }
            }
        }
    }

    private String extractKey(DuplicateKeyException e) {
        try {
            String forKey = "for key '";
            String message = e.getMessage();
            int start = message.indexOf(forKey);
            message = message.substring(start + forKey.length());
            int end = message.indexOf("'");
            return message.substring(0, end);
        } catch (Exception exception) {
            log.error("", exception);
            return "";
        }
    }

    public CorpCommerceInfo detail(Long clientId, Long startUserId) {
        // 重要！！！该方法只能在有用户登陆会话的场景使用，后台任务等无状态方法调用会报错
        Client existClient = clientMapper.selectById(clientId);
        if (existClient == null) {
            throw new MithrasException("客户已不存在");
        }
        LambdaQueryWrapper<NewCorpCommerceInfo> query = Wrappers.lambdaQuery();
        query.eq(ClientBaseModel::getClientId, clientId);
        if (Objects.nonNull(startUserId)) {
            // 流程中就看流程发起人
            query.eq(NewCorpCommerceInfo::getUserId, startUserId);
        } else {
            Long userId = authorityUtil.ensureNoProcessViewWhichUserData(clientId);
            if (Objects.isNull(userId)) {
                // 说明没有可看的数据
                return new CorpCommerceInfo();
            } else {
                query.eq(NewCorpCommerceInfo::getUserId, userId);
            }
        }
        NewCorpCommerceInfo newCorpCommerceInfo = newCcfMapper.selectOne(query);
        if (isNull(newCorpCommerceInfo)) {
            log.warn("clientId为:[{}]的工商信息不存在", clientId);
            newCorpCommerceInfo = new NewCorpCommerceInfo();
        }
        if (isNotNull(existClient)) {
            newCorpCommerceInfo.setClientName(existClient.getClientName());
            newCorpCommerceInfo.setClientCode(existClient.getClientCode());
            newCorpCommerceInfo.setUscCode(existClient.getUscCode());
            newCorpCommerceInfo.setClientType(existClient.getClientType());
        }
        newCorpCommerceInfo.setBelongGroupClientName(queryBelongGroupClientName(newCorpCommerceInfo.getClientId(), newCorpCommerceInfo.getBelongGroupClientId(), newCorpCommerceInfo.getClientName()));
        CorpCommerceInfo info = BeanUtil.copyProperties(newCorpCommerceInfo, CorpCommerceInfo.class);
        return info;
    }


    public List<CorpCommerceInfo> list(Collection<Long> clientIds) {
        List<CorpCommerceInfo> infos = ccfMapper.selectList(Wrappers.<CorpCommerceInfo>lambdaQuery().in(CorpCommerceInfo::getClientId, clientIds));
        if (isNull(infos)) {
            return new ArrayList<>();
        }
        Map<Long, Client> id2ClientMap = clientMapper.selectBatchIds(clientIds).stream().collect(Collectors.toMap(Client::getId, e -> e,
                (a, b) -> a));
        if (isNotNull(infos)) {
            infos.forEach(info -> {
                Client client = id2ClientMap.getOrDefault(info.getClientId(), new Client());
                info.setClientName(client.getClientName());
                info.setClientCode(client.getClientCode());
                info.setUscCode(client.getUscCode());
                info.setClientType(client.getClientType());
            });
        }
        return infos;
    }

    /**
     * 计算所属集团名称
     *
     * @param belongGroupClientId 所属集团客户id
     * @param clientName          客户名称
     * @return
     */
    public String queryBelongGroupClientName(Long clientId, Long belongGroupClientId, String clientName) {
        if (Objects.isNull(belongGroupClientId)) {
            return null;
        } else if (Objects.equals(-1L, belongGroupClientId)) {
            return "无";
        } else if (belongGroupClientId.equals(clientId)) {
            return clientName;
        }
//        } else if (Objects.equals(-2L, belongGroupClientId)) {
//            return clientName;
//        }
        return Optional.ofNullable(clientMapper.selectById(belongGroupClientId)).map(Client::getClientName).orElse(null);
    }

    public Optional<Map<Long, String>> selectIndustryTypeBatchByIds(Collection<Long> ids) {
        if (isEmpty(ids)) {
            return Optional.empty();
        }
        Map<Long, String> industryTypes = null;
        try {
            industryTypes = ccfMapper.selectList(Wrappers.<CorpCommerceInfo>lambdaQuery()
                            .select(ClientBaseModel::getClientId, CorpCommerceInfo::getIndustryType)
                            .in(ClientBaseModel::getClientId, ids)).stream()
                    .filter(corpCommerceInfo -> isNotEmpty(corpCommerceInfo.getIndustryType()))
                    .collect(Collectors.toMap(CorpCommerceInfo::getClientId, CorpCommerceInfo::getIndustryType));
        } catch (Exception e) {
            log.error("selectIndustryTypeBatchByIds error", e);
        }
        return Optional.ofNullable(industryTypes);
    }

    public ClientCorpCommerceInfoValidRSP valid(ClientCorpCommerceInfoValidREQ req) {
        Client client = clientMapper.selectById(req.getClientId());
        CorpCommerceInfo corpCommerceInfo = ccfMapper.selectOne(Wrappers.<CorpCommerceInfo>lambdaQuery()
                .eq(CorpCommerceInfo::getClientId, req.getClientId()));
        List<CorpShareholderInfo> corpShareholderInfos = corpShareholderInfoMapper.selectList(Wrappers.<CorpShareholderInfo>lambdaQuery()
                .eq(CorpShareholderInfo::getClientId, req.getClientId()));
        // 下面的接口设置超时机制
        CompletableFuture<Map<Long, ClientBusinessHistoryBO>> mapCompletableFuture = CompletableFuture.supplyAsync(() -> supportPort.compareBusiness(Collections.singletonList(req.getClientId())));
        Map<Long, ClientBusinessHistoryBO> longClientBusinessHistoryBoMap;
        try {
            longClientBusinessHistoryBoMap = mapCompletableFuture.get(10, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException e) {
            log.error("客户：{}的工商信息查询天眼查失败", client.getClientName(), e);
            throw new RuntimeException(e);
        } catch (TimeoutException e) {
            log.error("客户：{}的工商信息查询天眼查超时", client.getClientName(), e);
            throw new MithrasException(String.format("查询客户：%s的工商信息超时", client.getClientName()));
        }
        ContractCompareBusinessRSP rsp = supportPort.buildContractCompareBusinessRsp(longClientBusinessHistoryBoMap.get(req.getClientId()), client, corpCommerceInfo,
                corpShareholderInfos, "承租人");
        return BeanUtil.copyProperties(rsp, ClientCorpCommerceInfoValidRSP.class);
    }

//    private void setClientAuthority(NewCorpCommerceInfo newCorpCommerceInfo, Client existClient, Long startUserId) {
//        ClientAuthority clientAuthority = clientAuthorityMapper.selectOne(Wrappers.<ClientAuthority>lambdaQuery()
//                .eq(ClientAuthority::getClientId, existClient.getId())
//                .eq(ClientAuthority::getUserId, startUserId)
//                .eq(ClientAuthority::getDeleted, 0));
//        newCorpCommerceInfo.setUserId(startUserId);
//        newCorpCommerceInfo.setClientStatus(existClient.getClientStatus());
//        newCorpCommerceInfo.setIsReleased(existClient.getIsReleased());
//        newCorpCommerceInfo.setLevel(clientAuthority != null ? clientAuthority.getLevel() : 0);
//    }

}
