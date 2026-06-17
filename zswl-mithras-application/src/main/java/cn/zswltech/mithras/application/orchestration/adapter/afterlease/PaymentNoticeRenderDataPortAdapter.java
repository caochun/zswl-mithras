package cn.zswltech.mithras.application.orchestration.adapter.afterlease;

import cn.zswltech.gruul.biz.service.UserService;
import cn.zswltech.gruul.dao.dal.vo.UserVO;
import cn.zswltech.mithras.afterlease.application.PaymentNoticeRenderData;
import cn.zswltech.mithras.afterlease.application.PaymentNoticeRenderDataPort;
import cn.zswltech.mithras.basedata.persistence.mapper.BaseDataBankAccountMapper;
import cn.zswltech.mithras.basedata.persistence.model.BaseDataBankAccount;
import cn.zswltech.mithras.collection.mapper.CollectionBaseInfoMapper;
import cn.zswltech.mithras.collection.model.CollectionBaseInfo;
import cn.zswltech.mithras.contract.model.contract.ContractBaseInfoLib;
import cn.zswltech.mithras.contract.versioning.handler.impl.ContractBaseInfoLibHandler;
import cn.zswltech.mithras.customer.mapper.client.ClientMapper;
import cn.zswltech.mithras.customer.model.client.Client;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;
import java.util.Optional;

@Component
public class PaymentNoticeRenderDataPortAdapter implements PaymentNoticeRenderDataPort {
    @Resource
    private CollectionBaseInfoMapper collectionBaseInfoMapper;
    @Resource
    private ContractBaseInfoLibHandler contractBaseInfoLibHandler;
    @Resource
    private ClientMapper clientMapper;
    @Resource(name = "userServiceAPI")
    private UserService userServiceAPI;
    @Resource
    private BaseDataBankAccountMapper baseDataBankAccountMapper;

    @Override
    public PaymentNoticeRenderData load(Long collectionId, Long bankId) {
        CollectionBaseInfo collectionBaseInfo = collectionBaseInfoMapper.selectById(collectionId);
        if (Objects.isNull(collectionBaseInfo)) {
            throw new MithrasException("收款信息不存在");
        }
        Client client = clientMapper.selectById(collectionBaseInfo.getClientId());
        ContractBaseInfoLib contractBaseInfoLib = contractBaseInfoLibHandler.queryLatestDataByOriginId(collectionBaseInfo.getContractId());
        UserVO userVO = userServiceAPI.getUserInfoById(contractBaseInfoLib.getProjSponsorUserId()).getData();
        String sponsorPhone = userServiceAPI.getRealPhone(contractBaseInfoLib.getProjSponsorUserId());
        BaseDataBankAccount account = Optional.ofNullable(bankId).map(baseDataBankAccountMapper::selectById).orElse(new BaseDataBankAccount());
        return PaymentNoticeRenderData.builder()
                .clientName(client.getClientName())
                .contractCode(collectionBaseInfo.getContractCode())
                .phase(collectionBaseInfo.getPhase())
                .planCollectionDate(collectionBaseInfo.getPlanCollectionDate())
                .planCollectionAmount(collectionBaseInfo.getPlanCollectionAmount())
                .principal(collectionBaseInfo.getPrincipal())
                .interest(collectionBaseInfo.getInterest())
                .accountName(account.getAccountName())
                .accountBank(account.getAccountBank())
                .accountNumber(account.getAccountNumber())
                .sponsorUserName(userVO.getUserName())
                .sponsorTelephone(sponsorPhone)
                .build();
    }
}
