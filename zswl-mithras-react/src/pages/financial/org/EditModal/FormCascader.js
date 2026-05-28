import { Cascader } from 'antd'
import { http, observer } from '@zswl/admin'
import _ from 'lodash'
import { useEffect, useState } from 'react'

// 地区获取省/市/区列表
const getRegionListApi = (params) => http.get('/select/region/child', { params })

function Index({ value, ...rest }) {
  const [regionList, setRegionList] = useState([])

  const getRegionList = async (code) => {
    const list = await getRegionListApi({ code })
    const newRegionList = list.map((item) => {
      if (['810000', '820000', '710000'].includes(item.value)) {
        return {
          ...item,
          isLeaf: true,
        }
      }
      return {
        ...item,
        isLeaf: item.leaf,
      }
    })
    return newRegionList
  }
  const initRegionList = async () => {
    const newRegionList = await getRegionList('156')
    const deepGetRegionList = async (codeList, deppList) => {
      const code = codeList.shift()
      const findItem = deppList.find((item) => item.value === code)
      if (findItem) {
        if (findItem.isLeaf) {
          return
        }
        const data = await getRegionList(code)
        findItem.children = data
        await deepGetRegionList(codeList, data)
      }
    }
    if (_.isArray(value) && value.length > 0) {
      await deepGetRegionList([...value], newRegionList)
    }
    setRegionList(newRegionList)
  }

  useEffect(() => {
    initRegionList()
  }, [JSON.stringify(value)])
  const loadRegionListChild = async (selectedOptions) => {
    const targetOption = selectedOptions[selectedOptions.length - 1]
    if (targetOption.children) {
      return
    }
    targetOption.loading = true
    const data = await getRegionList(targetOption.value)
    targetOption.loading = false
    targetOption.children = data
    const newRegionList = [...regionList]
    setRegionList(newRegionList)
  }

  return (
    <Cascader
      placeholder="请选择"
      options={regionList}
      loadData={loadRegionListChild}
      value={value}
      {...rest}
    />
  )
}

export default observer(Index)
