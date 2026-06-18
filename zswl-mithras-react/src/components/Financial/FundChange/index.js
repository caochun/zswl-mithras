import EditDescription from '@/components/Table/EditDescription'
import { getDescColumns } from '@/utils'
import { observer, getQuery } from '@zswl/admin'
import { Page, Button } from '@zswl/components'
import { Space } from 'antd'
import { useEffect, useMemo, useRef } from 'react'
import ActualTable from '../FundActualTable'
import { SETTLE_COLUMNS, LPR_COLUMNS, CHANGE_TITLE } from './Column'
import DataList from './DataList'
import Store from './store'
import styles from './index.less'
// import BaseInfo from './BaseInfo'

// const EnumType = ['CHANGE_LPR', 'CHANGE_EARLY_SETTLE', 'CHANGE_OTHER']

function Index(props) {
  const { id } = props.params ?? {}
  const { canEditFlags = 'true', businessVersion, changeType = 'CHANGE_LPR' } = props.query ?? {}
  const SENCE = {
    CHANGE_LPR: 'CHANGE_LPR',
    CHANGE_EARLY_SETTLE: 'CHANGE_SETTLE_EARLY',
  }[changeType]

  const isFormApproval = getQuery('typeId') == 'approval'
  const canEdit = canEditFlags === 'true'
  const ref = useRef()
  const columns = useMemo(() => {
    const COLUMU = {
      CHANGE_LPR: getDescColumns(LPR_COLUMNS, LPR_COLUMNS),
      CHANGE_EARLY_SETTLE: getDescColumns(SETTLE_COLUMNS, SETTLE_COLUMNS),
    }[changeType]
    return COLUMU ?? []
  }, [changeType])

  const store = useMemo(() => {
    return new Store({ isFormApproval, id, businessVersion, changeType })
  }, [isFormApproval, id, businessVersion, changeType])

  const { baseData } = store
  const { detail, baseInfoDetail } = store.page.getData()

  useEffect(() => {
    id && store.getBaseData()
  }, [id])
  const isOther = changeType === 'CHANGE_OTHER'
  return (
    <Page
      header={null}
      store={store}
      current={CHANGE_TITLE[changeType]?.title}
      params={{ id, isFormApproval, changeType }}
    >
      <div className={styles.header}>
        <div className={styles.title}>{CHANGE_TITLE[changeType]?.title}</div>
        <div className={styles.btnGroup}>
          {!isFormApproval && (
            <Space>
              <Button onClick={store.onCancel}>取消操作</Button>
              <Button type="primary" onClick={store.onSubmit}>
                {changeType === 'CHANGE_LPR' ? '提交确认' : '提交审批'}
              </Button>
            </Space>
          )}
        </div>
      </div>
      {/* {changeType === 'CHANGE_EARLY_SETTLE' && (
        <BaseInfo detail={baseInfoDetail} id={id} saveData={store.saveBaseInfo} />
      )} */}
      <EditDescription
        title={CHANGE_TITLE[changeType]?.subTitle}
        detail={detail}
        saveData={store.saveData}
        canEdit={canEdit}
        initEdit={false}
        columns={columns}
        ref={ref}
      />
      <div className={styles.row}>
        <div className={styles.module}>
          <span className={styles.title}>实际还款计划 </span>
        </div>
        <ActualTable
          isFormApproval={isFormApproval}
          scene={SENCE}
          businessVersion={businessVersion}
          financingId={id}
          baseInfoData={baseData}
          canEdit={canEdit}
          canEditActualLoanDate={false}
          isOtherChange={isOther}
        ></ActualTable>
      </div>
      <div className={styles.row}>
        <DataList businessVersion={businessVersion} financingId={id} canEdit={canEdit} />
      </div>
    </Page>
  )
}

export default observer(Index)
