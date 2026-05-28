import { App, Page, Table } from '@zswl/components'
import { observer, toJS } from '@zswl/admin'
import { Tooltip, Space, Tag } from 'antd'
import store from './store'
import { useMemo } from 'react'
import api from './api'
import styles from './index.less'
import { saveServer } from '@/utils'

const readFlagEnum = [
  {
    label: '全部',
    value: '',
  },
  {
    label: '已读',
    value: 1,
  },
  {
    label: '未读',
    value: 2,
  },
]
function MsgNotification() {
  const readMessage = async (id) => {
    await api.postReadMessage({
      messageChannel: 'PC',
      noticeIds: [id],
    })
  }
  const columns = useMemo(() => {
    return [
      {
        title: '状态',
        dataIndex: 'readFlag',
        width: 80,
        resizable: true,
        render: (val, { overtimeFlag }) => {
          return val ? (
            <div className={styles.status1}>已读</div>
          ) : (
            <div className={styles.status}>未读</div>
          )
        },
      },
      {
        title: <div>通知时间</div>,
        dataIndex: 'gmtCreate',
        width: 250,
        render: (val, { overtimeFlag, type }) => {
          return <div style={{ color: overtimeFlag == 1 && type === 3 ? 'red' : '' }}>{val}</div>
        },
      },
      {
        title: '通知类型',
        width: 130,
        dataIndex: 'type',
        resizable: true,
        render: (_, { bizInfo }) => {
          const { other } = bizInfo ? JSON.parse(bizInfo) : {}
          return other?.messageType || '-'
        },
      },
      {
        title: '通知内容',
        dataIndex: 'title',
        resizable: true,
        render: (val) => {
          return (
            <Tooltip title={val?.replace(/\\n/g, '\n')} placement="left">
              {val}
            </Tooltip>
          )
        },
      },
      {
        title: '查看',
        dataIndex: 'content',
        resizable: true,
        render: (content, { bizInfo, id, overtimeFlag, type }) => {
          const { other } = bizInfo ? JSON.parse(bizInfo) : {}
          return (
            <Space>
              <a
                onClick={async () => {
                  await readMessage(id)
                  if (other?.messageType?.includes('待发起')) {
                    window.location.href = `${unescape(other?.businessUrl)}`
                  } else {
                    window.location.href = `${unescape(other?.businessUrl)}&tab=sendback`
                  }
                }}
              >
                <Tooltip title={content} placement="top">
                  {content || '-'}
                </Tooltip>
              </a>
              {overtimeFlag === 1 && type === 3 && <Tag color="red">已超时</Tag>}
            </Space>
          )
        },
      },
    ]
  }, [])

  return (
    <Page>
      <Table
        onFilter={(key, val) => saveServer('消息通知', val)}
        rowClassName={(record) => {
          return record.overtimeFlag == 1 && record.type === 3 ? styles.overtime : undefined
        }}
        resizable
        store={store.msgTable}
        columnsFilter={'消息通知'}
        searchbar={{
          labelCol: { span: 6 },
          items: [
            {
              label: '状态',
              name: 'needRead',
              options: readFlagEnum,
            },
            {
              label: '通知时间',
              name: 'time',
              type: 'rangePicker',
            },
            {
              label: '通知类型',
              name: 'messageType',
              options: 'messageTypeEnum',
              fieldNames: { label: 'label', value: 'label' },
            },
          ],
        }}
        columns={columns}
        actions={[
          {
            name: '全部已读',
            onClick: store.readmsgAll,
            type: 'primary',
          },
        ]}
      />
    </Page>
  )
}

export default observer(MsgNotification)
