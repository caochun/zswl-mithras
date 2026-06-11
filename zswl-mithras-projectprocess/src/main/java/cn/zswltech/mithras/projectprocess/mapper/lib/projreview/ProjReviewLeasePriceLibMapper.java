package cn.zswltech.mithras.projectprocess.mapper.lib.projreview;

import cn.zswltech.mithras.projectprocess.model.projreview.ProjReviewLeasePriceLib;
import cn.zswltech.mithras.projectprocess.application.lib.projreview.dto.ProjReviewPriceDto;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 租赁报价方案版本表
 *
 * @author: jackerhe
 * @date: 2022/8/2 10:32 上午
 **/
public interface ProjReviewLeasePriceLibMapper extends BaseMapper<ProjReviewLeasePriceLib> {

    List<ProjReviewLeasePriceLib> listNewestPrice(@Param("dto") ProjReviewPriceDto dto);
}
