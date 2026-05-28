module.exports = {
  '/example/list': {
    total: 20,
    'list|20': [
      {
        'id|+1': 1,
        name: '@cname',
        city: '@city',
        age: '@integer(20,50)',
        num: '@integer(10,100)',
        date: '@date',
        check: '@boolean',
        url: '@url',
        email: '@email',
        image: "@image('40x40')",
        color: '@color',
        range: '@range(1,20)',
        'select|1': [0, 1],
      },
    ],
  },
  'POST:/example/remove': {},
}
