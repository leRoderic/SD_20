<style>
    .flex-container {
      display: flex;
      flex-wrap: wrap;
      justify-content: center;
  }
  .card-img-top {
    width: 100%;
    height: 15vw;
    object-fit: cover;
}
</style>
<template>
  <div id="app">
    <h1> {{ message }} </h1>
    <div class="flex-container">
      <div class="card" style="width: 18rem" v-for="(event, index) in events" :key="event.id">
        <img class="card-img-top" :src="getImgUrl(index)" alt="Card image cap">
        <div class="card-body h-100">
          <h4 class="card-title">{{ event.name }}</h4>
          <h5 v-for="(artist) in event.artists" :key="artist.id">{{artist.name}}</h5>
          <h6>{{event.city}}</h6>
          <h6>{{event.place}}</h6>
          <h6>{{event.date}}</h6>
          <h6>{{event.price}}€</h6>
        </div>
        <div class="card-body justify-content-center" style="background-color: #236bef; color: #ffffff">
          <h6>Tickets available: 0</h6>
          <button id="buyTicketEvent" v-on:click="this.events_bought.push(event)" class="btn btn-success btn-lg">Add to cart</button>
        </div>
      </div>
    </div>
    <button id="bt" class="btn btn-success btn-lg" @click="buyTicket" :disabled="this.money == 0 || price == 0"> Buy
      ticket
    </button>
    <button id="st" class="btn btn-success btn-lg" @click="sellTicket"
            :disabled="this.tickets_bought == 0 || price == 0"> Sell ticket
    </button>
    <h4>Money: {{ money }}</h4>
    <h4>Ticket price: <input type="text" id="ticket-price" style="width: 90px" v-model="price"></h4>
    <h4> Total tickets bought: {{ tickets_bought }} </h4>

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
      events: []
    }
  },

  methods: {

    getImgUrl (index) {
      var images = require.context('../assets/', false, /\.jpg$/)
      return images('./festival' + ((index + 1) % 7) + '.jpg')
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

    buyTicket () {
      if (!this.price) {
        this.message = 'Set ticket price first'
      } else {
        if (this.money >= this.price) {
          this.message = this.messageas
          this.tickets_bought += 1
          this.money -= parseInt(this.price)
        } else {
          this.message = 'You are broke & can\'t buy tickets!'
        }
      }
    },

    sellTicket () {
      if (!this.price) {
        this.message = 'Set ticket price first'
      } else {
        if (this.tickets_bought > 0) {
          this.message = this.messageas
          this.tickets_bought -= 1
          this.money += parseInt(this.price)
        } else {
          this.message = 'You don\t have any tickets!'
        }
      }
    }

  }

}

</script>
