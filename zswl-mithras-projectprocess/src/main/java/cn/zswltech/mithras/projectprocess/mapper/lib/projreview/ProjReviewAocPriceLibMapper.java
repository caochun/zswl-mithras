package cn.zswltech.mithras.projectprocess.mapper.lib.projreview;

import cn.zswltech.mithras.projectprocess.mapper.model.projreview.ProjReviewAocPriceLib;
import cn.zswltech.mithras.projectprocess.service.riskcontrol.dto.ProjReviewPriceDto;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 债权转让报价方案版本表
 *
 * @author: jackerhe
 * @date: 2022/8/2 10:31 上午
 **/
@Repository
public interface ProjReviewAocPriceLibMapper extends BaseMapper<ProjReviewAocPriceLib> {
    List<ProjReviewAocPriceLib> listNewestPrice(@Param("dto") ProjReviewPriceDto dto);
}
