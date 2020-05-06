<style>
  @import '../assets/animate.css';
  @import '../assets/toastr.css';
  @import '../assets/login.css';

  @font-face {
    font-family: proxima;
    src: url('../assets/logo-font.otf') format('opentype');
  }

  body {
    display: -ms-flexbox;
    display: -webkit-box;
    display: flex;
    -ms-flex-align: center;
    -ms-flex-pack: center;
    -webkit-box-align: center;
    align-items: center;
    -webkit-box-pack: center;
    justify-content: center;
    padding-top: 40px;
    padding-bottom: 40px;
  }
</style>

<template>
  <div>
    <div class="animated slideInUp" id="signin">
      <div class="card" style="background-color: #343a40">
        <link rel="stylesheet" href="https://use.fontawesome.com/releases/v5.0.8/css/all.css">
        <article class="card-body">
          <h1 class="card-title text-center mb-4 mt-1" style="font-family: Proxima; font-size: 3rem;
           letter-spacing: 4px; animation-delay: 0.5s; color: white">TicketIt!<span class="badge badge-info"
                                                                                    style="font-family: Consolas;
                                                                    font-size: 10px">Alpha</span></h1>
          <hr>
          <form>
            <div class="form-group">
              <h4 style="color: white; margin-bottom: 20px"><b>Sign in</b></h4>
              <div class="input-group">
                <div class="input-group-prepend">
                <span class="input-group-text" style="background-color: #236bef; border-color: #236bef"
                ><i class="fa fa-user" style="color: white"></i> </span>
                </div>
                <input name="" class="form-control" placeholder="Username" type="email" id="username"
                       @click="runAnimation" v-model="username">
              </div>
            </div>
            <div class="form-group">
              <div class="input-group">
                <div class="input-group-prepend">
                <span class="input-group-text" style="background-color: #236bef; border-color: #236bef">
                  <i class="fa fa-lock" style="color: white"></i> </span>
                </div>
                <input class="form-control" placeholder="Password" type="password" id="password" v-model="password">
              </div>
            </div>
            <div class="form-group">
              <div id="login">
                <div class="button" id="button-2" style="margin: 0px; margin-top: 20px">
                  <div id="slide"></div>
                  <a href="#" id="btLogin" @click="doLogin">Sign in</a>
                </div>
              </div>
              <div id="create">
                <div class="button" id="button-2" style="margin: 0px; margin-top: 15px">
                  <div id="slide"></div>
                  <a href="#" id="btCreate" @click="toggleSignIn">Create account</a>
                </div>
              </div>
              <div id="events">
                <div class="button" id="button-2" style="margin: 0px; margin-top: 15px; margin-bottom: -30px">
                  <div id="slide"></div>
                  <a href="/" id="btBack">Back to events</a>
                </div>
              </div>
            </div>
          </form>
        </article>
      </div>
      <div id="footer">
        <h5 style="margin-top: 80px; color: white">&copy; Copyright {{this.getYear()}} TicketIt!. All Rights
          Reserved.</h5>
      </div>
    </div>
    <div class="animated slideInUp" id="createaccount" style="display: none">
      <div class="card" style="background-color: #343a40">
        <link rel="stylesheet" href="https://use.fontawesome.com/releases/v5.0.8/css/all.css">
        <article class="card-body">
          <h1 class="card-title text-center mb-4 mt-1" style="font-family: Proxima; font-size: 3rem;
           letter-spacing: 4px; animation-delay: 0.5s; color: white">TicketIt!<span class="badge badge-info"
                                                                                    style="font-family: Consolas;
                                                                    font-size: 10px">Alpha</span></h1>
          <hr>
          <form>
            <div class="form-group">
              <h4 style="color: white; margin-bottom: 20px"><b>Create account</b></h4>
              <div class="input-group">
                <div class="input-group-prepend">
                <span class="input-group-text" style="background-color: #236bef; border-color: #236bef"
                ><i class="fa fa-user" style="color: white"></i> </span>
                </div>
                <input name="" class="form-control" placeholder="Username" type="email" id="cusername"
                       @click="runAnimation" v-model="username">
              </div>
            </div>
            <div class="form-group">
              <div class="input-group">
                <div class="input-group-prepend">
                <span class="input-group-text" style="background-color: #236bef; border-color: #236bef">
                  <i class="fa fa-lock" style="color: white"></i> </span>
                </div>
                <input class="form-control" placeholder="Password" type="password" id="cpassword" v-model="password">
              </div>
            </div>
            <div class="form-group">
              <div id="login">
                <div class="button" id="button-2" style="margin: 0px; margin-top: 20px">
                  <div id="slide"></div>
                  <a href="#" id="btLogin" @click="createAccount">Create account</a>
                </div>
              </div>
              <div id="create">
                <div class="button" id="button-2" style="margin: 0px; margin-top: 15px">
                  <div id="slide"></div>
                  <a href="#" id="btCreate" @click="resetInput">Reset</a>
                </div>
              </div>
              <div id="events">
                <div class="button" id="button-2" style="margin: 0px; margin-top: 15px; margin-bottom: -30px">
                  <div id="slide"></div>
                  <a href="#" id="btBack" @click="toggleSignIn">Back to sign in</a>
                </div>
              </div>
            </div>
          </form>
        </article>
      </div>
      <div id="footer">
        <h5 style="margin-top: 80px; color: white">&copy; Copyright {{this.getYear()}} TicketIt!. All Rights
          Reserved.</h5>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios'
