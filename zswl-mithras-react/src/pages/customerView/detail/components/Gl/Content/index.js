import React, { useMemo, useState } from 'react'
import { Tabs } from 'antd'
import styles from './styles.less'
import { history, http, observer } from '@zswl/admin'
import { Table, TableStore } from '@zswl/components'
import xmzs from './imgs/xmzs.png'
import spjd from './imgs/spjd.png'
import spwtcct from './imgs/spwtcct.png'
import qyjd from './imgs/qyjd.png'
import fkjd from './imgs/fkjd.png'
import tfjd from './imgs/tfjd.png'
import hkjd from './imgs/hkjd.png'
import lxjdPng from './imgs/lxjd.png'
import { AmountColumn } from '@/components/Format'
import { saveServer } from '@/utils'

const { TabPane } = Tabs

const ICONS = {
  评审阶段: spjd,
  评审通过未创建合同: spwtcct,
  签约阶段: qyjd,
  付款阶段: fkjd,
  投放阶段: tfjd,
  还款阶段: hkjd,
  立项阶段: lxjdPng,
}

const ProjectStage = ({ store, id, clientId }) => {
  const [activeTab, setActiveTab] = useState('1')

  const cards = []

  store.projectStatisticsListCard.forEach((item) => {
    if (item.groupCode !== 'PROJECT_VIEW_STAGE_REVIEW_NO_LEGAL_REPORT') {
      cards.push({
        title: item.group,
        count: item.quantity,
        icon: ICONS[item.group] || 'nop',
      })
    }
  })

  const total = cards.reduce((prev, curr) => prev + curr.count, 0)

  const tableData = {
    1: [
      {
        key: '1',
        name: '统一视图项目001',
        amount: '100,000.00',
        stage: '签约阶段',
        department: '浙江业务部',
      },
      {
        key: '2',
        name: '统一视图项目002',
        amount: '100,000.00',
        stage: '立项阶段',
        department: '浙江业务部',
      },
      {
        key: '3',
        name: '统一视图项目003',
        amount: '100,000.00',
        stage: '投放阶段',
        department: '浙江业务部',
      },
      {
        key: '4',
        name: '统一视图项目004',
        amount: '100,000.00',
        stage: '投放阶段',
        department: '浙江业务部',
      },
    ],
  }

  const tableColumns1 = [
    {
      title: '项目名称',
      dataIndex: 'projName',
      key: 'projName',
      width: 220,
      render: (text, record) => (
        <a
          onClick={() => {
            history.push(`/project/establishment/detail/${record.id}?bizType=${record?.bizType}`)
          }}
        >
          {text}
        </a>
      ),
    },
    AmountColumn({
      title: '授信金额',
      dataIndex: 'applyCreditAmount',
      initFormat: 10000,
      width: 140,
    }),
    {
      title: '项目阶段',
      dataIndex: 'projectStage',
      key: 'projectStage',
      width: 120,
      render: (text) => {
        return store.dashboardCardGroupEnum.filter((item) => item.value === text)[0].label
      },
    },
    { title: '所属部门', dataIndex: 'belongDeptName', key: 'belongDeptName', width: 140 },
  ]

  const tableColumns2 = [
    {
      title: '项目名称',
      dataIndex: 'contractCode',
      key: 'contractCode',
      render: (text, record) => {
        return (
          <a
            onClick={() => {
              history.push(`/contract/list/detail/${record.id}`)
            }}
          >
            {text}
          </a>
        )
      },
    },
    AmountColumn({
      title: '授信金额',
      dataIndex: 'applyCreditAmount',
      key: 'applyCreditAmount',
      initFormat: 10000,
    }),
    { title: '合同状态', dataIndex: 'contractStatus', matchOption: 'contractStatus' },
    { title: '所属部门', dataIndex: 'belongDeptName', key: 'belongDeptName' },
  ]

  const tableColumns3 = [
    {
      title: '项目',
      dataIndex: 'projName',
      key: 'projName',
    },
    {
      title: '合同名称',
      dataIndex: 'contractCode',
      key: 'contractCode',
      width: 300,
      render: (text, record) => {
        return (
          <a
            onClick={() => {
              history.push(`/contract/list/detail/${record.contractId}`)
            }}
          >
            {text}
          </a>
        )
      },
    },
    { title: '本期应收日期', dataIndex: 'planCollectionDate', key: 'planCollectionDate' },
    AmountColumn({
      title: '本期应收金额',
      dataIndex: 'planCollectionAmount',
      key: 'planCollectionAmount',
    }),
    { title: '期项', dataIndex: 'phase', key: 'contractStatus' },
  ]
  const beOverdueTable = useMemo(() => {
    return new TableStore({
      request: async (params) => {
        const res = await http.post('/client/unified/view/overdue/rent', {
          ...params,
          clientId,
        })
        return res
      },
    })
  }, [])
  return (
    <>
      <div id={id} className={styles['title']}>
        项目阶段
      </div>
      <div className={styles.rightSection}>
        <Tabs
          activeKey={activeTab}
          onChange={setActiveTab}
          tabBarExtraContent={
            <a href={`/dashboard/workbench?openModal=PROJECT_VIEW_INFO_RENT_IN_MONTH`}>
              查看详情 &gt;
            </a>
          }
          tabBarStyle={{ padding: '0 10px' }}
        >
          <TabPane tab="项目列表" key="1" />
          <TabPane tab="合同列表" key="2" />
          <TabPane tab="最近一期待偿还租金" key="3" />
        </Tabs>
        {activeTab === '1' && (
          <Table
            columnsFilter={'Gl_Content_1'}
            onFilter={(key, val) => saveServer('Gl_Content_1', val)}
            resizable
            store={store.projectListStore}
            className="newTableWarningList"
            dataSource={tableData[activeTab]}
            columns={tableColumns1}
            pagination={false}
            columnWidth={120}
            scroll={{ x: 'auto' }}
          />
        )}

        {activeTab === '2' && (
          <Table
            columnsFilter={'Gl_Content_2'}
            onFilter={(key, val) => saveServer('Gl_Content_2', val)}
            resizable
            store={store.contractListStore}
            className="newTableWarningList"
            dataSource={tableData[activeTab]}
            columns={tableColumns2}
            columnWidth={120}
            scroll={{ x: 'auto' }}
            pagination={false}
          />
        )}
        {activeTab === '3' && (
          <Table
            columnsFilter={'Gl_Content_3'}
            onFilter={(key, val) => saveServer('Gl_Content_3', val)}
            resizable
            store={beOverdueTable}
            className="newTableWarningList"
            columns={tableColumns3}
            scroll={{ x: 'auto' }}
            rowClassName={(record) => {
              if (record.overdueFlag) return 'red-row'
            }}
            pagination={false}
          />
        )}
      </div>
      <div className={styles.container}>
        {/* 左侧卡片部分 */}
        <div className={styles.leftSection}>
          {/* 小卡片 */}
          <div className={styles.cardsContainer}>
            <div className={styles.card}>
              <div className={styles.icon}>
                <img src={xmzs} alt={'项目总数'} />
              </div>
              <div className={styles.content}>
                <div className={styles.count}>{total}</div>
                <div className={styles.title}>{'项目总数'}</div>
              </div>
            </div>

            {cards.map((item, index) => (
              <div key={index} className={styles.card}>
                <div className={styles.icon}>
                  <img src={item.icon} alt={item.title} />
                </div>
                <div className={styles.content}>
                  <div className={styles.count}>{item.count}</div>
                  <div className={styles.title}>{item.title}</div>
                </div>
              </div>
            ))}
          </div>
        </div>
        {/* 右侧表格部分 */}
      </div>
    </>
  )
}

export default observer(ProjectStage)
