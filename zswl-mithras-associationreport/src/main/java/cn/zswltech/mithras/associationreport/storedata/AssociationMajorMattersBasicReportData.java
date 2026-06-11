package cn.zswltech.mithras.associationreport.storedata;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.zswltech.mithras.associationreport.service.AssociationDictionaryService;
import cn.zswltech.mithras.associationreport.service.AssociationMajorMattersBasicReportService;
import cn.zswltech.mithras.associationreport.enums.AssociationReportCategoryEnum;
import cn.zswltech.mithras.associationreport.mapper.model.AssociationMajorMattersBasicReport;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import com.baomidou.mybatisplus.extension.service.IService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.*;

/**
 * @date 2025/8/26
 * @description 重大事项报告表-基本信息
 */
@Slf4j
@Component
public class AssociationMajorMattersBasicReportData extends AbstractDataStore<AssociationMajorMattersBasicReport> {
    @Override
    protected List<AssociationMajorMattersBasicReport> parseFromExcel(InputStream inputStream) {
        List<List<Object>> rows = ExcelUtil.getReader(inputStream).read();
        if (CollectionUtil.isEmpty(rows)) {
            return Collections.emptyList();
        }
        log.info("金融局报送【重大事项报告表-基本信息】-Excel解析结果:{}", JSONUtil.toJsonStr(rows));
        // 校验是否符合导入模板
        if (rows.size() < 4) {
            throw new MithrasException("<重大事项报告表-基本信息>导入文件格式有误，请下载系统模板进行导入");
        }
        Object row1col1 = rows.get(0).get(0);
        Object row2col1 = rows.get(1).get(0);
        Object row3col1 = rows.get(2).get(0);
        Object row4col1 = rows.get(3).get(0);
        boolean row1col1check = Objects.nonNull(row1col1) && StrUtil.equals(row1col1.toString(), "浙江省融资租赁公司重大事项报告表");
        boolean row2col1check = Objects.nonNull(row2col1) && StrUtil.equals(row2col1.toString(), "填报单位：");
        boolean row3col1check = Objects.nonNull(row3col1) && StrUtil.equals(row3col1.toString(), "填报人：");
        boolean row4col1check = Objects.nonNull(row4col1) && StrUtil.equals(row4col1.toString(), "融资租赁公司基本信息");
        if (!row1col1check || !row2col1check || !row3col1check || !row4col1check) {
            throw new MithrasException("<重大事项报告表-基本信息>导入文件格式有误，请下载系统模板进行导入");
        }
        // 查询一下字典，备用
        Map<String, Map<String, String>> dictNameMap = SpringUtil.getBean(AssociationDictionaryService.class).getDisplay2CodeMap();
        // 开始处理Excel数据
        List<AssociationMajorMattersBasicReport> list = new LinkedList<>();
        try {
            list.add(this.convert(rows, dictNameMap));
        } catch (Exception e) {
            log.error("金融局报送【重大事项报告表-基本信息】数据处理异常", e);
            throw new MithrasException("数据处理发生异常");
        }
        return list;
    }

    @Override
    protected void check(List<AssociationMajorMattersBasicReport> dataList) {

    }

    @Override
    protected IService<AssociationMajorMattersBasicReport> serviceBean() {
        return SpringUtil.getBean(AssociationMajorMattersBasicReportService.class);
    }

    @Override
    protected AssociationReportCategoryEnum category() {
        return AssociationReportCategoryEnum.J0014;
    }

    private AssociationMajorMattersBasicReport convert(List<List<Object>> excelRows, Map<String, Map<String, String>> dictNameMap) {
        AssociationMajorMattersBasicReport bean = new AssociationMajorMattersBasicReport();
        bean.setRowNum(1);
        bean.setOp("insert");
        bean.setInftContMode(Optional.ofNullable(excelRows.get(2).get(3)).map(Object::toString).orElse(null));//填报人联系方式

        bean.setCorpName(Optional.ofNullable(excelRows.get(4).get(1)).map(Object::toString).orElse(null)); // 企业名称
        bean.setLeglCptl(parseBigDecimal(excelRows.get(4).get(3)));//法定资产(万元)

        bean.setBusiAddr(Optional.ofNullable(excelRows.get(5).get(1)).map(Object::toString).orElse(null));//营业地址
        bean.setCorpLegpName(Optional.ofNullable(excelRows.get(5).get(3)).map(Object::toString).orElse(null));//公司法人名称

        bean.setBrchInsNum(Optional.ofNullable(excelRows.get(6).get(1)).filter(e -> NumberUtil.isNumber(e.toString())).map(e -> Integer.parseInt(e.toString())).orElse(null));//分支机构数量
        bean.setChrmName(Optional.ofNullable(excelRows.get(6).get(3)).map(Object::toString).orElse(null)); // 董事长姓名

        bean.setGmgrName(Optional.ofNullable(excelRows.get(7).get(1)).map(Object::toString).orElse(null)); // 总经理姓名
        bean.setContMode(Optional.ofNullable(excelRows.get(7).get(3)).map(Object::toString).orElse(null));//联系方式

        return bean;
    }

}
