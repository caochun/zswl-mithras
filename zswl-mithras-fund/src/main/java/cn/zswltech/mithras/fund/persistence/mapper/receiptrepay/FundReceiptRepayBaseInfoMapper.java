package cn.zswltech.mithras.fund.persistence.mapper.receiptrepay;

import cn.zswltech.mithras.fund.application.receiptrepay.dto.FundPlanFlowQueryDTO;
import cn.zswltech.mithras.fund.application.receiptrepay.dto.FundPlanFlowResultDTO;
import cn.zswltech.mithras.fund.application.receiptrepay.dto.FundReceiptBaseRichDTO;
import cn.zswltech.mithras.fund.application.receiptrepay.dto.FundReceiptBaseRichQueryDTO;
import cn.zswltech.mithras.fund.persistence.model.receiptrepay.FundReceiptRepayBaseInfo;
import cn.zswltech.mithras.foundation.persistence.plugin.CustomBaseMapper;
import cn.zswltech.mithras.fund.application.receiptrepay.FundReceiptRepayListQueryDto;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

/**
 * @author zhaozhengkang
 * @description 收付款
 * @date 2023-02-20
 */
public interface FundReceiptRepayBaseInfoMapper extends CustomBaseMapper<FundReceiptRepayBaseInfo> {

    Page<FundReceiptRepayBaseInfo> advancedList(Page<FundReceiptRepayBaseInfo> page,
                                                @Param("dto") FundReceiptRepayListQueryDto dto);

    Page<FundPlanFlowResultDTO> collectPageList(@Param("page") Page<FundReceiptRepayBaseInfo> page, @Param("query") FundPlanFlowQueryDTO query);

    Page<FundPlanFlowResultDTO> paymentPageList(@Param("page") Page<FundReceiptRepayBaseInfo> page, @Param("query") FundPlanFlowQueryDTO query);

    Page<FundReceiptBaseRichDTO> richInfoPageList(@Param("page") Page<FundReceiptRepayBaseInfo> page, @Param("query") FundReceiptBaseRichQueryDTO query);
}