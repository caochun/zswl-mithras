import { Page, Table, App, SearchBar, Select } from '@zswl/components'
import { observer } from '@zswl/admin'
import { OrgSelect, OrgSelectZh } from '@/components/Select'
import { DatePicker } from 'antd'
import moment from 'moment'
import PageListDown from '@/components/PageListDown'
import { useMemo } from 'react'
import Store from './store'
import ALL_COLUMNS from './Column'
import { getTableColumns, getFormColumns } from '@/utils'
import { saveServer } from '@/utils'
import { useState } from 'react'

const nameColumns = [
  '租后检查计划名称',
  '客户名称',
  '计划类型',
  '项目主办',
  '业务部门',
  '资产经理',
  '检查形式',
  '检查报告模板',
  '租后检查截止日',
  '现场检查日期',
  '报告提交日期',
  '当前状态',
  '是否逾期',
  '逾期天数',
]
const formNameColumns = ['业务部门', '当前状态', '是否逾期']
const { Item } = SearchBar
const columns = getTableColumns(ALL_COLUMNS, nameColumns)
const formColumns = getFormColumns(ALL_COLUMNS, formNameColumns)
function Index() {
  const { optionsType } = App.getData()
  const store = useMemo(() => {
    return new Store({})
  }, [])
  const deptIdList = useMemo(() => store.deptIdList, [store.deptIdList])
  return (
    <Page store={store}>
      <Table
        columnWidth={180}
        store={store.$table}
        editable={false}
        searchbar={{
          labelCol: { span: 8 },
          // items: [...formColumns],
          items: [
            <Item label="业务部门" name="belongDeptId" key="belongDeptId">
              <OrgSelectZh
                functionCode="selectorgs-adjust"
                valueType="array"
                filterKeys={deptIdList}
              ></OrgSelectZh>
            </Item>,
            {
              label: '当前状态',
              name: 'checkStatus',
              options: optionsType.checkStatus || [],
              allowClear: true,
            },
            <Item label="是否逾期" name="overdue" key="overdue">
              <Select
                options={[
                  { value: '0', label: '否' },
                  { value: '1', label: '是' },
                ]}
              />
            </Item>,
            <Item label="租后检查截止日" name="deadLineFrom" key="deadLineFrom">
              <MyDatePicker />
            </Item>,
          ],
        }}
        extra={[<PageListDown key="1" module="manageLedger" table={store.$table} />]}
        scroll={{
          x: 1500,
        }}
        columns={[...columns]}
        columnsFilter={'policyManage_list'}
        onFilter={(key, val) => saveServer('policyManage_list', val)}
      />
    </Page>
  )
}

const MyDatePicker = ({ value, onChange, placeholder, style }) => {
  const [date, setDate] = useState(value)
  const handleDateChange = (dateObj) => {
    setDate(dateObj)
    const formatDate = dateObj
      ? [moment(dateObj[0]).format('YYYY-MM-DD'), moment(dateObj[1]).format('YYYY-MM-DD')]
      : []
    onChange(formatDate)
  }

  return (
    <DatePicker.RangePicker
      style={style}
      format="YYYY-MM-DD"
      value={date}
      onChange={handleDateChange}
      // placeholder={placeholder}
    />
  )
}

export default observer(Index)
