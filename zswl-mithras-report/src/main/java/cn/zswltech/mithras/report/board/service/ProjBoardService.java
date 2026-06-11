package cn.zswltech.mithras.report.board.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.IdcardUtil;
import cn.zswltech.mithras.dto.projreview.price.ProjReviewAocPriceRSP;
import cn.zswltech.mithras.dto.projreview.price.ProjReviewFactoringPriceRSP;
import cn.zswltech.mithras.dto.projreview.price.ProjReviewLeasePriceRSP;
import cn.zswltech.mithras.dto.projreview.price.ProjReviewPriceDetailRSP;
import cn.zswltech.mithras.customer.enums.CorpAddressType;
import cn.zswltech.mithras.customer.enums.client.ClientType;
import cn.zswltech.mithras.payment.enums.PaymentWriteOffStatus;
import cn.zswltech.mithras.report.excel.exporter.BoardProjInfoExcelExporter;
import cn.zswltech.mithras.report.excel.model.BoardProjInfoExcelModel;
import cn.zswltech.mithras.basedata.mapper.AddressDictionaryMapper;
import cn.zswltech.mithras.contract.mapper.contract.ContractBaseInfoMapper;
import cn.zswltech.mithras.customer.mapper.corp.CorpAddressInfoMapper;
import cn.zswltech.mithras.basedata.mapper.model.AddressDictionary;
import cn.zswltech.mithras.customer.model.client.Client;
import cn.zswltech.mithras.customer.model.client.ClientBaseModel;
import cn.zswltech.mithras.customer.model.client.CorpAddressInfo;
import cn.zswltech.mithras.customer.model.client.NormalBaseInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.payment.mapper.model.PaymentBaseInfo;
import cn.zswltech.mithras.projectprocess.model.projestablish.ProjEstablishBaseInfo;
import cn.zswltech.mithras.projectprocess.model.projreview.ProjReviewBaseInfo;
import cn.zswltech.mithras.customer.mapper.normal.NormalBaseInfoMapper;
import cn.zswltech.mithras.payment.mapper.PaymentBaseInfoMapper;
import cn.zswltech.mithras.projectprocess.mapper.projreview.ProjReviewBaseInfoMapper;
import cn.zswltech.mithras.system.user.Id2NameService;
import cn.zswltech.mithras.application.orchestration.client.ClientService;
import cn.zswltech.mithras.application.orchestration.projectprocess.projestablish.ProjEstablishBaseInfoService;
import cn.zswltech.mithras.application.orchestration.projectprocess.projreview.ProjReviewPriceService;
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
    private ProjEstablishBaseInfoService projEstablishBaseInfoService;
    @Resource
    private Id2NameService id2NameService;
    @Resource
    private ClientService clientService;
    @Resource
    private PaymentBaseInfoMapper paymentBaseInfoMapper;
    @Resource
    private ContractBaseInfoMapper contractBaseInfoMapper;
    @Resource
    private ProjReviewBaseInfoMapper projReviewBaseInfoMapper;
    @Resource
    private ProjReviewPriceService projReviewPriceService;
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

        List<Client> clients = clientService.listByClientIds(clientIds);
        Map<Long, List<Client>> clientMap = clients.stream().collect(Collectors.groupingBy(Client::getId));

        List<Long> peIds = projReviewBaseInfos.stream().map(ProjReviewBaseInfo::getProjEstablishId).distinct().collect(Collectors.toList());
        List<ProjEstablishBaseInfo> projEstablishBaseInfos = projEstablishBaseInfoService.list(Wrappers.<ProjEstablishBaseInfo>lambdaQuery().in(ProjEstablishBaseInfo::getId, peIds));
        Map<Long, List<ProjEstablishBaseInfo>> projEstablishBaseInfoMap = projEstablishBaseInfos.stream().collect(Collectors.groupingBy(ProjEstablishBaseInfo::getId));

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

            ProjReviewPriceDetailRSP detail = projReviewPriceService.detail(projReviewBaseInfo.getId());
            if (detail.getLeasePriceDetailRSP() != null) {
                ProjReviewLeasePriceRSP leasePriceDetailRsp = detail.getLeasePriceDetailRSP();
                tmp.setApplyCreditAmount(leasePriceDetailRsp.getApplyCreditAmount() != null? BigDecimal.valueOf(leasePriceDetailRsp.getApplyCreditAmount()).divide(BigDecimal.valueOf(10000)).setScale(2, RoundingMode.HALF_UP).toString() : "0");
                tmp.setIrrPercent(leasePriceDetailRsp.getIrrPercent() != null ? BigDecimal.valueOf(leasePriceDetailRsp.getIrrPercent()).divide(BigDecimal.valueOf(10000)).setScale(2, RoundingMode.HALF_UP).toString()+"%" : "0.05%");
            }
            if (detail.getAocPriceDetailRSP() != null) {
                ProjReviewAocPriceRSP aocPriceDetailRsp = detail.getAocPriceDetailRSP();
                tmp.setApplyCreditAmount(aocPriceDetailRsp.getApplyCreditAmount() != null? BigDecimal.valueOf(aocPriceDetailRsp.getApplyCreditAmount()).divide(BigDecimal.valueOf(10000)).setScale(2, RoundingMode.HALF_UP).toString() : "0");
                tmp.setIrrPercent(aocPriceDetailRsp.getIrrPercent() != null ? BigDecimal.valueOf(aocPriceDetailRsp.getIrrPercent()).divide(BigDecimal.valueOf(10000)).setScale(2, RoundingMode.HALF_UP).toString()+"%" : "0.05%");
            }
            if (detail.getFactoringPriceDetailRSP() != null) {
                ProjReviewFactoringPriceRSP factoringPriceDetailRsp = detail.getFactoringPriceDetailRSP();
                tmp.setApplyCreditAmount(factoringPriceDetailRsp.getApplyCreditAmount() != null? BigDecimal.valueOf(factoringPriceDetailRsp.getApplyCreditAmount()).divide(BigDecimal.valueOf(10000)).setScale(2, RoundingMode.HALF_UP).toString() : "0");
                tmp.setIrrPercent(factoringPriceDetailRsp.getIrrPercent() != null ? BigDecimal.valueOf(factoringPriceDetailRsp.getIrrPercent()).divide(BigDecimal.valueOf(10000)).setScale(2, RoundingMode.HALF_UP).toString()+"%" : "0.05%");
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
