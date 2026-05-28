import { App } from '@zswl/components'
import { useEffect } from 'react'
import { history } from '@zswl/admin'
import { isOperationDept } from '@/utils'

function Index() {
  // const lastPath = App.getLastVisitPath()
  // useEffect(() => {
  //   const withoutJumpRouters = ['/login', '/preview/reportPreview', '/preview/pdfPreview']
  //   const hasJumpRouter = withoutJumpRouters.some((router) => lastPath?.includes(router))
  //   if (lastPath && lastPath !== '/' && !hasJumpRouter) {
  //     history.replace(lastPath)
  //   } else {
  //     history.replace('/workbench')
  //   }
  // }, [lastPath])

  useEffect(() => {
    history.replace('/dashboard/workbench')
  }, [])

  return null
}

export default Index
