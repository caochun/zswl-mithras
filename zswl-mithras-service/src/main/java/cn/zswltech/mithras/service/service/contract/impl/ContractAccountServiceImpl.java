package cn.zswltech.mithras.service.service.contract.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.dto.contract.account.*;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.service.enums.common.ProjectBizType;
import cn.zswltech.mithras.service.enums.contract.ContractAccountPayeeTypeEnum;
import cn.zswltech.mithras.service.enums.contract.ContractAccountUseEnum;
import cn.zswltech.mithras.service.mapper.contract.ContractAccountMapper;
import cn.zswltech.mithras.service.mapper.model.contract.ContractAccount;
import cn.zswltech.mithras.service.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.contract.ContractAccountService;
import cn.zswltech.mithras.service.service.contract.ContractBaseInfoService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/**
* @description 合同-收款账户表
* @author vico
* @date 2022-08-12
*/
@Service
public class ContractAccountServiceImpl extends ServiceImpl<ContractAccountMapper, ContractAccount> implements ContractAccountService {

    @Resource
    private ContractAccountMapper contractAccountMapper;
    @Resource
    private ContractBaseInfoService contractBaseInfoService;

    @Transactional(rollbackFor = Throwable.class)
    public String add(ContractAccountAddREQ req) {
        ContractAccount info = BeanUtil.copyProperties(req, ContractAccount.class);
        this.check(info);
        contractAccountMapper.insert(info);
        /*if(baseMapper.selectCount(Wrappers.<ContractAccount>lambdaQuery()
                .eq(ContractAccount::getContractId, req.getContractId())
                .eq(ContractAccount::getClientName, req.getClientName())) > 0){
            return (req.getAccountName() +"已存在收款账户");
        }*/
        return "保存成功";
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public void modify(List<ContractAccountModifyREQ> req) {
        ContractAccount info;
        for(ContractAccountModifyREQ modifyREQ : req){
            info = BeanUtil.copyProperties(modifyREQ, ContractAccount.class);
            this.check(info);
            contractAccountMapper.updateAnnotationIncludeNullById(info);
        }
    }

    @Override
    public List<ContractAccountListRSP> list(ContractAccountListREQ req) {
        List<ContractAccountListRSP> rsp = new ArrayList<>();
        ContractAccountListRSP contractAccountListRSP;
        List<ContractAccount> contractAccounts = baseMapper.selectList(Wrappers.<ContractAccount>lambdaQuery()
                .eq(ContractAccount::getContractId, req.getContractId())
                .eq(ContractAccount::getAccountUse, req.getAccountUse()));
        for (ContractAccount contractAccount : contractAccounts) {
            contractAccountListRSP = BeanUtil.copyProperties(contractAccount, ContractAccountListRSP.class);
            rsp.add(contractAccountListRSP);
        }
        return rsp;
    }

    @Transactional(rollbackFor = Throwable.class)
    public Boolean remove(ContractAccountRemoveREQ req) {
        ContractAccount originalInfo = contractAccountMapper.selectById(req.getId());
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        return contractAccountMapper.deleteById(req.getId()) > 0 ? Boolean.TRUE : Boolean.FALSE;
    }

    @Override
    public List<ContractAccount> listByContractUse(Long contractId, String accountUse) {
        LambdaQueryWrapper<ContractAccount> query = Wrappers.lambdaQuery();
        query.eq(ContractAccount::getContractId, contractId);
        query.eq(ContractAccount::getAccountUse, accountUse);
        return this.list(query);
    }

    private void check(ContractAccount contractAccount) {
        ContractBaseInfo contractBaseInfo = Assert.notNull(contractBaseInfoService.getById(contractAccount.getContractId()), () -> MithrasException.newException("合同信息不存在"));
        if (ProjectBizType.BL.name().equals(contractBaseInfo.getBizType()) || ProjectBizType.ZR.name().equals(contractBaseInfo.getBizType())) {
            if (Objects.equals(contractAccount.getAccountUse(), ContractAccountUseEnum.BLHK.name()) || Objects.equals(contractAccount.getAccountUse(), ContractAccountUseEnum.ZRHK.name())) {
                Assert.notBlank(contractAccount.getRepayWay(), () -> MithrasException.newException("还款方式不能为空"));
            }
        } else {
//            Assert.notBlank(contractAccount.getClientName(), () -> MithrasException.newException("客户名称不能为空"));
            Assert.notBlank(contractAccount.getPayeeType(), () -> MithrasException.newException("收款方类型不能为空"));
            if (Objects.equals(contractAccount.getPayeeType(), ContractAccountPayeeTypeEnum.JIA.name())) {
                Assert.notNull(contractAccount.getBankAccountId(), () -> MithrasException.newException("我方账户id不能为空"));
            }
        }
    }
}