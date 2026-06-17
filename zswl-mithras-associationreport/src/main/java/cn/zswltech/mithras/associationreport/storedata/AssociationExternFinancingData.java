package cn.zswltech.mithras.associationreport.storedata;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.zswltech.mithras.associationreport.exception.AssociationReportException;
import cn.zswltech.mithras.associationreport.application.AssociationReportExternalFinancingPort;
import cn.zswltech.mithras.associationreport.application.AssociationReportExternalFinancingSnapshot;
import cn.zswltech.mithras.associationreport.service.AssociationDictionaryService;
import cn.zswltech.mithras.associationreport.service.AssociationExternalFinancingService;
import cn.zswltech.mithras.associationreport.enums.AssociationDictionaryCategoryEnum;
import cn.zswltech.mithras.associationreport.enums.AssociationReportCategoryEnum;
import cn.zswltech.mithras.associationreport.mapper.model.AssociationExternalFinancing;
import cn.zswltech.mithras.associationreport.mapper.model.AssociationReport;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.foundation.util.Util;
import com.baomidou.mybatisplus.extension.service.IService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

/**
 * @date 2025/4/18
 * @description 对外融资清单
 */
@Slf4j
@Component
public class AssociationExternFinancingData extends AbstractDataStore<AssociationExternalFinancing> {
    @Resource
    private AssociationReportExternalFinancingPort externalFinancingPort;

    @Override
    public boolean storeFromSystemJobCheck(int year, int period) {
        // 无需前置数据
        return true;
    }

    @Override
    protected List<AssociationExternalFinancing> parseFromExcel(InputStream inputStream) {
        List<List<Object>> rows = ExcelUtil.getReader(inputStream).read();
        if (CollectionUtil.isEmpty(rows)) {
            return Collections.emptyList();
        }
        log.info("金融局报送【对外融资清单】-Excel解析结果:{}", JSONUtil.toJsonStr(rows));
        // 校验是否符合导入模板
        if (rows.size() < 3) {
            throw new MithrasException("<对外融资清单>导入文件格式有误，请下载系统模板进行导入");
        }
        Object row1col1 = rows.get(0).get(0);
        Object row2col1 = rows.get(1).get(0);
        Object row3col1 = rows.get(2).get(0);
        boolean row1col1check = Objects.nonNull(row1col1) && StrUtil.equals(row1col1.toString(), "融资租赁公司对外融资清单（季报）");
        boolean row2col1check = Objects.nonNull(row2col1) && StrUtil.equals(row2col1.toString(), "填报单位：");
        boolean row3col1check = Objects.nonNull(row3col1) && StrUtil.equals(row3col1.toString(), "序号");
        if (!row1col1check || !row2col1check || !row3col1check) {
            throw new MithrasException("<对外融资清单>导入文件格式有误，请下载系统模板进行导入");
        }
        // 查询一下字典，备用
        Map<String, Map<String, String>> dictNameMap = SpringUtil.getBean(AssociationDictionaryService.class).getDisplay2CodeMap();
        // 开始处理Excel数据
        List<AssociationExternalFinancing> list = new LinkedList<>();
        for (int i = 3; i < rows.size() - 1; i++) {
            List<Object> row = rows.get(i);
            if (row.get(0).toString().contains("备注：")) {
                break;
            }
            try {
                list.add(this.convert(i -2, row, dictNameMap));
            } catch (Exception e) {
                log.error("金融局报送【对外融资清单】-第{}行数据处理异常", (i-2), e);
                throw new MithrasException("数据处理异常");
            }
        }
        return list;
    }

    @Override
    protected List<AssociationExternalFinancing> parseFromSystemData(AssociationReport associationReport) {
        // 确定数据截止日期
        LocalDate targetDate = this.ensureMetricDate(associationReport);
        Map<String, String> dictNameMap = SpringUtil.getBean(AssociationDictionaryService.class)
                .getDisplay2CodeMap()
                .get(AssociationDictionaryCategoryEnum.EVT00052.name());
        List<AssociationReportExternalFinancingSnapshot> snapshots = externalFinancingPort.listExternalFinancing(targetDate);
        if (CollectionUtil.isEmpty(snapshots)) {
            return Collections.emptyList();
        }
        List<AssociationExternalFinancing> result = new LinkedList<>();
        for (AssociationReportExternalFinancingSnapshot snapshot : snapshots) {
            result.add(this.fromSnapshot(snapshot, dictNameMap));
        }
        return result;
    }

