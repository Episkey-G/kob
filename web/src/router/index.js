import { createRouter, createWebHistory } from 'vue-router'
import PkIndexView from '@/views/pk/PkIndexView.vue'
import RecordIndexView from '@/views/record/RecordIndexView.vue'
import RecordContentView from '@/views/record/RecordContentView.vue'
import RankLisIndextView from '@/views/ranklist/RankListIndexView.vue'
import GameLisIndextView from '@/views/gamelist/GameListIndexView.vue'
import UserBotIndexView from '@/views/user/bot/UserBotIndexView.vue'
import UserAccountProfileView from '@/views/user/account/UserAccountProfileView';
import NotFound from '@/views/error/NotFound.vue'
import UserAccountLoginView from "@/views/user/account/UserAccountLoginView.vue";
import UserAccountRegisterView from "@/views/user/account/UserAccountRegisterView.vue";
import store from "@/store/index";

const routes = [
  {
    path: '/',
    name: 'home',
    redirect: '/gamelist/',
    meta: {
      requestAuth: true,
    },
  },
  {
    path: '/pk/',
    name: 'pk_index',
    component: PkIndexView,
    meta: {
      requestAuth: true,
    },
  },
  {
    path: '/record/',
    name: 'record_index',
    component: RecordIndexView,
    meta: {
      requestAuth: true,
    },
  },
  {
    path: '/record/:recordId/',
    name: 'record_content',
    component: RecordContentView,
    meta: {
      requestAuth: true,
    },
  },
  {
    path: '/ranklist/',
    name: 'ranklist_index',
    component: RankLisIndextView,
    meta: {
      requestAuth: true,
    },
  },
  {
    path: '/gamelist/',
    name: 'gamelist_index',
    component: GameLisIndextView,
    meta: {
      requestAuth: true,
    },
  },
  {
    path: '/user/bot/',
    name: 'user_bot_index',
    component: UserBotIndexView,
    meta: {
      requestAuth: true,
    },
  },
  {
    path: '/user/account/profile/',
    name: 'user_account_profile',
    component: UserAccountProfileView,
    meta: {
      requestAuth: true,
    },
  },
  {
    path: '/user/account/login/',
    name: 'user_account_login',
    component: UserAccountLoginView,
    meta: {
      requestAuth: false,
    },
  },
  {
    path: '/user/account/register/',
    name: 'user_account_register',
    component: UserAccountRegisterView,
    meta: {
      requestAuth: false,
    },
  },
  {
    path: '/404/',
    name: '404',
    component: NotFound,
    meta: {
      requestAuth: false,
    },
  },
  {
    path: '/:catchall(.*)',
    redirect: '/404/',
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
});

router.beforeEach((to, from, next) => {

  let flag = 1;
  
  // const jwt_token = localStorage.getItem("jwt_token");
  // if (jwt_token) {
  //   store.commit("updateToken", jwt_token);
  //   store.dispatch("getInfo", {
  //     success() {
  //       store.dispatch("updatePullingInfo", false);
  //     },
  //     error() {
  //       store.dispatch("updatePullingInfo", false);
  //       alert("token无效,请重新登录！");
  //       router.push({ name: 'user_account_login' });
  //     }
  //   });
  // } else {
  //   store.dispatch("updatePullingInfo", false);
  //   flag = 2;
  // }

  const jwt_token = store.state.user.token;
  if (jwt_token) {
    store.commit("updateToken", jwt_token);
    store.dispatch("getInfo", {
      success() {
        store.dispatch("updatePullingInfo", false);
      },
      error() {
        store.dispatch("updatePullingInfo", false);
        alert("token无效,请重新登录！");
        router.push({ name: 'user_account_login' });
      }
    });
  } else {
    store.dispatch("updatePullingInfo", false);
    flag = 2;
  }




  if (to.meta.requestAuth && !store.state.user.is_login) {
    if (flag === 1) {
      next();
    } else {
      next({ name: "user_account_login" });
      alert("请先进行登录！");
    }
  } else {
    next();
  }

  
});
export default router
