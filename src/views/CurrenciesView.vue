<template>
  <div class="currencies-container">
    <h1>Управление валютами</h1>

    <!-- Режимы просмотра/редактирования -->
    <div v-if="!isEditing" class="view-mode">
      <!-- Фильтры и поиск -->
      <div class="controls">
        <div class="filters">
          <input v-model="searchQuery" placeholder="Поиск по коду или названию..." class="search-input">
        </div>

        <div class="action-buttons">
          <button @click="startCreate" class="action-button primary">
            <i class="fas fa-plus"></i> Добавить валюту
          </button>
          <button @click="showConvertModal = true" class="action-button secondary">
            <i class="fas fa-exchange-alt"></i> Конвертировать
          </button>
        </div>
      </div>

      <!-- Список валют -->
      <div v-if="filteredCurrencies.length > 0" class="currencies-list">
        <div v-for="currency in filteredCurrencies" :key="currency.id" class="currency-item" @click="startEdit(currency)">
          <div class="currency-icon">
            <i class="fas fa-coins"></i>
          </div>
          <div class="currency-details">
            <div class="currency-header">
              <span class="currency-code">{{ currency.code }}</span>
              <span class="currency-name">{{ currency.name }}</span>
            </div>
            <div class="currency-rateToBa">
              Курс: {{ formatNumber(currency.rate) }} ₽
            </div>
            <div class="currency-symbol">{{ currency.symbol }}</div>
          </div>
          <button @click.stop="confirmDelete(currency)" class="delete-btn">
            <i class="fas fa-trash"></i>
          </button>
        </div>
      </div>

      <div v-else class="empty-state">
        <p>Нет валют для отображения</p>
        <button @click="startCreate" class="action-button">
          <i class="fas fa-plus"></i> Добавить первую валюту
        </button>
      </div>
    </div>

    <!-- Режим редактирования/создания -->
    <div v-else class="edit-mode">
      <h2>{{ editingCurrency.id ? 'Редактирование валюты' : 'Новая валюта' }}</h2>

      <form @submit.prevent="submitForm" class="currency-form">
        <div class="form-row">
          <div class="form-group">
            <label>Код валюты (ISO)</label>
            <input 
              v-model="editingCurrency.code" 
              type="text" 
              required 
              placeholder="USD, EUR и т.д."
              maxlength="3"
              pattern="[A-Z]{3}"
              title="3 заглавные буквы"
            >
          </div>

          <div class="form-group">
            <label>Символ</label>
            <input 
              v-model="editingCurrency.symbol" 
              type="text" 
              required 
              placeholder="$, €, ₽ и т.д."
              maxlength="3"
            >
          </div>
        </div>

        <div class="form-group">
          <label>Название валюты</label>
          <input 
            v-model="editingCurrency.name" 
            type="text" 
            required 
            placeholder="Доллар США, Евро и т.д."
          >
        </div>

        <div class="form-group">
          <label>Курс к рублю</label>
          <input 
            v-model.number="editingCurrency.rateToBase" 
            type="number" 
            step="0.0001" 
            min="0"
            required 
            placeholder="1.0"
          >
        </div>

        <div class="form-actions">
          <button type="button" @click="cancelEdit" class="action-button secondary">
            Отмена
          </button>
          <button 
            v-if="editingCurrency.id"
            type="button" 
            @click="confirmDelete(editingCurrency)" 
            class="action-button danger"
          >
            Удалить
          </button>
          <button type="submit" class="action-button primary">
            {{ editingCurrency.id ? 'Обновить' : 'Создать' }}
          </button>
        </div>
      </form>
    </div>

    <!-- Модальное окно подтверждения удаления -->
    <div v-if="showDeleteModal" class="modal-overlay">
      <div class="modal-content">
        <h3>Подтверждение удаления</h3>
        <p>Вы уверены, что хотите удалить валюту {{ currencyToDelete?.code }}?</p>
        <div class="modal-actions">
          <button @click="showDeleteModal = false" class="action-button secondary">
            Отмена
          </button>
          <button @click="confirmDeleteAction" class="action-button danger">
            Удалить
          </button>
        </div>
      </div>
    </div>

    <!-- Модальное окно конвертации валют -->
    <div v-if="showConvertModal" class="modal-overlay">
      <div class="modal-content">
        <h3>Конвертер валют</h3>
        
        <div class="convert-form">
          <div class="form-row">
            <div class="form-group">
              <label>Из валюты</label>
              <select v-model="convertFrom" required>
                <option v-for="curr in currencies" :value="curr.code">{{ curr.code }} - {{ curr.name }}</option>
              </select>
            </div>
            
            <button @click="swapCurrencies" class="swap-btn" title="Поменять местами">
              <i class="fas fa-exchange-alt"></i>
            </button>
            
            <div class="form-group">
              <label>В валюту</label>
              <select v-model="convertTo" required>
                <option v-for="curr in currencies" :value="curr.code">{{ curr.code }} - {{ curr.name }}</option>
              </select>
            </div>
          </div>
          
          <div class="form-group">
            <label>Сумма</label>
            <input 
              v-model.number="convertAmount" 
              type="number" 
              step="0.01" 
              min="0"
              required 
              placeholder="Введите сумму"
            >
          </div>
          
          <div v-if="convertedResult !== null" class="convert-result">
            <h4>Результат:</h4>
            <p class="result-amount">
              {{ formatNumber(convertAmount) }} {{ getCurrencySymbol(convertFrom) }} = 
              {{ formatNumber(convertedResult) }} {{ getCurrencySymbol(convertTo) }}
            </p>
            <p class="result-rate">
              Курс: 1 {{ convertFrom }} = {{ formatNumber(conversionRate) }} {{ convertTo }}
            </p>
          </div>
          
          <div class="modal-actions">
            <button @click="showConvertModal = false" class="action-button secondary">
              Закрыть
            </button>
            <button @click="convertCurrency" class="action-button primary">
              Конвертировать
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      currencies: [],
      searchQuery: '',
      isEditing: false,
      editingCurrency: this.createEmptyCurrency(),
      showDeleteModal: false,
      currencyToDelete: null,
      showConvertModal: false,
      convertFrom: 'USD',
      convertTo: 'RUB',
      convertAmount: 100,
      convertedResult: null,
      conversionRate: null
    }
  },
  
  computed: {
    filteredCurrencies() {
      return this.currencies.filter(curr => {
        const searchLower = this.searchQuery.toLowerCase();
        return curr.code.toLowerCase().includes(searchLower) || 
               curr.name.toLowerCase().includes(searchLower);
      }).sort((a, b) => a.code.localeCompare(b.code));
    }
  },
  
  async created() {
    await this.loadCurrencies();
    // Устанавливаем начальные значения для конвертера
    if (this.currencies.length > 0) {
      this.convertFrom = this.currencies.find(c => c.code === 'USD')?.code || this.currencies[0].code;
      this.convertTo = this.currencies.find(c => c.code === 'RUB')?.code || 
                      (this.currencies.length > 1 ? this.currencies[1].code : this.currencies[0].code);
    }
  },
  
  methods: {
    createEmptyCurrency() {
      return {
        id: null,
        code: '',
        name: '',
        symbol: '',
        rate: 1.0
      };
    },
    
    async loadCurrencies() {
      try {
        const response = await fetch('http://localhost:8080/api/v1/currencies');
        if (!response.ok) throw new Error('Ошибка загрузки валют');
        this.currencies = await response.json();
      } catch (err) {
        console.error('Ошибка загрузки валют:', err);
        alert('Не удалось загрузить список валют');
      }
    },
    
    async loadCurrency(id) {
      try {
        const response = await fetch(`http://localhost:8080/api/v1/currencies/${id}`);
        if (!response.ok) throw new Error('Ошибка загрузки валюты');
        return await response.json();
      } catch (err) {
        console.error('Ошибка загрузки валюты:', err);
        alert('Не удалось загрузить данные валюты');
        return null;
      }
    },
    
    startCreate() {
      this.editingCurrency = this.createEmptyCurrency();
      this.isEditing = true;
    },
    
    async startEdit(currency) {
      const fullCurrency = await this.loadCurrency(currency.id);
      this.editingCurrency = fullCurrency || currency;
      this.isEditing = true;
    },
    
    cancelEdit() {
      this.isEditing = false;
      this.editingCurrency = this.createEmptyCurrency();
    },
    
    async submitForm() {
      try {
        const url = this.editingCurrency.id 
          ? `http://localhost:8080/api/v1/currencies/${this.editingCurrency.id}`
          : 'http://localhost:8080/api/v1/currencies';
          
        const method = this.editingCurrency.id ? 'PUT' : 'POST';
        
        const response = await fetch(url, {
          method,
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify(this.editingCurrency)
        });
        
        if (!response.ok) {
          const error = await response.text();
          throw new Error(`Ошибка ${response.status}: ${error}`);
        }
        
        const data = await response.json();
        
        if (this.editingCurrency.id) {
          // Обновляем существующую валюту в списке
          this.currencies = this.currencies.map(curr => 
            curr.id === data.id ? data : curr
          );
        } else {
          // Добавляем новую валюту
          this.currencies.push(data);
        }
        
        this.cancelEdit();
      } catch (err) {
        console.error('Ошибка сохранения:', err);
        alert('Не удалось сохранить валюту');
      }
    },

    confirmDelete(currency) {
      this.currencyToDelete = currency;
      this.showDeleteModal = true;
    },
    
    async confirmDeleteAction() {
      try {
        const res = await fetch(
          `http://localhost:8080/api/v1/currencies/${this.currencyToDelete.id}`, 
          { method: 'DELETE' }
        );
        
        if (!res.ok) throw new Error(res.statusText);
        
        this.currencies = this.currencies.filter(
          c => c.id !== this.currencyToDelete.id
        );
        
        this.showDeleteModal = false;
        this.currencyToDelete = null;
        
        // Если удаляли редактируемую валюту - выходим из режима редактирования
        if (this.editingCurrency.id === this.currencyToDelete?.id) {
          this.cancelEdit();
        }
      } catch (err) {
        console.error('Ошибка удаления:', err);
        alert('Не удалось удалить валюту');
      }
    },
    
    async convertCurrency() {
  if (!this.convertAmount || this.convertAmount <= 0) {
    alert('Введите корректную сумму для конвертации');
    return;
  }

  if (this.convertFrom === this.convertTo) {
    this.convertedResult = this.convertAmount;
    this.conversionRate = 1;
    return;
  }

  const fromCurrency = this.currencies.find(c => c.code === this.convertFrom);
  const toCurrency = this.currencies.find(c => c.code === this.convertTo);

  if (!fromCurrency || !toCurrency) {
    alert('Выбрана недопустимая валюта');
    return;
  }

  // Конвертация через рубль:
  // amount * (rate_from / rate_to)
  const rateFrom = fromCurrency.rate;
  const rateTo = toCurrency.rate;

  const rate = rateFrom / rateTo;
  const result = this.convertAmount * rate;

  this.conversionRate = rate;
  this.convertedResult = result;
},
    
    swapCurrencies() {
      [this.convertFrom, this.convertTo] = [this.convertTo, this.convertFrom];
      if (this.convertedResult !== null) {
        this.convertAmount = this.convertedResult;
        this.convertCurrency();
      }
    },
    
    getCurrencySymbol(code) {
      return this.currencies.find(c => c.code === code)?.symbol || code;
    },
    
    formatNumber(value) {
      return new Intl.NumberFormat('ru-RU', {
        minimumFractionDigits: 2,
        maximumFractionDigits: 4
      }).format(value);
    }
  }
}
</script>

