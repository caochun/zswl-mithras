import React from 'react'
import { Button, Page, Table, Tabs, SearchBar } from '@zswl/components'
import { observer, history, getQuery } from '@zswl/admin'
import { Input, Select, Badge, Checkbox } from 'antd'
import styles from './index.less'
import TopSection from './components/TopSection'
import VisualizationTwoD from './components/VisualizationTwoD'
import ListRed from '/public/assets/risk/monitoringAlertList/vector.svg'
import Frame from '/public/assets/risk/monitoringAlertList/Frame.svg'
import { ClientSelect, FounderSelect, OrgSelect } from '@/components/Select'
import { saveServer } from '@/utils'

import store from './store'
const { Item } = SearchBar

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

const StarLevel = ({ level }) => {
  const stars = Array(level).fill('★')
  return <span>{stars.join('')}</span>
}

function RiskMonitorEarly({ path, ...props }) {
  const { enterpriseName, tag } = getQuery()
  const onChange = (key) => {}

  const [directions, setDirections] = React.useState(['3', '2'])

  const handleCheckboxChange = (vals) => {
    setDirections(vals)
    store.warningList.setParams({ warnLevels: vals })
    store.warningList.search()
  }
  const columnsYj = [
    { title: '预警编号', dataIndex: 'warnCode', key: 'warnCode', width: 150 },
    {
      title: '预警标题',
      dataIndex: 'title',
      key: 'title',
      width: 350,
      render: (text, { linkAddress, dataSource }) => {
        if ('XINSIGHT' === dataSource) {
          return (
            <a
              onClick={() => {
                window.open(linkAddress)
              }}
            >
              {text}
            </a>
          )
        }
        return text
      },
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
      key: 'handleStatus',
      width: 250,
      render: (value) => {
        const label = store.riskControlOpinionHandleStatus.find((item) => item.value === value)?.label
        return <span>{label}</span>
      },
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
            history.push(`/process/receive/detail/${record.taskId}?typeId=approval&diff=taskId&curTab=pending`)
          }}
        >
          处理
        </Button>
      ),
    },
  ]
  const columnsYq = [
    {
      title: '舆情标题',
      dataIndex: 'title',
      key: 'title',
      width: 350,
      // render: (text, record) => (
      //   <a
      //     onClick={() => {
      //       history.push(`${path}/detail/${record.id}?tag=yj`)
      //     }}
      //   >
      //     {text}
      //   </a>
      // ),
    },
    { title: '统一社会信用代码', dataIndex: 'creditCode', key: 'creditCode', width: 250 },
    { title: '客户名称', dataIndex: 'chiName', key: 'chiName', width: 250 },
    {
      title: '重要度',
      dataIndex: 'warnStar',
      key: 'warnStar',
      width: 250,
      render: (level) => {
        const stars = Array(level).fill('★').join('')
        return <span style={{ color: '#faad14' }}>{stars}</span>
      },
    },
    {
      title: '预警级别',
      dataIndex: 'warnLevel',
      width: 250,
      key: 'warnLevel',
      render: (level) => <LightStatus level={level} />,
    },
    {
      title: '来源类型',
      width: 100,
      dataIndex: 'opinionType',
      editable: false,
    },
    {
      title: '创建人',
      width: 120,
      dataIndex: 'createName',
      editable: false,
    },
    {
      title: '关联关系描述',
      width: 120,
      dataIndex: 'relationTypeName',
      editable: false,
    },
    { title: '信息触发时间', width: 250, dataIndex: 'infoPublDate', key: 'infoPublDate' },
    {
      title: '处置状态',
      dataIndex: 'handleStatus',
      width: 250,
      key: 'handleStatus',
      render: (value) => {
        const label = store?.riskControlOpinionHandleStatus.find((item) => item.value === value)?.label
        return <span>{label}</span>
      },
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
            history.push(`/process/receive/detail/${record.taskId}?typeId=approval&diff=taskId&curTab=pending`)
          }}
        >
          处理
        </Button>
      ),
    },
  ]

  return (
    <Page store={store.page} noStyle className={styles.page}>
      <div className={styles.container}>
        <div className={styles.top}>
          <TopSection statistics={store.statistics} />
          <VisualizationTwoD pie={store.pie} fxsl={store.postQuantityChange} />
        </div>
        <div className={styles.content}>
          <Tabs
            defaultActiveKey={tag ? tag : '1'}
            onChange={onChange}
            items={[
              {
                label: `预警列表`,
                key: '1',
                children: (
                  <div style={{ padding: 10 }}>
                    <Table
                      columnsFilter={'pages_monitorEarly_1'}
                      onFilter={(key, val) => saveServer('pages_monitorEarly_1', val)}
                      scroll={{ x: 1700 }}
                      resizable
                      rowKey={(record) => record.id + '_' + Math.random()}
                      actions={[
                        <span>
                          预警级别：
                          <Checkbox.Group
                            options={[
                              { label: '红灯', value: '3' },
                              { label: '黄灯', value: '2' },
                            ]}
                            value={directions}
                            onChange={handleCheckboxChange}
                          />
                        </span>,
                      ]}
                      searchbar={{
                        labelCol: { span: 6 },
                        items: [
                          {
                            label: '预警编号',
                            name: 'warnCode',
                          },
                          {
                            label: '客户名称',
                            name: 'chiName',
                            element: <Input allowClear defaultValue={enterpriseName} value={enterpriseName} />,

                            allowClear: true,
                          },
                          // {
                          //   label: '所属部分',
                          //   name: 'bizType',
                          //   options: [],
                          //   allowClear: true,
                          // },
                          // <Item label="所属部门" name="belongDeptId" key="belongDeptId">
                          //   <OrgSelect functionCode="opinionWarnOrgSelect"></OrgSelect>
                          // </Item>,
                          {
                            label: '预警标题',
                            name: 'title',
                          },

                          {
                            label: '处置状态',
                            name: 'handleStatus',
                            options: store?.riskControlOpinionHandleStatus,
                            allowClear: true,
                          },
                        ],
                      }}
                      className="newTableWarningList"
                      store={store.warningList}
                      columns={columnsYj}
                    />
                  </div>
                ),
              },
              {
                label: `舆情列表`,
                key: '2',
                children: (
                  <div style={{ padding: 10 }}>
                    <Table
                      columnsFilter={'pages_monitorEarly_2'}
                      onFilter={(key, val) => saveServer('pages_monitorEarly_2', val)}
                      resizable
                      scroll={{ x: 1600 }}
                      searchbar={{
                        labelCol: { span: 6 },
                        items: [
                          {
                            label: '客户名称',
                            name: 'chiName',
                            element: <Input allowClear defaultValue={enterpriseName} value={enterpriseName} />,
                            allowClear: true,
                          },
                          {
                            label: '处置状态',
                            name: 'handleStatus',
                            options: store?.riskControlOpinionHandleStatus,
                            allowClear: true,
                          },
                        ],
                      }}
                      className="newTableWarningList"
                      store={store.monitorList}
                      columns={columnsYq}
                    />
                  </div>
                ),
              },
            ]}
          />
        </div>
      </div>
    </Page>
  )
}

export default observer(RiskMonitorEarly)
