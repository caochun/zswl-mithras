import { useState, useCallback } from 'react'

export default function useForceUpdate() {
  const [, setCount] = useState(0)
  return useCallback(() => {
    setCount((c) => c + 1)
  }, [])
}
