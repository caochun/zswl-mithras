package cn.zswltech.mithras.service.service.fund.receiptrepay;
import cn.zswltech.mithras.api.common.PageR;
import javax.annotation.Resource;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.zswltech.mithras.service.constant.ResultMsg;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.dto.fund.receiptrepay.FundReceiptRepayPlanAddREQ;
import cn.zswltech.mithras.dto.fund.receiptrepay.FundReceiptRepayPlanModifyREQ;
import cn.zswltech.mithras.dto.fund.receiptrepay.FundReceiptRepayPlanListREQ;
import cn.zswltech.mithras.dto.fund.receiptrepay.FundReceiptRepayPlanListRSP;
import cn.zswltech.mithras.dto.fund.receiptrepay.FundReceiptRepayPlanRemoveREQ;
import cn.zswltech.mithras.service.mapper.fund.receiptrepay.FundReceiptRepayPlanMapper;
import cn.zswltech.mithras.service.mapper.model.fund.receiptrepay.FundReceiptRepayPlan;

/**
* @description 还款计划
* @author zhaozhengkang
* @date 2023-02-20
*/
@Service
public class FundReceiptRepayPlanService extends ServiceImpl<FundReceiptRepayPlanMapper, FundReceiptRepayPlan> {

}