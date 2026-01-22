# Dispofast


## Arquitectura del proyecto

Al estar siguiendose una arquitectura monolitica por módulos, cada módulo se maneja de la siguiente manera:

    modules/customers
    api/           -> controllers REST, DTOs, mappers API
    application/   -> casos de uso, servicios de aplicación, puertos
    domain/        -> entidades, value objects, reglas
    infra/         -> JPA, repositorios, implementaciones de puertos, config
