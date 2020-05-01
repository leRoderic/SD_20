<style>
  @import '../assets/button.css';
  @import '../assets/animate.css';
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
    -webkit-box-shadow: inset 0 0 6px rgba(0,0,0,0.3);
    border-radius: 10px;
    background-color: transparent;

  }
  ::-webkit-scrollbar-thumb {
    border-radius: 10px;
    background-color: white;
    -webkit-box-shadow: inset 0 0 6px rgba(0,0,0,0.5);
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
      <source src="../assets/video.mp4" type="video/mp4">
    </video>
    <div>
      <nav class="navbar navbar-expand-md navbar-dark bg-dark animated slideInDown" style="margin-top: -60px; margin-bottom: 40px">
        <div class="navbar-collapse collapse w-100 order-1 order-md-0 dual-collapse2">
        </div>
        <div class="mx-auto order-0">
          <a class="navbar-brand mx-auto animated bounceInLeft" href="#" style="font-family: Proxima; font-size: 3rem; margin-top: -10px;
          margin-bottom: -20px; letter-spacing: 4px; animation-delay: 0.5s">TicketIt!<span class="badge badge-info" style="font-family: Consolas;
            font-size: 10px">Alpha</span></a>
          <button class="navbar-toggler" type="button" data-toggle="collapse" data-target=".dual-collapse2">
            <span class="navbar-toggler-icon"></span>
          </button>
        </div>
        <div class="navbar-collapse collapse w-100 order-3 dual-collapse2">
          <ul class="navbar-nav ml-auto animated bounceInLeft" style="animation-delay: 0.5s">
            <li class="nav-item">
              <div class="button" id="button-2" style="margin: 0px; margin-left: 10px">
                <div id="slide"></div>
                <a href="#" onclick="console.log(this.events_bought)" id="btLogin">View cart</a>
              </div>
            </li>
            <li class="nav-item">
              <div class="button" id="button-2" style="margin: 0px; margin-left: 10px">
                <div id="slide"></div>
                <a href="#" id="btLogin">Login</a>
              </div>
            </li>
            <li class="nav-item">
              <div class="button" id="button-2" style="margin: 0px; margin-left: 10px">
                <div id="slide"></div>
                <a href="#" id="btLogin">Logout</a>
              </div>
            </li>
          </ul>
        </div>
      </nav>
      <div class="flex-container">
        <div class="card" style="width: 18rem; opacity: 0.88" v-for="(event, index) in events" :key="event.id">
          <img class="card-img-top" :src="getImgUrl(index)" alt="asd">
          <div class="card-body h-100" style="margin-bottom: -10rem">
            <h4 class="card-title">{{ event.event.name}}</h4>
          </div>
          <div class="card-body" style="background-color: #236bef; color: #ffffff; height: 300px; overflow: auto;
            margin-top: 1rem">
            <div>
              <h6 v-for="(artist) in event.event.artists" :key="artist.id" style="list-style-type: none">{{artist.name}}</h6>
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
            <div class="button" id="button-2" style="margin-bottom: 0px">
              <div id="slide"></div>
              <a >Add to cart</a>
            </div>
          </div>
        </div>
      </div>
    </div>
    <h5 style="margin-top: 80px; color: white">&copy; Copyright {{this.getYear()}} TicketIt!. All Rights Reserved.</h5>
  </div>
</template>
<script>
import axios from 'axios'

export default {

  created () {
    this.getEvents()
  },

  data () {
    return {
      message: 'TicketIt!',
      tickets_bought: 0,
      price: 10,
      money: 100,
      events_bought: [],
      events: [],
      temp: []
    }
  },

  methods: {

    getYear () {
      return new Date().getFullYear()
    },

    getImgUrl (index) {
      var images = require.context('../assets/', false, /\.jpg$/)
      return images('./festival' + (index + 1) + '.jpg')
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

    purchase (event) {
      this.events_bought.push(event)
    }
  }

}

</script>
