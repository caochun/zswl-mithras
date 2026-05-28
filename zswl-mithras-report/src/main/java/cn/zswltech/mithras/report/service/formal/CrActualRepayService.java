package cn.zswltech.mithras.report.service.formal;

import cn.zswltech.mithras.report.mapper.formal.CrActualRepayMapper;
import cn.zswltech.mithras.report.mapper.formal.model.CrActualRepay;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 征信报送-实际还款表
 *
 * @author wangchuanhao
 * @date 2022/10/8 10:41 AM
 */
@Service
public class CrActualRepayService extends ServiceImpl<CrActualRepayMapper, CrActualRepay> implements IService<CrActualRepay> {

}
