package com.example.lending_service.model;

public enum ContractStatus {
    ACTIVE,      // действующий договор
    CLOSED,      // договор успешно завершён
    OVERDUE,     // есть просроченные платежи
    TERMINATED   // расторгнут досрочно
}