<style scoped>
.currencies-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.controls {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  gap: 15px;
}

.action-buttons {
  display: flex;
  gap: 10px;
}

.search-input {
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
  min-width: 250px;
}

.action-button {
  padding: 8px 16px;
  border-radius: 4px;
  border: none;
  cursor: pointer;
  font-weight: 500;
  display: flex;
  align-items: center;
  gap: 6px;
  transition: all 0.2s;
}

.action-button i {
  font-size: 14px;
}

.action-button.primary {
  background-color: #4CAF50;
  color: white;
}

.action-button.secondary {
  background-color: #f0f0f0;
  color: #333;
}

.action-button.danger {
  background-color: #f44336;
  color: white;
}

.action-button:hover {
  opacity: 0.9;
  transform: translateY(-1px);
}

.currencies-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.currency-item {
  display: flex;
  align-items: center;
  padding: 12px 15px;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.1);
  cursor: pointer;
  transition: all 0.2s;
}

.currency-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 6px rgba(0,0,0,0.1);
}

.currency-icon {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #FFC107;
  color: white;
  margin-right: 15px;
  flex-shrink: 0;
}

.currency-details {
  flex-grow: 1;
}

.currency-header {
  display: flex;
  gap: 10px;
  margin-bottom: 4px;
  align-items: baseline;
}

