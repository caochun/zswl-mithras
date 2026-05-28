package cn.zswltech.mithras.associationreport.storedata;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.zswltech.mithras.associationreport.AssociationReportException;
import cn.zswltech.mithras.associationreport.service.AssociationDictionaryService;
import cn.zswltech.mithras.associationreport.service.AssociationShahStorInfoService;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.service.enums.associationreport.AssociationDictionaryCategoryEnum;
import cn.zswltech.mithras.service.enums.associationreport.AssociationReportCategoryEnum;
import cn.zswltech.mithras.service.mapper.model.associationreport.AssociationShahStorInfo;
import cn.zswltech.mithras.service.others.MithrasException;
import com.baomidou.mybatisplus.extension.service.IService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.*;

/**
 * @date 2025/8/26
 * @description 股东股权信息一览表-股东股权信息
 */
@Slf4j
@Component
public class AssociationShahsStorInfoData extends AbstractDataStore<AssociationShahStorInfo> {
    @Override
    protected List<AssociationShahStorInfo> parseFromExcel(InputStream inputStream) {
        List<List<Object>> rows = ExcelUtil.getReader(inputStream).read();
        if (CollectionUtil.isEmpty(rows)) {
            return Collections.emptyList();
        }
        log.info("金融局报送【股东股权信息一览表-股东股权信息】-Excel解析结果:{}", JSONUtil.toJsonStr(rows));
        // 校验是否符合导入模板
        if (rows.size() < 3) {
            throw new MithrasException("<股东股权信息一览表-股东变更记录>导入文件格式有误，请下载系统模板进行导入");
        }
        Object row1col1 = rows.get(0).get(0);
        Object row2col1 = rows.get(1).get(0);
        Object row3col1 = rows.get(2).get(0);
        boolean row1col1check = Objects.nonNull(row1col1) && StrUtil.equals(row1col1.toString(), "股东股权信息一览表");
        boolean row2col1check = Objects.nonNull(row2col1) && StrUtil.equals(row2col1.toString(), "填报单位：");
        boolean row3col1check = Objects.nonNull(row3col1) && StrUtil.equals(row3col1.toString(), "序号");
        if (!row1col1check || !row2col1check || !row3col1check) {
            throw new MithrasException("<股东股权信息一览表-股东股权信息>导入文件格式有误，请下载系统模板进行导入");
        }
        // 查询一下字典，备用
        Map<String, Map<String, String>> dictNameMap = SpringUtil.getBean(AssociationDictionaryService.class).getDisplay2CodeMap();
        // 开始处理Excel数据
        List<AssociationShahStorInfo> list = new LinkedList<>();
        for (int i = 3; i < rows.size(); i++) {
            List<Object> row = rows.get(i);
            String indexValue = Optional.ofNullable(row.get(0)).map(Object::toString).orElse("");
            if (indexValue.indexOf("填报说明")!=-1) {
                break;
            }
            try {
                list.add(this.convert(i-2, row, dictNameMap));
            } catch (Exception e) {
                log.error("金融局报送【股东股权信息一览表-股东股权信息】-第{}行数据处理异常", (i-2), e);
                throw new MithrasException("数据处理发生异常");
            }
        }
        return list;
    }

    @Override
    protected void check(List<AssociationShahStorInfo> dataList) {
        List<String> errorList = new ArrayList<>();
        dataList.forEach(e -> {
            if (Objects.equals(e.getShahCharCode(), DICT_UNKNOWN_CODE)) {
                errorList.add("股东股权信息，D列：股东性质，取值字典范围");
            }

            if (Objects.equals(e.getShahGtoMode(), DICT_UNKNOWN_CODE)) {
                errorList.add("股东股权信息，E列：进入方式，取值字典范围");
            }

            if (Objects.equals(e.getStorTranFlag(), DICT_UNKNOWN_CODE)) {
                errorList.add("股东股权信息，H列：股权转让，取值字典范围");
            }

        });
        if (CollectionUtil.isNotEmpty(errorList)) {
            throw new AssociationReportException(errorList);
        }
    }

