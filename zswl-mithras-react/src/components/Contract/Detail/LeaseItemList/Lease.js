import { useMemo, useState } from 'react'
import { Button, Table, Select } from '@zswl/components'
import DataUpload from '@/components/DataUpload'
import { Space } from 'antd'
import { observer, history, ErrorBoundary, getQuery } from '@zswl/admin'
import Store from './store'
import styles from './index.less'
import ProcessLease from './ProcessLease'
import { hasPermission, amountFormat } from '@/utils'
import { saveServer } from '@/utils'
import Api from './api'

const Index = ({
  baseStore = {},
  isFormApproval,
  contractId,
  businessVersion,
  canEditFlag = true,
  flowId,
  // 版本日志标识
  isLog,
  isChange,
  taskStatus,
  taskActivityId,
}) => {
  const { priceData, leaseTypes, bizType } = baseStore
  const pageData = baseStore.page?.getData()
  console.log('pageData: ', pageData)
  // 处理历史租赁物，引导用户
  const {
    stockContractFlag,
    projName,
    leaseItemTypes = [],
  } = (isFormApproval ? pageData?.newDetail : pageData?.detail) || {}
  const [newLeaseItemTypes, setNewLeaseItemTypes] = useState(leaseItemTypes ?? [])
  // 当前节点, 项目经理、运营经理、法务经理 才可以编辑，taskStatus===“1” 表示处于当前节点

  // const isOperationmanagementagent = taskActivityId === 'Activity_0wrrxch'
  // const isLegalmanager = taskActivityId === 'userTask_lawManager'
  // const isProjmanager = taskActivityId === 'userTask_startUser'

  const canEditTask = isFormApproval
    ? ['1'].includes(taskStatus) &&
      ['Activity_0wrrxch', 'userTask_lawManager', 'userTask_startUser'].includes(taskActivityId)
    : canEditFlag

  const isZhiZu = leaseTypes === 'zhi_zu'
  const isRevocation = getQuery('tab') === 'revocation'
  const leaseTypesCanEdit =
    isZhiZu &&
    (isFormApproval
      ? (['1'].includes(taskStatus) && ['userTask_yunYingGuanLi'].includes(taskActivityId)) ||
        isRevocation
      : canEditFlag)

  // hui_zu 业务
  const isHuiZu = leaseTypes === 'hui_zu'
  const store = useMemo(() => {
    return new Store({ isFormApproval, contractId, businessVersion, flowId })
  }, [isFormApproval, contractId, businessVersion, flowId])

  const { headerList, totalAmount, saveTotalAmount, changeFlag } = store

  const columns = useMemo(() => {
    return headerList?.map((item) => {
      return {
        title: item,
        dataIndex: item,
        width: item.indexOf('序号') > -1 ? 80 : 160,
        render: (val) => {
          if (item.indexOf('元') > -1) {
            val = amountFormat(val)
          }
          return (
            <span style={isChange || (isFormApproval && changeFlag) ? { color: 'red' } : {}}>
              {typeof val === 'object' ? '-' : String(val)}
            </span>
          )
        },
      }
    })
  }, [JSON.stringify(headerList)])

  const goProcess = () => {
    const search = JSON.stringify({
      modelKeyList: ['LeaseCreateFlow', 'LeaseModifyFlow'],
      processName: projName,
    })

    window.open(`/process/query?search=${search}`, '_blank')
  }

  const leaseTypeChange = async (val) => {
    const id = baseStore.page?.getParams().contractId
    await Api.postLeaseTypesModify({
      id,
      leaseItemTypes: val,
    })
    setNewLeaseItemTypes(val)
  }
  return (
    <ErrorBoundary
      fallback={
        <div>
          <h3>租赁物清单</h3>
          <div>
            <Table
              columnsFilter={'detail_ZuLinWu_Lease_1'}
              onFilter={(key, val) => saveServer('detail_ZuLinWu_Lease_1', val)}
              columns={columns}
              dataSource={[]}
              scroll={{
                x: 1800,
                y: 400,
              }}
            ></Table>
          </div>
        </div>
      }
    >
      <div className={styles.page}>
        <div className={styles.header}>
          <div className={styles.title} style={{ display: 'flex', alignItems: 'center' }}>
            租赁物清单
            <div style={{ display: 'flex', alignItems: 'center', marginLeft: 20, fontSize: 14 }}>
              租赁物类型：
              <Select
                options={'leaseItemManagerLeaseItemType'}
                mode="multiple"
                value={newLeaseItemTypes}
                onChange={leaseTypeChange}
                style={{ width: 250 }}
                // 项目经理勾选，在运营经理审批节点允许编辑。
                disabled={!leaseTypesCanEdit}
              />
            </div>
          </div>
          {!isLog && (
            <Space>
              {isHuiZu && (
                <>
                  {/* 运营/法务 有操作权限,项目经理页面该按钮需置灰 */}
                  {hasPermission('contractleaseiteminprocesscheck') && canEditTask && (
                    <Button type="primary" onClick={store.checkInprocess}>
                      引入租赁物审核流程
                    </Button>
                  )}
                  {/* 项目经理、运营经理岗与法务经理岗,发起「租赁物变更审核」流程 */}
                  {hasPermission('leaseReviewModifyEffect') && canEditTask && (
                    <div>
                      {stockContractFlag ? (
                        <Button type="primary" onClick={store.changeLease2}>
                          导入
                        </Button>
                      ) : (
                        <Button type="primary" onClick={store.changeLease}>
                          变更租赁物清单
                        </Button>
                      )}
                    </div>
                  )}
                  {/* 在合同审批流程-【租赁物清单】增加【租赁物类型】下拉框，由项目经理勾选，在运营经理审批节点允许编辑。
                  在合同管理详情页-租赁物清单增加字段【租赁物类型】取值自租赁物审核流程，置灰，不允许修改。
                  todo
                  */}
                  {/* 租赁物类型：<Select options={[]}></Select> */}

                  <Button
                    onClick={() => {
                      // history.push(
                      //   `/contract/list/detail/log/${contractId}?bizType=${bizType}&leaseLog=1`
                      // )
                      window.open(
                        `/contract/list/detail/log/${contractId}?bizType=${bizType}&leaseLog=1`,
                        '_blank'
                      )
                    }}
                  >
                    历史版本
                  </Button>
                  <Button onClick={goProcess} type="primary">
                    租赁物审核流程
                  </Button>
                </>
              )}
              <Button type="primary" onClick={store.batchExport}>
                批量导出
              </Button>
              {!isHuiZu && (
                <>
                  <Button onClick={store.downloadTemplate}>模版下载</Button>
                  <DataUpload
                    accept=".xlsx"
                    maxCount={1}
                    onChange={store.onFileChange}
                    disabled={!(canEditFlag || leaseTypesCanEdit)}
                  >
                    <Button type="primary" disabled={!canEditFlag && !leaseTypesCanEdit}>
                      导入
                    </Button>
                  </DataUpload>
                </>
              )}
            </Space>
          )}
        </div>
        <Table
          columnsFilter={'detail_ZuLinWu_Lease_2'}
          onFilter={(key, val) => saveServer('detail_ZuLinWu_Lease_2', val)}
          selectable={{
            type: 'checkbox',
          }}
          columnWidth={180}
          resizable
          columns={columns}
          store={store.$table}
          scroll={{
            x: 1800,
            y: 400,
          }}
        ></Table>
        {!isLog && isHuiZu && <ProcessLease store={store}></ProcessLease>}
      </div>
    </ErrorBoundary>
  )
}

export default observer(Index)
