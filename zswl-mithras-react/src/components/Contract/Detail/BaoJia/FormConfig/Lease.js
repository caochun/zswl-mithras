
import ALL_COLUMNS from '../Column'
import { getDescColumns } from '@/utils'
import { observer } from '@zswl/admin'
import { useMemo } from 'react'
import WarnTip from '../WarnTip'
import { Context } from '../../Context'

function ContractLeaseQuoteForm({
  detail,
  saveData,
  isLog,
  canEdit = true,
  initEdit,
  store,
  leaseTypes,
  bizType,
  remainAvailableQuota,
}) {
  const nameColumns = useMemo(() => {
    return [
      {
        title: '合同金额(元)',
        rename:
          detail?.applyCreditAmount > remainAvailableQuota ? (
            <div>
              合同金额(元)
              <WarnTip
                title={'合同金额已超出该项目剩余可用额度，请关注需在付款流程发起前完成额度释放！'}
              ></WarnTip>
            </div>
          ) : (
            '合同金额(元)'
          ),
      },
      {
        title: '保证金(元)',
        rename: store?.legal_earnestMoney ? (
          <div>
            保证金(元)<WarnTip title={'保证金占合同金额比率不能小于项目的比率'}></WarnTip>
          </div>
        ) : (
          '保证金(元)'
        ),
      },
      '租赁期限(月)',
      {
        title: '首期租金(元)',
        rename: store?.legal_downPayment ? (
          <div>
            首期租金(元)<WarnTip title={'首期租金占合同金额比率不能小于项目的比率'}></WarnTip>
          </div>
        ) : (
          '首期租金(元)'
        ),
      },
      '还款频率',
      {
        title: '服务费/咨询费(元)',
        rename: store?.legal_consultingFee ? (
          <div>
            服务费/咨询费(元)
            <WarnTip title={'服务费/咨询费占合同金额比率不能小于项目的比率'}></WarnTip>
          </div>
        ) : (
          '服务费/咨询费(元)'
        ),
      },
      {
        title: '租赁-手续费(元)',
        rename: '手续费(元)',
      },
      {
        title: '租赁-首期利息(元)',
        rename: '首期利息(元)',
      },
      '还款期数',
      '名义价款(元)',
      '手续费率',
      '保证金率',
      '支付方式',
      bizType === 'ZL' && '利息计算方式',
      '结构化利息(元)',
      {
        title: '还款方式-租赁',
        rename: '还款方式',
      },
      '罚息日利率',
      '租赁利率',
      leaseTypes === 'zhi_zu' && '租前息利率',
    ].filter(Boolean)
  }, [
    bizType,
    detail?.applyCreditAmount,
    store?.legal_earnestMoney,
    store?.legal_downPayment,
    store?.legal_consultingFee,
    leaseTypes,
    remainAvailableQuota,
  ])

  return (
    <Context.Consumer>
      {(context) => {
        const handleCalc = () => {
          store?.handleCalc(context?.ref?.current?.form)
        }

        const onInterestWayChange = (value) => {
          context?.ref?.current?.form.instance.setFieldValue('rentalCalcType', undefined)
        }

        const columns = getDescColumns(
          ALL_COLUMNS({ handleCalc, isLog, onInterestWayChange, contractId: detail?.contractId }),
          nameColumns
        )
        return (
          <EditDescription
            title={'报价方案'}
            detail={detail}
            saveData={saveData}
            canEdit={canEdit}
            isLog={isLog}
            initEdit={initEdit}
            columns={columns}
            ref={context?.ref}
          />
        )
      }}
    </Context.Consumer>
  )
}

export default observer(ContractLeaseQuoteForm)
