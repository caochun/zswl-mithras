import ZhangHu from '@/pages/creditManage/creditTable/Tab/ZhangHu'
import HuanKuan from '@/pages/creditManage/creditTable/Tab/HuanKuan'
import JiaoYi from '@/pages/creditManage/creditTable/Tab/JiaoYi'
import YuQi from '@/pages/creditManage/creditTable/Tab/YuQi'
import Level5 from '@/pages/creditManage/creditTable/Tab/Level5'
import KuHu from '@/pages/creditManage/creditTable/Tab/KuHu'
import BaoZheng from '@/pages/creditManage/creditTable/Tab/BaoZheng'
import DiYa from '@/pages/creditManage/creditTable/Tab/DiYa'
import ZhiYa from '@/pages/creditManage/creditTable/Tab/ZhiYa'

import { formatAmountWan } from '@/components/Format'

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

export const CREATETABLE_PARAMS = 'CREATETABLE_PARAMS'

export const AmountFormat = (value) => {
  return formatAmountWan(value)
}

export const getHeaderWithFunctionCode = ({ channel, humpPath }) => {
  const functionCode = ['PROC', 'EDIT'].includes(channel) ? humpPath : humpPath + 'Effect'
  return {
    headers: {
      functionCode,
    },
  }
}
