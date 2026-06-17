import React, { useState, useEffect, useMemo } from 'react'
import { getQuery, observer } from '@zswl/admin'
import { Page, Table, Checkbox, Button, Modal, Form, Select, Access } from '@zswl/components'
import { Space } from 'antd'
import DepartmentSelector from '@/components/BudgetManagement/PlacementPlan/DepartmentSelector'
import styles from './style.less'
import Store from './store.ts'
import DetailModal from './DetailModal'
import TableExport from '@/components/Actions/TableExport'
import ProjectMonthTable from './ProjectMonthTable'
import ProjectTable from './ProjectTable'
import StatisticCard from '@/components/BudgetManagement/PlacementPlan/StatisticCard'
import {
  getUserInfo,
  isAdminAccount,
  isDept,
  isFinancialManager,
  isFinancialOfficer,
  isProjmanager,
} from '@/utils'
import deliveryPlanDetailApi from '@/api/budgetManagement/deliveryPlanDetailApi'

const useGetPermission = ({ taskActivityId, mainModule, budgetStatus, deptId }) => {
  const isAdmin = isAdminAccount()
  if (isAdmin) return { canAddOrDelete: true, canEdit: true }

  const isFormApproval = getQuery('typeId') == 'approval'
  const isRevocation = ['sendback', 'revocation'].includes(getQuery('tab'))
  const isPending = getQuery('curTab') == 'pending'
  const isFinalPlanEventFlow = mainModule == 'FinalPlanEventFlow'
  // 财务经理和财务主管 可以在菜单页都可以编辑
  const isFinancial = isFinancialManager() || isFinancialOfficer()
  const canEditStatus = ['COLLECTING', 'COLLECT_FINISH'].includes(budgetStatus)
  const financialCanEdit = isFinancial && canEditStatus
  const monthCanEditTaskId = [
    'userTask_startUser',
    'userTask_financeManager',
    'userTask_headofyyglb',
    'userTask_fundManager',
    'userTask_headofzj',
  ].includes(taskActivityId)
  let canEdit
  let monthCanEdit = isPending && monthCanEditTaskId

  // 财务流程，分成菜单页和流程页，
  if (isFinalPlanEventFlow) {
    //在常规详情页 过财务主管后，不能编辑
    const financialCanNotEditTaskId = [
      'userTask_headofyyglb',
      'userTask_fundManager',
      'userTask_headofzj',
    ].includes(taskActivityId)
    const isFinanceManager = ['userTask_financeManager'].includes(taskActivityId)
    // 在财务审批流程中，只有财务经理可以新增和删除项目，
    const monthCanAddOrDelete = isPending && isFinanceManager
    const canAddOrDelete = isFormApproval
      ? monthCanAddOrDelete || isRevocation
      : financialCanEdit && !financialCanNotEditTaskId

    canEdit = isFormApproval
      ? monthCanEdit || isRevocation
      : financialCanEdit && !financialCanNotEditTaskId
    return { canAddOrDelete, canEdit, isFinalPlanEventFlow }
  }

  // 收集流程
  const projCanEditTaskActivityId = [
    'userTask_project_manager',
    'userTask_deptMaster',
    'userTask_bizDivisionLeader',
  ].includes(taskActivityId)

  // 菜单页 项目经理在项目经理、业务部负责人、分管领导节点都可以编辑
  const projmanageCanEdit = projCanEditTaskActivityId && isProjmanager() && isDept(+deptId)
  const fundCanEditTaskActivityId = ['userTask_financialmanager'].includes(taskActivityId)
  const fundCanEdit = isPending && (projCanEditTaskActivityId || fundCanEditTaskActivityId)

  canEdit = isFormApproval
    ? fundCanEdit || (isPending && projmanageCanEdit) || isRevocation
    : financialCanEdit || projmanageCanEdit
  const canAddOrDelete = canEdit

  return { canAddOrDelete, canEdit, isFinalPlanEventFlow }
}
const PlacementPlanDetail = observer(({ params, query }) => {
  const { id, deptId, taskActivityId, processName, mainModule } = params
  const store = useMemo(() => new Store({ id, taskActivityId }), [id, taskActivityId])
  const {
    isMonth = true,
    writeDateFrom,
    writeDateTo,
    budgetStatus,
    curTaskActivityIds: processTaskActivityId,
    businessKey,
    mainModule: processMainModule,
  } = store.page.getData() ?? {}
  const { departmentList, statistics } = store

  const { canEdit, canAddOrDelete, isFinalPlanEventFlow } = useGetPermission({
    taskActivityId: taskActivityId ?? processTaskActivityId,
    mainModule: mainModule ?? processMainModule,
    budgetStatus,
    deptId: deptId ?? businessKey?.split('-')?.[1],
  })

  const [selectedDepartment, setSelectedDepartment] = useState('')
  const { keys, rows } = store?.projectTable?.getSelected()

  const handleDepartmentChange = (value) => {
    setSelectedDepartment(value)
    store.projectTable.setParams({ page: 1, belongDeptId: value })
    setTimeout(() => {
      store.refresh()
    }, 50)
  }
  const hasLevelOne = rows.some((item) => item.level === 1)
  const deleteDisabled = keys?.length === 0 || hasLevelOne
  const copyDisabled = keys?.length !== 1

  useEffect(() => {
    if (deptId) {
      handleDepartmentChange(+deptId)
    } else {
      setTimeout(() => {
        store.refresh()
      }, 50)
    }
  }, [isMonth, deptId])
  const addFunctionCode = isMonth
    ? 'budgetplanpaymonthdetailadd'
    : 'budgetplanpaynotmonthdetailcalculate'

  return (
    <Page store={store.page} params={{ id, deptId }} bodyStyle={{ position: 'relative' }}>
      {processName && <div className={styles.processName}>{processName}</div>}
      <div className={styles.header}>
        <Space>
          {canAddOrDelete && (
            <Button type="primary" onClick={store.addItem} access={addFunctionCode}>
              新建项目
            </Button>
          )}
          {canEdit && !isMonth && (
            <Button
              onClick={store?.copyItem}
              disabled={copyDisabled}
              access={'budgetplanpaynotmonthdetailcopy'}
            >
              复制项目
            </Button>
          )}
          {canAddOrDelete && (
            <Button
              onClick={store?.itemDelete}
              disabled={deleteDisabled}
              access={'budgetplanpaydetailbatchDelete'}
            >
              删除项目
            </Button>
          )}
        </Space>
        <TableExport table={store?.projectTable} otherExcelProps={{ fileName: '投放计划列表' }} />
      </div>
      <div className={styles.container}>
        {/* 提示信息 */}
        <div className={styles.reportPeriod}>
          <img className={styles.icon} src="/public/assets/budgetManage/time.png" />
          填报期间: {writeDateFrom} ~ {writeDateTo}
        </div>

        {/* 统计数据 */}
        <Access
          value={['budgetplanpaynotmonthdetailstatistics', 'budgetplanpaymonthdetailstatistics']}
        >
          <div className={styles.statisticsRow}>
            {store.statistics.map((item, index) => (
              <StatisticCard
                title={item.title}
                value={item.value}
                unit={item.unit}
                initFormat={item.initFormat}
                width={item.width}
              />
            ))}
          </div>
        </Access>

        {/* 部门选择器 */}
        <DepartmentSelector
          value={selectedDepartment}
          onChange={handleDepartmentChange}
          departments={departmentList}
          needBusinessHead={!deptId}
          needLeader={!deptId}
        />

        {isMonth === true && (
          <ProjectMonthTable
            store={store}
            canEdit={canEdit}
            taskActivityId={taskActivityId}
            isFinalPlanEventFlow={isFinalPlanEventFlow}
          />
        )}
        {isMonth === false && <ProjectTable store={store} />}
        <Modal title="新建项目" store={store?.editModal} destroyOnClose>
          <Form>
            <Form.Item label="项目名称" name="projReviewId">
              <Select
                options={() =>
                  deliveryPlanDetailApi.postProjreviewList({ belongDeptId: deptId }).then((res) =>
                    res.map((item) => ({
                      label: item.key,
                      value: item.value,
                    }))
                  )
                }
                getPopupContainer={() => document.body}
              />
            </Form.Item>
          </Form>
        </Modal>
        <DetailModal modal={store?.detailModal} tableRefetch={store.refresh} canEdit={canEdit} />
      </div>
    </Page>
  )
})

export default PlacementPlanDetail
