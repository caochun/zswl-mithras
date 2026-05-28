import { observer, history } from '@zswl/admin'
import { Table } from '@zswl/components'
import { Typography } from 'antd'
import store from './store'
import { saveServer } from '@/utils'

function RelatedPartyInformation({ id }) {
  const columns = [
    {
      title: '序号',
      dataIndex: 'index',
      key: 'index',
      width: 80,
      align: 'center',
      render: (text, record, index) => index + 1,
    },
    {
      title: '关联人名称',
      dataIndex: 'name',
      key: 'name',
      width: 300,
      render: (text, r) => {
        if (!text) return '-'
        return r?.type == 2 ? (
          <a
            onClick={(e) => {
              history.push(`/customerView/detail/?enterpriseName=${r.name}&uscc=${r.code}`)
            }}
          >
            {text}
          </a>
        ) : (
          text
        )
      },
    },
    {
      title: '关联人类型',
      dataIndex: 'nodeCategory',
      key: 'nodeCategory',
      width: 200,
      align: 'center',
      render: (text) => {
        if (!text) return '-'
        switch (text) {
          case 'ac':
            return '疑似实控人'
          case 'be':
            return '最终受益人'
          case 'up':
            return '上级股东'
          case 'down':
            return '对外投资'
          case 'director':
            return '高管'
          default:
            return text
        }
      },
    },
    {
      title: '持股比例',
      dataIndex: 'ratio',
      key: 'ratio',
      width: 150,
      align: 'center',
      render: (text) => text || '-',
    },
    {
      title: '黑灰标志',
      dataIndex: 'blackFlag',
      key: 'blackFlag',
      width: 150,
      align: 'center',
      render: (text) => {
        if (text === undefined || text === null || text === '') return '-'
        return <span style={{ color: text === 1 ? 'red' : 'inherit' }}>{text}</span>
      },
    },
    {
      title: '票据逾期',
      dataIndex: 'billOverdue',
      key: 'billOverdue',
      width: 150,
      align: 'center',
      render: (text) => {
        if (text === undefined || text === null || text === '') return '-'
        return <span style={{ color: text > 0 ? 'red' : 'inherit' }}>{text}</span>
      },
    },
    {
      title: '法律诉讼',
      dataIndex: 'legalAction',
      key: 'legalAction',
      width: 150,
      align: 'center',
      render: (text) => {
        if (text === undefined || text === null || text === '') return '-'
        return <span style={{ color: text > 0 ? 'red' : 'inherit' }}>{text}</span>
      },
    },
    {
      title: '经营风险',
      dataIndex: 'bizRisk',
      key: 'bizRisk',
      width: 150,
      align: 'center',
      render: (text) => {
        if (text === undefined || text === null || text === '') return '-'
        return <span style={{ color: text > 0 ? 'red' : 'inherit' }}>{text}</span>
      },
    },
    {
      title: '负面舆情',
      dataIndex: 'negativePo',
      key: 'negativePo',
      width: 150,
      align: 'center',
      render: (text) => {
        if (text === undefined || text === null || text === '') return '-'
        return <span style={{ color: text > 0 ? 'red' : 'inherit' }}>{text}</span>
      },
    },
  ]

  return (
    <div>
      <Table         columnsFilter={'detail_components_GlGx_1'}
              onFilter={(key,val) => saveServer('detail_components_GlGx_1',val)} resizable store={store.table} columns={columns} bordered />
    </div>
  )
}
export default observer(RelatedPartyInformation)
