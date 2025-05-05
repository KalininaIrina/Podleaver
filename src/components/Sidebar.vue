<template>
  <div class="sidebar" :class="{ open: isHovered }" @mouseenter="isHovered = true" @mouseleave="isHovered = false">
    <ul>
      <li><router-link to="/recurring-transactions">Recurring Transactions</router-link></li>
      <li><router-link to="/transactions">Transactions</router-link></li>
      <li><router-link to="/external-transactions">External Transactions</router-link></li>
      <li><router-link to="/currencies">Currencies</router-link></li>
      <li><router-link to="/categories">Categories</router-link></li>
      <li><router-link to="/accounts">Accounts</router-link></li>
    </ul>
  </div>
</template>

<script>
export default {
  data() {
    return {
      isHovered: false
    }
  }
}
</script>

<style scoped>
.sidebar {
  position: fixed;
  top: 0;
  left: -205px;
  height: 100vh;
  width: 200px;
  background-color: #222;
  color: white;
  padding: 20px;
  transition: left 0.3s ease;
  z-index: 1000;
}

.sidebar.open {
  left: 0;
}

.sidebar ul {
  list-style: none;
  padding: 0;
  margin-top: 40px;
}

.sidebar ul li {
  margin: 15px 0;
}

.sidebar ul li a {
  color: rgb(255, 255, 255);
  text-decoration: none;
}
</style>
