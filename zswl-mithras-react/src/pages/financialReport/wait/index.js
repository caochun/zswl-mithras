import { Table, Button, Modal, Form, Page, Select, App } from '@zswl/components'
import { Space, DatePicker, Input } from 'antd'
import { COMMON_COLUMNS } from '../Column'
import { useMemo } from 'react'
import Store, { tableConfig } from './store'
import DetailModal from './DetailModal'
import ReportModal from './ReportModal'
import { observer } from '@zswl/admin'

const FinancialReport = ({ listType = 'wait' }) => {
  const isWait = listType === 'wait'
  const store = useMemo(() => new Store({ listType }), [listType])
  const { rows, keys } = store.table.getSelected()

  const columns = [
    ...COMMON_COLUMNS({ listType, handleDetail: store.handleDetail }),
    isWait && {
      title: '操作',
      width: 80,
      fixed: 'right',
      actions: (record) => {
        const disabled = ['UNDER_APPROVAL'].includes(record.processStatus)
        const text =
          record.processStatus === 'APPROVAL_PASS' ? (
            <div style={{ color: 'red' }}>此报表数据已经审批通过，是否仍要删除？</div>
          ) : (
            '确认删除吗？'
          )
        return [
          {
            name: '删除',
            confirm: !disabled ? text : undefined,
            disabled,
            onClick: () => store.handleDelete(record),
          },
        ]
      },
    },
  ]

  const { associationReportStatusEnum } = App.getData().optionsType
  const reportStatusList = associationReportStatusEnum.filter((item) => item.value !== 'SUCCESS')
  const submitDisabled = !(
    rows.length > 0 &&
    rows.every((item) => ['UN_SUBMIT', 'CANCEL', 'APPROVAL_REJECT'].includes(item.processStatus))
  )
  return (
    <Page style={{ padding: '24px' }} onBack={null}>
      <div className="z-flex-jsb" style={{ marginBottom: 12 }}>
        <div></div>
        <Space>
          {isWait && (
            <Button.Submit
              type="primary"
              onClick={store.openReportModal}
              access={'associationreportpush'}
            >
              金融局上报
            </Button.Submit>
          )}
          {isWait && (
            <Button type="primary" onClick={store.submit} disabled={submitDisabled}>
              提交审批
            </Button>
          )}
        </Space>
      </div>
      <Table
        columns={columns}
        store={store.table}
        selectable={listType === 'wait'}
        scroll={{ x: 'max-content' }}
        searchbar={{
          items: [
            {
              label: '报表名称',
              dataIndex: 'reportCategoryCodeList',
              options: 'associationReportCategoryEnum',
              type: 'select',
              mode: 'multiple',
            },
            isWait && {
              label: '报送状态',
              dataIndex: 'reportStatusList',
              type: 'select',
              options: reportStatusList,
              mode: 'multiple',
            },
            {
              label: '周期类型',
              dataIndex: 'reportPeriodCategoryList',
              options: 'associationReportPeriodCategoryEnum',
              type: 'select',
              mode: 'multiple',
            },
            {
              label: '流程状态',
              dataIndex: 'processStatusList',
              options: 'associationProcessStatus',
              type: 'select',
              mode: 'multiple',
            },
          ].filter(Boolean),
        }}
        actions={[
          isWait && (
            <Button.Add type="primary" onClick={store.handleNewReport} style={{ marginRight: 8 }}>
              新增上报
            </Button.Add>
          ),
        ]}
      />
      <DetailModal store={store} listType={listType} />
      <ReportModal store={store} />
      <Modal title="新增上报" store={store.addModal} width={600} destroyOnClose>
        <Form>
          <Form.Item
            label="报表类型"
            name="reportCategoryCode"
            rules={[{ required: true, message: '请选择报表类型' }]}
          >
            <Select
              options="addAssociationReportCategoryEnum"
              onChange={store.handleChangeCategory}
            />
          </Form.Item>

          <Form.Item label="年份" name="year" rules={[{ required: true, message: '请选择年份' }]}>
            <DatePicker.YearPicker />
          </Form.Item>
          <Form.Item
            label="周期类型"
            name="periodCategory"
            rules={[{ required: true, message: '请选择周期类型' }]}
          >
            <Select options="associationReportPeriodCategoryEnum" disabled />
          </Form.Item>
          <Form.Item noStyle dependencies={['periodCategory']}>
            {({ getFieldValue }) => {
              const periodCategory = getFieldValue('periodCategory')
              if (['REALTIME'].includes(periodCategory)) {
                return (
                  <Form.Item
                    label="报表周期"
                    name="period"
                    rules={[{ required: true, message: '请选择报表周期' }]}
                  >
                    <Input disabled />
                  </Form.Item>
                )
              }
              const isQuarter = periodCategory === 'QUARTER'
              return (
                <Form.Item
                  label="报表周期"
                  name="period"
                  rules={[{ required: true, message: '请选择报表周期' }]}
                >
                  <Select options={isQuarter ? 'quarterEnum' : 'monthEnum'} />
                </Form.Item>
              )
            }}
          </Form.Item>
        </Form>
      </Modal>
    </Page>
  )
}

export default observer(FinancialReport)
