package cn.zswltech.mithras.associationreport.storedata;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.zswltech.mithras.associationreport.AssociationReportException;
import cn.zswltech.mithras.associationreport.service.AssociationBasicSituationService;
import cn.zswltech.mithras.associationreport.service.AssociationDictionaryService;
import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.associationreport.enums.AssociationDictionaryCategoryEnum;
import cn.zswltech.mithras.associationreport.enums.AssociationListFlagEnum;
import cn.zswltech.mithras.associationreport.enums.AssociationMnfrFlagEnum;
import cn.zswltech.mithras.associationreport.enums.AssociationReportCategoryEnum;
import cn.zswltech.mithras.associationreport.mapper.model.AssociationBasicSituation;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import com.baomidou.mybatisplus.extension.service.IService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.*;

/**
 * @date 2025/4/18
 * @description 基本情况统计表
 */
@Slf4j
@Component
public class AssociationBasicSituationData extends AbstractDataStore<AssociationBasicSituation> {
    @Override
    protected List<AssociationBasicSituation> parseFromExcel(InputStream inputStream) {
        List<List<Object>> rows = ExcelUtil.getReader(inputStream).read();
        if (CollectionUtil.isEmpty(rows)) {
            return Collections.emptyList();
        }
        log.info("金融局报送【基本情况统计表】-Excel解析结果:{}", JSONUtil.toJsonStr(rows));
        // 校验是否符合导入模板
        if (rows.size() < 3) {
            throw new MithrasException("<基本情况统计表>导入文件格式有误，请下载系统模板进行导入");
        }
        Object row1col1 = rows.get(0).get(0);
        Object row2col1 = rows.get(1).get(0);
        Object row3col1 = rows.get(2).get(0);
        boolean row1col1check = Objects.nonNull(row1col1) && StrUtil.equals(row1col1.toString(), "融资租赁公司基本情况统计表");
        boolean row2col1check = Objects.nonNull(row2col1) && StrUtil.equals(row2col1.toString(), "填报单位：");
        boolean row3col1check = Objects.nonNull(row3col1) && StrUtil.equals(row3col1.toString(), "序号");
        if (!row1col1check || !row2col1check || !row3col1check) {
            throw new MithrasException("<基本情况统计表>导入文件格式有误，请下载系统模板进行导入");
        }
        // 查询一下字典，备用
        Map<String, Map<String, String>> dictNameMap = SpringUtil.getBean(AssociationDictionaryService.class).getDisplay2CodeMap();
        // 开始处理Excel数据
        List<AssociationBasicSituation> list = new LinkedList<>();
        try {
           /* if (rows.size() < 22) {
                throw new MithrasException("表格格式不正确，确实必要行");
            }*/
            list.add(this.convert(rows, dictNameMap));
        } catch (Exception e) {
            log.error("金融局报送【基本情况统计表】数据处理异常", e);
            throw new MithrasException("数据处理异常");
        }
        return list;
    }

    @Override
    protected void check(List<AssociationBasicSituation> dataList) {
        List<String> errorList = new ArrayList<>();
        dataList.forEach(e -> {
            if (ObjectUtil.isEmpty(e.getEconClasCode()) || Objects.equals(e.getEconClasCode(), DICT_UNKNOWN_CODE)) {
                errorList.add("序号9：经济成分，必填字段,取值字典范围");
            }

            if (Objects.equals(e.getEconClasCode(), "01") && (ObjectUtil.isEmpty(e.getCtarCorpHoldFlag()) || ObjectUtil.isEmpty(e.getLcalSoeHoldFlag()))) {
                errorList.add("序号10、11：经济成分为国有控股时，请选择控股标志");
            }

            if (Objects.equals(e.getCtarCorpHoldFlag(), DICT_UNKNOWN_CODE)) {
                errorList.add("序号10：是否中央企业控股，取值字典范围");
            }

            if (Objects.equals(e.getLcalSoeHoldFlag(), DICT_UNKNOWN_CODE)) {
                errorList.add("序号11：是否地方国企控股，取值字典范围");
            }

            if (Objects.equals(e.getCorpClasCode(), DICT_UNKNOWN_CODE)) {
                errorList.add("序号15：内资/内资试点/外资，取值字典范围");
            }

            if (Objects.equals(e.getMnfrFlag(), DICT_UNKNOWN_CODE)) {
                errorList.add("序号16：厂商系/非厂商系，取值字典范围");
            }

            if (Objects.equals(e.getListFlag(), DICT_UNKNOWN_CODE)) {
                errorList.add("序号17：上市/非上市，取值字典范围");
            }

        });
        if (CollectionUtil.isNotEmpty(errorList)) {
            throw new AssociationReportException(errorList);
        }
    }

