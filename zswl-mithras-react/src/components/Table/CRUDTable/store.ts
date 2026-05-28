import { makeAutoObservable } from '@zswl/admin'
import { ModalStore, PageStore, TableStore } from '@zswl/components'

class Store {
  constructor() {
    makeAutoObservable(this)
  }
}
export default Store
