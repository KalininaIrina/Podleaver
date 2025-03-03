package com.podlive.tracker.currency.controller;

import com.podlive.tracker.currency.dto.CurrencyRequestDto;
import com.podlive.tracker.currency.dto.CurrencyResponseDto;
import com.podlive.tracker.currency.mapper.CurrencyMapper;
import com.podlive.tracker.currency.model.Currency;
import com.podlive.tracker.currency.service.CurrencyService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;

import java.util.List;

@RestController//указывает на то, что этот класс является rest контроллером
@RequestMapping(value = "api/v1/currency")//указывает базовый url для всех методов контроллера
@AllArgsConstructor//создание конструктора со всеми аргументами автоматически
public class CurrencyController{
    private final CurrencyService currencyService;//Сервис для обработки бизнес логики, связанной с валютой
    private final CurrencyMapper responseMapper;//Маппер для преобразования сущностей в DTO

    @Operation(summary = "Get all currencies")//Краткое описание метода для swagger-документации
    @GetMapping//Метод обрабатывает get запросы по адресу api/v1/currency
    public ResponseEntity<List<CurrencyResponseDto>> getAll(){//получаем список всех валют, преобразуем их в DTO и возвращаем в теле ответа
        return ResponseEntity.ok(currencyService.getAll().stream().map(responseMapper::map).toList());
    }

    @Operation(summary = "Get currency")
    @GetMapping("/{id}")//метод обрабатывает get запрос с переменной id в url
    public ResponseEntity<CurrencyResponseDto> getById(@PathVariable Integer id){//Получаем валюту по ID, преобразуем в DTO и возвращаем в теле ответа
        return ResponseEntity.ok(responseMapper.map(currencyService.getById(id)));
    }

    @Operation(summary = "Add currency")
    @PostMapping//Метод обрабатывает post запросы для создания новой валюты
    public ResponseEntity<CurrencyResponseDto> create(@RequestBody CurrencyRequestDto currencyRequestDto){//создаем новую валюту, сохраняем её и возвращаем в виде DTO
        Currency currency = currencyService.create(currencyRequestDto);
        return ResponseEntity.ok(responseMapper.map(currency));
    }

    @Operation(summary = "Update currency")
    @PutMapping("/{id}")//Метод обрабатывает pur запросы для редактирования валюты с указанным id
    public ResponseEntity<CurrencyResponseDto> update(@PathVariable Integer id, @RequestBody CurrencyRequestDto currencyRequestDto){//обновляем валюту, сохраняем изменения и возвращаем обновленные данные в виде DTO
        Currency currency = currencyService.update(id, currencyRequestDto);
        return ResponseEntity.ok(responseMapper.map(currency));
    }

    @Operation(summary = "Delete currency")
    @DeleteMapping("/{id}")//Обрабатывает delete запросы по id
    public ResponseEntity<CurrencyResponseDto> delete(@PathVariable Integer id){
        currencyService.delete(id);//удаляет валюту по id
        return ResponseEntity.noContent().build();//Возвращает HTTP-статус 204(no Content), так как удаленный ресурс не предполагает ответа
    }
}
//@PathVariable — это аннотация в Spring, которая используется для привязки значения из URI (путь запроса) к параметрам метода контроллера. Например, если у вас есть путь, который включает переменные (например, /users/{id}), аннотация @PathVariable позволяет передать значение id в метод контроллера.
//@RequestBody — аннотация, которая используется для привязки содержимого тела запроса (например, JSON) к параметрам метода контроллера. Это позволяет принимать данные в теле запроса, обычно для операций POST или PUT.
//ResponseEntity — это класс в Spring, который представляет полный HTTP-ответ, включая статус, заголовки и тело ответа. Используется для гибкой настройки ответа в REST API. Например, можно задать статус HTTP, передать тело ответа и настроить заголовки.
//Таким образом, @PathVariable извлекает данные из пути запроса, @RequestBody извлекает данные из тела запроса, а ResponseEntity используется для формирования HTTP-ответов.
