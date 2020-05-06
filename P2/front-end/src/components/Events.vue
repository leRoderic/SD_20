<style>
  @import '../assets/button.css';
  @import '../assets/animate.css';
  @import '../assets/toastr.css';

  .flex-container {
    display: flex;
    flex-wrap: wrap;
    justify-content: center;
  }

  ::-webkit-scrollbar {
    width: 12px;
    background-color: transparent;
  }

  ::-webkit-scrollbar-track {
    -webkit-box-shadow: inset 0 0 6px rgba(0, 0, 0, 0.3);
    border-radius: 10px;
    background-color: transparent;

  }

  ::-webkit-scrollbar-thumb {
    border-radius: 10px;
    background-color: white;
    -webkit-box-shadow: inset 0 0 6px rgba(0, 0, 0, 0.5);
  }

  .card-img-top {
    width: 100%;
    height: 15vw;
    object-fit: cover;
  }

  ul {
    width: auto;
    columns: 2;
    -webkit-columns: 2;
    -moz-columns: 2;
  }

  #vbgnd {
    min-width: 100%;
    min-height: 100%;
    width: auto;
    height: auto;
    position: fixed;
    top: 50%;
    left: 50%;
    transform: translateX(-50%) translateY(-50%);
    z-index: -100;
    background-size: cover;
  }

  @font-face {
    font-family: proxima;
    src: url('../assets/logo-font.otf') format('opentype');
  }

