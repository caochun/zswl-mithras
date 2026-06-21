import { observer, getQuery } from '@zswl/admin'

import ALL_COLUMNS from './Column'
import { getDescColumns } from '@/utils'
import PaymentDetail from './PaymentDetail'
import { useMemo } from 'react'

const zbj = '厂商质保金(元)'

const CpmPaymentApplicationInfo = (props) => {
  const { canEditFlag, store } = props
  const detail = store.page.getData()
  const { bizTypeCode } = detail
  const ZZ_ZL = ['ZL', 'ZZ'].includes(bizTypeCode)
  const isNew = getQuery('isNew')

  const nameColumns = [
    '申请付款日期',
    '申请付款金额(元)',
    '首期租金(元)',
    '资金拟投放金额(元)',
    '定价IRR',
    '最低IRR',
    zbj,
    !ZZ_ZL && '手续费/服务费/咨询费(元)',
    ZZ_ZL && '服务费/咨询费(元)',
    ZZ_ZL && '手续费(元)',
    ZZ_ZL && '首期利息(元)',
    '客户保证金(元)',
    '名义价款(元)',
    '租赁财产价值',
    '币种',
    '备注',
  ].filter(Boolean)

  const columns = useMemo(() => {
    let newColumns = [...nameColumns]
    if (detail.leaseTypeCode !== 'zhi_zu') {
      newColumns = nameColumns.filter((item) => item !== zbj)
    }
    return getDescColumns(ALL_COLUMNS(store), newColumns)
  }, [detail, store])

  const onEditStatusChange = (status) => {
    store.setApplicationEditStatus(status)
  }
  return (
    <div>
      <EditDescription
        initEdit={isNew}
        title="本次申请"
        onEditStatusChange={onEditStatusChange}
        detail={detail}
        saveData={store.saveApplicationForm}
        canEdit={canEditFlag}
        columns={columns}
        labelStyle={{
          width: 200,
          backgroundColor: '#F5F6FA',
        }}
      />
      <PaymentDetail store={store}></PaymentDetail>
    </div>
  )
}
export default observer(CpmPaymentApplicationInfo)
