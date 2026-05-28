import { Table, Button, Modal, Form, DatePicker, SearchBar, Select } from '@zswl/components'
import { useState } from 'react'
import { observer } from '@zswl/admin'
import { COMMON_COLUMNS } from '../Column'

const monthEnum = [
  { label: '1月', value: 1 },
  { label: '2月', value: 2 },
  { label: '3月', value: 3 },
  { label: '4月', value: 4 },
  { label: '5月', value: 5 },
  { label: '6月', value: 6 },
  { label: '7月', value: 7 },
  { label: '8月', value: 8 },
  { label: '9月', value: 9 },
  { label: '10月', value: 10 },
  { label: '11月', value: 11 },
  { label: '12月', value: 12 },
]
const realTimeEnum = [{ label: '-', value: null }]
const quarterEnum = [
  { label: '1季度', value: 1 },
  { label: '2季度', value: 2 },
  { label: '3季度', value: 3 },
  { label: '4季度', value: 4 },
]

const reportPeriodMap = {
  REALTIME: realTimeEnum,
  MONTH: monthEnum,
  QUARTER: quarterEnum,
}
/**
 * 金融局上报弹窗组件
 * 显示已审批的数据表格供用户选择后进行上报
 */
const ReportModal = ({ store }) => {
  // 表格列配置，去掉操作列
  const columns = COMMON_COLUMNS({
    listType: 'approved',
    handleDetail: store.handleDetail, // 在上报弹窗中不需要详情功能
  })
  const searchStore = store.approvalTable.getSearchStore()
  const form = searchStore.getFormStore()

  console.log('form: ', form)
  const reportPeriodChange = (value) => {
    form.setFieldsValue({
      reportPeriod: null,
    })
  }
  return (
    <Modal
      title="金融局上报 - 选择已审批数据"
      store={store.reportModal}
      width={1200}
      destroyOnClose
    >
      <Form>
        <Table
          columns={columns}
          store={store.approvalTable}
          selectable
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
              <SearchBar.Item name={'reportPeriodCategory'} label="周期类型">
                <Select
                  options={'associationReportPeriodCategoryEnum'}
                  onChange={reportPeriodChange}
                />
              </SearchBar.Item>,

              <SearchBar.Item noStyle dependencies={['reportPeriodCategory']}>
                {({ getFieldValue }) => {
                  const reportPeriodCategory = getFieldValue('reportPeriodCategory')
                  console.log('reportPeriodCategory: ', reportPeriodCategory)
                  const reportPeriodEnum = reportPeriodMap[reportPeriodCategory] || []
                  return (
                    <SearchBar.Item name={'reportPeriod'} label="报表周期">
                      <Select options={reportPeriodEnum} />
                    </SearchBar.Item>
                  )
                }}
              </SearchBar.Item>,
            ].filter(Boolean),
          }}
        />
      </Form>
    </Modal>
  )
}

export default observer(ReportModal)
