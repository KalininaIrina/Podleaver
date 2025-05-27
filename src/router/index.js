import { createRouter, createWebHistory } from 'vue-router'
import TransactionsView from '../views/TransactionsView.vue'
import RecurringTransactionsView from '../views/RecurringTransactionsView.vue'
import ExternalTransactionsView from '../views/ExternalTransactionsView.vue'
import CurrenciesView from '../views/CurrenciesView.vue'
import CategoriesView from '../views/CategoriesView.vue'
import AccountsView from '../views/AccountsView.vue'
import HomeView from '../views/HomeView.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView // Теперь это единственный маршрут для '/'
    },
    {
      path: '/transactions',
      name: 'Transactions',
      component: TransactionsView
    },
    {
      path: '/recurring-transactions',
      name: 'RecurringTransactions',
      component: RecurringTransactionsView
    },
    {
      path: '/external-transactions',
      name: 'ExternalTransactions',
      component: ExternalTransactionsView
    },
    {
      path: '/currencies',
      name: 'Currencies',
      component: CurrenciesView
    },
    {
      path: '/categories',
      name: 'Categories',
      component: CategoriesView
    },
    {
      path: '/accounts',
      name: 'Accounts',
      component: AccountsView
    }
  ]
})

export default router 