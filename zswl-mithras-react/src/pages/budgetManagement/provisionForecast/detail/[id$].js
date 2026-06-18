import React, { useState, useEffect, useMemo } from 'react'
import { observer } from '@zswl/admin'
import { Page, Table } from '@zswl/components'
import { Steps, Card, Form, Input, DatePicker, Button, Row, Col, Space, message } from 'antd'
import store from './store'
import './index.less'
import ParamsConfig from './ParamsConfig'
import {
  BudgetProvisioningDataAddModal as AddModal,
  BudgetProvisioningImpairmentColumns as ALL_COLUMNS,
} from '@/components/Budget/BudgetEntries'
import { getTableColumns } from '@/utils'
import { AmountColumn, DateColumn } from '@/components/Format'
import TableExport from '@/components/Actions/TableExport'

const { Step } = Steps

const nameColumns = [
  DateColumn({
    title: '测算时间',
    dataIndex: 'updateTime',
    dateFormat: 'YYYY-MM-DD HH:mm:ss',
    search: true,
  }),
  {
    title: '合同号',
    search: {
      element: <Input />,
    },
  },
  {
    title: '客户名称',
    dataIndex: 'clientName',
    search: {
      element: <Input />,
    },
  },
  '是否逾期',
  '计算月份',
  '合同到期日',
  '评估主体名称',
  '借据编号',
  '内评级别',
  '内评违约概率',
  '外评级别',
  '违约概率PD',
  '所属分组',
  '五级分类',
  '逾期天数',
  '租赁物类型',
  AmountColumn({ title: '剩余本金', dataIndex: 'remainPrincipal', initFormat: 1 }),
  AmountColumn({ title: '应计利息', dataIndex: 'accruedInterest', initFormat: 1 }),
  AmountColumn({ title: '保证金', dataIndex: 'deposit', initFormat: 1 }),
  'EAD',
  '债项阶段',
  '上迁债项阶段',
  '期限调整系数T',
  '前瞻因子Z',
  '情景权重',
  '基准PDforward',
  '乐观PDforward',
  '悲观PDforward',
  '基准PDIFRS9',
  '乐观PDIFRS9',
  '悲观PDIFRS9',
  '基准ECL',
  '乐观ECL',
  '悲观ECL',
  'ECL',
  AmountColumn({ title: '下期租金', dataIndex: 'nextRent', initFormat: 1 }),
  '备注',
]
const COLUMNS = getTableColumns(ALL_COLUMNS(), nameColumns, true)
/**
 * 设备预测计划详情页面组件
 * 包含参数配置、数据校对、模拟拨备计提三个步骤
 */
const ProvisionForecastDetail = observer(({ params }) => {
  const { id } = params
  const [currentStep, setCurrentStep] = useState(0)

  /**
   * 处理步骤切换
   */
  const handleStepChange = (step) => {
    setCurrentStep(step)
  }

  /**
   * 渲染数据校对步骤
   */
  const renderDataCalibration = () => (
    <>
      <Table
        store={store.dataTable}
        columns={[
          ...COLUMNS,
          {
            title: '操作',
            dataIndex: 'action',
            width: 120,
            fixed: 'right',
            actions: (record) => [
              {
                name: '修改',
                onClick: () => store.openAddModal(record),
              },
              { name: '删除', onClick: () => store.itemDelete(record), confirm: true },
            ],
          },
        ]}
        rowKey="id"
        columnWidth={180}
        actions={[
          <Button type="primary" onClick={store.openAddModal}>
            新增数据
          </Button>,
        ]}
      />
      <AddModal store={store} />
    </>
  )

  /**
   * 渲染模拟拨备计提步骤
   */
  const RenderProvisionSimulation = observer(() => (
    <Table
      store={store.provisionTable}
      columns={COLUMNS}
      rowKey="id"
      columnWidth={180}
      scroll={{ x: 'auto' }}
      actions={[
        <Button type="primary" onClick={store.handleProvisionSimulation}>
          测算
        </Button>,
      ]}
      extra={
        <TableExport
          table={store.provisionTable}
          otherExcelProps={{ fileName: '模拟拨备计提数据' }}
        />
      }
    />
  ))

  // 步骤内容映射
  const stepContents = [
    <ParamsConfig store={store} />,
    renderDataCalibration(),
    <RenderProvisionSimulation />,
  ]

  return (
    <Page store={store.page} className="provision-forecast-detail" params={{ id }}>
      <div className="steps-container">
        <Steps current={currentStep} onChange={handleStepChange}>
          <Step title="参数配置" />
          <Step title="数据校对" />
          <Step title="模拟拨备计提" />
        </Steps>
      </div>

      <div className="step-content">{stepContents[currentStep]}</div>
    </Page>
  )
})

export default ProvisionForecastDetail
