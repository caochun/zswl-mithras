import { observer } from '@zswl/admin'
import { Form, Select } from '@zswl/components'
import { Checkbox, DatePicker, Radio, Space } from 'antd'
import moment from 'moment'
const { Item } = Form
export const defaultParams = {
  searchMode: 'THREE_YEAR',
  quarter: '12',
  latest: true,
  year: [moment().subtract(3, 'year'), moment().subtract(1, 'year')],
}
const CustomerFinancialReportSearch = ({ searchStore, canEdit = true }) => {
  const modeChange = (e) => {
    if (e.target.value === 'THREE_YEAR') {
      searchStore.setParams(defaultParams)
    }
  }
  return (
    <>
      <Item label={'查询模式'} name={'searchMode'}>
        <Radio.Group
          onChange={modeChange}
          options={[
            { label: '三年一期', value: 'THREE_YEAR' },
            { label: '自定义报告期', value: 'BASE' },
          ]}
        />
      </Item>
      <Item noStyle dependencies={['searchMode']}>
        {({ getFieldValue }) => {
          const searchMode = getFieldValue('searchMode')

          return (
            <Space style={{ display: searchMode === 'BASE' ? 'flex' : 'none' }}>
              <Item label={'报告期'} name={'quarter'}>
                <Select
                  options="subjectQuarterType"
                  style={{ minWidth: '118px' }}
                  disabled={!canEdit}
                />
              </Item>
              <Item label={'最新'} name={'latest'} valuePropName="checked">
                <Checkbox disabled={!canEdit} />
              </Item>
              <Item label={'时间选择'} name={'year'}>
                <DatePicker.RangePicker
                  picker={'year'}
                  style={{ width: '186px' }}
                  disabled={!canEdit}
                />
              </Item>
            </Space>
          )
        }}
      </Item>
    </>
  )
}

export default observer(CustomerFinancialReportSearch)