    @Override
    protected void check(List<AssociationExternalFinancing> dataList) {
        List<String> errorList = new ArrayList<>();
        dataList.forEach(e -> {
            if (ObjectUtil.isEmpty(e.getOnum())) {
                errorList.add("A列：序号，必填字段");
            }
            if (ObjectUtil.isEmpty(e.getLoanBal())) {
                errorList.add("B列：借款余额，必填字段");
            }
            if (ObjectUtil.isEmpty(e.getFinBusiTypeCode()) || Objects.equals(e.getFinBusiTypeCode(), DICT_UNKNOWN_CODE)) {
                errorList.add("C列：融资业务类型，必填字段,取值字典范围");
            }
            if (ObjectUtil.isEmpty(e.getCptlProv())) {
                errorList.add("D列：资金提供方，必填字段");
            }
            if (ObjectUtil.isEmpty(e.getFinIntr())) {
                errorList.add("E列：融资利率，必填字段");
            }
            if (ObjectUtil.isEmpty(e.getFinLoanDate())) {
                errorList.add("F列：借款日期，必填字段");
            }
            if (ObjectUtil.isEmpty(e.getFinMatuDate())) {
                errorList.add("G列：到期日，必填字段");
            }
        });
        if (CollectionUtil.isNotEmpty(errorList)) {
            throw new AssociationReportException(errorList);
        }
    }

    @Override
    protected IService<AssociationExternalFinancing> serviceBean() {
        return SpringUtil.getBean(AssociationExternalFinancingService.class);
    }

    @Override
    protected AssociationReportCategoryEnum category() {
        return AssociationReportCategoryEnum.J0010;
    }

    private AssociationExternalFinancing convert(int rowNum, List<Object> row, Map<String, Map<String, String>> dictNameMap) {
        AssociationExternalFinancing bean = new AssociationExternalFinancing();
        bean.setRowNum(rowNum);
        bean.setOp("insert");
        bean.setOnum(Optional.ofNullable(row.get(0)).map(Object::toString).orElse(null));
        bean.setLoanBal(parseBigDecimal((row.get(1))));
        //融资类型
        Map<String, String> contractTypeMap = dictNameMap.get(AssociationDictionaryCategoryEnum.EVT00052.name());
        if (ObjectUtil.isNotEmpty(contractTypeMap)) {
            bean.setFinBusiTypeCode(contractTypeMap.get(Optional.ofNullable(row.get(2)).map(Object::toString).orElse(null)));
        } else {
            bean.setFinBusiTypeCode(DICT_UNKNOWN_CODE);
        }
        bean.setCptlProv(Optional.ofNullable(row.get(3)).map(Object::toString).orElse(null));
        bean.setFinIntr(parseBigDecimal(row.get(4)));
        bean.setFinLoanDate(parseDateTime(row.get(5)));
        bean.setFinMatuDate(parseDateTime(row.get(6)));
        return bean;
    }

    private LocalDate parseDateTime(Object o) {
        if (ObjectUtil.isEmpty(o)) {
            return null;
        }
        return DateUtil.parse(o.toString()).toLocalDateTime().toLocalDate();
    }

    private AssociationExternalFinancing fromSnapshot(AssociationReportExternalFinancingSnapshot snapshot, Map<String, String> dictNameMap) {
        AssociationExternalFinancing instance = new AssociationExternalFinancing();
        instance.setLoanBal(Util.millimeterLong2WanBigDecimal(snapshot.getLoanBalance()));
        instance.setFinBusiTypeCode(dictNameMap.get(snapshot.getFinancingBusinessTypeName()));
        instance.setCptlProv(snapshot.getCapitalProvider());
        instance.setFinIntr(snapshot.getFinancingInterestRate());
        instance.setFinLoanDate(snapshot.getFinancingLoanDate());
        instance.setFinMatuDate(snapshot.getFinancingMaturityDate());
        return instance;
    }
}
