package cn.zswltech.mithras.projectprocess.application.lib.projreview.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Set;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/3/17 18:24
 */
@Data
@Accessors(chain = true)
public class ProjReviewPriceDto {
    private Set<Long> projReviewIds;
}
