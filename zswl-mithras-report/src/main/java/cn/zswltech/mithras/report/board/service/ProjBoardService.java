package cn.zswltech.mithras.report.board.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.IdcardUtil;
import cn.zswltech.mithras.customer.enums.CorpAddressType;
import cn.zswltech.mithras.customer.enums.client.ClientType;
import cn.zswltech.mithras.payment.enums.PaymentWriteOffStatus;
import cn.zswltech.mithras.report.excel.exporter.BoardProjInfoExcelExporter;
import cn.zswltech.mithras.report.excel.model.BoardProjInfoExcelModel;
import cn.zswltech.mithras.basedata.persistence.mapper.AddressDictionaryMapper;
import cn.zswltech.mithras.contract.mapper.contract.ContractBaseInfoMapper;
import cn.zswltech.mithras.customer.mapper.client.ClientMapper;
import cn.zswltech.mithras.customer.mapper.corp.CorpAddressInfoMapper;
import cn.zswltech.mithras.basedata.persistence.model.AddressDictionary;
import cn.zswltech.mithras.customer.model.client.Client;
import cn.zswltech.mithras.customer.model.client.ClientBaseModel;
import cn.zswltech.mithras.customer.model.client.CorpAddressInfo;
import cn.zswltech.mithras.customer.model.client.NormalBaseInfo;
import cn.zswltech.mithras.contract.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.payment.model.PaymentBaseInfo;
import cn.zswltech.mithras.projectprocess.model.projestablish.ProjEstablishBaseInfo;
import cn.zswltech.mithras.projectprocess.model.projreview.ProjReviewAocPrice;
import cn.zswltech.mithras.projectprocess.model.projreview.ProjReviewBaseInfo;
import cn.zswltech.mithras.projectprocess.model.projreview.ProjReviewFactoringPrice;
import cn.zswltech.mithras.projectprocess.model.projreview.ProjReviewLeasePrice;
import cn.zswltech.mithras.customer.mapper.normal.NormalBaseInfoMapper;
import cn.zswltech.mithras.payment.mapper.PaymentBaseInfoMapper;
import cn.zswltech.mithras.projectprocess.mapper.projestablish.ProjEstablishBaseInfoMapper;
import cn.zswltech.mithras.projectprocess.mapper.projreview.ProjReviewAocPriceMapper;
import cn.zswltech.mithras.projectprocess.mapper.projreview.ProjReviewBaseInfoMapper;
import cn.zswltech.mithras.projectprocess.mapper.projreview.ProjReviewFactoringPriceMapper;
import cn.zswltech.mithras.projectprocess.mapper.projreview.ProjReviewLeasePriceMapper;
import cn.zswltech.mithras.system.user.Id2NameService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @create: 2022-12-15
 **/

@Service
public class ProjBoardService {
    @Resource
    private Id2NameService id2NameService;
    @Resource
    private ClientMapper clientMapper;
    @Resource
    private PaymentBaseInfoMapper paymentBaseInfoMapper;
    @Resource
    private ContractBaseInfoMapper contractBaseInfoMapper;
    @Resource
    private ProjReviewBaseInfoMapper projReviewBaseInfoMapper;
    @Resource
    private ProjEstablishBaseInfoMapper projEstablishBaseInfoMapper;
    @Resource
    private ProjReviewLeasePriceMapper projReviewLeasePriceMapper;
    @Resource
    private ProjReviewAocPriceMapper projReviewAocPriceMapper;
    @Resource
    private ProjReviewFactoringPriceMapper projReviewFactoringPriceMapper;
    @Resource
    private CorpAddressInfoMapper corpAddressInfoMapper;
    @Resource
    private NormalBaseInfoMapper normalBaseInfoMapper;
    @Resource
    private AddressDictionaryMapper addressDictionaryMapper;
    @Resource
    private BoardProjInfoExcelExporter boardProjInfoExcelExporter;

