import { App, Button, Page, Table } from '@zswl/components'
import { observer } from '@zswl/admin'
import store from './store'
import { DateColumn, MatchOptionColumn } from '@/components/Format'
import { Tag, Tooltip } from 'antd'
import { saveServer } from '@/utils'

const INTERFACE_STATUS_OPTIONS = [
  { value: '已推送', label: '已推送', color: 'green' },
  { value: '已删除', label: '已删除', color: 'red' },
  { value: '推送失败', label: '推送失败', color: '#a7a7a9' },
]

const ISDONE_OPTIONS = [
  { value: 1, label: '已推送' },
  { value: 0, label: '推送失败' },
]
export const JSONRender = (text) => {
  const title = <pre>{JSON.stringify(JSON.parse(text), null, 2)}</pre>
  if (text) {
    return (
      <Tooltip
        title={title}
        placement="topLeft"
        overlayInnerStyle={{ width: 500, overflowY: 'auto', maxHeight: 350 }}
        getPopupContainer={() => document.body}
      >
        <div className="z-single-line" style={{ maxWidth: 200 }}>
          {text}
        </div>
      </Tooltip>
    )
  }
}
function Index({ path }) {
  const columns = [
    MatchOptionColumn({
      title: '苍穹单据类型',
      dataIndex: 'billType',
      matchOption: 'cqBillTypeEnum',
      search: true,
      width: 140,
    }),
    {
      title: '单据编号',
      dataIndex: 'businessId',
      search: true,
      width: 400,
    },
    { title: '情况说明', dataIndex: 'situationDescription' },
    { title: '关联流水类型', dataIndex: 'sourceName' },
    { title: '关联 ID', dataIndex: 'businessTitle' },
    DateColumn({
      title: '更新时间',
      dataIndex: 'updateTime',
      search: true,
      dateFormat: 'yyyy-MM-DD HH:mm:ss',
    }),
    {
      title: '接口状态',
      dataIndex: 'status',
      width: 150,
      render: (text) => {
        const find = App.matchOption(INTERFACE_STATUS_OPTIONS, text) ?? {}
        return <Tag color={find.color}>{find.label}</Tag>
      },
    },
    {
      title: '请求参数',
      dataIndex: 'reqJson',
      width: 150,
      render: JSONRender,
    },
    { title: '返回参数', dataIndex: 'resJson', render: JSONRender, width: 150 },
    { title: '失败消息', dataIndex: 'withdrawFailMessage' },

    {
      title: '操作',
      width: 140,
      actions: (record) => {
        const canEdit = !record.isDone
        return [
          {
            name: '重新推送',
            onClick: () => canEdit && store.rePush(record),
            disabled: !canEdit,
            confirm: canEdit,
          },
          {
            name: '忽略',
            onClick: () => canEdit && store.rebuildingMasses(record),
            disabled: !canEdit,
            confirm: canEdit,
          },
        ]
      },
    },
  ]

  return (
    <Table
    columnsFilter="flowCenter_InterfaceManagement_1"
            onFilter={(key,val) => saveServer('flowCenter_InterfaceManagement_1',val)}
    
      store={store.table}
      editable={false}
      resizable
      columnWidth={120}
      scroll={{ x: 'auto' }}
      columns={columns}
      searchbar={{
        initialValues: {
          isDone: 0,
        },
        items: [
          MatchOptionColumn({
            title: '接口状态',
            dataIndex: 'isDone',
            matchOption: ISDONE_OPTIONS,
            search: true,
          }),
        ],
      }}
    />
  )
}

export default observer(Index)
