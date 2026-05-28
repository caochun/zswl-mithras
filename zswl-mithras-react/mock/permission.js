module.exports = {
  '/user/list': {
    total: 10,
    'userList|10': [
      {
        'id|+1': 1,
        name: '@cname',
        orgName: '@cname',
        city: '@city',
        age: '@integer(20,50)',
        num: '@integer(10,100)',
        expiration: '@date',
        check: '@boolean',
        url: '@url',
        email: '@email',
        image: "@image('40x40')",
        color: '@color',
        range: '@range(1,20)',
        'status|1': [0, 1],
      },
    ],
  },
  'PUT:/user/resetPwd': {},
  'PUT:/user/status': {},
  'DELETE:/user': {},
}