    public void boardProjInfoExport(ServletOutputStream outputStream){
        //只统计已经放款的项目
        List<PaymentBaseInfo> paymentBaseInfos = paymentBaseInfoMapper.selectList(Wrappers.<PaymentBaseInfo>lambdaQuery().eq(PaymentBaseInfo::getWriteOffStatus, PaymentWriteOffStatus.WRITTEN_OFF.name()));
        if (CollectionUtil.isEmpty(paymentBaseInfos)){
            return ;
        }

        Map<Long, List<PaymentBaseInfo>> cpMap = paymentBaseInfos.stream().collect(Collectors.groupingBy(PaymentBaseInfo::getContractId));
        List<Long> cids = paymentBaseInfos.stream().map(PaymentBaseInfo::getContractId).distinct().collect(Collectors.toList());
        List<ContractBaseInfo> contractInfos = contractBaseInfoMapper.selectList(Wrappers.<ContractBaseInfo>lambdaQuery().in(ContractBaseInfo::getId,cids));
        Map<Long, List<ContractBaseInfo>> pcMap = contractInfos.stream().collect(Collectors.groupingBy(ContractBaseInfo::getProjReviewId));
        List<Long> rids = contractInfos.stream().map(ContractBaseInfo::getProjReviewId).distinct().collect(Collectors.toList());
        List<ProjReviewBaseInfo> projReviewBaseInfos = projReviewBaseInfoMapper.selectList(Wrappers.<ProjReviewBaseInfo>lambdaQuery().in(ProjReviewBaseInfo::getId, rids));

        Set<Long> clientIds = projReviewBaseInfos.stream().map(ProjReviewBaseInfo::getClientId).collect(Collectors.toSet());
        Map<Long, String> clientNameMap = id2NameService.clientId2Name(clientIds);
        Set<Long> deptIds = projReviewBaseInfos.stream().map(ProjReviewBaseInfo::getBizDeptId).collect(Collectors.toSet());
        Map<Long, String> deptMap = id2NameService.deptId2Name(deptIds);

        List<Client> clients = clientMapper.selectBatchIds(clientIds);
        Map<Long, List<Client>> clientMap = clients.stream().collect(Collectors.groupingBy(Client::getId));

        List<Long> peIds = projReviewBaseInfos.stream().map(ProjReviewBaseInfo::getProjEstablishId).distinct().collect(Collectors.toList());
        List<ProjEstablishBaseInfo> projEstablishBaseInfos = projEstablishBaseInfoMapper.selectList(Wrappers.<ProjEstablishBaseInfo>lambdaQuery().in(ProjEstablishBaseInfo::getId, peIds));
        Map<Long, List<ProjEstablishBaseInfo>> projEstablishBaseInfoMap = projEstablishBaseInfos.stream().collect(Collectors.groupingBy(ProjEstablishBaseInfo::getId));
        Map<Long, ProjReviewLeasePrice> leasePriceMap = projReviewLeasePriceMapper.selectList(Wrappers.<ProjReviewLeasePrice>lambdaQuery().in(ProjReviewLeasePrice::getProjectId, rids))
                .stream().collect(Collectors.toMap(ProjReviewLeasePrice::getProjectId, e -> e, (a, b) -> a));
        Map<Long, ProjReviewAocPrice> aocPriceMap = projReviewAocPriceMapper.selectList(Wrappers.<ProjReviewAocPrice>lambdaQuery().in(ProjReviewAocPrice::getProjectId, rids))
                .stream().collect(Collectors.toMap(ProjReviewAocPrice::getProjectId, e -> e, (a, b) -> a));
        Map<Long, ProjReviewFactoringPrice> factoringPriceMap = projReviewFactoringPriceMapper.selectList(Wrappers.<ProjReviewFactoringPrice>lambdaQuery().in(ProjReviewFactoringPrice::getProjectId, rids))
                .stream().collect(Collectors.toMap(ProjReviewFactoringPrice::getProjectId, e -> e, (a, b) -> a));

        //所有法人，自然人 地址信息
        List<CorpAddressInfo> corpAddressInfos = corpAddressInfoMapper.selectList(Wrappers.<CorpAddressInfo>lambdaQuery().in(CorpAddressInfo::getClientId, clientIds));
        Map<Long, List<CorpAddressInfo>> corpAddressMap = corpAddressInfos.stream().collect(Collectors.groupingBy(ClientBaseModel::getClientId));
        List<NormalBaseInfo> normalBaseInfos = normalBaseInfoMapper.selectList(Wrappers.<NormalBaseInfo>lambdaQuery().in(NormalBaseInfo::getClientId, clientIds));
        Map<Long, List<NormalBaseInfo>> normalMap = normalBaseInfos.stream().collect(Collectors.groupingBy(ClientBaseModel::getClientId));


        List<BoardProjInfoExcelModel> dataList = new ArrayList<>();
        Set<String> codeSet = new HashSet<>();
        for (ProjReviewBaseInfo projReviewBaseInfo : projReviewBaseInfos){
            BoardProjInfoExcelModel tmp = new BoardProjInfoExcelModel();
            tmp.setProjName(projReviewBaseInfo.getProjName());
            tmp.setClientName(clientNameMap.get(projReviewBaseInfo.getClientId()));
            tmp.setDept(deptMap.get(projReviewBaseInfo.getBizDeptId()));
            Client client = clientMap.get(projReviewBaseInfo.getClientId()).get(0);
            if (client.getClientType().equals(ClientType.CORPORATION.name())){
                List<CorpAddressInfo> corpAddress = corpAddressMap.get(projReviewBaseInfo.getClientId());
                Map<String, List<CorpAddressInfo>> typeMap = corpAddress.stream().collect(Collectors.groupingBy(CorpAddressInfo::getAddressType));
                List<CorpAddressInfo> registryAddress = typeMap.get(CorpAddressType.REGISTRY_ADDRESS.name());
                if (registryAddress.get(0).getProvince() != null) {
                    codeSet.add(registryAddress.get(0).getProvince());
                    tmp.setProvince(registryAddress.get(0).getProvince());
                }else {
                    List<CorpAddressInfo> workAddress = typeMap.get(CorpAddressType.WORK_ADDRESS.name());
                    if (workAddress.get(0).getProvince() != null) {
                        codeSet.add(workAddress.get(0).getProvince());
                        tmp.setProvince(workAddress.get(0).getProvince());
                    }
                }
            }else {
                NormalBaseInfo info = normalMap.get(projReviewBaseInfo.getClientId()).get(0);
                tmp.setProvince(IdcardUtil.getProvinceByIdCard(info.getCertNumber()));
            }

            ProjEstablishBaseInfo projEstablishBaseInfo = projEstablishBaseInfoMap.get(projReviewBaseInfo.getProjEstablishId()).get(0);
            tmp.setProjestablishTime(projEstablishBaseInfo.getCreateTime().toLocalDate().toString());

            ProjReviewLeasePrice leasePrice = leasePriceMap.get(projReviewBaseInfo.getId());
            if (leasePrice != null) {
                tmp.setApplyCreditAmount(leasePrice.getApplyCreditAmount() != null? BigDecimal.valueOf(leasePrice.getApplyCreditAmount()).divide(BigDecimal.valueOf(10000)).setScale(2, RoundingMode.HALF_UP).toString() : "0");
                tmp.setIrrPercent(leasePrice.getIrrPercent() != null ? BigDecimal.valueOf(leasePrice.getIrrPercent()).divide(BigDecimal.valueOf(10000)).setScale(2, RoundingMode.HALF_UP).toString()+"%" : "0.05%");
            }
            ProjReviewAocPrice aocPrice = aocPriceMap.get(projReviewBaseInfo.getId());
            if (aocPrice != null) {
                tmp.setApplyCreditAmount(aocPrice.getApplyCreditAmount() != null? BigDecimal.valueOf(aocPrice.getApplyCreditAmount()).divide(BigDecimal.valueOf(10000)).setScale(2, RoundingMode.HALF_UP).toString() : "0");
                tmp.setIrrPercent(aocPrice.getIrrPercent() != null ? BigDecimal.valueOf(aocPrice.getIrrPercent()).divide(BigDecimal.valueOf(10000)).setScale(2, RoundingMode.HALF_UP).toString()+"%" : "0.05%");
            }
            ProjReviewFactoringPrice factoringPrice = factoringPriceMap.get(projReviewBaseInfo.getId());
            if (factoringPrice != null) {
                tmp.setApplyCreditAmount(factoringPrice.getApplyCreditAmount() != null? BigDecimal.valueOf(factoringPrice.getApplyCreditAmount()).divide(BigDecimal.valueOf(10000)).setScale(2, RoundingMode.HALF_UP).toString() : "0");
                tmp.setIrrPercent(factoringPrice.getIrrPercent() != null ? BigDecimal.valueOf(factoringPrice.getIrrPercent()).divide(BigDecimal.valueOf(10000)).setScale(2, RoundingMode.HALF_UP).toString()+"%" : "0.05%");
            }

            List<ContractBaseInfo> infos = pcMap.get(projReviewBaseInfo.getId());
            long sum = 0L;
            for (ContractBaseInfo contractBaseInfo :infos){
                List<PaymentBaseInfo> paymentBaseInfos1 = cpMap.get(contractBaseInfo.getId());
                sum += paymentBaseInfos1.stream().mapToLong(PaymentBaseInfo::getApplyPaymentAmount).sum();
            }
            tmp.setApplyPaymentAmount(BigDecimal.valueOf(sum).divide(BigDecimal.valueOf(10000)).setScale(2, RoundingMode.HALF_UP).toString());
            dataList.add(tmp);
        }
        Map<String, String> nameMap = addressDictionaryMapper.selectList(Wrappers.<AddressDictionary>lambdaQuery().in(AddressDictionary::getCode, codeSet))
                .stream().collect(Collectors.toMap(AddressDictionary::getCode, AddressDictionary::getDisplay));
        dataList.forEach(boardProjInfoExcelModel -> boardProjInfoExcelModel.setProvince(Optional.ofNullable(nameMap.get(boardProjInfoExcelModel.getProvince())).orElse("")));
        boardProjInfoExcelExporter.exportExcel(dataList,outputStream);
    }
}
