package cn.zswltech.mithras.rating.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
* 融租易内评区域模型指标值;
 * @author zswl
 */
@TableName("rzy_dm_calculate_indicator")
@Data
@Accessors(chain = true)
public class RzyDmCalculateIndicator implements Serializable{
   /**  */
   @TableId(type= IdType.AUTO)
   private Long id ;
   private Long areaUniCode ;
   private String areaName ;
   private Integer year;
   private String indicatorName;
   /** 指标code */
   private String indicatorCode ;
   /** 指标值 */
   private BigDecimal indicatorValue ;
   /** 指标计算日期时间 */
   private LocalDateTime dt ;
}