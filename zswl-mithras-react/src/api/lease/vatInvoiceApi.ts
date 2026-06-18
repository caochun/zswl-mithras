import { http } from '@zswl/admin'

const mock = false

export default {
  postVatInvoiceAmountCheckout: (data: any): Promise<any> =>
    http.post('/lease/vatInvoice/amountCheckout', data, { mock }),
}
