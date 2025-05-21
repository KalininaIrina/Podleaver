import { createRouter, createWebHistory } from 'vue-router'

// Импорт всех необходимых представлений (views)
import TransactionsView from '../views/TransactionsView.vue'
import RecurringTransactionsView from '../views/RecurringTransactionsView.vue'
import ExternalTransactionsView from '../views/ExternalTransactionsView.vue'
import CurrenciesView from '../views/CurrenciesView.vue'
import CategoriesView from '../views/CategoriesView.vue'
import AccountsView from '../views/AccountsView.vue'

const routes = [
  {
    path: '/',
    redirect: '/transactions'
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


const router = createRouter({
  history: createWebHistory(), // Используется HTML5 History API
  routes
})

export default router
