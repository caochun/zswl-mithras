package cn.zswltech.mithras.customer.externaldata.tianyancha.mapper.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author yibin
 */
@Data
@TableName("tyc_mock_data")
public class TycMockData {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String keyword;
    private String dataType;
    private String jsonData;
    private LocalDateTime createTime;
}
