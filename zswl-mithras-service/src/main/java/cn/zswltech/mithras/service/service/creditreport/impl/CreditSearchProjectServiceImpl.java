package cn.zswltech.mithras.creditreport.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.creditreport.*;
import cn.zswltech.mithras.creditreport.constant.CreditReportConstants;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.customer.domain.enums.client.ClientType;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.corp.CorpCommerceInfoMapper;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.Client;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.CorpCommerceInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractTradeStructure;
import cn.zswltech.mithras.credit.infrastructure.persistence.groupcredit.establish.model.GroupCreditEstablishBaseInfo;
import cn.zswltech.mithras.credit.infrastructure.persistence.groupcredit.review.model.GroupCreditReviewBaseInfo;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.model.PaymentBaseInfo;
import cn.zswltech.mithras.projectprocess.mapper.model.projestablish.ProjEstablishBaseInfo;
import cn.zswltech.mithras.projectprocess.mapper.model.projestablish.ProjEstablishTradeStructure;
import cn.zswltech.mithras.projectprocess.mapper.model.projreview.ProjReviewBaseInfo;
import cn.zswltech.mithras.projectprocess.mapper.model.projreview.ProjReviewTradeStructure;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.system.service.Id2NameService;
import cn.zswltech.mithras.service.service.client.ClientService;
import cn.zswltech.mithras.service.service.contract.ContractBaseInfoService;
import cn.zswltech.mithras.service.service.contract.ContractTradeStructureService;
import cn.zswltech.mithras.creditreport.service.CreditReportBaseInfoService;
import cn.zswltech.mithras.creditreport.service.CreditSearchProjectService;
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
public class CreditSearchProjectServiceImpl implements CreditSearchProjectService {

    /*@Resource
    private CreditReportItemDOMapper creditReportItemDOMapper;

    @Resource
    private CreditReportDOMapper creditReportDOMapper;*/

    @Resource
    private ClientService clientService;

    @Resource
    private CorpCommerceInfoMapper corpCommerceInfoMapper;

    //项目相关
    //项目立项
    @Resource
    private ProjEstablishTradeStructureService projEstablishTradeStructureService;

    @Resource
    private ProjEstablishBaseInfoService projEstablishBaseInfoService;

    //项目评审
    @Resource
    private ProjReviewTradeStructureService projReviewTradeStructureService;

    @Resource
    private ProjReviewBaseInfoService projReviewBaseInfoService;

    //授信立项
    @Resource
    private GroupCreditEstablishBaseInfoService groupCreditEstablishBaseInfoService;

    //授信评审
    @Resource
    private GroupCreditReviewBaseInfoService groupCreditReviewBaseInfoService;

    //付款申请
    @Resource
    private ContractTradeStructureService contractTradeStructureService;

    @Resource
    private ContractBaseInfoService contractBaseInfoService;

    @Resource
    private Id2NameService id2NameService;
    @Resource
    private CreditReportBaseInfoService creditReportBaseInfoService;

