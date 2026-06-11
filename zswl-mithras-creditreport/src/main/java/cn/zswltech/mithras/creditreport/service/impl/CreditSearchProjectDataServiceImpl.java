package cn.zswltech.mithras.creditreport.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.zswltech.mithras.contract.mapper.contract.ContractBaseInfoMapper;
import cn.zswltech.mithras.contract.mapper.contract.ContractTradeStructureMapper;
import cn.zswltech.mithras.contract.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.contract.model.contract.ContractTradeStructure;
import cn.zswltech.mithras.credit.groupcredit.establish.mapper.GroupCreditEstablishBaseInfoMapper;
import cn.zswltech.mithras.credit.groupcredit.establish.model.GroupCreditEstablishBaseInfo;
import cn.zswltech.mithras.credit.groupcredit.review.mapper.GroupCreditReviewBaseInfoMapper;
import cn.zswltech.mithras.credit.groupcredit.review.model.GroupCreditReviewBaseInfo;
import cn.zswltech.mithras.creditreport.service.CreditSearchProjectDataService;
import cn.zswltech.mithras.customer.enums.client.ClientType;
import cn.zswltech.mithras.customer.mapper.client.ClientMapper;
import cn.zswltech.mithras.customer.mapper.corp.CorpCommerceInfoMapper;
import cn.zswltech.mithras.customer.model.client.Client;
import cn.zswltech.mithras.customer.model.client.CorpCommerceInfo;
import cn.zswltech.mithras.dto.creditreport.CreditReportClientInfo;
import cn.zswltech.mithras.dto.creditreport.CreditReportProjectReviewAddDTO;
import cn.zswltech.mithras.payment.mapper.PaymentBaseInfoMapper;
import cn.zswltech.mithras.payment.mapper.model.PaymentBaseInfo;
import cn.zswltech.mithras.projectprocess.mapper.projestablish.ProjEstablishBaseInfoMapper;
import cn.zswltech.mithras.projectprocess.mapper.projestablish.ProjEstablishTradeStructureMapper;
import cn.zswltech.mithras.projectprocess.model.projestablish.ProjEstablishBaseInfo;
import cn.zswltech.mithras.projectprocess.model.projestablish.ProjEstablishTradeStructure;
import cn.zswltech.mithras.projectprocess.model.projreview.ProjReviewBaseInfo;
import cn.zswltech.mithras.projectprocess.model.projreview.ProjReviewTradeStructure;
import cn.zswltech.mithras.projectprocess.mapper.projreview.ProjReviewBaseInfoMapper;
import cn.zswltech.mithras.projectprocess.mapper.projreview.ProjReviewTradeStructureMapper;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CreditSearchProjectDataServiceImpl implements CreditSearchProjectDataService {

    private static final String PROJ_ESTABLISH = "PROJ_ESTABLISH";
    private static final String PROJ_REVIEW = "PROJ_REVIEW";
    private static final String GROUP_CREDIT_ESTABLISH = "GROUP_CREDIT_ESTABLISH";
    private static final String GROUP_CREDIT_REVIEW = "GROUP_CREDIT_REVIEW";
    private static final String PAYMENT = "PAYMENT";

    @Resource
    private ClientMapper clientMapper;
    @Resource
    private CorpCommerceInfoMapper corpCommerceInfoMapper;
    @Resource
    private ProjEstablishTradeStructureMapper projEstablishTradeStructureMapper;
    @Resource
    private ProjEstablishBaseInfoMapper projEstablishBaseInfoMapper;
    @Resource
    private ProjReviewTradeStructureMapper projReviewTradeStructureMapper;
    @Resource
    private ProjReviewBaseInfoMapper projReviewBaseInfoMapper;
    @Resource
    private GroupCreditEstablishBaseInfoMapper groupCreditEstablishBaseInfoMapper;
    @Resource
    private GroupCreditReviewBaseInfoMapper groupCreditReviewBaseInfoMapper;
    @Resource
    private ContractTradeStructureMapper contractTradeStructureMapper;
    @Resource
    private ContractBaseInfoMapper contractBaseInfoMapper;
    @Resource
    private PaymentBaseInfoMapper paymentBaseInfoMapper;

    @Override
    public String findProjectCode(String bizType, Long projectId) {
        if (StrUtil.equals(bizType, PROJ_REVIEW)) {
            ProjReviewBaseInfo projReviewBaseInfo = projReviewBaseInfoMapper.selectById(projectId);
            return projReviewBaseInfo.getProjCode();
        } else if (StrUtil.equals(bizType, PROJ_ESTABLISH)) {
            ProjEstablishBaseInfo projEstablishBaseInfo = projEstablishBaseInfoMapper.selectById(projectId);
            return projEstablishBaseInfo.getProjCode();
        } else if (StrUtil.equals(bizType, GROUP_CREDIT_REVIEW)) {
            GroupCreditReviewBaseInfo groupCreditReviewBaseInfo = groupCreditReviewBaseInfoMapper.selectById(projectId);
            return groupCreditReviewBaseInfo.getProjCode();
        } else if (StrUtil.equals(bizType, GROUP_CREDIT_ESTABLISH)) {
            GroupCreditEstablishBaseInfo groupCreditEstablishBaseInfo = groupCreditEstablishBaseInfoMapper.selectById(projectId);
            return groupCreditEstablishBaseInfo.getProjCode();
        } else if (StrUtil.equals(bizType, PAYMENT)) {
            PaymentBaseInfo paymentBaseInfo = paymentBaseInfoMapper.selectById(projectId);
            ContractBaseInfo contractBaseInfo = contractBaseInfoMapper.selectById(paymentBaseInfo.getContractId());
            return contractBaseInfo.getProjCode();
        }
        throw new MithrasException("未定义的处理类型");
    }

    @Override
    public CreditReportProjectReviewAddDTO buildProjectReviewAdd(String bizType, Long projectId) {
        if (StringUtils.isBlank(bizType)) {
            throw new MithrasException("业务类型不存在");
        }

        CreditReportProjectReviewAddDTO projectDTO = new CreditReportProjectReviewAddDTO();
        List<Long> clientIds = new ArrayList<>();

        if (PROJ_ESTABLISH.equals(bizType)) {
            clientIds = projEstablishTradeStructureMapper.selectList(Wrappers.<ProjEstablishTradeStructure>lambdaQuery()
                            .eq(ProjEstablishTradeStructure::getProjEstablishId, projectId))
                    .stream().map(ProjEstablishTradeStructure::getClientId).collect(Collectors.toList());
            ProjEstablishBaseInfo projEstablishBaseInfo = projEstablishBaseInfoMapper.selectById(projectId);
            projectDTO.setProjCode(projEstablishBaseInfo.getProjCode()).setProjectName(projEstablishBaseInfo.getProjName());
        }
        if (PROJ_REVIEW.equals(bizType)) {
            clientIds = projReviewTradeStructureMapper.selectList(Wrappers.<ProjReviewTradeStructure>lambdaQuery()
                            .eq(ProjReviewTradeStructure::getProjReviewId, projectId))
                    .stream().map(ProjReviewTradeStructure::getClientId).collect(Collectors.toList());
            ProjReviewBaseInfo projReviewBaseInfo = projReviewBaseInfoMapper.selectById(projectId);
            projectDTO.setProjCode(projReviewBaseInfo.getProjCode()).setProjectName(projReviewBaseInfo.getProjName());
        }
        if (GROUP_CREDIT_ESTABLISH.equals(bizType)) {
            GroupCreditEstablishBaseInfo groupCreditEstablishBaseInfo = groupCreditEstablishBaseInfoMapper.selectById(projectId);
            clientIds.add(groupCreditEstablishBaseInfo.getClientId());
            projectDTO.setProjCode(groupCreditEstablishBaseInfo.getProjCode()).setProjectName(groupCreditEstablishBaseInfo.getProjName());
        }
        if (GROUP_CREDIT_REVIEW.equals(bizType)) {
            GroupCreditReviewBaseInfo groupCreditReviewBaseInfo = groupCreditReviewBaseInfoMapper.selectById(projectId);
            clientIds.add(groupCreditReviewBaseInfo.getClientId());
            projectDTO.setProjCode(groupCreditReviewBaseInfo.getProjCode()).setProjectName(groupCreditReviewBaseInfo.getProjName());
        }
        if (PAYMENT.equals(bizType)) {
            PaymentBaseInfo paymentBaseInfo = paymentBaseInfoMapper.selectById(projectId);
            ContractBaseInfo contractBaseInfo = contractBaseInfoMapper.selectById(paymentBaseInfo.getContractId());
            projectDTO.setProjCode(contractBaseInfo.getProjCode()).setProjectName(contractBaseInfo.getProjName());
            clientIds = contractTradeStructureMapper.selectList(Wrappers.<ContractTradeStructure>lambdaQuery()
                            .eq(ContractTradeStructure::getContractId, contractBaseInfo.getId()))
                    .stream().map(ContractTradeStructure::getClientId).collect(Collectors.toList());
        }

        projectDTO.setClientInfos(buildClientInfos(clientIds));
        return projectDTO;
    }

    private List<CreditReportClientInfo> buildClientInfos(List<Long> clientIds) {
        List<Client> clients = clientMapper.selectBatchIds(clientIds);
        return clients.stream()
                .filter(item -> ClientType.CORPORATION.name().equals(item.getClientType()))
                .map(client -> {
                    CreditReportClientInfo info = new CreditReportClientInfo()
                            .setClientId(client.getId())
                            .setClientName(client.getClientName())
                            .setCscCode(client.getUscCode());

                    CorpCommerceInfo corpCommerceInfo = corpCommerceInfoMapper.selectOne(
                            Wrappers.<CorpCommerceInfo>lambdaQuery()
                                    .eq(CorpCommerceInfo::getClientId, client.getId())
                    );

                    Optional.ofNullable(corpCommerceInfo)
                            .map(CorpCommerceInfo::getZhongZhengCode)
                            .filter(StringUtils::isNotBlank)
                            .ifPresent(info::setZhongZhengCode);

                    return info;
                })
                .collect(Collectors.toList());
    }
}
