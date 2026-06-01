package cn.zswltech.mithras.service.service.lib.fund.receiptrepay.service;

import cn.zswltech.mithras.service.mapper.lib.fund.receiptrepay.FundReceiptRepayBaseInfoLibMapper;
import cn.zswltech.mithras.service.mapper.model.fund.receiptrepay.FundReceiptRepayBaseInfoLib;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 收付款基本信息
 *
 * @author wangchuanhao
 * @date 2023/2/20 4:16 PM
 */
@Service
public class FundReceiptRepayBaseInfoLibService extends ServiceImpl<FundReceiptRepayBaseInfoLibMapper, FundReceiptRepayBaseInfoLib> {
    public FundReceiptRepayBaseInfoLib getLastestDirectLib(Long recepitRepayId) {
        return baseMapper.getLastestDirectLib(recepitRepayId);
    }

}
