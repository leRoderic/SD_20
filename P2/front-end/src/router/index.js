import Vue from 'vue'
import Router from 'vue-router'
import Events from '@/components/Events'

Vue.use(Router)

export default new Router({
  routes: [
    {
      path: '/',
      name: 'Events',
      component: Events,
      meta: {
        title: 'TicketIt! - Home'
      }
    }
  ]
})
