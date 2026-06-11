package cn.zswltech.mithras.contract.gendoc;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.flow.core.api.FlowTaskApiService;
import cn.zswltech.flow.core.domain.req.task.ProcessPageReq;
import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.flow.core.util.Page;
import cn.zswltech.mithras.foundation.constant.GlobalConstants;
import cn.zswltech.mithras.contract.enums.contract.RepayRateEnum;
import cn.zswltech.mithras.customer.mapper.model.client.CorpAddressInfo;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

import static cn.hutool.core.util.NumberUtil.div;
import static cn.zswltech.mithras.foundation.constant.GlobalConstants.MONEY_MULTIPLE;
import static java.math.RoundingMode.HALF_UP;

/**
 * @author dingqi
 * @date 2022/8/17
 * @description
 */
public abstract class AbstractBasicRender<T> implements IDocumentRender<T> {
    @Resource
    protected BusinessDataRepository businessDataRepository;
    @Resource
    protected FlowTaskApiService flowTaskApiService;

    protected ProcessResp getLatestProcess(List<String> moduleKeyList, List<String> bizKeyList, List<Integer> processStatusList) {
        ProcessPageReq processPageReq = new ProcessPageReq();
        processPageReq.setPageIndex(1);
        processPageReq.setPageSize(1);
        processPageReq.setModelKeyList(moduleKeyList);
        processPageReq.setBusinessKeyList(bizKeyList);
        processPageReq.setProcessStatusList(processStatusList);
        processPageReq.setSortType(1);
        Page<ProcessResp> processRespPage = flowTaskApiService.queryProcess(processPageReq);
        if (CollectionUtil.isNotEmpty(processRespPage.getContents())) {
            return processRespPage.getContents().get(0);
        } else {
            return null;
        }
    }

    /**
     * 计算 租金支付 T + N 中的N
     *
     * @param repayRate  还款频率
     * @param monthCount 租赁期限
     * @param repayTimes 还款期数
     * @return N的值
     */
    protected Integer calculateN(String repayRate, Integer monthCount, Integer repayTimes) {
        if (Objects.equals(repayRate, RepayRateEnum.NON_STAGES.name()) || Objects.equals(repayRate, RepayRateEnum.LRREGULAR.name())) {
            // 非分期 或者 不规则分期 直接返回空
            return null;
        }
        if (Objects.isNull(monthCount) || Objects.isNull(repayTimes)) {
            return null;
        }
        int remainder = monthCount % repayTimes;
        if (remainder > 0) {
            // 除不尽返回空
            return null;
        }
        return monthCount / repayTimes;
    }

    /**
     * 拼接地址信息
     *
     * @param corpAddressInfo 地址数据
     * @return 省 + 市 + 区 + 详情
     */
    protected String appendAddress(CorpAddressInfo corpAddressInfo) {
        StringBuilder stringBuilder = new StringBuilder();
        //判空
        String province = ObjectUtil.isEmpty(corpAddressInfo.getProvince()) ? null : businessDataRepository.getAddressNameFromLocalCache(corpAddressInfo.getProvince());
        String city = ObjectUtil.isEmpty(corpAddressInfo.getCity()) ? null : businessDataRepository.getAddressNameFromLocalCache(corpAddressInfo.getCity());
        String district = ObjectUtil.isEmpty(corpAddressInfo.getDistrict()) ? null : businessDataRepository.getAddressNameFromLocalCache(corpAddressInfo.getDistrict());
        if (!StringUtils.isEmpty(province)) {
            stringBuilder.append(province);
        }
        if (!StringUtils.isEmpty(city)) {
            stringBuilder.append(city);
        }
        if (!StringUtils.isEmpty(district)) {
            stringBuilder.append(district);
        }
        if (!StringUtils.isEmpty(corpAddressInfo.getDetail())) {
            stringBuilder.append(corpAddressInfo.getDetail());
        }
        return stringBuilder.toString();
    }

    protected String toYuan(Long dbNumber) {
        if (Objects.isNull(dbNumber)) {
            return null;
        }
        BigDecimal bigDecimal = NumberUtil.div(dbNumber.toString(), String.valueOf(Long.parseLong(GlobalConstants.MONEY_MULTIPLE)));
        return NumberUtil.decimalFormat(",##0.00##", bigDecimal);
    }

    protected String percentValue(Integer percent) {
        return div(String.valueOf(percent), MONEY_MULTIPLE).setScale(2, HALF_UP).stripTrailingZeros().toPlainString();
    }

    protected String toYuan2Digit(Long dbNumber) {
        if (Objects.isNull(dbNumber)) {
            return null;
        }
        BigDecimal bigDecimal = NumberUtil.div(dbNumber.toString(), String.valueOf(Long.parseLong(GlobalConstants.MONEY_MULTIPLE)));
        return NumberUtil.decimalFormat(",##0.00##", bigDecimal);
    }

    /**
     * 金额为0时，返回空
     *
     * @param amountString
     * @return
     */
    protected String zeroToBlank(String amountString) {
        return CharSequenceUtil.equalsAny(amountString, "0", "0.0", "0.00") ? "" : amountString;
    }

    protected String toWan(Long dbNumber) {
        if (Objects.isNull(dbNumber)) {
            return null;
        }
        BigDecimal bigDecimal = NumberUtil.div(dbNumber.toString(), String.valueOf(10000 * Long.parseLong(GlobalConstants.MONEY_MULTIPLE)));
        return NumberUtil.decimalFormat(",##0.00######", bigDecimal);
    }

    /**
     * 适配公式1：ROUND(手续费/（1+税率）,2)       -> 传commission和taxRate
     * 适配公式2：ROUND(手续费/（1+税率）*税率,2)   -> 传commission和taxRate和taxRate2
     * @param commission 手续费
     * @param taxRate 税率
     * @param taxRate2 税率2
     * @return
     */
    protected String calculateTaxValue(Long commission,BigDecimal taxRate,BigDecimal taxRate2) {
        if(commission == null || taxRate == null){
            return null;
        }
        BigDecimal tax = NumberUtil.div(commission.toString(), String.valueOf(Long.parseLong(GlobalConstants.MONEY_MULTIPLE)));
        BigDecimal result = tax.divide((new BigDecimal(1).add(taxRate)), HALF_UP);
        if(taxRate2 != null){
            result = result.multiply(taxRate2);
        }
        return NumberUtil.decimalFormat(",##0.00", result);
    }
}
