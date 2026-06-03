package cn.zswltech.mithras.service.controller.kpi;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.kpi.KpiProjGuessExportApi;
import cn.zswltech.mithras.dto.kpi.*;
import cn.zswltech.mithras.service.constant.GlobalConstants;
import cn.zswltech.mithras.service.enums.JobEnum;
import cn.zswltech.mithras.kpi.enums.KpiProjectClassifyEnum;
import cn.zswltech.mithras.kpi.enums.KpiProjectSourceDistributionEnum;
import cn.zswltech.mithras.service.excel.exporter.kpi.*;
import cn.zswltech.mithras.service.excel.model.kpi.*;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.SysUserService;
import cn.zswltech.mithras.service.service.kpi.KpiProjGuessBaseInfoService;
import cn.zswltech.mithras.service.util.LongUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName KpiProjGuessExportController
 * @Description TODO
 * @Author jackerhe
 * @Date 2023/7/4 2:03 下午
 * @Version 1.0
 **/
@RestController
@Slf4j
public class KpiProjGuessExportController implements KpiProjGuessExportApi {

    @Resource
    private HttpServletResponse httpServletResponse;
    @Resource
    private KpiProjGuessBaseInfoService kpiProjGuessBaseInfoService;

    @Resource
    private KpiProjGuessContractListExcelExporter kpiProjGuessContractListExcelExporter;
    @Resource
    private KpiProjGuessTimeListExcelExporter kpiProjGuessTimeListExcelExporter;
    @Resource
    private KpiProjGuessDeptListExcelExporter kpiProjGuessDeptListExcelExporter;
    @Resource
    private KpiProjGuessPeopleListExcelExporter kpiProjGuessPeopleListExcelExporter;
    @Resource
    private KpiProjGuessContractDetailExcelExporter kpiProjGuessContractDetailExcelExporter;
    @Resource
    private KpiProjGuessPeopleDetailExcelExporter kpiProjGuessPeopleDetailExcelExporter;
    @Resource
    private SysUserService sysUserService;

    @Override
    public R<Void> contractList(@Valid KpiProjGuessIndexREQ req) {
        try {
            PageR<KpiProjGuessContractIndexRSP> kpiProjGuessContractIndexRSPPageR = kpiProjGuessBaseInfoService.contractList(req);
            List<KpiProjGuessContractIndexRSP> list = kpiProjGuessContractIndexRSPPageR.getList();
            if(ObjectUtil.isNotEmpty(list)){
                List<KpiProjGuessContractListExcelModel> kpiProjGuessContractListExcelModels = new ArrayList<>();
                list.forEach(base -> {
                    KpiProjGuessContractListExcelModel kpiProjGuessContractListExcelModel = BeanUtil.copyProperties(base, KpiProjGuessContractListExcelModel.class);
                    KpiProjectSourceDistributionEnum kpiProjectSourceDistributionEnum = KpiProjectSourceDistributionEnum.find(base.getProjSource());
                    kpiProjGuessContractListExcelModel.setProjSource(kpiProjectSourceDistributionEnum != null ?
                            kpiProjectSourceDistributionEnum.display() : null);
                    KpiProjectClassifyEnum kpiProjectClassifyEnum = KpiProjectClassifyEnum.find(base.getProjClassify());
                    kpiProjGuessContractListExcelModel.setProjClassify(kpiProjectClassifyEnum != null ?
                            kpiProjectClassifyEnum.display() : null);
                    kpiProjGuessContractListExcelModel.setProfitCurrent(getNumString(base.getProfitCurrent()));
                    kpiProjGuessContractListExcelModel.setProfitTotal(getNumString(base.getProfitTotal()));
                    kpiProjGuessContractListExcelModel.setBonusCurrent(getNumString(base.getBonusCurrent()));
                    kpiProjGuessContractListExcelModel.setBonusTotal(getNumString(base.getBonusTotal()));
                    kpiProjGuessContractListExcelModels.add(kpiProjGuessContractListExcelModel);
                });
                httpServletResponse.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
                httpServletResponse.setHeader("Content-Disposition",
                        "attachment;filename=" + URLEncoder.encode("绩效测算-合同维度列表" + GlobalConstants.OFFICE_EXCEL_SUFFIX,
                                StandardCharsets.UTF_8.name()));
                kpiProjGuessContractListExcelExporter.exportExcel(kpiProjGuessContractListExcelModels, httpServletResponse.getOutputStream());
            }
        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            log.error("导出绩效测算-合同维度列表发生未知异常[req: {}]", JSONUtil.toJsonStr(req), e);
            throw new MithrasException("绩效测算-合同维度列表发生未知异常");
        }
        return R.ok();
    }

