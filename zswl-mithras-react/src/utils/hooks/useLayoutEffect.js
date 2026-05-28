import { useLayoutEffect } from 'react'

const useInternalLayoutEffect = (callback, deps) => {
  const firstMountRef = React.useRef(true)

  useLayoutEffect(() => {
    return callback(firstMountRef.current)
  }, deps)

  useLayoutEffect(() => {
    firstMountRef.current = false
    return () => {
      firstMountRef.current = true
    }
  }, [])
}

export const useLayoutUpdateEffect = (callback, deps) => {
  useInternalLayoutEffect((firstMount) => {
    if (!firstMount) {
      return callback()
    }
  }, deps)
}

export default useLayoutEffect
