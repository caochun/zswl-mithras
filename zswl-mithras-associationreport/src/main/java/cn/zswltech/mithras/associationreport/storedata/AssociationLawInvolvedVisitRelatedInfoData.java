package cn.zswltech.mithras.associationreport.storedata;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.zswltech.mithras.associationreport.AssociationReportException;
import cn.zswltech.mithras.associationreport.service.AssociationDictionaryService;
import cn.zswltech.mithras.associationreport.service.AssociationLawInvolvedVisitRelatedInfoService;
import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.associationreport.enums.AssociationDictionaryCategoryEnum;
import cn.zswltech.mithras.associationreport.enums.AssociationReportCategoryEnum;
import cn.zswltech.mithras.associationreport.mapper.model.AssociationLawInvolvedVisitRelatedInfo;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import com.baomidou.mybatisplus.extension.service.IService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.*;

/**
 * @date 2025/8/26
 * @description 涉法涉讼涉访信息表
 */
@Slf4j
@Component
public class AssociationLawInvolvedVisitRelatedInfoData extends AbstractDataStore<AssociationLawInvolvedVisitRelatedInfo> {
    @Override
    protected List<AssociationLawInvolvedVisitRelatedInfo> parseFromExcel(InputStream inputStream) {
        List<List<Object>> rows = ExcelUtil.getReader(inputStream).read();
        if (CollectionUtil.isEmpty(rows)) {
            return Collections.emptyList();
        }
        log.info("金融局报送【涉法涉讼涉访信息表】-Excel解析结果:{}", JSONUtil.toJsonStr(rows));
        // 校验是否符合导入模板
        if (rows.size() < 3) {
            throw new MithrasException("<涉法涉讼涉访信息表>导入文件格式有误，请下载系统模板进行导入");
        }
        Object row1col1 = rows.get(0).get(0);
        Object row2col1 = rows.get(1).get(0);
        Object row3col1 = rows.get(2).get(0);
        boolean row1col1check = Objects.nonNull(row1col1) && StrUtil.equals(row1col1.toString(), "涉法涉讼涉访信息表");
        boolean row2col1check = Objects.nonNull(row2col1) && StrUtil.equals(row2col1.toString(), "填报单位：");
        boolean row3col1check = Objects.nonNull(row3col1) && StrUtil.equals(row3col1.toString(), "序号");
        if (!row1col1check || !row2col1check || !row3col1check) {
            throw new MithrasException("<涉法涉讼涉访信息表>导入文件格式有误，请下载系统模板进行导入");
        }
        // 查询一下字典，备用
        Map<String, Map<String, String>> dictNameMap = SpringUtil.getBean(AssociationDictionaryService.class).getDisplay2CodeMap();
        // 开始处理Excel数据
        List<AssociationLawInvolvedVisitRelatedInfo> list = new LinkedList<>();
        for (int i = 3; i < rows.size(); i++) {
            List<Object> row = rows.get(i);
            String indexValue = Optional.ofNullable(row.get(0)).map(Object::toString).orElse("");
            if (indexValue.indexOf("合计")!=-1) {
                break;
            }
            try {
                list.add(this.convert(i-2, row, dictNameMap));
            } catch (Exception e) {
                log.error("金融局报送【涉法涉讼涉访信息表】-第{}行数据处理异常", (i -2), e);
                throw new MithrasException("数据处理发生异常");
            }
        }
        return list;
    }

    @Override
    protected void check(List<AssociationLawInvolvedVisitRelatedInfo> dataList) {
        List<String> errorList = new ArrayList<>();
        dataList.forEach(e -> {
            if (Objects.equals(e.getCaseClasCode(), DICT_UNKNOWN_CODE)) {
                errorList.add("B列：信息类别，取值字典范围");
            }

            if (Objects.equals(e.getCanbFlag(), DICT_UNKNOWN_CODE)) {
                errorList.add("F列：是否销号，取值字典范围");
            }

        });
        if (CollectionUtil.isNotEmpty(errorList)) {
            throw new AssociationReportException(errorList);
        }
    }

    @Override
    protected IService<AssociationLawInvolvedVisitRelatedInfo> serviceBean() {
        return SpringUtil.getBean(AssociationLawInvolvedVisitRelatedInfoService.class);
    }

    @Override
    protected AssociationReportCategoryEnum category() {
        return AssociationReportCategoryEnum.J0013;
    }

    private AssociationLawInvolvedVisitRelatedInfo convert(int rowNum, List<Object> row, Map<String, Map<String, String>> dictNameMap) {
        AssociationLawInvolvedVisitRelatedInfo bean = new AssociationLawInvolvedVisitRelatedInfo();
        bean.setRowNum(rowNum);
        // 序号
        bean.setOnum(Optional.ofNullable(row.get(0)).map(Object::toString).orElse(null));
        // 信息类别
        if (row.get(1) != null && StrUtil.isNotBlank(row.get(1).toString())) {
            Map<String, String> contractTypeMap = dictNameMap.get(AssociationDictionaryCategoryEnum.PUB00250.name());
            if (Objects.nonNull(contractTypeMap)) {
                bean.setCaseClasCode(Optional.ofNullable(contractTypeMap.get(row.get(1).toString())).orElse(DICT_UNKNOWN_CODE));
            } else {
                bean.setCaseClasCode(DICT_UNKNOWN_CODE);
            }
        }
        bean.setAgmtName(Optional.ofNullable(row.get(2)).map(Object::toString).orElse(null));// 合同名称
        bean.setAgmtNo(Optional.ofNullable(row.get(3)).map(Object::toString).orElse(null));// 合同编号
        bean.setInvlAmt(Optional.ofNullable(row.get(4)).filter(e -> NumberUtil.isNumber(e.toString())).map(e -> new BigDecimal(e.toString())).orElse(null));// 涉及金额(元)
        // 是否销号
        if (row.get(5) != null && StrUtil.isNotBlank(row.get(5).toString())) {
            YesOrNoNumberEnum item = YesOrNoNumberEnum.findByChinese(row.get(5).toString());
            if (Objects.nonNull(item)) {
                bean.setCanbFlag(item.getCode().toString());
            } else {
                bean.setCanbFlag(DICT_UNKNOWN_CODE);
            }
        }
        return bean;
    }
}
