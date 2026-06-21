import { InputEditable } from '@/components/Format'
import { getFormColumns, getTableColumns, saveServer } from '@/utils'
import { observer } from '@zswl/admin'
import { Page, Table } from '@zswl/components'
import ALL_COLUMNS from './Column'
import FileModal from './FileModal'
import styles from './index.less'
import store from './store'

// 表格列配置（序号列在 Column.js 中按 page/pageSize 计算）
const nameColumns = [
  '序号',
  '项目名称',
  '项目编号',
  '客户名称',
  '资料类型',
  '业务部门',
  '发起人',
  '审批状态',
  '发起时间',
  '结束时间',
]

// 搜索表单配置
const formNameColumns = [
  { title: '项目名称', editable: InputEditable({ disabled: false }) },
  { title: '项目编号', editable: InputEditable({ disabled: false }) },
  '客户名称',
  '资料类型',
  '业务部门',
  '发起人',
  '审批状态',
  '发起时间',
  '结束时间',
]

const columns = getTableColumns(ALL_COLUMNS, nameColumns)
const formColumns = getFormColumns(ALL_COLUMNS, formNameColumns)

function ArchivesOtherFilingMaterials({ query }) {
  return (
    <Page>
      <div className={styles.customerWrap}>
        <Table
          columnsFilter={'archives_other_filing_materials_1'}
          onFilter={(key, val) => saveServer('archives_other_filing_materials_1', val)}
          selectable={{
            checkStrictly: false,
            getCheckboxProps(record) {
              return {
                disabled: record?.id?.toString().indexOf('folder') > -1 && !record.children?.length,
              }
            },
          }}
          store={store.table}
          editable={false}
          searchbar={{
            labelCol: { span: 6 },
            items: formColumns,
          }}
          actions={[
            {
              name: '其他资料归档',
              onClick: store.fileModal.open,
              type: 'primary',
            },
            {
              name: '批量导出',
              onClick: store.exportData,
            },
          ]}
          scroll={{
            x: 1600,
          }}
          columns={columns}
        />
      </div>
      <FileModal />
    </Page>
  )
}

export default observer(ArchivesOtherFilingMaterials)
