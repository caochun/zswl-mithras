import { useMemo } from 'react'
import { Table, Page, Form } from '@zswl/components'
import { observer } from '@zswl/admin'
import { OrgSelect } from '@/components/Select'
import { DatePicker } from 'antd'
import EditModal from '../EditModal/KpiPmAssessEditModal'
import { isBusinesshead } from '@/utils'
import styles from './index.less'
import Store from './store'
import { saveServer } from '@/utils'

const { Item } = Form

function Index() {
  const store = useMemo(() => {
    return new Store({})
  }, [])

  return (
    <Page>
      <Table
        columnsFilter={'kpi_pmAssess_1'}
        onFilter={(key, val) => saveServer('kpi_pmAssess_1', val)}
        scroll={false}
        className={styles.table}
        resizable
        store={store.$table}
        columnWidth={160}
        searchbar={{
          labelCol: { span: 6 },
          items: [
            <Item label="考核期" name="quarterInfo" key="quarterInfo">
              <DatePicker picker="quarter"></DatePicker>
            </Item>,
            <Item label="考核部门" name="deptId" key="deptId">
              <OrgSelect functionCode="selectorgs-kpipmassess"></OrgSelect>
            </Item>,
            {
              label: '审批状态',
              name: 'approvalStatus',
              options: 'commonProcessStatus',
            },
          ],
        }}
        columns={[
          {
            title: '考核年份',
            dataIndex: 'year',
          },
          {
            title: '考核季度',
            dataIndex: 'quarter',
            render: (value) => {
              return ['', 'Q1', 'Q2', 'Q3', 'Q4'][value]
            },
          },
          {
            title: '考核部门',
            dataIndex: 'deptName',
          },
          {
            title: '审批状态',
            dataIndex: 'approvalStatus',
            matchOption: 'commonProcessStatus',
          },
          {
            title: '操作',
            width: 100,
            fixed: 'right',
            actions(record) {
              return [
                {
                  name: '查看',
                  key: 'view',
                  onClick: () => store.$editModal.open({ ...record, isEdit: false }),
                },
                {
                  name: '编辑',
                  key: 'edit',
                  onClick: () => store.$editModal.open({ ...record, isEdit: true }),
                  disabled: !isBusinesshead(record.deptId),
                },
              ]
            },
          },
        ].filter(Boolean)}
      />
      <EditModal store={store}></EditModal>
    </Page>
  )
}

export default observer(Index)
