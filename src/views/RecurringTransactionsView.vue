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

    <div class="transaction-cards">
      <div 
        v-for="tx in transactions" 
        :key="tx.id" 
        class="transaction-card"
      >
        <h3>{{ tx.name }}</h3>
        <p>Сумма: <strong>{{ tx.amount }}₽</strong></p>
        <p>Частота: <strong>{{ tx.frequency }}</strong></p>
      </div>
    </div>

    <p v-if="transactions.length === 0">Нет данных.</p>
  </div>
</template>

<script>
export default {
  name: 'RecurringTransactionsView',
  data() {
    return {
      transactions: [],
      currentTransaction: null, // Для хранения данных текущей транзакции
      idToUpdateOrDelete: null, // Для хранения ID транзакции для обновления или удаления
    };
  },
  methods: {
    async getAll() {
      try {
        const res = await fetch('http://localhost:8080/api/v1/recurring-transaction');
        if (!res.ok) throw new Error('Ошибка при получении данных');
        const data = await res.json();
        this.transactions = data;
        console.log('Все транзакции:', data);
      } catch (err) {
        console.error('Ошибка при загрузке:', err);
      }
    },

    async getById() {
      const id = prompt('Введите ID транзакции:');
      if (!id) return;
      try {
        const res = await fetch(`http://localhost:8080/api/v1/recurring-transaction/${id}`);
        if (!res.ok) throw new Error(`Транзакция с ID ${id} не найдена`);
        const data = await res.json();
        this.currentTransaction = data;
        console.log(`Транзакция #${id}:`, data);
      } catch (err) {
        console.error('Ошибка при загрузке транзакции по ID:', err);
      }
    },

    async create() {
      const newTransaction = {
        name: 'Пример транзакции',
        amount: 100,
        frequency: 'monthly',
      };
      try {
        const res = await fetch('http://localhost:8080/api/v1/recurring-transaction', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(newTransaction),
        });
        if (!res.ok) throw new Error('Ошибка при создании транзакции');
        const data = await res.json();
        this.transactions.push(data); // Добавляем новую транзакцию в список
        console.log('Создано:', data);
      } catch (err) {
        console.error('Ошибка при создании транзакции:', err);
      }
    },

    async update() {
      const id = prompt('Введите ID транзакции для обновления:');
      if (!id) return;
      const updatedData = {
        name: 'Обновлённая транзакция',
        amount: 200,
        frequency: 'yearly',
      };
      try {
        const res = await fetch(`http://localhost:8080/api/v1/recurring-transaction/${id}`, {
          method: 'PUT',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(updatedData),
        });
        if (!res.ok) throw new Error('Ошибка при обновлении транзакции');
        const data = await res.json();
        // Обновляем список транзакций
        const index = this.transactions.findIndex(tx => tx.id === id);
        if (index !== -1) {
          this.transactions[index] = data;
        }
        console.log('Обновлено:', data);
      } catch (err) {
        console.error('Ошибка при обновлении транзакции:', err);
      }
    },

    async remove() {
      const id = prompt('Введите ID транзакции для удаления:');
      if (!id) return;
      try {
        const res = await fetch(`http://localhost:8080/api/v1/recurring-transaction/${id}`, {
          method: 'DELETE',
        });
        if (res.ok) {
          this.transactions = this.transactions.filter(tx => tx.id !== id); // Удаляем из списка
          console.log(`Удалена транзакция с ID ${id}`);
        } else {
          throw new Error('Ошибка при удалении транзакции');
        }
      } catch (err) {
        console.error('Ошибка при удалении транзакции:', err);
      }
    },

    async generate() {
      try {
        const res = await fetch('http://localhost:8080/api/v1/recurring-transaction/generate', {
          method: 'POST',
        });
        if (!res.ok) throw new Error('Ошибка при генерации транзакций');
        const data = await res.json();
        console.log('Сгенерировано:', data);
        // Возможно, обновить список транзакций после генерации
        this.transactions = data;
      } catch (err) {
        console.error('Ошибка при генерации транзакций:', err);
      }
    }
  }
};
</script>

<style scoped>
.transaction-cards {
  color: #426b97;
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 20px;
  margin-top: 20px;
}

.transaction-card {
  padding: 20px;
  border: 2px solid #ddd;
  border-radius: 8px;
  background-color: #f9f9f9;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s, box-shadow 0.3s;
}

.transaction-card:hover {
  transform: translateY(-10px);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.15);
}

.transaction-card h3 {
  margin-bottom: 10px;
}

.transaction-card p {
  margin: 5px 0;
}

.transaction-card strong {
  color: #426b97;
}
</style>
