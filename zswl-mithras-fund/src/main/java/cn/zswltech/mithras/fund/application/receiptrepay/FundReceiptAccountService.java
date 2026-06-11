package cn.zswltech.mithras.fund.application.receiptrepay;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.fund.receiptrepay.*;
import cn.zswltech.mithras.fund.application.receiptrepay.port.FundReceiptRepayStateUpdatePort;
import cn.zswltech.mithras.foundation.constant.ResultMsg;
import cn.zswltech.mithras.fund.application.convert.receiptrepay.FundReceiptAccountConverter;
import cn.zswltech.mithras.fund.mapper.receiptrepay.FundReceiptAccountMapper;
import cn.zswltech.mithras.fund.mapper.lib.receiptrepay.FundReceiptAccountLibMapper;
import cn.zswltech.mithras.fund.model.receiptrepay.FundReceiptAccount;
import cn.zswltech.mithras.fund.model.receiptrepay.FundReceiptAccountLib;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * @author zhaozhengkang
 * @description 资金管理-收付款-对方收款账户
 * @date 2023-02-22
 */
@Service
public class FundReceiptAccountService extends ServiceImpl<FundReceiptAccountMapper, FundReceiptAccount> {

    @Resource
    private FundReceiptAccountLibMapper baseLibMapper;
    @Resource
    private FundReceiptAccountConverter baseConverter;
    @Resource
    private FundReceiptRepayStateUpdatePort fundReceiptRepayStateUpdatePort;

    @Transactional(rollbackFor = Throwable.class)
    public void add(FundReceiptAccountAddREQ req) {
        FundReceiptAccount info = BeanUtil.copyProperties(req, FundReceiptAccount.class);
        baseMapper.insert(info);
        fundReceiptRepayStateUpdatePort.modifyUpdateProcessState(info.getReceiptRepayId());
    }

    @Transactional(rollbackFor = Throwable.class)
    public void modify(FundReceiptAccountModifyREQ req) {
        FundReceiptAccount originalInfo = baseMapper.selectById(req.getId());
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        FundReceiptAccount info = BeanUtil.copyProperties(req, FundReceiptAccount.class);
        baseMapper.updateAnnotationIncludeNullById(info);
        fundReceiptRepayStateUpdatePort.modifyUpdateProcessState(originalInfo.getReceiptRepayId());
    }

    public PageR<FundReceiptAccountListRSP> list(FundReceiptAccountListREQ req) {
        if (ObjectUtil.isNotEmpty(req.getVersion())) {
            Page<FundReceiptAccountLib> libPage = baseLibMapper.selectPage(
                    new Page<>(req.getPage(), req.getPageSize()),
                    Wrappers.<FundReceiptAccountLib>lambdaQuery()
                            .eq(FundReceiptAccountLib::getVersion, req.getVersion())
                            .eq(FundReceiptAccountLib::getReceiptRepayId, req.getReceiptRepayId()));
            return PageR.of(libPage, baseConverter.lib2ListRsp(libPage.getRecords()));
        }
        Page<FundReceiptAccount> page = baseMapper.selectPage(
                new Page<>(req.getPage(), req.getPageSize()),
                Wrappers.<FundReceiptAccount>lambdaQuery()
                        .eq(FundReceiptAccount::getReceiptRepayId, req.getReceiptRepayId()));
        return PageR.of(page, baseConverter.entity2ListRsp(page.getRecords()));
    }

    @Transactional(rollbackFor = Throwable.class)
    public void remove(FundReceiptAccountRemoveREQ req) {
        FundReceiptAccount originalInfo = baseMapper.selectById(req.getId());
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        baseMapper.deleteById(req.getId());
        fundReceiptRepayStateUpdatePort.modifyUpdateProcessState(originalInfo.getReceiptRepayId());
    }

}
