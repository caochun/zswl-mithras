import { observer } from '@zswl/admin'
import styles from './index.less'
import EditModal from './EditModal'
import IconFont from '@/components/Icon'
import { Table, Button } from '@zswl/components'
import { Space } from 'antd'
import { getTableColumns, saveServer } from '@/utils'
import { useEffect, useMemo } from 'react'
import Store from './store'
import ALL_COLUMNS from '../../CheckPlanColumns'

const Index = ({ planId, canEditFlag, businessVersion, detail }) => {
  const nameColumns = [
    '客户编号',
    '客户名称',
    '客户类型',
    '客户主办',
    '本次是否需要检查',
    '检查形式',
    '检查报告模版',
    '协查风控经理',
  ]
  const columns = getTableColumns(ALL_COLUMNS, nameColumns)

  const store = useMemo(() => {
    return new Store({})
  }, [])
  store.planId = planId
  const { $editModal, notQuarterProjList } = store

  useEffect(() => {
    store.getNotQuarterProjList({ id: planId, businessVersion })
  }, [planId, businessVersion])

  return (
    <>
      <div className={styles.title}>本次租后检查客户({notQuarterProjList.length})</div>
      <div className={styles.subTitle}>
        <div className={styles.total}></div>
        {/* 一般检查计划 */}
        {detail.planType !== 'COMMONLY' && (
          <Button
            type="primary"
            onClick={$editModal.open}
            disabled={!canEditFlag}
            icon={<IconFont type="icon-icon_add" />}
          >
            新增
          </Button>
        )}
      </div>
      <Table
        scroll={{
          x: 1300,
        }}
        resizable
        rowKey={'projectCode'}
        dataSource={notQuarterProjList}
        columnsFilter={'createPlan_OtherPlan_1'}
        onFilter={(key, val) => saveServer('createPlan_OtherPlan_1', val)}
        columns={[
          ...columns,
          {
            title: '操作',
            width: 120,
            fixed: 'right',
            isAction: true,
            render: (record) => {
              return (
                <Space>
                  {canEditFlag && <a onClick={() => $editModal.open(record)}>编辑 </a>}
                  {canEditFlag && <a onClick={() => store.remove(record)}>移除 </a>}
                </Space>
              )
            },
          },
        ]}
      ></Table>
      <EditModal store={store}></EditModal>
    </>
  )
}

export default observer(Index)