    @Override
    public R<Void> timeList(@Valid KpiProjGuessIndexREQ req) {
        try {
            List<KpiProjGuessContractIndexRSP> rsps = kpiProjGuessBaseInfoService.timeList(req);
            if (ObjectUtil.isNotEmpty(rsps)) {
                List<KpiProjGuessTimeListExcelModel> models = new ArrayList<>();
                rsps.forEach(base -> {
                    KpiProjGuessTimeListExcelModel model = BeanUtil.copyProperties(base, KpiProjGuessTimeListExcelModel.class);
                    model.setProfitCurrent(getNumString(base.getProfitCurrent()));
                    model.setProfitTotal(getNumString(base.getProfitTotal()));
                    model.setBonusCurrent(getNumString(base.getBonusCurrent()));
                    model.setBonusTotal(getNumString(base.getBonusTotal()));
                    models.add(model);
                });
                httpServletResponse.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
                httpServletResponse.setHeader("Content-Disposition",
                        "attachment;filename=" + URLEncoder.encode("绩效测算-时间维度列表" + GlobalConstants.OFFICE_EXCEL_SUFFIX,
                                StandardCharsets.UTF_8.name()));
                kpiProjGuessTimeListExcelExporter.exportExcel(models, httpServletResponse.getOutputStream());
            }
        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            log.error("导出绩效测算-时间维度列表发生未知异常[req: {}]", JSONUtil.toJsonStr(req), e);
            throw new MithrasException("导出时间维度列表发生未知异常");
        }
        return R.ok();
    }

    @Override
    public R<Void> deptList(@Valid KpiProjGuessIndexREQ req) {
        try {
            List<KpiProjGuessContractIndexRSP> rsps = kpiProjGuessBaseInfoService.deptList(req);
            if (ObjectUtil.isNotEmpty(rsps)) {
                List<KpiProjGuessDeptListExcelModel> models = new ArrayList<>();
                rsps.forEach(base -> {
                    KpiProjGuessDeptListExcelModel model = BeanUtil.copyProperties(base, KpiProjGuessDeptListExcelModel.class);
                    model.setProfitCurrent(getNumString(base.getProfitCurrent()));
                    model.setProfitTotal(getNumString(base.getProfitTotal()));
                    model.setBonusCurrent(getNumString(base.getBonusCurrent()));
                    model.setBonusTotal(getNumString(base.getBonusTotal()));
                    models.add(model);
                });
                httpServletResponse.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
                httpServletResponse.setHeader("Content-Disposition",
                        "attachment;filename=" + URLEncoder.encode("绩效测算-部门维度列表" + GlobalConstants.OFFICE_EXCEL_SUFFIX,
                                StandardCharsets.UTF_8.name()));
                kpiProjGuessDeptListExcelExporter.exportExcel(models, httpServletResponse.getOutputStream());
            }
        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            log.error("导出绩效测算-部门维度列表发生未知异常[req: {}]", JSONUtil.toJsonStr(req), e);
            throw new MithrasException("导出部门维度列表发生未知异常");
        }
        return R.ok();
    }

    @Override
    public R<Void> peopleList(@Valid KpiProjGuessIndexREQ req) {
        try {
            List<KpiProjGuessPeopleIndexRSP> rsps = kpiProjGuessBaseInfoService.peopleList(req);
            if (ObjectUtil.isNotEmpty(rsps)) {
                List<KpiProjGuessPeopleListExcelModel> models = new ArrayList<>();
                rsps.forEach(base -> {
                    KpiProjGuessPeopleListExcelModel model = BeanUtil.copyProperties(base, KpiProjGuessPeopleListExcelModel.class);
                    model.setProfitCurrent(getNumString(base.getProfitCurrent()));
                    model.setProfitTotal(getNumString(base.getProfitTotal()));
                    model.setBonusCurrent(getNumString(base.getBonusCurrent()));
                    model.setBonusTotal(getNumString(base.getBonusTotal()));
                    models.add(model);
                });
                httpServletResponse.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
                httpServletResponse.setHeader("Content-Disposition",
                        "attachment;filename=" + URLEncoder.encode("绩效测算-人员维度列表" + GlobalConstants.OFFICE_EXCEL_SUFFIX,
                                StandardCharsets.UTF_8.name()));
                kpiProjGuessPeopleListExcelExporter.exportExcel(models, httpServletResponse.getOutputStream());
            }
        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            log.error("导出绩效测算-人员维度列表发生未知异常[req: {}]", JSONUtil.toJsonStr(req), e);
            throw new MithrasException("导出人员维度列表发生未知异常");
        }
        return R.ok();
    }

