import { useEffect, useRef } from 'react'
import { InputNumber } from 'antd'
import { makeAutoObservable, observer } from '@zswl/admin'
import Api from '@/api/contract/contractDetail'

class ContractIrrStore {
  loading = false
  irrPercent

  constructor() {
    makeAutoObservable(this)
  }

  /**
   * 将接口返回的 IRR 统一归一化为“百分比数值”
   */
  normalizeToPercent = (result) => {
    if (result == null) return undefined

    const raw =
      typeof result === 'number' || typeof result === 'string'
        ? result
        : result?.irrPercent ?? result?.irr ?? result?.data ?? result?.result

    if (raw == null) return undefined

    const num = Number(raw)
    if (!Number.isFinite(num)) return undefined

    const abs = Math.abs(num)

    if (abs <= 1) return Number((num * 100).toFixed(2))
    if (abs >= 10000) return Number((num / 10000).toFixed(2))
    if (abs >= 1000) return Number((num / 100).toFixed(2))
    return Number(num.toFixed(2))
  }

  /**
   * 根据合同ID计算并设置平均IRR
   */
  fetch = async (contractId) => {
    if (!contractId) {
      this.irrPercent = undefined
      return
    }
    this.loading = true
    try {
      const res = await Api.calculateCombinedIrr({ contractId })
      this.irrPercent = this.normalizeToPercent(res)
    } finally {
      this.loading = false
    }
  }
}

/**
 * 合同IRR组件：展示平均IRR，禁用输入
 */
const ContractIRR = observer(({ contractId, showLabel = true, style }) => {
  const storeRef = useRef()
  if (!storeRef.current) {
    storeRef.current = new ContractIrrStore()
  }
  const store = storeRef.current

  useEffect(() => {
    store.fetch(contractId)
  }, [contractId, store])

  return (
    <div style={{ display: 'flex', alignItems: 'center', marginRight: 16, ...(style || {}) }}>
      {showLabel && <span style={{ marginRight: 8 }}>合同IRR：</span>}
      <InputNumber
        placeholder={'请选择'}
        disabled
        addonAfter="%"
        precision={2}
        step="0.01"
        style={{ width: 100 }}
        value={store.irrPercent}
      />
    </div>
  )
})

export default ContractIRR
