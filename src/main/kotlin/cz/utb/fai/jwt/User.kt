package cz.utb.fai.jwt

import java.util.*
import javax.persistence.*


@Entity
@Table(name = "USER")
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: UUID = UUID.randomUUID(),
    var firstName: String,
    var lastName: String,
    var email: String,
    var password: String,
    @Enumerated(EnumType.STRING)
    var role: Role
) {
    constructor() : this(firstName = "", lastName = "", email = "", password = "", role = Role.USER)
}

enum class Role { USER, ADMIN }