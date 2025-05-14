package com.workintech.twitter.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequestDto(String email, String password){
}
