package cn.zswltech.mithras.riskcontrol.relation;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.riskcontrol.RiskControlRelatedTransactionPageReq;
import cn.zswltech.mithras.dto.riskcontrol.RiskControlRelatedTransactionRsp;
import cn.zswltech.mithras.riskcontrol.application.port.RiskControlRelatedTransactionFact;
import cn.zswltech.mithras.riskcontrol.application.port.RiskControlRelatedTransactionPort;
import cn.zswltech.mithras.riskcontrol.application.port.RiskControlRelatedTransactionQuery;
import cn.zswltech.mithras.riskcontrol.excel.importer.RelatedClientImporter;
import cn.zswltech.mithras.riskcontrol.excel.model.RelatedClientExcelModel;
import cn.zswltech.mithras.riskcontrol.application.port.RiskControlClientFactPort;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
    private RiskControlRelatedClientConverter baseConverter;
    @Resource
    private RiskControlClientFactPort clientFactPort;
    @Resource
    private RiskControlRelatedTransactionPort relatedTransactionPort;

    private final Map<String, List<String>> pullDownMap = new java.util.HashMap<>();

    @Transactional(rollbackFor = Throwable.class)
    public void importFile(InputStream inputStream) {
        // 解析excel中的关联方名录
        List<RelatedClientExcelModel> relatedClients = relatedClientImporter.parse(inputStream);
        Map<String, RelatedClientExcelModel> uscdMap = relatedClients.parallelStream()
                .collect(Collectors.toMap(RelatedClientExcelModel::getUscd, item -> item, (k1, k2) -> k1));
        // 从客户管理模块查询出系统中存在的关联方
        Map<String, Long> sysClientIds = clientFactPort.activeClientIdsByCreditCodes(uscdMap.keySet());
        if(ObjectUtil.isEmpty(sysClientIds)){
            return ;
        }
        // 查询数据库中已经存在的关联方
        Map<String, RiskControlRelatedClient> dbRelatedClients = list().stream()
                .collect(Collectors.toMap(RiskControlRelatedClient::getUscd, item -> item, (k1, k2) -> k1));
        List<RiskControlRelatedClient> updateList = new ArrayList<>();
        // 以系统中存在的关联方为基准，判断数据库中是否存在，存在则更新，不存在则新增
        for (Map.Entry<String, Long> sysClient : sysClientIds.entrySet()) {
            String uscd = sysClient.getKey();
            RiskControlRelatedClient orDefault = dbRelatedClients.getOrDefault(uscd, new RiskControlRelatedClient());
            RelatedClientExcelModel excelModel = uscdMap.get(uscd);
            orDefault.setUscd(excelModel.getUscd());
            orDefault.setClientName(excelModel.getClientName());
            orDefault.setRelatedPartyType(excelModel.getRelatedPartyType());
            orDefault.setDescription(excelModel.getDescription());
            orDefault.setClientId(sysClient.getValue());
            orDefault.setParentRelationType(excelModel.getParentRelationType());
            orDefault.setSubRelationType(excelModel.getSubRelationType());
            updateList.add(orDefault);
            dbRelatedClients.remove(uscd);
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
        PageR<RiskControlRelatedTransactionFact> page = relatedTransactionPort.paymentTransactions(transactionQuery(req, clients.keySet()));
        if(ObjectUtil.isEmpty(page.getList())){
            return PageR.empty(req.getPage(), req.getPageSize());
        }
        return PageR.of(page, transactionRsp(clients, page.getList()));
    }

    public PageR<RiskControlRelatedTransactionRsp> collectionList(RiskControlRelatedTransactionPageReq req) {
        Map<Long, RiskControlRelatedClient> clients = getClients(req);
        if(ObjectUtil.isEmpty(clients)){
            return PageR.empty(req.getPage(), req.getPageSize());
        }
        PageR<RiskControlRelatedTransactionFact> page = relatedTransactionPort.collectionTransactions(transactionQuery(req, clients.keySet()));
        if(ObjectUtil.isEmpty(page.getList())){
            return PageR.empty(req.getPage(), req.getPageSize());
        }
        return PageR.of(page, transactionRsp(clients, page.getList()));
    }

    private List<RiskControlRelatedTransactionRsp> transactionRsp(Map<Long, RiskControlRelatedClient> clients,
                                                                  List<RiskControlRelatedTransactionFact> facts) {
        List<RiskControlRelatedTransactionRsp> listRsp = new ArrayList<>();
        for (RiskControlRelatedTransactionFact record : facts) {
            RiskControlRelatedTransactionRsp rsp = baseConverter.client2TransactionRsp(clients.get(record.getClientId()));
            rsp.setContractCode(record.getContractCode());
            rsp.setCashFlowCode(record.getCashFlowCode());
            rsp.setTransactionAmount(record.getTransactionAmount());
            rsp.setTransactionDate(record.getTransactionDate());
            listRsp.add(rsp);
        }
        return listRsp;
    }

    private RiskControlRelatedTransactionQuery transactionQuery(RiskControlRelatedTransactionPageReq req,
                                                                Set<Long> clientIds) {
        RiskControlRelatedTransactionQuery query = new RiskControlRelatedTransactionQuery();
        query.setClientIds(clientIds);
        query.setPage(req.getPage());
        query.setPageSize(req.getPageSize());
        query.setContractCode(req.getContractCode());
        query.setTransactionAmountFrom(req.getTransactionAmountFrom());
        query.setTransactionAmountTo(req.getTransactionAmountTo());
        query.setTransactionDateFrom(req.getTransactionDateFrom());
        query.setTransactionDateTo(req.getTransactionDateTo());
        return query;
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
