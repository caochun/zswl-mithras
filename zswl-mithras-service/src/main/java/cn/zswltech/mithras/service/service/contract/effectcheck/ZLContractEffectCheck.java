package cn.zswltech.mithras.service.service.contract.effectcheck;

import cn.hutool.core.lang.Assert;
import cn.zswltech.mithras.dto.contract.ContractIdListREQ;
import cn.zswltech.mithras.dto.contract.account.ContractAccountListREQ;
import cn.zswltech.mithras.dto.contract.tenantry.ContractTenantryListRSP;
import cn.zswltech.mithras.common.enums.ProcessStatus;
import cn.zswltech.mithras.common.enums.ProjectBizType;
import cn.zswltech.mithras.service.enums.contract.ContractAccountUseEnum;
import cn.zswltech.mithras.service.enums.projestablish.LeaseType;
import cn.zswltech.mithras.service.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.service.mapper.model.leaseholdproperty.LeaseItemInfo;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.contract.ContractAccountService;
import cn.zswltech.mithras.service.service.contract.ContractLeaseItemService;
import cn.zswltech.mithras.service.service.contract.ContractRentEstimateService;
import cn.zswltech.mithras.service.service.leaseholdproperty.LeaseItemInfoService;
import cn.zswltech.mithras.common.util.StringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * @author dingqi
 * @date 2022/11/2
 * @description
 */
@Component
public class ZLContractEffectCheck extends AbstractContractEffectCheck {
    @Resource
    private ContractRentEstimateService contractRentEstimateService;
    @Resource
    private ContractAccountService contractAccountService;
    @Resource
    private ContractLeaseItemService contractLeaseItemService;
    @Resource
    private LeaseItemInfoService leaseItemInfoService;

    @Override
    public void check(ContractBaseInfo contractBaseInfo, boolean inProcess) throws MithrasException {
        // 基本信息-租赁类型
        Assert.notBlank(contractBaseInfo.getLeaseType(), () -> MithrasException.newException("租赁类型不能为空"));
        // 概算租金表
        Assert.notNull(contractBaseInfo.getEstimatedLeaseDate(), () -> MithrasException.newException("计划起租日不能为空"));
        Assert.notEmpty(contractRentEstimateService.listByContractId(contractBaseInfo.getId(), null), () -> MithrasException.newException("概算租金表不能为空"));
        // 承租人
        ContractIdListREQ contractIdListREQ = new ContractIdListREQ();
        contractIdListREQ.setContractId(contractBaseInfo.getId());
        List<ContractTenantryListRSP> contractTenantryListRSPList = Assert.notEmpty(contractTenantryService.list(contractIdListREQ), () -> MithrasException.newException("承租人不能为空"));
        for (ContractTenantryListRSP contractTenantryListRSP : contractTenantryListRSPList) {
            Assert.notNull(contractTenantryListRSP.getIsReport(), () -> MithrasException.newException("承租人是否上报征信不能为空"));
        }
        // 收款账户
        ContractAccountListREQ contractAccountListREQ = new ContractAccountListREQ();
        contractAccountListREQ.setContractId(contractBaseInfo.getId());
        if (ProjectBizType.ZZ.name().equals(contractBaseInfo.getBizType())) {
            contractAccountListREQ.setAccountUse(ContractAccountUseEnum.ZZSK.name());
        } else {
            contractAccountListREQ.setAccountUse(ContractAccountUseEnum.ZLSK.name());
        }
        Assert.notEmpty(contractAccountService.list(contractAccountListREQ), () -> MithrasException.newException("收款账户不能为空"));
        // 租赁物清单
        Assert.notEmpty(contractLeaseItemService.listByContractId(contractBaseInfo.getId()), () -> MithrasException.newException("租赁物清单不能为空"));
        // 如果是租赁回租合同需要进一步校验租赁物相关
        if (Objects.equals(contractBaseInfo.getBizType(), ProjectBizType.ZL.name()) && Objects.equals(contractBaseInfo.getLeaseType(), LeaseType.hui_zu.name())) {
            // 查询最新一条租赁物审批记录，如果是审批中则不允许提交
            LambdaQueryWrapper<LeaseItemInfo> query = Wrappers.lambdaQuery();
            query.eq(LeaseItemInfo::getProjReviewId, contractBaseInfo.getProjReviewId());
            query.orderByDesc(LeaseItemInfo::getId);
            query.last(StringUtil.mysqlLimitOne());
            LeaseItemInfo leaseItemInfo = leaseItemInfoService.getOne(query);
            if (Objects.nonNull(leaseItemInfo) && Objects.equals(leaseItemInfo.getApprovalStatus(), ProcessStatus.UNDER_APPROVAL.name())) {
                throw new MithrasException("存在处于审批中的租赁物审核记录，不允许提交");
            }
//            if (Objects.isNull(contractBaseInfo.getItemTotalAmount())) {
//                throw new MithrasException("租赁物总额不能为空");
//            }
//            if (contractBaseInfo.getItemTotalAmount() * 0.9 < contractBaseInfo.getApplyCreditAmount()) {
//                throw new MithrasException("租赁物总额*90%小于合同金额，不允许提交流程");
//            }
        }
        this.commonContractFileCheck(contractBaseInfo, inProcess);
    }
}
