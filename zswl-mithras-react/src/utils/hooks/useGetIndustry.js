import { useEffect, useState } from 'react'
import { getSessionStorage, setSessionStorage } from '@zswl/admin'
import Api from '@/api/common/selectApi'

export default function useGetIndustry() {
  const [industry, setIndustry] = useState([])
  const [industryEnum, setIndustryEnum] = useState([])

  useEffect(() => {
    const initIndustry = async () => {
      const getLocalIndustryMap = getSessionStorage('industryMap')
      const getLocalIndustryEnum = getSessionStorage('industryEnum')

      if (getLocalIndustryEnum) {
        setIndustry(getLocalIndustryMap)
        setIndustryEnum(getLocalIndustryEnum)
        return
      }

      const list = await Api.getAllIndustry()
      setIndustry(list)

      const formatIndustryList = (list) => {
        const result = []
        const dfs = (data, parentLabels = []) => {
          data.forEach((item) => {
            if (item.children?.length) {
              dfs(item.children, [...parentLabels, item.label])
            } else {
              result.push({
                label: [...parentLabels, item.label].join('/'),
                value: item.value,
              })
            }
          })
        }
        dfs(list)
        return result
      }

      const formattedList = formatIndustryList(list)
      setIndustryEnum(formattedList)

      setSessionStorage('industryEnum', JSON.stringify(formattedList))
      setSessionStorage('industryMap', JSON.stringify(list))
    }

    initIndustry()
  }, [])

  return {
    industry,
    industryEnum,
  }
}
