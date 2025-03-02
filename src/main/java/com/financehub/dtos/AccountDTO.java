package com.financehub.dtos;

import com.financehub.models.Client;

public record AccountDTO(String id, double balance, Client owner) {
}
