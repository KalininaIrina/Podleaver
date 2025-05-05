<template>
  <div>
    <h1>Recurring Transactions</h1>
    <p>Здесь отображаются повторяющиеся транзакции.</p>

    <button @click="getAll">🔍 Получить все</button>
    <button @click="getById">🔍 Получить по ID</button>
    <button @click="create">➕ Создать</button>
    <button @click="update">✏️ Обновить</button>
    <button @click="remove">🗑 Удалить</button>
    <button @click="generate">⚙️ Генерировать</button>
  </div>
</template>

<script>
export default {
  name: 'RecurringTransactionsView',
  methods: {
    async getAll() {
      const res = await fetch('http://localhost:8000/api/v1/recurring-transaction');
      const data = await res.json();
      console.log('Все транзакции:', data);
    },
    async getById() {
      const id = 1; // Заменить на реальный ID
      const res = await fetch(`http://localhost:8000/api/v1/recurring-transaction/${id}`);
      const data = await res.json();
      console.log(`Транзакция #${id}:`, data);
    },
    async create() {
      const newTransaction = {
        name: 'Пример',
        amount: 100,
        frequency: 'monthly'
      };
      const res = await fetch('http://localhost:8000/api/v1/recurring-transaction', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(newTransaction)
      });
      const data = await res.json();
      console.log('Создано:', data);
    },
    async update() {
      const id = 1;
      const updatedData = {
        name: 'Обновлено',
        amount: 200
      };
      const res = await fetch(`http://localhost:8000/api/v1/recurring-transaction/${id}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(updatedData)
      });
      const data = await res.json();
      console.log('Обновлено:', data);
    },
    async remove() {
      const id = 1;
      const res = await fetch(`http://localhost:8000/api/v1/recurring-transaction/${id}`, {
        method: 'DELETE'
      });
      if (res.ok) console.log(`Удалена транзакция #${id}`);
    },
    async generate() {
      const res = await fetch('http://localhost:8000/api/v1/recurring-transaction/generate', {
        method: 'POST'
      });
      const data = await res.json();
      console.log('Сгенерировано:', data);
    }
  }
}
</script>
