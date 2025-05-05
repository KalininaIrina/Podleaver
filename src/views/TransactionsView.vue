<template>
  <div>
    <h1>Transactions</h1>
    <p>Работа с обычными транзакциями:</p>

    <button @click="getAll">🔍 Получить все</button>
    <button @click="getById">🔍 Получить по ID</button>
    <button @click="create">➕ Добавить</button>
    <button @click="update">✏️ Обновить</button>
    <button @click="remove">🗑 Удалить</button>
  </div>
</template>

<script>
export default {
  name: 'TransactionsView',
  methods: {
    async getAll() {
      const res = await fetch('http://localhost:8000/api/v1/transaction');
      const data = await res.json();
      console.log('Все транзакции:', data);
    },
    async getById() {
      const id = 1; // заменить на реальный ID
      const res = await fetch(`http://localhost:8000/api/v1/transaction/${id}`);
      const data = await res.json();
      console.log(`Транзакция #${id}:`, data);
    },
    async create() {
      const newTransaction = {
        name: 'Новая транзакция',
        amount: 500,
        date: '2025-05-02'
      };
      const res = await fetch('http://localhost:8000/api/v1/transaction', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(newTransaction)
      });
      const data = await res.json();
      console.log('Создана:', data);
    },
    async update() {
      const id = 1;
      const updatedData = {
        name: 'Обновлённая транзакция',
        amount: 777
      };
      const res = await fetch(`http://localhost:8000/api/v1/transaction/${id}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(updatedData)
      });
      const data = await res.json();
      console.log('Обновлено:', data);
    },
    async remove() {
      const id = 1;
      const res = await fetch(`http://localhost:8000/api/v1/transaction/${id}`, {
        method: 'DELETE'
      });
      if (res.ok) console.log(`Удалена транзакция #${id}`);
    }
  }
}
</script>
