package emy.backend.barua.app.user.domain.models

data class ProfileUser(
    var firstname  : String?="",
    var lastname   : String?="",
    var fullname   : String?="",
    var address    : String?="",
    var images     : String?="",
    var cardFront  : String?="",
    var cardBack   : String?="",
    var numberCard : String?=""
)
