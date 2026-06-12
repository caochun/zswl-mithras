package cn.zswltech.mithras.third.datashare.persistence.model;
import lombok.Data;
import java.time.LocalDateTime;
import java.io.Serializable;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;


/**
 * @description data_share_manager
 * @author vico
 * @date 2022-08-03
 */
@Data
public class DataShareManager implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 模块
     */
    @TableField("model_name")
    private String modelName;

    /**
     * 开始时间
     */
    @TableField("start_time")
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    @TableField("end_time")
    private LocalDateTime endTime;

    /**
     * 页大小
     */
    @TableField("page_size")
    private Integer pageSize;

    /**
     * 页码数
     */
    @TableField("page_num")
    private Integer pageNum;


    /**
     * 数据总量
     */
    @TableField("data_total")
    private Integer dataTotal;

}
