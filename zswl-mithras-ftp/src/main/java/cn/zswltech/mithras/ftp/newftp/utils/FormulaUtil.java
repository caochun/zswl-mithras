package cn.zswltech.mithras.ftp.newftp.utils;

import cn.zswltech.mithras.service.util.StringUtil;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.mithras.dto.newftp.NewFtpParameterDTO;
import cn.zswltech.mithras.service.others.MithrasException;
import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

/**
 * @ClassName FormulaUtil
 * @Description TODO
 * @Author jackerhe
 * @Date 2024/9/28 15:39
 * @Version 1.0
 **/
@Component
public class FormulaUtil {

    @Resource
    private ExpressRunner expressRunner;

    /**
     * 解析公式，原值入，原值出
     **/
    public BigDecimal analyticalFormula(String formula, BigDecimal fluctuationRange){
        if(ObjectUtil.isEmpty(formula)){
            return fluctuationRange;
        }
        List<NewFtpParameterDTO> newFtpParameterDTOS = JSONUtil.toList(formula, NewFtpParameterDTO.class);
        if(ObjectUtil.isEmpty(newFtpParameterDTOS)){
            return fluctuationRange;
        }
        DefaultContext<String, Object> context = new DefaultContext<>();
        context.put("T", String.valueOf(fluctuationRange));
        for(NewFtpParameterDTO dto : newFtpParameterDTOS){
            String fluctuationFormula = dto.getFluctuationFormula();
            if(ObjectUtil.isEmpty(fluctuationFormula)){
                return fluctuationRange;
            }
            //'in(1,3)'
            String format;
            if(fluctuationFormula.startsWith("=")){
                fluctuationFormula = fluctuationFormula.substring(1, fluctuationFormula.length());
            }

            if(fluctuationFormula.contains("if")){
                //自定义公式
                format = fluctuationFormula;
            } else if(isRange(fluctuationFormula)){
                //区间
                fluctuationFormula = fluctuationFormula.replace("(", "T>");
                fluctuationFormula = fluctuationFormula.replace("[", "T>=");
                fluctuationFormula = fluctuationFormula.replace(")", ">T");
                fluctuationFormula = fluctuationFormula.replace("]", ">=T");
                fluctuationFormula = "and(" + fluctuationFormula;
                fluctuationFormula = fluctuationFormula + ")";
                format = String.format("if(%s,true,false)", fluctuationFormula);
            } else if(StringUtil.isNumeric(fluctuationFormula)) {
                format = String.format("if(T==%s,true,false)", fluctuationFormula);
            } else {
                format = String.format("if(T%s,true,false)", fluctuationFormula);
            }
            try {
                if(((Boolean)expressRunner.execute(StringUtil.formatExcelFormula(format), context, null, true, false)).booleanValue()){
                    return new BigDecimal(dto.getMappingVal());
                }
            } catch (Exception e) {
                throw new MithrasException("计算失败");
            }
        }
        return fluctuationRange;
    }

    private boolean isRange(String value) {
        boolean startWith = value.startsWith("(") || value.startsWith("[");
        boolean endWith = value.endsWith(")") || value.endsWith("]");
        return startWith && endWith;
    }

}
