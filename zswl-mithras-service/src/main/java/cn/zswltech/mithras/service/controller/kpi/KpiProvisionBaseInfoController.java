package cn.zswltech.mithras.service.controller.kpi;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.kpi.KpiProvisionBaseInfoApi;
import cn.zswltech.mithras.dto.kpi.*;
import cn.zswltech.mithras.service.config.redis.RedisDistLock;
import cn.zswltech.mithras.service.constant.GlobalConstants;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.service.enums.CacheEnum;
import cn.zswltech.mithras.assetclassify.domain.enums.AssetClassifyResultEnum;
import cn.zswltech.mithras.kpi.enums.KpiProvisionStatusEnum;
import cn.zswltech.mithras.kpi.excel.exporter.KpiProvisionBaseInfoDetailExcelExporter;
import cn.zswltech.mithras.kpi.excel.model.KpiProvisionBaseInfoDetailExcelModel;
import cn.zswltech.mithras.kpi.mapper.model.KpiProvisionBaseInfo;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.kpi.KpiProvisionBaseInfoService;
import cn.zswltech.mithras.service.service.kpi.KpiProvisionDetailService;
import cn.zswltech.mithras.service.util.LongUtil;
import cn.zswltech.mithras.service.util.StringUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static cn.zswltech.mithras.service.repository.PlatformApiHandler.log;

/**
 * @author vico
 * @description 绩效-拨备表
 * @date 2023-06-19
 */
@RestController
public class KpiProvisionBaseInfoController implements KpiProvisionBaseInfoApi {

    @Resource
    private KpiProvisionDetailService kpiProvisionDetailService;
    @Resource
    private KpiProvisionBaseInfoService kpiProvisionBaseInfoService;
    @Resource
    private HttpServletResponse httpServletResponse;
    @Resource
    private KpiProvisionBaseInfoDetailExcelExporter kpiProvisionBaseInfoDetailExcelExporter;
    @Resource
    private RedisDistLock redisDistLock;

    @Override
    public R<KpiProvisionBaseInfoAddRSP> add(KpiProvisionBaseInfoAddREQ req) {
        KpiProvisionBaseInfoAddRSP rsp = new KpiProvisionBaseInfoAddRSP();
        rsp.setId(kpiProvisionBaseInfoService.add(req));
        return R.ok(rsp);
    }

