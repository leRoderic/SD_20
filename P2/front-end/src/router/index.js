import Vue from 'vue'
import Router from 'vue-router'
// HelloWorld from '@/components/HelloWorld'
import Events from '@/components/Events'

Vue.use(Router)

export default new Router({
  routes: [
    {
      path: '/',
      name: 'Events',
      component: Events
    }
  ]
})
