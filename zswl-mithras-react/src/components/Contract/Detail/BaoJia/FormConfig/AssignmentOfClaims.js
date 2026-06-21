
import ALL_COLUMNS from '../Column'
import { getDescColumns } from '@/utils'
import { observer } from '@zswl/admin'
import { useMemo } from 'react'
import WarnTip from '../WarnTip'
import { Context } from '../../Context'

function ContractAssignmentOfClaimsQuoteForm({ detail, saveData, isLog, canEdit = true, initEdit, store, remainAvailableQuota }) {
  const nameColumns = useMemo(() => {
    return [
      {
        title: '保理合同金额(元)',
        rename:
          detail?.contractAmount > remainAvailableQuota ? (
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
      '额度是否可循环',
      '转让额度有期限(月)',
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
      '还款频率',
      {
        title: '手续费(元)',
        rename: store?.legal_consultingFee ? (
          <div>
            手续费(元)
            <WarnTip title={'手续费占合同金额比率不能小于项目的比率'}></WarnTip>
          </div>
        ) : (
          '手续费(元)'
        ),
      },
      '转让费率',
      {
        title: '还款方式-保理',
        rename: '还款方式',
      },
      '结构化利息(元)',
    ].filter(Boolean)
  }, [
    detail?.contractAmount,
    store?.legal_earnestMoney,
    store?.legal_consultingFee,
    remainAvailableQuota,
  ])

  return (
    <Context.Consumer>
      {(context) => {
        const handleCalc = () => {
          store?.handleCalc(context?.ref?.current?.form)
        }

        const onInterestWayChange = (value) => {
          context?.ref?.current?.form.instance.setFieldValue('repayCalcType', undefined)
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

export default observer(ContractAssignmentOfClaimsQuoteForm)
