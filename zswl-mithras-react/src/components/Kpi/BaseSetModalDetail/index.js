import { cloneElement } from 'react'
import { Modal } from '@zswl/components'
import { observer } from '@zswl/admin'
import SuiLvWeiHu from './SuiLvWeiHu/KpiBaseSetSuiLvWeiHu'
import FeiYongJiTi from './FeiYongJiTi'
import BaBeiJiTi from './BaBeiJiTi/KpiBaseSetBaBeiJiTi'
import BuMenLiLun from './BuMenLiLun/KpiBaseSetBuMenLiLun'
import GongShiLiLun from './GongShiLiLun/KpiBaseSetGongShiLiLun'
import JinRongShiChangDept from './JinRongShiChangDept/KpiBaseSetJinRongShiChangDept'
import ZhongHouTaiDept from './ZhongHouTaiDept/KpiBaseSetZhongHouTaiDept'
import JinRongShiChangTiJiang from './JinRongShiChangTiJiang/KpiBaseSetJinRongShiChangTiJiang'
import YeWuDept from './YeWuDept/KpiBaseSetYeWuDept'
import ZhiDengXiShu from './ZhiDengXiShu/KpiBaseSetZhiDengXiShu'
import XiangMuTiJiang from './XiangMuTiJiang/KpiBaseSetXiangMuTiJiang'

const Index = ({ store }) => {
  const { typeInfo } = store
  if (!typeInfo) return null
  const { configCode } = typeInfo
  const compMap = {
    TAX_RATE: {
      comp: <SuiLvWeiHu></SuiLvWeiHu>,
      width: 600,
      footer: null,
      desc: '税率维护',
    },
    PROVISION_RADIO: {
      comp: <BaBeiJiTi></BaBeiJiTi>,
      width: 800,
      desc: '拔备计提比例',
    },
    EXPENSE_RADIO: {
      comp: <FeiYongJiTi></FeiYongJiTi>,
      width: 500,
      desc: '费用计提比例',
    },
    PROJECT_RADIO: {
      comp: <XiangMuTiJiang></XiangMuTiJiang>,
      width: 1200,
      desc: '项目提奖比例',
    },
    DEPT_PROFIT_FINISH_RADIO: {
      comp: <BuMenLiLun></BuMenLiLun>,
      width: 500,
      desc: '部门利润完成率系数',
    },
    CAREER_LEVEL: {
      comp: <ZhiDengXiShu></ZhiDengXiShu>,
      width: 600,
      desc: '职等系数',
    },
    BUSINESS_DEPT_ASSESS: {
      comp: <YeWuDept></YeWuDept>,
      width: 600,
      desc: '业务部门综合考评系数',
    },
    FINANCIAL_MARKET_DEPT_RADIO: {
      comp: <JinRongShiChangTiJiang></JinRongShiChangTiJiang>,
      width: 500,
      desc: '金融市场部提奖比例',
    },
    PROFIT_ADJUST: {
      comp: <GongShiLiLun></GongShiLiLun>,
      width: 700,
      desc: '公司利润调节系数',
    },
    FINANCIAL_MARKET_DEPT_ASSESS: {
      comp: <JinRongShiChangDept></JinRongShiChangDept>,
      width: 500,
      desc: '金融市场部综合考评系数',
    },
    MIDDLE_BACK_DEPT_ASSESS: {
      comp: <ZhongHouTaiDept></ZhongHouTaiDept>,
      width: 500,
      desc: '中后台部门综合考评系数',
    },
  }[configCode]
  if (!compMap) return null
  const { comp, footer, desc, ...rest } = compMap
  return (
    <Modal
      propsBy={(data) => {
        const { isEdit, configDesc } = data
        return {
          title: isEdit ? `编辑-${configDesc}` : `查看-${configDesc}`,
          // footer: !isEdit || !footer ? null : undefined,
          footer: null,
        }
      }}
      store={store.$editModal}
      destroyOnClose
      width={700}
      {...rest}
    >
      {cloneElement(comp, {
        typeInfo,
        baseStore: store,
      })}
    </Modal>
  )
}

export default observer(Index)
