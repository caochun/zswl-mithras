package cn.zswltech.mithras.kpi.mapper;

import cn.zswltech.mithras.kpi.dto.persistence.*;
import cn.zswltech.mithras.kpi.model.KpiProjGuessBaseInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @description 绩效-项目测算表
* @author jackerhe
* @date 2023-06-15
*/
public interface KpiProjGuessBaseInfoMapper extends BaseMapper<KpiProjGuessBaseInfo> {

    Page<KpiProjGuessContractDTO> getContractGuess(Page<KpiProjGuessContractDTO> page, @Param("param") KpiProjGuessParam param);

    Page<KpiProjGuessDetailDTO> getDetailGuess(Page<KpiProjGuessContractDTO> page, @Param("param") KpiProjDetailParam param);

    List<KpiProjGuessProjDTO> projManager(@Param("param") KpiProjGuessParam param);

    List<KpiProjGuessProjDetailDTO> projManagerDetail(@Param("param") KpiProjGuessProjDetailParam param);

    List<KpiProjGuessProjDetailDTO> projManagerDivideTypeDetail(@Param("param") KpiProjGuessProjDetailParam param);

    List<KpiProjGuessProjCompletionDTO> projManagerCompletion(@Param("param") KpiProjGuessProjDetailParam param);

    List<KpiProjGuessProjCompletionDetailDTO> projManagerCompletionDetail(@Param("param") KpiProjGuessProjCompletionDetailParam param);

    List<KpiProjGuessDeptPooleDTO> deptPool(@Param("param") KpiProjGuessProjDetailParam param);

}