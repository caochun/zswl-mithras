package cn.zswltech.mithras.third.providence.mapper;

import cn.zswltech.mithras.third.providence.entity.BillOverdue;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author Jim
 * @version 1.0.0
 * @descripition:
 * @date 2024/12/24 14:18
 */
public interface BillOverdueMapper extends BaseMapper<BillOverdue> {
    /**
     * 批量插入
     * @param billOverdues
     */
    @Insert("<script>" +
            "insert into bill_overdue(seq_no,org_code,org_name,org_type,overdue_start_date,busi_date) values \n" +
            "<foreach collection=\"list\" item=\"item\" separator=\",\">\n" +
            "(#{item.seqNo},\n" +
            " #{item.orgCode},\n" +
            " #{item.orgName},\n" +
            " #{item.orgType},\n" +
            " #{item.overdueStartDate},\n" +
            " #{item.busiDate})\n" +
            "</foreach>" +
            "</script>")
    void batchSave(List<BillOverdue> billOverdues);

    @Select("select max(busi_date) from bill_overdue")
    String selectLatestBusiDate();
}
