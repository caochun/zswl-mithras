package cn.zswltech.mithras.service.service.finance;

import cn.zswltech.mithras.service.mapper.finance.FinanceProjectProfitDetailReceiptMapper;
import cn.zswltech.mithras.service.mapper.model.finance.FinanceProjectProfitDetailReceipt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author dingqi
 * @date 2024/10/28
 * @description
 */
@Slf4j
@Service
public class FinanceProjectProfitDetailReceiptService extends ServiceImpl<FinanceProjectProfitDetailReceiptMapper, FinanceProjectProfitDetailReceipt> {
    public List<FinanceProjectProfitDetailReceipt> listByProfitId(Long profitId) {
        LambdaQueryWrapper<FinanceProjectProfitDetailReceipt> query = Wrappers.lambdaQuery();
        query.eq(FinanceProjectProfitDetailReceipt::getProjectProfitId, profitId);
        return this.list(query);
    }
}
