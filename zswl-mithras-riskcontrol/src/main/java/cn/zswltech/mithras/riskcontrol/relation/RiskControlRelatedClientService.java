package cn.zswltech.mithras.riskcontrol.relation;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.riskcontrol.RiskControlRelatedTransactionPageReq;
import cn.zswltech.mithras.dto.riskcontrol.RiskControlRelatedTransactionRsp;
import cn.zswltech.mithras.riskcontrol.relation.RiskControlRelatedClientApplicationService;
import cn.zswltech.mithras.riskcontrol.relation.RiskControlRelatedClientConverter;
import cn.zswltech.mithras.collection.enums.CollectionWriteOffStatusEnum;
import cn.zswltech.mithras.payment.enums.PaymentWriteOffStatus;
import cn.zswltech.mithras.riskcontrol.excel.importer.RelatedClientImporter;
import cn.zswltech.mithras.riskcontrol.excel.model.RelatedClientExcelModel;
import cn.zswltech.mithras.customer.enums.client.ClientStatus;
import cn.zswltech.mithras.customer.mapper.client.ClientMapper;
import cn.zswltech.mithras.customer.mapper.model.client.Client;
import cn.zswltech.mithras.collection.mapper.CollectionBaseInfoMapper;
import cn.zswltech.mithras.collection.mapper.model.CollectionBaseInfo;
import cn.zswltech.mithras.payment.mapper.PaymentBaseInfoMapper;
import cn.zswltech.mithras.payment.mapper.model.PaymentBaseInfo;
import cn.zswltech.mithras.riskcontrol.relation.RiskControlRelatedClient;
import cn.zswltech.mithras.riskcontrol.relation.RiskControlRelatedClientMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
* @description 金控关联方名录
* @author zhaozhengkang
* @date 2023-03-08
*/
@Service
public class RiskControlRelatedClientService
        extends ServiceImpl<RiskControlRelatedClientMapper, RiskControlRelatedClient>
        implements RiskControlRelatedClientApplicationService {
    @Resource
    private RelatedClientImporter relatedClientImporter;
    @Resource
    private ClientMapper clientMapper;
    @Resource
    private PaymentBaseInfoMapper paymentBaseInfoMapper;
    @Resource
    private CollectionBaseInfoMapper collectionBaseInfoMapper;
    @Resource
    private RiskControlRelatedClientConverter baseConverter;

    private final Map<String, List<String>> pullDownMap = new java.util.HashMap<>();

    @Transactional(rollbackFor = Throwable.class)
    public void importFile(InputStream inputStream) {
        // 解析excel中的关联方名录
        List<RelatedClientExcelModel> relatedClients = relatedClientImporter.parse(inputStream);
        Map<String, RelatedClientExcelModel> uscdMap = relatedClients.parallelStream()
                .collect(Collectors.toMap(RelatedClientExcelModel::getUscd, item -> item, (k1, k2) -> k1));
        // 从客户管理模块查询出系统中存在的关联方
        List<Client> sysClients = clientMapper.selectList(Wrappers.<Client>lambdaQuery()
                .eq(Client::getClientStatus, ClientStatus.TAKE_EFFECT.name())
                .in(Client::getUscCode, uscdMap.keySet()));
        if(ObjectUtil.isEmpty(sysClients)){
            return ;
        }
        // 查询数据库中已经存在的关联方
        Map<String, RiskControlRelatedClient> dbRelatedClients = list().stream()
                .collect(Collectors.toMap(RiskControlRelatedClient::getUscd, item -> item, (k1, k2) -> k1));
        List<RiskControlRelatedClient> updateList = new ArrayList<>();
        // 以系统中存在的关联方为基准，判断数据库中是否存在，存在则更新，不存在则新增
        for (Client sysClient : sysClients) {
            RiskControlRelatedClient orDefault = dbRelatedClients.getOrDefault(sysClient.getUscCode(), new RiskControlRelatedClient());
            RelatedClientExcelModel excelModel = uscdMap.get(sysClient.getUscCode());
            orDefault.setUscd(excelModel.getUscd());
            orDefault.setClientName(excelModel.getClientName());
            orDefault.setRelatedPartyType(excelModel.getRelatedPartyType());
            orDefault.setDescription(excelModel.getDescription());
            orDefault.setClientId(sysClient.getId());
            orDefault.setParentRelationType(excelModel.getParentRelationType());
            orDefault.setSubRelationType(excelModel.getSubRelationType());
            updateList.add(orDefault);
            dbRelatedClients.remove(sysClient.getUscCode());
        }
        List<String> deleteList = new ArrayList<>(dbRelatedClients.keySet());
        if(deleteList.size() > 0){
            remove(Wrappers.<RiskControlRelatedClient>lambdaQuery()
                    .in(RiskControlRelatedClient::getUscd, deleteList));
        }
        if(updateList.size() > 0){
            saveOrUpdateBatch(updateList);
        }
        pullDownMap.clear();
    }

    public PageR<RiskControlRelatedTransactionRsp> paymentList(RiskControlRelatedTransactionPageReq req) {
        Map<Long, RiskControlRelatedClient> clients = getClients(req);
        if(ObjectUtil.isEmpty(clients)){
            return PageR.empty(req.getPage(), req.getPageSize());
        }
        Page<PaymentBaseInfo> page = paymentBaseInfoMapper.selectPage(new Page<>(req.getPage(), req.getPageSize()),
                Wrappers.<PaymentBaseInfo>lambdaQuery()
                        .in(PaymentBaseInfo::getClientId, clients.keySet())
                        .in(PaymentBaseInfo::getWriteOffStatus,
                                Arrays.asList(PaymentWriteOffStatus.WRITTEN_OFF.name(),
                                        PaymentWriteOffStatus.PART_WRITTEN_OFF.name()))
                        .ge(ObjectUtil.isNotEmpty(req.getTransactionDateFrom()), PaymentBaseInfo::getPaidInDate,
                                req.getTransactionDateFrom())
                        .le(ObjectUtil.isNotEmpty(req.getTransactionDateTo()), PaymentBaseInfo::getPaidInDate,
                                req.getTransactionDateTo())
                        .like(ObjectUtil.isNotEmpty(req.getContractCode()), PaymentBaseInfo::getContractCode,
                                req.getContractCode())
                        .ge(ObjectUtil.isNotEmpty(req.getTransactionAmountFrom()),
                                PaymentBaseInfo::getApplyPaymentAmount, req.getTransactionAmountFrom())
                        .le(ObjectUtil.isNotEmpty(req.getTransactionAmountTo()), PaymentBaseInfo::getApplyPaymentAmount,
                                req.getTransactionAmountTo())
                        .orderByDesc(PaymentBaseInfo::getPaidInDate)
                        .orderByDesc(PaymentBaseInfo::getApplyPaymentDate));
        if(ObjectUtil.isEmpty(page.getRecords())){
            return PageR.empty(req.getPage(), req.getPageSize());
        }
        List<RiskControlRelatedTransactionRsp> listRsp = new ArrayList<>();
        for (PaymentBaseInfo record : page.getRecords()) {
            RiskControlRelatedTransactionRsp rsp = baseConverter.client2TransactionRsp(clients.get(record.getClientId()));
            rsp.setContractCode(record.getContractCode());
            rsp.setCashFlowCode(record.getPaymentCode());
            rsp.setTransactionAmount(record.getApplyPaymentAmount());
            rsp.setTransactionDate(record.getPaidInDate());
            listRsp.add(rsp);
        }
        return PageR.of(page, listRsp);
    }

    public PageR<RiskControlRelatedTransactionRsp> collectionList(RiskControlRelatedTransactionPageReq req) {
        Map<Long, RiskControlRelatedClient> clients = getClients(req);
        if(ObjectUtil.isEmpty(clients)){
            return PageR.empty(req.getPage(), req.getPageSize());
        }
        Page<CollectionBaseInfo> page = collectionBaseInfoMapper.selectPage(new Page<>(req.getPage(), req.getPageSize()),
                Wrappers.<CollectionBaseInfo>lambdaQuery()
                        .in(CollectionBaseInfo::getClientId, clients.keySet())
                        .in(CollectionBaseInfo::getWriteOffStatus,
                                Arrays.asList(CollectionWriteOffStatusEnum.WRITE_OFF_COMPLETED.name(),
                                        CollectionWriteOffStatusEnum.PORTION_WRITTEN_OFF.name()))
                        .ge(ObjectUtil.isNotEmpty(req.getTransactionDateFrom()), CollectionBaseInfo::getCollectionDate,
                                req.getTransactionDateFrom())
                        .le(ObjectUtil.isNotEmpty(req.getTransactionDateTo()), CollectionBaseInfo::getCollectionDate,
                                req.getTransactionDateTo())
                        .like(ObjectUtil.isNotEmpty(req.getContractCode()), CollectionBaseInfo::getContractCode,
                                req.getContractCode())
                        .ge(ObjectUtil.isNotEmpty(req.getTransactionAmountFrom()),
                                CollectionBaseInfo::getCollectionAmount, req.getTransactionAmountFrom())
                        .le(ObjectUtil.isNotEmpty(req.getTransactionAmountTo()),
                                CollectionBaseInfo::getCollectionAmount, req.getTransactionAmountTo())
                        .orderByDesc(CollectionBaseInfo::getCollectionDate));
        if(ObjectUtil.isEmpty(page.getRecords())){
            return PageR.empty(req.getPage(), req.getPageSize());
        }
        List<RiskControlRelatedTransactionRsp> listRsp = new ArrayList<>();
        for (CollectionBaseInfo record : page.getRecords()) {
            RiskControlRelatedTransactionRsp rsp = baseConverter.client2TransactionRsp(clients.get(record.getClientId()));
            rsp.setContractCode(record.getContractCode());
            rsp.setCashFlowCode(record.getCode());
            rsp.setTransactionAmount(record.getCollectionAmount());
            rsp.setTransactionDate(record.getCollectionDate());
            listRsp.add(rsp);
        }
        return PageR.of(page, listRsp);
    }

    private Map<Long, RiskControlRelatedClient> getClients(RiskControlRelatedTransactionPageReq req){
        return baseMapper.selectList(Wrappers.<RiskControlRelatedClient>lambdaQuery()
                        .like(ObjectUtil.isNotEmpty(req.getClientName()), RiskControlRelatedClient::getClientName,
                                req.getClientName())
                        .eq(ObjectUtil.isNotEmpty(req.getRelatedPartyType()), RiskControlRelatedClient::getRelatedPartyType,
                                req.getRelatedPartyType()))
                .stream().collect(Collectors.toMap(RiskControlRelatedClient::getClientId, item -> item, (k1, k2) -> k1));
    }

    public Map<String, List<String>> pullDown() {
        if (ObjectUtil.isNotEmpty(pullDownMap)) {
            return pullDownMap;
        }
        List<RiskControlRelatedClient> listAll = this.list();
        java.util.Set<String> relatedPartyType = listAll.stream().map(RiskControlRelatedClient::getRelatedPartyType)
                .collect(Collectors.toSet());
        java.util.Set<String> parentRelationType = listAll.stream().map(RiskControlRelatedClient::getParentRelationType)
                .collect(Collectors.toSet());
        java.util.Set<String> subRelationType = listAll.stream().map(RiskControlRelatedClient::getSubRelationType)
                .collect(Collectors.toSet());
        pullDownMap.put("relatedPartyType", new ArrayList<>(relatedPartyType));
        pullDownMap.put("parentRelationType", new ArrayList<>(parentRelationType));
        pullDownMap.put("subRelationType", new ArrayList<>(subRelationType));
        return pullDownMap;
    }
}
