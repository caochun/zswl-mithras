package cn.zswltech.mithras.service.service.riskcontrol;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.riskcontrol.RiskControlRelatedTransactionPageReq;
import cn.zswltech.mithras.dto.riskcontrol.RiskControlRelatedTransactionRsp;
import cn.zswltech.mithras.riskcontrol.relation.RiskControlRelatedClientConverter;
import cn.zswltech.mithras.service.enums.collection.CollectionWriteOffStatusEnum;
import cn.zswltech.mithras.service.enums.payment.PaymentWriteOffStatus;
import cn.zswltech.mithras.service.excel.importer.RelatedClientImporter;
import cn.zswltech.mithras.service.excel.model.RelatedClientExcelModel;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.Client;
import cn.zswltech.mithras.service.mapper.model.collection.CollectionBaseInfo;
import cn.zswltech.mithras.service.mapper.model.payment.PaymentBaseInfo;
import cn.zswltech.mithras.riskcontrol.relation.RiskControlRelatedClient;
import cn.zswltech.mithras.riskcontrol.relation.RiskControlRelatedClientMapper;
import cn.zswltech.mithras.service.service.client.ClientService;
import cn.zswltech.mithras.service.service.collection.CollectionBaseInfoService;
import cn.zswltech.mithras.service.service.payment.PaymentBaseInfoService;
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
        extends ServiceImpl<RiskControlRelatedClientMapper, RiskControlRelatedClient> {
    @Resource
    private RelatedClientImporter relatedClientImporter;
    @Resource
    private ClientService clientService;
    @Resource
    private PaymentBaseInfoService paymentBaseInfoService;
    @Resource
    private CollectionBaseInfoService collectionBaseInfoService;
    @Resource
    private RiskControlRelatedClientConverter baseConverter;

    @Transactional(rollbackFor = Throwable.class)
    public void importFile(InputStream inputStream) {
        // 解析excel中的关联方名录
        List<RelatedClientExcelModel> relatedClients = relatedClientImporter.parse(inputStream);
        Map<String, RelatedClientExcelModel> uscdMap = relatedClients.parallelStream()
                .collect(Collectors.toMap(RelatedClientExcelModel::getUscd, item -> item, (k1, k2) -> k1));
        // 从客户管理模块查询出系统中存在的关联方
        List<Client> sysClients = clientService.listRelatedClient(uscdMap.keySet());
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
    }

    public PageR<RiskControlRelatedTransactionRsp> paymentList(RiskControlRelatedTransactionPageReq req) {
        Map<Long, RiskControlRelatedClient> clients = getClients(req);
        if(ObjectUtil.isEmpty(clients)){
            return PageR.empty(req.getPage(), req.getPageSize());
        }
        Page<PaymentBaseInfo> page = paymentBaseInfoService.page(new Page<>(req.getPage(), req.getPageSize()),
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
        Page<CollectionBaseInfo> page = collectionBaseInfoService.page(new Page<>(req.getPage(), req.getPageSize()),
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
}