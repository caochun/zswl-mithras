import { RentCollectionProjectDetail as ProjectDetail } from '@/components/AfterLease/RentCollectionEntries'
import { useEffect, useState } from 'react'
import Api from '../api'
import styles from './index.less'
import { RentCollectionBatchInterest as BatchInterest } from '@/components/AfterLease/RentCollectionEntries'
const AfterLeaseRentCollection = ({ id, canEditFlag, businessVersion, modelKey }) => {
  const isBatchInterest = modelKey === 'NewRentCollectionExemptionFlow'
  if (isBatchInterest) {
    return (
      <BatchInterest
        id={id}
        canEditFlag={canEditFlag ? 'true' : 'false'}
        businessVersion={businessVersion}
      />
    )
  }
  const [contractId, setContractId] = useState()

  const query = async (reduceId) => {
    const res = await Api.getContractIdByReduceId({ reduceId })
    if (res) {
      setContractId(res.contractId)
    }
  }
  useEffect(() => {
    if (id && !isBatchInterest) {
      query(id)
    }
  }, [id])
  if (!contractId) {
    return null
  }
  return (
    <div className={styles.wrap}>
      <ProjectDetail
        contractId={contractId}
        isProcess
        query={{
          businessVersion,
          reduceId: id,
          canEditFlags: canEditFlag ? 'true' : 'false',
        }}
      />
    </div>
  )
}

export default AfterLeaseRentCollection