    @Override
    public R<Void> refresh(@Valid KpiProvisionBaseInfoDetailREQ req) {
        //不传默认刷新最新月份数据
        if(ObjectUtil.isEmpty(req.getId())) {
            KpiProvisionBaseInfo one = kpiProvisionBaseInfoService.getOne(Wrappers.<KpiProvisionBaseInfo>lambdaQuery()
                    .ne(KpiProvisionBaseInfo::getProvisionStatus, KpiProvisionStatusEnum.EFFECT.name())
                    .orderByDesc(KpiProvisionBaseInfo::getProvisionDate)
                    .last(StringUtil.mysqlLimitOne()));
            if (ObjectUtil.isNotEmpty(one)) {
                req.setId(one.getId());
            }
        }
        KpiProvisionBaseInfo byId = kpiProvisionBaseInfoService.getById(req.getId());
        if (ObjectUtil.isEmpty(byId)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        String lockKey = CacheEnum.KPI_PROVISION_DETAIL_CALCULATE_LOCK.buildKey("ALL");
        boolean getLockFlag = redisDistLock.tryLockWithoutReleaseTime(lockKey, 1000L);
        if (!getLockFlag) {
            throw new MithrasException(ResultMsg.KPI_PROVISION_DETAIL_LOCK);
        }
        try {
            kpiProvisionDetailService.add(byId.getProvisionDate(), byId.getId());
        }finally {
            redisDistLock.unlock(lockKey);
        }
        byId.setProvisionStatus(KpiProvisionStatusEnum.UN_EFFECT.name());
        kpiProvisionBaseInfoService.updateById(byId);
        return R.ok();
    }

    @Override
    public R<Void> effect(@Valid KpiProvisionBaseInfoDetailREQ req) {
        kpiProvisionBaseInfoService.effect(req.getId());
        return R.ok();
    }

    @Override
    public R<Void> modify(KpiProvisionBaseInfoModifyREQ req) {
        kpiProvisionDetailService.modify(req);
        return R.ok();
    }

    @Override
    public R<PageR<KpiProvisionBaseInfoListRSP>> list(KpiProvisionBaseInfoListREQ req) {
        return R.ok(kpiProvisionBaseInfoService.list(req));
    }

    @Override
    public R<KpiProvisionBaseInfoDetailRSP> detail(KpiProvisionBaseInfoDetailREQ req) {
        return R.ok(kpiProvisionDetailService.detail(req));
    }

    @Override
    public R<Void> detailExport(@Valid KpiProvisionBaseInfoDetailExportREQ req) {
        try {
            KpiProvisionBaseInfoDetailREQ kpiProvisionBaseInfoDetailREQ = JSONUtil.toBean(req.getOriginJson(), KpiProvisionBaseInfoDetailREQ.class);
            if ("2".equals(req.getDownloadType())) {
                kpiProvisionBaseInfoDetailREQ.setPage(1);
                kpiProvisionBaseInfoDetailREQ.setPageSize(Integer.MAX_VALUE);
            }
            KpiProvisionBaseInfoDetailRSP detail = kpiProvisionDetailService.detail(kpiProvisionBaseInfoDetailREQ);
            PageR<KpiProvisionBaseInfoDetailRSP.KpiProvisionBaseInfoBody> provisionBaseInfoList = detail.getProvisionBaseInfoList();
            if (ObjectUtil.isNotEmpty(provisionBaseInfoList)) {
                List<KpiProvisionBaseInfoDetailExcelModel> models = new ArrayList<>();
                List<KpiProvisionBaseInfoDetailRSP.KpiProvisionBaseInfoBody> list = provisionBaseInfoList.getList();
                if (ObjectUtil.isNotEmpty(list)) {
                    list.forEach(base -> {
                        KpiProvisionBaseInfoDetailExcelModel kpiProvisionBaseInfoDetailExcelModel = BeanUtil.copyProperties(base, KpiProvisionBaseInfoDetailExcelModel.class);
                        kpiProvisionBaseInfoDetailExcelModel.setRemainingPrincipal(getNumString((base.getRemainingPrincipal())));
                        kpiProvisionBaseInfoDetailExcelModel.setEarnestBalance(getNumString((base.getEarnestBalance())));
                        kpiProvisionBaseInfoDetailExcelModel.setExposure(getNumString((base.getExposure())));
                        //kpiProvisionBaseInfoDetailExcelModel.setWithdrawalRatio(getNumString((base.getWithdrawalRatio())));
                        kpiProvisionBaseInfoDetailExcelModel.setEndDate(base.getEndDate() == null ? null : base.getEndDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                        /*if (ObjectUtil.isNotEmpty(kpiProvisionBaseInfoDetailExcelModel.getWithdrawalRatio())) {
                            kpiProvisionBaseInfoDetailExcelModel.setWithdrawalRatio(kpiProvisionBaseInfoDetailExcelModel.getWithdrawalRatio() + "%");
                        }*/
                        AssetClassifyResultEnum of1 = AssetClassifyResultEnum.of(base.getRiskLevel());
                        if (of1 != null) {
                            kpiProvisionBaseInfoDetailExcelModel.setRiskLevel(of1.display());
                        }
                        kpiProvisionBaseInfoDetailExcelModel.setProfitCurrent(getNumString((base.getProfitCurrent())));
                        kpiProvisionBaseInfoDetailExcelModel.setProfitTotal(getNumString((base.getProfitTotal())));
                        kpiProvisionBaseInfoDetailExcelModel.setBonusCurrent(getNumString((base.getBonusCurrent())));
                        kpiProvisionBaseInfoDetailExcelModel.setAccruedInterest(getNumString(base.getAccruedInterest()));
                        kpiProvisionBaseInfoDetailExcelModel.setNextRent(getNumString(base.getNextRent()));
                        kpiProvisionBaseInfoDetailExcelModel.setRemark(base.getRemark());
                        models.add(kpiProvisionBaseInfoDetailExcelModel);
                    });
                }
                httpServletResponse.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
                httpServletResponse.setHeader("Content-Disposition",
                        "attachment;filename=" + URLEncoder.encode("拨备计提-" + detail.getProvisionDate() + GlobalConstants.OFFICE_EXCEL_SUFFIX,
                                StandardCharsets.UTF_8.name()));
                kpiProvisionBaseInfoDetailExcelExporter.exportExcel(models, httpServletResponse.getOutputStream());
            }
        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            log.error("导出拨备计提发生未知异常[req: {}]", JSONUtil.toJsonStr(req), e);
            throw new MithrasException("导出拨备计提发生未知异常");
        }
        return R.ok();
    }

    private String getNumString(Long num) {
        return LongUtil.tenThousand2Dollar(String.valueOf(num)).setScale(2, RoundingMode.HALF_UP).toPlainString();
    }


}