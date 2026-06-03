package cn.zswltech.mithras.fund.application.financing;

import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.financing.FundFinancingCollectAccountMapper;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.model.financing.FundFinancingCollectAccount;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class FundFinancingCollectAccountService extends ServiceImpl<FundFinancingCollectAccountMapper, FundFinancingCollectAccount> {
}
