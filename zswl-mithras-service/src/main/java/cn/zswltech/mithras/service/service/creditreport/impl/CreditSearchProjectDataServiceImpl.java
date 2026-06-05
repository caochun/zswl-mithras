package cn.zswltech.mithras.service.service.creditreport.impl;

import cn.hutool.core.util.StrUtil;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractTradeStructure;
import cn.zswltech.mithras.credit.infrastructure.persistence.groupcredit.establish.model.GroupCreditEstablishBaseInfo;
import cn.zswltech.mithras.credit.infrastructure.persistence.groupcredit.review.model.GroupCreditReviewBaseInfo;
import cn.zswltech.mithras.creditreport.service.CreditSearchProjectDataService;
import cn.zswltech.mithras.customer.domain.enums.client.ClientType;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.corp.CorpCommerceInfoMapper;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.Client;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.CorpCommerceInfo;
import cn.zswltech.mithras.dto.creditreport.CreditReportClientInfo;
import cn.zswltech.mithras.dto.creditreport.CreditReportProjectReviewAddDTO;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.model.PaymentBaseInfo;
import cn.zswltech.mithras.projectprocess.mapper.model.projestablish.ProjEstablishBaseInfo;
import cn.zswltech.mithras.projectprocess.mapper.model.projestablish.ProjEstablishTradeStructure;
import cn.zswltech.mithras.projectprocess.mapper.model.projreview.ProjReviewBaseInfo;
import cn.zswltech.mithras.projectprocess.mapper.model.projreview.ProjReviewTradeStructure;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.client.ClientService;
import cn.zswltech.mithras.service.service.contract.ContractBaseInfoService;
import cn.zswltech.mithras.service.service.contract.ContractTradeStructureService;
import cn.zswltech.mithras.service.service.groupcreditestablish.GroupCreditEstablishBaseInfoService;
import cn.zswltech.mithras.service.service.groupcreditreview.GroupCreditReviewBaseInfoService;
import cn.zswltech.mithras.service.service.payment.PaymentBaseInfoService;
import cn.zswltech.mithras.service.service.projestablish.ProjEstablishBaseInfoService;
import cn.zswltech.mithras.service.service.projestablish.ProjEstablishTradeStructureService;
import cn.zswltech.mithras.service.service.projreview.ProjReviewBaseInfoService;
import cn.zswltech.mithras.service.service.projreview.ProjReviewTradeStructureService;
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

    @Resource
    private ClientService clientService;
    @Resource
    private CorpCommerceInfoMapper corpCommerceInfoMapper;
    @Resource
    private ProjEstablishTradeStructureService projEstablishTradeStructureService;
    @Resource
    private ProjEstablishBaseInfoService projEstablishBaseInfoService;
    @Resource
    private ProjReviewTradeStructureService projReviewTradeStructureService;
    @Resource
    private ProjReviewBaseInfoService projReviewBaseInfoService;
    @Resource
    private GroupCreditEstablishBaseInfoService groupCreditEstablishBaseInfoService;
    @Resource
    private GroupCreditReviewBaseInfoService groupCreditReviewBaseInfoService;
    @Resource
    private ContractTradeStructureService contractTradeStructureService;
    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private PaymentBaseInfoService paymentBaseInfoService;

    @Override
    public String findProjectCode(String bizType, Long projectId) {
        if (StrUtil.equals(bizType, BusinessModuleEnum.PROJ_REVIEW.name())) {
            ProjReviewBaseInfo projReviewBaseInfo = projReviewBaseInfoService.getById(projectId);
            return projReviewBaseInfo.getProjCode();
        } else if (StrUtil.equals(bizType, BusinessModuleEnum.PROJ_ESTABLISH.name())) {
            ProjEstablishBaseInfo projEstablishBaseInfo = projEstablishBaseInfoService.getById(projectId);
            return projEstablishBaseInfo.getProjCode();
        } else if (StrUtil.equals(bizType, BusinessModuleEnum.GROUP_CREDIT_REVIEW.name())) {
            GroupCreditReviewBaseInfo groupCreditReviewBaseInfo = groupCreditReviewBaseInfoService.getById(projectId);
            return groupCreditReviewBaseInfo.getProjCode();
        } else if (StrUtil.equals(bizType, BusinessModuleEnum.GROUP_CREDIT_ESTABLISH.name())) {
            GroupCreditEstablishBaseInfo groupCreditEstablishBaseInfo = groupCreditEstablishBaseInfoService.getById(projectId);
            return groupCreditEstablishBaseInfo.getProjCode();
        } else if (StrUtil.equals(bizType, BusinessModuleEnum.PAYMENT.name())) {
            PaymentBaseInfo paymentBaseInfo = paymentBaseInfoService.getById(projectId);
            ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(paymentBaseInfo.getContractId());
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

        if (BusinessModuleEnum.PROJ_ESTABLISH.name().equals(bizType)) {
            clientIds = projEstablishTradeStructureService.list(Wrappers.<ProjEstablishTradeStructure>lambdaQuery()
                            .eq(ProjEstablishTradeStructure::getProjEstablishId, projectId))
                    .stream().map(ProjEstablishTradeStructure::getClientId).collect(Collectors.toList());
            ProjEstablishBaseInfo projEstablishBaseInfo = projEstablishBaseInfoService.getById(projectId);
            projectDTO.setProjCode(projEstablishBaseInfo.getProjCode()).setProjectName(projEstablishBaseInfo.getProjName());
        }
        if (BusinessModuleEnum.PROJ_REVIEW.name().equals(bizType)) {
            clientIds = projReviewTradeStructureService.list(Wrappers.<ProjReviewTradeStructure>lambdaQuery()
                            .eq(ProjReviewTradeStructure::getProjReviewId, projectId))
                    .stream().map(ProjReviewTradeStructure::getClientId).collect(Collectors.toList());
            ProjReviewBaseInfo projReviewBaseInfo = projReviewBaseInfoService.getById(projectId);
            projectDTO.setProjCode(projReviewBaseInfo.getProjCode()).setProjectName(projReviewBaseInfo.getProjName());
        }
        if (BusinessModuleEnum.GROUP_CREDIT_ESTABLISH.name().equals(bizType)) {
            GroupCreditEstablishBaseInfo groupCreditEstablishBaseInfo = groupCreditEstablishBaseInfoService.getById(projectId);
            clientIds.add(groupCreditEstablishBaseInfo.getClientId());
            projectDTO.setProjCode(groupCreditEstablishBaseInfo.getProjCode()).setProjectName(groupCreditEstablishBaseInfo.getProjName());
        }
        if (BusinessModuleEnum.GROUP_CREDIT_REVIEW.name().equals(bizType)) {
            GroupCreditReviewBaseInfo groupCreditReviewBaseInfo = groupCreditReviewBaseInfoService.getById(projectId);
            clientIds.add(groupCreditReviewBaseInfo.getClientId());
            projectDTO.setProjCode(groupCreditReviewBaseInfo.getProjCode()).setProjectName(groupCreditReviewBaseInfo.getProjName());
        }
        if (BusinessModuleEnum.PAYMENT.name().equals(bizType)) {
            PaymentBaseInfo paymentBaseInfo = paymentBaseInfoService.getById(projectId);
            ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(paymentBaseInfo.getContractId());
            projectDTO.setProjCode(contractBaseInfo.getProjCode()).setProjectName(contractBaseInfo.getProjName());
            clientIds = contractTradeStructureService.list(Wrappers.<ContractTradeStructure>lambdaQuery()
                            .eq(ContractTradeStructure::getContractId, contractBaseInfo.getId()))
                    .stream().map(ContractTradeStructure::getClientId).collect(Collectors.toList());
        }

        projectDTO.setClientInfos(buildClientInfos(clientIds));
        return projectDTO;
    }

    private List<CreditReportClientInfo> buildClientInfos(List<Long> clientIds) {
        List<Client> clients = clientService.listByIds(clientIds);
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
