package cn.zswltech.mithras.service.service;

import cn.zswltech.mithras.service.mapper.model.BaseModel;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.model.PaymentBaseInfo;
import cn.zswltech.mithras.service.mapper.model.projestablish.ProjEstablishBaseInfo;
import cn.zswltech.mithras.service.mapper.model.projreview.ProjReviewBaseInfo;
import cn.zswltech.mithras.service.service.contract.ContractBaseInfoService;
import cn.zswltech.mithras.service.service.lib.projestablish.ProjEstablishBaseInfoLibService;
import cn.zswltech.mithras.service.service.lib.projreview.ProjReviewBaseInfoLibService;
import cn.zswltech.mithras.service.service.payment.PaymentBaseInfoService;
import cn.zswltech.mithras.service.service.projestablish.ProjEstablishBaseInfoService;
import cn.zswltech.mithras.service.service.projreview.ProjReviewBaseInfoService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/11/19 21:33
 */
@Service
public class DataCleanService {
    @Resource
    private ProjEstablishBaseInfoService establishBaseInfoService;
    @Resource
    private ProjEstablishBaseInfoLibService establishBaseInfoLibService;
    @Resource
    private ProjReviewBaseInfoService reviewBaseInfoService;
    @Resource
    private ProjReviewBaseInfoLibService reviewBaseInfoLibService;
    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private PaymentBaseInfoService paymentBaseInfoService;

    @Transactional(rollbackFor = Throwable.class)
    public void clean(){
        LocalDateTime line = LocalDateTime.of(2022,9, 10,0,0);
        List<Long> establishIds = establishBaseInfoService.getBaseMapper()
                .selectList(Wrappers.<ProjEstablishBaseInfo>lambdaQuery().le(BaseModel::getCreateTime, line))
                .stream().map(ProjEstablishBaseInfo::getId).collect(Collectors.toList());
        List<Long> reviewIds = reviewBaseInfoService.getBaseMapper()
                .selectList(Wrappers.<ProjReviewBaseInfo>lambdaQuery()
                        .in(ProjReviewBaseInfo::getProjEstablishId, establishIds))
                .stream().map(ProjReviewBaseInfo::getId).collect(Collectors.toList());
        List<Long> contractIds = contractBaseInfoService.getBaseMapper().selectList(Wrappers.<ContractBaseInfo>lambdaQuery()
                        .in(ContractBaseInfo::getProjReviewId, reviewIds)).stream()
                .map(ContractBaseInfo::getId).collect(Collectors.toList());
        List<Long> paymentIds = paymentBaseInfoService.getBaseMapper()
                .selectList(Wrappers.<PaymentBaseInfo>lambdaQuery().in(PaymentBaseInfo::getContractId, contractIds))
                .stream().map(PaymentBaseInfo::getId).collect(Collectors.toList());
        establishBaseInfoService.removeByIds(establishIds);
        reviewBaseInfoService.removeByIds(reviewIds);
        contractBaseInfoService.removeByIds(contractIds);
        paymentBaseInfoService.removeByIds(paymentIds);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void cleanContract(){
        LocalDateTime line = LocalDateTime.of(2022,9, 10,0,0);
        List<Long> contractIds = contractBaseInfoService.getBaseMapper().selectList(Wrappers.<ContractBaseInfo>lambdaQuery()
                        .le(ContractBaseInfo::getCreateTime, line)).stream()
                .map(ContractBaseInfo::getId).collect(Collectors.toList());
        List<Long> paymentIds = paymentBaseInfoService.getBaseMapper()
                .selectList(Wrappers.<PaymentBaseInfo>lambdaQuery().in(PaymentBaseInfo::getContractId, contractIds))
                .stream().map(PaymentBaseInfo::getId).collect(Collectors.toList());
        paymentBaseInfoService.removeByIds(paymentIds);
        contractBaseInfoService.removeByIds(contractIds);
    }
}
