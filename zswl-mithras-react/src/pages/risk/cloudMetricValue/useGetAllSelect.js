import { useEffect, useState } from 'react'
import Api from './api'

export default function () {
  const [allSelect, setAllSelect] = useState({})
  const getSelect = async () => {
    Promise.all([Api.allSelect(), Api.pullDownList()]).then((res) => {
      const pullDownList = {}
      Object.keys(res[1]).map((key) => {
        const list = []
        res[1][key].map((item) => {
          list.push({
            value: item,
            label: item,
          })
        })
        pullDownList[key] = list
      })
      setAllSelect({
        ...res[0],
        ...pullDownList,
      })
    })
  }
  useEffect(() => {
    getSelect()
  }, [])
  return { allSelect, setAllSelect }
}
