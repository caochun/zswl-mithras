import ZhangHu from '@/components/CreditManage/CreditTable/Tab/ZhangHu'
import HuanKuan from '@/components/CreditManage/CreditTable/Tab/HuanKuan'
import JiaoYi from '@/components/CreditManage/CreditTable/Tab/JiaoYi'
import YuQi from '@/components/CreditManage/CreditTable/Tab/YuQi'
import Level5 from '@/components/CreditManage/CreditTable/Tab/Level5'
import KuHu from '@/components/CreditManage/CreditTable/Tab/KuHu'
import BaoZheng from '@/components/CreditManage/CreditTable/Tab/BaoZheng'
import DiYa from '@/components/CreditManage/CreditTable/Tab/DiYa'
import ZhiYa from '@/components/CreditManage/CreditTable/Tab/ZhiYa'


export const tabList = [
  {
    key: '1',
    label: `账户表`,
    children: <ZhangHu />,
  },
  {
    key: '2',
    label: `还款表`,
    children: <HuanKuan />,
  },
  {
    key: '3',
    label: `特定交易表`,
    children: <JiaoYi />,
  },
  {
    key: '4',
    label: `逾期表`,
    children: <YuQi />,
  },
  {
    key: '5',
    label: `五级分类表`,
    children: <Level5 />,
  },
  {
    key: '6',
    label: `客户表`,
    children: <KuHu />,
  },
  {
    key: '7',
    label: `保证表`,
    children: <BaoZheng />,
  },
  {
    key: '8',
    label: `抵押表`,
    children: <DiYa />,
  },
  {
    key: '9',
    label: `质押表`,
    children: <ZhiYa />,
  },
]

export { CREATETABLE_PARAMS, AmountFormat, getHeaderWithFunctionCode } from '@/components/CreditManage/CreditTableConfig'
