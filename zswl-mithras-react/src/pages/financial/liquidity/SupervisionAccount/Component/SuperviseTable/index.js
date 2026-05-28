import { Form, Input, Table, Flex, App } from '@zswl/components'
import { observer } from '@zswl/admin'
import { FiledFormat, AmountFormat, MatchFormat } from '@/components/Format'
import { pieColors } from '../../utils'
import { Space } from 'antd'
import { saveServer } from '@/utils'

// 待转资金表格列配置
const transferColumns = [
  {
    title: '沉淀时间(工作日)',
    dataIndex: 'settingTime',
    render: (value, record, index) => {
      return (
        <Space>
          <div
            style={{ width: 8, height: 8, borderRadius: '50%', background: pieColors[index] }}
          ></div>
          <span>{App.matchOption('settingTimeTypeEnum', value)?.label}</span>
        </Space>
      )
    },
  },
  {
    title: '占比',
    dataIndex: 'payAmount',
    align: 'right',
    render: (value) => {
      return <div>{`${value?.value}${value?.unit}`}</div>
    },
  },
  {
    title: '沉淀金额(元)',
    dataIndex: 'depositedAmount',
    align: 'right',
    render: (value) => AmountFormat({ value }),
  },
]

const Index = ({ pieData }) => {
  return (
    <div>
      <Table         columnsFilter={'Component_SuperviseTable_1'}
              onFilter={(key, val) => saveServer('Component_SuperviseTable_1', val)} columns={transferColumns} dataSource={pieData} pagination={false} />
    </div>
  )
}

export default observer(Index)
