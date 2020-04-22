<template>

  <div id="app">

    <h1> {{ message }} </h1>

    <button id="bt" class="btn btn-success btn-lg" @click="buyTicket" :disabled="this.money == 0 || price == 0"> Buy ticket</button>
    <button id="st" class="btn btn-success btn-lg" @click="sellTicket" :disabled="this.tickets_bought == 0 || price == 0"> Sell ticket</button>
    <h4>Money: {{ money }}</h4>
    <h4>Ticket price: <input type="text" id="ticket-price" style="width: 90px" v-model="price"></h4>
    <h4> Total tickets bought: {{ tickets_bought }} </h4>

  </div>

</template>

<script>

export default {

  data () {
    return {

      message: 'My first component',
      messageas: 'My first component',
      tickets_bought: 0,
      price: 10,
      money: 100

    }
  },

  methods: {

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
