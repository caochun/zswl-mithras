import { getSearchColumns, getTableColumns, saveServer } from '@/utils'
import { observer } from '@zswl/admin'
import { Table } from '@zswl/components'
import { useEffect } from 'react'
import styles from '../Financial/index.less'
import ALL_COLUMNS from './Column'
import store from './store'

// 表格列配置
const nameColumns = [
  '序号',
  '业务部门',
  '客户名称',
  '项目名称',
  '项目编号',
  '合同编号',
  '项目分类',
  '项目主办',
  '档案复核人',
  '流程ID',
  '是否归档超期',
  '是否补充材料超期',
  '全流程耗时（工作日）',
  '档案管理初审耗时（工作日）',
  '档案管理复核耗时（工作日）',
  '发起时间',
  '结束时间',
  '应归档日',
  '项目主办提交时间',
  '档案管理初审提交时间',
  '档案管理复核提交时间',
  '档案管理初审退回次数',
  '档案管理复核退回次数',
  '档案管理初审退回原因',
  '档案管理复核退回原因',
]

// 搜索表单配置
const formNameColumns = [
  '客户名称',
  '项目编号',
  '项目名称',
  '合同编号',
  '业务部门',
  '项目分类',
  '项目主办',
  '档案复核人',
  '流程ID',
  '是否归档超期',
  '是否补充材料超期',
  '发起时间',
  '结束时间',
]

const columns = getTableColumns(ALL_COLUMNS, nameColumns)
const formColumns = getSearchColumns(ALL_COLUMNS, formNameColumns)

function Index() {
  useEffect(() => {
    // 设置导出列配置
    store.setExportColumns(columns)
  }, [])

  return (
    <div className={styles.customerWrap}>
      <Table
        columnsFilter={'archives_project_tab'}
        onFilter={(key, val) => saveServer('archives_project_tab', val)}
        selectable
        rowKey={(record, index) => `${record.processInstanceId}_${record.projCode}_${index}`}
        store={store.table}
        editable={false}
        searchbar={{
          labelCol: { span: 10 },
          items: formColumns,
        }}
        actions={[
          {
            name: '批量导出',
            onClick: store.exportData,
          },
        ]}
        scroll={{
          x: 3500,
        }}
        columns={columns}
      />
    </div>
  )
}

export default observer(Index)