</style>
<template>
  <div id="app">
    <video autoplay muted loop id="vbgnd">
      <source src="../assets/video.mp4" type="video/mp4" id="vbgnd2">
    </video>
    <nav class="navbar navbar-expand-md navbar-dark bg-dark animated slideInDown"
         style="margin-top: -60px; margin-bottom: 40px">
      <div class="navbar-collapse collapse w-100 order-1 order-md-0 dual-collapse2">
      </div>
      <div class="mx-auto order-0">
        <a class="navbar-brand mx-auto animated bounceInLeft" href="#" style="font-family: Proxima; font-size: 3rem; margin-top: -10px;
          margin-bottom: -20px; letter-spacing: 4px; animation-delay: 0.5s">TicketIt!<span class="badge badge-info"
                                                                                           style="font-family: Consolas;
            font-size: 10px">Alpha</span></a>
        <button class="navbar-toggler" type="button" data-toggle="collapse" data-target=".dual-collapse2">
          <span class="navbar-toggler-icon"></span>
        </button>
      </div>
      <div class="navbar-collapse collapse w-100 order-3 dual-collapse2">
        <ul class="navbar-nav ml-auto animated bounceInLeft" style="animation-delay: 0.5s">
          <li class="nav-item">
            <div id="cartbt" v-if="this.logged == true">
              <div class="button" id="button-2" style="margin: 0px; margin-left: 10px">
                <div id="slide"></div>
                <a href="#" @click="this.toggleCart" id="btToggleCart">View cart</a>
              </div>
            </div>
          </li>
          <li class="nav-item">
            <div id="login">
              <div class="button" id="button-2" style="margin: 0px; margin-left: 10px">
                <div id="slide"></div>
                <a href="#/login" id="btLogin">Login</a>
              </div>
            </div>
          </li>
          <li class="nav-item">
            <div id="logout" v-if="this.logged == true">
              <div class="button" id="button-2" style="margin: 0px; margin-left: 10px">
                <div id="slide"></div>
                <a href="#" id="btLogout">Logout</a>
              </div>
            </div>
          </li>
        </ul>
      </div>
    </nav>
    <div id="eventsGrid">
      <div class="flex-container animated slideInUp">
        <div class="card" style="width: 18rem; opacity: 0.88" v-for="(event, index) in events" :key="event.id">
          <img class="card-img-top" :src="getImgUrl(index)" alt="asd">
          <div class="card-body h-100" style="margin-bottom: -10rem">
            <h4 class="card-title">{{ event.event.name}}</h4>
          </div>
          <div class="card-body" style="background-color: #236bef; color: #ffffff; height: 300px; overflow: auto;
            margin-top: 1rem">
            <div>
              <h6 v-for="(artist) in event.event.artists" :key="artist.id" style="list-style-type: none">
                {{artist.name}}</h6>
            </div>
          </div>
          <div class="card-body h-100">
            <h5>{{event.event.city}}</h5>
            <h5>{{event.event.place}}</h5>
            <h5>{{event.event.date.slice(0,2)}}/{{event.event.date.slice(3,5)}}/{{event.event.date.slice(6)}}</h5>
            <h5><b>{{event.event.price}}€</b></h5>
          </div>
          <div class="card-body justify-content-center" style="background-color: #236bef; color: #ffffff;
            margin-top: -5rem">
            <h6>Tickets available: <b>{{event.event.total_available_tickets}}</b></h6>
            <div class="button" id="button-2" style="margin-bottom: 0px" v-if="event.event.total_available_tickets > 0">
              <div id="slide"></div>
              <a @click="addEvent(event.event)">Add to cart</a>
            </div>
            <div class="button" style="margin-bottom: 0px; border: 2px solid #db0404" v-else>
              <a style="color: #db0404"><b>Sold out</b></a>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div class="animated slideInUp" id="cart" style="display: none; background-color: white; opacity: 0.88; width: 90rem">
      <table class="table" style="margin-bottom: -5px">
        <tr class="thead-dark">
          <th scope="col">Event name</th>
          <th scope="col">Quantity</th>
          <th scope="col">Price</th>
          <th scope="col">Total</th>
          <th scope="col"></th>
        </tr>
        <tr class="thead-light" v-for="index in this.events_bought" :key="index.id">
          <td><div style="margin-top: 5px"><b>{{index.name}}</b></div></td>
          <td><button type="button" class="btn btn-danger" id="decreaseTicket" style="border-radius: 50%; width: 35px; height: 35px;
            margin-right: 10px" @click="removeTicket(index)"><b>-</b></button>
            {{index.quant}}
          <button type="button" class="btn btn-success" style="border-radius: 50%; width: 35px; height: 35px;
            margin-left: 10px" id="increaseTicket" @click="addTicket(index)"><b>+</b></button>
          </td>
          <td><div style="margin-top: 5px"><b>{{index.price}}€</b></div></td>
          <td><div style="margin-top: 5px"><b style="color: #236bef">{{index.price * index.quant}}€</b></div></td>
          <td>
            <div class="button" id="button-3" style="margin: 0px; margin-right: -60px; margin-top: -5px; border: 2px solid #db0404">
              <div id="slide2"></div>
              <a class="a2" href="#" id="btRemoveTicket" @click="deleteTicket(index)">Remove ticket</a>
            </div>
          </td>
          <th scope="col"> </th>
        </tr>
        <tr class="thead-light" v-for="index in this.temp" :key="index.id">
          <td><b>{{index.name}}</b></td>
          <td><button type="button" class="btn btn-danger" style="border-radius: 50%; width: 40px; height: 40px;
            margin-right: 10px" v-on:click="decrease(index)" :disabled="getQuantity(index.name) == 0"><b>-</b></button>
            {{getQuantity(index.name)}}
          <button type="button" class="btn btn-success" style="border-radius: 50%; width: 40px; height: 40px;
            margin-left: 10px" v-on:click="increase(index)" ><b>+</b></button>
          </td>
          <td><b>{{index.price}}€</b></td>
          <td><b style="color: #236bef">{{index.price * getQuantity(index.name)}}</b></td>
          <td><button type="button" class="btn btn-danger" style="border-radius: 50%;height: 40px;
            margin-right: 10px;" v-on:click="eliminar(index)"><b>Delete ticket</b></button></td>
        </tr>
      </table>
      <div style="text-align: center; margin-top: 10px" id="emptyCartMessage">
        <h2>Your cart is empty! <a href="#" @click="this.toggleCart" style="color: #236bef"><u>Let's fill it up!</u></a></h2>
      </div>
      <div style="text-align: center; margin-top: 10px" id="checkoutButton">
        <button type="button" class="btn btn-success" style="width: 100%; border-radius: 0%; height: 40px" @click="completePurchase()">
          <h4 style="text-transform: uppercase; text-decoration: none; font-size: .8em; letter-spacing: 1.5px">
          <b>Complete order</b></h4></button>
      </div>
    </div>
    <div id="footer">
      <h5 style="margin-top: 80px; color: white">&copy; Copyright {{this.getYear()}} TicketIt!. All Rights
        Reserved.</h5>
    </div>
  </div>
</template>
<script>
import axios from 'axios'
import * as toastr from '../assets/toastr'

