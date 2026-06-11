package cn.zswltech.mithras.kpi.application.performance;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.kpi.*;
import cn.zswltech.mithras.foundation.constant.ResultMsg;
import cn.zswltech.mithras.kpi.enums.BelongTypeEnum;
import cn.zswltech.mithras.kpi.mapper.PerformanceMainInfoMapper;
import cn.zswltech.mithras.kpi.mapper.model.PerformanceBaseInfo;
import cn.zswltech.mithras.kpi.mapper.model.PerformanceMainInfo;
import cn.zswltech.mithras.kpi.mapper.model.PerformanceRecordInfo;
import cn.zswltech.mithras.kpi.application.performance.KpiPerformanceRecordInfoService;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.system.user.Id2NameService;
import cn.zswltech.mithras.kpi.excel.model.CompanyExcelModel;
import cn.zswltech.mithras.kpi.excel.model.DeptExcelModel;
import cn.zswltech.mithras.kpi.excel.model.PersonalExcelModel;
import cn.zswltech.mithras.kpi.excel.listener.CompanyListener;
import cn.zswltech.mithras.kpi.excel.listener.DepartmentListener;
import cn.zswltech.mithras.kpi.excel.listener.PersonalListener;
import cn.zswltech.mithras.foundation.util.StringUtil;
import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.read.metadata.ReadSheet;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static cn.hutool.extra.spring.SpringUtil.getBean;

/**
 * @author yangxiong
 * @date 2024/6/24/14:58
 * @description
 */
@Slf4j
@Service
public class KpiPerformanceMainInfoService extends ServiceImpl<PerformanceMainInfoMapper, PerformanceMainInfo> {
    @Resource
    private PerformanceMainInfoMapper performanceMainInfoMapper;
    @Resource
    private KpiPerformanceBaseInfoService kpiPerformanceBaseInfoService;
    @Resource
    private KpiPerformanceRecordInfoService kpiPerformanceRecordInfoService;

    @Transactional(rollbackFor = Throwable.class)
    public void add(KpiPerformanceManageAddREQ req) {
        PerformanceMainInfo mainInfo = performanceMainInfoMapper.selectOne(Wrappers.<PerformanceMainInfo>lambdaQuery()
                .eq(PerformanceMainInfo::getYear, req.getYear())
                .last(StringUtil.mysqlLimitOne()));
        if (ObjectUtil.isNotNull(mainInfo)) {
            throw new MithrasException("年度业绩目标需唯一，已创建该年度业绩目标，请在该目标基础上进行修改");
        }
        PerformanceMainInfo performanceMainInfo = new PerformanceMainInfo();
        performanceMainInfo.setYear(req.getYear());
        performanceMainInfo.setStatus(req.getStatus());
        performanceMainInfoMapper.insert(performanceMainInfo);
    }

