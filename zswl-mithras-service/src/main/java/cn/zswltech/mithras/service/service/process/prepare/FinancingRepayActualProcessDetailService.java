package cn.zswltech.mithras.service.service.process.prepare;

import cn.zswltech.mithras.dto.process.prepare.RentCollectionMonthModifyAccountREQ;
import cn.zswltech.mithras.service.mapper.model.process.prepare.FinancingRepayActualProcessDetail;
import cn.zswltech.mithras.service.mapper.model.process.prepare.RentCollectionMonthDetail;
import cn.zswltech.mithras.service.mapper.process.prepare.FinancingRepayActualProcessDetailMapper;
import cn.zswltech.mithras.service.mapper.process.prepare.RentCollectionMonthDetailMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author luyi
 */
@Slf4j
@Service
public class FinancingRepayActualProcessDetailService extends ServiceImpl<FinancingRepayActualProcessDetailMapper, FinancingRepayActualProcessDetail> {
    public List<FinancingRepayActualProcessDetail> byPrepareId(Long prepareId) {

        return this.list(Wrappers.<FinancingRepayActualProcessDetail>lambdaQuery()
                .eq(FinancingRepayActualProcessDetail::getPrepareId, prepareId)
        );

    }

}
