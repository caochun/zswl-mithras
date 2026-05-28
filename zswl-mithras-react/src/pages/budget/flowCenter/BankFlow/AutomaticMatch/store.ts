import { makeAutoObservable } from '@zswl/admin'

class Store {
  constructor({ type }) {
    makeAutoObservable(this)
  }
}

export default Store
