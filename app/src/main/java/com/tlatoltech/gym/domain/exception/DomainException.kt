package com.tlatoltech.gym.domain.exception
sealed class DomainException(message: String) : Exception(message)

class InvalidPriceException(message: String) : DomainException(message)
class InvalidLevelException(message: String) : DomainException(message)
class PlanAlreadyInactiveException(message: String) : DomainException(message)
class PlanNotFoundException(message: String) : DomainException(message)