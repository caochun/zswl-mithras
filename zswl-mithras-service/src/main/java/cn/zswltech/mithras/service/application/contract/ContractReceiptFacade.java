package cn.zswltech.mithras.service.application.contract;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.contract.application.contract.ContractReceiptApplicationService;
import cn.zswltech.mithras.dto.contract.ContractSingleIdREQ;
import cn.zswltech.mithras.dto.contract.receipt.*;
import cn.zswltech.mithras.dto.contract.rent.ContractReceiptComputeActualTaxRSP;
import cn.zswltech.mithras.dto.newftp.FtpAssessInfo;
import cn.zswltech.mithras.service.auth.aop.DataAuthCheck;
import cn.zswltech.mithras.service.auth.checker.contract.ContractBaseRemoveSubAuthChecker;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.contract.mapper.contract.ContractReceiptMapper;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractReceipt;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.model.FtpAssessmentInfo;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.system.service.Id2NameService;
import cn.zswltech.mithras.service.service.contract.ContractBaseInfoService;
import cn.zswltech.mithras.contract.core.application.ContractReceiptService;
import cn.zswltech.mithras.service.service.payment.FtpAssessmentInfoService;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

/**
 * @author dingqi
 * @date 2022/8/20
 * @description
 */
@Slf4j
@Service
public class ContractReceiptFacade implements ContractReceiptApplicationService {
    @Resource
    private ContractReceiptService contractReceiptService;
    @Resource
    private FtpAssessmentInfoService ftpAssessmentInfoService;

    private static final ThreadLocal<Long> contractIdThreadLocal = new ThreadLocal<>();

    @Override
    @DataAuthCheck(keyFieldName = "receiptId", checkerClass = ContractBaseRemoveSubAuthChecker.class, businessModule = BusinessModuleEnum.CONTRACT, mapperClass = ContractReceiptMapper.class)
    public R<Void> remove(@Valid ContractReceiptRemoveREQ contractReceiptRemoveREQ) {
        contractReceiptService.removeReceiptById(contractReceiptRemoveREQ.getReceiptId());
        return R.ok();
    }

    @Override
    public R<Void> updateActualIRR(ContractReceiptUpdateIrrREQ req) {
        ContractReceipt byId = contractReceiptService.getById(req.getReceiptId());
        if (ObjectUtil.isEmpty(byId)) {
            throw new RuntimeException("借据不存在");
        }
        byId.setActualIrr(req.getActualIrr());
        contractReceiptService.updateById(byId);
        return R.ok();
    }

    @Override
    public R<Void> updateActualTax(ContractReceiptUpdateActualTaxREQ req) {
        ContractReceipt contractReceipt = contractReceiptService.getById(req.getReceiptId());
        if (ObjectUtil.isEmpty(contractReceipt)) {
            throw new MithrasException("借据不存在");
        }
        Long rentExcludingTax = req.getRentExcludingTax();
        Long tax = req.getTax();
        //客户可能会只改一个内容
        contractReceiptService.lambdaUpdate()
                .set(!ObjectUtils.isEmpty(rentExcludingTax), ContractReceipt::getRentExcludingTax, rentExcludingTax)
                .set(!ObjectUtils.isEmpty(tax), ContractReceipt::getTax, tax)
                .eq(ContractReceipt::getId, contractReceipt.getId())
                .update();
        return R.ok();
    }

    @Override
    public R<List<ContractReceiptComputeActualTaxRSP>> computeFinancialCosts(ContractReceiptQueryActualTaxREQ req) {
        return contractReceiptService.computeFinancialCosts(req, false);
    }

    @Override
    public R<Void> generate(ContractReceiptQueryActualTaxREQ req) {
        try {
            contractIdThreadLocal.set(req.getContractId());
            return contractReceiptService.generate(req);
        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            log.error("生成实际租金表未知异常[contractId: {}]", req.getContractId(), e);
            throw new MithrasException("生成实际租金表发生未知异常");
        } finally {
            contractIdThreadLocal.remove();
        }
    }

    @Override
    public R<Void> updateActualStartDate(ContractReceiptUpdateStartDateREQ req) {
        contractReceiptService.updateActualStartDate(req);
        return R.ok();
    }

    @Override
    public R<Void> updateActualLeaseDate(ContractReceiptUpdateStartDateREQ req) {
        contractReceiptService.updateActualLeaseDate(req);
        return R.ok();
    }

    @Override
    public R<List<ContractReceiptInfoRSP>> listByContractId(ContractSingleIdREQ req) {
        List<ContractReceipt> contractReceiptList = contractReceiptService.listByContractId(req.getContractId());
        if (CollectionUtil.isEmpty(contractReceiptList)) {
            return R.ok(Collections.emptyList());
        }
        List<ContractReceiptInfoRSP> result = contractReceiptList.stream().map(e -> {
            ContractReceiptInfoRSP rsp = new ContractReceiptInfoRSP();
            rsp.setId(e.getId());
            rsp.setReceiptCode(e.getReceiptCode());
            // 查询最新的借据FTP考核信息
            FtpAssessmentInfo ftpAssessmentInfo = ftpAssessmentInfoService.findLatestEffect(LocalDate.now(), e.getId());
            if (Objects.nonNull(ftpAssessmentInfo)) {
                FtpAssessInfo ftpAssessInfo = BeanUtil.copyProperties(ftpAssessmentInfo, FtpAssessInfo.class);
                ContractBaseInfo contractBaseInfo = SpringUtil.getBean(ContractBaseInfoService.class).getById(e.getContractId());
                if (Objects.nonNull(contractBaseInfo)) {
                    ftpAssessInfo.setClientId(contractBaseInfo.getClientId());
                    ftpAssessInfo.setClientName(SpringUtil.getBean(Id2NameService.class).clientId2NameSingle(contractBaseInfo.getClientId()));
                    ftpAssessInfo.setContractId(contractBaseInfo.getId());
                    ftpAssessInfo.setContractCode(contractBaseInfo.getContractCode());
                    ftpAssessInfo.setReceiptId(e.getId());
                    ftpAssessInfo.setReceiptCode(e.getReceiptCode());
                }
                rsp.setFtpAssessInfo(ftpAssessInfo);
            }
            return rsp;
        }).collect(Collectors.toList());
        return R.ok(result);
    }
}
