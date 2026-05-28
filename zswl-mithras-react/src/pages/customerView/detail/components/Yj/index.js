import React, { useMemo } from 'react'
import { Button, Page, Table, Tabs, SearchBar, TableStore } from '@zswl/components'
import { observer, history, getQuery } from '@zswl/admin'
import { Input, Select, Badge, Checkbox } from 'antd'
import styles from './index.less'
import ListRed from '/public/assets/risk/monitoringAlertList/vector.svg'
import Frame from '/public/assets/risk/monitoringAlertList/Frame.svg'
import { saveServer } from '@/utils'
import Api from '@/pages/monitorEarly/api.js'

// 预警信号灯组件
const LightStatus = ({ level }) => {
  let icon = <Frame /> // 默认黄灯图标
  let text = '黄灯'
  let className = styles.yellowText

  if (level === 3) {
    icon = <ListRed /> // 红灯图标
    text = '红灯'
    className = styles.redText
  } else if (level === 1) {
    text = '绿灯'
    className = styles.greenText
  }

  return (
    <div className={className}>
      {icon}
      <div style={{ marginLeft: 3 }}>{text}</div>
    </div>
  )
}

function Index({ path, ...props }) {
  const { enterpriseName, tag } = getQuery()

  const warningList = useMemo(
    () =>
      new TableStore({
        request: async (params) => {
          return await Api.postWarnlist(
            { chiName: enterpriseName, params },
            'riskWarnMonitorWarnListCustomerView'
          )
        },
      }),
    [enterpriseName]
  )

  const columnsYj = [
    { title: '预警编号', dataIndex: 'warnCode', key: 'warnCode', width: 150 },
    {
      title: '预警标题',
      dataIndex: 'title',
      key: 'title',
      width: 350,
    },

    { title: '客户名称', dataIndex: 'chiName', key: 'chiName', width: 300 },
    {
      title: '预警级别',
      width: 120,
      dataIndex: 'warnLevel',
      key: 'warnLevel',
      render: (level) => <LightStatus level={level} />,
    },
    { title: '统一社会信用代码', dataIndex: 'creditCode', key: 'creditCode', width: 250 },
    { title: '预警时间', dataIndex: 'dataTime', key: 'dataTime', width: 250 },
    {
      title: '处置状态',
      dataIndex: 'handleStatus',
      matchOption: 'riskControlOpinionHandleStatus',
    },
    {
      title: '操作',
      key: 'action',
      width: 90,
      render: (text, record) => (
        <Button
          type="link"
          size="small"
          style={{ padding: 0 }}
          disabled={!record.taskId || !record.operableFlag}
          onClick={() => {
            history.push(
              `/process/receive/detail/${record.taskId}?typeId=approval&diff=taskId&curTab=pending`
            )
          }}
        >
          处理
        </Button>
      ),
    },
  ]

  return (
    <Page noStyle className={styles.page}>
      <Table
        columnsFilter={'pages_customerView_yj'}
        onFilter={(key, val) => saveServer('pages_customerView_yj', val)}
        scroll={{ x: 1700 }}
        resizable
        className="newTableWarningList"
        store={warningList}
        columns={columnsYj}
      />
    </Page>
  )
}

export default observer(Index)
