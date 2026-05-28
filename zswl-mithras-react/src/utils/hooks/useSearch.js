import { useEffect, useRef } from 'react'

export default function useSearch({ query, store }) {
  const { search } = query
  useEffect(() => {
    if (search) {
      store.setParams(JSON.parse(search))
      store.search()
    } else {
      store.setParams()
      store.search()
    }
  }, [search])
}