    public void exportExcel(KpiPerformanceManageExportREQ req, ServletOutputStream outputStream) throws IOException {
        PerformanceMainInfo performanceMainInfo = performanceMainInfoMapper.selectOne(Wrappers.<PerformanceMainInfo>lambdaQuery()
                .eq(PerformanceMainInfo::getYear, req.getYear()));
        if (ObjectUtil.isNull(performanceMainInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        kpiPerformanceBaseInfoService.exportExcel(performanceMainInfo, outputStream);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void importKpiPerformance(KpiPerformanceManageImportREQ req) {
        PerformanceMainInfo performanceMainInfo = performanceMainInfoMapper.selectById(req.getMainId());
        if (ObjectUtil.isNull(performanceMainInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        //不同的sheet页各自处理
        try {
            ExcelReader reader = EasyExcelFactory.read(req.getFile().getInputStream()).build();
            CompanyListener companyListener = getBean(CompanyListener.class);
            companyListener.setMainId(performanceMainInfo.getId());
            companyListener.setYear(performanceMainInfo.getYear());
            ReadSheet company = EasyExcelFactory.readSheet("公司")
                    .head(CompanyExcelModel.class)
                    .registerReadListener(companyListener)
                    .build();
            DepartmentListener departmentListener = getBean(DepartmentListener.class);
            departmentListener.setYear(performanceMainInfo.getYear());
            departmentListener.setMainId(performanceMainInfo.getId());
            ReadSheet department = EasyExcelFactory.readSheet("部门")
                    .head(DeptExcelModel.class)
                    .registerReadListener(departmentListener)
                    .build();
            PersonalListener personalListener = getBean(PersonalListener.class);
            personalListener.setMainId(performanceMainInfo.getId());
            personalListener.setYear(performanceMainInfo.getYear());
            ReadSheet personal = EasyExcelFactory.readSheet("个人")
                    .head(PersonalExcelModel.class)
                    .registerReadListener(personalListener)
                    .build();
            reader.read(company, department, personal);
            reader.finish();
            reader.close();
            //更新数据时间
            PerformanceMainInfo mainInfo = new PerformanceMainInfo();
            mainInfo.setId(performanceMainInfo.getId());
            mainInfo.setUpdateTime(LocalDateTime.now());
            performanceMainInfoMapper.updateById(mainInfo);
        } catch (IOException e) {
            log.error("导入业绩信息数据异常", e);
            throw new MithrasException("导入业绩信息数据异常");
        }
    }

    @Transactional(rollbackFor = Throwable.class)
    public void modifyStatus(KpiPerformanceManageModifyREQ req) {
        PerformanceMainInfo performanceMainInfo = performanceMainInfoMapper.selectById(req.getId());
        if (ObjectUtil.isNull(performanceMainInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        performanceMainInfoMapper.update(null, Wrappers.<PerformanceMainInfo>lambdaUpdate()
                .eq(PerformanceMainInfo::getId, performanceMainInfo.getId())
                .set(PerformanceMainInfo::getStatus, req.getStatus())
                .set(PerformanceMainInfo::getUpdateTime, LocalDateTime.now())
                .set(PerformanceMainInfo::getUpdateBy, AccountUtil.getLoginInfo().getId()));
    }

    public PageR<KpiPerformanceManageMainListRSP> mainList(KpiPerformanceManageMainListREQ req) {
        Page<PerformanceMainInfo> page = new Page<>();
        page.setSize(req.getPageSize());
        page.setCurrent(req.getPage());
        Page<PerformanceMainInfo> mainInfoPage = performanceMainInfoMapper.selectPage(page, null);
        if (CollectionUtils.isEmpty(mainInfoPage.getRecords())) {
            return PageR.empty(req.getPageSize(), req.getPageSize());
        }

        List<KpiPerformanceManageMainListRSP> list = fillInfo(mainInfoPage.getRecords());
        return PageR.of(list, mainInfoPage.getTotal());
    }

    private List<KpiPerformanceManageMainListRSP> fillInfo(List<PerformanceMainInfo> records) {
        //提前查出用户相关数据
        List<Long> userIds = records.stream().map(PerformanceMainInfo::getCreateBy).collect(Collectors.toList());
        userIds.addAll(records.stream().map(PerformanceMainInfo::getUpdateBy).collect(Collectors.toList()));
        Map<Long, String> longStringMap = getBean(Id2NameService.class).sysUserId2Name(userIds);
        return records.stream().map(performanceMainInfo -> {
            KpiPerformanceManageMainListRSP rsp = new KpiPerformanceManageMainListRSP();
            rsp.setId(performanceMainInfo.getId());
            rsp.setYear(performanceMainInfo.getYear());
            rsp.setStatus(performanceMainInfo.getStatus());
            rsp.setCreateBy(performanceMainInfo.getCreateBy());
            rsp.setCreateTime(performanceMainInfo.getCreateTime().format(DateTimeFormatter.ofPattern(DatePattern.NORM_DATETIME_PATTERN)));
            rsp.setUpdateBy(performanceMainInfo.getUpdateBy());
            rsp.setUpdateTime(performanceMainInfo.getUpdateTime().format(DateTimeFormatter.ofPattern(DatePattern.NORM_DATETIME_PATTERN)));
            rsp.setCreateByName(Optional.ofNullable(longStringMap.get(performanceMainInfo.getCreateBy())).orElse(null));
            rsp.setUpdateByName(Optional.ofNullable(longStringMap.get(performanceMainInfo.getUpdateBy())).orElse(null));
            return rsp;
        }).collect(Collectors.toList());
    }

    public KpiPerformanceManageMainDetailRSP mainDetail(KpiPerformanceManageMainDetailREQ req) {
        PerformanceMainInfo performanceMainInfo = performanceMainInfoMapper.selectById(req.getId());
        if (ObjectUtil.isNull(performanceMainInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        KpiPerformanceManageMainDetailRSP rsp = new KpiPerformanceManageMainDetailRSP();
        rsp.setId(performanceMainInfo.getId());
        rsp.setYear(performanceMainInfo.getYear());
        rsp.setStatus(performanceMainInfo.getStatus());
        return rsp;
    }

    public Map<PerformanceBaseInfo, PerformanceRecordInfo> getPerformanceByYearAndMonth(Integer year, Integer month) {
        PerformanceMainInfo mainInfo = performanceMainInfoMapper.selectOne(Wrappers.<PerformanceMainInfo>lambdaQuery()
                .eq(PerformanceMainInfo::getYear, year)
                .last(StringUtil.mysqlLimitOne()));
        if (ObjectUtil.isNull(mainInfo)) {
            return null;
        }
        //查询本年数据
        List<PerformanceBaseInfo> baseInfos = kpiPerformanceBaseInfoService.list(Wrappers.<PerformanceBaseInfo>lambdaQuery()
                .eq(PerformanceBaseInfo::getMainId, mainInfo.getId())
                .eq(PerformanceBaseInfo::getBelongType, BelongTypeEnum.DEPARTMENT.name()));
        //查询对应月份数据

        Map<Long, PerformanceRecordInfo> baseId2RecordMap = kpiPerformanceRecordInfoService.list(Wrappers.<PerformanceRecordInfo>lambdaQuery()
                .in(PerformanceRecordInfo::getPerformanceId, baseInfos.stream().map(PerformanceBaseInfo::getId).collect(Collectors.toList()))
                .eq(PerformanceRecordInfo::getMonth, month)).stream().collect(Collectors.toMap(PerformanceRecordInfo::getPerformanceId, e -> e, (a, b) -> b));
        return baseInfos.stream().collect(Collectors.toMap(e -> e, e -> baseId2RecordMap.get(e.getId()), (a, b) -> a));
    }
}
