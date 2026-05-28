import { Button, Page, Table } from '@zswl/components'
import { observer, http } from '@zswl/admin'
import store from './store'
// import ZTable from './ZTable'
// import ZDesc from './ZDesc'
// import ZFormModal from './ZFormModal'
// import ZForm from './ZForm'

function Index({ path }) {
  const id = 108

  return (
    <Page params={{ id }} store={store}>
      {/* <ZTable />
      <ZDesc />
      <ZForm />
      <ZFormModal id={id} /> */}
    </Page>
  )
}

export default observer(Index)
