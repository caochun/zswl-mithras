package cn.zswltech.mithras.service.controller.basedata;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.NumberUtil;
import cn.zswl.oss.core.OssClient;
import cn.zswltech.mithras.api.basedata.BaseDataLprApi;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.PageReq;
import cn.zswltech.mithras.dto.SingleFileREQ;
import cn.zswltech.mithras.dto.SinglePkREQ;
import cn.zswltech.mithras.dto.basedata.BaseDataLprDetailRSP;
import cn.zswltech.mithras.dto.basedata.BaseDataLprSaveREQ;
import cn.zswltech.mithras.common.constant.GlobalConstants;
import cn.zswltech.mithras.service.convert.CommonConvert;
import cn.zswltech.mithras.service.mapper.model.basedata.BaseDataLpr;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.basedata.BaseDataLprService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author dingqi
 * @date 2022/9/16
 * @description
 */
@Slf4j
@RestController
public class BaseDataLprController implements BaseDataLprApi {
    @Resource
    private BaseDataLprService baseDataLprService;
    @Resource
    private HttpServletResponse httpServletResponse;
    @Resource
    private OssClient ossClient;

    @Override
    public R<Long> save(@Valid BaseDataLprSaveREQ baseDataLprSaveREQ) {
        Assert.isTrue(NumberUtil.isNumber(baseDataLprSaveREQ.getOneYear()), () -> MithrasException.newException("1年期LPR报价必须是数字"));
        Assert.isTrue(NumberUtil.isNumber(baseDataLprSaveREQ.getFiveYear()), () -> MithrasException.newException("5年期LPR报价必须是数字"));
        LocalDate lprDate = LocalDateTimeUtil.parse(baseDataLprSaveREQ.getLprDate(), DatePattern.NORM_DATE_PATTERN).toLocalDate();
        // 前置校验
        boolean isExist = baseDataLprService.isExistByLprDate(lprDate);
        if (isExist) {
            return R.fail("已经存在" + baseDataLprSaveREQ.getLprDate() + "的LPR报价记录");
        }
        BaseDataLpr baseDataLpr = new BaseDataLpr();
        baseDataLpr.setLprDate(lprDate);
        baseDataLpr.setOneYear(baseDataLprSaveREQ.getOneYear());
        baseDataLpr.setFiveYear(baseDataLprSaveREQ.getFiveYear());
        baseDataLprService.saveOrUpdate(baseDataLpr);
        return R.ok(baseDataLpr.getId());
    }

    @Override
    public R<Void> delete(@Valid SinglePkREQ singlePkREQ) {
        baseDataLprService.removeById(singlePkREQ.getId());
        return R.ok();
    }

    @Override
    public R<Void> importExcel(@Valid SingleFileREQ singleFileREQ) {
        try {
            baseDataLprService.importExcel(singleFileREQ.getFile().getInputStream());
        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            log.error("导入LPR数据发生未知异常", e);
            return R.fail("导入LPR数据发生未知异常");
        }
        return R.ok();
    }

    @Override
    public R<PageR<BaseDataLprDetailRSP>> pageList(@Valid PageReq pageReq) {
        Page<BaseDataLpr> page = CommonConvert.toMybatisPlusPage(pageReq);
        LambdaQueryWrapper<BaseDataLpr> query = Wrappers.lambdaQuery();
        query.orderByDesc(BaseDataLpr::getLprDate);
        Page<BaseDataLpr> dbPage = baseDataLprService.page(page, query);
        PageR<BaseDataLprDetailRSP> result = new PageR<>();
        result.setCurrentPage(page.getCurrent());
        result.setPageSize(pageReq.getPageSize());
        result.setTotal(dbPage.getTotal());
        if (CollectionUtil.isEmpty(dbPage.getRecords())) {
            result.setList(Collections.emptyList());
        } else {
            List<BaseDataLprDetailRSP> list = dbPage.getRecords().stream().map(this::toBaseDataLprListRSP).collect(Collectors.toList());
            result.setList(list);
        }
        return R.ok(result);
    }

    @Override
    public R<String> downloadTemplate() {
        try {
            /*httpServletResponse.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(GlobalConstants.TEMPLATE_OSS_BASE_DATA_LPR, StandardCharsets.UTF_8.name()));
            ossClient.downLoad(httpServletResponse.getOutputStream(), GlobalConstants.TEMPLATE_OSS_BASE_DATA_LPR);*/
            return R.ok(ossClient.getPreviewUrl(GlobalConstants.TEMPLATE_OSS_BASE_DATA_LPR, GlobalConstants.FILE_TEMPLATE_EXPIRY));
        } catch (Exception e) {
            log.error("下载LPR基础数据导入模板发生异常", e);
            throw new MithrasException("下载模板发生异常");
        }

    }

    @Override
    public R<BaseDataLprDetailRSP> getLatestLpr() {
        LambdaQueryWrapper<BaseDataLpr> query = Wrappers.lambdaQuery();
        query.lt(BaseDataLpr::getLprDate, LocalDate.now());
        query.orderByDesc(BaseDataLpr::getLprDate);
        query.last("limit 0,1");
        BaseDataLpr latest = baseDataLprService.getOne(query);
        if (Objects.isNull(latest)) {
            return R.ok();
        }
        return R.ok(this.toBaseDataLprListRSP(latest));
    }

    private BaseDataLprDetailRSP toBaseDataLprListRSP(BaseDataLpr baseDataLpr) {
        BaseDataLprDetailRSP rsp = new BaseDataLprDetailRSP();
        rsp.setId(baseDataLpr.getId());
        rsp.setLprDate(LocalDateTimeUtil.format(baseDataLpr.getLprDate(), DatePattern.NORM_DATE_PATTERN));
        rsp.setOneYear(baseDataLpr.getOneYear());
        rsp.setFiveYear(baseDataLpr.getFiveYear());
        return rsp;
    }
}