    @Override
    public PageR<CreditReportListDTO> list(CreditSearchProjectQuery req) {
        String projCode;
        if (StrUtil.equals(req.getProjIdDataType(), BusinessModuleEnum.PROJ_REVIEW.name())) {
            ProjReviewBaseInfo projReviewBaseInfo = SpringUtil.getBean(ProjReviewBaseInfoService.class).getById(req.getProjectId());
            projCode = projReviewBaseInfo.getProjCode();
        } else if (StrUtil.equals(req.getProjIdDataType(), BusinessModuleEnum.PROJ_ESTABLISH.name())) {
            ProjEstablishBaseInfo projEstablishBaseInfo = SpringUtil.getBean(ProjEstablishBaseInfoService.class).getById(req.getProjectId());
            projCode = projEstablishBaseInfo.getProjCode();
        } else if (StrUtil.equals(req.getProjIdDataType(), BusinessModuleEnum.GROUP_CREDIT_REVIEW.name())) {
            GroupCreditReviewBaseInfo groupCreditReviewBaseInfo = SpringUtil.getBean(GroupCreditReviewBaseInfoService.class).getById(req.getProjectId());
            projCode = groupCreditReviewBaseInfo.getProjCode();
        } else if (StrUtil.equals(req.getProjIdDataType(), BusinessModuleEnum.GROUP_CREDIT_ESTABLISH.name())) {
            GroupCreditEstablishBaseInfo groupCreditEstablishBaseInfo = SpringUtil.getBean(GroupCreditEstablishBaseInfoService.class).getById(req.getProjectId());
            projCode = groupCreditEstablishBaseInfo.getProjCode();
        } else if (StrUtil.equals(req.getProjIdDataType(), BusinessModuleEnum.PAYMENT.name())) {
            PaymentBaseInfo paymentBaseInfo = SpringUtil.getBean(PaymentBaseInfoService.class).getById(req.getProjectId());
            ContractBaseInfo contractBaseInfo = SpringUtil.getBean(ContractBaseInfoService.class).getById(paymentBaseInfo.getContractId());
            projCode = contractBaseInfo.getProjCode();
        } else {
            throw new MithrasException("未定义的处理类型");
        }
        if (StrUtil.isBlank(projCode)) {
            throw new MithrasException("无法确定项目编号");
        }
        //List<CreditReportItemDO> itemList = creditReportItemDOMapper.selectList(Wrappers.<CreditReportItemDO>lambdaQuery().eq(CreditReportDO::getProjCode, projCode));
        //return this.convertCreditReport(itemList);
        if (ObjectUtil.isEmpty(projCode)) {
            return null;
        }
        CreditReportListREQ reportListREQ = new CreditReportListREQ();
        reportListREQ.setProjCode(projCode);
        return creditReportBaseInfoService.list(reportListREQ);
    }

   /* private List<CreditReportListDTO> convertCreditReport(List<CreditReportItemDO> items) {
        if (CollectionUtils.isEmpty(items)) {
            return Collections.emptyList();
        }

        //提取用户和部门 ID 集合
        List<Long> userIdList = items.stream()
                .map(CreditReportDO::getApplyUser)
                .distinct()
                .collect(Collectors.toList());

        Set<Long> deptIdList = items.stream()
                .map(CreditReportDO::getApplyOrg)
                .collect(Collectors.toSet());

        //批量获取用户名和部门名
        Map<Long, String> userId2Name = id2NameService.sysUserId2Name(userIdList);
        Map<Long, String> deptId2Name = id2NameService.deptId2Name(deptIdList);

        //最终转换为 DTO 列表
        return items.stream().map(item -> {

            // 构造 DTO
            CreditReportListDTO listDTO = new CreditReportListDTO();
            BeanUtils.copyProperties(item, listDTO);
            listDTO.setClientNameList(Collections.singletonList(item.getClientName()));
            listDTO.setCscCodeList(Collections.singletonList(item.getCscCode()));
            listDTO.setZhongZhengCodeList(Collections.singletonList(item.getZhongZhengCode()));
            listDTO.setSelectGoalList(Collections.singletonList(item.getSelectGoal()));
            // 设置用户和部门名称（带默认值）
            listDTO.setApplyUserName(userId2Name.getOrDefault(item.getApplyUser(), "未知用户"));
            listDTO.setApplyOrgName(deptId2Name.getOrDefault(item.getApplyOrg(), "未知部门"));

            return listDTO;
        }).collect(Collectors.toList());
    }*/

