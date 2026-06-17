import { DetailLayout } from '@/components'
import ActualTable from '@/components/Contract/ActualTable'
import { bizTypeMapText } from '@/components/Contract/bizTypeConfig'
import CantractDetailBaseInfo from '@/components/Contract/Detail/BaseInfo'
import { getQuery, history, observer } from '@zswl/admin'
import { Button, Form, Page } from '@zswl/components'
import { DatePicker, Space } from 'antd'
import moment from 'moment'
import { useEffect, useMemo, useRef, useState } from 'react'
import InadvanceDesc from './InadvanceDesc'
import NormalDesc from './NormalDesc'
import Protocol from './Protocol'
import Certificate from './certificate'
import styles from './index.less'
import Store from './store'

function Index({
  params: { id },
  query: { planType, canEditFlags = 'true', businessVersion, first },
}) {
  const [contractInfo, setContractInfo] = useState({})
  const [actualStartDate, setActualStartDate] = useState()
  const bizType = contractInfo.bizType

  const isFormApproval = getQuery('typeId') == 'approval'
  const canEditFlagsFormAuth = canEditFlags === 'true'
  const store = useMemo(() => {
    return new Store({ isFormApproval, canEdit: canEditFlagsFormAuth })
  }, [isFormApproval, canEditFlagsFormAuth])

  const $tableRef = useRef(null)
  const isNoraml = planType === 'SETTLE_NORMAL'

  const { settleDetail, saveInadvanceData, saveNormalData } = store

  useEffect(() => {
    if ($tableRef.current) {
      $tableRef.current.refresh()
    }
  }, [])

  const renderChangeTypeType = useMemo(() => {
    if (planType === 'SETTLE_NORMAL') {
      return (
        <NormalDesc
          detail={settleDetail}
          saveData={saveNormalData}
          canEdit={canEditFlagsFormAuth}
        ></NormalDesc>
      )
    } else if (planType === 'SETTLE_IN_ADVANCE') {
      // 提前结清
      return (
        <InadvanceDesc
          detail={settleDetail}
          saveData={saveInadvanceData}
          canEdit={canEditFlagsFormAuth}
        ></InadvanceDesc>
      )
    }
    return null
  }, [settleDetail])

  const onCantractInfoData = (data) => {
    setContractInfo(data)
  }

  const onReceiveDataFromChild = (values) => {
    if (values.length > 0) {
      setActualStartDate(values[0].actualStartDate || '-')
    }
  }
  const goProcess = () => {
    const search = JSON.stringify({
      projName: contractInfo.projName,
      // clientId: contractInfo.clientId,
    })
    if (isFormApproval) {
      window.open(`/process/query?search=${search}`)
    } else {
      history.push(`/process/query?search=${search}`)
    }
  }

  return (
    <Page store={store} header={null} params={{ id, isFormApproval, planType, businessVersion }}>
      <DetailLayout
        title={isNoraml ? '正常结清' : '提前结清'}
        extra={
          <Space>
            <Button onClick={goProcess} type="link">
              查询历史流程
            </Button>
            {!isFormApproval && canEditFlagsFormAuth && (
              <>
                <Button onClick={store.cancelFlow}>取消操作</Button>
                <Button type="primary" onClick={store.onSubmit} loading={store.isLoading}>
                  提交审批
                </Button>
              </>
            )}
          </Space>
        }
      >
        <CantractDetailBaseInfo
          contractId={id}
          businessVersion={businessVersion}
          isDetail
          canEditFlag={false}
          onEmitData={onCantractInfoData}
        />
        <div style={{ marginBottom: 20 }}>{renderChangeTypeType}</div>
        <div className={styles.subHeader}>
          <h3>{`实际${bizTypeMapText[bizType]?.rentTitle}`}</h3>
          <Space>
            <span>{`实际${bizTypeMapText[bizType]?.dateText}`}：</span>
            <span>{actualStartDate}</span>
          </Space>
        </div>
        <ActualTable
          extra={(item, index) => {
            return (
              <Form
                initialValues={{
                  receiptStartDate: item.actualStartDate ? moment(item.actualStartDate) : undefined,
                }}
              >
                <Form.Item label="借据起租日期" name={'receiptStartDate'}>
                  <DatePicker placeholder={'请选择'} disabled />
                </Form.Item>
              </Form>
            )
          }}
          businessVersion={businessVersion}
          bizType={bizType}
          ref={$tableRef}
          contractId={id}
          showImportBtn={isNoraml ? false : canEditFlagsFormAuth}
          onReceiveDataFromChild={onReceiveDataFromChild}
        ></ActualTable>
        {/* 所有权转移证书 */}
        <Certificate
          title="所有权转移证书"
          canEdit={false}
          startUserId={first}
          id={id}
          businessVersion={businessVersion}
        ></Certificate>
        <Protocol
          title="其他资料"
          canEdit={canEditFlagsFormAuth}
          id={id}
          businessVersion={businessVersion}
        ></Protocol>
      </DetailLayout>
    </Page>
  )
}

export default observer(Index)
