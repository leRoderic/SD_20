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
  <div>
    <link rel="stylesheet" href="https://use.fontawesome.com/releases/v5.13.0/css/all.css">
    <video autoplay loop muted id="vbgnd">
      <source src="https://leroderic.github.io/video.mp4" type="video/mp4" id="vbgnd2">
    </video>
    <!-- NAVBAR -->
    <nav class="navbar navbar-expand-md navbar-dark bg-dark animated slideInDown"
         style="margin-top: -60px; margin-bottom: 40px; width: 100%">
      <div id="attributes" class="navbar-collapse collapse w-100 order-1 order-md-0 dual-collapse2">
        <div v-if="this.logged == true">
          <i class="fa fa-user-tie" style="color: #236bef; margin-left: 10px"><span
            style="margin-left: 10px; color: white"><b>{{this.username}}</b></span></i>
          <i class="fa fa-money-bill-wave" style="color: #85bb65; margin-left: 10px">
            <span style="margin-left: 10px; color: white"><b>{{this.getUserMoney()}} €</b></span></i>
          <i class="fa fa-ticket-alt regular" style="color: #ffa500; margin-left: 10px">
            <span style="margin-left: 10px; color: white"><b>{{this.numberOfEventsInCart()}}</b></span></i>
        </div>
      </div>
      <div class="mx-auto order-0">
        <a class="navbar-brand mx-auto animated bounceInLeft" href="#" style="font-family: Proxima; font-size: 3rem; margin-top: -10px;
          margin-bottom: -20px; letter-spacing: 4px; animation-delay: 0.5s">TicketIt!<span
          class="badge badge-pill badge-info"
          style="font-family: Consolas;
                                                                    font-size: 10px">Beta</span></a>
        <button class="navbar-toggler" type="button" data-toggle="collapse" data-target=".dual-collapse2">
          <span class="navbar-toggler-icon"></span>
        </button>
      </div>
      <div class="navbar-collapse collapse w-100 order-3 dual-collapse2">
        <ul class="navbar-nav ml-auto animated bounceInLeft" style="animation-delay: 0.5s">
          <li class="nav-item">
            <div id="createEvent" v-if="this.logged == true &&  this.is_admin">
              <div class="button" id="button-2" style="margin: 0px; margin-left: 10px">
                <div id="slide"></div>
                <a href="#" id="btAddEvent" @click="toggleCreateEvent">Add event</a>
              </div>
            </div>
          </li>
          <li class="nav-item">
            <div id="updateEvent" v-if="this.logged == true &&  this.is_admin">
              <div class="button" id="button-2" style="margin: 0px; margin-left: 10px">
                <div id="slide"></div>
                <a href="#" id="btUpdateEvent" @click="toggleUpdateEvent">Update event</a>
              </div>
            </div>
          </li>
          <li class="nav-item">
            <div id="cartbt" v-if="this.logged == true">
              <div class="button" id="button-2" style="margin: 0px; margin-left: 10px">
                <div id="slide"></div>
                <a href="#" @click="this.toggleCart" id="btToggleCart">View cart</a>
              </div>
            </div>
          </li>
          <li class="nav-item">
            <div id="login" v-if="this.money == -1 || this.logged == false">
              <div class="button" id="button-2" style="margin: 0px; margin-left: 10px">
                <div id="slide"></div>
                <router-link to="/userlogin"><a id="btLogin">Login</a></router-link>
              </div>
            </div>
          </li>
          <li class="nav-item">
            <div id="logout" v-if="this.logged == true">
              <div class="button" id="button-2" style="margin: 0px; margin-left: 10px">
                <div id="slide"></div>
                <a href="#" id="btLogout" @click="logOut">Logout</a>
              </div>
            </div>
          </li>
        </ul>
      </div>
    </nav>
    <!-- EVENTS GRID -->
    <div id="eventsGrid">
      <div class="flex-container animated slideInUp">
        <div class="card" style="width: 18rem; opacity: 0.88" v-for="(event, index) in this.events" :key="event.id">
          <img class="card-img-top" :src="getImgUrl(index)" alt="asd">
          <div class="card-body h-100" style="margin-bottom: -10rem">
            <h4 class="card-title">{{event.name}}</h4>
          </div>
          <div class="card-body" style="background-color: #236bef; color: #ffffff; height: 300px; overflow: auto;
            margin-top: 1rem">
            <div>
              <h6 v-for="(artist) in event.artists" :key="artist.id" style="list-style-type: none">
                {{artist.name}}</h6>
            </div>
          </div>
          <div class="card-body h-100">
            <h5>{{event.city}}</h5>
            <h5>{{event.place}}</h5>
            <h5 v-if="event.total_available_tickets >= 0">{{event.date.slice(8)}}/{{event.date.slice(5,7)}}/{{event.date.slice(0,4)}}</h5>
            <h5 v-if="event.total_available_tickets == -1" style="text-decoration: line-through">{{event.date.slice(8)}}/{{event.date.slice(5,7)}}/{{event.date.slice(0,4)}}</h5>
            <h5><b>{{event.price}} €</b></h5>
          </div>
          <div class="card-body justify-content-center" style="background-color: #236bef; color: #ffffff;
            margin-top: -5rem">
            <h6 v-if="event.total_available_tickets >= 0">Tickets available: <b>{{event.total_available_tickets}}</b></h6>
            <h6 v-if="event.total_available_tickets == -1">Tickets available: <b>N/A</b></h6>
            <div class="button" id="button-2" style="margin-bottom: 0px" v-if="event.total_available_tickets > 0">
              <div id="slide"></div>
              <a @click="addEventCart(event)">Add to cart</a>
            </div>
            <div class="button" style="margin-bottom: 0px; border: 2px solid #db0404" v-if="event.total_available_tickets == 0">
              <a style="color: #db0404"><b>Sold out</b></a>
            </div>
            <div class="button" style="margin-bottom: 0px; border: 2px solid #db0404" v-if="event.total_available_tickets == -1">
              <a style="color: #db0404"><b>Cancelled</b></a>
            </div>
            <div class="button" id="button-4" style="margin: 0px; margin-top: 10px; border: 2px solid #fffb00; width: 100px" v-if="logged && is_admin">
              <div id="slide3"></div>
              <a class="a2" @click="toggleAddArtist(event)">
                Add artist</a>
            </div>
            <div class="button" id="button-4" style="margin: 0px; margin-top: 10px; border: 2px solid #fffb00; width: 140px" v-if="logged && is_admin">
              <div id="slide3"></div>
              <a class="a2" @click="toggleRemoveArtist(event)">
                Remove artist</a>
            </div>
            <div class="button" id="button-3" style="margin: 0px; margin-top: 10px; border: 2px solid #db0404" v-if="logged && is_admin">
              <div id="slide2"></div>
              <a class="a2" @click="remveEvent(event.id)">
                Delete event</a>
            </div>
          </div>
        </div>
      </div>
    </div>
    <!-- CART POP-UP -->
    <div class="animated slideInUp" id="cart" style="display: none; background-color: white; opacity: 0.88; width: 80%">
      <table class="table" style="margin-bottom: -5px">
        <tr class="thead-dark">
          <th scope="col">Event name</th>
          <th scope="col">Quantity</th>
          <th scope="col">Price</th>
          <th scope="col">Total</th>
          <th scope="col"></th>
        </tr>
        <tr class="thead-light" v-for="index in this.events_bought" :key="index.id">
          <td>
            <div style="margin-top: 5px"><b>{{index.name}}</b></div>
          </td>
          <td>
            <button type="button" class="btn btn-danger" id="decreaseTicket" style="border-radius: 50%; width: 35px; height: 35px;
            margin-right: 10px" @click="removeTicket(index)"><b>-</b></button>
            {{index.quant}}
            <button type="button" class="btn btn-success" style="border-radius: 50%; width: 35px; height: 35px;
            margin-left: 10px" id="increaseTicket" @click="addTicket(index)" :disabled="index.quant >= 50"><b>+</b>
            </button>
          </td>
          <td>
            <div style="margin-top: 5px"><b>{{index.price}} €</b></div>
          </td>
          <td>
            <div style="margin-top: 5px"><b style="color: #236bef">{{index.price * index.quant}} €</b></div>
          </td>
          <td>
            <div class="button" id="button-3" style="margin: 0px; margin-top: -5px; border: 2px solid #db0404;
              margin-left: 55%; width: 40px">
              <div id="slide2"></div>
              <a class="a2" href="#" id="btRemoveTicket" @click="deleteTicket(index)">
                <i class="fa fa-trash"></i></a>
            </div>
          </td>
        </tr>
      </table>
      <div style="text-align: center; margin-top: 10px" id="emptyCartMessage">
        <h2>Your cart is empty! <a href="#" @click="this.toggleCart" style="color: #236bef"><u>Let's fill it up!</u></a>
        </h2>
      </div>
      <div style="text-align: center; margin-top: 10px" id="checkoutButton">
        <button type="button" class="btn btn-success" style="width: 100%; border-radius: 0%; height: 40px"
                @click="completePurchase()">
          <h4 style="text-transform: uppercase; text-decoration: none; font-size: .8em; letter-spacing: 1.5px">
            <b>Complete order</b></h4></button>
      </div>
    </div>
    <!-- CREATE EVENT FORM -->
    <div class="animated slideInUp" id="createEventForm" style="display: none; opacity: 0.88; width: 30%">
      <div class="card" style="background-color: #343a40; border-radius: 0%">
        <article class="card-body">
          <form>
            <div class="form-group">
              <h4 style="color: white; margin-bottom: 20px"><b>Add new event</b></h4>
              <div class="input-group">
                <div class="input-group-prepend">
                <span class="input-group-text" style="background-color: #236bef; border-color: #236bef"
                ><i class="fas fa-sign" style="color: white"></i> </span>
                </div>
                <input name="" class="form-control" placeholder="Event name" type="text" v-model="addEventForm.name">
              </div>
            </div>
            <div class="form-group">
              <div class="input-group">
                <div class="input-group-prepend">
                <span class="input-group-text" style="background-color: #236bef; border-color: #236bef; width: 42px">
                  <i class="fas fa-euro-sign" style="color: white"></i> </span>
                </div>
                <input class="form-control" placeholder="Event price" type="number" v-model="addEventForm.price">
              </div>
            </div>
            <div class="form-group">
              <div class="input-group">
                <div class="input-group-prepend">
                <span class="input-group-text" style="background-color: #236bef; border-color: #236bef; width: 42px">
                  <i class="fas fa-calendar-day" style="color: white"></i> </span>
                </div>
                <input class="form-control" placeholder="Event date" type="date" v-model="addEventForm.date">
              </div>
            </div>
            <div class="form-group">
              <div class="input-group">
                <div class="input-group-prepend">
                <span class="input-group-text" style="background-color: #236bef; border-color: #236bef; width: 42px">
                  <i class="fas fa-city" style="color: white"></i> </span>
                </div>
                <input class="form-control" placeholder="Event city" type="text" v-model="addEventForm.city">
              </div>
            </div>
            <div class="form-group">
              <div class="input-group">
                <div class="input-group-prepend">
                <span class="input-group-text" style="background-color: #236bef; border-color: #236bef; width: 42px">
                  <i class="fas fa-map-marker-alt" style="color: white"></i> </span>
                </div>
                <input class="form-control" placeholder="Event place" type="text" v-model="addEventForm.place">
              </div>
            </div>
            <div class="form-group">
              <div class="input-group">
                <div class="input-group-prepend">
                <span class="input-group-text" style="background-color: #236bef; border-color: #236bef; width: 42px">
                  <i class="fas fa-ticket-alt" style="color: white"></i> </span>
                </div>
                <input class="form-control" placeholder="Number of tickets" type="number" v-model="addEventForm.total_available_tickets">
              </div>
            </div>
          </form>
        </article>
      </div>
      <div style="text-align: center; margin-top: 0px">
        <button type="button" class="btn btn-danger" style="width: 100%; border-radius: 0%; height: 30px" @click="initForm(addEventForm)">
        <h4 style="text-transform: uppercase; text-decoration: none; font-size: .8em; letter-spacing: 1.5px">
        <b>Reset</b></h4></button>
      </div>
      <div style="text-align: center; margin-top: 0px">
        <button type="button" class="btn btn-success" style="width: 100%; border-radius: 0%; height: 40px" @click="this.submitEvent">
        <h4 style="text-transform: uppercase; text-decoration: none; font-size: .8em; letter-spacing: 1.5px">
        <b>Create event</b></h4></button>
      </div>
    </div>
    <!-- UPDATE EVENT FORM -->
    <div class="animated slideInUp" id="updateEventForm" style="display: none; background-color: white; opacity: 0.88; width: 30%">
      <div class="card" style="background-color: #343a40; border-radius: 0%">
        <article class="card-body">
          <form>
            <div class="form-group">
              <h4 style="color: white; margin-bottom: 20px"><b>Update event</b></h4>
              <div class="input-group" style="margin-bottom: 15px">
                <div class="input-group-prepend">
                <span class="input-group-text" style="background-color: #236bef; border-color: #236bef; color: white"
                >Event</span>
                </div>
                <select class="form-control form-control-sm" style="height: auto" @change="updateEditFormData" id="eventSelector">
                  <option value="-1">Select event to edit</option>
                  <option v-for="(event, index) in events" :key="event.id" :value="index">{{event.name}}</option>
                </select>
              </div>
              <div class="input-group">
                <div class="input-group-prepend">
                <span class="input-group-text" style="background-color: #236bef; border-color: #236bef"
                ><i class="fas fa-sign" style="color: white"></i> </span>
                </div>
                <input class="form-control" placeholder="Event name" type="text" v-model="editEventForm.name" id="editEname" disabled="disabled">
              </div>
            </div>
            <div class="form-group">
              <div class="input-group">
                <div class="input-group-prepend">
                <span class="input-group-text" style="background-color: #236bef; border-color: #236bef; width: 42px">
                  <i class="fas fa-euro-sign" style="color: white"></i> </span>
                </div>
                <input class="form-control" placeholder="Event price" type="number" v-model="editEventForm.price" id="editEprice" disabled="disabled">
              </div>
            </div>
            <div class="form-group">
              <div class="input-group">
                <div class="input-group-prepend">
                <span class="input-group-text" style="background-color: #236bef; border-color: #236bef; width: 42px">
                  <i class="fas fa-calendar-day" style="color: white"></i> </span>
                </div>
                <input class="form-control" placeholder="Event date" type="date" v-model="editEventForm.date" id="editEdate" disabled="disabled">
              </div>
            </div>
            <div class="form-group">
              <div class="input-group">
                <div class="input-group-prepend">
                <span class="input-group-text" style="background-color: #236bef; border-color: #236bef; width: 42px">
                  <i class="fas fa-city" style="color: white"></i> </span>
                </div>
                <input class="form-control" placeholder="Event city" type="text" v-model="editEventForm.city" id="editEcity" disabled="disabled">
              </div>
            </div>
            <div class="form-group">
              <div class="input-group">
                <div class="input-group-prepend">
                <span class="input-group-text" style="background-color: #236bef; border-color: #236bef; width: 42px">
                  <i class="fas fa-map-marker-alt" style="color: white"></i> </span>
                </div>
                <input class="form-control" placeholder="Event location" type="text" v-model="editEventForm.place" id="editEplace" disabled="disabled">
              </div>
            </div>
            <div class="form-group">
              <div class="input-group">
                <div class="input-group-prepend">
                <span class="input-group-text" style="background-color: #236bef; border-color: #236bef; width: 42px">
                  <i class="fas fa-ticket-alt" style="color: white"></i> </span>
                </div>
                <input class="form-control" placeholder="Number of tickets" type="number" v-model="editEventForm.total_available_tickets" id="editEtickets" disabled="disabled">
              </div>
            </div>
          </form>
        </article>
      </div>
      <div style="text-align: center; margin-top: 0px">
        <button type="button" class="btn btn-danger" style="width: 100%; border-radius: 0%; height: 30px" @click="initForm(editEventForm)">
        <h4 style="text-transform: uppercase; text-decoration: none; font-size: .8em; letter-spacing: 1.5px">
        <b>Reset</b></h4></button>
      </div>
      <div style="text-align: center; margin-top: 0px">
        <button type="button" class="btn btn-success" style="width: 100%; border-radius: 0%; height: 40px" @click="this.submitUpdateEvent">
        <h4 style="text-transform: uppercase; text-decoration: none; font-size: .8em; letter-spacing: 1.5px">
        <b>Update event</b></h4></button>
      </div>
    </div>
    <!-- ADD ARTIST FORM -->
    <div class="animated slideInUp" id="addArtistForm" style="display: none; background-color: white; opacity: 0.88; width: auto">
      <div class="card" style="background-color: #343a40; border-radius: 0%">
        <article class="card-body">
          <form>
            <div class="form-group">
              <h4 style="color: white; margin-bottom: 20px"><b>Add artist to: {{this.editArtist.name}}</b></h4>
              <div class="input-group">
                <div class="input-group-prepend">
                <span class="input-group-text" style="background-color: #236bef; border-color: #236bef"
                ><i class="fas fa-user" style="color: white"></i> </span>
                </div>
                <input class="form-control" placeholder="Artist name" type="text" id="aArtistName" v-model="addArtistForm.name">
              </div>
            </div>
            <div class="form-group">
              <div class="input-group">
                <div class="input-group-prepend">
                <span class="input-group-text" style="background-color: #236bef; border-color: #236bef; width: 42px">
                  <i class="fas fa-map-marker-alt" style="color: white"></i> </span>
                </div>
                <input class="form-control" placeholder="Artist country" type="text" id="aArtistCountry" v-model="addArtistForm.country">
              </div>
            </div>
            <div class="form-group">
              <div class="input-group" style="margin-bottom: 15px">
                <div class="input-group-prepend">
                <span class="input-group-text" style="background-color: #236bef; border-color: #236bef; color: white"
                >Genre</span>
                </div>
                <select class="form-control form-control-sm" style="height: auto" id="addGenreSelector" v-model="addArtistForm.genre">
                  <option value="NONE">Select artist genre</option>
                  <option value="REGGAE">Reggae</option>
                  <option value="POP">Pop</option>
                  <option value="TRAP">Trap</option>
                  <option value="HIP HOP">Hip-Hop</option>
                  <option value="ROCK">Rock</option>
                  <option value="INDIE">Indie</option>
                  <option value="HEAVY">Heavy</option>
                  <option value="ELECTRONIC">Electronic</option>
                  <option value="OTHER">Other</option>
                </select>
              </div>
            </div>
          </form>
        </article>
      </div>
      <div class="card" style="background-color: #343a40; border-radius: 0%" >
        <article class="card-body">
          <form>
            <div class="form-group">
              <h4 style="color: white; margin-bottom: 20px"><b>Available artists in database</b></h4>
              <div class="input-group" style="height: 10rem; overflow: auto; width: auto">
                <table class="table" style="margin-bottom: -5px">
                  <tr class="thead-dark">
                    <th scope="col">Name</th>
                    <th scope="col">Country</th>
                    <th scope="col">Genre</th>
                  </tr>
                  <tr class="thead-light" v-for="index in this.artists" :key="index.id">
                    <td v-if="!chkArtistInEvent(index.id)">
                      <div style="margin-top: 5px; color: white"><b>{{index.name}}</b></div>
                    </td>
                    <td v-if="!chkArtistInEvent(index.id)">
                      <div style="margin-top: 5px; color: white"><b>{{index.country}}</b></div>
                    </td>
                    <td v-if="!chkArtistInEvent(index.id)">
                      <div style="margin-top: 5px; color: white"><b style="color: #236bef">{{index.genre}}</b></div>
                    </td>
                  </tr>
                </table>
              </div>
            </div>
          </form>
        </article>
      </div>
      <div style="text-align: center; margin-top: 0px">
        <button type="button" class="btn btn-light" style="width: 100%; border-radius: 0%; height: 30px" @click="toggleAddArtist({
        name: 'None'
      })">
        <h4 style="text-transform: uppercase; text-decoration: none; font-size: .8em; letter-spacing: 1.5px">
        <b>Back to events</b></h4></button>
      </div>
      <div style="text-align: center; margin-top: 0px">
        <button type="button" class="btn btn-danger" style="width: 100%; border-radius: 0%; height: 30px" @click="initFormA(addArtistForm)">
        <h4 style="text-transform: uppercase; text-decoration: none; font-size: .8em; letter-spacing: 1.5px">
        <b>Reset</b></h4></button>
      </div>
      <div style="text-align: center; margin-top: 0px">
        <button type="button" class="btn btn-success" style="width: 100%; border-radius: 0%; height: 40px" @click="eventWhereModifyArtist(editArtist)">
        <h4 style="text-transform: uppercase; text-decoration: none; font-size: .8em; letter-spacing: 1.5px">
        <b>Add artist</b></h4></button>
      </div>
    </div>
    <!-- REMOVE ARTIST FORM -->
    <div class="animated slideInUp" id="removeArtistForm" style="display: none; background-color: white; opacity: 0.88; width: 30%">
      <div class="card" style="background-color: #343a40; border-radius: 0%">
        <article class="card-body">
          <form>
            <div class="form-group">
              <h4 style="color: white; margin-bottom: 20px"><b>Remove artist from: {{this.editArtist.name}}</b></h4>
            </div>
            <div class="form-group">
              <div class="input-group" style="margin-bottom: 15px">
                <div class="input-group-prepend">
                <span class="input-group-text" style="background-color: #236bef; border-color: #236bef; color: white"
                >Select artist</span>
                </div>
                <select class="form-control form-control-sm" style="height: auto" id="remArtistSelector" v-model="deleteArtistForm.id">
                  <option value="-1">Select artist to delete</option>
                  <option v-for="(artist) in artistsList" :key="artist.id" :value="artist.id">{{artist.name}}</option>
                </select>
              </div>
            </div>
          </form>
        </article>
      </div>
      <div style="text-align: center; margin-top: 0px">
        <button type="button" class="btn btn-light" style="width: 100%; border-radius: 0%; height: 30px" @click="toggleRemoveArtist({
        name: 'None'
      })">
        <h4 style="text-transform: uppercase; text-decoration: none; font-size: .8em; letter-spacing: 1.5px">
        <b>Back to events</b></h4></button>
      </div>
      <div style="text-align: center; margin-top: 0px">
        <button type="button" class="btn btn-danger" style="width: 100%; border-radius: 0%; height: 30px" @click="initFormA(deleteArtistForm)">
        <h4 style="text-transform: uppercase; text-decoration: none; font-size: .8em; letter-spacing: 1.5px">
        <b>Reset</b></h4></button>
      </div>
      <div style="text-align: center; margin-top: 0px">
        <button type="button" class="btn btn-success" style="width: 100%; border-radius: 0%; height: 40px" @click="eventWhereDeleteArtist(editArtist)">
        <h4 style="text-transform: uppercase; text-decoration: none; font-size: .8em; letter-spacing: 1.5px">
        <b>Remove artist</b></h4></button>
      </div>
    </div>
    <!-- FOOTER -->
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

  beforeMount () {
    window.addEventListener('beforeunload', this.preventNav)
  },
  beforeDestroy () {
    window.removeEventListener('beforeunload', this.preventNav)
  },
  created () {
    window.history.forward(1)
    this.username = this.$route.query.username
    this.logged = this.$route.query.logged
    this.is_admin = this.$route.query.is_admin
    this.token = this.$route.query.token
    this.getEvents()
    if (this.logged) {
      this.getAttributes()
    }
  },
  data () {
    return {
      events_bought: [],
      events: [],
      temp: [],
      username: '',
      logged: false,
      is_admin: false,
      artistsList: [],
      editArtist: {
        name: 'None'
      },
      token: '',
      ticket_counter: 0,
      currentEventEditId: -1,
      money: -1,
      artists: [],
      artistsEvent: [],
      addEventForm: {
        place: '',
        name: '',
        city: '',
        date: '',
        price: '',
        total_available_tickets: ''
      },
      editEventForm: {
        place: '',
        name: '',
        city: '',
        date: '',
        price: '',
        total_available_tickets: ''
      },
      addArtistForm: {
        id: -1,
        name: '',
        country: '',
        genre: 'NONE'
      },
      deleteArtistForm: {
        id: -1,
        name: '',
        country: '',
        genre: ''
      }
    }
  },
  methods: {
    setInputDisable (s) {
      document.getElementById('editEname').disabled = s
      document.getElementById('editEprice').disabled = s
      document.getElementById('editEdate').disabled = s
      document.getElementById('editEcity').disabled = s
      document.getElementById('editEplace').disabled = s
      document.getElementById('editEtickets').disabled = s
    },
    eventWhereDeleteArtist (event) {
      let selector = document.getElementById('remArtistSelector')
      let artID = selector.options[selector.selectedIndex].value
      const path = 'http://localhost:5000/event/' + event.id + '/artist/' + artID
      axios.delete(path, {auth: {username: this.token}})
        .then((res) => {
          this.toggleRemoveArtist({name: 'None'})
          this.initFormA(this.deleteArtistForm)
          toastr.success('', 'Artist removed from event', {
            timeOut: 1500,
            progressBar: true,
            newestOnTop: true,
            positionClass: 'toast-bottom-right'
          })
        })
        .catch((error) => {
          console.log(error)
          // eslint-disable-next-line eqeqeq
          toastr.error('', 'Artist could not be removed',
            {timeOut: 1500, progressBar: true, newestOnTop: true, positionClass: 'toast-bottom-right'})
        })
    },
    getArtistsEvent (id) {
      const path = 'http://localhost:5000/event/' + id + '/artists'
      axios.get(path)
        .then((res) => {
          this.artistsEvent = res.data.artists
        })
        .catch((error) => {
          console.log(error)
        })
    },
    getArtists () {
      const path = 'http://localhost:5000/artists'
      axios.get(path)
        .then((res) => {
          this.artists = res.data.artists
        })
        .catch((error) => {
          console.log(error)
        })
    },
    getArtistsInEvent (event) {
      const path = 'http://localhost:5000/event/' + event.id + '/artists'
      axios.get(path)
        .then((res) => {
          this.artistsList = res.data.artists
        })
        .catch((error) => {
          console.log(error)
        })
    },
    chkArtistExists (name) {
      var i, item
      for (i in this.artists) {
        item = this.artists[i]
        // eslint-disable-next-line eqeqeq
        if (item.name == name) {
          return true
        }
      }
      return false
    },
    chkArtistInEvent (id) {
      var i, item
      for (i in this.artistsEvent) {
        item = this.artistsEvent[i]
        // eslint-disable-next-line eqeqeq
        if (item.id == id) {
          return true
        }
      }
      return false
    },
    addNewArtist (param) {
      const path = 'http://localhost:5000/artist'
      axios.post(path, param, {auth: {username: this.token}})
        .then((res) => {
        })
        .catch((error) => {
          console.log(error)
        })
    },
    eventWhereModifyArtist (event) {
      this.emptyFormToast(this.addArtistForm)
      let selector = document.getElementById('addGenreSelector')
      let genre = selector.options[selector.selectedIndex].value
      // eslint-disable-next-line eqeqeq
      if (genre == 'NONE') {
        toastr.info('', 'Select artist genre', {timeOut: 1500, progressBar: true, newestOnTop: true, positionClass: 'toast-bottom-right'})
      } else {
        const path = 'http://localhost:5000/event/' + event.id + '/artist'
        var params = {
          name: this.capitalize(this.addArtistForm.name),
          country: this.capitalize(this.addArtistForm.country),
          genre: genre
        }
        this.getArtists()
        if (!this.chkArtistExists(params.name)) {
          console.log('does not exist')
          this.addNewArtist(params)
        }
        axios.post(path, params, {auth: {username: this.token}})
          .then((res) => {
            this.toggleAddArtist({name: 'None'})
            this.initFormA(this.addArtistForm)
            toastr.success('', 'Artist added to event', {
              timeOut: 1500,
              progressBar: true,
              newestOnTop: true,
              positionClass: 'toast-bottom-right'
            })
          })
          .catch((error) => {
            console.log(error)
            // eslint-disable-next-line eqeqeq
            toastr.error('', 'Artist could not be added, because it either does not exist or the data typed into the' +
              'form is wrong.',
            {timeOut: 1500, progressBar: true, newestOnTop: true, positionClass: 'toast-bottom-right'})
          })
      }
    },
    capitalize (str) {
      var splitStr = str.toLowerCase().split(' ')
      for (var i = 0; i < splitStr.length; i++) {
        splitStr[i] = splitStr[i].charAt(0).toUpperCase() + splitStr[i].substring(1)
      }
      return splitStr.join(' ')
    },
    updateEditFormData () {
      let selector = document.getElementById('eventSelector')
      let index = selector.options[selector.selectedIndex].value
      // eslint-disable-next-line eqeqeq
      if (index == -1) {
        this.setInputDisable(true)
        this.initForm(this.editEventForm)
      } else {
        this.setInputDisable(false)
        this.currentEventEditId = this.events[index].id
        this.editEventForm.name = this.events[index].name
        this.editEventForm.price = this.events[index].price
        this.editEventForm.date = this.events[index].date
        this.editEventForm.city = this.events[index].city
        this.editEventForm.place = this.events[index].place
        this.editEventForm.total_available_tickets = this.events[index].total_available_tickets
      }
    },
    preventNav (event) {
      event.preventDefault()
    },
    logOut () {
      this.logged = false
      this.username = ''
      this.token = ''
      this.is_admin = false
      if (document.getElementById('createEventForm').style.display !== 'none') {
        this.toggleCreateEvent()
      } else if (document.getElementById('updateEventForm').style.display !== 'none') {
        this.toggleUpdateEvent()
      } else if (document.getElementById('cart').style.display !== 'none') {
        this.toggleCart()
      } else if (document.getElementById('removeArtistForm').style.display !== 'none') {
        this.toggleRemoveArtist(null)
      } else if (document.getElementById('addArtistForm').style.display !== 'none') {
        this.toggleAddArtist(null)
      }
      toastr.success('', 'Logged out successfully', {
        timeOut: 1500,
        progressBar: true,
        newestOnTop: true,
        positionClass: 'toast-bottom-right'
      })
    },
    addEvent (params) {
      const path = `http://localhost:5000/event`
      axios.post(path, params, {auth: {username: this.token}})
        .then((res) => {
          this.getEvents()
          this.getAttributes()
          this.toggleCreateEvent()
          this.initForm(this.addEventForm)
          toastr.success('', 'Event created!', {
            timeOut: 1500,
            progressBar: true,
            newestOnTop: true,
            positionClass: 'toast-bottom-right'
          })
        })
        .catch((error) => {
          console.log(error)
          // eslint-disable-next-line eqeqeq
          toastr.error('', 'Event could not be created',
            {timeOut: 1500, progressBar: true, newestOnTop: true, positionClass: 'toast-bottom-right'})
        })
    },
    remveEvent (id) {
      const path = 'http://localhost:5000/event/' + id
      axios.delete(path, {auth: {username: this.token}})
        .then((res) => {
          this.getEvents()
          toastr.success('', 'Event deleted', {
            timeOut: 1500,
            progressBar: true,
            newestOnTop: true,
            positionClass: 'toast-bottom-right'
          })
        })
        .catch((error) => {
          console.log(error)
          // eslint-disable-next-line eqeqeq
          toastr.error('', 'Event could not be deleted',
            {timeOut: 1500, progressBar: true, newestOnTop: true, positionClass: 'toast-bottom-right'})
        })
    },
    submitEvent () {
      this.emptyFormToast(this.addEventForm)
      const parameters = {
        place: this.addEventForm.place,
        name: this.addEventForm.name,
        city: this.addEventForm.city,
        country: this.addEventForm.country,
        date: this.addEventForm.date,
        price: this.addEventForm.price,
        total_available_tickets: this.addEventForm.total_available_tickets
      }
      this.addEvent(parameters)
    },
    updateEvent (params) {
      const path = `http://localhost:5000/event/`
      axios.put(path + this.currentEventEditId, params, {auth: {username: this.token}})
        .then((res) => {
          this.getEvents()
          this.getAttributes()
          this.toggleUpdateEvent()
          this.initForm(this.editEventForm)
          toastr.success('', 'Event updated!', {
            timeOut: 1500,
            progressBar: true,
            newestOnTop: true,
            positionClass: 'toast-bottom-right'
          })
        })
        .catch((error) => {
          console.log(error)
          // eslint-disable-next-line eqeqeq
          toastr.error('', 'Event could not be updated',
            {timeOut: 1500, progressBar: true, newestOnTop: true, positionClass: 'toast-bottom-right'})
        })
    },
    submitUpdateEvent () {
      this.emptyFormToast(this.editEventForm)
      const parameters = {
        place: this.editEventForm.place,
        name: this.editEventForm.name,
        city: this.editEventForm.city,
        country: this.editEventForm.country,
        date: this.editEventForm.date,
        price: this.editEventForm.price,
        total_available_tickets: this.editEventForm.total_available_tickets
      }
      this.updateEvent(parameters)
    },
    initFormA (form) {
      form.id = -1
      form.name = ''
      form.country = ''
      form.genre = 'NONE'
    },
    initForm (form) {
      form.place = ''
      form.name = ''
      form.city = ''
      form.date = ''
      form.price = ''
      form.total_available_tickets = ''
    },
    emptyFormToast (form) {
      if (form.place === '' || form.name === '' || form.city === '' || form.date === '' || form.price === '' ||
      form.total_available_tickets === '') {
        toastr.info('', 'Fill all fields to continue', {timeOut: 1500, progressBar: true, newestOnTop: true, positionClass: 'toast-bottom-right'})
      }
    },
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
    attributesAnimation () {
      const login = document.getElementById('attributes')

      login.classList.add('animated', 'bounce')
      setTimeout(function () {
        login.classList.remove('animated', 'bounce')
      }, 1000)
      setTimeout(() => this.buttonAnimation(), 5000)
    },
    toggleAddArtist (event) {
      this.editArtist = event
      // eslint-disable-next-line eqeqeq
      if (document.getElementById('eventsGrid').style.display != 'none') {
        document.getElementById('eventsGrid').style.display = 'none'
        document.getElementById('createEvent').style.display = 'none'
        document.getElementById('cartbt').style.display = 'none'
        document.getElementById('updateEvent').style.display = 'none'
        document.getElementById('addArtistForm').style.display = 'inline-block'
        this.getArtists()
        this.getArtistsEvent(event.id)
      } else {
        this.getEvents()
        document.getElementById('eventsGrid').style.display = 'inline-block'
        document.getElementById('createEvent').style.display = 'inline-block'
        document.getElementById('cartbt').style.display = 'inline-block'
        document.getElementById('updateEvent').style.display = 'inline-block'
        document.getElementById('addArtistForm').style.display = 'none'
      }
    },
    toggleRemoveArtist (event) {
      this.editArtist = event
      // eslint-disable-next-line eqeqeq
      if (document.getElementById('eventsGrid').style.display != 'none') {
        document.getElementById('eventsGrid').style.display = 'none'
        document.getElementById('createEvent').style.display = 'none'
        document.getElementById('cartbt').style.display = 'none'
        document.getElementById('updateEvent').style.display = 'none'
        document.getElementById('removeArtistForm').style.display = 'inline-block'
        this.getArtistsInEvent(event)
      } else {
        this.getEvents()
        document.getElementById('eventsGrid').style.display = 'inline-block'
        document.getElementById('createEvent').style.display = 'inline-block'
        document.getElementById('cartbt').style.display = 'inline-block'
        document.getElementById('updateEvent').style.display = 'inline-block'
        document.getElementById('removeArtistForm').style.display = 'none'
      }
    },
    toggleUpdateEvent () {
      // eslint-disable-next-line eqeqeq
      if (document.getElementById('eventsGrid').style.display != 'none') {
        document.getElementById('eventsGrid').style.display = 'none'
        document.getElementById('createEvent').style.display = 'none'
        document.getElementById('cartbt').style.display = 'none'
        document.getElementById('updateEventForm').style.display = 'inline-block'
        document.getElementById('btUpdateEvent').firstChild.data = 'Back to events'
      } else {
        document.getElementById('eventsGrid').style.display = 'inline-block'
        document.getElementById('createEvent').style.display = 'inline-block'
        document.getElementById('cartbt').style.display = 'inline-block'
        document.getElementById('updateEventForm').style.display = 'none'
        document.getElementById('btUpdateEvent').firstChild.data = 'Update event'
        this.getEvents()
      }
    },
    toggleCreateEvent () {
      // eslint-disable-next-line eqeqeq
      if (document.getElementById('eventsGrid').style.display != 'none') {
        document.getElementById('eventsGrid').style.display = 'none'
        document.getElementById('updateEvent').style.display = 'none'
        document.getElementById('cartbt').style.display = 'none'
        document.getElementById('createEventForm').style.display = 'inline-block'
        document.getElementById('btAddEvent').firstChild.data = 'Back to events'
      } else {
        document.getElementById('eventsGrid').style.display = 'inline-block'
        document.getElementById('updateEvent').style.display = 'inline-block'
        document.getElementById('cartbt').style.display = 'inline-block'
        document.getElementById('createEventForm').style.display = 'none'
        document.getElementById('btAddEvent').firstChild.data = 'Add event'
        this.getEvents()
      }
    },
    toggleCart () {
      // eslint-disable-next-line eqeqeq
      if (document.getElementById('eventsGrid').style.display != 'none') {
        document.getElementById('eventsGrid').style.display = 'none'
        document.getElementById('cart').style.display = 'inline-block'
        document.getElementById('btToggleCart').firstChild.data = 'Back to events'
        if (this.is_admin) {
          document.getElementById('updateEvent').style.display = 'none'
          document.getElementById('createEvent').style.display = 'none'
        }
      } else {
        document.getElementById('eventsGrid').style.display = 'inline-block'
        document.getElementById('cart').style.display = 'none'
        document.getElementById('btToggleCart').firstChild.data = 'View cart'
        if (this.is_admin) {
          document.getElementById('updateEvent').style.display = 'inline-block'
          document.getElementById('createEvent').style.display = 'inline-block'
        }
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
    getUserMoney () {
      if (this.money >= 922337203) {
        return '∞'
      } else {
        return this.money
      }
    },
    getAttributes () {
      const path = `http://localhost:5000/account/` + this.username
      axios.get(path, {})
        .then((res) => {
          this.money = res.data.account.available_money
        })
        .catch((error) => {
          // eslint-disable-next-line
            console.error(error)
        })
    },
    getYear () {
      return new Date().getFullYear()
    },
    getImgUrl (index) {
      /* Initial code used stored images. Since Heroku does not save media with the free plans, we've switched to images
        hosted online. There is no reason to go as far with AmazonWS, since this is just an assignment, so Imgur's been used
        for hosting our images and Gofile our background video.
        var images = require.context('../assets/', false, /\.jpg$/)
        return images('./festival' + (index % 12) + '.jpg') */
      var number = index % 24
      switch (number) {
        case 0:
          return 'https://i.imgur.com/DZh2KQp.jpg'
        case 1:
          return 'https://i.imgur.com/wapXLGY.jpg'
        case 2:
          return 'https://i.imgur.com/GG0weyI.jpg'
        case 3:
          return 'https://i.imgur.com/bPaM6QP.jpg'
        case 4:
          return 'https://i.imgur.com/sdQsE5I.jpg'
        case 5:
          return 'https://i.imgur.com/zfLjQkT.jpg'
        case 6:
          return 'https://i.imgur.com/sgXVYrE.jpg'
        case 7:
          return 'https://i.imgur.com/SlTBxgZ.jpg'
        case 8:
          return 'https://i.imgur.com/h7X4a4C.jpg'
        case 9:
          return 'https://i.imgur.com/84dpYra.jpg'
        case 10:
          return 'https://i.imgur.com/nAv2sx7.jpg'
        case 11:
          return 'https://i.imgur.com/PXFjPIR.jpg'
        case 12:
          return 'https://i.imgur.com/LXn81qH.jpg'
        case 13:
          return 'https://i.imgur.com/883y7kz.jpg'
        case 14:
          return 'https://i.imgur.com/1IqD8uR.jpg'
        case 15:
          return 'https://i.imgur.com/AZUDTLp.jpg'
        case 16:
          return 'https://i.imgur.com/hKgORps.jpg'
        case 17:
          return 'https://i.imgur.com/rtzmmZj.jpg'
        case 18:
          return 'https://i.imgur.com/xMZDO8r.jpg'
        case 19:
          return 'https://i.imgur.com/MzCrLvx.jpg'
        case 20:
          return 'https://i.imgur.com/tYVX7WS.jpg'
        case 21:
          return 'https://i.imgur.com/stjmyP9.jpg'
        case 22:
          return 'https://i.imgur.com/1yEsNnZ.jpg'
        case 23:
          return 'https://i.imgur.com/BhBxz5H.jpg'
      }
    },
    numberOfEventsInCart () {
      var i, item
      var quant = 0
      for (i in this.events_bought) {
        item = this.events_bought[i]
        quant += item.quant
      }
      return quant
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
      const path = `http://localhost:5000/orders/${this.username}`
      axios.post(path, parameters, {auth: {username: this.token}})
        .then((res) => {
          this.getEvents()
          this.getAttributes()
          this.toggleCart()
          toastr.success('', 'Order completed!', {
            timeOut: 1500,
            progressBar: true,
            newestOnTop: true,
            positionClass: 'toast-bottom-right'
          })
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
          'id_event': item.id,
          'tickets_bought': item.quant
        }
        this.addPurchase(parameters, item.name)
      }
      this.events_bought = []
      document.getElementById('emptyCartMessage').style.display = 'block'
      document.getElementById('checkoutButton').style.display = 'none'
    },
    addEventCart (event) {
      if (this.logged) {
        var ev = this.searchEvent(event)
        if (ev == null) {
          this.events_bought.push({'name': event.name, 'id': event.id, 'price': event.price, 'quant': 1})
          toastr.success('', 'Added to your cart', {
            timeOut: 1500,
            progressBar: true,
            newestOnTop: true,
            positionClass: 'toast-bottom-right'
          })
        } else {
          // eslint-disable-next-line eqeqeq
          if (ev.quant == 50) {
            toastr.info('', 'Tickets are limited to 50 tickets per order', {
              timeOut: 2500,
              progressBar: true,
              newestOnTop: true,
              positionClass: 'toast-bottom-right'
            })
          } else {
            ev.quant += 1
            toastr.success('', 'Added to your cart', {
              timeOut: 1500,
              progressBar: true,
              newestOnTop: true,
              positionClass: 'toast-bottom-right'
            })
          }
        }
      } else {
        toastr.info('', 'Sign in to your account first', {
          timeOut: 1500,
          progressBar: true,
          newestOnTop: true,
          positionClass: 'toast-bottom-right'
        })
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
      // eslint-disable-next-line eqeqeq
      if (ev.quant == 49) {
        toastr.info('', 'Tickets are limited to 50 tickets per order', {
          timeOut: 2500,
          progressBar: true,
          newestOnTop: true,
          positionClass: 'toast-bottom-right'
        })
      } else {
        toastr.info('', 'Cart updated', {
          timeOut: 1500,
          progressBar: true,
          newestOnTop: true,
          positionClass: 'toast-bottom-right'
        })
      }
      ev.quant += 1
    },
    removeTicket (event) {
      var ev = this.searchEvent(event)
      ev.quant -= 1
      toastr.info('', 'Cart updated', {
        timeOut: 1500,
        progressBar: true,
        newestOnTop: true,
        positionClass: 'toast-bottom-right'
      })
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
