import ProjectDetail from '@/components/AfterLease/RentCollection/ProjectDetail'
import { useEffect, useState } from 'react'
import Api from '../api'
import styles from './index.less'
const AfterLeaseRentCollection = ({ id, canEditFlag, businessVersion }) => {
  const [contractId, setContractId] = useState()

  const query = async (reduceId) => {
    const res = await Api.getContractIdByReduceId({ reduceId })
    if (res) {
      setContractId(res.contractId)
    }
  }
  useEffect(() => {
    if (id) {
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
