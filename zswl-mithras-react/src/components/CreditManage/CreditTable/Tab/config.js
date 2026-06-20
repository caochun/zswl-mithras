import CreditTableBaoZheng from './BaoZheng/CreditTableBaoZheng'
import CreditTableDiYa from './DiYa/CreditTableDiYa'
import CreditTableHuanKuan from './HuanKuan/CreditTableHuanKuan'
import CreditTableJiaoYi from './JiaoYi/CreditTableJiaoYi'
import CreditTableKuHu from './KuHu/CreditTableKuHu'
import CreditTableLevel5 from './Level5/CreditTableLevel5'
import CreditTableYuQi from './YuQi/CreditTableYuQi'
import CreditTableZhangHu from './ZhangHu/CreditTableZhangHu'
import CreditTableZhiYa from './ZhiYa/CreditTableZhiYa'


export const tabList = [
  {
    key: '1',
    label: `账户表`,
    children: <CreditTableZhangHu />,
  },
  {
    key: '2',
    label: `还款表`,
    children: <CreditTableHuanKuan />,
  },
  {
    key: '3',
    label: `特定交易表`,
    children: <CreditTableJiaoYi />,
  },
  {
    key: '4',
    label: `逾期表`,
    children: <CreditTableYuQi />,
  },
  {
    key: '5',
    label: `五级分类表`,
    children: <CreditTableLevel5 />,
  },
  {
    key: '6',
    label: `客户表`,
    children: <CreditTableKuHu />,
  },
  {
    key: '7',
    label: `保证表`,
    children: <CreditTableBaoZheng />,
  },
  {
    key: '8',
    label: `抵押表`,
    children: <CreditTableDiYa />,
  },
  {
    key: '9',
    label: `质押表`,
    children: <CreditTableZhiYa />,
  },
]

export { CREATETABLE_PARAMS, AmountFormat, getHeaderWithFunctionCode } from '../../CreditTableConfig/CreditTableConfig'
