import { useEffect } from 'react'
import { history } from '@zswl/admin'

function RootRedirect() {
  useEffect(() => {
    history.replace('/dashboard/workbench')
  }, [])

  return null
}

export default RootRedirect
