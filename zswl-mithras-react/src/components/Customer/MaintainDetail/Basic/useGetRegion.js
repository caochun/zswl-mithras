import { useEffect, useState } from 'react'
import Api from '@/api/customer/maintainApi'
const useGetRegion = () => {
  const [region, setRegion] = useState([])
  const getRegion = () => {
    Api.getRegionList({ code: 156 }).then((res) => {
      setRegion(res)
    })
  }
  useEffect(() => {
    getRegion()
  }, [])
  return { region }
}

export default useGetRegion
