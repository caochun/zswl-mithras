package cn.zswltech.mithras.ftp.oldftp.bo;

import cn.zswltech.mithras.ftp.newftp.enums.AssetIndustryClassify;
import cn.zswltech.mithras.service.enums.newftp.CustomerEntityClassify;
import cn.zswltech.mithras.ftp.newftp.enums.RelatedTermRange;
import cn.zswltech.mithras.ftp.newftp.enums.TermRange;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @ClassName FtpQuarterlyBasePricingBO
 * @Author jackerhe
 * @Date 2023/11/13 3:55 下午
 * @Version 1.0
 **/
@Data
public class FtpQuarterlyBasePricingBO {

    /**
     * 资产行业分类
     * {@link AssetIndustryClassify#name()}
     */
    private String assetIndustryClassify;
    /**
     * 地区分类 ZHEJIANG ENCOURAGE OTHER
     */
    private String regionalClassify;

    /**
     * 期项范围
     * {@link TermRange#name()}
     */
    private String termRange;

    /**
     * {@link CustomerEntityClassify#name()}
     * 风控行业分类
     **/
    private String customerEntityClassify;

    /**
     * 是否关联方
     */
    private boolean related = false;

    private RelatedTermRange relatedTermRange;


}