.currency-code {
  font-weight: bold;
  font-size: 1.1em;
}

.currency-name {
  color: #666;
  font-size: 0.9em;
}

.currency-rate {
  font-weight: 500;
}

.currency-symbol {
  color: #666;
  font-size: 0.85em;
  margin-top: 2px;
}

.delete-btn {
  background: none;
  border: none;
  color: #999;
  cursor: pointer;
  padding: 5px;
  margin-left: 10px;
}

.delete-btn:hover {
  color: #f44336;
}

.empty-state {
  text-align: center;
  padding: 40px 0;
  color: #666;
}

.edit-mode {
  background-color: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0,0,0,0.1);
  color: #666;
}

.currency-form {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.form-row {
  display: flex;
  gap: 15px;
}

.form-row .form-group {
  flex: 1;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.form-group label {
  font-weight: 500;
  color: #555;
}

.form-group input, 
.form-group select, 
.form-group textarea {
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 10px;
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0,0,0,0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  background-color: white;
  padding: 20px;
  border-radius: 8px;
  max-width: 500px;
  width: 100%;
}

.modal-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 20px;
}

.convert-form {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.swap-btn {
  background: none;
  border: none;
  cursor: pointer;
  color: #666;
  align-self: flex-end;
  margin-bottom: 15px;
  padding: 5px 10px;
}

.swap-btn:hover {
  color: #4CAF50;
}

.convert-result {
  margin: 15px 0;
  padding: 15px;
  background-color: #f9f9f9;
  border-radius: 4px;
}

.result-amount {
  font-size: 1.2em;
  font-weight: bold;
  margin-bottom: 5px;
}

.result-rate {
  color: #666;
  font-size: 0.9em;
}
</style>