import * as toastr from '../assets/toastr'

export default {

  created () {

  },

  data () {
    return {
      clicked: false,
      username: '',
      password: '',
      logged: false,
      find_match: false,
      is_admin: false
    }
  },

  methods: {
    checkLogin () {
      const parameters = {
        username: this.username,
        password: this.password
      }
      const path = `http://localhost:5000/login`
      axios.post(path, parameters)
        .then((res) => {
          console.log(res.data.token)
          this.logged = true
          this.token = res.data.token
          this.find_match = true
          this.is_admin = this.getAccount()
          toastr.success('', 'You are logged in!',
            {timeOut: 1500, progressBar: true, newestOnTop: true, positionClass: 'toast-bottom-right'})
          this.$router.push({path: '/', query: {username: this.username, logged: this.logged, is_admin: this.is_admin, token: res.data.token}})
        })
        .catch((error) => {
          // eslint-disable-next-line
          console.error(error)
          this.user = ''
          toastr.error('', 'Username or password incorrect',
            {timeOut: 1500, progressBar: true, newestOnTop: true, positionClass: 'toast-bottom-right'})
        })
    },
    getAccount () {
      const path = `http://localhost:5000/account/` + this.username
      axios.get(path, {})
        .then((res) => {
          // eslint-disable-next-line eqeqeq
          if (res.data.user.is_admin == 1) {
            return true
          } else {
            return false
          }
        })
        .catch((error) => {
          // eslint-disable-next-line
          console.error(error)
          this.user = ''
          toastr.error('', 'Username or password incorrect',
            {timeOut: 1500, progressBar: true, newestOnTop: true, positionClass: 'toast-bottom-right'})
        })
    },
    toggleSignIn () {
      // eslint-disable-next-line eqeqeq
      if (document.getElementById('signin').style.display == 'block') {
        document.getElementById('signin').style.display = 'none'
        document.getElementById('createaccount').style.display = 'block'
      } else {
        document.getElementById('signin').style.display = 'block'
        document.getElementById('createaccount').style.display = 'none'
      }
    },
    doLogin () {
      // eslint-disable-next-line eqeqeq
      if (document.getElementById('username').value == '' || document.getElementById('password').value == '') {
        toastr.info('', 'Fill all fields to continue', {timeOut: 1500, progressBar: true, newestOnTop: true, positionClass: 'toast-bottom-right'})
      } else {
        this.checkLogin()
      }
    },
    createAccount () {
      // eslint-disable-next-line eqeqeq
      if (document.getElementById('cusername').value == '' || document.getElementById('cpassword').value == '') {
        toastr.info('', 'Fill all fields to continue', {timeOut: 1500, progressBar: true, newestOnTop: true, positionClass: 'toast-bottom-right'})
      }
    },
    resetInput () {
      document.getElementById('cusername').value = ''
      document.getElementById('cpassword').value = ''
    },
    getYear () {
      return new Date().getFullYear()
    },
    runAnimation () {
      if (!this.clicked) {
        this.clicked = true
        this.buttonAnimation()
      }
    },
    buttonAnimation () {
      const login = document.getElementById('login')
      const create = document.getElementById('create')
      const events = document.getElementById('events')

      login.classList.add('animated', 'pulse')
      setTimeout(function () {
        login.classList.remove('animated', 'pulse')
        create.classList.add('animated', 'pulse')
      }, 1000)
      setTimeout(function () {
        create.classList.remove('animated', 'pulse')
        events.classList.add('animated', 'pulse')
      }, 2000)
      setTimeout(function () {
        events.classList.remove('animated', 'pulse')
      }, 3000)
      setTimeout(() => this.buttonAnimation(), 5000)
    }
  }
}
</script>

<style scoped>

</style>
