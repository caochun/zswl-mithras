package cn.zswltech.mithras.report.service.formal;

import cn.zswltech.mithras.report.mapper.formal.CrSpecialTradeMapper;
import cn.zswltech.mithras.report.mapper.formal.model.CrSpecialTrade;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 征信报送-特定交易表
 *
 * @author wangchuanhao
 * @date 2022/10/8 10:41 AM
 */
@Service
public class CrSpecialTradeService extends ServiceImpl<CrSpecialTradeMapper, CrSpecialTrade> implements IService<CrSpecialTrade> {

}
