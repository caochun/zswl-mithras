package cn.zswltech.mithras.associationreport.storedata;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.zswltech.mithras.associationreport.service.AssociationDictionaryService;
import cn.zswltech.mithras.associationreport.service.AssociationMajorMattersEventReportService;
import cn.zswltech.mithras.associationreport.enums.AssociationReportCategoryEnum;
import cn.zswltech.mithras.associationreport.mapper.model.AssociationMajorMattersEventReport;
import cn.zswltech.mithras.service.others.MithrasException;
import com.baomidou.mybatisplus.extension.service.IService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.*;

/**
 * @date 2025/8/26
 * @description 重大事项报告表-重大事项报告情况
 */
@Slf4j
@Component
public class AssociationMajorMattersEventReportData extends AbstractDataStore<AssociationMajorMattersEventReport> {
    @Override
    protected List<AssociationMajorMattersEventReport> parseFromExcel(InputStream inputStream) {
        List<List<Object>> rows = ExcelUtil.getReader(inputStream).read();
        if (CollectionUtil.isEmpty(rows)) {
            return Collections.emptyList();
        }
        log.info("金融局报送【重大事项报告表-重大事项报告情况】-Excel解析结果:{}", JSONUtil.toJsonStr(rows));
        // 校验是否符合导入模板
        if (rows.size() < 5) {
            throw new MithrasException("<重大事项报告表-重大事项报告情况>导入文件格式有误，请下载系统模板进行导入");
        }
        Object row1col1 = rows.get(0).get(0);
        Object row2col1 = rows.get(1).get(0);
        Object row3col1 = rows.get(2).get(0);
        Object row4col1 = rows.get(3).get(0);
        Object row5col1 = rows.get(4).get(0);
        boolean row1col1check = Objects.nonNull(row1col1) && StrUtil.equals(row1col1.toString(), "浙江省融资租赁公司重大事项报告表");
        boolean row2col1check = Objects.nonNull(row2col1) && StrUtil.equals(row2col1.toString(), "填报单位：");
        boolean row3col1check = Objects.nonNull(row3col1) && StrUtil.equals(row3col1.toString(), "填报人：");
        boolean row4col1check = Objects.nonNull(row4col1) && StrUtil.equals(row4col1.toString(), "重大事项报告情况");
        boolean row5col1check = Objects.nonNull(row5col1) && StrUtil.equals(row5col1.toString(), "事项名称");
        if (!row1col1check || !row2col1check || !row3col1check || !row4col1check || !row5col1check) {
            throw new MithrasException("<重大事项报告表-重大事项报告情况>导入文件格式有误，请下载系统模板进行导入");
        }
        // 查询一下字典，备用
        Map<String, Map<String, String>> dictNameMap = SpringUtil.getBean(AssociationDictionaryService.class).getDisplay2CodeMap();
        // 开始处理Excel数据
        List<AssociationMajorMattersEventReport> list = new LinkedList<>();
        for (int i = 5; i < rows.size(); i++) {
            List<Object> row = rows.get(i);
            try {
                list.add(this.convert(i-4, row, dictNameMap));
            } catch (Exception e) {
                log.error("金融局报送【重大事项报告表-重大事项报告情况】-第{}行数据处理异常", (i-4), e);
                throw new MithrasException("数据处理发生异常");
            }
        }
        return list;
    }

    @Override
    protected void check(List<AssociationMajorMattersEventReport> dataList) {

    }

    @Override
    protected IService<AssociationMajorMattersEventReport> serviceBean() {
        return SpringUtil.getBean(AssociationMajorMattersEventReportService.class);
    }

    @Override
    protected AssociationReportCategoryEnum category() {
        return AssociationReportCategoryEnum.J0015;
    }

    private AssociationMajorMattersEventReport convert(int rowNum, List<Object> row, Map<String, Map<String, String>> dictNameMap) {
        AssociationMajorMattersEventReport bean = new AssociationMajorMattersEventReport();
        bean.setRowNum(rowNum);

        bean.setPiecName(Optional.ofNullable(row.get(0)).map(Object::toString).orElse(null));// 事件名称
        bean.setImprPiecExpl(Optional.ofNullable(row.get(1)).map(Object::toString).orElse(null));// 重大事项说明

        return bean;
    }
}