    @Override
    public R<Void> contractDetail(@Valid KpiProjGuessDetailREQ req) {
        try {
            req.setPage(1);
            req.setPageSize(Integer.MAX_VALUE);
            PageR<KpiProjGuessDetailRSP> pageR = kpiProjGuessBaseInfoService.contractDetail(req);
            List<KpiProjGuessDetailRSP> rsps = pageR.getList();
            if (ObjectUtil.isNotEmpty(rsps)) {
                List<KpiProjGuessContractDetailExcelModel> models = new ArrayList<>();
                rsps.forEach(base -> {
                    if(ObjectUtil.isNotEmpty(base.getWeightInfoList())){
                        base.getWeightInfoList().forEach(weightInfo -> {
                            KpiProjGuessContractDetailExcelModel model = BeanUtil.copyProperties(base, KpiProjGuessContractDetailExcelModel.class);
                            KpiProjectSourceDistributionEnum kpiProjectSourceDistributionEnum = KpiProjectSourceDistributionEnum.find(base.getProjSource());
                            model.setProjSource(kpiProjectSourceDistributionEnum != null ? kpiProjectSourceDistributionEnum.display() : null);
                            KpiProjectClassifyEnum kpiProjectClassifyEnum = KpiProjectClassifyEnum.find(base.getProjClassify());
                            model.setProjClassify(kpiProjectClassifyEnum != null ? kpiProjectClassifyEnum.display() : null);
                            model.setProfitCurrent(getNumString(base.getProfitCurrent()));
                            model.setProfitTotal(getNumString(base.getProfitTotal()));
                            model.setAwardRatio(getNumString(base.getAwardRatio()));

                            model.setDivideTypeName(weightInfo.getDivideTypeName());
                            model.setDivideTargetName(weightInfo.getDivideTargetName());
                            model.setProfitShareCurrent(LongUtil.tenThousand2Dollar(String.valueOf(weightInfo.getProfitCurrent())).setScale(2, RoundingMode.HALF_UP).toPlainString());
                            model.setProfitShareTotal(LongUtil.tenThousand2Dollar(String.valueOf(weightInfo.getProfitTotal())).setScale(2, RoundingMode.HALF_UP).toPlainString());
                            model.setBonusCurrent(LongUtil.tenThousand2Dollar(String.valueOf(weightInfo.getBonusCurrent())).setScale(2, RoundingMode.HALF_UP).toPlainString());
                            model.setBonusTotal(LongUtil.tenThousand2Dollar(String.valueOf(weightInfo.getBonusTotal())).setScale(2, RoundingMode.HALF_UP).toPlainString());
                            model.setWeightValue(LongUtil.tenThousand2Dollar(String.valueOf(weightInfo.getWeightValue())).setScale(2, RoundingMode.HALF_UP).toPlainString());
                            models.add(model);
                        });
                    }
                });
                httpServletResponse.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
                httpServletResponse.setHeader("Content-Disposition",
                        "attachment;filename=" + URLEncoder.encode("绩效测算详情表" + GlobalConstants.OFFICE_EXCEL_SUFFIX,
                                StandardCharsets.UTF_8.name()));
                kpiProjGuessContractDetailExcelExporter.exportExcel(models, httpServletResponse.getOutputStream());
            }
        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            log.error("导出绩效测算详情表发生未知异常[req: {}]", JSONUtil.toJsonStr(req), e);
            throw new MithrasException("导出绩效测算详情表发生未知异常");
        }
        return R.ok();
    }

    @Override
    public R<Void> peopleDetail(@Valid KpiProjGuessDetailREQ req) {
        try {
            req.setPage(1);
            req.setPageSize(Integer.MAX_VALUE);
            PageR<KpiProjGuessPeopleDetailRSP> kpiProjGuessPeopleDetailRSPPageR = kpiProjGuessBaseInfoService.peopleDetail(req);
            List<KpiProjGuessPeopleDetailRSP> rsps = kpiProjGuessPeopleDetailRSPPageR.getList();
            if (ObjectUtil.isNotEmpty(rsps)) {
                List<KpiProjGuessPeopleDetailExcelModel> models = new ArrayList<>();
                boolean xmjl = sysUserService.currentUserIsSpecificJob(JobEnum.projmanager.name());
                rsps.forEach(base -> {
                    KpiProjGuessPeopleDetailExcelModel model = BeanUtil.copyProperties(base, KpiProjGuessPeopleDetailExcelModel.class);
                    if(xmjl){
                        model.setBonusCurrent(null);
                        model.setBonusTotal(null);
                    }else {
                        model.setBonusCurrent(getNumString(base.getBonusCurrent()));
                        model.setBonusTotal(getNumString(base.getBonusTotal()));
                    }
                    models.add(model);
                });
                httpServletResponse.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
                httpServletResponse.setHeader("Content-Disposition",
                        "attachment;filename=" + URLEncoder.encode("绩效测算详情表" + GlobalConstants.OFFICE_EXCEL_SUFFIX,
                                StandardCharsets.UTF_8.name()));
                kpiProjGuessPeopleDetailExcelExporter.exportExcel(models, httpServletResponse.getOutputStream());
            }
        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            log.error("导出绩效测算详情表-人员维度发生未知异常[req: {}]", JSONUtil.toJsonStr(req), e);
            throw new MithrasException("导出绩效测算详情表发生未知异常");
        }
        return R.ok();
    }

    private String getNumString(Long num){
        return LongUtil.tenThousand2Dollar(String.valueOf(num)).setScale(2, RoundingMode.HALF_UP).toPlainString();
    }
}
