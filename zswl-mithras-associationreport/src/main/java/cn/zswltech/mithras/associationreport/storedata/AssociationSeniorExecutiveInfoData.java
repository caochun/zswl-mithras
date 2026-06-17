package cn.zswltech.mithras.associationreport.storedata;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.zswltech.mithras.associationreport.exception.AssociationReportException;
import cn.zswltech.mithras.associationreport.service.AssociationDictionaryService;
import cn.zswltech.mithras.associationreport.service.AssociationSeniorExecutiveInfoService;
import cn.zswltech.mithras.associationreport.enums.AssociationDictionaryCategoryEnum;
import cn.zswltech.mithras.associationreport.enums.AssociationReportCategoryEnum;
import cn.zswltech.mithras.associationreport.mapper.model.AssociationSeniorExecutiveInfo;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import com.baomidou.mybatisplus.extension.service.IService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.*;

/**
 * @date 2025/8/26
 * @description 高管信息一览表
 */
@Slf4j
@Component
public class AssociationSeniorExecutiveInfoData extends AbstractDataStore<AssociationSeniorExecutiveInfo> {
    @Override
    protected List<AssociationSeniorExecutiveInfo> parseFromExcel(InputStream inputStream) {
        List<List<Object>> rows = ExcelUtil.getReader(inputStream).read();
        if (CollectionUtil.isEmpty(rows)) {
            return Collections.emptyList();
        }
        log.info("金融局报送【高管信息一览表】-Excel解析结果:{}", JSONUtil.toJsonStr(rows));
        // 校验是否符合导入模板
        if (rows.size() < 3) {
            throw new MithrasException("<高管信息一览表>导入文件格式有误，请下载系统模板进行导入");
        }
        Object row1col1 = rows.get(0).get(0);
        Object row2col1 = rows.get(1).get(0);
        Object row3col1 = rows.get(2).get(0);
        boolean row1col1check = Objects.nonNull(row1col1) && StrUtil.equals(row1col1.toString(), "高管信息一览表");
        boolean row2col1check = Objects.nonNull(row2col1) && StrUtil.equals(row2col1.toString(), "填报单位：");
        boolean row3col1check = Objects.nonNull(row3col1) && StrUtil.equals(row3col1.toString(), "序号");
        if (!row1col1check || !row2col1check || !row3col1check) {
            throw new MithrasException("<高管信息一览表>导入文件格式有误，请下载系统模板进行导入");
        }
        // 查询一下字典，备用
        Map<String, Map<String, String>> dictNameMap = SpringUtil.getBean(AssociationDictionaryService.class).getDisplay2CodeMap();
        // 开始处理Excel数据
        List<AssociationSeniorExecutiveInfo> list = new LinkedList<>();
        for (int i = 3; i < rows.size(); i++) {
            List<Object> row = rows.get(i);
            try {
                list.add(this.convert(i-2, row, dictNameMap));
            } catch (Exception e) {
                log.error("金融局报送【高管信息一览表】-第{}行数据处理异常", (i-2), e);
                throw new MithrasException("数据处理发生异常");
            }
        }
        return list;
    }

    @Override
    protected void check(List<AssociationSeniorExecutiveInfo> dataList) {
        List<String> errorList = new ArrayList<>();
        dataList.forEach(e -> {
            if (Objects.equals(e.getCurrDutyCode(), DICT_UNKNOWN_CODE)) {
                errorList.add("D列：现任职务，取值字典范围");
            }

            if (Objects.equals(e.getHighEduCode(), DICT_UNKNOWN_CODE)) {
                errorList.add("G列：最高学历，取值字典范围");
            }

        });
        if (CollectionUtil.isNotEmpty(errorList)) {
            throw new AssociationReportException(errorList);
        }
    }

    @Override
    protected IService<AssociationSeniorExecutiveInfo> serviceBean() {
        return SpringUtil.getBean(AssociationSeniorExecutiveInfoService.class);
    }

    @Override
    protected AssociationReportCategoryEnum category() {
        return AssociationReportCategoryEnum.J0004;
    }

    private AssociationSeniorExecutiveInfo convert(int rowNum, List<Object> row, Map<String, Map<String, String>> dictNameMap) {
        AssociationSeniorExecutiveInfo bean = new AssociationSeniorExecutiveInfo();
        bean.setRowNum(rowNum);
        // 序号
        bean.setOnum(Optional.ofNullable(row.get(0)).map(Object::toString).orElse(null));
        bean.setName(Optional.ofNullable(row.get(1)).map(Object::toString).orElse(null));// 姓名
        bean.setCertNum(Optional.ofNullable(row.get(2)).map(Object::toString).orElse(null)); // 证件号码
        // 现任职务
        if (row.get(3) != null && StrUtil.isNotBlank(row.get(3).toString())) {
            Map<String, String> contractTypeMap = dictNameMap.get(AssociationDictionaryCategoryEnum.PUB00247.name());
            if (Objects.nonNull(contractTypeMap)) {
                bean.setCurrDutyCode(Optional.ofNullable(contractTypeMap.get(row.get(3).toString())).orElse(DICT_UNKNOWN_CODE));
            } else {
                bean.setCurrDutyCode(DICT_UNKNOWN_CODE);
            }
        }
        bean.setAoffTime(Optional.ofNullable(row.get(4)).filter(e -> StrUtil.isNotBlank(e.toString())).map(e -> LocalDateTimeUtil.parseDate(e.toString().substring(0, 10), DatePattern.NORM_DATE_PATTERN)).orElse(null));//任职时间
        bean.setAprvFileNum(Optional.ofNullable(row.get(5)).map(Object::toString).orElse(null));//批复文号
        // 最高学历
        if (row.get(6) != null && StrUtil.isNotBlank(row.get(6).toString())) {
            Map<String, String> contractTypeMap = dictNameMap.get(AssociationDictionaryCategoryEnum.DIMLS803.name());
            if (Objects.nonNull(contractTypeMap)) {
                bean.setHighEduCode(Optional.ofNullable(contractTypeMap.get(row.get(6).toString())).orElse(DICT_UNKNOWN_CODE));
            } else {
                bean.setHighEduCode(DICT_UNKNOWN_CODE);
            }
        }
        bean.setGradScho(Optional.ofNullable(row.get(7)).map(Object::toString).orElse(null));//毕业院校
        bean.setSpjt(Optional.ofNullable(row.get(8)).map(Object::toString).orElse(null));// 就读专业
        bean.setHaveFinlTime(Optional.ofNullable(row.get(9)).map(Object::toString).orElse(null));//从事金融/经济工作时间
        bean.setContTel(Optional.ofNullable(row.get(10)).map(Object::toString).orElse(null));// 联系电话
        return bean;
    }
}
