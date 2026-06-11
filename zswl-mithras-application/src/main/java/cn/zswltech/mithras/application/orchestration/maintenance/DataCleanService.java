package cn.zswltech.mithras.application.orchestration.maintenance;

import cn.zswltech.mithras.contract.mapper.contract.ContractBaseInfoMapper;
import cn.zswltech.mithras.foundation.persistence.model.BaseModel;
import cn.zswltech.mithras.contract.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.payment.mapper.PaymentBaseInfoMapper;
import cn.zswltech.mithras.payment.model.PaymentBaseInfo;
import cn.zswltech.mithras.projectprocess.mapper.projestablish.ProjEstablishBaseInfoMapper;
import cn.zswltech.mithras.projectprocess.model.projestablish.ProjEstablishBaseInfo;
import cn.zswltech.mithras.projectprocess.mapper.projreview.ProjReviewBaseInfoMapper;
import cn.zswltech.mithras.projectprocess.model.projreview.ProjReviewBaseInfo;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/11/19 21:33
 */
@Service
public class DataCleanService {
    @Resource
    private ProjEstablishBaseInfoMapper establishBaseInfoMapper;
    @Resource
    private ProjReviewBaseInfoMapper reviewBaseInfoMapper;
    @Resource
    private ContractBaseInfoMapper contractBaseInfoMapper;
    @Resource
    private PaymentBaseInfoMapper paymentBaseInfoMapper;

    @Transactional(rollbackFor = Throwable.class)
    public void clean(){
        LocalDateTime line = LocalDateTime.of(2022,9, 10,0,0);
        List<Long> establishIds = establishBaseInfoMapper.selectList(Wrappers.<ProjEstablishBaseInfo>lambdaQuery().le(BaseModel::getCreateTime, line))
                .stream().map(ProjEstablishBaseInfo::getId).collect(Collectors.toList());
        List<Long> reviewIds = reviewBaseInfoMapper.selectList(Wrappers.<ProjReviewBaseInfo>lambdaQuery()
                        .in(ProjReviewBaseInfo::getProjEstablishId, establishIds))
                .stream().map(ProjReviewBaseInfo::getId).collect(Collectors.toList());
        List<Long> contractIds = contractBaseInfoMapper.selectList(Wrappers.<ContractBaseInfo>lambdaQuery()
                        .in(ContractBaseInfo::getProjReviewId, reviewIds)).stream()
                .map(ContractBaseInfo::getId).collect(Collectors.toList());
        List<Long> paymentIds = paymentBaseInfoMapper.selectList(Wrappers.<PaymentBaseInfo>lambdaQuery().in(PaymentBaseInfo::getContractId, contractIds))
                .stream().map(PaymentBaseInfo::getId).collect(Collectors.toList());
        establishBaseInfoMapper.deleteBatchIds(establishIds);
        reviewBaseInfoMapper.deleteBatchIds(reviewIds);
        contractBaseInfoMapper.deleteBatchIds(contractIds);
        paymentBaseInfoMapper.deleteBatchIds(paymentIds);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void cleanContract(){
        LocalDateTime line = LocalDateTime.of(2022,9, 10,0,0);
        List<Long> contractIds = contractBaseInfoMapper.selectList(Wrappers.<ContractBaseInfo>lambdaQuery()
                        .le(ContractBaseInfo::getCreateTime, line)).stream()
                .map(ContractBaseInfo::getId).collect(Collectors.toList());
        List<Long> paymentIds = paymentBaseInfoMapper.selectList(Wrappers.<PaymentBaseInfo>lambdaQuery().in(PaymentBaseInfo::getContractId, contractIds))
                .stream().map(PaymentBaseInfo::getId).collect(Collectors.toList());
        paymentBaseInfoMapper.deleteBatchIds(paymentIds);
        contractBaseInfoMapper.deleteBatchIds(contractIds);
    }
}