export default {

  created () {
    this.username = this.$route.query.username
    this.logged = this.$route.query.logged
    this.is_admin = this.$route.query.is_admin
    this.token = this.$route.query.token
    this.getEvents()
  },

  data () {
    return {
      events_bought: [],
      events: [],
      temp: [],
      username: '',
      logged: false,
      is_admin: false,
      token: ''
    }
  },

  methods: {
    updateLoggedView () {
      if (this.logged) {
        document.getElementById('cartbt').style.display = 'block'
        document.getElementById('logout').style.display = 'block'
        document.getElementById('login').style.display = 'none'
      } else {
        document.getElementById('cartbt').style.display = 'none'
        document.getElementById('logout').style.display = 'none'
        document.getElementById('login').style.display = 'block'
      }
    },
    toggleCart () {
      // eslint-disable-next-line eqeqeq
      if (document.getElementById('eventsGrid').style.display != 'none') {
        document.getElementById('eventsGrid').style.display = 'none'
        document.getElementById('cart').style.display = 'inline-block'
        document.getElementById('btToggleCart').firstChild.data = 'Back to events'
      } else {
        document.getElementById('eventsGrid').style.display = 'inline-block'
        document.getElementById('cart').style.display = 'none'
        document.getElementById('btToggleCart').firstChild.data = 'View cart'
        this.getEvents()
      }
      // eslint-disable-next-line eqeqeq
      if (this.events_bought.length == 0) {
        document.getElementById('emptyCartMessage').style.display = 'block'
        document.getElementById('checkoutButton').style.display = 'none'
      } else {
        document.getElementById('emptyCartMessage').style.display = 'none'
        document.getElementById('checkoutButton').style.display = 'block'
      }
    },

    getYear () {
      return new Date().getFullYear()
    },

    getImgUrl (index) {
      var images = require.context('../assets/', false, /\.jpg$/)
      return images('./festival' + (index % 10) + '.jpg')
    },
    getEvents () {
      const path = 'http://localhost:5000/events'
      axios.get(path)
        .then((res) => {
          this.events = res.data.events
        })
        .catch((error) => {
          console.error(error)
        })
    },
    addPurchase (parameters, ename) {
      const path = 'http://localhost:5000/orders/admin'
      axios.post(path, parameters)
        .then(() => {
        })
        .catch((error) => {
          var msg = error.response.data.message
          // eslint-disable-next-line eqeqeq
          if (msg == 'Not enough available tickets in event.') {
            toastr.error('', 'Tickets for the event \'' + ename + '\' are sold out!',
              {timeOut: 1500, progressBar: true, newestOnTop: true, positionClass: 'toast-bottom-right'})
            // eslint-disable-next-line eqeqeq
          } else if (msg == 'User does not have enough money to purchase order.') {
            toastr.error('', 'You don\'t have enough money to purchase tickets for the event \'' + ename + '\'!',
              {timeOut: 1500, progressBar: true, newestOnTop: true, positionClass: 'toast-bottom-right'})
          }
        })
    },
    completePurchase () {
      var i, item
      for (i in this.events_bought) {
        item = this.events_bought[i]
        const parameters = {
          'event_id': item.id,
          'tickets_bought': item.quant
        }
        this.addPurchase(parameters, item.name)
      }
      this.events_bought = []
      document.getElementById('emptyCartMessage').style.display = 'block'
      document.getElementById('checkoutButton').style.display = 'none'
    },
    addEvent (event) {
      toastr.success('', 'Added to your cart', {timeOut: 1500, progressBar: true, newestOnTop: true, positionClass: 'toast-bottom-right'})
      var ev = this.searchEvent(event)
      if (ev == null) {
        this.events_bought.push({'name': event.name, 'id': event.id, 'price': event.price, 'quant': 1})
      } else {
        ev.quant += 1
      }
    },
    searchEvent (event) {
      var i, item
      for (i in this.events_bought) {
        item = this.events_bought[i]
        // eslint-disable-next-line eqeqeq
        if (item.id == event.id) {
          return item
        }
      }
    },
    getEventIndex (event) {
      var i, item
      for (i in this.events_bought) {
        item = this.events_bought[i]
        // eslint-disable-next-line eqeqeq
        if (item.id == event.id) {
          return i
        }
      }
      return -1
    },
    addTicket (event) {
      var ev = this.searchEvent(event)
      ev.quant += 1
      toastr.info('', 'Cart updated', {timeOut: 1500, progressBar: true, newestOnTop: true, positionClass: 'toast-bottom-right'})
    },
    removeTicket (event) {
      var ev = this.searchEvent(event)
      ev.quant -= 1
      toastr.info('', 'Cart updated', {timeOut: 1500, progressBar: true, newestOnTop: true, positionClass: 'toast-bottom-right'})
      // eslint-disable-next-line eqeqeq
      if (ev.quant == 0) {
        this.events_bought.splice(this.getEventIndex(event), 1)
        // eslint-disable-next-line eqeqeq
        if (this.events_bought.length == 0) {
          document.getElementById('emptyCartMessage').style.display = 'block'
          document.getElementById('checkoutButton').style.display = 'none'
        }
      }
    },
    deleteTicket (event) {
      this.events_bought.splice(this.getEventIndex(event), 1)
      // eslint-disable-next-line eqeqeq
      if (this.events_bought.length == 0) {
        document.getElementById('emptyCartMessage').style.display = 'block'
        document.getElementById('checkoutButton').style.display = 'none'
      }
    }
  }
}
</script>
