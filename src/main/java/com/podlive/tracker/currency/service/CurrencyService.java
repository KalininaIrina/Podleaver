package com.podlive.tracker.currency.service;

import com.podlive.tracker.common.service.CrudService;
import com.podlive.tracker.currency.dto.CurrencyRequestDto;
import com.podlive.tracker.currency.model.Currency;
import com.podlive.tracker.currency.repository.CurrencyRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

@Service//указывает что класс является сервисом
public class CurrencyService extends CrudService<Currency, Integer> { //наследуемся от CrudService получая все CRUD методы
    private final CurrencyRepository currencyRepository;//создаем поле для репозитория, который будет работать с сущностью Currency

    public CurrencyService(CurrencyRepository currencyRepository){//конструктор класса, принимает репозиторий как зависимость
        super(currencyRepository);//передаем репозиторий в родительский класс (CrudService)
        this.currencyRepository = currencyRepository;//Присваиваем переданный репозиторий в поле текущего класса
    }

    public Currency create(CurrencyRequestDto requestDto){//метод создания новой валюты на основе CurrencyRequestDto
        Currency currency = Currency.builder()
                .name(requestDto.getName())
                .code(requestDto.getCode())
                .build();//создаем новый объект Currency устанавливая имя и код, и в конце заканчивая построение
        return save(currency);//вызываем метод save из CrudService, и сохраняем только что созданный объект
    }

    public Currency update(Integer id, CurrencyRequestDto requestDto){//метод обновления существующих объектов Currency, принимает в себя id объекта и данные из CurrencyRequestDto
        Currency currency = currencyRepository.findById(id).orElseThrow(EntityNotFoundException::new);//ищет объект с введенным id, если его нет - выбрасывает ошибку
        currency.setName(requestDto.getName());//обновляет имя валюты
        currency.setCode(requestDto.getCode());//обновляет код валюты
        return save(currency);//вызывает метод save из CrudService и сохраняет обновленный объект Currency
    }

}
