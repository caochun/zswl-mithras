package cn.zswltech.mithras.projectprocess.mapper.lib.projreview;

import cn.zswltech.mithras.projectprocess.mapper.model.projreview.ProjReviewFactoringPriceLib;
import cn.zswltech.mithras.projectprocess.application.lib.projreview.dto.ProjReviewPriceDto;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 保理报价方案版本表
 *
 * @author: jackerhe
 * @date: 2022/8/2 10:32 上午
 **/
@Repository
public interface ProjReviewFactoringPriceLibMapper extends BaseMapper<ProjReviewFactoringPriceLib> {
    List<ProjReviewFactoringPriceLib> listNewestPrice(@Param("dto") ProjReviewPriceDto dto);
}