    @Override
    public CreditReportProjectReviewAddDTO showCreditReportByProjId(CreditSearchProjectCmd cmd) {
        if (StringUtils.isBlank(cmd.getBizType())) {
            throw new MithrasException("业务类型不存在");
        }
        CreditReportProjectReviewAddDTO projectDTO = new CreditReportProjectReviewAddDTO();
        //项目下的客户id
        List<Long> clientIds = new ArrayList<>();
        if (BusinessModuleEnum.PROJ_ESTABLISH.name().equals(cmd.getBizType())) {
            clientIds = projEstablishTradeStructureService.list(Wrappers.<ProjEstablishTradeStructure>lambdaQuery().eq(ProjEstablishTradeStructure::getProjEstablishId, cmd.getProjectId()))
                    .stream().map(ProjEstablishTradeStructure::getClientId).collect(Collectors.toList());
            ProjEstablishBaseInfo projEstablishBaseInfo = projEstablishBaseInfoService.getById(cmd.getProjectId());
            projectDTO.setProjCode(projEstablishBaseInfo.getProjCode()).setProjectName(projEstablishBaseInfo.getProjName());
        }
        if (BusinessModuleEnum.PROJ_REVIEW.name().equals(cmd.getBizType())) {
            clientIds = projReviewTradeStructureService.list(Wrappers.<ProjReviewTradeStructure>lambdaQuery().eq(ProjReviewTradeStructure::getProjReviewId, cmd.getProjectId()))
                    .stream().map(ProjReviewTradeStructure::getClientId).collect(Collectors.toList());
            ProjReviewBaseInfo projReviewBaseInfo = projReviewBaseInfoService.getById(cmd.getProjectId());
            projectDTO.setProjCode(projReviewBaseInfo.getProjCode()).setProjectName(projReviewBaseInfo.getProjName());
        }
        if (BusinessModuleEnum.GROUP_CREDIT_ESTABLISH.name().equals(cmd.getBizType())) {
            GroupCreditEstablishBaseInfo groupCreditEstablishBaseInfo = groupCreditEstablishBaseInfoService.getById(cmd.getProjectId());
            clientIds.add(groupCreditEstablishBaseInfo.getClientId());
            projectDTO.setProjCode(groupCreditEstablishBaseInfo.getProjCode()).setProjectName(groupCreditEstablishBaseInfo.getProjName());
        }
        if (BusinessModuleEnum.GROUP_CREDIT_REVIEW.name().equals(cmd.getBizType())) {
            GroupCreditReviewBaseInfo groupCreditReviewBaseInfo = groupCreditReviewBaseInfoService.getById(cmd.getProjectId());
            clientIds.add(groupCreditReviewBaseInfo.getClientId());
            projectDTO.setProjCode(groupCreditReviewBaseInfo.getProjCode()).setProjectName(groupCreditReviewBaseInfo.getProjName());
        }
        if (BusinessModuleEnum.PAYMENT.name().equals(cmd.getBizType())) {
            PaymentBaseInfo paymentBaseInfo = SpringUtil.getBean(PaymentBaseInfoService.class).getById(cmd.getProjectId());
            ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(paymentBaseInfo.getContractId());
            projectDTO.setProjCode(contractBaseInfo.getProjCode()).setProjectName(contractBaseInfo.getProjName());
            clientIds = contractTradeStructureService.list(Wrappers.<ContractTradeStructure>lambdaQuery().eq(ContractTradeStructure::getContractId, contractBaseInfo.getId()))
                    .stream().map(ContractTradeStructure::getClientId).collect(Collectors.toList());
        }
        //构建CreditReportProjectReviewAddDTO
        projectDTO.setProjId(cmd.getProjectId())
                .setReportFormat(CreditReportConstants.REPORT_FORMAT)
                .setSelectVersion(CreditReportConstants.SELECT_VERSION);


        //批量查询客户信息
        List<Client> clients = clientService.listByIds(clientIds);

        //构建 CreditReportClientInfo列表
        List<CreditReportClientInfo> clientInfos = clients.stream()
                .filter(item->ClientType.CORPORATION.name().equals(item.getClientType()))
                .map(client -> {
                    CreditReportClientInfo info = new CreditReportClientInfo()
                            .setClientId(client.getId())
                            .setClientName(client.getClientName())
                            .setCscCode(client.getUscCode());

                    //查询中征码
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

        //设置客户信息到项目 DTO
        projectDTO.setClientInfos(clientInfos);

        return projectDTO;
    }

   /* @Override
    public void delete(Long id) {
        CreditReportItemDO itemDO = creditReportItemDOMapper.selectById(id);
        if (Objects.isNull(itemDO)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        if (!ProcessState.UN_SUBMIT.name().equals(itemDO.getApplyStatus())) {
            throw new MithrasException("征信查询申请状态不是待提交，无法删除");
        }
        Long creditReportId = itemDO.getCreditReportId();
        Integer count = creditReportItemDOMapper.selectCount(Wrappers.<CreditReportItemDO>lambdaQuery().eq(CreditReportItemDO::getCreditReportId, creditReportId));
        if (count == 1) {
            creditReportDOMapper.deleteById(creditReportId);
        }
        creditReportItemDOMapper.deleteById(itemDO.getId());
    }*/
}
