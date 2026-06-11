package cn.zswltech.mithras.customer.application.client;


import cn.zswltech.mithras.customer.application.client.ClientDataSaveCheckInterface;
import cn.zswltech.mithras.dto.client.normal.NormalBankAccountAddREQ;
import cn.zswltech.mithras.dto.client.normal.NormalBankAccountListREQ;
import cn.zswltech.mithras.dto.client.normal.NormalBankAccountModifyREQ;
import cn.zswltech.mithras.customer.model.client.NormalBankAccount;
import cn.zswltech.mithras.customer.mapper.normal.NormalBankAccountMapper;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import static cn.hutool.core.bean.BeanUtil.copyProperties;
import static cn.hutool.core.util.ObjectUtil.isNull;
import static cn.zswltech.mithras.foundation.constant.ResultMsg.RECORD_NOT_EXIST;

/**
 * @author junke
 */
@Service
public class NormalBankAccountService implements ClientDataSaveCheckInterface<NormalBankAccount> {
    @Resource
    private NormalBankAccountMapper bankAccountMapper;


    public void add(NormalBankAccountAddREQ req) {
        NormalBankAccount info = copyProperties(req, NormalBankAccount.class);
        check(info);
        bankAccountMapper.insert(info);
        recordClientStatus(info);
    }

    public void modify(NormalBankAccountModifyREQ req) {
        NormalBankAccount originalInfo = bankAccountMapper.selectById(req.getId());
        if (isNull(originalInfo)) {
            throw new MithrasException(RECORD_NOT_EXIST);
        }
        NormalBankAccount info = copyProperties(req, NormalBankAccount.class);
        check(info);
        bankAccountMapper.updateAnnotationIncludeNullById(copyProperties(req, NormalBankAccount.class));
        recordClientStatus(info);
    }

    public void remove(Long id) {
        NormalBankAccount originalInfo = bankAccountMapper.selectById(id);
        if (isNull(originalInfo)) {
            throw new MithrasException(RECORD_NOT_EXIST);
        }
        check(originalInfo);
        bankAccountMapper.deleteById(id);
        recordClientStatus(originalInfo);
    }

    public Page<NormalBankAccount> list(NormalBankAccountListREQ req) {
        return bankAccountMapper.selectPage(new Page<>(req.getPage(), req.getPageSize()),
                Wrappers.<NormalBankAccount>lambdaQuery().eq(NormalBankAccount::getClientId, req.getClientId())
                        .orderByDesc(NormalBankAccount::getUpdateTime)
        );
    }
}