    @Override
    protected IService<AssociationBasicSituation> serviceBean() {
        return SpringUtil.getBean(AssociationBasicSituationService.class);
    }

    @Override
    protected AssociationReportCategoryEnum category() {
        return AssociationReportCategoryEnum.J0001;
    }

    private AssociationBasicSituation convert(List<List<Object>> excelRows, Map<String, Map<String, String>> dictNameMap) {
        AssociationBasicSituation bean = new AssociationBasicSituation();
        bean.setRowNum(1);
        bean.setOp("insert");
//        int rowIndex = 3;
        int columnIndex = 2;
        bean.setUnifSociCredCode(Optional.ofNullable(excelRows.get(3).get(columnIndex)).map(Object::toString).orElse(null));//统一社会信用代码
        bean.setLegr(Optional.ofNullable(excelRows.get(4).get(columnIndex)).map(Object::toString).orElse(null));//法定代表人
        bean.setSetpDate(Optional.ofNullable(excelRows.get(5).get(columnIndex)).filter(e -> StrUtil.isNotBlank(e.toString())).map(e -> LocalDateTimeUtil.parseDate(e.toString().substring(0, 10), DatePattern.NORM_DATE_PATTERN)).orElse(null));//成立日期
        bean.setAprvUnit(Optional.ofNullable(excelRows.get(6).get(columnIndex)).map(Object::toString).orElse(null));//批准单位
        bean.setAprvFileNum(Optional.ofNullable(excelRows.get(7).get(columnIndex)).map(Object::toString).orElse(null));//批准文号
        bean.setOperCptl(parseBigDecimal(excelRows.get(8).get(columnIndex)));//营运资金(万元)
        bean.setSttoCptl(parseBigDecimal(excelRows.get(9).get(columnIndex)));//国有资本(万元)
        bean.setPaidCptl(parseBigDecimal(excelRows.get(10).get(columnIndex)));//实收资本(万元)
        // 经济成分
        if (excelRows.get(11).get(columnIndex) != null && StrUtil.isNotBlank(excelRows.get(11).get(columnIndex).toString())) {
            Map<String, String> contractTypeMap = dictNameMap.get(AssociationDictionaryCategoryEnum.PTY00003.name());
            if (Objects.nonNull(contractTypeMap)) {
                bean.setEconClasCode(Optional.ofNullable(contractTypeMap.get(excelRows.get(11).get(columnIndex).toString())).orElse(DICT_UNKNOWN_CODE));
            } else {
                bean.setEconClasCode(DICT_UNKNOWN_CODE);
            }
        }
        // 是否中央企业控股
        if (excelRows.get(12).get(columnIndex) != null && StrUtil.isNotBlank(excelRows.get(12).get(columnIndex).toString())) {
            YesOrNoNumberEnum item = YesOrNoNumberEnum.findByChinese(excelRows.get(12).get(columnIndex).toString());
            if (Objects.nonNull(item)) {
                bean.setCtarCorpHoldFlag(item.getCode().toString());
            } else {
                bean.setCtarCorpHoldFlag(DICT_UNKNOWN_CODE);
            }
        }
        // 是否地方国企控股
        if (excelRows.get(13).get(columnIndex) != null &&  StrUtil.isNotBlank(excelRows.get(13).get(columnIndex).toString())) {
            YesOrNoNumberEnum item = YesOrNoNumberEnum.findByChinese(excelRows.get(13).get(columnIndex).toString());
            if (Objects.nonNull(item)) {
                bean.setLcalSoeHoldFlag(item.getCode().toString());
            }else {
                bean.setLcalSoeHoldFlag(DICT_UNKNOWN_CODE);
            }
        }
        bean.setPrtiNum(Optional.ofNullable(excelRows.get(14).get(columnIndex)).filter(e -> NumberUtil.isNumber(e.toString())).map(e -> Integer.parseInt(e.toString())).orElse(null));//从业人员
        bean.setRegAddr(Optional.ofNullable(excelRows.get(15).get(columnIndex)).map(Object::toString).orElse(null));    //注册地址
        bean.setActlOperAddr(Optional.ofNullable(excelRows.get(16).get(columnIndex)).map(Object::toString).orElse(null)); // 实际经营地址
        //企业类别(内资/内资试点/外资)
        if (excelRows.get(17).get(columnIndex) != null && StrUtil.isNotBlank(excelRows.get(17).get(columnIndex).toString())) {
            Map<String, String> contractTypeMap = dictNameMap.get(AssociationDictionaryCategoryEnum.PTY00221.name());
            if (Objects.nonNull(contractTypeMap)) {
                bean.setCorpClasCode(Optional.ofNullable(contractTypeMap.get(excelRows.get(17).get(columnIndex).toString())).orElse(DICT_UNKNOWN_CODE));
            } else {
                bean.setCorpClasCode(DICT_UNKNOWN_CODE);
            }
        }
        // 厂商系标志(厂商系/非厂商系),值待定，先从下拉框取值
        if (excelRows.get(18).get(columnIndex) != null &&  StrUtil.isNotBlank(excelRows.get(18).get(columnIndex).toString())) {
            AssociationMnfrFlagEnum item = AssociationMnfrFlagEnum.findByChinese(excelRows.get(18).get(columnIndex).toString());
            if (Objects.nonNull(item)) {
                bean.setMnfrFlag(item.name());
            }else {
                bean.setMnfrFlag(DICT_UNKNOWN_CODE);
            }
        }
        //上市标志(上市/非上市),值待定，先从下拉框取值
        if (excelRows.get(19).get(columnIndex) != null &&  StrUtil.isNotBlank(excelRows.get(19).get(columnIndex).toString())) {
            AssociationListFlagEnum item = AssociationListFlagEnum.findByChinese(excelRows.get(19).get(columnIndex).toString());
            if (Objects.nonNull(item)) {
                bean.setListFlag(item.name());
            }else {
                bean.setListFlag(DICT_UNKNOWN_CODE);
            }
        }
        bean.setBrchInsNum(Optional.ofNullable(excelRows.get(20).get(columnIndex)).filter(e -> NumberUtil.isNumber(e.toString())).map(e -> Integer.parseInt(e.toString())).orElse(null));//分支机构数量(家)
        bean.setOprvBrchInsNum(Optional.ofNullable(excelRows.get(21).get(columnIndex)).filter(e -> NumberUtil.isNumber(e.toString())).map(e -> Integer.parseInt(e.toString())).orElse(null));//省外分支机构数量(家)
        bean.setWprvBrchInsNum(Optional.ofNullable(excelRows.get(22).get(columnIndex)).filter(e -> NumberUtil.isNumber(e.toString())).map(e -> Integer.parseInt(e.toString())).orElse(null));//省内分支机构数量(家)
        bean.setFnlChilCorpNum(Optional.ofNullable(excelRows.get(23).get(columnIndex)).filter(e -> NumberUtil.isNumber(e.toString())).map(e -> Integer.parseInt(e.toString())).orElse(null));//设立的其他融资租赁子公司数量
        bean.setSpclProjCorpSpvVol(Optional.ofNullable(excelRows.get(24).get(columnIndex)).filter(e -> NumberUtil.isNumber(e.toString())).map(e -> Integer.parseInt(e.toString())).orElse(null));//设立的特殊项目公司（SPV)数量
        bean.setBrchInsAddr(Optional.ofNullable(excelRows.get(25).get(columnIndex)).map(Object::toString).orElse(null));//分支机构地址
        bean.setHsapBusiScop(Optional.ofNullable(excelRows.get(26).get(columnIndex)).map(Object::toString).orElse(null));//经批准的业务范围
        bean.setActlCtlr(Optional.ofNullable(excelRows.get(27).get(columnIndex)).map(Object::toString).orElse(null));//实际控制人
        bean.setActlCtlrHoldRati(parseBigDecimal(excelRows.get(28).get(columnIndex)));//实际控制人持股比例
        bean.setCorpConp(Optional.ofNullable(excelRows.get(29).get(columnIndex)).map(Object::toString).orElse(null));//公司联系人
        bean.setContTel(Optional.ofNullable(excelRows.get(30).get(columnIndex)).map(Object::toString).orElse(null));// 联系电话
        bean.setContMail(Optional.ofNullable(excelRows.get(31).get(columnIndex)).map(Object::toString).orElse(null));// 联系邮箱
        bean.setCorpWeb(Optional.ofNullable(excelRows.get(32).get(columnIndex)).map(Object::toString).orElse(null));// 公司网址
        return bean;
    }
}
