package cn.zswltech.mithras.service.job;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.zswltech.mithras.contract.mapper.contract.ContractBaseInfoMapper;
import cn.zswltech.mithras.assetclassify.application.AssetClassifyClientRiskFactorTemplateService;
import cn.zswltech.mithras.system.service.Id2NameService;
import cn.zswltech.mithras.afterlease.application.RentCollectionDetailService;
import cn.zswltech.mithras.service.service.assetclassify.*;
import cn.zswltech.mithras.service.service.client.ClientService;
import cn.zswltech.mithras.service.service.client.CorpCommerceInfoService;
import cn.zswltech.mithras.service.service.collection.CollectionBaseInfoService;
import cn.zswltech.mithras.service.service.collection.CollectionService;
import cn.zswltech.mithras.service.service.contract.ContractBaseInfoService;
import cn.zswltech.mithras.service.service.contract.ContractReceiptService;
import cn.zswltech.mithras.service.service.contract.ContractRentActualService;
import cn.zswltech.mithras.kpi.service.KpiParameterConfigService;
import cn.zswltech.mithras.assetclassify.application.lib.AssetClassifyLibVersionService;
import cn.zswltech.mithras.service.service.payment.PaymentActualDetailService;
import cn.zswltech.mithras.service.service.payment.PaymentBaseInfoService;
import cn.zswltech.mithras.service.util.DateUtil;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author dingqi
 * @date 2023/1/3
 * @description 资产五级分类任务
 */
@Slf4j
@Component
public class AssetClassifyJob {
    @Resource
    private ContractRentActualService contractRentActualService;
    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private ClientService clientService;
    @Resource
    private AssetClassifyService assetClassifyService;
    @Resource
    private AssetClassifyClientService assetClassifyClientService;
    @Resource
    private AssetClassifyNodeRecordService assetClassifyNodeRecordService;
    @Resource
    private PaymentBaseInfoService paymentBaseInfoService;
    @Resource
    private PaymentActualDetailService paymentActualDetailService;
    @Resource
    private CorpCommerceInfoService corpCommerceInfoService;
    @Resource
    private CollectionBaseInfoService collectionBaseInfoService;
    @Resource
    private CollectionService collectionService;
    @Resource
    private RentCollectionDetailService rentCollectionDetailService;
    @Resource
    private Id2NameService id2NameService;
    @Resource
    private AssetClassifyLibVersionService assetClassifyLibVersionService;
    @Resource
    private ContractBaseInfoMapper contractBaseInfoMapper;
    @Resource
    private AssetClassifyClientRiskFactorService assetClassifyClientRiskFactorService;
    @Resource
    private AssetClassifyClientRiskFactorTemplateService assetClassifyClientRiskFactorTemplateService;
    @Resource
    private ContractReceiptService contractReceiptService;
    @Resource
    private KpiParameterConfigService kpiParameterConfigService;

    @XxlJob("assetClassifyReviewAutoPass")
    public void reviewAutoPass() {
        // TODO 临时从参数取，后续补全跑批任务逻辑
        String jobParam = XxlJobHelper.getJobParam();
//        String jobParam = "3662514,3658068,3658025,3655577,3657868,3655278,3652606,3655144,3645080,3645037,3647797,3647754,3647711,3647668,3647625,3647582,3647539,3642453,3642410,3642367,3642324,3642281,3642238,3642152,3642109,3642066,3641894";
        if (StrUtil.isBlank(jobParam)) {
            return;
        }
        String[] processInstanceIds = jobParam.split(",");
        for (String processInstanceId : processInstanceIds) {
            try {
                SpringUtil.getBean(AssetReviewExpirationListener.class).invoke(processInstanceId);
            } catch (Exception e) {
                log.error("资产五级分类-自动通过复核流程任务发生异常[{}]", processInstanceId, e);
            }
        }
    }

    @Transactional(rollbackFor = Throwable.class)
    @XxlJob("assetClassifyInit")
    public void init(){
        LocalDateTime targetDateTime;
        String jobParam = XxlJobHelper.getJobParam();
        if (StrUtil.isBlank(jobParam)) {
            targetDateTime = LocalDateTime.now();
        } else {
            targetDateTime = LocalDateTimeUtil.parse(jobParam, DatePattern.NORM_DATETIME_PATTERN);
        }
        // 保存资产五级分类主表
        int[] yearQuarter = DateUtil.ensureLastYearQuarter(targetDateTime);
        int year = yearQuarter[0];
        int quarter = yearQuarter[1];
        // 校验是否已有数据
        boolean exist = assetClassifyService.exist(year, quarter);
        if (exist) {
            log.info("{}年{}季度已经存在数据，不再生成数据", year, quarter);
            return;
        }
        assetClassifyService.init(year, quarter);
    }

    @Transactional(rollbackFor = Throwable.class)
    @XxlJob("assetClassifyWeekdayRemind")
    public void weekdayRemind(){
        String param = null;
        Integer day = 0;
        try {
            param = XxlJobHelper.getJobParam();
            day = Integer.valueOf(param);
        }catch (Exception e){
            log.warn("assetClassifyWeekdayRemind get day error param : {}", param, e);
        }
        log.info("job assetClassifyInit began day {}", param);
        assetClassifyService.weekdayRemind(day);
        log.info("job assetClassifyInit over");
    }


}