    @Override
    protected IService<AssociationShahStorInfo> serviceBean() {
        return SpringUtil.getBean(AssociationShahStorInfoService.class);
    }

    @Override
    protected AssociationReportCategoryEnum category() {
        return AssociationReportCategoryEnum.J0002;
    }

    private AssociationShahStorInfo convert(int rowNum, List<Object> row, Map<String, Map<String, String>> dictNameMap) {
        AssociationShahStorInfo bean = new AssociationShahStorInfo();
        bean.setRowNum(rowNum);
        // 序号
        bean.setOnum(Optional.ofNullable(row.get(0)).map(Object::toString).orElse(null));
        // 股东全称
        bean.setShahFn(Optional.ofNullable(row.get(1)).map(Object::toString).orElse(null));//股东全称
        bean.setShahCertNum(Optional.ofNullable(row.get(2)).map(Object::toString).orElse(null));//统一社会信用代码/身份证号
        // 股东性质
        if (row.get(3) != null && StrUtil.isNotBlank(row.get(3).toString())) {
            Map<String, String> contractTypeMap = dictNameMap.get(AssociationDictionaryCategoryEnum.PTY00021.name());
            if (Objects.nonNull(contractTypeMap)) {
                bean.setShahCharCode(Optional.ofNullable(contractTypeMap.get(row.get(3).toString())).orElse(DICT_UNKNOWN_CODE));
            } else {
                bean.setShahCharCode(DICT_UNKNOWN_CODE);
            }
        }
        // 股东进入方式
        if (row.get(4) != null && StrUtil.isNotBlank(row.get(4).toString())) {
            String shahGtoMode = row.get(4).toString();
            if(shahGtoMode.equals("创设") || shahGtoMode.equals("受让")){
                bean.setShahGtoMode(shahGtoMode);
            }else{
                bean.setShahGtoMode(DICT_UNKNOWN_CODE);
            }
        }
        // 变更前股东出资金额(万元)
        bean.setAltrBefShahFndrAmt(Optional.ofNullable(row.get(5)).filter(e -> NumberUtil.isNumber(e.toString())).map(e -> new BigDecimal(e.toString())).orElse(null));
        //变更前出资比例
        bean.setAltrBefFndrRati(Optional.ofNullable(row.get(6)).filter(e -> NumberUtil.isNumber(e.toString())).map(e -> new BigDecimal(e.toString())).orElse(null));
        // 股权转让标志，暂时和其它保持一致，存编码
        if (row.get(7) != null && StrUtil.isNotBlank(row.get(7).toString())) {
            YesOrNoNumberEnum item = YesOrNoNumberEnum.findByChinese(row.get(7).toString());
            if (Objects.nonNull(item)) {
                bean.setStorTranFlag(item.getCode().toString());
            } else {
                bean.setStorTranFlag(DICT_UNKNOWN_CODE);
            }
        }


        bean.setIordCptlAmt(Optional.ofNullable(row.get(8)).filter(e -> NumberUtil.isNumber(e.toString())).map(e -> new BigDecimal(e.toString())).orElse(null));//增减资金金额(万元)
        bean.setLastFndrAmt(Optional.ofNullable(row.get(9)).filter(e -> NumberUtil.isNumber(e.toString())).map(e -> new BigDecimal(e.toString())).orElse(null));//最新出资金额(万元)
        bean.setLastHoldRati(Optional.ofNullable(row.get(10)).filter(e -> NumberUtil.isNumber(e.toString())).map(e -> new BigDecimal(e.toString())).orElse(null));//最新持有比例
        bean.setAprvFileNum(Optional.ofNullable(row.get(11)).map(Object::toString).orElse(null));//批复文件号
        bean.setAprvTime(Optional.ofNullable(row.get(12)).filter(e -> StrUtil.isNotBlank(e.toString())).map(e -> LocalDateTimeUtil.parseDate(e.toString().substring(0, 10), DatePattern.NORM_DATE_PATTERN)).orElse(null));//批复时间
        return bean;
    }
}
