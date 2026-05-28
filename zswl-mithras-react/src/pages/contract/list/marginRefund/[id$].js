import DetailLayout from '@/components/DetailLayout'
import { getQuery, observer } from '@zswl/admin'
import { Button, Page } from '@zswl/components'
import { message } from 'antd'
import { useEffect, useMemo, useRef, useState } from 'react'
import Lease from '../detail/BaseInfo/FormConfig/Lease'
import Api from './api'
import MaterialsList from './materialsList'
import RefundScheme from './refundScheme'
import RentDeduction from './rentDeduction'
import Store from './store'

export const Context = React.createContext()

const Index = ({
  params: { id },
  query: { businessKey, planType, bizType, clientName },
  processInstanceId,
  businessVersion,
  startUserId,
  setEditing,
  registerCallback, // 注册回调函数
}) => {
  const metiralRef = useRef()
  const isFormApproval = getQuery('typeId') === 'approval'
  const revocation = getQuery('tab') == 'revocation'
  const sendBack = getQuery('tab') == 'sendback'
  const isPending = getQuery('curTab') == 'pending'
  const [curTaskDefKey, setCurTaskDefKey] = useState('')

  const canEdit = isFormApproval ? (['userTask_financeManager', 'userTask_startUser', 'userTask_theStartUser'].includes(curTaskDefKey) && isPending) || revocation || sendBack : true

  const store = useMemo(() => {
    return new Store(clientName, canEdit)
  }, [])

  useEffect(() => {
    if (processInstanceId) {
      Api.getCurTaskDefKey({ processInstanceId }).then((res) => {
        setCurTaskDefKey(res)
      })
    }
  }, [processInstanceId])

  const handleCheck = () => {
    if (!canEdit) {
      return false
    }
    if (
      (store.rentListSize === 0 && store.depostInfo.deductionAmount / 10000 > 0) ||
      (store.rentListSize > 0 && store.depostInfo.deductionAmount / 10000 === 0)
    ) {
      message.warn('抵扣租金与内扣金额不匹配，请检查！')
      return true
    }
    const size = metiralRef.current?.getSize()
    if (size && size.length === 0) {
      message.warn('请上传保证金退抵文件！')
      return true
    }
    return false
  }
  useEffect(() => {
    registerCallback && registerCallback(handleCheck)
    return () => {
      registerCallback && registerCallback(null)
    }
  }, [registerCallback, handleCheck])
  const anchorList = [{ label: '基本信息' }, { label: '保证金退抵方案' }, { label: '抵扣租金信息' }, { label: '资料清单' }]
  return (
    <Page store={store} header={null} params={{ contractId: id, bizType, planType, businessKey }}>
      <DetailLayout
        anchorList={anchorList}
        title={'保证金退抵'}
        extra={
          !isFormApproval ? (
            <Button
              type="primary"
              loading={store.approvalLoading}
              onClick={() => {
                if (
                  (store.rentListSize === 0 && store.depostInfo.deductionAmount / 10000 > 0) ||
                  (store.rentListSize > 0 && store.depostInfo.deductionAmount / 10000 === 0)
                ) {
                  message.warn('抵扣租金与内扣金额不匹配，请检查！')
                  return
                }
                const size = metiralRef.current?.getSize()
                if (size && size.length === 0) {
                  message.warn('请上传保证金退抵文件！')
                  return
                }
                store.submit()
              }}
            >
              提交审批
            </Button>
          ) : null
        }
        moduleName="establishment"
      >
        <Lease detail={store.page?.data?.detail} canEdit={false} />
        <RefundScheme detail={store.page?.data?.depostInfo} store={store} canEdit={canEdit} setEditing={setEditing} />
        <RentDeduction
          retreatInfoId={store.page?.data?.retreatInfoId}
          contractId={store.contractId}
          clientName={clientName || store.page?.data?.detail?.clientName}
          canEdit={canEdit}
          setRentListSize={store.setRentListSize}
        />
        <MaterialsList
          id={store.page?.data?.retreatInfoId}
          processInstanceId={processInstanceId}
          businessVersion={businessVersion}
          startUserId={startUserId}
          canEdit={canEdit}
          ref={metiralRef}
        />
      </DetailLayout>
    </Page>
  )
}

export default observer(Index)
