package cn.zswltech.mithras.service.service.fund.receiptrepay;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.fund.receiptrepay.*;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.fund.application.convert.receiptrepay.FundRepayAccountConverter;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.receiptrepay.FundRepayAccountMapper;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.lib.receiptrepay.FundRepayAccountLibMapper;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.model.receiptrepay.FundRepayAccount;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.model.receiptrepay.FundRepayAccountLib;
import cn.zswltech.mithras.service.others.MithrasException;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * @author zhaozhengkang
 * @description 资金管理-收付款-我方付款账户
 * @date 2023-02-22
 */
@Service
public class FundRepayAccountService extends ServiceImpl<FundRepayAccountMapper, FundRepayAccount> {
    @Resource
    private FundRepayAccountLibMapper baseLibMapper;
    @Resource
    private FundRepayAccountConverter baseConverter;
    @Resource
    private FundReceiptRepayStateService fundReceiptRepayStateService;

    @Transactional(rollbackFor = Throwable.class)
    public void add(FundRepayAccountAddREQ req) {
        FundRepayAccount info = BeanUtil.copyProperties(req, FundRepayAccount.class);
        info.setReceiptRepayId(req.getReceiptRepayId());
        baseMapper.insert(info);
        fundReceiptRepayStateService.modifyUpdateProcessState(info.getReceiptRepayId());
    }

    @Transactional(rollbackFor = Throwable.class)
    public void modify(FundRepayAccountModifyREQ req) {
        FundRepayAccount originalInfo = baseMapper.selectById(req.getId());
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        FundRepayAccount info = BeanUtil.copyProperties(req, FundRepayAccount.class);
        baseMapper.updateById(info);
        fundReceiptRepayStateService.modifyUpdateProcessState(originalInfo.getReceiptRepayId());
    }

    public PageR<FundRepayAccountListRSP> list(FundRepayAccountListREQ req) {
        if (ObjectUtil.isNotEmpty(req.getVersion())) {
            Page<FundRepayAccountLib> libs = baseLibMapper.selectPage(
                    new Page<>(req.getPage(), req.getPageSize()),
                    Wrappers.<FundRepayAccountLib>lambdaQuery()
                            .eq(FundRepayAccountLib::getReceiptRepayId, req.getReceiptRepayId())
                            .eq(FundRepayAccountLib::getVersion, req.getVersion()));
            return PageR.of(libs, baseConverter.lib2ListRsp(libs.getRecords()));
        }
        Page<FundRepayAccount> repayAccountPage = baseMapper.selectPage(
                new Page<>(req.getPage(), req.getPageSize()),
                Wrappers.<FundRepayAccount>lambdaQuery()
                        .eq(FundRepayAccount::getReceiptRepayId, req.getReceiptRepayId()));
        return PageR.of(repayAccountPage, baseConverter.entity2ListRsp(repayAccountPage.getRecords()));
    }

    @Transactional(rollbackFor = Throwable.class)
    public void remove(FundRepayAccountRemoveREQ req) {
        FundRepayAccount originalInfo = baseMapper.selectById(req.getId());
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        baseMapper.deleteById(req.getId());
        fundReceiptRepayStateService.modifyUpdateProcessState(originalInfo.getReceiptRepayId());
    }

}