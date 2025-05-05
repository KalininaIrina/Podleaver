import { createRouter, createWebHistory } from 'vue-router'

import TransactionsView from '../views/TransactionsView.vue'
import RecurringTransactionsView from '../views/RecurringTransactionsView.vue'
import ExternalTransactionsView from '../views/ExternalTransactionsView.vue'
import CurrenciesView from '../views/CurrenciesView.vue'
import CategoriesView from '../views/CategoriesView.vue'
import AccountsView from '../views/AccountsView.vue'

const routes = [
  { path: '/transactions', component: TransactionsView },
  { path: '/recurring-transactions', component: RecurringTransactionsView },
  { path: '/external-transactions', component: ExternalTransactionsView },
  { path: '/currencies', component: CurrenciesView },
  { path: '/categories', component: CategoriesView },
  { path: '/accounts', component: AccountsView },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

export default router
